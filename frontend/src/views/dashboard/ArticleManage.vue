<template>
  <div class="article-manage">
    <div class="manage-head">
      <div>
        <h2>文章管理</h2>
        <p>撰写、编辑与发布文章</p>
      </div>
      <el-button type="primary" @click="openCreate">新建文章</el-button>
    </div>

    <div class="filter-bar">
      <el-input v-model="query.keyword" clearable placeholder="搜索标题/摘要" @keyup.enter="load(1)">
        <template #prefix><AppIcon name="search" :size="15" /></template>
      </el-input>
      <el-select v-model="query.categoryId" clearable placeholder="全部分类" @change="load(1)">
        <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id" />
      </el-select>
      <el-select v-model="query.status" clearable placeholder="全部状态" @change="load(1)">
        <el-option label="已发布" :value="1" />
        <el-option label="草稿" :value="0" />
      </el-select>
      <el-button @click="load(1)">查询</el-button>
    </div>

    <el-table :data="articles" v-loading="loading" class="article-table">
      <el-table-column prop="title" label="标题" min-width="200">
        <template #default="{ row }">
          <router-link :to="`/article/${row.id}`" class="article-link">{{ row.title }}</router-link>
        </template>
      </el-table-column>
      <el-table-column label="分类" width="110">
        <template #default="{ row }">{{ row.categoryName || '-' }}</template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '已发布' : '草稿' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="updateTime" label="更新时间" width="170" />
      <el-table-column label="操作" width="170" fixed="right">
        <template #default="{ row }">
          <el-button link type="info" @click="preview(row)">预览</el-button>
          <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
          <el-button link type="danger" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-if="total > 0"
      class="pagination"
      background
      layout="prev, pager, next"
      :total="total"
      :page-size="query.size"
      :current-page="query.page"
      @current-change="load"
    />

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑文章' : '新建文章'" width="min(920px, 94vw)">
      <el-form :model="form" label-position="top">
        <el-form-item label="标题" required>
          <el-input v-model="form.title" maxlength="100" show-word-limit placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="摘要">
          <el-input v-model="form.summary" type="textarea" :rows="2" maxlength="255" show-word-limit placeholder="一句话介绍这篇文章" />
        </el-form-item>
        <div class="form-row">
          <el-form-item label="分类">
            <el-select v-model="form.categoryId" clearable placeholder="选择分类" style="width: 100%">
              <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="封面">
            <div class="cover-upload" :class="{ 'is-uploading': coverUploading }" @click="coverInput?.click()">
              <template v-if="form.coverImage">
                <img :src="form.coverImage" alt="封面预览" />
                <span class="cover-remove" @click.stop="form.coverImage = ''">移除</span>
              </template>
              <span v-else class="cover-placeholder">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="18" height="18" rx="3"/><circle cx="9" cy="9" r="2"/><path d="m21 15-3.2-3.2a2 2 0 0 0-2.8 0L7 20"/></svg>
                {{ coverUploading ? '上传中…' : '点击选择本地图片' }}
              </span>
            </div>
            <input ref="coverInput" type="file" accept="image/*" class="cover-input" @change="onCoverChange" />
          </el-form-item>
        </div>
        <el-form-item label="正文" required>
          <MdEditor v-model="form.content" :toolbars="toolbars" class="editor" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button @click="submit(false)">存草稿</el-button>
        <el-button type="primary" :loading="saving" @click="submit(editingId ? null : true)">
          {{ editingId ? '保存修改' : '发布' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { MdEditor } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import { listArticles, createArticle, updateArticle, deleteArticle, listCategories } from '../../api/article'
import { uploadFile } from '../../api/file'
import AppIcon from '../../components/AppIcon.vue'

const articles = ref([])
const categories = ref([])
const total = ref(0)
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const editingId = ref(null)
const coverInput = ref(null)
const coverUploading = ref(false)
const query = reactive({ page: 1, size: 10, keyword: '', categoryId: null, status: null })
const form = reactive({ title: '', summary: '', categoryId: null, coverImage: '', status: 1, content: '' })
const toolbars = ['bold', 'italic', 'underline', 'strike', 'title', '-', 'quote', 'unorderedList', 'orderedList', '-', 'code', 'link', 'image', 'table', '-', 'preview', 'fullscreen']

async function load(page = query.page) {
  query.page = page
  loading.value = true
  try {
    const params = {
      keyword: query.keyword || undefined,
      categoryId: query.categoryId || undefined,
      status: query.status ?? undefined
    }
    const { data } = await listArticles(page, query.size, params)
    articles.value = data.records || []
    total.value = Number(data.total || 0)
  } finally {
    loading.value = false
  }
}

async function loadCategories() {
  const { data } = await listCategories()
  categories.value = data || []
}

function resetForm() {
  editingId.value = null
  Object.assign(form, { title: '', summary: '', categoryId: null, coverImage: '', status: 1, content: '' })
}

function openCreate() {
  resetForm()
  dialogVisible.value = true
}

function openEdit(row) {
  editingId.value = row.id
  Object.assign(form, {
    title: row.title,
    summary: row.summary,
    categoryId: row.categoryId,
    coverImage: row.coverImage,
    status: row.status,
    content: row.content
  })
  dialogVisible.value = true
}

// 封面：点击选择本地图片 → 上传后端 → 回填 URL 预览
async function onCoverChange(e) {
  const file = e.target.files?.[0]
  e.target.value = ''
  if (!file) return
  if (!file.type.startsWith('image/')) {
    ElMessage.warning('请选择图片文件')
    return
  }
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.warning('图片不能超过 5MB')
    return
  }
  coverUploading.value = true
  try {
    const { data } = await uploadFile(file, 'article')
    form.coverImage = data.url
    ElMessage.success('封面已上传')
  } catch (err) {
    // 错误提示已由请求封装统一处理
  } finally {
    coverUploading.value = false
  }
}

// publish: true 发布 / false 存草稿 / null 编辑时保持原状态
async function submit(publish) {
  if (!form.title.trim() || !form.content.trim()) {
    ElMessage.warning('请填写标题和正文')
    return
  }
  if (publish === true) form.status = 1
  else if (publish === false) form.status = 0
  saving.value = true
  try {
    if (editingId.value) {
      await updateArticle(editingId.value, form)
      ElMessage.success(publish === false ? '已保存草稿' : '已保存修改')
    } else {
      await createArticle(form)
      ElMessage.success(publish ? '发布成功，可在首页查看 ♥' : '草稿已保存')
    }
    dialogVisible.value = false
    load()
  } finally {
    saving.value = false
  }
}

function preview(row) {
  window.open(`/article/${row.id}`, '_blank')
}

async function remove(row) {
  await ElMessageBox.confirm(`确定删除「${row.title}」吗？`, '提示', { type: 'warning' })
  await deleteArticle(row.id)
  ElMessage.success('已删除')
  load()
}

onMounted(() => {
  load()
  loadCategories()
})
</script>

<style scoped>
.article-manage{display:flex;flex-direction:column;gap:16px}
.manage-head{display:flex;justify-content:space-between;align-items:center;gap:14px}
.manage-head h2{margin:0;font-size:20px}
.manage-head p{margin:4px 0 0;color:var(--text-sub);font-size:13px}
.filter-bar{display:flex;gap:10px;flex-wrap:wrap}
.filter-bar .el-input{width:260px}
.filter-bar .el-select{width:150px}
.article-table{width:100%}
.article-link{color:var(--brand);font-weight:600}
.article-link:hover{text-decoration:underline}
.pagination{margin-top:16px;justify-content:flex-end}
.form-row{display:grid;grid-template-columns:1fr 1.4fr;gap:14px}
.editor{min-height:420px}
.cover-upload{position:relative;display:flex;align-items:center;justify-content:center;width:100%;height:120px;overflow:hidden;border:2px dashed var(--el-color-primary-light-7);border-radius:12px;background:var(--el-color-primary-light-9);cursor:pointer;transition:border-color .2s ease,background-color .2s ease}
.cover-upload:hover{border-color:var(--brand)}
.cover-upload.is-uploading{opacity:.7;pointer-events:none}
.cover-upload img{width:100%;height:100%;object-fit:cover}
.cover-placeholder{display:inline-flex;flex-direction:column;align-items:center;gap:6px;color:var(--text-sub);font-size:13px}
.cover-placeholder svg{width:26px;height:26px}
.cover-remove{position:absolute;right:8px;top:8px;padding:2px 10px;border-radius:999px;background:rgba(0,0,0,.55);color:#fff;font-size:12px;cursor:pointer}
.cover-remove:hover{background:rgba(0,0,0,.75)}
.cover-input{display:none}
@media (max-width:680px){
  .form-row{grid-template-columns:1fr}
  .filter-bar .el-input,.filter-bar .el-select{width:100%}
}
</style>
