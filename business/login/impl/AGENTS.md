# business:login:impl 模块

## 模块定位

登录功能的**实现模块**，提供内存桩实现。当前为开发期占位，后续替换为真实网络请求实现。

## 核心职责

1. **服务实现**：`LoginServiceImpl` 实现 `ILoginService` 接口
2. **Koin DI**：`LoginModule` 提供依赖注入绑定
3. **状态管理**：内存持有当前用户登录状态

## 详细目录结构

```
business/login/impl/
├── build.gradle.kts              # 标准构建配置（KMP）
├── build.ohos.gradle.kts         # OHOS 构建配置
└── src/commonMain/kotlin/com/kuikly/init/business/login/impl/
    ├── LoginModule.kt            # 📌 Koin 模块：single<ILoginService> { LoginServiceImpl() }
    └── LoginServiceImpl.kt       # 📌 IDebugService 内存桩实现
```

## 核心类说明

### LoginServiceImpl.kt

内存桩实现，逻辑简单：

| 方法 | 行为 |
|------|------|
| `login(username, password)` | 用户名密码非空则成功（合成 UserInfo），否则 `Failure(-1, "用户名或密码不能为空")` |
| `logout()` | 清空内存中的 `currentUser` 和 `loggedIn` 状态 |
| `isLoggedIn()` | 返回内存中的 `loggedIn` 布尔值 |
| `getCurrentUser()` | 返回内存中的 `currentUser`（可为 null） |

**内部状态：**
- `private var currentUser: UserInfo?`
- `private var loggedIn: Boolean`

### LoginModule.kt

```kotlin
val LoginModule: Module = module {
    single<ILoginService> { LoginServiceImpl() }
}
```

## 依赖

| 依赖 | 说明 |
|------|------|
| `:business:login:api` | 登录接口模块 |
| `:common:base` | 平台能力抽象层 |
| `io.insert-koin:koin-core` | Koin DI 容器 |

## 构建配置

- 目标平台：Android + iOS（X64 / Arm64 / SimulatorArm64）+ OHOS（独立构建）
- 插件：`multiplatform`, `android.library`

## 开发约定

- 当前为桩实现，替换为真实网络请求时保持接口签名不变
- 替换时需同步更新三端（Android/iOS/OHOS）的原生网络适配器
- 登录状态持久化（如需要）应由存储层处理，不在 Service 实现中硬编码
