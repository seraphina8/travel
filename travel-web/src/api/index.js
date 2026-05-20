import axios from "axios";
import { fixImageDeep } from "../utils/imageHelper";

const api = axios.create({
  baseURL: "/api",
  timeout: 30 * 1000,
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("userToken");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => {
    // 自动处理返回数据中的图片URL
    let data = response.data;
    if (data && typeof data === "object") {
      data = fixImageDeep(data);
    }
    return data;
  },
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem("userToken");
      localStorage.removeItem("user");
      window.location.href = "/login";
    }
    return Promise.reject(error);
  },
);

export default {
  // 用户认证
  login(data) {
    return api.post("/user/login", data);
  },
  register(data) {
    return api.post("/user/register", data);
  },
  getCaptcha() {
    return api.get("/user/captcha");
  },
  changePassword(data) {
    return api.put("/user/password", data);
  },
  forgotPassword(data) {
    return api.post("/user/forgot-password", data);
  },
  getCurrentUserInfo() {
    return api.get("/user/info");
  },

  // 景区
  getScenicList(params) {
    return api.get("/scenic", { params });
  },
  getScenicDetail(id) {
    return api.get(`/scenic/${id}`);
  },
  getTicketsByScenic(scenicId) {
    return api.get(`/ticket/scenic/${scenicId}`);
  },

  // 攻略
  getStrategyList(params) {
    return api.get("/strategy", { params });
  },
  getStrategyDetail(id) {
    return api.get(`/strategy/${id}`);
  },
  getRelatedStrategies(params) {
    return api.get("/strategy/related", { params });
  },
  publishStrategy(data) {
    return api.post("/strategy/publish", data);
  },
  getMyStrategies() {
    return api.get("/strategy/my");
  },

  // 订单
  createOrder(data) {
    return api.post("/order", data);
  },
  getMyOrders(params) {
    return api.get("/order/my", { params });
  },
  cancelOrder(id) {
    return api.put(`/order/${id}/cancel`);
  },
  // 收藏
  collectScenic(scenicId) {
    return api.post("/collect", { targetId: scenicId, type: 1 });
  },
  getMyCollections() {
    return api.get("/collect/my");
  },
  getHotAttractions() {
    return api.get("/stats/attraction/hot");
  },

  // 聊天
  getChatSessions() {
    return api.get("/chat/sessions");
  },
  getChatHistory(targetId, limit = 50) {
    return api.get(`/chat/history/${targetId}`, { params: { limit } });
  },
  getChatContacts() {
    return api.get("/chat/contacts");
  },
  checkOnline(userId) {
    return api.get(`/chat/online/${userId}`);
  },
  deleteSession(targetId) {
    return api.delete(`/chat/session/${targetId}`);
  },

  // 推荐
  getRecommendScenics(limit = 6) {
    return api.get("/recommend/scenics", { params: { limit } });
  },
  getHotScenics(limit = 6) {
    return api.get("/recommend/hot", { params: { limit } });
  },
  getRecommendStrategies(limit = 6) {
    return api.get("/recommend/strategies", { params: { limit } });
  },
  getRecommendTripCards(limit = 6) {
    return api.get("/recommend/trips", { params: { limit } });
  },
  getRecommendFoods(limit = 6) {
    return api.get("/recommend/foods", { params: { limit } });
  },
  getRecommendAttractions(limit = 6) {
    return api.get("/recommend/attractions", { params: { limit } });
  },
  recordBehavior(targetId, targetType, behaviorType) {
    return api.post("/recommend/behavior", {
      targetId,
      targetType,
      behaviorType,
    });
  },
  getUserFootprints(limit = 20) {
    return api.get("/recommend/footprint", { params: { limit } });
  },

  // 游记
  getTravelNotes(params) {
    return api.get("/note", { params });
  },
  getTravelNoteDetail(id) {
    return api.get(`/note/${id}`);
  },
  getRelatedNotes(params) {
    return api.get("/note/related", { params });
  },
  createTravelNote(data) {
    return api.post("/note", data);
  },
  getMyNotes() {
    return api.get("/note/my");
  },
  getFootprint() {
    return api.get("/note/footprint");
  },

  // 游记点赞
  toggleNoteLike(noteId) {
    return api.post(`/note/${noteId}/like`);
  },
  checkNoteLike(noteId) {
    return api.get(`/note/${noteId}/like/check`);
  },

  // 游记评论
  getNoteComments(noteId) {
    return api.get(`/note/${noteId}/comments`);
  },
  addNoteComment(noteId, content, parentId = null) {
    return api.post(`/note/${noteId}/comment`, { content, parentId });
  },
  deleteNoteComment(commentId) {
    return api.delete(`/note/comment/${commentId}`);
  },

  // 通用评论
  getComments(targetType, targetId) {
    return api.get(`/comment/${targetType}/${targetId}`);
  },
  addComment(data) {
    return api.post("/comment", data);
  },
  deleteComment(id) {
    return api.delete(`/comment/${id}`);
  },
  getCommentCount(targetType, targetId) {
    return api.get(`/comment/count/${targetType}/${targetId}`);
  },

  // 收藏
  toggleCollect(targetId, type) {
    return api.post("/collect", { targetId, type });
  },
  checkCollect(targetId, type) {
    return api.get("/collect/check", { params: { targetId, type } });
  },

  // 支付
  payOrder(orderId) {
    return api.post("/pay/mock", { orderId });
  },
  confirmPayment(orderNo, payType) {
    return api.post("/pay/confirm", { orderNo, payType });
  },
  // 支付宝沙箱支付
  alipayOrder(orderId) {
    return api.post("/pay/alipay", { orderId });
  },
  alipayReturn(params) {
    return api.get("/pay/alipay/return", { params });
  },

  // 行程
  getTripList(params) {
    return api.get("/trip", { params });
  },
  getTripDetail(id) {
    return api.get(`/trip/${id}`);
  },
  getRecommendTrips() {
    return api.get("/trip/recommend");
  },
  getMyTrips() {
    return api.get("/trip/my");
  },
  toggleTripPublic(id, isPublic) {
    return api.put(`/trip/${id}/public`, null, { params: { isPublic } });
  },
  createTrip(data) {
    return api.post("/trip", data);
  },
  updateTrip(id, data) {
    return api.put(`/trip/${id}`, data);
  },
  deleteTrip(id) {
    return api.delete(`/trip/${id}`);
  },
  getTripDetails(tripId) {
    return api.get(`/trip/${tripId}/details`);
  },
  saveTripDetails(tripId, details) {
    return api.post(`/trip/${tripId}/details`, details);
  },
  // AI生成行程
  aiGenerateTrip(params) {
    return api.post("/ai/generate-trip", params);
  },
  // 保存AI生成的行程
  saveAiTripDetails(tripId, details) {
    return api.post(`/trip/${tripId}/ai-details`, details);
  },

  // 景点
  getAttractionList(params) {
    return api.get("/attraction", { params });
  },
  getAttractionDetail(id) {
    return api.get(`/attraction/${id}`);
  },
  getAttractionRecommend(limit = 6) {
    return api.get("/attraction/recommend", { params: { limit } });
  },

  // 美食
  getFoodList(params) {
    return api.get("/food", { params });
  },
  getFoodDetail(id) {
    return api.get(`/food/${id}`);
  },
  shareFood(data) {
    return api.post("/food/share", data);
  },
  getMyFoods() {
    return api.get("/food/my");
  },
  deleteMyFood(id) {
    return api.delete(`/food/my/${id}`);
  },

  // 关注
  toggleFollow(targetUserId) {
    return api.post(`/follow/${targetUserId}`);
  },
  checkFollow(targetUserId) {
    return api.get(`/follow/check/${targetUserId}`);
  },
  getFollowList() {
    return api.get("/follow/list");
  },
  getFansList() {
    return api.get("/follow/fans");
  },
  getFollowCount() {
    return api.get("/follow/count");
  },
  getUserFollowCount(userId) {
    return api.get(`/follow/count/${userId}`);
  },

  // 用户信息
  getUserInfo(userId) {
    return api.get(`/user/${userId}`);
  },
  getUserFoods(userId) {
    return api.get(`/food/user/${userId}`);
  },
  searchUsers(keyword) {
    return api.get("/user/search", { params: { keyword } });
  },
  getRecommendUsers() {
    return api.get("/user/recommend");
  },

  // AI导游
  aiChat(message, history = []) {
    return api.post("/ai/chat", { message, history }, { timeout: 60000 });
  },

  // 标签
  getTagList(type) {
    return api.get("/tag", { params: { type } });
  },

  // 轮播图
  getActiveBanners() {
    return api.get("/banner/active");
  },

  // 文件上传
  uploadFile(file) {
    const formData = new FormData();
    formData.append("file", file);
    return api.post("/upload", formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
  },

  // 用户资料更新
  updateUserProfile(data) {
    return api.put("/user/profile", data);
  },
};
