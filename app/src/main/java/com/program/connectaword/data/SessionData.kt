package com.program.connectaword.data

import kotlinx.serialization.Serializable

@Serializable
data class SessionData(
    val authToken: String? = null,
    val user: User? = null,
    val lastUsedIp: String? = null
)