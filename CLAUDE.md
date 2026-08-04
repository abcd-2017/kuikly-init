# CLAUDE.md

本文件为 Claude Code 提供在本仓库中工作时的指导。

## 约束

- **回答必须使用中文**。

## 项目定位

基于 Kuikly 的**跨端脚手架项目**，目标是抽象系统级基础能力（导航、网络、存储、日志、平台桥等），让后续业务项目开箱即用。

当前处于**阶段 1（实体项目）**：用固定包名开发真实可编译运行的项目。后续**阶段 2（模板化）**：将实体项目转换为可复用模板。

## 技术选型

| 类别 | 选型 | 说明 |
|------|------|------|
| 框架 | Kuikly（腾讯） | 唯一生产可用的 KMP + 鸿蒙方案 |
| DSL | Compose DSL | Kuikly fork 版 Jetpack Compose 1.7.3 |
| 语言 | Kotlin 2.1.21 | 构建工具：Gradle 7.x + KSP |
| OHOS | Kotlin 2.0.21-KBA-010 | 鸿蒙独立构建变体 |

## 架构原则

### 三层解耦

```
com.xxx.app.*            ← 业务代码只 import 这层（永远不变）
com.xxx.internal.kuikly  ← Kuikly 适配实现（整体可替换）
com.tencent.kuikly.*     ← Kuikly 原生 API（业务代码绝不直接引用）
```

### 核心约束

1. 业务代码的 import 路径中**永远不出现框架名**（kuikly）
2. 包名集中定义在 `gradle.properties`，源码中引用而非硬编码
3. 切换引擎 = 新增适配模块 + 改一行 ServiceLocator 绑定，业务代码零改动

## 角色与权限

### 主 Agent（Controller）

| 权限 | 说明 |
|------|------|
| **只读 + 调度** | 禁止直接修改代码，所有编码任务必须派发给 subagent |

**职责**：
- 理解需求、拆解任务
- 派发 subagent（developer / reviewer / exporter / leader）
- 跟踪进度、协调依赖
- 汇总结果、报告状态
- 执行 final whole-branch review

**禁止**：
- ❌ 不使用 Edit / Write 修改业务代码
- ❌ 不直接编码，哪怕"只是改一行"

### Subagent 角色

| 角色 | 权限 | 说明 |
|------|------|------|
| **developer** | 读写 | 编码、调试、实现（主 agent 调度） |
| **reviewer** | 只读 | 代码审查，输出报告 |
| **exporter** | 只读 | 技术调研、方案探索 |
| **leader** | 只读 | 技术决策、架构把关 |

**核心红线：主 Agent 禁止直接修改代码，所有编码必须通过 subagent 执行。**

## 协作流程

### 普通模式（串行）

```
调研（exporter）→ 编码（developer）→ 审查（reviewer）→ 提交
```

### Agent Teams 模式（并行）

当任务可拆解为独立子任务时，主 agent 并行调度多个 subagent：

**量化标准**（满足任一即触发）：
- 任务涉及 2 个及以上平台需要同步实现
- 任务涉及 2 个及以上模块可以并行开发
- 调研与编码可以同时进行

**使用方式**：通过 Agent 工具 spawn 多个子 agent
**工作流**：复杂多阶段任务可使用 Workflow 工具编排

### 主 Agent 与 Exporter 的边界

| 角色 | 职责边界 |
|------|---------|
| **主 Agent** | 理解用户需求 → 判断复杂度 → 选择模式 → 派发 subagent → 跟踪进度 |
| **Exporter** | 接收调研任务 → 收集信息 → 对比方案 → 输出调研结论 |

**关键区别**：主 Agent 是用户与 subagent 之间的桥梁；Exporter 只负责信息收集与方案设计，不做调度决策。

### Final Whole-Branch Review

所有任务完成后，主 agent 派发 **reviewer** 执行一次全量 review：

1. **范围**：从分支起点到当前 HEAD 的全部变更
2. **内容**：
   - 整体架构一致性
   - 跨模块依赖是否合理
   - 是否有遗漏的测试或文档
   - commit 历史是否干净（是否需要 squash）
3. **输出**：review 报告保存到 `.claude/outputs/reviews/`
4. **命名**：`YYYY-MM-DD-<分支名>-final-review.md`

## 模型选择指导

根据任务复杂度选择 subagent 模型：

| 复杂度 | 场景 | 推荐模型 |
|--------|------|---------|
| **低** | 单文件修改、简单 Bug 修复、配置调整 | `haiku` |
| **中** | 多文件功能开发、跨平台适配、重构 | `sonnet` |
| **高** | 架构设计、复杂调试、技术选型 | `opus` |

**原则**：用能满足任务需求的最低级别模型，节约成本。

## 进度追踪

### 触发条件

满足任一条件时创建进度追踪文件：
- 使用了 Agent Teams 模式
- 使用了 Workflow 工具编排
- 任务涉及 3 个及以上子任务

### 追踪文件位置

`.claude/outputs/workflows/YYYY-MM-DD-<计划名>-progress.md`

### 追踪格式

```markdown
# 计划进度: <计划名>

## 任务列表

| 任务 | 状态 | subagent | commits | 备注 |
|------|------|----------|---------|------|
| Task 1: xxx | ✅ DONE | developer | abc1234 | |
| Task 2:yyy | 🔄 IN_PROGRESS | reviewer | | 等待审查 |
| Task 3: zzz | ⏳ PENDING | | | |

## 审查记录

- Task 1 review: ✅ Approved
- Task 2 review: ❌ Needs fixes (已派发修复)

## 决策记录

- 选择方案 A 而非 B，原因是...
```

### 状态说明

| 状态 | 含义 |
|------|------|
| ⏳ PENDING | 等待执行 |
| 🔄 IN_PROGRESS | 执行中 |
| ✅ DONE | 已完成并通过审查 |
| ❌ FAILED | 需要修复 |
| ⚠️ BLOCKED | 被阻塞，需要人类决策 |

## 核心能力（Skills 引用）

各角色在执行任务时，应使用以下 skills 增强能力：

| 角色 | 核心 Skills | 说明 |
|------|-------------|------|
| **developer** | `superpowers:test-driven-development` | 写代码前先写失败测试 |
| | `superpowers:systematic-debugging` | 先找根因再修复 |
| | `superpowers:verification-before-completion` | 完成前必须有验证证据 |
| | `superpowers:finishing-a-development-branch` | 完成开发分支的集成决策 |
| | `commit-commands:commit` | 标准化 commit |
| **reviewer** | `code-review:code-review` | 标准化代码审查 |
| | `pr-review-toolkit:review-pr` | PR 级全面审查 |
| | `pr-review-toolkit:silent-failure-hunter` | 排查静默失败 |
| | `superpowers:verification-before-completion` | 验证 developer 的完成声称 |
| **exporter** | `superpowers:brainstorming` | 探索需求、设计方案 |
| | `superpowers:writing-plans` | 编写实现计划 |
| | `episodic-memory:remembering-conversations` | 检索历史对话 |
| **leader** | `superpowers:brainstorming` | 决策前探索需求 |
| | `superpowers:writing-plans` | 制定实现计划 |
| | `superpowers:dispatching-parallel-agents` | 并行调度子 agent |
| | `superpowers:executing-plans` | 执行实现计划 |

## Git 工作流

1. **编码阶段**：分步提交，每个逻辑步骤一个 commit
2. **所有任务完成后**：squash 压缩为尽可能少的 commit（每个独立功能/修复最终只保留 1 个 commit）
3. **多模块变更**：同步更新对应模块的 `AGENTS.md`
4. **Final Review 后**：确认 commit 历史干净，准备合入

## 用户偏好

- 命名风格：直接、功能描述型，不整虚的
- 不喜欢过度设计，务实优先
- 习惯先做实再抽象，不搞预先过度抽象

---

## 详细文档索引

| 内容 | 路径 |
|------|------|
| 构建命令 | `.claude/references/build-commands.md` |
| 核心框架概念 | `.claude/references/framework-concepts.md` |
| 开发约定 | `.claude/references/dev-conventions.md` |
| 输出文件存放规范 | `.claude/references/output-conventions.md` |
| shared 模块 | `shared/AGENTS.md` |
| androidApp 模块 | `androidApp/AGENTS.md` |
| iosApp 模块 | `iosApp/AGENTS.md` |
| ohosApp 模块 | `ohosApp/AGENTS.md` |
| buildSrc 模块 | `buildSrc/AGENTS.md` |
| static_server 模块 | `static_server/AGENTS.md` |
| 角色定义 | `.claude/agents/` |
