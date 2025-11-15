package com.example.midiventaslvlup.network.dto

data class LoginResponse(
    val token: String?,
    val role: String?,
    val userId: Long?
)
