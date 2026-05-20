<template>
  <div class="admin-layout">
    <aside class="sidebar">
      <div class="sidebar-brand"><i class="bi bi-globe2"></i> 旅游平台管理</div>
      <ul class="sidebar-menu">
        <li>
          <router-link
            to="/dashboard"
            :class="{ active: $route.path === '/dashboard' }"
          >
            <i class="bi bi-speedometer2"></i>
            <span>控制台</span>
          </router-link>
        </li>

        <li>
          <router-link to="/user" :class="{ active: $route.path === '/user' }">
            <i class="bi bi-people"></i>
            <span>用户管理</span>
          </router-link>
        </li>

        <li>
          <router-link
            to="/scenic"
            :class="{ active: $route.path === '/scenic' }"
          >
            <i class="bi bi-image"></i>
            <span>景区管理</span>
          </router-link>
        </li>

        <li>
          <router-link
            to="/attraction"
            :class="{ active: $route.path === '/attraction' }"
          >
            <i class="bi bi-geo-alt"></i>
            <span>景点管理</span>
          </router-link>
        </li>

        <li>
          <router-link
            to="/admin"
            :class="{ active: $route.path === '/admin' }"
          >
            <i class="bi bi-person-gear"></i>
            <span>管理员管理</span>
          </router-link>
        </li>

        <li>
          <router-link
            to="/order"
            :class="{ active: $route.path === '/order' }"
          >
            <i class="bi bi-cart"></i>
            <span>订单管理</span>
          </router-link>
        </li>

        <li>
          <router-link
            to="/ticket"
            :class="{ active: $route.path === '/ticket' }"
          >
            <i class="bi bi-ticket"></i>
            <span>门票管理</span>
          </router-link>
        </li>

        <li>
          <router-link
            to="/strategy"
            :class="{ active: $route.path === '/strategy' }"
          >
            <i class="bi bi-journal-text"></i>
            <span>攻略管理</span>
          </router-link>
        </li>

        <li>
          <router-link
            to="/feedback"
            :class="{ active: $route.path === '/feedback' }"
          >
            <i class="bi bi-chat-dots"></i>
            <span>反馈管理</span>
          </router-link>
        </li>

        <li>
          <router-link
            to="/banner"
            :class="{ active: $route.path === '/banner' }"
          >
            <i class="bi bi-images"></i>
            <span>轮播图管理</span>
          </router-link>
        </li>

        <li>
          <router-link to="/food" :class="{ active: $route.path === '/food' }">
            <i class="bi bi-cup-hot"></i>
            <span>美食管理</span>
          </router-link>
        </li>

        <li>
          <router-link to="/tag" :class="{ active: $route.path === '/tag' }">
            <i class="bi bi-tags"></i>
            <span>标签管理</span>
          </router-link>
        </li>

        <li>
          <router-link
            to="/statistics"
            :class="{ active: $route.path === '/statistics' }"
          >
            <i class="bi bi-bar-chart"></i>
            <span>数据统计</span>
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
