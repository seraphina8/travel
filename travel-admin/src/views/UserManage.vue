<template>
  <div class="content-card">
    <div class="search-bar">
      <input type="text" class="form-control" v-model="searchUsername" placeholder="搜索用户名">
      <select class="form-select" style="width: 150px;" v-model="searchStatus">
        <option value="">全部状态</option>
        <option value="1">启用</option>
        <option value="0">禁用</option>
      </select>
      <button class="btn btn-primary" @click="loadData">
        <i class="bi bi-search"></i> 搜索
      </button>
    </div>
    
    <table class="table table-hover">
      <thead>
        <tr>
          <th>ID</th>
          <th>用户名</th>
          <th>昵称</th>
          <th>手机号</th>
          <th>邮箱</th>
          <th>状态</th>
          <th>注册时间</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="user in users" :key="user.id">
          <td>{{ user.id }}</td>
          <td>{{ user.username }}</td>
          <td>{{ user.nickname || '-' }}</td>
          <td>{{ user.phone || '-' }}</td>
          <td>{{ user.email || '-' }}</td>
          <td>
            <span class="status-badge" :class="user.status === 1 ? 'active' : 'inactive'">
              {{ user.status === 1 ? '启用' : '禁用' }}
            </span>
          </td>
          <td>{{ formatDate(user.createTime) }}</td>
          <td class="action-btns">
            <button 
              class="btn btn-sm" 
              :class="user.status === 1 ? 'btn-warning' : 'btn-success'"
              @click="toggleStatus(user)"
            >
              {{ user.status === 1 ? '禁用' : '启用' }}
            </button>
            <button class="btn btn-danger btn-sm" @click="handleDelete(user.id)">删除</button>
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

const users = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const searchUsername = ref('')
const searchStatus = ref('')

const totalPages = computed(() => Math.ceil(total.value / pageSize.value) || 1)

const loadData = async () => {
  const res = await api.getUserList({
    pageNum: pageNum.value,
    pageSize: pageSize.value,
    username: searchUsername.value,
    status: searchStatus.value || undefined
  })
  if (res.code === 200) {
    users.value = res.data.records
    total.value = res.data.total
  }
}

const toggleStatus = async (user) => {
  const newStatus = user.status === 1 ? 0 : 1
  const res = await api.updateUserStatus(user.id, newStatus)
  if (res.code === 200) {
    user.status = newStatus
  }
}

const handleDelete = async (id) => {
  if (confirm('确定要删除该用户吗？')) {
    const res = await api.deleteUser(id)
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
