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

// ── Signing ────────────────────────────────────────────────────────────────────
val keystoreFile = System.getenv("KEYSTORE_PATH")
    ?.let { file(it) }
    ?: rootProject.file("keystore/release.keystore").takeIf { it.exists() }

val ksStorePassword = System.getenv("KEYSTORE_PASSWORD") ?: "VideoAvatarAI2025"
val ksKeyAlias       = System.getenv("KEY_ALIAS")         ?: "videoavataraii"
val ksKeyPassword    = System.getenv("KEY_PASSWORD")      ?: "VideoAvatarAI2025"

android {
    namespace   = "org.nla.videoavataraii"
    compileSdk  = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId  = "org.nla.videoavataraii"
        minSdk         = libs.versions.android.minSdk.get().toInt()
        targetSdk      = libs.versions.android.targetSdk.get().toInt()
        versionCode    = 1
        versionName    = "1.0"

        buildConfigField("boolean", "IS_RUSTORE", "true")
        buildConfigField("String", "API_BASE_URL", "\"https://api.videoavataraii.com\"")
    }

    // Signing config только если keystore файл существует
    if (keystoreFile != null) {
        signingConfigs {
            create("release") {
                storeFile     = keystoreFile
                storePassword = ksStorePassword
                keyAlias      = ksKeyAlias
                keyPassword   = ksKeyPassword
            }
        }
    }

    buildFeatures { buildConfig = true }

    packaging {
        resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" }
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
        }
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            if (keystoreFile != null) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    dependencies {
        // Firebase (опционально — не блокирует сборку)
        implementation(platform("com.google.firebase:firebase-bom:33.13.0"))
        implementation("com.google.firebase:firebase-messaging-ktx")

        // Desugaring
        coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")

        // Google Play Billing
        implementation("com.android.billingclient:billing-ktx:7.1.1")
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}
