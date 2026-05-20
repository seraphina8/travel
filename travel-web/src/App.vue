<template>
  <div id="app">
    <Navbar v-if="showNavbar" />
    <router-view />
    <Footer v-if="showFooter" />
    
    <!-- AI导游悬浮球 -->
    <AiAssistant v-if="showNavbar" />
    
    <!-- 消息弹窗 -->
    <div class="message-toast" v-if="showMessageToast" @click="goToChat">
      <div class="toast-content">
        <img :src="messageToast.avatar" class="toast-avatar">
        <div class="toast-info">
          <div class="toast-name">{{ messageToast.senderName }}</div>
          <div class="toast-text">{{ messageToast.content }}</div>
        </div>
        <button class="toast-close" @click.stop="closeToast">
          <i class="bi bi-x"></i>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Navbar from './components/Navbar.vue'
import Footer from './components/Footer.vue'
import AiAssistant from './components/AiAssistant.vue'

const route = useRoute()
const router = useRouter()

const authPages = ['Login', 'Register', 'ForgotPassword']
const showNavbar = computed(() => !authPages.includes(route.name))
const showFooter = computed(() => !authPages.includes(route.name))

// 消息弹窗相关
const showMessageToast = ref(false)
const messageToast = ref({
  senderId: null,
  senderName: '',
  avatar: '',
  content: ''
})
let ws = null
let toastTimer = null

const getAvatar = (id) => `https://api.dicebear.com/7.x/avataaars/svg?seed=${id}`

const connectWebSocket = () => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  if (!user.id) return
  
  // 如果在聊天页面，不建立全局连接（聊天页面有自己的连接）
  if (route.name === 'Chat') return
  
  const wsUrl = `ws://${window.location.hostname}:8080/ws/chat/${user.id}`
  ws = new WebSocket(wsUrl)
  
  ws.onmessage = (event) => {
    const data = JSON.parse(event.data)
    if (data.type === 'chat' && data.senderId !== user.id) {
      // 收到新消息，显示弹窗
      messageToast.value = {
        senderId: data.senderId,
        senderName: data.senderName || '用户' + data.senderId,
        avatar: data.senderAvatar || getAvatar(data.senderId),
        content: data.content.length > 30 ? data.content.substring(0, 30) + '...' : data.content
      }
      showMessageToast.value = true
      
      // 5秒后自动关闭
      if (toastTimer) clearTimeout(toastTimer)
      toastTimer = setTimeout(() => {
        showMessageToast.value = false
      }, 5000)
    }
  }
  
  ws.onclose = () => {
    // 断开后尝试重连
    setTimeout(() => {
      if (localStorage.getItem('userToken') && route.name !== 'Chat') {
        connectWebSocket()
      }
    }, 5000)
  }
}

const closeToast = () => {
  showMessageToast.value = false
  if (toastTimer) clearTimeout(toastTimer)
}

const goToChat = () => {
  closeToast()
  // 存储聊天目标
  sessionStorage.setItem('chatTarget', JSON.stringify({
    id: messageToast.value.senderId,
    nickname: messageToast.value.senderName,
    avatar: messageToast.value.avatar
  }))
  router.push('/chat')
}

// 监听路由变化
watch(() => route.name, (newName) => {
  if (newName === 'Chat' && ws) {
    ws.close()
    ws = null
  } else if (newName !== 'Chat' && !ws && localStorage.getItem('userToken')) {
    connectWebSocket()
  }
})

// 监听登录状态
watch(() => localStorage.getItem('userToken'), (token) => {
  if (token && route.name !== 'Chat') {
    connectWebSocket()
  } else if (!token && ws) {
    ws.close()
    ws = null
  }
})

onMounted(() => {
  if (localStorage.getItem('userToken') && route.name !== 'Chat') {
    connectWebSocket()
  }
})

onUnmounted(() => {
  if (ws) {
    ws.close()
  }
})
</script>

<style>
.message-toast {
  position: fixed;
  top: 80px;
  right: 20px;
  z-index: 9999;
  animation: slideIn 0.3s ease;
  cursor: pointer;
}

@keyframes slideIn {
  from {
    transform: translateX(100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

.toast-content {
  display: flex;
  align-items: center;
  gap: 12px;
  background: #fff;
  padding: 12px 16px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  border-left: 4px solid #667eea;
  max-width: 320px;
}

.toast-avatar {
  width: 45px;
  height: 45px;
  border-radius: 50%;
  object-fit: cover;
}

.toast-info {
  flex: 1;
  min-width: 0;
}

.toast-name {
  font-weight: 600;
  font-size: 14px;
  color: #1e293b;
  margin-bottom: 2px;
}

.toast-text {
  font-size: 13px;
  color: #64748b;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.toast-close {
  background: none;
  border: none;
  color: #94a3b8;
  font-size: 18px;
  cursor: pointer;
  padding: 0;
  line-height: 1;
}

.toast-close:hover {
  color: #64748b;
}
</style>
