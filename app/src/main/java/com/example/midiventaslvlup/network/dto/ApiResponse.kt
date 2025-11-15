package com.example.midiventaslvlup.network.dto

import com.google.gson.annotations.SerializedName

/**
 * Clase genérica para mapear la estructura de respuesta estándar del backend.
 * {
 *   "success": true,
 *   "message": "Mensaje de éxito",
 *   "data": { ... }  // El objeto de datos real
 * }
 */
data class ApiResponse<T>(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: T? // Los datos pueden ser nulos si la petición falla
)
