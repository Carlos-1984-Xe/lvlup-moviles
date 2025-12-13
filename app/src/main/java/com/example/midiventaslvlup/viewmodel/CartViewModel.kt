package com.example.midiventaslvlup.viewmodel

// viewmodel/CartViewModel.kt (NUEVA VERSIÓN CON RETROFIT)

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.midiventaslvlup.network.dto.CartDto
import com.example.midiventaslvlup.network.dto.CartItemDto
import com.example.midiventaslvlup.model.repository.CartRepository
import com.example.midiventaslvlup.model.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CartViewModel(
    application: Application,
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val userId: Long // ID del usuario logueado
) : AndroidViewModel(application) {

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Estado de error
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Carrito completo
    private val _cart = MutableStateFlow<CartDto?>(null)
    val cart: StateFlow<CartDto?> = _cart.asStateFlow()

    // Items del carrito
    private val _cartItems = MutableStateFlow<List<CartItemDto>>(emptyList())
    val cartItems: StateFlow<List<CartItemDto>> = _cartItems.asStateFlow()

    // Total del carrito
    private val _cartTotal = MutableStateFlow(0)
    val cartTotal: StateFlow<Int> = _cartTotal.asStateFlow()

    // Contador de items
    private val _itemCount = MutableStateFlow(0)
    val itemCount: StateFlow<Int> = _itemCount.asStateFlow()

    // Estado del cupón
    private val _couponCode = MutableStateFlow("")
    val couponCode: StateFlow<String> = _couponCode.asStateFlow()

    private val _discount = MutableStateFlow(0)
    val discount: StateFlow<Int> = _discount.asStateFlow()

    // Total final con descuento
    private val _finalTotal = MutableStateFlow(0)
    val finalTotal: StateFlow<Int> = _finalTotal.asStateFlow()

    init {
        loadCart()
    }

    /**
     * Cargar carrito desde el servidor
     */
    fun loadCart() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            cartRepository.getCart(userId)
                .onSuccess { cartDto ->
                    _cart.value = cartDto
                    _cartItems.value = cartDto?.items ?: emptyList()
                    _cartTotal.value = cartDto?.getTotal() ?: 0  // ✅ CAMBIO
                    _itemCount.value = cartDto?.getItemCount() ?: 0  // ✅ CAMBIO
                    calculateFinalTotal()
                }
                .onFailure { exception ->
                    _error.value = exception.message
                    _cart.value = null
                    _cartItems.value = emptyList()
                    _cartTotal.value = 0
                    _itemCount.value = 0
                }

            _isLoading.value = false
        }
    }

    /**
     * Agregar producto al carrito
     */
    fun addToCart(productId: Long, quantity: Int = 1) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            cartRepository.addProductToCart(userId, productId, quantity)
                .onSuccess { cartDto ->
                    _cart.value = cartDto
                    _cartItems.value = cartDto.items
                    _cartTotal.value = cartDto.getTotal()  // ✅ CAMBIO
                    _itemCount.value = cartDto.getItemCount()  // ✅ CAMBIO
                    calculateFinalTotal()

                    Toast.makeText(
                        getApplication(),
                        "Producto agregado al carrito",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .onFailure { exception ->
                    _error.value = exception.message
                    Toast.makeText(
                        getApplication(),
                        "No se pudo agregar el producto al carrito. ${exception.message ?: "Intente nuevamente"}",
                        Toast.LENGTH_LONG
                    ).show()
                }

            _isLoading.value = false
        }
    }

    /**
     * Aumentar cantidad de un producto
     */
    fun increaseQuantity(productId: Long) {
        viewModelScope.launch {
            _error.value = null

            cartRepository.increaseQuantity(userId, productId)
                .onSuccess { cartDto ->
                    _cart.value = cartDto
                    _cartItems.value = cartDto.items
                    _cartTotal.value = cartDto.getTotal()  // ✅ CAMBIO
                    _itemCount.value = cartDto.getItemCount()  // ✅ CAMBIO
                    calculateFinalTotal()
                }
                .onFailure { exception ->
                    _error.value = exception.message
                    Toast.makeText(
                        getApplication(),
                        "No se pudo aumentar la cantidad. ${exception.message ?: "Verifique el stock disponible"}",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
    }

    /**
     * Disminuir cantidad de un producto
     */
    fun decreaseQuantity(productId: Long) {
        viewModelScope.launch {
            _error.value = null

            cartRepository.decreaseQuantity(userId, productId)
                .onSuccess { cartDto ->
                    _cart.value = cartDto
                    _cartItems.value = cartDto.items
                    _cartTotal.value = cartDto.getTotal()  // ✅ CAMBIO
                    _itemCount.value = cartDto.getItemCount()  // ✅ CAMBIO
                    calculateFinalTotal()
                }
                .onFailure { exception ->
                    _error.value = exception.message
                    Toast.makeText(
                        getApplication(),
                        "No se pudo disminuir la cantidad. ${exception.message ?: "Intente nuevamente"}",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
    }

    /**
     * Actualizar cantidad exacta
     */
    fun updateQuantity(productId: Long, quantity: Int) {
        viewModelScope.launch {
            _error.value = null

            cartRepository.updateQuantity(userId, productId, quantity)
                .onSuccess { cartDto ->
                    _cart.value = cartDto
                    _cartItems.value = cartDto.items
                    _cartTotal.value = cartDto.getTotal()  // ✅ CAMBIO
                    _itemCount.value = cartDto.getItemCount()  // ✅ CAMBIO
                    calculateFinalTotal()
                }
                .onFailure { exception ->
                    _error.value = exception.message
                    Toast.makeText(
                        getApplication(),
                        "No se pudo actualizar la cantidad. ${exception.message ?: "Verifique el valor ingresado"}",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
    }

    /**
     * Eliminar producto del carrito
     */
    fun removeFromCart(productId: Long) {
        viewModelScope.launch {
            _error.value = null

            cartRepository.removeProductFromCart(userId, productId)
                .onSuccess { cartDto ->
                    _cart.value = cartDto
                    _cartItems.value = cartDto.items
                    _cartTotal.value = cartDto.getTotal()  // ✅ CAMBIO
                    _itemCount.value = cartDto.getItemCount()  // ✅ CAMBIO
                    calculateFinalTotal()

                    Toast.makeText(
                        getApplication(),
                        "Producto eliminado del carrito",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .onFailure { exception ->
                    _error.value = exception.message
                    Toast.makeText(
                        getApplication(),
                        "No se pudo eliminar el producto del carrito. ${exception.message ?: "Intente nuevamente"}",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
    }

    /**
     * Vaciar carrito
     */
    fun clearCart() {
        viewModelScope.launch {
            _error.value = null

            cartRepository.clearCart(userId)
                .onSuccess { cartDto ->
                    _cart.value = cartDto
                    _cartItems.value = emptyList()
                    _cartTotal.value = 0
                    _itemCount.value = 0
                    _couponCode.value = ""
                    _discount.value = 0
                    calculateFinalTotal()

                    Toast.makeText(
                        getApplication(),
                        "Carrito vaciado",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .onFailure { exception ->
                    _error.value = exception.message
                    Toast.makeText(
                        getApplication(),
                        "No se pudo vaciar el carrito. ${exception.message ?: "Intente nuevamente"}",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
    }

    /**
     * Actualizar código de cupón
     */
    fun updateCouponCode(code: String) {
        _couponCode.value = code
    }

    /**
     * Aplicar cupón
     */
    fun applyCoupon() {
        val code = _couponCode.value.trim().uppercase()

        _discount.value = when (code) {
            "DESCUENTO10" -> (_cartTotal.value * 0.10).toInt()
            "DESCUENTO20" -> (_cartTotal.value * 0.20).toInt()
            "PRIMERACOMPRA" -> (_cartTotal.value * 0.15).toInt()
            "LVLUP50" -> 5000
            else -> 0
        }

        calculateFinalTotal()

        if (_discount.value > 0) {
            Toast.makeText(
                getApplication(),
                "Cupón aplicado: -$${_discount.value}",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                getApplication(),
                "Cupón inválido",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Calcular total final
     */
    private fun calculateFinalTotal() {
        _finalTotal.value = (_cartTotal.value - _discount.value).coerceAtLeast(0)
    }

    /**
     * Procesar pago y crear orden
     */
    fun processPayment(
        metodoPago: String,
        direccionEnvio: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val cupon = if (_discount.value > 0) _couponCode.value else null

            orderRepository.processPayment(
                userId = userId,
                metodoPago = metodoPago,
                direccionEnvio = direccionEnvio,
                codigoCupon = cupon
            ).onSuccess { order ->
                // Limpiar carrito local
                _cart.value = null
                _cartItems.value = emptyList()
                _cartTotal.value = 0
                _itemCount.value = 0
                _couponCode.value = ""
                _discount.value = 0
                _finalTotal.value = 0

                Toast.makeText(
                    getApplication(),
                    "¡Compra realizada exitosamente! Su pedido ha sido procesado",
                    Toast.LENGTH_LONG
                ).show()

                onSuccess()
            }.onFailure { exception ->
                _error.value = exception.message
                Toast.makeText(
                    getApplication(),
                    "No se pudo procesar su compra. ${exception.message ?: "Verifique sus datos e intente nuevamente"}",
                    Toast.LENGTH_LONG
                ).show()
            }

            _isLoading.value = false
        }
    }

    /**
     * Limpiar error
     */
    fun clearError() {
        _error.value = null
    }
}

class CartViewModelFactory(
    private val application: Application,
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val userId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CartViewModel(
                application = application,
                cartRepository = cartRepository,
                orderRepository = orderRepository,
                userId = userId
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}