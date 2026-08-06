#!/usr/bin/env node
/**
 * 索引查询脚本 - 供 exporter 查询 JSON 索引（避免 Grep 处理大文件）
 * 用法：node query-index.mjs <catalog|meta|method> <关键词>
 */

import { readFileSync } from 'fs';
import { fileURLToPath } from 'url';
import { dirname, join } from 'path';

const __dirname = dirname(fileURLToPath(import.meta.url));
const INDEX_DIR = join(__dirname, '..', 'index');

const [,, type, ...queryArgs] = process.argv;
const query = queryArgs.join(' ').toLowerCase();

const indexFiles = {
  catalog: join(INDEX_DIR, 'api-catalog.min.json'),
  meta: join(INDEX_DIR, 'meta.search.json'),
  method: join(INDEX_DIR, 'method-index.json')
};

if (!indexFiles[type]) {
  console.error('用法: node query-index.mjs <catalog|meta|method> <关键词>');
  process.exit(1);
}

const data = JSON.parse(readFileSync(indexFiles[type], 'utf-8'));
let results;

if (type === 'catalog') {
  results = data.filter(item =>
    item.name?.toLowerCase().includes(query) ||
    item.keywords?.toLowerCase().includes(query)
  );
} else if (type === 'meta') {
  results = Object.entries(data)
    .filter(([k, v]) =>
      k.toLowerCase().includes(query) ||
      v.some(e =>
        e.title?.toLowerCase().includes(query) ||
        e.description?.toLowerCase().includes(query)
      )
    )
    .map(([k, v]) => ({ keyword: k, matches: v }));
} else if (type === 'method') {
  results = Object.entries(data)
    .filter(([k]) => k.toLowerCase().includes(query))
    .map(([k, v]) => ({ method: k, doc: v }));
}

console.log(JSON.stringify(results.slice(0, 20), null, 2));
console.log(`共 ${results.length} 条匹配，显示前 20 条`);
