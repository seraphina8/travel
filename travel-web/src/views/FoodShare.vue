<template>
  <div class="page-header food-share-header">
    <div class="container">
      <nav aria-label="breadcrumb" class="mb-2">
        <ol class="breadcrumb breadcrumb-custom">
          <li class="breadcrumb-item">
            <router-link to="/"><i class="bi bi-house-door"></i> 首页</router-link>
          </li>
          <li class="breadcrumb-item">
            <router-link to="/food"><i class="bi bi-cup-hot"></i> 美食推荐</router-link>
          </li>
          <li class="breadcrumb-item active">分享美食</li>
        </ol>
      </nav>
      <h1 class="h3 fw-bold mb-1"><i class="bi bi-share me-2"></i>分享美食</h1>
      <p class="text-white-50 mb-0 small">分享你发现的美味佳肴</p>
    </div>
  </div>
  
  <div class="container py-4">
    <div class="row g-4">
      <!-- 分享表单 -->
      <div class="col-lg-8">
        <div class="form-card">
          <h5 class="form-title">发布美食</h5>
          
          <form @submit.prevent="submitFood">
            <div class="form-group">
              <label class="form-label">美食名称 *</label>
              <input type="text" class="form-control" v-model="form.name" placeholder="如：北京烤鸭" required>
            </div>
            
            <div class="form-row">
              <div class="form-group half">
                <label class="form-label">省份</label>
                <select class="form-control" v-model="form.province">
                  <option value="">请选择</option>
                  <option v-for="p in provinces" :key="p" :value="p">{{ p }}</option>
                </select>
              </div>
              <div class="form-group half">
                <label class="form-label">城市</label>
                <input type="text" class="form-control" v-model="form.city" placeholder="如：北京市">
              </div>
              <div class="form-group half">
                <label class="form-label">详细地址</label>
                <input type="text" class="form-control" v-model="form.address" placeholder="如：王府井大街XX号">
              </div>
            </div>
            
            <div class="form-group">
              <label class="form-label">封面图片</label>
              <div class="upload-area">
                <div class="cover-preview" v-if="form.coverImage">
                  <img :src="form.coverImage" alt="封面预览">
                  <button type="button" class="remove-btn" @click="form.coverImage = ''">
                    <i class="bi bi-x"></i>
                  </button>
                </div>
                <div class="upload-placeholder" v-else @click="$refs.fileInput.click()">
                  <i class="bi bi-cloud-upload"></i>
                  <p>点击上传图片</p>
                </div>
                <input type="file" ref="fileInput" accept="image/*" @change="handleFileUpload" style="display: none;">
              </div>
            </div>
            
            <div class="form-group">
              <label class="form-label">美食介绍</label>
              <textarea class="form-control" v-model="form.description" rows="5" placeholder="介绍一下这道美食的特色、口味、推荐理由..."></textarea>
            </div>
            
            <div class="form-group">
              <label class="form-label">标签</label>
              <div class="tag-options" v-if="tags.length">
                <button
                  v-for="tag in tags"
                  :key="tag.id"
                  type="button"
                  class="tag-option"
                  :class="{ active: selectedTags.includes(tag.name) }"
                  @click="toggleTag(tag.name)"
                >
                  {{ tag.name }}
                </button>
              </div>
              <input
                type="text"
                class="form-control mt-2"
                v-model="customTags"
                placeholder="也可以手动输入，多个标签用英文逗号分隔"
              >
            </div>

            <div class="form-actions">
              <button type="submit" class="submit-btn" :disabled="submitting">
                <i class="bi bi-send"></i> {{ submitting ? '提交中...' : '提交分享' }}
              </button>
              <button type="button" class="reset-btn" @click="resetForm">
                <i class="bi bi-arrow-counterclockwise"></i> 重置
              </button>
            </div>
          </form>
        </div>
      </div>
      
      <!-- 我的分享 -->
      <div class="col-lg-4">
        <div class="my-food-card">
          <h5 class="card-title">我的分享</h5>
          
          <div class="my-food-list">
            <div class="my-food-item" v-for="item in myFoods" :key="item.id">
              <img :src="item.coverImage || defaultImage" :alt="item.name">
              <div class="my-food-info">
                <h6>{{ item.name }}</h6>
                <div class="food-status">
                  <span class="status-badge" :class="item.status === 1 ? 'approved' : 'pending'">
                    {{ item.status === 1 ? '已通过' : '待审核' }}
                  </span>
                  <small>{{ formatDate(item.createTime) }}</small>
                </div>
              </div>
              <button class="delete-btn" @click="deleteFood(item.id)" title="删除">
                <i class="bi bi-trash"></i>
              </button>
            </div>
            
            <div class="empty-my-food" v-if="myFoods.length === 0">
              <i class="bi bi-inbox"></i>
              <p>暂无分享记录</p>
            </div>
          </div>
        </div>
        
        <!-- 分享须知 -->
        <div class="tips-card">
          <h5 class="card-title">分享须知</h5>
          <ul class="tips-list">
            <li>请确保分享的美食信息真实有效</li>
            <li>上传清晰的美食图片更容易通过审核</li>
            <li>详细的介绍可以帮助其他用户了解美食</li>
            <li>分享内容需经过审核后才会公开展示</li>
            <li>禁止发布虚假、违规内容</li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api'

const router = useRouter()
const submitting = ref(false)
const myFoods = ref([])
const fileInput = ref(null)
const tags = ref([])
const selectedTags = ref([])
const customTags = ref('')
const defaultImage = 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=400'

const provinces = [
  '北京', '天津', '河北', '山西', '内蒙古', '辽宁', '吉林', '黑龙江',
  '上海', '江苏', '浙江', '安徽', '福建', '江西', '山东', '河南',
  '湖北', '湖南', '广东', '广西', '海南', '重庆', '四川', '贵州',
  '云南', '西藏', '陕西', '甘肃', '青海', '宁夏', '新疆'
]

const form = ref({
  name: '',
  province: '',
  city: '',
  address: '',
  coverImage: '',
  tags: '',
  description: ''
})

const mergedTags = computed(() => {
  const manual = customTags.value
    .split(',')
    .map(item => item.trim())
    .filter(Boolean)
  return Array.from(new Set([...selectedTags.value, ...manual])).join(',')
})

const loadTags = async () => {
  try {
    const res = await api.getTagList(4)
    if (res.code === 200) {
      tags.value = res.data || []
    }
  } catch (e) {}
}

const toggleTag = (name) => {
  if (selectedTags.value.includes(name)) {
    selectedTags.value = selectedTags.value.filter(item => item !== name)
  } else {
    selectedTags.value.push(name)
  }
}

const handleFileUpload = async (e) => {
  const file = e.target.files[0]
  if (!file) return
  try {
    const res = await api.uploadFile(file)
    if (res.code === 200) {
      form.value.coverImage = res.data
    } else {
      alert('上传失败: ' + res.message)
    }
  } catch (err) {
    alert('上传失败')
  }
  e.target.value = ''
}

const submitFood = async () => {
  if (!localStorage.getItem('userToken')) {
    alert('请先登录')
    router.push('/login')
    return
  }
  
  if (!form.value.name) {
    alert('请输入美食名称')
    return
  }
  
  submitting.value = true
  try {
    const res = await api.shareFood({
      ...form.value,
      tags: mergedTags.value
    })
    if (res.code === 200) {
      alert('分享成功，等待审核')
      resetForm()
      loadMyFoods()
    } else {
      alert(res.message || '分享失败')
    }
  } catch (e) {
    alert('分享失败')
  } finally {
    submitting.value = false
  }
}

const resetForm = () => {
  form.value = {
    name: '',
    province: '',
    city: '',
    address: '',
    coverImage: '',
    tags: '',
    description: ''
  }
  selectedTags.value = []
  customTags.value = ''
}

const loadMyFoods = async () => {
  if (!localStorage.getItem('userToken')) return
  try {
    const res = await api.getMyFoods()
    if (res.code === 200) {
      myFoods.value = res.data || []
    }
  } catch (e) {}
}

const deleteFood = async (id) => {
  if (!confirm('确定要删除这条分享吗？')) return
  try {
    const res = await api.deleteMyFood(id)
    if (res.code === 200) {
      alert('删除成功')
      loadMyFoods()
    } else {
      alert(res.message || '删除失败')
    }
  } catch (e) {
    alert('删除失败')
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

onMounted(() => {
  if (!localStorage.getItem('userToken')) {
    alert('请先登录')
    router.push('/login')
    return
  }
  loadTags()
  loadMyFoods()
})
</script>

<style scoped>
/* 统一蓝色主题 */
.food-share-header {
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

.food-share-header h1 {
  color: #fff;
}

/* 表单卡片 - 直角设计 */
.form-card {
  background: #fff;
  border: 1px solid #e5e7eb;
  padding: 24px;
}

.form-title {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 20px 0;
  padding-bottom: 8px;
  border-bottom: 2px solid #5a9bcf;
  display: inline-block;
}

.form-group {
  margin-bottom: 20px;
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

.form-row {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.form-group.half {
  flex: 1;
  min-width: 150px;
}

/* 上传区域 */
.upload-area {
  width: 200px;
  height: 150px;
}

.cover-preview {
  position: relative;
  width: 100%;
  height: 100%;
}

.cover-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.remove-btn {
  position: absolute;
  top: -8px;
  right: -8px;
  width: 24px;
  height: 24px;
  background: #e74c3c;
  color: #fff;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.remove-btn:hover {
  background: #c0392b;
}

.upload-placeholder {
  width: 100%;
  height: 100%;
  border: 1px solid #e5e7eb;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  background: #f9fafb;
}

.upload-placeholder:hover {
  border-color: #5a9bcf;
  background: #fff;
}

.upload-placeholder i {
  font-size: 32px;
  color: #95a5a6;
}

.upload-placeholder p {
  font-size: 12px;
  color: #95a5a6;
  margin: 8px 0 0 0;
}

.tag-options {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-option {
  border: 1px solid #dcdfe6;
  background: #fff;
  color: #607080;
  padding: 6px 12px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.tag-option:hover {
  border-color: #5a9bcf;
  color: #5a9bcf;
}

.tag-option.active {
  background: #5a9bcf;
  border-color: #5a9bcf;
  color: #fff;
}

/* 表单按钮 */
.form-actions {
  display: flex;
  gap: 12px;
  margin-top: 24px;
}

.submit-btn {
  background: #5a9bcf;
  color: #fff;
  border: none;
  padding: 8px 24px;
  cursor: pointer;
  font-size: 14px;
}

.submit-btn:hover:not(:disabled) {
  background: #4a8bbf;
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.reset-btn {
  background: #fff;
  border: 1px solid #e5e7eb;
  padding: 8px 24px;
  cursor: pointer;
  font-size: 14px;
  color: #7f8c8d;
}

.reset-btn:hover {
  border-color: #5a9bcf;
  color: #5a9bcf;
}

/* 我的分享卡片 */
.my-food-card {
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

.my-food-list {
  max-height: 400px;
  overflow-y: auto;
}

.my-food-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #e5e7eb;
}

.my-food-item:last-child {
  border-bottom: none;
}

.my-food-item img {
  width: 60px;
  height: 60px;
  object-fit: cover;
}

.my-food-info {
  flex: 1;
  min-width: 0;
}

.my-food-info h6 {
  margin: 0 0 6px 0;
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.food-status {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.status-badge {
  padding: 2px 8px;
  font-size: 10px;
  font-weight: 500;
}

.status-badge.approved {
  background: #d1fae5;
  color: #10b981;
}

.status-badge.pending {
  background: #fed7aa;
  color: #f59e0b;
}

.food-status small {
  font-size: 10px;
  color: #95a5a6;
}

.delete-btn {
  background: none;
  border: 1px solid #e5e7eb;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #95a5a6;
}

.delete-btn:hover {
  border-color: #e74c3c;
  color: #e74c3c;
}

.empty-my-food {
  text-align: center;
  padding: 48px 20px;
  color: #95a5a6;
}

.empty-my-food i {
  font-size: 48px;
}

.empty-my-food p {
  margin: 12px 0 0 0;
}

/* 须知卡片 */
.tips-card {
  background: #fff;
  border: 1px solid #e5e7eb;
  padding: 20px;
}

.tips-list {
  padding-left: 20px;
  margin: 0;
}

.tips-list li {
  color: #7f8c8d;
  font-size: 13px;
  margin-bottom: 8px;
  line-height: 1.5;
}

.tips-list li:last-child {
  margin-bottom: 0;
}

/* 响应式 */
@media (max-width: 768px) {
  .food-share-header {
    padding: 30px 0;
  }
  
  .form-card {
    padding: 16px;
  }
  
  .form-row {
    flex-direction: column;
  }
  
  .form-group.half {
    width: 100%;
  }
  
  .form-actions {
    flex-direction: column;
  }
  
  .submit-btn,
  .reset-btn {
    width: 100%;
    text-align: center;
  }
  
  .upload-area {
    width: 100%;
  }
}
</style>
