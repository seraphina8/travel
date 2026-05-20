<template>
  <div class="page-header">
    <div class="container">
      <h1>我的订单</h1>
    </div>
  </div>
  
  <div class="container py-5">
    <!-- Tabs -->
    <div class="tabs-card">
      <ul class="nav-tabs">
        <li class="nav-item">
          <a class="tab-link" :class="{ active: status === '' }" href="#" @click.prevent="filterStatus('')">全部订单</a>
        </li>
        <li class="nav-item">
          <a class="tab-link" :class="{ active: status === '0' }" href="#" @click.prevent="filterStatus('0')">待支付</a>
        </li>
        <li class="nav-item">
          <a class="tab-link" :class="{ active: status === '1' }" href="#" @click.prevent="filterStatus('1')">已支付</a>
        </li>
        <li class="nav-item">
          <a class="tab-link" :class="{ active: status === '2' }" href="#" @click.prevent="filterStatus('2')">已使用</a>
        </li>
      </ul>
    </div>
    
    <!-- Order List -->
    <div class="order-card" v-for="order in orders" :key="order.id">
      <div class="order-header">
        <div>
          <span class="order-no">订单号：{{ order.orderNo }}</span>
          <span class="order-time">{{ formatDate(order.createTime) }}</span>
        </div>
        <span class="order-status" :class="getStatusClass(order.status)">{{ getStatusText(order.status) }}</span>
      </div>
      
      <div class="order-content">
        <div class="order-img">
          <img :src="order.scenicCover || defaultImage" alt="" @error="handleImageError">
        </div>
        <div class="order-info">
          <h6 class="order-name">{{ order.scenicName || '景区名称' }}</h6>
          <p class="order-detail">{{ order.ticketName || '门票' }} x {{ order.quantity }}</p>
          <p class="order-detail">游玩日期：{{ order.visitDate }}</p>
          <p class="order-detail" v-if="order.payType">
            <i class="bi bi-credit-card"></i> {{ order.payType }}
            <span v-if="order.alipayTradeNo" class="trade-no">交易号：{{ order.alipayTradeNo }}</span>
          </p>
        </div>
        <div class="order-price">
          <div class="price">¥{{ order.totalAmount }}</div>
        </div>
        <div class="order-actions">
          <button class="action-btn pay" v-if="order.status === 0" @click="payOrder(order)">
            立即支付
          </button>
          <button class="action-btn cancel" v-if="order.status === 0" @click="cancelOrder(order)">
            取消订单
          </button>
          <button class="action-btn voucher" v-if="order.status === 1" @click="showQrCode(order)">
            查看凭证
          </button>
        </div>
      </div>
    </div>
    
    <!-- 凭证二维码弹窗 -->
    <div class="modal fade" id="qrModal" tabindex="-1" ref="qrModalRef">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">入园凭证</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <div class="qr-container">
              <img :src="qrCodeUrl" alt="入园二维码">
            </div>
            <h6 class="qr-scenic">{{ currentOrder?.scenicName }}</h6>
            <p class="qr-info">订单号：{{ currentOrder?.orderNo }}</p>
            <p class="qr-info">{{ currentOrder?.ticketName }} x {{ currentOrder?.quantity }}</p>
            <p class="qr-info">游玩日期：{{ currentOrder?.visitDate }}</p>
            <div class="qr-tip">
              <i class="bi bi-info-circle"></i>
              请在入园时出示此二维码，工作人员扫码验证后即可入园
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Empty -->
    <div class="empty-state" v-if="orders.length === 0">
      <i class="bi bi-bag-x"></i>
      <p>暂无订单记录</p>
      <router-link to="/scenic" class="empty-btn">去逛逛</router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Modal } from 'bootstrap'
import api from '../api'

const orders = ref([])
const status = ref('')
const defaultImage = 'https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=200'
const qrModalRef = ref(null)
const currentOrder = ref(null)
const qrCodeUrl = ref('')
let qrModal = null

const statusMap = {
  0: { text: '待支付', class: 'pending' },
  1: { text: '已支付', class: 'paid' },
  2: { text: '已使用', class: 'used' },
  3: { text: '已取消', class: 'cancelled' },
  4: { text: '已退款', class: 'refunded' }
}

const getStatusText = (s) => statusMap[s]?.text || '未知'
const getStatusClass = (s) => statusMap[s]?.class || 'cancelled'

const loadData = async () => {
  try {
    const res = await api.getMyOrders({ status: status.value || undefined })
    if (res.code === 200) {
      orders.value = res.data.records || res.data || []
    }
  } catch (e) {
    console.error('加载订单失败', e)
  }
}

const filterStatus = (s) => {
  status.value = s
  loadData()
}

const payOrder = async (order) => {
  try {
    const res = await api.alipayOrder(order.id)
    if (res.code === 200 && res.data?.form) {
      const div = document.createElement('div')
      div.innerHTML = res.data.form
      document.body.appendChild(div)
      const form = div.querySelector('form')
      if (form) {
        form.submit()
      }
    } else {
      alert(res.message || '支付发起失败')
    }
  } catch (e) {
    alert('支付失败，请稍后重试')
  }
}

const cancelOrder = async (order) => {
  if (confirm('确定要取消该订单吗？')) {
    try {
      const res = await api.cancelOrder(order.id)
      if (res.code === 200) {
        loadData()
      }
    } catch (e) {
      alert('取消失败')
    }
  }
}

const showQrCode = (order) => {
  currentOrder.value = order
  const qrContent = encodeURIComponent(`ORDER:${order.orderNo}|SCENIC:${order.scenicId}|DATE:${order.visitDate}`)
  qrCodeUrl.value = `https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=${qrContent}`
  
  if (!qrModal) {
    qrModal = new Modal(qrModalRef.value)
  }
  qrModal.show()
}

const handleImageError = (event) => {
  event.target.src = defaultImage
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString('zh-CN')
}

onMounted(() => loadData())
</script>

<style scoped>
/* 统一蓝色主题 */
.page-header {
  background: linear-gradient(135deg, #5a9bcf 0%, #4a8bbf 100%);
  padding: 40px 0;
}

.page-header h1 {
  color: #fff;
  font-size: 28px;
  font-weight: 600;
  margin: 0;
}

/* Tabs卡片 - 直角设计 */
.tabs-card {
  background: #fff;
  border: 1px solid #e5e7eb;
  padding: 0 20px;
  margin-bottom: 24px;
}

.nav-tabs {
  display: flex;
  gap: 8px;
  list-style: none;
  padding: 0;
  margin: 0;
}

.nav-item {
  margin: 0;
}

.tab-link {
  display: inline-block;
  padding: 14px 22px;
  color: #7f8c8d;
  text-decoration: none;
  transition: all 0.2s;
  cursor: pointer;
  border-bottom: 2px solid transparent;
}

.tab-link:hover {
  color: #5a9bcf;
}

.tab-link.active {
  background: transparent;
  color: #5a9bcf;
  border-bottom-color: #5a9bcf;
  font-weight: 600;
}

/* 订单卡片 - 直角设计 */
.order-card {
  background: #fff;
  border: 1px solid #e5e7eb;
  margin-bottom: 16px;
  overflow: hidden;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 12px 18px;
  background: #f9fafb;
  border-bottom: 1px solid #e5e7eb;
}

.order-no {
  color: #7f8c8d;
  font-size: 13px;
  margin-right: 16px;
}

.order-time {
  color: #95a5a6;
  font-size: 12px;
}

.order-status {
  padding: 2px 10px;
  font-size: 12px;
  font-weight: 500;
}

.order-status.pending {
  background: #fed7aa;
  color: #f59e0b;
}

.order-status.paid {
  background: #d1fae5;
  color: #10b981;
}

.order-status.used {
  background: #e0e7ff;
  color: #5a9bcf;
}

.order-status.cancelled {
  background: #fee2e2;
  color: #e74c3c;
}

.order-status.refunded {
  background: #e5e7eb;
  color: #95a5a6;
}

/* 订单内容 */
.order-content {
  display: grid;
  grid-template-columns: 112px minmax(0, 1fr) 120px 190px;
  align-items: center;
  gap: 18px;
  padding: 18px;
}

.order-img {
  width: 112px;
  height: 84px;
  background: #f3f6f8;
  overflow: hidden;
}

.order-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.order-info {
  min-width: 0;
}

.order-name {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 6px 0;
}

.order-detail {
  font-size: 13px;
  color: #7f8c8d;
  margin: 0 0 4px 0;
}

.trade-no {
  margin-left: 8px;
}

.order-price {
  text-align: right;
}

.price {
  font-size: 20px;
  font-weight: 600;
  color: #e74c3c;
}

.order-actions {
  display: flex;
  gap: 8px;
  align-items: center;
  justify-content: flex-end;
  flex-wrap: wrap;
}

.action-btn {
  min-width: 86px;
  padding: 8px 14px;
  border: 1px solid #e5e7eb;
  background: #fff;
  cursor: pointer;
  font-size: 13px;
  transition: all 0.2s;
  text-align: center;
}

.action-btn.pay {
  background: #5a9bcf;
  border-color: #5a9bcf;
  color: #fff;
}

.action-btn.pay:hover {
  background: #4a8bbf;
}

.action-btn.cancel:hover {
  border-color: #e74c3c;
  color: #e74c3c;
}

.action-btn.voucher {
  background: #fff;
  border-color: #5a9bcf;
  color: #5a9bcf;
}

.action-btn.voucher:hover {
  background: #5a9bcf;
  color: #fff;
}

/* 空状态 */
.empty-state {
  background: #fff;
  border: 1px solid #e5e7eb;
  text-align: center;
  padding: 60px 20px;
}

.empty-state i {
  font-size: 64px;
  color: #95a5a6;
}

.empty-state p {
  color: #7f8c8d;
  margin: 16px 0 20px 0;
}

.empty-btn {
  display: inline-block;
  background: #5a9bcf;
  color: #fff;
  text-decoration: none;
  padding: 8px 24px;
}

.empty-btn:hover {
  background: #4a8bbf;
  color: #fff;
}

/* 弹窗样式 - 直角设计 */
.modal-content {
  border: 1px solid #e5e7eb;
}

.modal-header {
  border-bottom: 1px solid #e5e7eb;
  padding: 16px 20px;
}

.modal-title {
  color: #2c3e50;
  font-size: 18px;
  font-weight: 600;
}

.modal-body {
  padding: 24px;
  text-align: center;
}

.qr-container {
  margin-bottom: 20px;
}

.qr-container img {
  width: 200px;
  height: 200px;
  border: 1px solid #e5e7eb;
}

.qr-scenic {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 12px 0;
}

.qr-info {
  font-size: 13px;
  color: #7f8c8d;
  margin: 0 0 4px 0;
}

.qr-tip {
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  padding: 12px;
  margin-top: 16px;
  font-size: 12px;
  color: #7f8c8d;
  text-align: left;
}

.qr-tip i {
  margin-right: 6px;
  color: #5a9bcf;
}

/* 响应式 */
@media (max-width: 768px) {
  .page-header {
    padding: 30px 0;
  }
  
  .tabs-card {
    overflow-x: auto;
  }
  
  .nav-tabs {
    min-width: 400px;
  }
  
  .order-content {
    grid-template-columns: 96px minmax(0, 1fr);
    align-items: start;
    gap: 14px;
  }
  
  .order-img {
    width: 96px;
    height: 72px;
  }
  
  .order-price {
    grid-column: 1 / -1;
    text-align: left;
  }
  
  .order-actions {
    grid-column: 1 / -1;
    justify-content: flex-start;
  }
  
  .price {
    font-size: 18px;
  }
}
</style>
