<template>
  <div class="page-header">
    <div class="container">
      <h1>{{ attraction.name || '景点详情' }}</h1>
      <p class="text-white-50">
        <i class="bi bi-geo-alt"></i> {{ attraction.province }} · {{ attraction.city }}
      </p>
    </div>
  </div>
  
  <div class="container py-4">
    <div class="row g-4">
      <!-- 左侧：景点信息 -->
      <div class="col-lg-8">
        <div class="attraction-card">
          <div class="attraction-cover">
            <img :src="attraction.coverImage || defaultImage" :alt="attraction.name">
          </div>
          
          <div class="attraction-body">
            <div class="attraction-header">
              <h3 class="attraction-title">{{ attraction.name }}</h3>
              <div class="action-buttons">
                <button class="btn-action" @click="toggleCollect">
                  <i class="bi" :class="isCollected ? 'bi-heart-fill' : 'bi-heart'"></i>
                  {{ isCollected ? '已收藏' : '收藏' }}
                </button>
                <button class="btn-action" @click="showShare">
                  <i class="bi bi-share"></i> 分享
                </button>
              </div>
            </div>
            
            <div class="stats-row">
              <span><i class="bi bi-eye"></i> 浏览 {{ attraction.viewCount || 0 }}</span>
              <span><i class="bi bi-heart"></i> 收藏 {{ attraction.collectCount || 0 }}</span>
            </div>
            
            <div class="section-block">
              <h5 class="section-title">景点介绍</h5>
              <p class="attraction-desc">{{ attraction.description || '暂无介绍' }}</p>
            </div>
            
            <!-- 评论区 -->
            <div class="section-block">
              <h5 class="section-title">评论 ({{ comments.length }})</h5>
              
              <!-- 发表评论 -->
              <div class="comment-form">
                <textarea 
                  class="form-control" 
                  v-model="commentContent" 
                  rows="3" 
                  placeholder="写下你的评论..."
                  :disabled="!isLoggedIn"
                ></textarea>
                <div class="comment-form-footer">
                  <small class="text-muted" v-if="!isLoggedIn">请先登录后再评论</small>
                  <small class="text-muted" v-else>{{ commentContent.length }}/500</small>
                  <button 
                    class="btn-submit" 
                    @click="submitComment" 
                    :disabled="!commentContent.trim() || !isLoggedIn"
                  >
                    发表评论
                  </button>
                </div>
              </div>
              
              <!-- 评论列表 -->
              <div class="comment-list">
                <div v-if="comments.length === 0" class="empty-comment">
                  <i class="bi bi-chat"></i>
                  <p>暂无评论</p>
                </div>
                
                <div v-for="comment in comments" :key="comment.id" class="comment-item">
                  <div class="comment-avatar">
                    <img :src="comment.userAvatar || defaultAvatar" alt="">
                  </div>
                  <div class="comment-content">
                    <div class="comment-header">
                      <span class="comment-username">{{ comment.username || '匿名用户' }}</span>
                      <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
                    </div>
                    <p class="comment-text">{{ comment.content }}</p>
                    <div class="comment-actions">
                      <button class="btn-delete" v-if="canDelete(comment)" @click="deleteComment(comment.id)">
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
      
      <!-- 右侧：地图 -->
      <div class="col-lg-4">
        <div class="info-card">
          <h5 class="info-title">位置信息</h5>
          <div id="attractionMap" class="map-container"></div>
          <div class="info-content">
            <p class="info-item"><strong>经度：</strong>{{ attraction.longitude || '暂无' }}</p>
            <p class="info-item"><strong>纬度：</strong>{{ attraction.latitude || '暂无' }}</p>
            <a :href="getNavUrl()" target="_blank" class="btn-nav">
              <i class="bi bi-navigation"></i> 导航前往
            </a>
          </div>
        </div>
      </div>
    </div>
  </div>
  
  <!-- 分享弹窗 -->
  <ShareModal ref="shareModal" :title="attraction.name" :description="attraction.description" :url="shareUrl" :image="attraction.coverImage" />
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import AMapLoader from '@amap/amap-jsapi-loader'
import api from '../api'
import ShareModal from '../components/ShareModal.vue'

const route = useRoute()
const attraction = ref({})
const isCollected = ref(false)
const shareModal = ref(null)
const defaultImage = 'https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=800'
const defaultAvatar = 'https://via.placeholder.com/40'
const comments = ref([])
const commentContent = ref('')

let map = null

const shareUrl = computed(() => window.location.href)
const isLoggedIn = computed(() => !!localStorage.getItem('userToken'))
const currentUserId = computed(() => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  return user.id
})

const loadData = async () => {
  try {
    const res = await api.getAttractionDetail(route.params.id)
    if (res.code === 200) {
      attraction.value = res.data
    }
  } catch (e) {
    console.error('加载景点详情失败', e)
  }
}

const checkCollect = async () => {
  if (!localStorage.getItem('userToken')) return
  try {
    const res = await api.checkCollect(route.params.id, 1)
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
    const res = await api.toggleCollect(route.params.id, 1)
    if (res.code === 200) {
      isCollected.value = !isCollected.value
      alert(res.data)
    }
  } catch (e) {
    alert('操作失败')
  }
}

const showShare = () => {
  if (shareModal.value) {
    shareModal.value.show()
  }
}

const getNavUrl = () => {
  if (!attraction.value.longitude || !attraction.value.latitude) return '#'
  return `https://uri.amap.com/marker?position=${attraction.value.longitude},${attraction.value.latitude}&name=${encodeURIComponent(attraction.value.name)}`
}

const initMap = async () => {
  if (!attraction.value.longitude || !attraction.value.latitude) return
  
  try {
    window._AMapSecurityConfig = {
      securityJsCode: '734d51563a3b2c4a2433ae3481309408'
    }
    
    const AMap = await AMapLoader.load({
      key: 'd201b9704d5bff29ddd4510c45c75cf4',
      version: '2.0',
      plugins: ['AMap.Marker']
    })
    
    map = new AMap.Map('attractionMap', {
      zoom: 14,
      center: [parseFloat(attraction.value.longitude), parseFloat(attraction.value.latitude)]
    })
    
    new AMap.Marker({
      position: [parseFloat(attraction.value.longitude), parseFloat(attraction.value.latitude)],
      title: attraction.value.name,
      map: map
    })
  } catch (e) {
    console.error('地图加载失败', e)
  }
}

watch(() => attraction.value.id, (newId) => {
  if (newId && attraction.value.longitude) {
    setTimeout(initMap, 100)
  }
})

const recordView = async () => {
  if (localStorage.getItem('userToken')) {
    try {
      await api.recordBehavior(route.params.id, 5, 1)
    } catch (e) {}
  }
}

const loadComments = async () => {
  try {
    const res = await api.getComments('attraction', route.params.id)
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
      targetType: 'attraction',
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

onUnmounted(() => {
  if (map) {
    map.destroy()
  }
})
</script>

<style scoped>
/* 统一颜色变量 */
:root {
  --primary-blue: #5a9bcf;
  --primary-dark: #4a8bbf;
  --text-dark: #2c3e50;
  --text-gray: #7f8c8d;
  --text-light: #95a5a6;
  --border-color: #e5e7eb;
  --bg-light: #f9fafb;
  --danger: #e74c3c;
  --danger-dark: #c0392b;
}

/* 页面头部 */
.page-header {
  background: linear-gradient(135deg, #5a9bcf 0%, #4a8bbf 100%);
  padding: 48px 0;
}

.page-header h1 {
  font-size: 32px;
  font-weight: 600;
  color: #fff;
  margin: 0 0 8px 0;
}

/* 主卡片 - 直角设计 */
.attraction-card {
  background: #fff;
  border: 1px solid var(--border-color);
}

.attraction-cover {
  height: 400px;
  overflow: hidden;
  background: #f5f5f5;
}

.attraction-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.attraction-body {
  padding: 24px;
}

.attraction-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.attraction-title {
  font-size: 24px;
  font-weight: 600;
  color: var(--text-dark);
  margin: 0;
}

.action-buttons {
  display: flex;
  gap: 12px;
}

.btn-action {
  background: none;
  border: 1px solid var(--border-color);
  padding: 6px 16px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
  color: var(--text-gray);
}

.btn-action:hover {
  border-color: var(--primary-blue);
  color: var(--primary-blue);
}

.stats-row {
  display: flex;
  gap: 24px;
  padding-bottom: 20px;
  margin-bottom: 20px;
  border-bottom: 1px solid var(--border-color);
}

.stats-row span {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: var(--text-light);
}

/* 区块样式 */
.section-block {
  margin-top: 24px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-dark);
  margin: 0 0 16px 0;
  padding-bottom: 8px;
  border-bottom: 2px solid var(--primary-blue);
  display: inline-block;
}

.attraction-desc {
  color: var(--text-gray);
  line-height: 1.6;
  margin: 0;
}

/* 评论表单 */
.comment-form {
  background: var(--bg-light);
  padding: 16px;
  margin-bottom: 24px;
}

.form-control {
  width: 100%;
  border: 1px solid var(--border-color);
  padding: 10px 12px;
  font-size: 14px;
  font-family: inherit;
  resize: vertical;
}

.form-control:focus {
  outline: none;
  border-color: var(--primary-blue);
}

.comment-form-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
}

.btn-submit {
  background: var(--primary-blue);
  color: #fff;
  border: none;
  padding: 6px 20px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-submit:hover:not(:disabled) {
  background: var(--primary-dark);
}

.btn-submit:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 评论列表 */
.comment-list {
  margin-top: 24px;
}

.empty-comment {
  text-align: center;
  padding: 48px 20px;
  color: var(--text-light);
}

.empty-comment i {
  font-size: 48px;
}

.empty-comment p {
  margin: 12px 0 0 0;
}

.comment-item {
  display: flex;
  gap: 12px;
  padding: 16px 0;
  border-top: 1px solid var(--border-color);
}

.comment-item:first-child {
  border-top: none;
}

.comment-avatar {
  flex-shrink: 0;
}

.comment-avatar img {
  width: 40px;
  height: 40px;
  object-fit: cover;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
  flex-wrap: wrap;
}

.comment-username {
  font-weight: 600;
  font-size: 14px;
  color: var(--text-dark);
}

.comment-time {
  font-size: 12px;
  color: var(--text-light);
}

.comment-text {
  color: var(--text-gray);
  margin: 0 0 8px 0;
  line-height: 1.5;
  font-size: 14px;
}

.comment-actions {
  display: flex;
  gap: 16px;
}

.btn-delete {
  background: none;
  border: none;
  color: var(--danger);
  font-size: 12px;
  cursor: pointer;
  padding: 0;
}

.btn-delete:hover {
  color: var(--danger-dark);
}

/* 右侧信息卡片 */
.info-card {
  background: #fff;
  border: 1px solid var(--border-color);
  padding: 20px;
}

.info-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-dark);
  margin: 0 0 16px 0;
  padding-bottom: 8px;
  border-bottom: 2px solid var(--primary-blue);
  display: inline-block;
}

.map-container {
  height: 300px;
  background: var(--bg-light);
  margin-bottom: 16px;
}

.info-content {
  margin-top: 16px;
}

.info-item {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: var(--text-gray);
}

.info-item strong {
  color: var(--text-dark);
}

.btn-nav {
  display: block;
  text-align: center;
  background: var(--primary-blue);
  color: #fff;
  text-decoration: none;
  padding: 10px;
  margin-top: 16px;
  transition: all 0.2s;
}

.btn-nav:hover {
  background: var(--primary-dark);
  color: #fff;
}

/* 响应式 */
@media (max-width: 768px) {
  .page-header {
    padding: 32px 0;
  }
  
  .page-header h1 {
    font-size: 24px;
  }
  
  .attraction-cover {
    height: 250px;
  }
  
  .attraction-header {
    flex-direction: column;
    gap: 12px;
  }
  
  .action-buttons {
    width: 100%;
  }
  
  .btn-action {
    flex: 1;
    text-align: center;
  }
  
  .attraction-body {
    padding: 16px;
  }
  
  .info-card {
    margin-top: 16px;
  }
}
</style>