<template>
  <div class="overview">
    <el-card shadow="never" class="profile-card">
      <div class="profile">
        <span class="profile-avatar">{{ (userInfo?.nickname || '我').slice(0, 1) }}</span>
        <div class="profile-info">
          <p class="profile-name">{{ userInfo?.nickname || '未登录' }}</p>
          <p class="profile-sub">@{{ userInfo?.username || '—' }}</p>
        </div>
        <el-tag size="small" effect="light" round>Lynn's Blog</el-tag>
      </div>
    </el-card>

    <h2 class="section-title">功能入口</h2>
    <el-card shadow="never" class="modules-card">
      <div class="module-list">
        <button v-for="m in modules" :key="m.path" class="module" @click="$router.push(m.path)">
          <span class="module-icon"><AppIcon :name="m.icon" :size="18" /></span>
          <span class="module-body">
            <span class="module-name">{{ m.name }}</span>
            <span class="module-desc">{{ m.desc }}</span>
          </span>
          <AppIcon name="arrow-right" :size="16" class="module-arrow" />
        </button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import AppIcon from '../../components/AppIcon.vue'

// 从 localStorage 读取登录时保存的用户信息
const userInfo = computed(() => {
  try {
    return JSON.parse(localStorage.getItem('userInfo') || 'null')
  } catch {
    return null
  }
})

const modules = [
  { path: '/dashboard/article', icon: 'pen', name: '文章管理', desc: '撰写与管理技术文章' },
  { path: '/dashboard/diary', icon: 'lock', name: '私密日记本', desc: '记录只属于你的内容' },
  { path: '/dashboard/lolita', icon: 'sparkles', name: '我的衣橱', desc: '管理穿搭与兴趣收藏' },
  { path: '/dashboard/friends', icon: 'grid', name: '友链管理', desc: '维护交换友链的好朋友们' },
  { path: '/dashboard/projects', icon: 'briefcase', name: '作品管理', desc: '展示你的项目与 GitHub 链接' },
  { path: '/dashboard/files', icon: 'image', name: '我的相册', desc: '上传与管理素材文件' }
]
</script>

<style scoped>
.overview {
  display: flex;
  flex-direction: column;
  gap: 20px;
  max-width: 760px;
}

.profile-card {
  padding: 6px 8px;
}

.profile {
  display: flex;
  align-items: center;
  gap: 16px;
}

.profile-avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 52px;
  height: 52px;
  border-radius: 16px;
  background: linear-gradient(135deg, var(--brand), var(--brand-2));
  color: #fff;
  font-size: 20px;
  font-weight: 700;
  box-shadow: 0 6px 16px rgba(99, 102, 241, 0.3);
  flex-shrink: 0;
}

.profile-info {
  flex: 1;
  min-width: 0;
}

.profile-name {
  margin: 0 0 3px;
  font-size: 17px;
  font-weight: 700;
}

.profile-sub {
  margin: 0;
  font-size: 13px;
  color: var(--text-sub);
}

.section-title {
  margin: 4px 4px -8px;
  font-size: 16px;
}

.modules-card {
  padding: 4px;
}

.module-list {
  display: flex;
  flex-direction: column;
}

.module {
  display: flex;
  align-items: center;
  gap: 14px;
  width: 100%;
  padding: 13px 10px;
  border: none;
  border-radius: 12px;
  background: transparent;
  cursor: pointer;
  text-align: left;
  font: inherit;
  transition: background-color 0.2s ease;
}

.module:hover,
.module:focus-visible {
  background: var(--el-color-primary-light-9);
  outline: none;
}

.module-icon {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 12px;
  color: var(--brand);
  background: var(--el-color-primary-light-9);
  border: 1px solid var(--el-color-primary-light-7);
}

.module-body {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.module-name {
  font-size: 14.5px;
  font-weight:500;
  color: var(--text-main);
}

.module-desc {
  font-size: 12.5px;
  color: var(--text-sub);
}

.module-arrow {
  color: var(--text-tertiary);
  transition: transform 0.2s ease, color 0.2s ease;
}

.module:hover .module-arrow {
  transform: translateX(3px);
  color: var(--brand);
}
</style>