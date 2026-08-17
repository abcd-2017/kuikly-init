# business:debug:api 模块

## 模块定位

Debug 功能的**接口模块**，定义 Debug 服务契约。保持轻量，不依赖 Kuikly 框架，可被任意模块安全引用。

## 核心职责

1. **服务接口定义**：声明 `IDebugService` 接口，供 impl 模块实现、其他模块调用
2. **数据模型定义**：提供 `TestPageInfo` 数据类，统一测试页面元数据结构
3. **契约隔离**：api/impl 分离，调用方仅依赖接口，不感知实现细节

## 详细目录结构

```
business/debug/api/
├── build.gradle.kts              # KMP 库配置（Android + iOS，无 OHOS）
└── src/commonMain/kotlin/com/kuikly/init/business/debug/api/
    └── IDebugService.kt          # 📌 服务接口 + TestPageInfo 数据类
```

## 核心类说明

### IDebugService.kt

#### IDebugService 接口

| 方法 | 说明 |
|------|------|
| `listTestPages(): List<TestPageInfo>` | 返回所有 debug 测试页面列表 |
| `navigateToTestPage(pageId: String)` | 按 pageId 打开指定测试页面 |

#### TestPageInfo 数据类

```kotlin
data class TestPageInfo(
    val pageId: String,       // 页面唯一标识（与 @Page 注解名对应）
    val title: String,        // 页面标题
    val description: String,  // 页面描述
    val category: String      // 所属分类
)
```

## 依赖

| 依赖 | 说明 |
|------|------|
| `io.insert-koin:koin-core:4.0.1` | Koin DI（接口标记用） |

> **注意**：本模块**无 Kuikly @Page/Compose 依赖**，保持轻量，可被 impl 模块和 shared 模块安全引用。

## 构建配置

- 目标平台：Android + iOS（X64 / Arm64 / SimulatorArm64）
- 资源类：`DebugApiMR`（前缀 `debug_api_`）
- 插件：`multiplatform`, `android.library`, `kuiklybase.resource.generator`

## 开发约定

- 仅定义接口和数据模型，不包含实现逻辑
- 不依赖 Kuikly 框架 API（@Page、Compose 等）
- 新增方法需同步更新 impl 模块的 `DebugServiceImpl`
