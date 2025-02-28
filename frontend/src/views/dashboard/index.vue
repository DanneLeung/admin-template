<template>
  <div class="dashboard-container">
    <el-row :gutter="20" class="mb-4">
      <el-col :span="6" v-for="card in cards" :key="card.title">
        <el-card class="dashboard-card" :body-style="{ padding: '20px' }">
          <div class="card-header">
            <el-icon class="card-icon" :class="card.type">
              <component :is="card.icon" />
            </el-icon>
            <span class="card-title">{{ card.title }}</span>
          </div>
          <div class="card-value">{{ card.value }}</div>
          <div class="card-footer">
            <span :class="['trend', card.trend > 0 ? 'up' : 'down']">
              {{ Math.abs(card.trend) }}%
              <el-icon><CaretTop v-if="card.trend > 0" /><CaretBottom v-else /></el-icon>
            </span>
            <span class="compared">vs last month</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="card-header-title">
              <span>Revenue Trend</span>
              <el-radio-group v-model="chartPeriod" size="small">
                <el-radio-button label="week">Week</el-radio-button>
                <el-radio-button label="month">Month</el-radio-button>
                <el-radio-button label="year">Year</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div class="chart-container">
            <v-chart class="chart" :option="revenueChartOption" autoresize />
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>
            <div class="card-header-title">Expense Distribution</div>
          </template>
          <div class="chart-container">
            <v-chart class="chart" :option="pieChartOption" autoresize />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, PieChart } from 'echarts/charts'
import {
  GridComponent,
  TooltipComponent,
  LegendComponent,
  TitleComponent
} from 'echarts/components'
import VChart from 'vue-echarts'
import {
  Money,
  ShoppingCart,
  User,
  Wallet,
  CaretTop,
  CaretBottom
} from '@element-plus/icons-vue'

// Register ECharts components
use([
  CanvasRenderer,
  LineChart,
  PieChart,
  GridComponent,
  TooltipComponent,
  LegendComponent,
  TitleComponent
])

// Dashboard cards data
const cards = [
  {
    title: 'Total Revenue',
    value: '$54,230',
    trend: 12.5,
    icon: Money,
    type: 'success'
  },
  {
    title: 'Total Orders',
    value: '1,423',
    trend: 5.8,
    icon: ShoppingCart,
    type: 'primary'
  },
  {
    title: 'Active Users',
    value: '3,242',
    trend: -2.3,
    icon: User,
    type: 'warning'
  },
  {
    title: 'Average Order',
    value: '$38.10',
    trend: 8.4,
    icon: Wallet,
    type: 'info'
  }
]

// Chart period selector
const chartPeriod = ref('month')

// Revenue chart options
const revenueChartOption = computed(() => ({
  tooltip: {
    trigger: 'axis'
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
  },
  yAxis: {
    type: 'value'
  },
  series: [
    {
      name: 'Revenue',
      type: 'line',
      smooth: true,
      data: [820, 932, 901, 934, 1290, 1330, 1320],
      areaStyle: {
        opacity: 0.3
      },
      lineStyle: {
        width: 3
      },
      itemStyle: {
        color: '#409EFF'
      }
    }
  ]
}))

// Pie chart options
const pieChartOption = computed(() => ({
  tooltip: {
    trigger: 'item'
  },
  legend: {
    orient: 'vertical',
    left: 'left'
  },
  series: [
    {
      name: 'Expenses',
      type: 'pie',
      radius: '70%',
      data: [
        { value: 1048, name: 'Marketing' },
        { value: 735, name: 'Operations' },
        { value: 580, name: 'Product' },
        { value: 484, name: 'Sales' },
        { value: 300, name: 'Others' }
      ],
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }
  ]
}))
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.dashboard-card {
  height: 100%;
  transition: all 0.3s;
}

.dashboard-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
}

.card-header {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.card-icon {
  font-size: 20px;
  margin-right: 8px;
}

.card-icon.success { color: #67C23A; }
.card-icon.primary { color: #409EFF; }
.card-icon.warning { color: #E6A23C; }
.card-icon.info { color: #909399; }

.card-title {
  font-size: 14px;
  color: #606266;
}

.card-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin: 8px 0;
}

.card-footer {
  display: flex;
  align-items: center;
  font-size: 12px;
}

.trend {
  display: flex;
  align-items: center;
  margin-right: 8px;
}

.trend.up { color: #67C23A; }
.trend.down { color: #F56C6C; }

.compared {
  color: #909399;
}

.card-header-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-container {
  height: 350px;
}

.chart {
  height: 100%;
}

.mb-4 {
  margin-bottom: 16px;
}
</style>