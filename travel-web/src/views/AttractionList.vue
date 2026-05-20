<template>
  <div class="page-header attraction-header">
    <div class="container">
      <nav aria-label="breadcrumb" class="mb-2">
        <ol class="breadcrumb breadcrumb-custom">
          <li class="breadcrumb-item">
            <router-link to="/"><i class="bi bi-house-door"></i> 首页</router-link>
          </li>
          <li class="breadcrumb-item active">
            <i class="bi bi-geo-alt"></i> 景点列表
          </li>
        </ol>
      </nav>
      <h1 class="h3 fw-bold mb-1"><i class="bi bi-geo-alt me-2"></i>景点探索</h1>
      <p class="text-white-50 mb-0 small">发现精彩景点，开启美好旅程</p>
    </div>
  </div>
  
  <div class="container py-4">
    <!-- 筛选 -->
    <div class="filter-bar">
      <div class="filter-row">
        <input type="text" class="filter-input" v-model="keyword" placeholder="搜索景点..." @keyup.enter="handleSearch">
        <select class="filter-select" v-model="province" @change="handleFilterChange">
          <option value="">全部省份</option>
          <option v-for="p in provinces" :key="p" :value="p">{{ p }}</option>
        </select>
        <select class="filter-select" v-model="tagId" @change="handleFilterChange">
          <option value="">全部标签</option>
          <option v-for="tag in tags" :key="tag.id" :value="tag.id">{{ tag.name }}</option>
        </select>
        <button class="filter-btn" @click="handleSearch">
          <i class="bi bi-search"></i> 搜索
        </button>
      </div>
    </div>
    
    <!-- 景点列表 -->
    <div class="row g-4">
      <div class="col-lg-3 col-md-4 col-sm-6" v-for="item in attractions" :key="item.id">
        <router-link :to="`/attraction/${item.id}`" class="attraction-card">
          <div class="card-img">
            <img :src="item.coverImage || defaultImage" :alt="item.name">
          </div>
          <div class="card-body">
            <h5 class="card-title">{{ item.name }}</h5>
            <p class="card-location">
              <i class="bi bi-geo-alt"></i> {{ item.province }} · {{ item.city }}
            </p>
            <div class="card-stats">
              <span><i class="bi bi-eye"></i> {{ item.viewCount || 0 }}</span>
              <span><i class="bi bi-heart"></i> {{ item.collectCount || 0 }}</span>
            </div>
          </div>
        </router-link>
      </div>
    </div>
    
    <!-- 空状态 -->
    <div class="empty-state" v-if="attractions.length === 0 && !loading">
      <i class="bi bi-geo"></i>
      <p>暂无景点数据</p>
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

const attractions = ref([])
const keyword = ref('')
const province = ref('')
const tagId = ref('')
const tags = ref([])
const pageNum = ref(1)
const pageSize = ref(12)
const total = ref(0)
const loading = ref(false)
const defaultImage = 'https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=400'

const provinces = [
  '北京', '天津', '河北', '山西', '内蒙古', '辽宁', '吉林', '黑龙江',
  '上海', '江苏', '浙江', '安徽', '福建', '江西', '山东', '河南',
  '湖北', '湖南', '广东', '广西', '海南', '重庆', '四川', '贵州',
  '云南', '西藏', '陕西', '甘肃', '青海', '宁夏', '新疆'
]

const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

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

const loadTags = async () => {
  try {
    const res = await api.getTagList(1)
    if (res.code === 200) {
      tags.value = res.data || []
    }
  } catch (e) {}
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    if (keyword.value) params.keyword = keyword.value
    if (province.value) params.province = province.value
    if (tagId.value) {
      const selectedTag = tags.value.find(tag => String(tag.id) === String(tagId.value))
      params.tagId = tagId.value
      if (selectedTag?.name) params.tagName = selectedTag.name
    }
    
    const res = await api.getAttractionList(params)
    if (res.code === 200) {
      attractions.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (e) {
    console.error('加载景点失败', e)
  } finally {
    loading.value = false
  }
}

const handleFilterChange = () => {
  pageNum.value = 1
  loadData()
}

const handleSearch = () => {
  pageNum.value = 1
  loadData()
}

const changePage = (p) => {
  if (p < 1 || p > totalPages.value) return
  pageNum.value = p
  loadData()
}

onMounted(() => {
  loadTags()
  loadData()
})
</script>

<style scoped>
/* 统一蓝色主题 - #5a9bcf */
.attraction-header {
  background: linear-gradient(135deg, #5a9bcf 0%, #4a8bbf 100%);
  padding: 40px 0;
}

.breadcrumb-custom {
  background: rgba(90, 155, 207, 0.2);
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

.attraction-header h1 {
  color: #fff;
}

/* 筛选栏 */
.filter-bar {
  background: #fff;
  border: 1px solid #e5e7eb;
  padding: 20px;
  margin-bottom: 32px;
}

.filter-row {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.filter-input {
  flex: 1;
  min-width: 180px;
  padding: 8px 12px;
  border: 1px solid #e5e7eb;
  font-size: 14px;
  outline: none;
  transition: border-color 0.2s;
}

.filter-input:focus {
  border-color: #5a9bcf;
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

.filter-btn {
  background: #5a9bcf;
  color: #fff;
  border: none;
  padding: 8px 24px;
  font-size: 14px;
  cursor: pointer;
  transition: background 0.2s;
}

.filter-btn:hover {
  background: #4a8bbf;
}

/* 景点卡片 */
.attraction-card {
  display: block;
  background: #fff;
  border: 1px solid #e5e7eb;
  transition: all 0.2s;
  text-decoration: none;
  color: inherit;
  height: 100%;
}

.attraction-card:hover {
  transform: translateY(-4px);
  border-color: #5a9bcf;
  box-shadow: 0 8px 20px rgba(90, 155, 207, 0.1);
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

.attraction-card:hover .card-img img {
  transform: scale(1.05);
}

.card-body {
  padding: 16px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.card-location {
  font-size: 13px;
  color: #95a5a6;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.card-stats {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #95a5a6;
}

.card-stats i {
  margin-right: 4px;
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
  .attraction-header {
    padding: 30px 0;
  }
  
  .filter-row {
    flex-direction: column;
  }
  
  .filter-input,
  .filter-select,
  .filter-btn {
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
