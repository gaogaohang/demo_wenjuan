<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { mockMessageService, type Message } from '@/services/mock'
import { showFailToast, showConfirmDialog } from 'vant'
import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'
import 'dayjs/locale/zh-cn'

dayjs.extend(relativeTime)
dayjs.locale('zh-cn')

const messages = ref<Message[]>([])
const loading = ref(true)
const activeTab = ref('all')

const tabs = [
  { name: '全部', value: 'all' },
  { name: '未读', value: 'unread' },
  { name: '已读', value: 'read' }
]

const filteredMessages = computed(() => {
  if (activeTab.value === 'all') {
    return messages.value
  } else if (activeTab.value === 'unread') {
    return messages.value.filter(m => !m.isRead)
  } else {
    return messages.value.filter(m => m.isRead)
  }
})

const unreadCount = computed(() => messages.value.filter(m => !m.isRead).length)

const loadMessages = async () => {
  loading.value = true
  try {
    const response = await mockMessageService.getMessages()
    messages.value = response.data
  } catch (error) {
    showFailToast('加载消息失败')
  } finally {
    loading.value = false
  }
}

const handleTabChange = (tab: string) => {
  activeTab.value = tab
}

const handleRead = async (message: Message) => {
  if (!message.isRead) {
    try {
      await mockMessageService.markAsRead(message.id)
      message.isRead = true
      message.readTime = new Date().toISOString()
    } catch (error) {
      showFailToast('操作失败')
    }
  }
}

const handleDelete = async (message: Message) => {
  showConfirmDialog({
    title: '确认删除',
    message: '确定要删除这条消息吗？'
  }).then(async () => {
    try {
      await mockMessageService.deleteMessage(message.id)
      const index = messages.value.findIndex(m => m.id === message.id)
      if (index !== -1) {
        messages.value.splice(index, 1)
      }
    } catch (error) {
      showFailToast('删除失败')
    }
  })
}

const handleMarkAllRead = async () => {
  try {
    await mockMessageService.markAllAsRead()
    messages.value.forEach(m => {
      m.isRead = true
      m.readTime = new Date().toISOString()
    })
  } catch (error) {
    showFailToast('操作失败')
  }
}

const getMessageIcon = (type: string) => {
  const iconMap: Record<string, string> = {
    system: 'info-o',
    order: 'cart-o',
    survey: 'notes-o',
    pair: 'like-o'
  }
  return iconMap[type] || 'chat-o'
}

const getMessageColor = (type: string) => {
  const colorMap: Record<string, string> = {
    system: '#1989fa',
    order: '#07c160',
    survey: '#7232dd',
    pair: '#ee0a24'
  }
  return colorMap[type] || '#1989fa'
}

const getMessageTypeName = (type: string) => {
  const nameMap: Record<string, string> = {
    system: '系统',
    order: '订单',
    survey: '问卷',
    pair: '配对'
  }
  return nameMap[type] || '消息'
}

onMounted(() => {
  loadMessages()
})
</script>

<template>
  <div class="message-list-page">
    <div class="header-actions" v-if="unreadCount > 0">
      <van-button type="primary" plain size="small" @click="handleMarkAllRead">
        全部已读
      </van-button>
    </div>

    <div class="tabs-header">
      <van-tabs v-model:active="activeTab" @change="handleTabChange">
        <van-tab
          v-for="tab in tabs"
          :key="tab.value"
          :name="tab.value"
          :title="`${tab.name}${tab.value === 'unread' && unreadCount > 0 ? `(${unreadCount})` : ''}`"
        />
      </van-tabs>
    </div>

    <div class="message-list" v-if="filteredMessages.length > 0">
      <div
        class="message-item"
        v-for="message in filteredMessages"
        :key="message.id"
        :class="{ unread: !message.isRead }"
      >
        <div class="message-icon" :style="{ backgroundColor: getMessageColor(message.type) + '20' }">
          <van-icon :name="getMessageIcon(message.type)" :color="getMessageColor(message.type)" size="20" />
        </div>
        
        <div class="message-content" @click="handleRead(message)">
          <div class="message-header">
            <div class="message-title">
              <span class="type-tag" :style="{ backgroundColor: getMessageColor(message.type) }">
                {{ getMessageTypeName(message.type) }}
              </span>
              <span class="title-text">{{ message.title }}</span>
            </div>
            <span class="message-time">{{ dayjs(message.createdTime).fromNow() }}</span>
          </div>
          
          <div class="message-body">{{ message.content }}</div>
          
          <div class="message-sender" v-if="message.senderNickname">
            <van-image round :src="message.senderAvatar || ''" v-if="message.senderAvatar" />
            <span>{{ message.senderNickname }}</span>
          </div>
        </div>
        
        <div class="message-actions">
          <van-icon name="delete" @click="handleDelete(message)" />
        </div>
      </div>
    </div>

    <van-empty v-else-if="!loading" description="暂无消息" />

    <van-loading v-if="loading" size="24px" vertical>加载中...</van-loading>
  </div>
</template>

<style lang="scss" scoped>
.message-list-page {
  min-height: 100vh;
  background: #f7f8fa;
}

.header-actions {
  padding: 12px 16px;
  background: #fff;
  border-bottom: 1px solid #f5f5f5;
  display: flex;
  justify-content: flex-end;
}

.tabs-header {
  background: #fff;
}

.message-list {
  padding: 8px;
}

.message-item {
  display: flex;
  gap: 12px;
  padding: 16px;
  background: #fff;
  border-radius: 12px;
  margin-bottom: 8px;
  cursor: pointer;
  
  &.unread {
    background: #f0f9ff;
    
    .message-title {
      font-weight: 600;
    }
  }
}

.message-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.message-content {
  flex: 1;
  min-width: 0;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.message-title {
  display: flex;
  align-items: center;
  gap: 8px;
  overflow: hidden;
}

.type-tag {
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 10px;
  color: #fff;
  flex-shrink: 0;
}

.title-text {
  font-size: 14px;
  color: #323233;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.message-time {
  font-size: 12px;
  color: #969799;
  flex-shrink: 0;
}

.message-body {
  font-size: 14px;
  color: #969799;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.message-sender {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #969799;
  
  .van-image {
    width: 20px;
    height: 20px;
  }
}

.message-actions {
  display: flex;
  align-items: center;
  color: #969799;
  
  .van-icon {
    padding: 4px;
  }
}
</style>
