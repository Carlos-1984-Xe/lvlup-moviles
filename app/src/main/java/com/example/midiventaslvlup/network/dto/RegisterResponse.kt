package com.example.midiventaslvlup.network.dto

data class RegisterResponse(
    val message: String,
    val userId: Long? = null // O el DTO del usuario recién creado, según tu backend
)
