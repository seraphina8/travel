<template>
  <div>
    <div class="page-header strategy-header">
      <div class="container">
        <nav aria-label="breadcrumb" class="mb-3">
          <ol class="breadcrumb breadcrumb-custom">
            <li class="breadcrumb-item">
              <router-link to="/"><i class="bi bi-house-door"></i> 首页</router-link>
            </li>
            <li class="breadcrumb-item active">
              <i class="bi bi-journal-richtext"></i> 攻略列表
            </li>
          </ol>
        </nav>
        <h1 class="display-5 fw-bold mb-3">
          <i class="bi bi-compass me-2"></i>旅游攻略
        </h1>
        <p class="lead text-white-50 mb-0">发现精彩旅行故事，获取实用出行指南</p>
      </div>
    </div>
    
    <div class="container py-5">
      <!-- Search & Filter -->
      <div class="filter-card">
        <div class="filter-row">
          <div class="filter-search">
            <input type="text" class="filter-input" v-model="keyword" placeholder="搜索攻略标题" @keyup.enter="handleSearch">
            <button class="filter-btn" @click="handleSearch">
              <i class="bi bi-search"></i> 搜索
            </button>
          </div>
          <select class="filter-select" v-model="sortBy" @change="handleSearch">
            <option value="">默认排序</option>
            <option value="viewCount">浏览最多</option>
            <option value="likeCount">点赞最多</option>
            <option value="createTime">最新发布</option>
          </select>
          <button class="filter-reset" @click="resetSearch">
            <i class="bi bi-arrow-counterclockwise"></i> 重置
          </button>
          <router-link :to="{ name: 'StrategyCreate' }" class="filter-create">
            <i class="bi bi-plus-circle"></i> 发布攻略
          </router-link>
        </div>
        
        <!-- Tags -->
        <div class="tags-wrapper">
          <span class="tag" 
                :class="{ active: selectedTag === '' }"
                @click="selectTag('')">全部</span>
          <span class="tag" 
                v-for="tag in tags" :key="tag"
                :class="{ active: selectedTag === tag }"
                @click="selectTag(tag)">{{ tag }}</span>
        </div>
      </div>
      
      <!-- List -->
      <div class="row g-4">
        <div class="col-lg-3 col-md-4 col-sm-6" v-for="strategy in strategies" :key="strategy.id">
          <div class="strategy-card">
            <router-link :to="`/strategy/${strategy.id}`" class="card-link">
              <div class="card-img">
                <img :src="strategy.coverImage || defaultImage" :alt="strategy.title">
              </div>
              <div class="card-body">
                <h5 class="card-title">{{ strategy.title }}</h5>
                <div class="card-author">
                  <img :src="getAvatar(strategy.userId)" alt="">
                  <span>{{ strategy.username || '旅行达人' }}</span>
                </div>
              </div>
              <div class="card-footer">
                <span><i class="bi bi-eye"></i> {{ strategy.viewCount || 0 }}</span>
                <span><i class="bi bi-heart"></i> {{ strategy.likeCount || 0 }}</span>
              </div>
            </router-link>
            <div class="card-actions">
              <button 
                class="collect-btn" 
                :class="{ collected: strategy.collected }"
                @click.stop="quickCollect(strategy, $event)"
              >
                <i class="bi" :class="strategy.collected ? 'bi-heart-fill' : 'bi-heart'"></i>
              </button>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Empty -->
      <div class="empty-state" v-if="strategies.length === 0">
        <i class="bi bi-journal-x"></i>
        <p>暂无攻略数据</p>
      </div>
      
      <!-- Pagination -->
      <div class="pagination-wrapper" v-if="total > pageSize">
        <button class="page-btn" :disabled="pageNum === 1" @click="changePage(pageNum - 1)">
          上一页
        </button>
        <div class="page-numbers">
          <button 
            v-for="p in visiblePages" 
            :key="p" 
            class="page-number" 
            :class="{ active: p === pageNum }"
            @click="changePage(p)"
          >
            {{ p }}
          </button>
        </div>
        <button class="page-btn" :disabled="pageNum === totalPages" @click="changePage(pageNum + 1)">
          下一页
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api'

const router = useRouter()
const strategies = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(12)
const keyword = ref('')
const selectedTag = ref('')
const sortBy = ref('')
const defaultImage = 'https://images.unsplash.com/photo-1476514525535-07fb3b4ae5f1?w=400'
const collectedIds = ref(new Set())

const tags = ref([])

const totalPages = computed(() => Math.ceil(total.value / pageSize.value) || 1)

const visiblePages = computed(() => {
  const pages = []
  const maxVisible = 5
  let start = Math.max(1, pageNum.value - Math.floor(maxVisible / 2))
  let end = Math.min(totalPages.value, start + maxVisible - 1)
  
  if (end - start + 1 < maxVisible) {
    start = Math.max(1, end - maxVisible + 1)
  }
  
  for (let i = start; i <= end; i++) {
    pages.push(i)
  }
  return pages
})

const getAvatar = (id) => `https://api.dicebear.com/7.x/avataaars/svg?seed=${id}`

const loadCollectedIds = async () => {
  if (!localStorage.getItem('userToken')) return
  try {
    const res = await api.getMyCollections()
    if (res.code === 200 && res.data) {
      collectedIds.value = new Set(res.data.filter(c => c.type === 2).map(c => c.targetId))
    }
  } catch (e) {}
}

const loadData = async () => {
  const params = {
    pageNum: pageNum.value,
    pageSize: pageSize.value,
    status: 1
  }
  if (keyword.value) {
    params.title = keyword.value
  }
  if (selectedTag.value) {
    params.tag = selectedTag.value
  }
  if (sortBy.value) {
    params.sortBy = sortBy.value
  }
  const res = await api.getStrategyList(params)
  if (res.code === 200) {
    strategies.value = res.data.records.map(s => ({
      ...s,
      collected: collectedIds.value.has(s.id)
    }))
    total.value = res.data.total
  }
}

const loadTags = async () => {
  const res = await api.getTagList(2)
  if (res.code === 200 && res.data?.length) {
    tags.value = res.data.map(tag => tag.name)
  }
}

const handleSearch = () => {
  pageNum.value = 1
  loadData()
}

const resetSearch = () => {
  keyword.value = ''
  selectedTag.value = ''
  sortBy.value = ''
  pageNum.value = 1
  loadData()
}

const selectTag = (tag) => {
  selectedTag.value = tag
  pageNum.value = 1
  loadData()
}

const changePage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    pageNum.value = page
    loadData()
  }
}

const quickCollect = async (strategy, event) => {
  event.preventDefault()
  if (!localStorage.getItem('userToken')) {
    router.push('/login')
    return
  }
  try {
    const res = await api.toggleCollect(strategy.id, 2)
    if (res.code === 200) {
      strategy.collected = !strategy.collected
      if (strategy.collected) {
        collectedIds.value.add(strategy.id)
      } else {
        collectedIds.value.delete(strategy.id)
      }
    }
  } catch (e) {
    alert('操作失败')
  }
}

onMounted(async () => {
  await loadTags()
  await loadCollectedIds()
  loadData()
})
</script>

<style scoped>
/* 统一蓝色主题 */
.strategy-header {
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
  gap: 6px;
}

.breadcrumb-custom .breadcrumb-item a:hover {
  color: #fff;
}

.breadcrumb-custom .breadcrumb-item.active {
  color: #fff;
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.breadcrumb-custom .breadcrumb-item + .breadcrumb-item::before {
  color: rgba(255, 255, 255, 0.7);
  content: "/";
}

.strategy-header h1 {
  color: #fff;
}

.strategy-header .lead {
  font-size: 1.1rem;
}

/* 筛选卡片 - 直角设计 */
.filter-card {
  background: #fff;
  border: 1px solid #e5e7eb;
  padding: 20px;
  margin-bottom: 32px;
}

.filter-row {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  margin-bottom: 16px;
}

.filter-search {
  flex: 1;
  min-width: 200px;
  display: flex;
}

.filter-input {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #e5e7eb;
  font-size: 14px;
  outline: none;
}

.filter-input:focus {
  border-color: #5a9bcf;
}

.filter-btn {
  background: #5a9bcf;
  color: #fff;
  border: none;
  padding: 8px 20px;
  cursor: pointer;
}

.filter-btn:hover {
  background: #4a8bbf;
}

.filter-select {
  width: 150px;
  padding: 8px 12px;
  border: 1px solid #e5e7eb;
  font-size: 14px;
  outline: none;
  cursor: pointer;
  background: #fff;
}

.filter-select:focus {
  border-color: #5a9bcf;
}

.filter-reset {
  background: #fff;
  border: 1px solid #e5e7eb;
  padding: 8px 20px;
  cursor: pointer;
  transition: all 0.2s;
  color: #7f8c8d;
}

.filter-reset:hover {
  border-color: #5a9bcf;
  color: #5a9bcf;
}

.filter-create {
  background: #5a9bcf;
  color: #fff;
  border: none;
  padding: 8px 20px;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.filter-create:hover {
  background: #4a8bbf;
  color: #fff;
}

/* 标签 */
.tags-wrapper {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding-top: 8px;
  border-top: 1px solid #e5e7eb;
}

.tag {
  padding: 4px 12px;
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s;
  color: #7f8c8d;
}

.tag:hover {
  border-color: #5a9bcf;
  color: #5a9bcf;
}

.tag.active {
  background: #5a9bcf;
  border-color: #5a9bcf;
  color: #fff;
}

/* 攻略卡片 - 直角设计 */
.strategy-card {
  position: relative;
  background: #fff;
  border: 1px solid #e5e7eb;
  transition: all 0.2s;
  height: 100%;
}

.strategy-card:hover {
  transform: translateY(-4px);
  border-color: #5a9bcf;
  box-shadow: 0 8px 20px rgba(90, 155, 207, 0.1);
}

.card-link {
  text-decoration: none;
  color: inherit;
  display: block;
}

.card-img {
  height: 180px;
  overflow: hidden;
  background: #f9fafb;
}

.card-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.strategy-card:hover .card-img img {
  transform: scale(1.05);
}

.card-body {
  padding: 16px;
}

.card-title {
  font-size: 15px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.4;
  min-height: 42px;
}

.card-author {
  display: flex;
  align-items: center;
  gap: 8px;
}

.card-author img {
  width: 28px;
  height: 28px;
  object-fit: cover;
}

.card-author span {
  font-size: 12px;
  color: #7f8c8d;
}

.card-footer {
  padding: 12px 16px;
  border-top: 1px solid #e5e7eb;
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #95a5a6;
}

.card-footer i {
  margin-right: 4px;
}

/* 收藏按钮 */
.card-actions {
  position: absolute;
  top: 12px;
  right: 12px;
  z-index: 2;
}

.collect-btn {
  width: 32px;
  height: 32px;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid #e5e7eb;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  color: #95a5a6;
}

.collect-btn:hover {
  background: #fff;
  border-color: #e74c3c;
  color: #e74c3c;
}

.collect-btn.collected {
  background: #e74c3c;
  border-color: #e74c3c;
  color: #fff;
}

.collect-btn.collected:hover {
  background: #c0392b;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: #fff;
  border: 1px solid #e5e7eb;
}

.empty-state i {
  font-size: 48px;
  color: #95a5a6;
}

.empty-state p {
  color: #7f8c8d;
  margin: 12px 0 0 0;
}

/* 分页 */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 12px;
  margin-top: 32px;
}

.page-btn {
  background: #fff;
  border: 1px solid #e5e7eb;
  padding: 8px 16px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
  color: #7f8c8d;
}

.page-btn:hover:not(:disabled) {
  border-color: #5a9bcf;
  color: #5a9bcf;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-numbers {
  display: flex;
  gap: 8px;
}

.page-number {
  min-width: 36px;
  height: 36px;
  background: #fff;
  border: 1px solid #e5e7eb;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
  color: #7f8c8d;
}

.page-number:hover {
  border-color: #5a9bcf;
  color: #5a9bcf;
}

.page-number.active {
  background: #5a9bcf;
  border-color: #5a9bcf;
  color: #fff;
}

/* 响应式 */
@media (max-width: 768px) {
  .strategy-header {
    padding: 30px 0;
  }
  
  .filter-row {
    flex-direction: column;
  }
  
  .filter-search,
  .filter-select,
  .filter-reset,
  .filter-create {
    width: 100%;
  }
  
  .card-img {
    height: 160px;
  }
  
  .pagination-wrapper {
    flex-wrap: wrap;
  }
  
  .page-numbers {
    order: 3;
    width: 100%;
    justify-content: center;
  }
}
</style>
