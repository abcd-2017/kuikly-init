# 进度追踪规范

## 触发条件

满足任一条件时创建进度追踪文件：
- 使用了 Agent Teams 模式
- 使用了 Workflow 工具编排
- 任务涉及 3 个及以上子任务

## 追踪文件位置

`.claude/output s/workflows/YYYY-MM-DD-<计划名>-progress.md`

## 追踪格式

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

## 状态说明

| 状态 | 含义 |
|------|------|
| ⏳ PENDING | 等待执行 |
| 🔄 IN_PROGRESS | 执行中 |
| ✅ DONE | 已完成并通过审查 |
| ❌ FAILED | 需要修复 |
| ⚠️ BLOCKED | 被阻塞，需要人类决策 |
