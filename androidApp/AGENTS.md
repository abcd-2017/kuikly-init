# androidApp 模块

## 模块定位

Android 应用入口模块，是 Kuikly 渲染引擎在 Android 平台的**宿主容器**。负责承载 Kuikly 页面、注册平台适配器、实现原生桥接。

## 核心职责

1. **页面容器**：`KuiklyRenderActivity` 是承载 Kuikly 页面的 AppCompatActivity
2. **适配器注册**：在启动时通过 `KuiklyRenderAdapterManager` 注册所有平台适配器
3. **原生桥接实现**：实现 Kotlin 侧 `BridgeModule` 声明的所有方法
4. **生命周期管理**：将 Activity 生命周期事件传递给 Kuikly 渲染引擎

## 详细目录结构

```
androidApp/
├── build.gradle.kts              # Android 应用配置 (compileSdk=34, minSdk=23)
└── src/main/
    ├── AndroidManifest.xml       # 应用清单：INTERNET 权限、沉浸式、networkSecurityConfig
    ├── java/com/kuikly/init/
    │   ├── KRApplication.kt      # 📌 Application 单例：全局 Context 持有
    │   ├── KuiklyRenderActivity.kt # 📌 主 Activity：Kuikly 页面容器 + 适配器初始化
    │   ├── SkinIniFile.kt        # INI 文件解析器：用于颜色 token 配置加载
    │   ├── adapter/              # 📌 平台适配器实现（7 个）
    │   │   ├── KRImageAdapter.kt           # 图片加载适配器 (Glide)
    │   │   ├── KRLogAdapter.kt             # 日志适配器 (Android Log)
    │   │   ├── KRRouterAdapter.kt          # 页面导航适配器 (Activity 跳转)
    │   │   ├── KRThreadAdapter.kt          # 线程池适配器 (固定 2 线程)
    │   │   ├── KRFontAdapter.kt            # 自定义字体适配器 (Typeface)
    │   │   ├── KRColorParserAdapter.kt     # 颜色 token 解析适配器 (INI)
    │   │   └── KRUncaughtExceptionHandlerAdapter.kt  # 异常处理适配器
    │   └── module/               # 📌 原生桥接模块实现
    │       ├── KRBridgeModule.kt  # 核心桥接模块 (MODULE_NAME="HRBridgeModule")
    │       └── KRShareModule.kt   # 分享模块 (桩模块，待实现)
    └── res/
        ├── layout/activity_hr.xml        # Kuikly 渲染 Activity 布局 (container + loading + error)
        ├── values/styles.xml             # AppCompat NoActionBar 主题
        └── xml/network_security_config.xml # 网络安全配置
```

## 核心类说明

### KuiklyRenderActivity
- 继承 `AppCompatActivity`，实现 `KuiklyRenderViewBaseDelegatorDelegate`
- 使用 `KuiklyRenderViewBaseDelegator` 管理 Kuikly 渲染引擎生命周期
- **核心流程**：
  1. `onCreate()` → 设置布局、沉浸式模式、attach 渲染引擎
  2. `registerExternalModule()` → 注册 `KRBridgeModule` 和 `KRShareModule`
  3. `registerExternalRenderView()` → 注册自定义 View（当前为空）
  4. `onPause/onResume/onDestroy()` → 同步引擎生命周期
- **适配器初始化**（`initKuiklyAdapter()`）：

| 适配器 | 实现类 | 功能 |
|--------|--------|------|
| `krImageAdapter` | `KRImageAdapter` | Glide 加载图片（base64 / http / assets / gif） |
| `krLogAdapter` | `KRLogAdapter` | Android Log.i/d/e |
| `krRouterAdapter` | `KRRouterAdapter` | Activity startActivity / finish |
| `krThreadAdapter` | `KRThreadAdapter` | 固定 2 线程池执行子线程任务 |
| `krFontAdapter` | `KRFontAdapter` | Typeface 加载自定义字体（如 "Qvideo Digit"） |
| `krColorParseAdapter` | `KRColorParserAdapter` | 从 INI 文件解析颜色 token |
| `krUncaughtExceptionHandlerAdapter` | `KRUncaughtExceptionHandlerAdapter` | Debug 抛异常 / Release 日志兜底 |

### KRBridgeModule
- 实现 `KuiklyRenderBaseModule.call(method, params, callback)`
- 通过 `when(method)` 分发调用，方法名必须与 Kotlin 侧常量一致
- **已实现**：log、toast、copyToPasteboard、closePage、openPage、localServeTime、currentTimestamp、dateFormatter
- **待实现**：ssoRequest、qqLiveSSORequest、showAlert、reportDT、reportRealtime

### KRImageAdapter
- 实现 `IKRImageAdapter` 接口
- **base64**：`Base64.decode()` + `BitmapFactory` 子线程解码，支持采样压缩
- **http/assets/file**：Glide 加载，支持 GIF、缩放、centerCrop/fitCenter
- 通过 `CustomTarget<Drawable>` 回调加载结果

## 依赖

```
project(":shared")                    # KMM 共享模块
androidx.appcompat:appcompat:1.3.1   # AppCompat 支持
com.github.bumptech.glide:glide:4.12.0  # 图片加载
androidx.recyclerview:recyclerview:1.2.1
androidx.core:core-ktx:1.6.0
androidx.dynamicanimation:dynamicanimation:1.0.0
com.squareup.picasso:picasso:2.71828  # (未使用，可清理)
```

## 构建命令

```bash
./gradlew :androidApp:assembleDebug    # Debug APK
./gradlew :androidApp:assembleRelease  # Release APK
```
