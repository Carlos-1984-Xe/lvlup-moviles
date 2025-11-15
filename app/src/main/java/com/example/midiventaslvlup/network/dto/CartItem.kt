package com.example.midiventaslvlup.network.dto

data class CartItem(
    val id: String? = null, // ID del item en el carrito (si lo maneja el backend)
    val productId: String,
    val productName: String,
    val quantity: Int,
    val unitPrice: Double,
    val imageUrl: String? = null
)
