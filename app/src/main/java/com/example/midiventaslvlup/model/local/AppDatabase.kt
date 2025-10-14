package com.example.midiventaslvlup.model.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [ExpenseEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        INSTANCE?.let { database ->
                            CoroutineScope(Dispatchers.IO).launch {
                                database.expenseDao().insertAll(
                                    ExpenseEntity(nombre = "Laptop Gamer", categoria = "Tecnología", imagen = "https://i.imgur.com/n2yQ3e4.jpeg", descripcion = "Potente laptop para juegos", precio = 1500, stock = 10),
                                    ExpenseEntity(nombre = "Teclado Mecánico", categoria = "Accesorios", imagen = "https://i.imgur.com/0oW4v9c.jpeg", descripcion = "Teclado con switches Cherry MX", precio = 120, stock = 25),
                                    ExpenseEntity(nombre = "Mouse Inalámbrico", categoria = "Accesorios", imagen = "https://i.imgur.com/I2y6f1M.jpeg", descripcion = "Mouse ergonómico para largas sesiones", precio = 80, stock = 30)
                                )
                            }
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}