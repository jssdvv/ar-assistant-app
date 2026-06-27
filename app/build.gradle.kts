plugins {
    alias(libs.plugins.jetbrains.kotlin.plugin.serialization)
    alias(libs.plugins.jetbrains.compose.compiler)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.kapt)
    alias(libs.plugins.google.dagger.hilt.android)
    alias(libs.plugins.androidx.room)
}

android {
    namespace = "com.jssdvv.ara"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets {
        getByName("main") {
            java.srcDir("src/main/kotlin")
            manifest.srcFile("src/main/AndroidManifest.xml")
        }
    }

    defaultConfig {
        applicationId = "com.jssdvv.ara"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        //testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    room {
        schemaDirectory("$projectDir/schemas")

    }
    composeCompiler {}
}

dependencies {
    // Android
    implementation(libs.androidx.core.ktx) // Checked
    implementation(libs.androidx.activity.compose) // Checked
    implementation(libs.androidx.work.runtime.ktx) // Checked

    // Kotlin
    implementation(libs.jetbrains.kotlinx.serialization.json) // Checked
    implementation(libs.jetbrains.kotlinx.coroutines.android) // Checked
    implementation(libs.jetbrains.kotlinx.coroutines.guava) // Checked
    implementation(libs.jetbrains.kotlin.reflect) // Checked

    // Lifecycle
    implementation(libs.bundles.androidx.lifecycle) // Checked

    // Compose
    implementation(platform(libs.androidx.compose.bom)) // Checked
    implementation(libs.bundles.androidx.compose) // Checked
    implementation(libs.bundles.androidx.compose.adaptive) // Checked
    debugImplementation(libs.androidx.compose.ui.tooling) // Checked

    // Camera
    implementation(libs.bundles.androidx.camera) // Checked

    // Navigation
    implementation(libs.androidx.navigation.compose) // Checked

    // Room
    implementation(libs.bundles.androidx.room) // Checked
    kapt(libs.androidx.room.compiler) // Checked

    // Datastore
    implementation(libs.androidx.datastore.preferences) // Checked

    // DI
    implementation(libs.androidx.hilt.navigation.compose) // Checked
    implementation(libs.google.dagger.hilt.android) // Checked
    implementation(libs.androidx.hilt.work) // Checked
    kapt(libs.google.dagger.hilt.android.compiler) // Checked

    // Images
    implementation(libs.coil.compose) // Checked

    // Barcodes
    implementation(libs.bundles.google.barcode.scanning) // Checked

    // Augmented Reality
    implementation(libs.sceneview.arsceneview) // Checked

    // Hashing
    implementation(libs.openhft.zero.allocation.hashing) // Checked
}

kapt {
    correctErrorTypes = true
    useBuildCache = true
    arguments {
        arg("room.incremental", "true")
    }
}