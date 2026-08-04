# 构建命令参考

## 标准构建

```bash
# 构建 Android APK (debug)
./gradlew :androidApp:assembleDebug

# 构建 Android APK (release)
./gradlew :androidApp:assembleRelease

# 构建 shared 模块 (所有目标平台)
./gradlew :shared:build

# 构建 iOS framework (供 Xcode 使用)
./gradlew :shared:syncFramework \
    -Pkotlin.native.cocoapods.platform=IOS \
    -Pkotlin.native.cocoapods.archs=arm64

# 运行 JS/webpack 构建 (shared 模块输出 nativevue2.js)
./gradlew :shared:jsBrowserProductionWebpack

# 运行测试
./gradlew test
./gradlew :shared:test
```

## HarmonyOS 构建

OHOS 构建使用独立的 Gradle 配置：
- `settings.gradle.kts` → `settings.ohos.gradle.kts`
- `build.gradle.kts` → `build.ohos.gradle.kts`
- Kotlin `2.0.21-KBA-010` + `ohosArm64` 目标平台

## 本地开发服务器

```bash
npm install
npm run serve   # 8017 端口 Koa 服务器 + 8083 端口 whistle 代理
```

## 构建变体对照

| 构建类型 | Kotlin 版本 | Kuikly 版本 | 配置文件 |
|---------|-------------|-------------|---------|
| 标准构建 | 2.1.21 | 2.7.0-2.1.21 | `build.gradle.kts` / `settings.gradle.kts` |
| OHOS 构建 | 2.0.21-KBA-010 | 2.7.0-2.0.21-ohos | `build.ohos.gradle.kts` / `settings.ohos.gradle.kts` |

Kuikly 版本遵循 `${KUIKLY_VERSION}-${KOTLIN_VERSION}` 模式。
