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
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(request: RegisterRequest): Result<RegisterResponse> {
        return try {
            val response = api.register(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}