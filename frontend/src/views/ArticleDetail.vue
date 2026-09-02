<template>
  <div class="article-page">
    <!-- 返回列表 -->
    <div class="article-topbar">
      <router-link to="/blog" class="back-link">
        <AppIcon name="chevron-left" :size="15" />
        返回列表
      </router-link>
      <div class="topbar-extra">
        <router-link to="/archive" class="topbar-link">
          <AppIcon name="book" :size="13" />归档
        </router-link>
        <router-link to="/tags" class="topbar-link">
          <AppIcon name="heart" :size="13" />标签云
        </router-link>
      </div>
    </div>

    <!-- 加载骨架屏 -->
    <div v-if="status === 'loading'" class="paper-card skeleton-card" aria-hidden="true">
      <div class="sk-block" style="width: 32%"></div>
      <div class="sk-block sk-title"></div>
      <div class="sk-block"></div>
      <div class="sk-block" style="width: 92%"></div>
      <div class="sk-block" style="width: 78%"></div>
    </div>

    <!-- 正文卡片 -->
    <article v-else-if="status === 'ok'" class="paper-card" @click="onArticleClick">
      <header class="paper-head">
        <div class="paper-flags">
          <router-link v-if="article.categoryId" class="tag" :to="{ path: '/blog', query: { categoryId: article.categoryId } }">{{ article.categoryName || '随笔' }}</router-link>
          <time class="paper-date">{{ article.createTime }}</time>
        </div>
        <h1 class="paper-title">{{ article.title }}</h1>
        <div class="paper-meta">
          <span class="meta-author">
            <span class="author-avatar">{{ (article.authorNickname || '匿')[0] }}</span>
            {{ article.authorNickname || '匿名作者' }}
          </span>
          <span class="meta-item">
            <AppIcon name="eye" :size="14" />
            浏览 {{ article.viewCount || 0 }}
          </span>
          <span v-if="stats" class="meta-item meta-stats" :title="`纯文本约 ${formatNumber(stats.chars)} 字`">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/></svg>
            {{ formatNumber(stats.chars) }} 字 · 约 {{ stats.minutes }} 分钟
          </span>
          <button class="like-btn" :class="{ liked: article.isLiked }" type="button" :disabled="liking" @click="handleLike">
            <AppIcon name="heart" :size="14" />
            {{ article.isLiked ? '已赞' : '点赞' }} {{ article.likeCount || 0 }}
          </button>
        </div>
        <!-- 标签行：点击筛选同标签文章 -->
        <div v-if="tags.length" class="paper-tags">
          <router-link
            v-for="t in tags"
            :key="t"
            class="chip-tag"
            :to="{ path: '/blog', query: { tag: t } }"
          ># {{ t }}</router-link>
        </div>
      </header>

      <div v-if="article.coverImage" class="paper-cover">
        <img :src="article.coverImage" :alt="article.title" />
      </div>

      <!-- 目录（中屏折叠条） -->
      <div v-if="tocWrapVisible" class="toc-panel-wrap">
        <ArticleToc :headings="headings" :active-id="activeId" variant="panel" collapsible @go="onTocGo" />
      </div>

      <!-- 正文 + 宽屏右侧目录 -->
      <div class="paper-body-wrap">
        <div ref="contentEl" class="paper-content">
          <MdPreview :modelValue="article.content || ''" />
        </div>
        <div v-if="headings.length" class="toc-rail-wrap" aria-label="文章目录">
          <ArticleToc :headings="headings" :active-id="activeId" variant="rail" @go="onTocGo" />
        </div>
      </div>

      <footer class="paper-foot">—— END · Lynn's Blog ♥ ——</footer>
    </article>

    <!-- 相关文章（同分类优先，不足补最新） -->
    <section v-if="status === 'ok' && related.length" class="paper-card related-card" aria-label="相关文章">
      <h2 class="comments-title"><AppIcon name="book" :size="16" />相关文章<small>你可能还想读 ♥</small></h2>
      <div class="related-grid">
        <router-link v-for="r in related" :key="r.id" class="related-item" :to="`/article/${r.id}`">
          <div v-if="r.coverImage" class="related-thumb"><img :src="r.coverImage" :alt="r.title" loading="lazy" /></div>
          <span v-else class="related-date">{{ shortDate(r.createTime) }}</span>
          <div class="related-body">
            <div class="related-title">{{ r.title }}</div>
            <div class="related-summary">{{ r.summary || r.categoryName || '点击阅读全文 →' }}</div>
            <div class="related-meta">
              <span class="meta-item">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7-10-7-10-7Z"/><circle cx="12" cy="12" r="3"/></svg>
                {{ r.viewCount || 0 }}
              </span>
              <span class="meta-dot">·</span>
              <span class="meta-item">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/></svg>
                约 {{ articleMinutes(r) }} 分钟
              </span>
              <span class="tag">{{ r.categoryName || 'Article' }}</span>
            </div>
          </div>
        </router-link>
      </div>
    </section>

    <!-- 上下篇文章 -->
    <nav v-if="status === 'ok'" class="neighbors-card" aria-label="上下篇文章">
      <router-link v-if="neighbors.prevId" :to="`/article/${neighbors.prevId}`" class="neighbor-link">
        <small>← 上一篇</small>
        <span class="neighbor-title">{{ neighbors.prevTitle }}</span>
      </router-link>
      <span v-else class="neighbor-link disabled">← 上一篇：没有了</span>
      <router-link v-if="neighbors.nextId" :to="`/article/${neighbors.nextId}`" class="neighbor-link">
        <small>下一篇 →</small>
        <span class="neighbor-title">{{ neighbors.nextTitle }}</span>
      </router-link>
      <span v-else class="neighbor-link disabled">下一篇：没有了 →</span>
    </nav>

    <!-- 评论区 -->
    <section v-if="status === 'ok'" class="paper-card comments-card">
      <h2 class="comments-title"><AppIcon name="message" :size="16" />评论</h2>
      <div class="comment-editor">
        <el-input
          v-model="commentText"
          type="textarea"
          :rows="3"
          maxlength="300"
          show-word-limit
          placeholder="写下你的评论"
        />
        <el-button type="primary" round :loading="submitting" @click="handleComment">发表评论</el-button>
      </div>
      <div v-if="comments.length" class="comment-list">
        <div v-for="item in comments" :key="item.id" class="comment-item">
          <span class="comment-avatar">{{ (item.authorNickname || '匿')[0] }}</span>
          <div class="comment-body">
            <div class="comment-head">
              <b>{{ item.authorNickname || '匿名' }}</b>
              <time>{{ item.createTime }}</time>
            </div>
            <p>{{ item.content }}</p>
          </div>
        </div>
      </div>
      <p v-else class="comment-empty">还没有评论，来抢沙发 ~ ♥</p>
    </section>

    <!-- 错误态 -->
    <div v-else-if="status === 'error'" class="paper-card paper-empty">
      <p>{{ errorMsg || '文章不存在或已删除' }}</p>
      <el-button type="primary" plain round @click="load">重试</el-button>
    </div>

    <!-- 图片灯箱：正文/封面图点击放大 -->
    <ImageLightbox ref="lightboxRef" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, nextTick, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { MdPreview } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import {
  getArticleDetail,
  likeArticle,
  getArticleNeighbors,
  listArticleComments,
  addArticleComment,
  listArticles
} from '../api/article'
import AppIcon from '../components/AppIcon.vue'
import ArticleToc from '../components/ArticleToc.vue'
import ImageLightbox from '../components/ImageLightbox.vue'
import { useAuth } from '../composables/useAuth'
import { articleStats, parseTags, formatNumber } from '../utils/articleText'

const route = useRoute()
const router = useRouter()
const { isLoggedIn } = useAuth()
const article = ref(null)
const status = ref('loading')
const errorMsg = ref('')
const liking = ref(false)
const neighbors = ref({})
const comments = ref([])
const commentText = ref('')
const submitting = ref(false)
const related = ref([])
const lightboxRef = ref(null)

// ===== 图片灯箱：正文/封面图点击放大（a/button 内的图不拦截，保留原交互） =====
function onArticleClick(e) {
  const img = e.target.closest('img')
  if (!img || img.closest('a, button')) return
  lightboxRef.value?.open(img.currentSrc || img.src, img.alt)
}

// ===== 阅读统计 / 标签 =====
const stats = computed(() => (article.value ? articleStats(article.value.content) : null))
const tags = computed(() => (article.value ? parseTags(article.value.tags) : []))

// ===== 文章目录 TOC =====
const contentEl = ref(null)
const headings = ref([])
const activeId = ref('')
let tocObserver = null
let scanTimer = null

// 有目录时展示面板区；宽屏时右侧 rail 单独出现（CSS 控制）
const tocWrapVisible = computed(() => headings.value.length > 0)

function scanHeadings() {
  const root = contentEl.value
  const preview = root?.querySelector('.md-editor-preview') || root
  if (!preview) return
  const list = []
  const nodes = preview.querySelectorAll('h1,h2,h3,h4,h5,h6')
  nodes.forEach((el, i) => {
    const text = (el.textContent || '').trim()
    if (!text) return
    const id = `sec-${i}`
    el.id = id
    el.style.scrollMarginTop = '96px'
    list.push({ id, text, level: Number(el.tagName.charAt(1)) })
  })
  headings.value = list
  rebuildObserver(list.map((h) => h.id))
}

function rebuildObserver(ids) {
  if (tocObserver) {
    tocObserver.disconnect()
    tocObserver = null
  }
  if (!ids.length) return
  tocObserver = new IntersectionObserver(
    (entries) => {
      let best = null
      let bestTop = Infinity
      entries.forEach((entry) => {
        if (entry.isIntersecting && entry.boundingClientRect.top < bestTop) {
          bestTop = entry.boundingClientRect.top
          best = entry.target.id
        }
      })
      if (best) activeId.value = best
    },
    { rootMargin: '-12% 0px -76% 0px', threshold: 0 }
  )
  ids.forEach((id) => {
    const el = document.getElementById(id)
    if (el) tocObserver.observe(el)
  })
}

function onTocGo(h) {
  activeId.value = h.id // 点击立即高亮，滚动中由 observer 校正
  const el = document.getElementById(h.id)
  if (el) el.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

// ===== 相关文章：同分类取 3 篇，不足用最新补 =====
async function loadRelated() {
  const currentId = Number(route.params.id)
  const picked = []
  try {
    const params = { sort: 'latest' }
    if (article.value.categoryId) params.categoryId = article.value.categoryId
    const { data } = await listArticles(1, 8, params)
    ;(data.records || []).forEach((r) => {
      if (picked.length >= 3) return
      if (Number(r.id) !== currentId) picked.push(r)
    })
  } catch (e) {
    /* 静默 */
  }
  // 同分类不足 3 篇：用全站最新补位（排除已选）
  if (picked.length < 3) {
    try {
      const { data } = await listArticles(1, 6, { sort: 'latest' })
      const ids = new Set(picked.map((p) => Number(p.id)))
      ;(data.records || []).forEach((r) => {
        if (picked.length >= 3) return
        if (Number(r.id) !== currentId && !ids.has(Number(r.id))) picked.push(r)
      })
    } catch (e) {
      /* 静默 */
    }
  }
  related.value = picked
}

function articleMinutes(item) {
  const s = articleStats(item.content || item.summary || '')
  return s.minutes
}

function shortDate(value) {
  if (!value) return ''
  const d = String(value).slice(0, 10).split('-')
  return `${d[1]}.${d[2]}`
}

async function load() {
  status.value = 'loading'
  errorMsg.value = ''
  try {
    const { data } = await getArticleDetail(route.params.id)
    article.value = data
    status.value = 'ok'
    // 等 MdPreview 把内容渲染进 DOM 后再扫描标题
    await nextTick()
    scanTimer = setTimeout(() => scanHeadings(), 60)
    loadNeighbors()
    loadComments()
    loadRelated()
  } catch (e) {
    status.value = 'error'
    errorMsg.value = '文章加载失败，请稍后重试'
  }
}

async function loadNeighbors() {
  try {
    const { data } = await getArticleNeighbors(route.params.id)
    neighbors.value = data
  } catch (e) {
    neighbors.value = {}
  }
}

async function loadComments() {
  try {
    const { data } = await listArticleComments(route.params.id)
    comments.value = data.records || []
  } catch (e) {
    comments.value = []
  }
}

async function handleLike() {
  if (!isLoggedIn.value) {
    ElMessage.warning('登录后才能点赞')
    router.push({ path: '/login', query: { redirect: route.fullPath } })
    return
  }
  liking.value = true
  try {
    const { data } = await likeArticle(route.params.id)
    article.value.isLiked = data.liked
    article.value.likeCount = data.count
    ElMessage.success(data.liked ? '已点赞 ♥' : '已取消点赞')
  } catch (e) {
    // 错误提示已由请求封装统一处理
  } finally {
    liking.value = false
  }
}

async function handleComment() {
  if (!commentText.value.trim()) return
  submitting.value = true
  try {
    const { data } = await addArticleComment(route.params.id, commentText.value.trim())
    comments.value.push(data)
    commentText.value = ''
  } finally {
    submitting.value = false
  }
}

watch(
  () => route.params.id,
  () => {
    related.value = []
    load()
  }
)

onMounted(() => load())
onBeforeUnmount(() => {
  if (tocObserver) tocObserver.disconnect()
  clearTimeout(scanTimer)
})
</script>

<style scoped>
/* ===== 三丽鸥手账风文章详情 ===== */
.article-page{display:flex;flex-direction:column;gap:16px;min-width:0}
.article-topbar{display:flex;align-items:center;gap:10px}
.back-link{display:inline-flex;align-items:center;gap:4px;padding:6px 14px;border:1px solid var(--pink-200,#ffd0e1);border-radius:999px;background:var(--cream,#fffaf5);color:var(--rose,#e45b8d);font-size:13px;font-weight:700;box-shadow:0 4px 10px rgba(183,85,129,.1);transition:all .2s ease}
.back-link:hover{color:var(--berry,#a63b64);border-color:var(--pink-300,#ffb7d0);transform:translateY(-1px)}
.topbar-extra{margin-left:auto;display:flex;gap:6px}
.topbar-link{display:inline-flex;align-items:center;gap:4px;padding:6px 12px;border:1px solid var(--pink-100,#ffe7f0);border-radius:999px;background:var(--cream,#fffaf5);color:var(--muted,#8a6475);font-size:12px;font-weight:700}
.topbar-link:hover{color:var(--berry,#a63b64);border-color:var(--pink-300,#ffb7d0);transform:translateY(-1px)}
.paper-card{position:relative;border:1px solid var(--pink-200,#ffd0e1);border-radius:var(--radius-lg,22px);background:var(--cream,#fffaf5);box-shadow:var(--shadow,0 12px 30px rgba(183,85,129,.12));padding:26px 30px 22px}
.paper-head{padding-bottom:18px;border-bottom:1px dashed var(--pink-200,#ffd0e1)}
.paper-flags{display:flex;align-items:center;gap:10px;flex-wrap:wrap;margin-bottom:12px}
.tag{display:inline-flex;padding:3px 12px;border-radius:999px;background:var(--sky,#d9edfb);color:#35698a;font-size:12px;font-weight:800}
a.tag:hover{filter:brightness(.94)}
.paper-date{color:var(--muted,#8a6475);font-size:12px}
.paper-title{margin:0;font-size:30px;line-height:1.35;font-weight:800;letter-spacing:.5px;color:var(--berry,#a63b64)}
.paper-meta{display:flex;align-items:center;flex-wrap:wrap;gap:14px;margin-top:14px;color:var(--muted,#8a6475);font-size:13px}
.meta-item,.meta-author{display:inline-flex;align-items:center;gap:5px}
.meta-stats svg{width:14px;height:14px}
.meta-author{color:var(--ink,#4a2b3a);font-weight:700}
.author-avatar,.comment-avatar{display:inline-grid;place-items:center;width:24px;height:24px;border-radius:50%;background:linear-gradient(135deg,var(--pink-200,#ffd0e1),var(--pink-300,#ffb7d0));color:var(--berry,#a63b64);font-size:12px;font-weight:800}
.like-btn{display:inline-flex;align-items:center;gap:5px;margin-left:auto;padding:5px 16px;border:1px solid var(--pink-300,#ffb7d0);border-radius:999px;background:linear-gradient(135deg,#fff,var(--pink-50,#fff7fa));color:var(--rose,#e45b8d);font-size:13px;font-weight:800;cursor:pointer;box-shadow:0 4px 10px rgba(227,91,141,.12);transition:all .2s ease}
.like-btn:hover{transform:translateY(-1px);border-color:var(--pink-400,#f9a8c4)}
.like-btn:disabled{opacity:.7;cursor:default}
.like-btn.liked{background:linear-gradient(135deg,var(--pink-300,#ffb7d0),var(--rose,#e45b8d));border-color:transparent;color:#fff;box-shadow:0 6px 14px rgba(227,91,141,.3)}
/* 标签 chips */
.paper-tags{display:flex;flex-wrap:wrap;gap:8px;margin-top:12px}
.chip-tag{display:inline-flex;padding:3px 12px;border:1px solid var(--pink-200,#ffd0e1);border-radius:999px;background:linear-gradient(135deg,#fff,var(--pink-50,#fff7fa));color:var(--rose,#e45b8d);font-size:12px;font-weight:800;transition:all .2s ease}
.chip-tag:hover{border-color:var(--rose,#e45b8d);color:var(--berry,#a63b64);transform:translateY(-1px);box-shadow:0 4px 10px rgba(228,91,141,.14)}
.paper-cover{margin:20px 0 0;overflow:hidden;border:3px solid #fff;border-radius:18px;box-shadow:0 8px 20px rgba(183,85,129,.14)}
.paper-cover img{display:block;width:100%;max-height:420px;object-fit:cover}
/* 图片可点击放大提示 */
.paper-cover img,.paper-content img{cursor:zoom-in;transition:transform .25s ease,box-shadow .25s ease}
.paper-cover img:hover,.paper-content img:hover{transform:scale(1.008)}
/* 目录面板（中屏折叠条） */
.toc-panel-wrap{margin-top:16px}
/* 正文 + 右侧目录栅格 */
.paper-body-wrap{display:grid;grid-template-columns:minmax(0,1fr);gap:18px;margin-top:22px}
.toc-rail-wrap{display:none}
.paper-content{padding:24px 28px;background:#fff;border:1px solid var(--pink-100,#ffe7f0);border-radius:16px;min-width:0}
.paper-foot{margin-top:20px;text-align:center;color:var(--pink-400,#f9a8c4);font-size:12px;letter-spacing:2px}
/* Markdown 预览三丽鸥化 */
.paper-content :deep(.md-editor){background:transparent;box-shadow:none}
.paper-content :deep(.md-editor-preview){background:transparent;color:var(--ink,#4a2b3a);font-size:15px;line-height:1.9;padding:0}
.paper-content :deep(.md-editor-preview h1),
.paper-content :deep(.md-editor-preview h2),
.paper-content :deep(.md-editor-preview h3){color:var(--berry,#a63b64);margin-top:1.4em;scroll-margin-top:96px}
.paper-content :deep(.md-editor-preview a){color:var(--rose,#e45b8d)}
.paper-content :deep(.md-editor-preview blockquote){border-left:3px solid var(--pink-300,#ffb7d0);background:var(--pink-50,#fff7fa);border-radius:0 10px 10px 0;padding:8px 14px;color:var(--muted,#8a6475)}
.paper-content :deep(.md-editor-preview code){background:var(--pink-100,#ffe7f0);color:var(--berry,#a63b64);border-radius:6px;padding:1px 6px;font-size:.9em}
.paper-content :deep(.md-editor-preview pre){background:#fff;border:1px solid var(--pink-100,#ffe7f0);border-radius:14px;padding:14px 16px}
.paper-content :deep(.md-editor-preview pre code){background:transparent;color:inherit;padding:0}
.paper-content :deep(.md-editor-preview img){border-radius:14px}
.paper-content :deep(.md-editor-preview table){border-collapse:collapse;margin:1em 0}
.paper-content :deep(.md-editor-preview th),
.paper-content :deep(.md-editor-preview td){border:1px solid var(--pink-200,#ffd0e1);padding:6px 12px}
/* 相关文章 */
.related-card{padding-top:22px}
.related-card .comments-title{display:flex;align-items:center;gap:6px;margin:0 0 14px;font-size:17px;color:var(--berry,#a63b64)}
.related-card .comments-title small{margin-left:auto;font-size:11px;color:var(--muted,#8a6475);font-weight:700}
.related-grid{display:grid;grid-template-columns:repeat(auto-fill,minmax(230px,1fr));gap:12px}
.related-item{display:flex;flex-direction:column;gap:10px;min-width:0;padding:14px;border:1px solid var(--pink-100,#ffe7f0);border-radius:16px;background:linear-gradient(180deg,#fff,#fffaf5);transition:transform .2s ease,border-color .2s ease,box-shadow .2s ease}
.related-item:hover{transform:translateY(-2px);border-color:var(--pink-300,#ffb7d0);box-shadow:0 8px 18px rgba(183,85,129,.12)}
.related-thumb{height:110px;overflow:hidden;border-radius:12px;border:2px solid #fff;box-shadow:0 3px 10px rgba(183,85,129,.14)}
.related-thumb img{display:block;width:100%;height:100%;object-fit:cover}
.related-date{display:grid;place-items:center;height:54px;border-radius:12px;background:var(--lemon,#fff1b8);color:var(--berry,#a63b64);font-size:13px;font-weight:800}
.related-body{min-width:0;flex:1}
.related-title{color:var(--ink,#4a2b3a);font-size:14.5px;font-weight:800;line-height:1.45;display:-webkit-box;-webkit-line-clamp:2;-webkit-box-orient:vertical;overflow:hidden}
.related-summary{margin-top:5px;color:var(--muted,#8a6475);font-size:12px;line-height:1.55;display:-webkit-box;-webkit-line-clamp:2;-webkit-box-orient:vertical;overflow:hidden}
.related-meta{display:flex;align-items:center;gap:6px;margin-top:9px;flex-wrap:wrap}
.related-meta .meta-item{display:inline-flex;align-items:center;gap:4px;color:var(--muted,#8a6475);font-size:11px}
.related-meta .meta-item svg{width:12px;height:12px}
.related-meta .meta-dot{color:var(--pink-300,#ffb7d0);font-size:11px}
.related-meta .tag{font-size:10px;padding:2px 9px}
/* 上下篇 */
.neighbors-card{display:grid;grid-template-columns:1fr 1fr;gap:12px;padding:14px;background:linear-gradient(135deg,var(--pink-100,#ffe7f0),#fff);border:1px solid var(--pink-200,#ffd0e1);border-radius:var(--radius-lg,22px);box-shadow:var(--shadow,0 12px 30px rgba(183,85,129,.12))}
.neighbor-link{display:flex;flex-direction:column;gap:4px;min-width:0;padding:12px 16px;border:1px solid var(--pink-100,#ffe7f0);border-radius:14px;background:#fff;color:var(--ink,#4a2b3a);font-size:13px;font-weight:700;transition:all .2s ease}
.neighbor-link small{color:var(--rose,#e45b8d);font-weight:800;font-size:11px}
.neighbor-title{white-space:nowrap;overflow:hidden;text-overflow:ellipsis}
.neighbor-link:hover{border-color:var(--pink-300,#ffb7d0);transform:translateY(-1px);box-shadow:0 6px 14px rgba(183,85,129,.1)}
.neighbor-link.disabled{color:var(--muted,#8a6475);opacity:.7;cursor:default}
.neighbor-link.disabled:hover{border-color:var(--pink-100,#ffe7f0);transform:none;box-shadow:none}
/* 评论 */
.comments-card{padding-top:22px}
.comments-title{display:flex;align-items:center;gap:6px;margin:0 0 14px;font-size:17px;color:var(--berry,#a63b64)}
.comment-editor{display:flex;flex-direction:column;align-items:flex-end;gap:10px;margin-bottom:18px}
.comment-editor :deep(.el-textarea__inner){border-radius:14px;border:1px solid var(--pink-200,#ffd0e1);background:#fff;color:var(--ink,#4a2b3a);box-shadow:none;transition:border-color .2s ease,box-shadow .2s ease}
.comment-editor :deep(.el-textarea__inner:focus){border-color:var(--rose,#e45b8d);box-shadow:0 0 0 3px rgba(228,91,141,.1)}
.comment-list{display:grid;gap:10px}
.comment-item{display:flex;gap:10px;padding:12px 14px;border:1px solid var(--pink-100,#ffe7f0);border-radius:14px;background:#fff}
.comment-avatar{width:30px;height:30px;flex-shrink:0;margin-top:2px}
.comment-body{flex:1;min-width:0}
.comment-head{display:flex;justify-content:space-between;gap:10px;font-size:12px;color:var(--muted,#8a6475)}
.comment-head b{color:var(--ink,#4a2b3a)}
.comment-body p{margin:6px 0 0;font-size:13px;line-height:1.65;color:var(--ink,#4a2b3a)}
.comment-empty{color:var(--muted,#8a6475);text-align:center;padding:20px 0;font-size:13px}
/* 骨架屏 */
.skeleton-card{display:grid;gap:14px}
.sk-block{height:14px;border-radius:8px;background:linear-gradient(90deg,#ffe7f0 25%,#fff1f7 37%,#ffe7f0 63%);background-size:400% 100%;animation:shimmer 1.4s ease infinite}
.sk-title{height:26px;width:70%}
@keyframes shimmer{0%{background-position:100% 0}100%{background-position:-100% 0}}
/* 错误态 */
.paper-empty{display:flex;flex-direction:column;align-items:center;gap:12px;padding:60px 20px;text-align:center;color:var(--muted,#8a6475);font-size:14px}
.paper-empty p{margin:0}
/* 宽屏：正文 + 右侧固定目录 */
@media (min-width:1180px){
  .paper-body-wrap{grid-template-columns:minmax(0,1fr) 200px}
  .toc-panel-wrap{display:none}
  .toc-rail-wrap{display:block}
}
@media (max-width:680px){
  .paper-card{padding:20px 16px}
  .paper-title{font-size:23px}
  .paper-content{padding:16px 14px}
  .neighbors-card{grid-template-columns:1fr}
  .like-btn{margin-left:0}
}
</style>
