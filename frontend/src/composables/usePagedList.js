import { ref } from 'vue'
import { ElMessage } from 'element-plus'

/**
 * 通用分页列表逻辑
 * @param {(page:number,size:number)=>Promise<{records:Array,total:number}>} fetcher 请求函数
 * @param {number} defaultSize 每页条数
 *
 * 用法：
 *   const { list, total, page, size, loading, load } = usePagedList((p, s) => listArticle(p, s))
 *   onMounted(load)
 *   翻页时改 page.value 后调用 load()
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
      list.value = records || []
      total.value = t || 0
    } catch (e) {
      // 业务错误已由 request 响应拦截器统一提示，这里只兜底
      if (!e?.handled) ElMessage.error(e?.message || '加载失败')
    } finally {
      loading.value = false
    }
  }

  /** 翻页（页码从 1 开始，自动夹取到有效范围） */
  async function turn(p) {
    page.value = Math.max(1, p)
    await load()
  }

  return { list, total, page, size, loading, load, turn }
}
