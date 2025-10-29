plugins {
    // Android
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false

    // Kotlin
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.compose) apply false

    // Compose
    alias(libs.plugins.compose.multiplatform) apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}