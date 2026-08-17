# common:util 模块

## 模块定位

通用工具模块，提供跨平台的纯 Kotlin 工具函数。最大化可移植性，零框架依赖。

## 核心职责

1. **字符串工具**：提供通用的字符串判断和转换函数
2. **纯 Kotlin 实现**：不依赖平台 API，所有平台行为一致

## 详细目录结构

```
common/util/
├── build.gradle.kts              # KMP 库配置（Android + iOS，无 OHOS）
└── src/commonMain/kotlin/com/kuikly/init/common/util/
    └── StringUtils.kt            # 📌 字符串工具类
```

## 核心类说明

### StringUtils.kt

```kotlin
object StringUtils {
    fun isBlank(text: String?): Boolean              // 判断字符串是否为空或空白
    fun isNotBlank(text: String?): Boolean           // 判断字符串是否非空
    fun truncate(text: String, maxLength: Int, suffix: String = "..."): String  // 截断字符串
}
```

| 方法 | 说明 |
|------|------|
| `isBlank` | 委托给 `isNullOrBlank()`，null 安全 |
| `isNotBlank` | `isBlank` 取反 |
| `truncate` | 超出 maxLength 截断并追加 suffix（默认 `...`） |

## 依赖

> **注意**：本模块**零框架依赖**（无 Kuikly、无 Koin、无协程），最大化可移植性。

## 构建配置

- 目标平台：Android + iOS（X64 / Arm64 / SimulatorArm64）
- 资源类：`UtilMR`（前缀 `util_`）
- 插件：`multiplatform`, `android.library`, `kuiklybase.resource.generator`

## 开发约定

- 仅添加工具函数，不引入业务逻辑
- 保持纯 Kotlin 实现，不使用平台特定 API
- 新增工具函数需考虑 null 安全性
