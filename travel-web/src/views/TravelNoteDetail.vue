<template>
  <div class="page-header">
    <div class="container">
      <h1>{{ note.title || '游记详情' }}</h1>
      <p class="text-white-50">
        <i class="bi bi-geo-alt"></i> {{ note.province }} · {{ note.city }}
      </p>
    </div>
  </div>
  
  <div class="container py-4">
    <div class="row justify-content-center">
      <div class="col-lg-8">
        <div class="content-card">
          <!-- 作者信息 -->
          <div class="author-info">
            <img :src="note.userAvatar || getAvatar(note.userId)" class="avatar" @click="goToUserProfile(note.userId)">
            <div>
              <h6 class="author-name" @click="goToUserProfile(note.userId)">{{ note.username || '旅行者' }}</h6>
              <small class="author-time">{{ formatTime(note.createTime) }}</small>
            </div>
            <span class="province-badge">{{ note.province }}</span>
          </div>
          
          <!-- 标题 -->
          <h3 class="note-title">{{ note.title }}</h3>
          
          <!-- 内容 -->
          <div class="note-content" v-html="displayContent"></div>
          
          <!-- 图片 -->
          <div class="note-images" v-if="images.length > 0">
            <div class="image-grid">
              <div class="image-item" v-for="(img, idx) in images" :key="idx">
                <img :src="img" class="image-preview">
              </div>
            </div>
          </div>
          
          <!-- 统计和操作 -->
          <div class="note-footer">
            <div class="stats">
              <span><i class="bi bi-eye"></i> {{ note.viewCount || 0 }}</span>
              <span 
                class="like-btn" 
                :class="{ liked: isLiked }" 
                @click="toggleLike"
              >
                <i class="bi" :class="isLiked ? 'bi-heart-fill' : 'bi-heart'"></i> 
                {{ likeCount }}
              </span>
            </div>
            <button class="share-btn" @click="shareNote">
              <i class="bi bi-share"></i> 分享
            </button>
          </div>
        </div>
        
        <!-- 评论区 -->
        <div class="comment-card">
          <h5 class="comment-title">评论 ({{ comments.length }})</h5>
          
          <!-- 发表评论 -->
          <div class="comment-form" v-if="isLoggedIn">
            <div class="comment-input-wrapper">
              <img :src="currentUserAvatar" class="avatar-sm">
              <div class="comment-input-area">
                <textarea 
                  class="comment-textarea" 
                  v-model="commentContent" 
                  rows="2" 
                  placeholder="写下你的评论..."
                ></textarea>
                <div class="comment-submit">
                  <button class="submit-btn" @click="submitComment" :disabled="!commentContent.trim()">
                    <i class="bi bi-send"></i> 发表评论
                  </button>
                </div>
              </div>
            </div>
          </div>
          <div class="login-tip" v-else>
            <router-link to="/login" class="login-link">登录</router-link> 后参与评论
          </div>
          
          <!-- 评论列表 -->
          <div class="comment-list">
            <div class="comment-item" v-for="comment in comments" :key="comment.id">
              <img :src="comment.userAvatar || getAvatar(comment.userId)" class="avatar-sm" @click="goToUserProfile(comment.userId)">
              <div class="comment-content">
                <div class="comment-header">
                  <span class="username" @click="goToUserProfile(comment.userId)">{{ comment.username || '用户' }}</span>
                  <span class="time">{{ formatTime(comment.createTime) }}</span>
                </div>
                <p class="comment-text">{{ comment.content }}</p>
                <div class="comment-actions" v-if="comment.userId === currentUserId">
                  <button class="delete-btn" @click="deleteComment(comment.id)">
                    <i class="bi bi-trash"></i> 删除
                  </button>
                </div>
              </div>
            </div>
            
            <div class="empty-comments" v-if="comments.length === 0">
              <i class="bi bi-chat"></i>
              <p>暂无评论</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '../api'

const route = useRoute()
const router = useRouter()
const note = ref({})
const comments = ref([])
const commentContent = ref('')
const isLiked = ref(false)
const likeCount = ref(0)

const isLoggedIn = computed(() => !!localStorage.getItem('userToken'))
const currentUser = computed(() => JSON.parse(localStorage.getItem('user') || '{}'))
const currentUserId = computed(() => currentUser.value.id)
const currentUserAvatar = computed(() => 
  currentUser.value.avatar || `https://api.dicebear.com/7.x/avataaars/svg?seed=${currentUser.value.username}`
)

const images = computed(() => {
  if (!note.value.images) return []
  try {
    return JSON.parse(note.value.images)
  } catch {
    return []
  }
})

const displayContent = computed(() => {
  return note.value.content || ''
})

const getAvatar = (id) => `https://api.dicebear.com/7.x/avataaars/svg?seed=${id}`

const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleDateString('zh-CN')
}

const goToUserProfile = (userId) => {
  if (userId) {
    router.push(`/user/${userId}`)
  }
}

const loadData = async () => {
  try {
    const res = await api.getTravelNoteDetail(route.params.id)
    if (res.code === 200) {
      note.value = res.data
      likeCount.value = res.data.likeCount || 0
    }
  } catch (e) {
    console.error('加载游记详情失败', e)
  }
}

const loadComments = async () => {
  try {
    const res = await api.getNoteComments(route.params.id)
    if (res.code === 200) {
      comments.value = res.data || []
    }
  } catch (e) {
    console.error('加载评论失败', e)
  }
}

const checkLikeStatus = async () => {
  if (!isLoggedIn.value) return
  try {
    const res = await api.checkNoteLike(route.params.id)
    if (res.code === 200) {
      isLiked.value = res.data
    }
  } catch (e) {}
}

const toggleLike = async () => {
  if (!isLoggedIn.value) {
    router.push('/login')
    return
  }
  try {
    const res = await api.toggleNoteLike(route.params.id)
    if (res.code === 200) {
      isLiked.value = res.data.liked
      likeCount.value = res.data.likeCount
    }
  } catch (e) {
    alert('操作失败')
  }
}

const submitComment = async () => {
  if (!commentContent.value.trim()) return
  try {
    const res = await api.addNoteComment(route.params.id, commentContent.value)
    if (res.code === 200) {
      commentContent.value = ''
      loadComments()
    } else {
      alert(res.message)
    }
  } catch (e) {
    alert('评论失败')
  }
}

const deleteComment = async (commentId) => {
  if (!confirm('确定删除这条评论吗？')) return
  try {
    const res = await api.deleteNoteComment(commentId)
    if (res.code === 200) {
      loadComments()
    }
  } catch (e) {
    alert('删除失败')
  }
}

const shareNote = () => {
  const url = window.location.href
  if (navigator.share) {
    navigator.share({ title: note.value.title, url }).catch(() => {})
  } else {
    navigator.clipboard.writeText(url)
    alert('链接已复制到剪贴板')
  }
}

onMounted(() => {
  loadData()
  loadComments()
  checkLikeStatus()
})
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
  margin: 0 0 8px 0;
}

.page-header p {
  color: rgba(255, 255, 255, 0.8);
  margin: 0;
}

/* 内容卡片 - 直角设计 */
.content-card {
  background: #fff;
  border: 1px solid #e5e7eb;
  padding: 24px;
  margin-bottom: 24px;
}

/* 作者信息 */
.author-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e5e7eb;
}

.author-info .avatar {
  width: 48px;
  height: 48px;
  object-fit: cover;
  cursor: pointer;
}

.author-name {
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 4px 0;
  cursor: pointer;
}

.author-name:hover {
  color: #5a9bcf;
}

.author-time {
  font-size: 11px;
  color: #95a5a6;
}

.province-badge {
  background: #e8f0f7;
  color: #5a9bcf;
  padding: 4px 12px;
  font-size: 12px;
  font-weight: 500;
  margin-left: auto;
}

/* 标题 */
.note-title {
  font-size: 24px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 16px 0;
}

/* 内容 */
.note-content {
  font-size: 15px;
  line-height: 1.8;
  color: #7f8c8d;
  margin-bottom: 20px;
}

/* 图片网格 */
.note-images {
  margin-bottom: 20px;
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 12px;
}

.image-item {
  border: 1px solid #e5e7eb;
  overflow: hidden;
}

.image-preview {
  width: 100%;
  height: 150px;
  object-fit: cover;
}

/* 底部统计 */
.note-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid #e5e7eb;
}

.stats {
  display: flex;
  gap: 20px;
  color: #95a5a6;
  font-size: 14px;
}

.stats span {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.like-btn {
  cursor: pointer;
  transition: color 0.2s;
}

.like-btn:hover {
  color: #e74c3c;
}

.like-btn.liked {
  color: #e74c3c;
}

.share-btn {
  background: none;
  border: 1px solid #e5e7eb;
  padding: 6px 16px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
  color: #7f8c8d;
}

.share-btn:hover {
  border-color: #5a9bcf;
  color: #5a9bcf;
}

/* 评论卡片 */
.comment-card {
  background: #fff;
  border: 1px solid #e5e7eb;
  padding: 24px;
}

.comment-title {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 20px 0;
  padding-bottom: 8px;
  border-bottom: 2px solid #5a9bcf;
  display: inline-block;
}

/* 评论表单 */
.comment-form {
  margin-bottom: 24px;
}

.comment-input-wrapper {
  display: flex;
  gap: 12px;
}

.avatar-sm {
  width: 40px;
  height: 40px;
  object-fit: cover;
  flex-shrink: 0;
  cursor: pointer;
}

.comment-input-area {
  flex: 1;
}

.comment-textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #e5e7eb;
  font-size: 14px;
  font-family: inherit;
  resize: vertical;
  outline: none;
}

.comment-textarea:focus {
  border-color: #5a9bcf;
}

.comment-submit {
  display: flex;
  justify-content: flex-end;
  margin-top: 8px;
}

.submit-btn {
  background: #5a9bcf;
  color: #fff;
  border: none;
  padding: 6px 20px;
  font-size: 13px;
  cursor: pointer;
}

.submit-btn:hover:not(:disabled) {
  background: #4a8bbf;
}

.submit-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.login-tip {
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  padding: 12px;
  text-align: center;
  font-size: 13px;
  color: #7f8c8d;
  margin-bottom: 24px;
}

.login-link {
  color: #5a9bcf;
  text-decoration: none;
}

.login-link:hover {
  color: #4a8bbf;
  text-decoration: underline;
}

/* 评论列表 */
.comment-list {
  margin-top: 16px;
}

.comment-item {
  display: flex;
  gap: 12px;
  padding: 16px 0;
  border-top: 1px solid #e5e7eb;
}

.comment-item:first-child {
  border-top: none;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 6px;
  flex-wrap: wrap;
}

.comment-header .username {
  font-weight: 600;
  font-size: 13px;
  color: #2c3e50;
  cursor: pointer;
}

.comment-header .username:hover {
  color: #5a9bcf;
}

.comment-header .time {
  font-size: 11px;
  color: #95a5a6;
}

.comment-text {
  margin: 0;
  color: #7f8c8d;
  font-size: 13px;
  line-height: 1.5;
}

.comment-actions {
  margin-top: 8px;
}

.delete-btn {
  background: none;
  border: none;
  color: #95a5a6;
  font-size: 12px;
  cursor: pointer;
  padding: 0;
}

.delete-btn:hover {
  color: #e74c3c;
}

.empty-comments {
  text-align: center;
  padding: 48px 20px;
  color: #95a5a6;
}

.empty-comments i {
  font-size: 48px;
}

.empty-comments p {
  margin: 12px 0 0 0;
}

/* 响应式 */
@media (max-width: 768px) {
  .page-header {
    padding: 30px 0;
  }
  
  .page-header h1 {
    font-size: 24px;
  }
  
  .content-card {
    padding: 16px;
  }
  
  .note-title {
    font-size: 20px;
  }
  
  .image-grid {
    grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  }
  
  .image-preview {
    height: 120px;
  }
  
  .comment-input-wrapper {
    flex-direction: column;
  }
  
  .avatar-sm {
    width: 32px;
    height: 32px;
  }
}
</style>
