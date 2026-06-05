<template>
  <div class="help-request">
    <el-card>
      <div class="search-bar">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="求助人">
            <el-input v-model="searchForm.requesterName" placeholder="请输入求助人姓名" clearable></el-input>
          </el-form-item>
          <el-form-item label="求助类型">
            <el-select v-model="searchForm.helpType" placeholder="全部类型" clearable>
              <el-option label="车辆故障" value="车辆故障"></el-option>
              <el-option label="身体不适" value="身体不适"></el-option>
              <el-option label="物资需求" value="物资需求"></el-option>
              <el-option label="其他" value="其他"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="紧急程度">
            <el-select v-model="searchForm.urgency" placeholder="全部程度" clearable>
              <el-option label="低" value="低"></el-option>
              <el-option label="中" value="中"></el-option>
              <el-option label="高" value="高"></el-option>
              <el-option label="紧急" value="紧急"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="全部状态" clearable>
              <el-option label="待处理" value="pending"></el-option>
              <el-option label="处理中" value="processing"></el-option>
              <el-option label="已完成" value="completed"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
            <el-button icon="el-icon-refresh" @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
        <div class="action-bar">
          <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增求助</el-button>
        </div>
      </div>

      <el-table :data="filteredTableData" border stripe v-loading="loading" :row-class-name="getRowClassName">
        <el-table-column prop="id" label="ID" width="70"></el-table-column>
        <el-table-column label="紧急程度" width="90">
          <template slot-scope="scope">
            <el-tag :type="getUrgencyType(scope.row.urgency)" size="small" effect="dark">{{ scope.row.urgency }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="requesterName" label="求助人" width="100"></el-table-column>
        <el-table-column prop="requesterPhone" label="联系电话" width="130"></el-table-column>
        <el-table-column prop="helpType" label="求助类型" width="110">
          <template slot-scope="scope">
            <el-tag type="primary" size="small">{{ scope.row.helpType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="location" label="事发位置" min-width="180" show-overflow-tooltip></el-table-column>
        <el-table-column prop="description" label="问题描述" min-width="150" show-overflow-tooltip></el-table-column>
        <el-table-column label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)" size="small">{{ getStatusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="handler" label="处理人" width="100">
          <template slot-scope="scope">{{ scope.row.handler || '-' }}</template>
        </el-table-column>
        <el-table-column prop="createTime" label="登记时间" width="160">
          <template slot-scope="scope">{{ formatDate(scope.row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template slot-scope="scope">
            <el-button
              v-if="scope.row.status !== 'completed'"
              type="text"
              icon="el-icon-edit"
              @click="handleProcess(scope.row)"
            >处理</el-button>
            <el-button type="text" icon="el-icon-view" @click="handleView(scope.row)">详情</el-button>
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

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="650px" :close-on-click-modal="false">
      <el-form :model="form" :rules="formRules" ref="formRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="求助人姓名" prop="requesterName">
              <el-input v-model="form.requesterName" placeholder="请输入求助人姓名"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="requesterPhone">
              <el-input v-model="form.requesterPhone" placeholder="请输入联系电话"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="求助类型" prop="helpType">
              <el-select v-model="form.helpType" placeholder="请选择求助类型" style="width: 100%;">
                <el-option label="车辆故障" value="车辆故障"></el-option>
                <el-option label="身体不适" value="身体不适"></el-option>
                <el-option label="物资需求" value="物资需求"></el-option>
                <el-option label="其他" value="其他"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="紧急程度" prop="urgency">
              <el-select v-model="form.urgency" placeholder="请选择紧急程度" style="width: 100%;">
                <el-option label="低" value="低"></el-option>
                <el-option label="中" value="中"></el-option>
                <el-option label="高" value="高"></el-option>
                <el-option label="紧急" value="紧急"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="事发位置" prop="location">
          <el-input v-model="form.location" placeholder="请输入事发位置"></el-input>
        </el-form-item>
        <el-form-item label="问题描述" prop="description">
          <el-input type="textarea" v-model="form.description" :rows="3" placeholder="请详细描述遇到的问题"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="处理求助" :visible.sync="processDialogVisible" width="550px">
      <el-descriptions :column="2" border size="small" class="process-desc">
        <el-descriptions-item label="求助人">{{ currentRow.requesterName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentRow.requesterPhone }}</el-descriptions-item>
        <el-descriptions-item label="求助类型">
          <el-tag type="primary" size="small">{{ currentRow.helpType }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="紧急程度">
          <el-tag :type="getUrgencyType(currentRow.urgency)" size="small" effect="dark">{{ currentRow.urgency }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="事发位置" :span="2">{{ currentRow.location }}</el-descriptions-item>
        <el-descriptions-item label="问题描述" :span="2">{{ currentRow.description }}</el-descriptions-item>
      </el-descriptions>

      <el-form :model="processForm" :rules="processRules" ref="processFormRef" label-width="100px" style="margin-top: 20px;">
        <el-form-item label="处理状态" prop="status">
          <el-radio-group v-model="processForm.status">
            <el-radio label="processing">处理中</el-radio>
            <el-radio label="completed">已完成</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="处理人" prop="handler">
          <el-input v-model="processForm.handler" placeholder="请输入处理人姓名"></el-input>
        </el-form-item>
        <el-form-item label="处理结果" prop="handleResult">
          <el-input type="textarea" v-model="processForm.handleResult" :rows="4" placeholder="请输入处理结果"></el-input>
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="processDialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="processSubmitLoading" @click="handleProcessSubmit">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="求助详情" :visible.sync="detailVisible" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="求助人">{{ currentRow.requesterName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentRow.requesterPhone }}</el-descriptions-item>
        <el-descriptions-item label="求助类型">
          <el-tag type="primary" size="small">{{ currentRow.helpType }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="紧急程度">
          <el-tag :type="getUrgencyType(currentRow.urgency)" size="small" effect="dark">{{ currentRow.urgency }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="事发位置" :span="2">{{ currentRow.location }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentRow.status)" size="small">{{ getStatusText(currentRow.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="处理人">{{ currentRow.handler || '-' }}</el-descriptions-item>
        <el-descriptions-item label="登记时间" :span="2">{{ formatDate(currentRow.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="处理时间" :span="2">{{ currentRow.handleTime ? formatDate(currentRow.handleTime) : '-' }}</el-descriptions-item>
        <el-descriptions-item label="问题描述" :span="2">{{ currentRow.description }}</el-descriptions-item>
        <el-descriptions-item label="处理结果" :span="2">{{ currentRow.handleResult || '暂无' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
import { formatDate, getStatusType, getStatusText, getUrgencyType } from '@/utils'

export default {
  name: 'HelpRequest',
  data() {
    return {
      loading: false,
      submitLoading: false,
      processSubmitLoading: false,
      dialogVisible: false,
      processDialogVisible: false,
      detailVisible: false,
      dialogType: 'add',
      currentRow: {},
      searchForm: {
        requesterName: '',
        helpType: '',
        urgency: '',
        status: ''
      },
      pageInfo: {
        pageNum: 1,
        pageSize: 10,
        total: 0
      },
      tableData: [],
      form: {
        id: null,
        requesterName: '',
        requesterPhone: '',
        location: '',
        helpType: '',
        urgency: '中',
        description: '',
        status: 'pending',
        handler: '',
        handleResult: ''
      },
      processForm: {
        status: 'processing',
        handler: '',
        handleResult: ''
      },
      formRules: {
        requesterName: [{ required: true, message: '请输入求助人姓名', trigger: 'blur' }],
        requesterPhone: [
          { required: true, message: '请输入联系电话', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ],
        helpType: [{ required: true, message: '请选择求助类型', trigger: 'change' }],
        urgency: [{ required: true, message: '请选择紧急程度', trigger: 'change' }],
        location: [{ required: true, message: '请输入事发位置', trigger: 'blur' }],
        description: [{ required: true, message: '请输入问题描述', trigger: 'blur' }]
      },
      processRules: {
        status: [{ required: true, message: '请选择处理状态', trigger: 'change' }],
        handler: [{ required: true, message: '请输入处理人', trigger: 'blur' }],
        handleResult: [{ required: true, message: '请输入处理结果', trigger: 'blur' }]
      }
    }
  },
  computed: {
    dialogTitle() {
      return this.dialogType === 'add' ? '新增求助登记' : '编辑求助登记'
    },
    filteredTableData() {
      let data = [...this.tableData]
      if (this.searchForm.requesterName) {
        data = data.filter(item => item.requesterName.includes(this.searchForm.requesterName))
      }
      if (this.searchForm.helpType) {
        data = data.filter(item => item.helpType === this.searchForm.helpType)
      }
      if (this.searchForm.urgency) {
        data = data.filter(item => item.urgency === this.searchForm.urgency)
      }
      if (this.searchForm.status) {
        data = data.filter(item => item.status === this.searchForm.status)
      }
      return data
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    formatDate,
    getStatusType,
    getStatusText,
    getUrgencyType,
    getRowClassName({ row }) {
      if (row.urgency === '紧急' && row.status !== 'completed') return 'danger-row'
      if (row.urgency === '高' && row.status !== 'completed') return 'warning-row'
      return ''
    },
    loadData() {
      this.loading = true
      this.tableData = [
        { id: 1, requesterName: '小明', requesterPhone: '13700137001', location: '滨江区江南大道附近', helpType: '车辆故障', urgency: '高', status: 'pending', description: '自行车链条断裂，无法继续骑行', handler: '', handleResult: '', createTime: '2024-01-20 09:15:00', handleTime: '' },
        { id: 2, requesterName: '小红', requesterPhone: '13700137002', location: '西湖景区苏堤', helpType: '身体不适', urgency: '中', status: 'processing', description: '骑行中感觉头晕，需要休息和饮水', handler: '李医生', handleResult: '已提供饮用水和休息场所，情况好转后离开', createTime: '2024-01-20 08:30:00', handleTime: '2024-01-20 09:00:00' },
        { id: 3, requesterName: '小刚', requesterPhone: '13700137003', location: '余杭区未来科技城', helpType: '物资需求', urgency: '低', status: 'completed', description: '需要补充饮用水和能量补给', handler: '王运维', handleResult: '已提供矿泉水2瓶、能量棒3个', createTime: '2024-01-19 16:45:00', handleTime: '2024-01-19 17:10:00' },
        { id: 4, requesterName: '小李', requesterPhone: '13700137004', location: '拱墅区运河边绿道', helpType: '车辆故障', urgency: '紧急', status: 'pending', description: '骑行中突然爆胎，没有备胎', handler: '', handleResult: '', createTime: '2024-01-20 10:05:00', handleTime: '' },
        { id: 5, requesterName: '小张', requesterPhone: '13700137005', location: '上城区钱江新城', helpType: '其他', urgency: '低', status: 'completed', description: '询问最近的维修点位置', handler: '客服小王', handleResult: '已告知最近维修点地址和联系方式', createTime: '2024-01-19 14:20:00', handleTime: '2024-01-19 14:25:00' }
      ]
      this.pageInfo.total = this.tableData.length
      this.loading = false
    },
    handleSearch() {
      this.pageInfo.pageNum = 1
      this.$message.success('搜索完成')
    },
    handleReset() {
      this.searchForm = {
        requesterName: '',
        helpType: '',
        urgency: '',
        status: ''
      }
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
        requesterName: '',
        requesterPhone: '',
        location: '',
        helpType: '',
        urgency: '中',
        description: '',
        status: 'pending',
        handler: '',
        handleResult: ''
      }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.formRef && this.$refs.formRef.clearValidate()
      })
    },
    handleProcess(row) {
      this.currentRow = { ...row }
      this.processForm = {
        status: row.status === 'pending' ? 'processing' : 'completed',
        handler: row.handler || '',
        handleResult: row.handleResult || ''
      }
      this.processDialogVisible = true
    },
    handleProcessSubmit() {
      this.$refs.processFormRef.validate(valid => {
        if (valid) {
          this.processSubmitLoading = true
          setTimeout(() => {
            const index = this.tableData.findIndex(item => item.id === this.currentRow.id)
            if (index > -1) {
              this.tableData[index] = {
                ...this.tableData[index],
                ...this.processForm,
                handleTime: this.processForm.status === 'completed' ? new Date().toLocaleString() : this.currentRow.handleTime
              }
            }
            this.$message.success('处理成功')
            this.processSubmitLoading = false
            this.processDialogVisible = false
          }, 500)
        }
      })
    },
    handleView(row) {
      this.currentRow = { ...row }
      this.detailVisible = true
    },
    handleDelete(row) {
      this.$confirm(`确定要删除这条求助记录吗？`, '提示', {
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
              this.$message.success('登记成功')
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
.help-request {
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

  .process-desc {
    margin-bottom: 20px;
  }
}
</style>
