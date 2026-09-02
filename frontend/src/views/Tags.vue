<template>
  <!-- 标签云页：聚合全站标签，点击跳转 Blog 列表按标签筛选 -->
  <CardPanel title="Tags · 标签云" icon="heart">
    <p class="tags-summary">
      全站共 <b>{{ tags.length }}</b> 个标签 —— 点击任意标签，就能把散落各处的同类文章串起来读 ♥
    </p>

    <div v-if="loading" class="cloud-loading">
      <div v-for="i in 12" :key="i" class="sk-pill" :style="{ width: 60 + (i * 13) % 70 + 'px' }"></div>
    </div>

    <div v-else-if="!tags.length" class="arch-empty">还没有任何标签，去控制台给文章加上吧 ♥</div>

    <div v-else class="tag-cloud">
      <button
        v-for="(t, i) in tags"
        :key="t.name"
        class="cloud-tag"
        :class="`size-${cloudSize(t.count)}`"
        type="button"
        :title="`${t.count} 篇文章`"
        @click="goTag(t.name)"
      >
        # {{ t.name }}
        <span class="cloud-count">{{ t.count }}</span>
      </button>
    </div>

    <!-- 关联分类快捷入口 -->
    <div v-if="categories.length" class="cat-side">
      <span class="cat-side-label">或按分类逛：</span>
      <router-link
        v-for="c in categories"
        :key="c.id"
        class="cat-chip"
        :to="{ path: '/blog', query: { categoryId: c.id } }"
      >{{ c.name }}</router-link>
    </div>
  </CardPanel>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import CardPanel from '../components/CardPanel.vue'
import { fetchAllPublishedArticles } from '../utils/articleLoader'
import { aggregateTags } from '../utils/articleText'
import { listCategories } from '../api/article'

const router = useRouter()
const loading = ref(true)
const tags = ref([])
const categories = ref([])

function cloudSize(count) {
  return Math.min(count, 5)
}

function goTag(name) {
  router.push({ path: '/blog', query: { tag: name } })
}

onMounted(async () => {
  try {
    const all = await fetchAllPublishedArticles()
    tags.value = aggregateTags(all)
  } catch (e) {
    tags.value = []
  } finally {
    loading.value = false
  }
  try {
    const { data } = await listCategories()
    categories.value = Array.isArray(data) ? data : []
  } catch (e) {
    categories.value = []
  }
})
</script>

<style scoped>
.tags-summary{margin:0 0 18px;color:var(--muted,#8a6475);font-size:13px}
.tags-summary b{color:var(--rose,#e45b8d)}
.arch-empty{padding:40px 0;text-align:center;color:var(--muted,#8a6475);font-size:13px}
/* 云布局：flex 换行，字号随热度 */
.tag-cloud{display:flex;flex-wrap:wrap;align-items:baseline;gap:12px 14px;padding:22px 10px 26px;border:1px dashed var(--pink-200,#ffd0e1);border-radius:18px;background:repeating-linear-gradient(0deg,rgba(255,255,255,.5) 0 1px,transparent 1px 26px),linear-gradient(180deg,#fffaf5,#fff)}
.cloud-tag{display:inline-flex;align-items:baseline;gap:5px;padding:4px 13px;border:1px solid transparent;border-radius:999px;background:transparent;color:var(--berry,#a63b64);cursor:pointer;font-weight:800;transition:all .18s ease;font-family:inherit;line-height:1.4}
.cloud-tag:hover{transform:translateY(-2px) scale(1.03);background:linear-gradient(135deg,var(--pink-300,#ffb7d0),var(--rose,#e45b8d));border-color:transparent;color:#fff;box-shadow:0 8px 16px rgba(228,91,141,.25)}
.cloud-count{font-size:.62em;opacity:.65}
.size-1{font-size:13px;color:var(--muted,#8a6475)}
.size-2{font-size:16px}
.size-3{font-size:19px}
.size-4{font-size:23px;color:var(--rose,#e45b8d)}
.size-5{font-size:28px;color:var(--rose,#e45b8d);text-shadow:0 2px 8px rgba(228,91,141,.2)}
/* 分类快捷 */
.cat-side{display:flex;align-items:center;gap:8px;flex-wrap:wrap;margin-top:16px}
.cat-side-label{color:var(--muted,#8a6475);font-size:12px}
.cat-chip{display:inline-flex;padding:4px 13px;border:1px solid var(--pink-200,#ffd0e1);border-radius:999px;background:#fff;color:var(--muted,#8a6475);font-size:12px;font-weight:700;transition:all .2s ease}
.cat-chip:hover{border-color:var(--pink-400,#f9a8c4);color:var(--berry,#a63b64);transform:translateY(-1px)}
/* 骨架屏 */
.cloud-loading{display:flex;flex-wrap:wrap;gap:12px;padding:26px 10px}
.sk-pill{height:30px;border-radius:999px;background:linear-gradient(90deg,#ffe7f0 25%,#fff1f7 37%,#ffe7f0 63%);background-size:400% 100%;animation:shimmer 1.4s ease infinite}
@keyframes shimmer{0%{background-position:100% 0}100%{background-position:-100% 0}}
</style>
