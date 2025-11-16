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

    @SerializedName("data")
    val data: T? = null,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("count")
    val count: Int? = null
)
