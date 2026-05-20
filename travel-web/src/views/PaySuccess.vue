<template>
  <div class="pay-success-container">
    <div class="pay-success-card">
      <div class="success-icon" v-if="!loading">
        <i class="bi bi-check-circle-fill"></i>
      </div>
      <div class="loading-icon" v-else>
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
      </div>
      
      <h2 v-if="!loading">支付成功</h2>
      <h2 v-else>支付确认中...</h2>
      
      <div class="order-info" v-if="orderInfo.orderNo">
        <div class="info-row">
          <span class="label">订单编号</span>
          <span class="value">{{ orderInfo.orderNo }}</span>
        </div>
        <div class="info-row" v-if="orderInfo.tradeNo">
          <span class="label">支付宝交易号</span>
          <span class="value">{{ orderInfo.tradeNo }}</span>
        </div>
        <div class="info-row">
          <span class="label">支付金额</span>
          <span class="value text-danger fw-bold">¥{{ orderInfo.totalAmount }}</span>
        </div>
        <div class="info-row">
          <span class="label">支付方式</span>
          <span class="value">
            <i class="bi bi-alipay" style="color: #1677ff;"></i> 支付宝
          </span>
        </div>
      </div>
      
      <div class="actions">
        <router-link to="/order" class="btn btn-primary btn-lg">
          <i class="bi bi-list-check"></i> 查看订单
        </router-link>
        <router-link to="/" class="btn btn-outline-secondary btn-lg">
          <i class="bi bi-house"></i> 返回首页
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import api from '../api'

const route = useRoute()
const loading = ref(true)
const orderInfo = ref({
  orderNo: '',
  tradeNo: '',
  totalAmount: ''
})

onMounted(async () => {
  const query = route.query
  orderInfo.value.orderNo = query.out_trade_no || ''
  orderInfo.value.tradeNo = query.trade_no || ''
  orderInfo.value.totalAmount = query.total_amount || ''
  
  // 调用后端同步回调接口，更新订单状态为已支付
  if (query.out_trade_no) {
    try {
      const res = await api.alipayReturn({
        out_trade_no: query.out_trade_no,
        trade_no: query.trade_no,
        total_amount: query.total_amount
      })
      if (res.code === 200 && res.data) {
        orderInfo.value.orderNo = res.data.orderNo || orderInfo.value.orderNo
        orderInfo.value.tradeNo = res.data.tradeNo || orderInfo.value.tradeNo
        orderInfo.value.totalAmount = res.data.totalAmount || orderInfo.value.totalAmount
      }
    } catch (e) {
      console.error('同步回调失败', e)
    }
  }
  
  loading.value = false
})
</script>

<style scoped>
.pay-success-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #e8f5e9 0%, #f1f8e9 100%);
  padding: 20px;
}

.pay-success-card {
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 480px;
  padding: 50px 40px;
  text-align: center;
}

.success-icon {
  margin-bottom: 20px;
}

.success-icon i {
  font-size: 80px;
  color: #4caf50;
  animation: scaleIn 0.5s ease;
}

.loading-icon {
  margin-bottom: 20px;
}

@keyframes scaleIn {
  0% { transform: scale(0); opacity: 0; }
  50% { transform: scale(1.2); }
  100% { transform: scale(1); opacity: 1; }
}

h2 {
  color: #1e293b;
  font-weight: 700;
  margin-bottom: 30px;
}

.order-info {
  background: #f8fafc;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 30px;
  text-align: left;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #e2e8f0;
}

.info-row:last-child {
  border-bottom: none;
}

.info-row .label {
  color: #64748b;
  font-size: 14px;
}

.info-row .value {
  color: #1e293b;
  font-weight: 500;
  font-size: 14px;
  max-width: 60%;
  text-align: right;
  word-break: break-all;
}

.actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.actions .btn {
  border-radius: 12px;
  padding: 12px 24px;
  font-weight: 600;
}
</style>
