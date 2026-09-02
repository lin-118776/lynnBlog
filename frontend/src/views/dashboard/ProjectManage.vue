<template>
  <div class="project-manage">
    <div class="manage-head">
      <div>
        <h2>作品管理</h2>
        <p>管理作品集，勾选「显示」的会展示在 /works 页面</p>
      </div>
      <el-button type="primary" round @click="openForm()">＋ 新增作品</el-button>
    </div>

    <el-table :data="items" v-loading="loading" empty-text="还没有作品，点右上角新增吧 ♥">
      <el-table-column label="封面" width="86">
        <template #default="{ row }">
          <img v-if="row.coverImage" :src="row.coverImage" class="cover-thumb" :alt="row.name" />
          <span v-else class="cover-empty">{{ row.name?.slice(0, 1) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="项目名" min-width="140" show-overflow-tooltip />
      <el-table-column prop="description" label="简介" min-width="180" show-overflow-tooltip>
        <template #default="{ row }">{{ row.description || '-' }}</template>
      </el-table-column>
      <el-table-column label="技术栈" min-width="150">
        <template #default="{ row }">
          <span v-for="t in techList(row)" :key="t" class="tech-tag">{{ t }}</span>
        </template>
      </el-table-column>
      <el-table-column label="显示" width="76">
        <template #default="{ row }">
          <span :class="['pub-tag', row.visible ? 'pub-on' : 'pub-off']">{{ row.visible ? '显示' : '隐藏' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="130" fixed="right">
        <template #default="{ row }">
          <el-button size="small" text type="primary" @click="openForm(row)">编辑</el-button>
          <el-popconfirm title="确定删除这个作品？" width="170" @confirm="remove(row)">
            <template #reference><el-button size="small" text type="danger">删除</el-button></template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增 / 编辑弹窗 -->
    <el-dialog v-model="formVisible" :title="form.id ? '编辑作品' : '新增作品'" width="min(520px, 94vw)">
      <el-form :model="form" label-width="80px">
        <el-form-item label="项目名" required>
          <el-input v-model="form.name" maxlength="100" placeholder="如：Lynn's Blog" />
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="form.description" type="textarea" :rows="3" maxlength="500" placeholder="项目做什么的？" />
        </el-form-item>
        <el-form-item label="在线地址">
          <el-input v-model="form.url" maxlength="500" placeholder="https://example.com（选填）" />
        </el-form-item>
        <el-form-item label="GitHub">
          <el-input v-model="form.githubUrl" maxlength="500" placeholder="https://github.com/xxx/xxx（选填）" />
        </el-form-item>
        <el-form-item label="技术栈">
          <el-input v-model="form.tech" maxlength="200" placeholder="用逗号分隔，如：Vue3, Spring Boot, MySQL" />
        </el-form-item>
        <el-form-item label="封面">
          <div class="upload-row">
            <img v-if="form.coverImage" :src="form.coverImage" class="cover-preview" alt="封面预览" />
            <el-button size="small" :loading="uploading" @click="fileInput?.click()">选择图片</el-button>
            <input ref="fileInput" type="file" accept="image/*" class="hidden-input" @change="onCoverChange" />
          </div>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" :max="999" style="width: 100%" />
        </el-form-item>
        <el-form-item label="显示">
          <el-switch v-model="form.visible" :active-value="1" :inactive-value="0" active-text="显示" inactive-text="隐藏" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { listProjects, createProject, updateProject, deleteProject } from '../../api/project'
import { uploadFile } from '../../api/file'

const items = ref([])
const loading = ref(false)
const formVisible = ref(false)
const saving = ref(false)
const uploading = ref(false)
const fileInput = ref(null)

const emptyForm = () => ({
  id: null, name: '', description: '', url: '', githubUrl: '', tech: '',
  coverImage: '', sort: 0, visible: 1
})
const form = reactive(emptyForm())

function techList(row) {
  return (row.tech || '').split(',').map((t) => t.trim()).filter(Boolean)
}

async function load() {
  loading.value = true
  try {
    const { data } = await listProjects()
    items.value = Array.isArray(data) ? data : []
  } catch (e) {
    items.value = []
  } finally {
    loading.value = false
  }
}

function openForm(row) {
  if (row) Object.assign(form, emptyForm(), row)
  else Object.assign(form, emptyForm())
  formVisible.value = true
}

async function save() {
  if (!form.name.trim()) {
    ElMessage.warning('请填写项目名')
    return
  }
  saving.value = true
  try {
    const payload = { ...form }
    if (payload.id) await updateProject(payload.id, payload)
    else await createProject(payload)
    ElMessage.success('保存成功')
    formVisible.value = false
    load()
  } catch (e) {
    // 错误提示已由请求封装统一处理
  } finally {
    saving.value = false
  }
}

async function remove(row) {
  await deleteProject(row.id)
  ElMessage.success('已删除')
  load()
}

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
  uploading.value = true
  try {
    const { data } = await uploadFile(file, 'project')
    form.coverImage = data.url
    ElMessage.success('封面已上传')
  } catch (e) {
    // 错误提示已由请求封装统一处理
  } finally {
    uploading.value = false
  }
}

onMounted(() => load())
</script>

<style scoped>
.project-manage{display:flex;flex-direction:column;gap:14px}
.manage-head{display:flex;align-items:center;justify-content:space-between;gap:12px}
.manage-head h2{margin:0;font-size:18px}
.manage-head p{margin:4px 0 0;font-size:12px;color:var(--text-tertiary)}
.cover-thumb{width:72px;height:44px;object-fit:cover;border-radius:8px;border:2px solid #fff;box-shadow:0 3px 8px rgba(183,85,129,.14)}
.cover-empty{display:inline-grid;place-items:center;width:72px;height:44px;border-radius:8px;background:linear-gradient(135deg,var(--pink-300,#ffb7d0),var(--rose,#e45b8d));color:#fff;font-size:16px;font-weight:800}
.tech-tag{display:inline-flex;margin:1px 3px 1px 0;padding:1px 8px;border-radius:999px;background:var(--sky,#d9edfb);color:#35698a;font-size:10.5px;font-weight:800}
.pub-tag{font-size:12px;font-weight:700}
.pub-on{color:var(--brand)}
.pub-off{color:var(--text-tertiary)}
.upload-row{display:flex;align-items:center;gap:10px}
.cover-preview{width:96px;height:56px;object-fit:cover;border-radius:8px;border:2px solid #fff;box-shadow:0 3px 8px rgba(183,85,129,.14)}
.hidden-input{display:none}
</style>
