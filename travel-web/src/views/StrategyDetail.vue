<template>
  <div class="detail-header">
    <div class="container py-4">
      <nav aria-label="breadcrumb">
        <ol class="breadcrumb breadcrumb-custom">
          <li class="breadcrumb-item"><router-link to="/">首页</router-link></li>
          <li class="breadcrumb-item"><router-link to="/strategy">攻略</router-link></li>
          <li class="breadcrumb-item active">攻略详情</li>
        </ol>
      </nav>
    </div>
    
    <div class="container pb-5">
      <div class="row justify-content-center">
        <div class="col-lg-8">
          <div class="content-card">
            <!-- Header -->
            <h1 class="strategy-title">{{ strategy.title }}</h1>
            
            <div class="strategy-meta">
              <div class="author-info">
                <img :src="'https://api.dicebear.com/7.x/avataaars/svg?seed=' + strategy.userId" 
                     class="author-avatar" 
                     @click="goToUserProfile(strategy.userId)">
                <div>
                  <h6 class="author-name" @click="goToUserProfile(strategy.userId)">{{ strategy.username || '旅行达人' }}</h6>
                  <small class="publish-time">{{ formatDate(strategy.createTime) }}</small>
                </div>
              </div>
              
              <div class="stats-info">
                <span><i class="bi bi-eye"></i>{{ strategy.viewCount || 0 }}</span>
                <span><i class="bi bi-heart"></i>{{ strategy.likeCount || 0 }}</span>
              </div>
            </div>
            
            <!-- Cover -->
            <div class="cover-image" v-if="strategy.coverImage">
              <img :src="strategy.coverImage" alt="">
            </div>
            
            <!-- Tags -->
            <div class="tags-wrapper" v-if="strategy.tags">
              <span class="tag" v-for="tag in strategy.tags.split(',')" :key="tag">
                {{ tag }}
              </span>
            </div>
            
            <!-- Content -->
            <div class="strategy-content">
              <p v-for="(para, index) in contentParagraphs" :key="index">{{ para }}</p>
            </div>
            
            <!-- Actions -->
            <div class="action-bar">
              <button class="action-btn like" :class="{ liked: liked }" @click="handleLike">
                <i class="bi" :class="liked ? 'bi-heart-fill' : 'bi-heart'"></i>
                {{ liked ? '已点赞' : '点赞' }}
              </button>
              <button class="action-btn collect" :class="{ collected: collected }" @click="handleCollect">
                <i class="bi" :class="collected ? 'bi-bookmark-fill' : 'bi-bookmark'"></i>
                {{ collected ? '已收藏' : '收藏' }}
              </button>
              <button class="action-btn share" @click="handleShare">
                <i class="bi bi-share"></i> 分享
              </button>
            </div>
          </div>
          
          <!-- Comments -->
          <div class="comment-card">
            <h5 class="comment-title">评论 ({{ comments.length }})</h5>
            
            <!-- 发表评论 -->
            <div class="comment-form" v-if="isLoggedIn">
              <textarea 
                class="comment-textarea" 
                v-model="commentContent" 
                rows="3" 
                placeholder="写下你的评论..."
              ></textarea>
              <div class="comment-footer">
                <small>{{ commentContent.length }}/500</small>
                <button 
                  class="submit-btn" 
                  @click="submitComment" 
                  :disabled="!commentContent.trim()"
                >
                  发表评论
                </button>
              </div>
            </div>
            <div class="login-tip" v-else>
              <router-link to="/login" class="login-link">登录</router-link> 后可以发表评论
            </div>
            
            <!-- 评论列表 -->
            <div class="comment-list">
              <div v-if="comments.length === 0" class="empty-comments">
                <i class="bi bi-chat"></i>
                <p>暂无评论</p>
              </div>
              
              <div v-for="comment in comments" :key="comment.id" class="comment-item">
                <div class="comment-avatar" @click="goToUserProfile(comment.userId)">
                  <img :src="comment.userAvatar || 'https://api.dicebear.com/7.x/avataaars/svg?seed=' + comment.userId" alt="">
                </div>
                <div class="comment-body">
                  <div class="comment-header">
                    <span class="comment-username" @click="goToUserProfile(comment.userId)">{{ comment.username || '匿名用户' }}</span>
                    <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
                  </div>
                  <p class="comment-text">{{ comment.content }}</p>
                  <div class="comment-actions">
                    <button class="delete-btn" v-if="canDelete(comment)" @click="deleteComment(comment.id)">
                      <i class="bi bi-trash"></i> 删除
                    </button>
                  </div>
                </div>
              </div>
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
const strategy = ref({})
const liked = ref(false)
const collected = ref(false)
const comments = ref([])
const commentContent = ref('')

const isLoggedIn = computed(() => !!localStorage.getItem('userToken'))
const currentUserId = computed(() => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  return user.id
})

const contentParagraphs = computed(() => {
  if (!strategy.value.content) return ['暂无内容']
  return strategy.value.content.split('\n').filter(p => p.trim())
})

const loadData = async () => {
  const id = route.params.id
  const res = await api.getStrategyDetail(id)
  if (res.code === 200) {
    strategy.value = res.data
  }
}

const handleLike = () => {
  if (!localStorage.getItem('userToken')) {
    router.push('/login')
    return
  }
  liked.value = !liked.value
}

const handleCollect = async () => {
  if (!localStorage.getItem('userToken')) {
    router.push('/login')
    return
  }
  try {
    const res = await api.toggleCollect(route.params.id, 2)
    if (res.code === 200) {
      collected.value = !collected.value
      alert(res.data)
    }
  } catch (e) {
    alert('操作失败')
  }
}

const checkCollect = async () => {
  if (!localStorage.getItem('userToken')) return
  try {
    const res = await api.checkCollect(route.params.id, 2)
    if (res.code === 200) {
      collected.value = res.data
    }
  } catch (e) {}
}

const handleShare = () => {
  const url = window.location.href
  if (navigator.share) {
    navigator.share({ title: strategy.value.title, url }).catch(() => {})
  } else {
    navigator.clipboard.writeText(url)
    alert('链接已复制到剪贴板')
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

const goToUserProfile = (userId) => {
  if (userId) {
    router.push(`/user/${userId}`)
  }
}

const recordView = async () => {
  if (localStorage.getItem('userToken')) {
    try {
      await api.recordBehavior(route.params.id, 2, 1)
    } catch (e) {}
  }
}

const loadComments = async () => {
  try {
    const res = await api.getComments('strategy', route.params.id)
    if (res.code === 200) {
      comments.value = res.data || []
    }
  } catch (e) {
    console.error('加载评论失败', e)
  }
}

const submitComment = async () => {
  if (!commentContent.value.trim()) return
  if (!isLoggedIn.value) {
    alert('请先登录')
    return
  }
  
  try {
    const res = await api.addComment({
      targetType: 'strategy',
      targetId: route.params.id,
      content: commentContent.value.trim()
    })
    if (res.code === 200) {
      commentContent.value = ''
      loadComments()
      alert('评论成功')
    } else {
      alert(res.message || '评论失败')
    }
  } catch (e) {
    alert('评论失败')
  }
}

const deleteComment = async (id) => {
  if (!confirm('确定要删除这条评论吗？')) return
  
  try {
    const res = await api.deleteComment(id)
    if (res.code === 200) {
      loadComments()
    } else {
      alert(res.message || '删除失败')
    }
  } catch (e) {
    alert('删除失败')
  }
}

const canDelete = (comment) => {
  return currentUserId.value && comment.userId === currentUserId.value
}

const formatTime = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  if (diff < 2592000000) return Math.floor(diff / 86400000) + '天前'
  return date.toLocaleDateString('zh-CN')
}

onMounted(() => {
  loadData()
  recordView()
  checkCollect()
  loadComments()
})
</script>

<style scoped>
/* 统一蓝色主题 */
.detail-header {
  background: #fff;
  padding-top: 20px;
}

.breadcrumb-custom {
  background: #f9fafb;
  padding: 6px 16px;
  display: inline-flex;
  font-size: 13px;
  margin-bottom: 0;
  border: 1px solid #e5e7eb;
}

.breadcrumb-custom .breadcrumb-item a {
  color: #5a9bcf;
  text-decoration: none;
}

.breadcrumb-custom .breadcrumb-item a:hover {
  color: #4a8bbf;
}

.breadcrumb-custom .breadcrumb-item.active {
  color: #7f8c8d;
}

.breadcrumb-custom .breadcrumb-item + .breadcrumb-item::before {
  color: #95a5a6;
  content: "/";
}

/* 内容卡片 - 直角设计 */
.content-card {
  background: #fff;
  border: 1px solid #e5e7eb;
  padding: 32px;
  margin-top: 24px;
}

.strategy-title {
  font-size: 28px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 20px 0;
}

/* 元信息区域 */
.strategy-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 20px;
  margin-bottom: 24px;
  border-bottom: 1px solid #e5e7eb;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.author-avatar {
  width: 50px;
  height: 50px;
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

.publish-time {
  font-size: 12px;
  color: #95a5a6;
}

.stats-info {
  display: flex;
  gap: 16px;
  color: #95a5a6;
  font-size: 13px;
}

.stats-info span {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

/* 封面图片 */
.cover-image {
  margin-bottom: 24px;
  border: 1px solid #e5e7eb;
  overflow: hidden;
}

.cover-image img {
  width: 100%;
  max-height: 400px;
  object-fit: cover;
}

/* 标签 */
.tags-wrapper {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 24px;
}

.tag {
  background: #e8f0f7;
  color: #5a9bcf;
  padding: 4px 12px;
  font-size: 12px;
}

/* 内容区域 */
.strategy-content {
  line-height: 1.8;
  font-size: 16px;
  color: #7f8c8d;
  margin-bottom: 32px;
}

.strategy-content p {
  margin-bottom: 16px;
}

/* 操作按钮 */
.action-bar {
  display: flex;
  justify-content: center;
  gap: 16px;
  padding-top: 24px;
  border-top: 1px solid #e5e7eb;
}

.action-btn {
  padding: 10px 28px;
  border: 1px solid #e5e7eb;
  background: #fff;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.action-btn.like:hover {
  border-color: #e74c3c;
  color: #e74c3c;
}

.action-btn.like.liked {
  background: #e74c3c;
  border-color: #e74c3c;
  color: #fff;
}

.action-btn.collect:hover {
  border-color: #5a9bcf;
  color: #5a9bcf;
}

.action-btn.collect.collected {
  background: #5a9bcf;
  border-color: #5a9bcf;
  color: #fff;
}

.action-btn.share:hover {
  border-color: #5a9bcf;
  color: #5a9bcf;
}

/* 评论卡片 */
.comment-card {
  background: #fff;
  border: 1px solid #e5e7eb;
  padding: 24px;
  margin-top: 24px;
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

.comment-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
}

.comment-footer small {
  font-size: 12px;
  color: #95a5a6;
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

.comment-avatar {
  cursor: pointer;
  flex-shrink: 0;
}

.comment-avatar img {
  width: 40px;
  height: 40px;
  object-fit: cover;
}

.comment-body {
  flex: 1;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 6px;
  flex-wrap: wrap;
}

.comment-username {
  font-weight: 600;
  font-size: 13px;
  color: #2c3e50;
  cursor: pointer;
}

.comment-username:hover {
  color: #5a9bcf;
}

.comment-time {
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
  .content-card {
    padding: 20px;
  }
  
  .strategy-title {
    font-size: 24px;
  }
  
  .strategy-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .action-bar {
    flex-direction: column;
  }
  
  .action-btn {
    width: 100%;
    justify-content: center;
  }
  
  .comment-item {
    flex-direction: column;
  }
}
</style>