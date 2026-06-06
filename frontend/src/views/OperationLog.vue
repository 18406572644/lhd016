<template>
  <div class="operation-log">
    <el-card>
      <div class="search-bar">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="操作人">
            <el-input v-model="searchForm.operator" placeholder="请输入操作人" clearable></el-input>
          </el-form-item>
          <el-form-item label="模块">
            <el-select v-model="searchForm.module" placeholder="全部模块" clearable style="width: 150px;">
              <el-option
                v-for="item in moduleOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="操作类型">
            <el-select v-model="searchForm.operationType" placeholder="全部类型" clearable style="width: 150px;">
              <el-option
                v-for="item in operationTypeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="操作时间">
            <el-date-picker
              v-model="searchForm.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
              clearable
            ></el-date-picker>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
            <el-button icon="el-icon-refresh" @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="70"></el-table-column>
        <el-table-column prop="operator" label="操作人" width="120"></el-table-column>
        <el-table-column prop="operateTime" label="操作时间" width="170">
          <template slot-scope="scope">{{ formatDate(scope.row.operateTime) }}</template>
        </el-table-column>
        <el-table-column prop="module" label="模块" width="120">
          <template slot-scope="scope">
            <el-tag type="primary" size="small">{{ getModuleLabel(scope.row.module) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operationType" label="操作类型" width="120">
          <template slot-scope="scope">
            <el-tag :type="getOperationTypeTag(scope.row.operationType)" size="small">
              {{ getOperationTypeLabel(scope.row.operationType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ipAddress" label="IP地址" width="140"></el-table-column>
        <el-table-column prop="reason" label="原因" min-width="150" show-overflow-tooltip>
          <template slot-scope="scope">{{ scope.row.reason || '-' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="110" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" icon="el-icon-view" @click="handleViewDetail(scope.row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pageInfo.pageNum"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pageInfo.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pageInfo.total"
          background
        ></el-pagination>
      </div>
    </el-card>

    <el-dialog title="操作日志详情" :visible.sync="detailVisible" width="800px" :close-on-click-modal="false">
      <el-descriptions :column="2" border size="small" class="detail-desc">
        <el-descriptions-item label="ID">{{ currentLog.id }}</el-descriptions-item>
        <el-descriptions-item label="操作人">{{ currentLog.operator }}</el-descriptions-item>
        <el-descriptions-item label="操作时间" :span="2">{{ formatDate(currentLog.operateTime) }}</el-descriptions-item>
        <el-descriptions-item label="模块">
          <el-tag type="primary" size="small">{{ getModuleLabel(currentLog.module) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="操作类型">
          <el-tag :type="getOperationTypeTag(currentLog.operationType)" size="small">
            {{ getOperationTypeLabel(currentLog.operationType) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="IP地址">{{ currentLog.ipAddress }}</el-descriptions-item>
        <el-descriptions-item label="请求方法">{{ currentLog.requestMethod || '-' }}</el-descriptions-item>
        <el-descriptions-item label="请求URL" :span="2">{{ currentLog.requestUrl || '-' }}</el-descriptions-item>
        <el-descriptions-item label="耗时">{{ currentLog.costTime ? currentLog.costTime + 'ms' : '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentLog.success ? 'success' : 'danger'" size="small">
            {{ currentLog.success ? '成功' : '失败' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="原因" :span="2">{{ currentLog.reason || '-' }}</el-descriptions-item>
      </el-descriptions>

      <div class="operation-content-section" v-if="parsedContent">
        <div class="section-title">
          <i class="el-icon-document"></i> 操作内容
        </div>
        <div class="content-tabs" v-if="hasBeforeAfter">
          <el-radio-group v-model="activeContentTab" size="small">
            <el-radio-button label="diff">变更对比</el-radio-button>
            <el-radio-button label="before">修改前</el-radio-button>
            <el-radio-button label="after">修改后</el-radio-button>
            <el-radio-button label="raw">原始数据</el-radio-button>
          </el-radio-group>
        </div>
        <div class="content-display">
          <template v-if="activeContentTab === 'diff' && hasBeforeAfter">
            <div class="diff-container">
              <div class="diff-column">
                <div class="diff-header before">修改前</div>
                <pre class="json-display before" v-html="highlightJson(parsedContent.before, 'before')"></pre>
              </div>
              <div class="diff-column">
                <div class="diff-header after">修改后</div>
                <pre class="json-display after" v-html="highlightJson(parsedContent.after, 'after')"></pre>
              </div>
            </div>
          </template>
          <template v-else-if="activeContentTab === 'before' && hasBeforeAfter">
            <pre class="json-display" v-html="formatJson(parsedContent.before)"></pre>
          </template>
          <template v-else-if="activeContentTab === 'after' && hasBeforeAfter">
            <pre class="json-display" v-html="formatJson(parsedContent.after)"></pre>
          </template>
          <template v-else>
            <pre class="json-display" v-html="formatJson(parsedContent)"></pre>
          </template>
        </div>
      </div>
      <div class="operation-content-section" v-else-if="currentLog.operationContent">
        <div class="section-title">
          <i class="el-icon-document"></i> 操作内容
        </div>
        <pre class="json-display">{{ currentLog.operationContent }}</pre>
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="detailVisible = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { formatDate } from '@/utils'
import {
  getOperationLogList,
  getModuleOptions,
  getOperationTypeOptions
} from '@/api/operationLog'

export default {
  name: 'OperationLog',
  data() {
    return {
      loading: false,
      detailVisible: false,
      currentLog: {},
      parsedContent: null,
      activeContentTab: 'diff',
      moduleOptions: [],
      operationTypeOptions: [],
      searchForm: {
        operator: '',
        module: '',
        operationType: '',
        dateRange: []
      },
      pageInfo: {
        pageNum: 1,
        pageSize: 10,
        total: 0
      },
      tableData: []
    }
  },
  computed: {
    hasBeforeAfter() {
      return this.parsedContent &&
        typeof this.parsedContent === 'object' &&
        ('before' in this.parsedContent || 'after' in this.parsedContent)
    }
  },
  mounted() {
    this.loadModuleOptions()
    this.loadOperationTypeOptions()
    this.loadData()
  },
  methods: {
    formatDate,
    async loadModuleOptions() {
      try {
        const res = await getModuleOptions()
        if (res.code === 200) {
          this.moduleOptions = res.data || []
        }
      } catch (e) {
        console.error('加载模块选项失败', e)
      }
    },
    async loadOperationTypeOptions() {
      try {
        const res = await getOperationTypeOptions()
        if (res.code === 200) {
          this.operationTypeOptions = res.data || []
        }
      } catch (e) {
        console.error('加载操作类型选项失败', e)
      }
    },
    async loadData() {
      this.loading = true
      try {
        const params = {
          pageNum: this.pageInfo.pageNum,
          pageSize: this.pageInfo.pageSize,
          operator: this.searchForm.operator || undefined,
          module: this.searchForm.module || undefined,
          operationType: this.searchForm.operationType || undefined,
          startTime: this.searchForm.dateRange && this.searchForm.dateRange[0] ? this.searchForm.dateRange[0] : undefined,
          endTime: this.searchForm.dateRange && this.searchForm.dateRange[1] ? this.searchForm.dateRange[1] : undefined
        }
        const data = await getOperationLogList(params)
        this.tableData = data.list
        this.pageInfo.total = data.total
      } catch (e) {
        this.$message.error('加载操作日志失败')
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.pageInfo.pageNum = 1
      this.loadData()
    },
    handleReset() {
      this.searchForm = {
        operator: '',
        module: '',
        operationType: '',
        dateRange: []
      }
      this.pageInfo.pageNum = 1
      this.loadData()
    },
    handleSizeChange(val) {
      this.pageInfo.pageSize = val
      this.loadData()
    },
    handleCurrentChange(val) {
      this.pageInfo.pageNum = val
      this.loadData()
    },
    getModuleLabel(value) {
      const item = this.moduleOptions.find(opt => opt.value === value)
      return item ? item.label : value
    },
    getOperationTypeLabel(value) {
      const item = this.operationTypeOptions.find(opt => opt.value === value)
      return item ? item.label : value
    },
    getOperationTypeTag(value) {
      const tagMap = {
        'create': 'success',
        'add': 'success',
        'insert': 'success',
        'update': 'warning',
        'edit': 'warning',
        'modify': 'warning',
        'delete': 'danger',
        'remove': 'danger',
        'query': 'info',
        'select': 'info',
        'get': 'info',
        'export': 'primary',
        'import': 'primary',
        'login': 'success',
        'logout': 'info'
      }
      return tagMap[value] || 'primary'
    },
    handleViewDetail(row) {
      this.currentLog = { ...row }
      this.parsedContent = null
      this.activeContentTab = 'diff'
      
      if (row.operationContent) {
        try {
          this.parsedContent = JSON.parse(row.operationContent)
        } catch (e) {
          this.parsedContent = null
        }
      }
      
      this.detailVisible = true
    },
    formatJson(obj) {
      if (!obj) return ''
      try {
        const jsonStr = typeof obj === 'string' ? obj : JSON.stringify(obj, null, 2)
        return this.syntaxHighlight(jsonStr)
      } catch (e) {
        return String(obj)
      }
    },
    syntaxHighlight(json) {
      if (typeof json !== 'string') {
        json = JSON.stringify(json, undefined, 2)
      }
      return json.replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, (match) => {
        let cls = 'number'
        if (/^"/.test(match)) {
          if (/:$/.test(match)) {
            cls = 'key'
          } else {
            cls = 'string'
          }
        } else if (/true|false/.test(match)) {
          cls = 'boolean'
        } else if (/null/.test(match)) {
          cls = 'null'
        }
        return `<span class="json-${cls}">${match}</span>`
      })
    },
    highlightJson(obj, type) {
      if (!obj) return ''
      const jsonStr = typeof obj === 'string' ? obj : JSON.stringify(obj, null, 2)
      return this.syntaxHighlight(jsonStr)
    }
  }
}
</script>

<style lang="scss" scoped>
.operation-log {
  .search-bar {
    margin-bottom: 20px;
    padding: 16px;
    background: linear-gradient(135deg, #E8F5F2 0%, #F1F8E9 100%);
    border-radius: $border-radius;

    .search-form {
      margin-bottom: 0;

      .el-form-item {
        margin-bottom: 10px;
      }
    }
  }

  .pagination-container {
    display: flex;
    justify-content: flex-end;
    padding-top: 20px;
  }

  .detail-desc {
    margin-bottom: 20px;
  }

  .operation-content-section {
    margin-top: 10px;

    .section-title {
      font-weight: bold;
      color: #409EFF;
      margin-bottom: 10px;
      font-size: 14px;

      i {
        margin-right: 5px;
      }
    }

    .content-tabs {
      margin-bottom: 10px;
    }

    .content-display {
      background: #f5f7fa;
      border-radius: 4px;
      padding: 15px;
      max-height: 400px;
      overflow-y: auto;

      .diff-container {
        display: flex;
        gap: 15px;

        .diff-column {
          flex: 1;

          .diff-header {
            padding: 8px 12px;
            border-radius: 4px 4px 0 0;
            font-weight: bold;
            font-size: 13px;

            &.before {
              background: #fff1f0;
              color: #f5222d;
              border: 1px solid #ffa39e;
              border-bottom: none;
            }

            &.after {
              background: #f6ffed;
              color: #52c41a;
              border: 1px solid #b7eb8f;
              border-bottom: none;
            }
          }

          .json-display {
            margin: 0;
            border-radius: 0 0 4px 4px;
            border: 1px solid #e4e7ed;
            border-top: none;
            max-height: 350px;

            &.before {
              background: #fff;
              border-color: #ffa39e;
            }

            &.after {
              background: #fff;
              border-color: #b7eb8f;
            }
          }
        }
      }
    }
  }
}

.json-display {
  margin: 0;
  padding: 15px;
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 13px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-all;

  .json-key {
    color: #92278f;
    font-weight: bold;
  }

  .json-string {
    color: #3ab54a;
  }

  .json-number {
    color: #25aae2;
  }

  .json-boolean {
    color: #f98280;
  }

  .json-null {
    color: #898989;
  }
}
</style>
