package com.example.midiventaslvlup.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.midiventaslvlup.model.repository.AuthRepository
import com.example.midiventaslvlup.network.dto.LoginResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginState(
    val usuario: String = "",
    val contrasena: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loginSuccess: Boolean = false,
    val user: LoginResponse? = null
)

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository()

    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    fun onUsuarioChange(newUsuario: String) {
        _loginState.value = _loginState.value.copy(
            usuario = newUsuario,
            errorMessage = null
        )
    }

    fun onContrasenaChange(newContrasena: String) {
        _loginState.value = _loginState.value.copy(
            contrasena = newContrasena,
            errorMessage = null
        )
    }

    fun login() {
        val currentState = _loginState.value

        // Validaciones básicas
        if (currentState.usuario.isBlank() || currentState.contrasena.isBlank()) {
            _loginState.value = currentState.copy(
                errorMessage = "Por favor complete todos los campos"
            )
            return
        }

        _loginState.value = currentState.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            try {
                Log.d("LoginViewModel", "Intentando login con: ${currentState.usuario}")

                val result = authRepository.login(
                    correo = currentState.usuario.trim(),
                    contrasena = currentState.contrasena
                )

                if (result.isSuccess) {
                    val user = result.getOrNull()!!
                    Log.d("LoginViewModel", "Login exitoso: ${user.nombre}, Rol: ${user.rol}")

                    _loginState.value = currentState.copy(
                        isLoading = false,
                        loginSuccess = true,
                        user = user,
                        errorMessage = null
                    )
                } else {
                    val error = result.exceptionOrNull()
                    Log.e("LoginViewModel", "Error en login: ${error?.message}")

                    _loginState.value = currentState.copy(
                        isLoading = false,
                        loginSuccess = false,
                        errorMessage = error?.message ?: "Usuario o contraseña incorrectos"
                    )
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Error al iniciar sesión", e)
                _loginState.value = currentState.copy(
                    isLoading = false,
                    loginSuccess = false,
                    errorMessage = "Error de conexión: ${e.localizedMessage}"
                )
            }
        }
    }

    fun resetLoginState() {
        _loginState.value = LoginState()
    }
}
