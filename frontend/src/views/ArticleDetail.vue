<template>
  <div class="article-page">
    <!-- 返回列表 -->
    <div class="article-topbar">
      <router-link to="/blog" class="back-link">
        <AppIcon name="chevron-left" :size="15" />
        返回列表
      </router-link>
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
    <article v-else-if="status === 'ok'" class="paper-card">
      <header class="paper-head">
        <div class="paper-flags">
          <span class="tag">{{ article.categoryName || '随笔' }}</span>
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
          <button class="like-btn" :class="{ liked: article.isLiked }" type="button" :disabled="liking" @click="handleLike">
            <AppIcon name="heart" :size="14" />
            {{ article.isLiked ? '已赞' : '点赞' }} {{ article.likeCount || 0 }}
          </button>
        </div>
      </header>

      <div v-if="article.coverImage" class="paper-cover">
        <img :src="article.coverImage" :alt="article.title" />
      </div>

      <div class="paper-content">
        <MdPreview :modelValue="article.content || ''" />
      </div>

      <footer class="paper-foot">—— END · Lynn's Blog ♥ ——</footer>
    </article>

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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { MdPreview } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import {
  getArticleDetail,
  likeArticle,
  getArticleNeighbors,
  listArticleComments,
  addArticleComment
} from '../api/article'
import AppIcon from '../components/AppIcon.vue'
import { useAuth } from '../composables/useAuth'

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

async function load() {
  status.value = 'loading'
  errorMsg.value = ''
  try {
    const { data } = await getArticleDetail(route.params.id)
    article.value = data
    status.value = 'ok'
    loadNeighbors()
    loadComments()
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
  if (article.value?.isLiked) {
    ElMessage.info('已经赞过啦 ♥')
    return
  }
  liking.value = true
  try {
    const { data } = await likeArticle(route.params.id)
    article.value.likeCount = data
    article.value.isLiked = true
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

onMounted(() => load())
</script>

<style scoped>
/* ===== 三丽鸥手账风文章详情 ===== */
.article-page{display:flex;flex-direction:column;gap:16px;min-width:0}
.article-topbar{display:flex}
.back-link{display:inline-flex;align-items:center;gap:4px;padding:6px 14px;border:1px solid var(--pink-200,#ffd0e1);border-radius:999px;background:var(--cream,#fffaf5);color:var(--rose,#e45b8d);font-size:13px;font-weight:700;box-shadow:0 4px 10px rgba(183,85,129,.1);transition:all .2s ease}
.back-link:hover{color:var(--berry,#a63b64);border-color:var(--pink-300,#ffb7d0);transform:translateY(-1px)}
.paper-card{position:relative;border:1px solid var(--pink-200,#ffd0e1);border-radius:var(--radius-lg,22px);background:var(--cream,#fffaf5);box-shadow:var(--shadow,0 12px 30px rgba(183,85,129,.12));padding:26px 30px 22px}
.paper-head{padding-bottom:18px;border-bottom:1px dashed var(--pink-200,#ffd0e1)}
.paper-flags{display:flex;align-items:center;gap:10px;flex-wrap:wrap;margin-bottom:12px}
.tag{display:inline-flex;padding:3px 12px;border-radius:999px;background:var(--sky,#d9edfb);color:#35698a;font-size:12px;font-weight:800}
.paper-date{color:var(--muted,#8a6475);font-size:12px}
.paper-title{margin:0;font-size:30px;line-height:1.35;font-weight:800;letter-spacing:.5px;color:var(--berry,#a63b64)}
.paper-meta{display:flex;align-items:center;flex-wrap:wrap;gap:14px;margin-top:14px;color:var(--muted,#8a6475);font-size:13px}
.meta-item,.meta-author{display:inline-flex;align-items:center;gap:5px}
.meta-author{color:var(--ink,#4a2b3a);font-weight:700}
.author-avatar,.comment-avatar{display:inline-grid;place-items:center;width:24px;height:24px;border-radius:50%;background:linear-gradient(135deg,var(--pink-200,#ffd0e1),var(--pink-300,#ffb7d0));color:var(--berry,#a63b64);font-size:12px;font-weight:800}
.like-btn{display:inline-flex;align-items:center;gap:5px;margin-left:auto;padding:5px 16px;border:1px solid var(--pink-300,#ffb7d0);border-radius:999px;background:linear-gradient(135deg,#fff,var(--pink-50,#fff7fa));color:var(--rose,#e45b8d);font-size:13px;font-weight:800;cursor:pointer;box-shadow:0 4px 10px rgba(227,91,141,.12);transition:all .2s ease}
.like-btn:hover{transform:translateY(-1px);border-color:var(--pink-400,#f9a8c4)}
.like-btn:disabled{opacity:.7;cursor:default}
.like-btn.liked{background:linear-gradient(135deg,var(--pink-300,#ffb7d0),var(--rose,#e45b8d));border-color:transparent;color:#fff;box-shadow:0 6px 14px rgba(227,91,141,.3)}
.paper-cover{margin:20px 0 0;overflow:hidden;border:3px solid #fff;border-radius:18px;box-shadow:0 8px 20px rgba(183,85,129,.14)}
.paper-cover img{display:block;width:100%;max-height:420px;object-fit:cover}
.paper-content{margin-top:22px;padding:24px 28px;background:#fff;border:1px solid var(--pink-100,#ffe7f0);border-radius:16px}
.paper-foot{margin-top:20px;text-align:center;color:var(--pink-400,#f9a8c4);font-size:12px;letter-spacing:2px}
/* Markdown 预览三丽鸥化 */
.paper-content :deep(.md-editor){background:transparent;box-shadow:none}
.paper-content :deep(.md-editor-preview){background:transparent;color:var(--ink,#4a2b3a);font-size:15px;line-height:1.9;padding:0}
.paper-content :deep(.md-editor-preview h1),
.paper-content :deep(.md-editor-preview h2),
.paper-content :deep(.md-editor-preview h3){color:var(--berry,#a63b64);margin-top:1.4em}
.paper-content :deep(.md-editor-preview a){color:var(--rose,#e45b8d)}
.paper-content :deep(.md-editor-preview blockquote){border-left:3px solid var(--pink-300,#ffb7d0);background:var(--pink-50,#fff7fa);border-radius:0 10px 10px 0;padding:8px 14px;color:var(--muted,#8a6475)}
.paper-content :deep(.md-editor-preview code){background:var(--pink-100,#ffe7f0);color:var(--berry,#a63b64);border-radius:6px;padding:1px 6px;font-size:.9em}
.paper-content :deep(.md-editor-preview pre){background:#fff;border:1px solid var(--pink-100,#ffe7f0);border-radius:14px;padding:14px 16px}
.paper-content :deep(.md-editor-preview pre code){background:transparent;color:inherit;padding:0}
.paper-content :deep(.md-editor-preview img){border-radius:14px}
.paper-content :deep(.md-editor-preview table){border-collapse:collapse;margin:1em 0}
.paper-content :deep(.md-editor-preview th),
.paper-content :deep(.md-editor-preview td){border:1px solid var(--pink-200,#ffd0e1);padding:6px 12px}
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
@media (max-width:680px){
  .paper-card{padding:20px 16px}
  .paper-title{font-size:23px}
  .paper-content{padding:16px 14px}
  .neighbors-card{grid-template-columns:1fr}
  .like-btn{margin-left:0}
}
</style>
