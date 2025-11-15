package com.example.midiventaslvlup.model.repository

import com.example.midiventaslvlup.network.RetrofitClient
import com.example.midiventaslvlup.network.dto.LoginRequest
import com.example.midiventaslvlup.network.dto.LoginResponse
import com.example.midiventaslvlup.network.dto.RegisterRequest
import com.example.midiventaslvlup.network.dto.RegisterResponse

class AuthRepository {
    private val api = RetrofitClient.apiService

    suspend fun login(correo: String, contrasena: String): Result<LoginResponse> {
        return try {
            val response = api.login(LoginRequest(correo, contrasena))
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Error en el login"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(request: RegisterRequest): Result<Map<String, Any>> {
        return try {
            val response = api.register(request)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Error en el registro"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
