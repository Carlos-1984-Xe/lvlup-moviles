package com.example.midiventaslvlup.network.dto

import com.google.gson.annotations.SerializedName

/**
 * Clase genérica para mapear la estructura de respuesta estándar del backend.
 *
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
