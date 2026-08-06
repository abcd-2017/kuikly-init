# Skills 参考

各角色在执行任务时，应使用以下 skills 增强能力：

## 通用 Skills

| Skill | 使用场景 |
|-------|----------|
| `harmonyos-docs-builder` | 从 DevEco Studio 内置文档提取 API 索引，生成供 harmonyos-docs 使用的搜索索引。SDK 版本变化后需运行此 skill 重新生成索引 |
| `harmonyos-docs` | 查询 HarmonyOS API、@ohos.* 包、ArkTS/ArkUI 文档。从 DevEco Studio 内置文档提取索引，支持离线搜索 + 在线 fallback |

## Developer

| Skill | 使用场景 |
|-------|----------|
| `superpowers:test-driven-development` | 写代码前先写失败测试 |
| `superpowers:systematic-debugging` | 先找根因再修复 |
| `superpowers:verification-before-completion` | 完成前必须有验证证据 |
| `superpowers:finishing-a-development-branch` | 完成开发分支的集成决策 |
| `commit-commands:commit` | 标准化 commit |

## Reviewer

| Skill | 使用场景 |
|-------|----------|
| `code-review:code-review` | 标准化代码审查 |
| `pr-review-toolkit:review-pr` | PR 级全面审查 |
| `pr-review-toolkit:silent-failure-hunter` | 排查静默失败 |
| `superpowers:verification-before-completion` | 验证 developer 的完成声称 |

## Exporter

| Skill | 使用场景 |
|-------|----------|
| `superpowers:brainstorming` | 探索需求、设计方案 |
| `superpowers:writing-plans` | 编写实现计划 |
| `episodic-memory:remembering-conversations` | 检索历史对话 |

## Leader

| Skill | 使用场景 |
|-------|----------|
| `superpowers:brainstorming` | 决策前探索需求 |
| `superpowers:writing-plans` | 制定实现计划 |
| `superpowers:dispatching-parallel-agents` | 并行调度子 agent |
| `superpowers:executing-plans` | 执行实现计划 |
