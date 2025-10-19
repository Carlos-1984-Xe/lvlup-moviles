package com.example.midiventaslvlup.ui.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.midiventaslvlup.R
import com.example.midiventaslvlup.model.local.AppDatabase
import com.example.midiventaslvlup.ui.theme.*
import com.example.midiventaslvlup.viewmodel.DetailsViewModel
import com.example.midiventaslvlup.viewmodel.DetailsViewModelFactory
import com.example.midiventaslvlup.viewmodel.CartViewModel

data class Category(
    val name: String,
    val icon: ImageVector,
    val gradient: Brush
)

data class BlogPost(
    val title: String,
    val description: String,
    val gradient: Brush
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    modifier: Modifier = Modifier,
    onCategoryClick: (String) -> Unit = {},
    onNavigateToCart: () -> Unit = {},
    onNavigateToPcGamerBlog: () -> Unit = {},
    onNavigateToJuegosMesaBlog: () -> Unit = {},
    onNavigateToPuntosRetiro: () -> Unit = {}  // ← NUEVO PARÁMETRO
) {
    var showMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val viewModel: DetailsViewModel = viewModel(factory = DetailsViewModelFactory(db.expenseDao()))

    // ViewModel del carrito para obtener el contador
    val cartViewModel: CartViewModel = viewModel()
    val cartItems by cartViewModel.cartItems.collectAsState(initial = emptyList())
    val itemCount = cartItems.sumOf { it.cantidad }

    // Animación de pulso para el carrito
    val infiniteTransition = rememberInfiniteTransition(label = "cart_pulse")
    val cartScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cart_scale"
    )

    // Categorías con iconos y gradientes
    val categories = listOf(
        Category(
            "Juegos de Mesa",
            Icons.Default.Casino,
            Brush.linearGradient(colors = listOf(Color(0xFFFF6B6B), Color(0xFFFF8E53)))
        ),
        Category(
            "Consola",
            Icons.Default.Gamepad,
            Brush.linearGradient(colors = listOf(Color(0xFF4E54C8), Color(0xFF8F94FB)))
        ),
        Category(
            "Computador Gamer",
            Icons.Default.Computer,
            Brush.linearGradient(colors = listOf(Color(0xFF00F5FF), Color(0xFF0099FF)))
        ),
        Category(
            "Silla Gamer",
            Icons.Default.Chair,
            Brush.linearGradient(colors = listOf(Color(0xFFB06AB3), Color(0xFF4568DC)))
        ),
        Category(
            "Accesorios",
            Icons.Default.Headset,
            Brush.linearGradient(colors = listOf(Color(0xFFFFA726), Color(0xFFFB8C00)))
        ),
        Category(
            "Ropa",
            Icons.Default.Checkroom,
            Brush.linearGradient(colors = listOf(Color(0xFFE91E63), Color(0xFF9C27B0)))
        ),
        Category(
            "Mouse",
            Icons.Default.Mouse,
            Brush.linearGradient(colors = listOf(Color(0xFF00E676), Color(0xFF00C853)))
        ),
        Category(
            "Mousepad",
            Icons.Default.CropSquare,
            Brush.linearGradient(colors = listOf(Color(0xFFFF5722), Color(0xFFFF9800)))
        )
    )

    // Posts del blog
    val blogPosts = listOf(
        BlogPost(
            "Te ayudamos a armar tu Pc Gamer a solo 500mil Pesos",
            "Descubre los mejores componentes para tu presupuesto",
            Brush.linearGradient(
                colors = listOf(Color(0xFF00F5FF), Color(0xFF0099FF)),
                start = Offset(0f, 0f),
                end = Offset(1000f, 1000f)
            )
        ),
        BlogPost(
            "10 Juegos de mesa para iniciarte",
            "Los mejores juegos para comenzar tu colección",
            Brush.linearGradient(
                colors = listOf(Color(0xFFFF6B6B), Color(0xFFFF8E53)),
                start = Offset(0f, 0f),
                end = Offset(1000f, 1000f)
            )
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logolvlup_playstore),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .height(46.dp)
                                .padding(end = 8.dp)
                        )
                        Text(
                            "Level UP gamer",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = NeonGreen
                            )
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = NeonGreen
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.background(CardBackground)
                    ) {
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        Icons.Default.Category,
                                        contentDescription = null,
                                        tint = NeonGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Categorías", color = TextWhite)
                                }
                            },
                            onClick = {
                                showMenu = false
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        Icons.Default.ShoppingCart,
                                        contentDescription = null,
                                        tint = ElectricBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Carrito", color = TextWhite)
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
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        Icons.Default.ContactMail,
                                        contentDescription = null,
                                        tint = Color(0xFFFF6B6B),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Contacto", color = TextWhite)
                                }
                            },
                            onClick = {
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = Color(0xFF00E676),  // Verde brillante
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Puntos Retiro", color = TextWhite)
                                }
                            },
                            onClick = {
                                showMenu = false
                                onNavigateToPuntosRetiro()
                            }
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
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header con gradiente
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF00F5FF),
                                    Color(0xFF00FF88),
                                    Color(0xFFFF00FF)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "EXPLORA NUESTRAS CATEGORÍAS",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.Black,
                                letterSpacing = 2.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Level Up Your Game 🎮",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            // Grid de Categorías
            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(800.dp)
                        .padding(16.dp),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(categories) { category ->
                        CategoryCard(
                            category = category,
                            onClick = {
                                onCategoryClick(category.name)
                            }
                        )
                    }
                }
            }

            // Sección de Blogs
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "📚 BLOGS RECOMENDADOS",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = NeonGreen,
                        letterSpacing = 1.sp
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // Carrusel de Blogs
            item {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(blogPosts.size) { index ->
                        BlogCard(
                            blogPost = blogPosts[index],
                            onClick = {
                                when (index) {
                                    0 -> onNavigateToPcGamerBlog()
                                    1 -> onNavigateToJuegosMesaBlog()
                                }
                            }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun CategoryCard(
    category: Category,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(category.gradient)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = category.name,
                    modifier = Modifier.size(48.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
fun BlogCard(
    blogPost: BlogPost,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .width(320.dp)
            .height(180.dp)
            .shadow(12.dp, RoundedCornerShape(20.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(blogPost.gradient)
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Icon(
                        imageVector = Icons.Default.Article,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = blogPost.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            lineHeight = 24.sp
                        )
                    )
                }

                Text(
                    text = blogPost.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(alpha = 0.9f)
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Leer más",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}