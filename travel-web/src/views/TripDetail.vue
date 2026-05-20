<template>
  <div class="page-header">
    <div class="container">
      <h1>{{ trip.title || "行程详情" }}</h1>
      <p class="text-white-50">
        <i class="bi bi-calendar"></i> {{ trip.days }}天{{
          (trip.days || 1) - 1
        }}晚
      </p>
    </div>
  </div>

  <div class="container py-4">
    <div class="row g-4">
      <!-- 左侧：行程信息 -->
      <div class="col-lg-8">
        <div class="bg-white rounded-3 shadow-sm overflow-hidden">
          <div class="trip-cover" v-if="trip.coverImage">
            <img :src="trip.coverImage" :alt="trip.title" />
          </div>

          <div class="p-4">
            <h3 class="mb-3">{{ trip.title }}</h3>

            <div class="trip-meta mb-4">
              <span v-if="trip.startDate">
                <i class="bi bi-calendar-event"></i>
                {{ formatDate(trip.startDate) }} -
                {{ formatDate(trip.endDate) }}
              </span>
              <span> <i class="bi bi-clock"></i> {{ trip.days }}天行程 </span>
            </div>

            <h5>行程简介</h5>
            <p class="text-muted" style="white-space: pre-wrap">
              {{ trip.description || "暂无简介" }}
            </p>

            <!-- 行程明细 -->
            <h5 class="mt-4">行程安排</h5>
            <div class="timeline" v-if="details.length > 0">
              <div
                class="timeline-item"
                v-for="(day, idx) in groupedDetails"
                :key="idx"
              >
                <div class="timeline-day">第{{ idx + 1 }}天</div>
                <div class="timeline-content">
                  <div
                    class="timeline-point"
                    v-for="item in day"
                    :key="item.id"
                  >
                    <h6>{{ item.attractionName }}</h6>
                    <p class="text-muted small mb-0">{{ item.description }}</p>
                  </div>
                </div>
              </div>
            </div>
            <p class="text-muted" v-else>暂无详细行程安排</p>
          </div>
        </div>
      </div>

      <!-- 右侧：地图和操作 -->
      <div class="col-lg-4">
        <div class="bg-white rounded-3 shadow-sm p-4 mb-4">
          <h5 class="mb-3"><i class="bi bi-map text-primary"></i> 路线地图</h5>
          <div
            id="tripMap"
            style="height: 300px; border-radius: 8px; background: #f1f5f9"
          ></div>
        </div>

        <div class="bg-white rounded-3 shadow-sm p-4">
          <h5 class="mb-3">操作</h5>

          <button class="btn btn-primary w-100 mb-2" @click="editTrip">
            <i class="bi bi-pencil-square"></i> 编辑行程
          </button>

          <button
            class="btn btn-outline-danger w-100 mb-2"
            @click="toggleCollect"
          >
            <i
              class="bi"
              :class="isCollected ? 'bi-heart-fill' : 'bi-heart'"
            ></i>
            {{ isCollected ? "已收藏" : "收藏行程" }}
          </button>

          <button class="btn btn-outline-primary w-100" @click="shareTrip">
            <i class="bi bi-share"></i> 分享行程
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import AMapLoader from "@amap/amap-jsapi-loader";
import api from "../api";
import ShareUtils from "../utils/share";

const route = useRoute();
const router = useRouter();
const trip = ref({});
const details = ref([]);
const isCollected = ref(false);

let map = null;
let AMapInstance = null;

const groupedDetails = computed(() => {
  const groups = [];
  details.value.forEach((item) => {
    const dayIdx = (item.dayNum || 1) - 1;
    if (!groups[dayIdx]) groups[dayIdx] = [];
    groups[dayIdx].push(item);
  });
  return groups;
});

const formatDate = (date) => {
  if (!date) return "";
  return new Date(date).toLocaleDateString("zh-CN");
};

const loadData = async () => {
  try {
    const res = await api.getTripDetail(route.params.id);
    if (res.code === 200) {
      // 新接口返回 { trip, details }
      if (res.data.trip) {
        trip.value = res.data.trip;
        details.value = res.data.details || [];
      } else {
        // 兼容旧接口
        trip.value = res.data;
        if (res.data.routeData) {
          try {
            details.value = JSON.parse(res.data.routeData);
          } catch {}
        }
      }
      console.log("行程详情加载完成，景点数据:", details.value);
      // 数据加载完成后绘制路线
      if (AMapInstance && map) {
        drawRoute(AMapInstance);
      }
    }
  } catch (e) {
    console.error("加载行程详情失败", e);
  }
};

const checkCollect = async () => {
  if (!localStorage.getItem("userToken")) return;
  try {
    const res = await api.checkCollect(route.params.id, 3);
    if (res.code === 200) {
      isCollected.value = res.data;
    }
  } catch (e) {}
};

const toggleCollect = async () => {
  if (!localStorage.getItem("userToken")) {
    alert("请先登录");
    return;
  }
  try {
    const res = await api.toggleCollect(route.params.id, 3);
    if (res.code === 200) {
      isCollected.value = !isCollected.value;
      alert(res.data);
    }
  } catch (e) {
    alert("操作失败");
  }
};

const shareTrip = () => {
  ShareUtils.nativeShare(
    trip.value.title,
    trip.value.description?.substring(0, 100),
    window.location.href,
  );
};

const editTrip = () => {
  router.push(`/trip/edit/${route.params.id}`);
};

const initMap = async () => {
  try {
    window._AMapSecurityConfig = {
      securityJsCode: "734d51563a3b2c4a2433ae3481309408",
    };

    const AMap = await AMapLoader.load({
      key: "d201b9704d5bff29ddd4510c45c75cf4",
      version: "2.0",
      plugins: ["AMap.Geocoder", "AMap.Driving"],
    });

    AMapInstance = AMap;

    map = new AMap.Map("tripMap", {
      zoom: 5,
      center: [116.39747, 39.908823],
    });

    console.log("地图初始化完成");
    // 如果数据已加载，绘制路线
    if (details.value.length > 0) {
      drawRoute(AMap);
    }
  } catch (e) {
    console.error("地图加载失败", e);
  }
};

const drawRoute = async (AMap) => {
  if (!map || details.value.length === 0) {
    console.log("drawRoute: 地图或数据未准备好", {
      map: !!map,
      detailsLength: details.value.length,
    });
    return;
  }

  console.log("行程明细数据:", details.value);

  const markers = [];
  const positions = [];

  // 遍历所有景点，优先使用保存的经纬度
  for (let i = 0; i < details.value.length; i++) {
    const item = details.value[i];
    const name = item.attractionName || item.attraction_name;
    const lat = item.latitude;
    const lng = item.longitude;

    if (!name) continue;

    console.log(`景点 ${i + 1}: ${name}, 经纬度: ${lat}, ${lng}`);

    let position = null;

    // 如果有保存的经纬度，直接使用
    if (lat && lng) {
      position = new AMap.LngLat(lng, lat);
    } else {
      // 否则使用地理编码
      const geocoder = new AMap.Geocoder();
      try {
        position = await new Promise((resolve) => {
          geocoder.getLocation(name, (status, result) => {
            if (
              status === "complete" &&
              result.geocodes &&
              result.geocodes.length > 0
            ) {
              resolve(result.geocodes[0].location);
            } else {
              resolve(null);
            }
          });
        });
      } catch (e) {
        console.error("地理编码失败:", name, e);
      }
    }

    if (position) {
      positions.push(position);

      // 创建标记
      const marker = new AMap.Marker({
        position: position,
        title: name,
        label: {
          content: `<div style="background:#667eea;color:#fff;padding:2px 8px;border-radius:10px;font-size:12px;">${i + 1}. ${name}</div>`,
          direction: "top",
        },
      });
      markers.push(marker);
    }
  }

  console.log(`成功解析 ${markers.length} 个景点位置`);

  if (markers.length > 0) {
    map.add(markers);

    // 如果有多个点，绘制路线
    if (positions.length >= 2) {
      // 使用折线连接各点
      const path = positions.map((p) => [p.lng, p.lat]);
      console.log("绘制路线路径:", path);
      const polyline = new AMap.Polyline({
        path: path,
        strokeColor: "#667eea",
        strokeWeight: 4,
        strokeOpacity: 0.8,
        strokeStyle: "solid",
        lineJoin: "round",
        lineCap: "round",
        showDir: true,
      });
      map.add(polyline);
    }

    // 自适应显示所有标记
    map.setFitView(markers);
  }
};

const recordView = async () => {
  if (localStorage.getItem("userToken")) {
    try {
      await api.recordBehavior(route.params.id, 3, 1);
    } catch (e) {}
  }
};

onMounted(() => {
  loadData();
  recordView();
  checkCollect();
  initMap();
});

onUnmounted(() => {
  if (map) {
    map.destroy();
  }
});
</script>

<style scoped>
.trip-cover {
  height: 300px;
  overflow: hidden;
}

.trip-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.trip-meta {
  display: flex;
  gap: 24px;
  color: #64748b;
}

.timeline {
  position: relative;
  padding-left: 20px;
}

.timeline::before {
  content: "";
  position: absolute;
  left: 6px;
  top: 0;
  bottom: 0;
  width: 2px;
  background: #e2e8f0;
}

.timeline-item {
  position: relative;
  margin-bottom: 24px;
}

.timeline-day {
  font-weight: 600;
  color: #667eea;
  margin-bottom: 12px;
}

.timeline-day::before {
  content: "";
  position: absolute;
  left: -20px;
  top: 4px;
  width: 12px;
  height: 12px;
  background: #667eea;
  border-radius: 50%;
}

.timeline-point {
  padding: 12px;
  background: #f8fafc;
  border-radius: 8px;
  margin-bottom: 8px;
}

.timeline-point h6 {
  margin-bottom: 4px;
}
</style>
