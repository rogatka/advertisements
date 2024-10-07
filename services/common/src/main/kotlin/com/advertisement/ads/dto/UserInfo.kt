package com.advertisement.ads.dto

import java.util.*

data class UserInfo(
    val id: UUID,
    val phone: String,
    val firstName: String?,
    val lastName: String?,
    val roles: List<String>
)