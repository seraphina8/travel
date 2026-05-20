import axios from 'axios'
import { fixImageDeep, fixImageUrl } from '../utils/imageHelper'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

api.interceptors.response.use(
  response => fixImageDeep(response.data),
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default {
  // 管理员登录
  login(data) {
    return api.post('/admin/login', data)
  },
  
  // 管理员管理
  getAdminList(params) {
    return api.get('/admin/list', { params })
  },
  getAdmin(id) {
    return api.get(`/admin/${id}`)
  },
  addAdmin(data) {
    return api.post('/admin', data)
  },
  updateAdmin(id, data) {
    return api.put(`/admin/${id}`, data)
  },
  deleteAdmin(id) {
    return api.delete(`/admin/${id}`)
  },
  updateAdminStatus(id, status) {
    return api.put(`/admin/${id}/status`, null, { params: { status } })
  },
  
  // 用户管理
  getUserList(params) {
    return api.get('/user/list', { params })
  },
  updateUserStatus(id, status) {
    return api.put(`/user/${id}/status`, null, { params: { status } })
  },
  deleteUser(id) {
    return api.delete(`/user/${id}`)
  },
  
  // 景区管理
  getScenicList(params) {
    return api.get('/scenic', { params })
  },
  getScenic(id) {
    return api.get(`/scenic/${id}`)
  },
  addScenic(data) {
    return api.post('/scenic', data)
  },
  updateScenic(id, data) {
    return api.put(`/scenic/${id}`, data)
  },
  deleteScenic(id) {
    return api.delete(`/scenic/${id}`)
  },
  updateScenicStatus(id, status) {
    return api.put(`/scenic/${id}/status`, null, { params: { status } })
  },
  
  // 景点管理
  getAttractionList(params) {
    return api.get('/attraction', { params })
  },
  getAttraction(id) {
    return api.get(`/attraction/${id}`)
  },
  addAttraction(data) {
    return api.post('/attraction', data)
  },
  updateAttraction(id, data) {
    return api.put(`/attraction/${id}`, data)
  },
  deleteAttraction(id) {
    return api.delete(`/attraction/${id}`)
  },
  
  // 订单管理
  getOrderList(params) {
    return api.get('/order', { params })
  },
  getOrder(id) {
    return api.get(`/order/${id}`)
  },
  updateOrderStatus(id, status) {
    return api.put(`/order/${id}/status`, null, { params: { status } })
  },
  deleteOrder(id) {
    return api.delete(`/order/${id}`)
  },
  
  // 门票管理
  getTicketList(params) {
    return api.get('/ticket', { params })
  },
  getTicketsByScenic(scenicId) {
    return api.get(`/ticket/scenic/${scenicId}`)
  },
  addTicket(data) {
    return api.post('/ticket', data)
  },
  updateTicket(id, data) {
    return api.put(`/ticket/${id}`, data)
  },
  deleteTicket(id) {
    return api.delete(`/ticket/${id}`)
  },
  updateTicketStatus(id, status) {
    return api.put(`/ticket/${id}/status`, null, { params: { status } })
  },
  
  // 攻略管理
  getStrategyList(params) {
    return api.get('/strategy', { params })
  },
  getStrategy(id) {
    return api.get(`/strategy/${id}`)
  },
  updateStrategyStatus(id, status) {
    return api.put(`/strategy/${id}/status`, null, { params: { status } })
  },
  deleteStrategy(id) {
    return api.delete(`/strategy/${id}`)
  },
  
  // 反馈管理
  getFeedbackList(params) {
    return api.get('/feedback', { params })
  },
  getFeedback(id) {
    return api.get(`/feedback/${id}`)
  },
  replyFeedback(id, reply) {
    return api.post(`/feedback/${id}/reply`, { reply })
  },
  deleteFeedback(id) {
    return api.delete(`/feedback/${id}`)
  },
  
  // 文件上传
  uploadFile(file) {
    const formData = new FormData()
    formData.append('file', file)
    return api.post('/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    }).then(res => {
      if (res?.code === 200 && res.data) {
        res.data = fixImageUrl(res.data)
      }
      return res
    })
  },
  
  // 轮播图管理
  getBannerList(params) {
    return api.get('/banner', { params })
  },
  getBanner(id) {
    return api.get(`/banner/${id}`)
  },
  addBanner(data) {
    return api.post('/banner', data)
  },
  updateBanner(id, data) {
    return api.put(`/banner/${id}`, data)
  },
  deleteBanner(id) {
    return api.delete(`/banner/${id}`)
  },
  updateBannerStatus(id, status) {
    return api.put(`/banner/${id}/status`, null, { params: { status } })
  },
  
  // 美食管理
  getFoodList(params) {
    return api.get('/food', { params })
  },
  getFood(id) {
    return api.get(`/food/${id}`)
  },
  addFood(data) {
    return api.post('/food', data)
  },
  updateFood(id, data) {
    return api.put(`/food/${id}`, data)
  },
  deleteFood(id) {
    return api.delete(`/food/${id}`)
  },
  auditFood(id, status) {
    return api.put(`/food/${id}/status`, null, { params: { status } })
  },
  
  // 统计
  getTagList(params) {
    return api.get('/tag', { params })
  },
  addTag(data) {
    return api.post('/tag', data)
  },
  updateTag(id, data) {
    return api.put(`/tag/${id}`, data)
  },
  deleteTag(id) {
    return api.delete(`/tag/${id}`)
  },
  updateTagStatus(id, status) {
    return api.put(`/tag/${id}/status`, null, { params: { status } })
  },

  getStatistics() {
    return api.get('/admin/stats/overview')
  },
  getAttractionStats() {
    return api.get('/admin/stats/attraction')
  },
  getOrderStats() {
    return api.get('/admin/stats/order')
  },
  getTrendStats() {
    return api.get('/admin/stats/trend')
  }
}
