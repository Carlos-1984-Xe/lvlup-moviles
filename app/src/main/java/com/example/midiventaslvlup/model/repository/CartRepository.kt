package com.example.midiventaslvlup.model.repository


import com.example.midiventaslvlup.network.ApiService
import com.example.midiventaslvlup.network.RetrofitClient  // ✅ AGREGAR IMPORT
import com.example.midiventaslvlup.network.dto.CartDto
import com.example.midiventaslvlup.network.dto.AddToCartRequest
import com.example.midiventaslvlup.network.dto.UpdateQuantityRequest
import com.example.midiventaslvlup.network.dto.CartItemDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CartRepository(private val apiService: ApiService = RetrofitClient.apiService) {



    /**
     * Obtener carrito del usuario
     */
    suspend fun getCart(userId: Long): Result<CartDto?> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getCart(userId)
            if (response.success) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Error al obtener carrito"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtener detalles completos del carrito
     */
    suspend fun getCartDetails(userId: Long): Result<CartDto> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getCartDetails(userId)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Error al obtener carrito"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Agregar producto al carrito
     */
    suspend fun addProductToCart(
        userId: Long,
        productId: Long,
        quantity: Int = 1
    ): Result<CartDto> = withContext(Dispatchers.IO) {
        try {
            val request = AddToCartRequest(productId, quantity)
            val response = apiService.addProductToCart(userId, request)

            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Error al agregar producto"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Aumentar cantidad de un producto
     */
    suspend fun increaseQuantity(
        userId: Long,
        productId: Long
    ): Result<CartDto> = withContext(Dispatchers.IO) {
        try {
            val request = mapOf("productId" to productId)
            val response = apiService.increaseQuantity(userId, request)

            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Error al aumentar cantidad"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Disminuir cantidad de un producto
     */
    suspend fun decreaseQuantity(
        userId: Long,
        productId: Long
    ): Result<CartDto> = withContext(Dispatchers.IO) {
        try {
            val request = mapOf("productId" to productId)
            val response = apiService.decreaseQuantity(userId, request)

            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Error al disminuir cantidad"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualizar cantidad exacta de un producto
     */
    suspend fun updateQuantity(
        userId: Long,
        productId: Long,
        quantity: Int
    ): Result<CartDto> = withContext(Dispatchers.IO) {
        try {
            val request = UpdateQuantityRequest(productId, quantity)
            val response = apiService.updateQuantity(userId, request)

            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Error al actualizar cantidad"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Eliminar producto del carrito
     */
    suspend fun removeProductFromCart(
        userId: Long,
        productId: Long
    ): Result<CartDto> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.removeProductFromCart(userId, productId)

            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Error al eliminar producto"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Vaciar carrito
     */
    suspend fun clearCart(userId: Long): Result<CartDto> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.clearCart(userId)

            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Error al vaciar carrito"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtener total de items en el carrito
     */
    suspend fun getCartItemCount(userId: Long): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getCartItemCount(userId)

            if (response.success) {
                // El backend devuelve { success: true, count: X }
                Result.success(response.count ?: 0)
            } else {
                Result.failure(Exception(response.message ?: "Error al contar items"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtener items del carrito
     */
    suspend fun getCartItems(userId: Long): Result<List<CartItemDto>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getCartItems(userId)

            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Error al obtener items"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}