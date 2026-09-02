<template>
  <!-- 关于页：独立路由 /about（三丽鸥风格化，内容沿用原页面） -->
  <CardPanel title="About" icon="user">
    <p class="about-intro">集技术输出、求职复盘、生活记录、兴趣收藏、素材管理于一体的个人博客。</p>

    <ul class="feature-list">
      <li class="feature">
        <span class="feat-icon"><AppIcon name="code" :size="18" /></span>
        <span class="feat-body">
          <span class="feat-name">技术栈</span>
          <span class="feat-desc">Spring Boot 3 + MyBatis-Plus + MySQL 8 + Vue 3 + Element Plus</span>
        </span>
      </li>
      <li class="feature">
        <span class="feat-icon"><AppIcon name="pen" :size="18" /></span>
        <span class="feat-body">
          <span class="feat-name">技术文章</span>
          <span class="feat-desc">Markdown 写作与分享</span>
        </span>
      </li>
      <li class="feature">
        <span class="feat-icon"><AppIcon name="lock" :size="18" /></span>
        <span class="feat-body">
          <span class="feat-name">秘密日记</span>
          <span class="feat-desc">只属于你的记录空间</span>
        </span>
      </li>
      <li class="feature">
        <span class="feat-icon"><AppIcon name="sparkles" :size="18" /></span>
        <span class="feat-body">
          <span class="feat-name">Lolita 衣橱</span>
          <span class="feat-desc">兴趣收藏与穿搭统计</span>
        </span>
      </li>
    </ul>

    <p class="about-foot">感谢你的来访 ♥</p>
  </CardPanel>
</template>

<script setup>
import AppIcon from '../components/AppIcon.vue'
import CardPanel from '../components/CardPanel.vue'
</script>

<style scoped>
.about-intro{margin:0 0 16px;line-height:1.8;color:var(--ink,#4a2b3a);font-size:13px}
.feature-list{list-style:none;margin:0;padding:0;display:grid;gap:10px}
.feature{display:flex;gap:12px;align-items:center;padding:12px 14px;border:1px solid var(--pink-100,#ffe7f0);border-radius:14px;background:#fff;transition:transform .2s ease,box-shadow .2s ease}
.feature:hover{transform:translateY(-2px);box-shadow:0 8px 18px rgba(183,85,129,.1)}
.feat-icon{display:grid;place-items:center;flex:none;width:40px;height:40px;border-radius:12px;background:linear-gradient(135deg,var(--pink-100,#ffe7f0),var(--pink-200,#ffd0e1));color:var(--berry,#a63b64)}
.feat-body{display:flex;flex-direction:column;gap:2px;min-width:0}
.feat-name{font-size:13px;font-weight:800;color:var(--ink,#4a2b3a)}
.feat-desc{font-size:11px;color:var(--muted,#8a6475)}
.about-foot{margin:18px 0 2px;text-align:center;color:var(--rose,#e45b8d);font-size:12px;font-weight:800}
</style>
