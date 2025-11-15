package com.example.midiventaslvlup.network.dto

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String? = null,
    val stock: Int
)
