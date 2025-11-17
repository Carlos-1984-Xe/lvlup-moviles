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

data class UserManagementState(
    val users: List<UserResponse> = emptyList(),
    val isLoadingList: Boolean = false,
    val foundUser: UserResponse? = null,
    val isLoadingUser: Boolean = false,
    val searchEmail: String = "",
    val userMessage: String? = null,
    val userActionSuccess: Boolean = false
)

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(UserManagementState())
    val uiState: StateFlow<UserManagementState> = _uiState.asStateFlow()

    init {
        getUsers()
    }

    fun getUsers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingList = true) }
            userRepository.getUsers().onSuccess { userList ->
                _uiState.update { it.copy(users = userList, isLoadingList = false) }
            }.onFailure {
                _uiState.update { it.copy(isLoadingList = false) }
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
                getUsers()
            }.onFailure { error ->
                _uiState.update { it.copy(userMessage = "Error al crear usuario: ${error.message}") }
            }
        }
    }

    fun updateUser(updatedUser: UserResponse) {
        viewModelScope.launch {
            _uiState.value.foundUser?.let { originalUser ->
                var success = true
                var finalMessage = ""

                // 1. Verificar si el perfil cambió y actualizar si es necesario
                val profileChanged = originalUser.nombre != updatedUser.nombre ||
                                     originalUser.apellido != updatedUser.apellido ||
                                     originalUser.correo != updatedUser.correo
                if (profileChanged) {
                    val profileResult = userRepository.updateUser(originalUser.id, updatedUser)
                    profileResult.onFailure {
                        success = false
                        finalMessage += "Error al actualizar perfil. "
                    }
                }

                // 2. Verificar si el rol cambió y actualizar si es necesario
                if (originalUser.rol != updatedUser.rol && updatedUser.rol != null) {
                    val roleResult = userRepository.changeUserRole(originalUser.id, updatedUser.rol)
                    roleResult.onFailure {
                        success = false
                        finalMessage += "Error al cambiar rol."
                    }
                }
                
                if(success) {
                    finalMessage = "Usuario actualizado correctamente"
                    getUsers() // Refrescar la lista
                }

                _uiState.update {
                    it.copy(
                        userMessage = finalMessage,
                        userActionSuccess = success
                    )
                }
            }
        }
    }

    fun deleteUserByEmail(email: String) {
        viewModelScope.launch {
            userRepository.getUserByEmail(email).onSuccess { userToDelete ->
                userRepository.deleteUser(userToDelete.id).onSuccess {
                    _uiState.update { it.copy(userMessage = "Usuario eliminado", userActionSuccess = true) }
                    getUsers()
                }.onFailure { error ->
                     _uiState.update { it.copy(userMessage = "Error al eliminar: ${error.message}") }
                }
            }.onFailure {
                 _uiState.update { it.copy(userMessage = "Usuario no encontrado") }
            }
        }
    }

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
