export interface Order {
  id: number
  orderNo: string
  title: string
  description: string
  type: string
  status: string
  totalAmount: number
  note: string
  images: string[]
  location: string
  estimatedTime: string
  acceptedTime: string
  completedTime: string
  createdTime: string
  creator: {
    id: number
    username: string
    nickname: string
    avatarUrl: string
  }
  receiver: {
    id: number
    username: string
    nickname: string
    avatarUrl: string
  }
  items: OrderItem[]
  evaluations: OrderEvaluation[]
}

export interface OrderItem {
  id: number
  name: string
  description: string
  quantity: number
  unitPrice: number
  totalPrice: number
  imageUrl: string
  note: string
}

export interface OrderEvaluation {
  id: number
  rating: number
  comment: string
  images: string[]
  emojis: string[]
  tags: string[]
  isAnonymous: boolean
  evaluator: {
    id: number
    username: string
    nickname: string
    avatarUrl: string
  }
  createdTime: string
}

const mockOrders: Order[] = [
  {
    id: 1,
    orderNo: 'ORD202401200001',
    title: '晚餐外卖',
    description: '晚上想吃火锅，帮我点一份',
    type: 'food',
    status: 'completed',
    totalAmount: 168.00,
    note: '不要辣',
    images: ['https://images.unsplash.com/photo-1580476262798-bddd9c4b7363?w=400'],
    location: '北京市海淀区中关村大街1号',
    estimatedTime: '2024-01-20T19:00:00',
    acceptedTime: '2024-01-20T18:00:00',
    completedTime: '2024-01-20T19:30:00',
    createdTime: '2024-01-20T17:30:00',
    creator: {
      id: 1,
      username: 'xiaoming',
      nickname: '小明',
      avatarUrl: 'https://api.dicebear.com/7.x/avataaars/svg?seed=xiaoming'
    },
    receiver: {
      id: 2,
      username: 'xiaohong',
      nickname: '小红',
      avatarUrl: 'https://api.dicebear.com/7.x/avataaars/svg?seed=xiaohong'
    },
    items: [
      {
        id: 1,
        name: '肥牛卷',
        description: '新鲜肥牛卷500g',
        quantity: 2,
        unitPrice: 45.00,
        totalPrice: 90.00,
        imageUrl: 'https://images.unsplash.com/photo-1626805599170-e4324d737847?w=200',
        note: ''
      },
      {
        id: 2,
        name: '蔬菜拼盘',
        description: '新鲜时蔬',
        quantity: 1,
        unitPrice: 28.00,
        totalPrice: 28.00,
        imageUrl: '',
        note: ''
      },
      {
        id: 3,
        name: '酸梅汤',
        description: '330ml',
        quantity: 2,
        unitPrice: 5.00,
        totalPrice: 10.00,
        imageUrl: '',
        note: ''
      }
    ],
    evaluations: []
  },
  {
    id: 2,
    orderNo: 'ORD202401190001',
    title: '周末电影票',
    description: '想看最新的科幻电影',
    type: 'other',
    status: 'accepted',
    totalAmount: 120.00,
    note: '下午场',
    images: [],
    location: '北京万达影城',
    estimatedTime: '2024-01-21T14:00:00',
    acceptedTime: '2024-01-19T20:00:00',
    completedTime: '',
    createdTime: '2024-01-19T19:00:00',
    creator: {
      id: 2,
      username: 'xiaohong',
      nickname: '小红',
      avatarUrl: 'https://api.dicebear.com/7.x/avataaars/svg?seed=xiaohong'
    },
    receiver: {
      id: 1,
      username: 'xiaoming',
      nickname: '小明',
      avatarUrl: 'https://api.dicebear.com/7.x/avataaars/svg?seed=xiaoming'
    },
    items: [
      {
        id: 4,
        name: '电影票',
        description: '《流浪地球3》IMAX场',
        quantity: 2,
        unitPrice: 60.00,
        totalPrice: 120.00,
        imageUrl: 'https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=200',
        note: ''
      }
    ],
    evaluations: []
  },
  {
    id: 3,
    orderNo: 'ORD202401180001',
    title: '纪念日礼物',
    description: '下个月是我们在一起一周年',
    type: 'shopping',
    status: 'pending',
    totalAmount: 520.00,
    note: '帮我挑个惊喜的礼物',
    images: ['https://images.unsplash.com/photo-1519720169244-8b18cc7a3c10?w=400'],
    location: '',
    estimatedTime: '2024-02-14T00:00:00',
    acceptedTime: '',
    completedTime: '',
    createdTime: '2024-01-18T10:00:00',
    creator: {
      id: 1,
      username: 'xiaoming',
      nickname: '小明',
      avatarUrl: 'https://api.dicebear.com/7.x/avataaars/svg?seed=xiaoming'
    },
    receiver: {
      id: 2,
      username: 'xiaohong',
      nickname: '小红',
      avatarUrl: 'https://api.dicebear.com/7.x/avataaars/svg?seed=xiaohong'
    },
    items: [
      {
        id: 5,
        name: '永生花',
        description: 'roseonly永生花',
        quantity: 1,
        totalPrice: 520.00,
        imageUrl: 'https://images.unsplash.com/photo-1562690868-60bbe7624e6d?w=200',
        note: ''
      }
    ],
    evaluations: []
  }
]

const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms))

export const mockOrderService = {
  async getOrders(status?: string) {
    await delay(400)
    let orders = [...mockOrders]
    
    if (status && status !== 'all') {
      orders = orders.filter(o => o.status === status)
    }
    
    return { data: orders }
  },

  async getOrderDetail(id: number) {
    await delay(300)
    const order = mockOrders.find(o => o.id === id)
    if (!order) throw new Error('订单不存在')
    return { data: order }
  },

  async createOrder(data: any) {
    await delay(500)
    const newOrder: Order = {
      id: mockOrders.length + 1,
      orderNo: `ORD${Date.now().toString().slice(-12)}`,
      title: data.title,
      description: data.description,
      type: data.type,
      status: 'pending',
      totalAmount: data.totalAmount || 0,
      note: data.note || '',
      images: data.images || [],
      location: data.location || '',
      estimatedTime: data.estimatedTime || '',
      acceptedTime: '',
      completedTime: '',
      createdTime: new Date().toISOString(),
      creator: {
        id: 1,
        username: 'xiaoming',
        nickname: '小明',
        avatarUrl: 'https://api.dicebear.com/7.x/avataaars/svg?seed=xiaoming'
      },
      receiver: {
        id: 2,
        username: 'xiaohong',
        nickname: '小红',
        avatarUrl: 'https://api.dicebear.com/7.x/avataaars/svg?seed=xiaohong'
      },
      items: data.items || [],
      evaluations: []
    }
    mockOrders.unshift(newOrder)
    return { data: newOrder }
  },

  async updateOrderStatus(id: number, status: string) {
    await delay(400)
    const order = mockOrders.find(o => o.id === id)
    if (!order) throw new Error('订单不存在')
    order.status = status
    if (status === 'accepted') {
      order.acceptedTime = new Date().toISOString()
    } else if (status === 'completed') {
      order.completedTime = new Date().toISOString()
    }
    return { data: order }
  },

  async cancelOrder(id: number) {
    await delay(300)
    const order = mockOrders.find(o => o.id === id)
    if (!order) throw new Error('订单不存在')
    order.status = 'cancelled'
    return { data: order }
  },

  async evaluateOrder(id: number, data: any) {
    await delay(500)
    const order = mockOrders.find(o => o.id === id)
    if (!order) throw new Error('订单不存在')
    const evaluation: OrderEvaluation = {
      id: Date.now(),
      rating: data.rating,
      comment: data.comment,
      images: data.images || [],
      emojis: data.emojis || [],
      tags: data.tags || [],
      isAnonymous: data.isAnonymous || false,
      evaluator: {
        id: 1,
        username: 'xiaoming',
        nickname: '小明',
        avatarUrl: 'https://api.dicebear.com/7.x/avataaars/svg?seed=xiaoming'
      },
      createdTime: new Date().toISOString()
    }
    order.evaluations = [evaluation]
    return { data: order }
  }
}

export default mockOrderService
