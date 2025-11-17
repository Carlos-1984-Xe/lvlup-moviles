package com.example.midiventaslvlup.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.midiventaslvlup.network.dto.CartItemDto
import com.example.midiventaslvlup.ui.theme.GreenPrimary

@Composable
fun CartItemCard(
    item: CartItemDto,
    onIncreaseQuantity: () -> Unit,
    onDecreaseQuantity: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen del producto
            AsyncImage(
                // ✅ CORRECCIÓN: Si la imagen es nula, usa una cadena vacía para evitar el crash.
                model = item.productImage ?: "",
                // ✅ CORRECCIÓN: Si el nombre es nulo, usa un texto genérico.
                contentDescription = item.productName ?: "Imagen de producto",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Información del producto
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    // ✅ CORRECCIÓN: Proporciona un valor por defecto si el nombre es nulo.
                    text = item.productName ?: "Producto no disponible",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    // ✅ CORRECCIÓN: Proporciona un valor por defecto.
                    text = item.productCategory ?: "Sin categoría",
                    fontSize = 12.sp,
                    color = GreenPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    // ✅ CORRECCIÓN: Usa 0 como precio por defecto si es nulo.
                    text = "Precio: $${item.unitPrice ?: 0}",
                    fontSize = 14.sp,
                    color = Color.LightGray
                )

                Text(
                    // ✅ CORRECCIÓN: Usa 0 como subtotal por defecto si es nulo.
                    text = "Subtotal: $${item.subtotal ?: 0}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GreenPrimary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Controles de cantidad y eliminar
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Botón eliminar
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = Color(0xFFDC3545),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Controles de cantidad
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Botón disminuir
                    IconButton(
                        onClick = onDecreaseQuantity,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Remove,
                            contentDescription = "Disminuir",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Cantidad
                    Text(
                        // La cantidad rara vez será nula, pero es buena práctica protegerla también.
                        text = "${item.quantity}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.widthIn(min = 24.dp)
                    )

                    // Botón aumentar
                    IconButton(
                        onClick = onIncreaseQuantity,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Aumentar",
                            tint = GreenPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}