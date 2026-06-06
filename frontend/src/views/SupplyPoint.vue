<template>
  <div class="supply-point">
    <el-card>
      <div class="search-bar">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="名称">
            <el-input v-model="searchForm.name" placeholder="请输入补给点名称" clearable></el-input>
          </el-form-item>
          <el-form-item label="区域">
            <el-select v-model="searchForm.area" placeholder="全部区域" clearable>
              <el-option v-for="area in areaOptions" :key="area" :label="area" :value="area"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="类型">
            <el-select v-model="searchForm.type" placeholder="全部类型" clearable>
              <el-option label="饮水点" value="饮水点"></el-option>
              <el-option label="休息站" value="休息站"></el-option>
              <el-option label="充电站" value="充电站"></el-option>
              <el-option label="综合补给" value="综合补给"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="全部状态" clearable>
              <el-option label="正常" value="正常"></el-option>
              <el-option label="维护中" value="维护中"></el-option>
              <el-option label="关闭" value="关闭"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
            <el-button icon="el-icon-refresh" @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
        <div class="action-bar">
          <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增补给点</el-button>
        </div>
      </div>

      <el-table
        :data="tableData"
        border
        stripe
        v-loading="loading"
        @selection-change="handleSelectionChange"
        :row-class-name="getRowClassName"
      >
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column prop="id" label="ID" width="70"></el-table-column>
        <el-table-column prop="name" label="补给点名称" min-width="150"></el-table-column>
        <el-table-column prop="address" label="地址" min-width="200" show-overflow-tooltip></el-table-column>
        <el-table-column prop="type" label="类型" width="110">
          <template slot-scope="scope">
            <el-tag :type="getTypeTag(scope.row.type)" size="small">{{ scope.row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="area" label="所属区域" width="100"></el-table-column>
        <el-table-column prop="contactPerson" label="联系人" width="100"></el-table-column>
        <el-table-column prop="contactPhone" label="联系电话" width="130"></el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)" size="small">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160">
          <template slot-scope="scope">{{ formatDate(scope.row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" icon="el-icon-edit" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="text" icon="el-icon-view" @click="handleView(scope.row)">查看</el-button>
            <el-button type="text" icon="el-icon-delete" class="danger-btn" @click="handleDelete(scope.row)">删除</el-button>
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

    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="700px"
      :close-on-click-modal="false"
    >
      <el-form :model="form" :rules="formRules" ref="formRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="补给点名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入补给点名称"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属区域" prop="area">
              <el-select v-model="form.area" placeholder="请选择区域" style="width: 100%;">
                <el-option v-for="area in areaOptions" :key="area" :label="area" :value="area"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="详细地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入详细地址"></el-input>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="补给点类型" prop="type">
              <el-select v-model="form.type" placeholder="请选择类型" style="width: 100%;">
                <el-option label="饮水点" value="饮水点"></el-option>
                <el-option label="休息站" value="休息站"></el-option>
                <el-option label="充电站" value="充电站"></el-option>
                <el-option label="综合补给" value="综合补给"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%;">
                <el-option label="正常" value="正常"></el-option>
                <el-option label="维护中" value="维护中"></el-option>
                <el-option label="关闭" value="关闭"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系人" prop="contactPerson">
              <el-input v-model="form.contactPerson" placeholder="请输入联系人姓名"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="form.contactPhone" placeholder="请输入联系电话"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="经度">
              <div class="location-input-group">
                <el-input v-model="form.longitude" placeholder="请输入经度"></el-input>
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="纬度">
              <div class="location-input-group">
                <el-input v-model="form.latitude" placeholder="请输入纬度"></el-input>
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item>
          <el-button type="primary" icon="el-icon-location" @click="openMapPicker">
            <i class="el-icon-location"></i> 地图选点
          </el-button>
          <span class="map-tips">点击地图自动填充经纬度和地址</span>
        </el-form-item>
        <el-form-item label="描述说明">
          <el-input type="textarea" v-model="form.description" :rows="3" placeholder="请输入描述说明"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="补给点详情" :visible.sync="detailVisible" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="补给点名称">{{ currentRow.name }}</el-descriptions-item>
        <el-descriptions-item label="所属区域">{{ currentRow.area }}</el-descriptions-item>
        <el-descriptions-item label="类型">
          <el-tag :type="getTypeTag(currentRow.type)" size="small">{{ currentRow.type }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentRow.status)" size="small">{{ currentRow.status }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="详细地址" :span="2">{{ currentRow.address }}</el-descriptions-item>
        <el-descriptions-item label="联系人">{{ currentRow.contactPerson }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentRow.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="经纬度">{{ currentRow.longitude }}, {{ currentRow.latitude }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDate(currentRow.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ formatDate(currentRow.updateTime) }}</el-descriptions-item>
        <el-descriptions-item label="描述说明" :span="2">{{ currentRow.description || '暂无' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <MapPicker
      v-model="mapPickerVisible"
      :initialLongitude="form.longitude"
      :initialLatitude="form.latitude"
      :initialAddress="form.address"
      @confirm="onMapPickerConfirm"
    ></MapPicker>
  </div>
</template>

<script>
import { getSupplyPointList, getSupplyPoint, createSupplyPoint, updateSupplyPoint, deleteSupplyPoint } from '@/api/supplyPoint'
import { formatDate, getStatusType } from '@/utils'
import { sensitiveConfirm } from '@/utils/sensitiveConfirm'
import MapPicker from '@/components/MapPicker.vue'

export default {
  name: 'SupplyPoint',
  components: {
    MapPicker
  },
  data() {
    return {
      loading: false,
      submitLoading: false,
      dialogVisible: false,
      detailVisible: false,
      mapPickerVisible: false,
      dialogType: 'add',
      currentRow: {},
      searchForm: {
        name: '',
        area: '',
        type: '',
        status: ''
      },
      pageInfo: {
        pageNum: 1,
        pageSize: 10,
        total: 0
      },
      tableData: [],
      selectedRows: [],
      areaOptions: ['滨江区', '西湖区', '余杭区', '拱墅区', '上城区', '下城区', '江干区'],
      form: {
        id: null,
        name: '',
        address: '',
        type: '',
        status: '正常',
        area: '',
        contactPerson: '',
        contactPhone: '',
        longitude: '',
        latitude: '',
        description: ''
      },
      formRules: {
        name: [{ required: true, message: '请输入补给点名称', trigger: 'blur' }],
        area: [{ required: true, message: '请选择所属区域', trigger: 'change' }],
        address: [{ required: true, message: '请输入详细地址', trigger: 'blur' }],
        type: [{ required: true, message: '请选择补给点类型', trigger: 'change' }],
        status: [{ required: true, message: '请选择状态', trigger: 'change' }],
        contactPhone: [
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    dialogTitle() {
      return this.dialogType === 'add' ? '新增补给点' : '编辑补给点'
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    formatDate,
    getStatusType,
    openMapPicker() {
      this.mapPickerVisible = true
    },
    onMapPickerConfirm(location) {
      this.form.longitude = location.longitude
      this.form.latitude = location.latitude
      if (location.address && !this.form.address) {
        this.form.address = location.address
      }
      this.$message.success('已选择位置')
    },
    getTypeTag(type) {
      const map = {
        '饮水点': 'info',
        '休息站': 'success',
        '充电站': 'warning',
        '综合补给': 'primary'
      }
      return map[type] || 'primary'
    },
    getRowClassName({ row }) {
      if (row.status === '维护中') return 'warning-row'
      if (row.status === '关闭') return 'danger-row'
      return ''
    },
    async loadData() {
      this.loading = true
      try {
        const params = {
          pageNum: this.pageInfo.pageNum,
          pageSize: this.pageInfo.pageSize,
          ...this.searchForm
        }
        const data = await getSupplyPointList(params)
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
        name: '',
        area: '',
        type: '',
        status: ''
      }
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
    handleSelectionChange(rows) {
      this.selectedRows = rows
    },
    handleAdd() {
      this.dialogType = 'add'
      this.form = {
        id: null,
        name: '',
        address: '',
        type: '',
        status: '正常',
        area: '',
        contactPerson: '',
        contactPhone: '',
        longitude: '',
        latitude: '',
        description: ''
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
   async handleView(row) {
      try {
        const data = await getSupplyPoint(row.id)
        this.currentRow = data
        this.detailVisible = true
      } catch (e) {
        this.$message.error('获取详情失败')
      }
    },
    async handleDelete(row) {
      try {
        const reason = await sensitiveConfirm({
          title: '删除补给点',
          message: `您正在删除补给点「${row.name}」，此操作不可恢复，请输入删除原因：`,
          confirmButtonText: '确认删除',
          type: 'danger'
        })
        await deleteSupplyPoint(row.id, reason)
        this.$message.success('删除成功')
        this.loadData()
      } catch (e) {
        if (e.message !== 'cancel') {
          this.$message.error('删除失败')
        }
      }
    },
    handleSubmit() {
      this.$refs.formRef.validate(async valid => {
        if (valid) {
          this.submitLoading = true
          try {
            if (this.dialogType === 'add') {
              await createSupplyPoint(this.form)
              this.$message.success('新增成功')
            } else {
              await updateSupplyPoint(this.form.id, this.form)
              this.$message.success('更新成功')
            }
            this.dialogVisible = false
            this.loadData()
          } catch (e) {
            this.$message.error('保存失败')
          } finally {
            this.submitLoading = false
          }
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.supply-point {
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

  .pagination-container {
    display: flex;
    justify-content: flex-end;
    padding-top: 20px;
  }

  .danger-btn {
    color: $danger-color;
  }

  .location-input-group {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .map-tips {
    margin-left: 10px;
    font-size: 12px;
    color: #909399;
  }

  ::v-deep .el-descriptions {
    .el-descriptions-item__label {
      background: #F5F9F7;
    }
  }
}
</style>
