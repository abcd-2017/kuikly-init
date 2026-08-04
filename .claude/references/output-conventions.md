# 输出文件存放规范

## 目录结构

```
.claude/
├── agents/              # 角色定义（版本管理）
├── references/          # 参考文档（版本管理）
├── outputs/             # 各角色输出产物（git 排除）
│   ├── research/        # exporter 调研产出
│   ├── reviews/         # reviewer 审查报告
│   ├── designs/         # 方案设计文档
│   ├── plans/           # 实现计划
│   └── workflows/       # agent teams / workflow 运行产出
└── local/               # 本地临时文件（git 排除，不提交）
```

## 各目录用途与命名规则

### `outputs/research/` — exporter 调研产出

| 类型 | 命名规则 | 示例 |
|------|---------|------|
| 技术调研 | `YYYY-MM-DD-<主题>-research.md` | `2026-08-04-kuikly-image-adapter-research.md` |
| 方案对比 | `YYYY-MM-DD-<主题>-comparison.md` | `2026-08-04-state-management-comparison.md` |

### `outputs/reviews/` — reviewer 审查报告

| 类型 | 命名规则 | 示例 |
|------|---------|------|
| 代码审查 | `YYYY-MM-DD-<分支/功能>-review.md` | `2026-08-04-login-page-review.md` |
| PR 审查 | `PR-<编号>-review.md` | `PR-42-review.md` |

### `outputs/designs/` — 方案设计文档

| 类型 | 命名规则 | 示例 |
|------|---------|------|
| 架构设计 | `YYYY-MM-DD-<主题>-design.md` | `2026-08-04-navigator-architecture-design.md` |
| 技术方案 | `YYYY-MM-DD-<主题>-spec.md` | `2026-08-04-bridge-module-spec.md` |

### `outputs/plans/` — 实现计划

| 类型 | 命名规则 | 示例 |
|------|---------|------|
| 实现计划 | `YYYY-MM-DD-<主题>-plan.md` | `2026-08-04-login-feature-plan.md` |

### `outputs/workflows/` — agent teams / workflow 产出

| 类型 | 命名规则 | 示例 |
|------|---------|------|
| 任务简报 | `YYYY-MM-DD-<计划名>-task-N-brief.md` | `2026-08-04-login-plan-task-1-brief.md` |
| 实现报告 | `YYYY-MM-DD-<计划名>-task-N-report.md` | `2026-08-04-login-plan-task-1-report.md` |
| 审查包 | `YYYY-MM-DD-<计划名>-task-N-review.md` | `2026-08-04-login-plan-task-1-review.md` |

### `local/` — 本地临时文件

用于存放不需要版本管理的临时文件，如：
- 个人调试笔记
- 临时数据导出
- 个人配置覆盖

## Git 策略

| 目录 | Git 策略 | 说明 |
|------|---------|------|
| `agents/` | ✅ 版本管理 | 角色定义是项目规范 |
| `references/` | ✅ 版本管理 | 参考文档是项目知识 |
| `outputs/` | ❌ git 排除 | 产出物是过程文件，体积大且频繁变更 |
| `local/` | ❌ git 排除 | 纯本地临时文件 |

## 写入权限

| 角色 | 可写目录 |
|------|---------|
| exporter | `outputs/research/`、`outputs/designs/`、`outputs/plans/` |
| reviewer | `outputs/reviews/` |
| developer | 无（代码产出是 commit，不是文档） |
| 主 Agent | `outputs/workflows/`（进度追踪） |

**注意**：所有角色均不能修改 `.claude/agents/` 和 `CLAUDE.md`。
