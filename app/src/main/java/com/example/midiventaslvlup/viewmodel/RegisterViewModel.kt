package com.example.midiventaslvlup.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.midiventaslvlup.model.repository.AuthRepository
import com.example.midiventaslvlup.network.dto.RegisterRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// Data class para mantener el estado del UI
data class RegisterState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val rut: String = "",
    val fechaNacimiento: String = "",
    val telefono: String = "",
    val direccion: String = "",
    val region: String = "",
    val comuna: String = "",
    val isLoading: Boolean = false,
    val registerSuccess: Boolean = false,
    val errorMessage: String? = null
)

class RegisterViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _registerState = MutableStateFlow(RegisterState())
    val registerState: StateFlow<RegisterState> = _registerState.asStateFlow()

    // --- Métodos para actualizar el estado desde la UI ---
    fun onNameChange(name: String) { _registerState.update { it.copy(name = name) } }
    fun onEmailChange(email: String) { _registerState.update { it.copy(email = email) } }
    fun onPasswordChange(password: String) { _registerState.update { it.copy(password = password) } }
    fun onRutChange(rut: String) { _registerState.update { it.copy(rut = rut) } }
    fun onFechaNacimientoChange(fecha: String) { _registerState.update { it.copy(fechaNacimiento = fecha) } }
    fun onTelefonoChange(telefono: String) { _registerState.update { it.copy(telefono = telefono) } }
    fun onDireccionChange(direccion: String) { _registerState.update { it.copy(direccion = direccion) } }
    fun onRegionChange(region: String) { _registerState.update { it.copy(region = region) } }
    fun onComunaChange(comuna: String) { _registerState.update { it.copy(comuna = comuna) } }

    fun register() {
        val state = _registerState.value
        // Mover la validación de la UI al ViewModel
        if (state.name.isBlank() || state.email.isBlank() || state.password.isBlank() || state.rut.isBlank() || state.fechaNacimiento.isBlank() || state.telefono.isBlank() || state.direccion.isBlank() || state.region.isBlank() || state.comuna.isBlank()) {
            _registerState.update { it.copy(errorMessage = "Por favor, complete todos los campos") }
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            _registerState.update { it.copy(errorMessage = "Por favor, ingrese un formato de correo válido") }
            return
        }
        if (!isValidChileanRut(state.rut)) {
            _registerState.update { it.copy(errorMessage = "Por favor, ingrese un RUT chileno válido") }
            return
        }
        if (!isOfLegalAge(state.fechaNacimiento)) {
            _registerState.update { it.copy(errorMessage = "Debe ser mayor de 18 años") }
            return
        }

        _registerState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val birthDateMillis = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(state.fechaNacimiento)?.time
            val request = RegisterRequest(
                nombre = state.name.split(" ").firstOrNull() ?: "",
                apellido = state.name.split(" ").getOrElse(1) { "" },
                correo = state.email,
                contrasena = state.password,
                rut = state.rut,
                fechaNacimiento = birthDateMillis,
                telefono = state.telefono,
                direccion = state.direccion,
                region = state.region,
                comuna = state.comuna,
                rol = "CLIENTE" // Rol de cliente por defecto
            )

            val result = authRepository.register(request)
            result.onSuccess {
                _registerState.update { it.copy(isLoading = false, registerSuccess = true) }
            }.onFailure { exception ->
                _registerState.update { it.copy(isLoading = false, errorMessage = exception.message ?: "Error desconocido") }
            }
        }
    }

    fun clearErrorMessage() {
        _registerState.update { it.copy(errorMessage = null) }
    }

    // --- Lógica de validación (movida desde la UI) ---
    private fun isValidChileanRut(rut: String): Boolean {
        if (rut.isBlank()) return false
        val cleanRut = rut.replace(".", "").replace("-", "").lowercase()
        if (cleanRut.length < 2) return false

        val dv = cleanRut.last()
        val body = cleanRut.substring(0, cleanRut.length - 1)

        if (!body.all { it.isDigit() }) return false
        if (!dv.isDigit() && dv != 'k') return false

        try {
            var sum = 0
            var multiplier = 2
            for (i in body.reversed()) {
                sum += i.toString().toInt() * multiplier
                multiplier = if (multiplier == 7) 2 else multiplier + 1
            }
            val remainder = sum % 11
            val calculatedDv = 11 - remainder
            val expectedDv = when (calculatedDv) {
                11 -> '0'
                10 -> 'k'
                else -> calculatedDv.toString().first()
            }
            return dv == expectedDv
        } catch (e: Exception) {
            return false
        }
    }

    private fun isOfLegalAge(birthDateStr: String): Boolean {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        dateFormat.isLenient = false
        return try {
            val birthDate = dateFormat.parse(birthDateStr) ?: return false
            val today = Calendar.getInstance()
            val birth = Calendar.getInstance()
            birth.time = birthDate

            var age = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR)
            if (today.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR)) {
                age--
            }
            age >= 18
        } catch (e: Exception) {
            false
        }
    }
}
