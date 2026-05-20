<template>
  <div class="content-card">
    <div class="search-bar">
      <input type="text" class="form-control" v-model="searchTitle" placeholder="搜索攻略标题">
      <select class="form-select" style="width: 150px;" v-model="searchStatus">
        <option value="">全部状态</option>
        <option value="0">待审核</option>
        <option value="1">已发布</option>
        <option value="2">已拒绝</option>
      </select>
      <button class="btn btn-primary" @click="loadData">
        <i class="bi bi-search"></i> 搜索
      </button>
    </div>
    
    <table class="table table-hover">
      <thead>
        <tr>
          <th>ID</th>
          <th>封面</th>
          <th>标题</th>
          <th>作者</th>
          <th>标签</th>
          <th>浏览量</th>
          <th>点赞数</th>
          <th>状态</th>
          <th>发布时间</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="strategy in strategies" :key="strategy.id">
          <td>{{ strategy.id }}</td>
          <td>
            <img :src="strategy.coverImage || '/placeholder.jpg'" alt="" style="width: 60px; height: 40px; object-fit: cover; border-radius: 4px;">
          </td>
          <td>{{ strategy.title }}</td>
          <td>{{ strategy.username || '-' }}</td>
          <td>{{ strategy.tags || '-' }}</td>
          <td>{{ strategy.viewCount }}</td>
          <td>{{ strategy.likeCount }}</td>
          <td>
            <span class="status-badge" :class="getStatusClass(strategy.status)">
              {{ getStatusText(strategy.status) }}
            </span>
          </td>
          <td>{{ formatDate(strategy.createTime) }}</td>
          <td class="action-btns">
            <button class="btn btn-success btn-sm" v-if="strategy.status === 0" @click="handleApprove(strategy)">通过</button>
            <button class="btn btn-warning btn-sm" v-if="strategy.status === 0" @click="handleReject(strategy)">拒绝</button>
            <button class="btn btn-danger btn-sm" @click="handleDelete(strategy.id)">删除</button>
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
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import api from '../api'

const strategies = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const searchTitle = ref('')
const searchStatus = ref('')

const totalPages = computed(() => Math.ceil(total.value / pageSize.value) || 1)

const statusMap = {
  0: { text: '待审核', class: 'bg-warning text-dark' },
  1: { text: '已发布', class: 'bg-success' },
  2: { text: '已拒绝', class: 'bg-danger' }
}

const getStatusText = (status) => statusMap[status]?.text || '未知'
const getStatusClass = (status) => statusMap[status]?.class || ''

const loadData = async () => {
  const res = await api.getStrategyList({
    pageNum: pageNum.value,
    pageSize: pageSize.value,
    title: searchTitle.value,
    status: searchStatus.value || undefined
  })
  if (res.code === 200) {
    strategies.value = res.data.records
    total.value = res.data.total
  }
}

const handleApprove = async (strategy) => {
  if (confirm('确定审核通过该攻略？')) {
    const res = await api.updateStrategyStatus(strategy.id, 1)
    if (res.code === 200) loadData()
  }
}

const handleReject = async (strategy) => {
  if (confirm('确定拒绝该攻略？')) {
    const res = await api.updateStrategyStatus(strategy.id, 2)
    if (res.code === 200) loadData()
  }
}

const handleDelete = async (id) => {
  if (confirm('确定要删除该攻略吗？')) {
    const res = await api.deleteStrategy(id)
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

<style scoped>
.status-badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  color: #fff;
}
</style>
