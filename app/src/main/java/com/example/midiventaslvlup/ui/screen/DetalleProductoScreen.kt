package com.example.midiventaslvlup.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.midiventaslvlup.R
import com.example.midiventaslvlup.model.local.AppDatabase
import com.example.midiventaslvlup.viewmodel.DetalleProductoViewModel
import com.example.midiventaslvlup.viewmodel.DetalleProductoViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleProductoScreen(productId: Int, modifier: Modifier = Modifier) {
    var showMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val viewModel: DetalleProductoViewModel = viewModel(factory = DetalleProductoViewModelFactory(db.expenseDao(), productId))
    val product by viewModel.product.collectAsState(initial = null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.logolvlup_playstore),
                            contentDescription = "Logo",
                            modifier = Modifier.height(46.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Level UP gamer")
                    }
                },
                actions = {
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu"
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        val categoriasInteractionSource = remember { MutableInteractionSource() }
                        val isCategoriasHovered by categoriasInteractionSource.collectIsHoveredAsState()
                        val categoriasBackgroundColor = if (isCategoriasHovered) Color.LightGray else MaterialTheme.colorScheme.surface

                        DropdownMenuItem(
                            text = { Text("Categorias", modifier = Modifier.fillMaxWidth()) },
                            onClick = { /* Handle menu item click */ },
                            modifier = Modifier.background(categoriasBackgroundColor),
                            interactionSource = categoriasInteractionSource
                        )

                        val contactoInteractionSource = remember { MutableInteractionSource() }
                        val isContactoHovered by contactoInteractionSource.collectIsHoveredAsState()
                        val contactoBackgroundColor = if (isContactoHovered) Color.LightGray else MaterialTheme.colorScheme.surface

                        DropdownMenuItem(
                            text = { Text("Contacto", modifier = Modifier.fillMaxWidth()) },
                            onClick = { /* Handle menu item click */ },
                            modifier = Modifier.background(contactoBackgroundColor),
                            interactionSource = contactoInteractionSource
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        product?.let {
            Column(
                modifier = modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = it.imagen,
                    contentDescription = it.nombre,
                    modifier = Modifier
                        .height(300.dp)
                        .fillMaxWidth(),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = it.nombre, style = MaterialTheme.typography.headlineLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = it.descripcion, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Precio: $${it.precio}", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { /* Por ahora no hace nada */ }, modifier = Modifier.fillMaxWidth()) {
                    Text("Agregar al carrito")
                }
            }
        }
    }
}