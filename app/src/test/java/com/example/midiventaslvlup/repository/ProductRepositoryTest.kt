package com.example.midiventaslvlup.repository

import com.example.midiventaslvlup.model.repository.ProductRepository
import com.example.midiventaslvlup.network.ApiService
import com.example.midiventaslvlup.network.dto.ApiResponse
import com.example.midiventaslvlup.network.dto.ProductDto
import com.example.midiventaslvlup.network.dto.ProductRequest
import com.example.midiventaslvlup.util.TestDispatcherRule
import io.kotest.matchers.shouldBe
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension

/**
 * Tests unitarios para ProductRepository
 *
 * IMPORTANTE: Estos son UNIT TESTS con MOCKS
 * - NO se conectan al servidor real (http://10.0.2.2:8080/)
 * - Son rápidos, confiables y reproducibles
 * - Usan datos de ejemplo basados en el inventario real de la app
 *
 * Categorías reales de Level UP Gamer:
 * 1. Mouse
 * 2. Juegos de Mesa
 * 3. Mousepad
 * 4. Computador Gamer
 * 5. Ropa
 * 6. Silla Gamer
 * 7. Consola
 * 8. Accesorios
 *
 * Productos de ejemplo del inventario real:
 * - Logitech G502 HERO (Mouse) - $39,990
 * - Catan (Juegos de Mesa) - $29,990
 * - PlayStation 5 (Consola) - $699,990
 * - Secretlab Titan Evo (Silla Gamer) - $399,990
 *
 * Patrón AAA (Arrange-Act-Assert):
 * 1. Arrange: Configurar el escenario y mocks
 * 2. Act: Ejecutar el método a probar
 * 3. Assert: Verificar el resultado esperado
 */
@ExtendWith(TestDispatcherRule::class)
class ProductRepositoryTest {

    @JvmField
    @RegisterExtension
    val testDispatcherRule = TestDispatcherRule()

    private lateinit var mockApiService: ApiService
    private lateinit var repository: ProductRepository

    /**
     * Setup ejecutado ANTES de cada test
     * Crea mocks frescos para evitar interferencia entre tests
     */
    @BeforeEach
    fun setup() {
        mockApiService = mockk()

        // CLAVE: Inyectamos el testDispatcher al repositorio
        // Esto hace que todas las operaciones usen el dispatcher de prueba
        repository = ProductRepository(
            apiService = mockApiService,
            dispatcher = testDispatcherRule.testDispatcher
        )
    }

    /**
     * Cleanup ejecutado DESPUÉS de cada test
     * Limpia todos los mocks para liberar memoria
     */
    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    // ========================================
    // TESTS: getAllProducts()
    // ========================================

    @Test
    fun `getAllProducts retorna lista de productos cuando la API responde exitosamente`() = runTest {
        // ARRANGE: Simular productos reales del inventario de Level UP Gamer
        val mockProducts = listOf(
            ProductDto(
                id = 1L,
                nombre = "Logitech G502 HERO",
                categoria = "Mouse",
                imagen = "https://m.media-amazon.com/images/I/61mpMH5TzkL._AC_SL1500_.jpg",
                descripcion = "Mouse gamer con sensor HERO de alta precisión",
                precio = 39990,
                stock = 10
            ),
            ProductDto(
                id = 4L,
                nombre = "Catan",
                categoria = "Juegos de Mesa",
                imagen = "https://media.falabella.com/falabellaCL/123069773_01/w=1500,h=1500,fit=pad",
                descripcion = "¡Prepárate para convertirte en un pionero!",
                precio = 29990,
                stock = 10
            ),
            ProductDto(
                id = 58L,
                nombre = "PlayStation 5",
                categoria = "Consola",
                imagen = "https://media.falabella.com/falabellaCL/126614736_01/w=800,h=800,fit=pad",
                descripcion = "La consola de nueva generación de Sony",
                precio = 699990,
                stock = 10
            )
        )

        val mockResponse = ApiResponse(
            success = true,
            message = "Productos obtenidos exitosamente",
            data = mockProducts
        )

        coEvery { mockApiService.getAllProducts() } returns mockResponse

        // ACT: Ejecutar el método a probar
        val result = repository.getAllProducts()

        // ASSERT: Verificar el resultado
        result.isSuccess shouldBe true
        result.getOrNull() shouldBe mockProducts
        result.getOrNull()?.size shouldBe 3

        // Verificar que tenemos productos de diferentes categorías reales
        val categories = result.getOrNull()?.map { it.categoria }
        categories shouldBe listOf("Mouse", "Juegos de Mesa", "Consola")

        coVerify(exactly = 1) { mockApiService.getAllProducts() }
    }

    @Test
    fun `getAllProducts retorna error cuando la API responde con success false`() = runTest {
        // ARRANGE
        val mockResponse = ApiResponse<List<ProductDto>>(
            success = false,
            message = "Error del servidor al obtener productos",
            data = null
        )

        coEvery { mockApiService.getAllProducts() } returns mockResponse

        // ACT
        val result = repository.getAllProducts()

        // ASSERT
        result.isFailure shouldBe true
        result.exceptionOrNull()?.message shouldBe "Error del servidor al obtener productos"

        coVerify(exactly = 1) { mockApiService.getAllProducts() }
    }

    @Test
    fun `getAllProducts retorna error cuando hay problemas de conexion`() = runTest {
        // ARRANGE: Simular error de red (servidor caído o sin internet)
        coEvery { mockApiService.getAllProducts() } throws Exception("Error de conexión con el servidor")

        // ACT
        val result = repository.getAllProducts()

        // ASSERT
        result.isFailure shouldBe true
        result.exceptionOrNull()?.message shouldBe "Error de conexión con el servidor"

        coVerify(exactly = 1) { mockApiService.getAllProducts() }
    }

    // ========================================// TESTS: getProductById()
    // ========================================

    @Test
    fun `getProductById retorna producto cuando existe - Mouse Gamer`() = runTest {
        // ARRANGE: Simular que se busca el producto "Razer DeathAdder V2"
        val productId = 2L
        val mockProduct = ProductDto(
            id = productId,
            nombre = "Razer DeathAdder V2",
            categoria = "Mouse",
            imagen = "https://http2.mlstatic.com/D_NQ_NP_653924-MLC51868565175_102022-O.webp",
            descripcion = "Ergonomía icónica, sensor óptico Focus+ de 20,000 DPI",
            precio = 44990,
            stock = 10
        )

        val mockResponse = ApiResponse(
            success = true,
            message = "Producto encontrado",
            data = mockProduct
        )

        // CORRECCIÓN: Usar un argumento nombrado 'id' para coincidir con la interfaz ApiService.
        coEvery { mockApiService.getProductById(id = productId) } returns mockResponse

        // ACT
        val result = repository.getProductById(productId)

        // ASSERT
        result.isSuccess shouldBe true
        result.getOrNull() shouldBe mockProduct
        result.getOrNull()?.id shouldBe productId
        result.getOrNull()?.nombre shouldBe "Razer DeathAdder V2"
        result.getOrNull()?.precio shouldBe 44990

        // CORRECCIÓN: Usar también el argumento nombrado en el paso de verificación.
        coVerify(exactly = 1) { mockApiService.getProductById(id = productId) }
    }

    @Test
    fun `getProductById retorna producto cuando existe - Juego de Mesa`() = runTest {
        // ARRANGE: Simular búsqueda del juego "Catan"
        val productId = 4L
        val mockProduct = ProductDto(
            id = productId,
            nombre = "Catan",
            categoria = "Juegos de Mesa",
            imagen = "https://media.falabella.com/falabellaCL/123069773_01/w=1500,h=1500,fit=pad",
            descripcion = "¡Prepárate para convertirte en un pionero!",
            precio = 29990,
            stock = 10
        )

        val mockResponse = ApiResponse(
            success = true,
            message = "OK",
            data = mockProduct
        )

        // CORRECCIÓN: Usar un argumento nombrado 'id'.
        coEvery { mockApiService.getProductById(id = productId) } returns mockResponse

        // ACT
        val result = repository.getProductById(productId)

        // ASSERT
        result.isSuccess shouldBe true
        result.getOrNull()?.nombre shouldBe "Catan"
        result.getOrNull()?.categoria shouldBe "Juegos de Mesa"

        // CORRECCIÓN: Usar también el argumento nombrado en el paso de verificación.
        coVerify(exactly = 1) { mockApiService.getProductById(id = productId) }
    }

    @Test
    fun `getProductById retorna error cuando el producto no existe`() = runTest {
        // ARRANGE: Simular producto inexistente
        val productId = 99999L
        val mockResponse = ApiResponse<ProductDto>(
            success = false,
            message = "Producto no encontrado",
            data = null
        )

        //  CORRECCIÓN: Usar un argumento nombrado 'id'.
        coEvery { mockApiService.getProductById(id = productId) } returns mockResponse

        // ACT
        val result = repository.getProductById(productId)

        // ASSERT
        result.isFailure shouldBe true
        result.exceptionOrNull()?.message shouldBe "Producto no encontrado"

        // ✅ CORRECCIÓN: Usar también el argumento nombrado en el paso de verificación.
        coVerify(exactly = 1) { mockApiService.getProductById(id = productId) }
    }



    // ========================================
    // TESTS: getProductsByCategory()
    // ========================================

    @Test
    fun `getProductsByCategory con Todos llama a getAllProducts`() = runTest {
        // ARRANGE: Cuando la categoría es "Todos", debe retornar productos de todas las categorías
        val mockProducts = listOf(
            ProductDto(1L, "Logitech G502 HERO", "Mouse", "img1.jpg", "desc", 39990, 10),
            ProductDto(4L, "Catan", "Juegos de Mesa", "img2.jpg", "desc", 29990, 10),
            ProductDto(58L, "PlayStation 5", "Consola", "img3.jpg", "desc", 699990, 10)
        )

        val mockResponse = ApiResponse(
            success = true,
            message = "OK",
            data = mockProducts
        )

        // Debe llamar a getAllProducts, NO a getProductsByCategory
        coEvery { mockApiService.getAllProducts() } returns mockResponse

        // ACT
        val result = repository.getProductsByCategory("Todos")

        // ASSERT
        result.isSuccess shouldBe true
        result.getOrNull()?.size shouldBe 3

        // Verificar que llamó a getAllProducts y NO a getProductsByCategory
        coVerify(exactly = 1) { mockApiService.getAllProducts() }
        coVerify(exactly = 0) { mockApiService.getProductsByCategory(any()) }
    }

    @Test
    fun `getProductsByCategory con Mouse filtra correctamente`() = runTest {
        // ARRANGE: Simular respuesta con solo productos de categoría Mouse
        val categoria = "Mouse"
        val mockProducts = listOf(
            ProductDto(1L, "Logitech G502 HERO", categoria, "img1.jpg", "Mouse gamer RGB", 39990, 10),
            ProductDto(2L, "Razer DeathAdder V2", categoria, "img2.jpg", "Mouse ergonómico", 44990, 10),
            ProductDto(3L, "HyperX Pulsefire FPS Pro", categoria, "img3.jpg", "Mouse FPS", 29990, 10)
        )

        val mockResponse = ApiResponse(
            success = true,
            message = "Productos filtrados por categoría",
            data = mockProducts
        )

        coEvery { mockApiService.getProductsByCategory(categoria) } returns mockResponse

        // ACT
        val result = repository.getProductsByCategory(categoria)

        // ASSERT
        result.isSuccess shouldBe true
        result.getOrNull()?.size shouldBe 3
        result.getOrNull()?.all { it.categoria == "Mouse" } shouldBe true

        coVerify(exactly = 1) { mockApiService.getProductsByCategory(categoria) }
        coVerify(exactly = 0) { mockApiService.getAllProducts() }
    }

    @Test
    fun `getProductsByCategory con Silla Gamer filtra correctamente`() = runTest {
        // ARRANGE
        val categoria = "Silla Gamer"
        val mockProducts = listOf(
            ProductDto(43L, "Secretlab Titan Evo", categoria, "img.jpg", "Silla premium", 399990, 10),
            ProductDto(44L, "Racer Roja", categoria, "img.jpg", "Silla deportiva", 159990, 10)
        )

        val mockResponse = ApiResponse(
            success = true,
            message = "OK",
            data = mockProducts
        )

        coEvery { mockApiService.getProductsByCategory(categoria) } returns mockResponse

        // ACT
        val result = repository.getProductsByCategory(categoria)

        // ASSERT
        result.isSuccess shouldBe true
        result.getOrNull()?.size shouldBe 2
        result.getOrNull()?.all { it.categoria == "Silla Gamer" } shouldBe true
        result.getOrNull()?.first()?.nombre shouldBe "Secretlab Titan Evo"

        // VERIFICACIÓN AÑADIDA
        coVerify(exactly = 1) { mockApiService.getProductsByCategory(categoria) }
    }

    @Test
    fun `getProductsByCategory con Juegos de Mesa filtra correctamente`() = runTest {
        // ARRANGE
        val categoria = "Juegos de Mesa"
        val mockProducts = listOf(
            ProductDto(4L, "Catan", categoria, "img.jpg", "Juego estratégico", 29990, 10),
            ProductDto(5L, "Carcassonne", categoria, "img.jpg", "Construcción medieval", 24990, 10),
            ProductDto(6L, "Dixit", categoria, "img.jpg", "Juego de imaginación", 21990, 10)
        )

        val mockResponse = ApiResponse(
            success = true,
            message = "OK",
            data = mockProducts
        )

        coEvery { mockApiService.getProductsByCategory(categoria) } returns mockResponse

        // ACT
        val result = repository.getProductsByCategory(categoria)

        // ASSERT
        result.isSuccess shouldBe true
        result.getOrNull()?.size shouldBe 3
        result.getOrNull()?.all { it.categoria == "Juegos de Mesa" } shouldBe true

        // ✅ VERIFICACIÓN AÑADIDA
        coVerify(exactly = 1) { mockApiService.getProductsByCategory(categoria) }
    }

    @Test
    fun `getProductsByCategory con Consola filtra correctamente`() = runTest {
        // ARRANGE
        val categoria = "Consola"
        val mockProducts = listOf(
            ProductDto(58L, "PlayStation 5", categoria, "img.jpg", "Consola next-gen", 699990, 10),
            ProductDto(59L, "Xbox Series X", categoria, "img.jpg", "Consola Microsoft", 649990, 10),
            ProductDto(60L, "Nintendo Switch OLED", categoria, "img.jpg", "Consola híbrida", 399990, 10)
        )

        val mockResponse = ApiResponse(
            success = true,
            message = "OK",
            data = mockProducts
        )

        coEvery { mockApiService.getProductsByCategory(categoria) } returns mockResponse

        // ACT
        val result = repository.getProductsByCategory(categoria)

        // ASSERT
        result.isSuccess shouldBe true
        result.getOrNull()?.size shouldBe 3
        result.getOrNull()?.first()?.precio shouldBe 699990

        // ✅ VERIFICACIÓN AÑADIDA
        coVerify(exactly = 1) { mockApiService.getProductsByCategory(categoria) }
    }

    // ========================================
    // TESTS: getAllCategories()
    // ========================================

    @Test
    fun `getAllCategories agrega Todos al inicio y retorna categorias reales de Level UP Gamer`() = runTest {
        // ARRANGE: Categorías reales de la app Level UP Gamer
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

        val mockResponse = ApiResponse(
            success = true,
            message = "Categorías obtenidas",
            data = serverCategories
        )

        coEvery { mockApiService.getAllCategories() } returns mockResponse

        // ACT: Aquí necesitas completar la prueba.
        // val result = repository.getAllCategories()

        // ASSERT: Y luego verificar el resultado.
        // Por ejemplo:
        // result.isSuccess shouldBe true
        // val categories = result.getOrNull()
        // categories?.first() shouldBe "Todos"
        // categories?.size shouldBe serverCategories.size + 1

        // coVerify(exactly = 1) { mockApiService.getAllCategories() }
    }
}
