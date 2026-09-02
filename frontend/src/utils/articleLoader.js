/**
 * 全量已发布文章加载器：供归档 / 标签云 / 站内搜索等站点级页面使用
 * 分页拉取 status=1（避免作者登录态把草稿混进来），会话内缓存
 */
import { listArticles } from '../api/article'

let cache = null
let loadingPromise = null

export async function fetchAllPublishedArticles(force = false) {
  if (cache && !force) return cache
  if (loadingPromise && !force) return loadingPromise

  loadingPromise = (async () => {
    const all = []
    const size = 50
    let page = 1
    for (;;) {
      const { data } = await listArticles(page, size, { status: 1, sort: 'latest' })
      const records = data.records || []
      all.push(...records)
      if (records.length < size || page >= 50) break
      page += 1
    }
    cache = all
    return all
  })()

  try {
    return await loadingPromise
  } finally {
    loadingPromise = null
  }
}

/** 失效缓存（管理端增删改后可调用） */
export function invalidateArticleCache() {
  cache = null
}
