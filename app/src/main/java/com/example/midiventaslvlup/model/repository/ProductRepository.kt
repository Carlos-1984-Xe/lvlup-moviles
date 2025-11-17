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
 *
 * @param apiService Servicio de API para hacer las peticiones HTTP
 * @param dispatcher Dispatcher para ejecutar las corrutinas (IO en producción, Test en pruebas)
 *                   Valor por defecto: Dispatchers.IO (operaciones de red/base de datos)
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
                Result.failure(Exception(response.message ?: "Error al obtener productos"))
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
                Result.failure(Exception(response.message ?: "Error al obtener productos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtener producto por ID
     *
     * @param id ID único del producto a buscar
     * @return Result con el ProductDto encontrado o un error
     */
    suspend fun getProductById(id: Long): Result<ProductDto> = withContext(dispatcher) {
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
     *
     * @param categoria Nombre de la categoría ("Todos" retorna todos los productos)
     *
     * Lógica especial: Si categoria == "Todos", llama a getAllProducts()
     * sino, filtra por la categoría específica
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
                Result.failure(Exception(response.message ?: "Error al obtener productos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtener todas las categorías
     *
     * Agrega automáticamente "Todos" al inicio de la lista
     * para permitir ver todos los productos sin filtro
     */
    suspend fun getAllCategories(): Result<List<String>> = withContext(dispatcher) {
        try {
            val response = apiService.getAllCategories()
            if (response.success && response.data != null) {
                // Concatenar "Todos" al inicio de las categorías del servidor
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
     *
     * @param name Texto a buscar en el nombre del producto
     * @return Lista de productos que coinciden con el criterio de búsqueda
     */
    suspend fun searchProducts(name: String): Result<List<ProductDto>> = withContext(dispatcher) {
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
     * Crear un nuevo producto (Admin)
     *
     * Requiere permisos de administrador en el backend
     *
     * @param nombre Nombre del producto
     * @param categoria Categoría a la que pertenece
     * @param imagen URL de la imagen del producto
     * @param descripcion Descripción detallada
     * @param precio Precio en la moneda configurada
     * @param stock Cantidad disponible en inventario
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
                Result.failure(Exception(response.message ?: "Error al crear producto"))
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
     *
     * @param productId ID del producto a actualizar
     * @param nombre Nuevo nombre del producto
     * @param categoria Nueva categoría
     * @param imagen Nueva URL de imagen
     * @param descripcion Nueva descripción
     * @param precio Nuevo precio
     * @param stock Nueva cantidad en stock
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
                Result.failure(Exception(response.message ?: "Error al actualizar producto"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Eliminar un producto (Admin)
     *
     * Requiere permisos de administrador en el backend
     * Operación irreversible - el producto se elimina permanentemente
     *
     * @param productId ID del producto a eliminar
     * @return Result<Unit> - éxito sin datos o error con mensaje
     */
    suspend fun deleteProduct(productId: Long): Result<Unit> = withContext(dispatcher) {
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