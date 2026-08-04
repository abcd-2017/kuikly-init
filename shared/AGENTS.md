# shared 模块

## 模块定位

KMM（Kotlin Multiplatform）共享模块，是整个项目的**跨平台业务逻辑核心**。所有共享的 Compose UI、页面定义、原生桥接声明都放在这里，编译产物供 Android / iOS / JS / HarmonyOS 各平台消费。

## 核心职责

1. **页面定义**：通过 `@Page("name")` 注解声明 Kuikly 页面，KSP 在构建时自动注册路由
2. **共享 UI**：基于 Kuikly fork 版 Jetpack Compose（`com.tencent.kuikly.compose`）编写跨平台 UI 组件
3. **原生桥接声明**：`BridgeModule` 定义 Kotlin → Native 的调用接口（同步/异步）
4. **公共资源管理**：assets 中的图片、字体等静态资源

## 详细目录结构

```
shared/
├── build.gradle.kts              # 标准构建配置 (Kotlin 2.1.21 + Kuikly 2.7.0)
├── build.ohos.gradle.kts         # OHOS 构建配置 (Kotlin 2.0.21-KBA-010)
└── src/
    ├── commonMain/               # 所有平台共享代码
    │   ├── assets/               # 打包到各平台的静态资源
    │   │   └── image_adapter/
    │   │       └── sample.png    # 图片适配器测试用基准图 (132x132)
    │   └── kotlin/com/kuikly/init/
    │       ├── BasePager.kt      # 📌 页面基类：夜间模式、BridgeModule 注入、禁用调试检查器
    │       ├── BridgeModule.kt   # 📌 核心桥接模块：Kotlin 调用 Native 的全部接口定义
    │       ├── BasicWidget.kt    # 📌 共享 UI 组件库：Button / TextField / Modal / 扩展函数
    │       ├── RouterPage.kt     # 📌 入口页面 (@Page("router"))：页面路由跳转控制台
    │       ├── ImageAdapterBenchmarks.kt  # 图片适配器基准测试页 (@Page("image_adapter"))
    │       └── base/
    │           ├── Utils.kt              # 工具对象：bridgeModule 访问、日志、价格转换
    │           └── IPagerIdKtx.kt        # pagerId 扩展：无需显式传 pagerId 的语法糖
    ├── androidMain/              # Android 专属共享代码（当前为空）
    ├── iosMain/                  # iOS 专属共享代码（当前为空）
    └── commonTest/               # 共享测试代码
```

## 核心类说明

### BasePager
- 继承自 `ComposeContainer`，所有应用页面必须继承此类
- 提供夜间模式支持（`isNightMode()` / `themeDidChanged()`）
- 通过 `createExternalModules()` 自动注入 `BridgeModule`
- 禁用调试 UI 检查器（`debugUIInspector() = false`）

### BridgeModule（MODULE_NAME = "HRBridgeModule"）
- Kotlin 侧桥接模块，通过 `toNative()` 调用各平台原生实现
- **同步调用**：`syncCallNativeMethod()` → 返回值直接获取
- **异步调用**：`callNativeMethod()` → 通过 `CallbackFn` 回调
- 核心方法常量：

| 方法名 | 功能 | 同步/异步 |
|--------|------|-----------|
| `openPage` | 打开新页面 | 异步 |
| `closePage` | 关闭当前页面 | 异步 |
| `ssoRequest` | SSO 网络请求 | 异步 / suspend |
| `qqLiveSSORequest` | QQ Live SSO 请求 | 异步 |
| `reportDT` | 灯塔数据上报 | 异步 |
| `reportRealTime` | 实时数据上报 | 异步 |
| `localServeTime` | 获取本地服务器时间 | 异步 / suspend |
| `currentTimestamp` | 获取当前时间戳（毫秒） | **同步** |
| `dateFormatter` | 日期格式化 | **同步** |
| `getCachedFromNative` | 读取本地缓存 | **同步** |
| `setCachedToNative` | 写入本地缓存 | 异步 |
| `showAlert` | 显示弹窗 | 异步 |
| `toast` | 显示 Toast | 异步 |
| `copyToPasteboard` | 复制到剪贴板 | 异步 |
| `log` | 原生日志输出 | 异步 |
| `preDownloadImage` | 预下载图片资源 | 异步 |
| `urlEncode` / `urlDecode` | URL 编解码 | **同步** |
| `closeKeyboard` | 关闭键盘 | **同步** |
| `humanVerification` | 人机验证 | **同步** |

### BasicWidget.kt
- **扩展函数**：`backgroundColor`、`margin`、`padding`、`height`、`width`、`offset`、`borderRadius` 等跨平台适配
- **手势**：`touchListener`（Down/Move/Up）、`willAppear`（类似 iOS viewWillAppear）
- **组件**：`Button`（支持绝对坐标点击）、`TextField`（键盘高度监听、占位符）、`Modal`（全屏模态）、`ComposeNavigationBar`（导航栏）
- **工具**：`appearPercentage`（曝光百分比计算）、`dtReport`（灯塔上报）

### RouterPage.kt
- 入口页面 `@Page("router", supportInLocal = true)`，提供页面路由跳转控制台
- 输入 pageName 即可跳转到对应页面，支持 URL 参数（`router&key=value`）
- 内置 SharedPreferences 缓存最近输入
- 快捷跳转 "ImageAdapter基准测试" 页面

### ImageAdapterBenchmarks.kt
- 图片适配器基准测试页 `@Page("image_adapter")`
- 测试场景：base64 图片、assets 图片、http/https 图片、gif 动图
- 验证点：intrinsicSize、Image 渲染、drawImage、capInsets

## 构建产物

| 目标平台 | 产物 | 说明 |
|---------|------|------|
| Android | AAR (via `core-render-android`) | 由 androidApp 依赖 |
| iOS | Framework (`shared.framework`) | CocoaPods 集成 |
| JS | `nativevue2.js` | NativeVue 调试 / Web 部署 |
| HarmonyOS | `libshared.so` | 通过 NAPI 加载 |

## 开发约定

- 新增页面：继承 `BasePager` + `@Page("name")` 注解
- 新增桥接方法：在 `BridgeModule` 添加常量 + 对应方法，三平台原生模块同步实现
- 业务代码 import 路径中**不出现 kuikly**
- 页面名称通过 KSP `KEY_PAGE_NAME` 参数传递
