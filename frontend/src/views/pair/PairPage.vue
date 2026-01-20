<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { showFailToast, showSuccessToast } from 'vant'

const router = useRouter()
const userStore = useUserStore()

const phone = ref('')
const loading = ref(false)

const handlePair = async () => {
  if (!phone.value) {
    showFailToast('请输入手机号')
    return
  }
  
  if (phone.value === userStore.userInfo?.phone) {
    showFailToast('不能与自己配对')
    return
  }

  loading.value = true
  try {
    await userStore.createPair(phone.value)
    showSuccessToast('配对请求已发送')
    router.back()
  } catch (error) {
    showFailToast('配对失败')
  } finally {
    loading.value = false
  }
}

const showPairCode = () => {
  if (userStore.pairInfo) {
    showSuccessToast(`您的配对码是: ${userStore.pairInfo.pairCode}`)
  }
}
</script>

<template>
  <div class="pair-page">
    <div class="pair-info-section">
      <div class="pair-icon">
        <van-icon name="like" size="64" color="#ee0a24" />
      </div>
      <div class="pair-title">情侣配对</div>
      <div class="pair-desc">输入对方的手机号，即可发起配对请求</div>
    </div>

    <div class="pair-form-section">
      <van-form @submit="handlePair">
        <van-field
          v-model="phone"
          label=""
          placeholder="请输入对方手机号"
          type="tel"
          left-icon="phone-o"
          :rules="[{ required: true, message: '请输入手机号' }]"
        />
        <van-button type="primary" native-type="submit" block :loading="loading">
          发送配对请求
        </van-button>
      </van-form>
    </div>

    <div class="pair-code-section" @click="showPairCode">
      <div class="code-title">或分享您的配对码</div>
      <div class="code-display">
        <van-icon name="coupon" />
        <span>{{ userStore.pairInfo?.pairCode || '暂无配对码' }}</span>
        <van-icon name="arrow" />
      </div>
    </div>

    <div class="tips-section">
      <div class="tips-title">配对须知</div>
      <div class="tips-list">
        <div class="tips-item">
          <van-icon name="success" color="#07c160" />
          <span>配对成功后，你们可以共享订单、问卷等功能</span>
        </div>
        <div class="tips-item">
          <van-icon name="success" color="#07c160" />
          <span>每人只能与一位伴侣配对</span>
        </div>
        <div class="tips-item">
          <van-icon name="success" color="#07c160" />
          <span>配对后可以随时解除</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.pair-page {
  min-height: 100vh;
  background: #f7f8fa;
  padding: 32px 24px;
}

.pair-info-section {
  text-align: center;
  margin-bottom: 32px;
}

.pair-icon {
  width: 100px;
  height: 100px;
  background: #fee2e2;
  border-radius: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
}

.pair-title {
  font-size: 24px;
  font-weight: 600;
  color: #323233;
  margin-bottom: 8px;
}

.pair-desc {
  font-size: 14px;
  color: #969799;
}

.pair-form-section {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  
  .van-field {
    padding: 12px 0;
    margin-bottom: 16px;
    border-bottom: 1px solid #f5f5f5;
  }
  
  .van-button {
    border-radius: 24px;
    height: 48px;
  }
}

.pair-code-section {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 24px;
  cursor: pointer;
}

.code-title {
  font-size: 14px;
  color: #969799;
  margin-bottom: 12px;
}

.code-display {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #f7f8fa;
  border-radius: 8px;
  font-size: 18px;
  font-weight: 600;
  color: #323233;
  
  .van-icon:last-child {
    margin-left: auto;
    color: #969799;
  }
}

.tips-section {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
}

.tips-title {
  font-size: 14px;
  font-weight: 600;
  color: #323233;
  margin-bottom: 12px;
}

.tips-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.tips-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  font-size: 14px;
  color: #969799;
  
  span {
    flex: 1;
  }
}
</style>
