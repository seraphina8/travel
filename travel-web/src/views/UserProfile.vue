<template>
  <div class="page-header user-profile-header">
    <div class="container">
      <nav aria-label="breadcrumb" class="mb-2">
        <ol class="breadcrumb breadcrumb-custom">
          <li class="breadcrumb-item">
            <router-link to="/"><i class="bi bi-house-door"></i> 首页</router-link>
          </li>
          <li class="breadcrumb-item active">用户主页</li>
        </ol>
      </nav>
      <h1 class="h3 fw-bold mb-1"><i class="bi bi-person me-2"></i>用户主页</h1>
    </div>
  </div>

  <div class="container py-4">
    <div class="row g-4">
      <div class="col-lg-4">
        <div class="profile-card">
          <div class="profile-avatar">
            <img :src="targetUser.avatar || getAvatar(targetUser.id)" alt="">
          </div>
          <h4 class="profile-name">{{ targetUser.nickname || targetUser.username || '用户' + targetUser.id }}</h4>
          <p class="profile-email">{{ targetUser.email || '未公开邮箱' }}</p>

          <div class="profile-stats">
            <div class="stat-item">
              <h5>{{ followCount.followCount || 0 }}</h5>
              <small>关注</small>
            </div>
            <div class="stat-item">
              <h5>{{ followCount.fansCount || 0 }}</h5>
              <small>粉丝</small>
            </div>
          </div>

          <div class="profile-actions" v-if="!isSelf">
            <button
              class="action-btn follow"
              :class="{ followed: isFollowed }"
              @click="toggleFollow"
            >
              <i class="bi" :class="isFollowed ? 'bi-person-check' : 'bi-person-plus'"></i>
              {{ isFollowed ? '已关注' : '关注' }}
            </button>
            <button class="action-btn chat" @click="startChat">
              <i class="bi bi-chat-dots"></i> 私聊
            </button>
          </div>
          <div v-else class="self-tip">
            <small>这是您自己的主页</small>
          </div>
        </div>
      </div>

      <div class="col-lg-8">
        <div class="content-card">
          <h5 class="card-title">用户动态</h5>

          <div v-if="userStrategies.length > 0 || publicTrips.length > 0" class="content-section">
            <h6 class="section-subtitle">攻略与公开行程</h6>
            <div class="content-grid">
              <div class="content-item" v-for="strategy in userStrategies" :key="`strategy-${strategy.id}`">
                <router-link :to="`/strategy/${strategy.id}`" class="content-link">
                  <img :src="strategy.coverImage || strategy.image || strategyImage" :alt="strategy.title">
                  <div class="content-info">
                    <h6>{{ strategy.title }}</h6>
                    <small>已审核攻略</small>
                  </div>
                </router-link>
              </div>
              <div class="content-item" v-for="trip in publicTrips" :key="`trip-${trip.id}`">
                <router-link :to="`/trip/${trip.id}`" class="content-link">
                  <img :src="trip.coverImage || trip.image || tripImage" :alt="trip.title">
                  <div class="content-info">
                    <h6>{{ trip.title }}</h6>
                    <small>公开行程</small>
                  </div>
                </router-link>
              </div>
            </div>
          </div>

          <div v-if="userFoods.length > 0" class="content-section">
            <h6 class="section-subtitle">分享的美食</h6>
            <div class="content-grid">
              <div class="content-item" v-for="food in userFoods" :key="`food-${food.id}`">
                <router-link :to="`/food/${food.id}`" class="content-link">
                  <img :src="food.coverImage || defaultImage" :alt="food.name">
                  <div class="content-info">
                    <h6>{{ food.name }}</h6>
                    <small>{{ food.province }} · {{ food.city }}</small>
                  </div>
                </router-link>
              </div>
            </div>
          </div>

          <div class="empty-state" v-if="userFoods.length === 0 && userStrategies.length === 0 && publicTrips.length === 0">
            <i class="bi bi-inbox"></i>
            <p>该用户暂无公开动态</p>
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
const targetUser = ref({})
const followCount = ref({ followCount: 0, fansCount: 0 })
const isFollowed = ref(false)
const userFoods = ref([])
const userStrategies = ref([])
const publicTrips = ref([])
const defaultImage = 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=400'
const strategyImage = 'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?w=400'
const tripImage = 'https://images.unsplash.com/photo-1488646953014-85cb44e25828?w=400'

const currentUser = computed(() => JSON.parse(localStorage.getItem('user') || '{}'))
const isSelf = computed(() => currentUser.value.id == route.params.id)

const getAvatar = (id) => `https://api.dicebear.com/7.x/avataaars/svg?seed=${id || 'user'}`

const loadUserInfo = async () => {
  try {
    const res = await api.getUserInfo(route.params.id)
    if (res.code === 200) {
      targetUser.value = res.data || {}
    }
  } catch (e) {
    console.error('加载用户信息失败', e)
  }
}

const loadFollowCount = async () => {
  try {
    const res = await api.getUserFollowCount(route.params.id)
    if (res.code === 200) {
      followCount.value = res.data || { followCount: 0, fansCount: 0 }
    }
  } catch (e) {}
}

const checkFollow = async () => {
  if (!localStorage.getItem('userToken')) return
  try {
    const res = await api.checkFollow(route.params.id)
    if (res.code === 200) {
      isFollowed.value = res.data
    }
  } catch (e) {}
}

const toggleFollow = async () => {
  if (!localStorage.getItem('userToken')) {
    alert('请先登录')
    router.push('/login')
    return
  }
  try {
    const res = await api.toggleFollow(route.params.id)
    if (res.code === 200) {
      isFollowed.value = !isFollowed.value
      loadFollowCount()
      alert(res.data)
    }
  } catch (e) {
    alert('操作失败')
  }
}

const startChat = () => {
  if (!localStorage.getItem('userToken')) {
    alert('请先登录')
    router.push('/login')
    return
  }
  sessionStorage.setItem('chatTarget', JSON.stringify({
    id: targetUser.value.id,
    nickname: targetUser.value.nickname || targetUser.value.username,
    avatar: targetUser.value.avatar
  }))
  router.push('/chat')
}

const loadUserFoods = async () => {
  try {
    const res = await api.getUserFoods(route.params.id)
    if (res.code === 200) {
      userFoods.value = res.data || []
    }
  } catch (e) {}
}

const loadUserStrategies = async () => {
  try {
    const res = await api.getUserStrategies(route.params.id)
    if (res.code === 200) {
      userStrategies.value = res.data || []
    }
  } catch (e) {}
}

const loadPublicTrips = async () => {
  try {
    const res = await api.getUserPublicTrips(route.params.id)
    if (res.code === 200) {
      publicTrips.value = res.data || []
    }
  } catch (e) {}
}

onMounted(() => {
  loadUserInfo()
  loadFollowCount()
  checkFollow()
  loadUserFoods()
  loadUserStrategies()
  loadPublicTrips()
})
</script>

<style scoped>
.user-profile-header {
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

.user-profile-header h1 {
  color: #fff;
}

.profile-card {
  background: #fff;
  border: 1px solid #e5e7eb;
  padding: 32px 24px;
  text-align: center;
}

.profile-avatar {
  margin-bottom: 16px;
}

.profile-avatar img {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border: 1px solid #e5e7eb;
}

.profile-name {
  font-size: 20px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 4px 0;
}

.profile-email {
  font-size: 13px;
  color: #95a5a6;
  margin: 0 0 20px 0;
}

.profile-stats {
  display: flex;
  justify-content: center;
  gap: 32px;
  margin-bottom: 24px;
  padding: 16px 0;
  border-top: 1px solid #e5e7eb;
  border-bottom: 1px solid #e5e7eb;
}

.stat-item {
  text-align: center;
}

.stat-item h5 {
  font-size: 20px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 4px 0;
}

.stat-item small {
  font-size: 12px;
  color: #95a5a6;
}

.profile-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
}

.action-btn {
  padding: 8px 20px;
  border: 1px solid #e5e7eb;
  background: #fff;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
  display: inline-flex;
  align-items: center;
  gap: 6px;
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

.action-btn.chat:hover {
  border-color: #5a9bcf;
  color: #5a9bcf;
}

.self-tip {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #e5e7eb;
  color: #95a5a6;
  font-size: 12px;
}

.content-card {
  background: #fff;
  border: 1px solid #e5e7eb;
  padding: 24px;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 20px 0;
  padding-bottom: 8px;
  border-bottom: 2px solid #5a9bcf;
  display: inline-block;
}

.content-section + .content-section {
  margin-top: 24px;
}

.section-subtitle {
  font-size: 14px;
  font-weight: 600;
  color: #7f8c8d;
  margin: 0 0 12px 0;
}

.content-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.content-item {
  border: 1px solid #e5e7eb;
  transition: all 0.2s;
}

.content-item:hover {
  border-color: #5a9bcf;
  transform: translateY(-2px);
}

.content-link {
  display: flex;
  align-items: center;
  padding: 12px;
  text-decoration: none;
  color: inherit;
  gap: 12px;
}

.content-link img {
  width: 60px;
  height: 60px;
  object-fit: cover;
  flex-shrink: 0;
}

.content-info {
  flex: 1;
  min-width: 0;
}

.content-info h6 {
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 4px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.content-info small {
  font-size: 11px;
  color: #95a5a6;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #95a5a6;
}

.empty-state i {
  font-size: 48px;
}

.empty-state p {
  margin: 12px 0 0 0;
}

@media (max-width: 768px) {
  .user-profile-header {
    padding: 30px 0;
  }

  .profile-card {
    padding: 24px;
  }

  .content-grid {
    grid-template-columns: 1fr;
  }

  .profile-actions {
    flex-direction: column;
  }

  .action-btn {
    width: 100%;
    justify-content: center;
  }
}
</style>
