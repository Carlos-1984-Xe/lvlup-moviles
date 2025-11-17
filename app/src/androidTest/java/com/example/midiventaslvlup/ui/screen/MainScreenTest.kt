
package com.example.midiventaslvlup.ui.screen

import android.app.Application
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.platform.app.InstrumentationRegistry
import com.example.midiventaslvlup.model.enums.UserRole
import com.example.midiventaslvlup.model.repository.AuthRepository
import com.example.midiventaslvlup.network.dto.LoginResponse
import com.example.midiventaslvlup.viewmodel.LoginViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class MainScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockAuthRepository: AuthRepository = mockk(relaxed = true)
    private val application = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as Application

    /**
     * Test 1 (Renderizado de Estado Inicial)
     */
    @Test
    fun loginScreen_initialState_isRenderedCorrectly() {
        val loginViewModel = LoginViewModel(application, mockAuthRepository)

        composeTestRule.setContent {
            LoginScreen(
                loginViewModel = loginViewModel,
                onNavigateToDetails = {},
                onNavigateToAdmin = {},
                onGoToRegister = {}
            )
        }

        composeTestRule.onNodeWithText("Correo electrónico").assertIsDisplayed()
        composeTestRule.onNodeWithText("Contraseña").assertIsDisplayed()
        composeTestRule.onNodeWithText("Iniciar Sesión").assertIsDisplayed()
        composeTestRule.onNodeWithText("¿No tienes cuenta? Créala aquí").assertIsDisplayed()
    }

    /**
     * Test 2 (Interacción del Usuario):
     */
    @Test
    fun loginScreen_clickLoginButton_callsViewModel() = runTest {
        val loginViewModel = LoginViewModel(application, mockAuthRepository)

        composeTestRule.setContent {
            LoginScreen(
                loginViewModel = loginViewModel,
                onNavigateToDetails = {},
                onNavigateToAdmin = {},
                onGoToRegister = {}
            )
        }

        composeTestRule.onNodeWithText("Correo electrónico").performTextInput("test@test.com")
        composeTestRule.onNodeWithText("Contraseña").performTextInput("123456")
        composeTestRule.onNodeWithText("Iniciar Sesión").performClick()

        composeTestRule.waitForIdle()

        coVerify(timeout = 3000, atLeast = 1) {
            mockAuthRepository.login(any(), any())
        }
    }

    /**
     * Test 3 (Login Exitoso)
     */
    @Test
    fun loginScreen_successfulLogin_navigatesToDetails() = runTest {
        val loginViewModel = LoginViewModel(application, mockAuthRepository)
        val mockOnNavigateToDetails: () -> Unit = mockk(relaxed = true)
        val fakeLoginResponse = LoginResponse(
            token = "fake-token",
            userId = 1L,
            correo = "test@test.com",
            nombre = "Test User",
            rol = UserRole.CLIENTE
        )

        coEvery { mockAuthRepository.login(any(), any()) } returns Result.success(fakeLoginResponse)

        composeTestRule.setContent {
            LoginScreen(
                loginViewModel = loginViewModel,
                onNavigateToDetails = mockOnNavigateToDetails,
                onNavigateToAdmin = {},
                onGoToRegister = {}
            )
        }

        composeTestRule.onNodeWithText("Correo electrónico").performTextInput("test@test.com")
        composeTestRule.onNodeWithText("Contraseña").performTextInput("123456")
        composeTestRule.onNodeWithText("Iniciar Sesión").performClick()

        composeTestRule.waitForIdle()

        verify(timeout = 3000, atLeast = 1) {
            mockOnNavigateToDetails.invoke()
        }
    }

    /**
     * Test 4 (Login Fallido)
     */
    @Test
    fun loginScreen_failedLogin_showsErrorMessage() = runTest {
        val loginViewModel = LoginViewModel(application, mockAuthRepository)

        coEvery { mockAuthRepository.login(any(), any()) } returns Result.failure(Exception("Credenciales inválidas"))

        composeTestRule.setContent {
            LoginScreen(
                loginViewModel = loginViewModel,
                onNavigateToDetails = {},
                onNavigateToAdmin = {},
                onGoToRegister = {}
            )
        }

        composeTestRule.onNodeWithText("Correo electrónico").performTextInput("wrong@test.com")
        composeTestRule.onNodeWithText("Contraseña").performTextInput("wrongpassword")
        composeTestRule.onNodeWithText("Iniciar Sesión").performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Credenciales inválidas").assertIsDisplayed()
    }

    /**
     * Test 5 (Navegación a Registro)
     */
    @Test
    fun loginScreen_clickRegister_navigatesToRegister() {
        val loginViewModel = LoginViewModel(application, mockAuthRepository)
        val mockOnGoToRegister: () -> Unit = mockk(relaxed = true)

        composeTestRule.setContent {
            LoginScreen(
                loginViewModel = loginViewModel,
                onNavigateToDetails = {},
                onNavigateToAdmin = {},
                onGoToRegister = mockOnGoToRegister
            )
        }

        composeTestRule.onNodeWithText("¿No tienes cuenta? Créala aquí").performClick()

        verify { mockOnGoToRegister.invoke() }
    }
}
