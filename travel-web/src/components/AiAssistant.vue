<template>
  <!-- 悬浮球 -->
  <div 
    class="ai-float-ball" 
    :class="{ 'is-open': isOpen }"
    @click="toggleChat"
    v-if="!isOpen"
  >
    <i class="bi bi-robot"></i>
    <span class="ball-text">AI导游</span>
  </div>
  
  <!-- 聊天窗口 -->
  <div class="ai-chat-window" v-if="isOpen">
    <div class="chat-header">
      <div class="header-info">
        <i class="bi bi-robot me-2"></i>
        <span>AI导游 · 小游</span>
      </div>
      <button class="close-btn" @click="toggleChat">
        <i class="bi bi-x-lg"></i>
      </button>
    </div>
    
    <div class="chat-messages" ref="messagesContainer">
      <div class="welcome-message" v-if="messages.length === 0">
        <div class="welcome-icon">
          <i class="bi bi-robot"></i>
        </div>
        <h5>你好，我是AI导游小游！</h5>
        <p>我可以帮你：</p>
        <div class="quick-actions">
          <button @click="sendQuickMessage('推荐一些热门旅游景点')">🏞️ 推荐景点</button>
          <button @click="sendQuickMessage('有什么好吃的美食推荐')">🍜 美食推荐</button>
          <button @click="sendQuickMessage('帮我规划一个三天的旅行')">📅 行程规划</button>
          <button @click="sendQuickMessage('旅行需要注意什么')">💡 旅行建议</button>
        </div>
      </div>
      
      <div 
        v-for="(msg, index) in messages" 
        :key="index" 
        class="message-item"
        :class="msg.role"
      >
        <div class="message-avatar">
          <i class="bi" :class="msg.role === 'user' ? 'bi-person-fill' : 'bi-robot'"></i>
        </div>
        <div class="message-content">
          <div class="message-text" v-html="formatMessage(msg.content)"></div>
        </div>
      </div>
      
      <div class="message-item assistant" v-if="isLoading">
        <div class="message-avatar">
          <i class="bi bi-robot"></i>
        </div>
        <div class="message-content">
          <div class="typing-indicator">
            <span></span>
            <span></span>
            <span></span>
          </div>
        </div>
      </div>
    </div>
    
    <div class="chat-input">
      <input 
        type="text" 
        v-model="inputMessage" 
        placeholder="问我任何旅游相关的问题..."
        @keyup.enter="sendMessage"
        :disabled="isLoading"
      >
      <button @click="sendMessage" :disabled="isLoading || !inputMessage.trim()">
        <i class="bi bi-send-fill"></i>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, watch } from 'vue'
import api from '../api'

const isOpen = ref(false)
const messages = ref([])
const inputMessage = ref('')
const isLoading = ref(false)
const messagesContainer = ref(null)

const toggleChat = () => {
  isOpen.value = !isOpen.value
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

const formatMessage = (text) => {
  if (!text) return ''
  return text
    .replace(/\n/g, '<br>')
    .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
    .replace(/\*(.*?)\*/g, '<em>$1</em>')
}

const sendQuickMessage = (message) => {
  inputMessage.value = message
  sendMessage()
}

// ========== 修复点：彻底重写 sendMessage，确保请求一定发出 ==========
const sendMessage = async () => {
  const message = inputMessage.value.trim()
  if (!message || isLoading.value) return

  // 推入用户消息
  messages.value.push({ role: 'user', content: message })
  inputMessage.value = ''
  scrollToBottom()

  isLoading.value = true
  try {
    // 【修复】正确取历史消息：去掉最后一条（刚发的）
    const historyList = [...messages.value]
    historyList.pop()
    const history = historyList.slice(-10)

    console.log("发送消息:", message)
    console.log("历史消息:", history)

    // 【关键】调用后端，一定会发请求！
    const res = await api.aiChat(message, history)
    
    console.log("后端返回:", res)

    if (res.code === 200) {
      messages.value.push({ role: 'assistant', content: res.data })
    } else {
      messages.value.push({ role: 'assistant', content: res.message || '服务繁忙' })
    }
  } catch (e) {
    console.error('❌ 请求失败：', e)
    messages.value.push({
      role: 'assistant',
      content: '连接后端失败，请确认后端已启动'
    })
  } finally {
    isLoading.value = false
    scrollToBottom()
  }
}

watch(messages, () => {
  scrollToBottom()
}, { deep: true })
</script>

<style scoped>
.ai-float-ball {
  position: fixed;
  bottom: 100px;
  right: 30px;
  width: 60px;
  height: 60px;
  background: #667eea;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.4);
  z-index: 9998;
  transition: all 0.3s ease;
  color: #fff;
}

.ai-float-ball:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 25px rgba(102, 126, 234, 0.5);
}

.ai-float-ball i {
  font-size: 24px;
}

.ai-float-ball .ball-text {
  font-size: 10px;
  margin-top: 2px;
}

.ai-chat-window {
  position: fixed;
  bottom: 100px;
  right: 30px;
  width: 380px;
  height: 520px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
  z-index: 9999;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  animation: slideUp 0.3s ease;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.chat-header {
  background: #667eea;
  color: #fff;
  padding: 16px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-info {
  display: flex;
  align-items: center;
  font-weight: 600;
  font-size: 16px;
}

.close-btn {
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: #fff;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s;
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background: #f8f9fa;
}

.welcome-message {
  text-align: center;
  padding: 20px;
}

.welcome-icon {
  width: 60px;
  height: 60px;
  background: #667eea;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
}

.welcome-icon i {
  font-size: 28px;
  color: #fff;
}

.welcome-message h5 {
  color: #1e293b;
  margin-bottom: 8px;
}

.welcome-message p {
  color: #64748b;
  font-size: 14px;
  margin-bottom: 16px;
}

.quick-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: center;
}

.quick-actions button {
  background: #fff;
  border: 1px solid #e2e8f0;
  padding: 8px 14px;
  border-radius: 20px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.quick-actions button:hover {
  background: #667eea;
  color: #fff;
  border-color: #667eea;
}

.message-item {
  display: flex;
  gap: 10px;
  margin-bottom: 16px;
}

.message-item.user {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.message-item.user .message-avatar {
  background: #667eea;
  color: #fff;
}

.message-item.assistant .message-avatar {
  background: #667eea;
  color: #fff;
}

.message-content {
  max-width: 75%;
}

.message-text {
  padding: 12px 16px;
  border-radius: 16px;
  font-size: 14px;
  line-height: 1.5;
}

.message-item.user .message-text {
  background: #667eea;
  color: #fff;
  border-bottom-right-radius: 4px;
}

.message-item.assistant .message-text {
  background: #fff;
  color: #1e293b;
  border-bottom-left-radius: 4px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 12px 16px;
  background: #fff;
  border-radius: 16px;
  border-bottom-left-radius: 4px;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  background: #667eea;
  border-radius: 50%;
  animation: typing 1.4s infinite ease-in-out;
}

.typing-indicator span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-indicator span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0%, 60%, 100% {
    transform: translateY(0);
    opacity: 0.4;
  }
  30% {
    transform: translateY(-4px);
    opacity: 1;
  }
}

.chat-input {
  padding: 16px;
  background: #fff;
  border-top: 1px solid #e2e8f0;
  display: flex;
  gap: 10px;
}

.chat-input input {
  flex: 1;
  border: 1px solid #e2e8f0;
  border-radius: 24px;
  padding: 12px 20px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.2s;
}

.chat-input input:focus {
  border-color: #667eea;
}

.chat-input button {
  width: 44px;
  height: 44px;
  background: #667eea;
  border: none;
  border-radius: 50%;
  color: #fff;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.2s;
}

.chat-input button:hover:not(:disabled) {
  transform: scale(1.05);
}

.chat-input button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

@media (max-width: 480px) {
  .ai-chat-window {
    width: calc(100% - 20px);
    right: 10px;
    bottom: 80px;
    height: 60vh;
  }
  
  .ai-float-ball {
    right: 20px;
    bottom: 80px;
  }
}
</style>