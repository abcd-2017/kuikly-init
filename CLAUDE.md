# CLAUDE.md

本文件为 Claude Code (claude.ai/code) 提供在本仓库中工作时的指导。

## 约束

- **回答必须使用中文**。

## 项目定位

基于 Kuikly 的**跨端脚手架项目**，目标是抽象系统级基础能力（导航、网络、存储、日志、平台桥等），让后续业务项目开箱即用。

当前处于**阶段 1（实体项目）**：用固定包名开发真实可编译运行的项目，开发 → 测试 → 验证基础能力抽象是否合理。此阶段包名写死没关系，但遵守「集中管理 + 源码不硬编码」原则。

后续**阶段 2（模板化）**：将实体项目转换为可复用模板，包名替换为占位符 `__PACKAGE_NAME__`，实现 CLI init 命令：`kmp-cli init <project> --package <pkg>`。

## 已确定的技术选型

### 框架：Kuikly（腾讯）
- 仓库：https://github.com/Tencent-TDS/KuiklyUI
- 支持：Android / iOS / HarmonyOS / Web(Beta) / 小程序(Beta) / macOS(Alpha)
- 选 Kuikly 的核心原因：JetBrains CMP 官方不支持 HarmonyOS，Kuikly 是唯一生产可用的 KMP + 鸿蒙方案

### DSL：Compose DSL（非自研 DSL）
- 基于 Jetpack Compose 1.7.3（Kuikly fork 版，包名 `com.tencent.kuikly.compose`）
- 选 Compose DSL 原因：标准语法、学习成本低、团队招人容易、未来切 CMP 时 UI 层迁移成本最小

### 语言：Kotlin 2.1.21
构建工具：Gradle 7.x + KSP

## 架构设计原则

### 三层解耦

```
com.xxx.app.*            ← 业务代码只 import 这层（永远不变）
  ├── nav/               ← Navigator 等接口定义
  ├── net/               ← HttpClient 等接口定义
  ├── store/             ← Storage 等接口定义
  ├── ui/                ← 公共 UI 组件
  └── di/                ← ServiceLocator（唯一绑定实现的地方）

com.xxx.internal.kuikly  ← Kuikly 适配实现（整体可替换）
  ├── KuiklyNavigator    ← 实现 Navigator
  ├── KuiklyHttpClient   ← 实现 HttpClient
  └── KuiklyServiceLocator ← 绑定实现到接口

com.tencent.kuikly.*     ← Kuikly 原生 API（业务代码绝不直接引用）
```

### 核心约束

1. 业务代码的 import 路径中**永远不出现框架名**（kuikly）
2. 框架名只存在于 Gradle 依赖声明（artifactId）和适配层内部包名
3. 包名集中定义在 `gradle.properties`，源码中引用而非硬编码
4. 切换引擎 = 新增适配模块 + 改一行 ServiceLocator 绑定，业务代码零改动

## 构建命令

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

### HarmonyOS 构建使用独立的 Gradle 配置

在处理 OHOS 构建时，根目录的 `settings.gradle.kts` 会替换为 `settings.ohos.gradle.kts`（后者使用 `build.ohos.gradle.kts` 而非 `build.gradle.kts`）。OHOS 构建使用 Kotlin `2.0.21-KBA-010` 和 `ohosArm64` 目标平台。

### 本地开发服务器

```bash
npm install
npm run serve   # 在 8017 端口启动 Koa 服务器 + 8083 端口启动 whistle 代理
```

静态服务器 (`static_server/serve/index.js`) 用于提供 JS/SO 资源，并通过 whistle 代理支持 Kuikly 的 NativeVue 调试模式。配置位于 `.whistle.js`。

## 项目结构

```
kuikly-init/
├── shared/                    # KMM 共享模块 (通用 Kotlin 代码)
│   └── src/
│       ├── commonMain/kotlin/ # 共享的 Compose UI + 业务逻辑
│       │   └── com/kuikly/init/
│       │       ├── base/      # BasePager, BridgeModule, Utils, IPagerIdKtx
│       │       ├── BasicWidget.kt   # 共享 Compose 组件 (Button, TextField, Modal 等)
│       │       ├── RouterPage.kt    # @Page("router") — 入口/路由页面
│       │       └── ImageAdapterBenchmarks.kt  # @Page("image_adapter") — 图片测试页面
│       ├── androidMain/       # Android 专属的共享代码 (当前为空)
│       ├── iosMain/           # iOS 专属的共享代码 (当前为空)
│       └── commonMain/assets/ # 打包的静态资源 (sample.png)
├── androidApp/                # Android 应用入口
│   └── src/main/java/com/kuikly/init/
│       ├── KuiklyRenderActivity.kt   # 承载 Kuikly 页面的主 Activity
│       ├── KRApplication.kt          # Application 单例
│       ├── adapter/                  # 平台适配器实现
│       │   ├── KRImageAdapter.kt     # 图片加载 (Glide)
│       │   ├── KRLogAdapter.kt       # 日志
│       │   ├── KRRouterAdapter.kt    # 页面导航
│       │   ├── KRThreadAdapter.kt    # 线程池
│       │   ├── KRFontAdapter.kt      # 自定义字体
│       │   ├── KRColorParserAdapter.kt # 颜色 token 解析
│       │   └── KRUncaughtExceptionHandlerAdapter.kt
│       └── module/                   # 原生端桥接模块
│           ├── KRBridgeModule.kt      # Android 原生桥接实现
│           └── KRShareModule.kt       # 分享模块 (桩)
├── iosApp/                    # iOS 应用入口 (Swift/SwiftUI)
│   └── iosApp/
│       ├── iOSApp.swift / ContentView.swift
│       ├── KuiklyRenderViewPage.swift  # SwiftUI 包装器
│       └── KuiklyExpand/              # 原生 ObjC 桥接 + 适配器
├── ohosApp/                   # HarmonyOS 应用入口 (ArkTS/ETS)
│   └── entry/src/main/ets/kuikly/
│       ├── KuiklyViewDelegate.ets     # 页面/模块注册
│       ├── MyNativeManager.ets        # NAPI 初始化
│       ├── adapter/                   # OHOS 适配器 (Log, Router)
│       ├── modules/                   # OHOS 桥接模块
│       └── components/                # 自定义视图 (KRMyView)
├── buildSrc/                  # 构建辅助 (Version, BuildPlugin)
├── gradle/libs.versions.toml  # 版本目录
└── static_server/             # 用于 Kuikly 调试的 Node.js 开发服务器
```

## 核心框架概念

**Kuikly 页面**：每个页面是一个继承自 `ComposeContainer` 并使用 `@Page("pageName")` 注解的类。页面在构建时通过 KSP 注册。页面名称是路由器使用的路由标识符。

**BasePager**：抽象基类 (`shared/.../base/BasePager.kt`)，继承自 `ComposeContainer`。提供夜间模式支持、`BridgeModule` 接入，并禁用调试 UI 检查器。所有应用页面都应继承此类。

**BridgeModule** (`HRBridgeModule`)：核心原生桥接。在 `shared/.../base/BridgeModule.kt` 中声明（Kotlin 侧），并在各平台原生实现：
- Android: `androidApp/.../module/KRBridgeModule.kt`
- iOS: `iosApp/.../Modules/HRBridgeModule.m`
- OHOS: `ohosApp/.../modules/KRBridgeModule.ets`

Kotlin 侧的 `BridgeModule` 通过 `toNative()`/`syncCallNativeMethod()` 调用原生方法。原生实现使用 `KuiklyRenderBaseModule.call(method, params, callback)`。两侧的方法名必须匹配。

**Pager/Module 访问**：使用 `pagerId` 扩展或 `LocalActivity.current.getPager()` 获取当前 pager，然后通过 `acquireModule<ModuleName>(MODULE_NAME)` 访问模块。

**平台适配器**：在启动时通过 `KuiklyRenderAdapterManager` (Android) 或 `KuiklyRenderBridge` (iOS) 注册。每个平台提供图片加载、日志、路由、线程、字体和颜色解析的具体实现。

## 构建变体与 Kotlin 版本

- **标准构建** (`build.gradle.kts` / `settings.gradle.kts`): Kotlin `2.1.21`, Kuikly `2.7.0-2.1.21`
- **OHOS 构建** (`build.ohos.gradle.kts` / `settings.ohos.gradle.kts`): Kotlin `2.0.21-KBA-010`, Kuikly `2.7.0-2.0.21-ohos`

Kuikly 版本遵循 `${KUIKLY_VERSION}-${KOTLIN_VERSION}` 的模式。

## 原生桥接方法约定

桥接方法通过字符串名称分发。Kotlin 侧的 `BridgeModule` 定义常量 (`OPEN_PAGE`、`CLOSE_PAGE`、`SSO_REQUEST` 等)，这些常量必须与各平台原生模块 `call()` 方法中的 `when` 分支匹配。

同步调用使用 `syncCallNativeMethod()` → `toNative(sync=true)`；异步调用使用 `callNativeMethod()` → `toNative(sync=false)` 配合 `CallbackFn`。

## 开发约定

- 新增 Kuikly 页面：在 `shared/src/commonMain` 中创建一个继承 `BasePager` 并使用 `@Page("name")` 注解的类
- 新增原生桥接方法：在 Kotlin `BridgeModule` 中添加常量，并在三个平台模块实现中添加对应的 `when` 分支
- 平台特定代码放在平台应用模块 (`androidApp`、`iosApp`、`ohosApp`) 中，不要放在 `shared` 的 source sets 里
- JS 输出文件名为 `nativevue2.js` (在 `shared/build.gradle.kts` 的 webpackTask 和 `KuiklyConfig` 中都有配置)

## 用户偏好

- 命名风格：直接、功能描述型，不整虚的
- 不喜欢过度设计，务实优先
- 习惯先做实再抽象，不搞预先过度抽象
