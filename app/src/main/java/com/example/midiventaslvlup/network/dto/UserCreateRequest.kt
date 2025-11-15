package com.example.midiventaslvlup.network.dto

data class UserCreateRequest(
    val nombre: String,
    val apellido: String,
    val correo: String,
    val contrasena: String,
    val rut: String? = null,
    val fechaNacimiento: Long? = null,
    val telefono: String? = null,
    val direccion: String? = null,
    val region: String? = null,
    val comuna: String? = null,
    val rol: String? = null
)