<template>
  <div class="page-header">
    <div class="container">
      <h1>用户中心</h1>
    </div>
  </div>

  <div class="container py-5">
    <div class="row g-4">
      <!-- Sidebar -->
      <div class="col-lg-3">
        <div class="user-sidebar">
          <div class="user-avatar">
            <img
              :src="
                user.avatar ||
                'https://api.dicebear.com/7.x/avataaars/svg?seed=' +
                  user.username
              "
              alt=""
            />
            <h4>{{ user.nickname || user.username }}</h4>
            <p class="text-muted small mb-0">
              {{ user.email || "未绑定邮箱" }}
            </p>
          </div>

          <div class="follow-stats">
            <div class="stat-item" @click="activeTab = 'follow'">
              <h5 class="mb-0">{{ followCount.followCount || 0 }}</h5>
              <small>关注</small>
            </div>
            <div class="stat-item" @click="activeTab = 'fans'">
              <h5 class="mb-0">{{ followCount.fansCount || 0 }}</h5>
              <small>粉丝</small>
            </div>
          </div>

          <ul class="user-menu">
            <li>
              <a
                href="#"
                :class="{ active: activeTab === 'profile' }"
                @click.prevent="activeTab = 'profile'"
              >
                <i class="bi bi-person"></i> 个人资料
              </a>
            </li>
            <li>
              <a
                href="#"
                :class="{ active: activeTab === 'password' }"
                @click.prevent="activeTab = 'password'"
              >
                <i class="bi bi-shield-lock"></i> 修改密码
              </a>
            </li>
            <li>
              <router-link to="/order">
                <i class="bi bi-bag"></i> 我的订单
              </router-link>
            </li>
            <li>
              <a
                href="#"
                :class="{ active: activeTab === 'publish' }"
                @click.prevent="activeTab = 'publish'"
              >
                <i class="bi bi-journal-text"></i> 我的发布
              </a>
            </li>
            <li>
              <a
                href="#"
                :class="{ active: activeTab === 'collect' }"
                @click.prevent="activeTab = 'collect'"
              >
                <i class="bi bi-heart"></i> 我的收藏
              </a>
            </li>
            <li>
              <a
                href="#"
                :class="{ active: activeTab === 'follow' }"
                @click.prevent="activeTab = 'follow'"
              >
                <i class="bi bi-people"></i> 我的关注
              </a>
            </li>
            <li>
              <a
                href="#"
                :class="{ active: activeTab === 'fans' }"
                @click.prevent="activeTab = 'fans'"
              >
                <i class="bi bi-person-hearts"></i> 我的粉丝
              </a>
            </li>
            <li>
              <a
                href="#"
                :class="{ active: activeTab === 'discover' }"
                @click.prevent="activeTab = 'discover'"
              >
                <i class="bi bi-search-heart"></i> 发现用户
              </a>
            </li>
            <li>
              <a
                href="#"
                :class="{ active: activeTab === 'footprint' }"
                @click.prevent="activeTab = 'footprint'"
              >
                <i class="bi bi-geo-alt"></i> 我的足迹
              </a>
            </li>
          </ul>
        </div>
      </div>

      <!-- Content -->
      <div class="col-lg-9">
        <!-- Profile -->
        <div class="content-card" v-if="activeTab === 'profile'">
          <h5 class="card-title">个人资料</h5>
          <form @submit.prevent="updateProfile">
            <div class="form-row">
              <div class="form-group">
                <label class="form-label">用户名</label>
                <input
                  type="text"
                  class="form-control"
                  v-model="user.username"
                  disabled
                />
              </div>
              <div class="form-group">
                <label class="form-label">昵称</label>
                <input
                  type="text"
                  class="form-control"
                  v-model="profileForm.nickname"
                />
              </div>
              <div class="form-group">
                <label class="form-label">手机号</label>
                <input
                  type="text"
                  class="form-control"
                  v-model="profileForm.phone"
                />
              </div>
              <div class="form-group">
                <label class="form-label">邮箱</label>
                <input
                  type="email"
                  class="form-control"
                  v-model="profileForm.email"
                />
              </div>
              <div class="form-group full">
                <label class="form-label">头像</label>
                <div class="avatar-upload">
                  <div class="avatar-preview">
                    <img
                      :src="
                        profileForm.avatar ||
                        'https://api.dicebear.com/7.x/avataaars/svg?seed=' +
                          user.username
                      "
                    />
                  </div>
                  <div>
                    <input
                      type="file"
                      ref="avatarInput"
                      accept="image/*"
                      @change="handleAvatarUpload"
                      style="display: none"
                    />
                    <button
                      type="button"
                      class="upload-btn"
                      @click="$refs.avatarInput.click()"
                    >
                      <i class="bi bi-upload"></i> 上传头像
                    </button>
                    <p class="upload-tip">
                      支持 JPG、PNG 格式，建议尺寸 200x200
                    </p>
                  </div>
                </div>
              </div>
              <div class="form-actions">
                <button type="submit" class="submit-btn">保存修改</button>
              </div>
            </div>
          </form>
        </div>

        <!-- Password -->
        <div class="content-card" v-if="activeTab === 'password'">
          <h5 class="card-title">修改密码</h5>
          <form @submit.prevent="changePassword">
            <div class="form-row">
              <div class="form-group full">
                <label class="form-label">原密码</label>
                <input
                  type="password"
                  class="form-control"
                  v-model="passwordForm.oldPassword"
                  placeholder="请输入原密码"
                />
              </div>
              <div class="form-group">
                <label class="form-label">新密码</label>
                <input
                  type="password"
                  class="form-control"
                  v-model="passwordForm.newPassword"
                  placeholder="6到20位，包含字母和数字"
                />
              </div>
              <div class="form-group">
                <label class="form-label">确认新密码</label>
                <input
                  type="password"
                  class="form-control"
                  v-model="passwordForm.confirmPassword"
                  placeholder="请再次输入新密码"
                />
              </div>
              <div class="form-group full">
                <label class="form-label">图形验证码</label>
                <div class="captcha-row">
                  <input
                    type="text"
                    class="form-control captcha-code"
                    v-model="passwordForm.captchaCode"
                    maxlength="4"
                    placeholder="请输入验证码"
                  />
                  <button
                    type="button"
                    class="captcha-image"
                    @click="loadPasswordCaptcha"
                    title="点击刷新"
                  >
                    <img
                      v-if="passwordCaptcha.image"
                      :src="passwordCaptcha.image"
                      alt="验证码"
                    />
                    <span v-else>刷新</span>
                  </button>
                </div>
              </div>
              <div class="form-actions">
                <button
                  type="submit"
                  class="submit-btn"
                  :disabled="passwordLoading"
                >
                  {{ passwordLoading ? "提交中..." : "修改密码" }}
                </button>
              </div>
            </div>
          </form>
        </div>

        <div class="content-card" v-if="activeTab === 'publish'">
          <h5 class="card-title">我的发布</h5>
          <div class="collection-list" v-if="publishedItems.length > 0">
            <div
              class="collection-item"
              v-for="item in publishedItems"
              :key="`${item.kind}-${item.id}`"
              @click="router.push(item.path)"
            >
              <img :src="item.coverImage || defaultImage" :alt="item.title" />
              <div class="collection-info">
                <h6>{{ item.title }}</h6>
                <div class="collection-meta">
                  <span class="type-badge">{{ item.typeName }}</span>
                  <span class="type-badge">{{ getPublishStatusLabel(item.status) }}</span>
                  <span class="type-badge" v-if="false">{{
                    item.status === 1 ? "已发布" : "待审核"
                  }}</span>
                  <small>{{ formatDate(item.createTime) }}</small>
                </div>
              </div>
            </div>
          </div>
          <div class="empty-state" v-else>
            <i class="bi bi-journal-text"></i>
            <p>暂无攻略</p>
          </div>
        </div>

        <!-- Collections -->
        <div class="content-card" v-if="activeTab === 'collect'">
          <h5 class="card-title">我的收藏</h5>
          <div class="collection-list" v-if="collections.length > 0">
            <div
              class="collection-item"
              v-for="item in collections"
              :key="item.id"
              @click="goToDetail(item)"
            >
              <img
                :src="item.targetImage || defaultImage"
                :alt="item.targetName"
              />
              <div class="collection-info">
                <h6>{{ item.targetName || "未知名称" }}</h6>
                <div class="collection-meta">
                  <span class="type-badge">{{ getTypeName(item.type) }}</span>
                  <small>{{ formatDate(item.createTime) }}</small>
                </div>
              </div>
              <button class="delete-btn" @click.stop="cancelCollect(item)">
                <i class="bi bi-trash"></i>
              </button>
            </div>
          </div>
          <div class="empty-state" v-else>
            <i class="bi bi-heart"></i>
            <p>暂无收藏</p>
          </div>
        </div>

        <!-- Footprint -->
        <div class="content-card" v-if="activeTab === 'footprint'">
          <h5 class="card-title">我的足迹</h5>
          <div class="footprint-grid" v-if="footprints.length > 0">
            <div
              class="footprint-item"
              v-for="item in footprints"
              :key="item.id"
              @click="goToDetail(item.routePath)"
            >
              <div class="footprint-cover">
                <img :src="item.cover || defaultImage" :alt="item.title" />
                <span class="footprint-type">{{ item.typeName }}</span>
              </div>
              <div class="footprint-info">
                <h6>{{ item.title }}</h6>
                <small>{{ formatDate(item.createTime) }}</small>
              </div>
            </div>
          </div>
          <div class="empty-state" v-else>
            <i class="bi bi-geo-alt"></i>
            <p>暂无足迹记录</p>
            <small>浏览景区、攻略、美食等内容后会自动记录足迹</small>
          </div>
        </div>

        <!-- Follow List -->
        <div class="content-card" v-if="activeTab === 'follow'">
          <h5 class="card-title">我的关注</h5>
          <div class="user-list" v-if="followList.length > 0">
            <div class="user-item" v-for="item in followList" :key="item.id">
              <img
                :src="item.followUserAvatar || getAvatar(item.followUserId)"
                @click="viewUserProfile(item.followUserId)"
              />
              <div class="user-info">
                <h6 @click="viewUserProfile(item.followUserId)">
                  {{ item.followUserName || "用户" + item.followUserId }}
                </h6>
                <small>关注于 {{ formatDate(item.createTime) }}</small>
              </div>
              <div class="user-actions">
                <button
                  class="action-btn chat"
                  @click="
                    startPrivateChat(
                      item.followUserId,
                      item.followUserName,
                      item.followUserAvatar,
                    )
                  "
                >
                  <i class="bi bi-chat-dots"></i> 私聊
                </button>
                <button
                  class="action-btn unfollow"
                  @click="unfollow(item.followUserId)"
                >
                  取消关注
                </button>
              </div>
            </div>
          </div>
          <div class="empty-state" v-else>
            <i class="bi bi-people"></i>
            <p>暂无关注</p>
          </div>
        </div>

        <!-- Fans List -->
        <div class="content-card" v-if="activeTab === 'fans'">
          <h5 class="card-title">我的粉丝</h5>
          <div class="user-list" v-if="fansList.length > 0">
            <div class="user-item" v-for="item in fansList" :key="item.id">
              <img
                :src="item.followUserAvatar || getAvatar(item.userId)"
                @click="viewUserProfile(item.userId)"
              />
              <div class="user-info">
                <h6 @click="viewUserProfile(item.userId)">
                  {{ item.followUserName || "用户" + item.userId }}
                </h6>
                <small>关注于 {{ formatDate(item.createTime) }}</small>
              </div>
              <div class="user-actions">
                <button
                  class="action-btn chat"
                  @click="
                    startPrivateChat(
                      item.userId,
                      item.followUserName,
                      item.followUserAvatar,
                    )
                  "
                >
                  <i class="bi bi-chat-dots"></i> 私聊
                </button>
                <button
                  class="action-btn follow-back"
                  :class="{ followed: item.isFollowedBack }"
                  @click="followBack(item)"
                >
                  {{ item.isFollowedBack ? "已关注" : "回关" }}
                </button>
              </div>
            </div>
          </div>
          <div class="empty-state" v-else>
            <i class="bi bi-person-hearts"></i>
            <p>暂无粉丝</p>
          </div>
        </div>

        <!-- Discover Users -->
        <div class="content-card" v-if="activeTab === 'discover'">
          <h5 class="card-title">发现用户</h5>
          <div class="search-area">
            <input
              type="text"
              class="search-input"
              v-model="searchKeyword"
              placeholder="搜索用户名或昵称..."
              @keyup.enter="searchUsers"
            />
            <button class="search-btn" @click="searchUsers">
              <i class="bi bi-search"></i> 搜索
            </button>
          </div>
          <div class="user-list" v-if="discoverUsers.length > 0">
            <div class="user-item" v-for="item in discoverUsers" :key="item.id">
              <img
                :src="item.avatar || getAvatar(item.id)"
                @click="viewUserProfile(item.id)"
              />
              <div class="user-info">
                <h6 @click="viewUserProfile(item.id)">
                  {{ item.nickname || item.username }}
                </h6>
                <small>{{ item.email || "未公开邮箱" }}</small>
              </div>
              <div class="user-actions" v-if="item.id !== user.id">
                <button
                  class="action-btn chat"
                  @click="
                    startPrivateChat(
                      item.id,
                      item.nickname || item.username,
                      item.avatar,
                    )
                  "
                >
                  <i class="bi bi-chat-dots"></i> 私聊
                </button>
                <button
                  class="action-btn follow"
                  :class="{ followed: item.isFollowed }"
                  @click="followUser(item)"
                >
                  <i
                    class="bi"
                    :class="
                      item.isFollowed ? 'bi-person-check' : 'bi-person-plus'
                    "
                  ></i>
                  {{ item.isFollowed ? "已关注" : "关注" }}
                </button>
              </div>
              <span class="self-badge" v-else>我自己</span>
            </div>
          </div>
          <div class="empty-state" v-else>
            <i class="bi bi-search"></i>
            <p>搜索用户名或昵称来发现新朋友</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import api from "../api";

const router = useRouter();
const route = useRoute();
const user = ref({});
const activeTab = ref("profile");
const profileForm = ref({
  nickname: "",
  phone: "",
  email: "",
  avatar: "",
});
const passwordLoading = ref(false);
const passwordCaptcha = ref({ captchaId: "", image: "" });
const passwordForm = ref({
  oldPassword: "",
  newPassword: "",
  confirmPassword: "",
  captchaCode: "",
});
const followCount = ref({ followCount: 0, fansCount: 0 });
const followList = ref([]);
const fansList = ref([]);
const collections = ref([]);
const strategies = ref([]);
const notes = ref([]);
const foods = ref([]);
const trips = ref([]);
const footprints = ref([]);
const discoverUsers = ref([]);
const searchKeyword = ref("");
const defaultImage =
  "https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=100";

const getAvatar = (id) =>
  `https://api.dicebear.com/7.x/avataaars/svg?seed=${id}`;

const loadUser = () => {
  const userData = localStorage.getItem("user");
  if (userData) {
    user.value = JSON.parse(userData);
    profileForm.value = {
      nickname: user.value.nickname || "",
      phone: user.value.phone || "",
      email: user.value.email || "",
      avatar: user.value.avatar || "",
    };
  }
};

const loadFollowCount = async () => {
  try {
    const res = await api.getFollowCount();
    if (res.code === 200) {
      followCount.value = res.data;
    }
  } catch (e) {}
};

const loadFollowList = async () => {
  try {
    const res = await api.getFollowList();
    if (res.code === 200) {
      followList.value = res.data || [];
    }
  } catch (e) {}
};

const loadFansList = async () => {
  try {
    const res = await api.getFansList();
    if (res.code === 200) {
      const fans = res.data || [];
      for (const fan of fans) {
        try {
          const checkRes = await api.checkFollow(fan.userId);
          fan.isFollowedBack = checkRes.code === 200 && checkRes.data === true;
        } catch (e) {
          fan.isFollowedBack = false;
        }
      }
      fansList.value = fans;
    }
  } catch (e) {}
};

const loadCollections = async () => {
  try {
    const res = await api.getMyCollections();
    if (res.code === 200) {
      collections.value = res.data || [];
    }
  } catch (e) {
    console.error("加载收藏失败", e);
  }
};

const loadStrategies = async () => {
  try {
    const res = await api.getMyStrategies();
    if (res.code === 200) {
      strategies.value = res.data || [];
    }
  } catch (e) {
    console.error("加载我的攻略失败", e);
  }
};

const loadNotes = async () => {
  try {
    const res = await api.getMyNotes();
    if (res.code === 200) {
      notes.value = res.data || [];
    }
  } catch (e) {
    console.error("加载我的游记失败", e);
  }
};

const loadFoods = async () => {
  try {
    const res = await api.getMyFoods();
    if (res.code === 200) {
      foods.value = res.data || [];
    }
  } catch (e) {
    console.error("加载我的美食失败", e);
  }
};

const loadTrips = async () => {
  try {
    const res = await api.getMyTrips();
    if (res.code === 200) {
      trips.value = res.data || [];
    }
  } catch (e) {
    console.error("加载我的行程失败", e);
  }
};

const loadPublished = () => {
  loadStrategies();
  loadNotes();
  loadFoods();
  loadTrips();
};

const extractFirstImage = (html) => {
  const match = String(html || "").match(/<img[^>]+src=["']([^"']+)["']/i);
  return match?.[1] || "";
};

const publishedItems = computed(() =>
  [
    ...strategies.value.map((item) => ({
      id: item.id,
      kind: "strategy",
      typeName: "攻略",
      title: item.title,
      status: item.status,
      createTime: item.createTime,
      coverImage: item.coverImage,
      path: `/strategy/${item.id}`,
    })),
    ...notes.value.map((item) => ({
      id: item.id,
      kind: "note",
      typeName: "游记",
      title: item.title,
      status: item.status ?? 1,
      createTime: item.createTime,
      coverImage: item.coverImage || extractFirstImage(item.content),
      path: `/note/${item.id}`,
    })),
    ...foods.value.map((item) => ({
      id: item.id,
      kind: "food",
      typeName: "美食",
      title: item.name,
      status: item.status,
      createTime: item.createTime,
      coverImage: item.coverImage,
      path: `/food/${item.id}`,
    })),
    ...trips.value.map((item) => ({
      id: item.id,
      kind: "trip",
      typeName: "行程",
      title: item.title,
      status: item.status,
      createTime: item.createTime,
      coverImage: item.coverImage,
      path: `/trip/${item.id}`,
    })),
  ].sort((a, b) => new Date(b.createTime || 0) - new Date(a.createTime || 0)),
);

const getPublishStatusText = (status) => {
  if (status === 1) return "已发布";
  if (status === 2) return "已拒绝";
  return "待审核";
};

const getPublishStatusLabel = (status) => {
  if (status === 1) return "已发布";
  if (status === 2) return "已拒绝";
  return "待审核";
};

const cancelCollect = async (item) => {
  if (!confirm("确定取消收藏吗？")) return;
  try {
    const res = await api.toggleCollect(item.targetId, item.type);
    if (res.code === 200) {
      loadCollections();
    }
  } catch (e) {
    alert("操作失败");
  }
};

const getTypeName = (type) => {
  const types = { 1: "景区", 2: "攻略", 3: "行程", 4: "美食", 5: "景点" };
  return types[type] || "其他";
};

const goToDetail = (itemOrPath) => {
  if (typeof itemOrPath === "string") {
    router.push(itemOrPath);
    return;
  }
  const item = itemOrPath;
  const routes = {
    1: `/scenic/${item.targetId}`,
    2: `/strategy/${item.targetId}`,
    3: `/trip/${item.targetId}`,
    4: `/food/${item.targetId}`,
    5: `/attraction/${item.targetId}`,
  };
  const path = routes[item.type];
  if (path) {
    router.push(path);
  }
};

const loadFootprints = async () => {
  try {
    const res = await api.getUserFootprints(30);
    if (res.code === 200) {
      footprints.value = res.data || [];
    }
  } catch (e) {
    console.error("加载足迹失败", e);
  }
};

const unfollow = async (targetUserId) => {
  if (!confirm("确定取消关注吗？")) return;
  try {
    const res = await api.toggleFollow(targetUserId);
    if (res.code === 200) {
      loadFollowList();
      loadFollowCount();
    }
  } catch (e) {
    alert("操作失败");
  }
};

const followBack = async (fan) => {
  try {
    const res = await api.toggleFollow(fan.userId);
    if (res.code === 200) {
      fan.isFollowedBack = !fan.isFollowedBack;
      alert(res.data);
      loadFollowCount();
    }
  } catch (e) {
    alert("操作失败");
  }
};

const startPrivateChat = (targetId, targetName, targetAvatar) => {
  sessionStorage.setItem(
    "chatTarget",
    JSON.stringify({
      id: targetId,
      nickname: targetName,
      avatar: targetAvatar,
    }),
  );
  router.push("/chat");
};

const viewUserProfile = (userId) => {
  router.push(`/user/${userId}`);
};

const searchUsers = async () => {
  if (!searchKeyword.value.trim()) {
    loadRecommendUsers();
    return;
  }

  try {
    const res = await api.searchUsers(searchKeyword.value);
    if (res.code === 200) {
      const list = res.data || [];

      for (const item of list) {
        if (item.id !== user.value.id) {
          try {
            const checkRes = await api.checkFollow(item.id);
            item.isFollowed = checkRes.code === 200 && checkRes.data === true;
          } catch (e) {
            item.isFollowed = false;
          }
        }
      }

      discoverUsers.value = list;
    }
  } catch (e) {
    console.error("搜索用户失败", e);
  }
};

const loadRecommendUsers = async () => {
  try {
    const res = await api.getRecommendUsers();
    if (res.code === 200) {
      const list = res.data || [];

      for (const item of list) {
        if (item.id !== user.value.id) {
          try {
            const checkRes = await api.checkFollow(item.id);
            item.isFollowed = checkRes.code === 200 && checkRes.data === true;
          } catch (e) {
            item.isFollowed = false;
          }
        }
      }

      discoverUsers.value = list;
    }
  } catch (e) {}
};

const followUser = async (item) => {
  try {
    const res = await api.toggleFollow(item.id);
    if (res.code === 200) {
      item.isFollowed = !item.isFollowed;
      alert(res.data);
      loadFollowCount();
      loadFollowList();
    } else {
      alert(res.message || "操作失败");
    }
  } catch (e) {
    alert("操作失败");
  }
};

const formatDate = (dateStr) => {
  if (!dateStr) return "";
  return new Date(dateStr).toLocaleDateString("zh-CN");
};

const handleAvatarUpload = async (e) => {
  const file = e.target.files[0];
  if (!file) return;
  try {
    const res = await api.uploadFile(file);
    if (res.code === 200) {
      profileForm.value.avatar = res.data;
    } else {
      alert("上传失败: " + res.message);
    }
  } catch (err) {
    alert("上传失败");
  }
  e.target.value = "";
};

const validateProfile = () => {
  const nickname = profileForm.value.nickname?.trim();
  const phone = profileForm.value.phone?.trim();
  const email = profileForm.value.email?.trim();

  if (!nickname) {
    alert("昵称不能为空");
    return false;
  }

  if (!/^[\u4e00-\u9fa5a-zA-Z0-9_-]{2,20}$/.test(nickname)) {
    alert("昵称应为2到20位，只能包含中文、字母、数字、下划线或短横线");
    return false;
  }

  if (phone && !/^1[3-9]\d{9}$/.test(phone)) {
    alert("手机号格式不正确");
    return false;
  }

  if (email && !/^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$/.test(email)) {
    alert("邮箱格式不正确");
    return false;
  }

  return true;
};

const updateProfile = async () => {
  if (!validateProfile()) {
    return;
  }

  try {
    const res = await api.updateUserProfile({
      nickname: profileForm.value.nickname?.trim(),
      phone: profileForm.value.phone?.trim(),
      email: profileForm.value.email?.trim(),
      avatar: profileForm.value.avatar,
    });

    if (res.code === 200) {
      const updatedUser = { ...user.value, ...res.data };
      localStorage.setItem("user", JSON.stringify(updatedUser));
      user.value = updatedUser;
      alert("资料更新成功");
      location.reload();
    } else {
      alert(res.message || "更新失败");
    }
  } catch (e) {
    alert("更新失败，请检查网络");
  }
};

const loadPasswordCaptcha = async () => {
  passwordForm.value.captchaCode = "";
  try {
    const res = await api.getCaptcha();
    if (res.code === 200) {
      passwordCaptcha.value = res.data;
    }
  } catch (e) {
    alert("验证码加载失败");
  }
};

const validatePassword = () => {
  const oldPassword = passwordForm.value.oldPassword.trim();
  const newPassword = passwordForm.value.newPassword.trim();
  const confirmPassword = passwordForm.value.confirmPassword.trim();

  if (!oldPassword) {
    alert("请输入原密码");
    return false;
  }
  if (
    !/^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d_@#$%^&*!.-]{6,20}$/.test(newPassword)
  ) {
    alert("新密码应为6到20位，且至少包含字母和数字");
    return false;
  }
  if (newPassword !== confirmPassword) {
    alert("两次输入的新密码不一致");
    return false;
  }
  if (!passwordForm.value.captchaCode.trim()) {
    alert("请输入图形验证码");
    return false;
  }
  return true;
};

const changePassword = async () => {
  if (!validatePassword()) return;

  passwordLoading.value = true;
  try {
    const res = await api.changePassword({
      oldPassword: passwordForm.value.oldPassword.trim(),
      newPassword: passwordForm.value.newPassword.trim(),
      captchaId: passwordCaptcha.value.captchaId,
      captchaCode: passwordForm.value.captchaCode.trim(),
    });

    if (res.code === 200) {
      alert(res.message || "密码修改成功，请重新登录");
      localStorage.removeItem("userToken");
      localStorage.removeItem("user");
      router.push("/login");
    } else {
      alert(res.message || "修改失败");
      loadPasswordCaptcha();
    }
  } catch (e) {
    alert("修改失败，请检查网络");
    loadPasswordCaptcha();
  } finally {
    passwordLoading.value = false;
  }
};

watch(activeTab, (newTab) => {
  if (newTab === "follow") loadFollowList();
  if (newTab === "fans") loadFansList();
  if (newTab === "collect") loadCollections();
  if (newTab === "publish") loadPublished();
  if (newTab === "footprint") loadFootprints();
  if (newTab === "discover") loadRecommendUsers();
  if (newTab === "password") loadPasswordCaptcha();
});

onMounted(() => {
  loadUser();
  loadFollowCount();
  if (route.query.tab === "publish") {
    activeTab.value = "publish";
    loadPublished();
  }
});
</script>

<style scoped>
/* 统一蓝色主题 */
.page-header {
  background: linear-gradient(135deg, #5a9bcf 0%, #4a8bbf 100%);
  padding: 40px 0;
}

.page-header h1 {
  color: #fff;
  font-size: 28px;
  font-weight: 600;
  margin: 0;
}

/* 侧边栏 - 直角设计 */
.user-sidebar {
  background: #fff;
  border: 1px solid #e5e7eb;
  padding: 24px;
}

.user-avatar {
  text-align: center;
  padding-bottom: 20px;
  border-bottom: 1px solid #e5e7eb;
}

.user-avatar img {
  width: 100px;
  height: 100px;
  object-fit: cover;
  margin-bottom: 12px;
}

.user-avatar h4 {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 4px 0;
}

.follow-stats {
  display: flex;
  justify-content: space-around;
  padding: 16px 0;
  border-bottom: 1px solid #e5e7eb;
}

.stat-item {
  text-align: center;
  cursor: pointer;
}

.stat-item h5 {
  font-size: 20px;
  font-weight: 600;
  color: #2c3e50;
}

.stat-item small {
  font-size: 12px;
  color: #95a5a6;
}

.user-menu {
  list-style: none;
  padding: 0;
  margin: 16px 0 0 0;
}

.user-menu li {
  margin-bottom: 8px;
}

.user-menu li a {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  color: #7f8c8d;
  text-decoration: none;
  transition: all 0.2s;
}

.user-menu li a:hover {
  background: #f9fafb;
  color: #5a9bcf;
}

.user-menu li a.active {
  background: #e8f0f7;
  color: #5a9bcf;
  border-left: 3px solid #5a9bcf;
}

/* 内容卡片 */
.content-card {
  background: #fff;
  border: 1px solid #e5e7eb;
  padding: 24px;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 20px 0;
  padding-bottom: 8px;
  border-bottom: 2px solid #5a9bcf;
  display: inline-block;
}

/* 表单样式 */
.form-row {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.form-group {
  flex: 1;
  min-width: 200px;
}

.form-group.full {
  width: 100%;
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

.captcha-row {
  display: flex;
  gap: 12px;
  align-items: stretch;
  max-width: 360px;
}

.captcha-code {
  flex: 1;
}

.captcha-image {
  width: 120px;
  height: 38px;
  border: 1px solid #e5e7eb;
  background: #f8fafc;
  padding: 0;
  cursor: pointer;
}

.captcha-image img {
  width: 100%;
  height: 100%;
  display: block;
}

.avatar-upload {
  display: flex;
  align-items: center;
  gap: 20px;
}

.avatar-preview img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border: 1px solid #e5e7eb;
}

.upload-btn {
  background: #fff;
  border: 1px solid #e5e7eb;
  padding: 6px 16px;
  cursor: pointer;
  font-size: 13px;
}

.upload-btn:hover {
  border-color: #5a9bcf;
  color: #5a9bcf;
}

.upload-tip {
  font-size: 11px;
  color: #95a5a6;
  margin: 4px 0 0 0;
}

.form-actions {
  margin-top: 20px;
}

.submit-btn {
  background: #5a9bcf;
  color: #fff;
  border: none;
  padding: 8px 24px;
  cursor: pointer;
}

.submit-btn:hover {
  background: #4a8bbf;
}

/* 收藏列表 */
.collection-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.collection-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border: 1px solid #e5e7eb;
  cursor: pointer;
  transition: all 0.2s;
}

.collection-item:hover {
  border-color: #5a9bcf;
}

.collection-item img {
  width: 80px;
  height: 60px;
  object-fit: cover;
}

.collection-info {
  flex: 1;
}

.collection-info h6 {
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 4px 0;
}

.collection-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

.type-badge {
  background: #e8f0f7;
  color: #5a9bcf;
  padding: 2px 8px;
  font-size: 10px;
}

.collection-meta small {
  font-size: 11px;
  color: #95a5a6;
}

.delete-btn {
  background: none;
  border: 1px solid #e5e7eb;
  width: 32px;
  height: 32px;
  cursor: pointer;
  color: #95a5a6;
}

.delete-btn:hover {
  border-color: #e74c3c;
  color: #e74c3c;
}

/* 足迹网格 */
.footprint-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.footprint-item {
  border: 1px solid #e5e7eb;
  cursor: pointer;
  transition: all 0.2s;
}

.footprint-item:hover {
  border-color: #5a9bcf;
  transform: translateY(-2px);
}

.footprint-cover {
  position: relative;
  height: 120px;
  overflow: hidden;
}

.footprint-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.footprint-type {
  position: absolute;
  top: 8px;
  left: 8px;
  background: rgba(0, 0, 0, 0.7);
  color: #fff;
  padding: 2px 8px;
  font-size: 10px;
}

.footprint-info {
  padding: 10px;
}

.footprint-info h6 {
  font-size: 13px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 4px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.footprint-info small {
  font-size: 10px;
  color: #95a5a6;
}

/* 用户列表 */
.user-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.user-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border: 1px solid #e5e7eb;
}

.user-item img {
  width: 50px;
  height: 50px;
  object-fit: cover;
  cursor: pointer;
}

.user-info {
  flex: 1;
}

.user-info h6 {
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 4px 0;
  cursor: pointer;
}

.user-info h6:hover {
  color: #5a9bcf;
}

.user-info small {
  font-size: 11px;
  color: #95a5a6;
}

.user-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  padding: 4px 12px;
  border: 1px solid #e5e7eb;
  background: #fff;
  cursor: pointer;
  font-size: 12px;
}

.action-btn.chat:hover {
  border-color: #5a9bcf;
  color: #5a9bcf;
}

.action-btn.unfollow:hover {
  border-color: #e74c3c;
  color: #e74c3c;
}

.action-btn.follow {
  background: #5a9bcf;
  border-color: #5a9bcf;
  color: #fff;
}

.action-btn.follow:hover {
  background: #4a8bbf;
}

.action-btn.follow-back {
  background: #fff;
  border-color: #5a9bcf;
  color: #5a9bcf;
}

.action-btn.follow-back:hover {
  background: #5a9bcf;
  color: #fff;
}

.action-btn.follow-back.followed {
  background: #e5e7eb;
  border-color: #e5e7eb;
  color: #95a5a6;
  cursor: default;
}

.self-badge {
  background: #e5e7eb;
  padding: 4px 12px;
  font-size: 12px;
  color: #95a5a6;
}

/* 搜索区域 */
.search-area {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.search-input {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #e5e7eb;
  font-size: 14px;
  outline: none;
}

.search-input:focus {
  border-color: #5a9bcf;
}

.search-btn {
  background: #5a9bcf;
  color: #fff;
  border: none;
  padding: 8px 20px;
  cursor: pointer;
}

.search-btn:hover {
  background: #4a8bbf;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 48px 20px;
  color: #95a5a6;
}

.empty-state i {
  font-size: 48px;
}

.empty-state p {
  margin: 12px 0 0 0;
}

.empty-state small {
  font-size: 12px;
}

/* 响应式 */
@media (max-width: 768px) {
  .page-header {
    padding: 30px 0;
  }

  .form-row {
    flex-direction: column;
  }

  .avatar-upload {
    flex-direction: column;
    align-items: flex-start;
  }

  .collection-item {
    flex-wrap: wrap;
  }

  .user-item {
    flex-wrap: wrap;
  }

  .user-actions {
    width: 100%;
    justify-content: flex-end;
  }

  .footprint-grid {
    grid-template-columns: 1fr;
  }
}
.action-btn.follow.followed {
  background: #e8f5e9;
  color: #2e7d32;
  border-color: #2e7d32;
}

.action-btn.follow.followed:hover {
  background: #d6efd8;
}
</style>
