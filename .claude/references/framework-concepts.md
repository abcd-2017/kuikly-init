# 核心框架概念

## Kuikly 页面

每个页面是一个继承自 `ComposeContainer` 并使用 `@Page("pageName")` 注解的类。页面在构建时通过 KSP 注册。页面名称是路由器使用的路由标识符。

## BasePager

抽象基类 (`shared/.../base/BasePager.kt`)，继承自 `ComposeContainer`。提供夜间模式支持、`BridgeModule` 接入，并禁用调试 UI 检查器。**所有应用页面都应继承此类**。

## BridgeModule（HRBridgeModule）

核心原生桥接。在 `shared/.../base/BridgeModule.kt` 中声明（Kotlin 侧），并在各平台原生实现：
- Android: `androidApp/.../module/KRBridgeModule.kt`
- iOS: `iosApp/.../Modules/HRBridgeModule.m`
- OHOS: `ohosApp/.../modules/KRBridgeModule.ets`

Kotlin 侧的 `BridgeModule` 通过 `toNative()`/`syncCallNativeMethod()` 调用原生方法。原生实现使用 `KuiklyRenderBaseModule.call(method, params, callback)`。**两侧的方法名必须匹配**。

## Pager/Module 访问

使用 `pagerId` 扩展或 `LocalActivity.current.getPager()` 获取当前 pager，然后通过 `acquireModule<ModuleName>(MODULE_NAME)` 访问模块。

## 平台适配器

在启动时通过 `KuiklyRenderAdapterManager` (Android) 或 `KuiklyRenderBridge` (iOS) 注册。每个平台提供图片加载、日志、路由、线程、字体和颜色解析的具体实现。

## 原生桥接方法约定

桥接方法通过字符串名称分发。Kotlin 侧的 `BridgeModule` 定义常量 (`OPEN_PAGE`、`CLOSE_PAGE`、`SSO_REQUEST` 等)，这些常量必须与各平台原生模块 `call()` 方法中的 `when` 分支匹配。

- **同步调用**：`syncCallNativeMethod()` → `toNative(sync=true)`
- **异步调用**：`callNativeMethod()` → `toNative(sync=false)` 配合 `CallbackFn`
