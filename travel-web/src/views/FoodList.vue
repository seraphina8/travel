<template>
  <div class="page-header food-header">
    <div class="container">
      <nav aria-label="breadcrumb" class="mb-2">
        <ol class="breadcrumb breadcrumb-custom">
          <li class="breadcrumb-item">
            <router-link to="/"><i class="bi bi-house-door"></i> 首页</router-link>
          </li>
          <li class="breadcrumb-item active">
            <i class="bi bi-cup-hot"></i> 美食推荐
          </li>
        </ol>
      </nav>
      <h1 class="h3 fw-bold mb-1"><i class="bi bi-cup-hot me-2"></i>美食推荐</h1>
      <p class="text-white-50 mb-0 small">发现各地特色美食</p>
    </div>
  </div>
  
  <div class="container py-4">
    <!-- 筛选 -->
    <div class="filter-card">
      <div class="filter-row">
        <div class="filter-search">
          <input type="text" class="filter-input" v-model="keyword" placeholder="搜索美食名称..." @keyup.enter="loadData">
          <button class="filter-btn" @click="loadData">
            <i class="bi bi-search"></i>
          </button>
        </div>
        <select class="filter-select" v-model="province" @change="loadData">
          <option value="">全部省份</option>
          <option v-for="p in provinces" :key="p" :value="p">{{ p }}</option>
        </select>
        <select class="filter-select" v-model="selectedTag" @change="handleFilterChange">
          <option value="">全部标签</option>
          <option v-for="tag in tags" :key="tag.id" :value="tag.name">{{ tag.name }}</option>
        </select>
        <select class="filter-select" v-model="sortBy" @change="loadData">
          <option value="newest">最新发布</option>
          <option value="popular">最受欢迎</option>
        </select>
        <button class="filter-reset" @click="resetFilter">
          <i class="bi bi-arrow-counterclockwise"></i> 重置
        </button>
      </div>
      <div class="filter-footer">
        <router-link to="/food/share" class="share-link">
          <i class="bi bi-plus-circle"></i> 分享美食
        </router-link>
      </div>
    </div>
    
    <!-- 美食列表 -->
    <div class="row g-4">
      <div class="col-lg-3 col-md-4 col-sm-6" v-for="item in foods" :key="item.id">
        <router-link :to="`/food/${item.id}`" class="food-card">
          <div class="food-img">
            <img :src="item.coverImage || defaultImage" :alt="item.name">
          </div>
          <div class="food-body">
            <h5 class="food-title">{{ item.name }}</h5>
            <p class="food-location">
              <i class="bi bi-geo-alt"></i> {{ item.province }} · {{ item.city }}
            </p>
            <p class="food-desc">{{ item.description }}</p>
          </div>
        </router-link>
      </div>
    </div>
    
    <!-- 空状态 -->
    <div class="empty-state" v-if="foods.length === 0 && !loading">
      <i class="bi bi-cup"></i>
      <p>暂无美食数据</p>
    </div>
    
    <!-- 分页 -->
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
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import api from '../api'

const foods = ref([])
const province = ref('')
const keyword = ref('')
const selectedTag = ref('')
const sortBy = ref('newest')
const tags = ref([])
const pageNum = ref(1)
const pageSize = ref(12)
const total = ref(0)
const loading = ref(false)
const defaultImage = 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=400'

const provinces = [
  '北京', '天津', '河北', '山西', '内蒙古', '辽宁', '吉林', '黑龙江',
  '上海', '江苏', '浙江', '安徽', '福建', '江西', '山东', '河南',
  '湖北', '湖南', '广东', '广西', '海南', '重庆', '四川', '贵州',
  '云南', '西藏', '陕西', '甘肃', '青海', '宁夏', '新疆'
]

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

const loadData = async () => {
  loading.value = true
  try {
    const res = await api.getFoodList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      province: province.value || undefined,
      keyword: keyword.value || undefined,
      tag: selectedTag.value || undefined,
      sortBy: sortBy.value || undefined,
      status: 1
    })
    if (res.code === 200) {
      foods.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (e) {
    console.error('加载美食失败', e)
  } finally {
    loading.value = false
  }
}

const loadTags = async () => {
  const res = await api.getTagList(4)
  if (res.code === 200) {
    tags.value = res.data || []
  }
}

const handleFilterChange = () => {
  pageNum.value = 1
  loadData()
}

const changePage = (p) => {
  if (p < 1 || p > totalPages.value) return
  pageNum.value = p
  loadData()
}

const resetFilter = () => {
  keyword.value = ''
  province.value = ''
  selectedTag.value = ''
  sortBy.value = 'newest'
  pageNum.value = 1
  loadData()
}

onMounted(() => {
  loadTags()
  loadData()
})
</script>

<style scoped>
/* 统一蓝色主题 */
.food-header {
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

.food-header h1 {
  color: #fff;
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

.filter-footer {
  padding-top: 16px;
  border-top: 1px solid #e5e7eb;
  display: flex;
  justify-content: flex-end;
}

.share-link {
  background: #5a9bcf;
  color: #fff;
  border: none;
  padding: 8px 24px;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
}

.share-link:hover {
  background: #4a8bbf;
  color: #fff;
}

/* 美食卡片 - 直角设计 */
.food-card {
  display: block;
  background: #fff;
  border: 1px solid #e5e7eb;
  transition: all 0.2s;
  text-decoration: none;
  color: inherit;
  height: 100%;
}

.food-card:hover {
  transform: translateY(-4px);
  border-color: #5a9bcf;
  box-shadow: 0 8px 20px rgba(90, 155, 207, 0.1);
}

.food-img {
  height: 180px;
  overflow: hidden;
  background: #f9fafb;
}

.food-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.food-card:hover .food-img img {
  transform: scale(1.05);
}

.food-body {
  padding: 16px;
}

.food-title {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 8px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.food-location {
  font-size: 12px;
  color: #95a5a6;
  margin: 0 0 8px 0;
  display: flex;
  align-items: center;
  gap: 4px;
}

.food-desc {
  font-size: 13px;
  color: #7f8c8d;
  line-height: 1.4;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
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
  .food-header {
    padding: 30px 0;
  }
  
  .filter-row {
    flex-direction: column;
  }
  
  .filter-search,
  .filter-select,
  .filter-reset {
    width: 100%;
  }
  
  .filter-footer {
    justify-content: center;
  }
  
  .share-link {
    width: 100%;
    justify-content: center;
  }
  
  .food-img {
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
