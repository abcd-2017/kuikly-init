plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("com.google.devtools.ksp")
    id("maven-publish")
    id("org.jetbrains.compose")
    kotlin("plugin.compose")
    id("com.tencent.kuiklybase.knoi.plugin") version("0.0.4")
}

val KEY_PAGE_NAME = "pageName"

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
        publishLibraryVariants("release")
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "shared"
            freeCompilerArgs = freeCompilerArgs + getCommonCompilerArgs()
            isStatic = true
            license = "MIT"
        }
    }

    ohosArm64 {
        binaries.sharedLib {
            // 强制导出 KNOI 初始化符号，防止 DCE 消除
            // KNOI 通过 dlsym 动态查找 com_tencent_tmm_knoi_initBridge
            // 该符号必须存在于 libshared.so 的动态符号表中
            // 注意：ld.lld 不使用 -Wl, 前缀，直接传递参数
            // 1. --export-dynamic: 将所有符号导出到动态符号表，使其可被 dlsym 查找
            // 2. --undefined: 强制链接器解析该符号，防止被 DCE 裁剪
            // 3. 直接链接 bridge.c 编译出的对象文件，确保符号在动态符号表中
            linkerOpts += "--export-dynamic"
            linkerOpts += "--undefined=com_tencent_tmm_knoi_initBridge"
            // 添加 C 源文件编译出的对象文件，确保符号导出
            val bridgeSrc = "${project.projectDir}/src/ohosArm64Main/cinterop/bridge.c"
            val bridgeObj = "${project.buildDir}/cinterop/bridge.o"
            // 编译 C 源文件并链接
            linkerOpts += bridgeObj
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("com.tencent.kuikly-open:core:${Version.getKuiklyOhosVersion()}")
                implementation("com.tencent.kuikly-open:core-annotations:${Version.getKuiklyOhosVersion()}")
                implementation("com.tencent.kuikly-open:compose:${Version.getKuiklyOhosVersion()}")
                // 平台能力接口
                implementation(project(":common:base"))
                // 初始化任务
                implementation(project(":business:initTask"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                api("com.tencent.kuikly-open:core-render-android:${Version.getKuiklyOhosVersion()}")
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
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
        val ohosArm64Main by getting {
            dependsOn(commonMain)
        }
    }
}

group = "com.kuikly.init"
version = System.getenv("kuiklyBizVersion") ?: "1.0.0"

publishing {
    repositories {
        maven {
            credentials {
                username = System.getenv("mavenUserName") ?: ""
                password = System.getenv("mavenPassword") ?: ""
            }
            rootProject.properties["mavenUr?"]?.toString()?.let { url = uri(it) }
        }
    }
}

ksp {
    arg(KEY_PAGE_NAME, getPageName())
}

dependencies {
    compileOnly("com.tencent.kuikly-open:core-ksp:${Version.getKuiklyOhosVersion()}") {
        add("kspAndroid", this)
        add("kspIosArm64", this)
        add("kspIosX64", this)
        add("kspIosSimulatorArm64", this)
        add("kspOhosArm64", this)
    }
}

android {
    namespace = "com.kuikly.init.shared"
    compileSdk = 34
    defaultConfig {
        minSdk = 21
        targetSdk = 30
    }
    sourceSets {
        named("main") {
            assets.srcDirs("src/commonMain/assets")
        }
    }
}

fun getPageName(): String {
    return (project.properties[KEY_PAGE_NAME] as? String) ?: ""
}

fun getCommonCompilerArgs(): List<String> {
    return listOf(
        "-Xallocator=std"
    )
}

fun getLinkerArgs(): List<String> {
    return listOf()
}

// OHOS 构建任务已统一注册在根 build.gradle.kts 的 "harmony" 分组下
// (:harmonySyncDebug / :harmonySyncRelease / :harmonyClean)
// 此处不再重复注册

// 编译 C 桥接源文件，确保 com_tencent_tmm_knoi_initBridge 符号导出到动态符号表
val compileBridgeC by tasks.registering(Exec::class) {
    val bridgeSrc = "${project.projectDir}/src/ohosArm64Main/cinterop/bridge.c"
    val bridgeObj = "${project.buildDir}/cinterop/bridge.o"
    outputs.file(bridgeObj)
    doFirst {
        file("${project.buildDir}/cinterop").mkdirs()
    }
    // 使用 Kotlin/Native 的 clang 工具链编译，指定 aarch64-linux-ohos 目标
    val konanClang = System.getProperty("user.home") +
        "/.konan/dependencies/llvm-12.0.1-windows-x86_64-20250713/bin/clang.exe"
    commandLine(
        konanClang,
        "--target=aarch64-linux-ohos",
        "--sysroot=E:/IDE/DevEco Studio/sdk/default/openharmony/native/sysroot",
        "-c",
        "-o", bridgeObj,
        bridgeSrc
    )
}

// 确保 Kotlin/Native 编译前 C 文件已编译
tasks.named("compileKotlinOhosArm64") {
    dependsOn(compileBridgeC)
}