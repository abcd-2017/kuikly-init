#!/usr/bin/env node
/**
 * 从 DevEco Studio 内置文档中提取 JSON 索引，生成精简搜索索引
 *
 * 用法：node build-index.mjs
 * 读取：gradle.properties 中的 harmony.sdk.path
 * 输出：.claude/skills/harmonyos-docs/index/ 目录
 */

import { readFileSync, writeFileSync, mkdirSync, existsSync } from 'fs';
import { join, dirname } from 'path';
import { fileURLToPath } from 'url';
import { createInterface } from 'readline';

const __dirname = dirname(fileURLToPath(import.meta.url));
const SKILL_DIR = join(__dirname, '..');
const INDEX_DIR = join(SKILL_DIR, '..', 'harmonyos-docs', 'index');
const PROJECT_ROOT = join(SKILL_DIR, '..', '..', '..');

// 1. 从 gradle.properties 读取 SDK 路径
function getSdkPath() {
  const propsPath = join(PROJECT_ROOT, 'gradle.properties');
  if (!existsSync(propsPath)) {
    console.error('❌ 找不到 gradle.properties');
    process.exit(1);
  }

  const content = readFileSync(propsPath, 'utf-8');
  const match = content.match(/^harmony\.sdk\.path\s*=\s*(.+)$/m);

  if (!match || !match[1].trim()) {
    console.error('❌ gradle.properties 中 harmony.sdk.path 为空');
    console.error('   请运行初始化脚本设置 DevEco Studio 路径');
    process.exit(1);
  }

  // 处理 gradle.properties 中的转义：\: -> :，\\ -> \
  const rawPath = match[1].trim()
    .replace(/\\:/g, ':')   // 先处理转义冒号
    .replace(/\\\\/g, '\\'); // 再处理转义反斜杠

  // 如果是绝对路径（Windows 盘符 或 Unix 根目录），直接使用
  if (/^[a-zA-Z]:[\\/]/.test(rawPath) || rawPath.startsWith('/')) {
    return rawPath;
  }

  // 相对路径则基于项目根目录解析
  return join(PROJECT_ROOT, rawPath);
}

// 2. 确保输出目录存在
function ensureIndexDir() {
  if (!existsSync(INDEX_DIR)) {
    mkdirSync(INDEX_DIR, { recursive: true });
  }
}

// 3. 精简 API_Catalog.json
function buildApiCatalog(sdkPath) {
  const srcPath = join(sdkPath, 'plugins', 'openharmony', 'ohos-info-center-view', 'static', 'hos', 'JsEtsAPIReference', 'API_Catalog.json');

  if (!existsSync(srcPath)) {
    console.warn('⚠️ 找不到 API_Catalog.json:', srcPath);
    return null;
  }

  const data = JSON.parse(readFileSync(srcPath, 'utf-8'));

  // 只保留搜索必需的字段
  const simplified = data.map(item => ({
    name: item.nodeName,
    doc: item.relateDoc,
    url: item.url_path,
    isLeaf: item.isleaf === 'Y',
    code: item.code,
    pCode: item.p_code,
    keywords: item.keywords || '',
    searchTitle: item.searchTitle || ''
  }));

  writeFileSync(join(INDEX_DIR, 'api-catalog.min.json'), JSON.stringify(simplified));
  console.log(`✅ api-catalog.min.json: ${simplified.length} 条记录`);

  return simplified;
}

// 4. 精简 ALL_META.TXT.json
function buildMetaIndex(sdkPath) {
  const srcPath = join(sdkPath, 'plugins', 'openharmony', 'ohos-info-center-view', 'static', 'hos', 'JsEtsAPIReference', 'ALL_META.TXT.json');

  if (!existsSync(srcPath)) {
    console.warn('⚠️ 找不到 ALL_META.TXT.json:', srcPath);
    return null;
  }

  const data = JSON.parse(readFileSync(srcPath, 'utf-8'));

  // 构建搜索索引：关键词 -> API 元数据
  const searchIndex = {};

  data.forEach(item => {
    const entry = {
      title: item.title,
      uri: item.uri,
      description: item.des,
      keywords: item.kw,
      code: item.code
    };

    // 按关键词索引
    const keywords = (item.kw || '').split(',').map(k => k.trim()).filter(Boolean);
    keywords.forEach(kw => {
      if (!searchIndex[kw]) searchIndex[kw] = [];
      searchIndex[kw].push(entry);
    });

    // 按标题索引
    if (item.title) {
      if (!searchIndex[item.title]) searchIndex[item.title] = [];
      searchIndex[item.title].push(entry);
    }
  });

  writeFileSync(join(INDEX_DIR, 'meta.search.json'), JSON.stringify(searchIndex));
  console.log(`✅ meta.search.json: ${Object.keys(searchIndex).length} 个索引键`);

  return searchIndex;
}

// 5. 处理 Method_Name_Index.json（流式解析大文件）
async function buildMethodIndex(sdkPath) {
  const srcPath = join(sdkPath, 'plugins', 'openharmony', 'ohos-info-center-view', 'static', 'hos', 'JsEtsAPIReference', 'Method_Name_Index.json');

  if (!existsSync(srcPath)) {
    console.warn('⚠️ 找不到 Method_Name_Index.json:', srcPath);
    return null;
  }

  // 流式读取大文件
  const fileStream = createInterface({
    input: await import('fs').then(fs => fs.createReadStream(srcPath, { encoding: 'utf-8' })),
    crlfDelay: Infinity
  });

  let methodIndex = {};
  let count = 0;

  for await (const line of fileStream) {
    // 单行 JSON，需要解析
    try {
      const data = JSON.parse(line);
      Object.assign(methodIndex, data);
      count = Object.keys(methodIndex).length;
    } catch (e) {
      // 跳过解析失败的行
    }
  }

  // 精简：只保留方法名 -> 文档 URI 的映射
  const simplified = {};
  for (const [method, info] of Object.entries(methodIndex)) {
    simplified[method] = typeof info === 'string' ? info : (info.uri || info.doc || JSON.stringify(info).slice(0, 100));
  }

  writeFileSync(join(INDEX_DIR, 'method-index.json'), JSON.stringify(simplified));
  console.log(`✅ method-index.json: ${Object.keys(simplified).length} 个方法`);

  return simplified;
}

// 主流程
async function main() {
  console.log('🔍 读取配置...');
  const sdkPath = getSdkPath();
  console.log(`   SDK 路径: ${sdkPath}`);

  console.log('📁 准备输出目录...');
  ensureIndexDir();

  console.log('📖 读取索引文件...');
  buildApiCatalog(sdkPath);
  buildMetaIndex(sdkPath);
  await buildMethodIndex(sdkPath);

  console.log('✅ 索引构建完成！');
}

main().catch(err => {
  console.error('❌ 错误:', err.message);
  process.exit(1);
});
