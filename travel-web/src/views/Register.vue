<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="auth-header">
        <h2>创建账号</h2>
        <p>加入我们，开启精彩旅程</p>
      </div>
      
      <form @submit.prevent="handleRegister">
        <div class="form-item">
          <label class="form-label">用户名</label>
          <div class="input-field">
            <span class="field-icon"><i class="bi bi-person"></i></span>
            <input type="text" class="field-input" v-model="form.username" placeholder="请输入用户名" required>
          </div>
        </div>
        
        <div class="form-item">
          <label class="form-label">昵称</label>
          <div class="input-field">
            <span class="field-icon"><i class="bi bi-emoji-smile"></i></span>
            <input type="text" class="field-input" v-model="form.nickname" placeholder="请输入昵称">
          </div>
        </div>
        
        <div class="form-item">
          <label class="form-label">密码</label>
          <div class="input-field">
            <span class="field-icon"><i class="bi bi-lock"></i></span>
            <input type="password" class="field-input" v-model="form.password" placeholder="请输入密码" required>
          </div>
        </div>
        
        <div class="form-item">
          <label class="form-label">确认密码</label>
          <div class="input-field">
            <span class="field-icon"><i class="bi bi-lock-fill"></i></span>
            <input type="password" class="field-input" v-model="form.confirmPassword" placeholder="请再次输入密码" required>
          </div>
        </div>
        
        <div class="form-check">
          <input type="checkbox" id="agree" v-model="agree" required>
          <label for="agree">
            我已阅读并同意 <a href="#" class="link">用户协议</a> 和 <a href="#" class="link">隐私政策</a>
          </label>
        </div>
        
        <button type="submit" class="btn-submit" :disabled="loading">
          {{ loading ? '注册中...' : '注册' }}
        </button>
      </form>
      
      <p v-if="error" class="error-msg">{{ error }}</p>
      
      <div class="auth-footer">
        <span class="text-muted">已有账号?</span>
        <router-link to="/login" class="link">立即登录</router-link>
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
const agree = ref(false)

const form = ref({
  username: '',
  nickname: '',
  password: '',
  confirmPassword: ''
})

const validateRegister = () => {
  const username = form.value.username?.trim()
  const nickname = form.value.nickname?.trim()
  const password = form.value.password?.trim()
  const confirmPassword = form.value.confirmPassword?.trim()

  if (!/^[a-zA-Z0-9_]{4,20}$/.test(username)) {
    error.value = '用户名应为4到20位，只能包含字母、数字或下划线'
    return false
  }

  if (nickname && !/^[\u4e00-\u9fa5a-zA-Z0-9_-]{2,20}$/.test(nickname)) {
    error.value = '昵称应为2到20位，只能包含中文、字母、数字、下划线或短横线'
    return false
  }

  if (!/^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d_@#$%^&*!.-]{6,20}$/.test(password)) {
    error.value = '密码应为6到20位，且至少包含字母和数字'
    return false
  }

  if (password !== confirmPassword) {
    error.value = '两次输入的密码不一致'
    return false
  }

  if (!agree.value) {
    error.value = '请同意用户协议'
    return false
  }

  return true
}

const handleRegister = async () => {
  error.value = ''

  if (!validateRegister()) {
    return
  }

  loading.value = true

  try {
    const res = await api.register({
      username: form.value.username.trim(),
      nickname: form.value.nickname?.trim(),
      password: form.value.password.trim()
    })

    if (res.code === 200) {
      alert('注册成功，请登录')
      router.push('/login')
    } else {
      error.value = res.message || '注册失败'
    }
  } catch (e) {
    error.value = '注册失败，请检查网络'
  } finally {
    loading.value = false
  }
}

</script>

<style scoped>
/* 统一蓝色主题 */
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

/* 复选框 */
.form-check {
  margin: 24px 0;
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