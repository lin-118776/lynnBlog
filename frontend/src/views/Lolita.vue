<template>
  <div class="lolita-page">
    <!-- 手账风标题区 -->
    <header class="lolita-head">
      <div class="head-bow" aria-hidden="true">
        <svg viewBox="0 0 48 30" fill="currentColor"><path d="M24 12C20 6 14 2 8 4 4 5 2 9 3 13c1 6 8 10 21 15 13-5 20-9 21-15 1-4-1-8-5-9-6-2-12 2-16 8Z" transform="translate(-9 -3) scale(1.3)"/></svg>
      </div>
      <h1 class="head-title">穿搭墙<em>SWEET WARDROBE</em></h1>
      <p class="head-sub">每一条小裙子，都是值得贴进手账的心动 ♥</p>
      <div class="head-stars" aria-hidden="true">✦ ✧ ✦</div>
    </header>

    <!-- 分类贴纸筛选条 -->
    <!-- 状态贴纸筛选条 -->
    <div class="cat-tabs" role="tablist" aria-label="状态筛选">
      <button class="cat-tab" :class="{ active: !query.status }" type="button" @click="selectStatus('')">全部</button>
      <button
        v-for="(s, i) in statusTabs"
        :key="s"
        class="cat-tab"
        :class="{ active: query.status === s }"
        :style="{ '--tilt': (i % 2 ? '1.2deg' : '-1.2deg') }"
        type="button"
        @click="selectStatus(s)"
      >{{ s }}</button>
    </div>

    <!-- 手账拼贴瀑布流 -->
    <div v-loading="loading" class="wall">
      <article
        v-for="(item, idx) in items"
        :key="item.id"
        class="garment-card"
        :class="'tape-' + (idx % 4)"
        @click="openDetail(item)"
      >
        <div class="card-photo">
          <img v-if="item.coverImage" :src="item.coverImage" :alt="item.name" loading="lazy" />
          <ImgSlot v-else variant="tile" label="服饰" />
          <span v-if="item.wearCount >= 3" class="stamp">♥{{ item.wearCount }}</span>
        </div>
        <div class="card-body">
          <h3 class="card-name">{{ item.name }}</h3>
          <p class="card-sub">{{ item.brand || '未知品牌' }}<template v-if="item.series"> · {{ item.series }}</template></p>
          <div class="card-meta">
            <span class="sticker status">{{ item.status || '未标注' }}</span>
            <span class="sticker">{{ item.category }}</span>
            <span v-if="item.color" class="sticker color">{{ item.color }}</span>
            <span class="meta-price">{{ priceText(item) }}</span>
          </div>
        </div>
      </article>

      <div v-if="!loading && !items.length" class="wall-empty">
        <p>这里还没有贴纸，<br />等 Lynn 慢慢把心动贴上来 ♥</p>
      </div>
    </div>

    <!-- 分页 -->
    <el-pagination
      v-if="total > query.size"
      class="pagination"
      background
      layout="prev, pager, next"
      :total="total"
      :page-size="query.size"
      :current-page="query.page"
      @current-change="load"
    />

    <!-- 详情便签弹窗 -->
    <el-dialog v-model="detailVisible" :title="detail?.name || '穿搭详情'" width="min(660px, 94vw)">
      <div v-if="detail" class="detail-body">
        <div class="detail-photo">
          <img v-if="detail.coverImage" :src="detail.coverImage" :alt="detail.name" />
          <ImgSlot v-else variant="tile" label="服饰" />
        </div>
        <div class="detail-note">
          <div class="note-row"><span>品牌</span><b>{{ detail.brand || '未知' }}</b></div>
          <div class="note-row" v-if="detail.series"><span>系列</span><b>{{ detail.series }}</b></div>
          <div class="note-row"><span>类型</span><b>{{ detail.category }}</b></div>
          <div class="note-row" v-if="detail.color"><span>主色</span><b>{{ detail.color }}</b></div>
          <div class="note-row" v-if="detail.size"><span>尺码</span><b>{{ detail.size }}</b></div>
          <div class="note-row" v-if="detail.status"><span>状态</span><b>{{ detail.status }}</b></div>
          <div class="note-row" v-if="detail.status === '待补款' && detail.balanceDue"><span>待补金额</span><b>¥{{ detail.balanceDue }}</b></div>
          <div class="note-row" v-if="detail.purchasePrice"><span>入手价</span><b>¥{{ detail.purchasePrice }}</b></div>
          <div class="note-row" v-if="detail.purchaseDate"><span>入手日期</span><b>{{ fmtDate(detail.purchaseDate) }}</b></div>
          <div class="note-row"><span>穿着次数</span><b>{{ detail.wearCount || 0 }} 次</b></div>
          <div v-if="detail.note" class="note-text">{{ detail.note }}</div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { listLolita, getLolitaDetail } from '../api/lolita'
import ImgSlot from '../components/ImgSlot.vue'

const items = ref([])
const total = ref(0)
const loading = ref(false)
const query = reactive({ page: 1, size: 12, status: '' })
const detailVisible = ref(false)
const detail = ref(null)

// 按状态分类筛选（状态重新规定：待补款 / 已拥有 / 已售出）
const statusTabs = ['待补款', '已拥有', '已售出']

async function load(page = query.page) {
  query.page = page
  loading.value = true
  try {
    const { data } = await listLolita(query.page, query.size, { status: query.status || undefined })
    items.value = data.records || []
    total.value = Number(data.total || 0)
  } catch (e) {
    items.value = []
  } finally {
    loading.value = false
  }
}

function selectStatus(s) {
  query.status = s
  load(1)
}

async function openDetail(item) {
  try {
    const { data } = await getLolitaDetail(item.id)
    detail.value = data
    detailVisible.value = true
  } catch (e) {
    // 错误提示已由请求封装统一处理
  }
}

function fmtDate(v) {
  return v ? String(v).slice(0, 10) : ''
}

// 价格展示：待补款只显示待补金额（不显示总价），其他显示入手价
function priceText(item) {
  if (item.status === '待补款') return item.balanceDue != null ? `待补 ¥${item.balanceDue}` : ''
  return item.purchasePrice != null ? `¥${item.purchasePrice}` : ''
}

onMounted(() => load())
</script>

<style scoped>
.lolita-page{display:flex;flex-direction:column;gap:18px;min-width:0}

/* ===== 手账标题区 ===== */
.lolita-head{position:relative;padding:26px 24px 20px;border:1px solid var(--pink-200,#ffd0e1);border-radius:var(--radius-lg,22px);background:linear-gradient(160deg,#fff5f8 0%,#ffe7f0 55%,#fff0f6 100%);box-shadow:var(--shadow,0 12px 30px rgba(183,85,129,.12));text-align:center;overflow:hidden}
.lolita-head::before{content:"";position:absolute;left:0;right:0;top:0;height:6px;background:repeating-linear-gradient(90deg,var(--pink-300,#ffb7d0) 0 14px,#fff 14px 26px)}
.head-bow{color:var(--rose,#e45b8d);font-size:0;margin-bottom:2px}
.head-bow svg{width:52px;height:34px;filter:drop-shadow(0 3px 6px rgba(227,91,141,.28))}
.head-title{margin:2px 0 0;font-size:30px;line-height:1.1;color:var(--berry,#a63b64);letter-spacing:3px}
.head-title em{display:block;margin-top:6px;font-style:normal;font-size:11px;letter-spacing:5px;color:var(--pink-400,#f9a8c4)}
.head-sub{margin:12px 0 0;font-family:"Kaiti SC","STKaiti",KaiTi,serif;font-size:15px;color:var(--rose,#e45b8d)}
.head-stars{margin-top:10px;color:var(--pink-400,#f9a8c4);font-size:13px;letter-spacing:12px}

/* ===== 分类贴纸 ===== */
.cat-tabs{display:flex;flex-wrap:wrap;gap:10px;padding:4px 2px}
.cat-tab{position:relative;padding:6px 16px;border:1.5px dashed var(--pink-300,#ffb7d0);border-radius:10px;background:#fff;color:var(--muted,#8a6475);font-size:13px;font-weight:800;letter-spacing:1px;cursor:pointer;transition:all .2s ease;transform:rotate(var(--tilt,0deg));box-shadow:0 2px 6px rgba(183,85,129,.08)}
.cat-tab::after{content:"✂";position:absolute;right:-4px;top:-8px;font-size:10px;color:var(--pink-400,#f9a8c4);opacity:.7}
.cat-tab:hover{color:var(--rose,#e45b8d);transform:rotate(var(--tilt,0deg)) translateY(-2px);box-shadow:0 6px 14px rgba(183,85,129,.16)}
.cat-tab.active{background:linear-gradient(135deg,var(--pink-300,#ffb7d0),var(--rose,#e45b8d));color:#fff;border-style:solid;border-color:transparent;transform:rotate(calc(var(--tilt,0deg) * -1)) translateY(2px);box-shadow:0 6px 16px rgba(227,91,141,.35)}

/* ===== 手账墙：规整 4 列网格（每行固定 4 张） ===== */
.wall{display:grid;grid-template-columns:repeat(4,minmax(0,1fr));gap:16px;min-height:200px;padding:4px 2px}
.garment-card{position:relative;display:flex;flex-direction:column;min-width:0;background:#fff;border:1px solid var(--pink-100,#ffe7f0);border-radius:12px;box-shadow:0 5px 14px rgba(183,85,129,.1);cursor:pointer;transition:transform .25s cubic-bezier(.22,.9,.3,1.3),box-shadow .25s ease}
.garment-card:hover{transform:translateY(-5px);box-shadow:0 16px 30px rgba(183,85,129,.2);z-index:2}
/* 图片区域：统一 3:4 竖图比例 */
.card-photo{position:relative;aspect-ratio:3/4;overflow:hidden;border-radius:11px 11px 0 0;border-bottom:3px solid #fff}
.card-photo img{position:absolute;inset:0;width:100%;height:100%;object-fit:cover;transition:transform .35s ease}
.card-photo :deep(.img-slot){position:absolute;inset:0}
.garment-card:hover .card-photo img{transform:scale(1.05)}
/* 胶带装饰：统一贴左上角，颜色/长短/角度略有差异，可伸出卡片边界 */
.garment-card::before{content:"";position:absolute;top:-8px;left:-16px;z-index:3;width:90px;height:16px;border-radius:2px;background:rgba(255,183,208,.82);transform:rotate(-42deg);box-shadow:0 2px 6px rgba(183,85,129,.18);pointer-events:none}
.garment-card.tape-1::before{left:4px;top:-9px;width:62px;height:14px;background:rgba(249,168,196,.72);transform:rotate(-36deg)}
.garment-card.tape-2::before{left:-14px;top:2px;width:78px;height:15px;background:rgba(255,248,251,.95);transform:rotate(-48deg)}
.garment-card.tape-3::before{left:16px;top:-7px;width:66px;height:15px;background:rgba(255,208,225,.85);transform:rotate(-32deg)}
/* 穿着次数印章 */
.stamp{position:absolute;right:8px;bottom:8px;z-index:2;display:grid;place-items:center;width:40px;height:40px;border-radius:50%;background:var(--rose,#e45b8d);color:#fff;font-size:12px;font-weight:800;transform:rotate(-10deg);box-shadow:0 4px 10px rgba(227,91,141,.4);border:2px solid rgba(255,255,255,.7)}
.card-body{flex:none;padding:10px 12px 12px}
.card-name{margin:0;font-size:13.5px;color:var(--ink,#4a2b3a);font-weight:800;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}
.card-sub{margin:4px 0 8px;font-size:11.5px;color:var(--muted,#8a6475);white-space:nowrap;overflow:hidden;text-overflow:ellipsis}
/* 状态/分类/颜色/价格：标签挤在一行，价格右对齐 */
.card-meta{display:flex;flex-wrap:wrap;align-items:center;gap:5px;margin-bottom:2px}
.meta-price{margin-left:auto;font-size:12.5px;font-weight:800;color:var(--berry,#a63b64);white-space:nowrap}
.sticker{display:inline-flex;padding:2px 9px;border-radius:6px;background:var(--sky,#d9edfb);color:#35698a;font-size:10.5px;font-weight:800;transform:rotate(-1deg)}
.sticker.status{background:var(--pink-100,#ffe7f0);color:var(--rose,#e45b8d)}
.sticker.color{background:var(--pink-100,#ffe7f0);color:var(--rose,#e45b8d);transform:rotate(1deg)}
.wall-empty{grid-column:1/-1;display:flex;align-items:center;justify-content:center;padding:70px 20px;color:var(--muted,#8a6475);font-size:14px;line-height:1.9;text-align:center}
.pagination{margin-top:10px;justify-content:flex-end}

/* ===== 详情便签 ===== */
.detail-body{display:grid;grid-template-columns:250px 1fr;gap:20px}
.detail-photo{overflow:hidden;border:4px solid #fff;border-radius:16px;box-shadow:0 8px 22px rgba(183,85,129,.16);transform:rotate(-1.5deg)}
.detail-photo img{display:block;width:100%;aspect-ratio:3/4;object-fit:cover}
.detail-note{display:flex;flex-direction:column;gap:8px;min-width:0;padding:14px 16px;border-radius:14px;background:repeating-linear-gradient(180deg,#fffaf5 0 26px,#ffeef5 26px 27px);border:1px dashed var(--pink-300,#ffb7d0)}
.note-row{display:flex;gap:12px;font-size:13px;border-bottom:1px dashed var(--pink-200,#ffd0e1);padding-bottom:8px}
.note-row span{flex:none;width:62px;color:var(--muted,#8a6475)}
.note-row b{color:var(--ink,#4a2b3a)}
.note-text{margin-top:4px;font-family:"Kaiti SC","STKaiti",KaiTi,serif;font-size:14px;line-height:1.8;color:var(--berry,#a63b64);white-space:pre-wrap}
@media (max-width:1100px){
  .wall{grid-template-columns:repeat(3,minmax(0,1fr))}
}
@media (max-width:900px){
  .wall{grid-template-columns:repeat(2,minmax(0,1fr))}
  .detail-body{grid-template-columns:1fr}
}
@media (max-width:480px){
  .wall{grid-template-columns:1fr}
  .detail-photo img{aspect-ratio:4/3}
  .head-title{font-size:24px}
}
</style>
