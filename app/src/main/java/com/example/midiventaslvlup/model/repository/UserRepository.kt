package com.example.midiventaslvlup.model.repository

import com.example.midiventaslvlup.model.enums.UserRole
import com.example.midiventaslvlup.network.RetrofitClient
import com.example.midiventaslvlup.network.dto.RegisterRequest
import com.example.midiventaslvlup.network.dto.StatsResponse
import com.example.midiventaslvlup.network.dto.UserResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository {
    private val api = RetrofitClient.apiService

    suspend fun createUser(request: RegisterRequest): Result<Map<String, Any>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.createUser(request)
                if (response.success && response.data != null) {
                    Result.success(response.data)
                } else {
                    Result.failure(Exception(response.message ?: "Error en la creación"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getUsers(): Result<List<UserResponse>> {
        return runApiCall { api.getUsers() }
    }

    suspend fun getUserStats(): Result<StatsResponse> {
        return runApiCall { api.getUserStats() }
    }

    suspend fun getUserByEmail(email: String): Result<UserResponse> {
        return withContext(Dispatchers.IO) {
            try {
                // Primero intentamos con el endpoint específico si existiera
                // Como no existe, filtramos de la lista general
                val usersResult = runApiCall { api.getUsers() }
                usersResult.fold(
                    onSuccess = { users ->
                        val user = users.find { it.correo.equals(email, ignoreCase = true) }
                        if (user != null) {
                            Result.success(user)
                        } else {
                            Result.failure(Exception("Usuario con email '$email' no encontrado"))
                        }
                    },
                    onFailure = {
                        Result.failure(it)
                    }
                )
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun updateUser(id: Long, user: UserResponse): Result<UserResponse> {
        val updateData = mapOf(
            "nombre" to user.nombre,
            "apellido" to user.apellido,
            "telefono" to (user.telefono ?: ""),
            "direccion" to (user.direccion ?: ""),
            "region" to (user.region ?: ""),
            "comuna" to (user.comuna ?: "")
        )
        return runApiCall { api.updateUser(id, updateData) }
    }

    suspend fun changeUserRole(id: Long, newRole: UserRole): Result<UserResponse> {
        val roleData = mapOf("rol" to newRole.name)
        return runApiCall { api.changeUserRole(id, roleData) }
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
