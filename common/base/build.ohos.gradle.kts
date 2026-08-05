plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("com.google.devtools.ksp")
    id("com.tencent.kuiklybase.knoi.plugin") version("0.0.4")
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    ohosArm64 {
        binaries.sharedLib {
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
        val ohosArm64Main by getting {
            dependsOn(commonMain)
        }
    }
}

dependencies {
    // KNOI: Kotlin Native Ohos Interaction - Kotlin ↔ ArkTS 互调
    add("kspOhosArm64", "com.tencent.kuiklybase.knoi:knoi:0.0.4")
}

android {
    namespace = "com.kuikly.init.common.base"
    compileSdk = 34
    defaultConfig {
        minSdk = 21
    }
}
