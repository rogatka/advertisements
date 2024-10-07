package com.advertisement.auth.controller

import com.advertisement.auth.model.User
import com.advertisement.auth.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/api/v1/users")
class UserController(val userService: UserService) {

    @GetMapping
    fun findAll(): ResponseEntity<Flux<User>> {
        return ResponseEntity.ok(userService.findAll())
    }
}