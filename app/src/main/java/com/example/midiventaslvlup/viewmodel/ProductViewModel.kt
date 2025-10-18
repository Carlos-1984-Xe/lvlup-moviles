package com.example.midiventaslvlup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.midiventaslvlup.model.local.ExpenseEntity
import com.example.midiventaslvlup.model.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProductViewModel(private val productRepository: ProductRepository) : ViewModel() {

    private val _selectedCategory = MutableStateFlow("Todos")
    val selectedCategory = _selectedCategory.asStateFlow()

    val categories: StateFlow<List<String>> = productRepository.getAllCategories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val products: StateFlow<List<ExpenseEntity>> = _selectedCategory.flatMapLatest { category ->
        if (category == "Todos") {
            productRepository.getAllProducts()
        } else {
            productRepository.getProductsByCategory(category)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _foundProduct = MutableStateFlow<ExpenseEntity?>(null)
    val foundProduct = _foundProduct.asStateFlow()

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun findProductById(id: Int) {
        viewModelScope.launch {
            _foundProduct.value = productRepository.findProductById(id)
        }
    }

    suspend fun findProductByIdOnce(id: Int): ExpenseEntity? {
        return productRepository.findProductById(id)
    }

    fun clearFoundProduct() {
        _foundProduct.value = null
    }

    suspend fun createProduct(product: ExpenseEntity) {
        productRepository.insertProduct(product)
    }

    suspend fun updateProduct(product: ExpenseEntity) {
        productRepository.updateProduct(product)
    }

    suspend fun deleteProduct(product: ExpenseEntity) {
        productRepository.deleteProduct(product)
    }
}

class ProductViewModelFactory(private val productRepository: ProductRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductViewModel(productRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
