package com.example.midiventaslvlup.network

import com.example.midiventaslvlup.network.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // --- Auth Endpoints ---
    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<LoginResponse>

    @POST("/api/auth/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse<Map<String, Any>> // Respuesta de registro es más compleja, usamos un Mapa

    // --- User Management Endpoints ---
    @POST("/api/users")
    suspend fun createUser(@Body request: RegisterRequest): ApiResponse<UserResponse>

    @GET("/api/users")
    suspend fun getUsers(): ApiResponse<List<UserResponse>>

    @GET("/api/users/{id}")
    suspend fun getUserById(@Path("id") id: Long): ApiResponse<UserResponse>

    @PUT("/api/users/{id}")
    suspend fun updateUser(@Path("id") id: Long, @Body user: UserResponse): ApiResponse<UserResponse>

    @DELETE("/api/users/{id}")
    suspend fun deleteUser(@Path("id") id: Long): Response<ApiResponse<Unit>> // Respuesta sin cuerpo, pero dentro del wrapper
}
