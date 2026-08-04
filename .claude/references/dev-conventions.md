# 开发约定

## 新增 Kuikly 页面

1. 在 `shared/src/commonMain` 中创建一个继承 `BasePager` 并使用 `@Page("name")` 注解的类
2. 实现 `ComposeContent()` 编写 UI
3. 如需原生能力，通过 `BridgeModule` 声明接口
4. 在三个平台的原生模块中实现对应的 `when` 分支

## 新增原生桥接方法

1. 在 Kotlin `BridgeModule` 中添加常量
2. 实现 `toNative()` 调用
3. 在 Android `KRBridgeModule.kt` 添加 `when` 分支
4. 在 iOS `HRBridgeModule.m` 添加 `when` 分支
5. 在 OHOS `KRBridgeModule.ets` 添加 `when` 分支
6. **方法名必须三端一致**

## Koin 依赖注入规范

**禁止**使用全限定名调用 Koin API：

```kotlin
// ❌ 禁止
org.koin.core.context.GlobalContext.get().get()

// ✅ 正确
import org.koin.core.context.GlobalContext

GlobalContext.get().get()
```

## 平台代码分离

- 平台特定代码放在平台应用模块 (`androidApp`、`iosApp`、`ohosApp`) 中
- **不要放在 `shared` 的 source sets 里**
- `commonMain` 中禁止直接使用 `android.*` / `platform.*` 等，应使用 `expect/actual`

## JS 输出

JS 输出文件名为 `nativevue2.js`（在 `shared/build.gradle.kts` 的 webpackTask 和 `KuiklyConfig` 中都有配置）。

## Git 提交规范

### 编码阶段（分步提交）

每完成一个逻辑步骤就 commit 一次，使用 `commit-commands:commit` skill：

```
feat(shared): 添加登录页面骨架
fix(android): 修复图片适配器内存泄漏
refactor(common): 提取 BridgeModule 公共方法
```

### 任务完成后（squash）

全部完成后，将多个 commit 压缩为尽可能少的 commit：

```bash
# 查看当前分支与 main 的差异
git log main..HEAD --oneline

# 交互式 rebase 压缩（将 pick 改为 squash/fixup）
git rebase -i main
```

**目标**：每个独立功能/修复最终只保留 1 个 commit。

## 多模块变更文档同步

当单次任务修改了**多个模块**的代码时：

1. 扫描变更涉及的所有模块
2. 检查各模块的 `AGENTS.md` 是否需要更新
3. 如有新增/删除/变更的类、方法、配置，同步更新对应模块文档

**触发条件**：变更文件跨越 2 个及以上模块目录（如同时改了 `shared/` 和 `androidApp/`）。
