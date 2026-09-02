<template>
  <div class="lolita-manage">
    <div class="manage-head">
      <div>
        <h2>我的衣橱</h2>
        <p>管理穿搭与兴趣收藏，勾选「公开」的会展示在穿搭墙</p>
      </div>
      <el-button type="primary" round @click="openForm()">＋ 新增穿搭</el-button>
    </div>

    <el-table :data="items" v-loading="loading" empty-text="衣橱还是空的，点右上角「新增穿搭」开始收藏吧 ♥">
      <el-table-column label="封面" width="74">
        <template #default="{ row }">
          <img v-if="row.coverImage" :src="row.coverImage" class="cover-thumb" :alt="row.name" />
          <span v-else class="cover-empty">无图</span>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="名称" min-width="150" show-overflow-tooltip />
      <el-table-column label="品牌 / 系列" min-width="150" show-overflow-tooltip>
        <template #default="{ row }">{{ row.brand || '-' }}<template v-if="row.series"> · {{ row.series }}</template></template>
      </el-table-column>
      <el-table-column label="分类" width="88">
        <template #default="{ row }"><span class="cat-tag">{{ row.category }}</span></template>
      </el-table-column>
      <el-table-column label="状态" width="82">
        <template #default="{ row }">{{ row.status || '-' }}</template>
      </el-table-column>
      <el-table-column label="穿着" width="104">
        <template #default="{ row }">
          <span class="wear-num">{{ row.wearCount || 0 }} 次</span>
          <el-button size="small" text type="primary" @click="wear(row)">+1</el-button>
        </template>
      </el-table-column>
      <el-table-column label="公开" width="76">
        <template #default="{ row }">
          <span :class="['pub-tag', row.isPublic ? 'pub-on' : 'pub-off']">{{ row.isPublic ? '公开' : '私密' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="130" fixed="right">
        <template #default="{ row }">
          <el-button size="small" text type="primary" @click="openForm(row)">编辑</el-button>
          <el-popconfirm title="确定删除这件？" width="160" @confirm="remove(row)">
            <template #reference><el-button size="small" text type="danger">删除</el-button></template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-if="total > size"
      class="pagination"
      background
      layout="prev, pager, next"
      :total="total"
      :page-size="size"
      :current-page="page"
      @current-change="load"
    />

    <!-- 新增 / 编辑弹窗 -->
    <el-dialog v-model="formVisible" :title="form.id ? '编辑穿搭' : '新增穿搭'" width="min(560px, 94vw)">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称" required>
          <el-input v-model="form.name" maxlength="100" placeholder="如：草莓牛奶 OP" />
        </el-form-item>
        <el-form-item label="品牌">
          <el-input v-model="form.brand" maxlength="50" placeholder="如：AP / BABY" />
        </el-form-item>
        <el-form-item label="系列">
          <el-input v-model="form.series" maxlength="50" placeholder="印花 / 系列名" />
        </el-form-item>
        <el-form-item label="分类" required>
          <el-select v-model="form.category" placeholder="选择分类" style="width: 100%">
            <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>
        <el-form-item label="主色">
          <el-input v-model="form.color" maxlength="30" placeholder="如：粉白" />
        </el-form-item>
        <el-form-item label="尺码">
          <el-select v-model="form.size" placeholder="选择尺码" style="width: 100%" clearable>
            <el-option v-for="s in sizes" :key="s" :label="s" :value="s" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" placeholder="选择状态" style="width: 100%">
            <el-option v-for="s in statuses" :key="s" :label="s" :value="s" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="form.status === '待补款'" label="待补金额">
          <el-input-number v-model="form.balanceDue" :min="0" :precision="2" style="width: 100%" placeholder="还需补多少" />
        </el-form-item>
        <el-form-item label="入手价">
          <el-input-number v-model="form.purchasePrice" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="入手日期">
          <el-date-picker v-model="form.purchaseDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="存放位置">
          <el-input v-model="form.location" maxlength="100" placeholder="如：衣柜上层" />
        </el-form-item>
        <el-form-item label="主图">
          <div class="upload-row">
            <img v-if="form.coverImage" :src="form.coverImage" class="cover-preview" alt="封面预览" />
            <el-button size="small" :loading="uploading" @click="fileInput?.click()">选择图片</el-button>
            <input ref="fileInput" type="file" accept="image/*" class="hidden-input" @change="onCoverChange" />
          </div>
        </el-form-item>
        <el-form-item label="笔记">
          <el-input v-model="form.note" type="textarea" :rows="3" maxlength="500" show-word-limit placeholder="种草心得 / 搭配灵感" />
        </el-form-item>
        <el-form-item label="公开">
          <el-switch v-model="form.isPublic" :active-value="1" :inactive-value="0" active-text="公开" inactive-text="仅自己" />
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
import { listLolita, createLolita, updateLolita, deleteLolita, wearLolita } from '../../api/lolita'
import { uploadFile } from '../../api/file'

const items = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const loading = ref(false)
const formVisible = ref(false)
const saving = ref(false)
const uploading = ref(false)
const fileInput = ref(null)

const categories = ['OP', 'JSK', 'SK', 'SP', '衬衫', '配饰', '假发', '鞋', '其他']
const statuses = ['待补款', '已拥有', '已售出']
const sizes = ['XS', 'S', 'M', 'L', 'XL', '均码']

const emptyForm = () => ({
  id: null,
  name: '',
  brand: '',
  series: '',
  category: '',
  color: '',
  size: '',
  status: '现货',
  balanceDue: undefined,
  purchasePrice: undefined,
  purchaseDate: '',
  location: '',
  coverImage: '',
  note: '',
  isPublic: 0
})
const form = reactive(emptyForm())

async function load(p = page.value) {
  page.value = p
  loading.value = true
  try {
    const { data } = await listLolita(page.value, size.value)
    items.value = data.records || []
    total.value = Number(data.total || 0)
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
    ElMessage.warning('请填写名称')
    return
  }
  if (!form.category) {
    ElMessage.warning('请选择分类')
    return
  }
  saving.value = true
  try {
    const payload = { ...form }
    if (payload.id) await updateLolita(payload.id, payload)
    else await createLolita(payload)
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
  await deleteLolita(row.id)
  ElMessage.success('已删除')
  load()
}

async function wear(row) {
  const { data } = await wearLolita(row.id)
  row.wearCount = data
  ElMessage.success(`穿着次数 +1，共 ${data} 次`)
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
    const { data } = await uploadFile(file, 'lolita_img')
    form.coverImage = data.url
    ElMessage.success('主图已上传')
  } catch (e) {
    // 错误提示已由请求封装统一处理
  } finally {
    uploading.value = false
  }
}

onMounted(() => load())
</script>

<style scoped>
.lolita-manage{display:flex;flex-direction:column;gap:14px}
.manage-head{display:flex;align-items:center;justify-content:space-between;gap:12px}
.manage-head h2{margin:0;font-size:18px}
.manage-head p{margin:4px 0 0;font-size:12px;color:var(--text-tertiary)}
.cover-thumb{width:48px;height:48px;object-fit:cover;border-radius:10px;border:2px solid #fff;box-shadow:0 3px 8px rgba(183,85,129,.14)}
.cover-empty{display:inline-grid;place-items:center;width:48px;height:48px;border-radius:10px;background:var(--el-color-primary-light-9);color:var(--text-tertiary);font-size:11px}
.cat-tag{display:inline-flex;padding:2px 10px;border-radius:999px;background:var(--sky,#d9edfb);color:#35698a;font-size:12px;font-weight:700}
.wear-num{font-size:12px;color:var(--text-sub);margin-right:2px}
.pub-tag{font-size:12px;font-weight:700}
.pub-on{color:var(--brand)}
.pub-off{color:var(--text-tertiary)}
.pagination{margin-top:4px;justify-content:flex-end}
.upload-row{display:flex;align-items:center;gap:10px}
.cover-preview{width:56px;height:56px;object-fit:cover;border-radius:10px;border:2px solid #fff;box-shadow:0 3px 8px rgba(183,85,129,.14)}
.hidden-input{display:none}
</style>
