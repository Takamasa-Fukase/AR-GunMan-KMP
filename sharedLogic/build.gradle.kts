import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    kotlin("plugin.serialization") version "2.0.0"
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "SharedLogic"
            isStatic = true
        }
    }
    
    android {
       namespace = "com.takamasafukase.ar_gunman_kmp.sharedLogic"
       compileSdk = libs.versions.android.compileSdk.get().toInt()
       minSdk = libs.versions.android.minSdk.get().toInt()
    
       compilerOptions {
           jvmTarget = JvmTarget.JVM_11
       }
       androidResources {
           enable = true
       }
       withHostTest {
           isIncludeAndroidResources = true
       }
    }
    
    sourceSets {
        commonMain.dependencies {
            // put your Multiplatform dependencies here
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
            implementation("dev.gitlive:firebase-firestore:1.13.0")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
        }
        androidMain.dependencies {
            implementation(project.dependencies.platform("com.google.firebase:firebase-bom:33.1.2"))
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}