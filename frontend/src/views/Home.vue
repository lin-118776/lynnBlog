<template>
  <!-- 首页：各区块仅为预览，点击"查看全部"跳转到独立页面（参考 xnmoe.com） -->
  <div class="home-page">
    <section class="banner">
      <video class="banner-video" :src="bannerSrc" autoplay muted loop playsinline preload="auto" aria-label="顶部 Banner 视频"></video>
    </section>

    <section class="feature-grid">
      <div class="player">
        <div class="player-screen"><img :src="coverSrc" alt="音乐封面" :class="{ spinning: playing }" /></div>
        <div class="player-meta"><p>めるめろ♥マイメロデ<br /><small>Lynn · 2026</small></p></div>
        <div class="player-controls">
          <button class="player-btn" type="button" aria-label="重新播放" @click="restart"><svg viewBox="0 0 24 24" fill="currentColor"><path d="M6 5h2v14H6V5Zm12 1.2v11.6L10 12l8-5.8Z"/></svg></button>
          <button class="player-btn play" type="button" aria-label="播放或暂停" @click="togglePlay">
            <svg v-if="playing" viewBox="0 0 24 24" fill="currentColor"><path d="M7 5h4v14H7V5Zm6 0h4v14h-4V5Z"/></svg>
            <svg v-else viewBox="0 0 24 24" fill="currentColor"><path d="M8 5.5v13l11-6.5-11-6.5Z"/></svg>
          </button>
          <button class="player-btn" type="button" aria-label="重新播放" @click="restart"><svg viewBox="0 0 24 24" fill="currentColor"><path d="M16 5h2v14h-2V5ZM6 6.2v11.6L14 12 6 6.2Z"/></svg></button>
        </div>
        <audio ref="audioRef" :src="musicSrc" loop @play="playing = true" @pause="playing = false" @timeupdate="onTimeUpdate"></audio>
      </div>
      <div class="feature-main barrage">
        <img class="barrage-bg" :src="barrageBg" alt="留言弹幕背景" />
        <div class="barrage-track" aria-hidden="true">
          <span
            v-for="(d, i) in danmaku"
            :key="i"
            class="barrage-item"
            :style="barrageStyle(i)"
          >{{ d.text }}</span>
        </div>
      </div>
    </section>

    <CardPanel panel-id="updates" title="Updates" icon="book">
      <div v-if="latestArticles.length" class="blog-list">
        <router-link v-for="item in latestArticles" :key="item.id" class="blog-item" :to="`/article/${item.id}`">
          <div v-if="item.coverImage" class="blog-thumb"><img :src="item.coverImage" :alt="item.title" loading="lazy" /></div>
          <div v-else class="blog-date">{{ formatDate(item.createTime) }}</div>
          <div class="blog-body">
            <div class="blog-title">{{ item.title }}</div>
            <div class="blog-summary">{{ item.summary || '点击阅读全文 →' }}</div>
            <div class="blog-meta">
              <span class="meta-item">{{ item.authorNickname || 'Lynn' }}</span>
              <span class="meta-dot">·</span>
              <span class="meta-item">{{ item.categoryName || 'Article' }}</span>
              <span class="tag">{{ item.categoryName || 'Article' }}</span>
            </div>
          </div>
        </router-link>
      </div>
      <div v-else class="blog-list"><div class="blog-item"><div class="blog-title">暂无文章</div></div></div>
      <template #footer>
        <router-link class="section-more" to="/blog">查看全部文章 →</router-link>
      </template>
    </CardPanel>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import ImgSlot from '../components/ImgSlot.vue'
import CardPanel from '../components/CardPanel.vue'
import { listArticles } from '../api/article'
import { listGuestbook } from '../api/guestbook'
import bannerSrc from '../images/banner.mp4'
import musicSrc from '../images/music.mp3'
import coverSrc from '../images/音乐封面.jpg'
import barrageBg from '../images/留言弹幕背景图.jpg'

const latestArticles = ref([])

// ===== 留言弹幕：数据源 = 留言板真实留言；没有留言时用欢迎语兜底 =====
const danmaku = ref([])

// 兜底欢迎语（无真实留言时使用，营造"有人气"的暖场感）
const WELCOME_DANMAKU = [
  '♥ 欢迎光临 Lynn 的小站',
  '今天也要元气满满！',
  '✿ 裙子都好可爱～',
  '小裙子收藏家出没',
  '打卡！',
  '粉色系赛高！',
  '✧ 每一件都心动',
  '路过留个爪印',
  '手账风好好看 ✿',
  'Lynn 加油～',
  '今天也很开心 ♥',
  '愿你天天好心情',
  '裙子墙太绝了！',
  '悄悄路过～',
  '✨ 好喜欢这个站',
  '下次再来玩！'
]

function barrageStyle(i) {
  return {
    top: `${4 + ((i * 13) % 88)}%`,
    animationDuration: `${10 + (i % 6) * 3.5}s`,
    animationDelay: `${-(i * 2.6)}s`,
    fontSize: `${15 + (i % 4) * 2}px`
  }
}

// 拉取留言板内容做弹幕：最多 4 页；不足 10 条时混入欢迎语保证画面饱满
async function loadDanmaku() {
  const bullets = []
  try {
    for (let page = 1; page <= 4; page++) {
      const { data } = await listGuestbook(page, 10)
      const records = data.records || []
      records.forEach((m) => {
        const t = String(m.content || '').replace(/\s+/g, ' ').trim()
        if (t) bullets.push(t.length > 30 ? `${t.slice(0, 30)}…` : t)
      })
      if (records.length < 10) break
    }
  } catch (e) {
    bullets.length = 0 // 接口异常走兜底
  }
  // 无留言（或太少）时用欢迎语补充
  if (bullets.length === 0) {
    danmaku.value = WELCOME_DANMAKU.map((text) => ({ text }))
  } else {
    bullets.forEach((t) => {
      if (danmaku.value.length < 24) danmaku.value.push({ text: t })
    })
    if (danmaku.value.length < 8) {
      WELCOME_DANMAKU.forEach((text) => {
        if (danmaku.value.length < 24) danmaku.value.push({ text })
      })
    }
  }
}

// ===== 播放器：固定单曲（images/music.mp3） =====
const playing = ref(false)
const audioRef = ref(null)

function togglePlay() {
  const a = audioRef.value
  if (!a) return
  if (playing.value) a.pause()
  else a.play().catch(() => {})
}

function restart() {
  const a = audioRef.value
  if (!a) return
  a.currentTime = 0
  a.play().catch(() => {})
}

// 最后 2 秒不播：直接跳回开头循环（避免结尾空白/杂音）
function onTimeUpdate() {
  const a = audioRef.value
  if (a && a.duration && !isNaN(a.duration) && a.currentTime >= a.duration - 2) {
    a.currentTime = 0
    a.play().catch(() => {})
  }
}

function formatDate(value) {
  if (!value) return ''
  const d = String(value).slice(0, 10).split('-')
  return `${d[1]}.${d[2]}`
}

onMounted(async () => {
  loadDanmaku()
  try {
    const { data } = await listArticles(1, 6)
    latestArticles.value = data.records || []
  } catch (e) {
    latestArticles.value = []
  }
})
</script>

<style scoped>
.home-page{display:flex;flex-direction:column;gap:16px;min-width:0}
.banner{position:relative;overflow:hidden;border:1px solid var(--pink-200,#ffd0e1);border-radius:var(--radius-lg,22px);box-shadow:var(--shadow,0 12px 30px rgba(183,85,129,.12));background:var(--pink-100,#ffe7f0);height:430px}
.banner-video{display:block;width:100%;height:100%;object-fit:cover;object-position:-10px 60%}
.feature-grid{display:grid;grid-template-columns:200px minmax(0,1fr);gap:16px}
.player,.feature-main{border:1px solid var(--line,#efc2d3);border-radius:var(--radius-lg,22px);background:var(--cream,#fffaf5);box-shadow:var(--shadow,0 12px 30px rgba(183,85,129,.12))}
.player{padding:14px 14px 16px;display:flex;flex-direction:column;align-items:center;background:linear-gradient(145deg,#fff5f8,#ffdbe8)}
/* 主内容区：留言弹幕版 */
.feature-main{position:relative;min-height:0;overflow:hidden;padding:0}
.barrage-bg{position:absolute;inset:0;width:100%;height:100%;object-fit:cover}
.barrage-track{position:absolute;inset:0;overflow:hidden}
/* 胶囊弹幕：半透明深色底 + 白字，不加阴影，全宽飞过 */
.barrage-item{position:absolute;left:100%;top:0;white-space:nowrap;padding:3px 14px;border-radius:999px;background:rgba(74,43,58,.38);color:#fff;font-weight:800;letter-spacing:1.5px;animation:barrage-fly linear infinite;will-change:left}
@keyframes barrage-fly{from{left:100%}to{left:-120%}}
/* 圆形唱片播放器：画面为圆形封面，播放时缓慢旋转 */
.player-screen{overflow:hidden;width:142px;height:142px;margin:6px auto 0;border:4px solid #fff;border-radius:50%;background:var(--mint,#cdeede);box-shadow:0 6px 18px rgba(166,59,100,.22),inset 0 2px 8px rgba(166,59,100,.12)}
.player-screen img{display:block;width:100%;height:100%;object-fit:cover;border-radius:50%}
.player-screen img.spinning{animation:disc-spin 9s linear infinite}
@keyframes disc-spin{to{transform:rotate(360deg)}}
.player-meta{display:flex;flex-direction:column;align-items:center;text-align:center;margin-top:16px}
.player-meta p{margin:0;font-size:14px;line-height:1.5;color:var(--ink,#4a2b3a);font-weight:700}
.player-meta small{color:var(--muted,#8a6475);font-size:12px}
.player-controls{display:flex;gap:10px;justify-content:center;margin-top:12px}
.player-btn{display:grid;place-items:center;width:40px;height:40px;border:1px solid var(--pink-300,#ffb7d0);border-radius:50%;background:#fff;color:var(--berry,#a63b64);cursor:pointer;box-shadow:0 4px 10px rgba(183,85,129,.14);transition:transform .2s ease,border-color .2s ease}
.player-btn:hover{transform:translateY(-2px);background:var(--pink-50,#fff7fa);border-color:var(--pink-400,#f9a8c4)}
.player-btn svg{width:16px;height:16px}
.player-btn.play{width:46px;height:46px;background:linear-gradient(135deg,var(--pink-300,#ffb7d0),var(--rose,#e45b8d));color:#fff;border-color:transparent}
.player-btn.play svg{width:18px;height:18px}
/* Updates：复用文章列表页（Blog）的条目样式 */
.blog-list{display:grid;gap:10px}
.blog-item{display:grid;grid-template-columns:56px 1fr;gap:12px;align-items:center;padding:12px 14px;border:1px solid var(--pink-100,#ffe7f0);border-radius:14px;background:#fff;transition:transform .2s ease,border-color .2s ease,box-shadow .2s ease}
.blog-item:hover{transform:translateY(-2px);border-color:var(--pink-300,#ffb7d0);box-shadow:0 8px 18px rgba(183,85,129,.1)}
.blog-thumb{width:52px;height:52px;border-radius:12px;overflow:hidden;border:2px solid #fff;box-shadow:0 3px 8px rgba(183,85,129,.14)}
.blog-thumb img{display:block;width:100%;height:100%;object-fit:cover}
.blog-date{display:grid;place-items:center;width:52px;height:52px;border-radius:12px;background:var(--lemon,#fff1b8);color:var(--berry,#a63b64);font-size:12px;font-weight:800;text-align:center;line-height:1.2}
.blog-body{min-width:0}
.blog-title{color:var(--ink,#4a2b3a);font-size:15px;font-weight:800;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}
.blog-summary{margin-top:4px;color:var(--muted,#8a6475);font-size:12.5px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}
.blog-meta{display:flex;align-items:center;gap:6px;margin-top:7px;flex-wrap:wrap}
.meta-item{color:var(--muted,#8a6475);font-size:11.5px}
.meta-dot{color:var(--pink-300,#ffb7d0);font-size:11.5px}
.tag{display:inline-flex;padding:2px 9px;border-radius:999px;background:var(--sky,#d9edfb);color:#35698a;font-size:10px;font-weight:800;white-space:nowrap}
.section-more{display:block;margin-top:12px;padding:9px;border-radius:12px;background:var(--pink-50,#fff7fa);text-align:center;font-size:12px;font-weight:800}
@media (max-width:680px){
  .feature-grid{grid-template-columns:1fr}
  .feature-main{min-height:240px}
  .blog-item{grid-template-columns:46px 1fr}
  .blog-item .tag{display:none}
}
</style>
