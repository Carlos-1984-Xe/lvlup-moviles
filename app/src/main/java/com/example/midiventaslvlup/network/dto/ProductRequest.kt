package com.example.midiventaslvlup.network.dto

/**
 * DTO para enviar datos al crear o actualizar un producto.
 * Esto reemplaza el uso de Map<String, Any> que causa errores en Retrofit.
 */
data class ProductRequest(
    val nombre: String,
    val categoria: String,
    val imagen: String,
    val descripcion: String,
    val precio: Int,
    val stock: Int
)
