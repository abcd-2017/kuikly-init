# common:base 模块

## 模块定位

平台能力抽象层，通过 `expect/actual` 模式定义 27+ 设备/系统能力的跨平台接口。业务代码通过 `provide*()` 工厂函数获取平台能力实例，无需关心平台差异。

## 核心职责

1. **平台能力抽象**：定义跨平台接口（expect），各平台提供实际实现（actual）
2. **工厂函数解耦**：`provide*()` 函数隔离实例创建逻辑
3. **Koin 访问点**：`KoinContext` expect object 统一依赖获取
4. **数据类型定义**：sealed class、enum 等跨平台数据类型

## 详细目录结构

```
common/base/
├── build.gradle.kts              # 标准构建配置（KMP）
├── build.ohos.gradle.kts         # OHOS 构建配置
└── src/
    ├── commonMain/kotlin/com/kuikly/init/common/base/
    │   ├── KoinContext.kt              # 📌 expect object（Koin 访问点）
    │   └── platform/ (27+ 能力域)
    │       ├── AppContext.kt           — expect object
    │       ├── ContextProvider.kt      — expect class
    │       ├── DeviceInfo.kt           — expect class
    │       ├── FileSystem.kt           — expect class + provideFileSystem()
    │       ├── NetworkMonitor.kt       — expect class + provideNetworkMonitor() + NetworkType enum
    │       ├── app/AppInfo.kt          — expect class + provideAppInfo()
    │       ├── battery/Battery.kt      — BatteryInfo + provideBattery()
    │       ├── biometric/Biometric.kt  — BiometricType/Result + provideBiometric()
    │       ├── camera/Camera.kt        — CapturedMedia + provideCamera()
    │       ├── clipboard/Clipboard.kt  — provideClipboard()
    │       ├── crypto/Crypto.kt        — provideCrypto()
    │       ├── dialog/Dialog.kt        — DialogAction + provideDialog()
    │       ├── haptic/Haptic.kt        — HapticStyle/Notification + provideHaptic()
    │       ├── keyboard/Keyboard.kt    — provideKeyboard()
    │       ├── location/Location.kt    — Location/LocationAccuracy/LocationProvider + provideLocationProvider()
    │       ├── mediapicker/MediaPicker.kt — PickedMedia/MediaMediaType + provideMediaPicker()
    │       ├── permission/Permission.kt   — PermissionStatus + providePermission()
    │       ├── phone/Phone.kt             — providePhone()
    │       ├── picker/FilePicker.kt       — PickedFile + provideFilePicker()
    │       ├── scan/Scanner.kt            — ScanResult + provideScanner()
    │       ├── screen/ScreenInfo.kt       — ScreenInfo + provideScreenInfo()
    │       ├── settings/Settings.kt       — provideSettings()
    │       ├── share/Share.kt             — ShareContent sealed + provideShare()
    │       ├── time/NtpClock.kt           — provideNtpClock()
    │       ├── time/Timezone.kt           — provideTimezone()
    │       └── toast/Toast.kt             — ToastDuration + provideToast()
    ├── androidMain/ — 所有 actual 实现
    │   └── platform/
    │       ├── ActivityResultBridge.kt   — Activity 结果桥接
    │       ├── ContextProvider.kt        — actual 实现
    │       ├── PermissionResultBridge.kt — 权限结果桥接
    │       └── ... (各能力域 actual)
    ├── iosMain/ — 所有 actual 实现
    │   └── platform/
    │       ├── ContextProvider.kt        — actual 实现
    │       ├── crypto/PureKotlinCrypto.kt — 纯 Kotlin 加解密实现
    │       └── ... (各能力域 actual)
    └── ohosArm64Main/ — 所有 actual 实现
        ├── PlatformServices.kt           # 📌 KNOI 服务接口（ArkTS 实现）
        └── platform/
            ├── ContextProvider.kt        — actual 实现
            └── ... (各能力域 actual)
```

## 核心类说明

### KoinContext.kt

```kotlin
expect object KoinContext {
    inline fun <reified T : Any> get(qualifier: Any? = null, noinline parameters: (() -> Any)? = null): T
    fun <T : Any> get(clazz: KClass<*>, qualifier: Any? = null, parameters: (() -> Any)? = null): T
}
```

各平台提供 actual 实现，OHOS 平台使用桩实现。

### 能力域总览

| 能力域 | expect 类/函数 | 说明 |
|--------|---------------|------|
| 应用上下文 | `AppContext` | 全局 Context 初始化 |
| 上下文提供者 | `ContextProvider` | 平台上下文包装 |
| 设备信息 | `DeviceInfo` | 设备 ID、OS 版本、屏幕参数 |
| 文件系统 | `FileSystem` + `provideFileSystem()` | 文件读写、目录操作 |
| 网络监听 | `NetworkMonitor` + `provideNetworkMonitor()` | 连接状态、网络类型 |
| 应用信息 | `AppInfo` + `provideAppInfo()` | 应用名称、包名、版本 |
| 电池 | `Battery` + `provideBattery()` | 电量、充电状态 |
| 生物识别 | `Biometric` + `provideBiometric()` | 指纹、人脸认证 |
| 相机 | `Camera` + `provideCamera()` | 拍照、录像 |
| 剪贴板 | `Clipboard` + `provideClipboard()` | 复制、粘贴 |
| 加解密 | `Crypto` + `provideCrypto()` | AES/MD5/SHA256/Base64 |
| 对话框 | `Dialog` + `provideDialog()` | Alert/Confirm/ActionSheet |
| 震动反馈 | `Haptic` + `provideHaptic()` | 冲击/通知/选择反馈 |
| 键盘 | `Keyboard` + `provideKeyboard()` | 键盘显示/隐藏 |
| 定位 | `Location` + `provideLocationProvider()` | 经纬度、权限 |
| 媒体选择 | `MediaPicker` + `provideMediaPicker()` | 图片/视频选择 |
| 权限 | `Permission` + `providePermission()` | 权限检查/申请 |
| 电话 | `Phone` + `providePhone()` | 拨号 |
| 文件选择 | `FilePicker` + `provideFilePicker()` | 文件/图片选择 |
| 扫码 | `Scanner` + `provideScanner()` | 扫码 |
| 屏幕信息 | `ScreenInfo` + `provideScreenInfo()` | 宽高/DPI/密度/旋转 |
| 系统设置 | `Settings` + `provideSettings()` | 打开系统设置 |
| 分享 | `Share` + `provideShare()` | 文本/链接/图片/文件分享 |
| NTP 时钟 | `NtpClock` + `provideNtpClock()` | 服务器时间 |
| 时区 | `Timezone` + `provideTimezone()` | 时区 ID/偏移/夏令时 |
| Toast | `Toast` + `provideToast()` | Toast 提示 |

### ShareContent 密封类

```kotlin
sealed class ShareContent {
    data class Text(val text: String) : ShareContent()
    data class Link(val url: String, val title: String?, val description: String?) : ShareContent()
    data class Image(val localPath: String) : ShareContent()
    data class File(val localPath: String, val mimeType: String?) : ShareContent()
}
```

### PlatformServices.kt（OHOS）

📌 OHOS 平台能力服务接口（KNOI），通过 `@ServiceConsumer` 标记，由 ArkTS 侧实现。包含 30+ 方法覆盖：文件系统、设备信息、加解密、剪贴板、Toast、分享、对话框、文件选择、键盘、电话、权限、屏幕、生物识别、震动、定位、扫码、电池、系统设置、相机、媒体选择器。

## 依赖（commonMain）

| 依赖 | 类型 | 说明 |
|------|------|------|
| `io.insert-koin:koin-core` | implementation | Koin DI 容器 |
| `org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1` | api | Kotlin 协程 |

## 依赖（androidMain 额外）

| 依赖 | 说明 |
|------|------|
| `androidx.appcompat:appcompat:1.6.1` | AppCompat |
| `org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1` | 协程 |
| `androidx.biometric:biometric:1.1.0` | 生物识别 |

## 构建配置

- 目标平台：Android + iOS（X64 / Arm64 / SimulatorArm64）+ OHOS（独立构建）
- 插件：`multiplatform`, `android.library`

## 开发约定

- 新增能力域：在 `commonMain/platform/` 下创建 expect 类 + `provide*()` 函数
- 三端实现：每个 expect 必须在 androidMain / iosMain / ohosArm64Main 中提供 actual
- 工厂函数：使用 `provide*()` 命名约定，返回 expect 类型
- 数据类型：sealed class / enum 定义在 commonMain，平台无关
- OHOS 特殊：部分能力通过 `PlatformServices` KNOI 接口委托给 ArkTS
