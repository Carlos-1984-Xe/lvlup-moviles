package com.example.midiventaslvlup.network.dto

data class OrderItem(
    val id: String,
    val productId: String,
    val productName: String,
    val quantity: Int,
    val unitPrice: Double,
    val imageUrl: String? = null
)
