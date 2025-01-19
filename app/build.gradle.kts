// Definición de los plugins necesarios para el proyecto
plugins {
    id("com.android.application")  // Plugin para aplicaciones Android
    id("org.jetbrains.kotlin.android")  // Plugin para soporte de Kotlin en Android
}

android {
    // Configuración del espacio de nombres del paquete
    namespace = "com.insece.usbserialreader"
    // Versión del SDK de Android para compilación
    compileSdk = 34

    // Configuración por defecto de la aplicación
    defaultConfig {
        applicationId = "com.insece.usbserialreader"  // ID único de la aplicación
        minSdk = 21  // Versión mínima de Android soportada
        targetSdk = 34  // Versión objetivo de Android
        versionCode = 5  // Código de versión de la app (para actualizaciones)
        versionName = "2.2"  // Nombre de la versión de la app

        // Configuración para pruebas instrumentadas
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // Soporte para gráficos vectoriales
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    // Configuración de tipos de compilación
    buildTypes {
        release {
            isMinifyEnabled = false  // Desactiva la minificación del código
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    // Configuración de compatibilidad de Java
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    // Opciones de compilación de Kotlin
    kotlinOptions {
        jvmTarget = "1.8"
    }
    // Habilitación de características de compilación
    buildFeatures {
        compose = true  // Habilita Jetpack Compose
    }
    // Opciones de compilación para Compose
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"  // Excluye ciertos archivos del empaquetado
        }
    }
}

// Declaración de dependencias del proyecto
dependencies {
    // Dependencias principales de Android y Jetpack
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2024.02.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    // Dependencias específicas para la funcionalidad USB Serial
    implementation("com.github.mik3y:usb-serial-for-android:3.4.3")  // Biblioteca para comunicación USB Serial
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")  // Soporte para corrutinas en Android

    // Dani : Dependencias para la interfaz

    // Dependencias para pruebas
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.02.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    // Dependencias para depuración
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}