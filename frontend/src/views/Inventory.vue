<template>
  <div class="inventory">
    <el-card>
      <div class="search-bar">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="配件名称">
            <el-input v-model="searchForm.partName" placeholder="请输入配件名称" clearable></el-input>
          </el-form-item>
          <el-form-item label="分类">
            <el-select v-model="searchForm.category" placeholder="全部分类" clearable>
              <el-option label="轮胎类" value="轮胎类"></el-option>
              <el-option label="制动系统" value="制动系统"></el-option>
              <el-option label="传动系统" value="传动系统"></el-option>
              <el-option label="补给品" value="补给品"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="所属站点">
            <el-select v-model="searchForm.supplyPointId" placeholder="全部站点" clearable>
              <el-option v-for="point in supplyPoints" :key="point.id" :label="point.name" :value="point.id"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="库存状态">
            <el-select v-model="searchForm.stockStatus" placeholder="全部状态" clearable>
              <el-option label="低库存" value="low"></el-option>
              <el-option label="正常" value="normal"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
            <el-button icon="el-icon-refresh" @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
        <div class="action-bar">
          <el-button type="warning" icon="el-icon-alarm-clock" @click="showWarningOnly = !showWarningOnly">
            {{ showWarningOnly ? '显示全部' : '仅看预警' }}
            <el-tag v-if="warningCount > 0" type="danger" size="mini" style="margin-left: 4px;">{{ warningCount }}</el-tag>
          </el-button>
          <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增配件</el-button>
        </div>
      </div>

      <el-table :data="filteredTableData" border stripe v-loading="loading" :row-class-name="getRowClassName">
        <el-table-column prop="id" label="ID" width="70"></el-table-column>
        <el-table-column prop="partCode" label="配件编码" width="110"></el-table-column>
        <el-table-column prop="partName" label="配件名称" min-width="130">
          <template slot-scope="scope">
            <span v-if="isLowStock(scope.row)" class="warning-part pulse-animation">
              <i class="el-icon-warning"></i> {{ scope.row.partName }}
            </span>
            <span v-else>{{ scope.row.partName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="category" label="分类" width="110">
          <template slot-scope="scope">
            <el-tag size="small">{{ scope.row.category }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="supplyPointName" label="所属站点" width="150"></el-table-column>
        <el-table-column label="库存信息" width="200">
          <template slot-scope="scope">
            <div class="stock-info">
              <div class="stock-value">
                <span :class="{ 'danger-text': isLowStock(scope.row) }">
                  {{ scope.row.stockQuantity }}
                </span>
                <span class="stock-unit">{{ scope.row.unit }}</span>
                <span class="stock-threshold">
                  (预警: {{ scope.row.warningThreshold }}{{ scope.row.unit }})
                </span>
              </div>
              <el-progress
                :percentage="getStockPercent(scope.row)"
                :status="isLowStock(scope.row) ? 'exception' : 'success'"
                :stroke-width="6"
              ></el-progress>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="unitPrice" label="单价" width="100">
          <template slot-scope="scope">¥ {{ scope.row.unitPrice }}</template>
        </el-table-column>
        <el-table-column prop="supplier" label="供应商" width="120"></el-table-column>
        <el-table-column label="库存状态" width="100">
          <template slot-scope="scope">
            <el-tag v-if="isLowStock(scope.row)" type="danger" size="small">低库存</el-tag>
            <el-tag v-else type="success" size="small">正常</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" icon="el-icon-circle-plus" @click="handleStockIn(scope.row)">入库</el-button>
            <el-button type="text" icon="el-icon-minus" class="warning-btn" @click="handleStockOut(scope.row)">出库</el-button>
            <el-button type="text" icon="el-icon-edit" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="text" icon="el-icon-delete" class="danger-btn" @click="handleDelete(scope.row)">删除</el-button>
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
          :total="filteredTableData.length"
          background
        ></el-pagination>
      </div>
    </el-card>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="700px" :close-on-click-modal="false">
      <el-form :model="form" :rules="formRules" ref="formRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="配件编码" prop="partCode">
              <el-input v-model="form.partCode" placeholder="如：P001"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="配件名称" prop="partName">
              <el-input v-model="form.partName" placeholder="请输入配件名称"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="分类" prop="category">
              <el-select v-model="form.category" placeholder="请选择分类" style="width: 100%;">
                <el-option label="轮胎类" value="轮胎类"></el-option>
                <el-option label="制动系统" value="制动系统"></el-option>
                <el-option label="传动系统" value="传动系统"></el-option>
                <el-option label="补给品" value="补给品"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="计量单位" prop="unit">
              <el-input v-model="form.unit" placeholder="如：条、个、瓶"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属站点" prop="supplyPointId">
              <el-select v-model="form.supplyPointId" placeholder="请选择站点" style="width: 100%;">
                <el-option v-for="point in supplyPoints" :key="point.id" :label="point.name" :value="point.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="供应商">
              <el-input v-model="form.supplier" placeholder="请输入供应商"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="单价" prop="unitPrice">
              <el-input-number v-model="form.unitPrice" :min="0" :precision="2" :step="1" style="width: 100%;"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="库存数量" prop="stockQuantity">
              <el-input-number v-model="form.stockQuantity" :min="0" style="width: 100%;"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="预警阈值" prop="warningThreshold">
              <el-input-number v-model="form.warningThreshold" :min="1" style="width: 100%;"></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog :title="stockDialogTitle" :visible.sync="stockDialogVisible" width="500px">
      <el-form :model="stockForm" :rules="stockRules" ref="stockFormRef" label-width="80px">
        <el-form-item label="配件名称">
          <el-input v-model="currentPart.partName" disabled></el-input>
        </el-form-item>
        <el-form-item label="当前库存">
          <el-input :value="currentPart.stockQuantity + ' ' + currentPart.unit" disabled></el-input>
        </el-form-item>
        <el-form-item label="操作数量" prop="quantity">
          <el-input-number v-model="stockForm.quantity" :min="1" style="width: 100%;"></el-input-number>
        </el-form-item>
        <el-form-item label="操作人" prop="operator">
          <el-input v-model="stockForm.operator" placeholder="请输入操作人姓名"></el-input>
        </el-form-item>
        <el-form-item label="备注">
          <el-input type="textarea" v-model="stockForm.remark" :rows="2" placeholder="请输入备注信息"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="stockDialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="stockSubmitLoading" @click="handleStockSubmit">
          {{ stockType === 'in' ? '确认入库' : '确认出库' }}
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { formatDate, isLowStock as checkLowStock } from '@/utils'

export default {
  name: 'Inventory',
  data() {
    return {
      loading: false,
      submitLoading: false,
      stockSubmitLoading: false,
      dialogVisible: false,
      stockDialogVisible: false,
      showWarningOnly: false,
      dialogType: 'add',
      stockType: 'in',
      currentPart: {},
      searchForm: {
        partName: '',
        category: '',
        supplyPointId: null,
        stockStatus: ''
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
      form: {
        id: null,
        partCode: '',
        partName: '',
        category: '',
        unit: '',
        stockQuantity: 0,
        warningThreshold: 10,
        unitPrice: 0,
        supplier: '',
        supplyPointId: null,
        supplyPointName: ''
      },
      stockForm: {
        quantity: 1,
        operator: '',
        remark: ''
      },
      formRules: {
        partCode: [{ required: true, message: '请输入配件编码', trigger: 'blur' }],
        partName: [{ required: true, message: '请输入配件名称', trigger: 'blur' }],
        category: [{ required: true, message: '请选择分类', trigger: 'change' }],
        unit: [{ required: true, message: '请输入计量单位', trigger: 'blur' }],
        stockQuantity: [{ required: true, message: '请输入库存数量', trigger: 'blur' }],
        warningThreshold: [{ required: true, message: '请输入预警阈值', trigger: 'blur' }]
      },
      stockRules: {
        quantity: [{ required: true, message: '请输入操作数量', trigger: 'blur' }],
        operator: [{ required: true, message: '请输入操作人', trigger: 'blur' }]
      }
    }
  },
  computed: {
    dialogTitle() {
      return this.dialogType === 'add' ? '新增配件' : '编辑配件'
    },
    stockDialogTitle() {
      return this.stockType === 'in' ? '配件入库' : '配件出库'
    },
    warningCount() {
      return this.tableData.filter(item => this.isLowStock(item)).length
    },
    filteredTableData() {
      let data = [...this.tableData]
      if (this.showWarningOnly) {
        data = data.filter(item => this.isLowStock(item))
      }
      if (this.searchForm.partName) {
        data = data.filter(item => item.partName.includes(this.searchForm.partName))
      }
      if (this.searchForm.category) {
        data = data.filter(item => item.category === this.searchForm.category)
      }
      if (this.searchForm.supplyPointId) {
        data = data.filter(item => item.supplyPointId === this.searchForm.supplyPointId)
      }
      if (this.searchForm.stockStatus === 'low') {
        data = data.filter(item => this.isLowStock(item))
      } else if (this.searchForm.stockStatus === 'normal') {
        data = data.filter(item => !this.isLowStock(item))
      }
      return data
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    formatDate,
    isLowStock(item) {
      return checkLowStock(item.stockQuantity, item.warningThreshold)
    },
    getStockPercent(item) {
      const max = item.warningThreshold * 3
      return Math.min(100, Math.round((item.stockQuantity / max) * 100))
    },
    getRowClassName({ row }) {
      if (this.isLowStock(row)) return 'danger-row'
      return ''
    },
    loadData() {
      this.loading = true
      this.tableData = [
        { id: 1, partCode: 'P001', partName: '内胎', category: '轮胎类', unit: '条', stockQuantity: 5, warningThreshold: 10, unitPrice: 35.00, supplier: '永久配件', supplyPointId: 1, supplyPointName: '滨江公园补给站' },
        { id: 2, partCode: 'P002', partName: '外胎', category: '轮胎类', unit: '条', stockQuantity: 12, warningThreshold: 8, unitPrice: 85.00, supplier: '永久配件', supplyPointId: 1, supplyPointName: '滨江公园补给站' },
        { id: 3, partCode: 'P003', partName: '刹车皮', category: '制动系统', unit: '对', stockQuantity: 20, warningThreshold: 10, unitPrice: 25.00, supplier: '捷安特配件', supplyPointId: 1, supplyPointName: '滨江公园补给站' },
        { id: 4, partCode: 'P004', partName: '刹车线', category: '制动系统', unit: '根', stockQuantity: 8, warningThreshold: 15, unitPrice: 15.00, supplier: '捷安特配件', supplyPointId: 2, supplyPointName: '西湖休息站' },
        { id: 5, partCode: 'P005', partName: '链条', category: '传动系统', unit: '条', stockQuantity: 3, warningThreshold: 5, unitPrice: 65.00, supplier: '禧玛诺', supplyPointId: 1, supplyPointName: '滨江公园补给站' },
        { id: 6, partCode: 'P006', partName: '脚踏', category: '传动系统', unit: '副', stockQuantity: 18, warningThreshold: 10, unitPrice: 45.00, supplier: '禧玛诺', supplyPointId: 2, supplyPointName: '西湖休息站' },
        { id: 7, partCode: 'P007', partName: '矿泉水', category: '补给品', unit: '瓶', stockQuantity: 200, warningThreshold: 50, unitPrice: 2.00, supplier: '农夫山泉', supplyPointId: 1, supplyPointName: '滨江公园补给站' },
        { id: 8, partCode: 'P008', partName: '能量棒', category: '补给品', unit: '个', stockQuantity: 15, warningThreshold: 30, unitPrice: 8.00, supplier: '康比特', supplyPointId: 1, supplyPointName: '滨江公园补给站' }
      ]
      this.loading = false
    },
    handleSearch() {
      this.pageInfo.pageNum = 1
      this.$message.success('搜索完成')
    },
    handleReset() {
      this.searchForm = {
        partName: '',
        category: '',
        supplyPointId: null,
        stockStatus: ''
      }
      this.showWarningOnly = false
    },
    handleSizeChange(val) {
      this.pageInfo.pageSize = val
    },
    handleCurrentChange(val) {
      this.pageInfo.pageNum = val
    },
    handleAdd() {
      this.dialogType = 'add'
      this.form = {
        id: null,
        partCode: '',
        partName: '',
        category: '',
        unit: '',
        stockQuantity: 0,
        warningThreshold: 10,
        unitPrice: 0,
        supplier: '',
        supplyPointId: null,
        supplyPointName: ''
      }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.formRef && this.$refs.formRef.clearValidate()
      })
    },
    handleEdit(row) {
      this.dialogType = 'edit'
      this.form = { ...row }
      this.dialogVisible = true
    },
    handleDelete(row) {
      this.$confirm(`确定要删除配件「${row.partName}」吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.tableData = this.tableData.filter(item => item.id !== row.id)
        this.$message.success('删除成功')
      }).catch(() => {})
    },
    handleStockIn(row) {
      this.stockType = 'in'
      this.currentPart = { ...row }
      this.stockForm = { quantity: 1, operator: '', remark: '' }
      this.stockDialogVisible = true
    },
    handleStockOut(row) {
      if (row.stockQuantity <= 0) {
        this.$message.warning('库存不足，无法出库')
        return
      }
      this.stockType = 'out'
      this.currentPart = { ...row }
      this.stockForm = { quantity: 1, operator: '', remark: '' }
      this.stockDialogVisible = true
    },
    handleStockSubmit() {
      this.$refs.stockFormRef.validate(valid => {
        if (valid) {
          if (this.stockType === 'out' && this.stockForm.quantity > this.currentPart.stockQuantity) {
            this.$message.error('出库数量不能大于当前库存')
            return
          }
          this.stockSubmitLoading = true
          setTimeout(() => {
            const index = this.tableData.findIndex(item => item.id === this.currentPart.id)
            if (index > -1) {
              if (this.stockType === 'in') {
                this.tableData[index].stockQuantity += this.stockForm.quantity
              } else {
                this.tableData[index].stockQuantity -= this.stockForm.quantity
              }
            }
            this.$message.success(this.stockType === 'in' ? '入库成功' : '出库成功')
            this.stockSubmitLoading = false
            this.stockDialogVisible = false
          }, 500)
        }
      })
    },
    handleSubmit() {
      this.$refs.formRef.validate(valid => {
        if (valid) {
          this.submitLoading = true
          setTimeout(() => {
            const supplyPoint = this.supplyPoints.find(p => p.id === this.form.supplyPointId)
            const data = {
              ...this.form,
              supplyPointName: supplyPoint ? supplyPoint.name : ''
            }
            if (this.dialogType === 'add') {
              this.tableData.unshift({
                ...data,
                id: Date.now()
              })
              this.$message.success('新增成功')
            } else {
              const index = this.tableData.findIndex(item => item.id === this.form.id)
              if (index > -1) {
                this.tableData[index] = { ...data }
              }
              this.$message.success('更新成功')
            }
            this.submitLoading = false
            this.dialogVisible = false
          }, 500)
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.inventory {
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
      display: flex;
      gap: 10px;
      margin-left: 20px;
    }
  }

  .stock-info {
    .stock-value {
      font-size: 14px;
      margin-bottom: 4px;

      .danger-text {
        color: $danger-color;
        font-weight: 700;
      }

      .stock-unit {
        color: $text-secondary;
        margin-left: 2px;
      }

      .stock-threshold {
        color: $text-light;
        font-size: 12px;
        margin-left: 8px;
      }
    }
  }

  .warning-part {
    color: $danger-color;
    font-weight: 600;

    i {
      margin-right: 4px;
    }
  }

  .pagination-container {
    display: flex;
    justify-content: flex-end;
    padding-top: 20px;
  }

  .danger-btn {
    color: $danger-color;
  }

  .warning-btn {
    color: $warning-color;
  }
}
</style>
