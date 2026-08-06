---
name: harmonyos-docs-builder
description: 鸿蒙文档索引构建器。从 DevEco Studio 内置文档中提取 API 索引，生成供 harmonyos-docs 查询 skill 使用的搜索索引。当 SDK 版本变化或需要重新生成索引时调用。
---

# HarmonyOS Docs Builder

## 触发条件
- 用户要求重新生成/更新鸿蒙文档索引
- DevEco Studio SDK 版本变化后
- `.claude/skills/harmonyos-docs/index/` 目录不存在或内容过旧时

## 使用方法

### 1. 检查配置
读取项目根目录的 `gradle.properties`，找到 `harmony.sdk.path` 的值。
如果为空，提示用户设置路径。

### 2. 构建索引
运行脚本：
```bash
node .claude/skills/harmonyos-docs-builder/scripts/build-index.mjs
```

### 3. 输出
索引生成到 `.claude/skills/harmonyos-docs/index/` 目录：
- `api-catalog.min.json` — API 目录树
- `meta.search.json` — 关键词搜索索引
- `method-index.json` — 方法名索引

## 配置
- SDK 路径：`gradle.properties` 中的 `harmony.sdk.path`
- 输出目录：`.claude/skills/harmonyos-docs/index/`
