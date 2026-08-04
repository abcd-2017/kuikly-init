# InitTask 架构设计文档

> 创建日期：2026-08-04
> 状态：设计阶段（未开始编码）

## 一、设计目标

将网络请求、存储、日志等系统级基础能力抽象为 InitTask 形式，实现：
- 公共初始化逻辑复用
- 平台/变体差异化实现可插拔
- 构建时只加载对应模块的 task

## 二、模块分层

```
shared/                                  ← 页面、组件、base 类（已有）

business/initTask/                       ← 新建模块
  └── src/commonMain/kotlin/com/kuikly/init/business/initTask/
      ├── InitTask.kt                    ← 任务接口
      ├── NetworkInitTask.kt             ← 通用实现
      ├── StorageInitTask.kt             ← 通用实现
      └── CommonInitTasks.kt             ← 公共 task list 收集

business/util/ 或 shared/src/commonMain/ ← 平台能力接口层
  ├── Clipboard.kt                       ← 剪切板接口
  ├── DeviceInfo.kt                      ← 设备信息接口
  ├── FileSystem.kt                      ← 文件操作接口
  └── ContextProvider.kt                 ← 平台上下文接口

androidApp/                              ← 平台实现层
  ├── AndroidClipboard : Clipboard
  ├── AndroidDeviceInfo : DeviceInfo
  ├── AndroidFileSystem : FileSystem
  └── AndroidAccountInitTask             ← 平台差异化 task

iosApp/                                  ← 同上模式
ohosApp/                                 ← 同上模式
```

## 三、核心接口定义

### InitTask
```kotlin
interface InitTask {
    fun execute()
}
```

### CommonInitTasks（公共收集点）
```kotlin
object CommonInitTasks {
    fun getCommonTasks(): List<InitTask> = listOf(
        NetworkInitTask(KoinContext.get()),
        StorageInitTask(KoinContext.get()),
        LogInitTask()
    )
}
```

### 平台能力接口（需进一步分析补充）
```kotlin
interface Clipboard {
    fun copy(text: String)
    fun paste(): String
}

interface DeviceInfo {
    fun getDeviceId(): String
    fun getOSVersion(): String
}

interface FileSystem {
    fun getCacheDir(): String
    fun readFile(path: String): ByteArray
    fun writeFile(path: String, data: ByteArray)
}

interface ContextProvider {
    fun getApplicationContext(): Any  // 平台上下文抽象
}
```

## 四、各平台组装方式

### Android（KRApplication.onCreate）
```kotlin
// 1. Koin 绑定
startKoin {
    modules(
        netModule + storeModule
        + module { single<Clipboard> { AndroidClipboard(this@KRApplication) } }
        + module { single<DeviceInfo> { AndroidDeviceInfo() } }
    )
}

// 2. 组装 task list 并执行
val tasks = CommonInitTasks.getCommonTasks() + listOf(
    AndroidAccountInitTask(KoinContext.get(), KoinContext.get())
)
tasks.forEach { it.execute() }
```

### iOS / OHOS 同理，只换 platformTasks 内容

## 五、Koin 依赖管理

- 当前 Koin 在 `business/login` 模块中导入，位置不合理
- 需迁移到更基础的模块（待分析确定）

## 六、平台上下文集成方案

> 待调研分析

网络初始化需要调用的系统能力：
- 平台上下文（Application/Ability/ViewController）
- 文件操作（缓存目录、证书存储）
- 设备信息（设备 ID、OS 版本）
- 网络状态（联网状态、网络类型）
- 安全（密钥存储、证书管理）

## 七、编码顺序（待执行）

1. [ ] 确定 Koin 依赖放置位置
2. [ ] 分析并确定平台能力接口清单
3. [ ] 创建 `business/initTask` 模块
4. [ ] 定义 InitTask 接口 + 通用 task 实现
5. [ ] 定义平台能力接口（Clipboard/DeviceInfo/FileSystem/ContextProvider）
6. [ ] 各平台实现平台能力接口
7. [ ] 各平台入口集成 Koin + 组装 InitTask list
8. [ ] 验证编译通过

## 八、参考资料

- 项目架构原则：`CLAUDE.md` 三层解耦
- 现有适配器注册机制：`KuiklyRenderActivity.companion.init`
- Koin 当前位置：`business/login` 模块
