<template>
  <div>
    <div class="page-header scenic-header">
      <div class="container">
        <nav aria-label="breadcrumb" class="mb-2">
          <ol class="breadcrumb breadcrumb-custom">
            <li class="breadcrumb-item">
              <router-link to="/"
                ><i class="bi bi-house-door"></i> 首页</router-link
              >
            </li>
            <li class="breadcrumb-item active">
              <i class="bi bi-image"></i> 景区列表
            </li>
          </ol>
        </nav>
        <h1 class="h3 fw-bold mb-1">
          <i class="bi bi-image me-2"></i>热门景区
        </h1>
        <p class="text-white-50 mb-0 small">探索国内外热门旅游胜地</p>
      </div>
    </div>

    <div class="container py-5">
      <!-- Filter -->
      <div class="filter-bar">
        <div class="filter-row">
          <div class="filter-search">
            <input
              type="text"
              class="filter-input"
              v-model="keyword"
              placeholder="搜索景区名称"
              @keyup.enter="loadData"
            />
            <button class="filter-btn" @click="loadData">
              <i class="bi bi-search"></i>
            </button>
          </div>
          <select class="filter-select" v-model="province" @change="loadData">
            <option value="">全部省份</option>
            <option value="北京">北京</option>
            <option value="上海">上海</option>
            <option value="浙江">浙江</option>
            <option value="四川">四川</option>
            <option value="云南">云南</option>
            <option value="广东">广东</option>
          </select>
          <select class="filter-select" v-model="sortBy" @change="loadData">
            <option value="">默认排序</option>
            <option value="price">价格最低</option>
            <option value="rating">评分最高</option>
          </select>
          <select
            class="filter-select"
            v-model="selectedTag"
            @change="handleFilterChange"
          >
            <option value="">全部标签</option>
            <option v-for="tag in tags" :key="tag.id" :value="tag.name">
              {{ tag.name }}
            </option>
          </select>
        </div>
      </div>

      <!-- List -->
      <div class="row g-4">
        <div
          class="col-lg-3 col-md-4 col-sm-6"
          v-for="scenic in scenics"
          :key="scenic.id"
        >
          <div class="scenic-card">
            <router-link :to="`/scenic/${scenic.id}`" class="card-link">
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
            <div class="card-actions">
              <button
                class="collect-btn"
                :class="{ collected: scenic.collected }"
                @click.stop="quickCollect(scenic, $event)"
              >
                <i
                  class="bi"
                  :class="scenic.collected ? 'bi-heart-fill' : 'bi-heart'"
                ></i>
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Empty -->
      <div class="empty-state" v-if="scenics.length === 0">
        <i class="bi bi-inbox"></i>
        <p>暂无景区数据</p>
      </div>

      <!-- Pagination -->
      <div class="pagination-wrapper" v-if="total > pageSize">
        <button
          class="page-btn"
          :disabled="pageNum === 1"
          @click="changePage(pageNum - 1)"
        >
          上一页
        </button>
        <div class="page-numbers">
          <button
            v-for="p in visiblePages"
            :key="p"
            class="page-number"
            :class="{ active: p === pageNum }"
            @click="changePage(p)"
          >
            {{ p }}
          </button>
        </div>
        <button
          class="page-btn"
          :disabled="pageNum === totalPages"
          @click="changePage(pageNum + 1)"
        >
          下一页
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import api from "../api";

const route = useRoute();
const router = useRouter();
const scenics = ref([]);
const total = ref(0);
const pageNum = ref(1);
const pageSize = ref(12);
const keyword = ref("");
const province = ref("");
const selectedTag = ref("");
const sortBy = ref("");
const tags = ref([]);
const defaultImage =
  "https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=400";
const collectedIds = ref(new Set());

const totalPages = computed(() => Math.ceil(total.value / pageSize.value) || 1);

const visiblePages = computed(() => {
  const pages = [];
  const maxVisible = 5;
  let start = Math.max(1, pageNum.value - Math.floor(maxVisible / 2));
  let end = Math.min(totalPages.value, start + maxVisible - 1);

  if (end - start + 1 < maxVisible) {
    start = Math.max(1, end - maxVisible + 1);
  }

  for (let i = start; i <= end; i++) {
    pages.push(i);
  }
  return pages;
});

const loadCollectedIds = async () => {
  if (!localStorage.getItem("userToken")) return;
  try {
    const res = await api.getMyCollections();
    if (res.code === 200 && res.data) {
      collectedIds.value = new Set(
        res.data.filter((c) => c.type === 1).map((c) => c.targetId),
      );
    }
  } catch (e) {}
};

const loadData = async () => {
  const res = await api.getScenicList({
    pageNum: pageNum.value,
    pageSize: pageSize.value,
    name: keyword.value,
    province: province.value,
    tag: selectedTag.value || undefined,
  });
  if (res.code === 200) {
    scenics.value = res.data.records.map((s) => ({
      ...s,
      collected: collectedIds.value.has(s.id),
    }));
    total.value = res.data.total;
  }
};

const loadTags = async () => {
  const res = await api.getTagList(1);
  if (res.code === 200) {
    tags.value = res.data || [];
  }
};

const formatRating = (rating) => {
  const value = Number(rating || 0);
  return value > 0 ? value.toFixed(1) : "暂无";
};

const handleFilterChange = () => {
  pageNum.value = 1;
  loadData();
};

const changePage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    pageNum.value = page;
    loadData();
  }
};

const quickCollect = async (scenic, event) => {
  event.preventDefault();
  if (!localStorage.getItem("userToken")) {
    router.push("/login");
    return;
  }
  try {
    const res = await api.toggleCollect(scenic.id, 1);
    if (res.code === 200) {
      scenic.collected = !scenic.collected;
      if (scenic.collected) {
        collectedIds.value.add(scenic.id);
      } else {
        collectedIds.value.delete(scenic.id);
      }
    }
  } catch (e) {
    alert("操作失败");
  }
};

onMounted(async () => {
  if (route.query.keyword) {
    keyword.value = route.query.keyword;
  }
  await loadTags();
  await loadCollectedIds();
  loadData();
});
</script>

<style scoped>
/* 统一蓝色主题 */
.scenic-header {
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
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.breadcrumb-custom .breadcrumb-item a:hover {
  color: #fff;
}

.breadcrumb-custom .breadcrumb-item.active {
  color: #fff;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.breadcrumb-custom .breadcrumb-item + .breadcrumb-item::before {
  color: rgba(255, 255, 255, 0.7);
  content: "/";
}

.scenic-header h1 {
  color: #fff;
}

/* 筛选栏 - 直角设计 */
.filter-bar {
  background: #fff;
  border: 1px solid #e5e7eb;
  padding: 20px;
  margin-bottom: 32px;
}

.filter-row {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  align-items: center;
}

.filter-search {
  flex: 1;
  min-width: 200px;
  display: flex;
}

.filter-input {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #e5e7eb;
  font-size: 14px;
  outline: none;
  transition: border-color 0.2s;
}

.filter-input:focus {
  border-color: #5a9bcf;
}

.filter-btn {
  background: #5a9bcf;
  color: #fff;
  border: none;
  padding: 8px 16px;
  cursor: pointer;
  transition: background 0.2s;
}

.filter-btn:hover {
  background: #4a8bbf;
}

.filter-select {
  width: 150px;
  padding: 8px 12px;
  border: 1px solid #e5e7eb;
  font-size: 14px;
  outline: none;
  cursor: pointer;
  background: #fff;
}

.filter-select:focus {
  border-color: #5a9bcf;
}

/* 景区卡片 - 直角设计 */
.scenic-card {
  position: relative;
  background: #fff;
  border: 1px solid #e5e7eb;
  transition: all 0.2s;
  height: 100%;
}

.scenic-card:hover {
  transform: translateY(-4px);
  border-color: #5a9bcf;
  box-shadow: 0 8px 20px rgba(90, 155, 207, 0.1);
}

.card-link {
  text-decoration: none;
  color: inherit;
  display: block;
}

.card-img {
  position: relative;
  height: 180px;
  overflow: hidden;
  background: #f9fafb;
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

.badge-level {
  position: absolute;
  top: 12px;
  left: 12px;
  background: rgba(0, 0, 0, 0.7);
  color: #ffc107;
  padding: 4px 10px;
  font-size: 11px;
  font-weight: 500;
}

.badge-price {
  position: absolute;
  bottom: 12px;
  right: 12px;
  background: rgba(0, 0, 0, 0.7);
  color: #fff;
  padding: 4px 10px;
  font-size: 12px;
  font-weight: 500;
}

.card-body {
  padding: 16px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-location {
  font-size: 13px;
  color: #95a5a6;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.card-stats {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #95a5a6;
}

.card-stats i {
  margin-right: 4px;
}

.card-stats i.bi-star-fill {
  color: #fbbf24;
}

/* 收藏按钮 */
.card-actions {
  position: absolute;
  top: 12px;
  right: 12px;
  z-index: 2;
}

.collect-btn {
  width: 32px;
  height: 32px;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid #e5e7eb;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  color: #95a5a6;
}

.collect-btn:hover {
  background: #fff;
  border-color: #e74c3c;
  color: #e74c3c;
}

.collect-btn.collected {
  background: #e74c3c;
  border-color: #e74c3c;
  color: #fff;
}

.collect-btn.collected:hover {
  background: #c0392b;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: #fff;
  border: 1px solid #e5e7eb;
}

.empty-state i {
  font-size: 48px;
  color: #95a5a6;
}

.empty-state p {
  color: #7f8c8d;
  margin: 12px 0 0 0;
}

/* 分页 */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 12px;
  margin-top: 32px;
}

.page-btn {
  background: #fff;
  border: 1px solid #e5e7eb;
  padding: 8px 16px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
  color: #7f8c8d;
}

.page-btn:hover:not(:disabled) {
  border-color: #5a9bcf;
  color: #5a9bcf;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-numbers {
  display: flex;
  gap: 8px;
}

.page-number {
  min-width: 36px;
  height: 36px;
  background: #fff;
  border: 1px solid #e5e7eb;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
  color: #7f8c8d;
}

.page-number:hover {
  border-color: #5a9bcf;
  color: #5a9bcf;
}

.page-number.active {
  background: #5a9bcf;
  border-color: #5a9bcf;
  color: #fff;
}

/* 响应式 */
@media (max-width: 768px) {
  .scenic-header {
    padding: 30px 0;
  }

  .filter-row {
    flex-direction: column;
  }

  .filter-search,
  .filter-select {
    width: 100%;
  }

  .card-img {
    height: 160px;
  }

  .pagination-wrapper {
    flex-wrap: wrap;
  }

  .page-numbers {
    order: 3;
    width: 100%;
    justify-content: center;
  }
}
</style>
