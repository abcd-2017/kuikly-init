# ohosApp 模块

## 模块定位

HarmonyOS 应用入口模块，使用 ArkTS/ETS 语言开发，通过 NAPI 加载 Kuikly 共享库（`libshared.so`），是 Kuikly 在鸿蒙平台的**原生宿主**。

## 核心职责

1. **NAPI 初始化**：通过 `libentry.so` 导出 `initKuikly()` + `setupPlatform()` 两个 NAPI 方法
2. **平台服务定位**：`PlatformServiceLocator` 单例替代 Koin（ETS 无法使用 Kotlin DI）
3. **KNOI 注册**：`OHOSPlatformServiceImpl` 通过 `registerServiceProvider` 注册到 KNOI，供 Kotlin 侧调用
4. **页面承载**：`Index.ets` 通过 `Kuikly` 组件渲染页面
5. **适配器注册**：`EntryAbility` 注册日志和路由适配器
6. **原生桥接**：`KRBridgeModule`（20 个方法）+ `KRMyModule`（5 个方法，示例模块）
7. **自定义组件**：`KRMyView` 展示自定义原生 View 的接入方式

## 详细目录结构

```
ohosApp/
├── AppScope/                     # 应用级配置
│   ├── app.json5                 # 应用包名、版本等
│   └── resources/base/           # 应用级资源 (string, media)
├── hvigor/                       # Hvigor 构建配置
│   └── hvigor-config.json5
├── hvigorfile.ts                 # Hvigor 构建入口
├── build-profile.json5           # 模块构建配置
├── oh-package.json5              # OHPM 包管理配置
├── local.properties              # SDK 本地路径
├── runOhosApp.sh                 # 一键构建+安装脚本 (macOS)
├── entry/                        # 📌 主模块 (HAP 打包目标)
│   ├── build-profile.json5
│   ├── hvigorfile.ts
│   ├── oh-package.json5
│   ├── module.json5              # 模块名称、Ability 声明
│   ├── obfuscation-rules.txt     # 混淆规则
│   ├── src/
│   │   ├── main/
│   │   │   ├── cpp/              # 📌 NAPI C++ 层
│   │   │   │   ├── CMakeLists.txt
│   │   │   │   ├── napi_init.cpp # 📌 NAPI 模块注册，导出 initKuikly + setupPlatform
│   │   │   │   └── types/libentry/
│   │   │   │       ├── index.d.ts
│   │   │   │       └── oh-package.json5
│   │   │   ├── ets/              # 📌 ArkTS 业务代码
│   │   │   │   ├── entryability/
│   │   │   │   │   └── EntryAbility.ets       # 📌 Ability 入口：PlatformServiceLocator + KNOI + 注册服务 + Adapter
│   │   │   │   ├── pages/
│   │   │   │   │   └── Index.ets              # 📌 首页：Kuikly 组件容器
│   │   │   │   └── kuikly/
│   │   │   │       ├── KuiklyViewDelegate.ets  # 📌 页面/模块注册代理（KRMyView + KRBridgeModule + KRMyModule）
│   │   │   │       ├── MyNativeManager.ets     # 📌 NAPI 初始化管理器（调用 initKuikly + setupPlatform）
│   │   │   │       ├── PlatformServiceLocator.ets # 📌 单例服务定位器（替代 Koin）
│   │   │   │       ├── adapter/                # 📌 平台适配器（2 个）
│   │   │   │       │   ├── LogAdapter.ets       # 日志适配器 (hilog)
│   │   │   │       │   └── RouterAdapter.ets    # 路由适配器 (@ohos.router)
│   │   │   │       ├── components/             # 📌 自定义原生组件
│   │   │   │       │   └── KRMyView.ets        # 自定义 View 示例
│   │   │   │       ├── modules/                # 📌 原生桥接模块
│   │   │   │       │   ├── KRBridgeModule.ets   # 核心桥接模块（20 个方法）
│   │   │   │       │   └── KRMyModule.ets       # 自定义模块示例（5 个方法）
│   │   │   │       └── platform/               # 📌 平台能力实现（9 个文件）
│   │   │   │           ├── OHOSPlatformServiceImpl.ets  # 📌 平台能力服务实现（KNOI 注册）
│   │   │   │           ├── OHOSContextProvider.ets       # 上下文提供者（CacheDir/FilesDir/Asset）
│   │   │   │           ├── OHOSDeviceInfo.ets            # 设备信息（ID/OS版本/型号/时区）
│   │   │   │           ├── OHOSFileSystem.ets            # 文件系统（读/写/存在/删除）
│   │   │   │           ├── OHOSNetworkMonitor.ets        # 网络监控（连接状态/类型）
│   │   │   │           ├── OHOSNtpClient.ets             # NTP 客户端（桩）
│   │   │   │           ├── OHOSCrypto.ets                # 加解密（AES/MD5/SHA256/HMAC/Base64）
│   │   │   │           ├── OHOSScreenInfo.ets            # 屏幕信息（宽/高/DPI/密度/旋转）
│   │   │   │           └── OHOSAppInfo.ets               # 应用信息（名称/包名/版本/构建类型）
│   │   │   ├── resources/         # 模块资源 (colors, strings, media, profile)
│   │   │   └── module.json5       # 模块配置
│   │   ├── ohosTest/              # 仪器测试
│   │   └── test/                  # 单元测试
│   └── .gitignore
├── .gitignore
├── .npmrc
└── .ohpmrc
```

## 核心类说明

### EntryAbility.ets
- 继承 `UIAbility`，鸿蒙应用入口
- `onWindowStageCreate()` 中：
  1. `setWindowLayoutFullScreen(true)` 设置全屏布局
  2. `PlatformServiceLocator.getInstance().init(this.context)` 初始化平台能力服务定位器
  3. `setup("libshared.so", BuildProfile.DEBUG)` + `init()` 初始化 KNOI
  4. 创建 `OHOSPlatformServiceImpl` + `OHOSContextProvider`，通过 `registerServiceProvider("IOHOSPlatformService", false, platformService)` 注册到 KNOI
  5. 注册 `LogAdapter`（hilog）和 `RouterAdapter`（@ohos.router）
  6. `loadContent('pages/Index')` 加载首页

### Index.ets
- `@Entry` 首页组件，使用 `Kuikly` 渲染组件
- 从 `router.getParams()` 获取 `pageName` 和 `pageData`
- 通过 `KuiklyViewDelegate` 注册自定义模块和 View
- 异常处理：`onRenderException` 捕获渲染异常并展示

### KuiklyViewDelegate.ets
- 继承 `IKuiklyViewDelegate`
- **自定义 View 注册**：`KRMyView` (VIEW_NAME = "KRMyView")
- **自定义模块注册**：`KRBridgeModule` (MODULE_NAME = "HRBridgeModule")、`KRMyModule` (MODULE_NAME = "KRMyModule")

### MyNativeManager.ets
- 继承 `KuiklyNativeManager`
- `loadNative()` 调用 `Napi.initKuikly()` 初始化引擎 + `Napi.setupPlatform()` 初始化 OHOS 平台能力
- 导出全局单例 `globalNativeManager`

### PlatformServiceLocator.ets
- 📌 **单例服务定位器，替代 Koin**（ETS 无法使用 Kotlin DI 框架）
- `getInstance()` 返回全局唯一实例
- `init(context)` 初始化 `OHOSContextProvider`、`OHOSDeviceInfo`、`OHOSFileSystem`、`OHOSNetworkMonitor`
- Getter 方法：`getContextProvider()`、`getDeviceInfo()`、`getFileSystem()`、`getNetworkMonitor()`
- 未初始化时调用 getter 会抛出 Error

### KRBridgeModule.ets
- 继承 `KuiklyRenderBaseModule`，MODULE_NAME = "HRBridgeModule"
- `call()` 方法通过 `switch` 分发调用
- `syncMode() = false`（异步模式，运行在 UI 线程）
- **已实现（13 个）**：

| 方法 | 功能 |
|------|------|
| `log` | `console.log` 输出日志 |
| `localServeTime` | 返回 `Date.now() / 1000.0` |
| `currentTimestamp` | 返回 `Date.now().toString()` |
| `readAssetFile` | 通过 `fs.openSync` + `fileUri` 读取 assets 目录下的 JSON 文件 |
| `getCacheDir` | 通过 PlatformServiceLocator 获取缓存目录路径 |
| `getFilesDir` | 通过 PlatformServiceLocator 获取文件目录路径 |
| `getDeviceId` | 通过 PlatformServiceLocator 获取设备 ID |
| `getOSVersion` | 通过 PlatformServiceLocator 获取 OS 版本 |
| `getDeviceModel` | 通过 PlatformServiceLocator 获取设备型号 |
| `fileExists` | 通过 PlatformServiceLocator 判断文件是否存在 |
| `fileDelete` | 通过 PlatformServiceLocator 删除文件 |
| `isNetworkConnected` | 通过 PlatformServiceLocator 获取网络连接状态 |
| `getNetworkType` | 通过 PlatformServiceLocator 获取网络类型 |

- **桩方法（空实现，7 个）**：

| 方法 | 说明 |
|------|------|
| `showAlert` | 空 |
| `closePage` | 空（TODO） |
| `openPage` | 空（TODO） |
| `copyToPasteboard` | 空（TODO） |
| `toast` | 空（TODO） |
| `reportDT` | 空 |
| `reportRealtime` | 空 |
| `dateFormatter` | 返回空字符串（TODO） |

### KRMyModule.ets
- 自定义模块示例，展示数据交互模式
- MODULE_NAME = "KRMyModule"
- `syncMode() = false`（异步模式）
- **方法（5 个）**：

| 方法 | 功能 |
|------|------|
| `myFun1` | 接收 JSON 字符串，返回 `data` 字段值（同步返回） |
| `myFun2` | 接收 Int8Array，交换字节顺序（bytes[0]↔bytes[3], bytes[1]↔bytes[2]），同步返回 |
| `myFun3` | 接收 JSON 字符串，回调 `{"content": "data值"}`（异步回调） |
| `myFun4` | 接收数组，回调首元素（异步回调） |
| `aTestMethod` | 测试方法，回调 `{'a': Int8Array, 's': "hello"}` |

### KRMyView.ets
- 继承 `KuiklyRenderBaseView`，自定义原生 View 示例
- 展示 `Image` + `Text` 组合布局
- `setProp()` 接收 `message` 属性
- `createArkUIView()` 通过 `ComponentContent` 创建 ArkUI 组件

### OHOSPlatformServiceImpl.ets
- 📌 **平台能力服务实现**，通过 KNOI `registerServiceProvider` 注册
- Kotlin 侧通过 `getIOHOSPlatformServiceApi()` 获取此实例
- 聚合 8 个平台能力组件：`OHOSContextProvider`、`OHOSDeviceInfo`、`OHOSFileSystem`、`OHOSNetworkMonitor`、`OHOSNtpClient`、`OHOSCrypto`、`OHOSScreenInfo`、`OHOSAppInfo`
- **完整能力列表**：

| 类别 | 方法 | 状态 |
|------|------|------|
| **文件/目录** | `getCacheDirPath()` | ✅ |
| | `getFilesDirPath()` | ✅ |
| | `readAsset(path)` | ✅ |
| | `readFile(path)` | ✅ |
| | `writeFile(path, data)` | ✅ |
| | `fileExists(path)` | ✅ |
| | `fileDelete(path)` | ✅ |
| **网络** | `isNetworkConnected()` | ✅ |
| | `getNetworkType()` | ✅ |
| **设备信息** | `getDeviceId()` | ✅ |
| | `getOSVersion()` | ✅ |
| | `getDeviceModel()` | ✅ |
| | `getTimezoneId()` | ✅ |
| | `getOffsetMinutes()` | ✅ |
| | `isDaylightSaving()` | ✅ |
| | `getTimezoneAbbreviation()` | ✅ |
| **NTP** | `getServerTime(ntpServer)` | 桩 |
| | `getClockOffset(ntpServer)` | 桩 |
| **加解密** | `aesEncrypt(plaintext, key, iv)` | ✅ |
| | `aesDecrypt(ciphertext, key, iv)` | ✅ |
| | `md5(input)` | ✅ |
| | `sha256(input)` | ✅ |
| | `hmacSha256(data, key)` | ✅ |
| | `base64Encode(input)` | ✅ |
| | `base64Decode(input)` | ✅ |
| **剪贴板** | `setPasteboardText(content)` | ✅ |
| | `getPasteboardText()` | ✅ |
| | `clearPasteboard()` | ✅ |
| **Toast** | `showToast(message, duration)` | ✅ |
| **分享** | `shareText(text)` | 桩（API 12 移除） |
| | `shareLink(url, title, description)` | 桩（API 12 移除） |
| | `shareImage(localPath)` | 桩（API 12 移除） |
| | `shareFile(localPath, mimeType)` | 桩（API 12 移除） |
| **文件选择** | `pickFile(mimeType, allowMultiple)` | ✅ |
| | `pickImage(allowMultiple)` | ✅ |
| | `pickDocument(allowMultiple)` | ✅ |
| **对话框** | `showAlert(title, message, confirmText)` | ✅ |
| | `showConfirm(title, message, confirmText, cancelText, callback)` | ✅ |
| | `showActionSheet(title, message, options, callback)` | ✅ |
| **键盘** | `hideKeyboard()` | ✅ |
| | `showKeyboard()` | ✅（空操作，系统自动弹出） |
| **电话** | `callPhone(phoneNumber)` | ✅ |
| **权限** | `checkPermissionSync(tokenID, permission)` | ✅ |
| | `requestPermissions(permissions, callback)` | ✅ |
| **屏幕** | `getScreenWidth()` | ✅ |
| | `getScreenHeight()` | ✅ |
| | `getScreenDensityDpi()` | ✅ |
| | `getScreenDensity()` | ✅ |
| | `getScreenRotation()` | ✅ |
| **应用信息** | `getAppName()` | ✅ |
| | `getAppPackageName()` | ✅ |
| | `getVersionName()` | ✅ |
| | `getVersionCode()` | ✅ |
| | `getBuildType()` | ✅ |
| **生物识别** | `isBiometricSupported()` | ✅ |
| | `getSupportedBiometricTypes()` | ✅ |
| | `authenticate(title, cancelText, callback)` | ✅ |
| **震动反馈** | `hapticImpact(style)` | ✅ |
| | `hapticNotification(type)` | ✅ |
| | `hapticSelectionChanged()` | ✅ |
| | `hapticStop()` | ✅ |
| **地理位置** | `requestLocationPermission()` | ✅ |
| | `getCurrentLocation(accuracy, callback)` | ✅ |
| **扫码** | `startScan(callback)` | 桩（API 12 移除） |
| | `stopScan()` | 桩（API 12 移除） |
| **电池** | `getBatteryLevel()` | ✅ |
| | `isCharging()` | ✅ |
| | `isLowBattery()` | ✅ |
| **系统设置** | `openSystemSettings()` | 桩 |
| | `openAppSettings()` | 桩 |

### platform/ 目录 — 平台能力组件

| 文件 | 职责 |
|------|------|
| `OHOSContextProvider.ets` | 上下文提供者：getCacheDirPath / getFilesDirPath / readAsset |
| `OHOSDeviceInfo.ets` | 设备信息：getDeviceId / getOSVersion / getDeviceModel / 时区系列（TimezoneId/OffsetMinutes/DaylightSaving/Abbreviation） |
| `OHOSFileSystem.ets` | 文件系统：readFile / writeFile / exists / delete |
| `OHOSNetworkMonitor.ets` | 网络监控：isConnected / getNetworkType |
| `OHOSNtpClient.ets` | NTP 客户端：getServerTime / getClockOffset（桩） |
| `OHOSCrypto.ets` | 加解密：AES（CBC/ECB）/ MD5 / SHA256 / HMAC-SHA256 / Base64 |
| `OHOSScreenInfo.ets` | 屏幕信息：getScreenWidth / Height / DensityDpi / Density / Rotation |
| `OHOSAppInfo.ets` | 应用信息：getAppName / PackageName / VersionName / VersionCode / BuildType |

### napi_init.cpp
- NAPI 模块入口，模块名 `entry`
- 导出两个 NAPI 方法：
  - `initKuikly`：调用 `libshared_symbols()->kotlin.root.initKuikly()` 初始化 Kotlin 运行时，返回 int32 句柄
  - `setupPlatform`：调用 `libshared_symbols()->kotlin.root.com.kuikly.init.platform.setupOHOSPlatform()` 初始化 OHOS 平台能力（Koin + InitTask）
- 通过 `RegisterEntryModule` 构造函数属性自动注册

## 适配器

| 适配器 | 实现类 | 功能 |
|--------|--------|------|
| `krLogAdapter` | `LogAdapter` | hilog.debug/info/e |
| `krRouterAdapter` | `RouterAdapter` | @ohos.router.pushUrl/back |

## 依赖 (OHPM)

```
@kuikly-open/render 2.7.0    # Kuikly 鸿蒙渲染引擎
@kuiklybase/knoi 0.0.4       # Kotlin ↔ ArkTS 互调框架
@ohos/apng 1.1.4             # APNG 图片支持
```

## 环境准备

首次克隆项目后，需创建 `local.properties`（该文件被 `.gitignore` 排除，不会提交）：

```bash
cp local.properties.example local.properties
```

模板中的默认值适用于标准 DevEco Studio 安装路径。如果装在其他位置，需同步修改 `kuikly.*` 相关路径。

## 构建命令

```bash
# macOS 一键构建
./script/build-ohos.sh
./script/build-ohos.sh /Applications/DevEco-Studio.app/Contents

# Windows 一键构建
script\build-ohos.bat
script\build-ohos.bat "C:\Program Files\Huawei\DevEco Studio"

# 仅安装运行（需先构建 shared）
./ohosApp/runOhosApp.sh
```

## 构建流程

```
1. Gradle 构建 shared 模块 (ohosArm64) → libshared.so
2. ohpm install（安装 ohosApp 依赖）
3. Hvigor 同步项目
4. Hvigor 打包 HAP
5. (可选) hdc 安装到设备并启动
```

## 开发约定

- 新增桥接方法：在 `KRBridgeModule.ets` 的 `call()` 添加 `case` 分支
- 新增自定义 View：继承 `KuiklyRenderBaseView` + 在 `KuiklyViewDelegate` 注册
- 新增自定义模块：继承 `KuiklyRenderBaseModule` + 在 `KuiklyViewDelegate` 注册
- 新增平台能力：在 `OHOSPlatformServiceImpl.ets` 添加方法 + 在对应 platform/ 组件中实现
- OHOS 构建使用独立 Gradle 配置（`settings.ohos.gradle.kts` / `build.ohos.gradle.kts`）
