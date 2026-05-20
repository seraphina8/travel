<template>
  <div class="tag-manage">
    <div class="toolbar">
      <div class="toolbar-left">
        <select v-model="query.type" class="form-select" @change="loadTags">
          <option value="">全部类型</option>
          <option v-for="item in tagTypes" :key="item.value" :value="item.value">
            {{ item.label }}
          </option>
        </select>
        <button class="btn btn-outline-secondary" @click="resetQuery">
          <i class="bi bi-arrow-counterclockwise"></i> 重置
        </button>
      </div>
      <button class="btn btn-primary" @click="openModal()">
        <i class="bi bi-plus-lg"></i> 新增标签
      </button>
    </div>

    <div class="table-wrapper">
      <table class="table table-hover align-middle">
        <thead>
          <tr>
            <th>ID</th>
            <th>标签名称</th>
            <th>类型</th>
            <th>排序</th>
            <th>状态</th>
            <th class="text-end">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="tag in tags" :key="tag.id">
            <td>{{ tag.id }}</td>
            <td>{{ tag.name }}</td>
            <td>{{ getTypeName(tag.type) }}</td>
            <td>{{ tag.sortOrder || 0 }}</td>
            <td>
              <span class="badge" :class="tag.status === 1 ? 'bg-success' : 'bg-secondary'">
                {{ tag.status === 1 ? '启用' : '停用' }}
              </span>
            </td>
            <td class="text-end">
              <button class="btn btn-sm btn-outline-primary me-2" @click="openModal(tag)">
                <i class="bi bi-pencil"></i>
              </button>
              <button class="btn btn-sm btn-outline-warning me-2" @click="toggleStatus(tag)">
                <i class="bi" :class="tag.status === 1 ? 'bi-pause' : 'bi-play'"></i>
              </button>
              <button class="btn btn-sm btn-outline-danger" @click="removeTag(tag)">
                <i class="bi bi-trash"></i>
              </button>
            </td>
          </tr>
          <tr v-if="tags.length === 0">
            <td colspan="6" class="text-center text-muted py-4">暂无标签</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="modal fade" id="tagModal" tabindex="-1">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{ form.id ? '编辑标签' : '新增标签' }}</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <div class="mb-3">
              <label class="form-label">标签名称</label>
              <input v-model.trim="form.name" class="form-control" maxlength="50" />
            </div>
            <div class="mb-3">
              <label class="form-label">类型</label>
              <select v-model.number="form.type" class="form-select">
                <option v-for="item in tagTypes" :key="item.value" :value="item.value">
                  {{ item.label }}
                </option>
              </select>
            </div>
            <div class="mb-3">
              <label class="form-label">排序</label>
              <input v-model.number="form.sortOrder" type="number" class="form-control" min="0" />
            </div>
            <div>
              <label class="form-label">状态</label>
              <select v-model.number="form.status" class="form-select">
                <option :value="1">启用</option>
                <option :value="0">停用</option>
              </select>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-light" data-bs-dismiss="modal">取消</button>
            <button type="button" class="btn btn-primary" @click="saveTag">保存</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { Modal } from 'bootstrap'
import api from '../api'

const tagTypes = [
  { value: 1, label: '景区/景点' },
  { value: 2, label: '攻略' },
  { value: 3, label: '行程' },
  { value: 4, label: '美食' },
  { value: 5, label: '游记' }
]

const tags = ref([])
const query = reactive({ type: '' })
const form = reactive({ id: null, name: '', type: 1, sortOrder: 0, status: 1 })
let modal = null

const getTypeName = (type) => tagTypes.find(item => item.value === type)?.label || '未知'

const loadTags = async () => {
  const res = await api.getTagList({ type: query.type || undefined, includeDisabled: true })
  if (res.code === 200) {
    tags.value = res.data || []
  }
}

const resetQuery = () => {
  query.type = ''
  loadTags()
}

const openModal = (tag) => {
  Object.assign(form, tag ? { ...tag } : { id: null, name: '', type: 1, sortOrder: 0, status: 1 })
  modal.show()
}

const saveTag = async () => {
  if (!form.name) {
    alert('请输入标签名称')
    return
  }
  const payload = {
    name: form.name,
    type: form.type,
    sortOrder: form.sortOrder || 0,
    status: form.status
  }
  const res = form.id ? await api.updateTag(form.id, payload) : await api.addTag(payload)
  if (res.code === 200) {
    modal.hide()
    loadTags()
  }
}

const toggleStatus = async (tag) => {
  const res = await api.updateTagStatus(tag.id, tag.status === 1 ? 0 : 1)
  if (res.code === 200) loadTags()
}

const removeTag = async (tag) => {
  if (!confirm(`确定删除标签「${tag.name}」吗？`)) return
  const res = await api.deleteTag(tag.id)
  if (res.code === 200) loadTags()
}

onMounted(() => {
  modal = new Modal(document.getElementById('tagModal'))
  loadTags()
})
</script>

<style scoped>
.toolbar {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 16px;
}

.toolbar-left {
  display: flex;
  gap: 10px;
}

.toolbar-left .form-select {
  width: 180px;
}

.table-wrapper {
  background: #fff;
  border: 1px solid #e5e7eb;
  padding: 16px;
}

@media (max-width: 768px) {
  .toolbar,
  .toolbar-left {
    flex-direction: column;
  }

  .toolbar-left .form-select,
  .toolbar .btn {
    width: 100%;
  }
}
</style>
