<template>
  <div class="inventory-check">
    <el-card>
      <div class="search-bar">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="盘点单号">
            <el-input v-model="searchForm.checkNo" placeholder="请输入盘点单号" clearable></el-input>
          </el-form-item>
          <el-form-item label="站点">
            <el-select v-model="searchForm.supplyPointId" placeholder="全部站点" clearable>
              <el-option v-for="point in supplyPoints" :key="point.id" :label="point.name" :value="point.id"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="盘点日期">
            <el-date-picker
              v-model="searchForm.checkDate"
              type="date"
              placeholder="选择日期"
              value-format="YYYY-MM-DD"
              style="width: 180px;"
            ></el-date-picker>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="全部状态" clearable>
              <el-option label="草稿" value="draft"></el-option>
              <el-option label="已完成" value="completed"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
            <el-button icon="el-icon-refresh" @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
        <div class="action-bar">
          <el-button type="primary" icon="el-icon-plus" @click="handleCreate">新建盘点</el-button>
        </div>
      </div>

      <el-table :data="filteredTableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="70"></el-table-column>
        <el-table-column prop="checkNo" label="盘点单号" width="160"></el-table-column>
        <el-table-column prop="supplyPointName" label="盘点站点" width="150"></el-table-column>
        <el-table-column prop="checkDate" label="盘点日期" width="120"></el-table-column>
        <el-table-column prop="checker" label="盘点人" width="100"></el-table-column>
        <el-table-column prop="totalItems" label="盘点项数" width="100" align="center"></el-table-column>
        <el-table-column prop="diffCount" label="差异项数" width="100" align="center">
          <template slot-scope="scope">
            <span v-if="scope.row.diffCount > 0" class="diff-text">{{ scope.row.diffCount }}</span>
            <span v-else>{{ scope.row.diffCount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)" size="small">{{ getStatusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160">
          <template slot-scope="scope">{{ formatDate(scope.row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template slot-scope="scope">
            <el-button
              v-if="scope.row.status === 'draft'"
              type="text"
              icon="el-icon-edit"
              @click="handleEdit(scope.row)"
            >编辑</el-button>
            <el-button type="text" icon="el-icon-view" @click="handleView(scope.row)">查看</el-button>
            <el-button
              v-if="scope.row.status === 'draft'"
              type="text"
              icon="el-icon-delete"
              class="danger-btn"
              @click="handleDelete(scope.row)"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pageInfo.pageNum"
          :page-sizes="[10, 20, 50]"
          :page-size="pageInfo.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pageInfo.total"
          background
        ></el-pagination>
      </div>
    </el-card>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="900px" :close-on-click-modal="false">
      <el-form :model="form" :rules="formRules" ref="formRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="盘点单号">
              <el-input v-model="form.checkNo" disabled></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="盘点站点" prop="supplyPointId">
              <el-select v-model="form.supplyPointId" placeholder="请选择站点" style="width: 100%;" @change="loadParts">
                <el-option v-for="point in supplyPoints" :key="point.id" :label="point.name" :value="point.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="盘点日期" prop="checkDate">
              <el-date-picker
                v-model="form.checkDate"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
                style="width: 100%;"
              ></el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="盘点人" prop="checker">
              <el-input v-model="form.checker" placeholder="请输入盘点人姓名"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="备注">
              <el-input v-model="form.remark" placeholder="请输入备注信息"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">
          <span><i class="el-icon-goods"></i> 盘点明细</span>
        </el-divider>

        <el-table :data="form.details" border size="small">
          <el-table-column prop="partName" label="配件名称" min-width="140"></el-table-column>
          <el-table-column prop="systemQuantity" label="系统库存" width="110" align="center"></el-table-column>
          <el-table-column label="实盘数量" width="140">
            <template slot-scope="scope">
              <el-input-number
                v-model="scope.row.actualQuantity"
                :min="0"
                size="small"
                :controls="false"
                style="width: 100%;"
                @change="calculateDiff(scope.row)"
              ></el-input-number>
            </template>
          </el-table-column>
          <el-table-column label="差异数量" width="110" align="center">
            <template slot-scope="scope">
              <span :class="{ 'diff-text': scope.row.diffQuantity !== 0 }">
                {{ scope.row.diffQuantity > 0 ? '+' + scope.row.diffQuantity : scope.row.diffQuantity }}
              </span>
            </template>
          </el-table-column>
        </el-table>

        <div class="summary-info" v-if="form.details.length > 0">
          <el-row :gutter="20">
            <el-col :span="8">
              <div class="summary-item">
                <span class="label">总项数：</span>
                <span class="value">{{ form.details.length }}</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="summary-item">
                <span class="label">差异项数：</span>
                <span class="value diff-text">{{ diffCount }}</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="summary-item">
                <span class="label">状态：</span>
                <el-tag :type="getStatusType(form.status)" size="small">{{ getStatusText(form.status) }}</el-tag>
              </div>
            </el-col>
          </el-row>
        </div>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="info" :loading="submitLoading" @click="handleSaveDraft">保存草稿</el-button>
        <el-button type="primary" :loading="completeLoading" @click="handleComplete">完成盘点</el-button>
      </div>
    </el-dialog>

    <el-dialog title="盘点详情" :visible.sync="detailVisible" width="900px">
      <el-descriptions :column="3" border size="small">
        <el-descriptions-item label="盘点单号">{{ currentRow.checkNo }}</el-descriptions-item>
        <el-descriptions-item label="盘点站点">{{ currentRow.supplyPointName }}</el-descriptions-item>
        <el-descriptions-item label="盘点日期">{{ currentRow.checkDate }}</el-descriptions-item>
        <el-descriptions-item label="盘点人">{{ currentRow.checker }}</el-descriptions-item>
        <el-descriptions-item label="总项数">{{ currentRow.totalItems }}</el-descriptions-item>
        <el-descriptions-item label="差异项数">
          <span v-if="currentRow.diffCount > 0" class="diff-text">{{ currentRow.diffCount }}</span>
          <span v-else>{{ currentRow.diffCount }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentRow.status)" size="small">{{ getStatusText(currentRow.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentRow.remark || '暂无' }}</el-descriptions-item>
      </el-descriptions>

      <el-divider content-position="left">
        <span><i class="el-icon-goods"></i> 盘点明细</span>
      </el-divider>

      <el-table :data="currentRow.details || []" border size="small">
        <el-table-column prop="partName" label="配件名称" min-width="140"></el-table-column>
        <el-table-column prop="systemQuantity" label="系统库存" width="120" align="center"></el-table-column>
        <el-table-column prop="actualQuantity" label="实盘数量" width="120" align="center"></el-table-column>
        <el-table-column label="差异数量" width="120" align="center">
          <template slot-scope="scope">
            <span :class="{ 'diff-text': scope.row.diffQuantity !== 0 }">
              {{ scope.row.diffQuantity > 0 ? '+' + scope.row.diffQuantity : scope.row.diffQuantity }}
            </span>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import { formatDate, getStatusType, getStatusText, generateCheckNo } from '@/utils'
import { sensitiveConfirm } from '@/utils/sensitiveConfirm'
import { getInventoryCheckList, getInventoryCheck, createInventoryCheck, completeInventoryCheck, deleteInventoryCheck } from '@/api/inventoryCheck'

export default {
  name: 'InventoryCheck',
  data() {
    return {
      loading: false,
      submitLoading: false,
      completeLoading: false,
      dialogVisible: false,
      detailVisible: false,
      dialogType: 'add',
      currentRow: {},
      searchForm: {
        checkNo: '',
        supplyPointId: null,
        checkDate: '',
        status: ''
      },
      pageInfo: {
        pageNum: 1,
        pageSize: 10,
        total: 0
      },
      tableData: [],
      supplyPoints: [
        { id: 1, name: '滨江公园补给站' },
        { id: 2, name: '西湖休息站' },
        { id: 3, name: '科技园区充电站' },
        { id: 4, name: '运河饮水点' }
      ],
      allParts: [
        { id: 1, partCode: 'P001', partName: '内胎', unit: '条', stockQuantity: 5 },
        { id: 2, partCode: 'P002', partName: '外胎', unit: '条', stockQuantity: 12 },
        { id: 3, partCode: 'P003', partName: '刹车皮', unit: '对', stockQuantity: 20 },
        { id: 4, partCode: 'P007', partName: '矿泉水', unit: '瓶', stockQuantity: 200 },
        { id: 5, partCode: 'P008', partName: '能量棒', unit: '个', stockQuantity: 15 }
      ],
      form: {
        id: null,
        checkNo: '',
        supplyPointId: null,
        supplyPointName: '',
        checkDate: '',
        checker: '',
        status: 'draft',
        totalItems: 0,
        diffCount: 0,
        remark: '',
        details: []
      },
      formRules: {
        supplyPointId: [{ required: true, message: '请选择盘点站点', trigger: 'change' }],
        checkDate: [{ required: true, message: '请选择盘点日期', trigger: 'change' }],
        checker: [{ required: true, message: '请输入盘点人', trigger: 'blur' }]
      }
    }
  },
  computed: {
    dialogTitle() {
      return this.dialogType === 'add' ? '新建盘点' : '编辑盘点'
    },
    filteredTableData() {
      let data = [...this.tableData]
      if (this.searchForm.checkNo) {
        data = data.filter(item => item.checkNo.includes(this.searchForm.checkNo))
      }
      if (this.searchForm.supplyPointId) {
        data = data.filter(item => item.supplyPointId === this.searchForm.supplyPointId)
      }
      if (this.searchForm.checkDate) {
        data = data.filter(item => item.checkDate === this.searchForm.checkDate)
      }
      if (this.searchForm.status) {
        data = data.filter(item => item.status === this.searchForm.status)
      }
      return data
    },
    diffCount() {
      return this.form.details.filter(d => d.diffQuantity !== 0).length
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    formatDate,
    getStatusType,
    getStatusText,
    calculateDiff(row) {
      row.diffQuantity = row.actualQuantity - row.systemQuantity
    },
    loadParts() {
      const parts = this.allParts.map(p => ({
        partId: p.id,
        partName: p.partName,
        systemQuantity: p.stockQuantity,
        actualQuantity: p.stockQuantity,
        diffQuantity: 0
      }))
      this.form.details = parts
    },
    async loadData() {
      this.loading = true
      try {
        const params = {
          pageNum: this.pageInfo.pageNum,
          pageSize: this.pageInfo.pageSize,
          ...this.searchForm
        }
        const data = await getInventoryCheckList(params)
        this.tableData = data.list || data
        this.pageInfo.total = data.total || this.tableData.length
      } catch (e) {
        this.$message.error('加载数据失败')
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
        checkNo: '',
        supplyPointId: null,
        checkDate: '',
        status: ''
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
    handleCreate() {
      this.dialogType = 'add'
      this.form = {
        id: null,
        checkNo: generateCheckNo(),
        supplyPointId: null,
        supplyPointName: '',
        checkDate: this.$moment().format('YYYY-MM-DD'),
        checker: '',
        status: 'draft',
        totalItems: 0,
        diffCount: 0,
        remark: '',
        details: []
      }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.formRef && this.$refs.formRef.clearValidate()
      })
    },
    handleEdit(row) {
      this.dialogType = 'edit'
      this.form = { ...row, details: JSON.parse(JSON.stringify(row.details || [])) }
      this.dialogVisible = true
    },
   async handleView(row) {
      try {
        const data = await getInventoryCheck(row.id)
        this.currentRow = data
        this.detailVisible = true
      } catch (e) {
        this.$message.error('获取详情失败')
      }
    },
    async handleDelete(row) {
      try {
        const reason = await sensitiveConfirm({
          title: '删除盘点单',
          message: `您正在删除盘点单「${row.checkNo}」，此操作不可恢复，请输入删除原因：`,
          confirmButtonText: '确认删除',
          type: 'danger'
        })
        await deleteInventoryCheck(row.id, reason)
        this.$message.success('删除成功')
        this.loadData()
      } catch (e) {
        if (e.message !== 'cancel') {
          this.$message.error('删除失败')
        }
      }
    },
    handleSaveDraft() {
      this.$refs.formRef.validate(async valid => {
        if (valid) {
          this.submitLoading = true
          try {
            const supplyPoint = this.supplyPoints.find(p => p.id === this.form.supplyPointId)
            const saveData = {
              ...this.form,
              supplyPointName: supplyPoint ? supplyPoint.name : '',
              totalItems: this.form.details.length,
              diffCount: this.diffCount,
              status: 'draft'
            }
            if (this.dialogType === 'add') {
              await createInventoryCheck(saveData)
            } else {
              await createInventoryCheck(saveData)
            }
            this.$message.success('草稿已保存')
            this.dialogVisible = false
            this.loadData()
          } catch (e) {
            this.$message.error('保存失败')
          } finally {
            this.submitLoading = false
          }
        }
      })
    },
    handleComplete() {
      this.$refs.formRef.validate(valid => {
        if (valid) {
          this.$confirm('确定要完成本次盘点吗？完成后将无法修改。', '确认完成', {
            confirmButtonText: '确定完成',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(async () => {
            this.completeLoading = true
            try {
              const supplyPoint = this.supplyPoints.find(p => p.id === this.form.supplyPointId)
              const saveData = {
                ...this.form,
                supplyPointName: supplyPoint ? supplyPoint.name : '',
                totalItems: this.form.details.length,
                diffCount: this.diffCount,
                status: 'completed'
              }
              if (this.dialogType === 'add') {
                await completeInventoryCheck(null, saveData)
              } else {
                await completeInventoryCheck(this.form.id, saveData)
              }
              this.$message.success('盘点已完成')
              this.dialogVisible = false
              this.loadData()
            } catch (e) {
              this.$message.error('操作失败')
            } finally {
              this.completeLoading = false
            }
          }).catch(() => {})
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.inventory-check {
  .search-bar {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 20px;
    padding: 16px;
    background: linear-gradient(135deg, #E8F5F2 0%, #F1F8E9 100%);
    border-radius: $border-radius;

    .search-form {
      flex: 1;
      margin-bottom: 0;

      .el-form-item {
        margin-bottom: 10px;
      }
    }

    .action-bar {
      margin-left: 20px;
    }
  }

  .diff-text {
    color: $danger-color;
    font-weight: 600;
  }

  .pagination-container {
    display: flex;
    justify-content: flex-end;
    padding-top: 20px;
  }

  .danger-btn {
    color: $danger-color;
  }

  .summary-info {
    margin-top: 16px;
    padding: 12px;
    background: #F5F9F7;
    border-radius: $border-radius;

    .summary-item {
      display: flex;
      align-items: center;
      justify-content: center;

      .label {
        color: $text-secondary;
        margin-right: 8px;
      }

      .value {
        font-size: 18px;
        font-weight: 600;
        color: $primary-color;
      }
    }
  }
}
</style>
