<template>
  <!-- 站内全文搜索：本地即时模糊匹配 标题/摘要/正文/标签，不依赖后端、结果即输即出 -->
  <CardPanel title="Search · 搜索" icon="search">
    <div class="search-hero">
      <label class="big-search" :class="{ focus: focused }">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" aria-hidden="true"><circle cx="11" cy="11" r="7"/><path d="m20 20-3.5-3.5"/></svg>
        <input
          ref="searchInput"
          v-model="query"
          type="search"
          placeholder="输入关键词，搜索标题 / 摘要 / 正文 / 标签…"
          aria-label="全站搜索"
          @focus="focused = true"
          @blur="focused = false"
        />
        <kbd v-if="!query">Enter</kbd>
      </label>
      <p class="search-tip">
        <span v-if="ready">{{ indexCount }} 篇文章已建立本地索引，输入即搜 ♥</span>
        <span v-else>正在为 {{ indexCount }} 篇文章建立本地索引…</span>
      </p>
    </div>

    <!-- 结果区 -->
    <template v-if="ready">
      <!-- 空关键词：推荐最近文章 -->
      <div v-if="!query.trim()" class="recommend">
        <h3 class="results-title"><AppIcon name="book" :size="14" />随便逛逛 · 最近更新</h3>
        <router-link v-for="item in recent" :key="item.id" class="result-item" :to="`/article/${item.id}`">
          <span class="result-date">{{ shortDate(item.createTime) }}</span>
          <div class="result-body">
            <div class="result-title">{{ item.title }}</div>
            <div class="result-snippet">{{ item.summary || '点击阅读全文 →' }}</div>
          </div>
        </router-link>
      </div>

      <template v-else>
        <p v-if="results.length" class="results-count">找到 <b>{{ results.length }}</b> 条与「{{ query.trim() }}」相关的文章</p>
        <div v-else class="no-result">
          <p>没有找到与「{{ query.trim() }}」相关的文章 ♥</p>
          <p class="no-result-sub">试试更短的关键词，或换个说法？</p>
        </div>
        <div class="result-list">
          <router-link v-for="r in results" :key="r.id" class="result-item" :to="`/article/${r.id}`">
            <span class="result-date">{{ shortDate(r.item.createTime) }}</span>
            <div class="result-body">
              <div class="result-title" v-html="r.titleHtml"></div>
              <div class="result-snippet" v-html="r.snippetHtml"></div>
              <div class="result-meta">
                <span class="tag">{{ r.item.categoryName || 'Article' }}</span>
                <span v-for="t in r.tagHits" :key="t" class="chip-tag"># {{ t }}</span>
                <span class="meta-item">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/></svg>
                  约 {{ r.minutes }} 分钟
                </span>
                <span class="meta-item">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7-10-7-10-7Z"/><circle cx="12" cy="12" r="3"/></svg>
                  {{ r.item.viewCount || 0 }}
                </span>
              </div>
            </div>
          </router-link>
        </div>
      </template>
    </template>

    <div v-else class="no-result"><p>正在准备本地索引…</p></div>
  </CardPanel>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, nextTick } from 'vue'
import CardPanel from '../components/CardPanel.vue'
import AppIcon from '../components/AppIcon.vue'
import { fetchAllPublishedArticles } from '../utils/articleLoader'
import {
  tokenizeQuery, scoreArticle, makeSnippet, highlightText, articleStats, parseTags, stripMarkdown
} from '../utils/articleText'

const query = ref('')
const focused = ref(false)
const searchInput = ref(null)
const all = ref([])
const ready = ref(false)
let inputTimer = null

const indexCount = computed(() => all.value.length)

const tokensAndWhole = computed(() => {
  const q = query.value.trim()
  const whole = q.replace(/\s+/g, '').toLowerCase() // 中文整串
  const tokens = tokenizeQuery(q)
  return { whole: tokens.length === 1 ? whole : whole.length >= 2 ? whole : '', tokens }
})

// 即时搜索结果（数据量小，computed 足够快）
const results = computed(() => {
  const { tokens, whole } = tokensAndWhole.value
  if (!tokens.length) return []
  const scored = all.value
    .map((item) => {
      const score = scoreArticle(item, tokens, whole)
      if (score <= 0) return null
      const titleHtml = highlightText(item.title, tokens, whole)
      // 摘要未命中就用正文片段
      const summaryLower = String(item.summary || '').toLowerCase()
      const hitInSummary = whole && summaryLower.includes(whole)
        || tokens.some((tk) => summaryLower.includes(tk))
      const snippet = makeSnippet(hitInSummary ? item.summary : stripMarkdown(item.content), tokens, whole)
      const tags = parseTags(item.tags)
      const tagHits = tags.filter((t) => {
        const tl = t.toLowerCase()
        return (whole && tl.includes(whole)) || tokens.some((tk) => tl.includes(tk) || tk.includes(tl))
      })
      return {
        item,
        score,
        titleHtml,
        snippetHtml: snippet.html,
        tagHits,
        minutes: articleStats(item.content || item.summary || '').minutes
      }
    })
    .filter(Boolean)
    .sort((a, b) => b.score - a.score)
    .slice(0, 30)
  return scored
})

const recent = computed(() => all.value.slice(0, 5))

function shortDate(value) {
  if (!value) return ''
  const d = String(value).slice(0, 10).split('-')
  return `${d[1]}.${d[2]}`
}

onMounted(async () => {
  try {
    all.value = await fetchAllPublishedArticles()
  } catch (e) {
    all.value = []
  } finally {
    ready.value = true
    // 自动聚焦搜索框，方便直接输入
    await nextTick()
    searchInput.value?.focus()
  }
})

onBeforeUnmount(() => clearTimeout(inputTimer))
</script>

<style scoped>
.search-hero{margin-bottom:6px}
.big-search{display:flex;align-items:center;gap:10px;padding:13px 18px;border:2px solid var(--pink-300,#ffb7d0);border-radius:999px;background:#fff;color:var(--muted,#8a6475);box-shadow:0 6px 18px rgba(183,85,129,.08);transition:border-color .2s ease,box-shadow .2s ease}
.big-search.focus{border-color:var(--rose,#e45b8d);box-shadow:0 0 0 4px rgba(228,91,141,.12),0 8px 22px rgba(183,85,129,.14)}
.big-search svg{width:20px;height:20px;flex:none}
.big-search input{flex:1;min-width:0;border:0;outline:0;background:transparent;color:var(--ink,#4a2b3a);font-size:15px;font-family:inherit}
.big-search input::placeholder{color:var(--muted,#8a6475);opacity:.65}
.big-search kbd{flex:none;padding:2px 9px;border:1px solid var(--pink-200,#ffd0e1);border-bottom-width:2px;border-radius:7px;background:var(--pink-50,#fff7fa);color:var(--muted,#8a6475);font-size:11px;font-weight:700;font-family:inherit}
.search-tip{margin:10px 4px 0;color:var(--muted,#8a6475);font-size:12px}
.results-count{margin:16px 4px 10px;color:var(--muted,#8a6475);font-size:13px}
.results-count b{color:var(--rose,#e45b8d)}
.result-list,.recommend{display:grid;gap:10px;margin-top:12px}
.result-item{display:grid;grid-template-columns:46px 1fr;gap:12px;align-items:start;padding:14px 16px;border:1px solid var(--pink-100,#ffe7f0);border-radius:15px;background:#fff;transition:transform .18s ease,border-color .18s ease,box-shadow .18s ease}
.result-item:hover{transform:translateY(-2px);border-color:var(--pink-300,#ffb7d0);box-shadow:0 8px 18px rgba(183,85,129,.1)}
.result-date{display:grid;place-items:center;height:40px;border-radius:10px;background:var(--lemon,#fff1b8);color:var(--berry,#a63b64);font-size:11.5px;font-weight:800}
.result-body{min-width:0}
.result-title{color:var(--ink,#4a2b3a);font-size:15px;font-weight:800;line-height:1.5}
.result-title :deep(mark){background:linear-gradient(180deg,transparent 55%,var(--pink-300,#ffb7d0) 55%)}
.result-snippet{margin-top:4px;color:var(--muted,#8a6475);font-size:12.5px;line-height:1.75}
.result-snippet :deep(mark){background:var(--lemon,#fff1b8);color:var(--berry,#a63b64);padding:0 1px;border-radius:3px;font-weight:700}
.result-meta{display:flex;align-items:center;gap:7px;flex-wrap:wrap;margin-top:8px}
.result-meta .tag{display:inline-flex;padding:2px 9px;border-radius:999px;background:var(--sky,#d9edfb);color:#35698a;font-size:10px;font-weight:800}
.chip-tag{display:inline-flex;padding:2px 9px;border-radius:999px;border:1px solid var(--pink-200,#ffd0e1);background:#fff;color:var(--rose,#e45b8d);font-size:10px;font-weight:800}
.meta-item{display:inline-flex;align-items:center;gap:4px;color:var(--muted,#8a6475);font-size:11px;font-weight:700}
.meta-item svg{width:12px;height:12px}
.results-title{display:flex;align-items:center;gap:6px;margin:14px 0 0 2px;font-size:14px;color:var(--berry,#a63b64)}
.no-result{padding:36px 0;text-align:center;color:var(--muted,#8a6475);font-size:13px}
.no-result p{margin:4px 0}
.no-result-sub{font-size:12px;opacity:.8}
@media (max-width:560px){
  .result-item{grid-template-columns:1fr}
  .result-date{display:none}
}
</style>
