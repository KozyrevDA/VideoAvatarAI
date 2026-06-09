@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
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
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.android)
            implementation(libs.ktor.client.android)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            implementation(libs.navigation.compose)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.bundles.koin)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)

            implementation(libs.kotlinx.datetime)
            implementation(libs.bundles.coil)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.stately.common)
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "videoavataraii.composeapp.generated.resources"
    generateResClass = auto
}


// ─── Keystore ─────────────────────────────────────────────────────────────────
// Параметры через переменные окружения (не хранить в коде!)
val keystorePath     = System.getenv("KEYSTORE_PATH")     ?: rootProject.file("keystore/release.keystore").absolutePath
val keystorePassword = System.getenv("KEYSTORE_PASSWORD") ?: "VideoAvatarAI2025"
val keyAlias         = System.getenv("KEY_ALIAS")         ?: "videoavataraii"
val keyPassword      = System.getenv("KEY_PASSWORD")      ?: "VideoAvatarAI2025"

android {
    signingConfigs {
        create("release") {
            storeFile      = file(keystorePath)
            storePassword  = keystorePassword
            keyAlias       = keyAlias
            keyPassword    = keyPassword
        }
    }
        namespace = "org.nla.videoavataraii"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "org.nla.videoavataraii"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        buildConfigField("boolean", "IS_RUSTORE", "true")
        buildConfigField("String", "API_BASE_URL", "\"https://api.videoavataraii.com\"")
    }

    buildFeatures {
        buildConfig = true
    }

    packaging {
        resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" }
    }

    buildTypes {
        getByName("debug") { isMinifyEnabled = false }
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    dependencies {
        implementation(platform("com.google.firebase:firebase-bom:32.8.0"))
        implementation("com.google.firebase:firebase-analytics")
        implementation("com.google.firebase:firebase-messaging")
        coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.2")

        // RuStore Pay SDK (актуальный, BillingClient отключается 1 августа 2026)
        // https://www.rustore.ru/help/sdk/pay/kotlin-java
        implementation(platform("ru.rustore.sdk:bom:2026.04.01"))
        implementation("ru.rustore.sdk:pay")

        // Google Play Billing (для GP версии)
        implementation("com.android.billingclient:billing-ktx:8.0.0")
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}
