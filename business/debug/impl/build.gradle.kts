plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("com.google.devtools.ksp")
    id("com.tencent.kuikly-open.kuikly")
    id("org.jetbrains.compose")
    kotlin("plugin.compose")
    id("com.tencent.kuiklybase.resource.generator")
}

val KEY_PAGE_NAME = "pageName"

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
                api("com.tencent.kuikly-open:core:${Version.getKuiklyVersion()}")
                api("com.tencent.kuikly-open:core-annotations:${Version.getKuiklyVersion()}")
                api("com.tencent.kuikly-open:compose:${Version.getKuiklyVersion()}")
                implementation(project(":business:debug:api"))
                implementation(project(":common:base"))
                implementation(project(":common:widget"))
                implementation("io.insert-koin:koin-core:4.0.1")
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

ksp {
    arg(KEY_PAGE_NAME, getPageName())
    // 多模块配置：debug_impl 是子模块（moduleId 不能包含冒号）
    arg("moduleId", "debug_impl")
    arg("isMainModule", "false")
    arg("enableMultiModule", "true")
}

dependencies {
    compileOnly("com.tencent.kuikly-open:core-ksp:${Version.getKuiklyVersion()}") {
        add("kspAndroid", this)
        add("kspIosArm64", this)
        add("kspIosX64", this)
        add("kspIosSimulatorArm64", this)
    }
}

android {
    namespace = "com.kuikly.init.business.debug.impl"
    compileSdk = 34
    defaultConfig {
        minSdk = 21
    }
}

multiplatformResources {
    multiplatformResourcesPackage = "com.kuikly.init.business.debug.impl"
    multiplatformResourcesClassName = "DebugImplMR"
    multiplatformResourcesPrefix = "debug_impl_"
}

fun getPageName(): String {
    return (project.properties[KEY_PAGE_NAME] as? String) ?: ""
}
