<template>
  <div class="page-header">
    <div class="container">
      <h1>
        <i class="bi bi-calendar-plus"></i>
        {{ isEdit ? "编辑行程" : "创建行程" }}
      </h1>
    </div>
  </div>

  <div class="container py-4">
    <div class="row justify-content-center">
      <div class="col-lg-8">
        <div class="bg-white rounded-3 shadow-sm p-4">
          <form @submit.prevent="submitForm">
            <div class="mb-3">
              <label class="form-label">行程标题 *</label>
              <input
                type="text"
                class="form-control"
                v-model="form.title"
                required
              />
            </div>

            <div class="row mb-3">
              <div class="col-md-4">
                <label class="form-label">行程天数</label>
                <input
                  type="number"
                  class="form-control"
                  v-model="form.days"
                  min="1"
                  max="30"
                />
              </div>
              <div class="col-md-4">
                <label class="form-label">开始日期</label>
                <input
                  type="date"
                  class="form-control"
                  v-model="form.startDate"
                />
              </div>
              <div class="col-md-4">
                <label class="form-label">结束日期</label>
                <input
                  type="date"
                  class="form-control"
                  v-model="form.endDate"
                />
              </div>
            </div>

            <div class="mb-3">
              <label class="form-label">封面图片URL</label>
              <div class="input-group">
                <input
                  type="text"
                  class="form-control"
                  v-model="form.coverImage"
                  placeholder="https://..."
                />
                <input
                  type="file"
                  ref="fileInput"
                  accept="image/*"
                  class="d-none"
                  @change="handleFileUpload"
                />
                <button
                  class="btn btn-outline-secondary"
                  type="button"
                  @click="triggerUpload"
                >
                  <i class="bi bi-upload"></i> 上传图片
                </button>
              </div>
              <div v-if="form.coverImage" class="mt-2">
                <img
                  :src="form.coverImage"
                  alt="封面预览"
                  class="img-thumbnail"
                  style="max-height: 200px"
                />
              </div>
            </div>

            <div class="mb-3">
              <label class="form-label">行程简介</label>
              <textarea
                class="form-control"
                v-model="form.description"
                rows="3"
              ></textarea>
            </div>

            <div class="mb-3">
              <label class="form-label">标签</label>
              <div class="tag-options" v-if="tags.length">
                <button
                  v-for="tag in tags"
                  :key="tag.id"
                  type="button"
                  class="tag-option"
                  :class="{ active: selectedTags.includes(tag.name) }"
                  @click="toggleTag(tag.name)"
                >
                  {{ tag.name }}
                </button>
              </div>
              <input
                type="text"
                class="form-control mt-2"
                v-model="customTags"
                placeholder="也可以手动输入，多个标签用英文逗号分隔"
              />
            </div>

            <div class="mb-3">
              <div class="form-check form-switch">
                <input
                  class="form-check-input"
                  type="checkbox"
                  id="isPublicSwitch"
                  v-model="form.isPublic"
                />
                <label class="form-check-label" for="isPublicSwitch">
                  <i
                    class="bi"
                    :class="form.isPublic ? 'bi-globe' : 'bi-lock'"
                  ></i>
                  {{
                    form.isPublic
                      ? "公开行程（其他用户可浏览）"
                      : "私有行程（仅自己可见）"
                  }}
                </label>
              </div>
            </div>

            <!-- 行程明细 -->
            <div class="mb-4">
              <div
                class="d-flex justify-content-between align-items-center mb-3"
              >
                <label class="form-label mb-0">行程安排</label>
                <button
                  type="button"
                  class="btn btn-sm btn-outline-primary"
                  @click="addDetail"
                >
                  <i class="bi bi-plus"></i> 添加景点
                </button>
              </div>

              <div class="detail-list">
                <div
                  class="detail-item"
                  v-for="(item, idx) in details"
                  :key="idx"
                >
                  <div class="row g-2 align-items-center">
                    <div class="col-md-2">
                      <select
                        class="form-select form-select-sm"
                        v-model="item.dayNum"
                      >
                        <option v-for="d in form.days" :key="d" :value="d">
                          第{{ d }}天
                        </option>
                      </select>
                    </div>
                    <div class="col-md-4">
                      <div class="position-relative">
                        <input
                          type="text"
                          class="form-control form-control-sm"
                          v-model="item.attractionName"
                          placeholder="搜索景点..."
                          @input="searchAttraction(idx, item.attractionName)"
                          @focus="item.showDropdown = true"
                          @blur="hideDropdown(idx)"
                        />
                        <div
                          class="attraction-dropdown"
                          v-if="
                            item.showDropdown &&
                            item.searchResults &&
                            item.searchResults.length > 0
                          "
                        >
                          <div
                            class="dropdown-item"
                            v-for="attr in item.searchResults"
                            :key="attr.id"
                            @mousedown.prevent="selectAttraction(idx, attr)"
                          >
                            <div class="fw-bold">{{ attr.name }}</div>
                            <small class="text-muted"
                              >{{ attr.province }} {{ attr.city }}</small
                            >
                          </div>
                        </div>
                      </div>
                    </div>
                    <div class="col-md-5">
                      <input
                        type="text"
                        class="form-control form-control-sm"
                        v-model="item.description"
                        placeholder="安排说明"
                      />
                    </div>
                    <div class="col-md-1">
                      <button
                        type="button"
                        class="btn btn-sm btn-outline-danger"
                        @click="removeDetail(idx)"
                      >
                        <i class="bi bi-trash"></i>
                      </button>
                    </div>
                  </div>
                  <div class="mt-1" v-if="item.latitude && item.longitude">
                    <small class="text-success"
                      ><i class="bi bi-geo-alt"></i> 已获取位置:
                      {{ item.latitude }}, {{ item.longitude }}</small
                    >
                  </div>
                </div>
                <p class="text-muted small" v-if="details.length === 0">
                  暂无行程安排，点击上方按钮添加
                </p>
              </div>
            </div>

            <div class="d-flex gap-2">
              <button
                type="submit"
                class="btn btn-primary"
                :disabled="submitting"
              >
                {{
                  submitting ? "保存中..." : isEdit ? "保存修改" : "保存行程"
                }}
              </button>
              <router-link to="/trip" class="btn btn-outline-secondary"
                >取消</router-link
              >
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import api from "../api";

const route = useRoute();
const router = useRouter();

const isEdit = computed(() => !!route.params.id);
const editTripId = computed(() => route.params.id);

const submitting = ref(false);
const fileInput = ref(null);
const tags = ref([]);
const selectedTags = ref([]);
const customTags = ref("");
let syncingDate = false;

const form = ref({
  title: "",
  days: "",
  startDate: "",
  endDate: "",
  coverImage: "",
  tags: "",
  description: "",
  isPublic: false,
});

const details = ref([]);

const toDate = (value) => {
  if (!value) return null;
  const [year, month, day] = value.split("-").map(Number);
  if (!year || !month || !day) return null;
  return new Date(year, month - 1, day);
};

const toDateString = (date) => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
};

const addDays = (value, days) => {
  const date = toDate(value);
  if (!date || !Number.isFinite(days)) return "";
  date.setDate(date.getDate() + days);
  return toDateString(date);
};

const diffDays = (start, end) => {
  const startDate = toDate(start);
  const endDate = toDate(end);
  if (!startDate || !endDate) return null;
  const oneDay = 24 * 60 * 60 * 1000;
  return Math.round((endDate - startDate) / oneDay);
};

const syncEndDate = () => {
  if (!form.value.startDate) return;
  const days = Number(form.value.days);
  if (!Number.isInteger(days) || days < 1) return;
  syncingDate = true;
  form.value.endDate = addDays(form.value.startDate, days - 1);
  syncingDate = false;
};

const initializeCreateDates = () => {
  if (isEdit.value) return;
  if (!form.value.startDate) {
    form.value.startDate = toDateString(new Date());
  }
  syncEndDate();
};

const mergedTags = computed(() => {
  const manual = customTags.value
    .split(",")
    .map((item) => item.trim())
    .filter(Boolean);
  return Array.from(new Set([...selectedTags.value, ...manual])).join(",");
});

const loadTags = async () => {
  const res = await api.getTagList(3);
  if (res.code === 200) {
    tags.value = res.data || [];
  }
};

const splitTags = (value) => {
  return (value || "")
    .split(",")
    .map((item) => item.trim())
    .filter(Boolean);
};

const toggleTag = (name) => {
  if (selectedTags.value.includes(name)) {
    selectedTags.value = selectedTags.value.filter((item) => item !== name);
  } else {
    selectedTags.value.push(name);
  }
};

const handleFileUpload = async (event) => {
  const file = event.target.files[0];
  if (!file) return;

  try {
    const res = await api.uploadFile(file);
    if (res.code === 200) {
      form.value.coverImage = res.data;
    } else {
      alert("上传失败: " + res.message);
    }
  } catch (e) {
    alert("上传失败");
    console.error(e);
  }
};

const triggerUpload = () => {
  fileInput.value?.click();
};

const addDetail = () => {
  details.value.push({
    dayNum: 1,
    attractionName: "",
    attractionId: null,
    latitude: null,
    longitude: null,
    description: "",
    sortOrder: details.value.length,
    showDropdown: false,
    searchResults: [],
  });
};

const removeDetail = (idx) => {
  details.value.splice(idx, 1);
  details.value.forEach((item, index) => {
    item.sortOrder = index;
  });
};

let searchTimer = null;

const searchAttraction = async (idx, keyword) => {
  if (!keyword || keyword.length < 1) {
    details.value[idx].searchResults = [];
    return;
  }

  if (searchTimer) clearTimeout(searchTimer);

  searchTimer = setTimeout(async () => {
    try {
      const res = await api.getAttractionList({
        keyword,
        pageSize: 10,
      });

      if (res.code === 200) {
        details.value[idx].searchResults = res.data.records || res.data || [];
      }
    } catch (e) {
      console.error("搜索景点失败", e);
    }
  }, 300);
};

const selectAttraction = (idx, attr) => {
  details.value[idx].attractionName = attr.name;
  details.value[idx].attractionId = attr.id;
  details.value[idx].latitude = attr.latitude;
  details.value[idx].longitude = attr.longitude;
  details.value[idx].showDropdown = false;
  details.value[idx].searchResults = [];
};

const hideDropdown = (idx) => {
  setTimeout(() => {
    if (details.value[idx]) {
      details.value[idx].showDropdown = false;
    }
  }, 200);
};

const loadTrip = async () => {
  if (!isEdit.value) return;

  try {
    const res = await api.getTripDetail(editTripId.value);

    if (res.code === 200 && res.data) {
      const tripData = res.data.trip || res.data;

      form.value = {
        title: tripData.title || "",
        days: tripData.days || 3,
        startDate: tripData.startDate || "",
        endDate: tripData.endDate || "",
        coverImage: tripData.coverImage || "",
        tags: tripData.tags || "",
        description: tripData.description || "",
        isPublic: tripData.isPublic === 1,
      };
      selectedTags.value = splitTags(tripData.tags).filter((name) =>
        tags.value.some((tag) => tag.name === name),
      );
      customTags.value = splitTags(tripData.tags)
        .filter((name) => !tags.value.some((tag) => tag.name === name))
        .join(",");

      if (res.data.details && res.data.details.length > 0) {
        details.value = res.data.details.map((item, index) => ({
          dayNum: item.dayNum || 1,
          attractionName: item.attractionName || "",
          attractionId: item.attractionId || null,
          latitude: item.latitude || null,
          longitude: item.longitude || null,
          description: item.description || "",
          sortOrder: item.sortOrder ?? index,
          showDropdown: false,
          searchResults: [],
        }));
      } else if (tripData.routeData) {
        try {
          details.value = JSON.parse(tripData.routeData).map((item, index) => ({
            ...item,
            sortOrder: item.sortOrder ?? index,
            showDropdown: false,
            searchResults: [],
          }));
        } catch (e) {
          details.value = [];
        }
      }
    }
  } catch (e) {
    console.error("加载行程失败", e);
    alert("加载行程失败");
  }
};

const submitForm = async () => {
  if (!form.value.title || !form.value.title.trim()) {
    alert("请填写行程标题");
    return;
  }

  if (!form.value.days || form.value.days < 1) {
    alert("请填写正确的行程天数");
    return;
  }

  submitting.value = true;

  try {
    details.value.forEach((item, index) => {
      item.sortOrder = index;
    });

    const data = {
      ...form.value,
      title: form.value.title.trim(),
      days: Number(form.value.days),
      tags: mergedTags.value,
      isPublic: form.value.isPublic ? 1 : 0,
      routeData: JSON.stringify(details.value),
    };

    let currentTripId = editTripId.value;
    let res;

    if (isEdit.value) {
      res = await api.updateTrip(currentTripId, data);
    } else {
      res = await api.createTrip(data);
      if (res.code === 200 && res.data) {
        currentTripId = res.data;
      }
    }

    if (res.code !== 200) {
      alert(res.message || "保存失败");
      return;
    }

    if (currentTripId && details.value.length > 0) {
      await api.saveTripDetails(currentTripId, details.value);
    }

    alert(isEdit.value ? "修改成功" : "保存成功");
    router.push("/trip");
  } catch (e) {
    console.error("保存失败", e);
    alert("保存失败");
  } finally {
    submitting.value = false;
  }
};

watch(
  () => form.value.days,
  (newVal) => {
    details.value.forEach((item) => {
      if (item.dayNum > newVal) {
        item.dayNum = newVal;
      }
    });
    syncEndDate();
  },
);

watch(
  () => form.value.startDate,
  () => {
    syncEndDate();
  },
);

watch(
  () => form.value.endDate,
  (newEndDate) => {
    if (syncingDate || !form.value.startDate || !newEndDate) return;

    const days = diffDays(form.value.startDate, newEndDate);
    if (days === null) return;

    syncingDate = true;
    if (days < 0) {
      form.value.endDate = form.value.startDate;
      form.value.days = 1;
    } else {
      form.value.days = Math.min(days + 1, 30);
      if (days + 1 > 30) {
        syncEndDate();
      }
    }
    syncingDate = false;
  },
);

onMounted(() => {
  loadTags().then(async () => {
    await loadTrip();
    initializeCreateDates();
  });
});
</script>

<style scoped>
.detail-list {
  background: #f8fafc;
  border-radius: 8px;
  padding: 16px;
}

.detail-item {
  background: #fff;
  border-radius: 6px;
  padding: 12px;
  margin-bottom: 8px;
  border: 1px solid #e2e8f0;
}

.detail-item:last-child {
  margin-bottom: 0;
}

.attraction-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  max-height: 200px;
  overflow-y: auto;
  z-index: 1000;
}

.attraction-dropdown .dropdown-item {
  padding: 8px 12px;
  cursor: pointer;
  border-bottom: 1px solid #f1f5f9;
}

.attraction-dropdown .dropdown-item:last-child {
  border-bottom: none;
}

.attraction-dropdown .dropdown-item:hover {
  background: #f8fafc;
}

.tag-options {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-option {
  border: 1px solid #dcdfe6;
  background: #fff;
  color: #607080;
  padding: 6px 12px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.tag-option:hover {
  border-color: #5a9bcf;
  color: #5a9bcf;
}

.tag-option.active {
  background: #5a9bcf;
  border-color: #5a9bcf;
  color: #fff;
}
</style>
