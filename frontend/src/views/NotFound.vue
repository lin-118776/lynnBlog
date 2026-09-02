<template>
  <!-- 404 小纸条被风吹走了：三丽鸥手账风错误页 -->
  <div class="notfound-card">
    <div class="nf-art" aria-hidden="true">
      <div class="nf-bow">
        <svg viewBox="0 0 48 48" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linejoin="round" stroke-linecap="round"><path d="M8 16c-2-6 2-9 6-6 1-4 5-5 7-2 2-3 6-2 7 2 4-3 8 0 6 6-1 4-4 6-7 8-2 2-4 2-6 0-3-2-6-4-7-8Z"/><path d="M18 26c3 4 9 4 12 0"/></svg>
      </div>
      <div class="nf-num">404</div>
      <p class="nf-tape">小纸条不见了</p>
    </div>
    <h1 class="nf-title">咦？这张小纸条好像被风吹走了…</h1>
    <p class="nf-desc">你要找的页面不存在，或已被主人收进抽屉里啦 ♥</p>
    <div class="nf-actions">
      <router-link to="/" class="nf-btn primary">← 回到首页</router-link>
      <router-link to="/blog" class="nf-btn">去 Blog 逛逛</router-link>
      <router-link to="/search" class="nf-btn">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" aria-hidden="true"><circle cx="11" cy="11" r="7"/><path d="m20 20-3.5-3.5"/></svg>
        搜一搜
      </router-link>
    </div>
  </div>
</template>

<style scoped>
.notfound-card{display:flex;flex-direction:column;align-items:center;gap:8px;padding:54px 22px 44px;border:1px solid var(--pink-200,#ffd0e1);border-radius:var(--radius-lg,22px);background:linear-gradient(180deg,#fff,#fffaf5);box-shadow:var(--shadow,0 12px 30px rgba(183,85,129,.12));text-align:center}
.nf-art{position:relative}
.nf-bow{width:64px;height:64px;margin:0 auto;color:var(--rose,#e45b8d)}
.nf-bow svg{width:100%;height:100%}
.nf-num{margin-top:6px;font-size:64px;line-height:1;font-weight:800;letter-spacing:2px;background:linear-gradient(135deg,var(--pink-300,#ffb7d0),var(--rose,#e45b8d),var(--berry,#a63b64));-webkit-background-clip:text;background-clip:text;color:transparent;filter:drop-shadow(0 6px 14px rgba(228,91,141,.2))}
.nf-tape{display:inline-block;margin-top:14px;padding:4px 16px;border-radius:6px;background:linear-gradient(180deg,var(--lemon,#fff1b8),#ffe89c);color:var(--berry,#a63b64);font-size:12px;font-weight:800;box-shadow:0 4px 8px rgba(183,85,129,.1);transform:rotate(-2deg)}
.nf-title{margin:16px 0 0;color:var(--berry,#a63b64);font-size:20px;font-weight:800}
.nf-desc{margin:8px 0 0;color:var(--muted,#8a6475);font-size:13px}
.nf-actions{display:flex;flex-wrap:wrap;justify-content:center;gap:10px;margin-top:22px}
.nf-btn{display:inline-flex;align-items:center;gap:6px;padding:9px 18px;border:1px solid var(--pink-300,#ffb7d0);border-radius:999px;background:#fff;color:var(--berry,#a63b64);font-size:13px;font-weight:800;transition:all .2s ease}
.nf-btn svg{width:14px;height:14px}
.nf-btn:hover{border-color:var(--pink-400,#f9a8c4);transform:translateY(-2px);box-shadow:0 6px 14px rgba(183,85,129,.16)}
.nf-btn.primary{background:linear-gradient(135deg,var(--pink-300,#ffb7d0),var(--rose,#e45b8d));border-color:transparent;color:#fff;box-shadow:0 6px 16px rgba(228,91,141,.28)}
.nf-btn.primary:hover{filter:brightness(.96)}
@media (max-width:560px){
  .nf-num{font-size:52px}
  .nf-title{font-size:17px}
}
</style>
