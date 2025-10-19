package com.example.midiventaslvlup.ui.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.midiventaslvlup.ui.theme.*
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class StarkenPoint(
    val name: String,
    val address: String,
    val phone: String,
    val geoPoint: GeoPoint,
    var distance: String = "Calculando..."
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PuntosRetiroScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var currentLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var locationStatus by remember { mutableStateOf("Obteniendo ubicación GPS...") }
    var hasLocationPermission by remember { mutableStateOf(false) }

    // Verificar permisos
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (hasLocationPermission) {
            locationStatus = "Obteniendo ubicación GPS..."
        } else {
            locationStatus = "Permisos de ubicación denegados"
            Toast.makeText(context, "Se requieren permisos de ubicación", Toast.LENGTH_LONG).show()
        }
    }

    // Ubicación predeterminada: Valparaíso (por si falla el GPS o no hay permisos)
    val defaultLocation = GeoPoint(-33.0472, -71.6127)

    // Puntos Starken cercanos a Valparaíso
    val starkenPoints = remember {
        mutableStateListOf(
            StarkenPoint(
                name = "Starken Valparaíso Centro",
                address = "Av. Pedro Montt 2055, Valparaíso",
                phone = "+56 32 225 8900",
                geoPoint = GeoPoint(-33.0398, -71.6269)
            ),
            StarkenPoint(
                name = "Starken Viña del Mar",
                address = "Av. Libertad 1348, Viña del Mar",
                phone = "+56 32 268 4500",
                geoPoint = GeoPoint(-33.0246, -71.5518)
            ),
            StarkenPoint(
                name = "Starken Plaza Vergara",
                address = "Av. Valparaíso 505, Viña del Mar",
                phone = "+56 32 297 1200",
                geoPoint = GeoPoint(-33.0244, -71.5516)
            ),
            StarkenPoint(
                name = "Starken Quilpué",
                address = "Av. O'Higgins 1250, Quilpué",
                phone = "+56 32 239 4800",
                geoPoint = GeoPoint(-33.0475, -71.4425)
            ),
            StarkenPoint(
                name = "Starken Villa Alemana",
                address = "Av. Santiago 501, Villa Alemana",
                phone = "+56 32 295 7300",
                geoPoint = GeoPoint(-33.0439, -71.3733)
            )
        )
    }

    // Solicitar ubicación GPS real
    LaunchedEffect(hasLocationPermission) {
        // Verificar permisos primero
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineLocationGranted || coarseLocationGranted) {
            hasLocationPermission = true

            // Obtener ubicación GPS REAL usando FusedLocationProviderClient (API nativa de Android)
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            val cancellationTokenSource = CancellationTokenSource()

            try {
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    cancellationTokenSource.token
                ).addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        currentLocation = GeoPoint(location.latitude, location.longitude)
                        locationStatus = "Ubicación GPS obtenida: ${location.latitude}, ${location.longitude}"

                        // Calcular distancias reales
                        starkenPoints.forEachIndexed { index, point ->
                            val distance = calculateDistance(
                                location.latitude, location.longitude,
                                point.geoPoint.latitude, point.geoPoint.longitude
                            )
                            starkenPoints[index] = point.copy(distance = String.format("%.1f km", distance))
                        }

                        // Ordenar por distancia
                        starkenPoints.sortBy { it.distance }

                        Toast.makeText(
                            context,
                            "✅ Ubicación GPS obtenida exitosamente",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        locationStatus = "No se pudo obtener ubicación GPS, usando ubicación predeterminada"
                        currentLocation = defaultLocation
                    }
                }.addOnFailureListener {
                    locationStatus = "Error GPS: ${it.message}, usando ubicación predeterminada"
                    currentLocation = defaultLocation
                }
            } catch (e: SecurityException) {
                locationStatus = "Error de permisos: ${e.message}"
                currentLocation = defaultLocation
            }
        } else {
            // Solicitar permisos
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Puntos de Retiro Starken",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Estado de ubicación GPS
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (currentLocation != null) Color(0xFF1B5E20) else Color(0xFF424242)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (currentLocation != null) Icons.Default.GpsFixed else Icons.Default.GpsNotFixed,
                        contentDescription = null,
                        tint = if (currentLocation != null) NeonGreen else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (currentLocation != null) "GPS Activo" else "Buscando GPS...",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = locationStatus,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            // Mapa OpenStreetMap
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                if (currentLocation != null) {
                    OpenStreetMapView(
                        context = context,
                        centerPoint = currentLocation!!,
                        starkenPoints = starkenPoints,
                        isRealLocation = true
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = NeonGreen)
                    }
                }
            }

            // Texto informativo
            Text(
                "Puntos más cercanos ${if (currentLocation != null) "a tu ubicación GPS" else "(ubicación predeterminada)"}:",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = NeonGreen
                ),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Lista de puntos Starken
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(starkenPoints) { point ->
                    StarkenPointCard(point = point)
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun OpenStreetMapView(
    context: Context,
    centerPoint: GeoPoint,
    starkenPoints: List<StarkenPoint>,
    isRealLocation: Boolean = false
) {
    AndroidView(
        factory = {
            Configuration.getInstance().userAgentValue = context.packageName

            MapView(context).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)

                controller.setZoom(13.0)
                controller.setCenter(centerPoint)

                // Marcador de ubicación actual
                val userMarker = Marker(this).apply {
                    position = centerPoint
                    title = if (isRealLocation) "📍 Tu ubicación GPS" else "📍 Ubicación predeterminada"
                    snippet = "Lat: ${centerPoint.latitude}, Lon: ${centerPoint.longitude}"
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                }
                overlays.add(userMarker)

                // Marcadores de puntos Starken
                starkenPoints.forEach { point ->
                    val marker = Marker(this).apply {
                        position = point.geoPoint
                        title = "🏪 ${point.name}"
                        snippet = "${point.address} - ${point.distance}"
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    }
                    overlays.add(marker)
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun StarkenPointCard(point: StarkenPoint) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = point.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = NeonGreen
                    ),
                    modifier = Modifier.weight(1f)
                )
                Badge(
                    containerColor = ElectricBlue,
                    contentColor = Color.White
                ) {
                    Text(
                        point.distance,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = TextWhite.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = point.address,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextWhite.copy(alpha = 0.8f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    tint = TextWhite.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = point.phone,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextWhite.copy(alpha = 0.8f)
                    )
                )
            }
        }
    }
}

// Función para calcular distancia real entre dos coordenadas (Fórmula de Haversine)
fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val R = 6371.0 // Radio de la Tierra en km
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return R * c
}