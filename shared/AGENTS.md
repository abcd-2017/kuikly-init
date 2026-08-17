# shared 模块

## 模块定位

KMM（Kotlin Multiplatform）共享模块，是项目的**跨平台业务编译产物**。包含入口路由页面、图片适配器基准测试页、跨平台文件枚举声明，以及各平台特定的初始化逻辑。编译产物供 Android / iOS / HarmonyOS 各平台消费。

## 核心职责

1. **页面定义**：通过 `@Page("name")` 注解声明 Kuikly 页面，KSP 在构建时自动注册路由
2. **共享 UI**：基于 Kuikly fork 版 Jetpack Compose（`com.tencent.kuikly.compose`）编写跨平台 UI 组件
3. **平台初始化**：各平台通过 Koin（Android/iOS）或 C 桥接（OHOS）注册依赖服务
4. **公共资源管理**：assets 中的图片等静态资源

> **注意**：`BasePager`、`BridgeModule`、`BasicWidget`、`Utils`、`IPagerIdKtx` 等基础组件已提取到 `:common:widget` 和 `:common:base` 模块。本模块通过 `implementation` 依赖它们。

## 详细目录结构

```
shared/
├── build.gradle.kts              # 标准构建配置 (Kotlin 2.1.21 + Kuikly 2.25.0-2.1.21)
├── build.ohos.gradle.kts         # OHOS 构建配置 (Kotlin 2.0.21-ohos + Kuikly 2.25.0-2.0.21-ohos)
└── src/
    ├── commonMain/               # 所有平台共享代码
    │   ├── assets/               # 打包到各平台的静态资源
    │   │   └── image_adapter/
    │   │       └── sample.png    # 图片适配器测试用基准图
    │   └── kotlin/com/kuikly/init/
    │       ├── RouterPage.kt             # 📌 入口/路由控制台页面 (@Page("router", supportInLocal = true))
    │       ├── ImageAdapterBenchmarks.kt  # 📌 图片适配器基准测试页 (@Page("image_adapter"))
    │       └── FileListSupport.kt        # 📌 expect 跨平台文件枚举声明
    ├── androidMain/              # Android 专属共享代码
    │   └── kotlin/com/kuikly/init/
    │       └── FileListSupport.kt        # 📌 actual 实现（java.io.File 枚举）
    ├── iosMain/                  # iOS 专属共享代码
    │   └── kotlin/com/kuikly/init/
    │       ├── FileListSupport.kt        # 空实现桩
    │       └── platform/
    │           └── IOSInitTaskSetup.kt   # 📌 setupIOSKoin() — 启动 Koin + 执行 InitTaskRunner
    ├── ohosArm64Main/            # OHOS 专属共享代码
    │   └── kotlin/com/kuikly/init/
    │       ├── FileListSupport.kt        # 空实现桩
    │       └── platform/
    │           ├── OHOSInitSetup.kt      # 📌 setupOHKoin() 空桩（KNOI 注册已移至 ETS 侧）
    │           ├── OHOSNativeBridge.kt   # 📌 setupOHOSPlatform() C 桥接入口
    │           └── OHOSPlatformModules.kt # OHOS 平台模块说明
    └── commonTest/               # 共享测试代码
```

## 核心类说明

### RouterPage.kt
- 入口页面 `@Page("router", supportInLocal = true)`，提供页面路由跳转控制台
- UI 结构：logo + 输入框 + 跳转按钮，渐变紫色主题
- 内置 SharedPreferences 缓存最近输入（key: `router_last_input_key2`）
- 快捷跳转 "ImageAdapter基准测试"（`image_adapter`）和 "Debug 测试中心"（`debug_home`）
- 跳转逻辑：解析 `pageName` 或 `pageName&key=value` 格式，通过 `RouterModule.openPage()` 导航

### ImageAdapterBenchmarks.kt
- 图片适配器基准测试页 `@Page("image_adapter")`
- 继承 `ComposeContainer`，4 个测试场景：base64 图片、assets 图片、http/https 图片、gif 动图
- 验证点：intrinsicSize、Image 渲染、drawImage、capInsets

### FileListSupport.kt
- `internal expect fun listFilesInDirectory(dir: String): List<String>` 跨平台文件枚举声明
- **Android** `actual`：使用 `java.io.File` 枚举目录下文件名
- **iOS / OHOS** `actual`：空实现（返回空列表，当前未支持文件枚举）

### IOSInitTaskSetup.kt
- `fun setupIOSKoin()` — iOS 平台初始化入口
- 启动 Koin 注册服务：`ContextProvider`、`DeviceInfo`、`FileSystem`、`NetworkMonitor`、`DebugModule`
- 执行 `InitTaskRunner.runAll(emptyList())` 运行启动任务

### OHOSInitSetup.kt
- `fun setupOHKoin()` — OHOS 平台初始化入口（空桩）
- KNOI 服务注册已移至 ETS 侧（`EntryAbility.ets`），此函数保留以避免破坏 C 桥接调用

### OHOSNativeBridge.kt
- `fun setupOHOSPlatform()` — C 可调用的顶层函数
- 由 `napi_init.cpp` 在 `initKuikly()` 之后调用
- 导出为 C 函数 `com_kuikly_init_platform_setupOHOSPlatform`
- 内部委托给 `setupOHKoin()`

## 页面注册

本模块自身仅注册 **2 个页面**：

| 页面名 | 注解 | 说明 |
|--------|------|------|
| `router` | `@Page("router", supportInLocal = true)` | 入口/路由控制台 |
| `image_adapter` | `@Page("image_adapter")` | 图片适配器基准测试 |

Debug 系列页面由 `:business:debug:impl` 子模块通过 multi-module KSP 注册（`subModules=debug_impl`）。

## 构建配置

### 版本
- Kuikly：`2.25.0-2.1.21`（标准）/ `2.25.0-2.0.21-ohos`（OHOS）
- Kotlin：`2.1.21`（标准）/ `2.0.21-ohos`（OHOS）
- KmmResource：`0.0.1`（0.1.0 有 StackOverflow bug）

### 插件
`multiplatform`, `native.cocoapods`, `android.library`, `ksp`, `maven-publish`, `kuikly`, `jetcompose`, `plugin.compose`, `kuiklybase.resource.generator`

### KSP 参数
```kotlin
arg("moduleId", "shared")
arg("isMainModule", "true")
arg("subModules", "debug_impl")
arg("enableMultiModule", "true")
```

### 资源
- TMM package: `com.kuikly.init`
- 资源类: `SharedMR`

## 依赖（commonMain）

| 依赖 | 类型 | 说明 |
|------|------|------|
| `com.tencent.kuikly-open:core` | api | Kuikly 核心 |
| `com.tencent.kuikly-open:core-annotations` | api | Kuikly 注解 |
| `com.tencent.kuikly-open:compose` | api | Kuikly Compose |
| `:common:base` | implementation | 基础工具层 |
| `:common:widget` | implementation | 共享 UI 组件（BasePager/BridgeModule/BasicWidget 等） |
| `:business:initTask` | implementation | 启动任务框架 |
| `:business:debug:impl` | implementation | Debug 页面实现 |
| `libs.koin.core` | implementation | Koin DI 容器 |

## 构建产物

| 目标平台 | 产物 | 说明 |
|---------|------|------|
| Android | AAR (via `core-render-android`) | 由 androidApp 依赖 |
| iOS | Framework (`shared.framework`) | CocoaPods 集成 |
| HarmonyOS | `libshared.so` | 通过 NAPI 加载 |

## 开发约定

- 新增页面：继承 `:common:widget` 中的 `BasePager` + `@Page("name")` 注解
- 新增桥接方法：在 `:common:widget` 中的 `BridgeModule` 添加常量 + 对应方法，三平台原生模块同步实现
- 业务代码 import 路径中**不出现 kuikly**（BasePager/BridgeModule 等框架封装除外）
- 页面名称通过 KSP `pageName` 参数传递
- 平台初始化逻辑放在对应 `platform/` 目录下
