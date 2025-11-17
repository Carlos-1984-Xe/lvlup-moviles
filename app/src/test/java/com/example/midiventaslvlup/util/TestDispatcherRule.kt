package com.example.midiventaslvlup.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext


@OptIn(ExperimentalCoroutinesApi::class)
class TestDispatcherRule : BeforeEachCallback, AfterEachCallback {

    /**
     * TestDispatcher público para ser usado en los tests
     *
     * StandardTestDispatcher permite:
     * - Control manual del tiempo en tests
     * - Ejecución sincrónica de corrutinas
     * - Testing determinista sin delays reales
     */
    val testDispatcher: TestDispatcher = StandardTestDispatcher()

    /**
     * Ejecutado ANTES de cada test
     * Configura Dispatchers.Main para usar el TestDispatcher
     */
    override fun beforeEach(context: ExtensionContext?) {
        Dispatchers.setMain(testDispatcher)
    }

    /**
     * Ejecutado DESPUÉS de cada test
     * Restaura Dispatchers.Main a su configuración original
     */
    override fun afterEach(context: ExtensionContext?) {
        Dispatchers.resetMain()
    }
}
