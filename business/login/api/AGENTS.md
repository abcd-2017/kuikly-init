# business:login:api 模块

## 模块定位

登录功能的**接口模块**，定义登录服务契约。零框架依赖，可被任意模块安全引用。

## 核心职责

1. **服务接口定义**：声明 `ILoginService` 接口，供 impl 模块实现
2. **数据模型定义**：提供 `LoginResult`、`UserInfo` 等数据类型
3. **契约隔离**：api/impl 分离，调用方仅依赖接口

## 详细目录结构

```
business/login/api/
├── build.gradle.kts              # KMP 库配置（Android + iOS，无 OHOS）
└── src/commonMain/kotlin/com/kuikly/init/business/login/api/
    └── ILoginService.kt          # 📌 服务接口 + 数据类型定义
```

## 核心类说明

### ILoginService.kt

#### ILoginService 接口

| 方法 | 说明 |
|------|------|
| `suspend fun login(username, password): LoginResult` | 发起登录 |
| `suspend fun logout()` | 退出登录 |
| `fun isLoggedIn(): Boolean` | 是否已登录 |
| `fun getCurrentUser(): UserInfo?` | 获取当前用户信息 |

#### LoginResult 密封类

```kotlin
sealed class LoginResult {
    data class Success(val user: UserInfo) : LoginResult()
    data class Failure(val code: Int, val message: String) : LoginResult()
}
```

#### UserInfo 数据类

```kotlin
data class UserInfo(
    val userId: String,
    val username: String,
    val nickname: String,
    val avatarUrl: String? = null
)
```

## 依赖

> **注意**：本模块**零框架依赖**（无 Kuikly、无 Koin），最大化可移植性。

## 构建配置

- 目标平台：Android + iOS（X64 / Arm64 / SimulatorArm64）
- 插件：`multiplatform`, `android.library`

## 开发约定

- 仅定义接口和数据类型，不包含实现逻辑
- 不依赖任何框架 API
- 新增方法需同步更新 impl 模块的 `LoginServiceImpl`
