package com.example.midiventaslvlup.network.dto

data class CreateOrderRequest(
    val userId: Long,
    val metodoPago: String,
    val direccionEnvio: String,
    val codigoCupon: String? = null
)