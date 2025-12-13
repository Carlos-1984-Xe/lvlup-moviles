package com.example.midiventaslvlup.model.repository

import com.example.midiventaslvlup.network.RetrofitClient
import com.example.midiventaslvlup.network.dto.LoginRequest
import com.example.midiventaslvlup.network.dto.LoginResponse
import com.example.midiventaslvlup.network.dto.RegisterRequest
import com.example.midiventaslvlup.network.dto.RegisterResponse
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class AuthRepository {
    private val api = RetrofitClient.apiService

    suspend fun login(correo: String, contrasena: String): Result<LoginResponse> {
        return try {// 1. Crea el objeto LoginRequest
            val response = api.login(LoginRequest(correo, contrasena))
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Credenciales incorrectas. Verifique su correo y contraseña"))
            }
        } catch (e: HttpException) {
            // Manejo específico de errores HTTP
            val errorMessage = when (e.code()) {
                401 -> "Credenciales incorrectas. Verifique su correo y contraseña"
                404 -> "Servicio no disponible. Contacte al administrador"
                500, 502, 503 -> "Error en el servidor. Intente nuevamente más tarde"
                else -> "No se pudo iniciar sesión. Intente nuevamente"
            }
            Result.failure(Exception(errorMessage))
        } catch (e: SocketTimeoutException) {
            Result.failure(Exception("No se pudo conectar con el servidor. Verifique su conexión"))
        } catch (e: UnknownHostException) {
            Result.failure(Exception("No se pudo conectar con el servidor. Verifique su conexión a internet"))
        } catch (e: Exception) {
            Result.failure(Exception("No se pudo iniciar sesión. Intente nuevamente"))
        }
    }

    suspend fun register(request: RegisterRequest): Result<Map<String, Any>> {
        return try {
            val response = api.register(request)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "No se pudo completar el registro. Verifique que el correo no esté registrado"))
            }
        } catch (e: HttpException) {
            // Manejo específico de errores HTTP para registro
            val errorMessage = when (e.code()) {
                400 -> "Datos inválidos. Verifique la información ingresada"
                409 -> "El correo ya está registrado. Use otro correo"
                500, 502, 503 -> "Error en el servidor. Intente nuevamente más tarde"
                else -> "No se pudo completar el registro. Intente nuevamente"
            }
            Result.failure(Exception(errorMessage))
        } catch (e: SocketTimeoutException) {
            Result.failure(Exception("No se pudo conectar con el servidor. Verifique su conexión"))
        } catch (e: UnknownHostException) {
            Result.failure(Exception("No se pudo conectar con el servidor. Verifique su conexión a internet"))
        } catch (e: Exception) {
            Result.failure(Exception("No se pudo completar el registro. Intente nuevamente"))
        }
    }
}
