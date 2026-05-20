<template>
  <div>
    <div class="page-header strategy-create-header">
      <div class="container">
        <nav aria-label="breadcrumb" class="mb-2">
          <ol class="breadcrumb breadcrumb-custom">
            <li class="breadcrumb-item">
              <router-link to="/"><i class="bi bi-house-door"></i> 首页</router-link>
            </li>
            <li class="breadcrumb-item">
              <router-link to="/strategy"><i class="bi bi-journal-richtext"></i> 攻略</router-link>
            </li>
            <li class="breadcrumb-item active">发布攻略</li>
          </ol>
        </nav>
        <h1 class="h3 fw-bold mb-1"><i class="bi bi-pencil-square me-2"></i>发布攻略</h1>
        <p class="text-white-50 mb-0 small">记录路线、经验和旅途里的好用信息</p>
      </div>
    </div>

    <div class="container py-4">
      <div class="row g-4">
        <div class="col-lg-8">
          <div class="form-card">
            <h5 class="form-title">攻略内容</h5>

            <form @submit.prevent="submitStrategy">
              <div class="form-group">
                <label class="form-label">攻略标题 *</label>
                <input
                  v-model.trim="form.title"
                  type="text"
                  class="form-control"
                  maxlength="200"
                  placeholder="例如：北京三日经典路线"
                  required
                />
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
                  v-model.trim="customTags"
                  type="text"
                  class="form-control mt-2"
                  placeholder="也可以手动输入，多个标签用英文逗号分隔"
                />
              </div>

              <div class="form-group">
                <label class="form-label">封面图</label>
                <div class="upload-area">
                  <div class="cover-preview" v-if="form.coverImage">
                    <img :src="form.coverImage" alt="封面预览" />
                    <button type="button" class="remove-btn" @click="form.coverImage = ''">
                      <i class="bi bi-x"></i>
                    </button>
                  </div>
                  <div class="upload-placeholder" v-else @click="fileInput?.click()">
                    <i class="bi bi-cloud-upload"></i>
                    <p>点击上传封面图</p>
                  </div>
                  <input ref="fileInput" type="file" accept="image/*" @change="handleFileUpload" hidden />
                </div>
              </div>

              <div class="form-group">
                <label class="form-label">攻略正文 *</label>
                <textarea
                  v-model.trim="form.content"
                  class="form-control"
                  rows="12"
                  placeholder="写下路线安排、交通住宿、避坑经验、推荐理由等..."
                  required
                ></textarea>
              </div>

              <div class="form-actions">
                <button type="submit" class="submit-btn" :disabled="submitting">
                  <i class="bi bi-send"></i> {{ submitting ? '提交中...' : '提交审核' }}
                </button>
                <button type="button" class="reset-btn" @click="resetForm">
                  <i class="bi bi-arrow-counterclockwise"></i> 重置
                </button>
              </div>
            </form>
          </div>
        </div>

        <div class="col-lg-4">
          <div class="side-card">
            <h5 class="card-title">我的攻略</h5>
            <div class="my-list">
              <div class="my-item" v-for="item in myStrategies" :key="item.id">
                <img :src="item.coverImage || defaultImage" :alt="item.title" />
                <div class="my-info">
                  <h6>{{ item.title }}</h6>
                  <div class="item-meta">
                    <span class="status-badge" :class="getStatusClass(item.status)">
                      {{ getStatusText(item.status) }}
                    </span>
                    <small>{{ formatDate(item.createTime) }}</small>
                  </div>
                </div>
              </div>

              <div class="empty-state" v-if="myStrategies.length === 0">
                <i class="bi bi-journal-x"></i>
                <p>暂无发布记录</p>
              </div>
            </div>
          </div>

          <div class="side-card">
            <h5 class="card-title">发布提示</h5>
            <ul class="tips-list">
              <li>标题尽量包含目的地和天数。</li>
              <li>正文建议写清路线、交通、费用和注意事项。</li>
              <li>提交后需要管理员审核，通过后才会展示。</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api'

const router = useRouter()
const submitting = ref(false)
const fileInput = ref(null)
const tags = ref([])
const selectedTags = ref([])
const customTags = ref('')
const myStrategies = ref([])
const defaultImage = 'https://images.unsplash.com/photo-1476514525535-07fb3b4ae5f1?w=400'

const form = ref({
  title: '',
  content: '',
  coverImage: ''
})

const mergedTags = computed(() => {
  const manual = customTags.value
    .split(',')
    .map(item => item.trim())
    .filter(Boolean)
  return Array.from(new Set([...selectedTags.value, ...manual])).join(',')
})

const loadTags = async () => {
  const res = await api.getTagList(2)
  if (res.code === 200) {
    tags.value = res.data || []
  }
}

const loadMyStrategies = async () => {
  if (!localStorage.getItem('userToken')) return
  const res = await api.getMyStrategies()
  if (res.code === 200) {
    myStrategies.value = res.data || []
  }
}

const toggleTag = (name) => {
  if (selectedTags.value.includes(name)) {
    selectedTags.value = selectedTags.value.filter(item => item !== name)
  } else {
    selectedTags.value.push(name)
  }
}

const handleFileUpload = async (event) => {
  const file = event.target.files?.[0]
  if (!file) return

  try {
    const res = await api.uploadFile(file)
    if (res.code === 200) {
      form.value.coverImage = res.data
    } else {
      alert(res.message || '上传失败')
    }
  } catch (e) {
    alert('上传失败')
  } finally {
    event.target.value = ''
  }
}

const submitStrategy = async () => {
  if (!localStorage.getItem('userToken')) {
    alert('请先登录')
    router.push('/login')
    return
  }

  if (!form.value.title || !form.value.content) {
    alert('请填写标题和正文')
    return
  }

  submitting.value = true
  try {
    const res = await api.publishStrategy({
      ...form.value,
      tags: mergedTags.value
    })
    if (res.code === 200) {
      alert('发布成功，等待审核')
      resetForm()
      loadMyStrategies()
    } else {
      alert(res.message || '发布失败')
    }
  } catch (e) {
    alert('发布失败')
  } finally {
    submitting.value = false
  }
}

const resetForm = () => {
  form.value = {
    title: '',
    content: '',
    coverImage: ''
  }
  selectedTags.value = []
  customTags.value = ''
}

const getStatusText = (status) => {
  if (status === 1) return '已发布'
  if (status === 2) return '已拒绝'
  return '待审核'
}

const getStatusClass = (status) => {
  if (status === 1) return 'approved'
  if (status === 2) return 'rejected'
  return 'pending'
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN')
}

onMounted(() => {
  loadTags()
  loadMyStrategies()
})
</script>

<style scoped>
.strategy-create-header {
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

.breadcrumb-custom .breadcrumb-item.active,
.strategy-create-header h1 {
  color: #fff;
}

.form-card,
.side-card {
  background: #fff;
  border: 1px solid #e5e7eb;
  padding: 24px;
}

.side-card + .side-card {
  margin-top: 20px;
}

.form-title,
.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 20px;
}

.form-label {
  font-size: 14px;
  font-weight: 500;
  color: #2c3e50;
  margin-bottom: 8px;
}

.form-control {
  border: 1px solid #dcdfe6;
  border-radius: 2px;
  font-size: 14px;
  padding: 10px 12px;
}

.form-control:focus {
  border-color: #5a9bcf;
  box-shadow: none;
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
}

.tag-option.active {
  background: #5a9bcf;
  border-color: #5a9bcf;
  color: #fff;
}

.upload-area {
  border: 1px dashed #cfd8e3;
  min-height: 180px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f9fbfd;
}

.upload-placeholder {
  text-align: center;
  color: #7f8c8d;
  cursor: pointer;
}

.upload-placeholder i {
  font-size: 36px;
  color: #5a9bcf;
}

.cover-preview {
  position: relative;
  width: 100%;
}

.cover-preview img {
  width: 100%;
  max-height: 260px;
  object-fit: cover;
}

.remove-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 32px;
  height: 32px;
  border: none;
  background: rgba(0, 0, 0, 0.65);
  color: #fff;
}

.form-actions {
  display: flex;
  gap: 12px;
}

.submit-btn,
.reset-btn {
  border: none;
  padding: 10px 22px;
  font-size: 14px;
}

.submit-btn {
  background: #5a9bcf;
  color: #fff;
}

.submit-btn:disabled {
  opacity: 0.7;
}

.reset-btn {
  background: #fff;
  border: 1px solid #dcdfe6;
  color: #607080;
}

.my-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.my-item {
  display: flex;
  gap: 12px;
  border-bottom: 1px solid #edf0f3;
  padding-bottom: 12px;
}

.my-item img {
  width: 72px;
  height: 54px;
  object-fit: cover;
  flex-shrink: 0;
}

.my-info {
  min-width: 0;
}

.my-info h6 {
  font-size: 14px;
  margin: 0 0 6px;
  color: #2c3e50;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

.status-badge {
  font-size: 12px;
  padding: 2px 8px;
}

.status-badge.approved {
  background: #e8f5e9;
  color: #2e7d32;
}

.status-badge.pending {
  background: #fff8e1;
  color: #f57f17;
}

.status-badge.rejected {
  background: #ffebee;
  color: #c62828;
}

.empty-state {
  text-align: center;
  color: #95a5a6;
  padding: 28px 0;
}

.empty-state i {
  font-size: 36px;
}

.tips-list {
  padding-left: 18px;
  margin: 0;
  color: #607080;
  font-size: 14px;
  line-height: 1.8;
}

@media (max-width: 768px) {
  .strategy-create-header {
    padding: 30px 0;
  }

  .form-actions {
    flex-direction: column;
  }
}
</style>
