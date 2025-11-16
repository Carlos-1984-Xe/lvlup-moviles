package com.example.midiventaslvlup.model.repository

import com.example.midiventaslvlup.network.ApiService
import com.example.midiventaslvlup.network.RetrofitClient  // ✅ AGREGAR IMPORT
import com.example.midiventaslvlup.network.dto.ProductDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductRepository(private val apiService: ApiService = RetrofitClient.apiService) {

    /**
     * Obtener todos los productos
     */
    suspend fun getAllProducts(): Result<List<ProductDto>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getAllProducts()
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Error al obtener productos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtener productos con stock disponible
     */
    suspend fun getProductsWithStock(): Result<List<ProductDto>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getProductsWithStock()
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Error al obtener productos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtener producto por ID
     */
    suspend fun getProductById(id: Long): Result<ProductDto> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getProductById(id)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Producto no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtener productos por categoría
     */
    suspend fun getProductsByCategory(categoria: String): Result<List<ProductDto>> = withContext(Dispatchers.IO) {
        try {
            val response = if (categoria == "Todos") {
                apiService.getAllProducts()
            } else {
                apiService.getProductsByCategory(categoria)
            }

            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Error al obtener productos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtener todas las categorías
     */
    suspend fun getAllCategories(): Result<List<String>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getAllCategories()
            if (response.success && response.data != null) {
                // Agregar "Todos" al inicio
                val categoriesWithAll = listOf("Todos") + response.data
                Result.success(categoriesWithAll)
            } else {
                Result.failure(Exception(response.message ?: "Error al obtener categorías"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Buscar productos por nombre
     */
    suspend fun searchProducts(name: String): Result<List<ProductDto>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.searchProducts(name)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Error al buscar productos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtener productos por rango de precio
     */
    suspend fun getProductsByPriceRange(min: Int, max: Int): Result<List<ProductDto>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getProductsByPriceRange(min, max)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Error al obtener productos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========================================
    // MÉTODOS DE ADMINISTRACIÓN (CRUD)
    // ========================================

    /**
     * Crear un nuevo producto (Admin)
     */
    suspend fun createProduct(
        nombre: String,
        categoria: String,
        imagen: String,
        descripcion: String,
        precio: Int,
        stock: Int
    ): Result<ProductDto> = withContext(Dispatchers.IO) {
        try {
            val productData = mapOf(
                "nombre" to nombre,
                "categoria" to categoria,
                "imagen" to imagen,
                "descripcion" to descripcion,
                "precio" to precio,
                "stock" to stock
            )

            val response = apiService.createProduct(productData)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Error al crear producto"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualizar un producto existente (Admin)
     */
    suspend fun updateProduct(
        productId: Long,
        nombre: String,
        categoria: String,
        imagen: String,
        descripcion: String,
        precio: Int,
        stock: Int
    ): Result<ProductDto> = withContext(Dispatchers.IO) {
        try {
            val productData = mapOf(
                "nombre" to nombre,
                "categoria" to categoria,
                "imagen" to imagen,
                "descripcion" to descripcion,
                "precio" to precio,
                "stock" to stock
            )

            val response = apiService.updateProduct(productId, productData)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Error al actualizar producto"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualizar solo el stock de un producto
     */
    suspend fun updateProductStock(
        productId: Long,
        newStock: Int
    ): Result<ProductDto> = withContext(Dispatchers.IO) {
        try {
            val stockData = mapOf("stock" to newStock)
            val response = apiService.updateProductStock(productId, stockData)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Error al actualizar stock"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Eliminar un producto (Admin)
     */
    suspend fun deleteProduct(productId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.deleteProduct(productId)
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message ?: "Error al eliminar producto"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}