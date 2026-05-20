<template>
  <div class="pay-container">
    <div class="pay-card">
      <div class="pay-header">
        <i class="bi bi-shield-check text-success"></i>
        <h4>订单支付</h4>
      </div>
      
      <div class="order-info">
        <div class="info-row">
          <span class="label">订单编号</span>
          <span class="value">{{ orderNo }}</span>
        </div>
        <div class="info-row">
          <span class="label">商品名称</span>
          <span class="value">{{ subject }}</span>
        </div>
        <div class="info-row amount">
          <span class="label">支付金额</span>
          <span class="value text-danger">¥{{ amount }}</span>
        </div>
      </div>
      
      <div class="pay-methods">
        <h6>选择支付方式</h6>
        <div class="method-list">
          <div 
            class="method-item" 
            :class="{ active: payMethod === 'alipay' }"
            @click="payMethod = 'alipay'"
          >
            <i class="bi bi-alipay" style="color: #1677ff;"></i>
            <span>支付宝</span>
            <i class="bi bi-check-circle-fill text-primary" v-if="payMethod === 'alipay'"></i>
          </div>
          <div 
            class="method-item" 
            :class="{ active: payMethod === 'wechat' }"
            @click="payMethod = 'wechat'"
          >
            <i class="bi bi-wechat" style="color: #07c160;"></i>
            <span>微信支付</span>
            <i class="bi bi-check-circle-fill text-primary" v-if="payMethod === 'wechat'"></i>
          </div>
        </div>
      </div>
      
      <div class="pay-actions">
        <button class="btn btn-primary btn-lg w-100" @click="confirmPay" :disabled="paying">
          <span v-if="paying">
            <span class="spinner-border spinner-border-sm me-2"></span>
            支付中...
          </span>
          <span v-else>确认支付 ¥{{ amount }}</span>
        </button>
        <button class="btn btn-link text-muted mt-2" @click="cancelPay">取消支付</button>
      </div>
      
      <div class="pay-tips">
        <p><i class="bi bi-info-circle"></i> 这是模拟支付，点击确认即可完成支付</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '../api'

const route = useRoute()
const router = useRouter()

const orderNo = ref('')
const amount = ref(0)
const subject = ref('')
const payMethod = ref('alipay')
const paying = ref(false)

onMounted(() => {
  orderNo.value = route.query.orderNo || ''
  amount.value = route.query.amount || 0
  subject.value = route.query.subject || '门票订单'
})

const confirmPay = async () => {
  paying.value = true
  
  try {
    await new Promise(resolve => setTimeout(resolve, 1500))
    
    const res = await api.confirmPayment(orderNo.value, payMethod.value)
    
    if (res.code === 200) {
      alert('支付成功！')
      router.push('/order')
    } else {
      alert(res.message || '支付失败')
    }
  } catch (e) {
    alert('支付失败，请重试')
  } finally {
    paying.value = false
  }
}

const cancelPay = () => {
  router.push('/order')
}
</script>

<style scoped>
.pay-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #0ea5e9;
  padding: 20px;
}

.pay-card {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.2);
  width: 100%;
  max-width: 420px;
  overflow: hidden;
}

.pay-header {
  text-align: center;
  padding: 30px 20px 20px;
  border-bottom: 1px solid #f1f5f9;
}

.pay-header i {
  font-size: 48px;
  margin-bottom: 10px;
}

.pay-header h4 {
  margin: 0;
  color: #1e293b;
}

.order-info {
  padding: 20px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid #f1f5f9;
}

.info-row .label {
  color: #64748b;
}

.info-row .value {
  font-weight: 500;
}

.info-row.amount .value {
  font-size: 24px;
  font-weight: 700;
}

.pay-methods {
  padding: 20px;
  background: #f8fafc;
}

.pay-methods h6 {
  margin-bottom: 15px;
  color: #475569;
}

.method-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.method-item {
  display: flex;
  align-items: center;
  padding: 15px;
  background: #fff;
  border: 2px solid #e2e8f0;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s;
}

.method-item:hover {
  border-color: #667eea;
}

.method-item.active {
  border-color: #667eea;
  background: #f0f4ff;
}

.method-item i:first-child {
  font-size: 24px;
  margin-right: 12px;
}

.method-item span {
  flex: 1;
  font-weight: 500;
}

.pay-actions {
  padding: 20px;
  text-align: center;
}

.pay-tips {
  padding: 15px 20px;
  background: #fef3c7;
  text-align: center;
}

.pay-tips p {
  margin: 0;
  color: #92400e;
  font-size: 13px;
}
</style>
