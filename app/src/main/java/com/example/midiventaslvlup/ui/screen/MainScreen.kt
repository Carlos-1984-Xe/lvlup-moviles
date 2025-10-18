package com.example.midiventaslvlup.ui.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.midiventaslvlup.R
import com.example.midiventaslvlup.viewmodel.LoginViewModel

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    onNavigateToDetails: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onNavigateToCart: () -> Unit = {},
    loginViewModel: LoginViewModel = viewModel()
) {
    val loginState by loginViewModel.loginState.collectAsState()
    val context = LocalContext.current

    // Manejar navegación según el resultado del login
    LaunchedEffect(loginState.loginSuccess) {
        if (loginState.loginSuccess) {
            when (loginState.userRole) {
                "administrador" -> onNavigateToAdmin()
                "cliente" -> onNavigateToDetails()
                else -> onNavigateToDetails()
            }
            loginViewModel.resetLoginState()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
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
            Text(
                text = "Bienvenido a MiDiVentaslvlup",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextField(
                        value = loginState.usuario,
                        onValueChange = { loginViewModel.onUsuarioChange(it) },
                        label = { Text("Correo electrónico") },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !loginState.isLoading,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    TextField(
                        value = loginState.contrasena,
                        onValueChange = { loginViewModel.onContrasenaChange(it) },
                        label = { Text("Contraseña") },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !loginState.isLoading,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true
                    )

                    // Mostrar mensaje de error si existe
                    if (loginState.errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = loginState.errorMessage!!,
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { loginViewModel.login() },
                        enabled = !loginState.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (loginState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White
                            )
                        } else {
                            Text("Iniciar Sesión")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onNavigateToDetails,
                        enabled = !loginState.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Iniciar como invitado")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Usuarios de prueba:",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Text(
                text = "Admin: admin@duocuc.cl / admin123",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Text(
                text = "Cliente: cliente@gmail.com / cliente123",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        // Botón de emergencia para crear usuarios de prueba
        IconButton(
            onClick = {
                loginViewModel.createTestUsers()
                Toast.makeText(context, "Usuarios de prueba creados", Toast.LENGTH_SHORT).show()
            },
            enabled = !loginState.isLoading,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Build,
                contentDescription = "Crear usuarios de prueba",
                tint = Color(0xFFFF6B6B)
            )
        }
    }
}
