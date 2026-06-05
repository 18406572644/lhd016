<template>
  <div class="dashboard">
    <div class="stats-cards">
      <el-row :gutter="20">
        <el-col :span="4.8" v-for="(card, index) in statCards" :key="index" class="fade-in" :style="{ animationDelay: `${index * 0.1}s` }">
          <el-card class="stat-card" :body-style="{ padding: '20px' }">
            <div class="card-content">
              <div class="card-icon" :style="{ background: card.bgColor }">
                <i :class="card.icon"></i>
              </div>
              <div class="card-info">
                <div class="card-label">{{ card.label }}</div>
                <div class="card-value" :style="{ color: card.color }">{{ card.value }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card class="chart-card">
          <div slot="header" class="card-header">
            <span><i class="el-icon-warning"></i> 库存预警提醒</span>
            <el-tag type="danger" v-if="stats.lowStockCount > 0">{{ stats.lowStockCount }} 项预警</el-tag>
          </div>
          <div class="warning-list">
            <div
              v-for="item in stockWarningList"
              :key="item.id"
              class="warning-item pulse-animation"
            >
              <div class="warning-icon">
                <i class="el-icon-alarm-clock"></i>
              </div>
              <div class="warning-content">
                <div class="warning-title">{{ item.partName }}</div>
                <div class="warning-desc">
                  当前库存：<span class="danger-text">{{ item.stockQuantity }}{{ item.unit }}</span>
                  ，预警阈值：{{ item.warningThreshold }}{{ item.unit }}
                </div>
              </div>
              <el-tag type="danger" size="small">低库存</el-tag>
            </div>
            <el-empty v-if="stockWarningList.length === 0" description="暂无库存预警" :image-size="80"></el-empty>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card class="chart-card">
          <div slot="header" class="card-header">
            <span><i class="el-icon-bell"></i> 待处理求助</span>
            <el-tag type="warning" v-if="stats.pendingHelpCount > 0">{{ stats.pendingHelpCount }} 条待处理</el-tag>
          </div>
          <div class="help-list">
            <div
              v-for="item in pendingHelpList"
              :key="item.id"
              class="help-item"
            >
              <div class="help-urgency" :class="`urgency-${item.urgency}`">
                {{ item.urgency }}
              </div>
              <div class="help-content">
                <div class="help-title">
                  <span class="help-type">{{ item.helpType }}</span>
                  <span class="help-user">{{ item.requesterName }}</span>
                </div>
                <div class="help-location">
                  <i class="el-icon-location-outline"></i> {{ item.location }}
                </div>
                <div class="help-desc">{{ item.description }}</div>
              </div>
              <div class="help-actions">
                <el-button type="primary" size="small" @click="goToHelp">处理</el-button>
              </div>
            </div>
            <el-empty v-if="pendingHelpList.length === 0" description="暂无待处理求助" :image-size="80"></el-empty>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card class="chart-card">
          <div slot="header" class="card-header">
            <span><i class="el-icon-pie-chart"></i> 库存分类统计</span>
          </div>
          <div ref="categoryChart" class="chart-container"></div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card class="chart-card">
          <div slot="header" class="card-header">
            <span><i class="el-icon-data-line"></i> 近7天求助趋势</span>
          </div>
          <div ref="trendChart" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row style="margin-top: 20px;">
      <el-col :span="24">
        <el-card>
          <div slot="header" class="card-header">
            <span><i class="el-icon-menu"></i> 快捷入口</span>
          </div>
          <div class="quick-entries">
            <div
              v-for="(entry, index) in quickEntries"
              :key="index"
              class="quick-entry"
              @click="$router.push(entry.path)"
            >
              <div class="entry-icon" :style="{ background: entry.bgColor }">
                <i :class="entry.icon"></i>
              </div>
              <div class="entry-label">{{ entry.label }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getDashboardStats, getCategoryStats, getHelpTrendStats } from '@/api/dashboard'
import { formatDate } from '@/utils'

export default {
  name: 'Dashboard',
  data() {
    return {
      stats: {
        supplyPointCount: 0,
        repairShopCount: 0,
        lowStockCount: 0,
        pendingHelpCount: 0,
        todayCheckCount: 0
      },
      stockWarningList: [],
      pendingHelpList: [],
      categoryStats: [],
      helpTrendStats: [],
      categoryChart: null,
      trendChart: null,
      quickEntries: [
        { label: '补给点管理', icon: 'el-icon-location', path: '/supply', bgColor: 'linear-gradient(135deg, #4CAF90, #81C7B7)' },
        { label: '维修点档案', icon: 'el-icon-service', path: '/repair', bgColor: 'linear-gradient(135deg, #87CEEB, #B3E5FC)' },
        { label: '配件库存', icon: 'el-icon-goods', path: '/inventory', bgColor: 'linear-gradient(135deg, #FF9800, #FFB74D)' },
        { label: '求助登记', icon: 'el-icon-phone-outline', path: '/help', bgColor: 'linear-gradient(135deg, #F44336, #EF9A9A)' },
        { label: '物资盘点', icon: 'el-icon-document', path: '/inventory-check', bgColor: 'linear-gradient(135deg, #9C27B0, #CE93D8)' }
      ]
    }
  },
  computed: {
    statCards() {
      return [
        { label: '补给点总数', value: this.stats.supplyPointCount, icon: 'el-icon-location', color: '#4CAF90', bgColor: 'linear-gradient(135deg, #4CAF90, #81C7B7)' },
        { label: '维修点总数', value: this.stats.repairShopCount, icon: 'el-icon-service', color: '#2196F3', bgColor: 'linear-gradient(135deg, #87CEEB, #B3E5FC)' },
        { label: '库存预警', value: this.stats.lowStockCount, icon: 'el-icon-warning', color: '#F44336', bgColor: 'linear-gradient(135deg, #FF9800, #FFB74D)' },
        { label: '待处理求助', value: this.stats.pendingHelpCount, icon: 'el-icon-bell', color: '#FF9800', bgColor: 'linear-gradient(135deg, #F44336, #EF9A9A)' },
        { label: '今日盘点', value: this.stats.todayCheckCount, icon: 'el-icon-document', color: '#9C27B0', bgColor: 'linear-gradient(135deg, #9C27B0, #CE93D8)' }
      ]
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    async loadData() {
      try {
        const [statsRes, categoryRes, trendRes] = await Promise.all([
          getDashboardStats(),
          getCategoryStats(),
          getHelpTrendStats()
        ])
        const statsData = statsRes.data
        this.stats = {
          supplyPointCount: statsData.supplyPointCount,
          repairShopCount: statsData.repairShopCount,
          lowStockCount: statsData.lowStockCount,
          pendingHelpCount: statsData.pendingHelpCount,
          todayCheckCount: statsData.todayCheckCount
        }
        this.stockWarningList = statsData.stockWarningList || []
        this.pendingHelpList = statsData.pendingHelpList || []
        this.categoryStats = categoryRes.data || []
        this.helpTrendStats = trendRes.data || []
        this.$nextTick(() => {
          this.initCategoryChart()
          this.initTrendChart()
        })
      } catch (error) {
        this.$message.error('加载仪表盘数据失败')
      }
    },
    initCategoryChart() {
      const chartDom = this.$refs.categoryChart
      this.categoryChart = this.$echarts.init(chartDom)
      const option = {
        tooltip: { trigger: 'item' },
        legend: { orient: 'vertical', left: 'left', top: 'center' },
        color: ['#4CAF90', '#87CEEB', '#FF9800', '#9C27B0', '#E91E63', '#00BCD4', '#FFEB3B'],
        series: [
          {
            name: '库存分类',
            type: 'pie',
            radius: ['40%', '70%'],
            center: ['65%', '50%'],
            avoidLabelOverlap: false,
            itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 2 },
            label: { show: false, position: 'center' },
            emphasis: {
              label: { show: true, fontSize: 16, fontWeight: 'bold' }
            },
            data: this.categoryStats
          }
        ]
      }
      this.categoryChart.setOption(option)
    },
    initTrendChart() {
      const chartDom = this.$refs.trendChart
      this.trendChart = this.$echarts.init(chartDom)
      const days = this.helpTrendStats.map(item => item.date)
      const totalData = this.helpTrendStats.map(item => item.totalCount)
      const handledData = this.helpTrendStats.map(item => item.handledCount)
      const option = {
        tooltip: { trigger: 'axis' },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: days,
          axisLine: { lineStyle: { color: '#4CAF90' } }
        },
        yAxis: { type: 'value', min: 0 },
        color: ['#4CAF90', '#FF9800'],
        series: [
          {
            name: '求助数量',
            type: 'line',
            smooth: true,
            areaStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: 'rgba(76,175,144,0.4)' }, { offset: 1, color: 'rgba(76,175,144,0.05)' }] } },
            data: totalData
          },
          {
            name: '已处理',
            type: 'line',
            smooth: true,
            areaStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: 'rgba(255,152,0,0.4)' }, { offset: 1, color: 'rgba(255,152,0,0.05)' }] } },
            data: handledData
          }
        ]
      }
      this.trendChart.setOption(option)
    },
    goToHelp() {
      this.$router.push('/help')
    }
  },
  beforeDestroy() {
    if (this.categoryChart) this.categoryChart.dispose()
    if (this.trendChart) this.trendChart.dispose()
  }
}
</script>

<style lang="scss" scoped>
.dashboard {
  .stats-cards {
    .stat-card {
      .card-content {
        display: flex;
        align-items: center;
        gap: 16px;
      }

      .card-icon {
        width: 60px;
        height: 60px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;

        i {
          font-size: 28px;
          color: #fff;
        }
      }

      .card-info {
        flex: 1;

        .card-label {
          font-size: 13px;
          color: $text-secondary;
          margin-bottom: 4px;
        }

        .card-value {
          font-size: 28px;
          font-weight: 700;
        }
      }
    }
  }

  .card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-weight: 600;
    color: $text-primary;

    i {
      margin-right: 6px;
      color: $primary-color;
    }
  }

  .chart-card {
    height: 360px;
  }

  .warning-list {
    max-height: 280px;
    overflow-y: auto;

    .warning-item {
      display: flex;
      align-items: center;
      padding: 12px 0;
      border-bottom: 1px solid #f0f0f0;
      gap: 12px;

      &:last-child {
        border-bottom: none;
      }

      .warning-icon {
        width: 40px;
        height: 40px;
        background: rgba(244, 67, 54, 0.1);
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;

        i {
          font-size: 20px;
          color: $danger-color;
        }
      }

      .warning-content {
        flex: 1;

        .warning-title {
          font-weight: 600;
          color: $text-primary;
          margin-bottom: 4px;
        }

        .warning-desc {
          font-size: 13px;
          color: $text-secondary;

          .danger-text {
            color: $danger-color;
            font-weight: 600;
          }
        }
      }
    }
  }

  .help-list {
    max-height: 280px;
    overflow-y: auto;

    .help-item {
      display: flex;
      align-items: center;
      padding: 12px 0;
      border-bottom: 1px solid #f0f0f0;
      gap: 12px;

      &:last-child {
        border-bottom: none;
      }

      .help-urgency {
        width: 48px;
        height: 48px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 12px;
        font-weight: 600;
        color: #fff;

        &.urgency-紧急 {
          background: linear-gradient(135deg, #F44336, #EF9A9A);
        }

        &.urgency-高 {
          background: linear-gradient(135deg, #FF9800, #FFB74D);
        }

        &.urgency-中 {
          background: linear-gradient(135deg, #2196F3, #64B5F6);
        }

        &.urgency-低 {
          background: linear-gradient(135deg, #4CAF50, #81C784);
        }
      }

      .help-content {
        flex: 1;

        .help-title {
          margin-bottom: 4px;

          .help-type {
            font-weight: 600;
            color: $text-primary;
            margin-right: 8px;
          }

          .help-user {
            font-size: 13px;
            color: $text-secondary;
          }
        }

        .help-location {
          font-size: 13px;
          color: $text-secondary;
          margin-bottom: 4px;

          i {
            color: $primary-color;
            margin-right: 4px;
          }
        }

        .help-desc {
          font-size: 12px;
          color: $text-light;
        }
      }
    }
  }

  .chart-container {
    height: 280px;
  }

  .quick-entries {
    display: flex;
    gap: 24px;
    padding: 10px 0;

    .quick-entry {
      cursor: pointer;
      text-align: center;
      transition: $transition-base;

      &:hover {
        transform: translateY(-4px);

        .entry-icon {
          box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
        }
      }

      .entry-icon {
        width: 70px;
        height: 70px;
        border-radius: 16px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin: 0 auto 10px;
        transition: $transition-base;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);

        i {
          font-size: 30px;
          color: #fff;
        }
      }

      .entry-label {
        font-size: 14px;
        color: $text-primary;
        font-weight: 500;
      }
    }
  }
}

.fade-in {
  animation: fadeIn 0.5s ease-out forwards;
  opacity: 0;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
