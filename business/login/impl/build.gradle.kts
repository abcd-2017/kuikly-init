plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("com.tencent.kuiklybase.resource.generator")
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

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":business:login:api"))
                implementation(project(":common:base"))
                implementation(libs.koin.core)
                implementation("com.tencent.kuiklybase:resource-core:0.0.1")
                implementation("com.tencent.kuiklybase:resource-compose:0.0.1")
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
    }
}

android {
    namespace = "com.kuikly.init.business.login.impl"
    compileSdk = 34
    defaultConfig {
        minSdk = 21
    }
}

multiplatformResources {
    multiplatformResourcesPackage = "com.kuikly.init.business.login.impl"
    multiplatformResourcesClassName = "LoginImplMR"
    multiplatformResourcesPrefix = "login_impl_"
}
