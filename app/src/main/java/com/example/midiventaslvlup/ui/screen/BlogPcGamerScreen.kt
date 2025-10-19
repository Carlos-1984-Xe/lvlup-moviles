package com.example.midiventaslvlup.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.midiventaslvlup.ui.theme.*

data class PcComponent(
    val component: String,
    val recommended: String,
    val price: String,
    val alternative: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlogPcGamerScreen(
    onBackClick: () -> Unit
) {
    val components = listOf(
        PcComponent("CPU", "AMD Ryzen 5 5600", "$140.000", "Ryzen 5 3600 ($110.000)"),
        PcComponent("Placa Madre", "B450M / B550M", "$75.000", "A320M ($55.000)"),
        PcComponent("GPU", "Radeon RX 6600", "$210.000", "GTX 1660 Super ($170.000)"),
        PcComponent("RAM", "16GB DDR4 (2x8GB, 3200MHz)", "$55.000", "8GB DDR4 (1x8GB) ($28.000)"),
        PcComponent("Almacenamiento", "SSD NVMe 500GB", "$30.000", "SSD SATA 480GB ($25.000)"),
        PcComponent("Gabinete+Fuente", "Mid Tower + 500W 80+", "$35.000", "Gabinete básico + genérica ($25.000)")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "PC Gamer Económico",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = NeonGreen
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = NeonGreen
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
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Título principal con borde
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, NeonGreen, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.Black),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "¿Cómo Armar un PC Gamer Económico en Chile?",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = NeonGreen,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }
            }

            // Primer párrafo
            item {
                Text(
                    buildAnnotatedString {
                        append("Armar un PC gamer económico en Chile es posible sin superar los ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = NeonGreen)) {
                            append("$500.000")
                        }
                        append(", siempre que se escojan componentes equilibrados y con buena relación precio-rendimiento. Una excelente opción es comenzar con el ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = NeonGreen)) {
                            append("AMD Ryzen 5 5600")
                        }
                        append(", un procesador de 6 núcleos con gran desempeño en juegos y multitarea, junto a una placa base ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = NeonGreen)) {
                            append("B450M")
                        }
                        append(" o ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = NeonGreen)) {
                            append("B550M")
                        }
                        append(", que ofrece estabilidad y posibilidades de expansión para futuro.")
                    },
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = TextWhite,
                        lineHeight = 24.sp
                    )
                )
            }

            // Imagen 1
            item {
                AsyncImage(
                    model = "https://i.ytimg.com/vi/p0q5TQkqN-Y/hq720.jpg?sqp=-oaymwEhCK4FEIIDSFryq4qpAxMIARUAAAAAGAElAADIQj0AgKJD&rs=AOn4CLDKN4KxA4BMLzpkrdvJx5yqMAcfpQ",
                    contentDescription = "PC Gamer Económico",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(2.dp, NeonGreen, RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            // Segundo bloque: Imagen + Texto
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = "https://i.ytimg.com/vi/XCpfjcL9GAk/hq720.jpg?sqp=-oaymwEhCK4FEIIDSFryq4qpAxMIARUAAAAAGAElAADIQj0AgKJD&rs=AOn4CLAqQn0ZHOqi8kga4IrsKtz5HOQcSQ",
                        contentDescription = "PC Alternativo",
                        modifier = Modifier
                            .weight(0.4f)
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(2.dp, NeonGreen, RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Text(
                        buildAnnotatedString {
                            append("En cuanto a gráfica, la ")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = NeonGreen)) {
                                append("Radeon RX 6600")
                            }
                            append(" se posiciona como la mejor alternativa budget para jugar en 1080p con calidad alta, mientras que ")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = NeonGreen)) {
                                append("16GB de RAM DDR4")
                            }
                            append(" (2x8GB, 3200MHz) aseguran fluidez en prácticamente todos los títulos modernos.")
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextWhite,
                            lineHeight = 22.sp
                        ),
                        modifier = Modifier.weight(0.6f)
                    )
                }
            }

            // Título de tabla
            item {
                Text(
                    "Desglose de Componentes y Precios",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = NeonGreen,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Tabla de componentes
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, NeonGreen, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        // Header de la tabla
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Black, RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Componente",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NeonGreen
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                "Recomendado",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NeonGreen
                                ),
                                modifier = Modifier.weight(1.5f)
                            )
                            Text(
                                "Precio",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NeonGreen
                                ),
                                modifier = Modifier.weight(0.8f),
                                textAlign = TextAlign.End
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Filas de la tabla
                        components.forEach { component ->
                            ComponentRow(component)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }

            // Párrafo final
            item {
                Text(
                    "Como ves, con una selección inteligente puedes armar un PC gamer capaz de correr la mayoría de los títulos actuales sin romper el bolsillo. Si necesitas ajustar aún más el presupuesto, puedes optar por las alternativas más baratas de la tabla y dejar espacio para futuras actualizaciones.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = TextWhite,
                        lineHeight = 24.sp,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun ComponentRow(component: PcComponent) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1A1A1A), RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                component.component,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                modifier = Modifier.weight(1f)
            )
            Text(
                component.recommended,
                style = MaterialTheme.typography.bodySmall.copy(color = Color.White),
                modifier = Modifier.weight(1.5f)
            )
            Text(
                component.price,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = NeonGreen
                ),
                modifier = Modifier.weight(0.8f),
                textAlign = TextAlign.End
            )
        }

        if (component.alternative.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Alternativa: ${component.alternative}",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.Gray,
                    fontSize = 11.sp
                )
            )
        }
    }
}
