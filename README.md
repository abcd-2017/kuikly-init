# kuikly-init

基于 **Kuikly** 的跨端脚手架项目，支持 Android / iOS / HarmonyOS / Web 四端。抽象系统级基础能力（导航、网络、存储、日志、平台桥等），让后续业务项目开箱即用。

## 技术选型

| 维度 | 选型 | 说明 |
|------|------|------|
| 跨端框架 | [Kuikly](https://github.com/Tencent-TDS/KuiklyUI) 2.7.0 | 腾讯开源，唯一生产可用的 KMP + 鸿蒙方案 |
| UI DSL | Compose DSL (Kuikly fork) | 基于 Jetpack Compose 1.7.3，包名 `com.tencent.kuikly.compose` |
| 语言 | Kotlin 2.1.21 | Kotlin Multiplatform |
| 构建工具 | Gradle 8.5 + KSP | 标准 Gradle 构建 |
| iOS 图片 | SDWebImage | 通过组件扩展协议接入 |
| Android 图片 | Glide 4.12 | 通过适配器注册 |

## 项目结构

```
kuikly-init/
├── shared/                    # KMM 共享模块 (通用 Kotlin 代码)
│   └── src/commonMain/kotlin/com/kuikly/init/
│       ├── base/              # BasePager, BridgeModule, Utils, IPagerIdKtx
│       ├── BasicWidget.kt     # 共享 Compose 组件
│       ├── RouterPage.kt      # @Page("router") — 入口/路由页面
│       └── ImageAdapterBenchmarks.kt  # @Page("image_adapter") — 图片测试页
├── androidApp/                # Android 应用入口
│   └── src/main/java/com/kuikly/init/
│       ├── KuiklyRenderActivity.kt   # 承载 Kuikly 页面的主 Activity
│       ├── KRApplication.kt          # Application 单例
│       ├── adapter/                  # 平台适配器 (图片/日志/路由/线程/字体/颜色/异常)
│       └── module/                   # 原生桥接模块 (KRBridgeModule, KRShareModule)
├── iosApp/                    # iOS 应用入口 (Swift/SwiftUI + ObjC 桥接)
│   └── iosApp/
│       ├── iOSApp.swift / ContentView.swift / KuiklyRenderViewPage.swift
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
├── static_server/             # Node.js 开发服务器 (Kuikly NativeVue 调试)
└── script/                    # 构建脚本
    ├── build-ohos.sh          # OHOS 一键构建 (macOS/Linux)
    └── build-ohos.bat         # OHOS 一键构建 (Windows)
```

各模块详细文档：[shared/AGENTS.md](shared/AGENTS.md) | [androidApp/AGENTS.md](androidApp/AGENTS.md) | [iosApp/AGENTS.md](iosApp/AGENTS.md) | [ohosApp/AGENTS.md](ohosApp/AGENTS.md) | [buildSrc/AGENTS.md](buildSrc/AGENTS.md) | [static_server/AGENTS.md](static_server/AGENTS.md)

## 架构设计

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

### Android

```bash
# Debug APK
./gradlew :androidApp:assembleDebug

# Release APK
./gradlew :androidApp:assembleRelease
```

### shared 模块（所有平台）

```bash
# 构建 shared 模块
./gradlew :shared:build

# iOS Framework (供 Xcode 使用)
./gradlew :shared:syncFramework \
    -Pkotlin.native.cocoapods.platform=IOS \
    -Pkotlin.native.cocoapods.archs=arm64

# JS/Webpack 构建 (输出 nativevue2.js)
./gradlew :shared:jsBrowserProductionWebpack
```

### HarmonyOS

```bash
# macOS
./script/build-ohos.sh
./script/build-ohos.sh /Applications/DevEco-Studio.app/Contents

# Windows
script\build-ohos.bat
```

> OHOS 构建使用独立 Gradle 配置：`settings.ohos.gradle.kts` + `build.ohos.gradle.kts`（Kotlin 2.0.21-KBA-010）

### iOS

```bash
cd iosApp && pod install
# 通过 Xcode 或 xcodebuild 编译
```

### 开发服务器

```bash
npm install
npm run serve   # 8017 端口 Koa 服务 + 8083 端口 whistle 代理
```

### 测试

```bash
./gradlew test
./gradlew :shared:test
```

## 核心框架概念

**Kuikly 页面**：继承 `ComposeContainer` + `@Page("pageName")` 注解的类，KSP 构建时自动注册。

**BasePager**：抽象基类，提供夜间模式、BridgeModule 注入、禁用调试检查器。所有页面继承此类。

**BridgeModule (HRBridgeModule)**：核心原生桥接。Kotlin 侧声明 + 三平台原生实现：
- Android: `androidApp/.../module/KRBridgeModule.kt`
- iOS: `iosApp/.../Modules/HRBridgeModule.m`
- OHOS: `ohosApp/.../modules/KRBridgeModule.ets`

**平台适配器**：启动时通过 `KuiklyRenderAdapterManager` 注册，提供图片加载、日志、路由、线程、字体、颜色解析的具体实现。

## 构建变体与 Kotlin 版本

| 构建类型 | Kotlin 版本 | Kuikly 版本 | 配置文件 |
|---------|-------------|-------------|---------|
| 标准构建 | 2.1.21 | 2.7.0-2.1.21 | `build.gradle.kts` / `settings.gradle.kts` |
| OHOS 构建 | 2.0.21-KBA-010 | 2.7.0-2.0.21-ohos | `build.ohos.gradle.kts` / `settings.ohos.gradle.kts` |

## 开发约定

- 新增 Kuikly 页面：在 `shared/src/commonMain` 创建继承 `BasePager` + `@Page("name")` 的类
- 新增原生桥接方法：在 Kotlin `BridgeModule` 添加常量 + 三平台模块添加 `when` 分支
- 平台特定代码放在 `androidApp`/`iosApp`/`ohosApp`，不放 `shared` 的 source sets
- JS 输出文件名 `nativevue2.js`（webpackTask + KuiklyConfig 均已配置）

## CI/CD

项目使用 GitHub Actions 进行持续集成，详见 [.github/workflows/ci.yml](.github/workflows/ci.yml)。

## License

本项目基于 [MIT License](LICENSE) 开源，请遵守以下义务：

- 在软件及其副本中保留原始的版权声明和许可声明

```
MIT License

Copyright (c) 2026 kuikly-init

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
