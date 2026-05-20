<template>
  <div class="home">
    <!-- Hero Section with Banner Background -->
    <section class="hero-banner-section" :style="heroBannerStyle">
      <div class="hero-overlay"></div>
      <div class="hero-content">
        <h1 class="hero-title">
          {{ currentBannerData?.title || "发现世界之美" }}
        </h1>
        <p class="hero-subtitle">探索无限精彩，开启你的完美旅程</p>
        <div class="search-box">
          <input
            type="text"
            v-model="searchKeyword"
            placeholder="搜索目的地、景区、攻略..."
          />
          <button @click="handleSearch">
            <i class="bi bi-search"></i> 搜索
          </button>
        </div>
        <div class="banner-indicators" v-if="banners.length > 1">
          <span
            v-for="(banner, index) in banners"
            :key="index"
            :class="{ active: currentBanner === index }"
            @click="currentBanner = index"
          ></span>
        </div>
        <a
          v-if="currentBannerData && currentBannerData.linkType !== 0"
          :href="getBannerLink(currentBannerData)"
          :target="currentBannerData.linkType === 3 ? '_blank' : '_self'"
          class="banner-link-btn"
        >
          <i class="bi bi-arrow-right-circle"></i> 立即查看
        </a>
      </div>
      <button
        class="banner-nav banner-prev"
        @click="prevBanner"
        v-if="banners.length > 1"
      >
        <i class="bi bi-chevron-left"></i>
      </button>
      <button
        class="banner-nav banner-next"
        @click="nextBanner"
        v-if="banners.length > 1"
      >
        <i class="bi bi-chevron-right"></i>
      </button>
    </section>

    <!-- 为你推荐 -->
    <section class="py-5" v-if="recommendScenics.length > 0">
      <div class="container">
        <div class="section-title">
          <h2>为你推荐</h2>
          <div class="line"></div>
        </div>

        <div class="row g-4">
          <div
            class="col-lg-3 col-md-4 col-sm-6"
            v-for="scenic in recommendScenics"
            :key="scenic.id"
          >
            <router-link
              :to="`/scenic/${scenic.id}`"
              class="scenic-card d-block"
            >
              <div class="card-img">
                <img
                  :src="scenic.coverImage || defaultImage"
                  :alt="scenic.name"
                />
                <span class="badge-level" v-if="scenic.level">{{
                  scenic.level
                }}</span>
                <span class="badge-recommend">猜你喜欢</span>
                <span class="badge-price">¥{{ scenic.price || 99 }}起</span>
              </div>
              <div class="card-body">
                <h5 class="card-title">{{ scenic.name }}</h5>
                <p class="card-location">
                  <i class="bi bi-geo-alt"></i>{{ scenic.province }} ·
                  {{ scenic.city }}
                </p>
                <div class="card-stats">
                  <span
                    ><i class="bi bi-star-fill"></i>
                    {{ formatRating(scenic.rating) }}分</span
                  >
                  <span
                    ><i class="bi bi-eye"></i> {{ scenic.viewCount || 0 }}</span
                  >
                </div>
              </div>
            </router-link>
          </div>
        </div>
      </div>
    </section>

    <!-- Hot Scenic -->
    <section class="py-5">
      <div class="container">
        <div class="section-title">
          <h2>热门景区</h2>
          <p>精选国内外热门旅游目的地</p>
          <div class="line"></div>
        </div>

        <div class="row g-4">
          <div
            class="col-lg-3 col-md-4 col-sm-6"
            v-for="scenic in hotScenics"
            :key="scenic.id"
          >
            <router-link
              :to="`/scenic/${scenic.id}`"
              class="scenic-card d-block"
            >
              <div class="card-img">
                <img
                  :src="scenic.coverImage || defaultImage"
                  :alt="scenic.name"
                />
                <span class="badge-level" v-if="scenic.level">{{
                  scenic.level
                }}</span>
                <span class="badge-price">¥{{ scenic.minPrice || 99 }}起</span>
              </div>
              <div class="card-body">
                <h5 class="card-title">{{ scenic.name }}</h5>
                <p class="card-location">
                  <i class="bi bi-geo-alt"></i>{{ scenic.province }} ·
                  {{ scenic.city }}
                </p>
                <div class="card-stats">
                  <span
                    ><i class="bi bi-star-fill"></i>
                    {{ formatRating(scenic.rating) }}分</span
                  >
                  <span
                    ><i class="bi bi-heart"></i>
                    {{ scenic.collectCount || 0 }}</span
                  >
                </div>
              </div>
            </router-link>
          </div>
        </div>

        <div class="text-center mt-5">
          <router-link to="/scenic" class="btn btn-primary">
            查看更多景区 <i class="bi bi-arrow-right"></i>
          </router-link>
        </div>
      </div>
    </section>

    <!-- Hot Strategy -->
    <section class="py-5 bg-light">
      <div class="container">
        <div class="section-title">
          <h2>精选攻略</h2>
          <p>来自旅行达人的真实分享</p>
          <div class="line"></div>
        </div>

        <div class="row g-4">
          <div
            class="col-lg-3 col-md-4 col-sm-6"
            v-for="strategy in hotStrategies"
            :key="strategy.id"
          >
            <router-link
              :to="`/strategy/${strategy.id}`"
              class="strategy-card d-block"
            >
              <div class="card-img">
                <img
                  :src="strategy.coverImage || defaultImage"
                  :alt="strategy.title"
                />
              </div>
              <div class="card-body">
                <h5 class="card-title">{{ strategy.title }}</h5>
                <div class="card-author">
                  <img
                    :src="
                      'https://api.dicebear.com/7.x/avataaars/svg?seed=' +
                      strategy.userId
                    "
                    alt=""
                  />
                  <span>{{ strategy.username || "旅行达人" }}</span>
                </div>
              </div>
              <div class="card-footer">
                <span
                  ><i class="bi bi-eye"></i> {{ strategy.viewCount || 0 }}</span
                >
                <span
                  ><i class="bi bi-heart"></i>
                  {{ strategy.likeCount || 0 }}</span
                >
              </div>
            </router-link>
          </div>
        </div>

        <div class="text-center mt-5">
          <router-link to="/strategy" class="btn btn-primary">
            查看更多攻略 <i class="bi bi-arrow-right"></i>
          </router-link>
        </div>
      </div>
    </section>

    <!-- CTA Section -->
    <section class="py-5 cta-section">
      <div class="container text-center py-4">
        <h2 class="mb-3">开启你的精彩旅程</h2>
        <p class="mb-4">注册成为会员，享受更多专属优惠</p>
        <router-link to="/register" class="btn btn-outline-light btn-lg px-5">
          立即注册 <i class="bi bi-arrow-right"></i>
        </router-link>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from "vue";
import { useRouter } from "vue-router";
import api from "../api";

const formatRating = (rating) => {
  const value = Number(rating || 0);
  return value > 0 ? value.toFixed(1) : "暂无";
};

const router = useRouter();
const searchKeyword = ref("");
const hotScenics = ref([]);
const hotStrategies = ref([]);
const recommendScenics = ref([]);
const banners = ref([]);
const currentBanner = ref(0);
let bannerTimer = null;
const recommendCacheMinutes = 10;
const defaultImage =
  "https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=400";
const defaultBannerBg =
  "https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=1600";

const currentBannerData = computed(() => {
  return banners.value[currentBanner.value] || null;
});

const heroBannerStyle = computed(() => {
  const bgUrl = currentBannerData.value?.imageUrl || defaultBannerBg;
  return {
    backgroundImage: `url(${bgUrl})`,
  };
});

const getBannerLink = (banner) => {
  if (banner.linkType === 1 && banner.targetId) {
    return `/scenic/${banner.targetId}`;
  } else if (banner.linkType === 2 && banner.targetId) {
    return `/strategy/${banner.targetId}`;
  } else if (banner.linkType === 3 && banner.linkUrl) {
    return banner.linkUrl;
  }
  return "javascript:void(0)";
};

const prevBanner = () => {
  currentBanner.value =
    currentBanner.value === 0
      ? banners.value.length - 1
      : currentBanner.value - 1;
};

const nextBanner = () => {
  currentBanner.value = (currentBanner.value + 1) % banners.value.length;
};

const startBannerTimer = () => {
  if (bannerTimer) {
    clearInterval(bannerTimer);
    bannerTimer = null;
  }
  if (banners.value.length > 1) {
    bannerTimer = setInterval(() => {
      nextBanner();
    }, 5000);
  }
};

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push(
      `/search?keyword=${encodeURIComponent(searchKeyword.value.trim())}`,
    );
  }
};

const getRecommendCacheKey = () => {
  const user = JSON.parse(localStorage.getItem("user") || "{}");
  return `home_recommend_scenics_${user.id || "guest"}`;
};

const getCachedRecommendScenics = () => {
  try {
    const cacheKey = getRecommendCacheKey();
    const raw = sessionStorage.getItem(cacheKey);
    if (!raw) return null;
    const cache = JSON.parse(raw);
    const maxAge = recommendCacheMinutes * 60 * 1000;
    if (!cache.time || Date.now() - cache.time > maxAge) {
      sessionStorage.removeItem(cacheKey);
      return null;
    }
    return Array.isArray(cache.data) ? cache.data : null;
  } catch (e) {
    return null;
  }
};

const setCachedRecommendScenics = (data) => {
  try {
    sessionStorage.setItem(
      getRecommendCacheKey(),
      JSON.stringify({ time: Date.now(), data }),
    );
  } catch (e) {}
};

const loadData = async () => {
  try {
    if (bannerTimer) {
      clearInterval(bannerTimer);
      bannerTimer = null;
    }
    const cachedRecommend = getCachedRecommendScenics();
    if (cachedRecommend) {
      recommendScenics.value = cachedRecommend;
    }

    const requests = [
      api.getScenicList({ pageNum: 1, pageSize: 8 }),
      api.getStrategyList({ pageNum: 1, pageSize: 4, status: 1 }),
      api.getActiveBanners(),
    ];
    if (!cachedRecommend) {
      requests.push(api.getRecommendScenics(4));
    }

    const [scenicRes, strategyRes, bannerRes, recommendRes] = await Promise.all(
      requests,
    );
    if (scenicRes.code === 200) {
      hotScenics.value = scenicRes.data.records;
    }
    if (strategyRes.code === 200) {
      hotStrategies.value = strategyRes.data.records;
    }
    if (bannerRes.code === 200 && bannerRes.data) {
      banners.value = bannerRes.data;
      startBannerTimer();
    }
    if (recommendRes?.code === 200 && recommendRes.data) {
      recommendScenics.value = recommendRes.data;
      setCachedRecommendScenics(recommendRes.data);
    }
  } catch (e) {
    console.error("加载数据失败", e);
  }
};

onMounted(() => loadData());

onUnmounted(() => {
  if (bannerTimer) {
    clearInterval(bannerTimer);
  }
});
</script>

<style scoped>
/* 与行程规划页保持一致的浅蓝色系 */
:root {
  --primary-blue: #5a9bcf;
  --primary-dark: #4a8bbf;
  --text-dark: #2c3e50;
  --text-gray: #7f8c8d;
  --text-light: #95a5a6;
  --border-color: #ecf0f1;
  --bg-light: #f8fafc;
}

/* Hero Section */
.hero-banner-section {
  position: relative;
  min-height: 500px;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-image 0.8s ease-in-out;
}

.hero-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.45);
}

.hero-banner-section .hero-content {
  position: relative;
  z-index: 2;
  text-align: center;
  padding: 40px 20px;
}

.hero-title {
  font-size: 3rem;
  font-weight: 700;
  color: #fff;
  text-shadow: 2px 2px 10px rgba(0, 0, 0, 0.3);
  margin-bottom: 15px;
  animation: fadeInUp 0.6s ease;
}

.hero-subtitle {
  font-size: 1.25rem;
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: 30px;
  text-shadow: 1px 1px 5px rgba(0, 0, 0, 0.3);
}

.hero-banner-section .search-box {
  display: flex;
  max-width: 600px;
  margin: 0 auto 25px;
  background: rgba(255, 255, 255, 0.98);
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.hero-banner-section .search-box input {
  flex: 1;
  border: none;
  padding: 16px 28px;
  font-size: 15px;
  outline: none;
  background: transparent;
  color: var(--text-dark);
}

.hero-banner-section .search-box input::placeholder {
  color: var(--text-light);
}

.hero-banner-section .search-box button {
  padding: 16px 32px;
  background: var(--primary-blue);
  color: #fff;
  border: none;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.hero-banner-section .search-box button:hover {
  background: var(--primary-dark);
}

.banner-indicators {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-bottom: 20px;
}

.banner-indicators span {
  width: 10px;
  height: 10px;
  background: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  transition: all 0.3s;
}

.banner-indicators span:hover {
  background: rgba(255, 255, 255, 0.8);
}

.banner-indicators span.active {
  background: #fff;
  width: 30px;
}

.banner-link-btn {
  display: inline-block;
  padding: 10px 28px;
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
  border: 2px solid rgba(255, 255, 255, 0.6);
  text-decoration: none;
  font-weight: 500;
  transition: all 0.3s;
  backdrop-filter: blur(5px);
}

.banner-link-btn:hover {
  background: #fff;
  color: var(--primary-blue);
  border-color: #fff;
}

.banner-nav {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 48px;
  height: 48px;
  border: none;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(5px);
  cursor: pointer;
  font-size: 20px;
  color: #fff;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10;
}

.banner-nav:hover {
  background: rgba(255, 255, 255, 0.4);
}

.banner-prev {
  left: 30px;
}

.banner-next {
  right: 30px;
}

/* 区块标题 - 与行程规划页一致 */
.section-title {
  text-align: center;
  margin-bottom: 48px;
}

.section-title h2 {
  font-size: 28px;
  font-weight: 600;
  color: var(--text-dark);
  margin-bottom: 12px;
}

.section-title p {
  color: var(--text-gray);
  font-size: 15px;
  margin-bottom: 16px;
}

.section-title .line {
  width: 60px;
  height: 3px;
  background: var(--primary-blue);
  margin: 0 auto;
}

/* 景区卡片 - 减少圆角 */
.scenic-card {
  background: #fff;
  border: 1px solid var(--border-color);
  transition: all 0.3s;
  text-decoration: none;
  color: inherit;
  height: 100%;
  display: flex;
  flex-direction: column;
  position: relative;
}

.scenic-card:hover {
  transform: translateY(-4px);
  border-color: var(--primary-blue);
  box-shadow: 0 8px 20px rgba(90, 155, 207, 0.1);
}

.card-img {
  position: relative;
  height: 200px;
  overflow: hidden;
  background: #f5f5f5;
}

.card-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.scenic-card:hover .card-img img {
  transform: scale(1.05);
}

/* 等级标签 - 左上角 */
.badge-level {
  position: absolute;
  top: 12px;
  left: 12px;
  background: rgba(0, 0, 0, 0.7);
  color: #ffc107;
  padding: 4px 10px;
  font-size: 11px;
  font-weight: 500;
  z-index: 2;
}

/* 猜你喜欢标签 - 右上角 */
.badge-recommend {
  position: absolute;
  top: 12px;
  right: 12px;
  background: var(--primary-blue);
  color: #fff;
  padding: 4px 10px;
  font-size: 11px;
  font-weight: 500;
  z-index: 2;
}

/* 价格标签 - 右下角 */
.badge-price {
  position: absolute;
  bottom: 12px;
  right: 12px;
  background: rgba(0, 0, 0, 0.7);
  color: #fff;
  padding: 4px 10px;
  font-size: 12px;
  font-weight: 500;
  z-index: 2;
}

.card-body {
  padding: 16px;
  flex: 1;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-dark);
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-location {
  font-size: 12px;
  color: var(--text-light);
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.card-stats {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: var(--text-gray);
}

.card-stats i {
  margin-right: 4px;
}

.card-stats i.bi-star-fill {
  color: #fbbf24;
}

/* 攻略卡片 */
.strategy-card {
  background: #fff;
  border: 1px solid var(--border-color);
  transition: all 0.3s;
  text-decoration: none;
  color: inherit;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.strategy-card:hover {
  transform: translateY(-4px);
  border-color: var(--primary-blue);
  box-shadow: 0 8px 20px rgba(90, 155, 207, 0.1);
}

.strategy-card .card-img {
  height: 180px;
}

.strategy-card .card-body {
  padding: 16px;
  flex: 1;
}

.strategy-card .card-title {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 12px;
  display: -webkit-box;
  line-clamp: 2;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  white-space: normal;
  line-height: 1.4;
  min-height: 42px;
}

.card-author {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 8px;
}

.card-author img {
  width: 28px;
  height: 28px;
  border-radius: 50%;
}

.card-author span {
  font-size: 12px;
  color: var(--text-gray);
}

.card-footer {
  padding: 12px 16px;
  border-top: 1px solid var(--border-color);
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: var(--text-light);
}

.card-footer i {
  margin-right: 4px;
}

/* 按钮样式 - 与行程规划页一致 */
.btn-primary {
  background: var(--primary-blue);
  color: #fff;
  border: none;
  padding: 10px 28px;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
}

.btn-primary:hover {
  background: var(--primary-dark);
  transform: translateY(-2px);
}

.btn-outline-light {
  border: 2px solid #fff;
  color: #fff;
  background: transparent;
  padding: 12px 32px;
  font-size: 16px;
  font-weight: 500;
  transition: all 0.3s;
}

.btn-outline-light:hover {
  background: #fff;
  color: var(--primary-blue);
  border-color: #fff;
  transform: translateY(-2px);
}

/* 背景色 */
.bg-light {
  background: var(--bg-light) !important;
}

/* CTA Section - 与行程规划页头部颜色一致 */
.cta-section {
  background: linear-gradient(135deg, #5a9bcf 0%, #4a8bbf 100%);
  color: #fff;
}

/* 动画 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式 */
@media (max-width: 768px) {
  .hero-banner-section {
    min-height: 400px;
  }

  .hero-title {
    font-size: 2rem;
  }

  .hero-subtitle {
    font-size: 1rem;
  }

  .hero-banner-section .search-box input {
    padding: 12px 20px;
    font-size: 14px;
  }

  .hero-banner-section .search-box button {
    padding: 12px 24px;
    font-size: 14px;
  }

  .banner-nav {
    width: 40px;
    height: 40px;
    font-size: 16px;
  }

  .banner-prev {
    left: 15px;
  }

  .banner-next {
    right: 15px;
  }

  .section-title h2 {
    font-size: 24px;
  }

  .card-img {
    height: 180px;
  }

  .badge-level,
  .badge-recommend {
    top: 8px;
  }

  .badge-level {
    left: 8px;
  }

  .badge-recommend {
    right: 8px;
  }

  .badge-price {
    bottom: 8px;
    right: 8px;
  }
}
</style>
