package com.example.midiventaslvlup.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.midiventaslvlup.model.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminProfileState(
    val userName: String = "Cargando...",
    val userEmail: String = ""
)

data class AdminDashboardState(
    val totalUsers: Int? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class AdminViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _profileState = MutableStateFlow(AdminProfileState())
    val profileState: StateFlow<AdminProfileState> = _profileState.asStateFlow()

    private val _dashboardState = MutableStateFlow(AdminDashboardState())
    val dashboardState: StateFlow<AdminDashboardState> = _dashboardState.asStateFlow()

    fun loadAdminProfile(context: Context) {
        val prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE)
        val name = prefs.getString("userName", "Administrador") ?: "Administrador"
        val email = prefs.getString("userEmail", "") ?: ""
        _profileState.update { it.copy(userName = name, userEmail = email) }
    }

    fun loadDashboardStats() {
        viewModelScope.launch {
            _dashboardState.update { it.copy(isLoading = true, errorMessage = null) }
            userRepository.getUserStats().onSuccess { stats ->
                _dashboardState.update {
                    it.copy(
                        totalUsers = stats.totalUsers,
                        isLoading = false
                    )
                }
            }.onFailure { error ->
                _dashboardState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Error al cargar estadísticas"
                    )
                }
            }
        }
    }
}
