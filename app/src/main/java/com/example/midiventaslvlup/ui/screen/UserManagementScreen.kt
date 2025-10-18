package com.example.midiventaslvlup.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.midiventaslvlup.model.local.AppDatabase
import com.example.midiventaslvlup.model.local.UserEntity
import com.example.midiventaslvlup.model.repository.UserRepository
import com.example.midiventaslvlup.viewmodel.UserViewModel
import com.example.midiventaslvlup.viewmodel.UserViewModelFactory
import kotlinx.coroutines.launch

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
            verticalArrangement = Arrangement.Center
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
    var isAdmin by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Crear Nuevo Usuario", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre") }
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation()
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
                scope.launch {
                    val user = UserEntity(
                        nombre = name.split(" ").first(),
                        apellido = name.split(" ").getOrElse(1) { "" },
                        correo = email,
                        contrasena = password,
                        rol = if (isAdmin) "administrador" else "cliente",
                        telefono = "",
                        fechaNacimiento = 0L,
                        direccion = "",
                        rut = "",
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
            }) {
                Text("Crear")
            }
        }
    }
}

@Composable
private fun EditUserForm(onUserUpdated: () -> Unit, onCancel: () -> Unit) {
    var email by remember { mutableStateOf("") }
    // TODO: Add fields for user editing

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Editar Usuario", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Buscar por Email") }
        )
        // TODO: Display user details once found and allow editing
        Row {
            Button(onClick = onCancel,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                Text("Cancelar")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { /* TODO: validation and logic */ onUserUpdated() }) {
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

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(users) { user ->
            UserItem(user = user)
        }
    }
}

@Composable
fun UserItem(user: UserEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "${user.nombre} ${user.apellido}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(text = user.correo, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = user.rol.replaceFirstChar { it.uppercase() }, 
                style = MaterialTheme.typography.bodySmall,
                color = if (user.rol == "administrador") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}


@Composable
private fun DeleteUserForm(onUserDeleted: () -> Unit, onCancel: () -> Unit) {
    var email by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Borrar Usuario", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email del usuario a borrar") }
        )
        Row {
             Button(onClick = onCancel,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                Text("Cancelar")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { /* TODO: validation and logic */ onUserDeleted() }) {
                Text("Borrar")
            }
        }
    }
}
