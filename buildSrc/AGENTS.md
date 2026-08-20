# buildSrc 模块

## 模块定位

Gradle 构建辅助模块，集中管理项目版本号和 Kuikly 插件依赖。Gradle 会自动编译并将其类路径注入所有子模块的构建脚本。

## 核心职责

1. **版本统一管理**：Kuikly 版本、Kotlin 版本集中定义
2. **插件声明**：Kuikly Gradle 插件的延迟初始化

## 目录结构

```
buildSrc/
├── build.gradle.kts              # kotlin-dsl 插件 + mavenCentral 仓库
└── src/main/java/
    └── KotlinBuildVar.kt         # 📌 版本定义 + BuildPlugin 对象
```

## 核心类说明

### KotlinBuildVar.kt

#### Version 对象

| 常量 | 值 | 说明 |
|------|-----|------|
| `KUIKLY_VERSION` | `"2.25.0"` | Kuikly 框架版本 |
| `KOTLIN_VERSION` | `"2.1.21"` | 标准 Kotlin 版本 |
| `KOTLIN_OHOS_VERSION` | `"2.0.21-ohos"` | 鸿蒙适配 Kotlin 版本 |

| 方法 | 返回值示例 | 说明 |
|------|-----------|------|
| `getKuiklyVersion()` | `"2.25.0-2.1.21"` | 标准构建用 Kuikly 完整版本号 |
| `getKuiklyOhosVersion()` | `"2.25.0-2.0.21-ohos"` | OHOS 构建用 Kuikly 完整版本号 |

#### BuildPlugin 对象

| 属性 | 值 | 说明 |
|------|-----|------|
| `kuikly` | `"com.tencent.kuikly-open:core-gradle-plugin:2.25.0-2.1.21"` | Kuikly Gradle 插件（lazy 初始化） |

## 使用方式

子模块 `build.gradle.kts` 中直接引用：

```kotlin
dependencies {
    classpath(BuildPlugin.kuikly)
}

implementation("com.tencent.kuikly-open:core:${Version.getKuiklyVersion()}")
```

## 版本升级影响范围

修改 `Version` 中的常量后，所有子模块的 Kuikly 依赖版本会自动更新：
- `shared/build.gradle.kts`（标准构建）
- `shared/build.ohos.gradle.kts`（OHOS 构建）
- 根 `build.gradle.kts`（插件版本）
