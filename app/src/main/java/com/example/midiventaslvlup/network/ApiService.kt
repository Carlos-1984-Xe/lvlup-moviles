package com.example.midiventaslvlup.network

import com.example.midiventaslvlup.network.dto.*
import retrofit2.http.*

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("users")
    suspend fun createUser(@Body user: UserCreateRequest): UserResponse

    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: Long): UserResponse

    @GET("users")
    suspend fun getUsers(): List<UserResponse>
}