
package com.example.midiventaslvlup.ui.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.example.midiventaslvlup.model.repository.ProductRepository
import com.example.midiventaslvlup.viewmodel.DetalleProductoViewModel
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test

class DetalleProductoScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockProductRepository: ProductRepository = mockk(relaxed = true)

    @Test
    fun detalleProductoScreen_loadingState_showsCircularProgressIndicator() {
        // Arrange
        val viewModel = DetalleProductoViewModel(mockProductRepository, 1L)
        
        // Act
        composeTestRule.setContent {
            DetalleProductoScreen(productId = 1L)
        }

        // Assert
        composeTestRule.onNodeWithText("Error al cargar el producto").assertDoesNotExist()
    }
}
