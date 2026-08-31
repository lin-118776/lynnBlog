<template>
  <!-- 首页：各区块仅为预览，点击"查看全部"跳转到独立页面（参考 xnmoe.com） -->
  <div class="home-page">
    <div class="mood">今日心情 · 草莓牛奶味</div>

    <section class="banner"><ImgSlot variant="banner" label="顶部 Banner · 1200 × 300" /></section>

    <section class="feature-grid">
      <div class="player">
        <div class="player-screen"><ImgSlot variant="screen" label="画面" /></div>
        <div class="player-meta"><ImgSlot variant="cover" label="封面" /><p>Strawberry Float<br /><small>Lynn · 2026</small></p></div>
        <div class="player-controls">
          <button class="player-btn" type="button" aria-label="上一首"><svg viewBox="0 0 24 24" fill="currentColor"><path d="M6 5h2v14H6V5Zm12 1.2v11.6L10 12l8-5.8Z"/></svg></button>
          <button class="player-btn" type="button" aria-label="播放"><svg viewBox="0 0 24 24" fill="currentColor"><path d="M8 5.5v13l11-6.5-11-6.5Z"/></svg></button>
          <button class="player-btn" type="button" aria-label="下一首"><svg viewBox="0 0 24 24" fill="currentColor"><path d="M16 5h2v14h-2V5ZM6 6.2v11.6L14 12 6 6.2Z"/></svg></button>
        </div>
      </div>
      <div class="feature-main"><ImgSlot variant="fluid" label="主内容区 · 可放欢迎图或 iframe" /></div>
    </section>

    <CardPanel panel-id="updates" title="Updates" icon="book">
      <div v-if="latestArticles.length" class="updates-list">
        <router-link v-for="item in latestArticles" :key="item.id" class="update-item" :to="`/article/${item.id}`">
          <div class="update-date">{{ formatDate(item.createTime) }}</div>
          <div class="update-title">{{ item.title }}<small>{{ item.categoryName || '文章' }} · {{ item.summary || '点击阅读' }}</small></div>
          <span class="tag">{{ item.categoryName || 'Article' }}</span>
        </router-link>
      </div>
      <div v-else class="updates-list"><div class="update-item"><div class="update-title">暂无文章</div></div></div>
      <template #footer>
        <router-link class="section-more" to="/blog">查看全部文章 →</router-link>
      </template>
    </CardPanel>

    <CardPanel panel-id="chatbox" title="Chat Box" icon="message">
      <div class="chat-list">
        <div class="chat-item"><ImgSlot variant="chat" /><div><span class="chat-name">Momo</span><p class="chat-text">好喜欢这个粉粉的配色，已经收藏啦～</p></div><time class="chat-time">08-31</time></div>
        <div class="chat-item"><ImgSlot variant="chat" /><div><span class="chat-name">Yuki</span><p class="chat-text">更新辛苦了！贴纸的排版真好看。</p></div><time class="chat-time">08-30</time></div>
      </div>
      <template #footer>
        <router-link class="section-more" to="/guestbook">查看全部留言 →</router-link>
      </template>
    </CardPanel>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import ImgSlot from '../components/ImgSlot.vue'
import CardPanel from '../components/CardPanel.vue'
import { listArticles } from '../api/article'

const latestArticles = ref([])

function formatDate(value) {
  if (!value) return ''
  return String(value).slice(0, 10).replaceAll('-', '.')
}

onMounted(async () => {
  try {
    const { data } = await listArticles(1, 3)
    latestArticles.value = data.records || []
  } catch (e) {
    latestArticles.value = []
  }
})
</script>

<style scoped>
.home-page{display:flex;flex-direction:column;gap:16px;min-width:0}
.mood{display:inline-flex;align-self:flex-start;gap:8px;align-items:center;padding:6px 14px;border:1px solid var(--pink-200,#ffd0e1);border-radius:999px;background:rgba(255,250,245,.9);box-shadow:var(--shadow,0 12px 30px rgba(183,85,129,.12));color:var(--rose,#e45b8d);font-weight:800;font-size:13px;letter-spacing:1px}
.mood:before{content:"♪";color:var(--pink-400,#f9a8c4)}
.feature-grid{display:grid;grid-template-columns:200px minmax(0,1fr);gap:16px}
.player,.feature-main{border:1px solid var(--line,#efc2d3);border-radius:var(--radius-lg,22px);background:var(--cream,#fffaf5);box-shadow:var(--shadow,0 12px 30px rgba(183,85,129,.12))}
.player{padding:14px;background:linear-gradient(145deg,#fff5f8,#ffdbe8)}
.player-screen{overflow:hidden;border:3px solid #fff;border-radius:16px;background:var(--mint,#cdeede);box-shadow:inset 0 2px 8px rgba(166,59,100,.12)}
.player-meta{display:flex;gap:9px;align-items:center;margin-top:12px}
.player-meta p{margin:0;font-size:11px;line-height:1.5;color:var(--ink,#4a2b3a)}
.player-meta small{color:var(--muted,#8a6475)}
.player-controls{display:flex;gap:8px;justify-content:center;margin-top:12px}
.player-btn{display:grid;place-items:center;width:40px;height:40px;border:1px solid var(--pink-300,#ffb7d0);border-radius:50%;background:#fff;color:var(--berry,#a63b64);cursor:pointer;box-shadow:0 4px 10px rgba(183,85,129,.14)}
.player-btn:hover{transform:translateY(-2px);background:var(--pink-50,#fff7fa);border-color:var(--pink-400,#f9a8c4)}
.player-btn svg{width:16px;height:16px}
.feature-main{min-height:300px;overflow:hidden;padding:12px}
.updates-list{display:grid;gap:9px}
.update-item{display:grid;grid-template-columns:44px 1fr auto;gap:10px;align-items:center;padding:10px 12px;border:1px solid var(--pink-100,#ffe7f0);border-radius:14px;background:#fff;transition:transform .2s ease,border-color .2s ease,box-shadow .2s ease}
.update-item:hover{transform:translateY(-2px);border-color:var(--pink-300,#ffb7d0);box-shadow:0 8px 18px rgba(183,85,129,.1)}
.update-date{display:grid;place-items:center;width:42px;height:42px;border-radius:12px;background:var(--lemon,#fff1b8);color:var(--berry,#a63b64);font-size:11px;font-weight:800;text-align:center;line-height:1.2}
.update-title{color:var(--ink,#4a2b3a);font-size:13px;font-weight:700}
.update-title small{display:block;margin-top:2px;color:var(--muted,#8a6475);font-size:11px;font-weight:500}
.tag{display:inline-flex;padding:3px 8px;border-radius:999px;background:var(--sky,#d9edfb);color:#35698a;font-size:10px;font-weight:800;white-space:nowrap}
.chat-list{display:grid;gap:10px}
.chat-item{display:grid;grid-template-columns:34px 1fr auto;gap:9px;align-items:start;padding:10px 0;border-bottom:1px dashed var(--pink-200,#ffd0e1)}
.chat-item:last-child{border-bottom:0;padding-bottom:0}
.chat-name{font-size:12px;font-weight:800;color:var(--ink,#4a2b3a)}
.chat-text{margin:2px 0 0;font-size:11px;color:var(--muted,#8a6475);line-height:1.5}
.chat-time{font-size:9px;color:rgba(74,43,58,.48);white-space:nowrap}
.section-more{display:block;margin-top:12px;padding:9px;border-radius:12px;background:var(--pink-50,#fff7fa);text-align:center;font-size:12px;font-weight:800}
@media (max-width:680px){
  .feature-grid{grid-template-columns:1fr}
  .feature-main{min-height:240px}
  .update-item{grid-template-columns:40px 1fr}
  .update-item .tag{display:none}
}
</style>
