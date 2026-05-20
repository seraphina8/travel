<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-header">
        <i class="bi bi-globe2"></i>
        <h2>旅游平台管理后台</h2>
      </div>
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label class="form-label">用户名</label>
          <input 
            type="text" 
            class="form-control" 
            v-model="form.username" 
            placeholder="请输入用户名"
            required
          >
        </div>
        <div class="form-group">
          <label class="form-label">密码</label>
          <input 
            type="password" 
            class="form-control" 
            v-model="form.password" 
            placeholder="请输入密码"
            required
          >
        </div>
        <button type="submit" class="login-btn" :disabled="loading">
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </form>
      <p v-if="error" class="error-msg">{{ error }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api'

const router = useRouter()
const loading = ref(false)
const error = ref('')

const form = ref({
  username: '',
  password: ''
})

const handleLogin = async () => {
  loading.value = true
  error.value = ''
  try {
    const res = await api.login(form.value)
    if (res.code === 200) {
      localStorage.setItem('token', res.data.token)
      localStorage.setItem('admin', JSON.stringify(res.data.admin))
      router.push('/dashboard')
    } else {
      error.value = res.message
    }
  } catch (e) {
    error.value = '登录失败，请检查网络'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* 统一蓝色主题 */
.login-page {
  min-height: 100vh;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
}

/* 登录卡片 - 直角设计 */
.login-card {
  width: 100%;
  max-width: 400px;
  background: #ffffff;
  border: 1px solid #e5e7eb;
  padding: 40px 32px;
}

/* 头部 */
.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.login-header i {
  font-size: 48px;
  color: #5a9bcf;
  margin-bottom: 12px;
  display: inline-block;
}

.login-header h2 {
  font-size: 24px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
}

/* 表单 */
.form-group {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #2c3e50;
  margin-bottom: 8px;
}

.form-control {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #d1d5db;
  font-size: 14px;
  outline: none;
  transition: border-color 0.2s;
}

.form-control:focus {
  border-color: #5a9bcf;
}

.form-control::placeholder {
  color: #9ca3af;
}

/* 登录按钮 - 直角设计 */
.login-btn {
  width: 100%;
  background: #5a9bcf;
  color: #ffffff;
  border: none;
  padding: 12px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s;
  margin-top: 8px;
}

.login-btn:hover:not(:disabled) {
  background: #4a8bbf;
}

.login-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 错误消息 */
.error-msg {
  color: #e74c3c;
  text-align: center;
  margin: 16px 0 0 0;
  font-size: 13px;
}

/* 响应式 */
@media (max-width: 480px) {
  .login-card {
    padding: 32px 24px;
  }
  
  .login-header h2 {
    font-size: 20px;
  }
  
  .login-header i {
    font-size: 40px;
  }
}
</style>