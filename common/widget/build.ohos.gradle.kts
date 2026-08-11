plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("com.google.devtools.ksp")
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

    ohosArm64 {
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("com.tencent.kuikly-open:core:${Version.getKuiklyOhosVersion()}")
                implementation("com.tencent.kuikly-open:core-annotations:${Version.getKuiklyOhosVersion()}")
                implementation("com.tencent.kuikly-open:compose:${Version.getKuiklyOhosVersion()}")
                implementation(project(":common:base"))
                // 注意：koin-core 不支持 OHOS，在 OHOS 构建中移除
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
        val ohosArm64Main by getting {
            dependsOn(commonMain)
        }
    }
}

multiplatformResources {
    multiplatformResourcesPackage = "com.kuikly.init.common.widget"
    multiplatformResourcesClassName = "WidgetMR"
    multiplatformResourcesPrefix = "widget_"
}

android {
    namespace = "com.kuikly.init.common.widget"
    compileSdk = 34
    defaultConfig {
        minSdk = 21
    }
}
