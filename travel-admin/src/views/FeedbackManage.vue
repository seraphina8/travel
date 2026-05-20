<template>
  <div class="content-card">
    <div class="search-bar">
      <select class="form-select" style="width: 150px;" v-model="searchStatus">
        <option value="">全部状态</option>
        <option value="0">待处理</option>
        <option value="1">已回复</option>
      </select>
      <button class="btn btn-primary" @click="loadData">
        <i class="bi bi-search"></i> 搜索
      </button>
    </div>
    
    <table class="table table-hover">
      <thead>
        <tr>
          <th>ID</th>
          <th>用户</th>
          <th>反馈内容</th>
          <th>回复内容</th>
          <th>状态</th>
          <th>提交时间</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="feedback in feedbacks" :key="feedback.id">
          <td>{{ feedback.id }}</td>
          <td>{{ feedback.username || '-' }}</td>
          <td>
            <div class="text-truncate" style="max-width: 200px;" :title="feedback.content">
              {{ feedback.content }}
            </div>
          </td>
          <td>
            <div class="text-truncate" style="max-width: 200px;" :title="feedback.reply">
              {{ feedback.reply || '-' }}
            </div>
          </td>
          <td>
            <span class="status-badge" :class="feedback.status === 1 ? 'active' : 'inactive'">
              {{ feedback.status === 1 ? '已回复' : '待处理' }}
            </span>
          </td>
          <td>{{ formatDate(feedback.createTime) }}</td>
          <td class="action-btns">
            <button class="btn btn-primary btn-sm" @click="openReplyModal(feedback)">
              {{ feedback.status === 1 ? '查看' : '回复' }}
            </button>
            <button class="btn btn-danger btn-sm" @click="handleDelete(feedback.id)">删除</button>
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
  
  <!-- Reply Modal -->
  <div class="modal fade" id="replyModal" tabindex="-1" ref="modalRef">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">反馈详情</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
        </div>
        <div class="modal-body">
          <div class="mb-3">
            <label class="form-label fw-bold">用户反馈</label>
            <div class="p-3 bg-light rounded">{{ currentFeedback.content }}</div>
          </div>
          <div class="mb-3">
            <label class="form-label fw-bold">回复内容</label>
            <textarea 
              class="form-control" 
              rows="4" 
              v-model="replyContent"
              :disabled="currentFeedback.status === 1"
              placeholder="请输入回复内容..."
            ></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">关闭</button>
          <button 
            type="button" 
            class="btn btn-primary" 
            @click="handleReply"
            v-if="currentFeedback.status !== 1"
          >
            提交回复
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Modal } from 'bootstrap'
import api from '../api'

const feedbacks = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const searchStatus = ref('')
const modalRef = ref(null)
const currentFeedback = ref({})
const replyContent = ref('')
let modal = null

const totalPages = computed(() => Math.ceil(total.value / pageSize.value) || 1)

const loadData = async () => {
  const res = await api.getFeedbackList({
    pageNum: pageNum.value,
    pageSize: pageSize.value,
    status: searchStatus.value || undefined
  })
  if (res.code === 200) {
    feedbacks.value = res.data.records
    total.value = res.data.total
  }
}

const openReplyModal = (feedback) => {
  currentFeedback.value = feedback
  replyContent.value = feedback.reply || ''
  if (!modal) {
    modal = new Modal(modalRef.value)
  }
  modal.show()
}

const handleReply = async () => {
  if (!replyContent.value.trim()) {
    alert('请输入回复内容')
    return
  }
  const res = await api.replyFeedback(currentFeedback.value.id, replyContent.value)
  if (res.code === 200) {
    modal.hide()
    loadData()
  } else {
    alert(res.message)
  }
}

const handleDelete = async (id) => {
  if (confirm('确定要删除该反馈吗？')) {
    const res = await api.deleteFeedback(id)
    if (res.code === 200) loadData()
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
