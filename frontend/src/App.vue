<template>
  <div class="app">
    <!-- 公开页顶部导航；控制台内部有自己的侧边菜单 -->
    <header v-if="!isDashboard" class="site-header" :class="{ 'menu-open': menuOpen }">
      <div class="header-inner">
        <router-link to="/" class="brand" aria-label="回到首页">
          <span class="brand-logo" aria-hidden="true"></span>
          <span class="brand-name">我的数字中心</span>
        </router-link>

        <nav class="nav" :class="{ open: menuOpen }" aria-label="主导航">
          <router-link
            to="/"
            class="nav-link"
            :class="{ active: route.path === '/' }"
            @click="menuOpen = false"
          >文章</router-link>
          <router-link
            to="/works"
            class="nav-link"
            :class="{ active: route.path.startsWith('/works') }"
            @click="menuOpen = false"
          >作品</router-link>
          <router-link
            to="/lolita"
            class="nav-link"
            :class="{ active: route.path.startsWith('/lolita') }"
            @click="menuOpen = false"
          >穿搭墙</router-link>
          <router-link
            to="/about"
            class="nav-link"
            :class="{ active: route.path.startsWith('/about') }"
            @click="menuOpen = false"
          >关于</router-link>
          
          <router-link to="/dashboard" class="nav-link nav-dash" @click="menuOpen = false">控制台</router-link>
        </nav>

        <div class="header-actions">
          <router-link to="/dashboard" class="dash-link">控制台</router-link>
          <button
            class="menu-toggle"
            :class="{ active: menuOpen }"
            :aria-expanded="menuOpen"
            aria-label="打开导航菜单"
            @click="menuOpen = !menuOpen"
          >
            <span></span><span></span><span></span>
          </button>
        </div>
      </div>
    </header>

    <main class="site-main">
      <router-view v-slot="{ Component }">
        <transition name="page" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>

    <footer v-if="!isDashboard" class="site-footer">
      <p>Lynn's Blog · 记录、分享与成长</p>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
// 控制台与三丽鸥布局页面（自带顶部导航 + 侧栏）隐藏全局 header/footer，避免导航重复
const isDashboard = computed(() => route.path.startsWith('/dashboard') || route.path === '/login' || route.meta.layout === 'sanrio')

// 移动端导航菜单状态
const menuOpen = ref(false)
watch(() => route.path, () => { menuOpen.value = false })
</script>

<style>
.app {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.site-header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: rgba(255, 255, 255, 0.82);
  backdrop-filter: blur(14px);
  -webkit-backdrop-filter: blur(14px);
  border-bottom: 1px solid var(--line-weak);
}

.header-inner {
  max-width: 1100px;
  margin: 0 auto;
  height: 60px;
  padding: 0 24px;
  display: flex;
  align-items: center;
  gap: 24px;
  position: relative;
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 17px;
  font-weight: 700;
  letter-spacing: 0.5px;
  white-space: nowrap;
}

.brand-logo {
  width: 30px;
  height: 30px;
  border-radius: 9px;
  background: linear-gradient(135deg, var(--brand), var(--brand-2));
  box-shadow: 0 4px 10px rgba(99, 102, 241, 0.35);
  position: relative;
}

.brand-logo::after {
  content: "";
  position: absolute;
  left: 9px;
  top: 9px;
  width: 12px;
  height: 12px;
  border-radius: 4px;
  background: rgba(255, 255, 255, 0.92);
}

.nav {
  display: flex;
  gap: 6px;
  flex: 1;
}

.nav-link {
  padding: 7px 14px;
  border-radius: 9px;
  font-size: 14px;
  color: var(--text-sub);
  transition: color 0.2s ease, background-color 0.2s ease;
}

.nav-link:hover {
  color: var(--brand);
  background: var(--el-color-primary-light-9);
}

.nav-link.active {
  color: var(--brand);
  background: var(--el-color-primary-light-9);
  font-weight:500;
}

.nav-dash {
  display: none;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.dash-link {
  padding: 7px 16px;
  border-radius: 9px;
  font-size: 13px;
  color: var(--brand);
  border: 1px solid var(--el-color-primary-light-7);
  transition: all 0.2s ease;
  white-space: nowrap;
}

.dash-link:hover {
  background: var(--el-color-primary-light-9);
}

.menu-toggle {
  display: none;
  width: 38px;
  height: 38px;
  border: 1px solid var(--line-strong);
  border-radius: 10px;
  background: transparent;
  cursor: pointer;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4.5px;
  transition: border-color 0.2s ease, background-color 0.2s ease;
}

.menu-toggle span {
  display: block;
  width: 17px;
  height: 2px;
  border-radius: 2px;
  background: var(--text-main);
  transition: transform 0.24s ease, opacity 0.24s ease;
}

.menu-toggle:hover {
  background: var(--el-color-primary-light-9);
  border-color: var(--el-color-primary-light-7);
}

.menu-toggle.active span:nth-child(1) {
  transform: translateY(6.5px) rotate(45deg);
}

.menu-toggle.active span:nth-child(2) {
  opacity: 0;
}

.menu-toggle.active span:nth-child(3) {
  transform: translateY(-6.5px) rotate(-45deg);
}

.site-main {
  flex: 1;
}

.site-footer {
  padding: 26px 0 30px;
  text-align: center;
  color: var(--text-tertiary);
  font-size: 13px;
}

/* 路由过渡：一次克制的淡入上移 */
.page-enter-active,
.page-leave-active {
  transition: opacity 0.18s ease-out, transform 0.18s ease-out;
}

.page-enter-from {
  opacity: 0;
  transform: translateY(8px);
}

.page-leave-to {
  opacity: 0;
  transform: translateY(-6px);
}

/* ===== 响应式导航 ===== */
@media (max-width: 899px) {
  .menu-toggle {
    display: flex;
  }

  .dash-link {
    display: none;
  }

  .nav {
    position: absolute;
    top: 60px;
    left: 0;
    right: 0;
    flex-direction: column;
    gap: 4px;
    padding: 12px 16px 16px;
    background: rgba(255, 255, 255, 0.96);
    backdrop-filter: blur(14px);
    -webkit-backdrop-filter: blur(14px);
    border-bottom: 1px solid var(--line-weak);
    box-shadow: var(--shadow-md);
    opacity: 0;
    visibility: hidden;
    transform: translateY(-8px);
    transition: opacity 0.2s ease, transform 0.2s ease, visibility 0.2s;
  }

  .nav.open {
    opacity: 1;
    visibility: visible;
    transform: translateY(0);
  }

  .nav-link {
    padding: 12px 14px;
    border-radius: 10px;
    font-size: 15px;
  }

  .nav-dash {
    display: block;
  }
}

@media (max-width: 560px) {
  .header-inner {
    padding: 0 16px;
    gap: 14px;
  }

  .brand-name {
    font-size: 15px;
  }
}
</style>