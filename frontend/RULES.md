# Lynn's Blog 前端组件化编码约束

> 本文件是本项目前端代码的强制性约束。任何人（包括 AI 助手）在本项目 `frontend/` 下写代码前，必须先阅读并遵守本文件。
> 目标：组件化、可复用、单一职责。**能复用的代码必须封装成组件或组合式函数，禁止复制粘贴。**

---

## 0. 一句话总则

> **同样的结构出现第 2 次，就必须抽成组件；同样的逻辑出现第 2 次，就必须抽成 composable。**

---

## 1. 目录职责（分层）

```
src/
├── api/           # 按业务域拆分的接口层（auth.js、article.js…），组件禁止直接 import axios
├── components/    # 跨页面复用的通用组件（哑组件优先）
│   ├── AppIcon.vue        # 已有：统一 SVG 图标
│   └── EmptyState.vue     # 已有：空态占位
├── composables/   # 可复用逻辑（useXxx.js）：分页、加载态、登录态…
├── views/         # 页面组件：只做"布局 + 组合"，禁止再堆可复用 UI
│   └── dashboard/ # 控制台页面
├── router/        # 路由
├── utils/         # 纯函数工具（request.js 等），不依赖组件
└── styles/        # （可选）跨组件公共变量
```

判断标准：
- **components/**：不关心数据从哪来，只管"给我 props 我就渲染"。
- **views/**：负责取数（调 api）、组装 components、处理跳转。**views 里的模板超过 ~150 行且含重复块，视为需要拆分的信号。**

---

## 2. 组件命名

- 文件名与组件名一致，**PascalCase**，名词性：`ArticleCard.vue`、`PageHead.vue`。
- 语义前缀（参考 Element Plus 习惯）：
  - `Base` 前缀：最底层原子（`BaseTag.vue`、`BaseDialog.vue`）
  - 业务名前缀：`ArticleCard`、`DiaryCard`、`ChatItem`
- composable 一律 `use` 开头驼峰：`usePagedList.js`、`useAuth.js`。

---

## 3. 组件设计规范

### 3.1 通信：单向数据流

```vue
<script setup>
// props 只进不改；事件用 emits 通知父级
const props = defineProps({
  article: { type: Object, required: true },
  loading: { type: Boolean, default: false }
})
const emit = defineEmits(['click', 'delete'])
</script>
```

- **禁止**在子组件内直接修改 props；要改就 `emit` 出去。
- **禁止**跨层用 `provide/inject` 传业务数据（仅允许主题/配置类全局上下文）。
- 父组件不允许伸手进子组件内部状态（没有 `ref` 深挖）。

### 3.2 哑组件优先

- 通用组件**不调接口**、不碰 `localStorage`、不 import `api/`。数据由 views 准备好喂进来。
- 状态提升：两个组件要共享的状态，放最近公共父级。

### 3.3 内容分发

- 有"插槽感"的结构（卡片头、列表项尾部按钮）用具名插槽：
```vue
<CardPanel title="Updates">
  <template #action>…</template>
  <template #default>…</template>
</CardPanel>
```

### 3.4 样式

- 一律 `<style scoped>`；公共颜色/圆角/阴影用 CSS 变量（已有 `--pink-*`、`--radius-*` 体系，不得写魔法色值）。
- 父组件**不写**子组件内部样式；确需穿透时用 `:deep()` 并写明原因注释。

---

## 4. 逻辑复用：composables

凡是"请求 + 分页 + loading + 错误提示"这类逻辑，抽成 `src/composables/usePagedList.js`：

```js
// composables/usePagedList.js
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

/**
 * 通用分页列表逻辑
 * @param {(page:number,size:number)=>Promise<{records:Array,total:number}>} fetcher
 */
export function usePagedList(fetcher, defaultSize = 10) {
  const list = ref([])
  const total = ref(0)
  const page = ref(1)
  const size = ref(defaultSize)
  const loading = ref(false)

  async function load() {
    loading.value = true
    try {
      const { records, total: t } = await fetcher(page.value, size.value)
      list.value = records
      total.value = t
    } catch (e) {
      ElMessage.error(e.message || '加载失败')
    } finally {
      loading.value = false
    }
  }
  return { list, total, page, size, loading, load }
}
```

所有控制台的 `*Manage.vue` 必须复用它，**不许每个页面各写一份分页**。

---

## 5. API 层约束

- 每个业务域一个文件：`api/article.js`、`api/interview.js`、`api/file.js`。
- 组件里**禁止**出现 `request.post('/api/...')` 原始调用，只能 `import { listArticle } from '@/api/article'`。
- 接口函数返回解包后的 `Result.data`，错误统一在 `utils/request.js` 拦截器处理。

---

## 6. 本项目当前待抽离清单（已知欠账）

按优先级执行，新功能开发前先清这些：

| 现状 | 抽离为 | 说明 |
| --- | --- | --- |
| `Home.vue` 内 `img-slot` 占位结构重复 10+ 次 | `components/ImgSlot.vue`（props: shape、label） | 含虚线框/斜纹背景样式 |
| `Home.vue` 与各页面重复的 `card-heading` 卡片头 | `components/CardPanel.vue`（props: title/icon，具名插槽） | 图标复用 AppIcon |
| `Home.vue` 的 `update-item` 文章行 | `components/ArticleRow.vue`（props: article） | 接真实数据后使用 |
| `Home.vue` 的 `chat-item` 留言行 | `components/ChatItem.vue` | |
| 各 `views/*` 重复的 `page-container / page-head` 头部 | `components/PageHead.vue` | About.vue 已在用类似结构 |
| 5 个 `dashboard/*Manage.vue` 的 `el-card + EmptyState` 外壳 | `components/ManageShell.vue`（props: title、desc） | 现在每个文件都是复制粘贴 |
| 登录态判断（token/userInfo 的 localStorage 读写） | `composables/useAuth.js` | Login.vue 与 request.js 各写了一份 |

**规则**：以上组件一旦创建，旧的内联写法必须在同一次改动中删除，不允许新旧并存。

---

## 7. 正反示例

❌ **禁止**（在 view 里复制结构）：

```vue
<!-- About.vue 里复制一遍、Interview.vue 又复制一遍 -->
<div class="page-head">
  <div class="page-head-icon"><AppIcon name="user" :size="20" /></div>
  <div><h1>关于</h1><p>副标题</p></div>
</div>
```

✅ **要求**（抽成组件）：

```vue
<!-- components/PageHead.vue -->
<template>
  <div class="page-head">
    <div class="page-head-icon"><AppIcon :name="icon" :size="20" /></div>
    <div><h1>{{ title }}</h1><p v-if="subtitle">{{ subtitle }}</p></div>
    <slot name="action" />
  </div>
</template>
<script setup>
defineProps({ icon: String, title: String, subtitle: String })
</script>

<!-- views/About.vue -->
<PageHead icon="user" title="关于" subtitle="Lynn's Blog" />
```

---

## 8. 提交前自查清单

- [ ] 本文件之外，没有引入新的全局样式覆盖
- [ ] 新组件满足：props 进、emits 出、不调接口
- [ ] 同一 UI 结构在项目中出现不超过 1 次
- [ ] 分页/loading 逻辑用的是 `usePagedList` 而不是手写
- [ ] 接口调用在 `api/` 层，组件零 `axios` 依赖
- [ ] 删除了被组件替代的旧代码，无新旧并存
