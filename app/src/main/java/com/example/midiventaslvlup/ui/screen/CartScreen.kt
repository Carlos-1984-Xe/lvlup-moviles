package com.example.midiventaslvlup.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.midiventaslvlup.ui.theme.GreenPrimary
import com.example.midiventaslvlup.viewmodel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: CartViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val cartItems by viewModel.cartItems.collectAsState(initial = emptyList())
    val cartTotal by viewModel.cartTotal.collectAsState()
    val couponCode by viewModel.couponCode.collectAsState()
    val discount by viewModel.discount.collectAsState()
    val finalTotal by viewModel.finalTotal.collectAsState()

    var showPaymentDialog by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }
    var showSuccessSnackbar by remember { mutableStateOf(false) }  // ← AGREGAR

    val snackbarHostState = remember { SnackbarHostState() }  // ← AGREGAR

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tu Carrito", color = GreenPrimary) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver", tint = GreenPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A1A1A)
                )
            )
        },
        snackbarHost = {  // ← AGREGAR
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = GreenPrimary,
                    contentColor = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
            }
        },
        containerColor = Color.Black
    ) { padding ->

        if (cartItems.isEmpty()) {
            // Carrito vacío
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No hay productos en el carrito.",
                    color = Color.White,
                    fontSize = 18.sp
                )
            }
        } else {
            // Carrito con productos
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                // Lista de productos
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(cartItems) { item ->
                        CartItemCard(
                            item = item,
                            onIncreaseQuantity = { viewModel.increaseQuantity(item) },
                            onDecreaseQuantity = { viewModel.decreaseQuantity(item) },
                            onRemove = { viewModel.removeFromCart(item) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Panel de resumen
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF2A2A2A)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        // Total
                        Text(
                            text = "Total: $$cartTotal",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Cupón de descuento
                        OutlinedTextField(
                            value = couponCode,
                            onValueChange = { viewModel.updateCouponCode(it) },
                            label = { Text("Ingresa tu cupón") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = GreenPrimary,
                                unfocusedBorderColor = Color.Gray,
                                focusedLabelColor = GreenPrimary,
                                unfocusedLabelColor = Color.Gray
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Botón aplicar cupón
                        Button(
                            onClick = { viewModel.applyCoupon() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1E6F5C)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Aplicar cupón", fontSize = 16.sp)
                        }

                        // Mostrar descuento si existe
                        if (discount > 0) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Descuento aplicado: -$$discount",
                                color = GreenPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Total con descuento: $$finalTotal",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Botón Pagar
                        Button(
                            onClick = { showPaymentDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GreenPrimary
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Pagar", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Botón Vaciar carrito
                        Button(
                            onClick = { showClearDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFDC3545)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Vaciar carrito", fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }

    // Diálogo de confirmación de pago
    if (showPaymentDialog) {
        AlertDialog(
            onDismissRequest = { showPaymentDialog = false },
            title = { Text("Confirmar pago") },
            text = { Text("¿Deseas proceder con el pago de $$finalTotal?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.processPayment {
                            showPaymentDialog = false
                            showSuccessSnackbar = true  // ← ACTUALIZAR
                        }
                    }
                ) {
                    Text("Confirmar", color = GreenPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPaymentDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo de confirmación para vaciar carrito
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Vaciar carrito") },
            text = { Text("¿Estás seguro de que deseas vaciar el carrito?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearCart()
                        showClearDialog = false
                    }
                ) {
                    Text("Vaciar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // ← AGREGAR: Snackbar de éxito
    LaunchedEffect(showSuccessSnackbar) {
        if (showSuccessSnackbar) {
            snackbarHostState.showSnackbar(
                message = "¡Pago realizado con éxito! Stock actualizado ✓",
                duration = SnackbarDuration.Long
            )
            showSuccessSnackbar = false
        }
    }
}