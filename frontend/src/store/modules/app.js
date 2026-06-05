const state = {
  sidebarOpened: true
}

const mutations = {
  TOGGLE_SIDEBAR: (state) => {
    state.sidebarOpened = !state.sidebarOpened
  },
  SET_SIDEBAR: (state, opened) => {
    state.sidebarOpened = opened
  }
}

const actions = {
  toggleSidebar({ commit }) {
    commit('TOGGLE_SIDEBAR')
  },
  setSidebar({ commit }, opened) {
    commit('SET_SIDEBAR', opened)
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
