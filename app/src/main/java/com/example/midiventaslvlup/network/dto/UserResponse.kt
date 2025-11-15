package com.example.midiventaslvlup.network.dto

data class UserResponse(
    val id: Long,
    val nombre: String,
    val apellido: String,
    val correo: String,
    val rut: String? = null,
    val fechaNacimiento: Long? = null,
    val telefono: String? = null,
    val direccion: String? = null,
    val region: String? = null,
    val comuna: String? = null,
    val rol: String? = null
)