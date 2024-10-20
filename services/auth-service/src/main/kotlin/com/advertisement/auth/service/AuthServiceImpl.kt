package com.advertisement.auth.service

import com.advertisement.auth.configuration.SecurityProperties
import com.advertisement.auth.dto.OtpType
import com.advertisement.auth.dto.request.LoginRequest
import com.advertisement.auth.dto.response.AccessTokenResponse
import com.advertisement.auth.dto.response.AppClientResponse
import com.advertisement.auth.dto.response.LoginResponse
import com.advertisement.auth.exception.*
import com.advertisement.auth.model.*
import com.advertisement.auth.utils.Utils
import com.advertisement.grpc.InternalRegistrationResponse
import mu.KotlinLogging
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import java.time.Clock
import java.time.Instant
import java.util.*


private val logger = KotlinLogging.logger {}

@Service
class AuthServiceImpl(
    val userService: UserService,
    val refreshTokenService: RefreshTokenService,
    val tokenService: TokenService,
    val otpService: OtpService,
    val appClientService: AppClientService,
    val securityProperties: SecurityProperties,
    val clock: Clock
) : AuthService {

    @Transactional
    override fun register(
        internalRegistrationRequest: com.advertisement.grpc.InternalRegistrationRequest,
        internalAuthHeader: String
    ): Mono<com.advertisement.grpc.InternalRegistrationResponse> {
        if (securityProperties.internalAuth.registrationKey != internalAuthHeader) throw ApiForbiddenException("Invalid key")
        val appName = internalRegistrationRequest.appName
        return appClientService.findByName(appName)
            .flatMap<AppClientResponse> { app ->
                if (app != null) {
                    Mono.error(ApiForbiddenException("App with name '$appName' is already registered"))
                } else {
                    Mono.empty()
                }
            }
            .switchIfEmpty(appClientService.save(appName))
            .map { e -> InternalRegistrationResponse.newBuilder().setUsername(e.name).setPassword(e.password!!).build() }
    }

    override fun verify(token: String): Mono<com.advertisement.grpc.UserWithRoles> {
        return Mono.just(token)
            .flatMap { t -> tokenService.verifyToken(t, TokenType.ACCESS) }
            .onErrorResume { e ->
                logger.error("Error: ${e.message}")
                Mono.error(ApiUnauthorizedException("Invalid token ${e.message}"))
            }
            .flatMap { decodedJWT -> userService.findWithRolesById(decodedJWT.claims["userId"]!!.`as`(UUID::class.java)) }
            .map { u -> com.advertisement.grpc.UserWithRoles.newBuilder()
                .setId(u.id.toString())
                .setPhone(u.phone)
                .setFirstName(u.firstName.orEmpty())
                .setLastName(u.lastName.orEmpty())
                .setCreatedAt(com.google.protobuf.Timestamp.newBuilder()
                    .setSeconds(u.createdAt.epochSecond)
                    .setNanos(u.createdAt.nano))
                .addAllRoles(u.roles)
                .build()}
            .switchIfEmpty(Mono.error(ApiException("User not found")))
    }

    /*
            Note! Statements written with JOOQ will not be executed in a single transaction since JOOQ's reactive context
            will not be able to see Spring's transaction
            So I decided to use JOOQ statements only for SELECT-queries and use Spring's DatabaseClient for others
             */
    @Transactional
    override fun login(loginRequest: LoginRequest): Mono<LoginResponse> {
            return otpService.findLastByPhoneAndType(loginRequest.phone, OtpType.AUTH)
            .switchIfEmpty(Mono.error(ApiNotFoundException("Otp not found")))
            .flatMap { foundOtp ->
                if (isOtpExpired(foundOtp)) return@flatMap Mono.error(ApiOtpExpiredException("Otp is expired"))
                if (!isHashMatch(loginRequest, foundOtp)) return@flatMap Mono.error(ApiException("Otp does not match"))
                // find user in DB
                userService.findByPhone(loginRequest.phone)
                    // save new user if not found in DB
                    .switchIfEmpty(userService.save(User(UUID.randomUUID(), loginRequest.phone, null, null, Instant.now(clock))))
                    .flatMap {
                        // generate access token
                        user -> tokenService.generateToken(user, TokenType.ACCESS)
                            // generate refresh token
                        .zipWith(tokenService.generateToken(user, TokenType.REFRESH)
                            .flatMap { refreshToken ->
                                // revoke existing refresh token if exists
                                refreshTokenService.revokeIfExistsByUserId(user.id)
                                    .flatMap { _ ->
                                        // save refresh token to DB
                                        refreshTokenService.save(
                                            RefreshToken(
                                                UUID.randomUUID(),
                                                user.id,
                                                Utils.calculateHash(
                                                    refreshToken,
                                                    securityProperties.jwt.refreshToken.hashingSalt.toByteArray()
                                                ),
                                                false,
                                                Instant.now(clock)
                                            )
                                        )
                                    }
                                    // return refresh token
                                    .map { _ -> refreshToken }})
                    }
                    .map { result -> LoginResponse(result.t1, result.t2) }
            }
    }

    override fun logout(): Mono<Unit> {
        return ReactiveSecurityContextHolder.getContext()
            .map { s -> s.authentication }
            .map { a -> a.principal as UserWithRoles }
            .flatMap{ u -> refreshTokenService.revokeIfExistsByUserId(u.id) }
            .then(Mono.empty())
    }

    override fun getAccessToken(refreshToken: String): Mono<AccessTokenResponse> {
        return tokenService.verifyToken(refreshToken, TokenType.REFRESH)
            .flatMap { decodedToken ->
                refreshTokenService.findLastNotRevokedByUserId(decodedToken.claims["userId"]!!.`as`(UUID::class.java))
                    .switchIfEmpty(Mono.error(ApiException("Refresh token not found")))
                    .filter { foundToken ->
                        Utils.calculateHash(refreshToken, securityProperties.jwt.refreshToken.hashingSalt.toByteArray())
                        .contentEquals(foundToken.hash)
                    }
                    .flatMap { foundToken ->
                        userService.findById(foundToken.userId)
                            .switchIfEmpty(Mono.error(ApiNotFoundException("User not found")))
                            .flatMap { user -> tokenService.generateToken(user, TokenType.ACCESS) }
                        .map { tokenString -> AccessTokenResponse(tokenString) }}
            }
    }

    private fun isOtpExpired(foundOtp: Otp): Boolean {
        return foundOtp.createdAt.isBefore(Instant.now(clock).minusSeconds(securityProperties.otp.lifetimeSeconds))
    }

    private fun isHashMatch(loginRequest: LoginRequest, foundOtp: Otp): Boolean {
        return Utils.calculateHash(loginRequest.code, securityProperties.otp.hashingSalt.toByteArray())
            .contentEquals(foundOtp.hashedCode)
    }
}