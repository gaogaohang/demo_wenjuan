import type { UserInfo, UserSettings } from '@/stores/user'

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

const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms))

export const mockAuthService = {
  async login(account: string, password: string) {
    await delay(500)
    
    if (account === '13800138000' && password === '123456') {
      return {
        data: {
          accessToken: 'mock-access-token-' + Date.now(),
          refreshToken: 'mock-refresh-token-' + Date.now(),
          expiresIn: 86400,
          user: mockUserInfo
        }
      }
    }
    
    throw new Error('账号或密码错误')
  },

  async register(data: { phone: string; code: string; password: string; nickname: string }) {
    await delay(500)
    
    return {
      data: {
        accessToken: 'mock-access-token-' + Date.now(),
        refreshToken: 'mock-refresh-token-' + Date.now(),
        expiresIn: 86400,
        user: {
          ...mockUserInfo,
          phone: data.phone,
          nickname: data.nickname
        }
      }
    }
  },

  async getUserProfile() {
    await delay(300)
    return { data: mockUserInfo }
  },

  async updateProfile(data: Partial<UserInfo>) {
    await delay(300)
    Object.assign(mockUserInfo, data)
    return { data: mockUserInfo }
  },

  async updateSettings(data: Partial<UserSettings>) {
    await delay(300)
    Object.assign(mockUserSettings, data)
    return { data: mockUserSettings }
  },

  async logout() {
    await delay(200)
    return { data: null }
  }
}

export default mockAuthService
