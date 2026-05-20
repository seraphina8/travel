<template>
  <div class="page-header map-header">
    <div class="container">
      <nav aria-label="breadcrumb" class="mb-2">
        <ol class="breadcrumb breadcrumb-custom">
          <li class="breadcrumb-item">
            <router-link to="/"><i class="bi bi-house-door"></i> 首页</router-link>
          </li>
          <li class="breadcrumb-item active">
            <i class="bi bi-map"></i> 景区定位
          </li>
        </ol>
      </nav>
      <h1 class="h3 fw-bold mb-1"><i class="bi bi-map me-2"></i>景区定位</h1>
      <p class="text-white-50 mb-0 small">探索全国热门景区定位</p>
    </div>
  </div>
  
  <div class="container-fluid py-4">
    <div class="row g-4">
      <!-- 左侧景区列表 -->
      <div class="col-lg-3">
        <div class="sidebar-card">
          <div class="search-area">
            <input type="text" class="search-input" v-model="searchKeyword" placeholder="搜索景区" @keyup.enter="searchScenic">
            <button class="search-btn" @click="searchScenic">
              <i class="bi bi-search"></i>
            </button>
          </div>
          
          <div class="scenic-list" ref="scenicListRef">
            <div 
              v-for="scenic in scenicList" 
              :key="scenic.id"
              :ref="el => { if (el) scenicItemRefs[scenic.id] = el }"
              class="scenic-item"
              :class="{ active: selectedScenic?.id === scenic.id }"
              @click="selectScenic(scenic)"
            >
              <img :src="scenic.coverImage || defaultImage" class="cover">
              <div class="info">
                <h6 class="name">{{ scenic.name }}</h6>
                <div class="meta">
                  <span class="level-badge">{{ scenic.level || '4A' }}</span>
                  <span class="price">¥{{ scenic.price || 0 }}</span>
                </div>
                <p class="address">
                  <i class="bi bi-geo-alt"></i> {{ scenic.address || '暂无地址' }}
                </p>
              </div>
            </div>
            
            <div class="empty-state" v-if="scenicList.length === 0">
              <i class="bi bi-map"></i>
              <p>暂无景区数据</p>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 右侧地图 -->
      <div class="col-lg-9">
        <div class="map-card">
          <div id="mapContainer" class="map-container"></div>
        </div>
        
        <!-- 地图工具栏 -->
        <div class="map-toolbar">
          <button class="tool-btn" @click="resetMap">
            <i class="bi bi-arrow-counterclockwise"></i> 重置视图
          </button>
          <button class="tool-btn" @click="locateMe">
            <i class="bi bi-crosshair"></i> 我的位置
          </button>
          <button class="tool-btn" @click="showAllMarkers">
            <i class="bi bi-pin-map"></i> 显示全部
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import AMapLoader from '@amap/amap-jsapi-loader'
import api from '../api'

const scenicList = ref([])
const selectedScenic = ref(null)
const searchKeyword = ref('')
const scenicListRef = ref(null)
const scenicItemRefs = {}
const defaultImage = 'https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=400'
let map = null
let markers = []
let infoWindow = null

const loadScenicList = async () => {
  try {
    const res = await api.getScenicList({ pageSize: 100 })
    if (res.code === 200) {
      scenicList.value = res.data.records || []
      addMarkers()
    }
  } catch (e) {
    console.error('加载景区列表失败', e)
  }
}

const initMap = async () => {
  window._AMapSecurityConfig = {
    securityJsCode: '734d51563a3b2c4a2433ae3481309408'
  }
  
  try {
    const AMap = await AMapLoader.load({
      key: 'd201b9704d5bff29ddd4510c45c75cf4',
      version: '2.0',
      plugins: ['AMap.ToolBar', 'AMap.Scale', 'AMap.InfoWindow', 'AMap.Geolocation']
    })
    
    map = new AMap.Map('mapContainer', {
      zoom: 5,
      center: [116.397428, 39.90923],
      mapStyle: 'amap://styles/fresh'
    })
    
    map.addControl(new AMap.ToolBar({ position: 'RT' }))
    map.addControl(new AMap.Scale())
    
    infoWindow = new AMap.InfoWindow({
      isCustom: true,
      autoMove: true,
      offset: new AMap.Pixel(0, -30)
    })
    
    loadScenicList()
  } catch (e) {
    console.error('地图加载失败', e)
  }
}

const addMarkers = () => {
  if (!map) return
  
  markers.forEach(m => map.remove(m))
  markers = []
  
  scenicList.value.forEach(scenic => {
    if (scenic.longitude && scenic.latitude) {
      const marker = new AMap.Marker({
        position: [scenic.longitude, scenic.latitude],
        title: scenic.name,
        icon: new AMap.Icon({
          size: new AMap.Size(32, 32),
          image: 'https://webapi.amap.com/theme/v1.3/markers/n/mark_r.png',
          imageSize: new AMap.Size(32, 32)
        }),
        extData: scenic
      })
      
      marker.on('click', () => {
        selectScenic(scenic)
      })
      
      markers.push(marker)
      map.add(marker)
    }
  })
  
  if (markers.length > 0) {
    map.setFitView(markers)
  }
}

const selectScenic = (scenic) => {
  selectedScenic.value = scenic
  
  // 滚动左侧列表到选中项
  const itemEl = scenicItemRefs[scenic.id]
  if (itemEl && scenicListRef.value) {
    itemEl.scrollIntoView({ behavior: 'smooth', block: 'center' })
  }
  
  if (scenic.longitude && scenic.latitude && map) {
    // 地图居中到选中景点
    map.setZoomAndCenter(14, [scenic.longitude, scenic.latitude])
    
    const content = `
      <div class="info-window">
        <div class="info-header">
          <h6>${scenic.name}</h6>
          <span class="level-badge">${scenic.level || '4A'}</span>
        </div>
        <img src="${scenic.coverImage || defaultImage}" class="info-img">
        <div class="info-body">
          <p><i class="bi bi-geo-alt"></i> ${scenic.address || '暂无地址'}</p>
          <p><i class="bi bi-currency-yen"></i> <span class="price">${scenic.price || 0}</span> 元起</p>
          <a href="/scenic/${scenic.id}" class="detail-link">查看详情</a>
        </div>
      </div>
    `
    
    infoWindow.setContent(content)
    infoWindow.open(map, [scenic.longitude, scenic.latitude])
  }
}

const searchScenic = () => {
  if (!searchKeyword.value.trim()) {
    loadScenicList()
    return
  }
  
  const keyword = searchKeyword.value.toLowerCase()
  const filtered = scenicList.value.filter(s => 
    s.name?.toLowerCase().includes(keyword) || 
    s.address?.toLowerCase().includes(keyword)
  )
  
  if (filtered.length > 0) {
    selectScenic(filtered[0])
  }
}

const resetMap = () => {
  if (map) {
    map.setZoomAndCenter(5, [116.397428, 39.90923])
    infoWindow.close()
    selectedScenic.value = null
  }
}

const locateMe = () => {
  if (map) {
    const geolocation = new AMap.Geolocation({
      enableHighAccuracy: true,
      timeout: 10000
    })
    
    geolocation.getCurrentPosition((status, result) => {
      if (status === 'complete') {
        map.setZoomAndCenter(12, [result.position.lng, result.position.lat])
      } else {
        alert('定位失败，请检查定位权限')
      }
    })
  }
}

const showAllMarkers = () => {
  if (map && markers.length > 0) {
    map.setFitView(markers)
    infoWindow.close()
    selectedScenic.value = null
  }
}

onMounted(() => {
  initMap()
})

onUnmounted(() => {
  if (map) {
    map.destroy()
  }
})
</script>

<style scoped>
/* 统一蓝色主题 */
.map-header {
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

.map-header h1 {
  color: #fff;
}

/* 侧边栏卡片 - 直角设计 */
.sidebar-card {
  background: #fff;
  border: 1px solid #e5e7eb;
}

.search-area {
  display: flex;
  padding: 16px;
  border-bottom: 1px solid #e5e7eb;
}

.search-input {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #e5e7eb;
  font-size: 14px;
  outline: none;
  transition: border-color 0.2s;
}

.search-input:focus {
  border-color: #5a9bcf;
}

.search-btn {
  background: #5a9bcf;
  color: #fff;
  border: none;
  padding: 8px 16px;
  cursor: pointer;
  transition: background 0.2s;
}

.search-btn:hover {
  background: #4a8bbf;
}

/* 景区列表 */
.scenic-list {
  max-height: 550px;
  overflow-y: auto;
}

.scenic-item {
  display: flex;
  padding: 12px;
  cursor: pointer;
  transition: all 0.2s;
  border-bottom: 1px solid #e5e7eb;
}

.scenic-item:hover {
  background: #f9fafb;
}

.scenic-item.active {
  background: #e8f0f7;
  border-left: 3px solid #5a9bcf;
}

.scenic-item .cover {
  width: 60px;
  height: 60px;
  object-fit: cover;
  margin-right: 12px;
}

.scenic-item .info {
  flex: 1;
  min-width: 0;
}

.scenic-item .name {
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 4px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.meta {
  display: flex;
  gap: 8px;
  margin-bottom: 4px;
  align-items: center;
}

.level-badge {
  background: #fbbf24;
  color: #2c3e50;
  padding: 2px 6px;
  font-size: 10px;
  font-weight: 500;
}

.price {
  color: #e74c3c;
  font-weight: 600;
  font-size: 12px;
}

.address {
  font-size: 11px;
  color: #95a5a6;
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 地图卡片 */
.map-card {
  background: #fff;
  border: 1px solid #e5e7eb;
  overflow: hidden;
}

.map-container {
  height: 550px;
  width: 100%;
}

/* 工具栏 */
.map-toolbar {
  display: flex;
  gap: 8px;
  margin-top: 16px;
}

.tool-btn {
  background: #fff;
  border: 1px solid #e5e7eb;
  padding: 8px 16px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
  color: #7f8c8d;
}

.tool-btn:hover {
  border-color: #5a9bcf;
  color: #5a9bcf;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 48px 20px;
  color: #95a5a6;
}

.empty-state i {
  font-size: 48px;
}

.empty-state p {
  margin: 12px 0 0 0;
}

/* 响应式 */
@media (max-width: 768px) {
  .map-header {
    padding: 30px 0;
  }
  
  .map-container {
    height: 400px;
  }
  
  .map-toolbar {
    flex-wrap: wrap;
  }
  
  .tool-btn {
    flex: 1;
  }
}
</style>

<style>
/* 信息窗口样式 - 直角设计 */
.info-window {
  background: #fff;
  border: 1px solid #e5e7eb;
  width: 260px;
  overflow: hidden;
}

.info-window .info-header {
  padding: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #e5e7eb;
  background: #f9fafb;
}

.info-window .info-header h6 {
  margin: 0;
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
}

.info-window .level-badge {
  background: #fbbf24;
  color: #2c3e50;
  padding: 2px 6px;
  font-size: 10px;
  font-weight: 500;
}

.info-window .info-img {
  width: 100%;
  height: 120px;
  object-fit: cover;
}

.info-window .info-body {
  padding: 12px;
  font-size: 12px;
}

.info-window .info-body p {
  margin: 0 0 8px 0;
  color: #7f8c8d;
  display: flex;
  align-items: center;
  gap: 6px;
}

.info-window .info-body .price {
  color: #e74c3c;
  font-weight: 600;
}

.info-window .detail-link {
  display: block;
  text-align: center;
  background: #5a9bcf;
  color: #fff;
  text-decoration: none;
  padding: 8px;
  margin-top: 8px;
  transition: background 0.2s;
}

.info-window .detail-link:hover {
  background: #4a8bbf;
}
</style>