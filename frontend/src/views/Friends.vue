<template>
  <!-- 友链页：模仿 xnmoe 三段式（友链列表 / 交换名片 / 友链申请） -->
  <CardPanel title="Friends" icon="grid">
    <!-- 🌐 友链列表 -->
    <h3 class="section-title">🌐 友链列表</h3>
    <p class="section-desc">交换友链的朋友们 ♥ 点击卡片可以访问他们的站点</p>

    <div v-loading="loading" class="friend-grid">
      <a
        v-for="(f, i) in friends"
        :key="f.id"
        class="friend-item"
        :class="'tilt-' + (i % 4)"
        :href="f.url"
        target="_blank"
        rel="noopener noreferrer"
      >
        <span class="friend-avatar">
          <img v-if="f.avatar" :src="f.avatar" :alt="f.name" loading="lazy" />
          <span v-else class="avatar-fallback">♥</span>
        </span>
        <span class="friend-name">{{ f.name }}</span>
        <span class="friend-desc">{{ f.description || '欢迎来访 ♥' }}</span>
      </a>

      <div v-if="!loading && !friends.length" class="friend-empty">
        还没有友链，<br />想交换友链就去留言板找 Lynn 吧 ♥
      </div>
    </div>

    <!-- 💌 交换名片 -->
    <h3 class="section-title">💌 交换名片</h3>
    <div class="my-card">
      <div class="my-card-avatar">
        <img :src="avatarSrc" alt="Lynn 的头像" />
      </div>
      <div class="my-card-info">
        <p class="my-card-name">Lynn's Blog<small>SWEET &amp; TINY DIARY</small></p>
        <p class="my-card-desc">记录、分享与成长 ♥ 三丽鸥手账风小站</p>
        <p class="my-card-url">{{ siteUrl }}</p>
      </div>
      <el-button class="copy-btn" type="primary" round size="small" @click="copyCard">
        {{ copied ? '已复制!' : '复制名片' }}
      </el-button>
    </div>

    <!-- 📝 友链申请 -->
    <h3 class="section-title">📝 友链申请</h3>
    <div class="apply-box">
      <p class="apply-tip">想和我交换友链？填好下面的申请单，我看到后会尽快审核添加 ♥</p>
      <el-form :model="applyForm" label-position="top" class="apply-form">
        <el-form-item label="站点名" required>
          <el-input v-model="applyForm.name" maxlength="50" placeholder="你的站点叫什么？" />
        </el-form-item>
        <el-form-item label="地址" required>
          <el-input v-model="applyForm.url" maxlength="500" placeholder="https://example.com" />
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="applyForm.description" maxlength="200" placeholder="用一句话介绍一下你的站点" />
        </el-form-item>
        <el-form-item label="头像">
          <el-input v-model="applyForm.avatar" maxlength="500" placeholder="https://example.com/avatar.png（选填）" />
        </el-form-item>
        <!-- 蜜罐：正常人不会填写，机器人会自动填（防刷） -->
        <el-form-item class="honeypot" aria-hidden="true">
          <el-input v-model="applyForm.website" tabindex="-1" autocomplete="off" />
        </el-form-item>
        <el-button class="apply-submit" type="primary" round :loading="applying" @click="submitApply">
          提交申请
        </el-button>
      </el-form>
    </div>
  </CardPanel>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import CardPanel from '../components/CardPanel.vue'
import { listFriends, applyFriend } from '../api/friend'
import avatarSrc from '../images/touxiang.jpg'

const friends = ref([])
const loading = ref(false)
const copied = ref(false)
const applying = ref(false)
const applyForm = reactive({ name: '', url: '', description: '', avatar: '', website: '' })

const siteUrl = computed(() => {
  if (typeof window !== 'undefined') return window.location.origin
  return 'https://lynnblog.example.com'
})

async function submitApply() {
  if (!applyForm.name.trim()) {
    ElMessage.warning('请填写你的站点名')
    return
  }
  if (!applyForm.url.trim()) {
    ElMessage.warning('请填写站点地址')
    return
  }
  applying.value = true
  try {
    await applyFriend({ ...applyForm })
    ElMessage.success('申请已提交，等待博主审核 ♥')
    Object.assign(applyForm, { name: '', url: '', description: '', avatar: '', website: '' })
  } catch (e) {
    // 错误提示已由请求封装统一处理
  } finally {
    applying.value = false
  }
}

async function copyCard() {
  const text = [
    '站点名：Lynn\'s Blog',
    `地址：${siteUrl.value}`,
    '简介：记录、分享与成长 ♥ 三丽鸥手账风小站'
  ].join('\n')
  try {
    await navigator.clipboard.writeText(text)
    copied.value = true
    ElMessage.success('名片已复制，去发给想交换友链的朋友吧 ♥')
    setTimeout(() => (copied.value = false), 2000)
  } catch (e) {
    ElMessage.warning('复制失败，请手动复制地址：' + siteUrl.value)
  }
}

onMounted(async () => {
  loading.value = true
  try {
    const { data } = await listFriends()
    friends.value = Array.isArray(data) ? data : []
  } catch (e) {
    friends.value = []
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.section-title{margin:18px 0 6px;font-size:16px;font-weight:800;color:var(--berry,#a63b64)}
.section-title::after{content:"";display:block;margin-top:6px;height:1px;background:linear-gradient(90deg,var(--pink-300,#ffb7d0),transparent)}
.section-desc{margin:0 0 12px;color:var(--muted,#8a6475);font-size:12px;line-height:1.7}

/* ===== 友链列表 ===== */
.friend-grid{display:grid;grid-template-columns:repeat(4,minmax(0,1fr));gap:12px;min-height:80px}
.friend-item{display:flex;flex-direction:column;gap:4px;align-items:center;justify-content:center;min-height:96px;padding:14px 10px;border:1px dashed var(--pink-300,#ffb7d0);border-radius:16px;background:#fff;color:var(--muted,#8a6475);text-align:center;transition:transform .22s cubic-bezier(.22,.9,.3,1.3),box-shadow .22s ease,border-color .22s ease}
.friend-item.tilt-1{transform:rotate(-.8deg)}
.friend-item.tilt-2{transform:rotate(.7deg)}
.friend-item.tilt-3{transform:rotate(-.5deg)}
.friend-item:hover{border-style:solid;color:var(--berry,#a63b64);border-color:var(--pink-400,#f9a8c4);transform:rotate(0deg) translateY(-4px);box-shadow:0 10px 22px rgba(183,85,129,.16)}
.friend-avatar{width:44px;height:44px;border-radius:50%;overflow:hidden;border:2px solid #fff;box-shadow:0 3px 8px rgba(183,85,129,.18)}
.friend-avatar img{display:block;width:100%;height:100%;object-fit:cover}
.avatar-fallback{display:grid;place-items:center;width:100%;height:100%;background:linear-gradient(135deg,var(--pink-300,#ffb7d0),var(--rose,#e45b8d));color:#fff;font-size:16px}
.friend-name{margin-top:2px;font-size:13px;font-weight:800;color:var(--ink,#4a2b3a)}
.friend-desc{font-size:10.5px;line-height:1.4}
.friend-empty{grid-column:1/-1;display:flex;align-items:center;justify-content:center;padding:48px 16px;color:var(--muted,#8a6475);font-size:13px;line-height:1.9;text-align:center}

/* ===== 交换名片 ===== */
.my-card{display:flex;align-items:center;gap:14px;margin-top:4px;padding:16px;border:1px solid var(--pink-200,#ffd0e1);border-radius:16px;background:linear-gradient(145deg,#fff5f8,#ffe7f0);box-shadow:0 6px 16px rgba(183,85,129,.1)}
.my-card-avatar{flex:none;width:64px;height:64px;border-radius:50%;overflow:hidden;border:3px solid #fff;box-shadow:0 4px 10px rgba(183,85,129,.2)}
.my-card-avatar img{display:block;width:100%;height:100%;object-fit:cover}
.my-card-info{flex:1;min-width:0}
.my-card-name{margin:0;font-size:15px;font-weight:800;color:var(--berry,#a63b64)}
.my-card-name small{display:block;margin-top:2px;font-size:10px;color:var(--pink-400,#f9a8c4);letter-spacing:2px}
.my-card-desc{margin:6px 0 2px;font-size:12px;color:var(--muted,#8a6475);line-height:1.6}
.my-card-url{margin:0;font-size:11px;color:var(--rose,#e45b8d);word-break:break-all}
.copy-btn{flex:none}

/* ===== 友链申请 ===== */
.apply-box{margin-top:4px;padding:14px 16px;border:1px dashed var(--pink-300,#ffb7d0);border-radius:14px;background:#fff;font-size:12.5px;color:var(--ink,#4a2b3a);line-height:1.8}
.apply-tip{margin:0 0 10px}
.apply-form :deep(.el-form-item){margin-bottom:12px}
.apply-form :deep(.el-form-item__label){font-size:12px;color:var(--muted,#8a6475);font-weight:700;padding-bottom:4px}
.apply-form :deep(.el-input__wrapper){border-radius:10px}
.honeypot{position:absolute;left:-9999px;top:-9999px;opacity:0;height:0;overflow:hidden}
.apply-submit{width:100%}
@media (max-width:900px){
  .friend-grid{grid-template-columns:repeat(3,minmax(0,1fr))}
}
@media (max-width:620px){
  .friend-grid{grid-template-columns:repeat(2,minmax(0,1fr))}
  .my-card{flex-wrap:wrap}
  .copy-btn{width:100%}
}
</style>
