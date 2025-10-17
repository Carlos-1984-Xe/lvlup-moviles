package com.example.midiventaslvlup.model.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [ExpenseEntity::class, UserEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao
    abstract fun userDao(): UserDao

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
                                database.expenseDao().insertAll(*SampleData.products.toTypedArray())
                                database.userDao().insert(UserEntity(nombre = "Admin", apellido = "User", correo = "admin@duocuc.cl", contrasena = "admin123", telefono = "987654321", fechaNacimiento = 0, direccion = "DuocUC", rut= "12.345.678-9", region= "Valparaiso", comuna="Viña del mar", rol = "administrador"))
                                database.userDao().insert(UserEntity(nombre = "Cliente", apellido = "User", correo = "cliente@gmail.com", contrasena = "cliente123", telefono = "123456789", fechaNacimiento = 0, direccion = "Por ahi", rut= "12.345.678-9", region= "Valparaiso", comuna="Viña del mar", rol = "cliente"))
                            }
                        }
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}