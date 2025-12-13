package com.example.midiventaslvlup.model.repository

import com.example.midiventaslvlup.network.ApiService
import com.example.midiventaslvlup.network.RetrofitClient
import com.example.midiventaslvlup.network.dto.ProductDto
import com.example.midiventaslvlup.network.dto.ProductRequest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repositorio para manejar las operaciones de productos
 */
class ProductRepository(
    private val apiService: ApiService = RetrofitClient.apiService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO  // 🧪 Inyectable para tests
) {

    /**
     * Obtener todos los productos
     *
     * withContext(dispatcher) cambia el contexto de ejecución a IO thread
     * para no bloquear el UI thread con operaciones de red
     */
    suspend fun getAllProducts(): Result<List<ProductDto>> = withContext(dispatcher) {
        try {
            val response = apiService.getAllProducts()
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "No se pudieron cargar los productos. Intente nuevamente"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtener productos con stock disponible
     *
     * Útil para mostrar solo productos que pueden ser comprados
     */
    suspend fun getProductsWithStock(): Result<List<ProductDto>> = withContext(dispatcher) {
        try {
            val response = apiService.getProductsWithStock()
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "No se pudieron cargar los productos disponibles. Intente nuevamente"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtener producto por ID
     */
    suspend fun getProductById(id: Long): Result<ProductDto> = withContext(dispatcher) {
        try {
            val response = apiService.getProductById(id)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "No se encontró el producto solicitado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtener productos por categoría
     *
     */
    suspend fun getProductsByCategory(categoria: String): Result<List<ProductDto>> = withContext(dispatcher) {
        try {
            val response = if (categoria == "Todos") {
                apiService.getAllProducts()
            } else {
                apiService.getProductsByCategory(categoria)
            }

            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "No se pudieron cargar los productos de esta categoría"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtener todas las categorías
     *
     */
    suspend fun getAllCategories(): Result<List<String>> = withContext(dispatcher) {
        try {
            val response = apiService.getAllCategories()
            if (response.success && response.data != null) {
                // Concatenar "Todos" al inicio de las categorías del servidor
                val categoriesWithAll = listOf("Todos") + response.data
                Result.success(categoriesWithAll)
            } else {
                Result.failure(Exception(response.message ?: "No se pudieron cargar las categorías. Intente nuevamente"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Buscar productos por nombre
     */
    suspend fun searchProducts(name: String): Result<List<ProductDto>> = withContext(dispatcher) {
        try {
            val response = apiService.searchProducts(name)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "No se encontraron productos con ese nombre"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Crear un nuevo producto (Admin)
     *
     * Requiere permisos de administrador en el backend

     */
    suspend fun createProduct(
        nombre: String,
        categoria: String,
        imagen: String,
        descripcion: String,
        precio: Int,
        stock: Int
    ): Result<ProductDto> = withContext(dispatcher) {
        try {
            val productRequest = ProductRequest(
                nombre = nombre,
                categoria = categoria,
                imagen = imagen,
                descripcion = descripcion,
                precio = precio,
                stock = stock
            )
            val response = apiService.createProduct(productRequest)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "No se pudo crear el producto. Verifique los datos ingresados"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualizar un producto existente (Admin)
     *
     * Requiere permisos de administrador en el backend
     * Actualiza todos los campos del producto

     */
    suspend fun updateProduct(
        productId: Long,
        nombre: String,
        categoria: String,
        imagen: String,
        descripcion: String,
        precio: Int,
        stock: Int
    ): Result<ProductDto> = withContext(dispatcher) {
        try {
            val productRequest = ProductRequest(
                nombre = nombre,
                categoria = categoria,
                imagen = imagen,
                descripcion = descripcion,
                precio = precio,
                stock = stock
            )
            val response = apiService.updateProduct(productId, productRequest)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "No se pudo actualizar el producto. Verifique los datos ingresados"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun deleteProduct(productId: Long): Result<Unit> = withContext(dispatcher) {
        try {
            val response = apiService.deleteProduct(productId)
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message ?: "No se pudo eliminar el producto. Intente nuevamente"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}