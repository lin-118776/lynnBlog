<template>
  <div class="page-head">
    <div class="page-head-icon"><AppIcon :name="icon" :size="20" /></div>
    <div>
      <h1>{{ title }}</h1>
      <p v-if="subtitle">{{ subtitle }}</p>
    </div>
    <slot name="action" />
  </div>
</template>

<script setup>
import AppIcon from './AppIcon.vue'

// 通用页面头部：图标 + 标题 + 副标题（样式由全局 styles/main.css 的 .page-head 提供）
defineProps({
  icon: { type: String, required: true },
  title: { type: String, required: true },
  subtitle: { type: String, default: '' }
})
</script>
