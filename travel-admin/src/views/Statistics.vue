<template>
  <div class="statistics-page">
    <div class="statistics-header">
      <div>
        <h4>数据统计</h4>
        <p>统计平台用户、景区、订单、销售额和热门景点情况</p>
      </div>
    </div>

    <div class="stat-grid">
      <div class="stat-card blue">
        <div class="stat-icon"><i class="bi bi-people"></i></div>
        <div class="stat-info">
          <h3>{{ formatNumber(stats.userCount) }}</h3>
          <p>用户总数</p>
        </div>
      </div>

      <div class="stat-card green">
        <div class="stat-icon"><i class="bi bi-geo-alt"></i></div>
        <div class="stat-info">
          <h3>{{ formatNumber(stats.scenicCount) }}</h3>
          <p>景区总数</p>
        </div>
      </div>

      <div class="stat-card orange">
        <div class="stat-icon"><i class="bi bi-receipt"></i></div>
        <div class="stat-info">
          <h3>{{ formatNumber(stats.orderCount) }}</h3>
          <p>订单总数</p>
        </div>
      </div>

      <div class="stat-card red">
        <div class="stat-icon"><i class="bi bi-currency-yen"></i></div>
        <div class="stat-info">
          <h3>¥{{ formatMoney(stats.totalAmount) }}</h3>
          <p>销售总额</p>
        </div>
      </div>
    </div>

    <div class="chart-grid">
      <div class="chart-card">
        <div class="chart-title">
          <h5>热门景点TOP10</h5>
          <p>按浏览量统计景点热度</p>
        </div>
        <div ref="attractionChartRef" class="chart-container"></div>
      </div>

      <div class="chart-card">
        <div class="chart-title">
          <h5>订单状态分布</h5>
          <p>按订单状态统计数量</p>
        </div>
        <div ref="orderChartRef" class="chart-container"></div>
      </div>
    </div>

    <div class="chart-card full">
      <div class="chart-title">
        <h5>近30天订单趋势</h5>
        <p>统计每日订单数量与销售金额</p>
      </div>
      <div ref="trendChartRef" class="trend-container"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from "vue";
import * as echarts from "echarts";
import api from "../api";

const attractionChartRef = ref(null);
const orderChartRef = ref(null);
const trendChartRef = ref(null);

let attractionChart = null;
let orderChart = null;
let trendChart = null;

const stats = ref({
  userCount: 0,
  scenicCount: 0,
  orderCount: 0,
  totalAmount: 0,
});

const orderStats = ref({
  pending: 0,
  paid: 0,
  used: 0,
  cancelled: 0,
});

const formatNumber = (value) => {
  return Number(value || 0).toLocaleString();
};

const formatMoney = (value) => {
  return Number(value || 0).toFixed(2);
};

const loadStats = async () => {
  const res = await api.getStatistics();

  if (res.code === 200 && res.data) {
    stats.value = {
      userCount: res.data.userCount || 0,
      scenicCount: res.data.scenicCount || 0,
      orderCount: res.data.orderCount || 0,
      totalAmount: res.data.totalAmount || 0,
    };
  }
};

const initAttractionChart = async () => {
  const res = await api.getAttractionStats();
  const data = res.code === 200 ? res.data || [] : [];

  await nextTick();

  attractionChart = echarts.init(attractionChartRef.value);

  attractionChart.setOption({
    tooltip: {
      trigger: "axis",
    },
    grid: {
      top: 35,
      left: 55,
      right: 20,
      bottom: 85,
      containLabel: true,
    },
    xAxis: {
      type: "category",
      data: data.map((item) => item.name),
      axisTick: {
        show: false,
      },
      axisLabel: {
        interval: 0,
        rotate: 38,
        fontSize: 11,
        color: "#64748b",
      },
      axisLine: {
        lineStyle: {
          color: "#dbe3ec",
        },
      },
    },
    yAxis: {
      type: "value",
      name: "浏览量",
      minInterval: 1,
      nameTextStyle: {
        color: "#64748b",
        fontSize: 12,
      },
      axisLabel: {
        color: "#64748b",
      },
      splitLine: {
        lineStyle: {
          color: "#edf2f7",
          type: "dashed",
        },
      },
    },
    series: [
      {
        name: "浏览量",
        type: "bar",
        barWidth: 18,
        data: data.map((item) => item.viewCount || 0),
        itemStyle: {
          color: "#5a9bcf",
          borderRadius: [6, 6, 0, 0],
        },
      },
    ],
  });
};

const initOrderChart = async () => {
  const res = await api.getOrderStats();
  const data = res.code === 200 && res.data ? res.data : {};

  orderStats.value = {
    pending: data.pending || 0,
    paid: data.paid || 0,
    used: data.used || 0,
    cancelled: data.cancelled || 0,
  };

  await nextTick();

  orderChart = echarts.init(orderChartRef.value);

  orderChart.setOption({
    color: ["#f59e0b", "#2563eb", "#22c55e", "#ef4444"],
    tooltip: {
      trigger: "item",
    },
    legend: {
      bottom: 0,
      itemWidth: 12,
      itemHeight: 12,
    },
    series: [
      {
        name: "订单状态",
        type: "pie",
        radius: ["42%", "68%"],
        center: ["50%", "43%"],
        label: {
          formatter: "{b}: {c}",
          color: "#475569",
        },
        data: [
          { value: orderStats.value.pending, name: "待支付" },
          { value: orderStats.value.paid, name: "已支付" },
          { value: orderStats.value.used, name: "已使用" },
          { value: orderStats.value.cancelled, name: "已取消" },
        ],
      },
    ],
  });
};

const initTrendChart = async () => {
  const res = await api.getTrendStats();
  const data =
    res.code === 200 && res.data
      ? res.data
      : { dates: [], orderCounts: [], amounts: [] };

  await nextTick();

  trendChart = echarts.init(trendChartRef.value);

  trendChart.setOption({
    tooltip: {
      trigger: "axis",
    },
    legend: {
      top: 0,
      data: ["订单数", "销售额"],
    },
    grid: {
      top: 48,
      left: 55,
      right: 55,
      bottom: 35,
      containLabel: true,
    },
    xAxis: {
      type: "category",
      data: data.dates || [],
      axisTick: {
        show: false,
      },
      axisLabel: {
        color: "#64748b",
      },
      axisLine: {
        lineStyle: {
          color: "#dbe3ec",
        },
      },
    },
    yAxis: [
      {
        type: "value",
        name: "订单数",
        minInterval: 1,
        axisLabel: {
          color: "#64748b",
        },
        splitLine: {
          lineStyle: {
            color: "#edf2f7",
            type: "dashed",
          },
        },
      },
      {
        type: "value",
        name: "销售额",
        axisLabel: {
          color: "#64748b",
        },
        splitLine: {
          show: false,
        },
      },
    ],
    series: [
      {
        name: "订单数",
        type: "bar",
        barWidth: 14,
        data: data.orderCounts || [],
        itemStyle: {
          color: "#5a9bcf",
          borderRadius: [6, 6, 0, 0],
        },
      },
      {
        name: "销售额",
        type: "line",
        yAxisIndex: 1,
        smooth: true,
        data: data.amounts || [],
        lineStyle: {
          color: "#f59e0b",
          width: 2,
        },
        itemStyle: {
          color: "#f59e0b",
        },
        symbol: "circle",
        symbolSize: 6,
      },
    ],
  });
};

const resizeCharts = () => {
  attractionChart?.resize();
  orderChart?.resize();
  trendChart?.resize();
};

onMounted(async () => {
  await loadStats();
  await initAttractionChart();
  await initOrderChart();
  await initTrendChart();
  window.addEventListener("resize", resizeCharts);
});

onBeforeUnmount(() => {
  window.removeEventListener("resize", resizeCharts);
  attractionChart?.dispose();
  orderChart?.dispose();
  trendChart?.dispose();
});
</script>

<style scoped>
.statistics-page {
  width: 100%;
  overflow-x: hidden;
}

.statistics-header {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  padding: 22px 24px;
  margin-bottom: 20px;
}

.statistics-header h4 {
  margin: 0 0 6px;
  font-size: 22px;
  font-weight: 700;
  color: #1f2937;
}

.statistics-header p {
  margin: 0;
  font-size: 14px;
  color: #64748b;
}

.stat-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 18px;
  margin-bottom: 18px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
  color: #ffffff;
  border-radius: 10px;
}

.stat-card.blue {
  background: #5a9bcf;
}

.stat-card.green {
  background: #22c55e;
}

.stat-card.orange {
  background: #f59e0b;
}

.stat-card.red {
  background: #ef4444;
}

.stat-icon {
  width: 52px;
  height: 52px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.18);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  flex-shrink: 0;
}

.stat-icon i {
  font-size: 26px;
}

.stat-info h3 {
  margin: 0;
  font-size: 28px;
  font-weight: 700;
}

.stat-info p {
  margin: 4px 0 0;
  font-size: 14px;
  opacity: 0.9;
}

.chart-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 18px;
  margin-bottom: 18px;
}

.chart-card {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  padding: 20px;
  min-width: 0;
  border-radius: 10px;
}

.chart-card.full {
  margin-top: 0;
}

.chart-title {
  margin-bottom: 12px;
}

.chart-title h5 {
  margin: 0 0 4px;
  font-size: 16px;
  font-weight: 700;
  color: #1f2937;
}

.chart-title p {
  margin: 0;
  font-size: 13px;
  color: #94a3b8;
}

.chart-container {
  width: 100%;
  height: 320px;
}

.trend-container {
  width: 100%;
  height: 340px;
}

@media (max-width: 1200px) {
  .stat-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .chart-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .stat-grid {
    grid-template-columns: 1fr;
  }
}
</style>
