<template>
  <div class="dashboard-workbench">
    <!-- 欢迎区域 -->
    <div class="welcome-card">
      <div>
        <h3>后台工作台</h3>
        <p>
          欢迎回来，{{
            adminName
          }}。这里用于处理日常管理任务和查看近期业务动态。
        </p>
      </div>
      <div class="today-box">
        <i class="bi bi-calendar3"></i>
        <span>{{ todayText }}</span>
      </div>
    </div>

    <!-- 待办事项 -->
    <div class="todo-grid">
      <router-link to="/order" class="todo-card">
        <div class="todo-icon order">
          <i class="bi bi-receipt"></i>
        </div>
        <div class="todo-info">
          <div class="todo-title">订单处理</div>
          <div class="todo-desc">查看门票订单、支付状态和使用状态</div>
        </div>
        <i class="bi bi-chevron-right todo-arrow"></i>
      </router-link>

      <router-link to="/strategy" class="todo-card">
        <div class="todo-icon strategy">
          <i class="bi bi-journal-check"></i>
        </div>
        <div class="todo-info">
          <div class="todo-title">攻略审核</div>
          <div class="todo-desc">审核用户发布的旅游攻略内容</div>
        </div>
        <i class="bi bi-chevron-right todo-arrow"></i>
      </router-link>

      <router-link to="/food" class="todo-card">
        <div class="todo-icon food">
          <i class="bi bi-cup-hot"></i>
        </div>
        <div class="todo-info">
          <div class="todo-title">美食审核</div>
          <div class="todo-desc">维护城市美食内容和展示状态</div>
        </div>
        <i class="bi bi-chevron-right todo-arrow"></i>
      </router-link>

      <router-link to="/feedback" class="todo-card">
        <div class="todo-icon feedback">
          <i class="bi bi-chat-dots"></i>
        </div>
        <div class="todo-info">
          <div class="todo-title">反馈处理</div>
          <div class="todo-desc">查看用户反馈并进行回复处理</div>
        </div>
        <i class="bi bi-chevron-right todo-arrow"></i>
      </router-link>
    </div>

    <!-- 快捷入口 -->
    <div class="section-card">
      <div class="section-header">
        <div>
          <h5>快捷入口</h5>
          <p>常用后台功能快速进入</p>
        </div>
      </div>

      <div class="quick-grid">
        <router-link to="/user" class="quick-item">
          <i class="bi bi-people"></i>
          <span>用户管理</span>
        </router-link>

        <router-link to="/scenic" class="quick-item">
          <i class="bi bi-image"></i>
          <span>景区管理</span>
        </router-link>

        <router-link to="/attraction" class="quick-item">
          <i class="bi bi-geo-alt"></i>
          <span>景点管理</span>
        </router-link>

        <router-link to="/ticket" class="quick-item">
          <i class="bi bi-ticket"></i>
          <span>门票管理</span>
        </router-link>

        <router-link to="/banner" class="quick-item">
          <i class="bi bi-images"></i>
          <span>轮播图管理</span>
        </router-link>

        <router-link to="/statistics" class="quick-item">
          <i class="bi bi-bar-chart"></i>
          <span>数据统计</span>
        </router-link>
      </div>
    </div>

    <div class="work-grid">
      <!-- 最近订单 -->
      <div class="section-card">
        <div class="section-header">
          <div>
            <h5>最近订单</h5>
            <p>展示最新产生的门票订单</p>
          </div>
          <router-link to="/order" class="more-link">全部订单</router-link>
        </div>

        <div class="simple-list" v-if="recentOrders.length > 0">
          <div class="list-item" v-for="item in recentOrders" :key="item.id">
            <div class="list-main">
              <div class="list-title">
                {{ item.orderNo || item.orderNum || "订单编号未生成" }}
              </div>
              <div class="list-sub">
                {{ item.scenicName || "景区信息" }} · ¥{{
                  formatMoney(item.totalAmount || item.amount)
                }}
              </div>
            </div>
            <span
              class="order-status"
              :class="getOrderStatusClass(item.status)"
            >
              {{ getOrderStatusText(item.status) }}
            </span>
          </div>
        </div>

        <div class="empty-box" v-else>暂无订单数据</div>
      </div>

      <!-- 内容审核提醒 -->
      <div class="section-card">
        <div class="section-header">
          <div>
            <h5>内容管理提醒</h5>
            <p>攻略、反馈和美食内容处理入口</p>
          </div>
        </div>

        <div class="notice-list">
          <router-link to="/strategy" class="notice-item">
            <div class="notice-icon strategy">
              <i class="bi bi-journal-text"></i>
            </div>
            <div class="notice-info">
              <div class="notice-title">攻略内容管理</div>
              <div class="notice-desc">
                查看攻略发布内容，处理上下架与审核状态
              </div>
            </div>
          </router-link>

          <router-link to="/food" class="notice-item">
            <div class="notice-icon food">
              <i class="bi bi-cup-hot"></i>
            </div>
            <div class="notice-info">
              <div class="notice-title">美食内容管理</div>
              <div class="notice-desc">维护美食信息、图片、城市和展示状态</div>
            </div>
          </router-link>

          <router-link to="/feedback" class="notice-item">
            <div class="notice-icon feedback">
              <i class="bi bi-chat-dots"></i>
            </div>
            <div class="notice-info">
              <div class="notice-title">用户反馈处理</div>
              <div class="notice-desc">查看用户意见，补充管理员回复内容</div>
            </div>
          </router-link>
        </div>
      </div>
    </div>

    <!-- 系统说明 -->
    <div class="section-card">
      <div class="section-header">
        <div>
          <h5>后台管理说明</h5>
          <p>本后台主要用于旅游资源维护、内容审核、订单处理和数据统计分析。</p>
        </div>
      </div>

      <div class="desc-grid">
        <div class="desc-item">
          <strong>资源维护</strong>
          <span>维护景区、景点、门票、轮播图和美食内容。</span>
        </div>
        <div class="desc-item">
          <strong>内容审核</strong>
          <span>处理攻略内容、美食内容和用户反馈。</span>
        </div>
        <div class="desc-item">
          <strong>订单管理</strong>
          <span>查看订单状态，完成订单状态维护。</span>
        </div>
        <div class="desc-item">
          <strong>数据统计</strong>
          <span>在数据统计页面查看图表、趋势和排行。</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import api from "../api";

const recentOrders = ref([]);

const adminName = computed(() => {
  const adminText = localStorage.getItem("admin");
  const admin = adminText ? JSON.parse(adminText) : {};
  return admin.realName || admin.username || "管理员";
});

const todayText = computed(() => {
  return new Date().toLocaleDateString("zh-CN", {
    year: "numeric",
    month: "long",
    day: "numeric",
    weekday: "long",
  });
});

const formatMoney = (value) => {
  return Number(value || 0).toFixed(2);
};

const getOrderStatusText = (status) => {
  const map = {
    0: "待支付",
    1: "已支付",
    2: "已使用",
    3: "已取消",
  };
  return map[status] || "未知";
};

const getOrderStatusClass = (status) => {
  const map = {
    0: "pending",
    1: "paid",
    2: "used",
    3: "cancelled",
  };
  return map[status] || "unknown";
};

const loadRecentOrders = async () => {
  const res = await api.getOrderList({
    pageNum: 1,
    pageSize: 6,
  });

  if (res.code === 200) {
    recentOrders.value = res.data.records || [];
  }
};

onMounted(() => {
  loadRecentOrders();
});
</script>

<style scoped>
.dashboard-workbench {
  color: #1f2937;
}

.welcome-card {
  background: linear-gradient(135deg, #5a9bcf, #3678a8);
  color: #ffffff;
  padding: 26px 28px;
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.welcome-card h3 {
  margin: 0 0 8px;
  font-size: 24px;
  font-weight: 700;
}

.welcome-card p {
  margin: 0;
  font-size: 14px;
  opacity: 0.92;
}

.today-box {
  background: rgba(255, 255, 255, 0.16);
  padding: 10px 14px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  white-space: nowrap;
}

.todo-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}

.todo-card {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  padding: 18px;
  text-decoration: none;
  color: #1f2937;
  display: flex;
  align-items: center;
  transition: all 0.2s;
}

.todo-card:hover {
  border-color: #5a9bcf;
  background: #f5fbff;
  transform: translateY(-2px);
}

.todo-icon {
  width: 44px;
  height: 44px;
  color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  flex-shrink: 0;
}

.todo-icon i {
  font-size: 22px;
}

.todo-icon.order {
  background: #5a9bcf;
}

.todo-icon.strategy {
  background: #22c55e;
}

.todo-icon.food {
  background: #f59e0b;
}

.todo-icon.feedback {
  background: #ef4444;
}

.todo-info {
  flex: 1;
  min-width: 0;
}

.todo-title {
  font-size: 15px;
  font-weight: 700;
  margin-bottom: 4px;
}

.todo-desc {
  font-size: 12px;
  color: #64748b;
  line-height: 1.5;
}

.todo-arrow {
  color: #94a3b8;
  margin-left: 8px;
}

.section-card {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  padding: 22px;
  margin-bottom: 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-header h5 {
  margin: 0 0 4px;
  font-size: 17px;
  font-weight: 700;
}

.section-header p {
  margin: 0;
  font-size: 13px;
  color: #94a3b8;
}

.more-link {
  color: #5a9bcf;
  text-decoration: none;
  font-size: 13px;
}

.quick-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 12px;
}

.quick-item {
  border: 1px solid #e5e7eb;
  background: #f8fafc;
  padding: 18px 10px;
  color: #1f2937;
  text-decoration: none;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  transition: all 0.2s;
}

.quick-item i {
  color: #5a9bcf;
  font-size: 24px;
}

.quick-item span {
  font-size: 13px;
}

.quick-item:hover {
  border-color: #5a9bcf;
  background: #eef7ff;
}

.work-grid {
  display: grid;
  grid-template-columns: 1.2fr 1fr;
  gap: 20px;
}

.simple-list {
  display: flex;
  flex-direction: column;
}

.list-item {
  padding: 12px 0;
  border-bottom: 1px solid #edf2f7;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.list-item:last-child {
  border-bottom: none;
}

.list-main {
  min-width: 0;
}

.list-title {
  font-size: 14px;
  font-weight: 700;
  margin-bottom: 4px;
}

.list-sub {
  color: #64748b;
  font-size: 12px;
}

.order-status {
  min-width: 62px;
  text-align: center;
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
}

.order-status.pending {
  background: #fff7ed;
  color: #c2410c;
}

.order-status.paid {
  background: #dcfce7;
  color: #15803d;
}

.order-status.used {
  background: #e0f2fe;
  color: #0369a1;
}

.order-status.cancelled {
  background: #f1f5f9;
  color: #64748b;
}

.notice-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.notice-item {
  display: flex;
  align-items: center;
  padding: 14px;
  background: #f8fafc;
  border: 1px solid #edf2f7;
  color: #1f2937;
  text-decoration: none;
  transition: all 0.2s;
}

.notice-item:hover {
  border-color: #5a9bcf;
  background: #f5fbff;
}

.notice-icon {
  width: 40px;
  height: 40px;
  color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  flex-shrink: 0;
}

.notice-icon.strategy {
  background: #22c55e;
}

.notice-icon.food {
  background: #f59e0b;
}

.notice-icon.feedback {
  background: #ef4444;
}

.notice-title {
  font-size: 14px;
  font-weight: 700;
  margin-bottom: 4px;
}

.notice-desc {
  font-size: 12px;
  color: #64748b;
  line-height: 1.5;
}

.desc-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
}

.desc-item {
  background: #f8fafc;
  border: 1px solid #edf2f7;
  padding: 16px;
}

.desc-item strong {
  display: block;
  font-size: 14px;
  color: #1f2937;
  margin-bottom: 8px;
}

.desc-item span {
  font-size: 12px;
  color: #64748b;
  line-height: 1.6;
}

.empty-box {
  background: #f8fafc;
  border: 1px dashed #cbd5e1;
  color: #94a3b8;
  padding: 30px;
  text-align: center;
  font-size: 14px;
}

@media (max-width: 1200px) {
  .todo-grid,
  .desc-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .quick-grid {
    grid-template-columns: repeat(3, 1fr);
  }

  .work-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .welcome-card {
    flex-direction: column;
    align-items: flex-start;
    gap: 14px;
  }

  .todo-grid,
  .quick-grid,
  .desc-grid {
    grid-template-columns: 1fr;
  }
}
</style>
