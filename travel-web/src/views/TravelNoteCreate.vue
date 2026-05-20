<template>
  <div>
    <div class="page-header note-create-header">
      <div class="container">
        <nav aria-label="breadcrumb" class="mb-2">
          <ol class="breadcrumb breadcrumb-custom">
            <li class="breadcrumb-item">
              <router-link to="/"><i class="bi bi-house-door"></i> 首页</router-link>
            </li>
            <li class="breadcrumb-item">
              <router-link to="/note"><i class="bi bi-journal-text"></i> 游记</router-link>
            </li>
            <li class="breadcrumb-item active">发布游记</li>
          </ol>
        </nav>
        <h1 class="h3 fw-bold mb-1"><i class="bi bi-pencil-square me-2"></i>发布游记</h1>
        <p class="text-white-50 mb-0 small">记录你的旅行故事、照片和经验</p>
      </div>
    </div>

    <div class="container py-4">
      <div class="row g-4">
        <div class="col-lg-8">
          <div class="form-card">
            <h5 class="form-title">游记内容</h5>

            <form @submit.prevent="submitNote">
              <div class="form-row">
                <div class="form-group half">
                  <label class="form-label">游记标题 *</label>
                  <input v-model.trim="form.title" class="form-control" placeholder="给你的游记起个标题" required />
                </div>
                <div class="form-group half">
                  <label class="form-label">打卡省份 *</label>
                  <select v-model="form.province" class="form-control" required>
                    <option value="">选择省份</option>
                    <option v-for="p in provinces" :key="p" :value="p">{{ p }}</option>
                  </select>
                </div>
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
                  class="form-control mt-2"
                  placeholder="也可以手动输入，多个标签用英文逗号分隔"
                />
              </div>

              <div class="form-group">
                <label class="form-label">游记图片</label>
                <div class="image-upload-box" @click="triggerImageUpload">
                  <i class="bi bi-cloud-upload"></i>
                  <span>点击上传图片</span>
                </div>
                <input ref="imageInputRef" type="file" accept="image/*" multiple @change="handleImageSelect" hidden />
                <div class="image-preview-grid" v-if="noteImages.length">
                  <div class="image-preview-item" v-for="(img, index) in noteImages" :key="img">
                    <img :src="img" alt="游记图片" />
                    <button type="button" class="image-remove" @click="removeImage(index)">
                      <i class="bi bi-x"></i>
                    </button>
                  </div>
                </div>
              </div>

              <div class="form-group">
                <label class="form-label">游记正文 *</label>
                <textarea
                  v-model.trim="form.content"
                  class="form-control note-textarea"
                  rows="12"
                  placeholder="写下你的旅行故事、路线体验、注意事项和推荐理由..."
                  required
                ></textarea>
                <div v-if="false" class="editor-toolbar">
                  <button type="button" class="toolbar-btn" @click="formatText('bold')" title="加粗">
                    <i class="bi bi-type-bold"></i>
                  </button>
                  <button type="button" class="toolbar-btn" @click="formatText('italic')" title="斜体">
                    <i class="bi bi-type-italic"></i>
                  </button>
                  <button type="button" class="toolbar-btn" @click="formatText('insertUnorderedList')" title="列表">
                    <i class="bi bi-list-ul"></i>
                  </button>
                  <button type="button" class="toolbar-btn" @click="triggerImageUpload" title="插入图片">
                    <i class="bi bi-image"></i>
                  </button>
                  <input ref="imageInputRef" type="file" accept="image/*" @change="handleImageSelect" hidden />
                </div>
                <div
                  v-if="false"
                  ref="editorRef"
                  class="rich-editor"
                  contenteditable="true"
                  @input="onEditorInput"
                  @paste="onEditorPaste"
                ></div>
              </div>

              <div class="form-actions">
                <button type="submit" class="submit-btn" :disabled="submitting">
                  <i class="bi bi-send"></i> {{ submitting ? '发布中...' : '发布游记' }}
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
            <h5 class="card-title">发布提示</h5>
            <ul class="tips-list">
              <li>标题建议包含地点和主题。</li>
              <li>正文可插入图片，适合记录路线、体验和避坑信息。</li>
              <li>发布后会同步更新你的旅行足迹。</li>
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
const tags = ref([])
const selectedTags = ref([])
const customTags = ref('')
const noteImages = ref([])
const editorRef = ref(null)
const imageInputRef = ref(null)
const savedRange = ref(null)

const form = ref({
  title: '',
  province: '',
  content: ''
})

const provinces = [
  '北京', '天津', '河北', '山西', '内蒙古', '辽宁', '吉林', '黑龙江',
  '上海', '江苏', '浙江', '安徽', '福建', '江西', '山东', '河南',
  '湖北', '湖南', '广东', '广西', '海南', '重庆', '四川', '贵州',
  '云南', '西藏', '陕西', '甘肃', '青海', '宁夏', '新疆', '台湾',
  '香港', '澳门'
]

const mergedTags = computed(() => {
  const manual = customTags.value
    .split(',')
    .map(item => item.trim())
    .filter(Boolean)
  return Array.from(new Set([...selectedTags.value, ...manual])).join(',')
})

const loadTags = async () => {
  const res = await api.getTagList(5)
  if (res.code === 200) {
    tags.value = res.data || []
  }
}

const toggleTag = (name) => {
  if (selectedTags.value.includes(name)) {
    selectedTags.value = selectedTags.value.filter(item => item !== name)
  } else {
    selectedTags.value.push(name)
  }
}

const formatText = (command) => {
  restoreEditorSelection()
  document.execCommand(command, false, null)
  editorRef.value?.focus()
}

const onEditorInput = () => {
  form.value.content = editorRef.value?.innerHTML || ''
  saveEditorSelection()
}

const saveEditorSelection = () => {
  const selection = window.getSelection()
  if (!selection?.rangeCount || !editorRef.value) return
  const range = selection.getRangeAt(0)
  if (editorRef.value.contains(range.commonAncestorContainer)) {
    savedRange.value = range.cloneRange()
  }
}

const restoreEditorSelection = () => {
  editorRef.value?.focus()
  if (!savedRange.value) return
  const selection = window.getSelection()
  selection?.removeAllRanges()
  selection?.addRange(savedRange.value)
}

const getContentImages = () => {
  const div = document.createElement('div')
  div.innerHTML = form.value.content || ''
  return Array.from(div.querySelectorAll('img'))
    .map(img => img.getAttribute('src'))
    .filter(Boolean)
}

const uploadAndInsertImage = async (file) => {
  const res = await api.uploadFile(file)
  if (res.code === 200) {
    restoreEditorSelection()
    document.execCommand('insertHTML', false, `<p><img src="${res.data}" style="max-width: 100%; margin: 10px 0;"></p>`)
    onEditorInput()
  } else {
    alert(res.message || '图片上传失败')
  }
}

const onEditorPaste = async (event) => {
  const items = event.clipboardData?.items
  if (!items) return

  for (let i = 0; i < items.length; i++) {
    if (items[i].type.includes('image')) {
      event.preventDefault()
      const file = items[i].getAsFile()
      if (file) await uploadAndInsertImage(file)
      return
    }
  }
}

const triggerImageUpload = () => {
  imageInputRef.value?.click()
}

const handleImageSelect = async (event) => {
  const files = Array.from(event.target.files || [])
  for (const file of files) {
    const res = await api.uploadFile(file)
    if (res.code === 200) {
      noteImages.value.push(res.data)
    } else {
      alert(res.message || '图片上传失败')
    }
  }
  event.target.value = ''
}

const removeImage = (index) => {
  noteImages.value.splice(index, 1)
}

const resetForm = () => {
  form.value = { title: '', province: '', content: '' }
  noteImages.value = []
  selectedTags.value = []
  customTags.value = ''
  if (editorRef.value) {
    editorRef.value.innerHTML = ''
  }
}

const submitNote = async () => {
  if (!form.value.title || !form.value.province || !form.value.content) {
    alert('请填写完整信息')
    return
  }

  submitting.value = true
  try {
    const res = await api.createTravelNote({
      ...form.value,
      tags: mergedTags.value,
      images: JSON.stringify(noteImages.value)
    })
    if (res.code === 200) {
      alert('发布成功')
      router.push('/user?tab=publish')
    } else {
      alert(res.message || '发布失败')
    }
  } catch (e) {
    alert('发布失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadTags()
})
</script>

<style scoped>
.note-create-header {
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
.note-create-header h1 {
  color: #fff;
}

.form-card,
.side-card {
  background: #fff;
  border: 1px solid #e5e7eb;
  padding: 24px;
}

.form-title,
.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 20px;
}

.form-row {
  display: flex;
  gap: 16px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group.half {
  flex: 1;
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

.image-upload-box {
  border: 1px dashed #cfd8e3;
  min-height: 120px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  background: #f9fbfd;
  color: #607080;
  cursor: pointer;
}

.image-upload-box i {
  color: #5a9bcf;
  font-size: 30px;
}

.image-preview-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 12px;
  margin-top: 12px;
}

.image-preview-item {
  position: relative;
  aspect-ratio: 4 / 3;
  border: 1px solid #e5e7eb;
  overflow: hidden;
}

.image-preview-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-remove {
  position: absolute;
  top: 6px;
  right: 6px;
  width: 26px;
  height: 26px;
  border: none;
  background: rgba(0, 0, 0, 0.65);
  color: #fff;
}

.note-textarea {
  resize: vertical;
  line-height: 1.7;
}

.editor-toolbar {
  display: flex;
  gap: 6px;
  padding: 8px;
  border: 1px solid #dcdfe6;
  border-bottom: none;
  background: #f9fbfd;
}

.toolbar-btn {
  width: 34px;
  height: 34px;
  border: 1px solid #dcdfe6;
  background: #fff;
  color: #607080;
}

.rich-editor {
  min-height: 320px;
  border: 1px solid #dcdfe6;
  padding: 14px;
  outline: none;
  line-height: 1.7;
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

.reset-btn {
  background: #fff;
  border: 1px solid #dcdfe6;
  color: #607080;
}

.tips-list {
  padding-left: 18px;
  margin: 0;
  color: #607080;
  font-size: 14px;
  line-height: 1.8;
}

@media (max-width: 768px) {
  .form-row,
  .form-actions {
    flex-direction: column;
  }
}
</style>
