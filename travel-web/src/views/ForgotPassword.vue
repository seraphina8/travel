<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="auth-header">
        <h2>重置密码</h2>
        <p>通过用户名和绑定手机号或邮箱找回账号</p>
      </div>

      <form @submit.prevent="handleReset">
        <div class="form-item">
          <label class="form-label">用户名</label>
          <div class="input-field">
            <span class="field-icon"><i class="bi bi-person"></i></span>
            <input
              type="text"
              class="field-input"
              v-model="form.username"
              placeholder="请输入用户名"
              required
            />
          </div>
        </div>

        <div class="form-item">
          <label class="form-label">绑定手机号或邮箱</label>
          <div class="input-field">
            <span class="field-icon"><i class="bi bi-shield-check"></i></span>
            <input
              type="text"
              class="field-input"
              v-model="form.identity"
              placeholder="请输入绑定手机号或邮箱"
              required
            />
          </div>
        </div>

        <div class="form-item">
          <label class="form-label">新密码</label>
          <div class="input-field">
            <span class="field-icon"><i class="bi bi-lock"></i></span>
            <input
              type="password"
              class="field-input"
              v-model="form.newPassword"
              placeholder="请输入新密码"
              required
            />
          </div>
        </div>

        <div class="form-item">
          <label class="form-label">确认新密码</label>
          <div class="input-field">
            <span class="field-icon"><i class="bi bi-lock-fill"></i></span>
            <input
              type="password"
              class="field-input"
              v-model="form.confirmPassword"
              placeholder="请再次输入新密码"
              required
            />
          </div>
        </div>

        <div class="form-item">
          <label class="form-label">图形验证码</label>
          <div class="captcha-row">
            <div class="input-field captcha-input">
              <span class="field-icon"><i class="bi bi-patch-check"></i></span>
              <input
                type="text"
                class="field-input"
                v-model="form.captchaCode"
                placeholder="请输入验证码"
                maxlength="4"
                required
              />
            </div>
            <button
              type="button"
              class="captcha-image"
              @click="loadCaptcha"
              title="点击刷新"
            >
              <img v-if="captcha.image" :src="captcha.image" alt="验证码" />
              <span v-else>刷新</span>
            </button>
          </div>
        </div>

        <button type="submit" class="btn-submit" :disabled="loading">
          {{ loading ? "提交中..." : "重置密码" }}
        </button>
      </form>

      <p v-if="message" class="success-msg">{{ message }}</p>
      <p v-if="error" class="error-msg">{{ error }}</p>

      <div class="auth-footer">
        <span class="text-muted">想起密码了?</span>
        <router-link to="/login" class="link">立即登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import api from "../api";

const router = useRouter();
const loading = ref(false);
const error = ref("");
const message = ref("");
const captcha = ref({ captchaId: "", image: "" });
const form = ref({
  username: "",
  identity: "",
  newPassword: "",
  confirmPassword: "",
  captchaCode: "",
});

const loadCaptcha = async () => {
  form.value.captchaCode = "";
  const res = await api.getCaptcha();
  if (res.code === 200) {
    captcha.value = res.data;
  }
};

const validate = () => {
  const password = form.value.newPassword.trim();
  if (!/^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d_@#$%^&*!.-]{6,20}$/.test(password)) {
    error.value = "新密码应为6到20位，且至少包含字母和数字";
    return false;
  }
  if (password !== form.value.confirmPassword.trim()) {
    error.value = "两次输入的密码不一致";
    return false;
  }
  return true;
};

const handleReset = async () => {
  error.value = "";
  message.value = "";
  if (!validate()) return;

  loading.value = true;
  try {
    const res = await api.forgotPassword({
      username: form.value.username.trim(),
      identity: form.value.identity.trim(),
      newPassword: form.value.newPassword.trim(),
      captchaId: captcha.value.captchaId,
      captchaCode: form.value.captchaCode.trim(),
    });
    if (res.code === 200) {
      message.value = res.message || "密码重置成功";
      setTimeout(() => router.push("/login"), 800);
    } else {
      error.value = res.message || "重置失败";
      loadCaptcha();
    }
  } catch (e) {
    error.value = "重置失败，请检查网络";
    loadCaptcha();
  } finally {
    loading.value = false;
  }
};

onMounted(loadCaptcha);
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
}

.auth-card {
  width: 100%;
  max-width: 420px;
  background: #ffffff;
  border: 1px solid #e5e7eb;
  padding: 40px 32px;
}

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
  background: transparent;
}

.captcha-row {
  display: flex;
  gap: 10px;
}

.captcha-input {
  flex: 1;
}

.captcha-image {
  width: 120px;
  height: 42px;
  border: 1px solid #d1d5db;
  background: #f8fafc;
  padding: 0;
  cursor: pointer;
}

.captcha-image img {
  width: 100%;
  height: 100%;
  display: block;
}

.btn-submit {
  width: 100%;
  background: #3b82f6;
  color: #ffffff;
  border: none;
  padding: 12px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
}

.btn-submit:hover:not(:disabled) {
  background: #2563eb;
}

.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.error-msg,
.success-msg {
  text-align: center;
  margin: 16px 0 0 0;
  font-size: 13px;
}

.error-msg {
  color: #ef4444;
}

.success-msg {
  color: #16a34a;
}

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

.link {
  color: #3b82f6;
  text-decoration: none;
  font-size: 13px;
}

.link:hover {
  color: #2563eb;
  text-decoration: underline;
}
</style>
