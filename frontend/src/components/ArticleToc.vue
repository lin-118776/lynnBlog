<template>
  <nav class="toc" :class="`toc-${variant}`" :aria-label="title">
    <button
      v-if="collapsible"
      class="toc-head"
      type="button"
      :aria-expanded="!folded"
      @click="folded = !folded"
    >
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M4 6h16M4 12h10M4 18h7"/></svg>
      目录
      <span class="toc-count">{{ headings.length }}</span>
      <svg class="toc-arrow" :class="{ open: !folded }" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="m6 9 6 6 6-6"/></svg>
    </button>
    <template v-if="!collapsible || !folded">
      <p v-if="variant === 'rail'" class="toc-title">目录</p>
      <ul class="toc-list">
        <li v-for="h in headings" :key="h.id">
          <a
            :class="['toc-link', `lv-${Math.min(h.level, 4)}`, { active: h.id === activeId }]"
            :href="`#${h.id}`"
            :title="h.text"
            @click.prevent="go(h)"
          >{{ h.text }}</a>
        </li>
      </ul>
    </template>
  </nav>
</template>

<script setup>
import { ref } from 'vue'

defineProps({
  headings: { type: Array, default: () => [] },   // [{ id, text, level }]
  activeId: { type: String, default: '' },
  title: { type: String, default: '文章目录' },
  variant: { type: String, default: 'rail' },      // rail 侧栏 / panel 卡片折叠条
  collapsible: { type: Boolean, default: false }
})

const folded = ref(true)

function go(h) {
  const el = document.getElementById(h.id)
  if (!el) return
  folded.value = true
  el.scrollIntoView({ behavior: 'smooth', block: 'start' })
}
</script>

<style scoped>
.toc{min-width:0}
/* ===== 卡片折叠条（中屏/移动端：正文上方） ===== */
.toc-panel{border:1px dashed var(--pink-300,#ffb7d0);border-radius:16px;background:linear-gradient(180deg,#fff4f9,#fff);overflow:hidden}
.toc-head{display:flex;align-items:center;gap:7px;width:100%;padding:10px 14px;border:0;background:transparent;color:var(--berry,#a63b64);font-family:inherit;font-size:13px;font-weight:800;cursor:pointer}
.toc-head svg:first-child{width:15px;height:15px}
.toc-count{display:inline-grid;place-items:center;min-width:20px;height:20px;padding:0 6px;border-radius:999px;background:var(--pink-100,#ffe7f0);color:var(--rose,#e45b8d);font-size:11px}
.toc-arrow{margin-left:auto;width:14px;height:14px;transition:transform .2s ease}
.toc-arrow.open{transform:rotate(180deg)}
.toc-panel .toc-list{display:grid;grid-template-columns:repeat(auto-fill,minmax(180px,1fr));gap:2px;padding:2px 10px 10px;border-top:1px dashed var(--pink-200,#ffd0e1)}
/* ===== 右侧固定目录栏（宽屏） ===== */
.toc-rail{position:sticky;top:calc(var(--nav-h,66px) + 130px);max-height:calc(100vh - var(--nav-h,66px) - 170px);overflow-y:auto;padding:14px 10px;border:1px solid var(--pink-200,#ffd0e1);border-radius:18px;background:rgba(255,250,245,.92);box-shadow:0 8px 20px rgba(183,85,129,.1);backdrop-filter:blur(6px)}
.toc-title{margin:0 6px 8px;color:var(--muted,#8a6475);font-size:11px;font-weight:800;letter-spacing:2px;text-align:center}
.toc-rail .toc-list{display:flex;flex-direction:column;gap:2px}
/* 公共链接样式 */
.toc-link{display:block;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;padding:5px 9px;border-radius:9px;color:var(--muted,#8a6475);font-size:12.5px;line-height:1.4;font-weight:700;transition:all .15s ease;border:1px solid transparent}
.toc-link:hover{color:var(--berry,#a63b64);background:var(--pink-50,#fff7fa)}
.toc-link.lv-3{padding-left:19px;font-size:12px;font-weight:600;color:var(--muted,#8a6475)}
.toc-link.lv-3{opacity:.9}
.toc-link.lv-4{padding-left:29px;font-size:11.5px;font-weight:600;color:var(--muted,#8a6475);opacity:.8}
.toc-link.active{background:linear-gradient(135deg,var(--pink-300,#ffb7d0),var(--rose,#e45b8d));border-color:transparent;color:#fff;box-shadow:0 4px 10px rgba(228,91,141,.22)}
</style>
