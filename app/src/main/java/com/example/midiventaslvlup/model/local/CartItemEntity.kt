package com.example.midiventaslvlup.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val productoId: Int,  // ID del producto original
    val nombre: String,
    val categoria: String,
    val imagen: String,
    val descripcion: String,
    val precio: Int,
    val cantidad: Int = 1,  // Cantidad de este producto en el carrito
    val subtotal: Int = precio * cantidad  // Precio * cantidad
)