import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)

    // --- LÍNEA NUEVA PARA FIREBASE ---
    id("com.google.gms.google-services")
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)

            // Firebase Nativo Android
            implementation("com.google.android.gms:play-services-auth:21.0.0")
            implementation("com.google.firebase:firebase-auth-ktx:22.3.1")
            implementation("com.google.firebase:firebase-firestore-ktx:24.10.1")

            // Motor Ktor para Android (Asegúrate de que esté en libs.versions.toml)
            implementation(libs.ktor.client.okhttp)

            // 1. Esto soluciona el error "(Kotlin reflection is not available)"
            implementation(kotlin("reflect"))
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(compose.materialIconsExtended)

            // Firebase KMP
            implementation("dev.gitlive:firebase-auth:1.11.1")
            implementation("dev.gitlive:firebase-firestore:1.11.1")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
            
            // 2. Manejo de Fechas y Reloj (CORRECCIÓN UNRESOLVED CLOCK)
            implementation(libs.kotlinx.datetime)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation("io.ktor:ktor-client-logging:2.3.12") // Añadir esto
            // Imagenes y Red
            implementation(libs.kamel.image)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.cio) // Motor de reserva
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.example.fila_virtual"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.example.fila_virtual"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
        // Eliminamos la línea de ktor que estaba causando el error aquí
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(libs.compose.uiTooling)
}