package com.example.midiventaslvlup.ui.screen

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(action: String, onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    val titleText = when (action) {
                        "list" -> "Listado de Usuarios"
                        else -> "${action.replaceFirstChar { it.uppercase() }} Usuario"
                    }
                    Text(text = titleText)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            when (action) {
                "create" -> CreateUserForm(onUserCreated = onNavigateBack, onCancel = onNavigateBack)
                "edit" -> EditUserForm(onUserUpdated = onNavigateBack, onCancel = onNavigateBack)
                "list" -> ListUsers()
                "delete" -> DeleteUserForm(onUserDeleted = onNavigateBack, onCancel = onNavigateBack)
            }
        }
    }
}

@Composable
private fun CreateUserForm(
    onUserCreated: () -> Unit, 
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val userRepository = UserRepository(db.userDao())
    val viewModel: UserViewModel = viewModel(factory = UserViewModelFactory(userRepository))
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rut by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var isAdmin by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Crear Nuevo Usuario", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = rut,
            onValueChange = { rut = it },
            label = { Text("RUT") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = fechaNacimiento,
            onValueChange = { fechaNacimiento = it },
            label = { Text("Fecha Nacimiento (DD/MM/YYYY)") },
            modifier = Modifier.fillMaxWidth()
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isAdmin, onCheckedChange = { isAdmin = it })
            Text("Es Administrador")
        }
        Row {
            Button(onClick = onCancel,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                Text("Cancelar")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { 
                if (name.isBlank() || email.isBlank() || password.isBlank() || rut.isBlank() || fechaNacimiento.isBlank()) {
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
                            nombre = name.split(" ").first(),
                            apellido = name.split(" ").getOrElse(1) { "" },
                            correo = email,
                            contrasena = password,
                            rol = if (isAdmin) "administrador" else "cliente",
                            telefono = "",
                            fechaNacimiento = birthDateMillis,
                            direccion = "",
                            rut = rut,
                            region = "",
                            comuna = ""
                        )
                        val newUserId = viewModel.createUser(user)
                        if (newUserId > 0) {
                            Toast.makeText(context, "Usuario creado con ID: $newUserId", Toast.LENGTH_SHORT).show()
                            onUserCreated()
                        } else {
                            Toast.makeText(context, "Error al crear el usuario", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }) {
                Text("Crear")
            }
        }
    }
}

@Composable
private fun EditUserForm(onUserUpdated: () -> Unit, onCancel: () -> Unit) {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val userRepository = UserRepository(db.userDao())
    val viewModel: UserViewModel = viewModel(factory = UserViewModelFactory(userRepository))
    val scope = rememberCoroutineScope()

    var searchEmail by remember { mutableStateOf("") }
    val foundUser by viewModel.foundUser.collectAsState()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") } // For new password
    var isAdmin by remember { mutableStateOf(false) }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var rut by remember { mutableStateOf("") }
    var region by remember { mutableStateOf("") }
    var comuna by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }

    var searchAttempted by remember { mutableStateOf(false) }

    val isFormEnabled = foundUser != null

    LaunchedEffect(foundUser) {
        if (searchAttempted) {
            foundUser?.let {
                name = "${it.nombre} ${it.apellido}".trim()
                email = it.correo
                isAdmin = it.rol == "administrador"
                telefono = it.telefono
                direccion = it.direccion
                rut = it.rut
                region = it.region
                comuna = it.comuna
                fechaNacimiento = if (it.fechaNacimiento > 0) SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it.fechaNacimiento) else ""
                password = "" // Clear password field
                Toast.makeText(context, "Usuario encontrado", Toast.LENGTH_SHORT).show()
            } ?: Toast.makeText(context, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
            searchAttempted = false
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearFoundUser()
        }
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Editar Usuario", style = MaterialTheme.typography.headlineMedium)

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = searchEmail,
                onValueChange = { searchEmail = it },
                label = { Text("Email del usuario") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                enabled = !isFormEnabled,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { 
                    if (searchEmail.isNotBlank()) {
                        searchAttempted = true
                        viewModel.findUserByEmail(searchEmail)
                    } else {
                        Toast.makeText(context, "Ingrese un email para buscar", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = !isFormEnabled
            ) {
                Text("Buscar")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre Completo") }, enabled = isFormEnabled, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, enabled = isFormEnabled, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Nueva Contraseña (opcional)") }, visualTransformation = PasswordVisualTransformation(), enabled = isFormEnabled, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = telefono, onValueChange = { telefono = it }, label = { Text("Teléfono") }, enabled = isFormEnabled, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
        OutlinedTextField(value = rut, onValueChange = { rut = it }, label = { Text("RUT") }, enabled = isFormEnabled, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = fechaNacimiento, onValueChange = { fechaNacimiento = it }, label = { Text("Fecha Nacimiento (DD/MM/YYYY)") }, enabled = isFormEnabled, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = direccion, onValueChange = { direccion = it }, label = { Text("Dirección") }, enabled = isFormEnabled, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = region, onValueChange = { region = it }, label = { Text("Región") }, enabled = isFormEnabled, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = comuna, onValueChange = { comuna = it }, label = { Text("Comuna") }, enabled = isFormEnabled, modifier = Modifier.fillMaxWidth())
        
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Checkbox(checked = isAdmin, onCheckedChange = { isAdmin = it }, enabled = isFormEnabled)
            Text("Es Administrador")
        }

        Row {
            Button(onClick = { viewModel.clearFoundUser(); onCancel() }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) { Text("Cancelar") }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (name.isBlank() || email.isBlank() || rut.isBlank() || fechaNacimiento.isBlank()) {
                        Toast.makeText(context, "Por favor, complete todos los campos obligatorios", Toast.LENGTH_SHORT).show()
                    } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        Toast.makeText(context, "Por favor, ingrese un formato de correo válido", Toast.LENGTH_SHORT).show()
                    } else if (!isValidChileanRut(rut)) {
                        Toast.makeText(context, "Por favor, ingrese un RUT chileno válido", Toast.LENGTH_SHORT).show()
                    } else if (!isOfLegalAge(fechaNacimiento)) {
                        Toast.makeText(context, "Debe ser mayor de 18 años", Toast.LENGTH_SHORT).show()
                    } else {
                        foundUser?.let { userToUpdate ->
                            scope.launch {
                                val birthDateMillis = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(fechaNacimiento)?.time ?: userToUpdate.fechaNacimiento
                                val updatedUser = userToUpdate.copy(
                                    nombre = name.split(" ").firstOrNull() ?: "",
                                    apellido = name.split(" ").getOrNull(1) ?: "",
                                    correo = email,
                                    contrasena = if (password.isNotBlank()) password else userToUpdate.contrasena,
                                    rol = if (isAdmin) "administrador" else "cliente",
                                    telefono = telefono,
                                    direccion = direccion,
                                    rut = rut,
                                    region = region,
                                    comuna = comuna,
                                    fechaNacimiento = birthDateMillis
                                )
                                viewModel.updateUser(updatedUser)
                                Toast.makeText(context, "Usuario actualizado", Toast.LENGTH_SHORT).show()
                                onUserUpdated()
                            }
                        }
                    }
                },
                enabled = isFormEnabled
            ) {
                Text("Guardar")
            }
        }
    }
}

@Composable
private fun ListUsers() {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val userRepository = UserRepository(db.userDao())
    val viewModel: UserViewModel = viewModel(factory = UserViewModelFactory(userRepository))

    val users by viewModel.users.collectAsState()

    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(16.dp)) {
        items(users) { user -> UserItem(user = user) }
    }
}

@Composable
fun UserItem(user: UserEntity) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = "${user.nombre} ${user.apellido}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = user.correo, style = MaterialTheme.typography.bodyMedium)
            Text(text = user.rol.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.bodySmall, color = if (user.rol == "administrador") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun DeleteUserForm(onUserDeleted: () -> Unit, onCancel: () -> Unit) {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val userRepository = UserRepository(db.userDao())
    val viewModel: UserViewModel = viewModel(factory = UserViewModelFactory(userRepository))
    val loginViewModel: LoginViewModel = viewModel()
    val loginState by loginViewModel.loginState.collectAsState()
    val currentUserEmail = loginState.usuario

    val scope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Borrar Usuario", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email del usuario a borrar.") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), modifier = Modifier.fillMaxWidth())
        Row {
            Button(onClick = onCancel, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) { Text("Cancelar") }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { 
                val emailToDelete = email.trim()
                if (emailToDelete.isBlank()) {
                    Toast.makeText(context, "Por favor ingrese un email", Toast.LENGTH_SHORT).show()
                } else if (!Patterns.EMAIL_ADDRESS.matcher(emailToDelete).matches()) {
                    Toast.makeText(context, "Por favor, ingrese un formato de correo válido", Toast.LENGTH_SHORT).show()
                } else if (emailToDelete.equals(currentUserEmail, ignoreCase = true)) {
                    Toast.makeText(context, "No puedes borrar tu propia cuenta", Toast.LENGTH_SHORT).show()
                } else {
                    scope.launch {
                        val userToDelete = userRepository.getUserByEmail(emailToDelete)
                        if (userToDelete != null) {
                            viewModel.deleteUser(userToDelete)
                            Toast.makeText(context, "Usuario borrado exitosamente", Toast.LENGTH_SHORT).show()
                            onUserDeleted()
                        } else {
                            Toast.makeText(context, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }) {
                Text("Borrar")
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