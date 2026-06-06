<template>
  <el-dialog
    title="地图选点"
    :visible.sync="visible"
    width="900px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <div class="map-picker">
      <div class="search-bar">
        <el-input
          v-model="searchAddress"
          placeholder="请输入地址搜索"
          clearable
          class="search-input"
          @keyup.enter.native="handleSearch"
        >
          <el-button slot="append" icon="el-icon-search" @click="handleSearch">搜索</el-button>
        </el-input>
        <div class="tips">
          <i class="el-icon-info"></i>
          点击地图选择位置，或搜索地址后选择
        </div>
      </div>
      <div ref="mapContainer" class="map-container"></div>
      <div v-if="selectedLocation" class="selected-info">
        <el-descriptions :column="3" border size="small">
          <el-descriptions-item label="地址">{{ selectedLocation.address }}</el-descriptions-item>
          <el-descriptions-item label="经度">{{ selectedLocation.longitude }}</el-descriptions-item>
          <el-descriptions-item label="纬度">{{ selectedLocation.latitude }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </div>
    <div slot="footer" class="dialog-footer">
      <el-button @click="handleClose">取 消</el-button>
      <el-button type="primary" :disabled="!selectedLocation" @click="handleConfirm">确 定</el-button>
    </div>
  </el-dialog>
</template>

<script>
export default {
  name: 'MapPicker',
  props: {
    value: {
      type: Boolean,
      default: false
    },
    initialLongitude: {
      type: [Number, String],
      default: null
    },
    initialLatitude: {
      type: [Number, String],
      default: null
    },
    initialAddress: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      visible: false,
      map: null,
      geocoder: null,
      marker: null,
      searchAddress: '',
      selectedLocation: null
    }
  },
  watch: {
    value(val) {
      this.visible = val
      if (val) {
        this.$nextTick(() => {
          this.initMap()
        })
      }
    },
    visible(val) {
      this.$emit('input', val)
    }
  },
  methods: {
    initMap() {
      if (this.map) {
        this.map.destroy()
        this.map = null
      }

      const defaultLng = this.initialLongitude || 120.1551
      const defaultLat = this.initialLatitude || 30.2741

      this.map = new AMap.Map(this.$refs.mapContainer, {
        zoom: 13,
        center: [defaultLng, defaultLat],
        viewMode: '2D'
      })

      this.geocoder = new AMap.Geocoder({
        city: '全国',
        radius: 1000
      })

      this.map.on('click', this.handleMapClick)

      if (this.initialLongitude && this.initialLatitude) {
        this.addMarker(defaultLng, defaultLat)
        this.getAddress(defaultLng, defaultLat)
      }

      this.map.plugin(['AMap.ToolBar', 'AMap.Scale'], () => {
        this.map.addControl(new AMap.ToolBar())
        this.map.addControl(new AMap.Scale())
      })
    },
    handleMapClick(e) {
      const lng = e.lnglat.getLng()
      const lat = e.lnglat.getLat()
      this.addMarker(lng, lat)
      this.getAddress(lng, lat)
    },
    addMarker(lng, lat) {
      if (this.marker) {
        this.map.remove(this.marker)
      }
      this.marker = new AMap.Marker({
        position: [lng, lat],
        title: '选中位置',
        icon: new AMap.Icon({
          size: new AMap.Size(25, 34),
          image: 'https://a.amap.com/jsapi_demos/static/demo-center/icons/poi-marker-default.png',
          imageSize: new AMap.Size(25, 34)
        }),
        offset: new AMap.Pixel(-12, -34)
      })
      this.map.add(this.marker)
      this.map.setCenter([lng, lat])
    },
    getAddress(lng, lat) {
      this.geocoder.getAddress([lng, lat], (status, result) => {
        if (status === 'complete' && result.regeocode) {
          this.selectedLocation = {
            longitude: Number(lng.toFixed(6)),
            latitude: Number(lat.toFixed(6)),
            address: result.regeocode.formattedAddress
          }
        } else {
          this.selectedLocation = {
            longitude: Number(lng.toFixed(6)),
            latitude: Number(lat.toFixed(6)),
            address: `(${lng.toFixed(6)}, ${lat.toFixed(6)})`
          }
        }
      })
    },
    handleSearch() {
      if (!this.searchAddress.trim()) {
        this.$message.warning('请输入搜索地址')
        return
      }
      this.geocoder.getLocation(this.searchAddress, (status, result) => {
        if (status === 'complete' && result.geocodes.length > 0) {
          const location = result.geocodes[0].location
          const lng = location.getLng()
          const lat = location.getLat()
          this.addMarker(lng, lat)
          this.selectedLocation = {
            longitude: Number(lng.toFixed(6)),
            latitude: Number(lat.toFixed(6)),
            address: result.geocodes[0].formattedAddress
          }
        } else {
          this.$message.error('未找到该地址，请尝试其他关键词')
        }
      })
    },
    handleConfirm() {
      if (this.selectedLocation) {
        this.$emit('confirm', this.selectedLocation)
        this.visible = false
      }
    },
    handleClose() {
      this.visible = false
      this.searchAddress = ''
      this.selectedLocation = null
      if (this.map) {
        this.map.destroy()
        this.map = null
      }
      if (this.marker) {
        this.marker = null
      }
    }
  },
  beforeDestroy() {
    if (this.map) {
      this.map.destroy()
    }
  }
}
</script>

<style lang="scss" scoped>
.map-picker {
  .search-bar {
    margin-bottom: 15px;
    display: flex;
    align-items: center;
    gap: 15px;

    .search-input {
      flex: 1;
    }

    .tips {
      font-size: 12px;
      color: #909399;
      white-space: nowrap;
    }
  }

  .map-container {
    width: 100%;
    height: 500px;
    border-radius: 4px;
    overflow: hidden;
    border: 1px solid #e4e7ed;
  }

  .selected-info {
    margin-top: 15px;

    ::v-deep .el-descriptions__label {
      background: #f5f7fa;
      width: 80px;
    }
  }
}
</style>
