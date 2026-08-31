<template>
  <!-- 友链页：独立路由 /friends（参考 xnmoe.com 的 Friends 页面） -->
  <CardPanel title="Friends" icon="grid">
    <p class="panel-desc">交换友链的朋友们 ♥ 想申请友链请去留言板找我～</p>
    <div class="friend-grid">
      <a v-for="i in 8" :key="i" class="friend-item" href="#">
        <span class="friend-name">友链 {{ i }}</span>
        <span class="friend-desc">等待交换中…</span>
      </a>
    </div>
  </CardPanel>
</template>

<script setup>
import CardPanel from '../components/CardPanel.vue'
</script>

<style scoped>
.panel-desc{margin:0 0 14px;color:var(--muted,#8a6475);font-size:12px;line-height:1.7}
.friend-grid{display:grid;grid-template-columns:repeat(4,1fr);gap:10px}
.friend-item{display:flex;flex-direction:column;gap:4px;align-items:center;justify-content:center;min-height:74px;padding:10px;border:1px dashed var(--pink-300,#ffb7d0);border-radius:14px;background:#fff;color:var(--muted,#8a6475);text-align:center;transition:all .2s ease}
.friend-item:hover{border-style:solid;color:var(--berry,#a63b64);border-color:var(--pink-400,#f9a8c4);transform:translateY(-2px)}
.friend-name{font-size:12px;font-weight:800;color:var(--ink,#4a2b3a)}
.friend-desc{font-size:10px}
@media (max-width:560px){
  .friend-grid{grid-template-columns:repeat(2,1fr)}
}
</style>
