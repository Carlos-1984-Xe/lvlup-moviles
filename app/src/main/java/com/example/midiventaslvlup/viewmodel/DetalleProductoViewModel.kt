package com.example.midiventaslvlup.viewmodel

// viewmodel/DetalleProductoViewModel.kt (NUEVA VERSIÓN CON RETROFIT)


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.midiventaslvlup.network.dto.ProductDto
import com.example.midiventaslvlup.model.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetalleProductoViewModel(
    private val productRepository: ProductRepository,
    private val productId: Long
) : ViewModel() {

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Estado de error
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Producto
    private val _product = MutableStateFlow<ProductDto?>(null)
    val product: StateFlow<ProductDto?> = _product.asStateFlow()

    init {
        loadProduct()
    }

    /**
     * Cargar producto desde el servidor
     */
    private fun loadProduct() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            productRepository.getProductById(productId)
                .onSuccess { product ->
                    _product.value = product
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Error al cargar producto"
                    _product.value = null
                }

            _isLoading.value = false
        }
    }

    /**
     * Recargar producto
     */
    fun refreshProduct() {
        loadProduct()
    }

    /**
     * Limpiar error
     */
    fun clearError() {
        _error.value = null
    }
}

class DetalleProductoViewModelFactory(
    private val productRepository: ProductRepository,
    private val productId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetalleProductoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetalleProductoViewModel(productRepository, productId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}