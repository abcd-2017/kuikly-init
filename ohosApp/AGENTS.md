# ohosApp 模块

## 模块定位

HarmonyOS 应用入口模块，使用 ArkTS/ETS 语言开发，通过 NAPI 加载 Kuikly 共享库（`libshared.so`），是 Kuikly 在鸿蒙平台的**原生宿主**。

## 核心职责

1. **NAPI 初始化**：通过 `libentry.so` 调用 `initKuikly()` 初始化 Kuikly Native 引擎
2. **页面承载**：`Index.ets` 通过 `Kuikly` 组件渲染页面
3. **适配器注册**：`EntryAbility` 注册日志和路由适配器
4. **原生桥接**：`KRBridgeModule` 实现 Kotlin 侧声明的桥接方法
5. **自定义组件**：`KRMyView` 展示自定义原生 View 的接入方式

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
│   │   │   │   ├── napi_init.cpp # NAPI 模块注册 + initKuikly() 导出
│   │   │   │   └── types/libentry/
│   │   │   │       ├── index.d.ts
│   │   │   │       └── oh-package.json5
│   │   │   ├── ets/              # 📌 ArkTS 业务代码
│   │   │   │   ├── entryability/
│   │   │   │   │   └── EntryAbility.ets    # 📌 Ability 入口：适配器注册 + 页面加载
│   │   │   │   ├── pages/
│   │   │   │   │   └── Index.ets           # 📌 首页：Kuikly 组件容器
│   │   │   │   └── kuikly/
│   │   │   │       ├── KuiklyViewDelegate.ets  # 📌 页面/模块注册代理
│   │   │   │       ├── MyNativeManager.ets     # 📌 NAPI 初始化管理器
│   │   │   │       ├── adapter/                # 📌 平台适配器
│   │   │   │       │   ├── LogAdapter.ets      # 日志适配器 (hilog)
│   │   │   │       │   └── RouterAdapter.ets   # 路由适配器 (@ohos.router)
│   │   │   │       ├── modules/                # 📌 原生桥接模块
│   │   │   │       │   ├── KRBridgeModule.ets  # 核心桥接模块
│   │   │   │       │   └── KRMyModule.ets      # 自定义模块示例
│   │   │   │       └── components/             # 📌 自定义原生组件
│   │   │   │           └── KRMyView.ets       # 自定义 View 示例
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
  1. 设置全屏布局
  2. 注册 `LogAdapter`（hilog）和 `RouterAdapter`（@ohos.router）
  3. 加载 `pages/Index` 页面

### Index.ets
- `@Entry` 首页组件，使用 `Kuikly` 渲染组件
- 从 `router.getParams()` 获取 `pageName` 和 `pageData`
- 通过 `KuiklyViewDelegate` 注册自定义模块和 View
- 异常处理：`onRenderException` 捕获渲染异常并展示

### KuiklyViewDelegate.ets
- 继承 `IKuiklyViewDelegate`
- **自定义 View 注册**：`KRMyView` (VIEW_NAME = "KRMyView")
- **自定义模块注册**：`KRBridgeModule`、`KRMyModule`

### MyNativeManager.ets
- 继承 `KuiklyNativeManager`
- `loadNative()` 调用 `Napi.initKuikly()` 初始化引擎
- 导出全局单例 `globalNativeManager`

### KRBridgeModule.ets
- 继承 `KuiklyRenderBaseModule`，MODULE_NAME = "HRBridgeModule"
- `call()` 方法分发调用
- **已实现**：log、localServeTime、currentTimestamp、dateFormatter（空）、readAssetFile
- **待实现**：showAlert、closePage、openPage、copyToPasteboard、toast、reportDT、reportRealtime
- `syncMode() = false`（异步模式，运行在 UI 线程）

### KRMyModule.ets
- 自定义模块示例，展示数据交互模式
- `myFun1`：JSON 字符串 → JSON 对象（同步返回）
- `myFun2`：字节数组 → 字节数组（同步返回）
- `myFun3`：JSON 字符串 → 回调 JSON 对象（异步回调）
- `myFun4`：字节数组 → 回调字节（异步回调）

### KRMyView.ets
- 继承 `KuiklyRenderBaseView`，自定义原生 View 示例
- 展示 `Image` + `Text` 组合布局
- `setProp()` 接收 `message` 属性
- `createArkUIView()` 通过 `ComponentContent` 创建 ArkUI 组件

### napi_init.cpp
- NAPI 模块入口，导出 `initKuikly()` 函数
- 调用 `libshared_symbols()->kotlin.root.initKuikly()` 初始化 Kotlin 运行时
- 模块名 `entry`，对应 `import Napi from 'libentry.so'`

## 适配器

| 适配器 | 实现类 | 功能 |
|--------|--------|------|
| `krLogAdapter` | `LogAdapter` | hilog.debug/info/e |
| `krRouterAdapter` | `RouterAdapter` | @ohos.router.pushUrl/back |

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
- OHOS 构建使用独立 Gradle 配置（`settings.ohos.gradle.kts` / `build.ohos.gradle.kts`）
