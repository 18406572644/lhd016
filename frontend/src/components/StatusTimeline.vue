<template>
  <div class="status-timeline">
    <el-timeline v-if="records && records.length > 0">
      <el-timeline-item
        v-for="(record, index) in records"
        :key="index"
        :timestamp="formatDate(record.operateTime)"
        :type="getStatusColor(record.toStatus)"
        :color="getStatusColorCode(record.toStatus)"
      >
        <div class="timeline-content">
          <div class="status-flow">
            <el-tag size="small" :type="getStatusTagType(record.fromStatus)">
              {{ getStatusText(record.fromStatus) }}
            </el-tag>
            <i class="el-icon-arrow-right arrow-icon"></i>
            <el-tag size="small" :type="getStatusTagType(record.toStatus)">
              {{ getStatusText(record.toStatus) }}
            </el-tag>
          </div>
          <div class="operator-info">
            <span class="label">操作人：</span>
            <span class="value">{{ record.operator || '系统' }}</span>
          </div>
          <div class="remark" v-if="record.remark">
            <span class="label">备注：</span>
            <span class="value">{{ record.remark }}</span>
          </div>
        </div>
      </el-timeline-item>
    </el-timeline>
    <el-empty v-else description="暂无状态变更记录" :image-size="80"></el-empty>
  </div>
</template>

<script>
import { formatDate, getStatusText } from '@/utils'

export default {
  name: 'StatusTimeline',
  props: {
    records: {
      type: Array,
      default: () => []
    }
  },
  methods: {
    formatDate,
    getStatusText,
    getStatusColor(status) {
      const map = {
        pending: 'info',
        processing: 'primary',
        completed: 'success',
        cancelled: 'danger'
      }
      return map[status] || 'primary'
    },
    getStatusColorCode(status) {
      const map = {
        pending: '#909399',
        processing: '#409EFF',
        completed: '#67C23A',
        cancelled: '#F56C6C'
      }
      return map[status] || '#409EFF'
    },
    getStatusTagType(status) {
      const map = {
        pending: 'info',
        processing: 'primary',
        completed: 'success',
        cancelled: 'danger'
      }
      return map[status] || 'primary'
    }
  }
}
</script>

<style lang="scss" scoped>
.status-timeline {
  padding: 10px 0;

  ::v-deep .el-timeline-item__timestamp {
    color: #909399;
    font-size: 12px;
  }

  ::v-deep .el-timeline-item__wrapper {
    padding-bottom: 20px;
  }

  .timeline-content {
    .status-flow {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 8px;

      .arrow-icon {
        color: #909399;
        font-size: 14px;
      }
    }

    .operator-info {
      margin-bottom: 4px;
      font-size: 13px;

      .label {
        color: #909399;
      }

      .value {
        color: #606266;
        font-weight: 500;
      }
    }

    .remark {
      font-size: 13px;

      .label {
        color: #909399;
      }

      .value {
        color: #606266;
      }
    }
  }
}
</style>
