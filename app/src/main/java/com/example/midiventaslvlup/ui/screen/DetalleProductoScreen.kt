package com.example.midiventaslvlup.ui.screen

import android.app.Application
import com.example.midiventaslvlup.model.repository.CartRepository
import com.example.midiventaslvlup.model.repository.OrderRepository
import com.example.midiventaslvlup.model.repository.ProductRepository
import com.example.midiventaslvlup.util.SessionManager
import com.example.midiventaslvlup.viewmodel.CartViewModelFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.midiventaslvlup.R
import com.example.midiventaslvlup.ui.theme.GreenPrimary
import com.example.midiventaslvlup.viewmodel.CartViewModel
import com.example.midiventaslvlup.viewmodel.DetalleProductoViewModel
import com.example.midiventaslvlup.viewmodel.DetalleProductoViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleProductoScreen(
    productId: Long,
    modifier: Modifier = Modifier,
    onNavigateToCart: () -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }
    var showAddedSnackbar by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val userId = SessionManager.getUserId(context)

    val productRepository = remember { ProductRepository() }
    val viewModel: DetalleProductoViewModel = viewModel(
        factory = DetalleProductoViewModelFactory(productRepository, productId)
    )
    val product by viewModel.product.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val cartRepository = remember { CartRepository() }
    val orderRepository = remember { OrderRepository() }

    val cartViewModel: CartViewModel = viewModel(
        factory = CartViewModelFactory(
            application = context.applicationContext as Application,
            cartRepository = cartRepository,
            orderRepository = orderRepository,
            userId = userId
        )
    )

    val cartItems by cartViewModel.cartItems.collectAsState()
    val itemCount = cartItems.sumOf { it.quantity }

    val snackbarHostState = remember { SnackbarHostState() }

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
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = GreenPrimary,
                    contentColor = Color.White,
                    actionColor = Color(0xFFB2FF59),
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                error != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error al cargar el producto",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Red
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = error ?: "Error desconocido",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.refreshProduct() }) {
                            Text("Reintentar")
                        }
                    }
                }
                product != null -> {
                    val productData = product!!
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = productData.imagen,  // ✅ Ya es "imagen" en ProductDto
                            contentDescription = productData.nombre,  // ✅ Ya es "nombre"
                            modifier = Modifier
                                .height(300.dp)
                                .fillMaxWidth(),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = productData.nombre,
                            style = MaterialTheme.typography.headlineLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = productData.descripcion,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Precio: $${productData.precio}",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Stock disponible: ${productData.stock} unidades",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (productData.stock > 0) GreenPrimary else Color.Red
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                cartViewModel.addToCart(productId = productData.id, quantity = 1)
                                showAddedSnackbar = true
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GreenPrimary
                            ),
                            enabled = productData.stock > 0
                        ) {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                if (productData.stock > 0) "Agregar al carrito" else "Sin stock",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedButton(
                            onClick = onNavigateToCart,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Ver carrito ($itemCount)")
                        }
                    }
                }
            }
        }

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