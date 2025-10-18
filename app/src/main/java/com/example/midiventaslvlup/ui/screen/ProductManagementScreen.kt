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
import androidx.compose.runtime.DisposableEffect
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
import com.example.midiventaslvlup.model.local.AppDatabase
import com.example.midiventaslvlup.model.local.ExpenseEntity
import com.example.midiventaslvlup.model.repository.ProductRepository
import com.example.midiventaslvlup.viewmodel.ProductViewModel
import com.example.midiventaslvlup.viewmodel.ProductViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductManagementScreen(action: String, onNavigateBack: () -> Unit) {

    var currentAction by remember { mutableStateOf(action) }
    var selectedProductId by remember { mutableStateOf<Int?>(null) }

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            when (currentAction) {
                "create" -> CreateProductForm(onProductCreated = onNavigateBack, onCancel = onNavigateBack)
                "edit" -> EditProductForm(
                    productId = selectedProductId,
                    onProductUpdated = onNavigateBack, 
                    onCancel = { 
                        selectedProductId = null
                        currentAction = "list"
                    }
                )
                "list" -> ListProducts(
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
                    productId = selectedProductId,
                    onProductDeleted = onNavigateBack, 
                    onCancel = {
                        selectedProductId = null
                        currentAction = "list"
                    }
                )
            }
        }
    }
}

@Composable
private fun CreateProductForm(onProductCreated: () -> Unit, onCancel: () -> Unit) {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val productRepository = ProductRepository(db.expenseDao())
    val viewModel: ProductViewModel = viewModel(factory = ProductViewModelFactory(productRepository))
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
            Button(onClick = onCancel,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                Text("Cancelar")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { 
                val precioInt = precio.toIntOrNull()
                val stockInt = stock.toIntOrNull()

                if (nombre.isBlank() || categoria.isBlank() || imagen.isBlank() || descripcion.isBlank() || precioInt == null || stockInt == null) {
                    Toast.makeText(context, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                } else {
                    scope.launch {
                        val product = ExpenseEntity(
                            nombre = nombre,
                            categoria = categoria,
                            imagen = imagen,
                            descripcion = descripcion,
                            precio = precioInt,
                            stock = stockInt
                        )
                        viewModel.createProduct(product)
                        Toast.makeText(context, "Producto creado exitosamente", Toast.LENGTH_SHORT).show()
                        onProductCreated()
                    }
                }
            }) {
                Text("Crear")
            }
        }
    }
}

@Composable
private fun EditProductForm(productId: Int?, onProductUpdated: () -> Unit, onCancel: () -> Unit) {
    Text("Not implemented yet. Product ID: $productId")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ListProducts(onEditClick: (Int) -> Unit, onDeleteClick: (Int) -> Unit) {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val productRepository = ProductRepository(db.expenseDao())
    val viewModel: ProductViewModel = viewModel(factory = ProductViewModelFactory(productRepository))

    val products by viewModel.products.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    var productToShowOptions by remember { mutableStateOf<ExpenseEntity?>(null) }

    val allCategories = listOf("Todos") + categories

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

@Composable
private fun ProductItem(product: ExpenseEntity, onClick: () -> Unit) {
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
                Text(product.nombre, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(product.categoria, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Precio: $${product.precio}", style = MaterialTheme.typography.bodyLarge)
                Text("Stock: ${product.stock}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun ProductOptionsDialog(
    product: ExpenseEntity,
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
private fun DeleteProductForm(productId: Int?, onProductDeleted: () -> Unit, onCancel: () -> Unit) {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val productRepository = ProductRepository(db.expenseDao())
    val viewModel: ProductViewModel = viewModel(factory = ProductViewModelFactory(productRepository))
    val scope = rememberCoroutineScope()
    var productIdString by remember { mutableStateOf(productId?.toString() ?: "") }

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Borrar Producto", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(
            value = productIdString,
            onValueChange = { productIdString = it },
            label = { Text("ID del producto a borrar") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Row {
             Button(onClick = onCancel,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                Text("Cancelar")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { 
                val id = productIdString.toIntOrNull()
                if (id == null) {
                    Toast.makeText(context, "Por favor ingrese un ID válido", Toast.LENGTH_SHORT).show()
                } else {
                    scope.launch {
                        val productToDelete = productRepository.findProductById(id)
                        if (productToDelete != null) {
                            viewModel.deleteProduct(productToDelete)
                            Toast.makeText(context, "Producto borrado exitosamente", Toast.LENGTH_SHORT).show()
                            onProductDeleted()
                        } else {
                            Toast.makeText(context, "Producto no encontrado", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }) {
                Text("Borrar")
            }
        }
    }
}
