# common:widget 模块

## 模块定位

UI 组件 + 桥接模块，提供 Kuikly 页面基类、共享 UI 组件库和核心桥接模块。是整个 Kuikly 应用的**UI 基础设施层**。

## 核心职责

1. **页面基类**：`BasePager` 封装夜间模式、BridgeModule 注入、调试检查器禁用
2. **共享 UI 组件**：`BasicWidget` 提供 Modifier 扩展 + 通用组件
3. **核心桥接**：`BridgeModule` 封装 30+ 原生桥接方法
4. **桥接扩展**：`IPagerIdKtx` 提供 pagerId 扩展访问

## 详细目录结构

```
common/widget/
├── build.gradle.kts              # 标准构建配置（KMP + Kuikly + Compose + KSP）
├── build.ohos.gradle.kts         # OHOS 构建配置
└── src/
    ├── commonMain/kotlin/com/kuikly/init/common/widget/
    │   ├── BasePager.kt          # 📌 页面基类（夜间模式 + BridgeModule 注入 + 禁用调试检查器）
    │   ├── BasicWidget.kt        # 📌 共享 UI 组件库（扩展函数 + 手势 + Button/TextField/Modal/ComposeNavigationBar）
    │   ├── BridgeModule.kt       # 📌 核心桥接模块（30+ 方法，6 个 Deprecated）
    │   ├── IPagerIdKtx.kt        # 📌 pagerId 扩展（bridgeModule 访问 + setTimeout）
    │   └── Utils.kt              # 📌 bridgeModule 访问/日志/价格转换
    └── ohosArm64Main/
        └── BasicWidget.kt           — 简化版（部分 Compose 扩展 OHOS 不可用）
```

## 核心类说明

### BasePager.kt

📌 所有 Kuikly 页面的基类。

```kotlin
abstract class BasePager : ComposeContainer()
```

**功能：**
- **夜间模式**：通过 `observable` 属性 + `themeDidChanged` 回调跟踪主题
- **BridgeModule 注入**：`createExternalModules()` 自动注册 `BridgeModule`
- **禁用调试检查器**：`debugUIInspector()` 返回 `false`
- **页面参数**：从 `pageData.params` 读取 `isNightMode`

### BridgeModule.kt

📌 核心桥接模块，继承 `Module()`，MODULE_NAME = "HRBridgeModule"。

**方法总览（30+）：**

| 类别 | 方法 | 说明 |
|------|------|------|
| 页面导航 | `closePage()` | 关闭当前页面 |
| 页面导航 | `openPage(url, closeCurPage, closeSamePage, userData, callback)` | 打开页面 |
| 网络 | `ssoRequest(cmd, reqParams, callback)` | 异步网络请求 |
| 网络 | `ssoRequest(cmd, reqParams): JSONObject?` | suspend 版本 |
| 网络 | `qqLiveSSORequest(service, method, reqParams, callback)` | QQ Live 请求 |
| 上报 | `reportDT(eventCode, data)` | 灯塔上报 |
| 上报 | `reportRealTime(eventCode, data)` | 实时上报 |
| 上报 | `reportPageCostTimeFor{Cache,Success,Error}()` | 页面耗时上报 |
| 缓存 | `getCachedFromNative(key): String` | 同步获取缓存 |
| 缓存 | `fetchCachedFromNative(key, callback)` | 异步获取缓存 |
| 缓存 | `setCachedToNative(key, value, callback?)` | 写入缓存 |
| 时间 | `currentTimeStamp(): Long` | 同步获取时间戳（毫秒） |
| 时间 | `dateFormatter(timeStamp, format): String` | 同步格式化时间 |
| 时间 | `localServeTime(cb)` / `localServeTime(): JSONObject?` | 本地服务器时间 |
| 日志 | `log(content)` | 原生日志输出 |
| 其他 | `openSelectAddressView(addressData, callback)` | 选择地址 |
| 其他 | `openApplySampleSuccessPage(...)` | 申请样品成功页 |
| 其他 | `preDownloadImage/PAG/APNGResource(url)` | 预下载资源 |
| 其他 | `updateOfflineIfNeed(bid)` | 更新离线包 |
| 其他 | `showSignJumpAlert(params): String` | 签到跳转弹窗 |
| 其他 | `humanVerification(params, callback?): String` | 人机验证 |
| 其他 | `urlEncode/Decode(string): String` | URL 编解码 |

**Deprecated 委托方法（6 个）：**

| 旧方法 | 新方法 | 说明 |
|--------|--------|------|
| `copyToPasteboard` | `Clipboard.copyText` | 剪贴板操作委托给平台能力 |
| `callPhone` | `Phone.call` | 拨号委托给平台能力 |
| `toast` | `Toast.show` | Toast 委托给平台能力 |
| `showAlert` | `Dialog` | 对话框委托给平台能力 |
| `closeKeyboard` | `Keyboard.hide` | 键盘控制委托给平台能力 |

### BasicWidget.kt

📌 共享 UI 组件库，提供：

**Modifier 扩展函数：**
- `backgroundColor`, `margin`, `padding`, `height`, `width`, `offset`, `borderRadius`, `changeAlpha`

**手势/事件：**
- `willAppear`, `touchListener`, `appearPercentage`

**通用组件：**
- `Button`, `TextField`, `Modal`, `ComposeNavigationBar`

### IPagerIdKtx.kt

📌 pagerId 扩展，简化 BridgeModule 访问：

```kotlin
// 新方式（推荐）
bridgeModule.reportPageCostTimeForError()

// 旧方式
Utils.bridgeModule(pagerId).reportPageCostTimeForError()
```

### Utils.kt

📌 BridgeModule 访问工具：

| 方法 | 说明 |
|------|------|
| `bridgeModule(pager: String): BridgeModule` | 根据 pagerId 获取 BridgeModule |
| `currentBridgeModule(): BridgeModule` | 获取当前页面的 BridgeModule |
| `logToNative(pagerId/content)` | 日志输出 |
| `convertToPriceStr(price: Long): String` | 分转元价格转换 |

## 依赖

| 依赖 | 类型 | 说明 |
|------|------|------|
| `com.tencent.kuikly-open:core` | api | Kuikly 核心 |
| `com.tencent.kuikly-open:core-annotations` | api | Kuikly 注解 |
| `com.tencent.kuikly-open:compose` | api | Kuikly Compose |
| `:common:base` | implementation | 平台能力抽象层 |
| `io.insert-koin:koin-core:4.0.1` | implementation | Koin DI |

## 构建配置

- 目标平台：Android + iOS（X64 / Arm64 / SimulatorArm64）+ OHOS（独立构建）
- 资源类：`WidgetMR`（前缀 `widget_`）
- 插件：`multiplatform`, `android.library`, `ksp`, `kuiklybase.resource.generator`, `jetcompose`, `plugin.compose`

## 开发约定

- 新增页面：继承 `BasePager`，无需手动注入 BridgeModule
- 新增桥接方法：在 `BridgeModule` 添加常量 + 对应方法，三平台原生模块同步实现
- 访问桥接：通过 `bridgeModule` 扩展属性（无需传递 pagerId）
- UI 组件：优先使用 `BasicWidget` 中的共享组件
- Deprecated 方法：保留旧方法以兼容，内部委托给新的平台能力 API
