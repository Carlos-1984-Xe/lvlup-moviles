package com.example.midiventaslvlup.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.midiventaslvlup.R
import com.example.midiventaslvlup.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    // Estados de animación
    var logoScale by remember { mutableStateOf(0.3f) }
    var textAlpha by remember { mutableStateOf(0f) }
    var progress by remember { mutableStateOf(0f) }
    var currentMessageIndex by remember { mutableStateOf(0) }
    var showGlowEffect by remember { mutableStateOf(false) }

    // Mensajes de carga gaming
    val loadingMessages = listOf(
        "Subiendo la RAM...",
        "Batiendo High Scores...",
        "Maximizando tus FPS...",
        "Activando modo Gamer...",
        "Cargando inventario épico..."
    )

    // Animación infinita para el efecto de rotación del borde
    val infiniteTransition = rememberInfiniteTransition(label = "borderRotation")

    val borderRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "borderRotation"
    )

    // Animación de pulso para el glow
    val pulseAnimation by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseGlow"
    )

    // Efecto principal de carga
    LaunchedEffect(Unit) {
        // Espera inicial
        delay(200)

        // Animar escala del logo
        animate(
            initialValue = 0.3f,
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) { value, _ ->
            logoScale = value
        }

        // Activar efecto de glow
        delay(300)
        showGlowEffect = true

        // Fade in del texto
        delay(200)
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(1000, easing = FastOutSlowInEasing)
        ) { value, _ ->
            textAlpha = value
        }

        // Animar barra de progreso con cambios de mensaje
        var elapsed = 0
        while (progress < 1f) {
            delay(30)
            progress = (progress + 0.008f).coerceAtMost(1f)
            elapsed += 30

            // Cambiar mensaje cada 1.2 segundos
            if (elapsed % 1200 == 0 && currentMessageIndex < loadingMessages.size - 1) {
                currentMessageIndex++
            }
        }

        // Espera final antes de continuar
        delay(600)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 60.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Contenedor del logo con efectos
            Box(
                contentAlignment = Alignment.Center
            ) {
                // Efecto de glow de fondo
                if (showGlowEffect) {
                    Box(
                        modifier = Modifier
                            .size(220.dp * pulseAnimation)
                            .scale(logoScale)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        NeonGreen.copy(alpha = 0.3f),
                                        ElectricBlue.copy(alpha = 0.2f),
                                        Color.Transparent
                                    ),
                                    radius = 300f
                                )
                            )
                    )
                }

                // Logo con borde animado
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .scale(logoScale)
                        .size(180.dp)
                        .rotate(borderRotation)
                        .border(
                            width = 3.dp,
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    NeonGreen,
                                    NeonGreen.copy(alpha = 0.3f),
                                    ElectricBlue,
                                    ElectricBlue.copy(alpha = 0.3f),
                                    NeonGreen
                                ),
                                center = Offset.Zero
                            ),
                            shape = CircleShape
                        )
                        .shadow(
                            elevation = if (showGlowEffect) 20.dp else 0.dp,
                            shape = CircleShape,
                            spotColor = NeonGreen
                        )
                ) {
                    // Placeholder para el logo (círculo blanco si no existe la imagen)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(3.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                }

                // Logo real (sin rotación)
                Image(
                    painter = painterResource(id = R.drawable.logolvlup_round),
                    contentDescription = "Logo Level Up Gamer",
                    modifier = Modifier
                        .scale(logoScale)
                        .size(174.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Texto principal con efecto de aparición
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Bienvenido a la experiencia",
                    color = TextWhite.copy(alpha = textAlpha),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.alpha(textAlpha)
                )

                AnimatedVisibility(
                    visible = textAlpha > 0.5f,
                    enter = fadeIn(animationSpec = tween(800)) +
                            slideInVertically(initialOffsetY = { 20 })
                ) {
                    Text(
                        text = "LEVEL UP GAMER",
                        color = NeonGreen,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 3.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Sección de carga
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Mensaje de carga con animación
                AnimatedContent(
                    targetState = loadingMessages[currentMessageIndex],
                    transitionSpec = {
                        (fadeIn(animationSpec = tween(300)) +
                                slideInVertically(animationSpec = tween(300), initialOffsetY = { 20 }))
                            .togetherWith(
                                fadeOut(animationSpec = tween(300)) +
                                        slideOutVertically(animationSpec = tween(300), targetOffsetY = { -20 })
                            )
                    },
                    label = "loadingMessage"
                ) { message ->
                    Text(
                        text = message,
                        color = ElectricBlue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                // Barra de progreso customizada
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                ) {
                    // Fondo de la barra
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(CardBackground)
                    )

                    // Progreso con gradiente
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .fillMaxHeight()
                            .clip(CircleShape)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        ElectricBlue,
                                        NeonGreen,
                                        ElectricBlue
                                    )
                                )
                            )
                    )
                }

                // Porcentaje de carga
                Text(
                    text = "${(progress * 100).toInt()}%",
                    color = GrayText,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}