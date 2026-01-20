<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { mockOrderService, type Order } from '@/services/mock'
import { showFailToast, showToast } from 'vant'

const router = useRouter()
const orders = ref<Order[]>([])
const loading = ref(true)
const activeTab = ref('all')

const tabs = [
  { name: '全部', value: 'all' },
  { name: '待处理', value: 'pending' },
  { name: '已接受', value: 'accepted' },
  { name: '已完成', value: 'completed' }
]

const filteredOrders = computed(() => {
  if (activeTab.value === 'all') {
    return orders.value
  }
  return orders.value.filter(o => o.status === activeTab.value)
})

const loadOrders = async () => {
  loading.value = true
  try {
    const response = await mockOrderService.getOrders()
    orders.value = response.data
  } catch (error) {
    showFailToast('加载订单失败')
  } finally {
    loading.value = false
  }
}

const handleTabChange = (tab: string) => {
  activeTab.value = tab
}

const navigateToCreate = () => {
  router.push('/orders/create')
}

const navigateToDetail = (id: number) => {
  router.push(`/orders/${id}`)
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

const formatTime = (time: string) => {
  return new Date(time).toLocaleString('zh-CN')
}

onMounted(() => {
  loadOrders()
})
</script>

<template>
  <div class="order-list-page">
    <div class="tabs-header">
      <van-tabs v-model:active="activeTab" @change="handleTabChange">
        <van-tab
          v-for="tab in tabs"
          :key="tab.value"
          :name="tab.value"
          :title="tab.name"
        />
      </van-tabs>
    </div>

    <div class="order-list" v-if="filteredOrders.length > 0">
      <div
        class="order-card"
        v-for="order in filteredOrders"
        :key="order.id"
        @click="navigateToDetail(order.id)"
      >
        <div class="card-header">
          <span class="order-no">{{ order.orderNo }}</span>
          <span class="order-status" :style="{ color: getStatusColor(order.status) }">
            {{ getStatusText(order.status) }}
          </span>
        </div>
        
        <div class="card-content">
          <div class="order-type">
            <van-icon :name="order.type === 'food' ? 'fire' : order.type === 'shopping' ? 'cart' : 'more'" />
            <span>{{ order.type === 'food' ? '餐饮' : order.type === 'shopping' ? '购物' : '其他' }}</span>
          </div>
          <div class="order-title">{{ order.title }}</div>
          <div class="order-desc" v-if="order.description">{{ order.description }}</div>
        </div>
        
        <div class="card-footer">
          <span class="order-time">{{ formatTime(order.createdTime) }}</span>
          <span class="order-amount">¥{{ order.totalAmount.toFixed(2) }}</span>
        </div>
      </div>
    </div>

    <van-empty v-else-if="!loading" description="暂无订单" />

    <van-loading v-if="loading" size="24px" vertical>加载中...</van-loading>

    <van-button
      type="primary"
      round
      fixed
      right="16px"
      bottom="80px"
      icon="plus"
      @click="navigateToCreate"
    >
      创建订单
    </van-button>
  </div>
</template>

<style lang="scss" scoped>
.order-list-page {
  padding: 16px;
}

.tabs-header {
  margin-bottom: 16px;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.order-card {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f5f5f5;
}

.order-no {
  font-size: 12px;
  color: #969799;
}

.order-status {
  font-size: 14px;
  font-weight: 500;
}

.card-content {
  margin-bottom: 12px;
}

.order-type {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #1989fa;
  margin-bottom: 8px;
}

.order-title {
  font-size: 16px;
  font-weight: 500;
  color: #323233;
  margin-bottom: 8px;
}

.order-desc {
  font-size: 14px;
  color: #969799;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #f5f5f5;
}

.order-time {
  font-size: 12px;
  color: #969799;
}

.order-amount {
  font-size: 18px;
  font-weight: 600;
  color: #07c160;
}

.van-button--primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}
</style>
