package com.example.midiventaslvlup.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.midiventaslvlup.viewmodel.DetailsViewModel
import com.example.midiventaslvlup.viewmodel.DetailsViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(modifier: Modifier = Modifier) {
    var showMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val viewModel: DetailsViewModel = viewModel(factory = DetailsViewModelFactory(db.expenseDao()))
    val products by viewModel.products.collectAsState()

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
        LazyColumn(modifier = modifier.padding(paddingValues)) {
            items(products) { product ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        AsyncImage(
                            model = product.imagen,
                            contentDescription = null,
                            modifier = Modifier
                                .height(150.dp)
                                .fillMaxWidth(),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = product.nombre, style = MaterialTheme.typography.headlineSmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = product.descripcion)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Precio: $${product.precio}", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}