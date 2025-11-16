package com.example.midiventaslvlup.model.repository

import com.example.midiventaslvlup.network.ApiService
import com.example.midiventaslvlup.network.RetrofitClient  // ✅ AGREGAR IMPORT
import com.example.midiventaslvlup.network.dto.CreateOrderRequest
import com.example.midiventaslvlup.network.dto.OrderDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OrderRepository(private val apiService: ApiService = RetrofitClient.apiService) {

    /**
     * Crear una orden desde el carrito
     */
    suspend fun createOrder(
        userId: Long,
        metodoPago: String,
        direccionEnvio: String,
        codigoCupon: String? = null
    ): Result<OrderDto> = withContext(Dispatchers.IO) {
        try {
            val request = CreateOrderRequest(
                userId = userId,
                metodoPago = metodoPago,
                direccionEnvio = direccionEnvio,
                codigoCupon = codigoCupon
            )

            val response = apiService.createOrder(request)

            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Error al crear orden"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Procesar pago y crear orden
     */
    suspend fun processPayment(
        userId: Long,
        metodoPago: String,
        direccionEnvio: String,
        codigoCupon: String? = null
    ): Result<OrderDto> = withContext(Dispatchers.IO) {
        try {
            val request = CreateOrderRequest(
                userId = userId,
                metodoPago = metodoPago,
                direccionEnvio = direccionEnvio,
                codigoCupon = codigoCupon
            )

            val response = apiService.processPayment(request)

            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Error al procesar pago"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtener orden por ID
     */
    suspend fun getOrderById(orderId: Long): Result<OrderDto> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getOrderById(orderId)

            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Orden no encontrada"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtener todas las órdenes de un usuario
     */
    suspend fun getOrdersByUser(userId: Long): Result<List<OrderDto>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getOrdersByUser(userId)

            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Error al obtener órdenes"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtener historial completo de órdenes
     */
    suspend fun getUserOrderHistory(userId: Long): Result<Map<String, Any>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getUserOrderHistory(userId)

            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Error al obtener historial"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}