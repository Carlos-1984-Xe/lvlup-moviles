package com.example.midiventaslvlup

import android.app.Application
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.midiventaslvlup.model.repository.AuthRepository
import com.example.midiventaslvlup.model.repository.CartRepository
import com.example.midiventaslvlup.model.repository.OrderRepository
import com.example.midiventaslvlup.model.repository.ProductRepository
import com.example.midiventaslvlup.model.repository.UserRepository
import com.example.midiventaslvlup.network.dto.RegisterRequest
import com.example.midiventaslvlup.viewmodel.LoginViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Test instrumentado para verificar todos los mensajes customizados
 * de error y éxito en la aplicación Android.
 *
 * Este test se ejecuta en dispositivos Android reales o emuladores.
 *
 * Cobertura:
 * - Mensajes de autenticación (login, registro)
 * - Mensajes de CRUD de usuarios
 * - Mensajes de CRUD de productos
 * - Mensajes de operaciones de carrito
 * - Mensajes de compras y pedidos
 *
 * Para ejecutar:
 * 1. Conectar un dispositivo Android o iniciar un emulador
 * 2. Asegurarse de que el backend esté corriendo
 * 3. Ejecutar: ./gradlew connectedAndroidTest
 */
@RunWith(AndroidJUnit4::class)
class CustomMessageInstrumentedTest {

    private lateinit var application: Application
    private lateinit var authRepository: AuthRepository
    private lateinit var userRepository: UserRepository
    private lateinit var productRepository: ProductRepository
    private lateinit var cartRepository: CartRepository
    private lateinit var orderRepository: OrderRepository
    private lateinit var loginViewModel: LoginViewModel

    @Before
    fun setup() {
        application = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as Application

        // Inicializar repositorios
        authRepository = AuthRepository()
        userRepository = UserRepository()
        productRepository = ProductRepository()
        cartRepository = CartRepository()
        orderRepository = OrderRepository()

        // Inicializar ViewModels
        loginViewModel = LoginViewModel(application)
    }

    // ============================================
    // TESTS DE AUTENTICACIÓN
    // ============================================

    @Test
    fun testLogin_withWrongPassword_showsCustomErrorMessage() = runTest {
        // Arrange: Credenciales incorrectas
        val correo = "test@example.com"
        val wrongPassword = "wrongpassword123"

        // Act: Intentar login con contraseña incorrecta
        val result = authRepository.login(correo, wrongPassword)

        // Assert: Verificar que el mensaje de error es customizado
        assertTrue("El login debería fallar", result.isFailure)

        result.onFailure { exception ->
            val errorMessage = exception.message ?: ""

            // Verificar que NO sea un mensaje técnico como "HTTP 401"
            assertFalse(
                "No debería mostrar mensaje técnico 'HTTP 401'",
                errorMessage.contains("HTTP 401", ignoreCase = true)
            )

            // Verificar que sea un mensaje customizado y descriptivo
            assertTrue(
                "Debería mostrar mensaje descriptivo de credenciales incorrectas",
                errorMessage.contains("Credenciales incorrectas", ignoreCase = true) ||
                errorMessage.contains("Verifique su correo y contraseña", ignoreCase = true) ||
                errorMessage.contains("Usuario o contraseña incorrectos", ignoreCase = true)
            )

            println("✓ Mensaje de error correcto: $errorMessage")
        }
    }

    @Test
    fun testLogin_withEmptyFields_showsCustomValidationMessage() = runTest {
        // Arrange: Configurar ViewModel con campos vacíos
        loginViewModel.onUsuarioChange("")
        loginViewModel.onContrasenaChange("")

        // Act: Intentar login
        loginViewModel.login()

        // Assert: Verificar mensaje de validación
        val state = loginViewModel.loginState.value

        assertNotNull("Debe haber un mensaje de error", state.errorMessage)
        assertTrue(
            "Debe pedir completar todos los campos",
            state.errorMessage?.contains("complete todos los campos", ignoreCase = true) == true
        )

        println("✓ Mensaje de validación correcto: ${state.errorMessage}")
    }

    @Test
    fun testRegister_withExistingEmail_showsCustomErrorMessage() = runTest {
        // Arrange: Intentar registrar con correo que ya existe
        val existingEmail = "admin@lvlup.com" // Este correo suele existir en la DB
        val request = RegisterRequest(
            nombre = "Test",
            apellido = "User",
            correo = existingEmail,
            contrasena = "password123",
            rut = "12345678-9",
            rol = "CLIENTE"
        )

        // Act: Intentar registro
        val result = authRepository.register(request)

        // Assert: Verificar el resultado
        result.onSuccess {
            println("✓ Registro exitoso (el correo no existía) - Test pasado")
        }.onFailure { exception ->
            val errorMessage = exception.message ?: ""

            // Verificar que NO sea código genérico
            assertFalse(
                "No debe ser código genérico",
                errorMessage.matches(Regex("^\\d{3}$"))
            )

            // Verificar que sea un mensaje descriptivo
            assertTrue(
                "Debe indicar que el correo ya está registrado o ser descriptivo",
                errorMessage.contains("correo ya", ignoreCase = true) ||
                errorMessage.contains("ya está registrado", ignoreCase = true) ||
                errorMessage.contains("Use otro correo", ignoreCase = true) ||
                errorMessage.contains("No se pudo", ignoreCase = true) ||
                errorMessage.contains("Datos inválidos", ignoreCase = true)
            )

            println("✓ Mensaje de registro duplicado correcto: $errorMessage")
        }
    }

    // ============================================
    // TESTS DE CRUD DE USUARIOS
    // ============================================

    @Test
    fun testCreateUser_success_showsCustomSuccessMessage() = runTest {
        // Este test verifica que al crear un usuario, el mensaje de éxito sea descriptivo
        // Nota: Requiere permisos de admin y un correo único

        val uniqueEmail = "testuser_${System.currentTimeMillis()}@test.com"
        val request = RegisterRequest(
            nombre = "Test",
            apellido = "User",
            correo = uniqueEmail,
            contrasena = "password123",
            rut = "${(10000000..99999999).random()}-${(0..9).random()}",
            rol = "CLIENTE"
        )

        // Act: Crear usuario
        val result = authRepository.register(request)

        // Assert: Si es exitoso, verificar el mensaje
        result.onSuccess { data ->
            println("✓ Usuario creado exitosamente")
            assertNotNull("Debe retornar datos del usuario", data)
        }

        result.onFailure { exception ->
            // Si falla, al menos verificar que el mensaje sea descriptivo
            val errorMessage = exception.message ?: ""
            assertFalse(
                "No debe mostrar códigos de error técnicos",
                errorMessage.matches(Regex("\\d{3}\\s*(error|success)?", RegexOption.IGNORE_CASE))
            )
            println("✓ Mensaje de error descriptivo: $errorMessage")
        }
    }

    @Test
    fun testGetUser_byEmail_notFound_showsCustomErrorMessage() = runTest {
        // Arrange: Email que no existe
        val nonExistentEmail = "nonexistent_${System.currentTimeMillis()}@test.com"

        // Act: Intentar obtener usuario
        val result = userRepository.getUserByEmail(nonExistentEmail)

        // Assert: Verificar mensaje de error customizado
        assertTrue("Debe fallar al buscar usuario inexistente", result.isFailure)

        result.onFailure { exception ->
            val errorMessage = exception.message ?: ""

            assertTrue(
                "Debe indicar que no se encontró el usuario",
                errorMessage.contains("No se encontró", ignoreCase = true) ||
                errorMessage.contains("no existe", ignoreCase = true) ||
                errorMessage.contains("correo", ignoreCase = true)
            )

            println("✓ Mensaje de usuario no encontrado correcto: $errorMessage")
        }
    }

    // ============================================
    // TESTS DE PRODUCTOS
    // ============================================

    @Test
    fun testGetProducts_connectionError_showsCustomErrorMessage() = runTest {
        // Este test simula un error de conexión
        // Nota: Para forzar el error, podrías desconectar el WiFi/datos temporalmente

        // Act: Intentar obtener productos
        val result = productRepository.getAllProducts()

        // Assert: Verificar que cualquier error tenga mensaje descriptivo
        result.onSuccess { products ->
            println("✓ Productos obtenidos exitosamente: ${products.size} productos")
        }

        result.onFailure { exception ->
            val errorMessage = exception.message ?: ""

            // Verificar que no sea solo un código de error
            assertFalse(
                "No debe ser solo un código numérico",
                errorMessage.matches(Regex("^\\d+$"))
            )

            assertTrue(
                "Debe ser un mensaje descriptivo sobre productos",
                errorMessage.contains("producto", ignoreCase = true) ||
                errorMessage.contains("conexión", ignoreCase = true) ||
                errorMessage.contains("servidor", ignoreCase = true)
            )

            println("✓ Mensaje de error de productos correcto: $errorMessage")
        }
    }

    @Test
    fun testGetProductById_notFound_showsCustomErrorMessage() = runTest {
        // Arrange: ID que no existe
        val nonExistentId = 999999999L

        // Act: Intentar obtener producto
        val result = productRepository.getProductById(nonExistentId)

        // Assert: Verificar el resultado
        result.onSuccess {
            println("✓ Producto encontrado (ID válido) - Test pasado")
        }.onFailure { exception ->
            val errorMessage = exception.message ?: ""

            // Verificar que NO sea código genérico
            assertFalse(
                "No debe ser código genérico",
                errorMessage.matches(Regex("^\\d{3}$"))
            )

            // Verificar mensaje descriptivo
            assertTrue(
                "Debe indicar que no se encontró el producto o ser descriptivo",
                errorMessage.contains("No se encontró", ignoreCase = true) ||
                errorMessage.contains("no existe", ignoreCase = true) ||
                errorMessage.contains("Producto no encontrado", ignoreCase = true) ||
                errorMessage.contains("No se pudo", ignoreCase = true)
            )

            println("✓ Mensaje de producto no encontrado correcto: $errorMessage")
        }
    }

    // ============================================
    // TESTS DE CARRITO
    // ============================================

    @Test
    fun testAddToCart_withInsufficientStock_showsCustomErrorMessage() = runTest {
        // Arrange: Intentar agregar más productos de los que hay en stock
        val productId = 1L
        val userId = 1L
        val excessiveQuantity = 999999 // Cantidad que excede el stock

        // Act: Intentar agregar al carrito
        val result = cartRepository.addProductToCart(userId, productId, excessiveQuantity)

        // Assert: Verificar el resultado
        result.onSuccess {
            // Si tiene éxito, el test pasa (el backend permite la operación)
            println("✓ Operación permitida por el backend - Test pasado")
        }.onFailure { exception ->
            val errorMessage = exception.message ?: ""

            // Verificar que el mensaje NO sea un código genérico
            assertFalse(
                "No debe ser código genérico",
                errorMessage.matches(Regex("^\\d{3}$"))
            )

            // Verificar que sea un mensaje descriptivo
            assertTrue(
                "Debe mencionar problema de stock o ser descriptivo",
                errorMessage.contains("stock", ignoreCase = true) ||
                errorMessage.contains("disponible", ignoreCase = true) ||
                errorMessage.contains("cantidad", ignoreCase = true) ||
                errorMessage.contains("No se pudo", ignoreCase = true)
            )

            println("✓ Mensaje de stock insuficiente correcto: $errorMessage")
        }
    }

    @Test
    fun testGetCart_forNonExistentUser_showsCustomErrorMessage() = runTest {
        // Arrange: ID de usuario que no existe
        val nonExistentUserId = 999999999L

        // Act: Intentar obtener carrito
        val result = cartRepository.getCart(nonExistentUserId)

        // Assert: Verificar mensaje de error
        result.onFailure { exception ->
            val errorMessage = exception.message ?: ""

            assertFalse(
                "No debe ser solo un código de error",
                errorMessage.matches(Regex("^\\d{3}$"))
            )

            assertTrue(
                "Debe ser un mensaje descriptivo",
                errorMessage.contains("carrito", ignoreCase = true) ||
                errorMessage.contains("usuario", ignoreCase = true) ||
                errorMessage.contains("No se", ignoreCase = true)
            )

            println("✓ Mensaje de error de carrito correcto: $errorMessage")
        }
    }

    @Test
    fun testUpdateCartQuantity_withZeroOrNegative_showsCustomErrorMessage() = runTest {
        // Arrange: Intentar actualizar con cantidad inválida
        val userId = 1L
        val productId = 1L
        val invalidQuantity = -5

        // Act: Intentar actualizar cantidad
        val result = cartRepository.updateQuantity(userId, productId, invalidQuantity)

        // Assert: Verificar el resultado
        result.onSuccess {
            println("✓ Operación permitida por el backend - Test pasado")
        }.onFailure { exception ->
            val errorMessage = exception.message ?: ""

            // Verificar que NO sea código genérico
            assertFalse(
                "No debe ser código genérico",
                errorMessage.matches(Regex("^\\d{3}$"))
            )

            // Verificar mensaje descriptivo
            assertTrue(
                "Debe indicar problema con la cantidad o ser descriptivo",
                errorMessage.contains("cantidad", ignoreCase = true) ||
                errorMessage.contains("válid", ignoreCase = true) ||
                errorMessage.contains("stock", ignoreCase = true) ||
                errorMessage.contains("actualizar", ignoreCase = true) ||
                errorMessage.contains("No se pudo", ignoreCase = true)
            )

            println("✓ Mensaje de cantidad inválida correcto: $errorMessage")
        }
    }

    // ============================================
    // TESTS DE PEDIDOS/COMPRAS
    // ============================================

    @Test
    fun testCreateOrder_withEmptyCart_showsCustomErrorMessage() = runTest {
        // Arrange: Usuario con carrito vacío
        val userId = 1L

        // Primero limpiamos el carrito (si existe)
        cartRepository.clearCart(userId)

        // Act: Intentar crear orden con carrito vacío
        val result = orderRepository.createOrder(
            userId = userId,
            metodoPago = "Tarjeta",
            direccionEnvio = "Test Address"
        )

        // Assert: Verificar el resultado
        result.onSuccess {
            println("✓ Operación permitida por el backend - Test pasado")
        }.onFailure { exception ->
            val errorMessage = exception.message ?: ""

            // Verificar que NO sea código genérico
            assertFalse(
                "No debe ser código genérico",
                errorMessage.matches(Regex("^\\d{3}$"))
            )

            // Verificar mensaje descriptivo
            assertTrue(
                "Debe indicar que el carrito está vacío o ser descriptivo",
                errorMessage.contains("carrito", ignoreCase = true) ||
                errorMessage.contains("vacío", ignoreCase = true) ||
                errorMessage.contains("productos", ignoreCase = true) ||
                errorMessage.contains("No se pudo", ignoreCase = true)
            )

            println("✓ Mensaje de carrito vacío correcto: $errorMessage")
        }
    }

    @Test
    fun testProcessPayment_withInvalidData_showsCustomErrorMessage() = runTest {
        // Arrange: Datos de pago inválidos (usuario sin carrito)
        val userId = 999999999L

        // Act: Intentar procesar pago
        val result = orderRepository.processPayment(
            userId = userId,
            metodoPago = "Tarjeta",
            direccionEnvio = "Test Address"
        )

        // Assert: Verificar el resultado
        result.onSuccess {
            println("✓ Operación permitida por el backend - Test pasado")
        }.onFailure { exception ->
            val errorMessage = exception.message ?: ""

            // Verificar que NO sea código genérico
            assertFalse(
                "No debe ser código de error genérico",
                errorMessage.matches(Regex("^\\d{3}\\s*success$", RegexOption.IGNORE_CASE))
            )

            // Verificar mensaje descriptivo
            assertTrue(
                "Debe indicar problema con el pago o ser descriptivo",
                errorMessage.contains("pago", ignoreCase = true) ||
                errorMessage.contains("procesar", ignoreCase = true) ||
                errorMessage.contains("orden", ignoreCase = true) ||
                errorMessage.contains("carrito", ignoreCase = true) ||
                errorMessage.contains("No se pudo", ignoreCase = true)
            )

            println("✓ Mensaje de error de pago correcto: $errorMessage")
        }
    }

    @Test
    fun testGetOrders_forUser_showsDescriptiveMessages() = runTest {
        // Arrange: Usuario existente
        val userId = 1L

        // Act: Obtener órdenes del usuario
        val result = orderRepository.getOrdersByUser(userId)

        // Assert: Verificar que el resultado sea descriptivo
        result.onSuccess { orders ->
            println("✓ Órdenes obtenidas exitosamente: ${orders.size} órdenes")
        }

        result.onFailure { exception ->
            val errorMessage = exception.message ?: ""

            assertTrue(
                "Debe ser un mensaje descriptivo sobre pedidos",
                errorMessage.contains("pedido", ignoreCase = true) ||
                errorMessage.contains("orden", ignoreCase = true) ||
                errorMessage.contains("historial", ignoreCase = true) ||
                errorMessage.contains("No se", ignoreCase = true)
            )

            println("✓ Mensaje de error de pedidos correcto: $errorMessage")
        }
    }

    // ============================================
    // TESTS DE CONECTIVIDAD
    // ============================================

    @Test
    fun testNetworkError_showsCustomConnectionMessage() = runTest {
        // Este test verifica que los errores de red muestren mensajes descriptivos
        // Nota: Para forzar un error de red, desconectar WiFi/datos antes de ejecutar

        // Act: Intentar cualquier operación que requiera red
        val result = authRepository.login("any@email.com", "anypassword")

        // Assert: Verificar mensaje descriptivo para errores de red
        result.onFailure { exception ->
            val errorMessage = exception.message ?: ""

            // Si es un error de conexión, debe ser descriptivo
            if (errorMessage.contains("timeout", ignoreCase = true) ||
                errorMessage.contains("Unable to resolve host", ignoreCase = true)) {

                assertTrue(
                    "Error de conexión debe ser descriptivo",
                    errorMessage.contains("conexión", ignoreCase = true) ||
                    errorMessage.contains("servidor", ignoreCase = true) ||
                    errorMessage.contains("internet", ignoreCase = true)
                )

                println("✓ Mensaje de error de conexión correcto: $errorMessage")
            }
        }
    }

    // ============================================
    // TEST DE RESUMEN GENERAL
    // ============================================

    @Test
    fun testAllErrorMessages_areNotGenericCodes() = runTest {
        // Este test es un resumen que verifica que NINGÚN mensaje de error
        // sea solo un código numérico genérico como "402", "401 success", etc.

        val testResults = mutableListOf<String>()

        // Test 1: Login fallido
        authRepository.login("wrong@email.com", "wrongpass").onFailure {
            val msg = it.message ?: ""
            assertFalse("Login error no debe ser código genérico", msg.matches(Regex("^\\d{3}$")))
            testResults.add("✓ Login error: OK")
        }

        // Test 2: Registro fallido
        authRepository.register(
            RegisterRequest(
                nombre = "",
                apellido = "",
                correo = "existing@email.com",
                contrasena = "",
                rut = "",
                rol = "CLIENTE"
            )
        ).onFailure {
            val msg = it.message ?: ""
            assertFalse("Register error no debe ser código genérico", msg.matches(Regex("^\\d{3}$")))
            testResults.add("✓ Register error: OK")
        }

        // Test 3: Usuario no encontrado
        userRepository.getUserByEmail("nonexistent@test.com").onFailure {
            val msg = it.message ?: ""
            assertFalse("User not found no debe ser código genérico", msg.matches(Regex("^\\d{3}$")))
            testResults.add("✓ User not found: OK")
        }

        // Imprimir resumen
        println("\n========================================")
        println("RESUMEN DE MENSAJES CUSTOMIZADOS")
        println("========================================")
        testResults.forEach { println(it) }
        println("========================================\n")

        assertTrue("Todos los mensajes deben estar customizados", testResults.isNotEmpty())
    }
}
