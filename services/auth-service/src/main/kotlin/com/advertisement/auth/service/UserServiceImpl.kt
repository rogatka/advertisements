package com.advertisement.auth.service

import com.advertisement.auth.exception.ApiNotFoundException
import com.advertisement.auth.model.User
import com.advertisement.auth.model.UserRole
import com.advertisement.auth.model.UserWithRoles
import com.advertisement.auth.model.jooq.tables.references.ROLES
import com.advertisement.auth.model.jooq.tables.references.USERS
import com.advertisement.auth.model.jooq.tables.references.USERS_ROLES
import com.advertisement.auth.repository.RoleRepository
import com.advertisement.auth.repository.UserRepository
import com.advertisement.auth.repository.UserRoleRepository
import org.jooq.DSLContext
import org.jooq.impl.DSL.multiset
import org.jooq.impl.DSL.select
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
class UserServiceImpl (
    val dsl: DSLContext,
    val userRepository: UserRepository,
    val roleRepository: RoleRepository,
    val userRoleRepository: UserRoleRepository
) : UserService {

    @Transactional
    override fun save(t: User): Mono<User> {
        return userRepository.save(t)
            .flatMap { user ->
                roleRepository.findFirstByNameEquals("USER")
                    .switchIfEmpty(Mono.error(ApiNotFoundException("Role not found")))
                    .flatMap { role ->
                        userRoleRepository.save(UserRole(UUID.randomUUID(), user.id, role.id, user.createdAt)) }
                    .map { _ -> user }}
    }

    override fun findAll(): Flux<User> {
        return Flux.from(
            dsl
                .select(USERS.ID, USERS.PHONE, USERS.FIRST_NAME, USERS.LAST_NAME, USERS.CREATED_AT)
                .from(USERS)
        )
            .map { r -> User(r.value1()!!, r.value2()!!, r.value3(), r.value4(), r.value5()!!) }
    }

    override fun findByPhone(phone: String): Mono<User> {
        return Mono.from(
            dsl
                .select(USERS.ID, USERS.PHONE, USERS.FIRST_NAME, USERS.LAST_NAME, USERS.CREATED_AT)
                .from(USERS)
                .where(USERS.PHONE.eq(phone))
        )
            .map { r -> User(r.value1()!!, r.value2()!!, r.value3(), r.value4(), r.value5()!!) }
    }

    override fun findById(id: UUID): Mono<User> {
        return Mono.from(
            dsl
                .select(USERS.ID, USERS.PHONE, USERS.FIRST_NAME, USERS.LAST_NAME, USERS.CREATED_AT)
                .from(USERS)
                .where(USERS.ID.eq(id))
        )
            .map { r -> User(r.value1()!!, r.value2()!!, r.value3(), r.value4(), r.value5()!!) }
    }

    override fun findWithRolesById(id: UUID): Mono<UserWithRoles> {
        return Mono.from(
            dsl
                .select(
                    USERS.ID, USERS.PHONE, USERS.FIRST_NAME, USERS.LAST_NAME, USERS.CREATED_AT,
                    multiset(
                        select(ROLES.NAME)
                        .from(ROLES)
                        .join(USERS_ROLES).on(ROLES.ID.eq(USERS_ROLES.ROLE_ID))
                        .where(USERS_ROLES.USER_ID.eq(USERS.ID))
                    ).`as`("roles"))
                .from(USERS)
                .leftJoin(USERS_ROLES).on(USERS.ID.eq(USERS_ROLES.USER_ID))
                .where(USERS.ID.eq(id))
        )
            .map { r -> UserWithRoles(r.value1()!!, r.value2()!!, r.value3(), r.value4(), r.value5()!!, r.value6().map { record -> record.value1() }) }
    }
}