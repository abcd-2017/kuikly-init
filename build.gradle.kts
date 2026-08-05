plugins {
    //trick: for the same plugin versions in all sub-modules
    id("com.android.application").version("7.4.2").apply(false)
    id("com.android.library").version("7.4.2").apply(false)
    kotlin("android").version("2.1.21").apply(false)
    kotlin("multiplatform").version("2.1.21").apply(false)
    id("com.google.devtools.ksp").version("2.1.21-2.0.1").apply(false)
    id("org.jetbrains.compose").version("1.7.3").apply(false)
    kotlin("plugin.compose").version("2.1.21").apply(false)
    id("com.tencent.kuiklybase.knoi.plugin").version("0.0.4").apply(false)
}

buildscript {
    dependencies {
        classpath(BuildPlugin.kuikly)
    }
}

// ============================================================
//  Harmony (OHOS) 构建任务组
//  在 Android Studio Gradle 侧边栏 "harmony" 分组下可见
//
//  任务列表:
//    :harmonySyncDebug    编译 ohosArm64 debug + 复制产物到 ohosApp (热重载)
//    :harmonySyncRelease  编译 ohosArm64 release + 复制产物到 ohosApp
//    :harmonyClean        清理 OHOS 产物
//
//  原理: 根项目使用标准 settings (Kotlin 2.1.21)，无 ohosArm64 target。
//        通过 exec 派生子 Gradle 进程 (-c settings.ohos.gradle.kts)，
//        子进程使用 OHOS settings (Kotlin 2.0.21-KBA-010)，可访问 linkSharedOhosArm64。
// ============================================================

val ohosSettingsFile = "settings.ohos.gradle.kts"
val ohosSoName = "libshared.so"
val ohosSoOutputBase = "${rootDir}/shared/build/bin/ohosArm64"
val ohosSoDestDir = "${rootDir}/ohosApp/entry/libs/arm64-v8a"
val ohosAssetsSrc = "${rootDir}/shared/src/commonMain/assets"
val ohosAssetsDest = "${rootDir}/ohosApp/entry/src/main/resources/resfile"

// 根据构建类型确定产物路径与 link 任务名
fun ohosPaths(buildType: String): Pair<String, String> {
    val variant = if (buildType == "release") "releaseShared" else "debugShared"
    val linkTask = if (buildType == "release") "linkReleaseSharedOhosArm64" else "linkDebugSharedOhosArm64"
    return Pair("${ohosSoOutputBase}/${variant}/${ohosSoName}", linkTask)
}

// ---- 核心: 构建 + 复制产物 ----
fun Project.execHarmonySync(buildType: String) {
    val (soPath, linkTask) = ohosPaths(buildType)
    val isWindows = System.getProperty("os.name").lowercase().contains("windows")
    val gradlew = if (isWindows) "${rootDir}\\gradlew.bat" else "${rootDir}/gradlew"

    // 1. 触发子 Gradle 构建 OHOS
    logger.lifecycle("[harmony] 开始构建 ohosArm64 (${buildType})...")
    exec {
        commandLine(gradlew, "-c", ohosSettingsFile, ":shared:${linkTask}", "--no-daemon")
        setWorkingDir(rootDir)
    }

    // 2. 复制 libshared.so
    val soFile = file(soPath)
    if (soFile.exists()) {
        copy {
            from(soFile)
            into(file(ohosSoDestDir))
        }
        logger.lifecycle("[harmony] ✓ ${ohosSoName} → ${ohosSoDestDir}")
    } else {
        throw GradleException("[harmony] 未找到产物: ${soPath}")
    }

    // 3. 复制 assets 资源
    val assetsDir = file(ohosAssetsSrc)
    if (assetsDir.exists() && assetsDir.listFiles()?.isNotEmpty() == true) {
        copy {
            from(ohosAssetsSrc) { into(".") }
            into(file(ohosAssetsDest))
        }
        logger.lifecycle("[harmony] ✓ assets → ${ohosAssetsDest}")
    }
}

// ---- 任务: debug 同步 (含热重载) ----
tasks.register("harmonySyncDebug") {
    group = "harmony"
    description = "编译 ohosArm64 debug 并复制产物到 ohosApp（源码变更自动重编）"
    doLast {
        logger.lifecycle("[harmony] === 开始 Debug 同步 ===")
        execHarmonySync("debug")
    }
}

// ---- 任务: release 同步 ----
tasks.register("harmonySyncRelease") {
    group = "harmony"
    description = "编译 ohosArm64 release 并复制产物到 ohosApp"
    doLast {
        logger.lifecycle("[harmony] === 开始 Release 同步 ===")
        execHarmonySync("release")
    }
}

// ---- 任务: 清理 ----
tasks.register<Delete>("harmonyClean") {
    group = "harmony"
    description = "清理 OHOS 构建产物"
    delete("${ohosSoOutputBase}")
    delete("${ohosSoDestDir}/${ohosSoName}")
}
