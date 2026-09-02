<template>
  <!-- 作品页：作品集展示（独立路由 /works） -->
  <div class="works-page">
    <header class="works-head">
      <h1 class="works-title">作品<em>MY PROJECTS</em></h1>
      <p class="works-sub">做过的项目，都认真地记录下来 ♥</p>
    </header>

    <div v-loading="loading" class="works-grid">
      <article
        v-for="(p, i) in projects"
        :key="p.id"
        class="work-card"
        :class="'tilt-' + (i % 4)"
      >
        <div class="work-cover">
          <img v-if="p.coverImage" :src="p.coverImage" :alt="p.name" loading="lazy" />
          <div v-else class="cover-fallback">{{ p.name.slice(0, 1) }}</div>
        </div>
        <div class="work-body">
          <h3 class="work-name">{{ p.name }}</h3>
          <p class="work-desc">{{ p.description || '暂无简介' }}</p>
          <div v-if="techList(p).length" class="work-tech">
            <span v-for="t in techList(p)" :key="t" class="tech-tag">{{ t }}</span>
          </div>
          <div class="work-links">
            <a v-if="p.url" :href="p.url" target="_blank" rel="noopener noreferrer" class="work-link">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 4h6v6"/><path d="M20 4 10 14"/><path d="M20 14v5a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1V5a1 1 0 0 1 1-1h5"/></svg>
              在线预览
            </a>
            <a v-if="p.githubUrl" :href="p.githubUrl" target="_blank" rel="noopener noreferrer" class="work-link github">
              <svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 2a10 10 0 0 0-3.16 19.49c.5.09.68-.22.68-.48v-1.7c-2.78.6-3.37-1.34-3.37-1.34-.45-1.16-1.11-1.47-1.11-1.47-.9-.62.07-.6.07-.6 1 .07 1.53 1.03 1.53 1.03.9 1.52 2.34 1.08 2.91.83.09-.65.35-1.09.63-1.34-2.22-.25-4.56-1.11-4.56-4.94 0-1.09.39-1.98 1.03-2.68-.1-.25-.45-1.27.1-2.65 0 0 .84-.27 2.75 1.02a9.58 9.58 0 0 1 5 0c1.91-1.29 2.75-1.02 2.75-1.02.55 1.38.2 2.4.1 2.65.64.7 1.03 1.59 1.03 2.68 0 3.84-2.34 4.68-4.57 4.93.36.31.68.92.68 1.85v2.74c0 .27.18.58.69.48A10 10 0 0 0 12 2Z"/></svg>
              GitHub
            </a>
          </div>
        </div>
      </article>

      <div v-if="!loading && !projects.length" class="works-empty">
        还没有作品，敬请期待 ♥
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { listProjects } from '../api/project'

const projects = ref([])
const loading = ref(false)

function techList(p) {
  return (p.tech || '').split(',').map((t) => t.trim()).filter(Boolean)
}

onMounted(async () => {
  loading.value = true
  try {
    const { data } = await listProjects()
    projects.value = Array.isArray(data) ? data : []
  } catch (e) {
    projects.value = []
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.works-page{display:flex;flex-direction:column;gap:16px;min-width:0}
.works-head{padding:20px 22px 16px;border:1px solid var(--pink-200,#ffd0e1);border-radius:var(--radius-lg,22px);background:linear-gradient(160deg,#fff5f8,#ffe7f0);box-shadow:var(--shadow,0 12px 30px rgba(183,85,129,.12))}
.works-title{margin:0;font-size:26px;color:var(--berry,#a63b64);letter-spacing:3px}
.works-title em{display:block;margin-top:4px;font-style:normal;font-size:11px;letter-spacing:5px;color:var(--pink-400,#f9a8c4)}
.works-sub{margin:10px 0 0;font-family:"Kaiti SC","STKaiti",KaiTi,serif;font-size:14px;color:var(--rose,#e45b8d)}
.works-grid{display:grid;grid-template-columns:repeat(2,minmax(0,1fr));gap:16px;min-height:160px}
.work-card{display:flex;flex-direction:column;overflow:hidden;border:1px solid var(--pink-100,#ffe7f0);border-radius:16px;background:#fff;box-shadow:0 6px 16px rgba(183,85,129,.1);transition:transform .22s cubic-bezier(.22,.9,.3,1.3),box-shadow .22s ease}
.work-card.tilt-1{transform:rotate(-.5deg)}
.work-card.tilt-2{transform:rotate(.4deg)}
.work-card.tilt-3{transform:rotate(-.3deg)}
.work-card:hover{transform:rotate(0deg) translateY(-4px);box-shadow:0 16px 32px rgba(183,85,129,.2)}
.work-cover{aspect-ratio:16/9;overflow:hidden;border-bottom:3px solid #fff}
.work-cover img{display:block;width:100%;height:100%;object-fit:cover;transition:transform .3s ease}
.work-card:hover .work-cover img{transform:scale(1.05)}
.cover-fallback{display:grid;place-items:center;width:100%;height:100%;background:linear-gradient(135deg,var(--pink-300,#ffb7d0),var(--rose,#e45b8d));color:#fff;font-size:34px;font-weight:800}
.work-body{padding:14px 16px 16px;display:flex;flex-direction:column;gap:8px}
.work-name{margin:0;font-size:16px;font-weight:800;color:var(--ink,#4a2b3a)}
.work-desc{margin:0;font-size:12.5px;color:var(--muted,#8a6475);line-height:1.7}
.work-tech{display:flex;flex-wrap:wrap;gap:6px}
.tech-tag{padding:2px 10px;border-radius:999px;background:var(--sky,#d9edfb);color:#35698a;font-size:11px;font-weight:800}
.work-links{display:flex;gap:10px;margin-top:4px}
.work-link{display:inline-flex;align-items:center;gap:5px;padding:5px 13px;border:1px solid var(--pink-300,#ffb7d0);border-radius:999px;color:var(--berry,#a63b64);font-size:12px;font-weight:800;transition:all .2s ease}
.work-link:hover{background:var(--pink-50,#fff7fa);transform:translateY(-1px)}
.work-link svg{width:13px;height:13px}
.work-link.github{color:#333}
.work-link.github:hover{background:#f6f6f6;border-color:#999}
.works-empty{grid-column:1/-1;display:flex;align-items:center;justify-content:center;padding:60px 20px;color:var(--muted,#8a6475);font-size:14px}
@media (max-width:760px){
  .works-grid{grid-template-columns:1fr}
}
</style>
