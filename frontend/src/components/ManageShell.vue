<template>
  <el-card shadow="never">
    <template #header>{{ title }}</template>
    <!-- 模块未开发时展示占位空态；开发完成后传 developing=false 并用默认插槽填充内容 -->
    <EmptyState
      v-if="developing"
      :icon="icon"
      :title="`「${title}」模块开发中`"
      :description="description"
    />
    <slot v-else />
  </el-card>
</template>

<script setup>
import EmptyState from './EmptyState.vue'

defineProps({
  title: { type: String, required: true },
  icon: { type: String, default: 'pen' },
  description: { type: String, default: '' },
  developing: { type: Boolean, default: true }
})
</script>
