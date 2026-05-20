<template>
  <div class="page-header trip-header">
    <div class="container">
      <nav aria-label="breadcrumb" class="mb-2">
        <ol class="breadcrumb breadcrumb-custom">
          <li class="breadcrumb-item">
            <router-link to="/"
              ><i class="bi bi-house-door"></i> 首页</router-link
            >
          </li>
          <li class="breadcrumb-item active">
            <i class="bi bi-calendar-check"></i> 行程规划
          </li>
        </ol>
      </nav>
      <h1 class="h3 fw-bold mb-2 text-white">
        <i class="bi bi-calendar-check me-2"></i>行程规划
      </h1>
      <p class="text-white-50 mb-0 small">发现精彩行程，规划完美旅途</p>
    </div>
  </div>

  <div class="container py-5">
    <!-- 行程规划入口 -->
    <div class="plan-grid mb-5">
      <div class="plan-card" @click="openAiPlan">
        <div class="plan-icon">
          <i class="bi bi-robot"></i>
        </div>
        <div class="plan-content">
          <h3 class="plan-title">智能规划</h3>
          <p class="plan-desc">输入目的地，AI生成行程攻略</p>
        </div>
        <div class="plan-arrow">
          <i class="bi bi-arrow-right"></i>
        </div>
      </div>

      <div class="plan-card" @click="goToCreateTrip">
        <div class="plan-icon">
          <i class="bi bi-pencil"></i>
        </div>
        <div class="plan-content">
          <h3 class="plan-title">手动创建</h3>
          <p class="plan-desc">自由定制专属行程</p>
        </div>
        <div class="plan-arrow">
          <i class="bi bi-arrow-right"></i>
        </div>
      </div>
    </div>

    <!-- 推荐行程 -->
    <section class="mb-5" v-if="recommendTrips.length > 0">
      <div class="section-header mb-3">
        <h4 class="section-title">推荐行程</h4>
      </div>

      <div class="row g-3">
        <div
          class="col-lg-4 col-md-6"
          v-for="trip in recommendTrips"
          :key="trip.id"
        >
          <div class="trip-card">
            <div class="trip-cover">
              <img
                :src="trip.coverImage || defaultImage"
                :alt="trip.title"
                loading="lazy"
              />
              <span class="trip-days">{{ trip.days }}天</span>
            </div>
            <div class="trip-body">
              <h5 class="trip-title">{{ trip.title }}</h5>
              <p class="trip-description">
                {{ trip.description || "暂无描述" }}
              </p>
              <div class="trip-info">
                <span class="info-item">
                  <i class="bi bi-geo-alt"></i> {{ trip.dest || "目的地待定" }}
                </span>
                <span class="info-item">
                  <i class="bi bi-calendar"></i>
                  {{ trip.startDate ? formatDate(trip.startDate) : "随时出发" }}
                </span>
              </div>
              <div class="trip-footer">
                <span class="trip-author" v-if="trip.username">
                  <i class="bi bi-person"></i> {{ trip.username }}
                </span>
                <router-link :to="`/trip/${trip.id}`" class="btn-link">
                  查看详情 <i class="bi bi-arrow-right"></i>
                </router-link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 我的行程 -->
    <section v-if="isLoggedIn && myTrips.length > 0">
      <div class="section-header mb-3">
        <h4 class="section-title">我的行程</h4>
      </div>

      <div class="row g-3">
        <div class="col-lg-4 col-md-6" v-for="trip in myTrips" :key="trip.id">
          <div class="trip-card">
            <div class="trip-cover">
              <img
                :src="trip.coverImage || defaultImage"
                :alt="trip.title"
                loading="lazy"
              />
              <span class="trip-days">{{ trip.days }}天</span>
            </div>
            <div class="trip-body">
              <h5 class="trip-title">{{ trip.title }}</h5>
              <p class="trip-description">
                {{ trip.description || "暂无描述" }}
              </p>
              <div class="trip-info">
                <span class="info-item">
                  <i class="bi bi-calendar-check"></i>
                  {{ trip.startDate ? formatDate(trip.startDate) : "待定" }}
                </span>
                <span class="info-item">
                  <i class="bi bi-clock"></i>
                  {{
                    trip.createTime ? formatDate(trip.createTime) : "刚刚创建"
                  }}
                </span>
              </div>
              <div class="trip-footer">
                <div class="trip-actions">
                  <router-link :to="`/trip/${trip.id}`" class="btn-link"
                    >查看</router-link
                  >
                  <button class="btn-link" @click="editTrip(trip.id)">
                    编辑
                  </button>
                  <button class="btn-link delete" @click="deleteTrip(trip.id)">
                    删除
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>

  <!-- AI 规划弹窗 -->
  <div class="modal fade" id="aiModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content ai-modal-content">
        <div class="modal-header ai-modal-header">
          <div>
            <h5 class="modal-title">
              <i class="bi bi-robot me-2"></i>智能行程规划
            </h5>
            <p class="modal-subtitle">
              填写基本出行信息，系统将生成按天安排的行程方案
            </p>
          </div>
          <button
            type="button"
            class="btn-close"
            data-bs-dismiss="modal"
          ></button>
        </div>

        <div class="modal-body ai-modal-body">
          <div class="ai-tip">
            <i class="bi bi-info-circle"></i>
            <span
              >建议填写明确城市或目的地，例如“成都”“重庆”“西安”，不要输入过长句子。</span
            >
          </div>

          <div class="form-item">
            <label class="form-label">
              目的地 <span class="required">*</span>
            </label>
            <input
              v-model.trim="aiData.dest"
              type="text"
              class="form-control"
              maxlength="20"
              placeholder="例如：成都、重庆、西安"
            />
            <div class="form-hint">
              目的地限 2-20 个字符，可填写城市或景区所在地区。
            </div>
          </div>

          <div class="form-row">
            <div class="form-item">
              <label class="form-label">
                游玩天数 <span class="required">*</span>
              </label>
              <input
                v-model.number="aiData.days"
                type="number"
                min="1"
                max="10"
                class="form-control"
                placeholder="1-10天"
              />
              <div class="form-hint">最多支持 10 天，避免生成内容过长。</div>
            </div>

            <div class="form-item">
              <label class="form-label">
                出发日期 <span class="required">*</span>
              </label>
              <input
                v-model="aiData.startDate"
                type="date"
                class="form-control"
                :min="todayDate"
                :max="maxStartDate"
              />
              <div class="form-hint">不能选择今天之前的日期。</div>
            </div>
          </div>

          <div class="form-item">
            <label class="form-label">旅行风格</label>
            <div class="style-options">
              <button
                v-for="item in travelTypes"
                :key="item.value"
                type="button"
                class="style-option"
                :class="{ active: aiData.type === item.value }"
                @click="aiData.type = item.value"
              >
                <i :class="item.icon"></i>
                <span>{{ item.label }}</span>
              </button>
            </div>
          </div>

          <div class="mb-3">
            <label class="form-label">行程可见性</label>
            <select v-model.number="aiData.isPublic" class="form-select">
              <option :value="0">私密行程，仅自己可见</option>
              <option :value="1">公开行程，其他用户可查看</option>
            </select>
          </div>

          <div v-if="aiError" class="ai-error">
            <i class="bi bi-exclamation-circle"></i>
            <span>{{ aiError }}</span>
          </div>
        </div>

        <div class="modal-footer ai-modal-footer">
          <button type="button" class="btn btn-light" data-bs-dismiss="modal">
            取消
          </button>
          <button
            class="btn btn-primary ai-generate-btn"
            :disabled="loading"
            @click="startAIPlan"
          >
            <i
              class="bi"
              :class="loading ? 'bi-hourglass-split' : 'bi-stars'"
            ></i>
            {{ loading ? "正在生成..." : "生成行程" }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { Modal } from "bootstrap";
import api from "../api";

const router = useRouter();
const isLoggedIn = computed(() => !!localStorage.getItem("userToken"));
const recommendTrips = ref([]);
const myTrips = ref([]);
const defaultImage =
  "https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=400";

const aiData = ref({
  dest: "",
  days: "",
  startDate: "",
  type: "休闲轻松",
  isPublic: 0,
});

const aiError = ref("");

const travelTypes = [
  { label: "休闲轻松", value: "休闲轻松", icon: "bi bi-cup-hot" },
  { label: "经典打卡", value: "经典打卡", icon: "bi bi-camera" },
  { label: "美食为主", value: "美食为主", icon: "bi bi-shop" },
  { label: "深度人文", value: "深度人文", icon: "bi bi-bank" },
];

const getDateStr = (offsetDay = 0) => {
  const date = new Date();
  date.setDate(date.getDate() + offsetDay);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
};

const todayDate = getDateStr(0);
const maxStartDate = getDateStr(365);

const validateAiPlan = () => {
  aiError.value = "";

  const dest = aiData.value.dest ? aiData.value.dest.trim() : "";
  const days = Number(aiData.value.days);
  const startDate = aiData.value.startDate;

  if (!dest) {
    aiError.value = "请填写目的地";
    return false;
  }

  if (dest.length < 2 || dest.length > 20) {
    aiError.value = "目的地长度应为2到20个字符";
    return false;
  }

  if (!/^[\u4e00-\u9fa5a-zA-Z0-9\s]+$/.test(dest)) {
    aiError.value = "目的地只能包含中文、字母、数字或空格";
    return false;
  }

  if (!Number.isInteger(days) || days < 1 || days > 10) {
    aiError.value = "游玩天数应为1到10天";
    return false;
  }

  if (!startDate) {
    aiError.value = "请选择出发日期";
    return false;
  }

  if (startDate < todayDate) {
    aiError.value = "出发日期不能早于今天";
    return false;
  }

  if (startDate > maxStartDate) {
    aiError.value = "出发日期不能超过一年后";
    return false;
  }

  return true;
};

const loading = ref(false);
let aiModal = null;

onMounted(() => {
  aiModal = new Modal(document.getElementById("aiModal"));
  loadRecommendTrips();
  loadMyTrips();
});

const formatDate = (date) => {
  if (!date) return "";
  return new Date(date).toLocaleDateString("zh-CN");
};

const loadRecommendTrips = async () => {
  try {
    const res = await api.getRecommendTrips();
    if (res.code === 200) recommendTrips.value = res.data || [];
  } catch (e) {
    console.error(e);
  }
};

const loadMyTrips = async () => {
  if (!isLoggedIn.value) return;
  try {
    const res = await api.getMyTrips();
    if (res.code === 200) myTrips.value = res.data || [];
  } catch (e) {
    console.error(e);
  }
};

const editTrip = (id) => {
  router.push(`/trip/edit/${id}`);
};

const deleteTrip = async (id) => {
  if (!confirm("确定删除？")) return;
  try {
    const res = await api.deleteTrip(id);
    if (res.code === 200) {
      myTrips.value = myTrips.value.filter((t) => t.id !== id);
    }
  } catch (e) {
    alert("删除失败");
  }
};

const openAiPlan = () => {
  aiError.value = "";

  if (!aiData.value.startDate) {
    aiData.value.startDate = getDateStr(1);
  }

  aiModal.show();
};

const goToCreateTrip = () => {
  router.push("/trip/create");
};

const startAIPlan = async () => {
  if (!validateAiPlan()) {
    return;
  }

  const d = {
    dest: aiData.value.dest.trim(),
    days: Number(aiData.value.days),
    startDate: aiData.value.startDate,
    type: aiData.value.type,
  };

  loading.value = true;
  aiError.value = "";

  try {
    const prompt = `
    请作为专业导游，生成【${d.dest}】${d.days}天详细行程，风格：${d.type}。

    请严格按照下面格式输出，不要使用Markdown符号，不要使用表格，不要输出多余解释。

    第1天
    08:30-10:00 景点名称
    推荐：推荐理由或游玩内容
    地址：所在区域或地址
    亮点：主要看点
    交通：交通建议
    贴士：注意事项

    10:30-12:00 景点名称
    推荐：推荐理由或游玩内容
    地址：所在区域或地址
    亮点：主要看点
    交通：交通建议
    贴士：注意事项

    第2天
    08:30-10:00 景点名称
    推荐：推荐理由或游玩内容
    地址：所在区域或地址
    亮点：主要看点
    交通：交通建议
    贴士：注意事项

    要求：
    1. 每一天必须以“第1天”“第2天”这种格式开头；
    2. 每个行程点必须以“时间 + 景点名称”的格式单独成行；
    3. 推荐、地址、亮点、交通、贴士只能作为说明，不能作为景点名称；
    4. 不要把“贴士、推荐、交通、地址、时间”等文字放在景点名称前面；
    5. 每天安排2到4个行程点；
    6. 时间安排要合理，早上不早于08:00，晚上不晚于21:00；
    7. 同一天内时间段不要重复；
    8. 内容简洁，适合保存到行程明细。
    `;

    const res = await api.aiChat(prompt, []);

    if (res.code === 200) {
      router.push({
        path: "/ai-trip-result",
        query: {
          content: res.data,
          dest: d.dest,
          days: d.days,
          startDate: d.startDate,
          isPublic: d.isPublic,
        },
      });

      aiModal.hide();
    } else {
      aiError.value = res.message || "生成失败，请重试";
    }
  } catch (e) {
    console.error(e);
    aiError.value = "生成失败，请检查网络或稍后重试";
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
/* 页面头部 - 浅蓝色 */
.page-header {
  background: #5a9bcf;
  padding: 40px 0;
}

.breadcrumb-custom {
  background: rgba(255, 255, 255, 0.2);
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

/* 规划卡片网格 */
.plan-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 40px;
}

.plan-card {
  display: flex;
  align-items: center;
  padding: 24px;
  background: #fff;
  border: 1px solid #e8f0f7;
  cursor: pointer;
  transition: all 0.2s;
}

.plan-card:hover {
  border-color: #5a9bcf;
  background: #fafdff;
}

.plan-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #5a9bcf;
  margin-right: 16px;
  flex-shrink: 0;
}

.plan-content {
  flex: 1;
}

.plan-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 4px 0;
  color: #2c3e50;
}

.plan-desc {
  font-size: 13px;
  color: #7f8c8d;
  margin: 0;
}

.plan-arrow {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #bdc3c7;
  transition: all 0.2s;
}

.plan-card:hover .plan-arrow {
  color: #5a9bcf;
  transform: translateX(4px);
}

/* 区块标题 */
.section-header {
  margin-bottom: 20px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
  color: #2c3e50;
  border-left: 3px solid #5a9bcf;
  padding-left: 12px;
}

/* 行程卡片 */
.trip-card {
  background: #fff;
  border: 1px solid #ecf0f1;
  transition: all 0.2s;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.trip-card:hover {
  border-color: #5a9bcf;
  box-shadow: 0 2px 8px rgba(90, 155, 207, 0.1);
}

.trip-cover {
  position: relative;
  height: 160px;
  overflow: hidden;
  background: #f8f9fa;
}

.trip-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.trip-days {
  position: absolute;
  top: 12px;
  right: 12px;
  background: rgba(0, 0, 0, 0.6);
  color: #fff;
  padding: 2px 8px;
  font-size: 12px;
  font-weight: 500;
}

.trip-body {
  padding: 16px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.trip-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 8px 0;
  color: #2c3e50;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.trip-description {
  font-size: 13px;
  color: #7f8c8d;
  line-height: 1.4;
  margin: 0 0 12px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 36px;
}

.trip-info {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
  flex-wrap: wrap;
}

.info-item {
  font-size: 12px;
  color: #95a5a6;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.trip-footer {
  margin-top: auto;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.trip-author {
  font-size: 12px;
  color: #95a5a6;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.btn-link {
  background: none;
  border: none;
  color: #5a9bcf;
  font-size: 13px;
  font-weight: 500;
  text-decoration: none;
  padding: 0;
  cursor: pointer;
  transition: all 0.2s;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.btn-link:hover {
  color: #3a7ca5;
  gap: 6px;
}

.btn-link.delete {
  color: #e74c3c;
}

.btn-link.delete:hover {
  color: #c0392b;
}

.trip-actions {
  display: flex;
  gap: 16px;
  width: 100%;
  justify-content: flex-end;
}

/* 弹窗样式 */
.modal-content {
  border: none;
}

.modal-header {
  background: #fefefe;
  border-bottom: 1px solid #ecf0f1;
  padding: 16px 20px;
}

.modal-title {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
}

.modal-body {
  padding: 20px;
}

.modal-footer {
  padding: 16px 20px;
  border-top: 1px solid #ecf0f1;
}

.form-label {
  font-size: 13px;
  font-weight: 500;
  margin-bottom: 6px;
  color: #2c3e50;
}

.form-control,
.form-select {
  border: 1px solid #dcdfe6;
  font-size: 14px;
  padding: 8px 12px;
  border-radius: 2px;
}

.form-control:focus,
.form-select:focus {
  border-color: #5a9bcf;
  box-shadow: none;
}

.btn-primary {
  background: #5a9bcf;
  border: none;
  padding: 8px 16px;
  font-size: 14px;
  font-weight: 500;
  border-radius: 2px;
}

.btn-primary:hover {
  background: #4a8bbf;
}

/* 响应式 */
@media (max-width: 768px) {
  .page-header {
    padding: 30px 0;
  }

  .plan-grid {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .plan-card {
    padding: 20px;
  }

  .trip-cover {
    height: 140px;
  }
}

/* AI规划弹窗优化 */
.ai-modal-content {
  border: none;
  border-radius: 10px;
  overflow: hidden;
}

.ai-modal-header {
  background: linear-gradient(135deg, #5a9bcf, #7fb8e6);
  color: #fff;
  border-bottom: none;
  padding: 20px 24px;
}

.ai-modal-header .modal-title {
  color: #fff;
  font-size: 20px;
  font-weight: 600;
}

.ai-modal-header .btn-close {
  filter: invert(1);
  opacity: 0.9;
}

.modal-subtitle {
  margin: 6px 0 0;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.85);
}

.ai-modal-body {
  padding: 22px 24px;
  background: #fbfdff;
}

.ai-tip {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  background: #eef7ff;
  border: 1px solid #d7ebff;
  color: #4a7fa8;
  font-size: 13px;
  line-height: 1.6;
  padding: 10px 12px;
  border-radius: 6px;
  margin-bottom: 18px;
}

.ai-tip i {
  margin-top: 2px;
  color: #5a9bcf;
}

.form-item {
  margin-bottom: 18px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1.2fr;
  gap: 14px;
}

.required {
  color: #e74c3c;
}

.form-hint {
  font-size: 12px;
  color: #95a5a6;
  margin-top: 5px;
}

.style-options {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
}

.style-option {
  border: 1px solid #dce8f3;
  background: #fff;
  color: #4f6375;
  border-radius: 8px;
  padding: 12px 8px;
  font-size: 13px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  transition: all 0.2s;
}

.style-option i {
  font-size: 20px;
  color: #5a9bcf;
}

.style-option:hover {
  border-color: #5a9bcf;
  background: #f5fbff;
}

.style-option.active {
  border-color: #5a9bcf;
  background: #eaf5ff;
  color: #2f6f9f;
  font-weight: 600;
}

.ai-error {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #fff2f0;
  color: #d93026;
  border: 1px solid #ffd6d2;
  border-radius: 6px;
  padding: 10px 12px;
  font-size: 13px;
}

.ai-modal-footer {
  border-top: 1px solid #edf1f5;
  padding: 16px 24px;
  background: #fff;
}

.ai-generate-btn {
  min-width: 130px;
  border-radius: 6px;
}

@media (max-width: 768px) {
  .form-row {
    grid-template-columns: 1fr;
  }

  .style-options {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
