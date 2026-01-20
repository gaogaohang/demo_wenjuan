<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { showFailToast, showSuccessToast } from 'vant'

const router = useRouter()
const userStore = useUserStore()

const phone = ref('')
const code = ref('')
const password = ref('')
const confirmPassword = ref('')
const nickname = ref('')
const showPassword = ref(false)
const loading = ref(false)
const sendCodeLoading = ref(false)
const countdown = ref(0)

const handleSendCode = async () => {
  if (!phone.value) {
    showFailToast('请输入手机号')
    return
  }
  
  sendCodeLoading.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 500))
    countdown.value = 60
    const timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) {
        clearInterval(timer)
      }
    }, 1000)
    showSuccessToast('验证码已发送')
  } catch (error) {
    showFailToast('发送失败')
  } finally {
    sendCodeLoading.value = false
  }
}

const handleRegister = async () => {
  if (!phone.value || !code.value || !password.value || !nickname.value) {
    showFailToast('请填写完整信息')
    return
  }
  
  if (password.value !== confirmPassword.value) {
    showFailToast('两次密码不一致')
    return
  }
  
  if (password.value.length < 6) {
    showFailToast('密码至少6位')
    return
  }

  loading.value = true
  try {
    await userStore.handleRegister({
      phone: phone.value,
      code: code.value,
      password: password.value,
      nickname: nickname.value
    })
    showSuccessToast('注册成功')
    router.push('/')
  } catch (error: any) {
    showFailToast(error.message || '注册失败')
  } finally {
    loading.value = false
  }
}

const navigateToLogin = () => {
  router.push('/login')
}
</script>

<template>
  <div class="register-page">
    <div class="header">
      <van-icon name="arrow-left" @click="navigateToLogin" />
      <span>注册</span>
      <span></span>
    </div>

    <div class="form-section">
      <van-form @submit="handleRegister">
        <div class="form-title">欢迎注册</div>
        
        <van-field
          v-model="nickname"
          name="nickname"
          label=""
          placeholder="请输入昵称"
          :rules="[{ required: true, message: '请输入昵称' }]"
          left-icon="contact"
        />
        <van-field
          v-model="phone"
          name="phone"
          label=""
          placeholder="请输入手机号"
          :rules="[{ required: true, message: '请输入手机号' }]"
          left-icon="phone-o"
          type="tel"
        />
        <div class="code-field">
          <van-field
            v-model="code"
            name="code"
            label=""
            placeholder="请输入验证码"
            :rules="[{ required: true, message: '请输入验证码' }]"
            left-icon="coupon"
          />
          <van-button
            type="primary"
            size="small"
            :loading="sendCodeLoading"
            :disabled="countdown > 0"
            @click="handleSendCode"
          >
            {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
          </van-button>
        </div>
        <van-field
          v-model="password"
          :type="showPassword ? 'text' : 'password'"
          name="password"
          label=""
          placeholder="请设置6位以上密码"
          :rules="[{ required: true, message: '请设置密码' }]"
          left-icon="lock"
          :right-icon="showPassword ? 'eye-o' : 'closed-eye'"
          @click-right-icon="showPassword = !showPassword"
        />
        <van-field
          v-model="confirmPassword"
          :type="showPassword ? 'text' : 'password'"
          name="confirmPassword"
          label=""
          placeholder="请确认密码"
          :rules="[{ required: true, message: '请确认密码' }]"
          left-icon="lock"
        />
        
        <van-button
          type="primary"
          native-type="submit"
          block
          :loading="loading"
          class="register-btn"
        >
          注册
        </van-button>
      </van-form>

      <div class="agreement">
        注册即表示同意
        <span>《用户协议》</span>
        和
        <span>《隐私政策》</span>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.register-page {
  min-height: 100vh;
  background: #f7f8fa;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #fff;
  font-size: 17px;
  font-weight: 600;
}

.form-section {
  padding: 32px 24px;
}

.form-title {
  font-size: 24px;
  font-weight: 600;
  color: #323233;
  margin-bottom: 32px;
}

.van-field {
  padding: 12px 0;
  margin-bottom: 8px;
  background: #fff;
  border-bottom: 1px solid #f5f5f5;
}

.code-field {
  display: flex;
  align-items: center;
  background: #fff;
  border-bottom: 1px solid #f5f5f5;
  
  .van-field {
    flex: 1;
    margin-bottom: 0;
    border-bottom: none;
  }
  
  .van-button {
    margin-right: 12px;
    border-radius: 20px;
  }
}

.register-btn {
  height: 48px;
  border-radius: 24px;
  font-size: 16px;
  font-weight: 600;
  margin-top: 24px;
}

.agreement {
  text-align: center;
  margin-top: 24px;
  font-size: 12px;
  color: #969799;
  
  span {
    color: #1989fa;
  }
}
</style>
