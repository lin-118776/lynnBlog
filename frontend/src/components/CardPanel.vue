<template>
  <section class="card-panel" :id="panelId || undefined">
    <h2 class="card-heading">
      <AppIcon v-if="icon" :name="icon" :size="16" />
      <slot name="title">{{ title }}</slot>
    </h2>
    <div class="card-body">
      <slot />
    </div>
    <div v-if="$slots.footer" class="card-footer">
      <slot name="footer" />
    </div>
  </section>
</template>

<script setup>
import AppIcon from './AppIcon.vue'

// 通用卡片面板：粉色手账风的分区外壳（标题栏 + 内容区 + 可选底部区）
defineProps({
  title: { type: String, default: '' },
  icon: { type: String, default: '' },   // AppIcon 图标名，如 book / image / message
  panelId: { type: String, default: '' } // 锚点 id（如 #updates）
})
</script>

<style scoped>
/* 颜色/阴影变量来自外层 .sanrio-page（CSS 变量可继承），此处带兜底值 */
.card-panel{border:1px solid var(--line,#efc2d3);border-radius:var(--radius-lg,22px);background:var(--cream,#fffaf5);box-shadow:var(--shadow,0 12px 30px rgba(183,85,129,.12));overflow:hidden}
.card-heading{display:flex;align-items:center;gap:7px;margin:0;padding:13px 15px;border-bottom:1px solid var(--pink-200,#ffd0e1);background:linear-gradient(180deg,#fff1f7,#ffe7f0);border-radius:var(--radius-lg,22px) var(--radius-lg,22px) 0 0;color:var(--berry,#a63b64);font-size:13px;letter-spacing:.5px}
.card-body{padding:14px}
.card-footer{padding:0 14px 14px}
</style>
