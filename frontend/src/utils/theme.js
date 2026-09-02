/**
 * 主题换装系统（Design Token）
 * 全站颜色都取自 .sanrio-page 顶部的 CSS 变量，这里用更高优先级的选择器
 * （html[data-theme] .sanrio-page）注入各套色板，实现一键换装且无需改动组件。
 *
 * 默认"经典甜粉"不做任何覆盖（兼容无 JS / 旧缓存场景）；
 * 其余每套只重定义调色板变量，天然支持新增主题。
 */

export const THEMES = [
  { key: 'pink', label: '经典甜粉', dot: '#e45b8d' },
  { key: 'lavender', label: '梦幻薰衣草', dot: '#8a6fc4' },
  { key: 'mint', label: '薄荷奶绿', dot: '#2f9e8f' },
  { key: 'sunny', label: '阳光柠檬', dot: '#e8912d' }
]

const STORAGE_KEY = 'lynn-blog-theme'

const PALETTES = {
  // 梦幻薰衣草：粉紫低饱和，奶油白底
  lavender: {
    '--pink-50': '#f7f3ff',
    '--pink-100': '#efe8ff',
    '--pink-200': '#e0d5fb',
    '--pink-300': '#cbb7f5',
    '--pink-400': '#b49be9',
    '--rose': '#8a6fc4',
    '--berry': '#5c46a3',
    '--cream': '#fdfbff',
    '--line': '#d9cbf2',
    '--mint': '#dcefea',
    '--sky': '#dde7fb',
    '--lemon': '#f6efd3',
    '--shadow': '0 12px 30px rgba(110, 86, 176, .12)'
  },
  // 薄荷奶绿：清爽绿调
  mint: {
    '--pink-50': '#f0faf7',
    '--pink-100': '#e0f4ee',
    '--pink-200': '#c5e9e0',
    '--pink-300': '#9fd8cd',
    '--pink-400': '#7cc4b6',
    '--rose': '#2f9e8f',
    '--berry': '#1f7468',
    '--cream': '#fbfffd',
    '--line': '#c0e6dc',
    '--mint': '#cdeede',
    '--sky': '#d9edfb',
    '--lemon': '#f5efc8',
    '--shadow': '0 12px 30px rgba(47, 158, 143, .12)'
  },
  // 阳光柠檬：奶油黄油色，暖调
  sunny: {
    '--pink-50': '#fff9ef',
    '--pink-100': '#ffefd6',
    '--pink-200': '#ffe0b0',
    '--pink-300': '#ffcd85',
    '--pink-400': '#f5b463',
    '--rose': '#e0762a',
    '--berry': '#9c5b12',
    '--cream': '#fffcf5',
    '--line': '#f0d3a8',
    '--mint': '#dcefea',
    '--sky': '#d9edfb',
    '--lemon': '#ffe9a8',
    '--shadow': '0 12px 30px rgba(224, 118, 42, .12)'
  }
}

/** 收集某套色板的 CSS 变量文本 */
function paletteCss(key, vars) {
  const lines = Object.entries(vars).map(([name, value]) => `${name}:${value};`).join('')
  return `html[data-theme='${key}'] .sanrio-page{${lines}}`
}

let styleEl = null
function ensureStyle() {
  if (styleEl) return
  const css = Object.entries(PALETTES).map(([key, vars]) => paletteCss(key, vars)).join('\n')
  styleEl = document.createElement('style')
  styleEl.setAttribute('data-theme-css', 'lynn-blog')
  styleEl.textContent = css
  document.head.appendChild(styleEl)
}

/** 应用主题：切换 html[data-theme]，可选持久化 */
export function applyTheme(key, persist = true) {
  const k = THEMES.some((t) => t.key === key) ? key : 'pink'
  ensureStyle()
  document.documentElement.dataset.theme = k
  if (persist) {
    try {
      localStorage.setItem(STORAGE_KEY, k)
    } catch (e) {
      /* 隐私模式等场景静默失败 */
    }
  }
}

/** 读取已保存主题（无则默认粉色） */
export function getStoredTheme() {
  try {
    const v = localStorage.getItem(STORAGE_KEY)
    return THEMES.some((t) => t.key === v) ? v : 'pink'
  } catch (e) {
    return 'pink'
  }
}

/** 应用前执行（无闪烁）：在 createApp 之前调用一次即可 */
export function initTheme() {
  applyTheme(getStoredTheme(), false)
}
