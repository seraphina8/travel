<template>
  <div class="modal fade" id="shareModal" tabindex="-1" ref="modalRef">
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title"><i class="bi bi-share"></i> 分享</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
        </div>
        <div class="modal-body">
          <div class="share-preview mb-4" v-if="title">
            <h6>{{ title }}</h6>
            <p class="text-muted small mb-0">{{ description }}</p>
          </div>
          
          <div class="share-buttons">
            <button class="share-btn qq" @click="shareToQQ">
              <i class="bi bi-chat-square-dots"></i>
              <span>QQ</span>
            </button>
            <button class="share-btn weibo" @click="shareToWeibo">
              <i class="bi bi-sina-weibo"></i>
              <span>微博</span>
            </button>
            <button class="share-btn wechat" @click="shareToWechat">
              <i class="bi bi-wechat"></i>
              <span>微信</span>
            </button>
            <button class="share-btn copy" @click="copyLink">
              <i class="bi bi-link-45deg"></i>
              <span>复制链接</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Modal } from 'bootstrap'
import ShareUtils from '../utils/share'

const props = defineProps({
  title: String,
  description: String,
  url: String,
  image: String
})

const modalRef = ref(null)
let modal = null

onMounted(() => {
  modal = new Modal(modalRef.value)
})

const show = () => {
  if (modal) modal.show()
}

const hide = () => {
  if (modal) modal.hide()
}

const shareToQQ = () => {
  ShareUtils.shareToQQ(props.title, props.url, props.description, props.image)
  hide()
}

const shareToWeibo = () => {
  ShareUtils.shareToWeibo(props.title, props.url, props.image)
  hide()
}

const shareToWechat = () => {
  ShareUtils.shareToWechat(props.url)
  hide()
}

const copyLink = () => {
  ShareUtils.copyLink(props.url)
  hide()
}

defineExpose({ show, hide })
</script>

<style scoped>
.share-preview {
  padding: 15px;
  background: #f8fafc;
  border-radius: 8px;
}

.share-buttons {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 15px;
}

.share-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 15px;
  border: none;
  border-radius: 12px;
  background: #f1f5f9;
  cursor: pointer;
  transition: all 0.2s;
}

.share-btn:hover {
  transform: translateY(-2px);
}

.share-btn i {
  font-size: 24px;
  margin-bottom: 8px;
}

.share-btn span {
  font-size: 12px;
  color: #64748b;
}

.share-btn.qq { background: #e8f4ff; }
.share-btn.qq i { color: #12b7f5; }

.share-btn.weibo { background: #fff1f0; }
.share-btn.weibo i { color: #e6162d; }

.share-btn.wechat { background: #e8f8e8; }
.share-btn.wechat i { color: #07c160; }

.share-btn.copy { background: #f3f4f6; }
.share-btn.copy i { color: #6b7280; }
</style>
