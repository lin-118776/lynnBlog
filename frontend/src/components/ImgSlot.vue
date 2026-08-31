<template>
  <!-- 有 src 时渲染真实图片，否则渲染占位框 -->
  <img v-if="src" class="img-slot img-slot--real" :src="src" :alt="label || ''" />
  <div
    v-else
    class="img-slot"
    :class="variant ? `img-slot--${variant}` : null"
    role="img"
    :aria-label="label || '图片占位'"
  >
    <span v-if="label" v-html="label"></span>
  </div>
</template>

<script setup>
// 通用图片占位组件：占位框样式集中在此，父组件只传 variant
// variant 说明：
//   square  正方形（aspect 1/1）
//   banner  顶部横幅（aspect 16/6，带彩点背景）
//   small   小尺寸（min-height 48）
//   avatar  侧栏头像（min-height 88）
//   screen  播放器画面（无边框）
//   cover   播放器专辑封面（58×58）
//   tile    Gallery 作品格（min-height 118）
//   chat    留言头像（34×34 圆形）
//   fluid   主内容区（height 100%）
defineProps({
  src: { type: String, default: '' },
  label: { type: String, default: '' },
  variant: { type: String, default: '' }
})
</script>

<style scoped>
/* 颜色/圆角变量来自外层 .sanrio-page（CSS 变量可继承），此处带兜底值 */
.img-slot{position:relative;display:grid;place-items:center;width:100%;min-height:80px;overflow:hidden;border:2px dashed var(--pink-300,#ffb7d0);border-radius:var(--radius-md,16px);background:linear-gradient(135deg,rgba(249,168,196,.08),rgba(255,255,255,.72)),repeating-linear-gradient(45deg,rgba(249,168,196,.08) 0 10px,rgba(255,255,255,0) 10px 20px);color:var(--muted,#8a6475);font-size:12px;letter-spacing:.4px;text-align:center}
.img-slot:before{content:"";position:absolute;inset:8px;border:1px solid rgba(249,168,196,.34);border-radius:10px;pointer-events:none}
.img-slot>span{position:relative;z-index:1;padding:4px 8px;border-radius:999px;background:rgba(255,250,245,.86)}
.img-slot--square{aspect-ratio:1/1}
.img-slot--banner{aspect-ratio:16/6;border-radius:var(--radius-lg,22px);background:linear-gradient(135deg,rgba(249,168,196,.14),rgba(255,255,255,.78)),radial-gradient(circle at 12% 30%,rgba(255,241,184,.42),transparent 24%),radial-gradient(circle at 82% 70%,rgba(205,238,222,.52),transparent 26%)}
.img-slot--small{min-height:48px;border-radius:12px}
.img-slot--avatar{min-height:88px;border-radius:16px;font-size:10px}
.img-slot--screen{border:0;border-radius:0;background:rgba(255,255,255,.52);min-height:0}
.img-slot--cover{width:58px;height:58px;min-height:0;flex:none;border-radius:12px}
.img-slot--tile{min-height:118px}
.img-slot--chat{width:34px;height:34px;min-height:0;border-radius:50%;font-size:0}
.img-slot--fluid{height:100%;min-height:276px}
.img-slot--real{object-fit:cover;border-style:solid}
@media (max-width:680px){
  .img-slot--banner{aspect-ratio:16/9}
  .img-slot--fluid{min-height:216px}
}
</style>
