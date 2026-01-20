export interface Survey {
  id: number
  title: string
  description: string
  type: string
  status: string
  creatorId: number
  targetId: number
  creatorName: string
  creatorAvatar: string
  targetName: string
  targetAvatar: string
  isAnonymous: boolean
  allowMultiple: boolean
  startTime: string
  endTime: string
  responseCount: number
  createdTime: string
  questions: SurveyQuestion[]
}

export interface SurveyQuestion {
  id: number
  questionText: string
  questionType: string
  isRequired: boolean
  sortOrder: number
  options: string
  description: string
  imageUrl: string
}

export interface SurveyResponse {
  id: number
  surveyId: number
  responderId: number
  responderName: string
  responderAvatar: string
  answers: SurveyAnswer[]
  submittedAt: string
}

export interface SurveyAnswer {
  questionId: number
  questionText: string
  answer: string | string[] | number
}

const mockSurveys: Survey[] = [
  {
    id: 1,
    title: '情侣日常问答',
    description: '了解彼此的日常喜好，增进感情',
    type: 'custom',
    status: 'published',
    creatorId: 1,
    targetId: 2,
    creatorName: '小明',
    creatorAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=xiaoming',
    targetName: '小红',
    targetAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=xiaohong',
    isAnonymous: false,
    allowMultiple: false,
    startTime: '2024-01-15T00:00:00',
    endTime: '2024-02-15T00:00:00',
    responseCount: 1,
    createdTime: '2024-01-15T10:00:00',
    questions: [
      {
        id: 1,
        questionText: '你最喜欢的颜色是什么？',
        questionType: 'single_choice',
        isRequired: true,
        sortOrder: 1,
        options: JSON.stringify([
          { label: '红色', value: 'red' },
          { label: '蓝色', value: 'blue' },
          { label: '绿色', value: 'green' },
          { label: '粉色', value: 'pink' }
        ]),
        description: '请选择最喜欢的颜色',
        imageUrl: ''
      },
      {
        id: 2,
        questionText: '你喜欢的水果有哪些？',
        questionType: 'multiple_choice',
        isRequired: true,
        sortOrder: 2,
        options: JSON.stringify([
          { label: '苹果', value: 'apple' },
          { label: '香蕉', value: 'banana' },
          { label: '橙子', value: 'orange' },
          { label: '葡萄', value: 'grape' },
          { label: '西瓜', value: 'watermelon' }
        ]),
        description: '可以多选',
        imageUrl: ''
      },
      {
        id: 3,
        questionText: '请描述一下你最难忘的一次约会',
        questionType: 'text',
        isRequired: true,
        sortOrder: 3,
        options: '',
        description: '字数不少于50字',
        imageUrl: ''
      },
      {
        id: 4,
        questionText: '你对我们的关系满意度打分',
        questionType: 'rating',
        isRequired: true,
        sortOrder: 4,
        options: JSON.stringify({ min: 1, max: 5, step: 1 }),
        description: '1-5分',
        imageUrl: ''
      }
    ]
  },
  {
    id: 2,
    title: '2024年愿望清单',
    description: '一起制定新年的愿望和目标',
    type: 'custom',
    status: 'published',
    creatorId: 2,
    targetId: 1,
    creatorName: '小红',
    creatorAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=xiaohong',
    targetName: '小明',
    targetAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=xiaoming',
    isAnonymous: false,
    allowMultiple: false,
    startTime: '2024-01-01T00:00:00',
    endTime: '2024-12-31T23:59:59',
    responseCount: 0,
    createdTime: '2024-01-01T00:00:00',
    questions: [
      {
        id: 5,
        questionText: '今年最想去旅行的地方是？',
        questionType: 'single_choice',
        isRequired: true,
        sortOrder: 1,
        options: JSON.stringify([
          { label: '日本', value: 'japan' },
          { label: '泰国', value: 'thailand' },
          { label: '欧洲', value: 'europe' },
          { label: '马尔代夫', value: 'maldives' }
        ]),
        description: '',
        imageUrl: ''
      },
      {
        id: 6,
        questionText: '今年最想一起完成的事情？',
        questionType: 'text',
        isRequired: true,
        sortOrder: 2,
        options: '',
        description: '',
        imageUrl: ''
      }
    ]
  },
  {
    id: 3,
    title: '周末活动调查',
    description: '了解彼此周末的偏好',
    type: 'custom',
    status: 'closed',
    creatorId: 1,
    targetId: 2,
    creatorName: '小明',
    creatorAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=xiaoming',
    targetName: '小红',
    targetAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=xiaohong',
    isAnonymous: false,
    allowMultiple: false,
    startTime: '2024-01-10T00:00:00',
    endTime: '2024-01-17T00:00:00',
    responseCount: 2,
    createdTime: '2024-01-10T00:00:00',
    questions: []
  }
]

const mockSurveyResponses: SurveyResponse[] = [
  {
    id: 1,
    surveyId: 1,
    responderId: 2,
    responderName: '小红',
    responderAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=xiaohong',
    answers: [
      { questionId: 1, questionText: '你最喜欢的颜色是什么？', answer: 'pink' },
      { questionId: 2, questionText: '你喜欢的水果有哪些？', answer: ['apple', 'grape'] },
      { questionId: 3, questionText: '请描述一下你最难忘的一次约会', answer: '去年情人节一起去三亚，那是我最难忘的回忆。' },
      { questionId: 4, questionText: '你对我们的关系满意度打分', answer: 5 }
    ],
    submittedAt: '2024-01-16T15:30:00'
  }
]

const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms))

export const mockSurveyService = {
  async getSurveys(type: 'created' | 'target' | 'all' = 'all') {
    await delay(400)
    let surveys = [...mockSurveys]
    
    if (type === 'created') {
      surveys = surveys.filter(s => s.creatorId === 1)
    } else if (type === 'target') {
      surveys = surveys.filter(s => s.targetId === 1)
    }
    
    return { data: surveys }
  },

  async getSurveyDetail(id: number) {
    await delay(300)
    const survey = mockSurveys.find(s => s.id === id)
    if (!survey) throw new Error('问卷不存在')
    return { data: survey }
  },

  async createSurvey(data: any) {
    await delay(500)
    const newSurvey: Survey = {
      id: mockSurveys.length + 1,
      title: data.title,
      description: data.description,
      type: data.type || 'custom',
      status: 'draft',
      creatorId: 1,
      targetId: data.targetId,
      creatorName: '小明',
      creatorAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=xiaoming',
      targetName: '小红',
      targetAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=xiaohong',
      isAnonymous: data.isAnonymous || false,
      allowMultiple: data.allowMultiple || false,
      startTime: data.startTime || '',
      endTime: data.endTime || '',
      responseCount: 0,
      createdTime: new Date().toISOString(),
      questions: data.questions || []
    }
    mockSurveys.unshift(newSurvey)
    return { data: newSurvey }
  },

  async publishSurvey(id: number) {
    await delay(400)
    const survey = mockSurveys.find(s => s.id === id)
    if (!survey) throw new Error('问卷不存在')
    if (survey.status === 'published') throw new Error('问卷已发布')
    survey.status = 'published'
    return { data: survey }
  },

  async closeSurvey(id: number) {
    await delay(300)
    const survey = mockSurveys.find(s => s.id === id)
    if (!survey) throw new Error('问卷不存在')
    survey.status = 'closed'
    return { data: survey }
  },

  async deleteSurvey(id: number) {
    await delay(300)
    const index = mockSurveys.findIndex(s => s.id === id)
    if (index === -1) throw new Error('问卷不存在')
    mockSurveys.splice(index, 1)
    return { data: null }
  },

  async submitSurveyResponse(surveyId: number, answers: any[]) {
    await delay(500)
    const survey = mockSurveys.find(s => s.id === surveyId)
    if (!survey) throw new Error('问卷不存在')
    if (survey.status !== 'published') throw new Error('问卷未发布')
    if (survey.responseCount > 0 && !survey.allowMultiple) throw new Error('问卷已回复')
    
    survey.responseCount++
    
    const response: SurveyResponse = {
      id: mockSurveyResponses.length + 1,
      surveyId,
      responderId: 1,
      responderName: '小明',
      responderAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=xiaoming',
      answers,
      submittedAt: new Date().toISOString()
    }
    mockSurveyResponses.push(response)
    
    return { data: response }
  },

  async getSurveyResponses(surveyId: number) {
    await delay(400)
    const responses = mockSurveyResponses.filter(r => r.surveyId === surveyId)
    return { data: responses }
  }
}

export default mockSurveyService
