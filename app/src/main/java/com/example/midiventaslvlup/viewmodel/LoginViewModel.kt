package com.example.midiventaslvlup.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.midiventaslvlup.model.local.AppDatabase
import com.example.midiventaslvlup.model.local.UserEntity
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
    val userRole: String? = null
)

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = AppDatabase.getDatabase(application).userDao()

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

                // Verificar todos los usuarios en la base de datos
                val allUsers = userDao.getAllUsersSync()
                Log.d("LoginViewModel", "Total usuarios en DB: ${allUsers.size}")
                allUsers.forEach { user ->
                    Log.d("LoginViewModel", "Usuario encontrado: correo=${user.correo}, rol=${user.rol}, contrasena=${user.contrasena}")
                }

                val user = userDao.getUserByEmail(currentState.usuario.trim())
                Log.d("LoginViewModel", "Usuario encontrado: ${user?.correo}")

                if (user != null) {
                    Log.d("LoginViewModel", "Comparando contraseñas: '${currentState.contrasena}' vs '${user.contrasena}'")

                    if (user.contrasena == currentState.contrasena) {
                        Log.d("LoginViewModel", "Login exitoso para: ${user.correo}")
                        _loginState.value = currentState.copy(
                            isLoading = false,
                            loginSuccess = true,
                            userRole = user.rol,
                            errorMessage = null
                        )
                    } else {
                        Log.d("LoginViewModel", "Contraseña incorrecta")
                        _loginState.value = currentState.copy(
                            isLoading = false,
                            loginSuccess = false,
                            errorMessage = "Usuario o contraseña incorrectos"
                        )
                    }
                } else {
                    Log.d("LoginViewModel", "Usuario no encontrado en la base de datos")
                    _loginState.value = currentState.copy(
                        isLoading = false,
                        loginSuccess = false,
                        errorMessage = "Usuario no encontrado. Total usuarios: ${allUsers.size}"
                    )
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Error al iniciar sesión", e)
                _loginState.value = currentState.copy(
                    isLoading = false,
                    loginSuccess = false,
                    errorMessage = "Error al iniciar sesión: ${e.message}"
                )
            }
        }
    }

    fun resetLoginState() {
        _loginState.value = LoginState()
    }

    fun createTestUsers() {
        viewModelScope.launch {
            try {
                Log.d("LoginViewModel", "Creando usuarios de prueba...")

                // Verificar si ya existen
                val adminExists = userDao.getUserByEmail("admin@duocuc.cl")
                val clienteExists = userDao.getUserByEmail("cliente@gmail.com")

                if (adminExists == null) {
                    val adminId = userDao.insert(
                        UserEntity(
                            nombre = "Admin",
                            apellido = "User",
                            correo = "admin@duocuc.cl",
                            contrasena = "admin123",
                            telefono = "987654321",
                            fechaNacimiento = 0,
                            direccion = "DuocUC",
                            rut = "12.345.678-9",
                            region = "Valparaiso",
                            comuna = "Viña del mar",
                            rol = "administrador"
                        )
                    )
                    Log.d("LoginViewModel", "Usuario Admin creado con ID: $adminId")
                } else {
                    Log.d("LoginViewModel", "Usuario Admin ya existe")
                }

                if (clienteExists == null) {
                    val clienteId = userDao.insert(
                        UserEntity(
                            nombre = "Cliente",
                            apellido = "User",
                            correo = "cliente@gmail.com",
                            contrasena = "cliente123",
                            telefono = "123456789",
                            fechaNacimiento = 0,
                            direccion = "Por ahi",
                            rut = "12.345.678-9",
                            region = "Valparaiso",
                            comuna = "Viña del mar",
                            rol = "cliente"
                        )
                    )
                    Log.d("LoginViewModel", "Usuario Cliente creado con ID: $clienteId")
                } else {
                    Log.d("LoginViewModel", "Usuario Cliente ya existe")
                }

                _loginState.value = _loginState.value.copy(
                    errorMessage = "✓ Usuarios creados. Intenta iniciar sesión ahora."
                )
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Error al crear usuarios", e)
                _loginState.value = _loginState.value.copy(
                    errorMessage = "Error al crear usuarios: ${e.message}"
                )
            }
        }
    }
}