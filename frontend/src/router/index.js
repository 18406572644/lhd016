import Vue from 'vue'
import VueRouter from 'vue-router'
import Layout from '@/components/Layout.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    hidden: true
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '首页仪表盘', icon: 'el-icon-data-analysis' }
      }
    ]
  },
  {
    path: '/map',
    component: Layout,
    children: [
      {
        path: '',
        name: 'MapOverview',
        component: () => import('@/views/MapOverview.vue'),
        meta: { title: '地图总览', icon: 'el-icon-map-location' }
      }
    ]
  },
  {
    path: '/supply',
    component: Layout,
    children: [
      {
        path: '',
        name: 'Supply',
        component: () => import('@/views/SupplyPoint.vue'),
        meta: { title: '补给点管理', icon: 'el-icon-location' }
      }
    ]
  },
  {
    path: '/repair',
    component: Layout,
    children: [
      {
        path: '',
        name: 'Repair',
        component: () => import('@/views/RepairShop.vue'),
        meta: { title: '维修点档案', icon: 'el-icon-service' }
      }
    ]
  },
  {
    path: '/inventory',
    component: Layout,
    children: [
      {
        path: '',
        name: 'Inventory',
        component: () => import('@/views/Inventory.vue'),
        meta: { title: '配件库存', icon: 'el-icon-goods' }
      }
    ]
  },
  {
    path: '/help',
    component: Layout,
    children: [
      {
        path: '',
        name: 'Help',
        component: () => import('@/views/HelpRequest.vue'),
        meta: { title: '求助登记', icon: 'el-icon-phone-outline' }
      }
    ]
  },
  {
    path: '/inventory-check',
    component: Layout,
    children: [
      {
        path: '',
        name: 'InventoryCheck',
        component: () => import('@/views/InventoryCheck.vue'),
        meta: { title: '物资盘点', icon: 'el-icon-document' }
      }
    ]
  },
  {
    path: '/operation-logs',
    component: Layout,
    children: [
      {
        path: '',
        name: 'OperationLog',
        component: () => import('@/views/OperationLog.vue'),
        meta: { title: '操作日志', icon: 'el-icon-document-copy' }
      }
    ]
  },
  {
    path: '/inventory-analysis',
    component: Layout,
    children: [
      {
        path: '',
        name: 'InventoryAnalysis',
        component: () => import('@/views/InventoryAnalysis.vue'),
        meta: { title: '库存智能分析', icon: 'el-icon-data-analysis' }
      }
    ]
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path === '/login') {
    next()
  } else {
    if (!token) {
      next('/login')
    } else {
      next()
    }
  }
})

export default router
