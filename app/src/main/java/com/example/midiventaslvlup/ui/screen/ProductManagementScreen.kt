package com.example.midiventaslvlup.ui.screen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.midiventaslvlup.model.repository.ProductRepository
import com.example.midiventaslvlup.network.dto.ProductDto
import com.example.midiventaslvlup.viewmodel.ProductViewModel
import com.example.midiventaslvlup.viewmodel.ProductViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductManagementScreen(action: String, onNavigateBack: () -> Unit) {

    var currentAction by remember { mutableStateOf(action) }
    var selectedProductId by remember { mutableStateOf<Long?>(null) }

    val productRepository = remember { ProductRepository() }  // ✅ Sin parámetros, usa RetrofitClient
    val viewModel: ProductViewModel = viewModel(factory = ProductViewModelFactory(productRepository))

    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val titleText = when (currentAction) {
                        "list" -> "Listado de Productos"
                        "create" -> "Crear Producto"
                        "edit" -> "Editar Producto"
                        "delete" -> "Borrar Producto"
                        else -> "Producto"
                    }
                    Text(text = titleText)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Cargando...")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                when (currentAction) {
                    "create" -> CreateProductForm(
                        viewModel = viewModel,
                        onProductCreated = onNavigateBack,
                        onCancel = onNavigateBack
                    )
                    "edit" -> EditProductForm(
                        viewModel = viewModel,
                        productId = selectedProductId,
                        onProductUpdated = { currentAction = "list" },
                        onCancel = {
                            selectedProductId = null
                            currentAction = "list"
                        }
                    )
                    "list" -> ListProducts(
                        viewModel = viewModel,
                        onEditClick = {
                            selectedProductId = it
                            currentAction = "edit"
                        },
                        onDeleteClick = {
                            selectedProductId = it
                            currentAction = "delete"
                        }
                    )
                    "delete" -> DeleteProductForm(
                        viewModel = viewModel,
                        productId = selectedProductId,
                        onProductDeleted = { currentAction = "list" },
                        onCancel = {
                            selectedProductId = null
                            currentAction = "list"
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CreateProductForm(
    viewModel: ProductViewModel,
    onProductCreated: () -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var nombre by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var imagen by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Crear Nuevo Producto", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre del producto") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = categoria,
            onValueChange = { categoria = it },
            label = { Text("Categoría") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = imagen,
            onValueChange = { imagen = it },
            label = { Text("URL de la imagen") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = precio,
            onValueChange = { precio = it },
            label = { Text("Precio") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        OutlinedTextField(
            value = stock,
            onValueChange = { stock = it },
            label = { Text("Stock") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Row {
            Button(
                onClick = onCancel,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Cancelar")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                val precioInt = precio.toIntOrNull()
                val stockInt = stock.toIntOrNull()

                when {
                    nombre.isBlank() || categoria.isBlank() || imagen.isBlank() ||
                            descripcion.isBlank() || precio.isBlank() || stock.isBlank() -> {
                        Toast.makeText(context, "Complete todos los campos", Toast.LENGTH_SHORT).show()
                    }
                    precioInt == null -> {
                        Toast.makeText(context, "Precio debe ser numérico", Toast.LENGTH_SHORT).show()
                    }
                    stockInt == null -> {
                        Toast.makeText(context, "Stock debe ser numérico", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        scope.launch {
                            viewModel.createProduct(
                                nombre = nombre,
                                categoria = categoria,
                                imagen = imagen,
                                descripcion = descripcion,
                                precio = precioInt,
                                stock = stockInt
                            ).onSuccess {
                                Toast.makeText(context, "Producto creado exitosamente", Toast.LENGTH_SHORT).show()
                                onProductCreated()
                            }.onFailure { error ->
                                Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }) {
                Text("Crear")
            }
        }
    }
}

@Composable
private fun EditProductForm(
    viewModel: ProductViewModel,
    productId: Long?,
    onProductUpdated: () -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val productToEdit by viewModel.foundProduct.collectAsState()

    var nombre by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var imagen by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }

    LaunchedEffect(productId) {
        if (productId != null) {
            viewModel.findProductById(productId)
        } else {
            onCancel()
        }
    }

    LaunchedEffect(productToEdit) {
        productToEdit?.let {
            nombre = it.nombre
            categoria = it.categoria
            imagen = it.imagen
            descripcion = it.descripcion
            precio = it.precio.toString()
            stock = it.stock.toString()
        }
    }

    if (productToEdit == null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Cargando producto...")
        }
        return
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Editando: ${productToEdit?.nombre}", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = categoria,
            onValueChange = { categoria = it },
            label = { Text("Categoría") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = imagen,
            onValueChange = { imagen = it },
            label = { Text("URL de la imagen") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = precio,
            onValueChange = { precio = it },
            label = { Text("Precio") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        OutlinedTextField(
            value = stock,
            onValueChange = { stock = it },
            label = { Text("Stock") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Row {
            Button(
                onClick = onCancel,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Cancelar")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                val precioInt = precio.toIntOrNull()
                val stockInt = stock.toIntOrNull()

                when {
                    nombre.isBlank() || categoria.isBlank() || imagen.isBlank() ||
                            descripcion.isBlank() || precio.isBlank() || stock.isBlank() -> {
                        Toast.makeText(context, "Complete todos los campos", Toast.LENGTH_SHORT).show()
                    }
                    precioInt == null -> {
                        Toast.makeText(context, "Precio debe ser numérico", Toast.LENGTH_SHORT).show()
                    }
                    stockInt == null -> {
                        Toast.makeText(context, "Stock debe ser numérico", Toast.LENGTH_SHORT).show()
                    }
                    productId == null -> {
                        Toast.makeText(context, "Error: ID de producto no válido", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        scope.launch {
                            viewModel.updateProduct(
                                productId = productId,
                                nombre = nombre,
                                categoria = categoria,
                                imagen = imagen,
                                descripcion = descripcion,
                                precio = precioInt,
                                stock = stockInt
                            ).onSuccess {
                                Toast.makeText(context, "Producto actualizado exitosamente", Toast.LENGTH_SHORT).show()
                                onProductUpdated()
                            }.onFailure { error ->
                                Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }) {
                Text("Guardar")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ListProducts(
    viewModel: ProductViewModel,
    onEditClick: (Long) -> Unit,
    onDeleteClick: (Long) -> Unit
) {
    val products by viewModel.products.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    var productToShowOptions by remember { mutableStateOf<ProductDto?>(null) }

    val allCategories = categories

    if (productToShowOptions != null) {
        ProductOptionsDialog(
            product = productToShowOptions!!,
            onDismiss = { productToShowOptions = null },
            onEdit = {
                onEditClick(productToShowOptions!!.id)
                productToShowOptions = null
            },
            onDelete = {
                onDeleteClick(productToShowOptions!!.id)
                productToShowOptions = null
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(allCategories) { category ->
                FilterChip(
                    selected = category == selectedCategory,
                    onClick = { viewModel.selectCategory(category) },
                    label = { Text(category) }
                )
            }
        }

        if (products.isEmpty()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("No hay productos en esta categoría")
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(products) { product ->
                    ProductItem(
                        product = product,
                        onClick = { productToShowOptions = product }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductItem(product: ProductDto, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = product.imagen,
                contentDescription = "Imagen de ${product.nombre}",
                modifier = Modifier
                    .size(100.dp)
                    .padding(end = 16.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    product.nombre,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    product.categoria,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Precio: $${product.precio}", style = MaterialTheme.typography.bodyLarge)
                Text("Stock: ${product.stock}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun ProductOptionsDialog(
    product: ProductDto,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = product.nombre) },
        text = { Text("¿Qué deseas hacer con este producto?") },
        confirmButton = {
            TextButton(onClick = onEdit) {
                Text("Editar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDelete) {
                Text("Eliminar")
            }
        }
    )
}

@Composable
private fun DeleteProductForm(
    viewModel: ProductViewModel,
    productId: Long?,
    onProductDeleted: () -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var productIdString by remember { mutableStateOf(productId?.toString() ?: "") }
    var showConfirmDialog by remember { mutableStateOf(false) }

    if (showConfirmDialog && productId != null) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de que deseas eliminar este producto? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            viewModel.deleteProduct(productId).onSuccess {
                                Toast.makeText(context, "Producto eliminado exitosamente", Toast.LENGTH_SHORT).show()
                                showConfirmDialog = false
                                onProductDeleted()
                            }.onFailure { error ->
                                Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_LONG).show()
                                showConfirmDialog = false
                            }
                        }
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Borrar Producto", style = MaterialTheme.typography.headlineMedium)

        Text(
            text = "ID del producto: $productIdString",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(16.dp)
        )

        Text(
            text = "¿Está seguro de que desea eliminar este producto?",
            style = MaterialTheme.typography.bodyMedium
        )

        Row {
            Button(
                onClick = onCancel,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("Cancelar")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (productId != null) {
                        showConfirmDialog = true
                    } else {
                        Toast.makeText(context, "Error: ID de producto no válido", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Eliminar")
            }
        }
    }
}