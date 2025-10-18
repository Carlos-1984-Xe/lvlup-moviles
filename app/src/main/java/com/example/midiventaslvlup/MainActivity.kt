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
import com.example.midiventaslvlup.ui.screen.CartScreen
import com.example.midiventaslvlup.ui.screen.DetalleProductoScreen
import com.example.midiventaslvlup.ui.screen.DetailsScreen
import com.example.midiventaslvlup.ui.screen.MainScreen
import com.example.midiventaslvlup.ui.screen.SplashScreen
import com.example.midiventaslvlup.ui.screen.UserManagementScreen
import com.example.midiventaslvlup.ui.theme.LevelUpGamerTheme

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
                                    navController.navigate("main") {
                                        popUpTo("splash") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("main") {
                            MainScreen(
                                modifier = Modifier.fillMaxSize(),
                                onNavigateToDetails = {
                                    navController.navigate("details")
                                },
                                onNavigateToAdmin = {
                                    navController.navigate("admin")
                                },
                                onNavigateToCart = {  // ← AGREGAR ESTE PARÁMETRO
                                    navController.navigate("cart")
                                }
                            )
                        }

                        composable("admin") {
                            AdminScreen(onNavigateToUserManagement = { action ->
                                navController.navigate("userManagement/$action")
                            })
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

                        composable("details") {
                            DetailsScreen(
                                modifier = Modifier.fillMaxSize(),
                                onProductClick = { productId ->
                                    navController.navigate("productDetail/$productId")
                                },
                                onNavigateToCart = {  // ← AGREGAR ESTE PARÁMETRO
                                    navController.navigate("cart")
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
                                onNavigateToCart = {  // ← AGREGAR ESTE PARÁMETRO
                                    navController.navigate("cart")
                                }
                            )
                        }

                        // ← AGREGAR ESTA NUEVA RUTA DEL CARRITO
                        composable("cart") {
                            CartScreen(
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}