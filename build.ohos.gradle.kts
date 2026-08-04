plugins {
    //trick: for the same plugin versions in all sub-modules
    id("com.android.application").version("7.4.2").apply(false)
    id("com.android.library").version("7.4.2").apply(false)
    kotlin("android").version("2.0.21-KBA-010").apply(false)
    kotlin("multiplatform").version("2.0.21-KBA-010").apply(false)
    id("com.google.devtools.ksp").version("2.0.21-1.0.27").apply(false)
    id("org.jetbrains.compose").version("1.7.3").apply(false)
    kotlin("plugin.compose").version("2.0.21-KBA-010").apply(false)
}

// OHOS 构建任务已统一注册在根 build.gradle.kts (标准 settings) 的 "harmony" 分组下
// (:harmonySyncDebug / :harmonySyncRelease / :harmonyClean)
// 此处仅保留插件版本声明，供 OHOS settings 激活时使用
