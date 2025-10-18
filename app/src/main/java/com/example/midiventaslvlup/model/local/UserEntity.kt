package com.example.midiventaslvlup.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val apellido: String,
    val correo: String,
    val contrasena: String,
    val telefono: String,
    val fechaNacimiento: Long,
    val direccion: String,
    val rut: String,
    val region: String,
    val comuna: String,
    val rol: String
)