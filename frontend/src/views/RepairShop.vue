<template>
  <div class="repair-shop">
    <el-card>
      <div class="search-bar">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="名称">
            <el-input v-model="searchForm.name" placeholder="请输入维修点名称" clearable></el-input>
          </el-form-item>
          <el-form-item label="区域">
            <el-select v-model="searchForm.area" placeholder="全部区域" clearable>
              <el-option v-for="area in areaOptions" :key="area" :label="area" :value="area"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="等级">
            <el-select v-model="searchForm.level" placeholder="全部等级" clearable>
              <el-option label="一级" value="一级"></el-option>
              <el-option label="二级" value="二级"></el-option>
              <el-option label="三级" value="三级"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
            <el-button icon="el-icon-refresh" @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
        <div class="action-bar">
          <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增维修点</el-button>
        </div>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="70"></el-table-column>
        <el-table-column prop="name" label="维修点名称" min-width="180"></el-table-column>
        <el-table-column prop="address" label="地址" min-width="200" show-overflow-tooltip></el-table-column>
        <el-table-column prop="level" label="等级" width="90">
          <template slot-scope="scope">
            <el-tag :type="getLevelType(scope.row.level)" size="small">{{ scope.row.level }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="area" label="所属区域" width="100"></el-table-column>
        <el-table-column prop="serviceScope" label="服务范围" min-width="150" show-overflow-tooltip></el-table-column>
        <el-table-column prop="contactPerson" label="联系人" width="100"></el-table-column>
        <el-table-column prop="contactPhone" label="联系电话" width="130"></el-table-column>
        <el-table-column prop="staffCount" label="人员数量" width="100">
          <template slot-scope="scope">
            <i class="el-icon-user"></i> {{ scope.row.staffCount }} 人
          </template>
        </el-table-column>
        <el-table-column prop="businessHours" label="营业时间" width="140"></el-table-column>
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
          :page-sizes="[10, 20, 50]"
          :page-size="pageInfo.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pageInfo.total"
          background
        ></el-pagination>
      </div>
    </el-card>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="700px" :close-on-click-modal="false">
      <el-form :model="form" :rules="formRules" ref="formRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="维修点名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入维修点名称"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="维修点等级" prop="level">
              <el-select v-model="form.level" placeholder="请选择等级" style="width: 100%;">
                <el-option label="一级（综合维修中心）" value="一级"></el-option>
                <el-option label="二级（标准维修站）" value="二级"></el-option>
                <el-option label="三级（简易维修点）" value="三级"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属区域" prop="area">
              <el-select v-model="form.area" placeholder="请选择区域" style="width: 100%;">
                <el-option v-for="area in areaOptions" :key="area" :label="area" :value="area"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="营业时间" prop="businessHours">
              <el-input v-model="form.businessHours" placeholder="如：08:00-20:00"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="详细地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入详细地址"></el-input>
        </el-form-item>
        <el-form-item label="服务范围" prop="serviceScope">
          <el-input type="textarea" v-model="form.serviceScope" :rows="2" placeholder="如：整车维修、配件更换、保养服务等"></el-input>
        </el-form-item>
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
        <el-form-item label="维修人员数量" prop="staffCount">
          <el-input-number v-model="form.staffCount" :min="0" :max="50" style="width: 100%;"></el-input-number>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="维修点详情" :visible.sync="detailVisible" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="维修点名称">{{ currentRow.name }}</el-descriptions-item>
        <el-descriptions-item label="等级">
          <el-tag :type="getLevelType(currentRow.level)" size="small">{{ currentRow.level }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="所属区域">{{ currentRow.area }}</el-descriptions-item>
        <el-descriptions-item label="营业时间">{{ currentRow.businessHours }}</el-descriptions-item>
        <el-descriptions-item label="详细地址" :span="2">{{ currentRow.address }}</el-descriptions-item>
        <el-descriptions-item label="服务范围" :span="2">{{ currentRow.serviceScope }}</el-descriptions-item>
        <el-descriptions-item label="联系人">{{ currentRow.contactPerson }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentRow.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="维修人员">{{ currentRow.staffCount }} 人</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDate(currentRow.createTime) }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
import { formatDate, getLevelType } from '@/utils'

export default {
  name: 'RepairShop',
  data() {
    return {
      loading: false,
      submitLoading: false,
      dialogVisible: false,
      detailVisible: false,
      dialogType: 'add',
      currentRow: {},
      searchForm: {
        name: '',
        area: '',
        level: ''
      },
      pageInfo: {
        pageNum: 1,
        pageSize: 10,
        total: 0
      },
      tableData: [],
      areaOptions: ['滨江区', '西湖区', '余杭区', '拱墅区', '上城区', '下城区', '江干区'],
      form: {
        id: null,
        name: '',
        address: '',
        level: '',
        area: '',
        serviceScope: '',
        contactPerson: '',
        contactPhone: '',
        staffCount: 0,
        businessHours: ''
      },
      formRules: {
        name: [{ required: true, message: '请输入维修点名称', trigger: 'blur' }],
        level: [{ required: true, message: '请选择维修点等级', trigger: 'change' }],
        area: [{ required: true, message: '请选择所属区域', trigger: 'change' }],
        address: [{ required: true, message: '请输入详细地址', trigger: 'blur' }],
        contactPhone: [
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    dialogTitle() {
      return this.dialogType === 'add' ? '新增维修点' : '编辑维修点'
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    formatDate,
    getLevelType,
    loadData() {
      this.loading = true
      this.tableData = [
        { id: 1, name: '西湖一级维修中心', address: '西湖区体育场路150号', level: '一级', area: '西湖区', serviceScope: '整车维修、配件更换、保养服务', contactPerson: '刘师傅', contactPhone: '13900139001', staffCount: 8, businessHours: '08:00-20:00', createTime: '2024-01-15 10:30:00' },
        { id: 2, name: '滨江二级维修站', address: '滨江区江南大道100号', level: '二级', area: '滨江区', serviceScope: '普通维修、充气、补胎', contactPerson: '陈师傅', contactPhone: '13900139002', staffCount: 4, businessHours: '09:00-18:00', createTime: '2024-01-16 09:20:00' },
        { id: 3, name: '余杭三级维修点', address: '余杭区余杭塘路200号', level: '三级', area: '余杭区', serviceScope: '简单维修、应急处理', contactPerson: '周师傅', contactPhone: '13900139003', staffCount: 2, businessHours: '10:00-17:00', createTime: '2024-01-17 14:45:00' },
        { id: 4, name: '上城二级维修站', address: '上城区庆春路88号', level: '二级', area: '上城区', serviceScope: '普通维修、配件更换', contactPerson: '吴师傅', contactPhone: '13900139004', staffCount: 5, businessHours: '08:30-19:00', createTime: '2024-01-18 11:00:00' }
      ]
      this.pageInfo.total = this.tableData.length
      this.loading = false
    },
    handleSearch() {
      this.pageInfo.pageNum = 1
      this.loadData()
      this.$message.success('搜索完成')
    },
    handleReset() {
      this.searchForm = { name: '', area: '', level: '' }
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
    handleAdd() {
      this.dialogType = 'add'
      this.form = {
        id: null,
        name: '',
        address: '',
        level: '',
        area: '',
        serviceScope: '',
        contactPerson: '',
        contactPhone: '',
        staffCount: 0,
        businessHours: ''
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
    handleView(row) {
      this.currentRow = { ...row }
      this.detailVisible = true
    },
    handleDelete(row) {
      this.$confirm(`确定要删除维修点「${row.name}」吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.tableData = this.tableData.filter(item => item.id !== row.id)
        this.$message.success('删除成功')
      }).catch(() => {})
    },
    handleSubmit() {
      this.$refs.formRef.validate(valid => {
        if (valid) {
          this.submitLoading = true
          setTimeout(() => {
            if (this.dialogType === 'add') {
              this.tableData.unshift({
                ...this.form,
                id: Date.now(),
                createTime: new Date().toLocaleString()
              })
              this.$message.success('新增成功')
            } else {
              const index = this.tableData.findIndex(item => item.id === this.form.id)
              if (index > -1) {
                this.tableData[index] = { ...this.form }
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
.repair-shop {
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
}
</style>
