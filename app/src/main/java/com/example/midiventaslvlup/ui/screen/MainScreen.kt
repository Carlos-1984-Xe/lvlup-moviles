package com.example.midiventaslvlup.ui.screen

import android.util.Patterns
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.midiventaslvlup.model.local.AppDatabase
import com.example.midiventaslvlup.model.local.UserEntity
import com.example.midiventaslvlup.model.repository.UserRepository
import com.example.midiventaslvlup.viewmodel.LoginViewModel
import com.example.midiventaslvlup.viewmodel.UserViewModel
import com.example.midiventaslvlup.viewmodel.UserViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    onNavigateToDetails: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onNavigateToCart: () -> Unit = {},
    loginViewModel: LoginViewModel = viewModel()
) {
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

    // Manejar navegación según el resultado del login
    LaunchedEffect(loginState.loginSuccess) {
        if (loginState.loginSuccess) {
            when (loginState.userRole) {
                "administrador" -> onNavigateToAdmin()
                "cliente" -> onNavigateToDetails()
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

                    // Mostrar mensaje de error si existe
                    if (loginState.errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = loginState.errorMessage!!,
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { loginViewModel.login() },
                        enabled = !loginState.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (loginState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White
                            )
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

            Text(
                text = "Usuarios de prueba:",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Text(
                text = "Admin: admin@duocuc.cl / admin123",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Text(
                text = "Cliente: cliente@gmail.com / cliente123",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        // Botón de emergencia para crear usuarios de prueba
        IconButton(
            onClick = {
                loginViewModel.createTestUsers()
                Toast.makeText(context, "Usuarios de prueba creados", Toast.LENGTH_SHORT).show()
            },
            enabled = !loginState.isLoading,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Build,
                contentDescription = "Crear usuarios de prueba",
                tint = Color(0xFFFF6B6B)
            )
        }
    }
}

@Composable
private fun RegisterScreen(onRegisterSuccess: () -> Unit, onCancel: () -> Unit) {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val userRepository = UserRepository(db.userDao())
    val userViewModel: UserViewModel = viewModel(factory = UserViewModelFactory(userRepository))
    val scope = rememberCoroutineScope()

    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var rut by rememberSaveable { mutableStateOf("") }
    var fechaNacimiento by rememberSaveable { mutableStateOf("") }
    var telefono by rememberSaveable { mutableStateOf("") }
    var direccion by rememberSaveable { mutableStateOf("") }
    var region by rememberSaveable { mutableStateOf("") }
    var comuna by rememberSaveable { mutableStateOf("") }

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

            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre Completo") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Correo electrónico") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Contraseña") }, visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = rut, onValueChange = { rut = it }, label = { Text("RUT (ej: 12345678-9)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = fechaNacimiento, onValueChange = { fechaNacimiento = it }, label = { Text("Fecha Nacimiento (DD/MM/YYYY)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = telefono, onValueChange = { telefono = it }, label = { Text("Teléfono") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = direccion, onValueChange = { direccion = it }, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = region, onValueChange = { region = it }, label = { Text("Región") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = comuna, onValueChange = { comuna = it }, label = { Text("Comuna") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            
            Spacer(modifier = Modifier.height(24.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(onClick = onCancel, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) { Text("Cancelar") }
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = {
                    val fields = listOf(name, email, password, rut, fechaNacimiento, telefono, direccion, region, comuna)
                    if (fields.any { it.isBlank() }) {
                        Toast.makeText(context, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                    } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        Toast.makeText(context, "Por favor, ingrese un formato de correo válido", Toast.LENGTH_SHORT).show()
                    } else if (!isValidChileanRut(rut)) {
                        Toast.makeText(context, "Por favor, ingrese un RUT chileno válido", Toast.LENGTH_SHORT).show()
                    } else if (!isOfLegalAge(fechaNacimiento)) {
                        Toast.makeText(context, "Debe ser mayor de 18 años", Toast.LENGTH_SHORT).show()
                    } else {
                        scope.launch {
                            val birthDateMillis = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(fechaNacimiento)?.time ?: 0L
                            val user = UserEntity(
                                nombre = name.split(" ").firstOrNull() ?: "",
                                apellido = name.split(" ").getOrElse(1) { "" },
                                correo = email,
                                contrasena = password,
                                rol = "cliente", // Hardcoded to client
                                telefono = telefono,
                                fechaNacimiento = birthDateMillis,
                                direccion = direccion,
                                rut = rut,
                                region = region,
                                comuna = comuna
                            )
                            userViewModel.createUser(user)
                            Toast.makeText(context, "Usuario creado exitosamente. Ya puede iniciar sesión.", Toast.LENGTH_LONG).show()
                            onRegisterSuccess()
                        }
                    }
                }) {
                    Text("Crear Cuenta")
                }
            }
        }
    }
}

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
