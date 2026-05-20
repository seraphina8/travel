<template>
  <div class="content-card">
    <div class="search-bar">
      <input
        type="text"
        class="form-control"
        v-model="searchUsername"
        placeholder="搜索用户名"
      />
      <button class="btn btn-primary" @click="loadData">
        <i class="bi bi-search"></i> 搜索
      </button>
      <button class="btn btn-success ms-auto" @click="openModal()">
        <i class="bi bi-plus"></i> 添加管理员
      </button>
    </div>

    <table class="table table-hover">
      <thead>
        <tr>
          <th>ID</th>
          <th>用户名</th>
          <th>权限</th>
          <th>手机号</th>
          <th>邮箱</th>
          <th>状态</th>
          <th>最后登录</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="admin in admins" :key="admin.id">
          <td>{{ admin.id }}</td>
          <td>{{ admin.username }}</td>
          <td>{{ admin.realName || "-" }}</td>
          <td>{{ admin.phone || "-" }}</td>
          <td>{{ admin.email || "-" }}</td>
          <td>
            <span
              class="status-badge"
              :class="admin.status === 1 ? 'active' : 'inactive'"
            >
              {{ admin.status === 1 ? "启用" : "禁用" }}
            </span>
          </td>
          <td>{{ formatDate(admin.lastLoginTime) }}</td>
          <td class="action-btns">
            <button class="btn btn-primary btn-sm" @click="openModal(admin)">
              编辑
            </button>
            <button
              class="btn btn-sm"
              :class="admin.status === 1 ? 'btn-warning' : 'btn-success'"
              @click="toggleStatus(admin)"
              :disabled="admin.id === 1"
            >
              {{ admin.status === 1 ? "禁用" : "启用" }}
            </button>
            <button
              class="btn btn-danger btn-sm"
              @click="handleDelete(admin.id)"
              :disabled="admin.id === 1"
            >
              删除
            </button>
          </td>
        </tr>
      </tbody>
    </table>

    <nav class="pagination">
      <ul class="pagination mb-0">
        <li class="page-item" :class="{ disabled: pageNum === 1 }">
          <a class="page-link" href="#" @click.prevent="changePage(pageNum - 1)"
            >上一页</a
          >
        </li>
        <li
          class="page-item"
          v-for="p in totalPages"
          :key="p"
          :class="{ active: p === pageNum }"
        >
          <a class="page-link" href="#" @click.prevent="changePage(p)">{{
            p
          }}</a>
        </li>
        <li class="page-item" :class="{ disabled: pageNum === totalPages }">
          <a class="page-link" href="#" @click.prevent="changePage(pageNum + 1)"
            >下一页</a
          >
        </li>
      </ul>
    </nav>
  </div>

  <!-- Modal -->
  <div class="modal fade" id="adminModal" tabindex="-1" ref="modalRef">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">
            {{ isEdit ? "编辑管理员" : "添加管理员" }}
          </h5>
          <button
            type="button"
            class="btn-close"
            data-bs-dismiss="modal"
          ></button>
        </div>
        <div class="modal-body">
          <div class="mb-3">
            <label class="form-label">用户名 *</label>
            <input
              type="text"
              class="form-control"
              v-model="form.username"
              :disabled="isEdit"
              required
            />
          </div>
          <div class="mb-3">
            <label class="form-label">{{
              isEdit ? "新密码（留空不修改）" : "密码 *"
            }}</label>
            <input
              type="password"
              class="form-control"
              v-model="form.password"
              :required="!isEdit"
            />
          </div>
          <div class="mb-3">
            <label class="form-label">真实姓名</label>
            <input type="text" class="form-control" v-model="form.realName" />
          </div>
          <div class="mb-3">
            <label class="form-label">手机号</label>
            <input type="text" class="form-control" v-model="form.phone" />
          </div>
          <div class="mb-3">
            <label class="form-label">邮箱</label>
            <input type="email" class="form-control" v-model="form.email" />
          </div>
        </div>
        <div class="modal-footer">
          <button
            type="button"
            class="btn btn-secondary"
            data-bs-dismiss="modal"
          >
            取消
          </button>
          <button type="button" class="btn btn-primary" @click="handleSubmit">
            保存
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { Modal } from "bootstrap";
import api from "../api";

const admins = ref([]);
const total = ref(0);
const pageNum = ref(1);
const pageSize = ref(10);
const searchUsername = ref("");
const modalRef = ref(null);
const isEdit = ref(false);
let modal = null;

const form = ref({
  id: null,
  username: "",
  password: "",
  realName: "",
  phone: "",
  email: "",
});

const totalPages = computed(() => Math.ceil(total.value / pageSize.value) || 1);

const loadData = async () => {
  const res = await api.getAdminList({
    pageNum: pageNum.value,
    pageSize: pageSize.value,
    username: searchUsername.value,
  });
  if (res.code === 200) {
    admins.value = res.data.records;
    total.value = res.data.total;
  }
};

const openModal = (admin = null) => {
  isEdit.value = !!admin;
  if (admin) {
    form.value = { ...admin, password: "" };
  } else {
    form.value = {
      id: null,
      username: "",
      password: "",
      realName: "",
      phone: "",
      email: "",
    };
  }
  if (!modal) {
    modal = new Modal(modalRef.value);
  }
  modal.show();
};

const handleSubmit = async () => {
  if (!form.value.username) {
    alert("请输入用户名");
    return;
  }
  if (!isEdit.value && !form.value.password) {
    alert("请输入密码");
    return;
  }
  let res;
  if (isEdit.value) {
    res = await api.updateAdmin(form.value.id, form.value);
  } else {
    res = await api.addAdmin(form.value);
  }
  if (res.code === 200) {
    modal.hide();
    loadData();
  } else {
    alert(res.message);
  }
};

const toggleStatus = async (admin) => {
  const newStatus = admin.status === 1 ? 0 : 1;
  const res = await api.updateAdminStatus(admin.id, newStatus);
  if (res.code === 200) {
    admin.status = newStatus;
  }
};

const handleDelete = async (id) => {
  if (confirm("确定要删除该管理员吗？")) {
    const res = await api.deleteAdmin(id);
    if (res.code === 200) {
      loadData();
    }
  }
};

const changePage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    pageNum.value = page;
    loadData();
  }
};

const formatDate = (dateStr) => {
  if (!dateStr) return "-";
  return new Date(dateStr).toLocaleString("zh-CN");
};

onMounted(() => loadData());
</script>
