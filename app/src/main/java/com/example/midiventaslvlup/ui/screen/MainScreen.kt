package com.example.midiventaslvlup.ui.screen

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.midiventaslvlup.R
import com.example.midiventaslvlup.model.enums.UserRole
import com.example.midiventaslvlup.model.repository.AuthRepository
import com.example.midiventaslvlup.network.dto.LoginResponse
import com.example.midiventaslvlup.viewmodel.LoginViewModel
import com.example.midiventaslvlup.viewmodel.LoginViewModelFactory
import com.example.midiventaslvlup.viewmodel.RegisterViewModel
import com.example.midiventaslvlup.viewmodel.RegisterViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    onNavigateToDetails: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onNavigateToCart: () -> Unit = {}
) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val loginViewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(application, AuthRepository())
    )
    var showRegisterForm by rememberSaveable { mutableStateOf(false) }

    if (showRegisterForm) {
        RegisterScreen(
            onRegisterSuccess = { showRegisterForm = false },
            onCancel = { showRegisterForm = false }
        )
    } else {
        LoginScreen(
            modifier = modifier,
            loginViewModel = loginViewModel,
            onNavigateToAdmin = onNavigateToAdmin,
            onNavigateToDetails = onNavigateToDetails,
            onGoToRegister = { showRegisterForm = true }
        )
    }
}

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel,
    onNavigateToDetails: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onGoToRegister: () -> Unit
) {
    val loginState by loginViewModel.loginState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(key1 = loginState.loginSuccess, key2 = loginState.user) {
        val user = loginState.user // Crear una copia local
        if (loginState.loginSuccess && user != null) {
            guardarSesion(context, user)
            when (user.rol) { // Usar la copia local
                UserRole.ADMIN -> onNavigateToAdmin()
                UserRole.CLIENTE -> onNavigateToDetails()
                else -> onNavigateToDetails()
            }
            loginViewModel.resetLoginState()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logolvlup_playstore),
                contentDescription = "Logo",
                modifier = Modifier.size(150.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Bienvenido a MiDiVentaslvlup",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextField(
                        value = loginState.usuario,
                        onValueChange = { loginViewModel.onUsuarioChange(it) },
                        label = { Text("Correo electrónico") },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !loginState.isLoading,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TextField(
                        value = loginState.contrasena,
                        onValueChange = { loginViewModel.onContrasenaChange(it) },
                        label = { Text("Contraseña") },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !loginState.isLoading,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true
                    )

                    loginState.errorMessage?.let { error ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { loginViewModel.login() },
                        enabled = !loginState.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (loginState.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                        } else {
                            Text("Iniciar Sesión")
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = onGoToRegister) {
                        Text("¿No tienes cuenta? Créala aquí")
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Usuarios de prueba:", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Text(text = "Admin: admin@duocuc.cl / admin123", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Text(text = "Cliente: cliente@gmail.com / cliente123", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}

private fun guardarSesion(context: Context, user: LoginResponse) {
    val prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE)
    prefs.edit().apply {
        putLong("userId", user.userId)
        putString("userName", user.nombre)
        putString("userEmail", user.correo)
        putString("userRole", user.rol.name)
        apply()
    }
}

@Composable
private fun RegisterScreen(onRegisterSuccess: () -> Unit, onCancel: () -> Unit) {
    val context = LocalContext.current
    val registerViewModel: RegisterViewModel = viewModel(
        factory = RegisterViewModelFactory(AuthRepository())
    )
    val registerState by registerViewModel.registerState.collectAsState()

    LaunchedEffect(registerState.registerSuccess) {
        if (registerState.registerSuccess) {
            Toast.makeText(context, "Usuario creado exitosamente. Ya puede iniciar sesión.", Toast.LENGTH_LONG).show()
            onRegisterSuccess()
        }
    }

    LaunchedEffect(registerState.errorMessage) {
        registerState.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            registerViewModel.clearErrorMessage() // Limpiamos el error después de mostrarlo
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Crear Nueva Cuenta", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(value = registerState.name, onValueChange = registerViewModel::onNameChange, label = { Text("Nombre Completo") }, modifier = Modifier.fillMaxWidth(), singleLine = true, isError = registerState.errorMessage != null)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = registerState.email, onValueChange = registerViewModel::onEmailChange, label = { Text("Correo electrónico") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), modifier = Modifier.fillMaxWidth(), singleLine = true, isError = registerState.errorMessage != null)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = registerState.password, onValueChange = registerViewModel::onPasswordChange, label = { Text("Contraseña") }, visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), modifier = Modifier.fillMaxWidth(), singleLine = true, isError = registerState.errorMessage != null)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = registerState.rut, onValueChange = registerViewModel::onRutChange, label = { Text("RUT (ej: 12345678-9)") }, modifier = Modifier.fillMaxWidth(), singleLine = true, isError = registerState.errorMessage != null)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = registerState.fechaNacimiento, onValueChange = registerViewModel::onFechaNacimientoChange, label = { Text("Fecha Nacimiento (DD/MM/YYYY)") }, modifier = Modifier.fillMaxWidth(), singleLine = true, isError = registerState.errorMessage != null)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = registerState.telefono, onValueChange = registerViewModel::onTelefonoChange, label = { Text("Teléfono") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), modifier = Modifier.fillMaxWidth(), singleLine = true, isError = registerState.errorMessage != null)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = registerState.direccion, onValueChange = registerViewModel::onDireccionChange, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth(), singleLine = true, isError = registerState.errorMessage != null)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = registerState.region, onValueChange = registerViewModel::onRegionChange, label = { Text("Región") }, modifier = Modifier.fillMaxWidth(), singleLine = true, isError = registerState.errorMessage != null)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = registerState.comuna, onValueChange = registerViewModel::onComunaChange, label = { Text("Comuna") }, modifier = Modifier.fillMaxWidth(), singleLine = true, isError = registerState.errorMessage != null)

            Spacer(modifier = Modifier.height(24.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(onClick = onCancel, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error), enabled = !registerState.isLoading) { Text("Cancelar") }
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = { registerViewModel.register() }, enabled = !registerState.isLoading) {
                    if (registerState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                    } else {
                        Text("Crear Cuenta")
                    }
                }
            }
        }
    }
}
