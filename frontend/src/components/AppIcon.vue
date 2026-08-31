<template>
  <svg
    class="app-icon"
    :width="size"
    :height="size"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    :stroke-width="strokeWidth"
    stroke-linecap="round"
    stroke-linejoin="round"
    aria-hidden="true"
  >
    <template v-for="(seg, i) in icons[name]" :key="i">
      <path v-if="seg[0] === 'p'" :d="seg[1]" />
      <circle v-else-if="seg[0] === 'c'" :cx="seg[1]" :cy="seg[2]" :r="seg[3]" />
      <rect v-else-if="seg[0] === 'r'" :x="seg[1]" :y="seg[2]" :width="seg[3]" :height="seg[4]" :rx="seg[5] ?? 0" />
      <line v-else-if="seg[0] === 'l'" :x1="seg[1]" :y1="seg[2]" :x2="seg[3]" :y2="seg[4]" />
      <polyline v-else-if="seg[0] === 'pl'" :points="seg[1]" />
    </template>
  </svg>
</template>

<script setup>
const props = defineProps({
  name: { type: String, required: true },
  size: { type: [Number, String], default: 16 },
  strokeWidth: { type: [Number, String], default: 1.8 }
})

// 统一 Feather 风格线性图标：p=path, c=circle, r=rect, l=line, pl=polyline
const icons = {
  eye: [
    ['p', 'M1 12s4-7 11-7 11 7 11 7-4 7-11 7S1 12 1 12z'],
    ['c', 12, 12, 3]
  ],
  heart: [
    ['p', 'M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z']
  ],
  calendar: [
    ['r', 3, 4, 18, 17, 2],
    ['l', 16, 2, 16, 6],
    ['l', 8, 2, 8, 6],
    ['l', 3, 10, 21, 10]
  ],
  'chevron-left': [
    ['pl', '15 18 9 12 15 6']
  ],
  'arrow-right': [
    ['l', 5, 12, 19, 12],
    ['pl', '12 5 19 12 12 19']
  ],
  search: [
    ['c', 11, 11, 7],
    ['l', 21, 21, 16.65, 16.65]
  ],
  user: [
    ['p', 'M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2'],
    ['c', 12, 7, 4]
  ],
  lock: [
    ['r', 3, 11, 18, 10, 2],
    ['p', 'M7 11V7a5 5 0 0 1 10 0v4']
  ],
  book: [
    ['p', 'M4 19.5A2.5 2.5 0 0 1 6.5 17H20'],
    ['p', 'M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z']
  ],
  message: [
    ['p', 'M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z']
  ],
  sparkles: [
    ['p', 'M12 3l1.9 5.1L19 10l-5.1 1.9L12 17l-1.9-5.1L5 10l5.1-1.9L12 3z'],
    ['p', 'M19 15l.9 2.4L22 18.3l-2.1.9L19 21.6l-.9-2.4-2.1-.9 2.1-.9L19 15z'],
    ['p', 'M5 16l.7 1.8L7.5 18.5l-1.8.7L5 21l-.7-1.8-1.8-.7 1.8-.7L5 16z']
  ],
  logout: [
    ['p', 'M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4'],
    ['pl', '16 17 21 12 16 7'],
    ['l', 21, 12, 9, 12]
  ],
  grid: [
    ['r', 3, 3, 7, 7, 1],
    ['r', 14, 3, 7, 7, 1],
    ['r', 14, 14, 7, 7, 1],
    ['r', 3, 14, 7, 7, 1]
  ],
  menu: [
    ['l', 3, 6, 21, 6],
    ['l', 3, 12, 21, 12],
    ['l', 3, 18, 21, 18]
  ],
  close: [
    ['l', 6, 6, 18, 18],
    ['l', 18, 6, 6, 18]
  ],
  pen: [
    ['p', 'M12 20h9'],
    ['p', 'M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z']
  ],
  code: [
    ['pl', '16 18 22 12 16 6'],
    ['pl', '8 6 2 12 8 18']
  ],
  image: [
    ['r', 3, 3, 18, 18, 2],
    ['c', 8.5, 8.5, 1.5],
    ['pl', '21 15 16 10 5 21']
  ],
  key: [
    ['p', 'M21 2l-2 2m-7.61 7.61a5.5 5.5 0 1 1-7.778 7.778 5.5 5.5 0 0 1 7.777-7.777zm0 0L15.5 7.5m0 0l3 3L22 7l-3-3m-3.5 3.5L19 4']
  ],
  folder: [
    ['p', 'M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z']
  ],
  file: [
    ['p', 'M13 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V9z'],
    ['pl', '13 2 13 9 20 9']
  ],
  home: [
    ['p', 'M3 9.5 12 3l9 6.5'],
    ['p', 'M5 8.5V21h14V8.5']
  ]
}
</script>

<style scoped>
.app-icon {
  display: inline-block;
  vertical-align: -0.2em;
  flex-shrink: 0;
}
</style>