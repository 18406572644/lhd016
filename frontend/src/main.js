import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import './styles/index.scss'
import * as echarts from 'echarts'
import moment from 'moment'
import request from './utils/request'

Vue.config.productionTip = false
Vue.use(ElementUI, { size: 'medium' })
Vue.prototype.$echarts = echarts
Vue.prototype.$moment = moment
Vue.prototype.$request = request
Vue.prototype.$baseUrl = '/api'

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
