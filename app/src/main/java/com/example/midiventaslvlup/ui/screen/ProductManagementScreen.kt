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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductManagementScreen(action: String, onNavigateBack: () -> Unit) {

    var currentAction by remember { mutableStateOf(action) }
    var selectedProductId by remember { mutableStateOf<Long?>(null) }

    val viewModel: ProductViewModel = viewModel(
        factory = ProductViewModelFactory(ProductRepository())
    )

    // Ya no necesitamos un isLoading global aquí, cada sub-pantalla lo manejará
    // val isLoading by viewModel.isLoading.collectAsState()

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateProductForm(
    viewModel: ProductViewModel,
    onProductCreated: () -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val productActionSuccess by viewModel.productActionSuccess.collectAsState()
    val error by viewModel.error.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var nombre by remember { mutableStateOf("") }
    var imagen by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }

    val categories by viewModel.categories.collectAsState()
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("") }
    var isCreatingNewCategory by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }

    val createNewCategoryOption = "Crear nueva categoría..."

    // ✅ AGREGAR SOLO ESTO
    LaunchedEffect(Unit) {
        viewModel.loadCategories()
    }

    LaunchedEffect(productActionSuccess) {
        if (productActionSuccess) {
            Toast.makeText(context, "Producto creado exitosamente", Toast.LENGTH_SHORT).show()
            viewModel.resetProductActionSuccess()
            onProductCreated()
        }
    }
    
    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

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
            onValueChange = { nombre = it },  // ✅ Corregir aquí: onChange con C mayúscula
            label = { Text("Nombre del producto") },
            modifier = Modifier.fillMaxWidth()
        )
        //  Mostrar loader si las categorías aún no cargan
        if (categories.isEmpty() && isLoading) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                Text("Cargando categorías...")
            }
        } else {
            ExposedDropdownMenuBox(
                expanded = isDropdownExpanded,
                onExpandedChange = { isDropdownExpanded = !isDropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = if (isCreatingNewCategory) "Creando nueva..."
                           else selectedCategory.ifEmpty { "Selecciona una categoría" },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false }
                ) {
                    categories.filter { it != "Todos" }.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category) },
                            onClick = {
                                selectedCategory = category
                                isCreatingNewCategory = false
                                isDropdownExpanded = false
                            }
                        )
                    }
                    DropdownMenuItem(
                        text = { Text(createNewCategoryOption) },
                        onClick = {
                            isCreatingNewCategory = true
                            selectedCategory = ""
                            isDropdownExpanded = false
                        }
                    )
                }
            }
        }

        if (isCreatingNewCategory) {
            OutlinedTextField(
                value = newCategoryName,
                onValueChange = { newCategoryName = it },
                label = { Text("Nombre de la nueva categoría") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        OutlinedTextField(value = imagen, onValueChange = { imagen = it }, label = { Text("URL de la imagen") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = precio, onValueChange = { precio = it }, label = { Text("Precio") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Stock") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))

        Spacer(modifier = Modifier.height(8.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Row {
                Button(onClick = onCancel, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) { Text("Cancelar") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    val precioInt = precio.toIntOrNull()
                    val stockInt = stock.toIntOrNull()
                    val finalCategory = if (isCreatingNewCategory) newCategoryName else selectedCategory

                    when {
                        nombre.isBlank() || finalCategory.isBlank() || imagen.isBlank() || descripcion.isBlank() || precio.isBlank() || stock.isBlank() -> {
                            Toast.makeText(context, "Complete todos los campos", Toast.LENGTH_SHORT).show()
                        }
                        precioInt == null -> Toast.makeText(context, "Precio debe ser numérico", Toast.LENGTH_SHORT).show()
                        stockInt == null -> Toast.makeText(context, "Stock debe ser numérico", Toast.LENGTH_SHORT).show()
                        else -> {
                            viewModel.createProduct(
                                nombre = nombre,
                                categoria = finalCategory,
                                imagen = imagen,
                                descripcion = descripcion,
                                precio = precioInt,
                                stock = stockInt
                            )
                        }
                    }
                }) {
                    Text("Crear")
                }
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
    val productActionSuccess by viewModel.productActionSuccess.collectAsState()
    val error by viewModel.error.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val productToEdit by viewModel.foundProduct.collectAsState()

    var nombre by remember(productToEdit) { mutableStateOf(productToEdit?.nombre ?: "") }
    var categoria by remember(productToEdit) { mutableStateOf(productToEdit?.categoria ?: "") }
    var imagen by remember(productToEdit) { mutableStateOf(productToEdit?.imagen ?: "") }
    var descripcion by remember(productToEdit) { mutableStateOf(productToEdit?.descripcion ?: "") }
    var precio by remember(productToEdit) { mutableStateOf(productToEdit?.precio?.toString() ?: "") }
    var stock by remember(productToEdit) { mutableStateOf(productToEdit?.stock?.toString() ?: "") }

    LaunchedEffect(productActionSuccess) {
        if (productActionSuccess) {
            Toast.makeText(context, "Producto actualizado exitosamente", Toast.LENGTH_SHORT).show()
            viewModel.resetProductActionSuccess()
            onProductUpdated()
        }
    }
    
    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }
    
    LaunchedEffect(productId) {
        if (productId != null) {
            viewModel.findProductById(productId)
        } else {
            onCancel()
        }
    }

    if (productToEdit == null) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            CircularProgressIndicator()
            Text("Cargando producto...")
        }
    } else {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Editando: ${productToEdit?.nombre ?: "..."}", style = MaterialTheme.typography.headlineMedium)

            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = categoria, onValueChange = { categoria = it }, label = { Text("Categoría") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = imagen, onValueChange = { imagen = it }, label = { Text("URL de la imagen") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = precio, onValueChange = { precio = it }, label = { Text("Precio") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Stock") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            
            Spacer(modifier = Modifier.height(8.dp))

            if(isLoading) {
                CircularProgressIndicator()
            } else {
                 Row {
                    Button(onClick = onCancel, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) { Text("Cancelar") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        val precioInt = precio.toIntOrNull()
                        val stockInt = stock.toIntOrNull()

                        when {
                            nombre.isBlank() || categoria.isBlank() || imagen.isBlank() || descripcion.isBlank() || precio.isBlank() || stock.isBlank() -> {
                                Toast.makeText(context, "Complete todos los campos", Toast.LENGTH_SHORT).show()
                            }
                            precioInt == null -> Toast.makeText(context, "Precio debe ser numérico", Toast.LENGTH_SHORT).show()
                            stockInt == null -> Toast.makeText(context, "Stock debe ser numérico", Toast.LENGTH_SHORT).show()
                            productId == null -> Toast.makeText(context, "Error: ID de producto no válido", Toast.LENGTH_SHORT).show()
                            else -> {
                                viewModel.updateProduct(
                                    productId = productId,
                                    nombre = nombre,
                                    categoria = categoria,
                                    imagen = imagen,
                                    descripcion = descripcion,
                                    precio = precioInt,
                                    stock = stockInt
                                )
                            }
                        }
                    }) { Text("Guardar") }
                }
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
    val isLoading by viewModel.isLoading.collectAsState()

    var productToShowOptions by remember { mutableStateOf<ProductDto?>(null) }
    
    LaunchedEffect(Unit) {
        viewModel.loadProducts()
    }

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
            items(categories) { category ->
                FilterChip(
                    selected = category == selectedCategory,
                    onClick = { viewModel.selectCategory(category) },
                    label = { Text(category) }
                )
            }
        }

        if (isLoading) {
            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                CircularProgressIndicator()
            }
        } else if (products.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
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
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = product.imagen,
                contentDescription = "Imagen de ${product.nombre}",
                modifier = Modifier.size(100.dp).padding(end = 16.dp),
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
    product: ProductDto,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = product.nombre) },
        text = { Text("¿Qué deseas hacer con este producto?") },
        confirmButton = { TextButton(onClick = onEdit) { Text("Editar") } },
        dismissButton = { TextButton(onClick = onDelete) { Text("Eliminar") } }
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
    val productActionSuccess by viewModel.productActionSuccess.collectAsState()
    val error by viewModel.error.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showConfirmDialog by remember { mutableStateOf(false) }

    LaunchedEffect(productActionSuccess) {
        if (productActionSuccess) {
            Toast.makeText(context, "Producto eliminado exitosamente", Toast.LENGTH_SHORT).show()
            viewModel.resetProductActionSuccess()
            onProductDeleted()
        }
    }

    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    if (showConfirmDialog && productId != null) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de que deseas eliminar este producto?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteProduct(productId)
                    showConfirmDialog = false
                }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { showConfirmDialog = false }) { Text("Cancelar") } }
        )
    }

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Borrar Producto", style = MaterialTheme.typography.headlineMedium)
        Text(text = "ID del producto: ${productId ?: "N/A"}", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(16.dp))
        Text(text = "¿Está seguro de que desea eliminar este producto?")
        
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Row {
                Button(onClick = onCancel, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)) { Text("Cancelar") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { if (productId != null) showConfirmDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Eliminar") }
            }
        }
    }
}
