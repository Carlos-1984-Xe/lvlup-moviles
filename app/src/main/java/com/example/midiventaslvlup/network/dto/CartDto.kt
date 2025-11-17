package com.example.midiventaslvlup.network.dto

import com.google.gson.annotations.SerializedName

data class CartItemDto(
    @SerializedName("id")
    val id: Long?,

    @SerializedName("product")
    val product: ProductInCart,  // ✅ CAMBIO: Ahora es un objeto anidado

    @SerializedName("quantity")
    val quantity: Int,

    @SerializedName("unitPrice")
    val unitPrice: Double  // ✅ CAMBIO: Double en lugar de Int
) {
    val productId: Long get() = product.id
    val productName: String get() = product.nombre
    val productImage: String get() = product.imagen
    val productCategory: String get() = product.categoria
    val productDescription: String get() = product.descripcion
    val subtotal: Int get() = (unitPrice * quantity).toInt()
}

data class ProductInCart(
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
    val createdAt: String?,

    @SerializedName("updatedAt")
    val updatedAt: String?
)

data class CartDto(
    @SerializedName("id")
    val id: Long?,

    @SerializedName("userId")
    val userId: Long? = null,  // ✅ Opcional porque tu backend no lo envía siempre

    @SerializedName("items")
    val items: List<CartItemDto>,

    @SerializedName("total")
    val total: Int? = null,  // ✅ Opcional, lo calculamos si no viene

    @SerializedName("itemCount")
    val itemCount: Int? = null  // ✅ Opcional, lo calculamos si no viene
) {
    fun getTotal(): Int = total ?: items.sumOf { it.subtotal }
    fun getItemCount(): Int = itemCount ?: items.size
}