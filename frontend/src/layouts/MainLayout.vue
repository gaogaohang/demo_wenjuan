<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { mockMessageService } from '@/services/mock'
import { showDialog } from 'vant'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeTab = ref(0)
const unreadCount = ref(2)
const showLogoutDialog = ref(false)

const tabs = [
  { name: '首页', icon: 'home-o', routeName: 'home' },
  { name: '订单', icon: 'orders-o', routeName: 'orders' },
  { name: '问卷', icon: 'notes-o', routeName: 'surveys' },
  { name: '消息', icon: 'chat-o', routeName: 'messages', badge: unreadCount },
  { name: '我的', icon: 'user-o', routeName: 'profile' }
]

const isActive = (routeName: string) => {
  return route.name === routeName
}

const handleTabChange = (index: number) => {
  const tab = tabs[index]
  router.push({ name: tab.routeName })
}

const handleLogout = () => {
  showLogoutDialog.value = true
}

const confirmLogout = async () => {
  await userStore.handleLogout()
  showLogoutDialog.value = false
}

const getUnreadCount = async () => {
  try {
    const response = await mockMessageService.getUnreadCount()
    unreadCount.value = response.data
  } catch (error) {
    console.error('获取未读消息数失败:', error)
  }
}

onMounted(() => {
  getUnreadCount()
})
</script>

<template>
  <div class="main-layout">
    <van-nav-bar
      v-if="route.meta.title"
      :title="route.meta.title as string"
      fixed
      placeholder
    />
    
    <div class="content">
      <router-view />
    </div>

    <van-tabbar v-model="activeTab" fixed placeholder @change="handleTabChange">
      <van-tabbar-item
        v-for="(tab, index) in tabs"
        :key="index"
        :name="tab.routeName"
        :icon="tab.icon"
        :badge="tab.badge"
        :dot="tab.badge && tab.badge > 0"
      >
        {{ tab.name }}
      </van-tabbar-item>
    </van-tabbar>

    <van-dialog
      v-model:show="showLogoutDialog"
      title="确认退出"
      message="确定要退出登录吗？"
      show-cancel-button
      @confirm="confirmLogout"
    />
  </div>
</template>

<style lang="scss" scoped>
.main-layout {
  min-height: 100vh;
  background-color: #f7f8fa;
}

.content {
  min-height: calc(100vh - 50px);
  padding-bottom: 50px;
}
</style>
