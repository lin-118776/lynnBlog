/**
 * 文章文本工具：markdown 清理 / 字数统计 / 阅读时长 / 标签解析 / 本地搜索评分与高亮片段
 * 全站共用（文章详情、标签云、站内搜索、归档）
 */

/** 去除 Markdown 语法标记，仅保留可读文本 */
export function stripMarkdown(md) {
  if (!md) return ''
  return String(md)
    // 代码块 / 行内代码
    .replace(/```[\s\S]*?```/g, ' ')
    .replace(/`([^`]*)`/g, '$1')
    // 图片与链接：保留文本
    .replace(/!\[([^\]]*)\]\([^)]*\)/g, '$1')
    .replace(/\[([^\]]*)\]\([^)]*\)/g, '$1')
    // 标题符 / 引用 / 列表 / 分割线 / 表格分隔
    .replace(/^\s{0,3}#{1,6}\s+/gm, '')
    .replace(/^\s{0,3}>\s?/gm, '')
    .replace(/^\s*[-+*]\s+/gm, '')
    .replace(/^\s*\d+[.、)]\s+/gm, '')
    .replace(/^\s*\|.*\|\s*$/gm, ' ')
    .replace(/^\s*[-=]{3,}\s*$/gm, '')
    .replace(/^\s*([-*_])\1{2,}\s*$/gm, '')
    // 强调 / 删除线 / 脚注
    .replace(/(\*\*|__)([^*_]+)\1/g, '$2')
    .replace(/(\*|_)([^*_]+)\1/g, '$2')
    .replace(/~~([^~]+)~~/g, '$1')
    .replace(/\[\^[^\]]*\]/g, '')
    // HTML 标签
    .replace(/<\/?[^>]+>/g, ' ')
    .replace(/&[a-z#0-9]+;/gi, ' ')
    .replace(/\s+/g, ' ')
    .trim()
}

/** 统计纯文本：中文字符数 / 英文词数（英文单词按 2 个汉字折算） */
export function countStats(text) {
  const t = text || ''
  const cjk = (t.match(/[\u4e00-\u9fa5\u3400-\u4dbf]/g) || []).length
  const enWords = (t.match(/[A-Za-z0-9]+(?:['’-][A-Za-z0-9]+)*/g) || []).length
  // 阅读时长：中文按 400 字/分钟、英文按 120 词/分钟估算（取整，最少 1 分钟）
  const minutes = Math.max(1, Math.round(cjk / 400 + enWords / 120))
  return { chars: t.length, cjk, enWords, minutes }
}

/** 给定 Markdown 内容直接返回「字数 · 分钟」统计对象 */
export function articleStats(md) {
  return countStats(stripMarkdown(md))
}

/** 解析 tags 字段（英文逗号分隔，自动去空/去重） */
export function parseTags(raw) {
  if (!raw) return []
  const seen = new Set()
  return String(raw)
    .split(',')
    .map((s) => s.trim())
    .filter((s) => s && !seen.has(s) && seen.add(s))
}

/** 格式化字数：1234 → "1,234" */
export function formatNumber(n) {
  return Number(n || 0).toLocaleString('en-US')
}

/** 取标签聚合列表：[{ name, count }]，count 降序 */
export function aggregateTags(articles) {
  const map = new Map()
  ;(articles || []).forEach((a) => {
    parseTags(a.tags).forEach((t) => map.set(t, (map.get(t) || 0) + 1))
  })
  return [...map.entries()].map(([name, count]) => ({ name, count })).sort((a, b) => b.count - a.count || a.name.localeCompare(b.name, 'zh'))
}

/* ===================== 本地模糊搜索（标题 + 摘要 + 正文 + 标签） ===================== */

const CJK = /[\u4e00-\u9fa5]/g

/** 将查询切成检索词（连续中文为一段；英文按空格分词） */
export function tokenizeQuery(q) {
  const s = (q || '').trim().toLowerCase()
  if (!s) return []
  const parts = s.match(/[\u4e00-\u9fa5]+|[a-z0-9]+(?:['’-][a-z0-9]+)*/g)
  return parts || []
}

/**
 * 字段打分：命中越靠前、越连续、越多次，得分越高
 * 中文子串精确命中 +15；分词命中 +8；越靠前位置加权
 */
function scoreField(text, tokens, whole) {
  if (!text) return 0
  const t = text.toLowerCase()
  let score = 0
  // 整串（中文按连续片段）直接子串命中：权重最高
  if (whole && t.includes(whole)) {
    score += 15
    const idx = t.indexOf(whole)
    score += Math.max(0, (20 - idx / Math.max(t.length, 1) * 20)) * 0.2 // 位置靠前加分
  }
  tokens.forEach((tok) => {
    let from = 0
    let hits = 0
    while ((from = t.indexOf(tok, from)) !== -1) {
      hits++
      score += 6
      score += Math.max(0, (30 - from) * 0.12) // 越靠前加分越多
      from += tok.length
    }
    if (hits) score += Math.min(hits, 4)
  })
  return score
}

/**
 * 全文评分：title ×3、summary ×2、tags ×2、content ×1，返回综合得分
 */
export function scoreArticle(article, tokens, whole) {
  if (!tokens.length) return 0
  const title = scoreField(article.title, tokens, whole) * 3
  const summary = scoreField(article.summary, tokens, whole) * 2
  const tags = scoreField((article.tags || '').replace(/,/g, ' '), tokens, whole) * 2
  const content = scoreField(stripMarkdown(article.content), tokens, whole)
  return title + summary + tags + content
}

/** 生成命中片段（取首个命中点附近 ±radius 字），返回 { before, hit, after, html } */
export function makeSnippet(text, tokens, whole, radius = 46) {
  const t = String(text || '').replace(/\s+/g, ' ')
  if (!t) return { html: '' }
  const lower = t.toLowerCase()
  let idx = -1
  let hitLen = 0
  if (whole && (idx = lower.indexOf(whole)) !== -1) {
    hitLen = whole.length
  } else {
    for (const tok of tokens) {
      const i = lower.indexOf(tok)
      if (i !== -1 && (idx === -1 || i < idx)) {
        idx = i
        hitLen = tok.length
      }
    }
  }
  if (idx === -1) return { html: escapeHtml(t.slice(0, radius * 2)) }
  const start = Math.max(0, idx - radius)
  const end = Math.min(t.length, idx + hitLen + radius)
  let head = start > 0 ? '…' : ''
  let tail = end < t.length ? '…' : ''
  return { html: head + highlightText(t.slice(start, end), tokens, whole) + tail }
}

/** 全文高亮，返回带 <mark> 的 html（自动转义原文） */
export function highlightText(text, tokens, whole) {
  let t = escapeHtml(String(text || ''))
  const ranges = []
  const lower = t.toLowerCase()
  const push = (tok) => {
    let from = 0
    while ((from = lower.indexOf(tok, from)) !== -1) {
      ranges.push([from, from + tok.length])
      from += tok.length
    }
  }
  if (whole && lower.includes(whole)) push(whole)
  tokens.forEach(push)
  if (!ranges.length) return t
  ranges.sort((a, b) => a[0] - b[0])
  // 合并重叠区间
  const merged = []
  for (const r of ranges) {
    const last = merged[merged.length - 1]
    if (last && r[0] <= last[1]) last[1] = Math.max(last[1], r[1])
    else merged.push([...r])
  }
  let html = ''
  let pos = 0
  merged.forEach(([s, e]) => {
    html += t.slice(pos, s) + '<mark>' + t.slice(s, e) + '</mark>'
    pos = e
  })
  html += t.slice(pos)
  return html
}

export function escapeHtml(s) {
  return String(s).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;')
}

export { CJK }
