import org.gradle.kotlin.dsl.kotlin

val KEY_PAGE_NAME = "pageName"

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("com.google.devtools.ksp")
    id("com.tencent.kuiklybase.knoi.plugin") version("0.0.4")
}

ksp {
    arg("pageName", getPageName())
    arg("moduleId", "debug_impl")
    arg("enableMultiModule", "true")
}

fun getPageName(): String {
    return (project.properties["pageName"] as? String) ?: ""
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
                implementation(project(":business:debug:api"))
                implementation(project(":common:base"))
                implementation(project(":common:widget"))
                // 直接依赖 Kuikly OHOS 框架，替代 implementation(project(":shared")) 以打破循环依赖
                implementation("com.tencent.kuikly-open:core:${Version.getKuiklyOhosVersion()}")
                implementation("com.tencent.kuikly-open:core-annotations:${Version.getKuiklyOhosVersion()}")
                implementation("com.tencent.kuikly-open:compose:${Version.getKuiklyOhosVersion()}")
            }
            // OHOS 编译器不支持 commonMain 的部分语法，清空源码目录避免编译
            kotlin.setSrcDirs(listOf<Any>())
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

android {
    namespace = "com.kuikly.init.business.debug.impl"
    compileSdk = 34
    defaultConfig {
        minSdk = 21
    }
}
