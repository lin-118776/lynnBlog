<template>
  <div class="page-container">
    <PageHead icon="folder" title="分类管理" subtitle="文章分类的新建、重命名与删除" />

    <el-card shadow="never">
      <div class="cat-create">
        <el-input
          v-model="newName"
          class="cat-input"
          maxlength="50"
          placeholder="新分类名称，如：美食 / 旅行"
          clearable
          @keyup.enter="handleCreate"
        />
        <el-button type="primary" :loading="creating" @click="handleCreate">新建分类</el-button>
      </div>

      <el-table v-loading="loading" :data="categories" style="width: 100%">
        <el-table-column prop="name" label="名称" min-width="140">
          <template #default="{ row }">
            <span class="cat-name">{{ row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="articleCount" label="文章数" width="100">
          <template #default="{ row }">
            <span class="cat-count">{{ row.articleCount ?? 0 }} 篇</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleRename(row)">重命名</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import PageHead from '../../components/PageHead.vue'
import { listCategories, createCategory, renameCategory, deleteCategory } from '../../api/category'

const categories = ref([])
const loading = ref(false)
const newName = ref('')
const creating = ref(false)

async function load() {
  loading.value = true
  try {
    const { data } = await listCategories()
    categories.value = Array.isArray(data) ? data : []
  } catch (e) {
    categories.value = []
  } finally {
    loading.value = false
  }
}

async function handleCreate() {
  const name = newName.value.trim()
  if (!name) {
    ElMessage.warning('先输入分类名称吧')
    return
  }
  creating.value = true
  try {
    await createCategory(name)
    ElMessage.success(`已创建「${name}」`)
    newName.value = ''
    load()
  } catch (e) {
    // 错误提示已由请求封装统一处理
  } finally {
    creating.value = false
  }
}

async function handleRename(row) {
  try {
    const { value } = await ElMessageBox.prompt('输入新的分类名称', `重命名「${row.name}」`, {
      inputValue: row.name,
      inputPattern: /\S+/,
      inputErrorMessage: '分类名称不能为空',
      inputMaxlength: 50
    })
    const name = value.trim()
    if (name === row.name) return
    await renameCategory(row.id, name)
    ElMessage.success('重命名成功')
    load()
  } catch (e) {
    // 取消或失败不处理
  }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(
      `确定删除分类「${row.name}」吗？${row.articleCount ? `该分类下有 ${row.articleCount} 篇文章，将无法删除。` : ''}`,
      '删除确认',
      { type: 'warning', confirmButtonText: '删除', cancelButtonText: '取消' }
    )
    await deleteCategory(row.id)
    ElMessage.success('已删除')
    load()
  } catch (e) {
    // 取消或失败不处理
  }
}

onMounted(load)
</script>

<style scoped>
.cat-create {
  display: flex;
  gap: 10px;
  margin-bottom: 18px;
  max-width: 420px;
}

.cat-input {
  flex: 1;
}

.cat-name {
  font-weight: 600;
}

.cat-count {
  color: var(--text-sub);
  font-size: 13px;
}

@media (max-width: 560px) {
  .cat-create {
    flex-direction: column;
    max-width: none;
  }
}
</style>
