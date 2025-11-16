package com.example.midiventaslvlup.network.dto

import com.google.gson.annotations.SerializedName

data class CartItemDto(
    @SerializedName("id")
    val id: Long?,

    @SerializedName("productId")
    val productId: Long,

    @SerializedName("productName")
    val productName: String,

    @SerializedName("productImage")
    val productImage: String,

    @SerializedName("productCategory")
    val productCategory: String,

    @SerializedName("productDescription")
    val productDescription: String,

    @SerializedName("quantity")
    val quantity: Int,

    @SerializedName("unitPrice")
    val unitPrice: Int,

    @SerializedName("subtotal")
    val subtotal: Int
)

data class CartDto(
    @SerializedName("id")
    val id: Long?,

    @SerializedName("userId")
    val userId: Long,

    @SerializedName("items")
    val items: List<CartItemDto>,

    @SerializedName("total")
    val total: Int,

    @SerializedName("itemCount")
    val itemCount: Int
)

// ELIMINADAS - Ya existen en sus propios archivos
// AddToCartRequest.kt
// UpdateQuantityRequest.kt