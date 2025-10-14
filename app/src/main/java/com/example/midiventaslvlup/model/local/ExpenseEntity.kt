package com.example.midiventaslvlup.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val categoria: String,
    val imagen: String,
    val descripcion: String,
    val precio: Int,
    val stock: Int
)
