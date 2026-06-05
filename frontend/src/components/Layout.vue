<template>
  <div class="app-container">
    <el-container class="main-container">
      <el-header class="app-header">
        <div class="header-left">
          <i class="el-icon-s-fold fold-btn" @click="toggleSidebar"></i>
          <div class="logo">
            <i class="el-icon-aim"></i>
            <span v-show="sidebarOpened">城市单车补给站管理系统</span>
          </div>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <i class="el-icon-user-solid"></i>
              {{ userInfo.name }}
              <i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="profile">
                <i class="el-icon-setting"></i> 个人中心
              </el-dropdown-item>
              <el-dropdown-item command="logout" divided>
                <i class="el-icon-switch-button"></i> 退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </el-header>

      <el-container>
        <el-aside :width="sidebarOpened ? '220px' : '64px'" class="app-sidebar">
          <el-menu
            :default-active="activeMenu"
            :collapse="!sidebarOpened"
            :collapse-transition="false"
            background-color="#ffffff"
            text-color="#546E7A"
            active-text-color="#4CAF90"
            router
          >
            <el-menu-item v-for="route in menuRoutes" :key="route.path" :index="route.path">
              <i :class="route.meta.icon"></i>
              <span slot="title">{{ route.meta.title }}</span>
            </el-menu-item>
          </el-menu>
        </el-aside>

        <el-main class="app-main">
          <div class="page-container fade-in">
            <div class="breadcrumb-container">
              <el-breadcrumb separator="/">
                <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
                <el-breadcrumb-item>{{ $route.meta.title }}</el-breadcrumb-item>
              </el-breadcrumb>
            </div>
            <router-view v-slot="{ Component }">
              <transition name="fade" mode="out-in">
                <component :is="Component" />
              </transition>
            </router-view>
          </div>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
export default {
  name: 'Layout',
  computed: {
    sidebarOpened() {
      return this.$store.state.app.sidebarOpened
    },
    userInfo() {
      return this.$store.state.user.userInfo
    },
    activeMenu() {
      return this.$route.path
    },
    menuRoutes() {
      return this.$router.options.routes
        .filter(r => r.children && r.children[0] && r.children[0].meta)
        .map(r => ({
          path: r.path + (r.path === '/' ? 'dashboard' : ''),
          meta: r.children[0].meta
        }))
    }
  },
  methods: {
    toggleSidebar() {
      this.$store.dispatch('app/toggleSidebar')
    },
    handleCommand(command) {
      if (command === 'logout') {
        this.$confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$store.dispatch('user/logout').then(() => {
            this.$router.push('/login')
            this.$message.success('已退出登录')
          })
        }).catch(() => {})
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.app-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.app-header {
  background: $header-bg;
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  z-index: 100;

  .header-left {
    display: flex;
    align-items: center;
    gap: 16px;

    .fold-btn {
      font-size: 20px;
      color: #fff;
      cursor: pointer;
      transition: $transition-base;

      &:hover {
        transform: scale(1.1);
      }
    }

    .logo {
      display: flex;
      align-items: center;
      gap: 8px;
      color: #fff;
      font-size: 18px;
      font-weight: 600;

      i {
        font-size: 24px;
      }
    }
  }

  .header-right {
    .user-info {
      display: flex;
      align-items: center;
      gap: 8px;
      color: #fff;
      cursor: pointer;
      font-size: 14px;
    }
  }
}

.main-container > .el-container {
  display: flex;
  flex: 1;
  min-height: 0;
  overflow: hidden;
}

.app-sidebar {
  background: $sidebar-bg;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.05);
  transition: width 0.3s;
  overflow-x: hidden;
  overflow-y: auto;
  height: 100%;
  flex-shrink: 0;
}

.app-main {
  background: $content-bg;
  padding: 0;
  overflow-y: auto;
  overflow-x: hidden;
  flex: 1;
  min-height: 0;
}

.page-container {
  padding: 20px;
  min-height: 100%;
}

.breadcrumb-container {
  margin-bottom: 20px;
  padding: 12px 20px;
  background: #fff;
  border-radius: $border-radius;
  box-shadow: $card-shadow;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s, transform 0.3s;
}

.fade-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>
