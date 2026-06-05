<template>
  <div class="login-container">
    <div class="login-bg"></div>
    <div class="login-box fade-in">
      <div class="login-header">
        <div class="logo-wrapper">
          <i class="el-icon-aim logo-icon"></i>
        </div>
        <h1 class="title">城市单车骑行补给站点台账系统</h1>
        <p class="subtitle">City Bicycle Supply Station Management System</p>
      </div>

      <el-form
        ref="loginForm"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        @keyup.enter.native="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            prefix-icon="el-icon-user"
            size="large"
          ></el-input>
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="el-icon-lock"
            size="large"
            show-password
          ></el-input>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="login-btn"
            :loading="loading"
            @click="handleLogin"
          >
            登 录
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-footer">
        <p><i class="el-icon-info"></i> 默认账号：admin / admin123</p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Login',
  data() {
    return {
      loginForm: {
        username: 'admin',
        password: 'admin123'
      },
      loginRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
        ]
      },
      loading: false
    }
  },
  methods: {
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          this.$store.dispatch('user/login', this.loginForm).then(() => {
            this.$message.success('登录成功')
            this.$router.push('/dashboard')
          }).finally(() => {
            this.loading = false
          })
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.login-container {
  position: relative;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.login-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #4CAF90 0%, #87CEEB 50%, #90EE90 100%);

  &::before {
    content: '';
    position: absolute;
    top: 10%;
    left: 10%;
    width: 300px;
    height: 300px;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 50%;
    animation: float 6s ease-in-out infinite;
  }

  &::after {
    content: '';
    position: absolute;
    bottom: 15%;
    right: 10%;
    width: 400px;
    height: 400px;
    background: rgba(255, 255, 255, 0.08);
    border-radius: 50%;
    animation: float 8s ease-in-out infinite reverse;
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(30px);
  }
}

.login-box {
  position: relative;
  z-index: 10;
  width: 420px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
  backdrop-filter: blur(10px);
}

.login-header {
  text-align: center;
  margin-bottom: 30px;

  .logo-wrapper {
    width: 80px;
    height: 80px;
    margin: 0 auto 20px;
    background: linear-gradient(135deg, #4CAF90 0%, #87CEEB 100%);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 8px 20px rgba(76, 175, 144, 0.4);

    .logo-icon {
      font-size: 40px;
      color: #fff;
    }
  }

  .title {
    font-size: 22px;
    color: $text-primary;
    margin-bottom: 8px;
    font-weight: 600;
  }

  .subtitle {
    font-size: 13px;
    color: $text-light;
  }
}

.login-form {
  .login-btn {
    width: 100%;
    height: 48px;
    font-size: 16px;
    font-weight: 500;
    letter-spacing: 4px;
    background: linear-gradient(90deg, #4CAF90 0%, #87CEEB 100%);
    border: none;
    box-shadow: 0 4px 12px rgba(76, 175, 144, 0.4);
    transition: $transition-base;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 20px rgba(76, 175, 144, 0.5);
    }

    &:active {
      transform: translateY(0);
    }
  }
}

.login-footer {
  margin-top: 20px;
  text-align: center;

  p {
    font-size: 13px;
    color: $text-light;

    i {
      margin-right: 4px;
      color: $primary-color;
    }
  }
}

::v-deep .el-input__inner {
  height: 48px;
  border-radius: 8px;
}

::v-deep .el-input__prefix {
  color: $primary-color;
  font-size: 18px;
}
</style>
