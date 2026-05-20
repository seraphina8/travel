<template>
  <div class="trip-page">
    <!-- 顶部导航栏 -->
    <div class="header">
      <div class="header-content">
        <h1><i class="bi bi-map"></i> AI 行程规划结果</h1>
      </div>
    </div>

    <!-- 主体内容 -->
    <div class="main-container">
      <div class="trip-card">
        <!-- 行程标题 -->
        <div class="trip-title">
          <span class="dest">{{ dest }}</span>
          <span class="days">{{ days }}天行程</span>
        </div>

        <!-- 行程内容（结构化渲染，不再用v-html） -->
        <div class="trip-content">
          <div v-for="(day, dayIndex) in parsedTrip" :key="dayIndex" class="day-section">
            <div class="day-header">
              <span class="day-tag">第 {{ day.dayNum }} 天</span>
            </div>

            <div v-for="(item, itemIndex) in day.items" :key="itemIndex" class="time-item">
              <!-- 时间栏 -->
              <div class="time-bar">
                <span class="time">{{ item.time }}</span>
                <span class="time-line"></span>
              </div>

              <!-- 内容卡片 -->
              <div class="item-card">
                <h3 class="item-title">{{ item.title }}</h3>
                <div v-if="item.recommend" class="item-row recommend">
                  <i class="bi bi-star-fill"></i>
                  <span>{{ item.recommend }}</span>
                </div>
                <div v-if="item.address" class="item-row address">
                  <i class="bi bi-geo-alt-fill"></i>
                  <span>{{ item.address }}</span>
                </div>
                <div v-if="item.highlight" class="item-row highlight">
                  <i class="bi bi-check2-square"></i>
                  <span>{{ item.highlight }}</span>
                </div>
                <div v-if="item.tip" class="item-row tip">
                  <i class="bi bi-lightbulb-fill"></i>
                  <span>{{ item.tip }}</span>
                </div>
                <div v-if="item.transport" class="item-row transport">
                  <i class="bi bi-bus-front-fill"></i>
                  <span>{{ item.transport }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="action-bar">
          <button class="btn-save" @click="saveTrip" :disabled="saving">
            <i class="bi" :class="saving ? 'bi-hourglass-split' : 'bi-folder-plus'"></i>
            {{ saving ? '保存中...' : '保存到我的行程' }}
          </button>
          <button class="btn-back" @click="back">返回</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '../api'

const route = useRoute()
const router = useRouter()

const rawContent = ref('')
const dest = ref('')
const days = ref('')
const startDate = ref('')
const saving = ref(false)
const isPublic = ref(0)

/**
 * 清理普通文本
 * 注意：不能全局删除“-”，否则 08:30-10:00 会变成 08:3010:00
 */
const cleanText = (text) => {
  if (!text) return ''

  return String(text)
    .replace(/\*\*/g, '')
    .replace(/#/g, '')
    .replace(/^[\s\-•、]+/, '') // 只删除行首符号，不删除时间中间的 -
    .replace(/\s+/g, ' ')
    .trim()
}

/**
 * 修复时间格式
 * 兼容 08:3010:00 这种旧格式
 */
const normalizeTime = (time) => {
  if (!time) return ''

  const t = cleanText(time).replace(/[:：]/g, ':')

  // 08:3010:00 -> 08:30-10:00
  const match = t.match(/^(\d{1,2}:\d{2})(\d{1,2}:\d{2})$/)
  if (match) {
    return `${match[1]}-${match[2]}`
  }

  return t
}

/**
 * 判断是否是说明字段
 * 这些字段不能作为景点名称保存
 */
const isLabelLine = (text) => {
  const value = cleanText(text)
  return /^(时间|推荐|地址|亮点|交通|贴士|建议|备注|餐饮|住宿|费用)[:：]/.test(value)
}

/**
 * 提取“推荐：xxx”“地址：xxx”这类字段值
 */
const getFieldValue = (line, fieldName) => {
  const reg = new RegExp(`${fieldName}[:：]([^；;\\n]+)`)
  const match = line.match(reg)
  return match ? cleanText(match[1]) : ''
}

/**
 * 解析“时间 + 景点名称”
 */
const parseTimeAndTitle = (line) => {
  const text = cleanText(line)

  if (!text || isLabelLine(text)) {
    return null
  }

  // 08:30-10:00 武侯祠
  let match = text.match(/^(\d{1,2}[:：]\d{2}\s*[-—~至到]\s*\d{1,2}[:：]\d{2})\s+(.+)$/)
  if (match) {
    return {
      time: normalizeTime(match[1]),
      title: cleanText(match[2])
    }
  }

  // 兼容 08:3010:00 武侯祠
  match = text.match(/^(\d{1,2}[:：]\d{2})(\d{1,2}[:：]\d{2})\s+(.+)$/)
  if (match) {
    return {
      time: `${normalizeTime(match[1])}-${normalizeTime(match[2])}`,
      title: cleanText(match[3])
    }
  }

  // 上午 武侯祠 / 下午 锦里古街
  match = text.match(/^(早上|上午|中午|下午|傍晚|晚上|午餐|晚餐)\s+(.+)$/)
  if (match) {
    return {
      time: cleanText(match[1]),
      title: cleanText(match[2])
    }
  }

  // 1. 武侯祠 / 1、武侯祠
  match = text.match(/^\d+[\.、]\s*(.+)$/)
  if (match) {
    return {
      time: '',
      title: cleanText(match[1])
    }
  }

  // 普通景点名
  if (text.length >= 2 && text.length <= 25) {
    return {
      time: '',
      title: text
    }
  }

  return null
}

/**
 * 按“第1天、第2天”分割内容
 */
const splitByDay = (content) => {
  const text = content || ''
  const dayPattern = /第\s*(\d+|一|二|三|四|五|六|七|八|九|十)\s*天/g
  const blocks = []

  let match
  let lastIndex = 0

  while ((match = dayPattern.exec(text)) !== null) {
    if (match.index > lastIndex) {
      const block = text.slice(lastIndex, match.index).trim()
      if (block) {
        blocks.push(block)
      }
    }

    lastIndex = match.index + match[0].length
  }

  const lastBlock = text.slice(lastIndex).trim()
  if (lastBlock) {
    blocks.push(lastBlock)
  }

  return blocks.length > 0 ? blocks : [text]
}

/**
 * 解析某一天的内容
 */
const parseDayContent = (dayContent) => {
  const lines = dayContent
    .split('\n')
    .map(line => cleanText(line))
    .filter(Boolean)

  const items = []
  let currentItem = null

  lines.forEach(line => {
    // 跳过“第1天”这种标题
    if (/^第\s*(\d+|一|二|三|四|五|六|七|八|九|十)\s*天/.test(line)) {
      return
    }

    const titleInfo = parseTimeAndTitle(line)

    // 新的行程点
    if (titleInfo) {
      if (currentItem) {
        items.push(currentItem)
      }

      currentItem = {
        time: titleInfo.time,
        title: titleInfo.title,
        recommend: '',
        address: '',
        highlight: '',
        transport: '',
        tip: ''
      }

      return
    }

    // 如果还没有行程点，说明这一行不是有效内容
    if (!currentItem) {
      return
    }

    if (line.startsWith('时间：') || line.startsWith('时间:')) {
      currentItem.time = normalizeTime(getFieldValue(line, '时间') || currentItem.time)
    }

    if (line.startsWith('推荐：') || line.startsWith('推荐:')) {
      currentItem.recommend = getFieldValue(line, '推荐')
    }

    if (line.startsWith('地址：') || line.startsWith('地址:')) {
      currentItem.address = getFieldValue(line, '地址')
    }

    if (line.startsWith('亮点：') || line.startsWith('亮点:')) {
      currentItem.highlight = getFieldValue(line, '亮点')
    }

    if (line.startsWith('交通：') || line.startsWith('交通:')) {
      currentItem.transport = getFieldValue(line, '交通')
    }

    if (
      line.startsWith('贴士：') ||
      line.startsWith('贴士:') ||
      line.startsWith('建议：') ||
      line.startsWith('建议:')
    ) {
      currentItem.tip = getFieldValue(line, '贴士') || getFieldValue(line, '建议')
    }
  })

  if (currentItem) {
    items.push(currentItem)
  }

  return items.filter(item => {
    const title = cleanText(item.title)
    return title && !isLabelLine(title) && title.length >= 2
  })
}

/**
 * 页面展示用的结构化行程
 */
const parsedTrip = computed(() => {
  if (!rawContent.value) {
    return []
  }

  const dayBlocks = splitByDay(rawContent.value)

  return dayBlocks
    .map((block, index) => ({
      dayNum: index + 1,
      items: parseDayContent(block)
    }))
    .filter(day => day.items.length > 0)
})

/**
 * 拼接保存到数据库的说明字段
 */
const buildDescription = (item) => {
  const parts = []

  if (item.time) {
    parts.push(`时间：${normalizeTime(item.time)}`)
  }

  if (item.recommend) {
    parts.push(`推荐：${cleanText(item.recommend)}`)
  }

  if (item.address) {
    parts.push(`地址：${cleanText(item.address)}`)
  }

  if (item.highlight) {
    parts.push(`亮点：${cleanText(item.highlight)}`)
  }

  if (item.transport) {
    parts.push(`交通：${cleanText(item.transport)}`)
  }

  if (item.tip) {
    parts.push(`贴士：${cleanText(item.tip)}`)
  }

  return parts.join('；')
}

/**
 * 转换为后端 trip_detail 表需要的数据
 */
const parseTripContent = () => {
  const details = []
  const seen = new Set()

  parsedTrip.value.forEach(day => {
    day.items.forEach((item, index) => {
      let name = cleanText(item.title)
        .replace(/^[0-9]+[\.、]/, '')
        .replace(/^(景点|游览|打卡|活动|美食|餐厅)[:：]?/, '')
        .trim()

      if (!name || isLabelLine(name)) {
        return
      }

      if (name.length < 2) {
        return
      }

      if (name.length > 25) {
        name = name.substring(0, 25)
      }

      const key = `${day.dayNum}_${name}`
      if (seen.has(key)) {
        return
      }

      seen.add(key)

      details.push({
        dayNum: day.dayNum,
        attractionId: null,
        attractionName: name,
        description: buildDescription(item),
        sortOrder: index
      })
    })
  })

  return details
}

/**
 * 保存 AI 行程
 */
const saveTrip = async () => {
  if (saving.value) {
    return
  }

  if (!startDate.value) {
    alert('请选择出发日期')
    return
  }

  const details = parseTripContent()

  if (details.length === 0) {
    alert('未识别到有效行程，请重新生成后再保存')
    return
  }

  saving.value = true

  try {
    const sd = new Date(startDate.value)
    const ed = new Date(sd)
    ed.setDate(sd.getDate() + parseInt(days.value) - 1)

    const tripData = {
      title: `${dest.value} ${days.value}天AI行程`,
      description: `共${details.length}个行程点`,
      days: parseInt(days.value),
      startDate: startDate.value,
      endDate: ed.toISOString().split('T')[0],
      status: 1,
      isPublic: Number(isPublic.value)
    }

    const res = await api.createTrip(tripData)

    const tripId = typeof res.data === 'object'
      ? res.data.id
      : res.data || res.id

    if (!tripId) {
      throw new Error('未获取到行程ID')
    }

    await api.saveAiTripDetails(tripId, details)

    alert('保存成功')
    router.push('/trip')
  } catch (e) {
    console.error(e)
    alert('保存失败：' + (e.message || '请稍后重试'))
  } finally {
    saving.value = false
  }
}

const back = () => {
  router.back()
}

onMounted(() => {
  rawContent.value = route.query.content || ''
  dest.value = route.query.dest || ''
  days.value = route.query.days || ''
  startDate.value = route.query.startDate || ''
  isPublic.value = Number(route.query.isPublic || 0)
})
</script>

<style scoped>
/* 全局页面 */
.trip-page {
  min-height: 100vh;
  background-color: #f5f7fa;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

/* 顶部标题栏 */
.header {
  background-color: #4080ff;
  color: #fff;
  padding: 40px 0;
}

.header-content {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 20px;
}

.header h1 {
  font-size: 24px;
  font-weight: 500;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 主体容器 */
.main-container {
  max-width: 1000px;
  margin: 30px auto;
  padding: 0 20px;
}

/* 行程卡片 */
.trip-card {
  background: #fff;
  border-radius: 4px;
  padding: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

/* 行程标题 */
.trip-title {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 30px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e8e8e8;
}

.dest {
  font-size: 20px;
  font-weight: 600;
  color: #4080ff;
}

.days {
  font-size: 16px;
  color: #666;
}

/* 行程内容 */
.trip-content {
  margin-bottom: 30px;
}

/* 天区块 */
.day-section {
  margin-bottom: 30px;
}

.day-header {
  margin-bottom: 15px;
}

.day-tag {
  display: inline-block;
  background-color: #4080ff;
  color: #fff;
  padding: 4px 12px;
  border-radius: 2px;
  font-size: 14px;
  font-weight: 500;
}

/* 时间项 */
.time-item {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
}

.time-bar {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 5px;
}

.time {
  font-size: 14px;
  font-weight: 500;
  color: #4080ff;
  white-space: nowrap;
}

.time-line {
  width: 2px;
  flex: 1;
  background-color: #e8e8e8;
  margin-top: 5px;
}

/* 内容卡片 */
.item-card {
  flex: 1;
  background-color: #fafafa;
  border-radius: 4px;
  padding: 15px 20px;
  border-left: 3px solid #4080ff;
}

.item-title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin: 0 0 10px 0;
}

.item-row {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  font-size: 14px;
  color: #666;
  margin-bottom: 6px;
  line-height: 1.6;
}

.item-row i {
  color: #4080ff;
  font-size: 14px;
  margin-top: 2px;
  flex-shrink: 0;
}

/* 按钮栏 */
.action-bar {
  display: flex;
  gap: 12px;
  padding-top: 20px;
  border-top: 1px solid #e8e8e8;
}

.btn-save {
  background-color: #4080ff;
  color: #fff;
  border: none;
  padding: 10px 24px;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
  display: flex;
  align-items: center;
  gap: 6px;
}

.btn-save:hover {
  background-color: #3070e6;
}

.btn-save:disabled {
  background-color: #94b8f8;
  cursor: not-allowed;
}

.btn-back {
  background-color: #fff;
  color: #666;
  border: 1px solid #d9d9d9;
  padding: 10px 24px;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-back:hover {
  background-color: #f5f5f5;
}
</style>