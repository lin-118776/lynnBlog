<template>
  <div class="friend-manage">
    <div class="manage-head">
      <div>
        <h2>友链管理</h2>
        <p>管理公开友链：访客提交的申请显示「待审核」，打开「显示」开关即审核通过</p>
      </div>
      <el-button type="primary" round @click="openForm()">＋ 新增友链</el-button>
    </div>

    <el-table :data="items" v-loading="loading" empty-text="还没有友链，点右上角新增吧 ♥">
      <el-table-column label="头像" width="70">
        <template #default="{ row }">
          <img v-if="row.avatar" :src="row.avatar" class="avatar-thumb" :alt="row.name" />
          <span v-else class="avatar-empty">♥</span>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="站点名" min-width="130" show-overflow-tooltip />
      <el-table-column prop="url" label="地址" min-width="180" show-overflow-tooltip>
        <template #default="{ row }"><a :href="row.url" target="_blank" rel="noopener noreferrer" class="url-link">{{ row.url }}</a></template>
      </el-table-column>
      <el-table-column prop="description" label="介绍" min-width="160" show-overflow-tooltip>
        <template #default="{ row }">{{ row.description || '-' }}</template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <span :class="['pub-tag', row.visible ? 'pub-on' : 'pub-off']">{{ row.visible ? '已显示' : '待审核' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="130" fixed="right">
        <template #default="{ row }">
          <el-button size="small" text type="primary" @click="openForm(row)">编辑</el-button>
          <el-popconfirm title="确定删除这个友链？" width="170" @confirm="remove(row)">
            <template #reference><el-button size="small" text type="danger">删除</el-button></template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增 / 编辑弹窗 -->
    <el-dialog v-model="formVisible" :title="form.id ? '编辑友链' : '新增友链'" width="min(480px, 94vw)">
      <el-form :model="form" label-width="80px">
        <el-form-item label="站点名" required>
          <el-input v-model="form.name" maxlength="50" placeholder="如：小兔子的花园" />
        </el-form-item>
        <el-form-item label="地址" required>
          <el-input v-model="form.url" maxlength="500" placeholder="https://example.com" />
        </el-form-item>
        <el-form-item label="头像">
          <div class="upload-row">
            <img v-if="form.avatar" :src="form.avatar" class="avatar-preview" alt="头像预览" />
            <el-button size="small" :loading="uploading" @click="fileInput?.click()">选择图片</el-button>
            <input ref="fileInput" type="file" accept="image/*" class="hidden-input" @change="onAvatarChange" />
          </div>
        </el-form-item>
        <el-form-item label="介绍">
          <el-input v-model="form.description" maxlength="200" placeholder="一句话介绍这个站点" />
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
import { listAllFriends, createFriend, updateFriend, deleteFriend } from '../../api/friend'
import { uploadFile } from '../../api/file'

const items = ref([])
const loading = ref(false)
const formVisible = ref(false)
const saving = ref(false)
const uploading = ref(false)
const fileInput = ref(null)

const emptyForm = () => ({ id: null, name: '', url: '', avatar: '', description: '', sort: 0, visible: 1 })
const form = reactive(emptyForm())

async function load() {
  loading.value = true
  try {
    const { data } = await listAllFriends()
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
    ElMessage.warning('请填写站点名')
    return
  }
  if (!form.url.trim()) {
    ElMessage.warning('请填写友链地址')
    return
  }
  saving.value = true
  try {
    const payload = { ...form }
    if (payload.id) await updateFriend(payload.id, payload)
    else await createFriend(payload)
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
  await deleteFriend(row.id)
  ElMessage.success('已删除')
  load()
}

async function onAvatarChange(e) {
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
    const { data } = await uploadFile(file, 'friend')
    form.avatar = data.url
    ElMessage.success('头像已上传')
  } catch (e) {
    // 错误提示已由请求封装统一处理
  } finally {
    uploading.value = false
  }
}

onMounted(() => load())
</script>

<style scoped>
.friend-manage{display:flex;flex-direction:column;gap:14px}
.manage-head{display:flex;align-items:center;justify-content:space-between;gap:12px}
.manage-head h2{margin:0;font-size:18px}
.manage-head p{margin:4px 0 0;font-size:12px;color:var(--text-tertiary)}
.avatar-thumb{width:42px;height:42px;border-radius:50%;object-fit:cover;border:2px solid #fff;box-shadow:0 3px 8px rgba(183,85,129,.14)}
.avatar-empty{display:inline-grid;place-items:center;width:42px;height:42px;border-radius:50%;background:linear-gradient(135deg,var(--pink-300,#ffb7d0),var(--rose,#e45b8d));color:#fff;font-size:14px}
.url-link{color:var(--brand);font-size:12px;text-decoration:none}
.url-link:hover{text-decoration:underline}
.pub-tag{font-size:12px;font-weight:700}
.pub-on{color:var(--brand)}
.pub-off{color:var(--text-tertiary)}
.upload-row{display:flex;align-items:center;gap:10px}
.avatar-preview{width:48px;height:48px;border-radius:50%;object-fit:cover;border:2px solid #fff;box-shadow:0 3px 8px rgba(183,85,129,.14)}
.hidden-input{display:none}
</style>
