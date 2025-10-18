package com.example.midiventaslvlup.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.midiventaslvlup.R

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    onNavigateToDetails: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onNavigateToCart: () -> Unit = {}  // ← AGREGAR ESTE PARÁMETRO
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logolvlup_playstore),
            contentDescription = "Logo",
            modifier = Modifier.size(150.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Bienvenido a MiDiVentaslvlup", modifier = Modifier.padding(bottom = 24.dp))
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("Usuario") },
                    shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("Contraseña") },
                    shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = onNavigateToDetails) {
                    Text("Iniciar Sesión")
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = onNavigateToDetails) {
                    Text("Iniciar como invitado")
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = onNavigateToAdmin) {
                    Text("Acceder como Administrador")
                }
            }
        }
    }
}