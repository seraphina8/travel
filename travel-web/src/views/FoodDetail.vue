<template>
  <div class="page-header food-detail-header">
    <div class="container">
      <nav aria-label="breadcrumb" class="mb-2">
        <ol class="breadcrumb breadcrumb-custom">
          <li class="breadcrumb-item">
            <router-link to="/"><i class="bi bi-house-door"></i> 首页</router-link>
          </li>
          <li class="breadcrumb-item">
            <router-link to="/food"><i class="bi bi-cup-hot"></i> 美食推荐</router-link>
          </li>
          <li class="breadcrumb-item active">{{ food.name }}</li>
        </ol>
      </nav>
      <h1 class="h3 fw-bold mb-1">{{ food.name || '美食详情' }}</h1>
      <p class="text-white-50 mb-0 small">
        <i class="bi bi-geo-alt"></i> {{ food.province }} · {{ food.city }}
      </p>
    </div>
  </div>
  
  <div class="container py-4">
    <div class="row g-4">
      <div class="col-lg-8">
        <div class="detail-card">
          <div class="food-cover">
            <img :src="food.coverImage || defaultImage" :alt="food.name">
          </div>
          
          <div class="detail-body">
            <h3 class="detail-title">{{ food.name }}</h3>
            
            <!-- 发布人信息 -->
            <div class="author-info" v-if="food.userId">
              <div class="author-avatar">
                <img :src="publisher.avatar || getAvatar(food.userId)" 
                     @click="goToUserProfile(food.userId)">
              </div>
              <div class="author-detail">
                <h6 @click="goToUserProfile(food.userId)">{{ publisher.nickname || publisher.username || '用户' + food.userId }}</h6>
                <small>{{ formatTime(food.createTime) }} 发布</small>
              </div>
              <div class="author-actions" v-if="food.userId !== currentUserId">
                <button class="action-btn chat" @click="startPrivateChat">
                  <i class="bi bi-chat-dots"></i> 私聊
                </button>
                <button class="action-btn follow" :class="{ followed: isFollowed }" @click="toggleFollowPublisher">
                  <i class="bi" :class="isFollowed ? 'bi-person-check' : 'bi-person-plus'"></i>
                  {{ isFollowed ? '已关注' : '关注' }}
                </button>
              </div>
            </div>
            
            <div class="food-meta">
              <span><i class="bi bi-geo-alt"></i> {{ food.province }} {{ food.city }}</span>
              <span v-if="food.address"><i class="bi bi-pin-map"></i> {{ food.address }}</span>
            </div>
            
            <div class="food-section">
              <h5 class="section-title">美食介绍</h5>
              <p class="food-desc">{{ food.description || '暂无介绍' }}</p>
            </div>
          </div>
        </div>
        
        <!-- 评论区 -->
        <div class="comment-card">
          <h5 class="comment-title">用户评论 ({{ comments.length }})</h5>
          
          <!-- 发表评论 -->
          <div class="comment-form" v-if="isLoggedIn">
            <div class="comment-form-wrapper">
              <img :src="userAvatar" class="comment-avatar">
              <div class="comment-input-area">
                <textarea 
                  class="comment-textarea" 
                  v-model="commentContent" 
                  rows="3" 
                  placeholder="分享你对这道美食的看法..."
                ></textarea>
                <div class="comment-actions">
                  <button class="submit-btn" @click="submitComment" :disabled="!commentContent.trim()">
                    <i class="bi bi-send"></i> 发表评论
                  </button>
                </div>
              </div>
            </div>
          </div>
          <div class="login-tip" v-else>
            <router-link to="/login">登录</router-link> 后可以发表评论
          </div>
          
          <!-- 评论列表 -->
          <div class="comment-list">
            <div class="comment-item" v-for="comment in comments" :key="comment.id">
              <img :src="comment.userAvatar || getAvatar(comment.userId)" class="comment-avatar" @click="goToUserProfile(comment.userId)">
              <div class="comment-content">
                <div class="comment-header">
                  <span class="username" @click="goToUserProfile(comment.userId)">{{ comment.username || '用户' + comment.userId }}</span>
                  <span class="time">{{ formatTime(comment.createTime) }}</span>
                </div>
                <p class="comment-text">{{ comment.content }}</p>
              </div>
            </div>
            <div class="empty-comments" v-if="comments.length === 0">
              <i class="bi bi-chat"></i>
              <p>暂无评论</p>
            </div>
          </div>
        </div>
      </div>
      
      <div class="col-lg-4">
        <!-- 操作按钮 -->
        <div class="action-card">
          <h5 class="card-title">操作</h5>
          <button class="action-btn-full collect" :class="{ collected: isCollected }" @click="toggleCollect">
            <i class="bi" :class="isCollected ? 'bi-heart-fill' : 'bi-heart'"></i>
            {{ isCollected ? '已收藏' : '收藏美食' }}
          </button>
          <button class="action-btn-full share" @click="shareFood">
            <i class="bi bi-share"></i> 分享美食
          </button>
        </div>
        
        <!-- 相关推荐 -->
        <div class="recommend-card">
          <h5 class="card-title">相关推荐</h5>
          <div class="recommend-list">
            <router-link 
              :to="`/food/${item.id}`" 
              class="recommend-item" 
              v-for="item in recommendFoods" 
              :key="item.id"
            >
              <img :src="item.coverImage || defaultImage" :alt="item.name">
              <div class="recommend-info">
                <h6>{{ item.name }}</h6>
                <small>{{ item.province }} · {{ item.city }}</small>
              </div>
            </router-link>
            <div class="empty-recommend" v-if="recommendFoods.length === 0">
              暂无推荐
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '../api'

const route = useRoute()
const router = useRouter()
const food = ref({})
const isCollected = ref(false)
const comments = ref([])
const commentContent = ref('')
const recommendFoods = ref([])
const publisher = ref({})
const isFollowed = ref(false)
const defaultImage = 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=800'

const isLoggedIn = computed(() => !!localStorage.getItem('userToken'))
const currentUserId = computed(() => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  return user.id
})
const userAvatar = computed(() => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  return user.avatar || `https://api.dicebear.com/7.x/avataaars/svg?seed=${user.id || 1}`
})

const getAvatar = (id) => `https://api.dicebear.com/7.x/avataaars/svg?seed=${id}`

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  return date.toLocaleDateString('zh-CN')
}

const goToUserProfile = (userId) => {
  if (userId) {
    router.push(`/user/${userId}`)
  }
}

const loadPublisher = async () => {
  if (!food.value.userId) return
  try {
    const res = await api.getUserInfo(food.value.userId)
    if (res.code === 200) {
      publisher.value = res.data || {}
    }
  } catch (e) {}
}

const checkFollowStatus = async () => {
  if (!isLoggedIn.value || !food.value.userId) return
  try {
    const res = await api.checkFollow(food.value.userId)
    if (res.code === 200) {
      isFollowed.value = res.data
    }
  } catch (e) {}
}

const toggleFollowPublisher = async () => {
  if (!isLoggedIn.value) {
    alert('请先登录')
    router.push('/login')
    return
  }
  try {
    const res = await api.toggleFollow(food.value.userId)
    if (res.code === 200) {
      isFollowed.value = !isFollowed.value
      alert(res.data)
    }
  } catch (e) {
    alert('操作失败')
  }
}

const startPrivateChat = () => {
  if (!isLoggedIn.value) {
    alert('请先登录')
    router.push('/login')
    return
  }
  sessionStorage.setItem('chatTarget', JSON.stringify({
    id: food.value.userId,
    nickname: publisher.value.nickname || publisher.value.username,
    avatar: publisher.value.avatar
  }))
  router.push('/chat')
}

const loadData = async () => {
  try {
    const res = await api.getFoodDetail(route.params.id)
    if (res.code === 200) {
      food.value = res.data
    }
  } catch (e) {
    console.error('加载美食详情失败', e)
  }
}

const loadComments = async () => {
  try {
    const res = await api.getComments('food', route.params.id)
    if (res.code === 200) {
      comments.value = res.data || []
    }
  } catch (e) {
    console.error('加载评论失败', e)
  }
}

const submitComment = async () => {
  if (!commentContent.value.trim()) return
  try {
    const res = await api.addComment({
      targetType: 'food',
      targetId: route.params.id,
      content: commentContent.value
    })
    if (res.code === 200) {
      commentContent.value = ''
      loadComments()
    } else {
      alert(res.message || '评论失败')
    }
  } catch (e) {
    alert('评论失败')
  }
}

const loadRecommend = async () => {
  try {
    const res = await api.getFoodList({ 
      pageSize: 5, 
      province: food.value.province 
    })
    if (res.code === 200) {
      recommendFoods.value = (res.data.records || [])
        .filter(item => item.id != route.params.id)
        .slice(0, 4)
    }
  } catch (e) {}
}

const checkCollect = async () => {
  if (!localStorage.getItem('userToken')) return
  try {
    const res = await api.checkCollect(route.params.id, 4)
    if (res.code === 200) {
      isCollected.value = res.data
    }
  } catch (e) {}
}

const toggleCollect = async () => {
  if (!localStorage.getItem('userToken')) {
    alert('请先登录')
    return
  }
  try {
    const res = await api.toggleCollect(route.params.id, 4)
    if (res.code === 200) {
      isCollected.value = !isCollected.value
      alert(res.data)
    }
  } catch (e) {
    alert('操作失败')
  }
}

const shareFood = () => {
  const url = window.location.href
  if (navigator.share) {
    navigator.share({ title: food.value.name, url }).catch(() => {})
  } else {
    navigator.clipboard.writeText(url)
    alert('链接已复制到剪贴板')
  }
}

const recordView = async () => {
  if (localStorage.getItem('userToken')) {
    try {
      await api.recordBehavior(route.params.id, 4, 1)
    } catch (e) {}
  }
}

watch(() => route.params.id, (newId) => {
  if (newId) {
    loadData()
    loadComments()
    checkCollect()
    recordView()
  }
})

onMounted(async () => {
  await loadData()
  loadPublisher()
  checkFollowStatus()
  loadComments()
  loadRecommend()
  recordView()
  checkCollect()
})
</script>

<style scoped>
/* 统一蓝色主题 */
.food-detail-header {
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
}

.breadcrumb-custom .breadcrumb-item a:hover {
  color: #fff;
}

.breadcrumb-custom .breadcrumb-item.active {
  color: #fff;
}

.breadcrumb-custom .breadcrumb-item + .breadcrumb-item::before {
  color: rgba(255, 255, 255, 0.7);
  content: "/";
}

.food-detail-header h1 {
  color: #fff;
}

/* 详情卡片 - 直角设计 */
.detail-card {
  background: #fff;
  border: 1px solid #e5e7eb;
}

.food-cover {
  height: 350px;
  overflow: hidden;
  background: #f9fafb;
}

.food-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.detail-body {
  padding: 24px;
}

.detail-title {
  font-size: 24px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 16px 0;
}

/* 作者信息 */
.author-info {
  display: flex;
  align-items: center;
  padding: 16px 0;
  margin-bottom: 16px;
  border-top: 1px solid #e5e7eb;
  border-bottom: 1px solid #e5e7eb;
}

.author-avatar img {
  width: 50px;
  height: 50px;
  object-fit: cover;
  cursor: pointer;
  margin-right: 12px;
}

.author-detail {
  flex: 1;
}

.author-detail h6 {
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 4px 0;
  cursor: pointer;
}

.author-detail h6:hover {
  color: #5a9bcf;
}

.author-detail small {
  font-size: 12px;
  color: #95a5a6;
}

.author-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  padding: 6px 16px;
  border: 1px solid #e5e7eb;
  background: #fff;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.2s;
}

.action-btn.chat:hover {
  border-color: #5a9bcf;
  color: #5a9bcf;
}

.action-btn.follow {
  background: #5a9bcf;
  border-color: #5a9bcf;
  color: #fff;
}

.action-btn.follow:hover {
  background: #4a8bbf;
}

.action-btn.follow.followed {
  background: #fff;
  border-color: #e5e7eb;
  color: #7f8c8d;
}

.action-btn.follow.followed:hover {
  border-color: #5a9bcf;
  color: #5a9bcf;
}

/* 美食信息 */
.food-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-bottom: 24px;
}

.food-meta span {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #7f8c8d;
}

.food-section {
  margin-top: 24px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 16px 0;
  padding-bottom: 8px;
  border-bottom: 2px solid #5a9bcf;
  display: inline-block;
}

.food-desc {
  color: #7f8c8d;
  line-height: 1.8;
  margin: 0;
}

/* 评论卡片 */
.comment-card {
  background: #fff;
  border: 1px solid #e5e7eb;
  margin-top: 24px;
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

.comment-form {
  margin-bottom: 24px;
}

.comment-form-wrapper {
  display: flex;
  gap: 12px;
}

.comment-avatar {
  width: 40px;
  height: 40px;
  object-fit: cover;
  flex-shrink: 0;
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

.comment-actions {
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

.login-tip a {
  color: #5a9bcf;
  text-decoration: none;
}

/* 评论列表 */
.comment-list {
  margin-top: 24px;
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

.comment-item .comment-avatar {
  cursor: pointer;
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

/* 操作卡片 */
.action-card {
  background: #fff;
  border: 1px solid #e5e7eb;
  padding: 20px;
  margin-bottom: 24px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 16px 0;
  padding-bottom: 8px;
  border-bottom: 2px solid #5a9bcf;
  display: inline-block;
}

.action-btn-full {
  width: 100%;
  padding: 10px;
  border: 1px solid #e5e7eb;
  background: #fff;
  cursor: pointer;
  margin-bottom: 12px;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

.action-btn-full:last-child {
  margin-bottom: 0;
}

.action-btn-full.collect:hover {
  border-color: #e74c3c;
  color: #e74c3c;
}

.action-btn-full.collect.collected {
  background: #e74c3c;
  border-color: #e74c3c;
  color: #fff;
}

.action-btn-full.share:hover {
  border-color: #5a9bcf;
  color: #5a9bcf;
}

/* 推荐卡片 */
.recommend-card {
  background: #fff;
  border: 1px solid #e5e7eb;
  padding: 20px;
}

.recommend-list {
  margin-top: 8px;
}

.recommend-item {
  display: flex;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #e5e7eb;
  text-decoration: none;
  color: inherit;
  transition: all 0.2s;
}

.recommend-item:last-child {
  border-bottom: none;
}

.recommend-item:hover {
  background: #f9fafb;
  margin: 0 -12px;
  padding: 12px;
}

.recommend-item img {
  width: 60px;
  height: 60px;
  object-fit: cover;
}

.recommend-info {
  flex: 1;
}

.recommend-info h6 {
  margin: 0 0 4px;
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
}

.recommend-info small {
  font-size: 11px;
  color: #95a5a6;
}

.empty-recommend {
  text-align: center;
  padding: 24px;
  color: #95a5a6;
  font-size: 13px;
}

/* 响应式 */
@media (max-width: 768px) {
  .food-detail-header {
    padding: 30px 0;
  }
  
  .food-cover {
    height: 250px;
  }
  
  .detail-body {
    padding: 16px;
  }
  
  .author-info {
    flex-wrap: wrap;
  }
  
  .author-actions {
    width: 100%;
    margin-top: 12px;
  }
  
  .action-btn {
    flex: 1;
    text-align: center;
  }
  
  .comment-card {
    padding: 16px;
  }
  
  .comment-form-wrapper {
    flex-direction: column;
  }
}
</style>