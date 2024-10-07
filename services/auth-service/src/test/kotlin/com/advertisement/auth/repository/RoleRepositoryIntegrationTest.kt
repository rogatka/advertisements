package com.advertisement.auth.repository

import com.advertisement.auth.BaseIntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.test.StepVerifier
import java.time.Instant
import java.time.temporal.ChronoUnit


class RoleRepositoryIntegrationTest:BaseIntegrationTest() {

    @Autowired
    lateinit var roleRepository: RoleRepository

    @Test
    fun `should correctly find role by name`() {
        // given
        val roleName = "USER"

        // when-then
        roleRepository.findFirstByNameEquals(roleName)
            .`as` { a -> StepVerifier.create(a) }
            .expectNextMatches{role ->
                (role.id == 1L)
                    .and(role.name == roleName)
                    .and(role.createdAt.isAfter(Instant.now().minus(10, ChronoUnit.MINUTES)))
            }
            .verifyComplete()
    }
}