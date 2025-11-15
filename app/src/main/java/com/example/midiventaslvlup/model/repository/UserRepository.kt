package com.example.midiventaslvlup.model.repository

import com.example.midiventaslvlup.network.RetrofitClient
import com.example.midiventaslvlup.network.dto.RegisterRequest
import com.example.midiventaslvlup.network.dto.UserResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository {
    private val api = RetrofitClient.apiService

    suspend fun createUser(request: RegisterRequest): Result<UserResponse> {
        return runApiCall { api.createUser(request) }
    }

    suspend fun getUsers(): Result<List<UserResponse>> {
        return runApiCall { api.getUsers() }
    }
    
    suspend fun getUserByEmail(email: String): Result<UserResponse> {
        return withContext(Dispatchers.IO) {
            try {
                // Obtenemos la lista completa de usuarios desde la API
                val usersResult = runApiCall { api.getUsers() }
                usersResult.fold(
                    onSuccess = { users ->
                        // Filtramos en el cliente para encontrar el usuario por email
                        val user = users.find { it.correo.equals(email, ignoreCase = true) }
                        if (user != null) {
                            Result.success(user)
                        } else {
                            Result.failure(Exception("Usuario con email '$email' no encontrado"))
                        }
                    },
                    onFailure = {
                        // Si la llamada a getUsers() falla, propagamos el error
                        Result.failure(it)
                    }
                )
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun updateUser(id: Long, user: UserResponse): Result<UserResponse> {
        return runApiCall { api.updateUser(id, user) }
    }

    suspend fun deleteUser(id: Long): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.deleteUser(id)
                if (response.isSuccessful && response.body()?.success == true) {
                    Result.success(Unit)
                } else {
                    val errorBody = response.errorBody()?.string() ?: response.message()
                    Result.failure(Exception("Error al eliminar usuario: $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // Helper para encapsular llamadas a la API y manejo de errores
    private suspend fun <T> runApiCall(apiCall: suspend () -> com.example.midiventaslvlup.network.dto.ApiResponse<T>): Result<T> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiCall()
                if (response.success && response.data != null) {
                    Result.success(response.data)
                } else {
                    Result.failure(Exception(response.message ?: "Respuesta de API no exitosa"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
