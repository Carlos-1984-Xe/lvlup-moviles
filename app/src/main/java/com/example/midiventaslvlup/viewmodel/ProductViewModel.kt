package com.example.midiventaslvlup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.midiventaslvlup.model.repository.ProductRepository
import com.example.midiventaslvlup.network.dto.ProductDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductViewModel(private val productRepository: ProductRepository) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Todos")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    private val _products = MutableStateFlow<List<ProductDto>>(emptyList())
    val products: StateFlow<List<ProductDto>> = _products.asStateFlow()

    private val _foundProduct = MutableStateFlow<ProductDto?>(null)
    val foundProduct: StateFlow<ProductDto?> = _foundProduct.asStateFlow()

    // Estado para notificar a la UI sobre acciones exitosas (crear, editar, borrar)
    private val _productActionSuccess = MutableStateFlow(false)
    val productActionSuccess: StateFlow<Boolean> = _productActionSuccess.asStateFlow()

    init {
        loadCategories()
        // ✅ HEMOS QUITADO loadProducts() de aquí para evitar la race condition.
        // La vista será ahora responsable de la carga inicial.
    }

    fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            productRepository.getAllCategories()
                .onSuccess { categories -> _categories.value = categories }
                .onFailure { exception -> _error.value = exception.message ?: "Error al cargar categorías" }
            _isLoading.value = false
        }
    }

    fun loadProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = if (_selectedCategory.value == "Todos") {
                productRepository.getAllProducts()
            } else {
                productRepository.getProductsByCategory(_selectedCategory.value)
            }
            result.onSuccess { products -> _products.value = products }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Error al cargar productos"
                    _products.value = emptyList()
                }
            _isLoading.value = false
        }
    }

    fun selectCategory(category: String) {
        if (_selectedCategory.value != category) {
            _selectedCategory.value = category
            loadProducts()
        }
    }

    fun findProductById(id: Long) {
        viewModelScope.launch {
            _error.value = null
            _foundProduct.value = null // Limpiar el estado anterior para mostrar la carga local
            productRepository.getProductById(id)
                .onSuccess { product -> _foundProduct.value = product }
                .onFailure { exception -> _error.value = exception.message ?: "Producto no encontrado" }
        }
    }

    fun searchProducts(name: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            productRepository.searchProducts(name)
                .onSuccess { products -> _products.value = products }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Error al buscar productos"
                    _products.value = emptyList()
                }
            _isLoading.value = false
        }
    }

    fun loadProductsWithStock() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            productRepository.getProductsWithStock()
                .onSuccess { products -> _products.value = products }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Error al cargar productos"
                    _products.value = emptyList()
                }
            _isLoading.value = false
        }
    }

    fun clearFoundProduct() { _foundProduct.value = null }
    fun clearError() { _error.value = null }
    fun resetProductActionSuccess() { _productActionSuccess.value = false }

    // ========================================
    // MÉTODOS DE ADMINISTRACIÓN (CRUD) - CORREGIDOS
    // ========================================

    fun createProduct(nombre: String, categoria: String, imagen: String, descripcion: String, precio: Int, stock: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            productRepository.createProduct(nombre, categoria, imagen, descripcion, precio, stock)
                .onSuccess {
                    loadProducts()
                    _productActionSuccess.value = true
                }
                .onFailure { exception ->
                    _error.value = exception.message
                }
            _isLoading.value = false
        }
    }

    fun updateProduct(productId: Long, nombre: String, categoria: String, imagen: String, descripcion: String, precio: Int, stock: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            productRepository.updateProduct(productId, nombre, categoria, imagen, descripcion, precio, stock)
                .onSuccess {
                    loadProducts()
                    _productActionSuccess.value = true
                }
                .onFailure { exception ->
                    _error.value = exception.message
                }
            _isLoading.value = false
        }
    }

    fun deleteProduct(productId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            productRepository.deleteProduct(productId)
                .onSuccess {
                    loadProducts()
                    _productActionSuccess.value = true
                }
                .onFailure { exception ->
                    _error.value = exception.message
                }
            _isLoading.value = false
        }
    }
}
