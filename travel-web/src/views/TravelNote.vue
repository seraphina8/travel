<template>
  <div class="page-header note-header">
    <div class="container">
      <nav aria-label="breadcrumb" class="mb-2">
        <ol class="breadcrumb breadcrumb-custom">
          <li class="breadcrumb-item">
            <router-link to="/"><i class="bi bi-house-door"></i> 首页</router-link>
          </li>
          <li class="breadcrumb-item active">
            <i class="bi bi-journal-text"></i> 游记打卡
          </li>
        </ol>
      </nav>
      <h1 class="h3 fw-bold mb-1"><i class="bi bi-journal-text me-2"></i>游记打卡</h1>
      <p class="text-white-50 mb-0 small">分享你的旅行故事</p>
    </div>
  </div>
  
  <div class="container py-4">
    <!-- 顶部操作栏 -->
    <div class="action-bar">
      <div class="action-left">
        <h5 class="action-title">最新游记</h5>
        <select class="province-select" v-model="filterProvince" @change="loadNotes">
          <option value="">全部省份</option>
          <option v-for="p in provinces" :key="p" :value="p">{{ p }}</option>
        </select>
        <select class="province-select" v-model="filterTag" @change="loadNotes">
          <option value="">全部标签</option>
          <option v-for="tag in tags" :key="tag.id" :value="tag.name">{{ tag.name }}</option>
        </select>
      </div>
      <router-link class="publish-btn" :to="{ name: 'TravelNoteCreate' }" v-if="isLoggedIn">
        <i class="bi bi-plus-lg"></i> 发布游记
      </router-link>
      <router-link to="/login" class="login-btn" v-else>
        <i class="bi bi-box-arrow-in-right"></i> 登录后发布
      </router-link>
    </div>
    
    <div class="row g-4">
      <!-- 左侧：中国地图 -->
      <div class="col-lg-5">
        <div class="map-card">
          <h5 class="map-title"><i class="bi bi-map"></i> 我去过的省份</h5>
          <div id="chinaMap" class="map-container"></div>
          <div class="map-stats">
            <span class="stats-badge">已打卡 {{ visitedProvinces.length }} 个省份</span>
          </div>
        </div>
      </div>
      
      <!-- 右侧：游记列表 -->
      <div class="col-lg-7">
        <div class="note-list">
          <div class="note-card" v-for="note in notes" :key="note.id">
            <div class="note-header-info">
              <img :src="note.userAvatar || getAvatar(note.userId)" class="avatar">
              <div class="user-info">
                <h6 class="user-name">{{ note.username || '旅行者' }}</h6>
                <small class="note-time">{{ formatTime(note.createTime) }}</small>
              </div>
              <span class="province-tag">{{ note.province }}</span>
            </div>
            <div class="note-content" @click="$router.push(`/note/${note.id}`)">
              <h5 class="note-title">{{ note.title }}</h5>
              <p class="note-text">{{ getTextContent(note.content) }}</p>
            </div>
            <div class="note-footer-info">
              <span><i class="bi bi-eye"></i> {{ note.viewCount || 0 }}</span>
              <span><i class="bi bi-heart"></i> {{ note.likeCount || 0 }}</span>
              <button class="share-btn" @click="shareNote(note)">
                <i class="bi bi-share"></i> 分享
              </button>
            </div>
          </div>
          
          <div class="empty-notes" v-if="notes.length === 0">
            <i class="bi bi-journal-x"></i>
            <p>暂无游记</p>
          </div>
        </div>
      </div>
    </div>
  </div>
  
  <!-- 发布游记弹窗 -->
  <div class="modal fade" id="noteModal" tabindex="-1" ref="modalRef">
    <div class="modal-dialog modal-lg">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">发布游记</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="submitNote">
            <div class="form-row">
              <div class="form-group half">
                <label class="form-label">游记标题 *</label>
                <input type="text" class="form-control" v-model="noteForm.title" placeholder="给你的游记起个标题" required>
              </div>
              <div class="form-group half">
                <label class="form-label">打卡省份 *</label>
                <select class="form-control" v-model="noteForm.province" required>
                  <option value="">选择省份</option>
                  <option v-for="p in provinces" :key="p" :value="p">{{ p }}</option>
                </select>
              </div>
            </div>
            <div class="form-group full">
              <label class="form-label">标签</label>
              <div class="tag-options" v-if="tags.length">
                <button
                  v-for="tag in tags"
                  :key="tag.id"
                  type="button"
                  class="tag-option"
                  :class="{ active: selectedNoteTags.includes(tag.name) }"
                  @click="toggleNoteTag(tag.name)"
                >
                  {{ tag.name }}
                </button>
              </div>
              <input type="text" class="form-control mt-2" v-model="customNoteTags" placeholder="也可以手动输入，多个标签用英文逗号分隔">
            </div>
            <div class="form-group full">
              <label class="form-label">游记内容 *</label>
              <div class="editor-toolbar">
                <button type="button" class="toolbar-btn" @click="formatText('bold')" title="加粗">
                  <i class="bi bi-type-bold"></i>
                </button>
                <button type="button" class="toolbar-btn" @click="formatText('italic')" title="斜体">
                  <i class="bi bi-type-italic"></i>
                </button>
                <button type="button" class="toolbar-btn" @click="formatText('underline')" title="下划线">
                  <i class="bi bi-type-underline"></i>
                </button>
                <span class="toolbar-divider"></span>
                <button type="button" class="toolbar-btn" @click="formatText('insertUnorderedList')" title="无序列表">
                  <i class="bi bi-list-ul"></i>
                </button>
                <button type="button" class="toolbar-btn" @click="formatText('insertOrderedList')" title="有序列表">
                  <i class="bi bi-list-ol"></i>
                </button>
                <span class="toolbar-divider"></span>
                <button type="button" class="toolbar-btn" @click="formatText('justifyLeft')" title="左对齐">
                  <i class="bi bi-text-left"></i>
                </button>
                <button type="button" class="toolbar-btn" @click="formatText('justifyCenter')" title="居中">
                  <i class="bi bi-text-center"></i>
                </button>
                <button type="button" class="toolbar-btn" @click="formatText('justifyRight')" title="右对齐">
                  <i class="bi bi-text-right"></i>
                </button>
                <span class="toolbar-divider"></span>
                <button type="button" class="toolbar-btn" @click="triggerImageUpload" title="插入图片">
                  <i class="bi bi-image"></i>
                </button>
                <input type="file" ref="imageInputRef" accept="image/*" @change="handleImageSelect" style="display: none;">
              </div>
              <div 
                ref="editorRef"
                class="rich-editor" 
                contenteditable="true" 
                @input="onEditorInput"
                @paste="onEditorPaste"
                placeholder="分享你的旅行故事..."
              ></div>
            </div>
          </form>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn-cancel" data-bs-dismiss="modal">取消</button>
          <button type="button" class="btn-submit" @click="submitNote" :disabled="submitting">
            <span v-if="submitting"><i class="bi bi-arrow-repeat spin"></i> 发布中...</span>
            <span v-else><i class="bi bi-send"></i> 发布游记</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { Modal } from 'bootstrap'
import * as echarts from 'echarts'
import api from '../api'

const isLoggedIn = computed(() => !!localStorage.getItem('userToken'))
const notes = ref([])
const visitedProvinces = ref([])
const filterProvince = ref('')
const filterTag = ref('')
const tags = ref([])
const selectedNoteTags = ref([])
const customNoteTags = ref('')
const submitting = ref(false)
const modalRef = ref(null)
const editorRef = ref(null)
const imageInputRef = ref(null)
let noteModal = null

const noteForm = ref({
  title: '',
  province: '',
  tags: '',
  content: ''
})

const mergedNoteTags = computed(() => {
  const manual = customNoteTags.value
    .split(',')
    .map(item => item.trim())
    .filter(Boolean)
  return Array.from(new Set([...selectedNoteTags.value, ...manual])).join(',')
})

const provinces = [
  '北京', '天津', '河北', '山西', '内蒙古', '辽宁', '吉林', '黑龙江',
  '上海', '江苏', '浙江', '安徽', '福建', '江西', '山东', '河南',
  '湖北', '湖南', '广东', '广西', '海南', '重庆', '四川', '贵州',
  '云南', '西藏', '陕西', '甘肃', '青海', '宁夏', '新疆', '台湾',
  '香港', '澳门'
]

const getAvatar = (id) => `https://api.dicebear.com/7.x/avataaars/svg?seed=${id}`

const getTextContent = (html) => {
  if (!html) return ''
  const div = document.createElement('div')
  div.innerHTML = html
  const text = div.textContent || div.innerText || ''
  return text.length > 100 ? text.substring(0, 100) + '...' : text
}

const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleDateString('zh-CN')
}

const loadNotes = async () => {
  try {
    const res = await api.getTravelNotes({
      province: filterProvince.value || undefined,
      tag: filterTag.value || undefined
    })
    if (res.code === 200) {
      notes.value = res.data.records || []
    }
  } catch (e) {
    console.error('加载游记失败', e)
  }
}

const loadTags = async () => {
  const res = await api.getTagList(5)
  if (res.code === 200) {
    tags.value = res.data || []
  }
}

const toggleNoteTag = (name) => {
  if (selectedNoteTags.value.includes(name)) {
    selectedNoteTags.value = selectedNoteTags.value.filter(item => item !== name)
  } else {
    selectedNoteTags.value.push(name)
  }
}

const loadFootprint = async () => {
  if (!isLoggedIn.value) return
  try {
    const res = await api.getFootprint()
    if (res.code === 200) {
      visitedProvinces.value = res.data || []
      initMap()
    }
  } catch (e) {
    console.error('加载足迹失败', e)
  }
}

const initMap = async () => {
  const mapContainer = document.getElementById('chinaMap')
  if (!mapContainer) return
  
  try {
    let chinaJson
    try {
      chinaJson = await fetch('https://geo.datav.aliyun.com/areas_v3/bound/100000_full.json').then(r => {
        if (!r.ok) throw new Error('Network error')
        return r.json()
      })
    } catch (fetchErr) {
      console.warn('远程地图数据加载失败，使用简化版地图', fetchErr)
      const { default: simpleChinaJson } = await import('../assets/china-simple.json')
      chinaJson = simpleChinaJson
    }
    echarts.registerMap('china', chinaJson)
    
    const chart = echarts.init(mapContainer)
    
    const provinceNameMap = {
      '北京': '北京市', '天津': '天津市', '河北': '河北省', '山西': '山西省',
      '内蒙古': '内蒙古自治区', '辽宁': '辽宁省', '吉林': '吉林省', '黑龙江': '黑龙江省',
      '上海': '上海市', '江苏': '江苏省', '浙江': '浙江省', '安徽': '安徽省',
      '福建': '福建省', '江西': '江西省', '山东': '山东省', '河南': '河南省',
      '湖北': '湖北省', '湖南': '湖南省', '广东': '广东省', '广西': '广西壮族自治区',
      '海南': '海南省', '重庆': '重庆市', '四川': '四川省', '贵州': '贵州省',
      '云南': '云南省', '西藏': '西藏自治区', '陕西': '陕西省', '甘肃': '甘肃省',
      '青海': '青海省', '宁夏': '宁夏回族自治区', '新疆': '新疆维吾尔自治区',
      '台湾': '台湾省', '香港': '香港特别行政区', '澳门': '澳门特别行政区'
    }
    
    const mapProvinceNames = chinaJson.features.map(f => f.properties.name)
    
    const data = mapProvinceNames.map(mapName => {
      const isVisited = visitedProvinces.value.some(v => {
        return v === mapName || 
               provinceNameMap[v] === mapName || 
               Object.entries(provinceNameMap).find(([k, val]) => val === mapName)?.[0] === v
      })
      return {
        name: mapName,
        value: isVisited ? 1 : 0
      }
    })
    
    chart.setOption({
      tooltip: {
        trigger: 'item',
        formatter: (params) => {
          return params.name + (params.value ? ' ✓ 已打卡' : '')
        }
      },
      visualMap: {
        show: false,
        min: 0,
        max: 1,
        inRange: {
          color: ['#e5e7eb', '#5a9bcf']
        }
      },
      series: [{
        type: 'map',
        map: 'china',
        roam: true,
        label: {
          show: true,
          fontSize: 8,
          color: '#666'
        },
        emphasis: {
          label: { show: true, fontSize: 12 },
          itemStyle: { areaColor: '#4a8bbf' }
        },
        data: data
      }]
    })
    
    window.addEventListener('resize', () => chart.resize())
  } catch (e) {
    console.error('地图加载失败', e)
  }
}

const openNoteModal = () => {
  if (!noteModal) {
    noteModal = new Modal(modalRef.value)
  }
  noteForm.value = { title: '', province: '', tags: '', content: '' }
  selectedNoteTags.value = []
  customNoteTags.value = ''
  noteModal.show()
  nextTick(() => {
    if (editorRef.value) {
      editorRef.value.innerHTML = ''
    }
  })
}

const formatText = (command) => {
  document.execCommand(command, false, null)
  editorRef.value?.focus()
}

const onEditorInput = () => {
  noteForm.value.content = editorRef.value?.innerHTML || ''
}

const onEditorPaste = async (e) => {
  const items = e.clipboardData?.items
  if (!items) return
  
  for (let i = 0; i < items.length; i++) {
    if (items[i].type.indexOf('image') !== -1) {
      e.preventDefault()
      const file = items[i].getAsFile()
      if (file) {
        await uploadAndInsertImage(file)
      }
      return
    }
  }
  
  e.preventDefault()
  const text = e.clipboardData.getData('text/plain')
  document.execCommand('insertText', false, text)
}

const uploadAndInsertImage = async (file) => {
  try {
    const res = await api.uploadFile(file)
    if (res.code === 200) {
      const imgUrl = res.data
      document.execCommand('insertHTML', false, `<img src="${imgUrl}" style="max-width: 100%; margin: 10px 0;">`)
      onEditorInput()
    } else {
      alert('图片上传失败: ' + res.message)
    }
  } catch (err) {
    alert('图片上传失败')
  }
}

const triggerImageUpload = () => {
  imageInputRef.value?.click()
}

const handleImageSelect = async (e) => {
  const file = e.target.files[0]
  if (file) {
    await uploadAndInsertImage(file)
  }
  e.target.value = ''
}

const submitNote = async () => {
  if (!noteForm.value.title || !noteForm.value.province || !noteForm.value.content) {
    alert('请填写完整信息')
    return
  }
  
  submitting.value = true
  try {
    const res = await api.createTravelNote({
      ...noteForm.value,
      tags: mergedNoteTags.value
    })
    if (res.code === 200) {
      alert('发布成功！')
      noteForm.value = { title: '', province: '', tags: '', content: '' }
      selectedNoteTags.value = []
      customNoteTags.value = ''
      if (editorRef.value) {
        editorRef.value.innerHTML = ''
      }
      noteModal?.hide()
      loadNotes()
      loadFootprint()
    } else {
      alert(res.message)
    }
  } catch (e) {
    alert('发布失败')
  } finally {
    submitting.value = false
  }
}

const shareNote = (note) => {
  const url = window.location.origin + '/note/' + note.id
  if (navigator.share) {
    navigator.share({ title: note.title, url })
  } else {
    navigator.clipboard.writeText(url)
    alert('链接已复制到剪贴板')
  }
}

onMounted(() => {
  loadTags()
  loadNotes()
  loadFootprint()
  if (!isLoggedIn.value) {
    initMap()
  }
})
</script>

<style scoped>
/* 统一蓝色主题 */
.note-header {
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

.note-header h1 {
  color: #fff;
}

/* 操作栏 */
.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  flex-wrap: wrap;
  gap: 12px;
}

.action-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.action-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
}

.province-select {
  padding: 6px 12px;
  border: 1px solid #e5e7eb;
  font-size: 14px;
  outline: none;
  background: #fff;
}

.province-select:focus {
  border-color: #5a9bcf;
}

.publish-btn {
  background: #5a9bcf;
  color: #fff;
  border: none;
  padding: 8px 20px;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.publish-btn:hover {
  background: #4a8bbf;
}

.login-btn {
  background: transparent;
  color: #5a9bcf;
  border: 1px solid #5a9bcf;
  padding: 8px 20px;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.login-btn:hover {
  background: #5a9bcf;
  color: #fff;
}

/* 地图卡片 */
.map-card {
  background: #fff;
  border: 1px solid #e5e7eb;
  padding: 20px;
}

.map-title {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 16px 0;
  padding-bottom: 8px;
  border-bottom: 2px solid #5a9bcf;
  display: inline-block;
}

.map-container {
  height: 400px;
  width: 100%;
}

.map-stats {
  margin-top: 16px;
  text-align: center;
}

.stats-badge {
  background: #5a9bcf;
  color: #fff;
  padding: 6px 16px;
  font-size: 14px;
  font-weight: 500;
}

/* 游记卡片 */
.note-card {
  background: #fff;
  border: 1px solid #e5e7eb;
  margin-bottom: 16px;
}

.note-header-info {
  display: flex;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #e5e7eb;
}

.note-header-info .avatar {
  width: 40px;
  height: 40px;
  object-fit: cover;
  margin-right: 12px;
}

.user-info {
  flex: 1;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 2px 0;
}

.note-time {
  font-size: 11px;
  color: #95a5a6;
}

.province-tag {
  background: #e8f0f7;
  color: #5a9bcf;
  padding: 4px 12px;
  font-size: 12px;
  font-weight: 500;
}

.note-content {
  padding: 16px;
  cursor: pointer;
}

.note-title {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 8px 0;
}

.note-text {
  color: #7f8c8d;
  font-size: 13px;
  line-height: 1.5;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.note-footer-info {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  background: #f9fafb;
  gap: 16px;
  border-top: 1px solid #e5e7eb;
}

.note-footer-info span {
  color: #95a5a6;
  font-size: 12px;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.share-btn {
  background: transparent;
  border: 1px solid #e5e7eb;
  padding: 4px 12px;
  font-size: 12px;
  cursor: pointer;
  margin-left: auto;
  color: #7f8c8d;
}

.share-btn:hover {
  border-color: #5a9bcf;
  color: #5a9bcf;
}

.empty-notes {
  text-align: center;
  padding: 60px 20px;
  background: #fff;
  border: 1px solid #e5e7eb;
}

.empty-notes i {
  font-size: 48px;
  color: #95a5a6;
}

.empty-notes p {
  color: #7f8c8d;
  margin: 12px 0 0 0;
}

/* 弹窗样式 */
.modal-content {
  border: 1px solid #e5e7eb;
}

.modal-header {
  border-bottom: 1px solid #e5e7eb;
  padding: 16px 20px;
}

.modal-title {
  color: #2c3e50;
  font-size: 18px;
  font-weight: 600;
}

.modal-body {
  padding: 20px;
}

.modal-footer {
  border-top: 1px solid #e5e7eb;
  padding: 16px 20px;
  gap: 12px;
}

.form-row {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
}

.form-group {
  margin-bottom: 16px;
}

.form-group.half {
  flex: 1;
}

.form-group.full {
  width: 100%;
}

.form-label {
  display: block;
  font-size: 13px;
  font-weight: 500;
  color: #2c3e50;
  margin-bottom: 6px;
}

.form-control {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #e5e7eb;
  font-size: 14px;
  outline: none;
}

.form-control:focus {
  border-color: #5a9bcf;
}

/* 编辑器 */
.editor-toolbar {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 8px 12px;
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-bottom: none;
}

.toolbar-btn {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  border: 1px solid transparent;
  color: #7f8c8d;
  cursor: pointer;
}

.toolbar-btn:hover {
  border-color: #e5e7eb;
  background: #fff;
  color: #5a9bcf;
}

.toolbar-divider {
  width: 1px;
  height: 20px;
  background: #e5e7eb;
  margin: 0 4px;
}

.rich-editor {
  min-height: 200px;
  max-height: 400px;
  overflow-y: auto;
  padding: 12px;
  border: 1px solid #e5e7eb;
  outline: none;
  line-height: 1.6;
  font-size: 14px;
}

.rich-editor:empty:before {
  content: attr(placeholder);
  color: #95a5a6;
}

.rich-editor:focus {
  border-color: #5a9bcf;
}

.btn-cancel {
  background: #fff;
  border: 1px solid #e5e7eb;
  padding: 8px 20px;
  cursor: pointer;
  color: #7f8c8d;
}

.btn-cancel:hover {
  border-color: #5a9bcf;
  color: #5a9bcf;
}

.btn-submit {
  background: #5a9bcf;
  color: #fff;
  border: none;
  padding: 8px 20px;
  cursor: pointer;
}

.btn-submit:hover:not(:disabled) {
  background: #4a8bbf;
}

.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.spin {
  display: inline-block;
  animation: spin 1s linear infinite;
}

/* 响应式 */
@media (max-width: 768px) {
  .note-header {
    padding: 30px 0;
  }
  
  .form-row {
    flex-direction: column;
  }
  
  .action-bar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .action-left {
    justify-content: space-between;
  }
  
  .publish-btn,
  .login-btn {
    text-align: center;
  }
}
</style>
