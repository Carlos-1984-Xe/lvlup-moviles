package com.example.midiventaslvlup.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.midiventaslvlup.model.repository.ProductRepository
import com.example.midiventaslvlup.network.dto.ProductDto
import com.example.midiventaslvlup.ui.theme.*
import com.example.midiventaslvlup.viewmodel.ProductViewModel
import com.example.midiventaslvlup.viewmodel.ProductViewModelFactory
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductosScreen(
    categoria: String,
    onBackClick: () -> Unit,
    onProductClick: (ProductDto) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // usar ProductRepository con Retrofit
    val productRepository = remember { ProductRepository() }
    val viewModel: ProductViewModel = viewModel(
        factory = ProductViewModelFactory(productRepository)
    )

    // Seleccionar la categoría
    LaunchedEffect(categoria) {
        viewModel.selectCategory(categoria)
    }

    // Obtener productos filtrados
    val productos by viewModel.products.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = categoria,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = NeonGreen
                            )
                        )
                        Text(
                            text = "${productos.size} productos disponibles",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextWhite.copy(alpha = 0.7f)
                            )
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = NeonGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground,
                    titleContentColor = TextWhite
                )
            )
        },
        containerColor = DarkBackground
    ) { paddingValues ->
        when {
            isLoading -> {
                //  Mostrar loading
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = NeonGreen)
                }
            }
            error != null -> {
                //  Mostrar error
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text(
                            "Error al cargar productos",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.Red
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            error ?: "Error desconocido",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextWhite.copy(alpha = 0.5f)
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.selectCategory(categoria) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonGreen
                            )
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            }
            productos.isEmpty() -> {
                // Mensaje cuando no hay productos
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = TextWhite.copy(alpha = 0.3f),
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No hay productos en esta categoría",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = TextWhite.copy(alpha = 0.5f)
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Pronto agregaremos más productos",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextWhite.copy(alpha = 0.3f)
                            )
                        )
                    }
                }
            }
            else -> {
                //  Grid de productos
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(productos) { producto ->
                        ProductCard(
                            producto = producto,
                            onClick = { onProductClick(producto) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard(
    producto: ProductDto,  //  camibo de ExpenseEntity a ProductDto
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Imagen del producto
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF1A1A2E),
                                Color(0xFF16213E)
                            )
                        )
                    )
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(producto.imagen)
                        .crossfade(true)
                        .build(),
                    contentDescription = producto.nombre,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )

                // Badge de stock bajo
                if (producto.stock < 5) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Red.copy(alpha = 0.9f)
                    ) {
                        Text(
                            text = "¡Últimas unidades!",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            ),
                            fontSize = 10.sp
                        )
                    }
                }
            }

            // Información del producto
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Precio
                Text(
                    text = formatearPrecio(producto.precio),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = NeonGreen
                    )
                )

                // Stock disponible
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (producto.stock > 5) NeonGreen else Color.Red)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${producto.stock} disponibles",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextWhite.copy(alpha = 0.7f)
                        ),
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

// Función para formatear precio en CLP
fun formatearPrecio(precio: Int): String {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
    return format.format(precio)
}