<template>
  <div class="content-card">
    <div class="search-bar">
      <input type="text" class="form-control" v-model="searchName" placeholder="搜索景区名称">
      <button class="btn btn-primary" @click="loadData">
        <i class="bi bi-search"></i> 搜索
      </button>
      <button class="btn btn-success ms-auto" @click="openModal()">
        <i class="bi bi-plus"></i> 添加景区
      </button>
    </div>
    
    <table class="table table-hover">
      <thead>
        <tr>
          <th>ID</th>
          <th>封面</th>
          <th>景区名称</th>
          <th>省份</th>
          <th>城市</th>
          <th>等级</th>
          <th>状态</th>
          <th>创建时间</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="scenic in scenics" :key="scenic.id">
          <td>{{ scenic.id }}</td>
          <td>
            <img :src="scenic.coverImage || '/placeholder.jpg'" alt="" style="width: 60px; height: 40px; object-fit: cover; border-radius: 4px;">
          </td>
          <td>{{ scenic.name }}</td>
          <td>{{ scenic.province || '-' }}</td>
          <td>{{ scenic.city || '-' }}</td>
          <td>{{ scenic.level || '-' }}</td>
          <td>
            <span class="status-badge" :class="scenic.status === 1 ? 'active' : 'inactive'">
              {{ scenic.status === 1 ? '上架' : '下架' }}
            </span>
          </td>
          <td>{{ formatDate(scenic.createTime) }}</td>
          <td class="action-btns">
            <button class="btn btn-primary btn-sm" @click="openModal(scenic)">编辑</button>
            <button 
              class="btn btn-sm" 
              :class="scenic.status === 1 ? 'btn-warning' : 'btn-success'"
              @click="toggleStatus(scenic)"
            >
              {{ scenic.status === 1 ? '下架' : '上架' }}
            </button>
            <button class="btn btn-danger btn-sm" @click="handleDelete(scenic.id)">删除</button>
          </td>
        </tr>
      </tbody>
    </table>
    
    <nav class="pagination">
      <ul class="pagination mb-0">
        <li class="page-item" :class="{ disabled: pageNum === 1 }">
          <a class="page-link" href="#" @click.prevent="changePage(pageNum - 1)">上一页</a>
        </li>
        <li class="page-item" v-for="p in totalPages" :key="p" :class="{ active: p === pageNum }">
          <a class="page-link" href="#" @click.prevent="changePage(p)">{{ p }}</a>
        </li>
        <li class="page-item" :class="{ disabled: pageNum === totalPages }">
          <a class="page-link" href="#" @click.prevent="changePage(pageNum + 1)">下一页</a>
        </li>
      </ul>
    </nav>
  </div>
  
  <!-- Modal -->
  <div class="modal fade" id="scenicModal" tabindex="-1" ref="modalRef">
    <div class="modal-dialog modal-lg">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">{{ isEdit ? '编辑景区' : '添加景区' }}</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
        </div>
        <div class="modal-body">
          <div class="row g-3">
            <div class="col-md-6">
              <label class="form-label">景区名称 *</label>
              <input type="text" class="form-control" v-model="form.name" required>
            </div>
            <div class="col-md-3">
              <label class="form-label">省份</label>
              <input type="text" class="form-control" v-model="form.province">
            </div>
            <div class="col-md-3">
              <label class="form-label">城市</label>
              <input type="text" class="form-control" v-model="form.city">
            </div>
            <div class="col-md-6">
              <label class="form-label">地址</label>
              <input type="text" class="form-control" v-model="form.address">
            </div>
            <div class="col-md-3">
              <label class="form-label">等级</label>
              <select class="form-select" v-model="form.level">
                <option value="">请选择</option>
                <option value="5A">5A</option>
                <option value="4A">4A</option>
                <option value="3A">3A</option>
              </select>
            </div>
            <div class="col-md-3">
              <label class="form-label">开放时间</label>
              <input type="text" class="form-control" v-model="form.openTime" placeholder="如: 08:00-18:00">
            </div>
            <div class="col-12">
              <label class="form-label">封面图</label>
              <div class="d-flex align-items-center gap-3">
                <div class="cover-preview" v-if="form.coverImage">
                  <img :src="form.coverImage" alt="封面预览" style="max-width: 200px; max-height: 120px; object-fit: cover; border-radius: 8px;">
                </div>
                <div>
                  <input type="file" ref="fileInput" accept="image/*" @change="handleFileChange" style="display: none;">
                  <button type="button" class="btn btn-outline-primary btn-sm" @click="$refs.fileInput.click()">
                    <i class="bi bi-upload"></i> 上传图片
                  </button>
                  <div class="mt-2">
                    <input type="text" class="form-control form-control-sm" v-model="form.coverImage" placeholder="或输入图片URL" style="width: 300px;">
                  </div>
                </div>
              </div>
            </div>
            <div class="col-12">
              <label class="form-label">标签</label>
              <input type="text" class="form-control" v-model="form.tags" placeholder="多个标签用英文逗号分隔">
            </div>
            <div class="col-12">
              <label class="form-label">景区描述</label>
              <div style="border: 1px solid #ccc; border-radius: 4px;">
                <Toolbar
                  style="border-bottom: 1px solid #ccc"
                  :editor="editorRef"
                  :defaultConfig="toolbarConfig"
                  mode="simple"
                />
                <Editor
                  style="height: 300px; overflow-y: hidden;"
                  v-model="form.description"
                  :defaultConfig="editorConfig"
                  mode="simple"
                  @onCreated="handleCreated"
                />
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
          <button type="button" class="btn btn-primary" @click="handleSubmit">保存</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, shallowRef, onBeforeUnmount } from 'vue'
import { Modal } from 'bootstrap'
import api from '../api'
import '@wangeditor/editor/dist/css/style.css'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'

const scenics = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const searchName = ref('')
const modalRef = ref(null)
const isEdit = ref(false)
let modal = null

const form = ref({
  id: null,
  name: '',
  province: '',
  city: '',
  address: '',
  level: '',
  openTime: '',
  coverImage: '',
  tags: '',
  description: ''
})

// 富文本编辑器
const editorRef = shallowRef()
const toolbarConfig = {}
const editorConfig = { placeholder: '请输入景区描述...' }

const handleCreated = (editor) => {
  editorRef.value = editor
}

// 图片上传
const fileInput = ref(null)
const handleFileChange = async (e) => {
  const file = e.target.files[0]
  if (!file) return
  try {
    const res = await api.uploadFile(file)
    if (res.code === 200) {
      form.value.coverImage = res.data
    } else {
      alert('上传失败: ' + res.message)
    }
  } catch (err) {
    alert('上传失败')
  }
  e.target.value = ''
}

onBeforeUnmount(() => {
  const editor = editorRef.value
  if (editor) editor.destroy()
})

const totalPages = computed(() => Math.ceil(total.value / pageSize.value) || 1)

const loadData = async () => {
  const res = await api.getScenicList({
    pageNum: pageNum.value,
    pageSize: pageSize.value,
    name: searchName.value
  })
  if (res.code === 200) {
    scenics.value = res.data.records
    total.value = res.data.total
  }
}

const openModal = (scenic = null) => {
  isEdit.value = !!scenic
  if (scenic) {
    form.value = { ...scenic }
  } else {
    form.value = { id: null, name: '', province: '', city: '', address: '', level: '', openTime: '', coverImage: '', tags: '', description: '' }
  }
  if (!modal) {
    modal = new Modal(modalRef.value)
  }
  modal.show()
}

const handleSubmit = async () => {
  if (!form.value.name) {
    alert('请输入景区名称')
    return
  }
  let res
  if (isEdit.value) {
    res = await api.updateScenic(form.value.id, form.value)
  } else {
    res = await api.addScenic(form.value)
  }
  if (res.code === 200) {
    modal.hide()
    loadData()
  } else {
    alert(res.message)
  }
}

const toggleStatus = async (scenic) => {
  const newStatus = scenic.status === 1 ? 0 : 1
  const res = await api.updateScenicStatus(scenic.id, newStatus)
  if (res.code === 200) {
    scenic.status = newStatus
  }
}

const handleDelete = async (id) => {
  if (confirm('确定要删除该景区吗？')) {
    const res = await api.deleteScenic(id)
    if (res.code === 200) {
      loadData()
    }
  }
}

const changePage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    pageNum.value = page
    loadData()
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}

onMounted(() => loadData())
</script>
