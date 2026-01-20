<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { showFailToast, showSuccessToast } from 'vant'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const account = ref('13800138000')
const password = ref('123456')
const showPassword = ref(false)
const loading = ref(false)

const handleLogin = async () => {
  if (!account.value || !password.value) {
    showFailToast('请输入账号和密码')
    return
  }

  loading.value = true
  try {
    await userStore.handleLogin(account.value, password.value)
    showSuccessToast('登录成功')
    
    const redirect = route.query.redirect as string
    if (redirect) {
      router.push(redirect)
    } else {
      router.push('/')
    }
  } catch (error: any) {
    showFailToast(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}

const navigateToRegister = () => {
  router.push('/register')
}
</script>

<template>
  <div class="login-page">
    <div class="logo-section">
      <div class="logo">
        <van-icon name="like" size="64" color="#fff" />
      </div>
      <div class="app-name">情侣互动</div>
      <div class="app-slogan">让爱更有仪式感</div>
    </div>

    <div class="form-section">
      <van-form @submit="handleLogin">
        <van-field
          v-model="account"
          name="account"
          label=""
          placeholder="请输入手机号/用户名"
          :rules="[{ required: true, message: '请输入账号' }]"
          left-icon="user-o"
        />
        <van-field
          v-model="password"
          :type="showPassword ? 'text' : 'password'"
          name="password"
          label=""
          placeholder="请输入密码"
          :rules="[{ required: true, message: '请输入密码' }]"
          left-icon="lock"
          :right-icon="showPassword ? 'eye-o' : 'closed-eye'"
          @click-right-icon="showPassword = !showPassword"
        />
        <div class="forgot-password">
          <span>忘记密码？</span>
        </div>
        <van-button
          type="primary"
          native-type="submit"
          block
          :loading="loading"
          class="login-btn"
        >
          登录
        </van-button>
      </van-form>

      <div class="register-section">
        <span>还没有账号？</span>
        <span class="register-link" @click="navigateToRegister">立即注册</span>
      </div>
    </div>

    <div class="other-login">
      <div class="divider">
        <span>其他登录方式</span>
      </div>
      <div class="login-options">
        <div class="login-option">
          <van-icon name="wechat" size="32" color="#07c160" />
          <span>微信</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
  padding: 60px 32px;
}

.logo-section {
  text-align: center;
  margin-bottom: 48px;
}

.logo {
  width: 100px;
  height: 100px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
}

.app-name {
  font-size: 28px;
  font-weight: 600;
  color: #fff;
  margin-bottom: 8px;
}

.app-slogan {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
}

.form-section {
  background: #fff;
  border-radius: 16px;
  padding: 32px 24px;
  margin-bottom: 24px;
}

.van-field {
  padding: 16px 0;
  margin-bottom: 8px;
  border-bottom: 1px solid #f5f5f5;
}

.forgot-password {
  text-align: right;
  margin: 16px 0;
  font-size: 14px;
  color: #1989fa;
  cursor: pointer;
}

.login-btn {
  height: 48px;
  border-radius: 24px;
  font-size: 16px;
  font-weight: 600;
}

.register-section {
  text-align: center;
  margin-top: 24px;
  font-size: 14px;
  color: #969799;
}

.register-link {
  color: #1989fa;
  margin-left: 4px;
  cursor: pointer;
}

.other-login {
  text-align: center;
}

.divider {
  position: relative;
  margin-bottom: 24px;
  
  &::before {
    content: '';
    position: absolute;
    left: 0;
    top: 50%;
    width: 100%;
    height: 1px;
    background: rgba(255, 255, 255, 0.3);
  }
  
  span {
    position: relative;
    background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
    padding: 0 16px;
    font-size: 14px;
    color: rgba(255, 255, 255, 0.8);
  }
}

.login-options {
  display: flex;
  justify-content: center;
  gap: 48px;
}

.login-option {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  
  span {
    font-size: 12px;
    color: rgba(255, 255, 255, 0.8);
  }
}
</style>
