import org.gradle.internal.declarativedsl.parsing.main

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

    sourceSets.all {
        res.srcDirs(
            "src/main/res",
            "src/main/res/ara",
            "src/main/res/core",
            "src/main/res/scanner",
            "src/main/res/machines",
            "src/main/res/machines/machines",
            "src/main/res/machines/machine-details",
            "src/main/res/machines/activities",
            "src/main/res/machines/activity-details",
            "src/main/res/machines/markers",
            "src/main/res/machines/ar-session",
            "src/main/res/inventory"
        )
        java.srcDir("src/main/kotlin")
        manifest.srcFile("src/main/AndroidManifest.xml")
    }
    defaultConfig {
        applicationId = "com.jssdvv.ara"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    kotlinOptions {
        jvmTarget = "21"
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
    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
    //androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    // Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.material3.adaptive.navigation.suite.android)

    // Kotlin
    implementation(libs.jetbrains.kotlinx.serialization.json)
    implementation(libs.jetbrains.kotlinx.coroutines.android)
    implementation(libs.jetbrains.kotlin.reflect)

    // Lifecycle
    implementation(libs.bundles.androidx.lifecycle)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.androidx.compose)
    debugImplementation(libs.androidx.compose.ui.tooling)
    //debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Camera
    implementation(libs.bundles.androidx.camera)

    // Barcodes
    implementation(libs.google.mlkit.barcodeScanning)
    implementation(libs.google.zxing.core)

    // Augmented Reality
    implementation(libs.sceneview.arsceneview)

    // Databases
    implementation(libs.androidx.room.runtime)
    annotationProcessor(libs.androidx.room.compiler)
    kapt(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    // Datastore
    implementation(libs.androidx.datastore.preferences)

    // Dependency Injection
    implementation(libs.google.dagger.hilt.android)
    kapt(libs.google.dagger.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Images Loader
    implementation(libs.coil.compose)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-guava:1.9.0")
}

kapt {
    correctErrorTypes = true
}