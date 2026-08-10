# static_server 模块

## 模块定位

Node.js 开发服务器，用于 Kuikly 的 **NativeVue 调试模式**。提供静态资源服务（JS/SO）和 whistle 代理，支持移动端真机调试。

## 核心职责

1. **静态资源服务**：在 8017 端口提供 JS bundle 和 SO 文件的静态访问
2. **whistle 代理**：在 8083 端口启动 whistle 代理，拦截和转发调试请求
3. **NativeVue 调试**：配合 `.whistle.js` 规则，将 nv_js/nv_so 请求转发到本地服务器

## 详细目录结构

```
static_server/
├── serve/
│   ├── index.js                  # 📌 Koa 服务器入口：静态服务 + whistle 启动
│   └── config/
│       └── serve.conf.js         # 📌 服务配置：端口 8017 + 静态路径
└── static/                       # 静态资源根目录
    ├── nativevue2.js             # Kuikly 构建产物 (JS bundle)
    ├── *.so                      # 原生库文件
    └── ...                       # 其他调试资源
```

## 核心文件说明

### index.js
- 使用 **Koa** 框架启动 HTTP 服务器
- **启动流程**：
  1. `shell.exec('w2 start -p 8083')` → 启动 whistle 代理
  2. `shell.exec('w2 add --force')` → 加载 whistle 规则
  3. `koa-static` 中间件提供静态文件服务
  4. `koa-bodyparser` 解析请求体
  5. 监听 `serveConf.port`（默认 8017）
- **退出清理**：`SIGINT` / `exit` 事件触发 `w2 stop` 关闭 whistle

### serve.conf.js
- `port: 8017` — Koa 服务端口
- `staticPath` — 指向 `static_server/static/` 目录

### .whistle.js（根目录）
- 定义代理规则：
  - `/.*/debug/nv_js/(.*)/` → `127.0.0.1:8017/$1`（JS 资源转发）
  - `/.*/debug/nv_so/(.*)/` → `127.0.0.1:8017/$1`（SO 资源转发）

## 使用方式

> **注意**：static_server 目录下**无 package.json**，`npm install` / `npm run serve` 需要用户全局安装依赖（koa, koa-static, koa-bodyparser, shelljs）。

```bash
npm install              # 安装依赖
npm run serve            # 启动服务（8017 + 8083）
```

启动后控制台输出：
```
nv2 serve is starting at port 8017
visit: localhost:8017
whistle: http://localhost:8017/
quit: control + c
```

## 调试工作流

```
┌──────────────┐     whistle 代理      ┌──────────────────┐
│  移动设备     │ ←──────────────────→  │  whistle :8083   │
│  (Kuikly App) │                       └────────┬─────────┘
└──────────────┘                                │ 转发 nv_js/nv_so
                                                ▼
                                      ┌──────────────────┐
                                      │  Koa 静态服务     │
                                      │  localhost:8017   │
                                      └──────────────────┘
```

## 依赖

| 包名 | 用途 |
|------|------|
| `koa` | Web 框架 |
| `koa-static` | 静态文件中间件 |
| `koa-bodyparser` | 请求体解析 |
| `shelljs` | 执行 shell 命令（w2 whistle） |
| `console-error/info/warn` | 彩色控制台输出 |

## 注意事项

- 需要全局安装 whistle (`npm install -g whistle`)
- static_server 目录下**无 package.json**，依赖需全局安装
- `static/` 目录下的 `nativevue2.js` 由 Gradle 构建产物同步过来
- 移动端调试时需确保手机和电脑在同一 WiFi，并配置 whistle 代理
