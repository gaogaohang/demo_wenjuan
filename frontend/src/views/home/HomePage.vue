<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { mockOrderService, mockSurveyService, mockMessageService } from '@/services/mock'
import type { Order, Survey } from '@/services/mock'
import { showFailToast } from 'vant'

const router = useRouter()
const userStore = useUserStore()

const recentOrders = ref<Order[]>([])
const recentSurveys = ref<Survey[]>([])
const loading = ref(true)

const partnerInfo = computed(() => {
  if (userStore.pairInfo) {
    return {
      nickname: userStore.pairInfo.partnerNickname,
      avatar: userStore.pairInfo.partnerAvatar
    }
  }
  return null
})

const stats = computed(() => ({
  orders: recentOrders.value.length,
  surveys: recentSurveys.value.length,
  pendingOrders: recentOrders.value.filter(o => o.status === 'pending').length
}))

const quickActions = [
  { name: '创建订单', icon: 'add-o', color: '#07c160', route: '/orders/create' },
  { name: '创建问卷', icon: 'edit', color: '#1989fa', route: '/surveys/create' },
  { name: '情侣配对', icon: 'coupon-o', color: '#ee0a24', route: '/pair' },
  { name: '消息', icon: 'chat-o', color: '#7232dd', route: '/messages' }
]

const loadData = async () => {
  loading.value = true
  try {
    const [ordersRes, surveysRes] = await Promise.all([
      mockOrderService.getOrders(),
      mockSurveyService.getSurveys()
    ])
    recentOrders.value = ordersRes.data.slice(0, 3)
    recentSurveys.value = surveysRes.data.filter(s => s.status === 'published').slice(0, 3)
  } catch (error) {
    showFailToast('加载数据失败')
  } finally {
    loading.value = false
  }
}

const navigateTo = (route: string) => {
  router.push(route)
}

const formatTime = (time: string) => {
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  return `${Math.floor(diff / 86400000)}天前`
}

const getStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    pending: '待处理',
    accepted: '已接受',
    processing: '处理中',
    completed: '已完成',
    cancelled: '已取消'
  }
  return statusMap[status] || status
}

const getStatusColor = (status: string) => {
  const colorMap: Record<string, string> = {
    pending: '#ff976a',
    accepted: '#1989fa',
    processing: '#7232dd',
    completed: '#07c160',
    cancelled: '#969799'
  }
  return colorMap[status] || '#969799'
}

onMounted(() => {
  loadData()
})
</script>

<template>
  <div class="home-page">
    <div class="header-section">
      <div class="user-card">
        <div class="user-info">
          <van-image
            round
            :src="userStore.userInfo?.avatarUrl"
            class="avatar"
          />
          <div class="info-text">
            <div class="nickname">{{ userStore.userInfo?.nickname }}</div>
            <div class="partner" v-if="partnerInfo">
              <van-icon name="like" color="#ee0a24" />
              <span>{{ partnerInfo.nickname }}</span>
            </div>
            <div class="partner" v-else>
              <span>还未配对</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="stats-section">
      <div class="stat-card">
        <div class="stat-value">{{ stats.orders }}</div>
        <div class="stat-label">订单数</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ stats.surveys }}</div>
        <div class="stat-label">问卷数</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ stats.pendingOrders }}</div>
        <div class="stat-label">待处理</div>
      </div>
    </div>

    <div class="quick-actions">
      <div class="section-title">快捷操作</div>
      <div class="actions-grid">
        <div
          class="action-item"
          v-for="action in quickActions"
          :key="action.name"
          @click="navigateTo(action.route)"
        >
          <div class="action-icon" :style="{ backgroundColor: action.color }">
            <van-icon :name="action.icon" size="24" />
          </div>
          <div class="action-name">{{ action.name }}</div>
        </div>
      </div>
    </div>

    <div class="recent-section">
      <div class="section-header">
        <div class="section-title">最近订单</div>
        <div class="see-all" @click="navigateTo('/orders')">查看全部</div>
      </div>
      <div class="order-list" v-if="recentOrders.length > 0">
        <div
          class="order-item"
          v-for="order in recentOrders"
          :key="order.id"
          @click="navigateTo(`/orders/${order.id}`)"
        >
          <div class="order-header">
            <span class="order-no">{{ order.orderNo }}</span>
            <span class="order-status" :style="{ color: getStatusColor(order.status) }">
              {{ getStatusText(order.status) }}
            </span>
          </div>
          <div class="order-content">
            <div class="order-title">{{ order.title }}</div>
            <div class="order-time">{{ formatTime(order.createdTime) }}</div>
          </div>
          <div class="order-amount">¥{{ order.totalAmount.toFixed(2) }}</div>
        </div>
      </div>
      <van-empty v-else description="暂无订单" />
    </div>

    <div class="recent-section">
      <div class="section-header">
        <div class="section-title">问卷动态</div>
        <div class="see-all" @click="navigateTo('/surveys')">查看全部</div>
      </div>
      <div class="survey-list" v-if="recentSurveys.length > 0">
        <div
          class="survey-item"
          v-for="survey in recentSurveys"
          :key="survey.id"
          @click="navigateTo(`/surveys/${survey.id}`)"
        >
          <div class="survey-icon">
            <van-icon name="notes" color="#1989fa" size="24" />
          </div>
          <div class="survey-info">
            <div class="survey-title">{{ survey.title }}</div>
            <div class="survey-meta">
              <span>{{ survey.responseCount }}人已回答</span>
              <span>{{ formatTime(survey.createdTime) }}</span>
            </div>
          </div>
          <van-icon name="arrow" color="#969799" />
        </div>
      </div>
      <van-empty v-else description="暂无问卷" />
    </div>
  </div>
</template>

<style lang="scss" scoped>
.home-page {
  padding: 16px;
}

.header-section {
  margin-bottom: 16px;
}

.user-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 20px;
  color: #fff;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.avatar {
  width: 64px;
  height: 64px;
  border: 3px solid rgba(255, 255, 255, 0.3);
}

.info-text {
  flex: 1;
}

.nickname {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 8px;
}

.partner {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  opacity: 0.9;
}

.stats-section {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.stat-card {
  flex: 1;
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #1989fa;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: #969799;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #323233;
  margin-bottom: 12px;
}

.quick-actions {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.actions-grid {
  display: flex;
  justify-content: space-around;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.action-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.action-name {
  font-size: 12px;
  color: #323233;
}

.recent-section {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.see-all {
  font-size: 14px;
  color: #1989fa;
  cursor: pointer;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.order-item {
  display: flex;
  align-items: center;
  padding: 12px;
  background: #f7f8fa;
  border-radius: 8px;
  cursor: pointer;
}

.order-header {
  width: 100px;
}

.order-no {
  font-size: 12px;
  color: #969799;
}

.order-status {
  font-size: 12px;
  margin-left: 8px;
}

.order-content {
  flex: 1;
}

.order-title {
  font-size: 14px;
  color: #323233;
  margin-bottom: 4px;
}

.order-time {
  font-size: 12px;
  color: #969799;
}

.order-amount {
  font-size: 16px;
  font-weight: 600;
  color: #07c160;
}

.survey-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.survey-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #f7f8fa;
  border-radius: 8px;
  cursor: pointer;
}

.survey-icon {
  width: 40px;
  height: 40px;
  background: #e6f4ff;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.survey-info {
  flex: 1;
}

.survey-title {
  font-size: 14px;
  color: #323233;
  margin-bottom: 4px;
}

.survey-meta {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: #969799;
}
</style>
