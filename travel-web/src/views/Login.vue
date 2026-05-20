<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="auth-header">
        <h2>欢迎回来</h2>
        <p>登录你的账号，继续探索世界</p>
      </div>
      
      <form @submit.prevent="handleLogin">
        <div class="form-item">
          <label class="form-label">用户名</label>
          <div class="input-field">
            <span class="field-icon"><i class="bi bi-person"></i></span>
            <input type="text" class="field-input" v-model="form.username" placeholder="请输入用户名" required>
          </div>
        </div>
        
        <div class="form-item">
          <label class="form-label">密码</label>
          <div class="input-field">
            <span class="field-icon"><i class="bi bi-lock"></i></span>
            <input type="password" class="field-input" v-model="form.password" placeholder="请输入密码" required>
          </div>
        </div>
        
        <div class="form-options">
          <div class="form-check">
            <input type="checkbox" id="remember" v-model="remember">
            <label for="remember">记住我</label>
          </div>
          <router-link to="/forgot-password" class="link">忘记密码?</router-link>
        </div>
        
        <button type="submit" class="btn-submit" :disabled="loading">
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </form>
      
      <p v-if="error" class="error-msg">{{ error }}</p>
      
      <div class="auth-footer">
        <span class="text-muted">还没有账号?</span>
        <router-link to="/register" class="link">立即注册</router-link>
      </div>
      
      <div class="back-home">
        <router-link to="/" class="link">
          <i class="bi bi-arrow-left"></i> 返回首页
        </router-link>
      </div>
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
const remember = ref(false)

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
      localStorage.setItem('userToken', res.data.token)
      localStorage.setItem('user', JSON.stringify(res.data.user))
      
      if (remember.value) {
        localStorage.setItem('rememberUsername', form.value.username)
      } else {
        localStorage.removeItem('rememberUsername')
      }
      
      router.push('/')
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
/* 统一蓝色主题 - 与注册页一致 */
.auth-page {
  min-height: 100vh;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
}

/* 卡片 */
.auth-card {
  width: 100%;
  max-width: 420px;
  background: #ffffff;
  border: 1px solid #e5e7eb;
  padding: 40px 32px;
}

/* 头部 */
.auth-header {
  text-align: center;
  margin-bottom: 32px;
}

.auth-header h2 {
  font-size: 28px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 8px 0;
}

.auth-header p {
  color: #6b7280;
  font-size: 14px;
  margin: 0;
}

/* 表单项 */
.form-item {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #374151;
  margin-bottom: 8px;
}

.input-field {
  display: flex;
  align-items: center;
  border: 1px solid #d1d5db;
  background: #ffffff;
  transition: all 0.2s;
}

.input-field:focus-within {
  border-color: #3b82f6;
}

.field-icon {
  width: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #9ca3af;
  border-right: 1px solid #e5e7eb;
}

.field-input {
  flex: 1;
  border: none;
  padding: 10px 12px;
  font-size: 14px;
  outline: none;
  font-family: inherit;
  background: transparent;
}

.field-input:focus {
  background: transparent;
}

.field-input::placeholder {
  color: #9ca3af;
}

/* 选项栏 */
.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 20px 0 24px 0;
}

.form-check {
  display: flex;
  align-items: center;
  gap: 8px;
}

.form-check input[type="checkbox"] {
  width: 16px;
  height: 16px;
  margin: 0;
  cursor: pointer;
  accent-color: #3b82f6;
}

.form-check label {
  font-size: 13px;
  color: #6b7280;
  cursor: pointer;
}

.link {
  color: #3b82f6;
  text-decoration: none;
  font-size: 13px;
}

.link:hover {
  color: #2563eb;
  text-decoration: underline;
}

/* 提交按钮 */
.btn-submit {
  width: 100%;
  background: #3b82f6;
  color: #ffffff;
  border: none;
  padding: 12px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-submit:hover:not(:disabled) {
  background: #2563eb;
}

.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 错误消息 */
.error-msg {
  color: #ef4444;
  text-align: center;
  margin: 16px 0 0 0;
  font-size: 13px;
}

/* 底部链接 */
.auth-footer {
  text-align: center;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #e5e7eb;
  font-size: 14px;
}

.auth-footer .text-muted {
  color: #9ca3af;
}

.back-home {
  text-align: center;
  margin-top: 16px;
}

.back-home .link {
  font-size: 13px;
  color: #9ca3af;
}

.back-home .link:hover {
  color: #6b7280;
}

/* 响应式 */
@media (max-width: 480px) {
  .auth-card {
    padding: 24px 20px;
  }
  
  .auth-header h2 {
    font-size: 24px;
  }
  
  .field-icon {
    width: 40px;
  }
  
  .field-input {
    padding: 8px 12px;
  }
}
</style>
