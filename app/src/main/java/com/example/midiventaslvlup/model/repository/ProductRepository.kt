package com.example.midiventaslvlup.model.repository

import com.example.midiventaslvlup.model.local.ExpenseDao
import com.example.midiventaslvlup.model.local.ExpenseEntity
import kotlinx.coroutines.flow.Flow

class ProductRepository(private val expenseDao: ExpenseDao) {

    fun getAllProducts(): Flow<List<ExpenseEntity>> = expenseDao.getAllExpenses()

    fun getProductsByCategory(category: String): Flow<List<ExpenseEntity>> = expenseDao.getExpensesByCategory(category)

    fun getAllCategories(): Flow<List<String>> = expenseDao.getAllCategories()

    suspend fun findProductById(id: Int): ExpenseEntity? = expenseDao.findExpenseById(id)

    suspend fun insertProduct(product: ExpenseEntity) {
        expenseDao.insert(product)
    }

    suspend fun updateProduct(product: ExpenseEntity) {
        expenseDao.update(product)
    }

    suspend fun deleteProduct(product: ExpenseEntity) {
        expenseDao.delete(product)
    }
}
