package com.example.midiventaslvlup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.midiventaslvlup.model.local.UserEntity
import com.example.midiventaslvlup.model.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    val users: StateFlow<List<UserEntity>> = userRepository.getAllUsers()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _foundUser = MutableStateFlow<UserEntity?>(null)
    val foundUser = _foundUser.asStateFlow()

    fun findUserByEmail(email: String) {
        viewModelScope.launch {
            _foundUser.value = userRepository.getUserByEmail(email)
        }
    }

    suspend fun updateUser(user: UserEntity) {
        userRepository.updateUser(user)
    }

    suspend fun deleteUser(user: UserEntity) {
        userRepository.deleteUser(user)
    }

    fun clearFoundUser() {
        _foundUser.value = null
    }

    suspend fun createUser(user: UserEntity): Long {
        return userRepository.insertUser(user)
    }
}

class UserViewModelFactory(private val userRepository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
