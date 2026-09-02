<template>
  <!-- 留言板页：真实数据 + 游客可留言（无需登录，接口 /api/guestbook） -->
  <CardPanel title="留言板 · Guestbook" icon="message">
    <p class="panel-desc">这里是一本小小的留言簿——不需要登录，写几句想说的话（或夸夸这个粉粉的页面）就可以啦 ♥</p>

    <!-- 留言输入 -->
    <form class="guest-write" @submit.prevent="submit">
      <div class="nick-row">
        <span class="nick-prefix">昵称</span>
        <el-input
          v-model="nickname"
          maxlength="20"
          show-word-limit
          size="small"
          class="nick-input"
          placeholder="匿名旅人"
        />
      </div>
      <el-input
        v-model="draft"
        type="textarea"
        :rows="3"
        maxlength="300"
        show-word-limit
        placeholder="想对我说点什么？"
      />
      <div class="guest-actions">
        <el-button type="primary" round :loading="submitting" @click="submit">留下小纸条</el-button>
      </div>
    </form>

    <!-- 列表状态 -->
    <div v-if="loading" class="guest-list" aria-hidden="true">
      <div v-for="i in 4" :key="i" class="guest-skeleton">
        <div class="sk-avatar"></div>
        <div class="skeleton-body">
          <div class="sk-block" style="width: 34%"></div>
          <div class="sk-block" style="width: 80%"></div>
        </div>
      </div>
    </div>

    <template v-else-if="messages.length">
      <p class="list-count">已有 <b>{{ total }}</b> 条留言，最近 3 条在下面 ~</p>
      <div class="guest-list">
        <div v-for="m in messages" :key="m.id" class="guest-item">
          <span class="guest-avatar">{{ (m.nickname || '匿')[0] }}</span>
          <div class="guest-body">
            <div class="guest-head">
              <b>{{ m.nickname || '匿名旅人' }}</b>
              <time>{{ m.createTime }}</time>
            </div>
            <p class="guest-text">{{ m.content }}</p>
          </div>
        </div>
      </div>
      <div v-if="totalPages > 1" class="pager">
        <button class="pager-btn" type="button" :disabled="page <= 1" @click="go(page - 1)">← 上一页</button>
        <span class="pager-info">{{ page }} / {{ totalPages }}</span>
        <button class="pager-btn" type="button" :disabled="page >= totalPages" @click="go(page + 1)">下一页 →</button>
      </div>
    </template>

    <div v-else class="guest-empty">
      <p v-if="apiDown">留言板暂时休息一下下…（后端重启后就能留言啦 ♥）</p>
      <p v-else>还没有留言，来做第一个写下小纸条的人吧 ♥</p>
    </div>
  </CardPanel>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import CardPanel from '../components/CardPanel.vue'
import { listGuestbook, addGuestbook } from '../api/guestbook'
import { useAuth } from '../composables/useAuth'

const { userInfo, isLoggedIn } = useAuth()

const messages = ref([])
const total = ref(0)
const totalPages = ref(1)
const page = ref(1)
const size = 6
const loading = ref(false)
const submitting = ref(false)
const apiDown = ref(false)
const nickname = ref('')
const draft = ref('')

async function load(p = page.value) {
  loading.value = true
  try {
    const { data } = await listGuestbook(p, size)
    messages.value = data.records || []
    total.value = Number(data.total || 0)
    totalPages.value = Math.max(1, Math.ceil(total.value / size))
    apiDown.value = false
  } catch (e) {
    messages.value = []
    apiDown.value = true
  } finally {
    loading.value = false
  }
}

async function submit() {
  const text = draft.value.trim()
  if (!text) {
    ElMessage.warning('先写点什么再留言吧')
    return
  }
  submitting.value = true
  try {
    const { data } = await addGuestbook(nickname.value.trim(), text)
    ElMessage.success('小纸条已贴上 ♥')
    draft.value = ''
    // 数据库默认时间不回填到返回体，本地补一个用于即时回显
    if (!data.createTime) data.createTime = nowString()
    // 回到第一页展示最新留言
    if (page.value !== 1) {
      page.value = 1
      await load(1)
    } else {
      messages.value.unshift(data)
      total.value += 1
    }
    apiDown.value = false
  } catch (e) {
    // 旧进程/异常时保持输入，方便重启后重试
  } finally {
    submitting.value = false
  }
}

function nowString() {
  const d = new Date()
  const p = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())} ${p(d.getHours())}:${p(d.getMinutes())}:${p(d.getSeconds())}`
}

function go(p) {
  page.value = p
  load(p)
}

onMounted(async () => {
  if (!nickname.value && isLoggedIn.value && userInfo.value?.nickname) {
    nickname.value = userInfo.value.nickname
  }
  load()
})
</script>

<style scoped>
.panel-desc{margin:0 0 14px;color:var(--muted,#8a6475);font-size:12px;line-height:1.7}
/* 留言输入 */
.guest-write{display:flex;flex-direction:column;gap:10px;padding:14px;border:1px dashed var(--pink-300,#ffb7d0);border-radius:18px;background:linear-gradient(180deg,var(--pink-50,#fff7fa),#fff);margin-bottom:16px}
.nick-row{display:flex;align-items:center;gap:10px}
.nick-prefix{flex:none;color:var(--berry,#a63b64);font-size:12px;font-weight:800}
.nick-input{width:180px}
.guest-actions{display:flex;justify-content:flex-end;margin-top:2px}
.guest-actions :deep(.el-button--primary){background:linear-gradient(135deg,var(--pink-300,#ffb7d0),var(--rose,#e45b8d));border-color:transparent}
/* 列表 */
.list-count{margin:0 0 8px;color:var(--muted,#8a6475);font-size:12px}
.list-count b{color:var(--rose,#e45b8d)}
.guest-list{display:grid;gap:10px}
.guest-item{display:flex;gap:11px;padding:13px 15px;border:1px solid var(--pink-100,#ffe7f0);border-radius:16px;background:#fff;transition:transform .18s ease,border-color .18s ease}
.guest-item:hover{transform:translateX(3px);border-color:var(--pink-300,#ffb7d0)}
.guest-avatar{display:inline-grid;place-items:center;width:34px;height:34px;flex:none;border-radius:50%;background:linear-gradient(135deg,var(--pink-200,#ffd0e1),var(--pink-300,#ffb7d0));color:var(--berry,#a63b64);font-size:14px;font-weight:800}
.guest-body{flex:1;min-width:0}
.guest-head{display:flex;justify-content:space-between;align-items:baseline;gap:10px;font-size:12px;color:var(--muted,#8a6475)}
.guest-head b{color:var(--ink,#4a2b3a);font-weight:800}
.guest-text{margin:4px 0 0;font-size:13px;line-height:1.65;color:var(--ink,#4a2b3a);word-break:break-word}
.guest-empty,.guest-skeleton{display:grid;gap:8px;padding:18px 0}
.guest-empty{text-align:center;color:var(--muted,#8a6475);font-size:13px}
.guest-empty p{margin:0}
.skeleton-body{display:grid;gap:6px;flex:1}
.guest-skeleton{grid-template-columns:34px 1fr}
.sk-avatar{width:34px;height:34px;border-radius:50%;background:linear-gradient(90deg,#ffe7f0 25%,#fff1f7 37%,#ffe7f0 63%);background-size:400% 100%;animation:shimmer 1.4s ease infinite}
.sk-block{height:12px;border-radius:8px;background:linear-gradient(90deg,#ffe7f0 25%,#fff1f7 37%,#ffe7f0 63%);background-size:400% 100%;animation:shimmer 1.4s ease infinite}
@keyframes shimmer{0%{background-position:100% 0}100%{background-position:-100% 0}}
.pager{display:flex;align-items:center;justify-content:center;gap:14px;margin-top:16px}
.pager-btn{display:inline-flex;padding:6px 14px;border:1px solid var(--pink-300,#ffb7d0);border-radius:999px;background:#fff;color:var(--berry,#a63b64);font-size:12px;font-weight:800;cursor:pointer;transition:all .2s ease}
.pager-btn:hover:not(:disabled){background:var(--pink-50,#fff7fa)}
.pager-btn:disabled{opacity:.45;cursor:not-allowed}
.pager-info{font-size:12px;color:var(--muted,#8a6475);font-weight:700}
</style>
