<template>
  <div class="content-card">
    <div class="search-bar">
      <input type="text" class="form-control" v-model="searchOrderNo" placeholder="搜索订单号">
      <select class="form-select" style="width: 150px;" v-model="searchStatus">
        <option value="">全部状态</option>
        <option value="0">待支付</option>
        <option value="1">已支付</option>
        <option value="2">已使用</option>
        <option value="3">已取消</option>
        <option value="4">已退款</option>
      </select>
      <button class="btn btn-primary" @click="loadData">
        <i class="bi bi-search"></i> 搜索
      </button>
    </div>
    
    <table class="table table-hover">
      <thead>
        <tr>
          <th>订单号</th>
          <th>用户</th>
          <th>景区</th>
          <th>门票</th>
          <th>数量</th>
          <th>金额</th>
          <th>游玩日期</th>
          <th>状态</th>
          <th>下单时间</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="order in orders" :key="order.id">
          <td>{{ order.orderNo }}</td>
          <td>{{ order.username || '-' }}</td>
          <td>{{ order.scenicName || '-' }}</td>
          <td>{{ order.ticketName || '-' }}</td>
          <td>{{ order.quantity }}</td>
          <td class="text-danger fw-bold">¥{{ order.totalAmount }}</td>
          <td>{{ order.visitDate }}</td>
          <td>
            <span class="status-badge" :class="getStatusClass(order.status)">
              {{ getStatusText(order.status) }}
            </span>
          </td>
          <td>{{ formatDate(order.createTime) }}</td>
          <td class="action-btns">
            <button class="btn btn-success btn-sm" v-if="order.status === 0" @click="handlePay(order)">确认支付</button>
            <button class="btn btn-info btn-sm" v-if="order.status === 1" @click="handleUse(order)">确认使用</button>
            <button class="btn btn-warning btn-sm" v-if="order.status === 0" @click="handleCancel(order)">取消</button>
            <button class="btn btn-danger btn-sm" @click="handleDelete(order.id)">删除</button>
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

const orders = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const searchOrderNo = ref('')
const searchStatus = ref('')

const totalPages = computed(() => Math.ceil(total.value / pageSize.value) || 1)

const statusMap = {
  0: { text: '待支付', class: 'bg-warning text-dark' },
  1: { text: '已支付', class: 'bg-success' },
  2: { text: '已使用', class: 'bg-info' },
  3: { text: '已取消', class: 'bg-secondary' },
  4: { text: '已退款', class: 'bg-danger' }
}

const getStatusText = (status) => statusMap[status]?.text || '未知'
const getStatusClass = (status) => statusMap[status]?.class || ''

const loadData = async () => {
  const res = await api.getOrderList({
    pageNum: pageNum.value,
    pageSize: pageSize.value,
    orderNo: searchOrderNo.value,
    status: searchStatus.value || undefined
  })
  if (res.code === 200) {
    orders.value = res.data.records
    total.value = res.data.total
  }
}

const handlePay = async (order) => {
  if (confirm('确认该订单已支付？')) {
    const res = await api.updateOrderStatus(order.id, 1)
    if (res.code === 200) loadData()
  }
}

const handleUse = async (order) => {
  if (confirm('确认该订单已使用？')) {
    const res = await api.updateOrderStatus(order.id, 2)
    if (res.code === 200) loadData()
  }
}

const handleCancel = async (order) => {
  if (confirm('确定要取消该订单吗？')) {
    const res = await api.updateOrderStatus(order.id, 3)
    if (res.code === 200) loadData()
  }
}

const handleDelete = async (id) => {
  if (confirm('确定要删除该订单吗？')) {
    const res = await api.deleteOrder(id)
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
