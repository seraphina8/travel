import { createRouter, createWebHistory } from "vue-router";

const routes = [
  {
    path: "/",
    name: "Home",
    component: () => import("../views/Home.vue"),
  },
  {
    path: "/login",
    name: "Login",
    component: () => import("../views/Login.vue"),
  },
  {
    path: "/register",
    name: "Register",
    component: () => import("../views/Register.vue"),
  },
  {
    path: "/forgot-password",
    name: "ForgotPassword",
    component: () => import("../views/ForgotPassword.vue"),
  },
  {
    path: "/scenic",
    name: "ScenicList",
    component: () => import("../views/ScenicList.vue"),
  },
  {
    path: "/scenic/:id",
    name: "ScenicDetail",
    component: () => import("../views/ScenicDetail.vue"),
  },
  {
    path: "/strategy",
    name: "StrategyList",
    component: () => import("../views/StrategyList.vue"),
  },
  {
    path: "/strategy/create",
    name: "StrategyCreate",
    component: () => import("../views/StrategyCreate.vue"),
    meta: { requiresAuth: true },
  },
  {
    path: "/strategy/:id",
    name: "StrategyDetail",
    component: () => import("../views/StrategyDetail.vue"),
  },
  {
    path: "/user",
    name: "UserCenter",
    component: () => import("../views/UserCenter.vue"),
    meta: { requiresAuth: true },
  },
  {
    path: "/user/:id",
    name: "UserProfile",
    component: () => import("../views/UserProfile.vue"),
  },
  {
    path: "/order",
    name: "MyOrders",
    component: () => import("../views/MyOrders.vue"),
    meta: { requiresAuth: true },
  },
  {
    path: "/chat",
    name: "Chat",
    component: () => import("../views/Chat.vue"),
    meta: { requiresAuth: true },
  },
  {
    path: "/map",
    name: "ScenicMap",
    component: () => import("../views/ScenicMap.vue"),
  },
  {
    path: "/note",
    name: "TravelNote",
    component: () => import("../views/TravelNote.vue"),
  },
  {
    path: "/note/create",
    name: "TravelNoteCreate",
    component: () => import("../views/TravelNoteCreate.vue"),
    meta: { requiresAuth: true },
  },
  {
    path: "/pay/mock",
    name: "MockPay",
    component: () => import("../views/MockPay.vue"),
    meta: { requiresAuth: true },
  },
  {
    path: "/pay/success",
    name: "PaySuccess",
    component: () => import("../views/PaySuccess.vue"),
  },
  {
    path: "/trip",
    name: "TripList",
    component: () => import("../views/TripList.vue"),
  },
  {
    path: "/trip/create",
    name: "TripCreate",
    component: () => import("../views/TripCreate.vue"),
    meta: { requiresAuth: true },
  },
  {
    path: "/trip/edit/:id",
    name: "TripEdit",
    component: () => import("../views/TripCreate.vue"),
    meta: { requiresAuth: true },
  },
  {
    path: "/trip/:id",
    name: "TripDetail",
    component: () => import("../views/TripDetail.vue"),
  },
  {
    path: "/attraction",
    name: "AttractionList",
    component: () => import("../views/AttractionList.vue"),
  },
  {
    path: "/attraction/:id",
    name: "AttractionDetail",
    component: () => import("../views/AttractionDetail.vue"),
  },
  {
    path: "/note/:id",
    name: "TravelNoteDetail",
    component: () => import("../views/TravelNoteDetail.vue"),
  },
  {
    path: "/food",
    name: "FoodList",
    component: () => import("../views/FoodList.vue"),
  },
  {
    path: "/food/share",
    name: "FoodShare",
    component: () => import("../views/FoodShare.vue"),
    meta: { requiresAuth: true },
  },
  {
    path: "/food/:id",
    name: "FoodDetail",
    component: () => import("../views/FoodDetail.vue"),
  },
  {
    path: "/search",
    name: "SearchResult",
    component: () => import("../views/SearchResult.vue"),
  },
  // ai行程
  {
    path: "/ai-trip-result",
    name: "AiTripResult",
    component: () => import("../views/AiTripResult.vue"),
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 };
  },
});

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem("userToken");
  if (to.meta.requiresAuth && !token) {
    next("/login");
  } else {
    next();
  }
});

export default router;
