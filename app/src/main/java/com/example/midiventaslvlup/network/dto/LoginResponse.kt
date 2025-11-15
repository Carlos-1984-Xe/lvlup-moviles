package com.example.midiventaslvlup.network.dto

import com.example.midiventaslvlup.model.enums.UserRole

data class LoginResponse(
    val token: String,
    val userId: Long,
    val correo: String,
    val nombre: String,
    val rol: UserRole
)
