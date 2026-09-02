<template>
  <!-- 文章归档页：按 年 → 月 的时间线（博客的年轮感 ✦） -->
  <CardPanel title="Archive · 归档" icon="book">
    <p class="arch-summary">
      共 <b>{{ total }}</b> 篇文字，从 {{ firstYear || '?' }} 年写到现在，谢谢你来翻。
    </p>

    <!-- 加载中 -->
    <div v-if="loading" class="blog-list" aria-hidden="true">
      <div v-for="i in 5" :key="i" class="skeleton-item">
        <div class="sk-block" style="width: 16%"></div>
        <div class="sk-block" style="width: 64%"></div>
      </div>
    </div>

    <div v-else-if="!hasTimeline" class="arch-empty">还没有文章，等我来写第一篇 ♥</div>

    <!-- 时间线 -->
    <div v-else class="arch-timeline">
      <section v-for="(months, year) in timeline" :key="year" class="arch-year-block">
        <h3 class="arch-year">
          <span>{{ year }}</span>
          <small>{{ yearCount(year) }} 篇</small>
        </h3>
        <div v-for="(list, month) in months" :key="`${year}-${month}`" class="arch-month-block">
          <div class="arch-month">{{ Number(month) }} 月</div>
          <router-link v-for="item in list" :key="item.id" class="arch-item" :to="`/article/${item.id}`">
            <time class="arch-day">{{ dayOf(item) }}</time>
            <span class="arch-title">{{ item.title }}</span>
            <span class="arch-tags">
              <span v-for="t in tagsOf(item).slice(0, 3)" :key="t" class="chip-tag"># {{ t }}</span>
            </span>
            <span class="arch-meta">
              {{ item.categoryName || '' }}<span class="meta-dot">·</span>{{ readOf(item) }} 分钟
            </span>
            <span class="arch-view">{{ item.viewCount || 0 }} 阅</span>
          </router-link>
        </div>
      </section>
    </div>
  </CardPanel>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import CardPanel from '../components/CardPanel.vue'
import { fetchAllPublishedArticles } from '../utils/articleLoader'
import { parseTags, articleStats } from '../utils/articleText'

const loading = ref(true)
const list = ref([])

const total = computed(() => list.value.length)
const hasTimeline = computed(() => Object.keys(timeline.value).length > 0)
const firstYear = computed(() => {
  const first = list.value[list.value.length - 1] // 升序的最后一篇 = 最早
  return first ? String(first.createTime).slice(0, 4) : ''
})

// 按 年 → 月 分组（倒序）
const timeline = computed(() => {
  const tree = {}
  list.value.forEach((a) => {
    const s = String(a.createTime || '')
    const year = s.slice(0, 4)
    const month = Number(s.slice(5, 7)) || 0
    if (!month) return
    if (!tree[year]) tree[year] = {}
    if (!tree[year][month]) tree[year][month] = []
    tree[year][month].push(a)
  })
  // 年倒序、月倒序、文章内按时间倒序
  const sorted = {}
  Object.keys(tree).sort((a, b) => b.localeCompare(a)).forEach((y) => {
    sorted[y] = {}
    Object.keys(tree[y]).sort((a, b) => b.localeCompare(a)).forEach((m) => {
      sorted[y][m] = [...tree[y][m]].sort((a, b) => String(b.createTime).localeCompare(String(a.createTime)))
    })
  })
  return sorted
})

function yearCount(year) {
  return Object.values(timeline.value[year] || {}).reduce((n, arr) => n + arr.length, 0)
}

function dayOf(item) {
  const s = String(item.createTime || '')
  const d = Number(s.slice(8, 10))
  return Number.isFinite(d) && d > 0 ? `${d}日` : ''
}

function tagsOf(item) {
  return parseTags(item.tags)
}

function readOf(item) {
  return articleStats(item.content || item.summary || '').minutes
}

onMounted(async () => {
  try {
    list.value = await fetchAllPublishedArticles()
  } catch (e) {
    list.value = []
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.arch-summary{margin:0 0 16px;color:var(--muted,#8a6475);font-size:13px}
.arch-summary b{color:var(--rose,#e45b8d)}
.arch-empty{padding:40px 0;text-align:center;color:var(--muted,#8a6475);font-size:13px}
/* 时间线 */
.arch-timeline{position:relative;padding-left:18px}
.arch-timeline:before{content:"";position:absolute;left:6px;top:6px;bottom:6px;width:2px;border-radius:2px;background:linear-gradient(180deg,var(--pink-300,#ffb7d0),var(--pink-200,#ffd0e1) 70%,transparent)}
.arch-year-block{position:relative;margin-bottom:26px}
.arch-year{position:relative;display:flex;align-items:baseline;gap:10px;margin:0 0 14px;color:var(--berry,#a63b64)}
.arch-year:before{content:"";position:absolute;left:-19px;top:5px;width:11px;height:11px;border-radius:50%;background:linear-gradient(135deg,var(--pink-300,#ffb7d0),var(--rose,#e45b8d));box-shadow:0 0 0 4px #fff,0 0 0 5px var(--pink-200,#ffd0e1)}
.arch-year span{font-size:22px;font-weight:800}
.arch-year small{color:var(--muted,#8a6475);font-size:11px;font-weight:700}
.arch-month-block{margin:0 0 8px 26px}
.arch-month{position:relative;margin:14px 0 8px;color:var(--rose,#e45b8d);font-size:13px;font-weight:800;letter-spacing:1px}
.arch-month:before{content:"";position:absolute;left:-33px;top:6px;width:7px;height:7px;border-radius:50%;background:#fff;border:2px solid var(--rose,#e45b8d)}
.arch-item{display:flex;align-items:center;gap:10px;flex-wrap:wrap;padding:10px 14px;margin-bottom:6px;border:1px solid var(--pink-100,#ffe7f0);border-radius:13px;background:#fff;transition:all .18s ease}
.arch-item:hover{border-color:var(--pink-300,#ffb7d0);transform:translateX(3px);box-shadow:0 5px 14px rgba(183,85,129,.1)}
.arch-day{flex:none;display:inline-grid;place-items:center;min-width:42px;height:30px;border-radius:9px;background:var(--lemon,#fff1b8);color:var(--berry,#a63b64);font-size:11px;font-weight:800}
.arch-title{color:var(--ink,#4a2b3a);font-size:14px;font-weight:800;min-width:0;flex:1 1 180px}
.arch-item:hover .arch-title{color:var(--berry,#a63b64)}
.arch-tags{display:flex;gap:5px;flex-wrap:wrap}
.chip-tag{display:inline-flex;padding:2px 8px;border-radius:999px;border:1px solid var(--pink-200,#ffd0e1);background:#fff;color:var(--rose,#e45b8d);font-size:10px;font-weight:800}
.arch-meta{color:var(--muted,#8a6475);font-size:11px;font-weight:700;white-space:nowrap}
.meta-dot{color:var(--pink-300,#ffb7d0);margin:0 4px}
.arch-view{color:var(--muted,#8a6475);font-size:11px;white-space:nowrap}
/* 骨架屏 */
.skeleton-item{display:grid;gap:10px;padding:14px 16px;margin-bottom:6px;border:1px solid var(--pink-100,#ffe7f0);border-radius:13px;background:#fff}
.sk-block{height:13px;border-radius:8px;background:linear-gradient(90deg,#ffe7f0 25%,#fff1f7 37%,#ffe7f0 63%);background-size:400% 100%;animation:shimmer 1.4s ease infinite}
@keyframes shimmer{0%{background-position:100% 0}100%{background-position:-100% 0}}
@media (max-width:560px){
  .arch-meta,.arch-view{display:none}
}
</style>
