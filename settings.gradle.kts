rootProject.name = "VideoAvatarAI"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        // JetBrains Compose dev repo (нужен для CMP betas/alphas)
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        // JetBrains: navigation-compose, lifecycle, etc.
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
        // RuStore SDK
        maven { url = uri("https://artifactory.rustore.ru/artifactory/projects") }
        // JitPack fallback
        maven { url = uri("https://jitpack.io") }
    }
}

include(":composeApp")
