package com.example.midiventaslvlup.network.dto

import com.example.midiventaslvlup.model.enums.OrderStatus

data class Order(
    val id: String,
    val userId: String,
    val items: List<OrderItem>,
    val total: Double,
    val status: OrderStatus,
    val orderDate: Long // O String, dependiendo de cómo lo manejes en el backend (timestamp o ISO 8601)
)
