<template>
  <div class="content-card">
    <div class="search-bar">
      <input type="text" class="form-control" v-model="searchName" placeholder="搜索美食名称" @keyup.enter="loadData">
      <select class="form-select" v-model="searchProvince" style="width: 150px;">
        <option value="">全部省份</option>
        <option v-for="p in provinces" :key="p" :value="p">{{ p }}</option>
      </select>
      <select class="form-select" v-model="searchStatus" style="width: 120px;">
        <option value="">全部状态</option>
        <option value="1">已审核</option>
        <option value="0">待审核</option>
      </select>
      <button class="btn btn-primary" @click="loadData">
        <i class="bi bi-search"></i> 搜索
      </button>
      <button class="btn btn-outline-secondary" @click="resetSearch">
        <i class="bi bi-arrow-counterclockwise"></i> 重置
      </button>
      <button class="btn btn-success ms-auto" @click="openModal()">
        <i class="bi bi-plus"></i> 添加美食
      </button>
    </div>
    
    <div class="table-info mb-2">
      <span class="text-muted">共 {{ total }} 条数据</span>
    </div>
    
    <table class="table table-hover">
      <thead>
        <tr>
          <th width="60">ID</th>
          <th width="80">封面</th>
          <th>美食名称</th>
          <th width="80">省份</th>
          <th width="80">城市</th>
          <th width="80">状态</th>
          <th width="100">发布者</th>
          <th width="160">创建时间</th>
          <th width="180">操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in foods" :key="item.id">
          <td>{{ item.id }}</td>
          <td>
            <img :src="item.coverImage || 'https://via.placeholder.com/60x40'" alt="" style="width: 60px; height: 40px; object-fit: cover; border-radius: 4px; cursor: pointer;" @click="previewImage(item.coverImage)">
          </td>
          <td>
            <div class="fw-bold">{{ item.name }}</div>
            <small class="text-muted" v-if="item.description">{{ item.description.substring(0, 30) }}{{ item.description.length > 30 ? '...' : '' }}</small>
          </td>
          <td>{{ item.province || '-' }}</td>
          <td>{{ item.city || '-' }}</td>
          <td>
            <span class="badge" :class="item.status === 1 ? 'bg-success' : 'bg-warning'">
              {{ item.status === 1 ? '已审核' : '待审核' }}
            </span>
          </td>
          <td>
            <span v-if="item.userId">用户{{ item.userId }}</span>
            <span v-else class="text-muted">管理员</span>
          </td>
          <td>{{ formatDate(item.createTime) }}</td>
          <td class="action-btns">
            <button class="btn btn-info btn-sm" @click="viewDetail(item)" title="查看详情">
              <i class="bi bi-eye"></i>
            </button>
            <button class="btn btn-success btn-sm" v-if="item.status === 0" @click="handleAudit(item.id, 1)" title="审核通过">
              <i class="bi bi-check-lg"></i>
            </button>
            <button class="btn btn-warning btn-sm" v-if="item.status === 1" @click="handleAudit(item.id, 0)" title="取消审核">
              <i class="bi bi-x-lg"></i>
            </button>
            <button class="btn btn-primary btn-sm" @click="openModal(item)" title="编辑">
              <i class="bi bi-pencil"></i>
            </button>
            <button class="btn btn-danger btn-sm" @click="handleDelete(item.id)" title="删除">
              <i class="bi bi-trash"></i>
            </button>
          </td>
        </tr>
      </tbody>
    </table>
    
    <div class="text-center text-muted py-4" v-if="foods.length === 0">
      暂无数据
    </div>
    
    <nav class="pagination" v-if="total > pageSize">
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
  <div class="modal fade" id="foodModal" tabindex="-1" ref="modalRef">
    <div class="modal-dialog modal-lg">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">{{ isEdit ? '编辑美食' : '添加美食' }}</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
        </div>
        <div class="modal-body">
          <div class="row g-3">
            <div class="col-md-6">
              <label class="form-label">美食名称 *</label>
              <input type="text" class="form-control" v-model="form.name" required>
            </div>
            <div class="col-md-6">
              <label class="form-label">状态</label>
              <select class="form-select" v-model="form.status">
                <option :value="1">已审核</option>
                <option :value="0">待审核</option>
              </select>
            </div>
            <div class="col-md-4">
              <label class="form-label">省份</label>
              <select class="form-select" v-model="form.province">
                <option value="">请选择</option>
                <option v-for="p in provinces" :key="p" :value="p">{{ p }}</option>
              </select>
            </div>
            <div class="col-md-4">
              <label class="form-label">城市</label>
              <input type="text" class="form-control" v-model="form.city">
            </div>
            <div class="col-md-4">
              <label class="form-label">详细地址</label>
              <input type="text" class="form-control" v-model="form.address" placeholder="如: XX路XX号">
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
              <label class="form-label">美食描述</label>
              <textarea class="form-control" v-model="form.description" rows="4" placeholder="请输入美食描述..."></textarea>
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
  
  <!-- 详情弹窗 -->
  <div class="modal fade" id="detailModal" tabindex="-1" ref="detailModalRef">
    <div class="modal-dialog modal-lg">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title"><i class="bi bi-info-circle text-info"></i> 美食详情</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
        </div>
        <div class="modal-body" v-if="detailData">
          <div class="row">
            <div class="col-md-4" v-if="detailData.coverImage">
              <img :src="detailData.coverImage" class="img-fluid rounded" style="max-height: 200px; object-fit: cover;">
            </div>
            <div :class="detailData.coverImage ? 'col-md-8' : 'col-12'">
              <h4>{{ detailData.name }}</h4>
              <p class="text-muted mb-2">
                <i class="bi bi-geo-alt"></i> {{ detailData.province }} · {{ detailData.city || '未知城市' }}
              </p>
              <div class="row mb-3">
                <div class="col-4">
                  <div class="text-muted small">状态</div>
                  <span class="badge" :class="detailData.status === 1 ? 'bg-success' : 'bg-warning'">
                    {{ detailData.status === 1 ? '已审核' : '待审核' }}
                  </span>
                </div>
                <div class="col-4">
                  <div class="text-muted small">发布者</div>
                  <div class="small">{{ detailData.userId ? '用户' + detailData.userId : '管理员' }}</div>
                </div>
                <div class="col-4">
                  <div class="text-muted small">创建时间</div>
                  <div class="small">{{ formatDate(detailData.createTime) }}</div>
                </div>
              </div>
              <div v-if="detailData.address" class="mb-2">
                <span class="badge bg-info"><i class="bi bi-pin-map"></i> {{ detailData.address }}</span>
              </div>
            </div>
          </div>
          <div class="mt-3" v-if="detailData.description">
            <h6>美食描述</h6>
            <p class="text-muted" style="white-space: pre-wrap;">{{ detailData.description }}</p>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">关闭</button>
          <button type="button" class="btn btn-primary" @click="editFromDetail">编辑</button>
        </div>
      </div>
    </div>
  </div>
  
  <!-- 图片预览弹窗 -->
  <div class="modal fade" id="imageModal" tabindex="-1" ref="imageModalRef">
    <div class="modal-dialog modal-lg modal-dialog-centered">
      <div class="modal-content bg-transparent border-0">
        <div class="text-center">
          <img :src="previewImageUrl" class="img-fluid" style="max-height: 80vh;">
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Modal } from 'bootstrap'
import api from '../api'

const foods = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const searchName = ref('')
const searchProvince = ref('')
const searchStatus = ref('')
const modalRef = ref(null)
const detailModalRef = ref(null)
const imageModalRef = ref(null)
const isEdit = ref(false)
const detailData = ref(null)
const previewImageUrl = ref('')
let modal = null
let detailModal = null
let imageModal = null

const provinces = [
  '北京', '天津', '河北', '山西', '内蒙古', '辽宁', '吉林', '黑龙江',
  '上海', '江苏', '浙江', '安徽', '福建', '江西', '山东', '河南',
  '湖北', '湖南', '广东', '广西', '海南', '重庆', '四川', '贵州',
  '云南', '西藏', '陕西', '甘肃', '青海', '宁夏', '新疆'
]

const form = ref({
  id: null,
  name: '',
  province: '',
  city: '',
  address: '',
  coverImage: '',
  tags: '',
  description: '',
  status: 1
})

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

const totalPages = computed(() => Math.ceil(total.value / pageSize.value) || 1)

const loadData = async () => {
  const params = {
    pageNum: pageNum.value,
    pageSize: pageSize.value
  }
  if (searchName.value) params.keyword = searchName.value
  if (searchProvince.value) params.province = searchProvince.value
  if (searchStatus.value !== '') params.status = searchStatus.value
  
  const res = await api.getFoodList(params)
  if (res.code === 200) {
    foods.value = res.data.records || []
    total.value = res.data.total || 0
  }
}

const openModal = (item = null) => {
  isEdit.value = !!item
  if (item) {
    form.value = { ...item }
  } else {
    form.value = { id: null, name: '', province: '', city: '', address: '', coverImage: '', tags: '', description: '', status: 1 }
  }
  if (!modal) {
    modal = new Modal(modalRef.value)
  }
  modal.show()
}

const handleSubmit = async () => {
  if (!form.value.name) {
    alert('请输入美食名称')
    return
  }
  let res
  if (isEdit.value) {
    res = await api.updateFood(form.value.id, form.value)
  } else {
    res = await api.addFood(form.value)
  }
  if (res.code === 200) {
    modal.hide()
    loadData()
  } else {
    alert(res.message)
  }
}

const handleDelete = async (id) => {
  if (confirm('确定要删除该美食吗？')) {
    try {
      const res = await api.deleteFood(id)
      if (res.code === 200) {
        alert('删除成功')
        loadData()
      } else {
        alert('删除失败: ' + (res.message || '未知错误'))
      }
    } catch (err) {
      console.error('删除失败', err)
      alert('删除失败: ' + (err.message || '网络错误'))
    }
  }
}

const handleAudit = async (id, status) => {
  const msg = status === 1 ? '确定审核通过该美食吗？' : '确定取消审核该美食吗？'
  if (confirm(msg)) {
    try {
      const res = await api.auditFood(id, status)
      if (res.code === 200) {
        alert('操作成功')
        loadData()
      } else {
        alert('操作失败: ' + (res.message || '未知错误'))
      }
    } catch (err) {
      alert('操作失败')
    }
  }
}

const changePage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    pageNum.value = page
    loadData()
  }
}

const resetSearch = () => {
  searchName.value = ''
  searchProvince.value = ''
  searchStatus.value = ''
  pageNum.value = 1
  loadData()
}

const viewDetail = (item) => {
  detailData.value = item
  if (!detailModal) {
    detailModal = new Modal(detailModalRef.value)
  }
  detailModal.show()
}

const editFromDetail = () => {
  detailModal.hide()
  openModal(detailData.value)
}

const previewImage = (url) => {
  if (!url) return
  previewImageUrl.value = url
  if (!imageModal) {
    imageModal = new Modal(imageModalRef.value)
  }
  imageModal.show()
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}

onMounted(() => {
  loadData()
})
</script>
