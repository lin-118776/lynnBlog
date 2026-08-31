<template>
  <el-container class="layout">
    <div v-if="asideOpen" class="aside-mask" @click="asideOpen = false"></div>
    <el-aside width="220px" class="aside" :class="{ open: asideOpen }">
      <router-link to="/" class="logo" aria-label="返回站点首页">
        <span class="logo-mark" aria-hidden="true"></span>
        <span class="logo-name">Lynn's Blog</span>
      </router-link>
      <el-menu :default-active="activeMenu" router class="menu">
        <el-menu-item index="/dashboard">
          <AppIcon name="home" :size="16" />
          <span>控制台概览</span>
        </el-menu-item>
        <el-menu-item index="/dashboard/article">
          <AppIcon name="pen" :size="16" />
          <span>文章管理</span>
        </el-menu-item>
        <el-menu-item index="/dashboard/category">
          <AppIcon name="folder" :size="16" />
          <span>分类管理</span>
        </el-menu-item>
        <el-menu-item index="/dashboard/interview">
          <AppIcon name="message" :size="16" />
          <span>面试库管理</span>
        </el-menu-item>
        <el-menu-item index="/dashboard/diary">
          <AppIcon name="lock" :size="16" />
          <span>私密日记本</span>
        </el-menu-item>
        <el-menu-item index="/dashboard/lolita">
          <AppIcon name="sparkles" :size="16" />
          <span>我的衣橱</span>
        </el-menu-item>
        <el-menu-item index="/dashboard/files">
          <AppIcon name="image" :size="16" />
          <span>我的相册</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <button class="menu-btn" aria-label="切换菜单" @click="asideOpen = !asideOpen">
            <AppIcon name="menu" :size="18" />
          </button>
          <span class="header-title">{{ route.meta.title }}</span>
        </div>
        <div class="header-right">
          <router-link to="/" class="back-site-btn">
            <AppIcon name="home" :size="15" />
            返回站点
          </router-link>
          <span v-if="userInfo?.nickname" class="user-chip">
            <span class="user-avatar">{{ userInfo.nickname.slice(0, 1) }}</span>
            {{ userInfo.nickname }}
          </span>
          <el-button link type="danger" class="logout-btn" @click="logout">
            <AppIcon name="logout" :size="15" />
            退出登录
          </el-button>
        </div>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AppIcon from '../../components/AppIcon.vue'

const route = useRoute()
const router = useRouter()
const activeMenu = computed(() => route.path)
const asideOpen = ref(false)

const userInfo = computed(() => {
  try {
    return JSON.parse(localStorage.getItem('userInfo') || 'null')
  } catch {
    return null
  }
})

// 路由切换后自动收起移动端侧栏
watch(() => route.path, () => { asideOpen.value = false })

// 退出登录：清除本地凭证并跳转登录页
function logout() {
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  router.push('/login')
}
</script>

<style scoped>
.layout {
  min-height: 100vh;
}

.aside {
  background: var(--surface);
  border-right: 1px solid var(--line-weak);
  position: sticky;
  top: 0;
  height: 100vh;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 9px;
  font-weight: 700;
  border-bottom: 1px solid var(--line-weak);
  letter-spacing: 0.3px;
  color: var(--text-main);
  text-decoration: none;
  transition: color 0.2s ease;
}

.logo:hover {
  color: var(--brand);
}

.logo-mark {
  width: 24px;
  height: 24px;
  border-radius: 8px;
  background: linear-gradient(135deg, var(--brand), var(--brand-2));
  box-shadow: 0 4px 10px rgba(99, 102, 241, 0.35);
  position: relative;
  flex-shrink: 0;
}

.logo-mark::after {
  content: "";
  position: absolute;
  left: 7px;
  top: 7px;
  width: 10px;
  height: 10px;
  border-radius: 3px;
  background: rgba(255, 255, 255, 0.92);
}

.menu {
  border-right: none;
  padding: 10px 8px;
}

.menu :deep(.el-menu-item) {
  border-radius: 9px;
  margin-bottom: 4px;
  height: 44px;
  line-height: 44px;
}

.menu :deep(.el-menu-item .app-icon) {
  margin-right: 10px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: var(--surface);
  border-bottom: 1px solid var(--line-weak);
  height: 60px;
  padding: 0 20px;
  position: sticky;
  top: 0;
  z-index: 90;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.menu-btn {
  display: none;
  width: 36px;
  height: 36px;
  border: 1px solid var(--line-strong);
  border-radius: 9px;
  background: transparent;
  color: var(--text-main);
  cursor: pointer;
  align-items: center;
  justify-content: center;
  transition: background-color 0.2s ease, border-color 0.2s ease;
}

.menu-btn:hover {
  background: var(--el-color-primary-light-9);
  border-color: var(--el-color-primary-light-7);
}

.header-title {
  font-weight: 600;
  font-size: 15px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.back-site-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: var(--text-sub);
  transition: color 0.2s ease;
}

.back-site-btn:hover {
  color: var(--brand);
}

.user-chip {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: var(--text-sub);
}

.user-avatar {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 26px;
  height: 26px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--brand), var(--brand-2));
  color: #fff;
  font-size: 12px;
  font-weight: 600;
}

.logout-btn {
  gap: 4px;
  display: inline-flex;
  align-items: center;
}

.main {
  padding: 20px;
  background: transparent;
}

.aside-mask {
  display: none;
}

/* ===== 移动端：抽屉式侧栏 ===== */
@media (max-width: 899px) {
  .aside {
    position: fixed;
    left: 0;
    top: 0;
    z-index: 120;
    transform: translateX(-100%);
    transition: transform 0.24s ease-out;
    box-shadow: var(--shadow-lg);
  }

  .aside.open {
    transform: translateX(0);
  }

  .aside-mask {
    display: block;
    position: fixed;
    inset: 0;
    z-index: 110;
    background: rgba(31, 36, 48, 0.35);
    backdrop-filter: blur(2px);
  }

  .menu-btn {
    display: flex;
  }
}

@media (max-width: 560px) {
  .main {
    padding: 14px;
  }

  .user-chip {
    display: none;
  }
}
</style>