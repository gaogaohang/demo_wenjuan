import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { mockAuthService, mockMessageService } from '@/services/mock'
import type { UserInfo, UserSettings, PairInfo } from '@/stores/user'
import router from '@/router'

const mockUserInfo: UserInfo = {
  id: 1,
  username: 'xiaoming',
  nickname: '小明',
  phone: '13800138000',
  avatarUrl: 'https://api.dicebear.com/7.x/avataaars/svg?seed=xiaoming',
  gender: 'male',
  bio: '热爱生活的程序员',
  birthday: '1995-06-15',
  coupleId: 2,
  createdTime: '2024-01-01T10:00:00'
}

const mockUserSettings: UserSettings = {
  id: 1,
  userId: 1,
  theme: 'light',
  language: 'zh-CN',
  notifications: true,
  privacyMode: false
}

const mockPairInfo: PairInfo = {
  id: 1,
  partnerId: 2,
  partnerNickname: '小红',
  partnerAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=xiaohong',
  pairCode: 'COUPLE2024',
  status: 'active',
  createdTime: '2023-12-25T20:00:00'
}

export const useUserStore = defineStore('user', () => {
  const token = ref<string | null>(localStorage.getItem('token') || 'mock-token')
  const userInfo = ref<UserInfo | null>(mockUserInfo)
  const userSettings = ref<UserSettings | null>(mockUserSettings)
  const pairInfo = ref<PairInfo | null>(mockPairInfo)
  const loading = ref(false)

  const isLoggedIn = computed(() => !!token.value)

  const setToken = (newToken: string) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  const clearToken = () => {
    token.value = null
    localStorage.removeItem('token')
  }

  const setUserInfo = (info: UserInfo) => {
    userInfo.value = info
  }

  const setUserSettings = (settings: UserSettings) => {
    userSettings.value = settings
  }

  const setPairInfo = (info: PairInfo) => {
    pairInfo.value = info
  }

  const handleLogin = async (account: string, password: string) => {
    loading.value = true
    try {
      const response = await mockAuthService.login(account, password)
      setToken(response.data.accessToken)
      setUserInfo(response.data.user)
      return response.data
    } finally {
      loading.value = false
    }
  }

  const handleRegister = async (data: { phone: string; code: string; password: string; nickname: string }) => {
    loading.value = true
    try {
      const response = await mockAuthService.register(data)
      setToken(response.data.accessToken)
      setUserInfo(response.data.user)
      return response.data
    } finally {
      loading.value = false
    }
  }

  const handleLogout = async () => {
    loading.value = true
    try {
      await mockAuthService.logout()
    } finally {
      clearToken()
      userInfo.value = null
      userSettings.value = null
      pairInfo.value = null
      loading.value = false
      router.push('/login')
    }
  }

  const fetchUserProfile = async () => {
    if (!token.value) return
    
    try {
      const response = await mockAuthService.getUserProfile()
      setUserInfo(response.data)
    } catch (error) {
      console.error('获取用户信息失败:', error)
    }
  }

  const updateUserProfile = async (data: Partial<UserInfo>) => {
    loading.value = true
    try {
      const response = await mockAuthService.updateProfile(data)
      setUserInfo(response.data)
      return response.data
    } finally {
      loading.value = false
    }
  }

  const updateUserSettings = async (data: Partial<UserSettings>) => {
    loading.value = true
    try {
      const response = await mockAuthService.updateSettings(data)
      setUserSettings(response.data)
      return response.data
    } finally {
      loading.value = false
    }
  }

  const fetchPairInfo = async () => {
    try {
      setPairInfo(mockPairInfo)
      return mockPairInfo
    } catch (error) {
      console.error('获取配对信息失败:', error)
    }
  }

  const createPair = async (partnerPhone: string) => {
    loading.value = true
    try {
      await new Promise(resolve => setTimeout(resolve, 500))
      return { success: true, message: '配对请求已发送' }
    } finally {
      loading.value = false
    }
  }

  const acceptPair = async (requestId: number) => {
    loading.value = true
    try {
      await new Promise(resolve => setTimeout(resolve, 500))
      setPairInfo(mockPairInfo)
      return { success: true }
    } finally {
      loading.value = false
    }
  }

  return {
    token,
    userInfo,
    userSettings,
    pairInfo,
    loading,
    isLoggedIn,
    setToken,
    clearToken,
    setUserInfo,
    setUserSettings,
    setPairInfo,
    handleLogin,
    handleRegister,
    handleLogout,
    fetchUserProfile,
    updateUserProfile,
    updateUserSettings,
    fetchPairInfo,
    createPair,
    acceptPair
  }
})
