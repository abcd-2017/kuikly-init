# iosApp 模块

## 模块定位

iOS 应用入口模块，使用 SwiftUI 作为外壳，通过 `KuiklyRenderViewController` 承载 Kuikly 页面。提供 iOS 平台的原生桥接实现和图片加载扩展。

## 核心职责

1. **SwiftUI 宿主**：`iOSApp` → `ContentView` → `KuiklyRenderViewPage` 三级包装
2. **Koin 初始化**：`KuiklyRenderViewPage` 中调用 `SetupIOSKoinKt.setupIOSKoin()` 初始化 Koin 容器
3. **页面控制器**：`KuiklyRenderViewController` 管理单个 Kuikly 页面的生命周期
4. **原生桥接**：`HRBridgeModule` 实现 Kotlin 侧声明的桥接方法
5. **路由处理**：`KRRouterHandler` 管理页面导航栈（push/pop）
6. **图片扩展**：`KuiklyRenderComponentExpandHandler` 使用 SDWebImage 加载图片

## 详细目录结构

```
iosApp/
├── Podfile                        # 📌 CocoaPods 依赖：OpenKuiklyIOSRender ~> 2.7.0, SDWebImage, shared (local path)
├── Podfile.lock                   # 依赖锁定文件
├── iosApp-Bridging-Header.h       # Swift ↔ ObjC 桥接头
├── contents.xcworkspacedata       # Xcode Workspace 配置
├── iosApp.xcodeproj/              # Xcode 项目文件
├── iosApp.xcworkspace/            # Xcode Workspace (CocoaPods)
└── iosApp/
    ├── iOSApp.swift               # 📌 SwiftUI App 入口 (@main)
    ├── ContentView.swift          # 📌 根视图：渲染 KuiklyRenderViewPage(pageName: "router")
    ├── KuiklyRenderViewPage.swift # 📌 UIViewControllerRepresentable：初始化 Koin + 创建 KuiklyRenderViewController
    ├── Info.plist                 # 应用配置
    ├── Assets.xcassets/           # 资源目录 (AppIcon, AccentColor)
    ├── Preview Content/           # SwiftUI 预览资源
    ├── FDFullscreenPopGesture/    # 第三方：全屏侧滑返回手势
    │   ├── UINavigationController+FDFullscreenPopGesture.h
    │   └── UINavigationController+FDFullscreenPopGesture.m
    └── KuiklyExpand/              # 📌 Kuikly 扩展层
        ├── KuiklyRenderViewController.h  # 页面控制器头文件
        ├── KuiklyRenderViewController.m  # 📌 页面控制器实现：生命周期 + 桥接注册
        ├── Handler/                      # 事件处理器
        │   ├── KRRouterHandler.h         # 路由处理器头文件
        │   ├── KRRouterHandler.m         # 📌 路由处理器：push/pop 导航
        │   ├── KuiklyRenderComponentExpandHandler.h  # 组件扩展头文件
        │   └── KuiklyRenderComponentExpandHandler.m  # 📌 组件扩展：SDWebImage 加载 + 颜色处理
        └── Modules/                      # 原生桥接模块
            ├── HRBridgeModule.h          # 桥接模块头文件
            └── HRBridgeModule.m          # 📌 桥接模块实现：copyToPasteboard / log
```

## 核心类说明

### iOSApp (SwiftUI)
- `@main` 入口，`WindowGroup` 包裹 `ContentView`

### ContentView
- 根视图，直接加载 Kuikly 的 `router` 页面
- `KuiklyRenderViewPage(pageName: "router", data: [:]).ignoresSafeArea()`

### KuiklyRenderViewPage : UIViewControllerRepresentable
- SwiftUI ↔ UIKit 桥接
- **Koin 初始化**：`makeUIViewController()` 中首先调用 `SetupIOSKoinKt.setupIOSKoin()` 初始化 Koin 容器与公共任务
- `makeUIViewController()` 创建 `KuiklyRenderViewController` 并嵌入 `UINavigationController`
- 导航栏隐藏（`setNavigationBarHidden(YES)`）

### KuiklyRenderViewController
- 继承 `UIViewController`，通过 `KuiklyRenderViewControllerBaseDelegator` 管理渲染引擎
- **生命周期桥接**：`viewDidLoad/WillAppear/DidAppear/WillDisappear/DidDisappear/LayoutSubviews` 全部同步到引擎
- **自定义 View**：`createLoadingView()` / `createErrorView()` 提供白底加载/错误占位
- **Framework 查找**：`fetchContextCodeWithPageName()` 返回 `"shared"` framework 名
- 集成 `FDFullscreenPopGesture` 实现全屏侧滑返回

### KRRouterHandler
- 实现 `KRRouterProtocol`，通过 `+load` 自动注册到 `KRRouterModule`
- `openPageWithName()` → push 新 `KuiklyRenderViewController`
- `closePage()` → pop 当前控制器

### KuiklyRenderComponentExpandHandler
- 实现 `KuiklyRenderComponentExpandProtocol`，通过 `+load` 自动注册
- **图片加载**：`hr_setImageWithUrl()` → `SDWebImage` 的 `sd_setImageWithURL()`
- **颜色解析**：`hr_colorWithValue()` → 返回 nil（使用默认实现）

### HRBridgeModule
- 继承 `KRBaseModule`
- **已实现（2 个）**：

| 方法 | 功能 |
|------|------|
| `copyToPasteboard:` | `UIPasteboard.generalPasteboard.string = content` 写入剪贴板 |
| `log:` | `NSLog(@"KuiklyRender:%@", content)` 日志输出 |

- **待实现**：与 Android/OHOS 侧对齐的其他桥接方法（closePage、toast、openPage、localServeTime、currentTimestamp、dateFormatter 等）

## 依赖 (CocoaPods)

```
OpenKuiklyIOSRender ~> 2.7.0   # Kuikly iOS 渲染引擎（注意：Podfile 版本为 2.7.0，实际 Kuikly 框架已升至 2.25.0）
shared (local path)            # KMM 共享模块编译产物
SDWebImage                     # 图片加载库
platform :ios, '14.1'          # 最低部署版本
```

## 构建命令

```bash
pod install                    # 安装依赖
# 通过 Xcode 或 xcodebuild 编译
./gradlew :shared:syncFramework \
    -Pkotlin.native.cocoapods.platform=IOS \
    -Pkotlin.native.cocoapods.archs=arm64
```

## 开发约定

- 页面跳转通过 Kuikly 内置 RouterModule，不直接操作 UINavigationController
- 新增桥接方法：在 `HRBridgeModule.m` 添加对应方法
- 图片加载统一走 `KuiklyRenderComponentExpandHandler`
