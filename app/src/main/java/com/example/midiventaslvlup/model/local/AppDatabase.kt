package com.example.midiventaslvlup.model.local

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@Database(
    entities = [
        ExpenseEntity::class,
        UserEntity::class,
        CartItemEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao
    abstract fun userDao(): UserDao
    abstract fun cartDao(): CartDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Scope para operaciones de base de datos
        private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .addCallback(DatabaseCallback(applicationScope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            Log.d("AppDatabase", "onCreate - Iniciando población de base de datos")

            INSTANCE?.let { database ->
                scope.launch {
                    try {
                        populateDatabase(database)
                        Log.d("AppDatabase", "Base de datos poblada exitosamente")
                    } catch (e: Exception) {
                        Log.e("AppDatabase", "Error al poblar la base de datos", e)
                    }
                }
            }
        }

        private suspend fun populateDatabase(database: AppDatabase) {
            val expenseDao = database.expenseDao()
            val userDao = database.userDao()

            // Insertar productos de ejemplo
            Log.d("AppDatabase", "Insertando productos...")
            expenseDao.insertAll(*SampleData.products.toTypedArray())
            Log.d("AppDatabase", "Productos insertados: ${SampleData.products.size}")

            // Verificar si los usuarios ya existen
            val adminExists = userDao.getUserByEmail("admin@duocuc.cl")
            val clienteExists = userDao.getUserByEmail("cliente@gmail.com")

            // Insertar usuario administrador
            if (adminExists == null) {
                val adminId = userDao.insert(
                    UserEntity(
                        nombre = "Admin",
                        apellido = "User",
                        correo = "admin@duocuc.cl",
                        contrasena = "admin123",
                        telefono = "987654321",
                        fechaNacimiento = 0,
                        direccion = "DuocUC",
                        rut = "12.345.678-9",
                        region = "Valparaiso",
                        comuna = "Viña del mar",
                        rol = "administrador"
                    )
                )
                Log.d("AppDatabase", "Usuario Admin creado con ID: $adminId")
            } else {
                Log.d("AppDatabase", "Usuario Admin ya existe")
            }

            // Insertar usuario cliente
            if (clienteExists == null) {
                val clienteId = userDao.insert(
                    UserEntity(
                        nombre = "Cliente",
                        apellido = "User",
                        correo = "cliente@gmail.com",
                        contrasena = "cliente123",
                        telefono = "123456789",
                        fechaNacimiento = 0,
                        direccion = "Por ahi",
                        rut = "12.345.678-9",
                        region = "Valparaiso",
                        comuna = "Viña del mar",
                        rol = "cliente"
                    )
                )
                Log.d("AppDatabase", "Usuario Cliente creado con ID: $clienteId")
            } else {
                Log.d("AppDatabase", "Usuario Cliente ya existe")
            }

            // Verificar usuarios creados
            val allUsers = userDao.getAllUsersSync()
            Log.d("AppDatabase", "Total de usuarios en la base de datos: ${allUsers.size}")
            allUsers.forEach { user ->
                Log.d("AppDatabase", "Usuario: ${user.correo} - Rol: ${user.rol}")
            }
        }
    }
}