package com.example.midiventaslvlup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.midiventaslvlup.network.dto.ProductDto
import com.example.midiventaslvlup.model.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductViewModel(private val productRepository: ProductRepository) : ViewModel() {

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Estado de error
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Categoría seleccionada
    private val _selectedCategory = MutableStateFlow("Todos")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // Lista de categorías
    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    // Lista de productos
    private val _products = MutableStateFlow<List<ProductDto>>(emptyList())
    val products: StateFlow<List<ProductDto>> = _products.asStateFlow()

    // Producto encontrado (para búsquedas específicas)
    private val _foundProduct = MutableStateFlow<ProductDto?>(null)
    val foundProduct: StateFlow<ProductDto?> = _foundProduct.asStateFlow()

    init {
        loadCategories()
        //loadProducts() los productos se cargaran cuando se seleccione una categoria esto esta mal.
    }

    /**
     * Cargar categorías
     */
    fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            productRepository.getAllCategories()
                .onSuccess { categories ->
                    _categories.value = categories
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Error al cargar categorías"
                }

            _isLoading.value = false
        }
    }

    /**
     * Cargar productos según la categoría seleccionada
     */
    fun loadProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val result = if (_selectedCategory.value == "Todos") {
                productRepository.getAllProducts()
            } else {
                productRepository.getProductsByCategory(_selectedCategory.value)
            }

            result.onSuccess { products ->
                _products.value = products
            }.onFailure { exception ->
                _error.value = exception.message ?: "Error al cargar productos"
                _products.value = emptyList()
            }

            _isLoading.value = false
        }
    }

    /**
     * Seleccionar categoría y recargar productos
     */
    fun selectCategory(category: String) {
        if (_selectedCategory.value != category) {
            _selectedCategory.value = category
            loadProducts()
        }
    }

    /**
     * Buscar producto por ID
     */
    fun findProductById(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            productRepository.getProductById(id)
                .onSuccess { product ->
                    _foundProduct.value = product
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Producto no encontrado"
                    _foundProduct.value = null
                }

            _isLoading.value = false
        }
    }

    /**
     * Buscar productos por nombre
     */
    fun searchProducts(name: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            productRepository.searchProducts(name)
                .onSuccess { products ->
                    _products.value = products
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Error al buscar productos"
                    _products.value = emptyList()
                }

            _isLoading.value = false
        }
    }

    /**
     * Obtener productos con stock
     */
    fun loadProductsWithStock() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            productRepository.getProductsWithStock()
                .onSuccess { products ->
                    _products.value = products
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Error al cargar productos"
                    _products.value = emptyList()
                }

            _isLoading.value = false
        }
    }

    /**
     * Limpiar producto encontrado
     */
    fun clearFoundProduct() {
        _foundProduct.value = null
    }

    /**
     * Limpiar error
     */
    fun clearError() {
        _error.value = null
    }

    // ========================================
    // MÉTODOS DE ADMINISTRACIÓN (CRUD)
    // ========================================

    /**
     * Crear un nuevo producto
     */
    suspend fun createProduct(
        nombre: String,
        categoria: String,
        imagen: String,
        descripcion: String,
        precio: Int,
        stock: Int
    ): Result<ProductDto> {
        _isLoading.value = true
        _error.value = null

        val result = productRepository.createProduct(
            nombre = nombre,
            categoria = categoria,
            imagen = imagen,
            descripcion = descripcion,
            precio = precio,
            stock = stock
        )

        result.onSuccess {
            // Recargar productos después de crear
            loadProducts()
        }.onFailure { exception ->
            _error.value = exception.message
        }

        _isLoading.value = false
        return result
    }

    /**
     * Actualizar un producto
     */
    suspend fun updateProduct(
        productId: Long,
        nombre: String,
        categoria: String,
        imagen: String,
        descripcion: String,
        precio: Int,
        stock: Int
    ): Result<ProductDto> {
        _isLoading.value = true
        _error.value = null

        val result = productRepository.updateProduct(
            productId = productId,
            nombre = nombre,
            categoria = categoria,
            imagen = imagen,
            descripcion = descripcion,
            precio = precio,
            stock = stock
        )

        result.onSuccess {
            // Recargar productos después de actualizar
            loadProducts()
        }.onFailure { exception ->
            _error.value = exception.message
        }

        _isLoading.value = false
        return result
    }

    /**
     * Eliminar un producto
     */
    suspend fun deleteProduct(productId: Long): Result<Unit> {
        _isLoading.value = true
        _error.value = null

        val result = productRepository.deleteProduct(productId)

        result.onSuccess {
            // Recargar productos después de eliminar
            loadProducts()
        }.onFailure { exception ->
            _error.value = exception.message
        }

        _isLoading.value = false
        return result
    }
}

class ProductViewModelFactory(
    private val productRepository: ProductRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductViewModel(productRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}