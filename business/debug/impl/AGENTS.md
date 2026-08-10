# business:debug:impl 模块

## 模块定位

Debug 功能的**实现模块**，包含 21 个测试页面，覆盖基础渲染、平台能力、平台桥、硬件系统四大类别。通过 KSP multi-module 机制注册到 shared 模块的路由表中。

## 核心职责

1. **测试页面实现**：21 个 Kuikly 页面，每个页面测试一项特定能力
2. **页面注册**：`DebugPageConfig` 集中管理所有页面元数据
3. **服务实现**：`DebugServiceImpl` 实现 `IDebugService` 接口
4. **Koin DI**：`DebugModule` 提供依赖注入绑定
5. **共享组件**：`DebugWidgets` 提供通用 UI 组件库

## 详细目录结构

```
business/debug/impl/
├── build.gradle.kts              # 标准构建配置（KMP + Kuikly + KSP + Compose）
├── build.ohos.gradle.kts         # OHOS 构建配置
└── src/commonMain/kotlin/com/kuikly/init/business/debug/impl/
    ├── DebugModule.kt              # 📌 Koin 模块：single<IDebugService> { DebugServiceImpl() }
    ├── DebugPageConfig.kt          # 📌 21 个测试页面注册表（4 类别）
    ├── DebugServiceImpl.kt         # 📌 IDebugService 实现
    ├── home/
    │   ├── DebugHomePage.kt        — debug_home：紫色 header + LazyColumn 分类列表
    │   └── DebugNavigatePage.kt    — debug_navigate：测试 openPage/closePage/多级导航
    ├── ui/widgets/DebugWidgets.kt  — 📌 共享 widget 工具库
    ├── platform/ (8 个页面)
    │   ├── DebugClipboardPage.kt   — 使用 provideClipboard()
    │   ├── DebugCryptoPage.kt      — 使用 provideCrypto()
    │   ├── DebugDevicePage.kt      — 使用 DeviceInfo
    │   ├── DebugFilePage.kt        — 使用 provideFileSystem()
    │   ├── DebugHapticPage.kt      — 使用 provideHaptic()
    │   ├── DebugNetworkPage.kt     — 使用 provideNetworkMonitor()
    │   ├── DebugTimePage.kt        — 使用 provideNtpClock()/provideTimezone()
    │   └── DebugToastDialogPage.kt — 使用 provideToast()/provideDialog()
    ├── bridge/ (3 个页面)
    │   ├── DebugCachePage.kt       — 使用 bridgeModule 操作缓存
    │   ├── DebugNetworkRequestPage.kt — 使用 ssoRequest
    │   └── DebugReportPage.kt      — 使用 reportDT/reportRealTime
    ├── hardware/ (7 个页面)
    │   ├── DebugBiometricPage.kt   — 使用 provideBiometric()
    │   ├── DebugCameraPage.kt      — 使用 provideCamera()
    │   ├── DebugFilePickerPage.kt  — 使用 provideFilePicker()
    │   ├── DebugLocationPage.kt    — 使用 provideLocationProvider()
    │   ├── DebugPhoneKeyboardPage.kt — 使用 providePhone()/provideKeyboard()
    │   ├── DebugScannerPage.kt     — 使用 provideScanner()
    │   └── DebugSharePage.kt       — 使用 provideShare()
    └── render/ (3 个页面)
        ├── DebugImagePage.kt       — 图片渲染测试
        ├── DebugLayoutPage.kt      — 布局测试
        └── DebugTextPage.kt        — 文本渲染测试
```

## 核心类说明

### DebugPageConfig.kt

集中管理所有测试页面的元数据，按 4 个分类组织：

| 分类常量 | 中文名 | 页面数 | 页面 ID |
|---------|--------|--------|---------|
| `CATEGORY_RENDER` | 基础渲染 | 3 | `debug_text`, `debug_image`, `debug_layout` |
| `CATEGORY_PLATFORM` | 平台能力 | 8 | `debug_toast_dialog`, `debug_clipboard`, `debug_crypto`, `debug_file`, `debug_network`, `debug_device`, `debug_haptic`, `debug_time` |
| `CATEGORY_BRIDGE` | 平台桥 | 4 | `debug_navigate`, `debug_cache`, `debug_network_request`, `debug_report` |
| `CATEGORY_HARDWARE` | 硬件系统 | 6 | `debug_camera`, `debug_scanner`, `debug_file_picker`, `debug_location`, `debug_biometric`, `debug_share`, `debug_phone` |

**关键属性/方法：**

| 名称 | 说明 |
|------|------|
| `pages: List<TestPageInfo>` | 所有页面信息列表 |
| `pagesByCategory: Map<String, List<TestPageInfo>>` | 按分类分组的页面 |
| `findPage(pageId): TestPageInfo?` | 根据 pageId 查找页面 |

### DebugServiceImpl.kt

- `listTestPages()` 直接返回 `DebugPageConfig.pages`
- `navigateToTestPage(pageId)` 通过 `PagerManager` + `RouterModule.openPage()` 实现页面跳转

### DebugModule.kt

```kotlin
val DebugModule: Module = module {
    single<IDebugService> { DebugServiceImpl() }
}
```

### DebugWidgets.kt

📌 共享 widget 工具库，所有 Debug 页面通用：

| 组件 | 说明 |
|------|------|
| `DebugTestButton` | 渐变紫色按钮 |
| `DebugResultArea` | 灰色结果展示区 |
| `DebugSectionTitle` | 分组标题 |
| `DebugInfoRow` | 标签: 值 信息行 |
| `DebugCardItem` | 可点击卡片（带箭头） |
| `DebugVSpacer` | 垂直间距 |
| `DebugTextField` | 通用输入框 |
| `DebugScaffold` | 带顶部栏 + 关闭按钮的脚手架 |

## 页面注册

本模块通过 KSP multi-module 机制注册页面：

```kotlin
ksp {
    arg("moduleId", "debug_impl")
    arg("isMainModule", "false")
    arg("enableMultiModule", "true")
}
```

所有页面继承 `BasePager` + `LocalContextProvider`，使用 `@Page("pageName")` 注解。

## 依赖

| 依赖 | 类型 | 说明 |
|------|------|------|
| `com.tencent.kuikly-open:core` | api | Kuikly 核心 |
| `com.tencent.kuikly-open:core-annotations` | api | Kuikly 注解 |
| `com.tencent.kuikly-open:compose` | api | Kuikly Compose |
| `:business:debug:api` | implementation | Debug 接口模块 |
| `:common:base` | implementation | 平台能力抽象层 |
| `:common:widget` | implementation | 共享 UI 组件 |
| `io.insert-koin:koin-core:4.0.1` | implementation | Koin DI |
| `kuiklybase:resource-core:0.0.1` | implementation | KmmResource 核心 |
| `kuiklybase:resource-compose:0.0.1` | implementation | KmmResource Compose |
| `com.tencent.kuikly-open:core-ksp` | compileOnly + ksp | KSP 注解处理器 |

## 构建配置

- 目标平台：Android + iOS（X64 / Arm64 / SimulatorArm64）+ OHOS（独立构建）
- 资源类：`DebugImplMR`（前缀 `debug_impl_`）
- 插件：`multiplatform`, `android.library`, `ksp`, `kuikly`, `jetcompose`, `plugin.compose`, `kuiklybase.resource.generator`

## 开发约定

- 新增页面：继承 `BasePager` + `@Page("pageName")` 注解，在 `DebugPageConfig` 中注册
- 页面分类：在 `CATEGORY_*` 常量中添加新分类（如需要）
- UI 复用：优先使用 `DebugWidgets` 中的共享组件
- 平台能力：通过 `provide*()` 工厂函数获取，不直接引用平台 API
- 桥接调用：通过 `bridgeModule` 扩展属性访问
