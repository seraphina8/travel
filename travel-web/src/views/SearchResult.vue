<template>
  <div>
    <div class="page-header">
      <div class="container">
        <h1>搜索结果</h1>
        <nav aria-label="breadcrumb">
          <ol class="breadcrumb">
            <li class="breadcrumb-item"><router-link to="/">首页</router-link></li>
            <li class="breadcrumb-item active">搜索: {{ keyword }}</li>
          </ol>
        </nav>
      </div>
    </div>
    
    <div class="container py-5">
      <!-- Search Box -->
      <div class="bg-white rounded-3 p-4 mb-4 shadow-sm">
        <div class="row g-3 align-items-center">
          <div class="col-md-8">
            <div class="input-group">
              <input type="text" class="form-control form-control-lg" v-model="searchKeyword" 
                     placeholder="搜索景区、攻略、景点、美食..." @keyup.enter="handleSearch">
              <button class="btn btn-primary btn-lg" @click="handleSearch">
                <i class="bi bi-search"></i> 搜索
              </button>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Category Tabs -->
      <ul class="nav nav-pills mb-4">
        <li class="nav-item">
          <a class="nav-link" :class="{ active: activeTab === 'all' }" href="#" @click.prevent="activeTab = 'all'">
            全部 <span class="badge bg-secondary ms-1">{{ totalCount }}</span>
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link" :class="{ active: activeTab === 'scenic' }" href="#" @click.prevent="activeTab = 'scenic'">
            景区 <span class="badge bg-secondary ms-1">{{ scenics.length }}</span>
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link" :class="{ active: activeTab === 'strategy' }" href="#" @click.prevent="activeTab = 'strategy'">
            攻略 <span class="badge bg-secondary ms-1">{{ strategies.length }}</span>
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link" :class="{ active: activeTab === 'attraction' }" href="#" @click.prevent="activeTab = 'attraction'">
            景点 <span class="badge bg-secondary ms-1">{{ attractions.length }}</span>
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link" :class="{ active: activeTab === 'food' }" href="#" @click.prevent="activeTab = 'food'">
            美食 <span class="badge bg-secondary ms-1">{{ foods.length }}</span>
          </a>
        </li>
      </ul>
      
      <!-- Loading -->
      <div class="text-center py-5" v-if="loading">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">加载中...</span>
        </div>
        <p class="mt-2 text-muted">正在搜索...</p>
      </div>
      
      <!-- Results -->
      <div v-else>
        <!-- Scenic Results -->
        <div v-if="activeTab === 'all' || activeTab === 'scenic'">
          <div v-if="scenics.length > 0" class="mb-5">
            <h5 class="mb-3" v-if="activeTab === 'all'"><i class="bi bi-geo-alt text-primary"></i> 景区</h5>
            <div class="row g-4">
              <div class="col-lg-3 col-md-4 col-sm-6" v-for="item in scenics" :key="'scenic-' + item.id">
                <router-link :to="`/scenic/${item.id}`" class="scenic-card d-block">
                  <div class="card-img">
                    <img :src="item.coverImage || defaultImage" :alt="item.name">
                    <span class="badge-level" v-if="item.level">{{ item.level }}</span>
                  </div>
                  <div class="card-body">
                    <h5 class="card-title">{{ item.name }}</h5>
                    <p class="card-location">
                      <i class="bi bi-geo-alt"></i>{{ item.province }} · {{ item.city }}
                    </p>
                  </div>
                </router-link>
              </div>
            </div>
          </div>
        </div>
        
        <!-- Strategy Results -->
        <div v-if="activeTab === 'all' || activeTab === 'strategy'">
          <div v-if="strategies.length > 0" class="mb-5">
            <h5 class="mb-3" v-if="activeTab === 'all'"><i class="bi bi-journal-text text-success"></i> 攻略</h5>
            <div class="row g-4">
              <div class="col-lg-3 col-md-4 col-sm-6" v-for="item in strategies" :key="'strategy-' + item.id">
                <router-link :to="`/strategy/${item.id}`" class="strategy-card d-block h-100">
                  <div class="card-img">
                    <img :src="item.coverImage || defaultImage" :alt="item.title">
                  </div>
                  <div class="card-body">
                    <h5 class="card-title">{{ item.title }}</h5>
                    <div class="card-footer">
                      <span><i class="bi bi-eye"></i> {{ item.viewCount || 0 }}</span>
                      <span><i class="bi bi-heart"></i> {{ item.likeCount || 0 }}</span>
                    </div>
                  </div>
                </router-link>
              </div>
            </div>
          </div>
        </div>
        
        <!-- Attraction Results -->
        <div v-if="activeTab === 'all' || activeTab === 'attraction'">
          <div v-if="attractions.length > 0" class="mb-5">
            <h5 class="mb-3" v-if="activeTab === 'all'"><i class="bi bi-pin-map text-warning"></i> 景点</h5>
            <div class="row g-4">
              <div class="col-lg-3 col-md-4 col-sm-6" v-for="item in attractions" :key="'attraction-' + item.id">
                <router-link :to="`/attraction/${item.id}`" class="scenic-card d-block">
                  <div class="card-img">
                    <img :src="item.coverImage || defaultImage" :alt="item.name">
                  </div>
                  <div class="card-body">
                    <h5 class="card-title">{{ item.name }}</h5>
                    <p class="card-location text-muted small">{{ item.description?.substring(0, 30) }}...</p>
                  </div>
                </router-link>
              </div>
            </div>
          </div>
        </div>
        
        <!-- Food Results -->
        <div v-if="activeTab === 'all' || activeTab === 'food'">
          <div v-if="foods.length > 0" class="mb-5">
            <h5 class="mb-3" v-if="activeTab === 'all'"><i class="bi bi-cup-hot text-danger"></i> 美食</h5>
            <div class="row g-4">
              <div class="col-lg-3 col-md-4 col-sm-6" v-for="item in foods" :key="'food-' + item.id">
                <router-link :to="`/food/${item.id}`" class="scenic-card d-block">
                  <div class="card-img">
                    <img :src="item.coverImage || defaultImage" :alt="item.name">
                  </div>
                  <div class="card-body">
                    <h5 class="card-title">{{ item.name }}</h5>
                    <p class="card-location text-muted small">{{ [item.province, item.city].filter(Boolean).join(' · ') || item.address || '暂无地区' }}</p>
                  </div>
                </router-link>
              </div>
            </div>
          </div>
        </div>
        
        <!-- Empty State -->
        <div class="text-center py-5" v-if="totalCount === 0 && !loading">
          <i class="bi bi-search fs-1 text-muted"></i>
          <p class="text-muted mt-3">未找到与"{{ keyword }}"相关的结果</p>
          <p class="text-muted small">试试其他关键词吧</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '../api'

const route = useRoute()
const router = useRouter()

const keyword = ref('')
const searchKeyword = ref('')
const activeTab = ref('all')
const loading = ref(false)
const defaultImage = 'https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=400'

const scenics = ref([])
const strategies = ref([])
const attractions = ref([])
const foods = ref([])

const totalCount = computed(() =>
  scenics.value.length + strategies.value.length + attractions.value.length + foods.value.length
)

const handleSearch = () => {
  const kw = searchKeyword.value.trim()
  if (kw) {
    router.push(`/search?keyword=${encodeURIComponent(kw)}`)
  }
}

const recordsOf = (res) => {
  if (!res || res.code !== 200) return []
  if (Array.isArray(res.data)) return res.data
  return res.data?.records || []
}

const mergeUnique = (base, extra) => {
  const map = new Map()
  base.forEach(item => {
    if (item && item.id != null) map.set(item.id, item)
  })
  extra.forEach(item => {
    if (item && item.id != null && !map.has(item.id)) map.set(item.id, item)
  })
  return Array.from(map.values())
}

const loadData = async () => {
  const kw = keyword.value.trim()
  if (!kw) {
    scenics.value = []
    strategies.value = []
    attractions.value = []
    foods.value = []
    return
  }

  loading.value = true
  try {
    const [scenicRes, strategyRes, attractionRes, foodRes] = await Promise.all([
      // 景区接口使用 name 参数，后端已扩展为匹配名称、省份、城市、地址和简介
      api.getScenicList({ pageNum: 1, pageSize: 20, name: kw }),
      // 攻略使用 tag 参数，后端匹配标题、正文和标签
      api.getStrategyList({ pageNum: 1, pageSize: 20, tag: kw, status: 1 }),
      // 景点、美食接口使用 keyword 参数
      api.getAttractionList({ pageNum: 1, pageSize: 20, keyword: kw }),
      api.getFoodList({ pageNum: 1, pageSize: 20, keyword: kw, status: 1 })
    ])

    scenics.value = recordsOf(scenicRes)
    strategies.value = recordsOf(strategyRes)
    attractions.value = recordsOf(attractionRes)
    foods.value = recordsOf(foodRes)

    // 如果关键词精确命中某个景区，则补充加载该景区下属景点。
    // 注意：美食和攻略表没有 scenic_id 字段，因此不再用 scenicId 去请求它们，避免返回不相关数据。
    const matchedScenic = scenics.value.find(s => s.name && s.name.trim() === kw)
    if (matchedScenic?.id) {
      const scenicAttractionsRes = await api.getAttractionList({
        pageNum: 1,
        pageSize: 50,
        scenicId: matchedScenic.id
      })
      attractions.value = mergeUnique(attractions.value, recordsOf(scenicAttractionsRes))
    }
  } catch (e) {
    console.error('搜索失败', e)
    scenics.value = []
    strategies.value = []
    attractions.value = []
    foods.value = []
  } finally {
    loading.value = false
  }
}

watch(() => route.query.keyword, (newKeyword) => {
  const kw = typeof newKeyword === 'string' ? newKeyword : ''
  keyword.value = kw
  searchKeyword.value = kw
  activeTab.value = 'all'
  loadData()
}, { immediate: true })
</script>

<style scoped>
.nav-pills .nav-link {
  color: #6b7280;
  border-radius: 20px;
  padding: 8px 20px;
  margin-right: 8px;
}

.nav-pills .nav-link.active {
  background: #0ea5e9;
  color: #fff;
}

.nav-pills .nav-link:hover:not(.active) {
  background: #f3f4f6;
}
</style>
