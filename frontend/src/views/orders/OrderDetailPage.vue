<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { mockOrderService, type Order } from '@/services/mock'
import { showFailToast, showSuccessToast, showDialog } from 'vant'

const route = useRoute()
const router = useRouter()

const order = ref<Order | null>(null)
const loading = ref(true)
const actionLoading = ref(false)

const loadOrder = async () => {
  const id = parseInt(route.params.id as string)
  loading.value = true
  try {
    const response = await mockOrderService.getOrderDetail(id)
    order.value = response.data
  } catch (error) {
    showFailToast('加载订单失败')
    router.back()
  } finally {
    loading.value = false
  }
}

const handleAccept = async () => {
  if (!order.value) return
  
  actionLoading.value = true
  try {
    await mockOrderService.updateOrderStatus(order.value.id, 'accepted')
    showSuccessToast('已接受订单')
    order.value.status = 'accepted'
  } catch (error) {
    showFailToast('操作失败')
  } finally {
    actionLoading.value = false
  }
}

const handleComplete = async () => {
  if (!order.value) return
  
  actionLoading.value = true
  try {
    await mockOrderService.updateOrderStatus(order.value.id, 'completed')
    showSuccessToast('订单已完成')
    order.value.status = 'completed'
  } catch (error) {
    showFailToast('操作失败')
  } finally {
    actionLoading.value = false
  }
}

const handleCancel = async () => {
  if (!order.value) return
  
  showDialog({
    title: '确认取消',
    message: '确定要取消这个订单吗？',
    showCancelButton: true
  }).then(async () => {
    actionLoading.value = true
    try {
      await mockOrderService.cancelOrder(order.value!.id)
      showSuccessToast('订单已取消')
      order.value!.status = 'cancelled'
    } catch (error) {
      showFailToast('操作失败')
    } finally {
      actionLoading.value = false
    }
  })
}

const handleEvaluate = () => {
  showSuccessToast('评价功能开发中')
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
  loadOrder()
})
</script>

<template>
  <div class="order-detail-page" v-if="order">
    <div class="status-bar" :style="{ backgroundColor: getStatusColor(order.status) }">
      <div class="status-text">{{ getStatusText(order.status) }}</div>
      <div class="status-desc" v-if="order.status === 'pending'">等待对方处理</div>
      <div class="status-desc" v-else-if="order.status === 'accepted'">对方正在处理中</div>
      <div class="status-desc" v-else-if="order.status === 'completed'">订单已完成</div>
    </div>

    <div class="order-info">
      <div class="info-header">
        <van-icon :name="order.type === 'food' ? 'fire' : order.type === 'shopping' ? 'cart' : 'more'" size="24" />
        <span class="order-title">{{ order.title }}</span>
      </div>
      
      <div class="info-row" v-if="order.description">
        <span class="label">订单描述</span>
        <span class="value">{{ order.description }}</span>
      </div>
      
      <div class="info-row" v-if="order.note">
        <span class="label">备注</span>
        <span class="value">{{ order.note }}</span>
      </div>
      
      <div class="info-row" v-if="order.location">
        <span class="label">地址</span>
        <span class="value">{{ order.location }}</span>
      </div>
      
      <div class="info-row" v-if="order.estimatedTime">
        <span class="label">预计时间</span>
        <span class="value">{{ new Date(order.estimatedTime).toLocaleString() }}</span>
      </div>
    </div>

    <div class="items-section" v-if="order.items.length > 0">
      <div class="section-title">订单明细</div>
      <div class="item-list">
        <div class="item" v-for="item in order.items" :key="item.id">
          <div class="item-info">
            <div class="item-name">{{ item.name }}</div>
            <div class="item-desc" v-if="item.description">{{ item.description }}</div>
            <div class="item-qty">x{{ item.quantity }}</div>
          </div>
          <div class="item-price">¥{{ item.totalPrice.toFixed(2) }}</div>
        </div>
      </div>
      <div class="total-row">
        <span>合计</span>
        <span class="total-price">¥{{ order.totalAmount.toFixed(2) }}</span>
      </div>
    </div>

    <div class="users-section">
      <div class="section-title">参与用户</div>
      <div class="user-row">
        <div class="user-info">
          <van-image round :src="order.creator.avatarUrl" />
          <div class="user-text">
            <div class="user-name">{{ order.creator.nickname }}</div>
            <div class="user-role">创建者</div>
          </div>
        </div>
        <van-icon name="arrow" />
      </div>
      <div class="user-row">
        <div class="user-info">
          <van-image round :src="order.receiver.avatarUrl" />
          <div class="user-text">
            <div class="user-name">{{ order.receiver.nickname }}</div>
            <div class="user-role">接收者</div>
          </div>
        </div>
        <van-icon name="arrow" />
      </div>
    </div>

    <div class="time-section">
      <div class="section-title">时间信息</div>
      <div class="info-row">
        <span class="label">创建时间</span>
        <span class="value">{{ new Date(order.createdTime).toLocaleString() }}</span>
      </div>
      <div class="info-row" v-if="order.acceptedTime">
        <span class="label">接受时间</span>
        <span class="value">{{ new Date(order.acceptedTime).toLocaleString() }}</span>
      </div>
      <div class="info-row" v-if="order.completedTime">
        <span class="label">完成时间</span>
        <span class="value">{{ new Date(order.completedTime).toLocaleString() }}</span>
      </div>
    </div>

    <div class="actions-bar" v-if="order.status !== 'cancelled' && order.status !== 'completed'">
      <van-button v-if="order.status === 'pending'" type="primary" :loading="actionLoading" @click="handleAccept">
        接受订单
      </van-button>
      <van-button v-if="order.status === 'accepted'" type="primary" :loading="actionLoading" @click="handleComplete">
        完成订单
      </van-button>
      <van-button v-if="order.status !== 'cancelled'" plain hairline type="default" @click="handleCancel">
        取消订单
      </van-button>
    </div>

    <div class="actions-bar" v-if="order.status === 'completed' && order.evaluations.length === 0">
      <van-button type="primary" @click="handleEvaluate">评价订单</van-button>
    </div>
  </div>

  <van-loading v-else size="24px" vertical>加载中...</van-loading>
</template>

<style lang="scss" scoped>
.order-detail-page {
  padding-bottom: 80px;
}

.status-bar {
  padding: 32px 16px;
  color: #fff;
}

.status-text {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 8px;
}

.status-desc {
  font-size: 14px;
  opacity: 0.9;
}

.order-info {
  background: #fff;
  padding: 16px;
  margin: -20px 16px 16px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.info-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
  color: #1989fa;
}

.order-title {
  font-size: 18px;
  font-weight: 600;
  color: #323233;
}

.info-row {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid #f5f5f5;
  
  &:last-child {
    border-bottom: none;
  }
}

.label {
  color: #969799;
  font-size: 14px;
}

.value {
  color: #323233;
  font-size: 14px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #323233;
  margin-bottom: 12px;
}

.items-section,
.users-section,
.time-section {
  background: #fff;
  padding: 16px;
  margin: 16px;
  border-radius: 12px;
}

.item-list {
  margin-bottom: 12px;
}

.item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
  
  &:last-child {
    border-bottom: none;
  }
}

.item-name {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 4px;
}

.item-desc {
  font-size: 12px;
  color: #969799;
}

.item-qty {
  font-size: 12px;
  color: #969799;
}

.item-price {
  font-size: 14px;
  font-weight: 500;
}

.total-row {
  display: flex;
  justify-content: flex-end;
  padding-top: 12px;
  border-top: 1px solid #f5f5f5;
  gap: 16px;
}

.total-price {
  font-size: 18px;
  font-weight: 600;
  color: #07c160;
}

.user-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
  
  &:last-child {
    border-bottom: none;
  }
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-text {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
}

.user-role {
  font-size: 12px;
  color: #969799;
}

.actions-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  gap: 12px;
  padding: 16px;
  background: #fff;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.1);
  
  .van-button {
    flex: 1;
    border-radius: 24px;
  }
}
</style>
