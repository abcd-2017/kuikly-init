# business:initTask 模块

## 模块定位

启动初始化任务框架，提供跨平台的初始化任务注册和执行能力。各模块通过实现 `InitTask` 接口定义初始化逻辑，平台入口统一调度执行。

## 核心职责

1. **任务接口定义**：`InitTask` 接口，单方法 `execute()`
2. **任务注册**：`CommonInitTasks` 作为公共任务收集点
3. **任务执行**：`InitTaskRunner` 合并执行公共任务 + 平台任务，失败隔离
4. **扩展点**：平台入口可传入平台差异化任务

## 详细目录结构

```
business/initTask/
├── build.gradle.kts              # 标准构建配置（KMP）
├── build.ohos.gradle.kts         # OHOS 构建配置
└── src/commonMain/kotlin/com/kuikly/init/business/initTask/
    ├── InitTask.kt           # 📌 接口定义：单方法 execute()
    ├── InitTaskRunner.kt     # 📌 执行器：runAll(platformTasks) 合并执行 + 失败隔离
    ├── CommonInitTasks.kt    # 📌 注册表：MutableList<InitTask> 自注册
    └── tasks/
        ├── NetworkInitTask.kt  — 桩（TODO: net-impl 实现）
        └── StorageInitTask.kt  — 桩（TODO: store-impl 实现）
```

## 核心类说明

### InitTask.kt

```kotlin
interface InitTask {
    fun execute()
}
```

各模块/平台实现此接口，定义具体的初始化逻辑。任务应完成服务的配置工作（如设置 baseUrl、超时时间等），依赖绑定由 Koin 在 `startKoin` 阶段完成。

### InitTaskRunner.kt

```kotlin
object InitTaskRunner {
    fun runAll(platformTasks: List<InitTask> = emptyList())
}
```

**执行逻辑：**
1. 合并 `CommonInitTasks.getCommonTasks()` + `platformTasks`
2. 顺序执行每个任务
3. 使用 `runCatching` 包裹，单个任务失败不阻塞后续任务
4. 失败时输出日志：`[InitTaskRunner] Task failed: {className}, error: {message}`

### CommonInitTasks.kt

```kotlin
object CommonInitTasks {
    fun register(task: InitTask)      // 注册公共任务
    fun getCommonTasks(): List<InitTask>  // 获取任务列表（副本）
```

各模块在初始化时调用 `CommonInitTasks.register(task)` 将通用 task 加入公共列表。返回副本以避免外部修改影响内部状态。

### tasks/ 目录

| 文件 | 说明 |
|------|------|
| `NetworkInitTask.kt` | 网络初始化桩，TODO 由 net-impl 模块实现（配置 HttpClient baseUrl/timeout/拦截器） |
| `StorageInitTask.kt` | 存储初始化桩，TODO 由 store-impl 模块实现（初始化缓存目录/命名空间） |

## 执行流程

```
平台入口（Application / setupIOSKoin / setupOHKoin）
    │
    ├── startKoin { modules(...) }   ← 依赖绑定
    │
    └── InitTaskRunner.runAll(platformTasks)   ← 初始化执行
            │
            ├── CommonInitTasks.getCommonTasks()  ← 公共任务
            │       ├── NetworkInitTask (桩)
            │       └── StorageInitTask (桩)
            │
            └── platformTasks  ← 平台差异化任务（通常为空）
```

## 依赖

| 依赖 | 说明 |
|------|------|
| `:common:base` | 平台能力抽象层 |
| `io.insert-koin:koin-core` | Koin DI 容器 |

> **注意**：本模块**无 Kuikly core/compose 依赖**，保持轻量，可被各平台初始化代码安全引用。

## 构建配置

- 目标平台：Android + iOS（X64 / Arm64 / SimulatorArm64）+ OHOS（独立构建）
- 资源类：`InitTaskMR`（前缀 `init_task_`）
- 插件：`multiplatform`, `android.library`, `kuiklybase.resource.generator`

## 开发约定

- 新增公共任务：实现 `InitTask` 接口 + 调用 `CommonInitTasks.register()`
- 新增平台任务：在平台入口传入 `platformTasks` 参数
- 任务失败不阻断：框架已做 `runCatching` 处理，无需额外 try-catch
- 任务应幂等：避免重复执行导致状态异常
