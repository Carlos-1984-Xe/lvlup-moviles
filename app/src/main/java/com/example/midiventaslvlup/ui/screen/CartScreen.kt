package com.example.midiventaslvlup.ui.screen

import android.app.Application
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.midiventaslvlup.model.repository.CartRepository
import com.example.midiventaslvlup.model.repository.OrderRepository
import com.example.midiventaslvlup.ui.theme.GreenPrimary
import com.example.midiventaslvlup.util.SessionManager
import com.example.midiventaslvlup.viewmodel.CartViewModel
import com.example.midiventaslvlup.viewmodel.CartViewModelFactory


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    val userId = SessionManager.getUserId(context)

    val cartRepository = remember { CartRepository() }
    val orderRepository = remember { OrderRepository() }

    val viewModel: CartViewModel = viewModel(
        factory = CartViewModelFactory(
            application = context.applicationContext as Application,
            cartRepository = cartRepository,
            orderRepository = orderRepository,
            userId = userId
        )
    )

    val cartItems by viewModel.cartItems.collectAsState()
    val cartTotal by viewModel.cartTotal.collectAsState()
    val couponCode by viewModel.couponCode.collectAsState()
    val discount by viewModel.discount.collectAsState()
    val finalTotal by viewModel.finalTotal.collectAsState()

    var showPaymentDialog by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }
    var showSuccessSnackbar by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

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
        snackbarHost = {
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(cartItems) { item ->
                        CartItemCard(
                            item = item,
                            onIncreaseQuantity = { viewModel.increaseQuantity(item.productId) },
                            onDecreaseQuantity = { viewModel.decreaseQuantity(item.productId) },
                            onRemove = { viewModel.removeFromCart(item.productId) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

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
                        Text(
                            text = "Total: $$cartTotal",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

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

    if (showPaymentDialog) {
        var metodoPago by remember { mutableStateOf("Efectivo") }
        var direccionEnvio by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showPaymentDialog = false },
            title = { Text("Confirmar pago") },
            text = {
                Column {
                    Text("Total a pagar: $$finalTotal")
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = direccionEnvio,
                        onValueChange = { direccionEnvio = it },
                        label = { Text("Dirección de envío") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = metodoPago,
                        onValueChange = { metodoPago = it },
                        label = { Text("Método de pago") },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Efectivo, Tarjeta, etc.") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (direccionEnvio.isNotBlank()) {
                            viewModel.processPayment(
                                metodoPago = metodoPago,
                                direccionEnvio = direccionEnvio
                            ) {
                                showPaymentDialog = false
                                showSuccessSnackbar = true
                            }
                        }
                    },
                    enabled = direccionEnvio.isNotBlank()
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

    LaunchedEffect(showSuccessSnackbar) {
        if (showSuccessSnackbar) {
            snackbarHostState.showSnackbar(
                message = "¡Pago realizado con éxito!",
                duration = SnackbarDuration.Long
            )
            showSuccessSnackbar = false
        }
    }
}
