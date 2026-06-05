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

      <el-table :data="tableData" border stripe v-loading="loading" :row-class-name="getRowClassName">
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
        <el-table-column prop="location" label="事发位置" min-width="150" show-overflow-tooltip></el-table-column>
        <el-table-column label="分配维修点" width="150">
          <template slot-scope="scope">
            <span v-if="scope.row.repairShop">{{ scope.row.repairShop.name }}</span>
            <el-tag v-else type="info" size="small">待分配</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="预计到达" width="100">
          <template slot-scope="scope">
            <span v-if="scope.row.etaText">{{ scope.row.etaText }}</span>
            <span v-else-if="scope.row.estimatedArrivalTime">{{ scope.row.estimatedArrivalTime }}分钟</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="距离" width="100">
          <template slot-scope="scope">
            <span v-if="scope.row.distanceText">{{ scope.row.distanceText }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
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
        <el-table-column label="操作" width="280" fixed="right">
          <template slot-scope="scope">
            <el-button
              v-if="scope.row.status !== 'completed' && !scope.row.repairShopId"
              type="text"
              icon="el-icon-location"
              @click="handleDispatch(scope.row)"
            >分配</el-button>
            <el-button
              v-if="scope.row.status !== 'completed' && scope.row.repairShopId"
              type="text"
              icon="el-icon-s-promotion"
              @click="handleAdjust(scope.row)"
            >调整</el-button>
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
          :total="pageInfo.total"
          background
        ></el-pagination>
      </div>
    </el-card>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="700px" :close-on-click-modal="false">
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
              <el-select v-model="form.helpType" placeholder="请选择求助类型" style="width: 100%;" @change="onHelpTypeChange">
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
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="经度" prop="longitude">
              <el-input v-model.number="form.longitude" placeholder="请输入经度，如120.1551"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="纬度" prop="latitude">
              <el-input v-model.number="form.latitude" placeholder="请输入纬度，如30.2741"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="问题描述" prop="description">
          <el-input type="textarea" v-model="form.description" :rows="3" placeholder="请详细描述遇到的问题"></el-input>
        </el-form-item>

        <div v-if="recommendedShops.length > 0" class="recommend-section">
          <div class="section-title">
            <i class="el-icon-star-on"></i> 智能推荐维修点
          </div>
          <el-radio-group v-model="selectedShopId" class="shop-list">
            <el-radio
              v-for="(shop, index) in recommendedShops"
              :key="shop.repairShop.id"
              :label="shop.repairShop.id"
              class="shop-item"
            >
              <div class="shop-header">
                <el-tag size="small" :type="getLevelTagType(shop.repairShop.level)">
                  {{ shop.repairShop.level }}
                </el-tag>
                <span class="shop-name">{{ shop.repairShop.name }}</span>
                <el-tag v-if="index === 0" type="success" size="small" effect="dark">推荐</el-tag>
              </div>
              <div class="shop-info">
                <span><i class="el-icon-location"></i> {{ shop.distance.toFixed(2) }}公里</span>
                <span><i class="el-icon-time"></i> 预计{{ shop.estimatedArrivalTime }}分钟</span>
                <span><i class="el-icon-user"></i> 负载: {{ shop.currentWorkload }}/{{ shop.maxWorkload }}</span>
                <span>匹配度: {{ (shop.score * 100).toFixed(0) }}%</span>
              </div>
              <div class="shop-contact">
                <span>负责人: {{ shop.repairShop.contactPerson }}</span>
                <span>电话: {{ shop.repairShop.contactPhone }}</span>
              </div>
            </el-radio>
          </el-radio-group>
        </div>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleGetRecommend" v-if="form.longitude && form.latitude && !recommendLoading">
          <i class="el-icon-refresh"></i> 刷新推荐
        </el-button>
        <el-button type="primary" :loading="recommendLoading" @click="handleGetRecommend" v-if="form.longitude && form.latitude && recommendLoading">
          计算中...
        </el-button>
        <el-button type="success" :loading="submitLoading" @click="handleSubmitAndDispatch" v-if="recommendedShops.length > 0">
          <i class="el-icon-check"></i> 保存并分配
        </el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit" v-else>确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="分配维修点" :visible.sync="dispatchDialogVisible" width="600px">
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
      </el-descriptions>

      <div v-if="recommendedShops.length > 0" class="recommend-section" style="margin-top: 20px;">
        <div class="section-title">
          <i class="el-icon-star-on"></i> 智能推荐维修点
        </div>
        <el-radio-group v-model="selectedShopId" class="shop-list">
          <el-radio
            v-for="(shop, index) in recommendedShops"
            :key="shop.repairShop.id"
            :label="shop.repairShop.id"
            class="shop-item"
          >
            <div class="shop-header">
              <el-tag size="small" :type="getLevelTagType(shop.repairShop.level)">
                {{ shop.repairShop.level }}
              </el-tag>
              <span class="shop-name">{{ shop.repairShop.name }}</span>
              <el-tag v-if="index === 0" type="success" size="small" effect="dark">推荐</el-tag>
            </div>
            <div class="shop-info">
              <span><i class="el-icon-location"></i> {{ shop.distance.toFixed(2) }}公里</span>
              <span><i class="el-icon-time"></i> 预计{{ shop.estimatedArrivalTime }}分钟</span>
              <span><i class="el-icon-user"></i> 负载: {{ shop.currentWorkload }}/{{ shop.maxWorkload }}</span>
              <span>匹配度: {{ (shop.score * 100).toFixed(0) }}%</span>
            </div>
            <div class="shop-contact">
              <span>负责人: {{ shop.repairShop.contactPerson }}</span>
              <span>电话: {{ shop.repairShop.contactPhone }}</span>
            </div>
          </el-radio>
        </el-radio-group>
      </div>

      <el-form :model="dispatchForm" label-width="100px" style="margin-top: 20px;">
        <el-form-item label="通知负责人">
          <el-switch v-model="dispatchForm.notifyContact" active-text="是" inactive-text="否"></el-switch>
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="dispatchDialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="dispatchLoading" @click="handleDispatchSubmit">确 定 分 配</el-button>
      </div>
    </el-dialog>

    <el-dialog title="调整分配" :visible.sync="adjustDialogVisible" width="600px">
      <el-descriptions :column="2" border size="small" class="process-desc">
        <el-descriptions-item label="求助人">{{ currentRow.requesterName }}</el-descriptions-item>
        <el-descriptions-item label="当前维修点">
          {{ currentRow.repairShop ? currentRow.repairShop.name : '未分配' }}
        </el-descriptions-item>
        <el-descriptions-item label="求助类型">{{ currentRow.helpType }}</el-descriptions-item>
        <el-descriptions-item label="事发位置" :span="2">{{ currentRow.location }}</el-descriptions-item>
      </el-descriptions>

      <el-form :model="adjustForm" label-width="100px" style="margin-top: 20px;">
        <el-form-item label="选择维修点" prop="repairShopId">
          <el-select v-model="adjustForm.repairShopId" placeholder="请选择维修点" style="width: 100%;">
            <el-option
              v-for="shop in allShops"
              :key="shop.id"
              :label="shop.name + ' (' + shop.level + ')'"
              :value="shop.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="通知负责人">
          <el-switch v-model="adjustForm.notify" active-text="是" inactive-text="否"></el-switch>
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="adjustDialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="adjustLoading" @click="handleAdjustSubmit">确 定 调 整</el-button>
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
        <el-descriptions-item label="维修点" v-if="currentRow.repairShop">
          {{ currentRow.repairShop.name }}
        </el-descriptions-item>
        <el-descriptions-item label="预计到达" v-if="currentRow.etaText">
          {{ currentRow.etaText }}
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

    <el-dialog title="求助详情" :visible.sync="detailVisible" width="650px">
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
        <el-descriptions-item label="经度" v-if="currentRow.longitude">{{ currentRow.longitude }}</el-descriptions-item>
        <el-descriptions-item label="纬度" v-if="currentRow.latitude">{{ currentRow.latitude }}</el-descriptions-item>
        <el-descriptions-item label="分配维修点" v-if="currentRow.repairShop">
          {{ currentRow.repairShop.name }} ({{ currentRow.repairShop.level }})
        </el-descriptions-item>
        <el-descriptions-item label="预计到达时间" v-if="currentRow.etaText">
          {{ currentRow.etaText }}
        </el-descriptions-item>
        <el-descriptions-item label="距离" v-if="currentRow.distanceText">
          {{ currentRow.distanceText }}
        </el-descriptions-item>
        <el-descriptions-item label="维修点负责人" v-if="currentRow.repairShop">
          {{ currentRow.repairShop.contactPerson }}
        </el-descriptions-item>
        <el-descriptions-item label="联系电话" v-if="currentRow.repairShop">
          {{ currentRow.repairShop.contactPhone }}
        </el-descriptions-item>
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
import {
  getHelpRequestListWithShop,
  getHelpRequestWithShop,
  getRecommendedShops,
  createHelpRequest,
  dispatchHelpRequest,
  adjustDispatch,
  handleHelpRequest,
  deleteHelpRequest
} from '@/api/helpRequest'
import { getRepairShopList } from '@/api/repairShop'

export default {
  name: 'HelpRequest',
  data() {
    return {
      loading: false,
      submitLoading: false,
      processSubmitLoading: false,
      dispatchLoading: false,
      adjustLoading: false,
      recommendLoading: false,
      dialogVisible: false,
      processDialogVisible: false,
      detailVisible: false,
      dispatchDialogVisible: false,
      adjustDialogVisible: false,
      dialogType: 'add',
      currentRow: {},
      recommendedShops: [],
      selectedShopId: null,
      allShops: [],
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
        longitude: null,
        latitude: null,
        helpType: '',
        urgency: '中',
        description: '',
        status: 'pending',
        handler: '',
        handleResult: '',
        repairShopId: null,
        estimatedArrivalTime: null
      },
      dispatchForm: {
        helpRequestId: null,
        repairShopId: null,
        notifyContact: true
      },
      adjustForm: {
        repairShopId: null,
        notify: true
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
        longitude: [{ required: true, message: '请输入经度', trigger: 'blur' }],
        latitude: [{ required: true, message: '请输入纬度', trigger: 'blur' }],
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
    }
  },
  mounted() {
    this.loadData()
    this.loadAllShops()
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
    getLevelTagType(level) {
      if (level === '一级') return 'danger'
      if (level === '二级') return 'warning'
      return 'info'
    },
    async loadData() {
      this.loading = true
      try {
        const params = {
          pageNum: this.pageInfo.pageNum,
          pageSize: this.pageInfo.pageSize,
          ...this.searchForm
        }
        const res = await getHelpRequestListWithShop(params)
        if (res.code === 200) {
          this.tableData = res.data.list
          this.pageInfo.total = res.data.total
        }
      } catch (e) {
        this.$message.error('加载数据失败')
      } finally {
        this.loading = false
      }
    },
    async loadAllShops() {
      try {
        const res = await getRepairShopList({ pageNum: 1, pageSize: 100 })
        if (res.code === 200) {
          this.allShops = res.data.list
        }
      } catch (e) {
        console.error('加载维修点列表失败', e)
      }
    },
    async loadRecommendedShops() {
      if (!this.form.longitude || !this.form.latitude) {
        this.$message.warning('请先输入经纬度')
        return
      }
      this.recommendLoading = true
      try {
        const params = {
          longitude: this.form.longitude,
          latitude: this.form.latitude,
          helpType: this.form.helpType
        }
        const res = await getRecommendedShops(params)
        if (res.code === 200) {
          this.recommendedShops = res.data
          if (this.recommendedShops.length > 0) {
            this.selectedShopId = this.recommendedShops[0].repairShop.id
          } else {
            this.selectedShopId = null
          }
        }
      } catch (e) {
        this.$message.error('获取推荐维修点失败')
      } finally {
        this.recommendLoading = false
      }
    },
    onHelpTypeChange() {
      if (this.form.longitude && this.form.latitude) {
        this.loadRecommendedShops()
      }
    },
    handleGetRecommend() {
      this.loadRecommendedShops()
    },
    handleSearch() {
      this.pageInfo.pageNum = 1
      this.loadData()
    },
    handleReset() {
      this.searchForm = {
        requesterName: '',
        helpType: '',
        urgency: '',
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
    handleAdd() {
      this.dialogType = 'add'
      this.form = {
        id: null,
        requesterName: '',
        requesterPhone: '',
        location: '',
        longitude: null,
        latitude: null,
        helpType: '',
        urgency: '中',
        description: '',
        status: 'pending',
        handler: '',
        handleResult: '',
        repairShopId: null,
        estimatedArrivalTime: null
      }
      this.recommendedShops = []
      this.selectedShopId = null
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.formRef && this.$refs.formRef.clearValidate()
      })
    },
    handleDispatch(row) {
      this.currentRow = { ...row }
      this.recommendedShops = []
      this.selectedShopId = null
      this.dispatchForm = {
        helpRequestId: row.id,
        repairShopId: null,
        notifyContact: true
      }

      const longitude = row.longitude
      const latitude = row.latitude

      if (longitude && latitude) {
        this.loadDispatchRecommendShops(longitude, latitude, row.helpType)
      } else {
        this.$message.warning('该求助没有经纬度信息，无法智能推荐')
      }

      this.dispatchDialogVisible = true
    },
    async loadDispatchRecommendShops(longitude, latitude, helpType) {
      this.recommendLoading = true
      try {
        const params = { longitude, latitude, helpType }
        const res = await getRecommendedShops(params)
        if (res.code === 200) {
          this.recommendedShops = res.data
          if (this.recommendedShops.length > 0) {
            this.selectedShopId = this.recommendedShops[0].repairShop.id
          }
        }
      } catch (e) {
        this.$message.error('获取推荐维修点失败')
      } finally {
        this.recommendLoading = false
      }
    },
    async handleDispatchSubmit() {
      if (!this.selectedShopId) {
        this.$message.warning('请选择维修点')
        return
      }
      this.dispatchLoading = true
      try {
        const dto = {
          helpRequestId: this.currentRow.id,
          repairShopId: this.selectedShopId,
          notifyContact: this.dispatchForm.notifyContact
        }
        const res = await dispatchHelpRequest(dto)
        if (res.code === 200) {
          this.$message.success('分配成功，已通知维修点负责人')
          this.dispatchDialogVisible = false
          this.loadData()
        }
      } catch (e) {
        this.$message.error('分配失败')
      } finally {
        this.dispatchLoading = false
      }
    },
    handleAdjust(row) {
      this.currentRow = { ...row }
      this.adjustForm = {
        repairShopId: row.repairShopId,
        notify: true
      }
      this.adjustDialogVisible = true
    },
    async handleAdjustSubmit() {
      if (!this.adjustForm.repairShopId) {
        this.$message.warning('请选择维修点')
        return
      }
      this.adjustLoading = true
      try {
        const res = await adjustDispatch(
          this.currentRow.id,
          this.adjustForm.repairShopId,
          this.adjustForm.notify
        )
        if (res.code === 200) {
          this.$message.success('调整成功')
          this.adjustDialogVisible = false
          this.loadData()
        }
      } catch (e) {
        this.$message.error('调整失败')
      } finally {
        this.adjustLoading = false
      }
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
      this.$refs.processFormRef.validate(async valid => {
        if (valid) {
          this.processSubmitLoading = true
          try {
            const res = await handleHelpRequest(this.currentRow.id, this.processForm)
            if (res.code === 200) {
              this.$message.success('处理成功')
              this.processDialogVisible = false
              this.loadData()
            }
          } catch (e) {
            this.$message.error('处理失败')
          } finally {
            this.processSubmitLoading = false
          }
        }
      })
    },
    async handleView(row) {
      try {
        const res = await getHelpRequestWithShop(row.id)
        if (res.code === 200) {
          this.currentRow = res.data
          this.detailVisible = true
        }
      } catch (e) {
        this.$message.error('获取详情失败')
      }
    },
    handleDelete(row) {
      this.$confirm(`确定要删除这条求助记录吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const res = await deleteHelpRequest(row.id)
          if (res.code === 200) {
            this.$message.success('删除成功')
            this.loadData()
          }
        } catch (e) {
          this.$message.error('删除失败')
        }
      }).catch(() => {})
    },
    handleSubmit() {
      this.$refs.formRef.validate(async valid => {
        if (valid) {
          this.submitLoading = true
          try {
            const saveData = { ...this.form }
            const res = await createHelpRequest(saveData)
            if (res.code === 200) {
              this.$message.success('登记成功')
              this.dialogVisible = false
              this.loadData()
            }
          } catch (e) {
            this.$message.error('保存失败')
          } finally {
            this.submitLoading = false
          }
        }
      })
    },
    async handleSubmitAndDispatch() {
      this.$refs.formRef.validate(async valid => {
        if (valid) {
          if (!this.selectedShopId) {
            this.$message.warning('请选择维修点')
            return
          }
          this.submitLoading = true
          try {
            const saveData = { ...this.form }
            const saveRes = await createHelpRequest(saveData)
            if (saveRes.code === 200) {
              const newId = saveRes.data || (await this.getLatestId())
              const dispatchDto = {
                helpRequestId: newId,
                repairShopId: this.selectedShopId,
                longitude: this.form.longitude,
                latitude: this.form.latitude,
                helpType: this.form.helpType,
                notifyContact: true
              }
              const dispatchRes = await dispatchHelpRequest(dispatchDto)
              if (dispatchRes.code === 200) {
                this.$message.success('登记并分配成功，已通知维修点负责人')
                this.dialogVisible = false
                this.loadData()
              }
            }
          } catch (e) {
            this.$message.error('操作失败')
          } finally {
            this.submitLoading = false
          }
        }
      })
    },
    async getLatestId() {
      const res = await getHelpRequestListWithShop({ pageNum: 1, pageSize: 1 })
      if (res.code === 200 && res.data.list.length > 0) {
        return res.data.list[0].id
      }
      return null
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

  .recommend-section {
    margin-top: 10px;
    padding: 15px;
    background: #f5f7fa;
    border-radius: 4px;

    .section-title {
      font-weight: bold;
      color: #409EFF;
      margin-bottom: 15px;
      font-size: 14px;

      i {
        margin-right: 5px;
      }
    }

    .shop-list {
      display: flex;
      flex-direction: column;
      gap: 10px;

      .shop-item {
        margin-right: 0;
        padding: 12px;
        background: #fff;
        border: 1px solid #e4e7ed;
        border-radius: 4px;
        transition: all 0.3s;

        &:hover {
          border-color: #409EFF;
          box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
        }

        .shop-header {
          display: flex;
          align-items: center;
          gap: 10px;
          margin-bottom: 8px;

          .shop-name {
            font-weight: bold;
            font-size: 14px;
          }
        }

        .shop-info {
          display: flex;
          flex-wrap: wrap;
          gap: 15px;
          font-size: 12px;
          color: #606266;
          margin-bottom: 5px;

          span {
            i {
              margin-right: 3px;
            }
          }
        }

        .shop-contact {
          display: flex;
          gap: 20px;
          font-size: 12px;
          color: #909399;
        }
      }
    }
  }
}
</style>
