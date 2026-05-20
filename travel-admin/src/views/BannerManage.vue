<template>
  <div class="content-card">
    <div class="search-bar">
      <button class="btn btn-success ms-auto" @click="openModal()">
        <i class="bi bi-plus"></i> 添加轮播图
      </button>
    </div>
    
    <table class="table table-hover">
      <thead>
        <tr>
          <th>ID</th>
          <th>图片</th>
          <th>标题</th>
          <th>链接类型</th>
          <th>排序</th>
          <th>状态</th>
          <th>创建时间</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="banner in banners" :key="banner.id">
          <td>{{ banner.id }}</td>
          <td>
            <img :src="banner.imageUrl" alt="" style="width: 120px; height: 60px; object-fit: cover; border-radius: 4px;">
          </td>
          <td>{{ banner.title || '-' }}</td>
          <td>{{ getLinkTypeName(banner.linkType) }}</td>
          <td>{{ banner.sortOrder }}</td>
          <td>
            <span class="status-badge" :class="banner.status === 1 ? 'active' : 'inactive'">
              {{ banner.status === 1 ? '启用' : '禁用' }}
            </span>
          </td>
          <td>{{ formatDate(banner.createTime) }}</td>
          <td class="action-btns">
            <button class="btn btn-primary btn-sm" @click="openModal(banner)">编辑</button>
            <button 
              class="btn btn-sm" 
              :class="banner.status === 1 ? 'btn-warning' : 'btn-success'"
              @click="toggleStatus(banner)"
            >
              {{ banner.status === 1 ? '禁用' : '启用' }}
            </button>
            <button class="btn btn-danger btn-sm" @click="handleDelete(banner.id)">删除</button>
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
  <div class="modal fade" id="bannerModal" tabindex="-1" ref="modalRef">
    <div class="modal-dialog modal-lg">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">{{ isEdit ? '编辑轮播图' : '添加轮播图' }}</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
        </div>
        <div class="modal-body">
          <div class="mb-3">
            <label class="form-label">标题</label>
            <input type="text" class="form-control" v-model="form.title" placeholder="可选">
          </div>
          <div class="mb-3">
            <label class="form-label">轮播图片 *</label>
            <div class="d-flex align-items-start gap-3">
              <div class="banner-preview" v-if="form.imageUrl">
                <img :src="form.imageUrl" alt="预览" style="max-width: 300px; max-height: 150px; object-fit: cover; border-radius: 8px;">
              </div>
              <div>
                <input type="file" ref="fileInput" accept="image/*" @change="handleFileChange" style="display: none;">
                <button type="button" class="btn btn-outline-primary btn-sm" @click="$refs.fileInput.click()">
                  <i class="bi bi-upload"></i> 上传图片
                </button>
                <div class="mt-2">
                  <input type="text" class="form-control form-control-sm" v-model="form.imageUrl" placeholder="或输入图片URL" style="width: 300px;">
                </div>
              </div>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col-md-6">
              <label class="form-label">链接类型</label>
              <select class="form-select" v-model="form.linkType">
                <option :value="0">无跳转</option>
                <option :value="1">景区详情</option>
                <option :value="2">攻略详情</option>
                <option :value="3">外部链接</option>
              </select>
            </div>
            <div class="col-md-6">
              <label class="form-label">排序</label>
              <input type="number" class="form-control" v-model="form.sortOrder" placeholder="数字越小越靠前">
            </div>
          </div>
          <div class="mb-3" v-if="form.linkType === 1 || form.linkType === 2">
            <label class="form-label">目标ID</label>
            <input type="number" class="form-control" v-model="form.targetId" placeholder="景区或攻略的ID">
          </div>
          <div class="mb-3" v-if="form.linkType === 3">
            <label class="form-label">跳转链接</label>
            <input type="text" class="form-control" v-model="form.linkUrl" placeholder="https://...">
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
import { ref, computed, onMounted } from 'vue'
import { Modal } from 'bootstrap'
import api from '../api'

const banners = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const modalRef = ref(null)
const isEdit = ref(false)
let modal = null

const form = ref({
  id: null,
  title: '',
  imageUrl: '',
  linkType: 0,
  linkUrl: '',
  targetId: null,
  sortOrder: 0
})

const totalPages = computed(() => Math.ceil(total.value / pageSize.value) || 1)

const getLinkTypeName = (type) => {
  const types = { 0: '无跳转', 1: '景区详情', 2: '攻略详情', 3: '外部链接' }
  return types[type] || '未知'
}

const loadData = async () => {
  const res = await api.getBannerList({
    pageNum: pageNum.value,
    pageSize: pageSize.value
  })
  if (res.code === 200) {
    banners.value = res.data.records
    total.value = res.data.total
  }
}

const openModal = (banner = null) => {
  isEdit.value = !!banner
  if (banner) {
    form.value = { ...banner }
  } else {
    form.value = { id: null, title: '', imageUrl: '', linkType: 0, linkUrl: '', targetId: null, sortOrder: 0 }
  }
  if (!modal) {
    modal = new Modal(modalRef.value)
  }
  modal.show()
}

const handleFileChange = async (e) => {
  const file = e.target.files[0]
  if (!file) return
  try {
    const res = await api.uploadFile(file)
    if (res.code === 200) {
      form.value.imageUrl = res.data
    } else {
      alert('上传失败: ' + res.message)
    }
  } catch (err) {
    alert('上传失败')
  }
  e.target.value = ''
}

const handleSubmit = async () => {
  if (!form.value.imageUrl) {
    alert('请上传轮播图片')
    return
  }
  let res
  if (isEdit.value) {
    res = await api.updateBanner(form.value.id, form.value)
  } else {
    res = await api.addBanner(form.value)
  }
  if (res.code === 200) {
    modal.hide()
    loadData()
  } else {
    alert(res.message)
  }
}

const toggleStatus = async (banner) => {
  const newStatus = banner.status === 1 ? 0 : 1
  const res = await api.updateBannerStatus(banner.id, newStatus)
  if (res.code === 200) {
    banner.status = newStatus
  }
}

const handleDelete = async (id) => {
  if (confirm('确定要删除该轮播图吗？')) {
    const res = await api.deleteBanner(id)
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
