<template>
  <div class="page-header chat-header">
    <div class="container">
      <nav aria-label="breadcrumb" class="mb-2">
        <ol class="breadcrumb breadcrumb-custom">
          <li class="breadcrumb-item">
            <router-link to="/"><i class="bi bi-house-door"></i> 首页</router-link>
          </li>
          <li class="breadcrumb-item active">
            <i class="bi bi-chat-dots"></i> 在线聊天
          </li>
        </ol>
      </nav>
      <h1 class="h3 fw-bold mb-1"><i class="bi bi-chat-dots me-2"></i>在线聊天</h1>
      <p class="text-white-50 mb-0 small">与旅友实时交流，分享旅行心得</p>
    </div>
  </div>
  
  <div class="container py-4">
    <div class="row g-4">
      <!-- 会话列表 -->
      <div class="col-md-4">
        <div class="session-card">
          <div class="session-header">
            <input type="text" class="search-input" v-model="searchUser" placeholder="搜索用户">
            <button class="add-btn" @click="showContacts = true">
              <i class="bi bi-plus"></i>
            </button>
          </div>
          
          <div class="session-list">
            <div 
              v-for="session in sessions" 
              :key="session.id"
              class="session-item"
              :class="{ active: currentTarget?.id === session.targetId }"
              @click="selectSession(session)"
            >
              <img :src="session.targetAvatar || getAvatar(session.targetId)" class="avatar">
              <div class="session-info">
                <div class="session-header-info">
                  <span class="session-name">{{ session.targetName || '用户' + session.targetId }}</span>
                  <span class="session-time">{{ formatTime(session.updateTime) }}</span>
                </div>
                <div class="session-preview">
                  <span class="last-msg">{{ session.lastMessage || '暂无消息' }}</span>
                  <span class="unread-badge" v-if="session.unreadCount > 0">
                    {{ session.unreadCount }}
                  </span>
                </div>
              </div>
            </div>
            
            <div class="empty-session" v-if="sessions.length === 0">
              <i class="bi bi-chat-dots"></i>
              <p>暂无会话</p>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 聊天窗口 -->
      <div class="col-md-8">
        <div class="chat-window">
          <template v-if="currentTarget">
            <!-- 头部 -->
            <div class="chat-window-header">
              <img :src="currentTarget.avatar || getAvatar(currentTarget.id)" class="avatar">
              <div>
                <h6 class="mb-0">{{ currentTarget.nickname || currentTarget.username }}</h6>
                <small class="status-text">
                  <span class="status-dot" :class="{ online: isOnline }"></span>
                  {{ isOnline ? '在线' : '离线' }}
                </small>
              </div>
            </div>
            
            <!-- 消息区域 -->
            <div class="chat-messages" ref="messagesRef">
              <div 
                v-for="msg in messages" 
                :key="msg.id"
                class="message"
                :class="{ mine: msg.senderId === userId }"
              >
                <img :src="msg.senderId === userId ? myAvatar : getAvatar(msg.senderId)" class="avatar">
                <div class="message-content">
                  <div class="message-bubble">{{ msg.content }}</div>
                  <div class="message-time">{{ formatTime(msg.createTime) }}</div>
                </div>
              </div>
              
              <div class="empty-message" v-if="messages.length === 0">
                <p>开始聊天吧~</p>
              </div>
            </div>
            
            <!-- 输入区域 -->
            <div class="chat-input-area">
              <input 
                type="text" 
                class="chat-input" 
                v-model="inputMessage"
                placeholder="输入消息..."
                @keyup.enter="sendMessage"
              >
              <button class="send-btn" @click="sendMessage" :disabled="!inputMessage.trim()">
                <i class="bi bi-send"></i> 发送
              </button>
            </div>
          </template>
          
          <div class="empty-chat" v-else>
            <div class="empty-content">
              <i class="bi bi-chat-square-text"></i>
              <p>选择一个会话开始聊天</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
  
  <!-- 联系人弹窗 -->
  <div class="modal fade" id="contactsModal" tabindex="-1" ref="contactsModalRef">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">选择联系人</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
        </div>
        <div class="modal-body">
          <div class="contact-list">
            <div 
              class="contact-item"
              v-for="contact in contacts"
              :key="contact.id"
              @click="startChat(contact)"
            >
              <img :src="contact.avatar || getAvatar(contact.id)" class="avatar">
              <div>
                <h6 class="mb-0">{{ contact.nickname || contact.username }}</h6>
              </div>
            </div>
          </div>
          <div class="empty-contact" v-if="contacts.length === 0">
            暂无联系人
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { Modal } from 'bootstrap'
import api from '../api'

const userId = ref(null)
const myAvatar = ref('')
const sessions = ref([])
const contacts = ref([])
const messages = ref([])
const currentTarget = ref(null)
const inputMessage = ref('')
const searchUser = ref('')
const isOnline = ref(false)
const showContacts = ref(false)
const messagesRef = ref(null)
const contactsModalRef = ref(null)
let contactsModal = null
let ws = null

const getAvatar = (id) => `https://api.dicebear.com/7.x/avataaars/svg?seed=${id}`

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  if (date.toDateString() === now.toDateString()) {
    return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  }
  return date.toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' })
}

const loadSessions = async () => {
  try {
    const res = await api.getChatSessions()
    if (res.code === 200) {
      sessions.value = res.data || []
    }
  } catch (e) {
    console.error('加载会话失败', e)
  }
}

const loadContacts = async () => {
  try {
    const res = await api.getChatContacts()
    if (res.code === 200) {
      contacts.value = res.data || []
    }
  } catch (e) {
    console.error('加载联系人失败', e)
  }
}

const loadHistory = async (targetId) => {
  try {
    const res = await api.getChatHistory(targetId)
    if (res.code === 200) {
      messages.value = res.data || []
      scrollToBottom()
    }
  } catch (e) {
    console.error('加载聊天记录失败', e)
  }
}

const checkOnline = async (targetId) => {
  try {
    const res = await api.checkOnline(targetId)
    if (res.code === 200) {
      isOnline.value = res.data.online
    }
  } catch (e) {
    isOnline.value = false
  }
}

const selectSession = (session) => {
  currentTarget.value = {
    id: session.targetId,
    nickname: session.targetName,
    avatar: session.targetAvatar
  }
  loadHistory(session.targetId)
  checkOnline(session.targetId)
  session.unreadCount = 0
}

const startChat = (contact) => {
  currentTarget.value = contact
  loadHistory(contact.id)
  checkOnline(contact.id)
  if (contactsModal) {
    contactsModal.hide()
  }
}

const sendMessage = () => {
  if (!inputMessage.value.trim() || !currentTarget.value) return
  
  const msg = {
    type: 'chat',
    receiverId: currentTarget.value.id,
    content: inputMessage.value.trim()
  }
  
  if (ws && ws.readyState === WebSocket.OPEN) {
    ws.send(JSON.stringify(msg))
    inputMessage.value = ''
  }
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesRef.value) {
      messagesRef.value.scrollTop = messagesRef.value.scrollHeight
    }
  })
}

const connectWebSocket = () => {
  if (!userId.value) return
  
  const wsUrl = `ws://${window.location.hostname}:8080/ws/chat/${userId.value}`
  ws = new WebSocket(wsUrl)
  
  ws.onopen = () => {
    console.log('WebSocket连接成功')
  }
  
  ws.onmessage = (event) => {
    const data = JSON.parse(event.data)
    
    if (data.type === 'chat') {
      if (currentTarget.value && 
          (data.senderId === currentTarget.value.id || data.senderId === userId.value)) {
        messages.value.push({
          id: data.messageId,
          senderId: data.senderId,
          receiverId: data.receiverId,
          content: data.content,
          createTime: data.createTime
        })
        scrollToBottom()
      }
      loadSessions()
    }
  }
  
  ws.onclose = () => {
    console.log('WebSocket连接关闭')
    setTimeout(connectWebSocket, 3000)
  }
  
  ws.onerror = (error) => {
    console.error('WebSocket错误', error)
  }
}

watch(showContacts, (val) => {
  if (val) {
    loadContacts()
    if (!contactsModal) {
      contactsModal = new Modal(contactsModalRef.value)
    }
    contactsModal.show()
    showContacts.value = false
  }
})

onMounted(() => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  userId.value = user.id
  myAvatar.value = user.avatar || getAvatar(user.id)
  
  if (userId.value) {
    loadSessions()
    connectWebSocket()
    
    const chatTargetStr = sessionStorage.getItem('chatTarget')
    if (chatTargetStr) {
      const chatTarget = JSON.parse(chatTargetStr)
      sessionStorage.removeItem('chatTarget')
      setTimeout(() => {
        startChat(chatTarget)
      }, 500)
    }
  }
})

onUnmounted(() => {
  if (ws) {
    ws.close()
  }
})
</script>

<style scoped>
/* 统一蓝色主题 */
.chat-header {
  background: linear-gradient(135deg, #5a9bcf 0%, #4a8bbf 100%);
  padding: 40px 0;
}

.breadcrumb-custom {
  background: rgba(255, 255, 255, 0.15);
  padding: 6px 16px;
  display: inline-flex;
  font-size: 13px;
}

.breadcrumb-custom .breadcrumb-item a {
  color: rgba(255, 255, 255, 0.9);
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.breadcrumb-custom .breadcrumb-item a:hover {
  color: #fff;
}

.breadcrumb-custom .breadcrumb-item.active {
  color: #fff;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.breadcrumb-custom .breadcrumb-item + .breadcrumb-item::before {
  color: rgba(255, 255, 255, 0.7);
  content: "/";
}

.chat-header h1 {
  color: #fff;
}

/* 会话卡片 */
.session-card {
  background: #fff;
  border: 1px solid #e5e7eb;
  height: 100%;
}

.session-header {
  display: flex;
  padding: 16px;
  border-bottom: 1px solid #e5e7eb;
  gap: 8px;
}

.search-input {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #e5e7eb;
  font-size: 14px;
  outline: none;
}

.search-input:focus {
  border-color: #5a9bcf;
}

.add-btn {
  background: #5a9bcf;
  color: #fff;
  border: none;
  padding: 8px 16px;
  cursor: pointer;
}

.add-btn:hover {
  background: #4a8bbf;
}

/* 会话列表 */
.session-list {
  max-height: 500px;
  overflow-y: auto;
}

.session-item {
  display: flex;
  padding: 12px 16px;
  cursor: pointer;
  border-bottom: 1px solid #e5e7eb;
  transition: background 0.2s;
}

.session-item:hover {
  background: #f9fafb;
}

.session-item.active {
  background: #e8f0f7;
  border-left: 3px solid #5a9bcf;
}

.session-item .avatar {
  width: 48px;
  height: 48px;
  object-fit: cover;
  margin-right: 12px;
}

.session-info {
  flex: 1;
  min-width: 0;
}

.session-header-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 4px;
}

.session-name {
  font-weight: 600;
  font-size: 14px;
  color: #2c3e50;
}

.session-time {
  font-size: 11px;
  color: #95a5a6;
}

.session-preview {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.last-msg {
  font-size: 12px;
  color: #7f8c8d;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 150px;
}

.unread-badge {
  background: #5a9bcf;
  color: #fff;
  padding: 2px 6px;
  font-size: 10px;
  font-weight: 500;
}

.empty-session {
  text-align: center;
  padding: 48px 20px;
  color: #95a5a6;
}

.empty-session i {
  font-size: 48px;
}

.empty-session p {
  margin: 12px 0 0 0;
}

/* 聊天窗口 */
.chat-window {
  background: #fff;
  border: 1px solid #e5e7eb;
  height: 600px;
  display: flex;
  flex-direction: column;
}

.chat-window-header {
  display: flex;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #e5e7eb;
}

.chat-window-header .avatar {
  width: 40px;
  height: 40px;
  object-fit: cover;
  margin-right: 12px;
}

.status-text {
  font-size: 12px;
  color: #7f8c8d;
  display: flex;
  align-items: center;
  gap: 4px;
}

.status-dot {
  display: inline-block;
  width: 8px;
  height: 8px;
  background: #95a5a6;
}

.status-dot.online {
  background: #10b981;
}

/* 消息区域 */
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background: #f9fafb;
}

.message {
  display: flex;
  margin-bottom: 16px;
}

.message.mine {
  flex-direction: row-reverse;
}

.message .avatar {
  width: 36px;
  height: 36px;
  object-fit: cover;
  flex-shrink: 0;
}

.message-content {
  margin: 0 12px;
  max-width: 70%;
}

.message-bubble {
  padding: 10px 14px;
  background: #fff;
  border: 1px solid #e5e7eb;
  word-break: break-word;
  font-size: 14px;
}

.message.mine .message-bubble {
  background: #5a9bcf;
  border-color: #5a9bcf;
  color: #fff;
}

.message-time {
  font-size: 11px;
  color: #95a5a6;
  margin-top: 4px;
}

.message.mine .message-time {
  text-align: right;
}

.empty-message {
  text-align: center;
  padding: 48px 20px;
  color: #95a5a6;
}

/* 输入区域 */
.chat-input-area {
  display: flex;
  padding: 16px;
  border-top: 1px solid #e5e7eb;
  gap: 8px;
}

.chat-input {
  flex: 1;
  padding: 10px 12px;
  border: 1px solid #e5e7eb;
  font-size: 14px;
  outline: none;
}

.chat-input:focus {
  border-color: #5a9bcf;
}

.send-btn {
  background: #5a9bcf;
  color: #fff;
  border: none;
  padding: 10px 20px;
  cursor: pointer;
}

.send-btn:hover:not(:disabled) {
  background: #4a8bbf;
}

.send-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.empty-chat {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.empty-content {
  text-align: center;
  color: #95a5a6;
}

.empty-content i {
  font-size: 48px;
}

.empty-content p {
  margin: 12px 0 0 0;
}

/* 联系人弹窗 */
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

.contact-list {
  max-height: 400px;
  overflow-y: auto;
}

.contact-item {
  display: flex;
  align-items: center;
  padding: 12px;
  cursor: pointer;
  border-bottom: 1px solid #e5e7eb;
  transition: background 0.2s;
}

.contact-item:hover {
  background: #f9fafb;
}

.contact-item .avatar {
  width: 40px;
  height: 40px;
  object-fit: cover;
  margin-right: 12px;
}

.empty-contact {
  text-align: center;
  padding: 48px 20px;
  color: #95a5a6;
}

/* 响应式 */
@media (max-width: 768px) {
  .chat-header {
    padding: 30px 0;
  }
  
  .session-item .avatar {
    width: 40px;
    height: 40px;
  }
  
  .last-msg {
    max-width: 100px;
  }
  
  .chat-window {
    height: 500px;
  }
  
  .message-content {
    max-width: 85%;
  }
}
</style>