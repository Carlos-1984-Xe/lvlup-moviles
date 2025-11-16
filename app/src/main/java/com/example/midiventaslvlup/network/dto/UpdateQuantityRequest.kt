package com.example.midiventaslvlup.network.dto

data class UpdateQuantityRequest(
    val productId: Long,
    val quantity: Int
)