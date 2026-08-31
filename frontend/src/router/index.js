import { createRouter, createWebHistory } from 'vue-router'
import { useAuth } from '../composables/useAuth'

// 路由规划：
//  公开页面全部挂在 SanrioLayout（三丽鸥风公共布局：顶部导航跳转 + 左侧资料栏 + 右侧内容区）下，
//  每个导航项都是独立的页面路由（参考 xnmoe.com 的页面拆分方式）。
//  控制台（/dashboard）与登录页独立。
const routes = [
  {
    path: '/',
    component: () => import('../views/layout/SanrioLayout.vue'),
    meta: { layout: 'sanrio', title: '首页' },
    children: [
      { path: '', name: 'Home', component: () => import('../views/Home.vue'), meta: { title: 'Home' } },
      { path: 'blog', name: 'Blog', component: () => import('../views/Blog.vue'), meta: { title: 'Blog' } },
      { path: 'interview', name: 'Interview', component: () => import('../views/Interview.vue'), meta: { title: 'Interview' } },
      { path: 'lolita', name: 'Lolita', component: () => import('../views/Lolita.vue'), meta: { title: 'Lolita' } },
      { path: 'friends', name: 'Friends', component: () => import('../views/Friends.vue'), meta: { title: 'Friends' } },
      { path: 'guestbook', name: 'Guestbook', component: () => import('../views/Guestbook.vue'), meta: { title: 'Guestbook' } },
      { path: 'about', name: 'About', component: () => import('../views/About.vue'), meta: { title: 'About' } },
      { path: 'article/:id', name: 'ArticleDetail', component: () => import('../views/ArticleDetail.vue'), meta: { title: '文章详情' } }
    ]
  },
  { path: '/login', name: 'Login', component: () => import('../views/Login.vue'), meta: { title: '登录' } },
  {
    path: '/dashboard',
    component: () => import('../views/dashboard/DashboardLayout.vue'),
    meta: { requiresAuth: true, title: '控制台' },
    children: [
      { path: '', name: 'Dashboard', component: () => import('../views/dashboard/Dashboard.vue'), meta: { title: '控制台概览' } },
      { path: 'article', name: 'DashboardArticle', component: () => import('../views/dashboard/ArticleManage.vue'), meta: { title: '文章管理' } },
      { path: 'category', name: 'DashboardCategory', component: () => import('../views/dashboard/CategoryManage.vue'), meta: { title: '分类管理' } },
      { path: 'interview', name: 'DashboardInterview', component: () => import('../views/dashboard/InterviewManage.vue'), meta: { title: '面试库管理' } },
      { path: 'diary', name: 'DashboardDiary', component: () => import('../views/dashboard/DiaryManage.vue'), meta: { title: '私密日记本' } },
      { path: 'lolita', name: 'DashboardLolita', component: () => import('../views/dashboard/LolitaManage.vue'), meta: { title: '我的衣橱' } },
      { path: 'files', name: 'DashboardFiles', component: () => import('../views/dashboard/FileManage.vue'), meta: { title: '我的相册' } }
    ]
  },
  { path: '/:pathMatch(.*)*', redirect: '/' }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局前置守卫：设置标题 + 校验登录
router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - Lynn's Blog` : "Lynn's Blog"
  const { isLoggedIn } = useAuth()
  if (to.meta.requiresAuth && !isLoggedIn.value) {
    // 未登录访问控制台：跳转登录并记录来源
    next({ path: '/login', query: { redirect: to.fullPath } })
    return
  }
  next()
})

export default router
