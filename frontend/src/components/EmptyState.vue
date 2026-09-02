<template>
  <div class="empty-state">
    <div class="empty-icon" :style="{ width: iconSize + 26 + 'px', height: iconSize + 26 + 'px' }">
      <AppIcon :name="icon" :size="iconSize" />
    </div>
    <h3 class="empty-title">{{ title }}</h3>
    <p v-if="description" class="empty-desc">{{ description }}</p>
    <div v-if="$slots.default" class="empty-actions">
      <slot />
    </div>
  </div>
</template>

<script setup>
import AppIcon from './AppIcon.vue'

defineProps({
  icon: { type: String, default: 'sparkles' },
  title: { type: String, required: true },
  description: { type: String, default: '' },
  iconSize: { type: [Number, String], default: 30 }
})
</script>

<style scoped>
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 56px 24px;
}

.empty-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 20px;
  color: var(--brand);
  background: var(--el-color-primary-light-9);
  border: 1px solid var(--el-color-primary-light-7);
  margin-bottom: 18px;
}

.empty-title {
  margin: 0 0 8px;
  font-size: 17px;
  font-weight:500;
  color: var(--text-main);
}

.empty-desc {
  margin: 0;
  max-width: 380px;
  color: var(--text-sub);
  font-size: 13px;
  line-height: 1.7;
}

.empty-actions {
  margin-top: 22px;
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  justify-content: center;
}
</style>