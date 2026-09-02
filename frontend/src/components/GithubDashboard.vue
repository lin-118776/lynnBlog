<template>
  <!-- GitHub 贡献热力图仪表盘：后端 /api/github/contributions 实时抓取解析，前端按周渲染彩色格子 -->
  <section class="gh-dashboard">
    <div class="gh-head">
      <h2 class="gh-title">GitHub 仪表盘<em>GITHUB CONTRIBUTION</em></h2>
      <a class="gh-follow" :href="`https://github.com/${user}`" target="_blank" rel="noopener noreferrer">
        <svg viewBox="0 0 24 24" fill="currentColor" aria-hidden="true"><path d="M12 2a10 10 0 0 0-3.16 19.49c.5.09.68-.22.68-.48v-1.7c-2.78.6-3.37-1.34-3.37-1.34-.45-1.16-1.11-1.47-1.11-1.47-.9-.62.07-.6.07-.6 1 .07 1.53 1.03 1.53 1.03.9 1.52 2.34 1.08 2.91.83.09-.65.35-1.09.63-1.34-2.22-.25-4.56-1.11-4.56-4.94 0-1.09.39-1.98 1.03-2.68-.1-.25-.45-1.27.1-2.65 0 0 .84-.27 2.75 1.02a9.58 9.58 0 0 1 5 0c1.91-1.29 2.75-1.02 2.75-1.02.55 1.38.2 2.4.1 2.65.64.7 1.03 1.59 1.03 2.68 0 3.84-2.34 4.68-4.57 4.93.36.31.68.92.68 1.85v2.74c0 .27.18.58.69.48A10 10 0 0 0 12 2Z"/></svg>
        @{{ user }}
      </a>
    </div>

    <!-- 加载中 -->
    <div v-if="loading" class="gh-loading">
      <div class="sk-avatar"></div>
      <div class="sk-lines"><div class="sk-block" style="width: 40%"></div><div class="sk-block" style="width: 96%"></div></div>
    </div>

    <!-- 失败降级 -->
    <div v-else-if="error" class="gh-error">
      <p>GitHub 贡献数据暂时拉取失败（网络受限？）</p>
      <a :href="`https://github.com/${user}`" target="_blank" rel="noopener noreferrer" class="gh-retry">去 GitHub 主页看看 →</a>
    </div>

    <template v-else>
      <div class="gh-profile">
        <img class="gh-avatar" :src="profile.avatarUrl" :alt="`${user} 的头像`" loading="lazy" />
        <div class="gh-profile-info">
          <p class="gh-name">{{ profile.name || user }}<span class="gh-login">@{{ user }}</span></p>
          <p class="gh-bio">{{ profile.bio || 'Keep coding & keep sweet ♥' }}</p>
        </div>
        <div class="gh-stats">
          <div class="gh-stat"><b>{{ profile.publicRepos }}</b><span>仓库</span></div>
          <div class="gh-stat"><b>{{ profile.followers }}</b><span>粉丝</span></div>
          <div class="gh-stat hot"><b>{{ total }}</b><span>年提交</span></div>
        </div>
      </div>

      <!-- 贡献热力图：一格一格，按天染色 -->
      <div class="gh-graph">
        <p class="gh-graph-title">过去一年的提交<small v-if="activeDays">· 活跃 {{ activeDays }} 天</small></p>
        <div class="gh-graph-scroll">
          <svg :viewBox="`0 0 ${width} ${height}`" class="gh-svg" role="img" aria-label="GitHub 贡献热力图">
            <!-- 年份标记 -->
            <text v-for="(y, i) in yearLabels" :key="'y' + i" :x="y.col * step + 2" y="8" class="gh-year-label">{{ y.text }}</text>
            <!-- 月份标记 -->
            <text v-for="(m, i) in monthLabels" :key="'m' + i" :x="m.col * step + 2" y="18" class="gh-month-label">{{ m.text }}</text>
            <!-- 贡献格 -->
            <g v-for="(col, ci) in columns" :key="'c' + ci">
              <rect
                v-for="r in 7"
                :key="'r' + r"
                :x="ci * step + 1"
                :y="(r - 1) * step + CELL_TOP"
                width="10"
                height="10"
                rx="2.5"
                :fill="cellColor(col[r - 1])"
              >
                <title v-if="col[r - 1]">{{ dateText(col[r - 1]) }}</title>
              </rect>
            </g>
          </svg>
        </div>
        <div class="gh-legend">
          <span class="legend-text">少</span>
          <i v-for="(c, i) in LEVEL_COLORS" :key="i" class="legend-cell" :style="{ background: c }"></i>
          <span class="legend-text">多</span>
        </div>
      </div>
    </template>
  </section>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

// 展示的 GitHub 用户名
const user = 'lin-118776'

// 由浅到深的粉色阶（站点配色同源）
const LEVEL_COLORS = ['#ffe9f2', '#ffd0e1', '#f9a8c4', '#e45b8d', '#a63b64']

const CELL = 10
const GAP = 3
const step = CELL + GAP
const CELL_TOP = 26
const DAYS = 7

const loading = ref(true)
const error = ref(false)
const profile = ref({})
const cells = ref([]) // [{ d:'YYYY-MM-DD', l:0..4 }]
const total = ref(0)

function pad(n) {
  return String(n).padStart(2, '0')
}

// 把每日格子排成 GitHub 式列（列=周，行=周日~周六），并计算顶部月份/年份标记
const columns = computed(() => {
  const sorted = [...cells.value].sort((a, b) => a.d.localeCompare(b.d))
  if (!sorted.length) return []
  const map = new Map(sorted.map((c) => [c.d, c.l]))
  // 从最早一天滚动到最近一天
  const [fy, fm, fd] = sorted[0].d.split('-').map(Number)
  const [ly, lm, ld] = sorted[sorted.length - 1].d.split('-').map(Number)
  const cur = new Date(fy, fm - 1, fd)
  const end = new Date(ly, lm - 1, ld)
  const cols = []
  let col = []
  while (cur <= end) {
    const key = `${cur.getFullYear()}-${pad(cur.getMonth() + 1)}-${pad(cur.getDate())}`
    col[cur.getDay()] = { l: map.get(key) || 0, d: key }
    if (cur.getDay() === 6) {
      cols.push(col)
      col = []
    }
    cur.setDate(cur.getDate() + 1)
  }
  if (col.length) cols.push(col)
  return cols
})

// 月份标签：列首日期换月时标注
const monthLabels = computed(() => {
  const labels = []
  let prevKey = ''
  columns.value.forEach((col, i) => {
    const first = col.find((x) => x)
    if (!first) return
    const key = first.d.slice(0, 7)
    if (key !== prevKey) {
      prevKey = key
      labels.push({ text: `${Number(key.slice(5, 7))}月`, col: i })
    }
  })
  return labels
})

// 年份标签：跨年首列标注（避免与月份重叠，仅当列间距足够）
const yearLabels = computed(() => {
  const labels = []
  let prevYear = ''
  columns.value.forEach((col, i) => {
    const first = col.find((x) => x)
    if (!first) return
    const y = first.d.slice(0, 4)
    if (y !== prevYear && i > 0) {
      prevYear = y
      if (i >= 4) labels.push({ text: y, col: i }) // 留足左侧空间给旧年份月份
    }
  })
  return labels
})

const width = computed(() => Math.max(columns.value.length * step + 2, 260))
const height = 14 + CELL_TOP + DAYS * step - 3
const activeDays = computed(() => cells.value.filter((c) => c.l > 0).length)

function cellColor(c) {
  return c ? LEVEL_COLORS[Math.min(c.l, 4)] : LEVEL_COLORS[0]
}

function dateText(c) {
  if (!c) return ''
  return c.l > 0 ? `${c.d} · 有提交` : `${c.d} · 无提交`
}

onMounted(async () => {
  try {
    const [uRes, cRes] = await Promise.all([
      fetch(`https://api.github.com/users/${user}`, { headers: { Accept: 'application/vnd.github+json' } }),
      fetch(`/api/github/contributions?user=${user}`)
    ])
    if (!uRes.ok || !cRes.ok) throw new Error('gh')
    const u = await uRes.json()
    const c = await cRes.json()
    profile.value = {
      name: u.name || '',
      bio: u.bio || '',
      followers: u.followers ?? 0,
      publicRepos: u.public_repos ?? 0,
      avatarUrl: (u.avatar_url || '').includes('?') ? `${u.avatar_url}&s=160` : `${u.avatar_url}?s=160`
    }
    cells.value = (c.data?.cells || []).map((x) => ({ d: x.d, l: Math.min(Math.max(x.l, 0), 4) }))
    total.value = c.data?.total ?? activeDays.value
  } catch (e) {
    error.value = true
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.gh-dashboard{display:flex;flex-direction:column;gap:14px;padding:20px 22px;border:1px solid var(--pink-200,#ffd0e1);border-radius:var(--radius-lg,22px);background:linear-gradient(160deg,#fffaf5,#fff);box-shadow:var(--shadow,0 12px 30px rgba(183,85,129,.12))}
.gh-head{display:flex;align-items:center;justify-content:space-between;gap:12px;flex-wrap:wrap}
.gh-title{margin:0;display:flex;flex-direction:column;color:var(--berry,#a63b64);font-size:18px;font-weight:800}
.gh-title em{font-style:normal;font-size:10px;letter-spacing:4px;color:var(--pink-400,#f9a8c4)}
.gh-follow{display:inline-flex;align-items:center;gap:7px;padding:7px 16px;border:1px solid var(--pink-300,#ffb7d0);border-radius:999px;background:#fff;color:var(--berry,#a63b64);font-size:12.5px;font-weight:800;transition:all .2s ease}
.gh-follow:hover{transform:translateY(-2px);box-shadow:0 6px 14px rgba(183,85,129,.18);border-color:var(--rose,#e45b8d)}
.gh-follow svg{width:16px;height:16px;color:#333}
/* 头部 */
.gh-profile{display:flex;align-items:center;gap:16px;flex-wrap:wrap}
.gh-avatar{width:60px;height:60px;border-radius:50%;border:3px solid #fff;box-shadow:0 6px 16px rgba(183,85,129,.22);background:var(--pink-100,#ffe7f0)}
.gh-profile-info{flex:1 1 160px;min-width:0}
.gh-name{margin:0;font-size:17px;font-weight:800;color:var(--ink,#4a2b3a)}
.gh-login{margin-left:8px;font-size:12px;font-weight:600;color:var(--muted,#8a6475)}
.gh-bio{margin:5px 0 0;font-size:12.5px;color:var(--muted,#8a6475);line-height:1.6}
.gh-stats{display:flex;gap:10px}
.gh-stat{display:flex;flex-direction:column;align-items:center;min-width:64px;padding:8px 12px;border:1px solid var(--pink-100,#ffe7f0);border-radius:14px;background:linear-gradient(180deg,#fff,var(--pink-50,#fff7fa))}
.gh-stat b{color:var(--rose,#e45b8d);font-size:19px;font-weight:800;line-height:1.2}
.gh-stat.hot b{color:var(--berry,#a63b64)}
.gh-stat span{color:var(--muted,#8a6475);font-size:11px;font-weight:700;margin-top:3px}
/* 贡献图 */
.gh-graph{padding:14px 16px;border:1px dashed var(--pink-200,#ffd0e1);border-radius:18px;background:linear-gradient(180deg,var(--pink-50,#fff7fa),#fff)}
.gh-graph-title{margin:0 0 8px;font-size:12.5px;font-weight:800;color:var(--berry,#a63b64)}
.gh-graph-title small{margin-left:8px;font-weight:600;color:var(--muted,#8a6475)}
.gh-graph-scroll{overflow-x:auto;padding-bottom:4px}
.gh-svg{display:block;max-width:none}
.gh-year-label{font-size:9px;fill:#b28aa0;font-weight:700}
.gh-month-label{font-size:9px;fill:#b28aa0}
.gh-legend{display:flex;align-items:center;justify-content:flex-end;gap:4px;margin-top:6px}
.legend-text{font-size:10px;color:var(--muted,#8a6475);margin:0 3px}
.legend-cell{width:10px;height:10px;border-radius:2.5px}
/* 加载 / 错误 */
.gh-loading{display:flex;gap:14px;align-items:center;padding:8px 0}
.sk-avatar{width:56px;height:56px;border-radius:50%;flex:none;background:linear-gradient(90deg,#ffe7f0 25%,#fff1f7 37%,#ffe7f0 63%);background-size:400% 100%;animation:shimmer 1.4s ease infinite}
.sk-lines{flex:1;display:grid;gap:8px}
.sk-block{height:12px;border-radius:8px;background:linear-gradient(90deg,#ffe7f0 25%,#fff1f7 37%,#ffe7f0 63%);background-size:400% 100%;animation:shimmer 1.4s ease infinite}
@keyframes shimmer{0%{background-position:100% 0}100%{background-position:-100% 0}}
.gh-error{padding:26px 0;text-align:center;color:var(--muted,#8a6475);font-size:13px}
.gh-error p{margin:0 0 10px}
.gh-retry{display:inline-flex;padding:6px 16px;border:1px solid var(--pink-300,#ffb7d0);border-radius:999px;color:var(--berry,#a63b64);font-size:12px;font-weight:800}
@media (max-width:560px){
  .gh-stats{width:100%}
  .gh-stat{flex:1}
}
</style>
