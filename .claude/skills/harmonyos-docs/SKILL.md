---
name: harmonyos-docs
description: 鸿蒙开发文档查询 skill。读取预生成的 API 索引，提供离线搜索能力。当需要查询 HarmonyOS API、@ohos.* 包、ArkTS/ArkUI 文档时使用。
---

# HarmonyOS Docs Skill

## 触发条件
- 用户询问鸿蒙 API、@ohos.* 包、ArkTS/ArkUI 文档
- 需要查找鸿蒙官方接口说明

## 使用方法

### 1. 使用查询脚本（推荐）

索引文件较大（单行 JSON），Grep 解析会失败。使用预置查询脚本：

```bash
node .claude/skills/harmonyos-docs/scripts/query-index.mjs <类型> <关键词>
```

**参数说明**：

| 类型 | 说明 | 匹配字段 |
|------|------|----------|
| `catalog` | API 目录树 | `name`, `keywords` |
| `meta` | 关键词搜索 | `keyword`, `title`, `description` |
| `method` | 方法名索引 | `methodName` |

**示例**：

```bash
# 搜索 @ohos.net.http 相关 API
node .claude/skills/harmonyos-docs/scripts/query-index.mjs catalog http

# 搜索关键词
node .claude/skills/harmonyos-docs/scripts/query-index.mjs meta 网络

# 查找方法
node .claude/skills/harmonyos-docs/scripts/query-index.mjs method createHttp
```

### 2. 直接读取索引（小文件适用）

索引文件位于 `.claude/skills/harmonyos-docs/index/`：

| 文件 | 用途 |
|------|------|
| `api-catalog.min.json` | API 目录树 |
| `meta.search.json` | 关键词搜索 |
| `method-index.json` | 方法名索引 |

### 3. 在线 fallback
本地索引未命中时，使用 WebFetch 访问华为文档 API：
- 基础 URL：`https://developer.huawei.com/consumer/cn/documentPortal/getDocumentById`
- 需要请求头：`User-Agent`（模拟浏览器）、`Referer: https://developer.huawei.com/consumer/cn/doc/`

## 索引更新
索引由 `harmonyos-docs-builder` skill 生成。SDK 版本变化后需重新运行 builder。

## 配置
- SDK 路径配置位置：`gradle.properties` 中的 `harmony.sdk.path`
- 索引目录：`.claude/skills/harmonyos-docs/index/`
