package com.example.midiventaslvlup.network.dto

import com.google.gson.annotations.SerializedName

data class OrderItemDto(
    @SerializedName("id")
    val id: Long,

    @SerializedName("productId")
    val productId: Long,

    @SerializedName("productName")
    val productName: String,

    @SerializedName("quantity")
    val quantity: Int,

    @SerializedName("unitPrice")
    val unitPrice: Int,

    @SerializedName("subtotal")
    val subtotal: Int
)

data class OrderDto(
    @SerializedName("id")
    val id: Long,

    @SerializedName("userId")
    val userId: Long,

    @SerializedName("total")
    val total: Int,

    @SerializedName("descuento")
    val descuento: Int,

    @SerializedName("codigoCupon")
    val codigoCupon: String?,

    @SerializedName("metodoPago")
    val metodoPago: String,

    @SerializedName("estado")
    val estado: String,

    @SerializedName("direccionEnvio")
    val direccionEnvio: String,

    @SerializedName("items")
    val items: List<OrderItemDto>,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("updatedAt")
    val updatedAt: String
)

//  ELIMINADO - Ya existe en CreateOrderRequest.kt