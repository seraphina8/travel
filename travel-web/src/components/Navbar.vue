<template>
  <nav class="navbar navbar-expand-lg navbar-custom">
    <div class="container">
      <router-link class="navbar-brand" to="/">
        <i class="bi bi-globe-asia-australia"></i> 智能旅游规划
      </router-link>
      
      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
        <span class="navbar-toggler-icon"></span>
      </button>
      
      <div class="collapse navbar-collapse" id="navbarNav">
        <ul class="navbar-nav me-auto">
          <li class="nav-item">
            <router-link class="nav-link" :class="{ active: $route.path === '/' }" to="/">
              <i class="bi bi-house"></i> 首页
            </router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" :class="{ active: $route.path.startsWith('/scenic') }" to="/scenic">
              <i class="bi bi-image"></i> 景区
            </router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" :class="{ active: $route.path.startsWith('/strategy') }" to="/strategy">
              <i class="bi bi-journal-richtext"></i> 攻略
            </router-link>
          </li>
          <li class="nav-item" v-if="isLoggedIn">
            <router-link class="nav-link" :class="{ active: $route.path === '/chat' }" to="/chat">
              <i class="bi bi-chat-dots"></i> 聊天
            </router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" :class="{ active: $route.path === '/map' }" to="/map">
              <i class="bi bi-geo-alt"></i> 地图
            </router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" :class="{ active: $route.path === '/note' }" to="/note">
              <i class="bi bi-journal-text"></i> 游记
            </router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" :class="{ active: $route.path === '/attraction' }" to="/attraction">
              <i class="bi bi-pin-map"></i> 景点
            </router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" :class="{ active: $route.path === '/trip' }" to="/trip">
              <i class="bi bi-calendar-check"></i> 行程
            </router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" :class="{ active: $route.path === '/food' }" to="/food">
              <i class="bi bi-cup-hot"></i> 美食
            </router-link>
          </li>
        </ul>
        
        <div class="navbar-nav" v-if="!isLoggedIn">
          <router-link class="nav-link btn-login" to="/login">
            <i class="bi bi-person"></i> 登录
          </router-link>
        </div>
        
        <div class="navbar-nav dropdown" v-else ref="dropdownRef">
          <a
            class="nav-link dropdown-toggle d-flex align-items-center"
            href="#"
            role="button"
            :class="{ show: showDropdown }"
            :aria-expanded="showDropdown"
            @click.prevent="toggleDropdown"
          >
            <img :src="user.avatar || 'https://api.dicebear.com/7.x/avataaars/svg?seed=' + user.username" 
                 class="rounded-circle me-2" style="width: 32px; height: 32px;">
            {{ user.nickname || user.username }}
          </a>
          <ul class="dropdown-menu dropdown-menu-end" :class="{ show: showDropdown }">
            <li><router-link class="dropdown-item" to="/user" @click="closeDropdown"><i class="bi bi-person me-2"></i>用户中心</router-link></li>
            <li><router-link class="dropdown-item" to="/order" @click="closeDropdown"><i class="bi bi-bag me-2"></i>我的订单</router-link></li>
            <li><hr class="dropdown-divider"></li>
            <li><a class="dropdown-item text-danger" href="#" @click.prevent="logout"><i class="bi bi-box-arrow-right me-2"></i>退出登录</a></li>
          </ul>
        </div>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const router = useRouter()
const route = useRoute()
const dropdownRef = ref(null)
const showDropdown = ref(false)

const isLoggedIn = computed(() => !!localStorage.getItem('userToken'))
const user = computed(() => JSON.parse(localStorage.getItem('user') || '{}'))

const toggleDropdown = () => {
  showDropdown.value = !showDropdown.value
}

const closeDropdown = () => {
  showDropdown.value = false
}

const handleClickOutside = (e) => {
  if (dropdownRef.value && !dropdownRef.value.contains(e.target)) {
    closeDropdown()
  }
}

const logout = () => {
  localStorage.removeItem('userToken')
  localStorage.removeItem('user')
  closeDropdown()
  router.push('/')
  location.reload()
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside)
})

watch(() => route.fullPath, () => closeDropdown())
</script>
