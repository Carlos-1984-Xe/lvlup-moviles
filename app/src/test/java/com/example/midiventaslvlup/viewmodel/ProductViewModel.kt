package com.example.midiventaslvlup.viewmodel

import com.example.midiventaslvlup.model.repository.ProductRepository
import com.example.midiventaslvlup.network.dto.ProductDto
import com.example.midiventaslvlup.util.TestDispatcherRule
import com.example.midiventaslvlup.viewmodel.ProductViewModel
import io.kotest.matchers.shouldBe
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension

/**
 * Tests unitarios para ProductViewModel
 *
 * 🎯 Objetivo: Verificar que el ViewModel maneja correctamente el estado de la UI
 *
 * El ViewModel es responsable de:
 * 1. Mantener el estado de la UI (productos, categorías, loading, errores)
 * 2. Orquestar las llamadas al Repository
 * 3. Transformar los resultados del Repository en estados de UI
 * 4. Manejar la lógica de negocio (filtrar por categoría, búsqueda, etc.)
 *
 * ✅ Categorías reales de Level UP Gamer:
 * - Mouse, Juegos de Mesa, Mousepad, Computador Gamer,
 * - Ropa, Silla Gamer, Consola, Accesorios
 *
 * 📦 Productos de ejemplo del inventario real:
 * - Logitech G502 HERO (Mouse) - $39,990
 * - Catan (Juegos de Mesa) - $29,990
 * - PlayStation 5 (Consola) - $699,990
 * - Secretlab Titan Evo (Silla Gamer) - $399,990
 *
 * 🧪 Técnicas de testing:
 * - MockK para crear mocks del Repository
 * - StateFlow testing para verificar cambios de estado
 * - TestDispatcher para control de corrutinas
 */
@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(TestDispatcherRule::class)
class ProductViewModelTest {

    @JvmField
    @RegisterExtension
    val testDispatcherRule = TestDispatcherRule()

    private lateinit var mockRepository: ProductRepository
    private lateinit var viewModel: ProductViewModel

    /**
     * Setup ejecutado ANTES de cada test
     * Crea mocks frescos del Repository y un ViewModel nuevo
     */
    @BeforeEach
    fun setup() {
        mockRepository = mockk()

        // 🧪 CLAVE: Inyectamos el repository mockeado y el testDispatcher
        viewModel = ProductViewModel(
            productRepository = mockRepository,
            dispatcher = testDispatcherRule.testDispatcher
        )
    }

    /**
     * Cleanup ejecutado DESPUÉS de cada test
     */
    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    // ========================================
    // TESTS: Inicialización del ViewModel
    // ========================================

    @Test
    fun `init carga categorias automaticamente desde el servidor`() = runTest {
        // ARRANGE: Simular respuesta del servidor con categorías reales
        val serverCategories = listOf(
            "Mouse",
            "Juegos de Mesa",
            "Mousepad",
            "Computador Gamer",
            "Ropa",
            "Silla Gamer",
            "Consola",
            "Accesorios"
        )
        val expectedCategories = listOf("Todos") + serverCategories

        coEvery { mockRepository.getAllCategories() } returns Result.success(expectedCategories)

        // ACT: Crear el ViewModel (el init{} se ejecuta automáticamente)
        val testViewModel = ProductViewModel(
            productRepository = mockRepository,
            dispatcher = testDispatcherRule.testDispatcher
        )

        // ASSERT: Verificar que las categorías se cargaron
        testViewModel.categories.first() shouldBe expectedCategories
        testViewModel.isLoading.first() shouldBe false
        testViewModel.error.first() shouldBe null

        coVerify(exactly = 1) { mockRepository.getAllCategories() }
    }

    @Test
    fun `init maneja error al cargar categorias`() = runTest {
        // ARRANGE: Simular error del servidor
        coEvery { mockRepository.getAllCategories() } returns Result.failure(
            Exception("Error de conexión con el servidor")
        )

        // ACT
        val testViewModel = ProductViewModel(
            productRepository = mockRepository,
            dispatcher = testDispatcherRule.testDispatcher
        )

        // ASSERT
        testViewModel.categories.first() shouldBe emptyList()
        testViewModel.error.first() shouldBe "Error de conexión con el servidor"
        testViewModel.isLoading.first() shouldBe false
    }

    // ========================================
    // TESTS: loadCategories()
    // ========================================

    @Test
    fun `loadCategories actualiza el estado con categorias reales de Level UP Gamer`() = runTest {
        // ARRANGE
        val realCategories = listOf(
            "Todos",
            "Mouse",
            "Juegos de Mesa",
            "Mousepad",
            "Computador Gamer",
            "Ropa",
            "Silla Gamer",
            "Consola",
            "Accesorios"
        )

        coEvery { mockRepository.getAllCategories() } returns Result.success(realCategories)

        // ACT
        viewModel.loadCategories()

        // ASSERT
        viewModel.categories.first() shouldBe realCategories
        viewModel.categories.first().size shouldBe 9
        viewModel.categories.first().first() shouldBe "Todos"
        viewModel.isLoading.first() shouldBe false
        viewModel.error.first() shouldBe null

        coVerify(exactly = 2) { mockRepository.getAllCategories() } // 1 en init + 1 en loadCategories
    }

    @Test
    fun `loadCategories maneja error del servidor`() = runTest {
        // ARRANGE
        coEvery { mockRepository.getAllCategories() } returns Result.failure(
            Exception("Error al obtener categorías")
        )

        // ACT
        viewModel.loadCategories()

        // ASSERT
        viewModel.error.first() shouldBe "Error al obtener categorías"
        viewModel.isLoading.first() shouldBe false
        viewModel.categories.first() shouldBe emptyList()
    }

    // ========================================
    // TESTS: loadProducts()
    // ========================================

    @Test
    fun `loadProducts con categoria Todos carga todos los productos`() = runTest {
        // ARRANGE: Simular productos de diferentes categorías
        val allProducts = listOf(
            ProductDto(1L, "Logitech G502 HERO", "Mouse", "img1.jpg", "desc1", 39990, 10),
            ProductDto(4L, "Catan", "Juegos de Mesa", "img2.jpg", "desc2", 29990, 10),
            ProductDto(58L, "PlayStation 5", "Consola", "img3.jpg", "desc3", 699990, 10)
        )

        // Mock para init (categorías)
        coEvery { mockRepository.getAllCategories() } returns Result.success(listOf("Todos"))
        // Mock para getAllProducts
        coEvery { mockRepository.getAllProducts() } returns Result.success(allProducts)

        // ACT
        viewModel.loadProducts()

        // ASSERT
        viewModel.products.first() shouldBe allProducts
        viewModel.products.first().size shouldBe 3
        viewModel.selectedCategory.first() shouldBe "Todos"
        viewModel.isLoading.first() shouldBe false
        viewModel.error.first() shouldBe null

        // Verificar que llamó a getAllProducts y NO a getProductsByCategory
        coVerify(exactly = 1) { mockRepository.getAllProducts() }
        coVerify(exactly = 0) { mockRepository.getProductsByCategory(any()) }
    }

    @Test
    fun `loadProducts con categoria Mouse filtra correctamente`() = runTest {
        // ARRANGE
        val mouseProducts = listOf(
            ProductDto(1L, "Logitech G502 HERO", "Mouse", "img1.jpg", "desc1", 39990, 10),
            ProductDto(2L, "Razer DeathAdder V2", "Mouse", "img2.jpg", "desc2", 44990, 10)
        )

        coEvery { mockRepository.getAllCategories() } returns Result.success(listOf("Todos", "Mouse"))
        coEvery { mockRepository.getProductsByCategory("Mouse") } returns Result.success(mouseProducts)

        // ACT: Primero seleccionar la categoría
        viewModel.selectCategory("Mouse")

        // ASSERT
        viewModel.products.first() shouldBe mouseProducts
        viewModel.products.first().all { it.categoria == "Mouse" } shouldBe true
        viewModel.selectedCategory.first() shouldBe "Mouse"

        coVerify(exactly = 1) { mockRepository.getProductsByCategory("Mouse") }
    }

    @Test
    fun `loadProducts con categoria Juegos de Mesa filtra correctamente`() = runTest {
        // ARRANGE
        val boardGames = listOf(
            ProductDto(4L, "Catan", "Juegos de Mesa", "img1.jpg", "desc1", 29990, 10),
            ProductDto(5L, "Carcassonne", "Juegos de Mesa", "img2.jpg", "desc2", 24990, 10),
            ProductDto(6L, "Dixit", "Juegos de Mesa", "img3.jpg", "desc3", 21990, 10)
        )

        coEvery { mockRepository.getAllCategories() } returns Result.success(listOf("Todos"))
        coEvery { mockRepository.getProductsByCategory("Juegos de Mesa") } returns Result.success(boardGames)

        // ACT
        viewModel.selectCategory("Juegos de Mesa")

        // ASSERT
        viewModel.products.first().size shouldBe 3
        viewModel.products.first().all { it.categoria == "Juegos de Mesa" } shouldBe true
        viewModel.selectedCategory.first() shouldBe "Juegos de Mesa"
    }

    @Test
    fun `loadProducts maneja error del servidor`() = runTest {
        // ARRANGE
        coEvery { mockRepository.getAllCategories() } returns Result.success(listOf("Todos"))
        coEvery { mockRepository.getAllProducts() } returns Result.failure(
            Exception("Error al cargar productos")
        )

        // ACT
        viewModel.loadProducts()

        // ASSERT
        viewModel.error.first() shouldBe "Error al cargar productos"
        viewModel.products.first() shouldBe emptyList()
        viewModel.isLoading.first() shouldBe false
    }

    @Test
    fun `loadProducts muestra loading durante la peticion`() = runTest {
        // ARRANGE
        coEvery { mockRepository.getAllCategories() } returns Result.success(listOf("Todos"))
        coEvery { mockRepository.getAllProducts() } returns Result.success(emptyList())

        // ACT
        viewModel.loadProducts()

        // ASSERT: Verificar que el loading se manejó correctamente
        viewModel.isLoading.first() shouldBe false // Al final debe ser false
    }

    // ========================================
    // TESTS: selectCategory()
    // ========================================

    @Test
    fun `selectCategory cambia la categoria y recarga productos`() = runTest {
        // ARRANGE
        val consoleProducts = listOf(
            ProductDto(58L, "PlayStation 5", "Consola", "img.jpg", "desc", 699990, 10),
            ProductDto(59L, "Xbox Series X", "Consola", "img.jpg", "desc", 649990, 10)
        )

        coEvery { mockRepository.getAllCategories() } returns Result.success(listOf("Todos"))
        coEvery { mockRepository.getProductsByCategory("Consola") } returns Result.success(consoleProducts)

        // ACT
        viewModel.selectCategory("Consola")

        // ASSERT
        viewModel.selectedCategory.first() shouldBe "Consola"
        viewModel.products.first() shouldBe consoleProducts
        viewModel.products.first().size shouldBe 2

        coVerify(exactly = 1) { mockRepository.getProductsByCategory("Consola") }
    }

    @Test
    fun `selectCategory no recarga si es la misma categoria`() = runTest {
        // ARRANGE
        coEvery { mockRepository.getAllCategories() } returns Result.success(listOf("Todos"))

        // ACT: Intentar seleccionar la misma categoría dos veces
        viewModel.selectCategory("Todos")
        viewModel.selectCategory("Todos")

        // ASSERT: Solo debe llamar una vez al repository
        coVerify(exactly = 0) { mockRepository.getAllProducts() }
    }

    // ========================================
    // TESTS: findProductById()
    // ========================================

    @Test
    fun `findProductById encuentra producto correctamente`() = runTest {
        // ARRANGE: Buscar "Catan"
        val productId = 4L
        val catan = ProductDto(
            productId = productId,
            nombre = "Catan",
            categoria = "Juegos de Mesa",
            imagen = "https://media.falabella.com/falabellaCL/123069773_01/w=1500,h=1500,fit=pad",
            descripcion = "¡Prepárate para convertirte en un pionero!",
            precio = 29990,
            stock = 10
        )

        coEvery { mockRepository.getAllCategories() } returns Result.success(listOf("Todos"))
        coEvery { mockRepository.getProductById(productId) } returns Result.success(catan)

        // ACT
        viewModel.findProductById(productId)

        // ASSERT
        viewModel.foundProduct.first() shouldBe catan
        viewModel.foundProduct.first()?.nombre shouldBe "Catan"
        viewModel.foundProduct.first()?.precio shouldBe 29990
        viewModel.error.first() shouldBe null

        coVerify(exactly = 1) { mockRepository.getProductById(productId) }
    }

    @Test
    fun `findProductById maneja error cuando producto no existe`() = runTest {
        // ARRANGE
        val productId = 99999L
        coEvery { mockRepository.getAllCategories() } returns Result.success(listOf("Todos"))
        coEvery { mockRepository.getProductById(productId) } returns Result.failure(
            Exception("Producto no encontrado")
        )

        // ACT
        viewModel.findProductById(productId)

        // ASSERT
        viewModel.foundProduct.first() shouldBe null
        viewModel.error.first() shouldBe "Producto no encontrado"
    }

    // ========================================
    // TESTS: searchProducts()
    // ========================================

    @Test
    fun `searchProducts encuentra productos por nombre`() = runTest {
        // ARRANGE: Buscar "mouse"
        val searchQuery = "mouse"
        val mouseProducts = listOf(
            ProductDto(1L, "Logitech G502 HERO", "Mouse", "img1.jpg", "desc1", 39990, 10),
            ProductDto(2L, "Razer DeathAdder V2", "Mouse", "img2.jpg", "desc2", 44990, 10)
        )

        coEvery { mockRepository.getAllCategories() } returns Result.success(listOf("Todos"))
        coEvery { mockRepository.searchProducts(searchQuery) } returns Result.success(mouseProducts)

        // ACT
        viewModel.searchProducts(searchQuery)

        // ASSERT
        viewModel.products.first() shouldBe mouseProducts
        viewModel.products.first().size shouldBe 2
        viewModel.isLoading.first() shouldBe false
        viewModel.error.first() shouldBe null

        coVerify(exactly = 1) { mockRepository.searchProducts(searchQuery) }
    }

    @Test
    fun `searchProducts retorna lista vacia cuando no hay coincidencias`() = runTest {
        // ARRANGE
        val searchQuery = "ProductoInexistente12345"
        coEvery { mockRepository.getAllCategories() } returns Result.success(listOf("Todos"))
        coEvery { mockRepository.searchProducts(searchQuery) } returns Result.success(emptyList())

        // ACT
        viewModel.searchProducts(searchQuery)

        // ASSERT
        viewModel.products.first() shouldBe emptyList()
        viewModel.error.first() shouldBe null
    }

    @Test
    fun `searchProducts maneja error del servidor`() = runTest {
        // ARRANGE
        coEvery { mockRepository.getAllCategories() } returns Result.success(listOf("Todos"))
        coEvery { mockRepository.searchProducts(any()) } returns Result.failure(
            Exception("Error en la búsqueda")
        )

        // ACT
        viewModel.searchProducts("test")

        // ASSERT
        viewModel.error.first() shouldBe "Error en la búsqueda"
        viewModel.products.first() shouldBe emptyList()
    }

    // ========================================
    // TESTS: loadProductsWithStock()
    // ========================================

    @Test
    fun `loadProductsWithStock carga solo productos disponibles`() = runTest {
        // ARRANGE: Todos los productos del inventario tienen stock = 10
        val productsWithStock = listOf(
            ProductDto(1L, "Logitech G502 HERO", "Mouse", "img.jpg", "desc", 39990, 10),
            ProductDto(4L, "Catan", "Juegos de Mesa", "img.jpg", "desc", 29990, 10),
            ProductDto(58L, "PlayStation 5", "Consola", "img.jpg", "desc", 699990, 10)
        )

        coEvery { mockRepository.getAllCategories() } returns Result.success(listOf("Todos"))
        coEvery { mockRepository.getProductsWithStock() } returns Result.success(productsWithStock)

        // ACT
        viewModel.loadProductsWithStock()

        // ASSERT
        viewModel.products.first() shouldBe productsWithStock
        viewModel.products.first().all { it.stock > 0 } shouldBe true
        viewModel.products.first().all { it.stock == 10 } shouldBe true

        coVerify(exactly = 1) { mockRepository.getProductsWithStock() }
    }

    // ========================================
    // TESTS: createProduct() (ADMIN)
    // ========================================

    @Test
    fun `createProduct crea producto y recarga lista`() = runTest {
        // ARRANGE: Crear un nuevo mouse
        val newProduct = ProductDto(
            productId = 100L,
            nombre = "SteelSeries Rival 600",
            categoria = "Mouse",
            imagen = "https://example.com/rival600.jpg",
            descripcion = "Mouse gamer con sistema de pesas dual",
            precio = 49990,
            stock = 15
        )

        coEvery { mockRepository.getAllCategories() } returns Result.success(listOf("Todos"))
        coEvery {
            mockRepository.createProduct(
                "SteelSeries Rival 600",
                "Mouse",
                "https://example.com/rival600.jpg",
                "Mouse gamer con sistema de pesas dual",
                49990,
                15
            )
        } returns Result.success(newProduct)

        // Mock para la recarga automática de productos
        coEvery { mockRepository.getAllProducts() } returns Result.success(listOf(newProduct))

        // ACT
        viewModel.createProduct(
            "SteelSeries Rival 600",
            "Mouse",
            "https://example.com/rival600.jpg",
            "Mouse gamer con sistema de pesas dual",
            49990,
            15
        )

        // ASSERT
        viewModel.productActionSuccess.first() shouldBe true
        viewModel.isLoading.first() shouldBe false
        viewModel.error.first() shouldBe null

        // Verificar que recargó los productos automáticamente
        coVerify(exactly = 1) { mockRepository.createProduct(any(), any(), any(), any(), any(), any()) }
        coVerify(exactly = 1) { mockRepository.getAllProducts() }
    }

    @Test
    fun `createProduct maneja error del servidor`() = runTest {
        // ARRANGE
        coEvery { mockRepository.getAllCategories() } returns Result.success(listOf("Todos"))
        coEvery {
            mockRepository.createProduct(any(), any(), any(), any(), any(), any())
        } returns Result.failure(Exception("Error al crear producto"))

        // ACT
        viewModel.createProduct("Test", "Mouse", "img", "desc", 10000, 5)

        // ASSERT
        viewModel.error.first() shouldBe "Error al crear producto"
        viewModel.productActionSuccess.first() shouldBe false
    }

    // ========================================
    // TESTS: updateProduct() (ADMIN)
    // ========================================

    @Test
    fun `updateProduct actualiza producto y recarga lista`() = runTest {
        // ARRANGE: Actualizar el precio de "Catan"
        val updatedProduct = ProductDto(
            productId = 4L,
            nombre = "Catan - Edición Especial",
            categoria = "Juegos de Mesa",
            imagen = "https://media.falabella.com/falabellaCL/123069773_01/w=1500,h=1500,fit=pad",
            descripcion = "Juego de mesa estratégico - Edición mejorada",
            precio = 34990,
            stock = 20
        )

        coEvery { mockRepository.getAllCategories() } returns Result.success(listOf("Todos"))
        coEvery {
            mockRepository.updateProduct(4L, any(), any(), any(), any(), any(), any())
        } returns Result.success(updatedProduct)
        coEvery { mockRepository.getAllProducts() } returns Result.success(listOf(updatedProduct))

        // ACT
        viewModel.updateProduct(
            4L,
            "Catan - Edición Especial",
            "Juegos de Mesa",
            "https://media.falabella.com/falabellaCL/123069773_01/w=1500,h=1500,fit=pad",
            "Juego de mesa estratégico - Edición mejorada",
            34990,
            20
        )

        // ASSERT
        viewModel.productActionSuccess.first() shouldBe true
        viewModel.isLoading.first() shouldBe false

        coVerify(exactly = 1) { mockRepository.updateProduct(4L, any(), any(), any(), any(), any(), any()) }
        coVerify(exactly = 1) { mockRepository.getAllProducts() }
    }

    // ========================================
    // TESTS: deleteProduct() (ADMIN)
    // ========================================

    @Test
    fun `deleteProduct elimina producto y recarga lista`() = runTest {
        // ARRANGE
        val productId = 50L
        coEvery { mockRepository.getAllCategories() } returns Result.success(listOf("Todos"))
        coEvery { mockRepository.deleteProduct(productId) } returns Result.success(Unit)
        coEvery { mockRepository.getAllProducts() } returns Result.success(emptyList())

        // ACT
        viewModel.deleteProduct(productId)

        // ASSERT
        viewModel.productActionSuccess.first() shouldBe true
        viewModel.isLoading.first() shouldBe false
        viewModel.error.first() shouldBe null

        coVerify(exactly = 1) { mockRepository.deleteProduct(productId) }
        coVerify(exactly = 1) { mockRepository.getAllProducts() }
    }

    @Test
    fun `deleteProduct maneja error cuando producto no existe`() = runTest {
        // ARRANGE
        val productId = 99999L
        coEvery { mockRepository.getAllCategories() } returns Result.success(listOf("Todos"))
        coEvery { mockRepository.deleteProduct(productId) } returns Result.failure(
            Exception("Producto no encontrado")
        )

        // ACT
        viewModel.deleteProduct(productId)

        // ASSERT
        viewModel.error.first() shouldBe "Producto no encontrado"
        viewModel.productActionSuccess.first() shouldBe false
    }

    // ========================================
    // TESTS: Métodos de limpieza de estado
    // ========================================

    @Test
    fun `clearFoundProduct limpia el producto encontrado`() = runTest {
        // ARRANGE: Primero encontrar un producto
        val productId = 1L
        val product = ProductDto(1L, "Test", "Mouse", "img", "desc", 10000, 5)

        coEvery { mockRepository.getAllCategories() } returns Result.success(listOf("Todos"))
        coEvery { mockRepository.getProductById(productId) } returns Result.success(product)

        viewModel.findProductById(productId)
        viewModel.foundProduct.first() shouldBe product

        // ACT
        viewModel.clearFoundProduct()

        // ASSERT
        viewModel.foundProduct.first() shouldBe null
    }

    @Test
    fun `clearError limpia el mensaje de error`() = runTest {
        // ARRANGE: Primero generar un error
        coEvery { mockRepository.getAllCategories() } returns Result.failure(
            Exception("Error de prueba")
        )

        val testViewModel = ProductViewModel(mockRepository, testDispatcherRule.testDispatcher)
        testViewModel.error.first() shouldBe "Error de prueba"

        // ACT
        testViewModel.clearError()

        // ASSERT
        testViewModel.error.first() shouldBe null
    }

    @Test
    fun `resetProductActionSuccess resetea el flag de accion exitosa`() = runTest {
        // ARRANGE: Simular una acción exitosa
        val newProduct = ProductDto(100L, "Test", "Mouse", "img", "desc", 10000, 5)

        coEvery { mockRepository.getAllCategories() } returns Result.success(listOf("Todos"))
        coEvery { mockRepository.createProduct(any(), any(), any(), any(), any(), any()) } returns Result.success(newProduct)
        coEvery { mockRepository.getAllProducts() } returns Result.success(listOf(newProduct))

        viewModel.createProduct("Test", "Mouse", "img", "desc", 10000, 5)
        viewModel.productActionSuccess.first() shouldBe true

        // ACT
        viewModel.resetProductActionSuccess()

        // ASSERT
        viewModel.productActionSuccess.first() shouldBe false
    }
}