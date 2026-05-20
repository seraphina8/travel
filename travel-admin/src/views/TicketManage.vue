<template>
  <div class="content-card">
    <div class="search-bar">
      <input type="text" class="form-control" v-model="searchName" placeholder="搜索门票名称">
      <button class="btn btn-primary" @click="loadData">
        <i class="bi bi-search"></i> 搜索
      </button>
      <button class="btn btn-success ms-auto" @click="openModal()">
        <i class="bi bi-plus"></i> 添加门票
      </button>
    </div>
    
    <table class="table table-hover">
      <thead>
        <tr>
          <th>ID</th>
          <th>门票名称</th>
          <th>所属景区</th>
          <th>价格</th>
          <th>原价</th>
          <th>库存</th>
          <th>状态</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="ticket in tickets" :key="ticket.id">
          <td>{{ ticket.id }}</td>
          <td>{{ ticket.name }}</td>
          <td>{{ ticket.scenicName || '-' }}</td>
          <td class="text-danger fw-bold">¥{{ ticket.price }}</td>
          <td class="text-muted text-decoration-line-through">¥{{ ticket.originalPrice }}</td>
          <td>{{ ticket.stock }}</td>
          <td>
            <span class="status-badge" :class="ticket.status === 1 ? 'active' : 'inactive'">
              {{ ticket.status === 1 ? '上架' : '下架' }}
            </span>
          </td>
          <td class="action-btns">
            <button class="btn btn-primary btn-sm" @click="openModal(ticket)">编辑</button>
            <button 
              class="btn btn-sm" 
              :class="ticket.status === 1 ? 'btn-warning' : 'btn-success'"
              @click="toggleStatus(ticket)"
            >
              {{ ticket.status === 1 ? '下架' : '上架' }}
            </button>
            <button class="btn btn-danger btn-sm" @click="handleDelete(ticket.id)">删除</button>
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
  <div class="modal fade" id="ticketModal" tabindex="-1" ref="modalRef">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">{{ isEdit ? '编辑门票' : '添加门票' }}</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
        </div>
        <div class="modal-body">
          <div class="mb-3">
            <label class="form-label">门票名称 *</label>
            <input type="text" class="form-control" v-model="form.name" required>
          </div>
          <div class="mb-3">
            <label class="form-label">所属景区 *</label>
            <div class="scenic-select-wrapper">
              <input 
                type="text" 
                class="form-control" 
                v-model="scenicSearchKey" 
                @input="searchScenic"
                @focus="showScenicDropdown = true"
                placeholder="输入景区名称搜索"
              >
              <div class="scenic-dropdown" v-show="showScenicDropdown && scenicOptions.length > 0">
                <div 
                  class="scenic-option" 
                  v-for="scenic in scenicOptions" 
                  :key="scenic.id"
                  @click="selectScenic(scenic)"
                >
                  {{ scenic.name }} <span class="text-muted">({{ scenic.province }} {{ scenic.city }})</span>
                </div>
              </div>
              <div class="selected-scenic" v-if="form.scenicId && selectedScenicName">
                <span class="badge bg-primary">已选: {{ selectedScenicName }}</span>
              </div>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col-6">
              <label class="form-label">价格 *</label>
              <input type="number" step="0.01" class="form-control" v-model="form.price" required>
            </div>
            <div class="col-6">
              <label class="form-label">原价</label>
              <input type="number" step="0.01" class="form-control" v-model="form.originalPrice">
            </div>
          </div>
          <div class="mb-3">
            <label class="form-label">库存</label>
            <input type="number" class="form-control" v-model="form.stock">
          </div>
          <div class="mb-3">
            <label class="form-label">说明</label>
            <textarea class="form-control" rows="2" v-model="form.description"></textarea>
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

const tickets = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const searchName = ref('')
const modalRef = ref(null)
const isEdit = ref(false)
let modal = null

// 景区搜索相关
const scenicSearchKey = ref('')
const scenicOptions = ref([])
const showScenicDropdown = ref(false)
const selectedScenicName = ref('')
let searchTimer = null

const searchScenic = () => {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(async () => {
    if (scenicSearchKey.value.trim()) {
      const res = await api.getScenicList({
        pageNum: 1,
        pageSize: 20,
        name: scenicSearchKey.value
      })
      if (res.code === 200) {
        scenicOptions.value = res.data.records
      }
    } else {
      scenicOptions.value = []
    }
  }, 300)
}

const selectScenic = (scenic) => {
  form.value.scenicId = scenic.id
  selectedScenicName.value = scenic.name
  scenicSearchKey.value = scenic.name
  showScenicDropdown.value = false
  scenicOptions.value = []
}

// 点击外部关闭下拉框
document.addEventListener('click', (e) => {
  if (!e.target.closest('.scenic-select-wrapper')) {
    showScenicDropdown.value = false
  }
})

const form = ref({
  id: null,
  name: '',
  scenicId: null,
  price: null,
  originalPrice: null,
  stock: 0,
  description: ''
})

const totalPages = computed(() => Math.ceil(total.value / pageSize.value) || 1)

const loadData = async () => {
  const res = await api.getTicketList({
    pageNum: pageNum.value,
    pageSize: pageSize.value,
    name: searchName.value
  })
  if (res.code === 200) {
    tickets.value = res.data.records
    total.value = res.data.total
  }
}

const openModal = (ticket = null) => {
  isEdit.value = !!ticket
  if (ticket) {
    form.value = { ...ticket }
    selectedScenicName.value = ticket.scenicName || ''
    scenicSearchKey.value = ticket.scenicName || ''
  } else {
    form.value = { id: null, name: '', scenicId: null, price: null, originalPrice: null, stock: 0, description: '' }
    selectedScenicName.value = ''
    scenicSearchKey.value = ''
  }
  scenicOptions.value = []
  showScenicDropdown.value = false
  if (!modal) {
    modal = new Modal(modalRef.value)
  }
  modal.show()
}

const handleSubmit = async () => {
  if (!form.value.name || !form.value.scenicId || !form.value.price) {
    alert('请填写必填项')
    return
  }
  let res
  if (isEdit.value) {
    res = await api.updateTicket(form.value.id, form.value)
  } else {
    res = await api.addTicket(form.value)
  }
  if (res.code === 200) {
    modal.hide()
    loadData()
  } else {
    alert(res.message)
  }
}

const toggleStatus = async (ticket) => {
  const newStatus = ticket.status === 1 ? 0 : 1
  const res = await api.updateTicketStatus(ticket.id, newStatus)
  if (res.code === 200) {
    ticket.status = newStatus
  }
}

const handleDelete = async (id) => {
  if (confirm('确定要删除该门票吗？')) {
    const res = await api.deleteTicket(id)
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

onMounted(() => loadData())
</script>

<style scoped>
.scenic-select-wrapper {
  position: relative;
}

.scenic-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 4px;
  max-height: 200px;
  overflow-y: auto;
  z-index: 1000;
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.scenic-option {
  padding: 10px 12px;
  cursor: pointer;
  border-bottom: 1px solid #f0f0f0;
}

.scenic-option:last-child {
  border-bottom: none;
}

.scenic-option:hover {
  background: #f5f5f5;
}

.selected-scenic {
  margin-top: 8px;
}
</style>
