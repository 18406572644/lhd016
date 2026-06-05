import Vue from 'vue'
import Vuex from 'vuex'
import user from './modules/user'
import app from './modules/app'

Vue.use(Vuex)

const store = new Vuex.Store({
  modules: {
    user,
    app
  },
  getters: {
    token: state => state.user.token,
    userInfo: state => state.user.userInfo,
    sidebarOpened: state => state.app.sidebarOpened
  }
})

export default store
