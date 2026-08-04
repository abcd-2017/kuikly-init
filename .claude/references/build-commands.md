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

### 构建命令

根 `build.gradle.kts` 注册了 OHOS 专用任务组（Gradle 侧边栏 "harmony" 分组下可见）：

```bash
# 编译 ohosArm64 release + 复制产物到 ohosApp
./gradlew :harmonySyncRelease

# 编译 ohosArm64 debug + 复制产物到 ohosApp（热重载用）
./gradlew :harmonySyncDebug

# 清理 OHOS 产物
./gradlew :harmonyClean
```

### 构建产物

| 产物 | 路径 | 说明 |
|------|------|------|
| `libshared.so` | `ohosApp/entry/libs/arm64-v8a/` | 鸿蒙 NAPI 共享库（最终产物） |
| `libshared.so` | `shared/build/bin/ohosArm64/releaseShared/` | 中间产物 |
| `libshared_api.h` | `shared/build/bin/ohosArm64/releaseShared/` | C/C++ 头文件 |
| assets 资源 | `ohosApp/entry/src/main/resources/resfile/` | 自动复制的共享资源 |

### 构建原理

根项目使用标准 settings（Kotlin 2.1.21），本身不含 `ohosArm64` target。
`harmonySyncRelease` 任务通过 `exec` 派生子 Gradle 进程：

```
./gradlew -c settings.ohos.gradle.kts :shared:linkReleaseSharedOhosArm64 --no-daemon
```

子进程使用 OHOS settings（Kotlin 2.0.21-KBA-010），可访问 `linkReleaseSharedOhosArm64` 任务。
编译完成后自动：
1. 复制 `libshared.so` → `ohosApp/entry/libs/arm64-v8a/`
2. 复制 `commonMain/assets/` → `ohosApp/entry/src/main/resources/resfile/`

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
