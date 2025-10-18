package com.example.midiventaslvlup.ui.screen

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.midiventaslvlup.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    onNavigateToUserManagement: (String) -> Unit,
    onNavigateToProductManagement: (String) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedOption by remember { mutableStateOf("Home") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isDarkMode by remember { mutableStateOf(false) }
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> imageUri = uri }
    )


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(modifier = Modifier.fillMaxSize()) {
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Group, contentDescription = "Administrar usuarios") },
                        label = { Text("Administrar usuarios") },
                        selected = selectedOption == "UserAdmin",
                        onClick = {
                            selectedOption = "UserAdmin"
                            scope.launch { drawerState.close() }
                        }
                    )
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Store, contentDescription = "Administrar productos") },
                        label = { Text("Administrar productos") },
                        selected = selectedOption == "ProductAdmin",
                        onClick = {
                            selectedOption = "ProductAdmin"
                            scope.launch { drawerState.close() }
                        }
                    )
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                        label = { Text("Perfil") },
                        selected = selectedOption == "Profile",
                        onClick = {
                            selectedOption = "Profile"
                            scope.launch { drawerState.close() }
                        }
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Opciones") },
                        label = { Text("Opciones") },
                        selected = selectedOption == "Settings",
                        onClick = {
                            selectedOption = "Settings"
                            scope.launch { drawerState.close() }
                        }
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("") },
                    navigationIcon = {
                        IconButton(onClick = { selectedOption = "Home" }) {
                            Image(
                                painter = painterResource(id = R.drawable.logolvlup_playstore),
                                contentDescription = "Logo",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Abrir menú"
                            )
                        }
                    }
                )
            },
            containerColor = Color.White,
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = if (selectedOption == "Settings") Arrangement.Top else Arrangement.Center
            ) {
                when (selectedOption) {
                    "Home" -> {
                        Text(text = "Bienvenido, Administrador", color = Color.Black)
                    }
                    "UserAdmin" -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Button(onClick = { onNavigateToUserManagement("create") }) {
                                Text("Crear usuario")
                            }
                            Spacer(modifier = Modifier.size(16.dp))
                            Button(onClick = { onNavigateToUserManagement("edit") }) {
                                Text("Editar usuario")
                            }
                            Spacer(modifier = Modifier.size(16.dp))
                            Button(onClick = { onNavigateToUserManagement("list") }) {
                                Text("Listar usuario")
                            }
                            Spacer(modifier = Modifier.size(16.dp))
                            Button(onClick = { onNavigateToUserManagement("delete") }) {
                                Text("Borrar usuario")
                            }
                        }
                    }
                    "ProductAdmin" -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Button(onClick = { onNavigateToProductManagement("create") }) {
                                Text("Crear producto")
                            }
                            Spacer(modifier = Modifier.size(16.dp))
                            Button(onClick = { onNavigateToProductManagement("edit") }) {
                                Text("Editar producto")
                            }
                            Spacer(modifier = Modifier.size(16.dp))
                            Button(onClick = { onNavigateToProductManagement("list") }) {
                                Text("Listar producto")
                            }
                            Spacer(modifier = Modifier.size(16.dp))
                            Button(onClick = { onNavigateToProductManagement("delete") }) {
                                Text("Borrar producto")
                            }
                        }
                    }
                    "Profile" -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            AsyncImage(
                                model = imageUri ?: "https://picsum.photos/300",
                                contentDescription = "Foto de perfil",
                                modifier = Modifier
                                    .size(150.dp)
                                    .clip(CircleShape)
                                    .clickable { // Todavía puedes tocar la imagen para cambiarla
                                        singlePhotoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    }
                            )
                            Spacer(modifier = Modifier.size(16.dp))
                            Text("Nombre de usuario", color = Color.Black)
                            Spacer(modifier = Modifier.size(8.dp))
                            Text("admin@lvlup.com", color = Color.Black)
                            Spacer(modifier = Modifier.size(24.dp))
                            Button(onClick = onNavigateToLogin) {
                                Text("Cambiar cuenta")
                            }
                        }
                    }
                    "Settings" -> {
                        val context = LocalContext.current
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Modo Oscuro", color = Color.Black)
                                Switch(
                                    checked = isDarkMode,
                                    onCheckedChange = { isDarkMode = it }
                                )
                            }
                            Spacer(modifier = Modifier.size(16.dp))
                            Button(onClick = { Toast.makeText(context, "Caché limpiado", Toast.LENGTH_SHORT).show() }) {
                                Text("Limpiar caché")
                            }
                            Spacer(modifier = Modifier.weight(1f)) // This pushes the version to the bottom
                            Text(
                                text = "Versión de la app: 1.0.0",
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminScreenPreview() {
    AdminScreen(
        onNavigateToUserManagement = {},
        onNavigateToProductManagement = {},
        onNavigateToLogin = {}
    )
}
