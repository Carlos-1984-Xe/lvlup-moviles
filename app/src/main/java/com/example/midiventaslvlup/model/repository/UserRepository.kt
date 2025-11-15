package com.example.midiventaslvlup.model.repository

import com.example.midiventaslvlup.network.RetrofitClient
import com.example.midiventaslvlup.network.dto.*

class UserRepository {
    private val api = RetrofitClient.apiService

    suspend fun login(correo: String, contrasena: String): LoginResponse {
        return api.login(LoginRequest(correo, contrasena))
    }

    suspend fun register(request: RegisterRequest): RegisterResponse {
        return api.register(request)
    }

    suspend fun getUser(id: Long): UserResponse {
        return api.getUser(id)
    }

    suspend fun getUsers(): List<UserResponse> {
        return api.getUsers()
    }
}
