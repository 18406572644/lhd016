<template>
  <div class="inventory-analysis">
    <div class="filter-bar">
      <el-form :inline="true" :model="filters" class="filter-form">
        <el-form-item label="补给点">
          <el-select v-model="filters.supplyPointId" placeholder="全部" clearable @change="loadData">
            <el-option
              v-for="point in supplyPoints"
              :key="point.id"
              :label="point.name"
              :value="point.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="filters.category" placeholder="全部" clearable @change="loadData">
            <el-option
              v-for="cat in categories"
              :key="cat"
              :label="cat"
              :value="cat"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="统计周期">
          <el-select v-model="filters.period" @change="loadTurnoverRate">
            <el-option label="月度" value="month"></el-option>
            <el-option label="季度" value="quarter"></el-option>
            <el-option label="年度" value="year"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-refresh" @click="loadAllData">刷新</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="overview-cards">
      <el-row :gutter="20">
        <el-col :span="4" v-for="(card, index) in overviewCards" :key="index" class="fade-in" :style="{ animationDelay: `${index * 0.1}s` }">
          <el-card class="stat-card" :body-style="{ padding: '20px' }">
            <div class="card-content">
              <div class="card-icon" :style="{ background: card.bgColor }">
                <i :class="card.icon"></i>
              </div>
              <div class="card-info">
                <div class="card-label">{{ card.label }}</div>
                <div class="card-value" :style="{ color: card.color }">{{ card.value }}</div>
                <div class="card-sub" v-if="card.sub">{{ card.sub }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="8">
        <el-card class="chart-card">
          <div slot="header" class="card-header">
            <span><i class="el-icon-odometer"></i> 库存健康度评分</span>
            <el-tag :type="healthScoreTagType">{{ healthScore.healthLevel || '--' }}</el-tag>
          </div>
          <div ref="healthGauge" class="chart-container"></div>
          <div class="health-details">
            <div class="health-item">
              <div class="health-label">低库存占比</div>
              <div class="health-bar">
                <div class="health-progress" :style="{ width: healthScore.lowStockRatio + '%', background: healthScore.lowStockRatio > 15 ? '#f56c6c' : healthScore.lowStockRatio > 5 ? '#e6a23c' : '#67c23a' }"></div>
              </div>
              <div class="health-value">{{ healthScore.lowStockRatio || 0 }}% ({{ healthScore.lowStockItems || 0 }}项)</div>
            </div>
            <div class="health-item">
              <div class="health-label">滞销占比</div>
              <div class="health-bar">
                <div class="health-progress" :style="{ width: healthScore.deadStockRatio + '%', background: healthScore.deadStockRatio > 25 ? '#f56c6c' : healthScore.deadStockRatio > 10 ? '#e6a23c' : '#67c23a' }"></div>
              </div>
              <div class="health-value">{{ healthScore.deadStockRatio || 0 }}% ({{ healthScore.deadStockItems || 0 }}项)</div>
            </div>
            <div class="health-item">
              <div class="health-label">账实差异率</div>
              <div class="health-bar">
                <div class="health-progress" :style="{ width: healthScore.inventoryDiffRatio + '%', background: healthScore.inventoryDiffRatio > 10 ? '#f56c6c' : healthScore.inventoryDiffRatio > 3 ? '#e6a23c' : '#67c23a' }"></div>
              </div>
              <div class="health-value">{{ healthScore.inventoryDiffRatio || 0 }}% ({{ healthScore.diffItems || 0 }}项)</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="chart-card">
          <div slot="header" class="card-header">
            <span><i class="el-icon-sort"></i> 库存周转率分析</span>
            <el-radio-group v-model="turnoverGroupBy" size="small" @change="loadTurnoverRate">
              <el-radio-button label="category">按分类</el-radio-button>
              <el-radio-button label="supplyPoint">按补给点</el-radio-button>
            </el-radio-group>
          </div>
          <div ref="turnoverChart" class="chart-container"></div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="chart-card">
          <div slot="header" class="card-header">
            <span><i class="el-icon-medal"></i> 库存 ABC 分类</span>
            <el-tag type="danger" effect="dark">A类 {{ abcStats.aCount }} 项</el-tag>
            <el-tag type="warning" effect="dark">B类 {{ abcStats.bCount }} 项</el-tag>
            <el-tag type="success" effect="dark">C类 {{ abcStats.cCount }} 项</el-tag>
          </div>
          <div ref="abcChart" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card class="chart-card">
          <div slot="header" class="card-header">
            <span><i class="el-icon-pie-chart"></i> 库存价值分布</span>
            <el-radio-group v-model="valueDimension" size="small" @change="loadValueDistribution">
              <el-radio-button label="category">按分类</el-radio-button>
              <el-radio-button label="supplyPoint">按补给点</el-radio-button>
            </el-radio-group>
          </div>
          <div ref="valuePieChart" class="chart-container"></div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card class="chart-card">
          <div slot="header" class="card-header">
            <span><i class="el-icon-data-line"></i> 出入库趋势</span>
            <el-select v-model="trendMonths" size="small" @change="loadInOutTrend" style="width: 100px;">
              <el-option label="近3个月" :value="3"></el-option>
              <el-option label="近6个月" :value="6"></el-option>
              <el-option label="近12个月" :value="12"></el-option>
            </el-select>
          </div>
          <div ref="trendChart" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row style="margin-top: 20px;">
      <el-col :span="24">
        <el-card>
          <div slot="header" class="card-header">
            <span><i class="el-icon-data-board"></i> 补给点多维度对比</span>
          </div>
          <el-table :data="supplyPointComparison" border stripe style="width: 100%">
            <el-table-column prop="supplyPointName" label="补给点" fixed="left" width="150">
              <template slot-scope="scope">
                <i class="el-icon-location-outline" style="color: #4CAF90; margin-right: 5px;"></i>{{ scope.row.supplyPointName }}
              </template>
            </el-table-column>
            <el-table-column prop="totalItems" label="SKU数量" width="100" align="center"></el-table-column>
            <el-table-column prop="totalValue" label="库存总价值(元)" width="140" align="right">
              <template slot-scope="scope">
                <span class="value-text">¥{{ formatNumber(scope.row.totalValue) }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="lowStockItems" label="低库存项" width="100" align="center">
              <template slot-scope="scope">
                <el-tag :type="scope.row.lowStockItems > 5 ? 'danger' : scope.row.lowStockItems > 0 ? 'warning' : 'success'" size="mini">
                  {{ scope.row.lowStockItems }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="lowStockRatio" label="低库存占比(%)" width="120" align="center">
              <template slot-scope="scope">
                <el-progress :percentage="scope.row.lowStockRatio || 0" :color="getProgressColor(scope.row.lowStockRatio)" :stroke-width="8"></el-progress>
              </template>
            </el-table-column>
            <el-table-column prop="avgTurnoverRate" label="平均周转率" width="110" align="center">
              <template slot-scope="scope">
                <span :class="getTurnoverClass(scope.row.avgTurnoverRate)">{{ scope.row.avgTurnoverRate?.toFixed(2) || '0.00' }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="stockInQuantity" label="入库量" width="90" align="center"></el-table-column>
            <el-table-column prop="stockOutQuantity" label="出库量" width="90" align="center"></el-table-column>
            <el-table-column prop="inventoryHealthScore" label="健康评分" width="120" align="center">
              <template slot-scope="scope">
                <div class="health-score-badge" :style="{ background: getHealthScoreBg(scope.row.inventoryHealthScore) }">
                  <span>{{ scope.row.inventoryHealthScore?.toFixed(0) || 0 }}</span>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-row style="margin-top: 20px;">
      <el-col :span="24">
        <el-card>
          <div slot="header" class="card-header">
            <span><i class="el-icon-table"></i> ABC 分类明细</span>
            <el-input
              v-model="abcSearch"
              placeholder="搜索配件名称/编码"
              style="width: 250px;"
              clearable
              prefix-icon="el-icon-search"
            >
            </el-input>
          </div>
          <el-tabs v-model="abcTab" @tab-click="filterAbcList">
            <el-tab-pane label="全部" name="ALL"></el-tab-pane>
            <el-tab-pane :label="'A类 (' + abcStats.aCount + ')'" name="A"></el-tab-pane>
            <el-tab-pane :label="'B类 (' + abcStats.bCount + ')'" name="B"></el-tab-pane>
            <el-tab-pane :label="'C类 (' + abcStats.cCount + ')'" name="C"></el-tab-pane>
          </el-tabs>
          <el-table :data="filteredAbcList" border stripe style="width: 100%;" height="400">
            <el-table-column prop="partCode" label="配件编码" width="120"></el-table-column>
            <el-table-column prop="partName" label="配件名称" width="150"></el-table-column>
            <el-table-column prop="category" label="分类" width="100"></el-table-column>
            <el-table-column prop="supplyPointName" label="所属补给点" width="130"></el-table-column>
            <el-table-column prop="stockQuantity" label="库存数量" width="100" align="right"></el-table-column>
            <el-table-column prop="unitPrice" label="单价(元)" width="100" align="right">
              <template slot-scope="scope">
                ¥{{ formatNumber(scope.row.unitPrice) }}
              </template>
            </el-table-column>
            <el-table-column prop="totalValue" label="库存价值(元)" width="120" align="right">
              <template slot-scope="scope">
                <span class="value-text">¥{{ formatNumber(scope.row.totalValue) }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="valuePercentage" label="价值占比(%)" width="110" align="right">
              <template slot-scope="scope">
                {{ scope.row.valuePercentage?.toFixed(2) || '0.00' }}%
              </template>
            </el-table-column>
            <el-table-column prop="cumulativePercentage" label="累计占比(%)" width="110" align="right">
              <template slot-scope="scope">
                {{ scope.row.cumulativePercentage?.toFixed(2) || '0.00' }}%
              </template>
            </el-table-column>
            <el-table-column prop="abcClass" label="分类" width="80" align="center">
              <template slot-scope="scope">
                <el-tag :type="scope.row.abcClass === 'A' ? 'danger' : scope.row.abcClass === 'B' ? 'warning' : 'success'" effect="dark" size="mini">
                  {{ scope.row.abcClass }}类
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import {
  getInventoryOverview,
  getTurnoverRate,
  getAbcClassification,
  getValueDistribution,
  getInOutTrend,
  getHealthScore,
  getSupplyPointComparison,
  getFilterOptions
} from '@/api/inventoryAnalysis'

export default {
  name: 'InventoryAnalysis',
  data() {
    return {
      filters: {
        supplyPointId: null,
        category: null,
        period: 'month'
      },
      supplyPoints: [],
      categories: [],
      overview: {},
      healthScore: {},
      turnoverData: [],
      turnoverGroupBy: 'category',
      abcData: [],
      abcSearch: '',
      abcTab: 'ALL',
      abcStats: { aCount: 0, bCount: 0, cCount: 0 },
      valueDistribution: [],
      valueDimension: 'category',
      trendData: [],
      trendMonths: 6,
      supplyPointComparison: [],
      healthGauge: null,
      turnoverChart: null,
      abcChart: null,
      valuePieChart: null,
      trendChart: null
    }
  },
  computed: {
    overviewCards() {
      return [
        {
          label: '总 SKU 数',
          value: this.overview.totalItems || 0,
          sub: `共 ${this.overview.totalCategories || 0} 个分类`,
          icon: 'el-icon-goods',
          color: '#4CAF90',
          bgColor: 'linear-gradient(135deg, #4CAF90, #81C7B7)'
        },
        {
          label: '库存总价值',
          value: '¥' + this.formatNumber(this.overview.totalInventoryValue || 0),
          sub: `${this.overview.totalSupplyPoints || 0} 个补给点`,
          icon: 'el-icon-monitor',
          color: '#2196F3',
          bgColor: 'linear-gradient(135deg, #87CEEB, #B3E5FC)'
        },
        {
          label: '平均周转率',
          value: (this.overview.avgTurnoverRate || 0).toFixed(2),
          sub: this.getTurnoverLabel(this.overview.avgTurnoverRate),
          icon: 'el-icon-refresh',
          color: '#FF9800',
          bgColor: 'linear-gradient(135deg, #FF9800, #FFB74D)'
        },
        {
          label: '低库存预警',
          value: this.overview.lowStockItems || 0,
          sub: `占比 ${this.overview.lowStockRatio || 0}%`,
          icon: 'el-icon-warning',
          color: '#F44336',
          bgColor: 'linear-gradient(135deg, #F44336, #EF9A9A)'
        },
        {
          label: '滞销商品',
          value: this.overview.deadStockItems || 0,
          sub: `占比 ${this.overview.deadStockRatio || 0}%`,
          icon: 'el-icon-time',
          color: '#9C27B0',
          bgColor: 'linear-gradient(135deg, #9C27B0, #CE93D8)'
        },
        {
          label: '健康评分',
          value: this.healthScore.totalScore?.toFixed(0) || '--',
          sub: this.healthScore.healthLevel || '--',
          icon: 'el-icon-magic-stick',
          color: this.getHealthScoreColor(),
          bgColor: this.getHealthScoreGradient()
        }
      ]
    },
    healthScoreTagType() {
      const score = this.healthScore.totalScore || 0
      if (score >= 80) return 'success'
      if (score >= 60) return 'warning'
      return 'danger'
    },
    filteredAbcList() {
      let list = this.abcData
      if (this.abcTab !== 'ALL') {
        list = list.filter(item => item.abcClass === this.abcTab)
      }
      if (this.abcSearch) {
        const search = this.abcSearch.toLowerCase()
        list = list.filter(item =>
          item.partName?.toLowerCase().includes(search) ||
          item.partCode?.toLowerCase().includes(search)
        )
      }
      return list
    }
  },
  mounted() {
    this.loadFilters()
    this.loadAllData()
    window.addEventListener('resize', this.handleResize)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize)
    this.disposeCharts()
  },
  methods: {
    async loadFilters() {
      try {
        const res = await getFilterOptions()
        this.supplyPoints = res.data?.supplyPoints || []
        this.categories = res.data?.categories || []
      } catch (e) {
        console.error('加载筛选条件失败', e)
        this.$message.error('加载筛选条件失败')
      }
    },
    async loadAllData() {
      this.$nextTick(() => {
        this.initCharts()
      })
      await Promise.all([
        this.loadOverview(),
        this.loadHealthScore(),
        this.loadTurnoverRate(),
        this.loadAbcClassification(),
        this.loadValueDistribution(),
        this.loadInOutTrend(),
        this.loadSupplyPointComparison()
      ])
    },
    async loadOverview() {
      try {
        const res = await getInventoryOverview()
        this.overview = res.data || {}
      } catch (e) {
        this.$message.error('加载概览数据失败')
      }
    },
    async loadHealthScore() {
      try {
        const res = await getHealthScore()
        this.healthScore = res.data || {}
        this.$nextTick(() => {
          this.updateHealthGauge()
        })
      } catch (e) {
        this.$message.error('加载健康评分失败')
      }
    },
    async loadTurnoverRate() {
      try {
        const res = await getTurnoverRate({
          groupBy: this.turnoverGroupBy,
          period: this.filters.period,
          supplyPointId: this.filters.supplyPointId,
          category: this.filters.category
        })
        this.turnoverData = res.data || []
        this.$nextTick(() => {
          this.updateTurnoverChart()
        })
      } catch (e) {
        this.$message.error('加载周转率数据失败')
      }
    },
    async loadAbcClassification() {
      try {
        const res = await getAbcClassification()
        this.abcData = res.data || []
        this.abcStats = {
          aCount: this.abcData.filter(i => i.abcClass === 'A').length,
          bCount: this.abcData.filter(i => i.abcClass === 'B').length,
          cCount: this.abcData.filter(i => i.abcClass === 'C').length
        }
        this.$nextTick(() => {
          this.updateAbcChart()
        })
      } catch (e) {
        this.$message.error('加载ABC分类失败')
      }
    },
    async loadValueDistribution() {
      try {
        const res = await getValueDistribution(this.valueDimension)
        this.valueDistribution = res.data || []
        this.$nextTick(() => {
          this.updateValuePieChart()
        })
      } catch (e) {
        this.$message.error('加载价值分布失败')
      }
    },
    async loadInOutTrend() {
      try {
        const res = await getInOutTrend({
          category: this.filters.category,
          supplyPointId: this.filters.supplyPointId,
          months: this.trendMonths
        })
        this.trendData = res.data || []
        this.$nextTick(() => {
          this.updateTrendChart()
        })
      } catch (e) {
        this.$message.error('加载出入库趋势失败')
      }
    },
    async loadSupplyPointComparison() {
      try {
        const res = await getSupplyPointComparison()
        this.supplyPointComparison = res.data || []
      } catch (e) {
        this.$message.error('加载补给点对比失败')
      }
    },
    loadData() {
      this.loadTurnoverRate()
      this.loadInOutTrend()
    },
    filterAbcList() {},
    initCharts() {
      const chartDom1 = this.$refs.healthGauge
      if (chartDom1) {
        this.healthGauge = this.$echarts.init(chartDom1)
        this.updateHealthGauge()
      }
      const chartDom2 = this.$refs.turnoverChart
      if (chartDom2) {
        this.turnoverChart = this.$echarts.init(chartDom2)
        this.updateTurnoverChart()
      }
      const chartDom3 = this.$refs.abcChart
      if (chartDom3) {
        this.abcChart = this.$echarts.init(chartDom3)
        this.updateAbcChart()
      }
      const chartDom4 = this.$refs.valuePieChart
      if (chartDom4) {
        this.valuePieChart = this.$echarts.init(chartDom4)
        this.updateValuePieChart()
      }
      const chartDom5 = this.$refs.trendChart
      if (chartDom5) {
        this.trendChart = this.$echarts.init(chartDom5)
        this.updateTrendChart()
      }
    },
    updateHealthGauge() {
      if (!this.healthGauge) return
      const score = this.healthScore.totalScore || 0
      const option = {
        series: [
          {
            type: 'gauge',
            startAngle: 200,
            endAngle: -20,
            min: 0,
            max: 100,
            splitNumber: 10,
            radius: '90%',
            itemStyle: {
              color: this.getHealthScoreColor()
            },
            progress: {
              show: true,
              width: 20
            },
            pointer: {
              show: false
            },
            axisLine: {
              lineStyle: {
                width: 20,
                color: [
                  [0.4, '#f56c6c'],
                  [0.6, '#e6a23c'],
                  [0.8, '#67c23a'],
                  [1, '#4CAF90']
                ]
              }
            },
            axisTick: {
              distance: -25,
              splitNumber: 5,
              lineStyle: {
                width: 2,
                color: '#999'
              }
            },
            splitLine: {
              distance: -30,
              length: 10,
              lineStyle: {
                width: 3,
                color: '#999'
              }
            },
            axisLabel: {
              distance: 10,
              color: '#999',
              fontSize: 12
            },
            anchor: {
              show: false
            },
            title: {
              show: false
            },
            detail: {
              valueAnimation: true,
              width: '60%',
              lineHeight: 40,
              borderRadius: 8,
              offsetCenter: [0, '0%'],
              fontSize: 36,
              fontWeight: 'bolder',
              formatter: '{value}',
              color: this.getHealthScoreColor()
            },
            data: [
              {
                value: score
              }
            ]
          }
        ]
      }
      this.healthGauge.setOption(option)
    },
    updateTurnoverChart() {
      if (!this.turnoverChart) return
      const names = this.turnoverData.map(item =>
        this.turnoverGroupBy === 'category' ? item.category : item.supplyPointName
      )
      const rates = this.turnoverData.map(item => item.turnoverRate || 0)
      const levels = this.turnoverData.map(item => this.getTurnoverLevelColor(item.turnoverLevel))

      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' },
          formatter: function(params) {
            const data = params[0]
            const item = this.turnoverData[data.dataIndex]
            return `${data.name}<br/>周转率: ${data.value.toFixed(2)}<br/>等级: ${item.turnoverLevel}<br/>出库量: ${item.outgoingQuantity}`
          }.bind(this)
        },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: {
          type: 'category',
          data: names,
          axisLabel: {
            interval: 0,
            rotate: 30
          }
        },
        yAxis: {
          type: 'value',
          name: '周转率'
        },
        series: [
          {
            name: '周转率',
            type: 'bar',
            data: rates.map((value, index) => ({
              value,
              itemStyle: { color: levels[index] }
            })),
            barWidth: '50%',
            label: {
              show: true,
              position: 'top',
              formatter: '{c}'
            }
          }
        ]
      }
      this.turnoverChart.setOption(option)
    },
    updateAbcChart() {
      if (!this.abcChart) return
      const aValue = this.abcStats.aCount > 0
        ? this.abcData.filter(i => i.abcClass === 'A').reduce((sum, i) => sum + (i.totalValue || 0), 0)
        : 0
      const bValue = this.abcStats.bCount > 0
        ? this.abcData.filter(i => i.abcClass === 'B').reduce((sum, i) => sum + (i.totalValue || 0), 0)
        : 0
      const cValue = this.abcStats.cCount > 0
        ? this.abcData.filter(i => i.abcClass === 'C').reduce((sum, i) => sum + (i.totalValue || 0), 0)
        : 0

      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{b}: ¥{c} ({d}%)'
        },
        legend: {
          bottom: '5%',
          left: 'center'
        },
        color: ['#f56c6c', '#e6a23c', '#67c23a'],
        series: [
          {
            name: 'ABC分类',
            type: 'pie',
            radius: ['40%', '70%'],
            center: ['50%', '40%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: true,
              formatter: '{b}\n{d}%'
            },
            data: [
              { value: aValue, name: 'A类 - 高价值' },
              { value: bValue, name: 'B类 - 中价值' },
              { value: cValue, name: 'C类 - 低价值' }
            ]
          }
        ]
      }
      this.abcChart.setOption(option)
    },
    updateValuePieChart() {
      if (!this.valuePieChart) return
      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{b}: ¥{c} ({d}%)'
        },
        legend: {
          type: 'scroll',
          orient: 'vertical',
          right: '5%',
          top: 'center'
        },
        color: ['#4CAF90', '#87CEEB', '#FF9800', '#9C27B0', '#E91E63', '#00BCD4', '#FFEB3B', '#795548'],
        series: [
          {
            name: this.valueDimension === 'category' ? '分类价值' : '补给点价值',
            type: 'pie',
            radius: ['40%', '70%'],
            center: ['35%', '50%'],
            itemStyle: {
              borderRadius: 8,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: false
            },
            emphasis: {
              label: {
                show: true,
                fontSize: 14,
                fontWeight: 'bold',
                formatter: '{b}\n¥{c}'
              }
            },
            data: this.valueDistribution.map(item => ({
              value: item.value,
              name: item.name
            }))
          }
        ]
      }
      this.valuePieChart.setOption(option)
    },
    updateTrendChart() {
      if (!this.trendChart) return
      const months = [...new Set(this.trendData.map(item => item.month))].sort()

      const categoryMap = {}
      this.trendData.forEach(item => {
        const cat = item.category || '全部'
        if (!categoryMap[cat]) {
          categoryMap[cat] = { in: [], out: [] }
        }
      })

      Object.keys(categoryMap).forEach(cat => {
        months.forEach(month => {
          const record = this.trendData.find(item => item.month === month && (item.category || '全部') === cat)
          categoryMap[cat].in.push(record ? record.stockInQuantity : 0)
          categoryMap[cat].out.push(record ? record.stockOutQuantity : 0)
        })
      })

      const series = []
      const colors = ['#4CAF90', '#FF9800', '#2196F3', '#9C27B0', '#E91E63']
      let colorIndex = 0

      Object.keys(categoryMap).forEach(cat => {
        const color = colors[colorIndex % colors.length]
        series.push({
          name: cat + '-入库',
          type: 'line',
          smooth: true,
          stack: cat + '-in',
          lineStyle: { width: 2 },
          areaStyle: {
            color: {
              type: 'linear',
              x: 0, y: 0, x2: 0, y2: 1,
              colorStops: [
                { offset: 0, color: this.hexToRgba(color, 0.4) },
                { offset: 1, color: this.hexToRgba(color, 0.05) }
              ]
            }
          },
          itemStyle: { color },
          data: categoryMap[cat].in
        })
        series.push({
          name: cat + '-出库',
          type: 'line',
          smooth: true,
          stack: cat + '-out',
          lineStyle: { width: 2, type: 'dashed' },
          itemStyle: { color: this.lightenColor(color, 30) },
          data: categoryMap[cat].out
        })
        colorIndex++
      })

      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'cross' }
        },
        legend: {
          type: 'scroll',
          bottom: '0%'
        },
        grid: { left: '3%', right: '4%', bottom: '15%', containLabel: true },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: months,
          axisLine: { lineStyle: { color: '#4CAF90' } }
        },
        yAxis: {
          type: 'value',
          name: '数量'
        },
        series
      }
      this.trendChart.setOption(option)
    },
    disposeCharts() {
      if (this.healthGauge) this.healthGauge.dispose()
      if (this.turnoverChart) this.turnoverChart.dispose()
      if (this.abcChart) this.abcChart.dispose()
      if (this.valuePieChart) this.valuePieChart.dispose()
      if (this.trendChart) this.trendChart.dispose()
    },
    handleResize() {
      this.healthGauge?.resize()
      this.turnoverChart?.resize()
      this.abcChart?.resize()
      this.valuePieChart?.resize()
      this.trendChart?.resize()
    },
    formatNumber(num) {
      if (num == null) return '0'
      return Number(num).toLocaleString('zh-CN', { maximumFractionDigits: 2 })
    },
    getTurnoverLabel(rate) {
      if (!rate) return '无数据'
      if (rate >= 2) return '快销'
      if (rate >= 0.5) return '正常'
      if (rate >= 0.1) return '慢销'
      return '滞销'
    },
    getTurnoverLevelColor(level) {
      const colors = {
        '快销': '#67c23a',
        '正常': '#409EFF',
        '慢销': '#e6a23c',
        '滞销': '#f56c6c'
      }
      return colors[level] || '#909399'
    },
    getTurnoverClass(rate) {
      if (rate >= 2) return 'turnover-fast'
      if (rate >= 0.5) return 'turnover-normal'
      if (rate >= 0.1) return 'turnover-slow'
      return 'turnover-dead'
    },
    getHealthScoreColor() {
      const score = this.healthScore.totalScore || 0
      if (score >= 80) return '#67c23a'
      if (score >= 60) return '#e6a23c'
      return '#f56c6c'
    },
    getHealthScoreGradient() {
      const score = this.healthScore.totalScore || 0
      if (score >= 80) return 'linear-gradient(135deg, #67c23a, #95d475)'
      if (score >= 60) return 'linear-gradient(135deg, #e6a23c, #f0c78a)'
      return 'linear-gradient(135deg, #f56c6c, #f89898)'
    },
    getProgressColor(ratio) {
      if (ratio <= 5) return '#67c23a'
      if (ratio <= 15) return '#e6a23c'
      return '#f56c6c'
    },
    hexToRgba(hex, alpha) {
      const r = parseInt(hex.slice(1, 3), 16)
      const g = parseInt(hex.slice(3, 5), 16)
      const b = parseInt(hex.slice(5, 7), 16)
      return `rgba(${r}, ${g}, ${b}, ${alpha})`
    },
    lightenColor(hex, percent) {
      const num = parseInt(hex.slice(1), 16)
      const amt = Math.round(2.55 * percent)
      const R = Math.min(255, (num >> 16) + amt)
      const G = Math.min(255, ((num >> 8) & 0x00FF) + amt)
      const B = Math.min(255, (num & 0x0000FF) + amt)
      return '#' + (0x1000000 + R * 0x10000 + G * 0x100 + B).toString(16).slice(1)
    },
    getHealthScoreBg(score) {
      const s = score || 0
      if (s >= 80) return 'linear-gradient(135deg, #67c23a, #95d475)'
      if (s >= 60) return 'linear-gradient(135deg, #e6a23c, #f0c78a)'
      return 'linear-gradient(135deg, #f56c6c, #f89898)'
    }
  }
}
</script>

<style lang="scss" scoped>
.inventory-analysis {
  .filter-bar {
    background: #fff;
    padding: 16px;
    border-radius: 8px;
    margin-bottom: 20px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

    .filter-form {
      margin: 0;

      .el-form-item {
        margin-bottom: 0;
        margin-right: 20px;
      }
    }
  }

  .overview-cards {
    .stat-card {
      .card-content {
        display: flex;
        align-items: center;
        gap: 16px;
      }

      .card-icon {
        width: 56px;
        height: 56px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;

        i {
          font-size: 26px;
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
          font-size: 24px;
          font-weight: 700;
          line-height: 1.2;
        }

        .card-sub {
          font-size: 12px;
          color: $text-light;
          margin-top: 2px;
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

    .el-tag {
      margin-left: 8px;
    }
  }

  .chart-card {
    height: 420px;
  }

  .chart-container {
    height: 280px;
  }

  .health-details {
    padding: 0 16px;

    .health-item {
      margin-bottom: 12px;

      .health-label {
        font-size: 13px;
        color: $text-secondary;
        margin-bottom: 4px;
      }

      .health-bar {
        height: 6px;
        background: #f0f0f0;
        border-radius: 3px;
        overflow: hidden;

        .health-progress {
          height: 100%;
          border-radius: 3px;
          transition: width 0.3s ease;
        }
      }

      .health-value {
        font-size: 12px;
        color: $text-light;
        margin-top: 2px;
        text-align: right;
      }
    }
  }

  .value-text {
    color: $primary-color;
    font-weight: 600;
  }

  .turnover-fast {
    color: #67c23a;
    font-weight: 600;
  }

  .turnover-normal {
    color: #409EFF;
    font-weight: 600;
  }

  .turnover-slow {
    color: #e6a23c;
    font-weight: 600;
  }

  .turnover-dead {
    color: #f56c6c;
    font-weight: 600;
  }

  .health-score-badge {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 auto;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);

    span {
      color: #fff;
      font-weight: 700;
      font-size: 16px;
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
