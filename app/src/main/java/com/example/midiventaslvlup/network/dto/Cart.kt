package com.example.midiventaslvlup.network.dto

data class Cart(
    val id: String,
    val userId: String,
    val items: List<CartItem>,
    val subtotal: Double
)
