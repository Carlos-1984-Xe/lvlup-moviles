package com.example.midiventaslvlup.network.dto

data class AddToCartRequest(
    val productId: Long,
    val quantity: Int
)