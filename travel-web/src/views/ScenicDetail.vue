<template>
  <div class="detail-header">
    <div class="container py-4">
      <nav aria-label="breadcrumb">
        <ol class="breadcrumb breadcrumb-custom">
          <li class="breadcrumb-item">
            <router-link to="/">首页</router-link>
          </li>
          <li class="breadcrumb-item">
            <router-link to="/scenic">景区</router-link>
          </li>
          <li class="breadcrumb-item active">{{ scenic.name }}</li>
        </ol>
      </nav>

      <div class="row g-4">
        <div class="col-lg-7">
          <div class="detail-gallery">
            <img :src="scenic.coverImage || defaultImage" :alt="scenic.name" />
          </div>
        </div>

        <div class="col-lg-5">
          <div class="detail-info">
            <span class="level-badge" v-if="scenic.level"
              >{{ scenic.level }}景区</span
            >
            <h1 class="detail-title">{{ scenic.name }}</h1>

            <div class="rating">
              <div class="stars">
                <i class="bi bi-star-fill"></i>
                <i class="bi bi-star-fill"></i>
                <i class="bi bi-star-fill"></i>
                <i class="bi bi-star-fill"></i>
                <i class="bi bi-star-half"></i>
              </div>
              <span
                >{{ formatRating(scenic.rating) }}分 |
                {{ scenic.ratingCount || 0 }}条评分</span
              >
            </div>

            <p class="detail-location">
              <i class="bi bi-geo-alt"></i>
              {{ scenic.province }} {{ scenic.city }} {{ scenic.address }}
            </p>

            <p class="detail-time">
              <i class="bi bi-clock"></i>
              开放时间：{{ scenic.openTime || "08:00-18:00" }}
            </p>

            <div class="price-card">
              <div>
                <span class="price-label">门票价格</span>
                <div class="price-value">
                  ¥<span class="price-number">{{ minPrice }}</span>
                  <span class="price-unit">起</span>
                </div>
              </div>
              <button class="book-btn" @click="scrollToTickets">
                <i class="bi bi-ticket"></i> 立即预订
              </button>
            </div>

            <div class="action-buttons">
              <button
                class="action-btn collect"
                :class="{ collected: collected }"
                @click="handleCollect"
              >
                <i
                  class="bi"
                  :class="collected ? 'bi-heart-fill' : 'bi-heart'"
                ></i>
                {{ collected ? "已收藏" : "收藏" }}
              </button>
              <button class="action-btn share" @click="handleShare">
                <i class="bi bi-share"></i> 分享
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Content -->
    <div class="container py-5">
      <div class="row g-4">
        <div class="col-lg-8">
          <div class="content-card">
            <h4 class="section-title">景区介绍</h4>
            <p class="section-content">
              {{ scenic.description || "暂无介绍信息" }}
            </p>
          </div>

          <div class="content-card">
            <h4 class="section-title">景区景点</h4>

            <div v-if="attractions.length > 0" class="attraction-grid">
              <div
                class="attraction-card"
                v-for="item in attractions"
                :key="item.id"
                @click="router.push(`/attraction/${item.id}`)"
              >
                <div class="attraction-cover">
                  <img
                    :src="item.coverImage || defaultImage"
                    :alt="item.name"
                  />
                </div>

                <div class="attraction-info">
                  <h5>{{ item.name }}</h5>
                  <p>{{ item.description || "暂无介绍" }}</p>
                  <div class="attraction-meta">
                    <span
                      ><i class="bi bi-eye"></i> {{ item.viewCount || 0 }}</span
                    >
                    <span
                      ><i class="bi bi-heart"></i>
                      {{ item.collectCount || 0 }}</span
                    >
                  </div>
                </div>
              </div>
            </div>

            <div v-else class="empty-attractions">暂无景点信息</div>
          </div>

          <div
            class="content-card"
            v-if="
              relatedNotes.length > 0 ||
              relatedStrategies.length > 0 ||
              relatedFoods.length > 0
            "
          >
            <h4 class="section-title">相关推荐</h4>

            <div class="related-block" v-if="relatedNotes.length > 0">
              <div class="related-header">
                <h5><i class="bi bi-journal-text"></i> 相关游记</h5>
                <router-link to="/note">更多游记</router-link>
              </div>

              <div class="related-grid">
                <div
                  class="related-card"
                  v-for="item in relatedNotes"
                  :key="item.id"
                  @click="router.push(`/note/${item.id}`)"
                >
                  <img :src="getNoteCover(item)" :alt="item.title" />
                  <div class="related-info">
                    <h6>{{ item.title }}</h6>
                    <p>{{ item.province }} {{ item.city }}</p>
                    <span
                      ><i class="bi bi-eye"></i> {{ item.viewCount || 0 }}</span
                    >
                  </div>
                </div>
              </div>
            </div>

            <div class="related-block" v-if="relatedStrategies.length > 0">
              <div class="related-header">
                <h5><i class="bi bi-map"></i> 相关攻略</h5>
                <router-link to="/strategy">更多攻略</router-link>
              </div>

              <div class="related-grid">
                <div
                  class="related-card"
                  v-for="item in relatedStrategies"
                  :key="item.id"
                  @click="router.push(`/strategy/${item.id}`)"
                >
                  <img
                    :src="item.coverImage || defaultImage"
                    :alt="item.title"
                  />
                  <div class="related-info">
                    <h6>{{ item.title }}</h6>
                    <p>{{ item.tags || "旅游攻略" }}</p>
                    <span
                      ><i class="bi bi-eye"></i> {{ item.viewCount || 0 }}</span
                    >
                  </div>
                </div>
              </div>
            </div>

            <div class="related-block" v-if="relatedFoods.length > 0">
              <div class="related-header">
                <h5><i class="bi bi-shop"></i> 周边美食</h5>
                <router-link to="/food">更多美食</router-link>
              </div>

              <div class="related-grid">
                <div
                  class="related-card"
                  v-for="item in relatedFoods"
                  :key="item.id"
                  @click="router.push(`/food/${item.id}`)"
                >
                  <img
                    :src="item.coverImage || defaultImage"
                    :alt="item.name"
                  />
                  <div class="related-info">
                    <h6>{{ item.name }}</h6>
                    <p>{{ item.city }} {{ item.address || "" }}</p>
                    <span
                      ><i class="bi bi-geo-alt"></i> {{ item.province }}</span
                    >
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="content-card">
            <h4 class="section-title">景区位置</h4>
            <div id="detailMap" class="map-container"></div>
            <div class="map-footer">
              <small
                >经度: {{ scenic.longitude }} | 纬度:
                {{ scenic.latitude }}</small
              >
              <a
                :href="`https://uri.amap.com/marker?position=${scenic.longitude},${scenic.latitude}&name=${scenic.name}`"
                target="_blank"
                class="nav-link"
              >
                <i class="bi bi-signpost-2"></i> 导航到这里
              </a>
            </div>
          </div>

          <!-- 评论区 -->
          <div class="content-card">
            <h4 class="section-title">用户评论 ({{ comments.length }})</h4>

            <!-- 发表评论 -->
            <div class="comment-form">
              <textarea
                class="comment-textarea"
                v-model="commentContent"
                rows="3"
                placeholder="写下你的评论..."
                :disabled="!isLoggedIn"
              ></textarea>
              <div class="rating-input" v-if="isLoggedIn">
                <button
                  v-for="star in 5"
                  :key="star"
                  type="button"
                  :class="{ active: commentRating >= star }"
                  @click="commentRating = star"
                >
                  <i class="bi bi-star-fill"></i>
                </button>
              </div>
              <div class="comment-footer">
                <small v-if="!isLoggedIn">请先登录后再评论</small>
                <small v-else>{{ commentContent.length }}/500</small>
                <button
                  class="submit-btn"
                  @click="submitComment"
                  :disabled="!commentContent.trim() || !isLoggedIn"
                >
                  发表评论
                </button>
              </div>
            </div>

            <!-- 评论列表 -->
            <div class="comment-list">
              <div v-if="comments.length === 0" class="empty-comments">
                <i class="bi bi-chat"></i>
                <p>暂无评论</p>
              </div>

              <div
                v-for="comment in comments"
                :key="comment.id"
                class="comment-item"
              >
                <div class="comment-avatar">
                  <img :src="comment.userAvatar || defaultAvatar" alt="" />
                </div>
                <div class="comment-body">
                  <div class="comment-header">
                    <span class="comment-username">{{
                      comment.username || "匿名用户"
                    }}</span>
                    <span class="comment-time">{{
                      formatTime(comment.createTime)
                    }}</span>
                  </div>
                  <p class="comment-text">{{ comment.content }}</p>
                  <div class="comment-actions">
                    <button
                      class="delete-btn"
                      v-if="canDelete(comment)"
                      @click="deleteComment(comment.id)"
                    >
                      <i class="bi bi-trash"></i> 删除
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="col-lg-4">
          <div class="ticket-card" id="ticketSection" ref="ticketSectionRef">
            <h5 class="ticket-title">门票类型</h5>
            <div v-for="ticket in tickets" :key="ticket.id" class="ticket-item">
              <div class="ticket-info">
                <h6>{{ ticket.name }}</h6>
                <p>{{ ticket.description || "即买即用" }}</p>
              </div>
              <div class="ticket-price">
                <div class="price">¥{{ ticket.price }}</div>
                <button class="buy-btn" @click="buyTicket(ticket)">购买</button>
              </div>
            </div>
            <div v-if="tickets.length === 0" class="empty-tickets">
              暂无门票信息
            </div>
          </div>

          <div class="tips-card">
            <h5 class="tips-title">游玩须知</h5>
            <ul class="tips-list">
              <li><i class="bi bi-check-circle"></i>支持随时退票</li>
              <li><i class="bi bi-check-circle"></i>无需预约，扫码入园</li>
              <li><i class="bi bi-check-circle"></i>有效期内可用</li>
              <li><i class="bi bi-info-circle"></i>建议游玩时间：4-6小时</li>
            </ul>
          </div>
        </div>
      </div>
    </div>

    <!-- Buy Modal -->
    <div class="modal fade" id="buyModal" tabindex="-1" ref="buyModalRef">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">购买门票</h5>
            <button
              type="button"
              class="btn-close"
              data-bs-dismiss="modal"
            ></button>
          </div>
          <div class="modal-body">
            <div class="form-group">
              <label class="form-label">门票类型</label>
              <input
                type="text"
                class="form-control"
                :value="selectedTicket?.name"
                disabled
              />
            </div>
            <div class="form-group">
              <label class="form-label">游玩日期</label>
              <input
                type="date"
                class="form-control"
                v-model="visitDate"
                :min="minDate"
              />
            </div>
            <div class="form-group">
              <label class="form-label">购买数量</label>
              <div class="quantity-control">
                <button class="qty-btn" @click="quantity > 1 && quantity--">
                  -
                </button>
                <input
                  type="text"
                  class="qty-input"
                  v-model="quantity"
                  readonly
                />
                <button class="qty-btn" @click="quantity++">+</button>
              </div>
            </div>
            <div class="total-price">
              <span>合计金额</span>
              <span class="total-amount"
                >¥{{ (selectedTicket?.price || 0) * quantity }}</span
              >
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn-cancel" data-bs-dismiss="modal">
              取消
            </button>
            <button type="button" class="btn-submit" @click="submitOrder">
              提交订单
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, onUnmounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { Modal } from "bootstrap";
import AMapLoader from "@amap/amap-jsapi-loader";
import api from "../api";

const route = useRoute();
const router = useRouter();
const scenic = ref({});

const tickets = ref([]);
const attractions = ref([]);
const collected = ref(false);
const relatedNotes = ref([]);
const relatedStrategies = ref([]);
const relatedFoods = ref([]);

const selectedTicket = ref(null);
const visitDate = ref("");
const quantity = ref(1);
const buyModalRef = ref(null);
const ticketSectionRef = ref(null);
const comments = ref([]);
const commentContent = ref("");
const commentRating = ref(5);
let buyModal = null;
let map = null;

const isLoggedIn = computed(() => !!localStorage.getItem("userToken"));
const currentUserId = computed(() => {
  const user = JSON.parse(localStorage.getItem("user") || "{}");
  return user.id;
});

const defaultImage =
  "https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=800";
const defaultAvatar = "https://via.placeholder.com/40";
const minPrice = computed(() => {
  if (tickets.value.length === 0) return 99;
  return Math.min(...tickets.value.map((t) => t.price));
});
const minDate = computed(() => {
  const d = new Date();
  return d.toISOString().split("T")[0];
});

const loadData = async () => {
  const id = route.params.id;

  const [scenicRes, ticketRes, attractionRes] = await Promise.all([
    api.getScenicDetail(id),
    api.getTicketsByScenic(id),
    api.getAttractionList({
      scenicId: id,
      pageNum: 1,
      pageSize: 20,
    }),
  ]);

  if (scenicRes.code === 200) {
    scenic.value = scenicRes.data;
    await loadRelatedResources();
  }

  if (ticketRes.code === 200) {
    tickets.value = ticketRes.data;
  }

  if (attractionRes.code === 200) {
    attractions.value = attractionRes.data.records || [];
  }
};

const getNoteCover = (note) => {
  if (!note.images) {
    return defaultImage;
  }

  try {
    const images = JSON.parse(note.images);
    return images && images.length > 0 ? images[0] : defaultImage;
  } catch (e) {
    return defaultImage;
  }
};

const loadRelatedResources = async () => {
  const id = route.params.id;
  const province = scenic.value.province || "";
  const city = scenic.value.city || "";
  const keyword = scenic.value.name || "";
  const foodKeyword = city || province;

  try {
    const [noteRes, strategyRes, foodRes] = await Promise.all([
      api.getRelatedNotes({
        scenicId: id,
        province,
        city,
        limit: 4,
      }),

      api.getRelatedStrategies({
        keyword,
        city,
        limit: 4,
      }),

      api.getFoodList({
        keyword: foodKeyword,
        status: 1,
        pageNum: 1,
        pageSize: 4,
      }),
    ]);

    if (noteRes.code === 200) {
      relatedNotes.value = noteRes.data || [];
    }

    if (strategyRes.code === 200) {
      relatedStrategies.value = strategyRes.data || [];
    }

    if (foodRes.code === 200) {
      relatedFoods.value = foodRes.data.records || [];
    }
  } catch (e) {
    console.error("加载相关内容失败", e);
  }
};

const handleCollect = async () => {
  if (!localStorage.getItem("userToken")) {
    router.push("/login");
    return;
  }
  try {
    const res = await api.toggleCollect(route.params.id, 1);
    if (res.code === 200) {
      collected.value = !collected.value;
      alert(res.data);
    }
  } catch (e) {
    alert("操作失败");
  }
};

const checkCollect = async () => {
  if (!localStorage.getItem("userToken")) return;
  try {
    const res = await api.checkCollect(route.params.id, 1);
    if (res.code === 200) {
      collected.value = res.data;
    }
  } catch (e) {}
};

const handleShare = () => {
  const url = window.location.href;
  const title = scenic.value.name || "景区详情";
  if (navigator.share) {
    navigator.share({ title, url }).catch(() => {});
  } else {
    navigator.clipboard.writeText(url);
    alert("链接已复制到剪贴板");
  }
};

const scrollToTickets = () => {
  if (ticketSectionRef.value) {
    ticketSectionRef.value.scrollIntoView({
      behavior: "smooth",
      block: "start",
    });
  }
};

const buyTicket = (ticket) => {
  if (!localStorage.getItem("userToken")) {
    router.push("/login");
    return;
  }
  selectedTicket.value = ticket;
  visitDate.value = minDate.value;
  quantity.value = 1;
  if (!buyModal) {
    buyModal = new Modal(buyModalRef.value);
  }
  buyModal.show();
};

const submitOrder = async () => {
  if (!visitDate.value) {
    alert("请选择游玩日期");
    return;
  }
  try {
    const res = await api.createOrder({
      scenicId: scenic.value.id,
      ticketId: selectedTicket.value.id,
      quantity: quantity.value,
      visitDate: visitDate.value,
    });
    if (res.code === 200) {
      buyModal.hide();
      alert("订单创建成功！");
      router.push("/order");
    } else {
      alert(res.message);
    }
  } catch (e) {
    alert("创建订单失败");
  }
};

const initMap = async () => {
  if (!scenic.value.longitude || !scenic.value.latitude) return;

  window._AMapSecurityConfig = {
    securityJsCode: "734d51563a3b2c4a2433ae3481309408",
  };

  try {
    const AMap = await AMapLoader.load({
      key: "d201b9704d5bff29ddd4510c45c75cf4",
      version: "2.0",
      plugins: ["AMap.ToolBar", "AMap.Scale"],
    });

    map = new AMap.Map("detailMap", {
      zoom: 15,
      center: [scenic.value.longitude, scenic.value.latitude],
      mapStyle: "amap://styles/fresh",
    });

    const marker = new AMap.Marker({
      position: [scenic.value.longitude, scenic.value.latitude],
      title: scenic.value.name,
    });
    map.add(marker);

    map.addControl(new AMap.Scale());
  } catch (e) {
    console.error("地图加载失败", e);
  }
};

watch(
  () => scenic.value.id,
  (newId) => {
    if (newId && scenic.value.longitude) {
      setTimeout(initMap, 100);
    }
  },
);

const recordView = async () => {
  if (localStorage.getItem("userToken")) {
    try {
      await api.recordBehavior(route.params.id, 1, 1);
    } catch (e) {}
  }
};

const loadComments = async () => {
  try {
    const res = await api.getComments("scenic", route.params.id);
    if (res.code === 200) {
      comments.value = res.data || [];
    }
  } catch (e) {
    console.error("加载评论失败", e);
  }
};

const submitComment = async () => {
  if (!commentContent.value.trim()) return;
  if (!isLoggedIn.value) {
    alert("请先登录");
    return;
  }

  try {
    const res = await api.addComment({
      targetType: "scenic",
      targetId: route.params.id,
      content: commentContent.value.trim(),
      rating: commentRating.value,
    });
    if (res.code === 200) {
      commentContent.value = "";
      commentRating.value = 5;
      await loadData();
      loadComments();
      alert("评论成功");
    } else {
      alert(res.message || "评论失败");
    }
  } catch (e) {
    alert("评论失败");
  }
};

const formatRating = (rating) => {
  const value = Number(rating || 0);
  return value > 0 ? value.toFixed(1) : "暂无";
};

const deleteComment = async (id) => {
  if (!confirm("确定要删除这条评论吗？")) return;

  try {
    const res = await api.deleteComment(id);
    if (res.code === 200) {
      loadComments();
    } else {
      alert(res.message || "删除失败");
    }
  } catch (e) {
    alert("删除失败");
  }
};

const canDelete = (comment) => {
  return currentUserId.value && comment.userId === currentUserId.value;
};

const formatTime = (dateStr) => {
  if (!dateStr) return "";
  const date = new Date(dateStr);
  const now = new Date();
  const diff = now - date;

  if (diff < 60000) return "刚刚";
  if (diff < 3600000) return Math.floor(diff / 60000) + "分钟前";
  if (diff < 86400000) return Math.floor(diff / 3600000) + "小时前";
  if (diff < 2592000000) return Math.floor(diff / 86400000) + "天前";
  return date.toLocaleDateString("zh-CN");
};

onMounted(() => {
  loadData();
  recordView();
  checkCollect();
  loadComments();
});

onUnmounted(() => {
  if (map) {
    map.destroy();
  }
});
</script>

<style scoped>
/* 白色背景头部 */
.detail-header {
  background: #fff;
  padding: 20px 0 0;
}

/* 面包屑样式 */
.breadcrumb-custom {
  background: #f9fafb;
  padding: 6px 16px;
  display: inline-flex;
  font-size: 13px;
  margin-bottom: 20px;
  border: 1px solid #e5e7eb;
}

.breadcrumb-custom .breadcrumb-item a {
  color: #5a9bcf;
  text-decoration: none;
}

.breadcrumb-custom .breadcrumb-item a:hover {
  color: #4a8bbf;
}

.breadcrumb-custom .breadcrumb-item.active {
  color: #7f8c8d;
}

.breadcrumb-custom .breadcrumb-item + .breadcrumb-item::before {
  color: #95a5a6;
  content: "/";
}

/* 图片画廊 */
.detail-gallery {
  background: #fff;
  border: 1px solid #e5e7eb;
  overflow: hidden;
}

.detail-gallery img {
  width: 100%;
  height: 400px;
  object-fit: cover;
}

/* 详情信息 */
.detail-info {
  background: #fff;
  border: 1px solid #e5e7eb;
  padding: 24px;
  height: 100%;
}

.level-badge {
  display: inline-block;
  background: #fbbf24;
  color: #2c3e50;
  padding: 4px 12px;
  font-size: 12px;
  font-weight: 500;
  margin-bottom: 12px;
}

.detail-title {
  font-size: 28px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 12px 0;
}

.rating {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
}

.stars {
  color: #fbbf24;
}

.rating span {
  font-size: 13px;
  color: #7f8c8d;
}

.detail-location,
.detail-time {
  font-size: 14px;
  color: #7f8c8d;
  margin: 0 0 12px 0;
  display: flex;
  align-items: center;
  gap: 6px;
}

/* 价格卡片 */
.price-card {
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  padding: 16px;
  margin: 20px 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price-label {
  font-size: 13px;
  color: #7f8c8d;
}

.price-value {
  font-size: 28px;
  font-weight: 600;
  color: #e74c3c;
}

.price-number {
  font-size: 32px;
}

.price-unit {
  font-size: 14px;
  font-weight: normal;
}

.book-btn {
  background: #5a9bcf;
  color: #fff;
  border: none;
  padding: 10px 24px;
  font-size: 16px;
  cursor: pointer;
  transition: background 0.2s;
}

.book-btn:hover {
  background: #4a8bbf;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: 12px;
}

.action-btn {
  flex: 1;
  padding: 8px;
  border: 1px solid #e5e7eb;
  background: #fff;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

.action-btn.collect:hover {
  border-color: #e74c3c;
  color: #e74c3c;
}

.action-btn.collect.collected {
  background: #e74c3c;
  border-color: #e74c3c;
  color: #fff;
}

.action-btn.share:hover {
  border-color: #5a9bcf;
  color: #5a9bcf;
}

/* 内容卡片 */
.content-card {
  background: #fff;
  border: 1px solid #e5e7eb;
  padding: 24px;
  margin-bottom: 24px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 16px 0;
  padding-bottom: 8px;
  border-bottom: 2px solid #5a9bcf;
  display: inline-block;
}

.section-content {
  color: #7f8c8d;
  line-height: 1.8;
  margin: 0;
}

/* 地图 */
.map-container {
  height: 300px;
  background: #f9fafb;
  border: 1px solid #e5e7eb;
}

.map-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #e5e7eb;
}

.map-footer small {
  font-size: 12px;
  color: #95a5a6;
}

.nav-link {
  color: #5a9bcf;
  text-decoration: none;
  font-size: 13px;
}

.nav-link:hover {
  color: #4a8bbf;
}

/* 门票卡片 */
.ticket-card {
  background: #fff;
  border: 1px solid #e5e7eb;
  padding: 20px;
  margin-bottom: 24px;
}

.ticket-title {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 16px 0;
  padding-bottom: 8px;
  border-bottom: 2px solid #5a9bcf;
  display: inline-block;
}

.ticket-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #e5e7eb;
}

.ticket-item:last-child {
  border-bottom: none;
}

.ticket-info h6 {
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 4px 0;
}

.ticket-info p {
  font-size: 12px;
  color: #95a5a6;
  margin: 0;
}

.ticket-price {
  text-align: right;
}

.ticket-price .price {
  font-size: 18px;
  font-weight: 600;
  color: #e74c3c;
}

.buy-btn {
  background: #5a9bcf;
  color: #fff;
  border: none;
  padding: 4px 16px;
  font-size: 12px;
  cursor: pointer;
  margin-top: 4px;
}

.buy-btn:hover {
  background: #4a8bbf;
}

.empty-tickets {
  text-align: center;
  padding: 32px;
  color: #95a5a6;
}

/* 须知卡片 */
.tips-card {
  background: #fff;
  border: 1px solid #e5e7eb;
  padding: 20px;
}

.tips-title {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 16px 0;
  padding-bottom: 8px;
  border-bottom: 2px solid #5a9bcf;
  display: inline-block;
}

.tips-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.tips-list li {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #7f8c8d;
  margin-bottom: 12px;
}

.tips-list li:last-child {
  margin-bottom: 0;
}

.tips-list li i.bi-check-circle {
  color: #10b981;
}

.tips-list li i.bi-info-circle {
  color: #5a9bcf;
}

/* 评论区域 */
.comment-form {
  margin-bottom: 24px;
}

.comment-textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #e5e7eb;
  font-size: 14px;
  font-family: inherit;
  resize: vertical;
  outline: none;
}

.comment-textarea:focus {
  border-color: #5a9bcf;
}

.comment-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
}

.comment-footer small {
  font-size: 12px;
  color: #95a5a6;
}

.submit-btn {
  background: #5a9bcf;
  color: #fff;
  border: none;
  padding: 6px 20px;
  font-size: 13px;
  cursor: pointer;
}

.submit-btn:hover:not(:disabled) {
  background: #4a8bbf;
}

.submit-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.comment-list {
  margin-top: 16px;
}

.comment-item {
  display: flex;
  gap: 12px;
  padding: 16px 0;
  border-top: 1px solid #e5e7eb;
}

.comment-item:first-child {
  border-top: none;
}

.comment-avatar img {
  width: 40px;
  height: 40px;
  object-fit: cover;
}

.comment-body {
  flex: 1;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 6px;
  flex-wrap: wrap;
}

.comment-username {
  font-weight: 600;
  font-size: 13px;
  color: #2c3e50;
}

.comment-time {
  font-size: 11px;
  color: #95a5a6;
}

.comment-text {
  color: #7f8c8d;
  font-size: 13px;
  line-height: 1.5;
  margin: 0 0 8px 0;
}

.delete-btn {
  background: none;
  border: none;
  color: #95a5a6;
  font-size: 12px;
  cursor: pointer;
  padding: 0;
}

.delete-btn:hover {
  color: #e74c3c;
}

.empty-comments {
  text-align: center;
  padding: 48px 20px;
  color: #95a5a6;
}

.empty-comments i {
  font-size: 48px;
}

.empty-comments p {
  margin: 12px 0 0 0;
}

/* 弹窗样式 */
.modal-content {
  border: 1px solid #e5e7eb;
}

.modal-header {
  border-bottom: 1px solid #e5e7eb;
  padding: 16px 20px;
}

.modal-title {
  color: #2c3e50;
  font-size: 18px;
  font-weight: 600;
}

.modal-body {
  padding: 20px;
}

.form-group {
  margin-bottom: 16px;
}

.form-label {
  display: block;
  font-size: 13px;
  font-weight: 500;
  color: #2c3e50;
  margin-bottom: 6px;
}

.form-control {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #e5e7eb;
  font-size: 14px;
  outline: none;
}

.form-control:focus {
  border-color: #5a9bcf;
}

.form-control:disabled {
  background: #f9fafb;
  color: #95a5a6;
}

.quantity-control {
  display: flex;
  width: 120px;
  border: 1px solid #e5e7eb;
}

.qty-btn {
  width: 36px;
  height: 36px;
  background: #fff;
  border: none;
  cursor: pointer;
  font-size: 18px;
}

.qty-btn:hover {
  background: #f9fafb;
}

.qty-input {
  flex: 1;
  text-align: center;
  border: none;
  outline: none;
  font-size: 14px;
}

.total-price {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  margin-top: 16px;
  border-top: 1px solid #e5e7eb;
}

.total-amount {
  font-size: 24px;
  font-weight: 600;
  color: #e74c3c;
}

.modal-footer {
  border-top: 1px solid #e5e7eb;
  padding: 16px 20px;
  gap: 12px;
}

.btn-cancel {
  background: #fff;
  border: 1px solid #e5e7eb;
  padding: 8px 20px;
  cursor: pointer;
}

.btn-cancel:hover {
  border-color: #5a9bcf;
  color: #5a9bcf;
}

.btn-submit {
  background: #5a9bcf;
  color: #fff;
  border: none;
  padding: 8px 20px;
  cursor: pointer;
}

.btn-submit:hover {
  background: #4a8bbf;
}

/* 响应式 */
@media (max-width: 768px) {
  .detail-header {
    padding: 16px 0 0;
  }

  .detail-gallery img {
    height: 250px;
  }

  .detail-title {
    font-size: 24px;
  }

  .price-value {
    font-size: 24px;
  }

  .price-number {
    font-size: 28px;
  }

  .action-buttons {
    flex-direction: column;
  }

  .map-container {
    height: 250px;
  }
}

/* 景区下属景点 */
.attraction-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.attraction-card {
  display: flex;
  gap: 12px;
  background: #f8fafc;
  border: 1px solid #e5e7eb;
  padding: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.attraction-card:hover {
  border-color: #5a9bcf;
  background: #fafdff;
  box-shadow: 0 2px 8px rgba(90, 155, 207, 0.12);
}

.attraction-cover {
  width: 110px;
  height: 80px;
  flex-shrink: 0;
  overflow: hidden;
  background: #eef2f7;
}

.attraction-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.attraction-info {
  flex: 1;
  min-width: 0;
}

.attraction-info h5 {
  font-size: 15px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 6px;
}

.attraction-info p {
  font-size: 13px;
  color: #6b7280;
  line-height: 1.5;
  margin: 0 0 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.attraction-meta {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: #95a5a6;
}

.attraction-meta span {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.empty-attractions {
  color: #95a5a6;
  font-size: 14px;
  padding: 18px;
  text-align: center;
  background: #f8fafc;
  border: 1px dashed #d7dee8;
}

@media (max-width: 768px) {
  .attraction-grid {
    grid-template-columns: 1fr;
  }

  .attraction-card {
    flex-direction: column;
  }

  .attraction-cover {
    width: 100%;
    height: 150px;
  }
}
/* 相关推荐 */
.related-block {
  margin-top: 20px;
}

.related-block:first-of-type {
  margin-top: 0;
}

.related-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
}

.related-header h5 {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 6px;
}

.related-header a {
  font-size: 13px;
  color: #5a9bcf;
  text-decoration: none;
}

.related-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.related-card {
  display: flex;
  gap: 10px;
  background: #f8fafc;
  border: 1px solid #e5e7eb;
  padding: 10px;
  cursor: pointer;
  transition: all 0.2s;
}

.related-card:hover {
  border-color: #5a9bcf;
  background: #fafdff;
  box-shadow: 0 2px 8px rgba(90, 155, 207, 0.12);
}

.related-card img {
  width: 86px;
  height: 66px;
  object-fit: cover;
  flex-shrink: 0;
  background: #eef2f7;
}

.related-info {
  flex: 1;
  min-width: 0;
}

.related-info h6 {
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 5px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.related-info p {
  font-size: 12px;
  color: #7f8c8d;
  margin: 0 0 5px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.related-info span {
  font-size: 12px;
  color: #95a5a6;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

@media (max-width: 768px) {
  .related-grid {
    grid-template-columns: 1fr;
  }
}
.rating-input {
  display: flex;
  gap: 4px;
  margin: 10px 0;
}

.rating-input button {
  border: none;
  background: transparent;
  color: #d1d5db;
  padding: 0;
  font-size: 18px;
  cursor: pointer;
}

.rating-input button.active {
  color: #fbbf24;
}
</style>
