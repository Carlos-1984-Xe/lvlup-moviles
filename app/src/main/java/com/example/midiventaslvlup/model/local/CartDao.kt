package com.example.midiventaslvlup.model.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    // Obtener todos los items del carrito
    @Query("SELECT * FROM cart_items ORDER BY id DESC")
    fun getAllCartItems(): Flow<List<CartItemEntity>>

    // Obtener un item específico del carrito
    @Query("SELECT * FROM cart_items WHERE id = :id")
    suspend fun getCartItemById(id: Int): CartItemEntity?

    // Verificar si un producto ya está en el carrito
    @Query("SELECT * FROM cart_items WHERE productoId = :productoId LIMIT 1")
    suspend fun getCartItemByProductoId(productoId: Int): CartItemEntity?

    // Insertar un item al carrito
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItemEntity)

    // Actualizar un item del carrito (para cambiar cantidad)
    @Update
    suspend fun updateCartItem(item: CartItemEntity)

    // Eliminar un item del carrito
    @Delete
    suspend fun deleteCartItem(item: CartItemEntity)

    // Vaciar todo el carrito
    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

    // Obtener el total del carrito
    @Query("SELECT SUM(subtotal) FROM cart_items")
    fun getCartTotal(): Flow<Int?>

    // Obtener la cantidad de items en el carrito
    @Query("SELECT COUNT(*) FROM cart_items")
    fun getCartItemCount(): Flow<Int>
}