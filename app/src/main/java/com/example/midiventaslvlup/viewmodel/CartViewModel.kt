package com.example.midiventaslvlup.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.midiventaslvlup.model.local.AppDatabase
import com.example.midiventaslvlup.model.local.CartItemEntity
import com.example.midiventaslvlup.model.local.ExpenseEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CartViewModel(application: Application) : AndroidViewModel(application) {

    private val cartDao = AppDatabase.getDatabase(application).cartDao()
    private val expenseDao = AppDatabase.getDatabase(application).expenseDao()

    // Estado del carrito
    private val _cartItems = MutableStateFlow<List<CartItemEntity>>(emptyList())
    val cartItems: StateFlow<List<CartItemEntity>> = _cartItems.asStateFlow()

    // Total del carrito
    private val _cartTotal = MutableStateFlow(0)
    val cartTotal: StateFlow<Int> = _cartTotal.asStateFlow()

    // Estado del cupón
    private val _couponCode = MutableStateFlow("")
    val couponCode: StateFlow<String> = _couponCode.asStateFlow()

    private val _discount = MutableStateFlow(0)
    val discount: StateFlow<Int> = _discount.asStateFlow()

    // Total final con descuento
    private val _finalTotal = MutableStateFlow(0)
    val finalTotal: StateFlow<Int> = _finalTotal.asStateFlow()

    init {
        // Observar items del carrito
        viewModelScope.launch {
            cartDao.getAllCartItems().collect { items ->
                _cartItems.value = items
            }
        }

        // Observar total del carrito
        viewModelScope.launch {
            cartDao.getCartTotal().collect { total ->
                _cartTotal.value = total ?: 0
                calculateFinalTotal()
            }
        }
    }

    // Agregar producto al carrito
    fun addToCart(producto: ExpenseEntity) {
        viewModelScope.launch {
            if (producto.stock <= 0) {
                Toast.makeText(getApplication(), "Producto sin stock", Toast.LENGTH_SHORT).show()
                return@launch
            }

            val existingItem = cartDao.getCartItemByProductoId(producto.id)

            if (existingItem != null) {
                if (existingItem.cantidad < producto.stock) {
                    val updatedItem = existingItem.copy(
                        cantidad = existingItem.cantidad + 1,
                        subtotal = producto.precio * (existingItem.cantidad + 1)
                    )
                    cartDao.updateCartItem(updatedItem)
                } else {
                    Toast.makeText(getApplication(), "No puedes agregar más, stock máximo alcanzado", Toast.LENGTH_SHORT).show()
                }
            } else {
                val newItem = CartItemEntity(
                    productoId = producto.id,
                    nombre = producto.nombre,
                    categoria = producto.categoria,
                    imagen = producto.imagen,
                    descripcion = producto.descripcion,
                    precio = producto.precio,
                    cantidad = 1,
                    subtotal = producto.precio
                )
                cartDao.insertCartItem(newItem)
            }
        }
    }

    // Aumentar cantidad de un item
    fun increaseQuantity(item: CartItemEntity) {
        viewModelScope.launch {
            val product = expenseDao.findExpenseById(item.productoId)
            if (product != null && item.cantidad < product.stock) {
                val updatedItem = item.copy(
                    cantidad = item.cantidad + 1,
                    subtotal = item.precio * (item.cantidad + 1)
                )
                cartDao.updateCartItem(updatedItem)
            } else {
                Toast.makeText(getApplication(), "No puedes agregar más, stock máximo alcanzado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Disminuir cantidad de un item
    fun decreaseQuantity(item: CartItemEntity) {
        viewModelScope.launch {
            if (item.cantidad > 1) {
                val updatedItem = item.copy(
                    cantidad = item.cantidad - 1,
                    subtotal = item.precio * (item.cantidad - 1)
                )
                cartDao.updateCartItem(updatedItem)
            } else {
                // Si la cantidad es 1, eliminar el item
                cartDao.deleteCartItem(item)
            }
        }
    }

    // Eliminar un item del carrito
    fun removeFromCart(item: CartItemEntity) {
        viewModelScope.launch {
            cartDao.deleteCartItem(item)
        }
    }

    // Vaciar todo el carrito
    fun clearCart() {
        viewModelScope.launch {
            cartDao.clearCart()
            _couponCode.value = ""
            _discount.value = 0
            calculateFinalTotal()
        }
    }

    // Actualizar código de cupón
    fun updateCouponCode(code: String) {
        _couponCode.value = code
    }

    // Aplicar cupón
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
    }

    // Calcular total final
    private fun calculateFinalTotal() {
        _finalTotal.value = (_cartTotal.value - _discount.value).coerceAtLeast(0)
    }

    // ✨ PROCESAR PAGO Y ACTUALIZAR STOCK
    fun processPayment(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val items = _cartItems.value

                // 1. Verificar que hay suficiente stock para todos los productos
                for (item in items) {
                    val producto = expenseDao.findExpenseById(item.productoId)
                    if (producto == null) {
                        Toast.makeText(getApplication(), "El producto '${item.nombre}' ya no existe.", Toast.LENGTH_LONG).show()
                        return@launch
                    }
                    if (producto.stock < item.cantidad) {
                        Toast.makeText(getApplication(), "Stock insuficiente para '${item.nombre}'. Solo quedan ${producto.stock}.", Toast.LENGTH_LONG).show()
                        return@launch
                    }
                }

                // 2. Actualizar el stock de cada producto
                for (item in items) {
                    val producto = expenseDao.findExpenseById(item.productoId)
                    if (producto != null) {
                        val productoActualizado = producto.copy(
                            stock = producto.stock - item.cantidad
                        )
                        expenseDao.update(productoActualizado)
                    }
                }

                // 3. Limpiar el carrito
                clearCart()

                // 4. Llamar callback de éxito
                onSuccess()

            } catch (e: Exception) {
                // Manejar error si algo falla
                e.printStackTrace()
            }
        }
    }
}