plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // Agregar Jacoco para cobertura de código
    jacoco
}

android {
    namespace = "com.example.midiventaslvlup"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.midiventaslvlup"
        minSdk = 33
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }

    // Configuración para JUnit 5
    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    // Configuración de packaging para evitar conflictos
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE-notice.md"
        }
    }
}

dependencies {

    val lifecycleVersion = "2.8.6"
    val activityCompose = "1.9.3"
    val navCompose = "2.8.3"

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // OkHttp
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

    // Compose
    implementation(platform("androidx.compose:compose-bom:2024.09.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // Navigation
    implementation("androidx.activity:activity-compose:$activityCompose")
    implementation("androidx.navigation:navigation-compose:$navCompose")

    // Lifecycle / MVVM
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")

    // Coil (Cargar imágenes desde URL)
    implementation("io.coil-kt:coil-compose:2.7.0")

    // Gson
    implementation("com.google.code.gson:gson:2.10.1")

    // Google Maps para Compose
    implementation("com.google.maps.android:maps-compose:4.3.3")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.1.0")

    // OSMDroid para OpenStreetMap
    implementation("org.osmdroid:osmdroid-android:6.1.18")

    // AndroidX Core
    implementation(libs.androidx.core.ktx)

    // ============================================
    // DEPENDENCIAS DE TESTING - NUEVAS
    // ============================================

    // JUnit 5 (Jupiter) para unit tests
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.1")

    // Kotest para assertions más expresivas
    testImplementation("io.kotest:kotest-assertions-core:5.8.0")
    testImplementation("io.kotest:kotest-property:5.8.0")

    // MockK para mocking en Kotlin
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("io.mockk:mockk-android:1.13.8")
    testImplementation("io.mockk:mockk-agent:1.13.8")

    // Coroutines Test
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")

    // Turbine para testear Flows y StateFlows
    testImplementation("app.cash.turbine:turbine:1.0.0")

    // Robolectric para tests con contexto Android en JVM
    testImplementation("org.robolectric:robolectric:4.11.1")

    // AndroidX Test - Core
    testImplementation("androidx.test:core:1.5.0")
    testImplementation("androidx.test:core-ktx:1.5.0")
    testImplementation("androidx.test.ext:junit:1.1.5")
    testImplementation("androidx.test.ext:junit-ktx:1.1.5")

    // Arch Core Testing para LiveData (si lo usas)
    testImplementation("androidx.arch.core:core-testing:2.2.0")

    // OkHttp MockWebServer para testear llamadas HTTP
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")

    // ============================================
    // ANDROID INSTRUMENTED TESTS (UI)
    // ============================================

    // Compose UI Testing
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.09.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.compose.ui:ui-test-manifest")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // AndroidX Test Runner and Rules
    androidTestImplementation("androidx.test:runner:1.5.0")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.5")

    // Espresso
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.0")

    // MockK para Android Tests
    androidTestImplementation("io.mockk:mockk-android:1.13.8")

    // Navigation Testing
    androidTestImplementation("androidx.navigation:navigation-testing:$navCompose")

    // Coroutines Test para Android Tests
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")

    // ============================================
    // LEGACY TESTS (los que ya tenías)
    // ============================================
    testImplementation(libs.junit)
}

// ============================================
// TAREA DE JACOCO PARA COBERTURA DE CÓDIGO
// ============================================
tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }

    val fileFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*",
        "**/data/remote/api/*", // Excluir interfaces de Retrofit
        "**/di/*" // Excluir dependency injection si la agregas
    )

    val debugTree = fileTree("${project.buildDir}/tmp/kotlin-classes/debug") {
        exclude(fileFilter)
    }

    val mainSrc = "${project.projectDir}/src/main/java"

    sourceDirectories.setFrom(files(mainSrc))
    classDirectories.setFrom(files(debugTree))
    executionData.setFrom(fileTree(project.buildDir) {
        include("jacoco/testDebugUnitTest.exec")
    })
}

// Task para limpiar antes de ejecutar tests
tasks.register("cleanTest") {
    dependsOn("clean")
    doLast {
        delete("${project.buildDir}/jacoco")
        delete("${project.buildDir}/test-results")
    }
}
