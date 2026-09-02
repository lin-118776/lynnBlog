<template>
  <!-- 阅读进度条（顶部细条）+ 回到顶部（右下圆钮），站点级组件 -->
  <div class="scroll-gadgets">
    <!-- 顶部阅读进度 -->
    <div class="reading-progress" :class="{ show: progress > 0.02 }" aria-hidden="true">
      <div class="rp-bar" :style="{ width: `${progress * 100}%` }"></div>
    </div>

    <!-- 回到顶部 -->
    <Transition name="tp-pop">
      <button
        v-if="showTop"
        class="back-top"
        type="button"
        aria-label="回到顶部"
        title="回到顶部"
        @click="scrollTop"
      >
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="m6 14 6-6 6 6"/></svg>
      </button>
    </Transition>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'

const progress = ref(0)
const showTop = ref(false)
let ticking = false

function compute() {
  ticking = false
  const doc = document.documentElement
  const max = doc.scrollHeight - window.innerHeight
  progress.value = max > 0 ? Math.min(1, Math.max(0, window.scrollY / max)) : 0
  showTop.value = window.scrollY > 520
}

function onScroll() {
  if (ticking) return
  ticking = true
  requestAnimationFrame(compute)
}

function scrollTop() {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(() => {
  window.addEventListener('scroll', onScroll, { passive: true })
  window.addEventListener('resize', onScroll, { passive: true })
  compute()
})

onBeforeUnmount(() => {
  window.removeEventListener('scroll', onScroll)
  window.removeEventListener('resize', onScroll)
})
</script>

<style scoped>
.reading-progress{position:fixed;inset:0 0 auto;z-index:1600;height:3px;pointer-events:none;opacity:0;transition:opacity .3s ease}
.reading-progress.show{opacity:1}
.rp-bar{height:100%;background:linear-gradient(90deg,var(--pink-300,#ffb7d0),var(--rose,#e45b8d),var(--berry,#a63b64));border-radius:0 2px 2px 0;box-shadow:0 1px 4px rgba(228,91,141,.35)}
.back-top{position:fixed;right:20px;bottom:84px;z-index:1500;display:grid;place-items:center;width:42px;height:42px;border:2px solid var(--pink-300,#ffb7d0);border-radius:50%;background:rgba(255,255,255,.94);color:var(--rose,#e45b8d);cursor:pointer;box-shadow:0 8px 18px rgba(183,85,129,.16);transition:transform .2s ease,color .2s ease}
.back-top:hover{transform:translateY(-3px);color:var(--berry,#a63b64)}
.back-top svg{width:19px;height:19px}
.tp-pop-enter-active,.tp-pop-leave-active{transition:opacity .18s ease,transform .18s ease}
.tp-pop-enter-from,.tp-pop-leave-to{opacity:0;transform:translateY(8px)}
@media (max-width:560px){
  .back-top{right:14px;bottom:72px;width:38px;height:38px}
}
</style>
