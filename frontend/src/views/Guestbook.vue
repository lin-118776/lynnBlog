<template>
  <!-- 留言板页：独立路由 /guestbook（参考 xnmoe.com 的 Guestbook 页面） -->
  <CardPanel title="Guestbook" icon="message">
    <p class="panel-desc">在这里留下你的足迹吧 ♥（留言接口待接入，当前为预览）</p>

    <div class="guest-list">
      <div v-for="m in mockMessages" :key="m.name" class="guest-item">
        <ImgSlot variant="chat" />
        <div class="guest-body">
          <span class="guest-name">{{ m.name }}</span>
          <p class="guest-text">{{ m.text }}</p>
        </div>
        <time class="guest-time">{{ m.date }}</time>
      </div>
    </div>

    <form class="guest-form" @submit.prevent="submitMessage">
      <el-input
        v-model="draft"
        type="textarea"
        :rows="3"
        maxlength="200"
        show-word-limit
        placeholder="想对我说点什么？"
      />
      <div class="guest-actions">
        <el-button type="primary" round @click="submitMessage">留言</el-button>
      </div>
    </form>
  </CardPanel>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import CardPanel from '../components/CardPanel.vue'
import ImgSlot from '../components/ImgSlot.vue'

const draft = ref('')

const mockMessages = [
  { name: 'Momo', text: '好喜欢这个粉粉的配色，已经收藏啦～', date: '08-31' },
  { name: 'Yuki', text: '更新辛苦了！贴纸的排版真好看。', date: '08-30' },
  { name: 'Sora', text: '三丽鸥风格也太可爱了，蹲一个友链。', date: '08-28' },
  { name: 'Nana', text: '路过留爪，欢迎回访～', date: '08-25' }
]

function submitMessage() {
  if (!draft.value.trim()) {
    ElMessage.warning('先写点什么再留言吧')
    return
  }
  ElMessage.info('留言功能建设中，敬请期待 ♥')
  draft.value = ''
}
</script>

<style scoped>
.panel-desc{margin:0 0 10px;color:var(--muted,#8a6475);font-size:12px;line-height:1.7}
.guest-list{display:grid;gap:2px}
.guest-item{display:grid;grid-template-columns:34px 1fr auto;gap:9px;align-items:start;padding:11px 0;border-bottom:1px dashed var(--pink-200,#ffd0e1)}
.guest-item:last-child{border-bottom:0}
.guest-name{font-size:12px;font-weight:800;color:var(--ink,#4a2b3a)}
.guest-text{margin:2px 0 0;font-size:11px;color:var(--muted,#8a6475);line-height:1.55}
.guest-time{font-size:9px;color:rgba(74,43,58,.48);white-space:nowrap}
.guest-form{margin-top:16px;padding-top:14px;border-top:1px solid var(--pink-100,#ffe7f0)}
.guest-actions{display:flex;justify-content:flex-end;margin-top:10px}
</style>
