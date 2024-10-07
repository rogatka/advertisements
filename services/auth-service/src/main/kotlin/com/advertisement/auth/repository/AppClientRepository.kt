package com.advertisement.auth.repository

import com.advertisement.auth.configuration.SecurityProperties
import com.advertisement.auth.dto.response.AppClientResponse
import com.advertisement.auth.utils.Utils
import org.apache.commons.text.RandomStringGenerator
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.time.Clock
import java.time.Instant
import java.time.OffsetDateTime
import java.util.*


@Repository
class AppClientRepository (
    val databaseClient: DatabaseClient,
    val clock: Clock,
    val securityProperties: SecurityProperties
) {
    fun save(name: String): Mono<AppClientResponse> {
        val password = generatePassword()
        return databaseClient.sql("""
            insert into advertisement.app_clients (id, name, password, disabled, created_at) values
            (:id, :name, :password, :disabled, :created_at)  
        """.trimIndent())
            .bind("id", UUID.randomUUID())
            .bind("name", name)
            .bind("password", Utils.calculateHash(password, securityProperties.internalAuth.hashingSalt.toByteArray()))
            .bind("disabled", false)
            .bind("created_at", Instant.now(clock))
            .filter{statement -> statement.returnGeneratedValues("id", "name", "disabled", "created_at")}
            .fetch()
            .first()
            .map { r -> AppClientResponse(r["id"] as UUID, r["name"] as String, password, r["disabled"] as Boolean, (r["created_at"] as OffsetDateTime).toInstant(), null) }
    }

    private fun generatePassword():String {
        val pwString = buildString {
            append(generateRandomSpecialCharacters(2))
            append(generateRandomNumbers(2))
            append(generateRandomAlphabet(2, true))
            append(generateRandomAlphabet(2, false))
            append(generateRandomCharacters(2))
        }
        val pwChars: MutableList<Char>  = pwString.chars()
            .mapToObj{data -> data.toChar() }
            .toList()
            .toMutableList()
        pwChars.shuffle()
        val password = pwChars.stream()
            .collect(::StringBuilder, StringBuilder::append, StringBuilder::append)
            .toString();
        return password;
    }

    private fun generateRandomSpecialCharacters(length: Int): String {
        val pwdGenerator = RandomStringGenerator.Builder().withinRange(33, 45).build();
        return pwdGenerator.generate(length);
    }

    private fun generateRandomNumbers(length: Int):String {
        val pwdGenerator = RandomStringGenerator.Builder().withinRange(48, 57).build();
        return pwdGenerator.generate(length);
    }

    private fun generateRandomCharacters(length:Int):String {
        val pwdGenerator = RandomStringGenerator.Builder().withinRange(48, 57).build();
        return pwdGenerator.generate(length);
    }

    private fun generateRandomAlphabet(length:Int, lowerCase:Boolean): String {
        val low: Int;
        val hi: Int;
        if (lowerCase) {
            low = 97;
            hi = 122;
        } else {
            low = 65;
            hi = 90;
        }
        val pwdGenerator = RandomStringGenerator.Builder().withinRange(low, hi).build();
        return pwdGenerator.generate(length);
    }
}