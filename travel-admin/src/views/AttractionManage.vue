<template>
  <div class="content-card">
    <div class="search-bar">
      <input type="text" class="form-control" v-model="searchName" placeholder="搜索景点名称" @keyup.enter="loadData">
      <select class="form-select" v-model="searchProvince" style="width: 150px;">
        <option value="">全部省份</option>
        <option v-for="p in provinces" :key="p" :value="p">{{ p }}</option>
      </select>
      <select class="form-select" v-model="searchCity" style="width: 150px;">
        <option value="">全部城市</option>
      </select>
      <button class="btn btn-primary" @click="loadData">
        <i class="bi bi-search"></i> 搜索
      </button>
      <button class="btn btn-outline-secondary" @click="resetSearch">
        <i class="bi bi-arrow-counterclockwise"></i> 重置
      </button>
      <button class="btn btn-success ms-auto" @click="openModal()">
        <i class="bi bi-plus"></i> 添加景点
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
          <th>景点名称</th>
          <th width="80">省份</th>
          <th width="80">城市</th>
          <th width="80">坐标</th>
          <th width="80">浏览量</th>
          <th width="80">收藏数</th>
          <th width="160">创建时间</th>
          <th width="190">操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in attractions" :key="item.id">
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
            <span class="badge bg-info" v-if="item.longitude && item.latitude" :title="`${item.longitude}, ${item.latitude}`">
              <i class="bi bi-geo-alt"></i> 有
            </span>
            <span class="badge bg-secondary" v-else>无</span>
          </td>
          <td>{{ item.viewCount || 0 }}</td>
          <td>{{ item.collectCount || 0 }}</td>
          <td>{{ formatDate(item.createTime) }}</td>
          <td class="action-btns">
            <button class="btn btn-info btn-sm" @click="viewDetail(item)">查看</button>
            <button class="btn btn-primary btn-sm" @click="openModal(item)">编辑</button>
            <button class="btn btn-danger btn-sm" @click="handleDelete(item.id)">删除</button>
          </td>
        </tr>
      </tbody>
    </table>
    
    <div class="text-center text-muted py-4" v-if="attractions.length === 0">
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
  <div class="modal fade" id="attractionModal" tabindex="-1" ref="modalRef">
    <div class="modal-dialog modal-lg">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">{{ isEdit ? '编辑景点' : '添加景点' }}</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
        </div>
        <div class="modal-body">
          <div class="row g-3">
            <div class="col-md-6">
              <label class="form-label">景点名称 *</label>
              <input type="text" class="form-control" v-model="form.name" required>
            </div>
            <div class="col-md-6">
              <label class="form-label">所属景区</label>
              <select class="form-select" v-model="form.scenicId">
                <option value="">不关联景区</option>
                <option v-for="s in scenicList" :key="s.id" :value="s.id">{{ s.name }}</option>
              </select>
            </div>
            <div class="col-md-3">
              <label class="form-label">省份</label>
              <select class="form-select" v-model="form.province">
                <option value="">请选择</option>
                <option v-for="p in provinces" :key="p" :value="p">{{ p }}</option>
              </select>
            </div>
            <div class="col-md-3">
              <label class="form-label">城市</label>
              <input type="text" class="form-control" v-model="form.city">
            </div>
            <div class="col-md-3">
              <label class="form-label">经度</label>
              <input type="number" step="0.000001" class="form-control" v-model="form.longitude" placeholder="如: 116.397428">
            </div>
            <div class="col-md-3">
              <label class="form-label">纬度</label>
              <input type="number" step="0.000001" class="form-control" v-model="form.latitude" placeholder="如: 39.90923">
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
              <label class="form-label">景点描述</label>
              <textarea class="form-control" v-model="form.description" rows="4" placeholder="请输入景点描述..."></textarea>
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
          <h5 class="modal-title"><i class="bi bi-info-circle text-info"></i> 景点详情</h5>
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
                  <div class="text-muted small">浏览量</div>
                  <div class="fw-bold text-primary">{{ detailData.viewCount || 0 }}</div>
                </div>
                <div class="col-4">
                  <div class="text-muted small">收藏数</div>
                  <div class="fw-bold text-danger">{{ detailData.collectCount || 0 }}</div>
                </div>
                <div class="col-4">
                  <div class="text-muted small">创建时间</div>
                  <div class="small">{{ formatDate(detailData.createTime) }}</div>
                </div>
              </div>
              <div v-if="detailData.longitude && detailData.latitude" class="mb-2">
                <span class="badge bg-info"><i class="bi bi-pin-map"></i> 坐标: {{ detailData.longitude }}, {{ detailData.latitude }}</span>
              </div>
            </div>
          </div>
          <div class="mt-3" v-if="detailData.description">
            <h6>景点描述</h6>
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

const attractions = ref([])
const scenicList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const searchName = ref('')
const searchProvince = ref('')
const searchCity = ref('')
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
  scenicId: '',
  province: '',
  city: '',
  longitude: '',
  latitude: '',
  coverImage: '',
  tags: '',
  description: ''
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
  
  const res = await api.getAttractionList(params)
  if (res.code === 200) {
    attractions.value = res.data.records || []
    total.value = res.data.total || 0
  }
}

const loadScenicList = async () => {
  const res = await api.getScenicList({ pageSize: 100 })
  if (res.code === 200) {
    scenicList.value = res.data.records || []
  }
}

const openModal = (item = null) => {
  isEdit.value = !!item
  if (item) {
    form.value = { ...item }
  } else {
    form.value = { id: null, name: '', scenicId: '', province: '', city: '', longitude: '', latitude: '', coverImage: '', tags: '', description: '' }
  }
  if (!modal) {
    modal = new Modal(modalRef.value)
  }
  modal.show()
}

const handleSubmit = async () => {
  if (!form.value.name) {
    alert('请输入景点名称')
    return
  }
  let res
  if (isEdit.value) {
    res = await api.updateAttraction(form.value.id, form.value)
  } else {
    res = await api.addAttraction(form.value)
  }
  if (res.code === 200) {
    modal.hide()
    loadData()
  } else {
    alert(res.message)
  }
}

const handleDelete = async (id) => {
  if (confirm('确定要删除该景点吗？')) {
    try {
      const res = await api.deleteAttraction(id)
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

const changePage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    pageNum.value = page
    loadData()
  }
}

const resetSearch = () => {
  searchName.value = ''
  searchProvince.value = ''
  searchCity.value = ''
  pageNum.value = 1
  loadData()
}

const viewDetail = async (item) => {
  detailData.value = item
  try {
    const res = await api.getAttraction(item.id)
    if (res.code === 200 && res.data) {
      detailData.value = res.data
      item.viewCount = res.data.viewCount
    }
  } catch (e) {
    // 查看详情失败时仍展示当前行数据
  }
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
  loadScenicList()
})
</script>
