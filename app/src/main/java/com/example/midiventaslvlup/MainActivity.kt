package com.example.midiventaslvlup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.midiventaslvlup.ui.screen.AdminScreen
import com.example.midiventaslvlup.ui.screen.BlogJuegosMesaScreen  // ← NUEVO IMPORT
import com.example.midiventaslvlup.ui.screen.BlogPcGamerScreen  // ← NUEVO IMPORT
import com.example.midiventaslvlup.ui.screen.CartScreen
import com.example.midiventaslvlup.ui.screen.DetalleProductoScreen
import com.example.midiventaslvlup.ui.screen.DetailsScreen
import com.example.midiventaslvlup.ui.screen.MainScreen
import com.example.midiventaslvlup.ui.screen.ProductManagementScreen
import com.example.midiventaslvlup.ui.screen.ProductosScreen
import com.example.midiventaslvlup.ui.screen.SplashScreen
import com.example.midiventaslvlup.ui.screen.UserManagementScreen
import com.example.midiventaslvlup.ui.theme.LevelUpGamerTheme
import com.example.midiventaslvlup.ui.screen.PuntosRetiroScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LevelUpGamerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "splash",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("splash") {
                            SplashScreen(
                                onSplashFinished = {
                                    navController.navigate("main") { popUpTo("splash") { inclusive = true } }
                                }
                            )
                        }

                        composable("main") {
                            MainScreen(
                                modifier = Modifier.fillMaxSize(),
                                onNavigateToDetails = { navController.navigate("details") },
                                onNavigateToAdmin = { navController.navigate("admin") },
                                onNavigateToCart = { navController.navigate("cart") }
                            )
                        }

                        composable("admin") {
                            AdminScreen(
                                onNavigateToUserManagement = { action ->
                                    navController.navigate("userManagement/$action")
                                },
                                onNavigateToProductManagement = { action ->
                                    navController.navigate("productManagement/$action")
                                },
                                onNavigateToLogin = {
                                    navController.navigate("main") { popUpTo("admin") { inclusive = true } }
                                }
                            )
                        }

                        composable(
                            route = "userManagement/{action}",
                            arguments = listOf(navArgument("action") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val action = backStackEntry.arguments?.getString("action") ?: ""
                            UserManagementScreen(
                                action = action,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable(
                            route = "productManagement/{action}",
                            arguments = listOf(navArgument("action") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val action = backStackEntry.arguments?.getString("action") ?: ""
                            ProductManagementScreen(
                                action = action,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("details") {
                            DetailsScreen(
                                modifier = Modifier.fillMaxSize(),
                                onCategoryClick = { categoria ->
                                    navController.navigate("productos/$categoria")
                                },
                                onNavigateToCart = { navController.navigate("cart") },
                                onNavigateToPcGamerBlog = {
                                    navController.navigate("blogPcGamer")
                                },
                                onNavigateToJuegosMesaBlog = {
                                    navController.navigate("blogJuegosMesa")
                                },
                                onNavigateToPuntosRetiro = {  // ← AGREGAR ESTO
                                    navController.navigate("puntosRetiro")
                                }
                            )
                        }

                        composable(
                            route = "productos/{categoria}",
                            arguments = listOf(
                                navArgument("categoria") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val categoria = backStackEntry.arguments?.getString("categoria") ?: ""
                            ProductosScreen(
                                categoria = categoria,
                                onBackClick = { navController.popBackStack() },
                                onProductClick = { producto ->
                                    navController.navigate("productDetail/${producto.id}")
                                }
                            )
                        }

                        composable(
                            route = "productDetail/{productId}",
                            arguments = listOf(navArgument("productId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val productId = backStackEntry.arguments?.getInt("productId") ?: 0
                            DetalleProductoScreen(
                                productId = productId,
                                modifier = Modifier.fillMaxSize(),
                                onNavigateToCart = { navController.navigate("cart") }
                            )
                        }

                        composable("cart") {
                            CartScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        // ← NUEVA RUTA: Blog PC Gamer
                        composable("blogPcGamer") {
                            BlogPcGamerScreen(
                                onBackClick = { navController.popBackStack() }
                            )
                        }

                        // ← NUEVA RUTA: Blog Juegos de Mesa
                        composable("blogJuegosMesa") {
                            BlogJuegosMesaScreen(
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                        composable("puntosRetiro") {
                            PuntosRetiroScreen(
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}