<template>
  <!-- 文章列表页：独立路由 /blog（参考 xnmoe.com 的 Blog 页面）
       搜索 / 排序 / 分类筛选 / 分页 / 骨架屏 / 信息增强 -->
  <CardPanel title="Blog" icon="book">
    <div class="cat-tabs" role="tablist" aria-label="分类筛选">
      <button
        class="cat-tab"
        :class="{ active: !categoryId }"
        type="button"
        @click="switchCategory(null)"
      >全部</button>
      <button
        v-for="c in categories"
        :key="c.id"
        class="cat-tab"
        :class="{ active: categoryId === c.id }"
        type="button"
        @click="switchCategory(c.id)"
      >{{ c.name }}</button>
    </div>

    <div class="toolbar">
      <label class="search-box">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><circle cx="11" cy="11" r="7"/><path d="m20 20-3.5-3.5"/></svg>
        <input
          v-model="keyword"
          type="search"
          placeholder="搜索标题 / 摘要 / 正文…"
          aria-label="搜索文章"
          @input="onKeywordInput"
          @keyup.enter="onKeywordEnter"
        />
      </label>
      <div class="sort-tabs" role="tablist" aria-label="排序方式">
        <button
          v-for="t in sortTabs"
          :key="t.key"
          class="cat-tab"
          :class="{ active: sort === t.key }"
          type="button"
          @click="switchSort(t.key)"
        >{{ t.label }}</button>
      </div>
    </div>

    <!-- 加载骨架屏 -->
    <div v-if="loading" class="blog-list" aria-hidden="true">
      <div v-for="i in 5" :key="i" class="skeleton-item">
        <div class="sk-block sk-date"></div>
        <div class="skeleton-body">
          <div class="sk-block" style="width: 62%"></div>
          <div class="sk-block" style="width: 88%"></div>
          <div class="sk-block" style="width: 40%"></div>
        </div>
      </div>
    </div>

    <div v-else-if="articles.length" class="blog-list">
      <router-link v-for="item in articles" :key="item.id" class="blog-item" :to="`/article/${item.id}`">
        <div class="blog-date">{{ formatDate(item.createTime) }}</div>
        <div class="blog-body">
          <div class="blog-title">{{ item.title }}</div>
          <div class="blog-summary">{{ item.summary || '点击阅读全文 →' }}</div>
          <div class="blog-meta">
            <span class="meta-item">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="8" r="4"/><path d="M4 21c0-4 3.6-6 8-6s8 2 8 6"/></svg>
              {{ item.authorNickname || 'Lynn' }}
            </span>
            <span class="meta-dot">·</span>
            <span class="meta-item">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 3c3 3.2 3.4 6.4 1.5 9-1.4 1.9-3.8 2-5.2.4-1.4-1.7-.4-4.6 2-6.4"/><path d="M7.5 16.5c2.4 1.8 4.4 1.5 5.8 0"/></svg>
              阅读 {{ readMinutes(item) }} 分钟
            </span>
            <span class="tag">{{ item.categoryName || 'Article' }}</span>
          </div>
        </div>
        <div class="blog-stats">
          <span class="stat">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7-10-7-10-7Z"/><circle cx="12" cy="12" r="3"/></svg>
            {{ item.viewCount || 0 }}
          </span>
          <span class="stat stat-like">
            <svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 20.6S3 14.9 3 9.2C3 6 5.5 4 7.7 4c2 0 3.5 1.4 4.3 2.5C12.8 5.4 14.3 4 16.3 4 18.5 4 21 6 21 9.2c0 5.7-9 11.4-9 11.4Z"/></svg>
            {{ item.likeCount || 0 }}
          </span>
        </div>
      </router-link>
    </div>

    <div v-else class="blog-empty">
      {{ keyword ? '没有找到相关文章 ♥' : (categoryId ? '这个分类还没有文章 ♥' : '还没有发布文章，敬请期待 ♥') }}
    </div>

    <div v-if="totalPages > 1" class="pager">
      <button class="pager-btn" type="button" :disabled="page <= 1" @click="go(page - 1)">← 上一页</button>
      <span class="pager-info">{{ page }} / {{ totalPages }}</span>
      <button class="pager-btn" type="button" :disabled="page >= totalPages" @click="go(page + 1)">下一页 →</button>
    </div>
  </CardPanel>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import CardPanel from '../components/CardPanel.vue'
import { listArticles, listCategories } from '../api/article'

const articles = ref([])
const categories = ref([])
const categoryId = ref(null)
const keyword = ref('')
const sort = ref('latest')
const loading = ref(false)
const page = ref(1)
const size = 10
const total = ref(0)
const totalPages = ref(1)

const sortTabs = [
  { key: 'latest', label: '最新' },
  { key: 'hot', label: '最热' },
  { key: 'liked', label: '点赞' }
]

let searchTimer = null

function formatDate(value) {
  if (!value) return ''
  const d = String(value).slice(0, 10).split('-')
  return `${d[1]}.${d[2]}`
}

// 估算阅读时长：中文约 500 字/分钟
function readMinutes(item) {
  const len = (item.content || item.summary || '').length
  return Math.max(1, Math.round(len / 500))
}

async function load() {
  loading.value = true
  const params = { sort: sort.value }
  if (categoryId.value) params.categoryId = categoryId.value
  const kw = keyword.value.trim()
  if (kw) params.keyword = kw
  try {
    const { data } = await listArticles(page.value, size, params)
    articles.value = data.records || []
    total.value = data.total || 0
    totalPages.value = Math.max(1, Math.ceil(total.value / size))
  } catch (e) {
    articles.value = []
    total.value = 0
    totalPages.value = 1
  } finally {
    loading.value = false
  }
}

function switchCategory(id) {
  categoryId.value = id
  page.value = 1
  load()
}

function switchSort(key) {
  if (sort.value === key) return
  sort.value = key
  page.value = 1
  load()
}

// 搜索防抖：停顿 350ms 自动搜索
function onKeywordInput() {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => {
    page.value = 1
    load()
  }, 350)
}

// 回车立即搜索
function onKeywordEnter() {
  clearTimeout(searchTimer)
  page.value = 1
  load()
}

function go(p) {
  page.value = p
  load()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(async () => {
  load()
  try {
    const { data } = await listCategories()
    categories.value = Array.isArray(data) ? data : []
  } catch (e) {
    categories.value = []
  }
})

onBeforeUnmount(() => clearTimeout(searchTimer))
</script>

<style scoped>
.cat-tabs{display:flex;flex-wrap:wrap;gap:8px;margin-bottom:12px}
.cat-tab{display:inline-flex;padding:5px 14px;border:1px solid var(--pink-200,#ffd0e1);border-radius:999px;background:#fff;color:var(--muted,#8a6475);font-size:12px;font-weight:700;cursor:pointer;transition:all .2s ease}
.cat-tab:hover{color:var(--berry,#a63b64);border-color:var(--pink-300,#ffb7d0)}
.cat-tab.active{background:linear-gradient(180deg,var(--pink-300,#ffb7d0),var(--pink-400,#f9a8c4));border-color:transparent;color:#fff;box-shadow:0 4px 10px rgba(227,91,141,.18)}
.toolbar{display:flex;flex-wrap:wrap;align-items:center;gap:10px;margin-bottom:14px}
.search-box{display:flex;align-items:center;gap:7px;flex:1 1 200px;min-width:0;padding:7px 13px;border:1px solid var(--pink-200,#ffd0e1);border-radius:999px;background:#fff;color:var(--muted,#8a6475);transition:border-color .2s ease,box-shadow .2s ease}
.search-box:focus-within{border-color:var(--rose,#e45b8d);box-shadow:0 0 0 3px rgba(228,91,141,.1)}
.search-box svg{width:15px;height:15px;flex:none}
.search-box input{flex:1;min-width:0;border:0;outline:0;background:transparent;color:var(--ink,#4a2b3a);font-size:12px;font-family:inherit}
.search-box input::placeholder{color:var(--muted,#8a6475);opacity:.7}
.sort-tabs{display:flex;gap:6px}
.blog-list{display:grid;gap:10px}
.blog-item{display:grid;grid-template-columns:52px 1fr auto;gap:12px;align-items:center;padding:12px 14px;border:1px solid var(--pink-100,#ffe7f0);border-radius:14px;background:#fff;transition:transform .2s ease,border-color .2s ease,box-shadow .2s ease}
.blog-item:hover{transform:translateY(-2px);border-color:var(--pink-300,#ffb7d0);box-shadow:0 8px 18px rgba(183,85,129,.1)}
.blog-date{display:grid;place-items:center;width:50px;min-height:50px;border-radius:12px;background:var(--lemon,#fff1b8);color:var(--berry,#a63b64);font-size:12px;font-weight:800;text-align:center;line-height:1.2}
.blog-body{min-width:0}
.blog-title{color:var(--ink,#4a2b3a);font-size:14px;font-weight:800;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}
.blog-summary{margin-top:3px;color:var(--muted,#8a6475);font-size:12px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}
.blog-meta{display:flex;align-items:center;gap:6px;margin-top:6px;flex-wrap:wrap}
.meta-item{display:inline-flex;align-items:center;gap:4px;color:var(--muted,#8a6475);font-size:11px}
.meta-item svg{width:12px;height:12px}
.meta-dot{color:var(--pink-300,#ffb7d0);font-size:11px}
.tag{display:inline-flex;padding:2px 9px;border-radius:999px;background:var(--sky,#d9edfb);color:#35698a;font-size:10px;font-weight:800;white-space:nowrap}
.blog-stats{display:flex;flex-direction:column;gap:5px;align-items:flex-end;color:var(--muted,#8a6475)}
.stat{display:inline-flex;align-items:center;gap:4px;font-size:11px;font-weight:700;white-space:nowrap}
.stat svg{width:13px;height:13px}
.stat-like{color:var(--rose,#e45b8d)}
.blog-empty{padding:32px 0;text-align:center;color:var(--muted,#8a6475);font-size:13px}
.pager{display:flex;align-items:center;justify-content:center;gap:16px;margin-top:16px}
.pager-btn{display:inline-flex;padding:7px 16px;border:1px solid var(--pink-300,#ffb7d0);border-radius:999px;background:#fff;color:var(--berry,#a63b64);font-size:12px;font-weight:800;cursor:pointer;transition:all .2s ease}
.pager-btn:hover:not(:disabled){background:var(--pink-50,#fff7fa);border-color:var(--pink-400,#f9a8c4)}
.pager-btn:disabled{opacity:.45;cursor:not-allowed}
.pager-info{font-size:12px;color:var(--muted,#8a6475);font-weight:700}
.skeleton-item{display:grid;grid-template-columns:52px 1fr;gap:12px;padding:12px 14px;border:1px solid var(--pink-100,#ffe7f0);border-radius:14px;background:#fff}
.skeleton-body{display:grid;gap:8px}
.sk-block{height:13px;border-radius:8px;background:linear-gradient(90deg,#ffe7f0 25%,#fff1f7 37%,#ffe7f0 63%);background-size:400% 100%;animation:shimmer 1.4s ease infinite}
.sk-date{width:50px;height:50px;border-radius:12px}
@keyframes shimmer{0%{background-position:100% 0}100%{background-position:-100% 0}}
@media (max-width:560px){
  .blog-item{grid-template-columns:46px 1fr}
  .blog-item .tag{display:none}
  .blog-stats{flex-direction:row;grid-column:1/-1;justify-content:flex-end;padding-top:2px}
}
</style>
