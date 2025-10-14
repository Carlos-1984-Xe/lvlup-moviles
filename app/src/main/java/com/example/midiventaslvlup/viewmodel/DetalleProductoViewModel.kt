package com.example.midiventaslvlup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.midiventaslvlup.model.local.ExpenseDao
import com.example.midiventaslvlup.model.local.ExpenseEntity
import kotlinx.coroutines.flow.Flow

class DetalleProductoViewModel(expenseDao: ExpenseDao, productId: Int) : ViewModel() {
    val product: Flow<ExpenseEntity> = expenseDao.getExpenseById(productId)
}

class DetalleProductoViewModelFactory(
    private val expenseDao: ExpenseDao,
    private val productId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetalleProductoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetalleProductoViewModel(expenseDao, productId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}