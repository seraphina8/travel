<template>
  <div>
    <div class="page-header">
      <div class="container">
        <h1>意见反馈</h1>
        <p>把遇到的问题或建议告诉我们</p>
      </div>
    </div>

    <div class="container py-5">
      <div class="feedback-layout">
        <section class="feedback-panel">
          <h5>提交反馈</h5>
          <textarea
            v-model.trim="content"
            class="form-control"
            rows="7"
            maxlength="500"
            placeholder="请描述你遇到的问题、页面位置或希望改进的地方"
          ></textarea>
          <div class="form-footer">
            <span>{{ content.length }}/500</span>
            <button
              class="submit-btn"
              :disabled="submitting || !content"
              @click="submit"
            >
              {{ submitting ? "提交中..." : "提交反馈" }}
            </button>
          </div>
        </section>

        <section class="feedback-panel">
          <h5>我的反馈</h5>
          <div v-if="feedbacks.length === 0" class="empty-state">
            暂无反馈记录
          </div>
          <div v-for="item in feedbacks" :key="item.id" class="feedback-item">
            <div class="item-head">
              <span
                class="status"
                :class="item.status === 1 ? 'done' : 'pending'"
              >
                {{ item.status === 1 ? "已回复" : "待处理" }}
              </span>
              <small>{{ formatDate(item.createTime) }}</small>
            </div>
            <p>{{ item.content }}</p>
            <div v-if="item.reply" class="reply">
              <strong>管理员回复：</strong>{{ item.reply }}
            </div>
          </div>
        </section>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import api from "../api";

const content = ref("");
const submitting = ref(false);
const feedbacks = ref([]);

const loadFeedbacks = async () => {
  const res = await api.getMyFeedback();
  if (res.code === 200) {
    feedbacks.value = res.data || [];
  }
};

const submit = async () => {
  if (!content.value) return;
  submitting.value = true;
  try {
    const res = await api.submitFeedback({ content: content.value });
    if (res.code === 200) {
      alert("反馈提交成功");
      content.value = "";
      loadFeedbacks();
    } else {
      alert(res.message || "提交失败");
    }
  } catch (e) {
    alert("提交失败，请稍后重试");
  } finally {
    submitting.value = false;
  }
};

const formatDate = (date) => {
  if (!date) return "";
  return new Date(date).toLocaleString("zh-CN");
};

onMounted(loadFeedbacks);
</script>

<style scoped>
.page-header {
  background: linear-gradient(135deg, #5a9bcf 0%, #4a8bbf 100%);
  padding: 42px 0;
  color: #fff;
}

.page-header h1 {
  margin: 0 0 8px;
  font-size: 28px;
  font-weight: 600;
}

.page-header p {
  margin: 0;
  color: rgba(255, 255, 255, 0.85);
}

.feedback-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(320px, 420px);
  gap: 24px;
}

.feedback-panel {
  background: #fff;
  border: 1px solid #e5e7eb;
  padding: 24px;
}

.feedback-panel h5 {
  margin: 0 0 16px;
  color: #2c3e50;
  font-weight: 600;
}

.form-control {
  border-radius: 2px;
  resize: vertical;
}

.form-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
  color: #7f8c8d;
  font-size: 13px;
}

.submit-btn {
  border: none;
  background: #5a9bcf;
  color: #fff;
  padding: 9px 24px;
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.feedback-item {
  border-bottom: 1px solid #edf0f3;
  padding: 14px 0;
}

.feedback-item:last-child {
  border-bottom: none;
}

.item-head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 8px;
}

.status {
  font-size: 12px;
  padding: 2px 8px;
}

.status.pending {
  background: #fff8e1;
  color: #f57f17;
}

.status.done {
  background: #e8f5e9;
  color: #2e7d32;
}

.feedback-item p {
  margin: 0;
  color: #334155;
  line-height: 1.7;
}

.reply {
  margin-top: 10px;
  padding: 10px 12px;
  background: #f8fafc;
  color: #475569;
  font-size: 13px;
}

.empty-state {
  padding: 28px 0;
  color: #95a5a6;
  text-align: center;
}

@media (max-width: 992px) {
  .feedback-layout {
    grid-template-columns: 1fr;
  }
}
</style>
