<template>
  <div class="admin-layout">
    <aside class="sidebar">
      <div class="sidebar-brand">
        <i class="bi bi-globe2"></i> 旅游平台管理
      </div>
      <ul class="sidebar-menu">
        <li v-for="item in menuItems" :key="item.path">
          <router-link :to="item.path" :class="{ active: $route.path === item.path }">
            <i :class="item.icon"></i>
            <span>{{ item.label }}</span>
          </router-link>
        </li>
      </ul>
    </aside>

    <main class="main-content">
      <div class="header">
        <h1 class="page-title">{{ $route.meta.title }}</h1>
        <div class="header-right">
          <span class="me-3">欢迎，{{ adminName }}</span>
          <button class="btn btn-outline-secondary btn-sm" @click="logout">
            <i class="bi bi-box-arrow-right"></i> 退出
          </button>
        </div>
      </div>
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { computed } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();

const menuItems = [
  { path: "/dashboard", icon: "bi bi-speedometer2", label: "控制台" },
  { path: "/user", icon: "bi bi-people", label: "用户管理" },
  { path: "/scenic", icon: "bi bi-image", label: "景区管理" },
  { path: "/attraction", icon: "bi bi-geo-alt", label: "景点管理" },
  { path: "/admin", icon: "bi bi-person-gear", label: "管理员管理" },
  { path: "/order", icon: "bi bi-cart", label: "订单管理" },
  { path: "/ticket", icon: "bi bi-ticket", label: "门票管理" },
  { path: "/strategy", icon: "bi bi-journal-text", label: "攻略管理" },
  { path: "/feedback", icon: "bi bi-chat-dots", label: "反馈管理" },
  { path: "/banner", icon: "bi bi-images", label: "轮播图管理" },
  { path: "/food", icon: "bi bi-cup-hot", label: "美食管理" },
  { path: "/tag", icon: "bi bi-tags", label: "标签管理" },
  { path: "/statistics", icon: "bi bi-bar-chart", label: "数据统计" },
];

const adminName = computed(() => {
  const admin = JSON.parse(localStorage.getItem("admin") || "{}");
  return admin.realName || admin.username || "管理员";
});

const logout = () => {
  localStorage.removeItem("token");
  localStorage.removeItem("admin");
  router.push("/login");
};
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
}
</style>
