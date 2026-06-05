const state = {
  token: localStorage.getItem('token') || 'mock-token-12345',
  userInfo: JSON.parse(localStorage.getItem('userInfo') || 'null') || {
    username: 'admin',
    name: '系统管理员',
    role: 'admin'
  }
}

const mutations = {
  SET_TOKEN: (state, token) => {
    state.token = token
    localStorage.setItem('token', token)
  },
  SET_USER_INFO: (state, userInfo) => {
    state.userInfo = userInfo
    localStorage.setItem('userInfo', JSON.stringify(userInfo))
  },
  LOGOUT: (state) => {
    state.token = ''
    state.userInfo = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }
}

const actions = {
  login({ commit }, userInfo) {
    return new Promise((resolve) => {
      commit('SET_TOKEN', 'mock-token-' + Date.now())
      commit('SET_USER_INFO', {
        username: userInfo.username,
        name: '系统管理员',
        role: 'admin'
      })
      resolve()
    })
  },
  logout({ commit }) {
    return new Promise((resolve) => {
      commit('LOGOUT')
      resolve()
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
