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

data class BoardGame(
    val name: String,
    val price: String,
    val imageUrl: String,
    val height: Int = 250
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlogJuegosMesaScreen(
    onBackClick: () -> Unit
) {
    val games = listOf(
        BoardGame(
            "Codenames",
            "≈$20.000",
            "https://i0.wp.com/la-matatena.com/wp-content/uploads/2016/11/912e9b5c3c4311e598faf23c91709c91_1438869660-e1480708531784.jpg?resize=600%2C343",
            300
        ),
        BoardGame(
            "Dixit",
            "≈$45.000",
            "https://imagenes.20minutos.es/files/image_1920_1080/uploads/imagenes/2022/04/07/el-juego-de-mesa-dixit.jpeg",
            250
        ),
        BoardGame(
            "Splendor",
            "≈$38.000",
            "https://i0.wp.com/misutmeeple.com/wp-content/uploads/2015/05/splendor_partida_preparada.jpg?resize=1200%2C549&ssl=1",
            250
        ),
        BoardGame(
            "Kingdomino",
            "≈$25.000",
            "https://2.blogs.elcomercio.pe/geekgames/wp-content/uploads/sites/57/2017/09/Kingdomino-Header.jpg",
            160
        ),
        BoardGame(
            "Azul",
            "≈$40.000",
            "https://i0.wp.com/misutmeeple.com/wp-content/uploads/2017/12/azul_detalle_tablero.jpg?resize=1200%2C791&ssl=1",
            180
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Juegos de Mesa Modernos",
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
            // Título principal
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
                            "Juegos de Mesa Modernos para Empezar Sin Gastar Demasiado",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = NeonGreen,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }
            }

            // Intro + Codenames
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Si quieres comenzar en el mundo de los juegos de mesa modernos sin gastar demasiado, hay una gran variedad de títulos ideales para dar el primer paso. Atrás quedaron los días en que \"jugar de mesa\" era sinónimo solo de Monopoly o Uno. Hoy existe una enorme oferta de juegos frescos, entretenidos y fáciles de aprender, con reglas que en pocos minutos pueden hacer que cualquier reunión se transforme en una experiencia memorable.",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = TextWhite,
                            lineHeight = 24.sp
                        )
                    )

                    GameCard(game = games[0])
                }
            }

            // Codenames y Dixit (alternado)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        buildAnnotatedString {
                            append("Entre los más recomendados para nuevos jugadores destacan títulos como ")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = NeonGreen)) {
                                append("Codenames")
                            }
                            append(", perfecto para jugar en equipos y poner a prueba tu capacidad de asociación de palabras, y ")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = NeonGreen)) {
                                append("Dixit")
                            }
                            append(", un juego visual y narrativo que despierta la imaginación con ilustraciones únicas.")
                        },
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = TextWhite,
                            lineHeight = 24.sp
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                GameCard(game = games[1])
            }

            // Splendor (imagen izquierda)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GameCard(
                        game = games[2],
                        modifier = Modifier.weight(0.45f)
                    )

                    Text(
                        buildAnnotatedString {
                            append("También sobresalen ")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = NeonGreen)) {
                                append("Splendor")
                            }
                            append(", ideal para quienes disfrutan de estrategias simples pero profundas, y ")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = NeonGreen)) {
                                append("Kingdomino")
                            }
                            append(", una versión moderna y rápida de los clásicos juegos de construcción de reinos.")
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextWhite,
                            lineHeight = 22.sp
                        ),
                        modifier = Modifier.weight(0.55f)
                    )
                }
            }

            // Kingdomino
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        GameCard(game = games[3])
                    }
                }
            }

            // Otros juegos
            item {
                Text(
                    buildAnnotatedString {
                        append("Si buscas juegos familiares o para compartir con amigos en ambientes más relajados, opciones como ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF00BCD4))) {
                            append("Sushi Go!")
                        }
                        append(", ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF00BCD4))) {
                            append("Love Letter")
                        }
                        append(" y ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF00BCD4))) {
                            append("Hanabi")
                        }
                        append(" ofrecen partidas rápidas y muy rejugables. En la misma línea, ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFFFFA726))) {
                            append("Carcassonne")
                        }
                        append(" y ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFFFFA726))) {
                            append("Ticket to Ride: New York")
                        }
                        append(" son versiones más compactas de grandes clásicos que mantienen la esencia de la estrategia pero con partidas que no superan los 30 minutos.")
                    },
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = TextWhite,
                        lineHeight = 24.sp
                    )
                )
            }

            // Azul y conclusión
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        buildAnnotatedString {
                            append("Finalmente, ")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF2196F3))) {
                                append("Azul")
                            }
                            append(" se convierte en un imprescindible gracias a su mezcla de belleza estética y sencillez en la mecánica. Lo mejor de esta selección es que todos estos juegos se mantienen bajo los $50.000, lo que los convierte en una excelente puerta de entrada al hobby.")
                        },
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = TextWhite,
                            lineHeight = 24.sp
                        ),
                        modifier = Modifier.weight(0.6f)
                    )

                    GameCard(
                        game = games[4],
                        modifier = Modifier.weight(0.4f)
                    )
                }
            }

            // Conclusión final
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, NeonGreen, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Además, la mayoría ocupa poco espacio, se explica en minutos y puede disfrutarse tanto en familia como con grupos de amigos. Con esta lista, armar tu primera ludoteca será sencillo, entretenido y sin romper el bolsillo.",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = TextWhite,
                            lineHeight = 24.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun GameCard(
    game: BoardGame,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(2.dp, NeonGreen, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            AsyncImage(
                model = game.imageUrl,
                contentDescription = game.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(game.height.dp),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    game.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = NeonGreen
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Badge(
                    containerColor = NeonGreen,
                    contentColor = Color.Black
                ) {
                    Text(
                        game.price,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}