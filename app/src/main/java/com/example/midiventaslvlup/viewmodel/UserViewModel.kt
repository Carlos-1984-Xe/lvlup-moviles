package com.example.midiventaslvlup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.midiventaslvlup.model.repository.UserRepository
import com.example.midiventaslvlup.network.dto.RegisterRequest
import com.example.midiventaslvlup.network.dto.UserResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

// Estado para la pantalla de gestión de usuarios
data class UserManagementState(
    // Para la lista de usuarios
    val users: List<UserResponse> = emptyList(),
    val isLoadingList: Boolean = false,

    // Para buscar y editar
    val foundUser: UserResponse? = null,
    val isLoadingUser: Boolean = false,
    val searchEmail: String = "",

    // Para mensajes y eventos
    val userMessage: String? = null,
    val userActionSuccess: Boolean = false
)

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(UserManagementState())
    val uiState: StateFlow<UserManagementState> = _uiState.asStateFlow()

    init {
        // Cargar la lista de usuarios al iniciar el ViewModel
        getUsers()
    }

    // --- ACCIONES ---

    fun getUsers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingList = true) }
            userRepository.getUsers().onSuccess { userList ->
                _uiState.update { it.copy(users = userList, isLoadingList = false) }
            }.onFailure {
                _uiState.update { it.copy(userMessage = "Error al cargar usuarios", isLoadingList = false) }
            }
        }
    }

    fun findUserByEmail(email: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingUser = true) }
            userRepository.getUserByEmail(email).onSuccess { user ->
                _uiState.update { it.copy(foundUser = user, isLoadingUser = false, userMessage = "Usuario encontrado") }
            }.onFailure {
                _uiState.update { it.copy(foundUser = null, isLoadingUser = false, userMessage = "Usuario no encontrado") }
            }
        }
    }

    fun createUser(request: RegisterRequest) {
        viewModelScope.launch {
            userRepository.createUser(request).onSuccess {
                _uiState.update { it.copy(userMessage = "Usuario creado exitosamente", userActionSuccess = true) }
                getUsers() // Refrescar la lista
            }.onFailure { error ->
                _uiState.update { it.copy(userMessage = "Error al crear usuario: ${error.message}") }
            }
        }
    }

    fun updateUser(user: UserResponse) {
        viewModelScope.launch {
            _uiState.value.foundUser?.let { originalUser ->
                userRepository.updateUser(originalUser.id, user).onSuccess { updatedUser ->
                    _uiState.update {
                        it.copy(
                            userMessage = "Usuario actualizado",
                            userActionSuccess = true,
                            foundUser = updatedUser // Opcional: actualizar el usuario encontrado
                        )
                    }
                    getUsers() // Refrescar la lista
                }.onFailure { error ->
                    _uiState.update { it.copy(userMessage = "Error al actualizar: ${error.message}") }
                }
            }
        }
    }

    fun deleteUser(user: UserResponse) {
        viewModelScope.launch {
            userRepository.deleteUser(user.id).onSuccess {
                _uiState.update { it.copy(userMessage = "Usuario eliminado", userActionSuccess = true) }
                getUsers() // Refrescar la lista
            }.onFailure { error ->
                _uiState.update { it.copy(userMessage = "Error al eliminar: ${error.message}") }
            }
        }
    }
    
    fun deleteUserByEmail(email: String) {
        viewModelScope.launch {
            val result = userRepository.getUserByEmail(email)
            result.onSuccess { userToDelete ->
                deleteUser(userToDelete)
            }.onFailure {
                 _uiState.update { it.copy(userMessage = "Usuario no encontrado") }
            }
        }
    }

    // --- MANEJO DE ESTADO ---
    
    fun onSearchEmailChange(email: String) {
        _uiState.update { it.copy(searchEmail = email) }
    }

    fun clearFoundUser() {
        _uiState.update { it.copy(foundUser = null, searchEmail = "") }
    }

    fun clearUserMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }
    
    fun resetUserActionSuccess() {
        _uiState.update { it.copy(userActionSuccess = false) }
    }
}
