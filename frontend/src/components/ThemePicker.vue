<template>
  <!-- 换装按钮：右下贴纸风圆钮，点击展开色板（主题持久化于 localStorage） -->
  <div class="theme-picker">
    <Teleport to="body">
      <transition name="tp-pop">
        <div v-if="open" class="tp-panel" role="dialog" aria-label="选择主题色">
          <p class="tp-title">今日心情 · 换装 ♥</p>
          <button
            v-for="t in themes"
            :key="t.key"
            class="tp-item"
            :class="{ on: current === t.key }"
            type="button"
            @click="pick(t.key)"
          >
            <span class="tp-dot" :style="{ background: t.dot }"></span>
            <span class="tp-label">{{ t.label }}</span>
            <svg v-if="current === t.key" class="tp-check" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M20 6 9 17l-5-5"/></svg>
          </button>
        </div>
      </transition>
    </Teleport>

    <button
      class="tp-toggle"
      type="button"
      :aria-expanded="open"
      aria-label="切换主题装扮"
      :title="`换装（当前：${currentLabel}）`"
      @click="open = !open"
    >
      <span class="tp-dot-ring" :style="{ borderColor: currentDot }">
        <span class="tp-dot" :style="{ background: currentDot }"></span>
      </span>
    </button>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { THEMES, applyTheme, getStoredTheme } from '../utils/theme'

const themes = THEMES
const open = ref(false)
const current = ref(getStoredTheme())

const currentDot = computed(() => themes.find((t) => t.key === current.value)?.dot || '#e45b8d')
const currentLabel = computed(() => themes.find((t) => t.key === current.value)?.label || '经典甜粉')

function pick(key) {
  if (current.value !== key) {
    current.value = key
    applyTheme(key)
  }
  open.value = false
}

function onDocClick(e) {
  if (open.value && !e.target.closest('.theme-picker') && !e.target.closest('.tp-panel')) open.value = false
}

onMounted(() => document.addEventListener('click', onDocClick))
onBeforeUnmount(() => document.removeEventListener('click', onDocClick))
</script>

<style scoped>
.theme-picker{position:fixed;right:20px;bottom:26px;z-index:1500}
.tp-toggle{display:grid;place-items:center;width:44px;height:44px;border:2px solid var(--pink-300,#ffb7d0);border-radius:50%;background:rgba(255,255,255,.92);box-shadow:0 8px 18px rgba(183,85,129,.16);cursor:pointer;transition:transform .2s ease,box-shadow .2s ease}
.tp-toggle:hover{transform:translateY(-2px) rotate(12deg);box-shadow:0 10px 22px rgba(183,85,129,.24)}
.tp-dot-ring{display:grid;place-items:center;width:22px;height:22px;border-radius:50%;border:2px dashed currentColor}
.tp-dot-ring .tp-dot{width:11px;height:11px;border-radius:50%}
.tp-panel{position:fixed;right:20px;bottom:80px;z-index:1500;display:grid;gap:4px;min-width:172px;padding:12px;border:1px solid var(--pink-300,#ffb7d0);border-radius:18px;background:rgba(255,255,255,.97);box-shadow:0 14px 30px rgba(74,43,58,.18)}
.tp-title{margin:0 6px 8px;color:var(--berry,#a63b64);font-size:12px;font-weight:800;text-align:center}
.tp-item{display:flex;align-items:center;gap:9px;width:100%;padding:7px 10px;border:1px solid transparent;border-radius:12px;background:transparent;color:var(--ink,#4a2b3a);font-family:inherit;font-size:13px;font-weight:700;cursor:pointer;transition:all .15s ease;text-align:left}
.tp-item:hover{background:var(--pink-50,#fff7fa)}
.tp-item.on{border-color:var(--pink-300,#ffb7d0);background:var(--pink-50,#fff7fa)}
.tp-item .tp-dot{width:16px;height:16px;flex:none;border-radius:50%;box-shadow:0 0 0 2px #fff,0 2px 6px rgba(0,0,0,.14)}
.tp-check{width:14px;height:14px;margin-left:auto;color:var(--rose,#e45b8d)}
.tp-pop-enter-active,.tp-pop-leave-active{transition:opacity .16s ease,transform .16s ease}
.tp-pop-enter-from,.tp-pop-leave-to{opacity:0;transform:translateY(6px)}
@media (max-width:560px){
  .theme-picker{right:14px;bottom:14px}
  .tp-panel{right:14px;bottom:66px}
}
</style>
