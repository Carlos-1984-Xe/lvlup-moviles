package com.example.midiventaslvlup.ui.screen

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.midiventaslvlup.model.enums.UserRole
import com.example.midiventaslvlup.model.repository.UserRepository
import com.example.midiventaslvlup.network.dto.RegisterRequest
import com.example.midiventaslvlup.network.dto.UserResponse
import com.example.midiventaslvlup.viewmodel.UserViewModel
import com.example.midiventaslvlup.viewmodel.UserViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(action: String, onNavigateBack: () -> Unit) {
    val userViewModel: UserViewModel = viewModel(factory = UserViewModelFactory(UserRepository()))

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
                "create" -> CreateUserForm(viewModel = userViewModel, onActionDone = onNavigateBack)
                "edit" -> EditUserForm(viewModel = userViewModel, onActionDone = onNavigateBack)
                "list" -> ListUsers(viewModel = userViewModel)
                "delete" -> DeleteUserForm(viewModel = userViewModel, onActionDone = onNavigateBack)
            }
        }
    }
}

@Composable
private fun CreateUserForm(viewModel: UserViewModel, onActionDone: () -> Unit) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rut by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var region by remember { mutableStateOf("") }
    var comuna by remember { mutableStateOf("") }
    var isAdmin by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearUserMessage()
        }
    }
    LaunchedEffect(uiState.userActionSuccess) {
        if (uiState.userActionSuccess) {
            onActionDone()
            viewModel.resetUserActionSuccess()
        }
    }

    Column(
        modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Crear Nuevo Usuario", style = MaterialTheme.typography.headlineMedium)
        
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre Completo") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Contraseña") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = rut, onValueChange = { rut = it }, label = { Text("RUT (ej: 12.345.678-9)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = fechaNacimiento, onValueChange = { fechaNacimiento = it }, label = { Text("Fecha Nacimiento (dd/MM/yyyy)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = telefono, onValueChange = { telefono = it }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
        OutlinedTextField(value = direccion, onValueChange = { direccion = it }, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = region, onValueChange = { region = it }, label = { Text("Región") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = comuna, onValueChange = { comuna = it }, label = { Text("Comuna") }, modifier = Modifier.fillMaxWidth())

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isAdmin, onCheckedChange = { isAdmin = it })
            Text("Es Administrador")
        }
        Row {
            Button(onClick = onActionDone, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) { Text("Cancelar") }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                val birthDateMillis = try {
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(fechaNacimiento)?.time
                } catch (e: Exception) {
                    null
                }

                val request = RegisterRequest(
                    nombre = name.split(" ").firstOrNull() ?: "",
                    apellido = name.split(" ").getOrElse(1) { "" },
                    correo = email,
                    contrasena = password,
                    rol = if (isAdmin) UserRole.ADMIN.name else UserRole.CLIENTE.name,
                    rut = rut,
                    fechaNacimiento = birthDateMillis,
                    telefono = telefono,
                    direccion = direccion,
                    region = region,
                    comuna = comuna
                )
                viewModel.createUser(request)
            }) { Text("Crear") }
        }
    }
}

@Composable
private fun EditUserForm(viewModel: UserViewModel, onActionDone: () -> Unit) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val foundUser = uiState.foundUser

    // Estados para todos los campos del formulario
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var rut by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var region by remember { mutableStateOf("") }
    var comuna by remember { mutableStateOf("") }
    var isAdmin by remember { mutableStateOf(false) }

    // Cuando se encuentra un usuario, se rellenan todos los campos del formulario
    LaunchedEffect(foundUser) {
        foundUser?.let {
            name = "${it.nombre} ${it.apellido}".trim()
            email = it.correo
            rut = it.rut ?: ""
            fechaNacimiento = it.fechaNacimiento?.let { millis ->
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(millis))
            } ?: ""
            telefono = it.telefono ?: ""
            direccion = it.direccion ?: ""
            region = it.region ?: ""
            comuna = it.comuna ?: ""
            isAdmin = it.rol == UserRole.ADMIN
        }
    }

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearUserMessage()
        }
    }
    LaunchedEffect(uiState.userActionSuccess) {
        if (uiState.userActionSuccess) {
            onActionDone()
            viewModel.resetUserActionSuccess()
        }
    }

    DisposableEffect(Unit) { onDispose { viewModel.clearFoundUser() } }

    Column(
        modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Editar Usuario", style = MaterialTheme.typography.headlineMedium)

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = uiState.searchEmail,
                onValueChange = viewModel::onSearchEmailChange,
                label = { Text("Email del usuario") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                enabled = foundUser == null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { viewModel.findUserByEmail(uiState.searchEmail) },
                enabled = foundUser == null
            ) { Text("Buscar") }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- FORMULARIO COMPLETO ---
        if (foundUser != null) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre Completo") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = email, onValueChange = { /* No-op */ }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), enabled = false)
            OutlinedTextField(value = rut, onValueChange = { /* No-op */ }, label = { Text("RUT") }, modifier = Modifier.fillMaxWidth(), enabled = false)
            OutlinedTextField(value = fechaNacimiento, onValueChange = { fechaNacimiento = it }, label = { Text("Fecha Nacimiento (dd/MM/yyyy)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = telefono, onValueChange = { telefono = it }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
            OutlinedTextField(value = direccion, onValueChange = { direccion = it }, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = region, onValueChange = { region = it }, label = { Text("Región") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = comuna, onValueChange = { comuna = it }, label = { Text("Comuna") }, modifier = Modifier.fillMaxWidth())

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(checked = isAdmin, onCheckedChange = { isAdmin = it })
                Text("Es Administrador")
            }
            Row {
                Button(
                    onClick = { viewModel.clearFoundUser(); onActionDone() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Cancelar") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    val birthDateMillis = try {
                        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(fechaNacimiento)?.time
                    } catch (e: Exception) { null }

                    val updatedUser = foundUser.copy(
                        nombre = name.split(" ").firstOrNull() ?: "",
                        apellido = name.split(" ").getOrNull(1) ?: "",
                        fechaNacimiento = birthDateMillis,
                        telefono = telefono,
                        direccion = direccion,
                        region = region,
                        comuna = comuna,
                        rol = if (isAdmin) UserRole.ADMIN else UserRole.CLIENTE
                    )
                    viewModel.updateUser(updatedUser)
                }) { Text("Guardar") }
            }
        }
    }
}

@Composable
private fun ListUsers(viewModel: UserViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val users = uiState.users
    
    if (uiState.isLoadingList) {
        CircularProgressIndicator()
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(16.dp)) {
            items(users) { user -> UserItem(user = user) }
        }
    }
}

@Composable
fun UserItem(user: UserResponse) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = "${user.nombre} ${user.apellido}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = user.correo, style = MaterialTheme.typography.bodyMedium)
            Text(text = user.rol?.name?.replaceFirstChar { it.uppercase() } ?: "Cliente", style = MaterialTheme.typography.bodySmall, color = if (user.rol == UserRole.ADMIN) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun DeleteUserForm(viewModel: UserViewModel, onActionDone: () -> Unit) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearUserMessage()
        }
    }
    LaunchedEffect(uiState.userActionSuccess) {
        if (uiState.userActionSuccess) {
            onActionDone()
            viewModel.resetUserActionSuccess()
        }
    }

    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Borrar Usuario", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email del usuario a borrar.") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), modifier = Modifier.fillMaxWidth())
        Row {
            Button(onClick = onActionDone, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) { Text("Cancelar") }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                if (email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    viewModel.deleteUserByEmail(email)
                } else {
                    Toast.makeText(context, "Por favor, ingrese un email válido", Toast.LENGTH_SHORT).show()
                }
            }) { Text("Borrar") }
        }
    }
}
