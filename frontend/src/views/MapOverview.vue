<template>
  <div class="map-overview">
    <el-card class="map-card">
      <div class="map-header">
        <div class="header-title">
          <i class="el-icon-location-outline"></i>
          <span>地图总览</span>
        </div>
        <div class="header-stats">
          <el-tag type="success" class="stat-tag">
            <i class="el-icon-location"></i>
            补给点: {{ stats.supplyPointCount }}
          </el-tag>
          <el-tag type="warning" class="stat-tag">
            <i class="el-icon-service"></i>
            维修点: {{ stats.repairShopCount }}
          </el-tag>
          <el-tag type="danger" class="stat-tag" v-if="stats.lowStockCount > 0">
            <i class="el-icon-warning"></i>
            库存预警: {{ stats.lowStockCount }}
          </el-tag>
          <el-tag type="danger" effect="dark" class="stat-tag" v-if="stats.pendingHelpCount > 0">
            <i class="el-icon-bell"></i>
            待处理求助: {{ stats.pendingHelpCount }}
          </el-tag>
        </div>
        <div class="refresh-btn">
          <el-button type="primary" icon="el-icon-refresh" :loading="loading" @click="loadData">
            刷新数据
          </el-button>
        </div>
      </div>

      <div class="map-content">
        <div class="filter-panel">
          <div class="filter-section">
            <div class="filter-title">
              <i class="el-icon-filter"></i>
              筛选条件
            </div>
            <el-form :inline="true" :model="filterForm" class="filter-form">
              <el-form-item label="区域">
                <el-select v-model="filterForm.area" placeholder="全部区域" clearable @change="applyFilters">
                  <el-option v-for="area in areaOptions" :key="area" :label="area" :value="area"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="类型">
                <el-select v-model="filterForm.type" placeholder="全部类型" clearable @change="applyFilters">
                  <el-option label="补给点" value="supply"></el-option>
                  <el-option label="维修点" value="repair"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="补给点类型">
                <el-select v-model="filterForm.supplyType" placeholder="全部" clearable @change="applyFilters">
                  <el-option label="饮水点" value="饮水点"></el-option>
                  <el-option label="休息站" value="休息站"></el-option>
                  <el-option label="充电站" value="充电站"></el-option>
                  <el-option label="综合补给" value="综合补给"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="状态">
                <el-select v-model="filterForm.status" placeholder="全部状态" clearable @change="applyFilters">
                  <el-option label="正常" value="正常"></el-option>
                  <el-option label="维护中" value="维护中"></el-option>
                  <el-option label="关闭" value="关闭"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="仅显示预警">
                <el-switch v-model="filterForm.onlyWarning" @change="applyFilters"></el-switch>
              </el-form-item>
            </el-form>
          </div>

          <div class="legend-section">
            <div class="filter-title">
              <i class="el-icon-picture-outline"></i>
              图例说明
            </div>
            <div class="legend-list">
              <div class="legend-item">
                <div class="legend-icon supply-icon"></div>
                <span>补给点</span>
              </div>
              <div class="legend-item">
                <div class="legend-icon repair-icon"></div>
                <span>维修点</span>
              </div>
              <div class="legend-item">
                <div class="legend-icon help-icon"></div>
                <span>求助事件</span>
              </div>
              <div class="legend-item">
                <div class="legend-icon warning-icon pulse"></div>
                <span>库存预警</span>
              </div>
              <div class="legend-item">
                <div class="legend-route"></div>
                <span>救援路线</span>
              </div>
            </div>
          </div>

          <div class="active-help-section" v-if="activeHelpList.length > 0">
            <div class="filter-title">
              <i class="el-icon-bell"></i>
              待处理求助
            </div>
            <div class="help-list">
              <div
                v-for="help in activeHelpList"
                :key="help.id"
                class="help-item"
                :class="{ active: selectedHelpId === help.id }"
                @click="focusHelp(help)"
              >
                <div class="help-header">
                  <el-tag :type="getUrgencyType(help.urgency)" size="small" effect="dark">
                    {{ help.urgency }}
                  </el-tag>
                  <span class="help-name">{{ help.requesterName }}</span>
                  <el-tag :type="getStatusType(help.status)" size="small">
                    {{ getStatusText(help.status) }}
                  </el-tag>
                </div>
                <div class="help-content">
                  <div class="help-type">
                    <i class="el-icon-question"></i>
                    {{ help.helpType }}
                  </div>
                  <div class="help-location" :title="help.location">
                    <i class="el-icon-location"></i>
                    {{ help.location }}
                  </div>
                  <div class="help-route" v-if="help.repairShop">
                    <i class="el-icon-guide"></i>
                    派往: {{ help.repairShop.name }}
                    <span class="route-info">({{ help.distanceText }}, {{ help.etaText }})</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="map-wrapper">
          <div ref="mapContainer" class="map-container"></div>
        </div>
      </div>
    </el-card>

    <el-dialog :title="detailTitle" :visible.sync="detailVisible" width="600px">
      <div v-if="detailData">
        <template v-if="detailType === 'supply'">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="补给点名称">{{ detailData.name }}</el-descriptions-item>
            <el-descriptions-item label="所属区域">{{ detailData.area }}</el-descriptions-item>
            <el-descriptions-item label="类型">
              <el-tag :type="getSupplyTypeTag(detailData.type)" size="small">{{ detailData.type }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="getStatusType(detailData.status)" size="small">{{ detailData.status }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="详细地址" :span="2">{{ detailData.address }}</el-descriptions-item>
            <el-descriptions-item label="联系人">{{ detailData.contactPerson }}</el-descriptions-item>
            <el-descriptions-item label="联系电话">{{ detailData.contactPhone }}</el-descriptions-item>
            <el-descriptions-item label="经纬度">{{ detailData.longitude }}, {{ detailData.latitude }}</el-descriptions-item>
            <el-descriptions-item label="库存预警">
              <el-tag v-if="detailData.hasLowStock" type="danger" size="small">
                有 {{ detailData.lowStockCount }} 种配件预警
              </el-tag>
              <el-tag v-else type="success" size="small">正常</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="描述说明" :span="2">{{ detailData.description || '暂无' }}</el-descriptions-item>
          </el-descriptions>
        </template>
        <template v-else-if="detailType === 'repair'">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="维修点名称">{{ detailData.name }}</el-descriptions-item>
            <el-descriptions-item label="等级">
              <el-tag :type="getLevelType(detailData.level)" size="small">{{ detailData.level }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="所属区域">{{ detailData.area }}</el-descriptions-item>
            <el-descriptions-item label="营业时间">{{ detailData.businessHours }}</el-descriptions-item>
            <el-descriptions-item label="详细地址" :span="2">{{ detailData.address }}</el-descriptions-item>
            <el-descriptions-item label="经纬度" :span="2">{{ detailData.longitude }}, {{ detailData.latitude }}</el-descriptions-item>
            <el-descriptions-item label="服务范围" :span="2">{{ detailData.serviceScope }}</el-descriptions-item>
            <el-descriptions-item label="联系人">{{ detailData.contactPerson }}</el-descriptions-item>
            <el-descriptions-item label="联系电话">{{ detailData.contactPhone }}</el-descriptions-item>
            <el-descriptions-item label="维修人员">{{ detailData.staffCount }} 人</el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ formatDate(detailData.createTime) }}</el-descriptions-item>
          </el-descriptions>
        </template>
      </div>
      <div slot="footer">
        <el-button @click="detailVisible = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getMapOverview } from '@/api/map'
import { formatDate, getStatusType, getStatusText, getUrgencyType, getLevelType } from '@/utils'

export default {
  name: 'MapOverview',
  data() {
    return {
      loading: false,
      map: null,
      driving: null,
      supplyMarkers: [],
      repairMarkers: [],
      helpMarkers: [],
      routePolylines: [],
      warningMarkers: [],
      allSupplyPoints: [],
      allRepairShops: [],
      allHelpRequests: [],
      activeHelpList: [],
      filteredSupplyPoints: [],
      filteredRepairShops: [],
      selectedHelpId: null,
      detailVisible: false,
      detailType: '',
      detailData: null,
      refreshTimer: null,
      filterForm: {
        area: '',
        type: '',
        supplyType: '',
        status: '',
        onlyWarning: false
      },
      stats: {
        supplyPointCount: 0,
        repairShopCount: 0,
        lowStockCount: 0,
        pendingHelpCount: 0
      },
      areaOptions: ['滨江区', '西湖区', '余杭区', '拱墅区', '上城区', '下城区', '江干区']
    }
  },
  computed: {
    detailTitle() {
      return this.detailType === 'supply' ? '补给点详情' : '维修点详情'
    }
  },
  mounted() {
    this.initMap()
    this.loadData()
    this.startAutoRefresh()
  },
  beforeDestroy() {
    this.stopAutoRefresh()
    if (this.map) {
      this.map.destroy()
    }
  },
  methods: {
    formatDate,
    getStatusType,
    getStatusText,
    getUrgencyType,
    getLevelType,
    getSupplyTypeTag(type) {
      const map = {
        '饮水点': 'info',
        '休息站': 'success',
        '充电站': 'warning',
        '综合补给': 'primary'
      }
      return map[type] || 'primary'
    },
    initMap() {
      this.$nextTick(() => {
        this.map = new AMap.Map(this.$refs.mapContainer, {
          zoom: 11,
          center: [120.1551, 30.2741],
          viewMode: '2D'
        })

        this.map.plugin(['AMap.ToolBar', 'AMap.Scale'], () => {
          this.map.addControl(new AMap.ToolBar())
          this.map.addControl(new AMap.Scale())
        })
      })
    },
    async loadData() {
      this.loading = true
      try {
        const res = await getMapOverview()
        if (res.code === 200) {
          const data = res.data
          this.allSupplyPoints = data.supplyPoints || []
          this.allRepairShops = data.repairShops || []
          this.allHelpRequests = data.helpRequests || []

          this.stats.supplyPointCount = this.allSupplyPoints.length
          this.stats.repairShopCount = this.allRepairShops.length
          this.stats.lowStockCount = this.allSupplyPoints.filter(s => s.hasLowStock).length
          this.stats.pendingHelpCount = this.allHelpRequests.filter(h => h.status !== 'completed').length

          this.activeHelpList = this.allHelpRequests.filter(h => h.status !== 'completed')

          this.applyFilters()
        }
      } catch (e) {
        this.$message.error('加载地图数据失败')
      } finally {
        this.loading = false
      }
    },
    applyFilters() {
      const { area, type, supplyType, status, onlyWarning } = this.filterForm

      this.filteredSupplyPoints = this.allSupplyPoints.filter(sp => {
        if (type && type !== 'supply') return false
        if (area && sp.area !== area) return false
        if (supplyType && sp.type !== supplyType) return false
        if (status && sp.status !== status) return false
        if (onlyWarning && !sp.hasLowStock) return false
        return true
      })

      this.filteredRepairShops = this.allRepairShops.filter(rs => {
        if (type && type !== 'repair') return false
        if (area && rs.area !== area) return false
        return true
      })

      this.renderMarkers()
    },
    renderMarkers() {
      this.clearAllMarkers()

      this.filteredSupplyPoints.forEach(sp => {
        this.addSupplyPointMarker(sp)
      })

      this.filteredRepairShops.forEach(rs => {
        this.addRepairShopMarker(rs)
      })

      this.allHelpRequests.forEach(hr => {
        if (hr.status !== 'completed') {
          this.addHelpRequestMarker(hr)
        }
      })

      this.fitMapToView()
    },
    clearAllMarkers() {
      this.supplyMarkers.forEach(m => this.map.remove(m))
      this.repairMarkers.forEach(m => this.map.remove(m))
      this.helpMarkers.forEach(m => this.map.remove(m))
      this.warningMarkers.forEach(m => this.map.remove(m))
      this.routePolylines.forEach(r => this.map.remove(r))

      this.supplyMarkers = []
      this.repairMarkers = []
      this.helpMarkers = []
      this.warningMarkers = []
      this.routePolylines = []
    },
    addSupplyPointMarker(sp) {
      if (!sp.longitude || !sp.latitude) return

      const color = sp.hasLowStock ? '#F56C6C' : '#67C23A'
      const content = this.createMarkerContent('supply', sp.name, color, sp.hasLowStock)

      const marker = new AMap.Marker({
        position: [sp.longitude, sp.latitude],
        content: content,
        offset: new AMap.Pixel(-18, -36),
        title: sp.name
      })

      marker.on('click', () => {
        this.showDetail('supply', sp)
      })

      this.map.add(marker)
      this.supplyMarkers.push(marker)

      if (sp.hasLowStock) {
        this.addWarningEffect(sp.longitude, sp.latitude)
      }
    },
    addRepairShopMarker(rs) {
      if (!rs.longitude || !rs.latitude) return

      const content = this.createMarkerContent('repair', rs.name, '#E6A23C', false)

      const marker = new AMap.Marker({
        position: [rs.longitude, rs.latitude],
        content: content,
        offset: new AMap.Pixel(-18, -36),
        title: rs.name
      })

      marker.on('click', () => {
        this.showDetail('repair', rs)
      })

      this.map.add(marker)
      this.repairMarkers.push(marker)
    },
    addHelpRequestMarker(hr) {
      if (!hr.longitude || !hr.latitude) return

      const urgencyColor = {
        '低': '#909399',
        '中': '#67C23A',
        '高': '#E6A23C',
        '紧急': '#F56C6C'
      }
      const color = urgencyColor[hr.urgency] || '#F56C6C'

      const content = this.createHelpMarkerContent(hr.helpType, color)

      const marker = new AMap.Marker({
        position: [hr.longitude, hr.latitude],
        content: content,
        offset: new AMap.Pixel(-18, -36),
        title: hr.requesterName + ' - ' + hr.helpType,
        zIndex: 150
      })

      marker.on('click', () => {
        this.focusHelp(hr)
      })

      this.map.add(marker)
      this.helpMarkers.push(marker)

      if (hr.repairShop && hr.repairShop.longitude && hr.repairShop.latitude) {
        this.drawRoute(hr)
      }
    },
    addWarningEffect(lng, lat) {
      const circle = new AMap.Circle({
        center: [lng, lat],
        radius: 80,
        strokeColor: '#F56C6C',
        strokeWeight: 2,
        strokeOpacity: 0.8,
        fillColor: '#F56C6C',
        fillOpacity: 0.1,
        zIndex: 10
      })

      this.map.add(circle)
      this.warningMarkers.push(circle)

      this.animateWarning(circle)
    },
    animateWarning(circle) {
      let growing = true
      let radius = 50
      const animate = () => {
        if (growing) {
          radius += 2
          if (radius >= 100) growing = false
        } else {
          radius -= 2
          if (radius <= 50) growing = true
        }
        circle.setRadius(radius)
        circle.setOptions({
          fillOpacity: 0.3 - (radius - 50) / 200
        })
      }
      circle.animateTimer = setInterval(animate, 50)
    },
    createMarkerContent(type, name, color, hasWarning) {
      const icon = type === 'supply' ? '📍' : '🔧'
      const warningClass = hasWarning ? 'marker-pulse' : ''
      return `
        <div class="custom-marker ${warningClass}">
          <div class="marker-icon" style="background: ${color}">
            <span>${icon}</span>
          </div>
          <div class="marker-label">${name}</div>
          ${hasWarning ? '<div class="warning-dot"></div>' : ''}
        </div>
      `
    },
    createHelpMarkerContent(helpType, color) {
      const iconMap = {
        '车辆故障': '🚲',
        '身体不适': '💊',
        '物资需求': '🎒',
        '其他': '❓'
      }
      const icon = iconMap[helpType] || '🆘'
      return `
        <div class="custom-marker help-marker">
          <div class="marker-icon" style="background: ${color}">
            <span>${icon}</span>
          </div>
          <div class="marker-label help-label">求助</div>
          <div class="marker-pulse-ring"></div>
        </div>
      `
    },
    drawRoute(helpRequest) {
      if (!this.driving) {
        this.driving = new AMap.Driving({
          map: this.map,
          hideMarkers: true,
          showTraffic: false,
          policy: AMap.DrivingPolicy.LEAST_TIME
        })
      }

      const start = [helpRequest.longitude, helpRequest.latitude]
      const end = [helpRequest.repairShop.longitude, helpRequest.repairShop.latitude]

      const polyline = new AMap.Polyline({
        path: [start, end],
        strokeColor: '#F56C6C',
        strokeWeight: 4,
        strokeOpacity: 0.8,
        strokeStyle: 'dashed',
        lineJoin: 'round',
        zIndex: 80
      })

      this.map.add(polyline)
      this.routePolylines.push(polyline)

      this.animateRoute(polyline)
    },
    animateRoute(polyline) {
      let opacity = 0.8
      let decreasing = true
      const animate = () => {
        if (decreasing) {
          opacity -= 0.05
          if (opacity <= 0.3) decreasing = false
        } else {
          opacity += 0.05
          if (opacity >= 0.8) decreasing = true
        }
        polyline.setOptions({ strokeOpacity: opacity })
      }
      polyline.animateTimer = setInterval(animate, 80)
    },
    fitMapToView() {
      const allPositions = []
      this.filteredSupplyPoints.forEach(sp => {
        if (sp.longitude && sp.latitude) {
          allPositions.push([sp.longitude, sp.latitude])
        }
      })
      this.filteredRepairShops.forEach(rs => {
        if (rs.longitude && rs.latitude) {
          allPositions.push([rs.longitude, rs.latitude])
        }
      })
      this.allHelpRequests.forEach(hr => {
        if (hr.status !== 'completed' && hr.longitude && hr.latitude) {
          allPositions.push([hr.longitude, hr.latitude])
        }
      })

      if (allPositions.length > 0) {
        this.map.setFitView(null, false, [60, 60, 60, 60])
      }
    },
    showDetail(type, data) {
      this.detailType = type
      this.detailData = data
      this.detailVisible = true
    },
    focusHelp(help) {
      this.selectedHelpId = help.id
      if (help.longitude && help.latitude) {
        this.map.setCenter([help.longitude, help.latitude])
        this.map.setZoom(14)
      }
    },
    startAutoRefresh() {
      this.refreshTimer = setInterval(() => {
        this.loadData()
      }, 30000)
    },
    stopAutoRefresh() {
      if (this.refreshTimer) {
        clearInterval(this.refreshTimer)
        this.refreshTimer = null
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.map-overview {
  .map-card {
    padding: 0;
    height: calc(100vh - 180px);
    display: flex;
    flex-direction: column;

    ::v-deep .el-card__body {
      padding: 0;
      height: 100%;
      display: flex;
      flex-direction: column;
    }
  }

  .map-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 15px 20px;
    background: linear-gradient(135deg, #4CAF90 0%, #87CEEB 100%);
    color: #fff;

    .header-title {
      display: flex;
      align-items: center;
      gap: 10px;
      font-size: 18px;
      font-weight: 600;

      i {
        font-size: 24px;
      }
    }

    .header-stats {
      display: flex;
      gap: 10px;

      .stat-tag {
        font-size: 13px;
        padding: 4px 10px;
      }
    }
  }

  .map-content {
    flex: 1;
    display: flex;
    min-height: 0;
  }

  .filter-panel {
    width: 320px;
    background: #fff;
    border-right: 1px solid #e4e7ed;
    display: flex;
    flex-direction: column;
    overflow-y: auto;
    flex-shrink: 0;
  }

  .filter-section,
  .legend-section,
  .active-help-section {
    padding: 15px;
    border-bottom: 1px solid #f0f0f0;
  }

  .filter-title {
    font-weight: 600;
    margin-bottom: 12px;
    color: #303133;
    display: flex;
    align-items: center;
    gap: 6px;

    i {
      color: #4CAF90;
    }
  }

  .filter-form {
    .el-form-item {
      margin-bottom: 12px;
      margin-right: 0;
      width: 100%;

      .el-form-item__label {
        width: 80px;
      }

      .el-select,
      .el-input {
        width: calc(100% - 80px);
      }
    }
  }

  .legend-list {
    display: flex;
    flex-direction: column;
    gap: 10px;
  }

  .legend-item {
    display: flex;
    align-items: center;
    gap: 10px;
    font-size: 13px;
    color: #606266;
  }

  .legend-icon {
    width: 18px;
    height: 18px;
    border-radius: 50%;
    flex-shrink: 0;

    &.supply-icon {
      background: #67C23A;
    }

    &.repair-icon {
      background: #E6A23C;
    }

    &.help-icon {
      background: #F56C6C;
    }

    &.warning-icon {
      background: #F56C6C;
      position: relative;
    }

    &.pulse {
      animation: legend-pulse 1.5s ease-in-out infinite;
    }
  }

  .legend-route {
    width: 30px;
    height: 4px;
    background: #F56C6C;
    border-radius: 2px;
    position: relative;

    &::before {
      content: '';
      position: absolute;
      left: 0;
      top: 50%;
      width: 100%;
      height: 4px;
      background: repeating-linear-gradient(
        90deg,
        #F56C6C,
        #F56C6C 4px,
        transparent 4px,
        transparent 8px
      );
      transform: translateY(-50%);
    }
  }

  @keyframes legend-pulse {
    0%, 100% {
      transform: scale(1);
      opacity: 1;
    }
    50% {
      transform: scale(1.3);
      opacity: 0.7;
    }
  }

  .help-list {
    display: flex;
    flex-direction: column;
    gap: 10px;
  }

  .help-item {
    padding: 12px;
    background: #f9f9f9;
    border-radius: 6px;
    cursor: pointer;
    transition: all 0.3s;
    border-left: 3px solid transparent;

    &:hover {
      background: #f0f9ff;
    }

    &.active {
      background: #ecf5ff;
      border-left-color: #409EFF;
    }

    .help-header {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 8px;

      .help-name {
        flex: 1;
        font-weight: 500;
        color: #303133;
      }
    }

    .help-content {
      font-size: 12px;
      color: #606266;
      display: flex;
      flex-direction: column;
      gap: 4px;

      .help-type,
      .help-location,
      .help-route {
        display: flex;
        align-items: center;
        gap: 5px;
        line-height: 1.5;

        i {
          color: #909399;
        }

        .route-info {
          color: #4CAF90;
        }
      }
    }
  }

  .map-wrapper {
    flex: 1;
    position: relative;
    min-width: 0;
  }

  .map-container {
    width: 100%;
    height: 100%;
  }
}

::v-deep .custom-marker {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  transition: transform 0.2s;

  &:hover {
    transform: translateY(-3px);
  }

  .marker-icon {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    font-size: 18px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
    border: 2px solid #fff;
    position: relative;
    z-index: 2;

    span {
      font-size: 16px;
    }
  }

  .marker-label {
    background: rgba(0, 0, 0, 0.75);
    color: #fff;
    padding: 2px 8px;
    border-radius: 3px;
    font-size: 11px;
    white-space: nowrap;
    margin-top: 2px;
    max-width: 100px;
    overflow: hidden;
    text-overflow: ellipsis;
    z-index: 2;
  }

  &.help-marker {
    .marker-label.help-label {
      background: #F56C6C;
      font-weight: 600;
    }

    .marker-pulse-ring {
      position: absolute;
      width: 50px;
      height: 50px;
      border-radius: 50%;
      border: 3px solid #F56C6C;
      top: -7px;
      left: -7px;
      animation: marker-pulse-ring 1.5s ease-out infinite;
      z-index: 1;
    }
  }

  .warning-dot {
    position: absolute;
    top: -2px;
    right: -2px;
    width: 12px;
    height: 12px;
    background: #F56C6C;
    border-radius: 50%;
    border: 2px solid #fff;
    animation: warning-blink 1s ease-in-out infinite;
    z-index: 3;
  }

  &.marker-pulse {
    .marker-icon {
      animation: icon-pulse 2s ease-in-out infinite;
    }
  }
}

@keyframes marker-pulse-ring {
  0% {
    transform: scale(0.6);
    opacity: 1;
  }
  100% {
    transform: scale(2);
    opacity: 0;
  }
}

@keyframes warning-blink {
  0%, 100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.5;
    transform: scale(1.2);
  }
}

@keyframes icon-pulse {
  0%, 100% {
    box-shadow: 0 2px 8px rgba(245, 108, 108, 0.5);
  }
  50% {
    box-shadow: 0 2px 20px rgba(245, 108, 108, 0.9);
  }
}
</style>
