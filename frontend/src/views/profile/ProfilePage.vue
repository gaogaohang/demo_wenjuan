<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { showActionSheet, showFailToast, showSuccessToast } from 'vant'

const router = useRouter()
const userStore = useUserStore()

const showEditSheet = ref(false)
const editingField = ref('')
const editingValue = ref('')

const menuItems = [
  { name: '个人资料', icon: 'user-o', route: '/profile/edit' },
  { name: '账号安全', icon: 'lock', route: '/profile/security' },
  { name: '隐私设置', icon: 'shield-o', route: '/profile/privacy' },
  { name: '通知设置', icon: 'bell', route: '/profile/notifications' }
]

const aboutItems = [
  { name: '帮助中心', icon: 'question-o', route: '/profile/help' },
  { name: '意见反馈', icon: 'chat-o', route: '/profile/feedback' },
  { name: '关于我们', icon: 'info-o', route: '/profile/about' }
]

const partnerInfo = computed(() => {
  if (userStore.pairInfo) {
    return {
      nickname: userStore.pairInfo.partnerNickname,
      avatar: userStore.pairInfo.partnerAvatar,
      pairCode: userStore.pairInfo.pairCode
    }
  }
  return null
})

const handleLogout = () => {
  showActionSheet({
    title: '退出登录',
    message: '确定要退出登录吗？',
    actions: [
      { name: '退出登录', color: '#ee0a24' }
    ],
    cancelButtonText: '取消'
  }).then((action) => {
    if (action.name === '退出登录') {
      userStore.handleLogout()
    }
  })
}

const navigateTo = (route: string) => {
  router.push(route)
}

const handlePair = () => {
  router.push('/pair')
}

const handleUnpair = () => {
  showActionSheet({
    title: '解除配对',
    message: '确定要解除情侣配对吗？此操作不可恢复。',
    actions: [
      { name: '解除配对', color: '#ee0a24' }
    ],
    cancelButtonText: '取消'
  }).then(async (action) => {
    if (action.name === '解除配对') {
      showSuccessToast('已解除配对')
      userStore.setPairInfo(null)
    }
  })
}
</script>

<template>
  <div class="profile-page">
    <div class="profile-header">
      <div class="header-bg"></div>
      <div class="user-card">
        <van-image
          round
          :src="userStore.userInfo?.avatarUrl"
          class="avatar"
        />
        <div class="user-info">
          <div class="nickname">{{ userStore.userInfo?.nickname }}</div>
          <div class="username">@{{ userStore.userInfo?.username }}</div>
        </div>
        <van-button type="primary" plain size="small" round @click="navigateTo('/profile/edit')">
          编辑资料
        </van-button>
      </div>
    </div>

    <div class="pair-section" v-if="partnerInfo">
      <div class="section-title">我的伴侣</div>
      <div class="pair-card">
        <div class="partner-info">
          <van-image round :src="partnerInfo.avatar" class="partner-avatar" />
          <div class="partner-text">
            <div class="partner-name">{{ partnerInfo.nickname }}</div>
            <div class="pair-code">配对码: {{ partnerInfo.pairCode }}</div>
          </div>
        </div>
        <div class="pair-actions">
          <van-button type="primary" plain size="small" @click="handleUnpair">
            解除配对
          </van-button>
        </div>
      </div>
    </div>

    <div class="pair-section" v-else>
      <div class="section-title">情侣配对</div>
      <div class="no-pair-card">
        <van-icon name="like-o" size="48" color="#969799" />
        <div class="no-pair-text">还未配对恋人</div>
        <div class="no-pair-desc">输入对方的手机号即可配对</div>
        <van-button type="primary" @click="handlePair">立即配对</van-button>
      </div>
    </div>

    <div class="menu-section">
      <div class="section-title">常用功能</div>
      <div class="menu-grid">
        <div
          class="menu-item"
          v-for="item in menuItems"
          :key="item.route"
          @click="navigateTo(item.route)"
        >
          <van-icon :name="item.icon" size="24" color="#1989fa" />
          <span>{{ item.name }}</span>
        </div>
      </div>
    </div>

    <div class="menu-section">
      <div class="section-title">关于我们</div>
      <div class="menu-list">
        <div
          class="menu-row"
          v-for="item in aboutItems"
          :key="item.route"
          @click="navigateTo(item.route)"
        >
          <div class="menu-left">
            <van-icon :name="item.icon" />
            <span>{{ item.name }}</span>
          </div>
          <van-icon name="arrow" />
        </div>
      </div>
    </div>

    <div class="logout-section">
      <van-button type="default" block @click="handleLogout">退出登录</van-button>
    </div>

    <div class="version-info">
      <span>版本 1.0.0</span>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.profile-page {
  min-height: 100vh;
  background: #f7f8fa;
  padding-bottom: 32px;
}

.profile-header {
  position: relative;
  margin-bottom: 16px;
}

.header-bg {
  height: 120px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.user-card {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 0 16px 24px;
  margin: -40px 16px 0;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.avatar {
  width: 80px;
  height: 80px;
  margin-top: -40px;
  border: 4px solid #fff;
}

.user-info {
  text-align: center;
  margin: 12px 0;
}

.nickname {
  font-size: 20px;
  font-weight: 600;
  color: #323233;
}

.username {
  font-size: 14px;
  color: #969799;
  margin-top: 4px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #323233;
  margin-bottom: 12px;
  padding: 0 16px;
}

.pair-section {
  background: #fff;
  padding: 16px 0;
  margin-bottom: 16px;
}

.pair-card {
  margin: 0 16px;
  padding: 16px;
  background: #f7f8fa;
  border-radius: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.partner-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.partner-avatar {
  width: 48px;
  height: 48px;
}

.partner-name {
  font-size: 16px;
  font-weight: 500;
  color: #323233;
}

.pair-code {
  font-size: 12px;
  color: #969799;
  margin-top: 4px;
}

.no-pair-card {
  margin: 0 16px;
  padding: 32px 16px;
  background: #f7f8fa;
  border-radius: 12px;
  text-align: center;
}

.no-pair-text {
  font-size: 16px;
  font-weight: 500;
  color: #323233;
  margin: 16px 0 8px;
}

.no-pair-desc {
  font-size: 14px;
  color: #969799;
  margin-bottom: 16px;
}

.menu-section {
  background: #fff;
  padding: 16px 0;
  margin-bottom: 16px;
}

.menu-grid {
  display: flex;
  flex-wrap: wrap;
  padding: 0 16px;
}

.menu-item {
  width: 25%;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 12px 0;
  cursor: pointer;
  
  span {
    font-size: 12px;
    color: #323233;
  }
}

.menu-list {
  padding: 0 16px;
}

.menu-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
  
  &:last-child {
    border-bottom: none;
  }
}

.menu-left {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  color: #323233;
}

.logout-section {
  padding: 16px;
  margin: 16px;
  
  .van-button {
    border-radius: 24px;
  }
}

.version-info {
  text-align: center;
  padding: 16px;
  font-size: 12px;
  color: #969799;
}
</style>
