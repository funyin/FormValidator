# Installation

FormValidator is published via [JitPack](https://jitpack.io/#funyin/FormValidator).

## Step 1 — Add the JitPack repository

=== "Kotlin Gradle (settings.gradle.kts)"

    ```kotlin
    dependencyResolutionManagement {
        repositories {
            maven { url = uri("https://jitpack.io") }
        }
    }
    ```

=== "Groovy (settings.gradle)"

    ```groovy
    dependencyResolutionManagement {
        repositories {
            maven { url 'https://jitpack.io' }
        }
    }
    ```

## Step 2 — Add the dependency

=== "Kotlin Gradle"

    ```kotlin
    implementation("com.github.funyin:FormValidator:LIBRARY_VERSION")
    ```

=== "Groovy"

    ```groovy
    implementation 'com.github.funyin:FormValidator:LIBRARY_VERSION'
    ```

!!! tip "Current version"
    Replace `LIBRARY_VERSION` with the latest tag from the [releases page](https://github.com/funyin/FormValidator/releases).

## Supported targets

| Target | Minimum |
|--------|---------|
| Android | API 21 |
| JVM (desktop) | JVM 8 |
| iOS | iosArm64, iosX64, iosSimulatorArm64 |

## Requirements

- Kotlin 2.0+
- Compose Multiplatform 1.7+
