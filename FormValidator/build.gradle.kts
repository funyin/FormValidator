import java.util.Properties
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven
import org.gradle.plugins.signing.Sign

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.dokka)
    alias(libs.plugins.nmcp)
    id("maven-publish")
    signing
}

val localProperties = Properties().apply {
    rootProject.file("local.properties").takeIf { it.exists() }?.inputStream()?.use { load(it) }
}

kotlin {
    applyDefaultHierarchyTemplate()

    androidTarget {
        publishLibraryVariants("release", "debug")
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8)
        }
    }

    jvm("desktop")

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.animation)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        androidMain.dependencies {
            implementation(compose.uiTooling)
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.common)
            }
        }
    }
}

android {
    namespace = "com.github.funyin.formvalidator"
    compileSdk = 35

    defaultConfig {
        minSdk = 21
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        compose = true
    }
}

group = "com.funyinkash"
version = "1.0.5"

publishing {
    val javadocJar by tasks.registering(Jar::class) {
        archiveClassifier.set("javadoc")
        dependsOn("dokkaHtml")
        from(layout.buildDirectory.dir("dokka/html"))
    }

    publications.withType<MavenPublication>().configureEach {
        artifact(javadocJar)

        pom {
            name.set("FormValidator")
            description.set(
                "A Kotlin Multiplatform library for declarative form validation in Jetpack Compose. " +
                "Supports Android, JVM (Desktop), and iOS."
            )
            url.set("https://github.com/funyin/FormValidator")

            issueManagement {
                system.set("Github")
                url.set("https://github.com/funyin/FormValidator/issues")
            }

            licenses {
                license {
                    name.set("The Apache License, Version 2.0")
                    url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                }
            }

            developers {
                developer {
                    id.set("funyin")
                    name.set("Funyinoluwa Kashimawo")
                    email.set("funyin.kash@gmail.com")
                }
            }

            scm {
                connection.set("scm:git:git://github.com/funyin/FormValidator.git")
                developerConnection.set("scm:git:ssh://github.com/funyin/FormValidator.git")
                url.set("https://github.com/funyin/FormValidator")
            }
        }
    }

}

nmcp {
    publishAllPublications {
        username = localProperties.getProperty("osshr.username").orEmpty()
        password = localProperties.getProperty("osshr.password").orEmpty()
        // AUTOMATIC releases without manual promotion; use USER_MANAGED to inspect before release
        publicationType = "AUTOMATIC"
    }
}

val signingKeyPath = localProperties.getProperty("signing.secretKeyFile")
if (!signingKeyPath.isNullOrBlank()) {
    signing {
        useInMemoryPgpKeys(
            rootProject.file(signingKeyPath).readText(),
            localProperties.getProperty("signing.password").orEmpty(),
        )
        sign(publishing.publications)
    }
}

// KMP creates one publish task per target, and each reads the shared .module metadata
// which references artifacts signed by other targets' sign tasks. Without this ordering
// constraint Gradle detects it as an implicit dependency and fails configuration.
tasks.withType<AbstractPublishToMaven>().configureEach {
    mustRunAfter(tasks.withType<Sign>())
}
