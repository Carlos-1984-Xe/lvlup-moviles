package com.example.midiventaslvlup

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MainScreen(modifier: Modifier = Modifier, onNavigateToDetails: () -> Unit) {
    Column(modifier = modifier) {
        Text("Bienvenido a MiDiVentaslvlup")
        TextField(value = "", onValueChange = {}, label = { Text("Usuario") })
        TextField(value = "", onValueChange = {}, label = { Text("Contraseña") })
        Button(onClick = onNavigateToDetails) {
            Text("Iniciar Sesión")
        }
    }
}