<template>
  <div class="sanrio-page" :class="{ 'mobile-open': menuOpen }">
    <a class="skip-link" href="#home-main">跳到主要内容</a>

    <!-- 顶部导航：全部为独立页面跳转（参考 xnmoe.com 的导航方式） -->
    <nav class="nav" aria-label="主导航">
      <div class="nav-inner">
        <router-link class="brand" to="/" aria-label="回到首页">
          <span class="brand-mark" aria-hidden="true">
            <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 3c3 3.2 3.4 6.4 1.5 9-1.4 1.9-3.8 2-5.2.4-1.4-1.7-.4-4.6 2-6.4"/><path d="M7.5 16.5c2.4 1.8 4.4 1.5 5.8 0"/><path d="M9 20h6"/></svg>
          </span>
          Lynn's Blog
        </router-link>
        <div class="nav-tabs" role="list">
          <router-link
            v-for="item in navItems"
            :key="item.to"
            class="nav-tab"
            :class="{ active: isActive(item.key) }"
            :to="item.to"
            @click="menuOpen = false"
          ><span class="tab-ico" v-html="item.icon"></span>{{ item.label }}</router-link>
          <router-link
            v-if="isLoggedIn"
            to="/dashboard"
            class="nav-tab nav-console"
            @click="menuOpen = false"
          ><span class="tab-ico"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="16" rx="3"/><path d="M3 9h18M9 17h6"/></svg></span>控制台</router-link>
          <router-link
            v-else
            to="/login"
            class="nav-tab nav-console"
            @click="menuOpen = false"
          ><span class="tab-ico"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 3v3M12 18v3M3 12h3M18 12h3M6 6l2 2M16 16l2 2M18 6l-2 2M8 16l-2 2"/><circle cx="12" cy="12" r="3.2"/></svg></span>登录</router-link>
        </div>
      </div>
    </nav>

    <button class="mobile-toggle" type="button" :aria-expanded="menuOpen" aria-controls="profile-rail" @click="menuOpen = !menuOpen">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><path d="M4 7h16M4 12h16M4 17h16"/></svg>
      菜单
    </button>
    <div class="sidebar-backdrop" aria-hidden="true" @click="menuOpen = false"></div>

    <main class="page" id="home-main">
      <div class="layout">
        <!-- 左侧个人资料栏：所有页面共用 -->
        <aside class="profile-rail" id="profile-rail" aria-label="个人资料" @click="menuOpen = false">
          <h1 class="profile-title">Lynn's Blog ©<small>SWEET &amp; TINY DIARY</small></h1>

          <section class="profile-card">
            <div class="profile-top">
              <!-- 头像：替换成 <img src="assets/images/pfp.png" alt="头像"> -->
              <div class="avatar"><ImgSlot variant="avatar" label="头像<br />120 × 120" /></div>
              <div><span class="online">ONLINE!</span><p class="profile-bio">幸运一点点すこしのこううん♥</p></div>
            </div>
            <!-- 心情背景图：想用图时给 .profile-note 加 background-image -->
            <div class="profile-note">这里是lynn的Blog，最近在收集昭和风贴纸、蝴蝶结发饰，又新买了一件美乐蒂lolitao(*￣▽￣*)ブ。</div>
          </section>

          <section class="contact-card" @mouseleave="hideTip">
            <h2 class="card-heading"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="9"/><path d="M3.5 9h17M3.5 15h17M12 3a15 15 0 0 1 0 18M12 3a15 15 0 0 0 0 18"/></svg>Connect</h2>
            <!-- 悬停浮窗：跟随图标显示对应联系方式；双击进入编辑，回车保存 -->
            <div
              v-if="tooltip.visible"
              class="contact-tip"
              :style="tipStyle"
              @dblclick="startEdit"
            >
              <template v-if="!editing">
                <span class="tip-label">{{ tooltip.label }}</span>
                <span class="tip-value">{{ contacts[tooltip.key] }}</span>
                <span class="tip-hint">双击编辑</span>
              </template>
              <template v-else>
                <input
                  ref="tipInput"
                  v-model="editValue"
                  class="tip-input"
                  placeholder="输入联系方式后回车"
                  @keydown.enter="saveEdit"
                  @blur="cancelEdit"
                />
              </template>
            </div>
            <div class="contact-grid">
              <!-- 图标可替换成自己的图片或 SVG -->
              <a class="contact-link" href="#" aria-label="Bilibili" @mouseenter="showTip('bilibili', 'Bilibili', $event)"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M6.5 5.5 8.5 3.5M17.5 5.5 15.5 3.5"/><rect x="3" y="7" width="18" height="13" rx="3.5"/><path d="M7 11h.01M17 11h.01"/><path d="M9.5 15.5c1.3 1 3.7 1 5 0"/><path d="M3 9.5v-1M21 9.5v-1"/></svg><span>Bili</span></a>
              <a class="contact-link" href="#" aria-label="GitHub" @mouseenter="showTip('github', 'GitHub', $event)"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M15 22v-3.5c0-1 .1-1.6-.6-2.1 2.2-.2 4.6-1.1 4.6-4.7a3.7 3.7 0 0 0-1-2.5 3.4 3.4 0 0 0-.1-2.6s-.8-.3-2.7 1a9.3 9.3 0 0 0-5 0C8.4 6.4 7.6 6.6 7.6 6.6a3.4 3.4 0 0 0-.1 2.6 3.7 3.7 0 0 0-1 2.5c0 3.6 2.4 4.5 4.6 4.7-.4.3-.6.7-.6 1.3V22"/><path d="M9 19c-2.4.5-4-1-5-2"/></svg><span>GitHub</span></a>
              <a class="contact-link" href="#" aria-label="TikTok" @mouseenter="showTip('tiktok', 'TikTok', $event)"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M9 18V6a2 2 0 0 1 2-2h4"/><path d="M9 12c0-1 4-1.5 6-3"/><circle cx="7.5" cy="18" r="2.5"/><circle cx="15.5" cy="15.5" r="2.5"/></svg><span>TikTok</span></a>
              <a class="contact-link" href="#" aria-label="小红书" @mouseenter="showTip('red', 'Red', $event)"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M12 20.6S3 14.9 3 9.2C3 6 5.5 4 7.7 4c2 0 3.5 1.4 4.3 2.5C12.8 5.4 14.3 4 16.3 4 18.5 4 21 6 21 9.2c0 5.7-9 11.4-9 11.4Z"/></svg><span>Red</span></a>
              <a class="contact-link" href="#" aria-label="QQ" @mouseenter="showTip('qq', 'QQ', $event)"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M12 3c-3 1.6-5 4.2-5 7.2v2.6c0 2 2 3 2 3s-1 2.2-2 3.2c2 .9 4.2 1 5 .3 1 .8 3 .8 5-.3-1-1-2-3.2-2-3.2s2-1 2-3v-2.6c0-3-2-5.6-5-7.2Z"/><path d="M9.2 10.5h.01M14.8 10.5h.01"/><path d="M10.5 13.8c.9.8 2.1.8 3 0"/></svg><span>QQ</span></a>
              <a class="contact-link" href="#" aria-label="邮箱" @mouseenter="showTip('mail', 'Mail', $event)"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="5" width="18" height="14" rx="3"/><path d="m3.5 7.5 8.5 6 8.5-6"/></svg><span>Mail</span></a>
            </div>
          </section>

          <footer class="rail-footer">
            <!-- 小图：替换成 <img src="assets/images/footer.png" alt="装饰图"> -->
            <ImgSlot variant="small" label="小图" />
            <p>本站图片素材均来源于网络，如有问题请联系我删除。</p>
          </footer>
        </aside>

        <!-- 右侧内容区：各路由页面渲染在这里 -->
        <div class="content-col">
          <router-view />
        </div>
      </div>
    </main>

    <div class="deco deco-bow" aria-hidden="true"><svg viewBox="0 0 48 48" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linejoin="round" stroke-linecap="round"><path d="M8 16c-2-6 2-9 6-6 1-4 5-5 7-2 2-3 6-2 7 2 4-3 8 0 6 6-1 4-4 6-7 8-2 2-4 2-6 0-3-2-6-4-7-8Z"/><path d="M18 26c3 4 9 4 12 0"/></svg></div>
    <div class="deco deco-heart" aria-hidden="true"><svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 20.6S3 14.9 3 9.2C3 6 5.5 4 7.7 4c2 0 3.5 1.4 4.3 2.5C12.8 5.4 14.3 4 16.3 4 18.5 4 21 6 21 9.2c0 5.7-9 11.4-9 11.4Z"/></svg></div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import ImgSlot from '../../components/ImgSlot.vue'
import { listContacts, updateContact } from '../../api/contact'
import { useAuth } from '../../composables/useAuth'

const route = useRoute()
const router = useRouter()
const menuOpen = ref(false)

// 导航项：全部跳转独立页面（参考 xnmoe.com）
const navItems = [
  { to: '/', key: '', label: 'Home', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M4 11 12 4l8 7"/><path d="M6 10v9h12v-9"/></svg>' },
  { to: '/blog', key: 'blog', label: 'Blog', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M5 5h5a3 3 0 0 1 3 3v11H7a2 2 0 0 1-2-2V5Z"/><path d="M10 8h6a3 3 0 0 1 3 3v7"/></svg>' },
  { to: '/interview', key: 'interview', label: 'Interview', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15a4 4 0 0 1-4 4H8l-5 3V7a4 4 0 0 1 4-4h10a4 4 0 0 1 4 4Z"/></svg>' },
  { to: '/lolita', key: 'lolita', label: 'Lolita', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 3c3 3.2 3.4 6.4 1.5 9-1.4 1.9-3.8 2-5.2.4-1.4-1.7-.4-4.6 2-6.4"/><path d="M7.5 16.5c2.4 1.8 4.4 1.5 5.8 0"/></svg>' },
  { to: '/friends', key: 'friends', label: 'Friends', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9.5" cy="7" r="4"/><path d="M22 21v-2a4 4 0 0 0-3-3.9"/><path d="M15 3.1a4 4 0 0 1 0 7.8"/></svg>' },
  { to: '/guestbook', key: 'guestbook', label: 'Guestbook', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 3a2.8 2.8 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5L17 3Z"/></svg>' },
  { to: '/about', key: 'about', label: 'About', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="9"/><path d="M12 11v6"/><path d="M12 7h.01"/></svg>' }
]

function isActive(key) {
  return (route.path.split('/')[1] || '') === key
}

// ===== Connect 联系方式浮窗：悬停显示、双击编辑、回车保存（后端接口持久化） =====
const defaultContacts = {
  bilibili: 'space.bilibili.com/待补充',
  github: 'github.com/待补充',
  tiktok: '@待补充',
  red: 'xiaohongshu.com/user/待补充',
  qq: 'QQ：待补充',
  mail: '待补充@example.com'
}

const { isLoggedIn } = useAuth()
const contacts = ref({ ...defaultContacts })
const tooltip = ref({ visible: false, key: '', label: '' })
const tipStyle = ref({})
const editing = ref(false)
const editValue = ref('')
const tipInput = ref(null)

// 进入页面时从后端加载联系方式（公开接口，失败则用默认占位保证页面可用）
onMounted(async () => {
  try {
    const { data } = await listContacts()
    if (Array.isArray(data)) {
      const map = {}
      data.forEach((item) => { map[item.contactKey] = item.contactValue })
      contacts.value = { ...defaultContacts, ...map }
    }
  } catch (e) {
    // 接口异常时保持默认占位
  }
})

function showTip(key, label, ev) {
  const rect = ev.currentTarget.getBoundingClientRect()
  tipStyle.value = {
    left: `${rect.left + rect.width / 2}px`,
    top: `${rect.top - 30}px`
  }
  tooltip.value = { visible: true, key, label }
  editing.value = false
}

function hideTip() {
  tooltip.value.visible = false
  editing.value = false
}

async function startEdit() {
  if (!tooltip.value.visible) return
  editing.value = true
  editValue.value = contacts.value[tooltip.value.key] || ''
  await nextTick()
  tipInput.value?.focus()
}

async function saveEdit() {
  if (!editing.value) return
  const key = tooltip.value.key
  const val = editValue.value.trim()
  editing.value = false
  if (!val) return
  // 保存需登录：未登录时提示并跳转登录页
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录后再编辑联系方式')
    router.push({ path: '/login', query: { redirect: route.fullPath } })
    return
  }
  try {
    await updateContact(key, val)
    contacts.value[key] = val
    ElMessage.success('联系方式已保存')
  } catch (e) {
    // 失败提示已由请求封装统一处理，本地保持旧值
  }
}

function cancelEdit() {
  editing.value = false
}
</script>

<style scoped>
.sanrio-page{--pink-50:#fff7fa;--pink-100:#ffe7f0;--pink-200:#ffd0e1;--pink-300:#ffb7d0;--pink-400:#f9a8c4;--rose:#e45b8d;--berry:#a63b64;--ink:#4a2b3a;--muted:#8a6475;--cream:#fffaf5;--mint:#cdeede;--sky:#d9edfb;--lemon:#fff1b8;--line:#efc2d3;--shadow:0 12px 30px rgba(183,85,129,.12);--radius-lg:22px;--radius-md:16px;--rail-w:284px;--nav-h:66px;min-height:100vh;color:var(--ink);font-family:ui-rounded,"Hiragino Maru Gothic ProN","Yu Gothic","Microsoft YaHei","PingFang SC",sans-serif;line-height:1.6;background-color:var(--pink-100);background-image:repeating-linear-gradient(0deg,rgba(255,255,255,.36) 0 2px,transparent 2px 18px),repeating-linear-gradient(90deg,rgba(255,255,255,.36) 0 2px,transparent 2px 18px),linear-gradient(180deg,#ffe3ee 0%,#fff4f8 42%,#f9f0f5 100%);background-attachment:fixed}
.sanrio-page *{box-sizing:border-box}
.sanrio-page a{color:var(--rose);text-decoration:none;transition:color .2s ease,background-color .2s ease,transform .2s ease}
.sanrio-page a:hover{color:var(--berry)}
.sanrio-page button,.sanrio-page a{-webkit-tap-highlight-color:transparent}
.sanrio-page :focus-visible{outline:3px solid var(--rose);outline-offset:3px;border-radius:6px}
.sanrio-page .sr-only{position:absolute;width:1px;height:1px;padding:0;margin:-1px;overflow:hidden;clip:rect(0,0,0,0);white-space:nowrap;border:0}
.skip-link{position:fixed;top:12px;left:12px;z-index:2000;padding:8px 14px;border-radius:10px;background:var(--cream);color:var(--berry);box-shadow:var(--shadow);transform:translateY(-150%)}
.skip-link:focus{transform:translateY(0)}
.nav{position:fixed;inset:0 0 auto;z-index:1000;min-height:var(--nav-h);padding:10px 16px;display:flex;align-items:center;justify-content:center;background:rgba(255,250,245,.9);border-bottom:1px solid var(--line);box-shadow:0 6px 18px rgba(183,85,129,.08);backdrop-filter:blur(12px)}
.nav-inner{display:flex;align-items:center;gap:6px;max-width:1240px;width:100%}
.brand{display:inline-flex;align-items:center;gap:8px;margin-right:14px;font-weight:800;letter-spacing:.3px;white-space:nowrap;color:var(--berry)}
.brand-mark{display:grid;place-items:center;width:34px;height:34px;border-radius:12px;background:linear-gradient(135deg,var(--pink-300),var(--rose));color:#fff;box-shadow:0 5px 12px rgba(227,91,141,.24)}
.nav-tabs{flex:1;display:flex;align-items:flex-end;gap:4px;min-width:0;overflow-x:auto;scrollbar-width:none}
.nav-tabs::-webkit-scrollbar{display:none}
.nav-tab{flex:0 0 auto;display:inline-flex;align-items:center;gap:6px;padding:9px 13px 8px;border-radius:14px 14px 5px 5px;color:var(--muted);font-size:13px;font-weight:700;white-space:nowrap;border:1px solid transparent}
.nav-tab:hover{color:var(--berry);background:var(--pink-100)}
.nav-tab.active{color:var(--berry);background:linear-gradient(180deg,var(--pink-200),var(--pink-100));border-color:var(--line);box-shadow:0 4px 12px rgba(227,91,141,.12)}
.nav-tab .tab-ico{display:inline-flex}
.nav-tab svg{width:15px;height:15px;flex:0 0 auto}
.nav-console{margin-left:auto;color:var(--rose,#e45b8d);background:var(--pink-50,#fff7fa);border:1px solid var(--pink-200,#ffd0e1);border-radius:999px;box-shadow:none}
.nav-console:hover{color:var(--berry,#a63b64);background:var(--pink-100,#ffe7f0);border-color:var(--pink-300,#ffb7d0);transform:translateY(-1px)}
.nav-console.active{color:var(--berry,#a63b64);background:var(--pink-100,#ffe7f0);border-color:var(--pink-300,#ffb7d0);box-shadow:none}
.page{max-width:1380px;margin:0 auto;padding:calc(var(--nav-h) + 18px) 16px 40px}
.layout{display:grid;grid-template-columns:var(--rail-w) minmax(0,1fr);gap:18px;align-items:start}
.profile-rail{position:sticky;top:calc(var(--nav-h) + 18px);display:flex;flex-direction:column;gap:14px;min-width:0}
.profile-title{position:relative;margin:0;padding:16px 18px 14px;border:1px solid var(--line);border-radius:var(--radius-lg) var(--radius-lg) 4px 4px;background:linear-gradient(135deg,var(--pink-300),#ffd4e3);color:#fff;text-shadow:0 1px 0 rgba(166,59,100,.2);font-size:20px;letter-spacing:.6px}
.profile-title small{display:block;margin-top:3px;color:var(--berry);font-size:11px;font-weight:700;letter-spacing:1px;text-shadow:none}
.profile-card,.contact-card,.rail-footer{border:1px solid var(--line);border-radius:var(--radius-lg);background:var(--cream);box-shadow:var(--shadow)}
.profile-card{overflow:hidden;padding:16px}
.profile-top{display:grid;grid-template-columns:92px 1fr;gap:12px;align-items:center}
.avatar{position:relative;border-radius:20px;border:3px solid #fff;box-shadow:0 5px 14px rgba(183,85,129,.18)}
.online{display:inline-flex;align-items:center;gap:5px;margin-bottom:4px;color:var(--berry);font-size:12px;font-weight:800}
.online:before{content:"";width:8px;height:8px;border-radius:50%;background:#59c48e;box-shadow:0 0 0 4px rgba(89,196,142,.14)}
.profile-bio{margin:0;font-size:12px;line-height:1.55;color:var(--muted)}
.profile-note{margin-top:14px;padding:12px 13px;border:1px solid var(--pink-200);border-radius:14px;background:linear-gradient(180deg,#fff8fb,#fff);color:var(--ink);font-size:12px;line-height:1.7}
.contact-grid{display:grid;grid-template-columns:repeat(3,1fr);gap:8px;padding:12px}
.contact-tip{position:fixed;z-index:60;display:flex;align-items:center;gap:8px;max-width:230px;padding:6px 12px;border:1px solid var(--pink-300,#ffb7d0);border-radius:12px;background:#fff;box-shadow:0 10px 22px rgba(183,85,129,.18);transform:translateX(-50%);font-size:11px;cursor:text;pointer-events:auto}
.contact-tip:before{content:"";position:absolute;left:50%;top:100%;transform:translateX(-50%);border:6px solid transparent;border-top-color:var(--pink-300,#ffb7d0)}
.tip-label{color:var(--berry,#a63b64);font-weight:800;white-space:nowrap}
.tip-value{color:var(--ink,#4a2b3a);font-weight:600;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}
.tip-hint{margin-left:auto;color:var(--muted,#8a6475);font-size:9px;opacity:.75;white-space:nowrap}
.tip-input{flex:1;min-width:120px;padding:4px 9px;border:1px solid var(--pink-300,#ffb7d0);border-radius:9px;background:#fff;color:var(--ink,#4a2b3a);font-size:11px;outline:none;font-family:inherit}
.tip-input:focus{border-color:var(--rose,#e45b8d);box-shadow:0 0 0 3px rgba(228,91,141,.12)}
.contact-link{display:grid;place-items:center;min-height:56px;border:1px solid var(--pink-200);border-radius:14px;background:#fff;color:var(--muted);font-size:11px;font-weight:800;text-align:center}
.contact-link:hover{transform:translateY(-2px);color:var(--berry);border-color:var(--pink-300);box-shadow:0 6px 14px rgba(183,85,129,.12)}
.contact-link span{display:block;margin-top:4px;font-size:11px}
.contact-link svg{width:21px;height:21px}
.rail-footer{display:grid;grid-template-columns:86px 1fr;gap:10px;align-items:center;padding:12px;font-size:10px;color:var(--muted)}
.rail-footer p{margin:0;line-height:1.6}
.contact-card h2{display:flex;align-items:center;gap:7px;margin:0;padding:13px 15px;border-bottom:1px solid var(--pink-200);background:linear-gradient(180deg,#fff1f7,#ffe7f0);border-radius:var(--radius-lg) var(--radius-lg) 0 0;color:var(--berry);font-size:13px;letter-spacing:.5px}
.contact-card h2 svg{width:16px;height:16px}
.content-col{display:flex;flex-direction:column;gap:16px;min-width:0}
.deco{position:fixed;pointer-events:none;z-index:500;opacity:.9}
.deco-bow{right:18px;top:88px;width:44px;height:44px;color:var(--pink-400)}
.deco-heart{right:38px;bottom:28px;width:24px;height:24px;color:var(--rose);opacity:.45}
.mobile-toggle,.sidebar-backdrop{display:none}
@media (max-width:980px){
  .layout{grid-template-columns:1fr}
  .profile-rail{position:fixed;top:0;left:0;bottom:0;z-index:1400;width:min(86vw,320px);padding:84px 14px 20px;overflow-y:auto;background:var(--pink-50);border-right:1px solid var(--line);box-shadow:20px 0 40px rgba(74,43,58,.16);transform:translateX(-105%);transition:transform .22s ease-out}
  .sanrio-page.mobile-open .profile-rail{transform:translateX(0)}
  .mobile-toggle{position:fixed;top:12px;left:12px;z-index:1200;display:inline-flex;align-items:center;gap:6px;min-height:40px;padding:8px 12px;border:1px solid var(--line);border-radius:12px;background:rgba(255,250,245,.96);color:var(--berry);cursor:pointer;font-weight:800;box-shadow:var(--shadow)}
  .mobile-toggle svg{width:18px;height:18px}
  .sidebar-backdrop{position:fixed;inset:0;z-index:1300;display:block;background:rgba(74,43,58,.32);opacity:0;pointer-events:none;transition:opacity .2s ease}
  .sanrio-page.mobile-open .sidebar-backdrop{opacity:1;pointer-events:auto}
  .brand{display:none}
  .nav{justify-content:flex-start;padding-left:62px}
}
@media (max-width:680px){
  .sanrio-page{--nav-h:60px}
  .page{padding-inline:10px}
  .profile-title{font-size:18px}
}
@media (prefers-reduced-motion:reduce){
  .sanrio-page{scroll-behavior:auto}
  .sanrio-page *,.sanrio-page *:before,.sanrio-page *:after{animation-duration:.001ms!important;animation-iteration-count:1!important;transition-duration:.001ms!important}
}
</style>
