export interface Message {
  id: number
  senderId: number | null
  senderUsername: string | null
  senderNickname: string | null
  senderAvatar: string | null
  receiverId: number
  type: string
  title: string
  content: string
  data: string | null
  isRead: boolean
  readTime: string | null
  createdTime: string
}

const mockMessages: Message[] = [
  {
    id: 1,
    senderId: null,
    senderUsername: null,
    senderNickname: null,
    senderAvatar: null,
    receiverId: 1,
    type: 'system',
    title: '欢迎使用情侣互动平台',
    content: '欢迎来到情侣互动平台！在这里你们可以创建订单、发送问卷、分享生活点滴。',
    data: null,
    isRead: true,
    readTime: '2024-01-20T09:00:00',
    createdTime: '2024-01-01T10:00:00'
  },
  {
    id: 2,
    senderId: 2,
    senderUsername: 'xiaohong',
    senderNickname: '小红',
    senderAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=xiaohong',
    receiverId: 1,
    type: 'order',
    title: '订单已接受',
    content: '我已接受你的订单「周末电影票」，下午见！',
    data: JSON.stringify({ orderId: 2 }),
    isRead: false,
    readTime: null,
    createdTime: '2024-01-19T20:00:00'
  },
  {
    id: 3,
    senderId: 2,
    senderUsername: 'xiaohong',
    senderNickname: '小红',
    senderAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=xiaohong',
    receiverId: 1,
    type: 'survey',
    title: '新问卷待填写',
    content: '小明发送了问卷《2024年愿望清单》，快来填写吧！',
    data: JSON.stringify({ surveyId: 2 }),
    isRead: false,
    readTime: null,
    createdTime: '2024-01-01T00:00:00'
  },
  {
    id: 4,
    senderId: 1,
    senderUsername: 'xiaoming',
    senderNickname: '小明',
    senderAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=xiaoming',
    receiverId: 2,
    type: 'pair',
    title: '配对成功',
    content: '你们已经成为情侣！祝你们幸福美满！',
    data: null,
    isRead: true,
    readTime: '2023-12-25T20:00:00',
    createdTime: '2023-12-25T20:00:00'
  },
  {
    id: 5,
    senderId: null,
    senderUsername: null,
    senderNickname: null,
    senderAvatar: null,
    receiverId: 1,
    type: 'system',
    title: '订单已完成',
    content: '你的订单「晚餐外卖」已完成配送，感谢使用！',
    data: JSON.stringify({ orderId: 1 }),
    isRead: true,
    readTime: '2024-01-20T19:35:00',
    createdTime: '2024-01-20T19:30:00'
  }
]

const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms))

export const mockMessageService = {
  async getMessages(isRead?: boolean, type?: string) {
    await delay(400)
    let messages = [...mockMessages].sort((a, b) => 
      new Date(b.createdTime).getTime() - new Date(a.createdTime).getTime()
    )
    
    if (isRead !== undefined) {
      messages = messages.filter(m => m.isRead === isRead)
    }
    
    if (type) {
      messages = messages.filter(m => m.type === type)
    }
    
    return { data: messages }
  },

  async getMessageDetail(id: number) {
    await delay(300)
    const message = mockMessages.find(m => m.id === id)
    if (!message) throw new Error('消息不存在')
    return { data: message }
  },

  async markAsRead(id: number) {
    await delay(200)
    const message = mockMessages.find(m => m.id === id)
    if (!message) throw new Error('消息不存在')
    message.isRead = true
    message.readTime = new Date().toISOString()
    return { data: null }
  },

  async markAllAsRead() {
    await delay(300)
    mockMessages.forEach(m => {
      m.isRead = true
      m.readTime = new Date().toISOString()
    })
    return { data: null }
  },

  async deleteMessage(id: number) {
    await delay(200)
    const index = mockMessages.findIndex(m => m.id === id)
    if (index === -1) throw new Error('消息不存在')
    mockMessages.splice(index, 1)
    return { data: null }
  },

  async getUnreadCount() {
    await delay(200)
    const count = mockMessages.filter(m => !m.isRead).length
    return { data: count }
  },

  async sendMessage(data: { receiverId: number; type: string; title: string; content: string; data?: string }) {
    await delay(400)
    const newMessage: Message = {
      id: mockMessages.length + 1,
      senderId: 1,
      senderUsername: 'xiaoming',
      senderNickname: '小明',
      senderAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=xiaoming',
      receiverId: data.receiverId,
      type: data.type,
      title: data.title,
      content: data.content,
      data: data.data || null,
      isRead: false,
      readTime: null,
      createdTime: new Date().toISOString()
    }
    mockMessages.unshift(newMessage)
    return { data: newMessage.id }
  }
}

export default mockMessageService
