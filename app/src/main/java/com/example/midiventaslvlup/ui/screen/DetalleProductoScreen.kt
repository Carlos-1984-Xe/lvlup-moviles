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
import androidx.compose.material.icons.filled.ShoppingCart  // ← AGREGAR
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight  // ← AGREGAR
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp  // ← AGREGAR
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.midiventaslvlup.R
import com.example.midiventaslvlup.model.local.AppDatabase
import com.example.midiventaslvlup.ui.theme.GreenPrimary  // ← AGREGAR
import com.example.midiventaslvlup.viewmodel.CartViewModel  // ← AGREGAR
import com.example.midiventaslvlup.viewmodel.DetalleProductoViewModel
import com.example.midiventaslvlup.viewmodel.DetalleProductoViewModelFactory
import kotlinx.coroutines.delay  // ← AGREGAR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleProductoScreen(
    productId: Int,
    modifier: Modifier = Modifier,
    onNavigateToCart: () -> Unit = {}  // ← AGREGAR ESTE PARÁMETRO
) {
    var showMenu by remember { mutableStateOf(false) }
    var showAddedSnackbar by remember { mutableStateOf(false) }  // ← AGREGAR

    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val viewModel: DetalleProductoViewModel = viewModel(factory = DetalleProductoViewModelFactory(db.expenseDao(), productId))
    val product by viewModel.product.collectAsState(initial = null)

    // ← AGREGAR: ViewModel del carrito
    val cartViewModel: CartViewModel = viewModel()
    val cartItems by cartViewModel.cartItems.collectAsState(initial = emptyList())
    val itemCount = cartItems.sumOf { it.cantidad }

    val snackbarHostState = remember { SnackbarHostState() }  // ← AGREGAR

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

                        // ← AGREGAR: Opción del Carrito en el menú
                        val carritoInteractionSource = remember { MutableInteractionSource() }
                        val isCarritoHovered by carritoInteractionSource.collectIsHoveredAsState()
                        val carritoBackgroundColor = if (isCarritoHovered) Color.LightGray else MaterialTheme.colorScheme.surface

                        DropdownMenuItem(
                            text = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.ShoppingCart,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Carrito")
                                    Spacer(modifier = Modifier.weight(1f))
                                    if (itemCount > 0) {
                                        Badge(
                                            containerColor = Color.Red,
                                            modifier = Modifier.size(20.dp)
                                        ) {
                                            Text("$itemCount", fontSize = 10.sp, color = Color.White)
                                        }
                                    }
                                }
                            },
                            onClick = {
                                showMenu = false
                                onNavigateToCart()
                            },
                            modifier = Modifier.background(carritoBackgroundColor),
                            interactionSource = carritoInteractionSource
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
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }  // ← AGREGAR
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
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Stock disponible: ${it.stock} unidades",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (it.stock > 0) GreenPrimary else Color.Red
                )
                Spacer(modifier = Modifier.height(24.dp))

                // ← ACTUALIZAR: Botón Agregar al carrito funcional
                Button(
                    onClick = {
                        cartViewModel.addToCart(it)
                        showAddedSnackbar = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenPrimary
                    ),
                    enabled = it.stock > 0  // Deshabilitar si no hay stock
                ) {
                    Icon(
                        Icons.Default.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (it.stock > 0) "Agregar al carrito" else "Sin stock",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ← AGREGAR: Botón para ir al carrito
                OutlinedButton(
                    onClick = onNavigateToCart,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ver carrito ($itemCount)")
                }
            }
        }

        // ← AGREGAR: Snackbar cuando se agrega al carrito
        LaunchedEffect(showAddedSnackbar) {
            if (showAddedSnackbar) {
                snackbarHostState.showSnackbar(
                    message = "Producto agregado al carrito",
                    duration = SnackbarDuration.Short
                )
                showAddedSnackbar = false
            }
        }
    }
}