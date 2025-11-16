package com.example.midiventaslvlup.network.dto


import com.google.gson.annotations.SerializedName

data class ProductDto(
    @SerializedName("id")
    val id: Long,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("categoria")
    val categoria: String,

    @SerializedName("imagen")
    val imagen: String,

    @SerializedName("descripcion")
    val descripcion: String,

    @SerializedName("precio")
    val precio: Int,

    @SerializedName("stock")
    val stock: Int,

    @SerializedName("createdAt")
    val createdAt: String? = null,

    @SerializedName("updatedAt")
    val updatedAt: String? = null
)