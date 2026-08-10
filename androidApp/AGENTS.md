# androidApp 模块

## 模块定位

Android 应用入口模块，是 Kuikly 渲染引擎在 Android 平台的**宿主容器**。负责承载 Kuikly 页面、注册平台适配器、实现原生桥接。

## 核心职责

1. **Application 入口**：`KRApplication` 初始化全局 Context、Koin DI 容器、运行 InitTaskRunner
2. **页面容器**：`KuiklyRenderActivity` 是承载 Kuikly 页面的 AppCompatActivity
3. **适配器注册**：在启动时通过 `KuiklyRenderAdapterManager` 注册所有平台适配器
4. **原生桥接实现**：实现 Kotlin 侧 `BridgeModule` 声明的所有方法
5. **生命周期管理**：将 Activity 生命周期事件传递给 Kuikly 渲染引擎

## 详细目录结构

```
androidApp/
├── build.gradle.kts              # Android 应用配置 (compileSdk=34, minSdk=23, targetSdk=30)
└── src/main/
    ├── AndroidManifest.xml       # 应用清单：INTERNET 权限、沉浸式、networkSecurityConfig
    ├── java/com/kuikly/init/
    │   ├── KRApplication.kt      # 📌 Application 入口：初始化 AppContext、Koin DI（注入 ContextProvider/DeviceInfo/FileSystem/NetworkMonitor/DebugModule）、运行 InitTaskRunner
    │   ├── KuiklyRenderActivity.kt # 📌 宿主 Activity：注册 KRBridgeModule + KRShareModule，companion init 中初始化 Kuikly Adapter
    │   ├── SkinIniFile.kt        # INI 文件解析器：从 assets 加载 configColor.ini
    │   ├── adapter/              # 📌 平台适配器实现（7 个）
    │   │   ├── KRImageAdapter.kt           # 图片加载适配器（Glide：base64/http/assets/file/gif，含采样压缩）
    │   │   ├── KRLogAdapter.kt             # 日志适配器（Android Log.i/d/e）
    │   │   ├── KRUncaughtExceptionHandlerAdapter.kt  # 异常处理适配器（Debug 抛异常，Release 打日志）
    │   │   ├── KRFontAdapter.kt            # 自定义字体适配器（Typeface 加载 "Qvideo Digit"）
    │   │   ├── KRColorParserAdapter.kt     # 颜色 token 解析适配器（从 configColor.ini 解析）
    │   │   ├── KRRouterAdapter.kt          # 页面导航适配器（打开/关闭 KuiklyRenderActivity）
    │   │   └── KRThreadAdapter.kt          # 线程池适配器（固定 2 线程池）
    │   └── module/               # 📌 原生桥接模块实现
    │       ├── KRBridgeModule.kt  # 核心桥接模块（MODULE_NAME="HRBridgeModule"，13 个方法）
    │       └── KRShareModule.kt   # 分享模块（MODULE_NAME="HRShareModule"，空模块，无方法）
    └── res/
        ├── layout/activity_hr.xml        # Kuikly 渲染 Activity 布局
        ├── values/styles.xml             # AppCompat NoActionBar 主题
        └── xml/network_security_config.xml # 网络安全配置
```

## 核心类说明

### KRApplication
- 继承 `Application`，应用入口
- `onCreate()` 中：
  1. `AppContext.init(this)` 初始化全局 Context
  2. `startKoin` 启动 Koin DI，注入 `ContextProvider`、`DeviceInfo`、`FileSystem`、`NetworkMonitor`、`DebugModule`
  3. `InitTaskRunner.runAll(platformTasks = listOf())` 运行初始化任务
- `companion object` 持有全局 `application` 单例

### KuiklyRenderActivity
- 继承 `AppCompatActivity`，实现 `KuiklyRenderViewBaseDelegatorDelegate`
- 使用 `KuiklyRenderViewBaseDelegator` 管理 Kuikly 渲染引擎生命周期
- **核心流程**：
  1. `onCreate()` → 设置布局、沉浸式模式、attach 渲染引擎
  2. `registerExternalModule()` → 注册 `KRBridgeModule` 和 `KRShareModule`
  3. `registerExternalRenderView()` → 注册自定义 View（当前为空）
  4. `onPause/onResume/onDestroy()` → 同步引擎生命周期
- **适配器初始化**（`initKuiklyAdapter()`，在 companion init 中调用）：

| 适配器 | 实现类 | 功能 |
|--------|--------|------|
| `krImageAdapter` | `KRImageAdapter` | Glide 加载图片（base64 / http / assets / file / gif，含采样压缩） |
| `krLogAdapter` | `KRLogAdapter` | Android Log.i/d/e |
| `krRouterAdapter` | `KRRouterAdapter` | 打开/关闭 KuiklyRenderActivity |
| `krThreadAdapter` | `KRThreadAdapter` | 固定 2 线程池执行子线程任务 |
| `krFontAdapter` | `KRFontAdapter` | Typeface 加载自定义字体（"Qvideo Digit"） |
| `krColorParseAdapter` | `KRColorParserAdapter` | 从 configColor.ini 解析颜色 token |
| `krUncaughtExceptionHandlerAdapter` | `KRUncaughtExceptionHandlerAdapter` | Debug 抛异常 / Release 日志兜底 |

### KRBridgeModule
- 实现 `KuiklyRenderBaseModule.call(method, params, callback)`
- 通过 `when(method)` 分发调用，方法名必须与 Kotlin 侧常量一致
- MODULE_NAME = "HRBridgeModule"
- **已实现（7 个）**：

| 方法 | 功能 |
|------|------|
| `closePage` | `activity?.finish()` 关闭当前页面 |
| `copyToPasteboard` | `ClipboardManager` 写入剪贴板 |
| `toast` | `Toast.makeText` 显示 Toast |
| `log` | `Log.i("KuiklyRender", content)` |
| `localServeTime` | 返回 `System.currentTimeMillis() / 1000.0` |
| `currentTimestamp` | 返回 `System.currentTimeMillis().toString()` |
| `dateFormatter` | `SimpleDateFormat` 格式化时间戳 |

- **桩方法（空实现，6 个）**：

| 方法 | 说明 |
|------|------|
| `ssoRequest` | 空 |
| `showAlert` | 解析 title/message/buttons，无实际弹窗 |
| `openPage` | 解析 url，无实际跳转 |
| `reportDT` | 空 |
| `reportRealtime` | 空 |
| `qqLiveSSORequest` | 空 |

### KRShareModule
- 空模块，仅定义 `MODULE_NAME = "HRShareModule"`
- 继承 `KuiklyRenderBaseModule`，无 `call()` 方法实现

### KRImageAdapter
- 实现 `IKRImageAdapter` 接口
- **base64**：`Base64.decode()` + `BitmapFactory` 子线程解码，支持采样压缩
- **http/assets/file**：Glide 加载，支持 GIF、缩放、centerCrop/fitCenter
- 通过 `CustomTarget<Drawable>` 回调加载结果

## 依赖

```
project(":shared")                        # KMM 共享模块
project(":common:base")                   # 公共基础模块
project(":business:initTask")             # 初始化任务模块
project(":business:debug:impl")           # Debug 业务模块
io.insert-koin:koin-android:4.0.1         # Koin DI 框架
com.github.bumptech.glide:glide:4.12.0   # 图片加载
com.github.bumptech.glide:compiler:4.12.0 # Glide 注解处理器
com.squareup.picasso:picasso:2.71828      # Picasso（历史遗留，未使用）
androidx.recyclerview:recyclerview:1.2.1
androidx.appcompat:appcompat:1.3.1       # force 1.6.1
androidx.core:core-ktx:1.6.0             # force 1.12.0
androidx.dynamicanimation:dynamicanimation:1.0.0
```

**强制版本（resolutionStrategy.force）**：fragment 1.6.2, activity 1.8.2, appcompat 1.6.1, core 1.12.0, core-ktx 1.12.0, lifecycle 2.6.2 等（解决 R8/D8 Kotlin 元数据兼容性问题）

## 构建配置

- `compileSdk = 34`, `minSdk = 23`, `targetSdk = 30`
- Java 1.8 / JVM target 1.8
- CI 签名配置（仅在 CI 环境变量存在时生效）

## 构建命令

```bash
./gradlew :androidApp:assembleDebug    # Debug APK
./gradlew :androidApp:assembleRelease  # Release APK
```
