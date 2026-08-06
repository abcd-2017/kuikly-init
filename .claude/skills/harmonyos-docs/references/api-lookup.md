# API 查询指南

## 快速查询（推荐）

索引文件较大（单行 JSON），**禁止用 Grep 直接搜索**，使用查询脚本：

```bash
# 搜索元数据（标题、描述、关键词）
node .claude/skills/harmonyos-docs/scripts/query-index.mjs meta <关键词>

# 搜索方法名
node .claude/skills/harmonyos-docs/scripts/query-index.mjs method <方法名>

# 搜索 API 目录树（包名、关键词）
node .claude/skills/harmonyos-docs/scripts/query-index.mjs catalog <包名>
```

**示例**：

```bash
node .claude/skills/harmonyos-docs/scripts/query-index.mjs meta 一键登录
node .claude/skills/harmonyos-docs/scripts/query-index.mjs method Login
node .claude/skills/harmonyos-docs/scripts/query-index.mjs catalog @ohos.net
```

## 查询方式

### 1. 本地索引查询（推荐，离线）

索引文件位于 `.claude/skills/harmonyos-docs/index/`：

| 文件 | 用途 | 结构 |
|------|------|------|
| `api-catalog.min.json` | API 目录树 | `[{ name, doc, url, isLeaf, code, pCode, keywords }]` |
| `meta.search.json` | 关键词搜索 | `{ "关键词": [{ title, uri, description, keywords }] }` |
| `method-index.json` | 方法名索引 | `{ "methodName": "doc-uri" }` |

### 2. 在线 API fallback

本地未命中时，使用华为文档 API：

```
POST https://developer.huawei.com/consumer/cn/documentPortal/getDocumentById
Headers:
  User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36
  Referer: https://developer.huawei.com/consumer/cn/doc/
  Content-Type: application/json
Body: { "docId": "<文档ID>" }
```

其他可用端点：
- `getCatalogTree` — 获取目录树
- `getNavigationAddress` — 获取导航地址
- `celiaSearch` — 搜索

### 3. 查询示例

```javascript
// 搜索 @ohos.net.http
const meta = require('./index/meta.search.json');
const results = meta['@ohos.net.http'] || meta['net.http'] || [];

// 查找方法
const methods = require('./index/method-index.json');
const docUri = methods['createHttp'];
```

## 注意事项

- 本地索引需要定期更新（SDK 版本变化时）
- 在线 API 需要代理（Clash SOCKS5: 127.0.0.1:7897）
- 部分 HTML 文档页面是导航页，详细参数需查在线 API
