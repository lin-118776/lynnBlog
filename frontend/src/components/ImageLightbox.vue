<template>
  <!-- 图片灯箱：点击正文/封面图片放大查看，ESC 或点击关闭 -->
  <Teleport to="body">
    <Transition name="lb">
      <div v-if="visible" class="lightbox" role="dialog" aria-modal="true" aria-label="图片预览" @click="close">
        <div class="lb-stage" @click.stop>
          <img :src="src" :alt="alt" />
          <span v-if="caption" class="lb-caption">{{ caption }}</span>
        </div>
        <button class="lb-close" type="button" aria-label="关闭预览" @click="close">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round" aria-hidden="true"><path d="m6 6 12 12M18 6 6 18"/></svg>
        </button>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'

const visible = ref(false)
const src = ref('')
const alt = ref('')
const caption = ref('')

function open(url, altText) {
  if (!url) return
  src.value = url
  alt.value = altText || ''
  visible.value = true
  document.body.style.overflow = 'hidden'
}

function close() {
  if (!visible.value) return
  visible.value = false
  document.body.style.overflow = ''
}

function onKeydown(e) {
  if (e.key === 'Escape') close()
}

onMounted(() => document.addEventListener('keydown', onKeydown))
onBeforeUnmount(() => {
  document.removeEventListener('keydown', onKeydown)
  document.body.style.overflow = ''
})

defineExpose({ open, close })
</script>

<style scoped>
.lightbox{position:fixed;inset:0;z-index:3000;display:grid;place-items:center;padding:5vh 4vw;background:rgba(43,24,34,.72);backdrop-filter:blur(6px)}
.lb-stage{position:relative;max-width:min(1200px,92vw);max-height:88vh;display:flex;flex-direction:column;align-items:center;gap:10px}
.lb-stage img{display:block;max-width:100%;max-height:82vh;border-radius:16px;border:4px solid #fff;box-shadow:0 24px 60px rgba(0,0,0,.4);object-fit:contain;background:#fff}
.lb-caption{color:#fff;font-size:12.5px;text-shadow:0 1px 4px rgba(0,0,0,.4)}
.lb-close{position:absolute;top:16px;right:16px;display:grid;place-items:center;width:42px;height:42px;border:0;border-radius:50%;background:rgba(255,255,255,.16);color:#fff;cursor:pointer;transition:background .2s ease,transform .2s ease}
.lb-close:hover{background:rgba(255,255,255,.32);transform:rotate(90deg)}
.lb-close svg{width:20px;height:20px}
.lb-enter-active,.lb-leave-active{transition:opacity .22s ease}
.lb-enter-active .lb-stage,.lb-leave-active .lb-stage{transition:transform .22s ease}
.lb-enter-from,.lb-leave-to{opacity:0}
.lb-enter-from .lb-stage,.lb-leave-to .lb-stage{transform:scale(.94)}
</style>
