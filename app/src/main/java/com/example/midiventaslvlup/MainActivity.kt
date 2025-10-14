package com.example.midiventaslvlup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.midiventaslvlup.ui.screen.DetailsScreen
import com.example.midiventaslvlup.ui.screen.MainScreen
import com.example.midiventaslvlup.ui.theme.LevelUpGamerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LevelUpGamerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "main", modifier = Modifier.padding(innerPadding)) {
                        composable("main") { MainScreen(modifier = Modifier.fillMaxSize(), onNavigateToDetails = { navController.navigate("details") }) }
                        composable("details") { DetailsScreen(modifier = Modifier.fillMaxSize()) }
                    }
                }
            }
        }
    }
}