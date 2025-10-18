package com.example.midiventaslvlup.model.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: ExpenseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg expenses: ExpenseEntity)

    @Update
    suspend fun update(expense: ExpenseEntity)

    @Delete
    suspend fun delete(expense: ExpenseEntity)

    @Query("SELECT * FROM productos")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM productos WHERE id = :id")
    fun getExpenseById(id: Int): Flow<ExpenseEntity>

    @Query("SELECT * FROM productos WHERE id = :id")
    suspend fun findExpenseById(id: Int): ExpenseEntity?

    @Query("SELECT * FROM productos WHERE categoria = :category")
    fun getExpensesByCategory(category: String): Flow<List<ExpenseEntity>>

    @Query("SELECT DISTINCT categoria FROM productos ORDER BY categoria ASC")
    fun getAllCategories(): Flow<List<String>>
}
