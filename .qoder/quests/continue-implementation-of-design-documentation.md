# 情侣互动在线点单与问卷平台设计文档 - 继续实现

**状态**: 🚀 **准备开始实施** - 设计文档已完成，可以开始开发任务

**最后更新**: 2024年12月19日

## 继续完善架构实现

**📋 开发就绪状态检查**:
- ✅ 技术栈选型完成
- ✅ 数据库设计完成
- ✅ API接口设计完成
- ✅ 前端组件设计完成
- ✅ 后端服务架构完成
- ✅ 部署配置完成
- ✅ 测试策略制定完成

**🚀 可以开始的开发任务**:
1. 后端基础架构实现
2. 数据库表结构创建
3. 用户认证模块开发
4. 核心业务逻辑实现
5. 前端页面开发
6. API集成测试
7. 部署和上线准备

### 8.2.4 前端Vue组件实现

#### 8.2.4.1 订单模块组件

**OrderCreate.vue - 订单创建组件**
```vue
<template>
  <div class="order-create">
    <van-nav-bar title="创建订单" left-arrow @click-left="$router.back()" />
    
    <van-form @submit="submitOrder">
      <van-field
        v-model="orderForm.title"
        name="title"
        label="订单标题"
        placeholder="请输入订单标题"
        :rules="[{ required: true, message: '请填写订单标题' }]"
      />
      
      <van-field
        v-model="orderForm.description"
        name="description"
        label="订单描述"
        type="textarea"
        placeholder="详细描述你想要的内容..."
        rows="3"
      />
      
      <van-field name="orderType" label="订单类型">
        <template #input>
          <van-radio-group v-model="orderForm.orderType" direction="horizontal">
            <van-radio name="FOOD">美食</van-radio>
            <van-radio name="DRINK">饮品</van-radio>
            <van-radio name="SNACK">零食</van-radio>
            <van-radio name="OTHER">其他</van-radio>
          </van-radio-group>
        </template>
      </van-field>
      
      <van-field name="priority" label="优先级">
        <template #input>
          <van-rate v-model="orderForm.priority" :count="3" />
        </template>
      </van-field>
      
      <van-field name="dueTime" label="期望完成时间">
        <template #input>
          <van-datetime-picker
            v-model="orderForm.dueTime"
            type="datetime"
            title="选择时间"
          />
        </template>
      </van-field>
      
      <div class="order-items">
        <van-cell title="订单项目" />
        <div v-for="(item, index) in orderForm.items" :key="index" class="item-card">
          <van-field
            v-model="item.itemName"
            placeholder="项目名称"
            :rules="[{ required: true, message: '请填写项目名称' }]"
          />
          <van-stepper v-model="item.quantity" min="1" />
          <van-field
            v-model="item.note"
            placeholder="备注（可选）"
            type="textarea"
            rows="2"
          />
          <van-button
            v-if="orderForm.items.length > 1"
            size="small"
            type="danger"
            @click="removeItem(index)"
          >
            删除
          </van-button>
        </div>
        
        <van-button
          block
          type="primary"
          plain
          @click="addItem"
          class="add-item-btn"
        >
          + 添加项目
        </van-button>
      </div>
      
      <div class="submit-section">
        <van-button
          round
          block
          type="primary"
          native-type="submit"
          :loading="submitting"
        >
          创建订单
        </van-button>
      </div>
    </van-form>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showConfirmDialog } from 'vant'
import { useOrderStore } from '@/stores/order'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const orderStore = useOrderStore()
const userStore = useUserStore()

const submitting = ref(false)

const orderForm = reactive({
  title: '',
  description: '',
  orderType: 'FOOD',
  priority: 2,
  dueTime: new Date(),
  items: [
    {
      itemName: '',
      quantity: 1,
      note: ''
    }
  ]
})

const addItem = () => {
  orderForm.items.push({
    itemName: '',
    quantity: 1,
    note: ''
  })
}

const removeItem = (index) => {
  orderForm.items.splice(index, 1)
}

const submitOrder = async () => {
  if (!userStore.user.partnerId) {
    showToast('请先完成情侣配对')
    return
  }
  
  try {
    submitting.value = true
    
    const orderData = {
      ...orderForm,
      assigneeId: userStore.user.partnerId,
      priority: ['LOW', 'MEDIUM', 'HIGH'][orderForm.priority - 1]
    }
    
    await orderStore.createOrder(orderData)
    
    showToast('订单创建成功')
    router.push('/orders')
  } catch (error) {
    showToast(`创建失败: ${error.message}`)
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.order-create {
  padding-bottom: 20px;
}

.item-card {
  background: #f8f9fa;
  margin: 10px 16px;
  padding: 16px;
  border-radius: 8px;
}

.add-item-btn {
  margin: 16px;
}

.submit-section {
  padding: 20px 16px;
}
</style>
```

**SurveyCreate.vue - 问卷创建组件**
```vue
<template>
  <div class="survey-create">
    <van-nav-bar title="创建问卷" left-arrow @click-left="handleBack" />
    
    <van-form @submit="submitSurvey">
      <van-field
        v-model="surveyForm.title"
        name="title"
        label="问卷标题"
        placeholder="请输入问卷标题"
        :rules="[{ required: true, message: '请填写问卷标题' }]"
      />
      
      <van-field
        v-model="surveyForm.description"
        name="description"
        label="问卷描述"
        type="textarea"
        placeholder="描述问卷的目的和内容..."
        rows="3"
      />
      
      <van-cell-group title="问卷设置">
        <van-field name="anonymous" label="匿名问卷">
          <template #input>
            <van-switch v-model="surveyForm.anonymous" />
          </template>
        </van-field>
        
        <van-field name="multipleSubmit" label="允许多次提交">
          <template #input>
            <van-switch v-model="surveyForm.multipleSubmit" />
          </template>
        </van-field>
      </van-cell-group>
      
      <div class="questions-section">
        <van-cell title="问题列表" />
        
        <div v-for="(question, index) in surveyForm.questions" :key="index" class="question-card">
          <div class="question-header">
            <span class="question-number">Q{{ index + 1 }}</span>
            <van-button
              size="small"
              type="danger"
              plain
              @click="removeQuestion(index)"
              v-if="surveyForm.questions.length > 1"
            >
              删除
            </van-button>
          </div>
          
          <van-field
            v-model="question.title"
            placeholder="请输入问题内容"
            :rules="[{ required: true, message: '请填写问题内容' }]"
          />
          
          <van-field name="questionType" label="问题类型">
            <template #input>
              <van-radio-group v-model="question.questionType">
                <van-radio name="SINGLE_CHOICE">单选题</van-radio>
                <van-radio name="MULTIPLE_CHOICE">多选题</van-radio>
                <van-radio name="TEXT_INPUT">填空题</van-radio>
                <van-radio name="RATING">评分题</van-radio>
              </van-radio-group>
            </template>
          </van-field>
          
          <!-- 选择题选项 -->
          <div v-if="question.questionType === 'SINGLE_CHOICE' || question.questionType === 'MULTIPLE_CHOICE'">
            <van-field label="选项设置" />
            <div v-for="(option, optIndex) in question.options" :key="optIndex" class="option-input">
              <van-field
                v-model="option.text"
                :placeholder="`选项 ${String.fromCharCode(65 + optIndex)}`"
              />
              <van-button
                size="small"
                type="danger"
                plain
                @click="removeOption(index, optIndex)"
                v-if="question.options.length > 2"
              >
                删除
              </van-button>
            </div>
            <van-button
              size="small"
              type="primary"
              plain
              @click="addOption(index)"
            >
              + 添加选项
            </van-button>
          </div>
          
          <!-- 评分题设置 -->
          <div v-if="question.questionType === 'RATING'">
            <van-field
              v-model.number="question.settings.maxRating"
              label="最高分数"
              type="number"
            />
          </div>
          
          <van-field name="required" label="必填">
            <template #input>
              <van-switch v-model="question.required" />
            </template>
          </van-field>
        </div>
        
        <van-button
          block
          type="primary"
          plain
          @click="addQuestion"
          class="add-question-btn"
        >
          + 添加问题
        </van-button>
      </div>
      
      <div class="submit-section">
        <van-button
          round
          block
          type="primary"
          native-type="submit"
          :loading="submitting"
        >
          创建问卷
        </van-button>
      </div>
    </van-form>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showConfirmDialog } from 'vant'
import { useSurveyStore } from '@/stores/survey'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const surveyStore = useSurveyStore()
const userStore = useUserStore()

const submitting = ref(false)

const surveyForm = reactive({
  title: '',
  description: '',
  anonymous: false,
  multipleSubmit: false,
  questions: [
    {
      title: '',
      questionType: 'SINGLE_CHOICE',
      required: true,
      options: [
        { text: '' },
        { text: '' }
      ],
      settings: {
        maxRating: 5
      }
    }
  ]
})

const addQuestion = () => {
  surveyForm.questions.push({
    title: '',
    questionType: 'SINGLE_CHOICE',
    required: true,
    options: [
      { text: '' },
      { text: '' }
    ],
    settings: {
      maxRating: 5
    }
  })
}

const removeQuestion = (index) => {
  surveyForm.questions.splice(index, 1)
}

const addOption = (questionIndex) => {
  surveyForm.questions[questionIndex].options.push({ text: '' })
}

const removeOption = (questionIndex, optionIndex) => {
  surveyForm.questions[questionIndex].options.splice(optionIndex, 1)
}

const handleBack = async () => {
  const hasContent = surveyForm.title || surveyForm.questions.some(q => q.title)
  
  if (hasContent) {
    try {
      await showConfirmDialog({
        title: '确认退出',
        message: '当前内容未保存，确定要退出吗？'
      })
      router.back()
    } catch (error) {
      // 用户取消
    }
  } else {
    router.back()
  }
}

const submitSurvey = async () => {
  if (!userStore.user.partnerId) {
    showToast('请先完成情侣配对')
    return
  }
  
  try {
    submitting.value = true
    
    const surveyData = {
      ...surveyForm,
      questions: surveyForm.questions.map((q, index) => ({
        ...q,
        orderIndex: index,
        options: ['SINGLE_CHOICE', 'MULTIPLE_CHOICE'].includes(q.questionType) 
          ? q.options.filter(opt => opt.text.trim()) 
          : null
      }))
    }
    
    await surveyStore.createSurvey(surveyData)
    
    showToast('问卷创建成功')
    router.push('/surveys')
  } catch (error) {
    showToast(`创建失败: ${error.message}`)
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.survey-create {
  padding-bottom: 20px;
}

.question-card {
  background: #f8f9fa;
  margin: 10px 16px;
  padding: 16px;
  border-radius: 8px;
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.question-number {
  font-weight: bold;
  color: #007AFF;
}

.option-input {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.add-question-btn {
  margin: 16px;
}

.submit-section {
  padding: 20px 16px;
}
</style>
```

#### 8.2.4.2 状态管理Store

**order.js - 订单状态管理**
```javascript
import { defineStore } from 'pinia'
import { orderApi } from '@/api/order'

export const useOrderStore = defineStore('order', {
  state: () => ({
    orders: [],
    currentOrder: null,
    loading: false,
    orderStats: {
      total: 0,
      pending: 0,
      completed: 0,
      cancelled: 0
    }
  }),
  
  getters: {
    pendingOrders: (state) => state.orders.filter(order => order.status === 'PENDING'),
    completedOrders: (state) => state.orders.filter(order => order.status === 'COMPLETED'),
    myOrders: (state) => (userId) => state.orders.filter(order => order.creatorId === userId),
    assignedOrders: (state) => (userId) => state.orders.filter(order => order.assigneeId === userId)
  },
  
  actions: {
    async fetchOrders() {
      try {
        this.loading = true
        const response = await orderApi.getOrders()
        this.orders = response.data
        this.updateOrderStats()
      } catch (error) {
        console.error('获取订单列表失败:', error)
        throw error
      } finally {
        this.loading = false
      }
    },
    
    async createOrder(orderData) {
      try {
        const response = await orderApi.createOrder(orderData)
        this.orders.unshift(response.data)
        this.updateOrderStats()
        return response.data
      } catch (error) {
        console.error('创建订单失败:', error)
        throw error
      }
    },
    
    async updateOrderStatus(orderId, status) {
      try {
        await orderApi.updateOrderStatus(orderId, status)
        const order = this.orders.find(o => o.id === orderId)
        if (order) {
          order.status = status
          if (status === 'COMPLETED') {
            order.completedAt = new Date().toISOString()
          }
        }
        this.updateOrderStats()
      } catch (error) {
        console.error('更新订单状态失败:', error)
        throw error
      }
    },
    
    async evaluateOrder(orderId, evaluation) {
      try {
        const response = await orderApi.evaluateOrder(orderId, evaluation)
        const order = this.orders.find(o => o.id === orderId)
        if (order) {
          order.evaluation = response.data
        }
        return response.data
      } catch (error) {
        console.error('评价订单失败:', error)
        throw error
      }
    },
    
    async getOrderDetail(orderId) {
      try {
        this.loading = true
        const response = await orderApi.getOrderDetail(orderId)
        this.currentOrder = response.data
        return response.data
      } catch (error) {
        console.error('获取订单详情失败:', error)
        throw error
      } finally {
        this.loading = false
      }
    },
    
    updateOrderStats() {
      this.orderStats = {
        total: this.orders.length,
        pending: this.orders.filter(o => o.status === 'PENDING').length,
        completed: this.orders.filter(o => o.status === 'COMPLETED').length,
        cancelled: this.orders.filter(o => o.status === 'CANCELLED').length
      }
    },
    
    // WebSocket 实时更新
    onOrderStatusUpdate(data) {
      const order = this.orders.find(o => o.id === data.orderId)
      if (order) {
        order.status = data.status
        order.updatedAt = data.updatedAt
      }
      this.updateOrderStats()
    },
    
    onNewOrder(orderData) {
      this.orders.unshift(orderData)
      this.updateOrderStats()
    }
  }
})
```

**survey.js - 问卷状态管理**
```javascript
import { defineStore } from 'pinia'
import { surveyApi } from '@/api/survey'

export const useSurveyStore = defineStore('survey', {
  state: () => ({
    surveys: [],
    currentSurvey: null,
    currentSubmission: null,
    loading: false,
    submissionProgress: 0,
    surveyStats: {
      total: 0,
      published: 0,
      draft: 0,
      responses: 0
    }
  }),
  
  getters: {
    publishedSurveys: (state) => state.surveys.filter(survey => survey.status === 'PUBLISHED'),
    draftSurveys: (state) => state.surveys.filter(survey => survey.status === 'DRAFT'),
    mySurveys: (state) => (userId) => state.surveys.filter(survey => survey.creatorId === userId),
    availableSurveys: (state) => (userId) => state.surveys.filter(survey => 
      survey.status === 'PUBLISHED' && 
      survey.creatorId !== userId && 
      !survey.submissions?.some(s => s.respondentId === userId)
    )
  },
  
  actions: {
    async fetchSurveys() {
      try {
        this.loading = true
        const response = await surveyApi.getSurveys()
        this.surveys = response.data
        this.updateSurveyStats()
      } catch (error) {
        console.error('获取问卷列表失败:', error)
        throw error
      } finally {
        this.loading = false
      }
    },
    
    async createSurvey(surveyData) {
      try {
        const response = await surveyApi.createSurvey(surveyData)
        this.surveys.unshift(response.data)
        this.updateSurveyStats()
        return response.data
      } catch (error) {
        console.error('创建问卷失败:', error)
        throw error
      }
    },
    
    async getSurveyDetail(surveyId) {
      try {
        this.loading = true
        const response = await surveyApi.getSurveyDetail(surveyId)
        this.currentSurvey = response.data
        return response.data
      } catch (error) {
        console.error('获取问卷详情失败:', error)
        throw error
      } finally {
        this.loading = false
      }
    },
    
    async submitSurveyResponse(surveyId, responses) {
      try {
        const response = await surveyApi.submitSurveyResponse(surveyId, responses)
        
        // 更新本地数据
        const survey = this.surveys.find(s => s.id === surveyId)
        if (survey) {
          survey.submissions = survey.submissions || []
          survey.submissions.push(response.data)
        }
        
        this.updateSurveyStats()
        return response.data
      } catch (error) {
        console.error('提交问卷失败:', error)
        throw error
      }
    },
    
    async getSurveyAnalysis(surveyId) {
      try {
        const response = await surveyApi.getSurveyAnalysis(surveyId)
        return response.data
      } catch (error) {
        console.error('获取问卷分析失败:', error)
        throw error
      }
    },
    
    // 问卷填写进度管理
    startSubmission(surveyId) {
      this.currentSubmission = {
        surveyId,
        responses: {},
        startedAt: new Date().toISOString()
      }
      this.submissionProgress = 0
    },
    
    updateResponse(questionId, answer) {
      if (this.currentSubmission) {
        this.currentSubmission.responses[questionId] = answer
        this.calculateProgress()
      }
    },
    
    calculateProgress() {
      if (!this.currentSurvey || !this.currentSubmission) return
      
      const totalQuestions = this.currentSurvey.questions.length
      const answeredQuestions = Object.keys(this.currentSubmission.responses).length
      this.submissionProgress = Math.round((answeredQuestions / totalQuestions) * 100)
    },
    
    clearSubmission() {
      this.currentSubmission = null
      this.submissionProgress = 0
    },
    
    updateSurveyStats() {
      this.surveyStats = {
        total: this.surveys.length,
        published: this.surveys.filter(s => s.status === 'PUBLISHED').length,
        draft: this.surveys.filter(s => s.status === 'DRAFT').length,
        responses: this.surveys.reduce((sum, s) => sum + (s.submissions?.length || 0), 0)
      }
    },
    
    // WebSocket 实时更新
    onNewSurvey(surveyData) {
      this.surveys.unshift(surveyData)
      this.updateSurveyStats()
    },
    
    onSurveyResponse(data) {
      const survey = this.surveys.find(s => s.id === data.surveyId)
      if (survey) {
        survey.submissions = survey.submissions || []
        survey.submissions.push(data)
      }
      this.updateSurveyStats()
    }
  }
})
```

#### 8.2.4.3 API接口实现

**api/order.js - 订单API**
```javascript
import request from '@/utils/request'

export const orderApi = {
  // 获取订单列表
  getOrders(params = {}) {
    return request({
      url: '/orders',
      method: 'get',
      params
    })
  },
  
  // 创建订单
  createOrder(data) {
    return request({
      url: '/orders',
      method: 'post',
      data
    })
  },
  
  // 获取订单详情
  getOrderDetail(id) {
    return request({
      url: `/orders/${id}`,
      method: 'get'
    })
  },
  
  // 更新订单状态
  updateOrderStatus(id, status) {
    return request({
      url: `/orders/${id}/status`,
      method: 'put',
      data: { status }
    })
  },
  
  // 评价订单
  evaluateOrder(id, evaluation) {
    return request({
      url: `/orders/${id}/evaluate`,
      method: 'post',
      data: evaluation
    })
  }
}

export default orderApi
```

**api/survey.js - 问卷API**
```javascript
import request from '@/utils/request'

export const surveyApi = {
  // 获取问卷列表
  getSurveys(params = {}) {
    return request({
      url: '/surveys',
      method: 'get',
      params
    })
  },
  
  // 创建问卷
  createSurvey(data) {
    return request({
      url: '/surveys',
      method: 'post',
      data
    })
  },
  
  // 获取问卷详情
  getSurveyDetail(id) {
    return request({
      url: `/surveys/${id}`,
      method: 'get'
    })
  },
  
  // 提交问卷回答
  submitSurveyResponse(id, responses) {
    return request({
      url: `/surveys/${id}/submit`,
      method: 'post',
      data: { responses }
    })
  },
  
  // 获取问卷分析数据
  getSurveyAnalysis(id) {
    return request({
      url: `/surveys/${id}/analysis`,
      method: 'get'
    })
  }
}

export default surveyApi
```

### 8.3 后端核心业务逻辑实现

#### 8.3.1 订单服务实现

**OrderService.java**
```java
package com.couple.platform.service;

import com.couple.platform.entity.Order;
import com.couple.platform.entity.OrderItem;
import com.couple.platform.entity.OrderEvaluation;
import com.couple.platform.entity.User;
import com.couple.platform.repository.OrderRepository;
import com.couple.platform.repository.OrderItemRepository;
import com.couple.platform.repository.OrderEvaluationRepository;
import com.couple.platform.repository.UserRepository;
import com.couple.platform.dto.request.OrderCreateRequest;
import com.couple.platform.dto.request.OrderEvaluationRequest;
import com.couple.platform.dto.response.OrderResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderEvaluationRepository orderEvaluationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final PushNotificationService pushNotificationService;
    
    @Transactional
    public OrderResponse createOrder(Long creatorId, OrderCreateRequest request) {
        // 1. 验证用户是否已配对
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        
        if (creator.getPartnerId() == null) {
            throw new IllegalArgumentException("请先完成情侣配对");
        }
        
        // 2. 创建订单
        Order order = new Order();
        order.setCreatorId(creatorId);
        order.setAssigneeId(creator.getPartnerId());
        order.setTitle(request.getTitle());
        order.setDescription(request.getDescription());
        order.setOrderType(Order.OrderType.valueOf(request.getOrderType()));
        order.setPriority(Order.Priority.valueOf(request.getPriority()));
        order.setStatus(Order.Status.PENDING);
        order.setDueTime(request.getDueTime());
        
        order = orderRepository.save(order);
        
        // 3. 创建订单项
        List<OrderItem> items = request.getItems().stream()
                .map(itemRequest -> {
                    OrderItem item = new OrderItem();
                    item.setOrderId(order.getId());
                    item.setItemName(itemRequest.getItemName());
                    item.setQuantity(itemRequest.getQuantity());
                    item.setNote(itemRequest.getNote());
                    item.setPrice(itemRequest.getPrice());
                    return item;
                })
                .collect(Collectors.toList());
        
        orderItemRepository.saveAll(items);
        order.setItems(items);
        
        // 4. 发送实时通知
        sendOrderNotification(order, "ORDER_CREATED");
        
        // 5. 发送推送通知
        pushNotificationService.sendNotification(
                creator.getPartnerId(),
                createOrderNotificationData(order, "新订单通知", "你有一个新的订单需要处理")
        );
        
        return convertToOrderResponse(order);
    }
    
    @Transactional
    public void updateOrderStatus(Long orderId, Order.Status status, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        
        // 验证权限：只有创建者或接收者可以更新状态
        if (!order.getCreatorId().equals(userId) && !order.getAssigneeId().equals(userId)) {
            throw new IllegalArgumentException("没有权限操作此订单");
        }
        
        Order.Status oldStatus = order.getStatus();
        order.setStatus(status);
        
        if (status == Order.Status.COMPLETED) {
            order.setCompletedAt(LocalDateTime.now());
        }
        
        orderRepository.save(order);
        
        // 发送状态更新通知
        sendOrderStatusUpdate(order, oldStatus);
        
        // 推送通知
        Long notifyUserId = order.getCreatorId().equals(userId) ? order.getAssigneeId() : order.getCreatorId();
        pushNotificationService.sendNotification(
                notifyUserId,
                createOrderNotificationData(order, "订单状态更新", getStatusMessage(status))
        );
        
        log.info("订单状态更新: orderId={}, oldStatus={}, newStatus={}, userId={}", 
                orderId, oldStatus, status, userId);
    }
    
    @Transactional
    public OrderEvaluation evaluateOrder(Long orderId, OrderEvaluationRequest request, Long evaluatorId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        
        // 验证订单是否已完成
        if (order.getStatus() != Order.Status.COMPLETED) {
            throw new IllegalArgumentException("只能评价已完成的订单");
        }
        
        // 验证是否已评价
        if (orderEvaluationRepository.existsByOrderId(orderId)) {
            throw new IllegalArgumentException("订单已被评价");
        }
        
        // 验证评价者权限（创建者可以评价）
        if (!order.getCreatorId().equals(evaluatorId)) {
            throw new IllegalArgumentException("只有订单创建者可以进行评价");
        }
        
        // 创建评价
        OrderEvaluation evaluation = new OrderEvaluation();
        evaluation.setOrderId(orderId);
        evaluation.setEvaluatorId(evaluatorId);
        evaluation.setRating(request.getRating());
        evaluation.setComment(request.getComment());
        evaluation.setMediaFiles(request.getMediaFiles());
        
        evaluation = orderEvaluationRepository.save(evaluation);
        
        // 发送评价通知
        sendOrderEvaluationNotification(order, evaluation);
        
        // 推送通知
        pushNotificationService.sendNotification(
                order.getAssigneeId(),
                createOrderNotificationData(order, "订单评价", "你的订单收到了评价")
        );
        
        return evaluation;
    }
    
    public List<OrderResponse> getOrdersByUser(Long userId) {
        List<Order> orders = orderRepository.findByCreatorIdOrAssigneeIdOrderByCreatedAtDesc(userId, userId);
        return orders.stream()
                .map(this::convertToOrderResponse)
                .collect(Collectors.toList());
    }
    
    public OrderResponse getOrderDetail(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        
        // 验证权限
        if (!order.getCreatorId().equals(userId) && !order.getAssigneeId().equals(userId)) {
            throw new IllegalArgumentException("没有权限查看此订单");
        }
        
        return convertToOrderResponse(order);
    }
    
    private void sendOrderNotification(Order order, String type) {
        try {
            // 发送WebSocket通知给接收者
            messagingTemplate.convertAndSendToUser(
                    order.getAssigneeId().toString(),
                    "/queue/orders",
                    Map.of(
                            "type", type,
                            "order", convertToOrderResponse(order),
                            "timestamp", LocalDateTime.now()
                    )
            );
        } catch (Exception e) {
            log.error("发送订单通知失败", e);
        }
    }
    
    private void sendOrderStatusUpdate(Order order, Order.Status oldStatus) {
        try {
            // 通知给创建者和接收者
            List<Long> userIds = List.of(order.getCreatorId(), order.getAssigneeId());
            
            for (Long userId : userIds) {
                messagingTemplate.convertAndSendToUser(
                        userId.toString(),
                        "/queue/orders/status",
                        Map.of(
                                "type", "ORDER_STATUS_CHANGED",
                                "orderId", order.getId(),
                                "oldStatus", oldStatus,
                                "newStatus", order.getStatus(),
                                "timestamp", LocalDateTime.now()
                        )
                );
            }
        } catch (Exception e) {
            log.error("发送订单状态更新通知失败", e);
        }
    }
    
    private void sendOrderEvaluationNotification(Order order, OrderEvaluation evaluation) {
        try {
            messagingTemplate.convertAndSendToUser(
                    order.getAssigneeId().toString(),
                    "/queue/orders/evaluation",
                    Map.of(
                            "type", "ORDER_EVALUATED",
                            "orderId", order.getId(),
                            "evaluation", evaluation,
                            "timestamp", LocalDateTime.now()
                    )
            );
        } catch (Exception e) {
            log.error("发送订单评价通知失败", e);
        }
    }
    
    private NotificationData createOrderNotificationData(Order order, String title, String body) {
        return NotificationData.builder()
                .title(title)
                .body(body)
                .data(Map.of(
                        "type", "order",
                        "orderId", order.getId().toString(),
                        "url", "/orders/" + order.getId()
                ))
                .tag("order_" + order.getId())
                .build();
    }
    
    private String getStatusMessage(Order.Status status) {
        return switch (status) {
            case ACCEPTED -> "订单已被接受";
            case IN_PROGRESS -> "订单处理中";
            case COMPLETED -> "订单已完成";
            case CANCELLED -> "订单已取消";
            default -> "订单状态已更新";
        };
    }
    
    private OrderResponse convertToOrderResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .creatorId(order.getCreatorId())
                .assigneeId(order.getAssigneeId())
                .title(order.getTitle())
                .description(order.getDescription())
                .orderType(order.getOrderType().name())
                .status(order.getStatus().name())
                .priority(order.getPriority().name())
                .dueTime(order.getDueTime())
                .completedAt(order.getCompletedAt())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .items(order.getItems())
                .evaluation(order.getEvaluation())
                .build();
    }
}
```

#### 8.3.2 问卷服务实现

**SurveyService.java**
```java
package com.couple.platform.service;

import com.couple.platform.entity.*;
import com.couple.platform.repository.*;
import com.couple.platform.dto.request.SurveyCreateRequest;
import com.couple.platform.dto.request.SurveySubmissionRequest;
import com.couple.platform.dto.response.SurveyResponse;
import com.couple.platform.dto.response.SurveyAnalysisResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SurveyService {
    
    private final SurveyRepository surveyRepository;
    private final SurveyQuestionRepository surveyQuestionRepository;
    private final SurveySubmissionRepository surveySubmissionRepository;
    private final SurveyResponseRepository surveyResponseRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final PushNotificationService pushNotificationService;
    
    @Transactional
    public SurveyResponse createSurvey(Long creatorId, SurveyCreateRequest request) {
        // 1. 验证用户是否已配对
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        
        if (creator.getPartnerId() == null) {
            throw new IllegalArgumentException("请先完成情侣配对");
        }
        
        // 2. 创建问卷
        Survey survey = new Survey();
        survey.setCreatorId(creatorId);
        survey.setTitle(request.getTitle());
        survey.setDescription(request.getDescription());
        survey.setAnonymous(request.getAnonymous());
        survey.setMultipleSubmit(request.getMultipleSubmit());
        survey.setStartTime(request.getStartTime());
        survey.setEndTime(request.getEndTime());
        survey.setStatus(Survey.Status.DRAFT);
        
        survey = surveyRepository.save(survey);
        
        // 3. 创建问题
        List<SurveyQuestion> questions = new ArrayList<>();
        for (int i = 0; i < request.getQuestions().size(); i++) {
            var questionRequest = request.getQuestions().get(i);
            
            SurveyQuestion question = new SurveyQuestion();
            question.setSurveyId(survey.getId());
            question.setQuestionType(SurveyQuestion.QuestionType.valueOf(questionRequest.getQuestionType()));
            question.setTitle(questionRequest.getTitle());
            question.setDescription(questionRequest.getDescription());
            question.setRequired(questionRequest.getRequired());
            question.setOrderIndex(i);
            question.setOptions(questionRequest.getOptions());
            question.setValidation(questionRequest.getValidation());
            question.setSettings(questionRequest.getSettings());
            
            questions.add(question);
        }
        
        surveyQuestionRepository.saveAll(questions);
        survey.setQuestions(questions);
        
        // 4. 如果设置为立即发布，则发布问卷
        if (request.getPublishImmediately()) {
            publishSurvey(survey.getId(), creatorId);
        }
        
        return convertToSurveyResponse(survey);
    }
    
    @Transactional
    public void publishSurvey(Long surveyId, Long creatorId) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new IllegalArgumentException("问卷不存在"));
        
        // 验证权限
        if (!survey.getCreatorId().equals(creatorId)) {
            throw new IllegalArgumentException("没有权限操作此问卷");
        }
        
        // 验证问卷状态
        if (survey.getStatus() != Survey.Status.DRAFT) {
            throw new IllegalArgumentException("只能发布草稿状态的问卷");
        }
        
        // 验证问卷内容
        if (survey.getQuestions().isEmpty()) {
            throw new IllegalArgumentException("问卷至少需要包含一个问题");
        }
        
        // 更新状态
        survey.setStatus(Survey.Status.PUBLISHED);
        surveyRepository.save(survey);
        
        // 发送通知
        User creator = userRepository.findById(creatorId).orElse(null);
        if (creator != null && creator.getPartnerId() != null) {
            sendSurveyNotification(survey, "SURVEY_PUBLISHED");
            
            // 推送通知
            pushNotificationService.sendNotification(
                    creator.getPartnerId(),
                    createSurveyNotificationData(survey, "新问卷通知", "你有一份新问卷待填写")
            );
        }
        
        log.info("问卷发布成功: surveyId={}, creatorId={}", surveyId, creatorId);
    }
    
    @Transactional
    public SurveySubmission submitSurvey(Long surveyId, Long respondentId, SurveySubmissionRequest request) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new IllegalArgumentException("问卷不存在"));
        
        // 验证问卷状态
        if (survey.getStatus() != Survey.Status.PUBLISHED) {
            throw new IllegalArgumentException("问卷未发布或已关闭");
        }
        
        // 验证时间范围
        LocalDateTime now = LocalDateTime.now();
        if (survey.getStartTime() != null && now.isBefore(survey.getStartTime())) {
            throw new IllegalArgumentException("问卷尚未开始");
        }
        if (survey.getEndTime() != null && now.isAfter(survey.getEndTime())) {
            throw new IllegalArgumentException("问卷已结束");
        }
        
        // 验证是否允许重复提交
        if (!survey.getMultipleSubmit()) {
            boolean hasSubmitted = surveySubmissionRepository.existsBySurveyIdAndRespondentId(surveyId, respondentId);
            if (hasSubmitted) {
                throw new IllegalArgumentException("已提交过此问卷，不允许重复提交");
            }
        }
        
        // 创建提交记录
        SurveySubmission submission = new SurveySubmission();
        submission.setSurveyId(surveyId);
        submission.setRespondentId(respondentId);
        submission = surveySubmissionRepository.save(submission);
        
        // 保存答案
        List<SurveyResponse> responses = new ArrayList<>();
        for (var responseRequest : request.getResponses()) {
            SurveyResponse response = new SurveyResponse();
            response.setSubmissionId(submission.getId());
            response.setQuestionId(responseRequest.getQuestionId());
            response.setAnswerText(responseRequest.getAnswerText());
            response.setAnswerOptions(responseRequest.getAnswerOptions());
            response.setAnswerNumber(responseRequest.getAnswerNumber());
            
            responses.add(response);
        }
        
        surveyResponseRepository.saveAll(responses);
        
        // 发送提交通知
        sendSurveySubmissionNotification(survey, submission);
        
        // 推送通知给创建者
        pushNotificationService.sendNotification(
                survey.getCreatorId(),
                createSurveyNotificationData(survey, "问卷回复", "你的问卷收到了新的回复")
        );
        
        log.info("问卷提交成功: surveyId={}, respondentId={}, submissionId={}", 
                surveyId, respondentId, submission.getId());
        
        return submission;
    }
    
    public List<SurveyResponse> getSurveysByUser(Long userId) {
        List<Survey> surveys = surveyRepository.findByCreatorIdOrAvailableToUser(userId);
        return surveys.stream()
                .map(this::convertToSurveyResponse)
                .collect(Collectors.toList());
    }
    
    public SurveyResponse getSurveyDetail(Long surveyId, Long userId) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new IllegalArgumentException("问卷不存在"));
        
        // 验证访问权限
        if (!canAccessSurvey(survey, userId)) {
            throw new IllegalArgumentException("没有权限访问此问卷");
        }
        
        return convertToSurveyResponse(survey);
    }
    
    public SurveyAnalysisResponse getSurveyAnalysis(Long surveyId, Long userId) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new IllegalArgumentException("问卷不存在"));
        
        // 验证权限：只有创建者可以查看分析
        if (!survey.getCreatorId().equals(userId)) {
            throw new IllegalArgumentException("只有问卷创建者可以查看分析结果");
        }
        
        // 获取所有提交数据
        List<SurveySubmission> submissions = surveySubmissionRepository.findBySurveyId(surveyId);
        
        SurveyAnalysisResponse analysis = new SurveyAnalysisResponse();
        analysis.setSurveyId(surveyId);
        analysis.setSurveyTitle(survey.getTitle());
        analysis.setTotalSubmissions(submissions.size());
        analysis.setSubmissionRate(calculateSubmissionRate(survey));
        
        // 分析每个问题的回答情况
        Map<Long, QuestionAnalysis> questionAnalyses = new HashMap<>();
        
        for (SurveyQuestion question : survey.getQuestions()) {
            QuestionAnalysis questionAnalysis = analyzeQuestion(question, submissions);
            questionAnalyses.put(question.getId(), questionAnalysis);
        }
        
        analysis.setQuestionAnalyses(questionAnalyses);
        analysis.setGeneratedAt(LocalDateTime.now());
        
        return analysis;
    }
    
    private QuestionAnalysis analyzeQuestion(SurveyQuestion question, List<SurveySubmission> submissions) {
        List<SurveyResponse> responses = submissions.stream()
                .flatMap(s -> s.getResponses().stream())
                .filter(r -> r.getQuestionId().equals(question.getId()))
                .collect(Collectors.toList());
        
        QuestionAnalysis analysis = new QuestionAnalysis();
        analysis.setQuestionId(question.getId());
        analysis.setQuestionTitle(question.getTitle());
        analysis.setQuestionType(question.getQuestionType().name());
        analysis.setTotalResponses(responses.size());
        
        switch (question.getQuestionType()) {
            case SINGLE_CHOICE:
            case MULTIPLE_CHOICE:
                analysis.setOptionStats(analyzeChoiceQuestion(responses));
                break;
            case RATING:
                analysis.setRatingStats(analyzeRatingQuestion(responses));
                break;
            case TEXT_INPUT:
                analysis.setTextResponses(analyzeTextQuestion(responses));
                break;
        }
        
        return analysis;
    }
    
    private Map<String, Integer> analyzeChoiceQuestion(List<SurveyResponse> responses) {
        Map<String, Integer> stats = new HashMap<>();
        
        for (SurveyResponse response : responses) {
            if (response.getAnswerOptions() != null) {
                // 多选题
                List<String> options = response.getAnswerOptions();
                for (String option : options) {
                    stats.merge(option, 1, Integer::sum);
                }
            } else if (response.getAnswerText() != null) {
                // 单选题
                stats.merge(response.getAnswerText(), 1, Integer::sum);
            }
        }
        
        return stats;
    }
    
    private RatingStats analyzeRatingQuestion(List<SurveyResponse> responses) {
        List<Integer> ratings = responses.stream()
                .filter(r -> r.getAnswerNumber() != null)
                .map(r -> r.getAnswerNumber().intValue())
                .collect(Collectors.toList());
        
        if (ratings.isEmpty()) {
            return new RatingStats(0.0, 0, 0, new HashMap<>());
        }
        
        double average = ratings.stream().mapToInt(Integer::intValue).average().orElse(0.0);
        int min = ratings.stream().mapToInt(Integer::intValue).min().orElse(0);
        int max = ratings.stream().mapToInt(Integer::intValue).max().orElse(0);
        
        Map<Integer, Integer> distribution = ratings.stream()
                .collect(Collectors.groupingBy(
                        rating -> rating,
                        Collectors.collectingAndThen(Collectors.counting(), Math::toIntExact)
                ));
        
        return new RatingStats(average, min, max, distribution);
    }
    
    private List<String> analyzeTextQuestion(List<SurveyResponse> responses) {
        return responses.stream()
                .map(SurveyResponse::getAnswerText)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    
    private boolean canAccessSurvey(Survey survey, Long userId) {
        // 创建者可以访问
        if (survey.getCreatorId().equals(userId)) {
            return true;
        }
        
        // 发布状态的问卷，配对用户可以访问
        if (survey.getStatus() == Survey.Status.PUBLISHED) {
            User user = userRepository.findById(userId).orElse(null);
            return user != null && user.getPartnerId() != null && user.getPartnerId().equals(survey.getCreatorId());
        }
        
        return false;
    }
    
    private double calculateSubmissionRate(Survey survey) {
        // 简化计算：假设目标受众是配对用户
        User creator = userRepository.findById(survey.getCreatorId()).orElse(null);
        if (creator == null || creator.getPartnerId() == null) {
            return 0.0;
        }
        
        long submissions = surveySubmissionRepository.countBySurveyId(survey.getId());
        return submissions > 0 ? 100.0 : 0.0; // 配对情况下，1个人提交就是100%
    }
    
    private void sendSurveyNotification(Survey survey, String type) {
        try {
            User creator = userRepository.findById(survey.getCreatorId()).orElse(null);
            if (creator != null && creator.getPartnerId() != null) {
                messagingTemplate.convertAndSendToUser(
                        creator.getPartnerId().toString(),
                        "/queue/surveys",
                        Map.of(
                                "type", type,
                                "survey", convertToSurveyResponse(survey),
                                "timestamp", LocalDateTime.now()
                        )
                );
            }
        } catch (Exception e) {
            log.error("发送问卷通知失败", e);
        }
    }
    
    private void sendSurveySubmissionNotification(Survey survey, SurveySubmission submission) {
        try {
            messagingTemplate.convertAndSendToUser(
                    survey.getCreatorId().toString(),
                    "/queue/surveys/submission",
                    Map.of(
                            "type", "SURVEY_SUBMITTED",
                            "surveyId", survey.getId(),
                            "submissionId", submission.getId(),
                            "timestamp", LocalDateTime.now()
                    )
            );
        } catch (Exception e) {
            log.error("发送问卷提交通知失败", e);
        }
    }
    
    private NotificationData createSurveyNotificationData(Survey survey, String title, String body) {
        return NotificationData.builder()
                .title(title)
                .body(body)
                .data(Map.of(
                        "type", "survey",
                        "surveyId", survey.getId().toString(),
                        "url", "/surveys/" + survey.getId()
                ))
                .tag("survey_" + survey.getId())
                .build();
    }
    
    private SurveyResponse convertToSurveyResponse(Survey survey) {
        return SurveyResponse.builder()
                .id(survey.getId())
                .creatorId(survey.getCreatorId())
                .title(survey.getTitle())
                .description(survey.getDescription())
                .status(survey.getStatus().name())
                .anonymous(survey.getAnonymous())
                .multipleSubmit(survey.getMultipleSubmit())
                .startTime(survey.getStartTime())
                .endTime(survey.getEndTime())
                .createdAt(survey.getCreatedAt())
                .updatedAt(survey.getUpdatedAt())
                .questions(survey.getQuestions())
                .submissions(survey.getSubmissions())
                .build();
    }
}
```

### 8.4 数据库优化与索引策略

#### 8.4.1 性能优化索引

```sql
-- 用户表索引优化
CREATE INDEX idx_users_phone_status ON users(phone, status);
CREATE INDEX idx_users_wechat_openid_status ON users(wechat_openid, status);
CREATE INDEX idx_users_partner_pair ON users(partner_id, pair_code);

-- 订单表索引优化
CREATE INDEX idx_orders_creator_status_created ON orders(creator_id, status, created_at DESC);
CREATE INDEX idx_orders_assignee_status_created ON orders(assignee_id, status, created_at DESC);
CREATE INDEX idx_orders_status_due_time ON orders(status, due_time);
CREATE INDEX idx_orders_type_priority ON orders(order_type, priority);

-- 问卷表索引优化
CREATE INDEX idx_surveys_creator_status_created ON surveys(creator_id, status, created_at DESC);
CREATE INDEX idx_surveys_status_start_end ON surveys(status, start_time, end_time);

-- 问卷题目表索引优化
CREATE INDEX idx_survey_questions_survey_order ON survey_questions(survey_id, order_index);
CREATE INDEX idx_survey_questions_type ON survey_questions(question_type);

-- 问卷提交表索引优化
CREATE INDEX idx_survey_submissions_survey_submitted ON survey_submissions(survey_id, submitted_at DESC);
CREATE INDEX idx_survey_submissions_respondent_submitted ON survey_submissions(respondent_id, submitted_at DESC);

-- 问卷回答表索引优化
CREATE INDEX idx_survey_responses_submission ON survey_responses(submission_id);
CREATE INDEX idx_survey_responses_question ON survey_responses(question_id);

-- 文件存储表索引优化
CREATE INDEX idx_media_files_uploader_type_created ON media_files(uploaded_by, file_type, created_at DESC);
CREATE INDEX idx_media_files_type_status ON media_files(file_type, status);
CREATE INDEX idx_media_files_bucket_object ON media_files(bucket_name, object_key);

-- 推送通知表索引优化
CREATE INDEX idx_push_notifications_user_sent ON push_notifications(user_id, sent_at DESC);
CREATE INDEX idx_push_notifications_sent_read ON push_notifications(sent_at, read_at);

-- 表情包索引优化
CREATE INDEX idx_emojis_package_active_sort ON emojis(package_id, is_active, sort_order);
CREATE INDEX idx_emojis_code_usage ON emojis(emoji_code, usage_count DESC);
```

#### 8.4.2 查询优化示例

**复杂查询优化**
```sql
-- 获取用户相关订单（创建的和分配的）
SELECT o.*, 
       creator.nickname as creator_name,
       assignee.nickname as assignee_name,
       COUNT(oi.id) as item_count,
       AVG(oe.rating) as avg_rating
FROM orders o
LEFT JOIN users creator ON o.creator_id = creator.id
LEFT JOIN users assignee ON o.assignee_id = assignee.id
LEFT JOIN order_items oi ON o.id = oi.order_id
LEFT JOIN order_evaluations oe ON o.id = oe.order_id
WHERE (o.creator_id = ? OR o.assignee_id = ?)
  AND o.created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY)
GROUP BY o.id
ORDER BY o.created_at DESC
LIMIT 20;

-- 获取问卷统计数据
SELECT s.id,
       s.title,
       s.status,
       COUNT(DISTINCT sub.id) as submission_count,
       COUNT(DISTINCT sr.id) as response_count,
       AVG(CASE WHEN sq.question_type = 'RATING' 
                THEN sr.answer_number 
                ELSE NULL END) as avg_rating
FROM surveys s
LEFT JOIN survey_questions sq ON s.id = sq.survey_id
LEFT JOIN survey_submissions sub ON s.id = sub.survey_id
LEFT JOIN survey_responses sr ON sub.id = sr.submission_id
WHERE s.creator_id = ?
  AND s.status = 'PUBLISHED'
GROUP BY s.id
ORDER BY s.created_at DESC;
```

### 8.5 实时通信WebSocket实现

#### 8.5.1 WebSocket配置

**WebSocketConfig.java**
```java
package com.couple.platform.config;

import com.couple.platform.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    private final JwtTokenProvider jwtTokenProvider;
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 启用简单消息代理，并设置消息代理的前缀
        config.enableSimpleBroker("/queue", "/topic");
        // 设置应用程序目标前缀
        config.setApplicationDestinationPrefixes("/app");
        // 设置用户目标前缀
        config.setUserDestinationPrefix("/user");
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 注册STOMP端点
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
    
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String token = accessor.getFirstNativeHeader("Authorization");
                    if (token != null && token.startsWith("Bearer ")) {
                        token = token.substring(7);
                        try {
                            if (jwtTokenProvider.validateToken(token)) {
                                Long userId = jwtTokenProvider.getUserIdFromToken(token);
                                accessor.setUser(new UsernamePasswordAuthenticationToken(userId, null));
                            }
                        } catch (Exception e) {
                            // Token无效，连接将被拒绝
                            return null;
                        }
                    }
                }
                
                return message;
            }
        });
    }
}
```

#### 8.5.2 WebSocket消息处理

**WebSocketController.java**
```java
package com.couple.platform.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketController {
    
    private final SimpMessagingTemplate messagingTemplate;
    
    @MessageMapping("/heartbeat")
    @SendTo("/topic/heartbeat")
    public Map<String, Object> heartbeat(Authentication authentication) {
        return Map.of(
                "type", "heartbeat",
                "userId", authentication.getName(),
                "timestamp", LocalDateTime.now()
        );
    }
    
    @SubscribeMapping("/queue/orders")
    public void subscribeToOrders(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        log.info("用户 {} 订阅了订单通知", userId);
    }
    
    @SubscribeMapping("/queue/surveys")
    public void subscribeToSurveys(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        log.info("用户 {} 订阅了问卷通知", userId);
    }
    
    @MessageMapping("/order/status")
    public void updateOrderStatus(Map<String, Object> message, Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        Long orderId = Long.parseLong(message.get("orderId").toString());
        String status = message.get("status").toString();
        
        // 这里可以触发订单状态更新的业务逻辑
        log.info("收到订单状态更新请求: userId={}, orderId={}, status={}", userId, orderId, status);
    }
}
```

### 8.6 部署配置

#### 8.6.1 Docker配置

**Dockerfile**
```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/couple-platform-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

**docker-compose.yml**
```yaml
version: '3.8'

services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DB_HOST=mysql
      - DB_USERNAME=couple_user
      - DB_PASSWORD=couple_pass
      - REDIS_HOST=redis
      - MINIO_ENDPOINT=http://minio:9000
    depends_on:
      - mysql
      - redis
      - minio
    volumes:
      - ./logs:/app/logs

  mysql:
    image: mysql:8.0
    environment:
      - MYSQL_ROOT_PASSWORD=root_password
      - MYSQL_DATABASE=couple_platform
      - MYSQL_USER=couple_user
      - MYSQL_PASSWORD=couple_pass
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./database/init.sql:/docker-entrypoint-initdb.d/init.sql

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data

  minio:
    image: minio/minio:latest
    command: server /data --console-address ":9001"
    ports:
      - "9000:9000"
      - "9001:9001"
    environment:
      - MINIO_ROOT_USER=minioadmin
      - MINIO_ROOT_PASSWORD=minioadmin
    volumes:
      - minio_data:/data

  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf
      - ./ssl:/etc/nginx/ssl
      - ./frontend/dist:/usr/share/nginx/html
    depends_on:
      - app

volumes:
  mysql_data:
  redis_data:
  minio_data:
```

#### 8.6.2 Nginx配置

**nginx.conf**
```nginx
events {
    worker_connections 1024;
}

http {
    include       /etc/nginx/mime.types;
    default_type  application/octet-stream;
    
    upstream backend {
        server app:8080;
    }
    
    # 前端静态文件服务
    server {
        listen 80;
        server_name localhost;
        
        # 前端路由
        location / {
            root /usr/share/nginx/html;
            try_files $uri $uri/ /index.html;
        }
        
        # API代理
        location /api/ {
            proxy_pass http://backend;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }
        
        # WebSocket代理
        location /ws/ {
            proxy_pass http://backend;
            proxy_http_version 1.1;
            proxy_set_header Upgrade $http_upgrade;
            proxy_set_header Connection "upgrade";
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }
        
        # 文件上传大小限制
        client_max_body_size 10M;
    }
}
```

### 8.7 测试策略

#### 8.7.1 单元测试示例

**OrderServiceTest.java**
```java
package com.couple.platform.service;

import com.couple.platform.entity.Order;
import com.couple.platform.entity.User;
import com.couple.platform.repository.OrderRepository;
import com.couple.platform.repository.UserRepository;
import com.couple.platform.dto.request.OrderCreateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    
    @Mock
    private OrderRepository orderRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PushNotificationService pushNotificationService;
    
    @InjectMocks
    private OrderService orderService;
    
    private User testUser;
    private User testPartner;
    
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setPhone("13800138001");
        testUser.setNickname("测试用户");
        testUser.setPartnerId(2L);
        
        testPartner = new User();
        testPartner.setId(2L);
        testPartner.setPhone("13800138002");
        testPartner.setNickname("测试伴侣");
        testPartner.setPartnerId(1L);
    }
    
    @Test
    void createOrder_Success() {
        // Given
        OrderCreateRequest request = new OrderCreateRequest();
        request.setTitle("测试订单");
        request.setDescription("测试描述");
        request.setOrderType("FOOD");
        request.setPriority("MEDIUM");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return order;
        });
        
        // When
        var result = orderService.createOrder(1L, request);
        
        // Then
        assertNotNull(result);
        assertEquals("测试订单", result.getTitle());
        assertEquals(1L, result.getCreatorId());
        assertEquals(2L, result.getAssigneeId());
        
        verify(orderRepository).save(any(Order.class));
        verify(pushNotificationService).sendNotification(eq(2L), any());
    }
    
    @Test
    void createOrder_UserNotPaired_ThrowsException() {
        // Given
        testUser.setPartnerId(null);
        OrderCreateRequest request = new OrderCreateRequest();
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrder(1L, request);
        });
        
        verify(orderRepository, never()).save(any(Order.class));
    }
    
    @Test
    void updateOrderStatus_Success() {
        // Given
        Order order = new Order();
        order.setId(1L);
        order.setCreatorId(1L);
        order.setAssigneeId(2L);
        order.setStatus(Order.Status.PENDING);
        
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        
        // When
        orderService.updateOrderStatus(1L, Order.Status.COMPLETED, 2L);
        
        // Then
        assertEquals(Order.Status.COMPLETED, order.getStatus());
        assertNotNull(order.getCompletedAt());
        
        verify(orderRepository).save(order);
        verify(pushNotificationService).sendNotification(eq(1L), any());
    }
}
```

#### 8.7.2 集成测试示例

**OrderControllerIntegrationTest.java**
```java
package com.couple.platform.controller;

import com.couple.platform.entity.User;
import com.couple.platform.repository.UserRepository;
import com.couple.platform.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class OrderControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private String authToken;
    private User testUser;
    
    @BeforeEach
    void setUp() {
        // 创建测试用户
        testUser = new User();
        testUser.setPhone("13800138001");
        testUser.setNickname("测试用户");
        testUser.setPartnerId(2L);
        testUser = userRepository.save(testUser);
        
        // 生成JWT Token
        authToken = tokenProvider.generateToken(testUser.getId());
    }
    
    @Test
    void createOrder_Success() throws Exception {
        var orderRequest = Map.of(
                "title", "测试订单",
                "description", "测试描述",
                "orderType", "FOOD",
                "priority", "MEDIUM",
                "items", List.of(
                        Map.of("itemName", "汉堡", "quantity", 1)
                )
        );
        
        mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("测试订单"))
                .andExpect(jsonPath("$.data.creatorId").value(testUser.getId()))
                .andExpect(jsonPath("$.data.status").value("PENDING"));
    }
    
    @Test
    void getOrders_Success() throws Exception {
        mockMvc.perform(get("/api/orders")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }
}
```

## 总结

这个继续实现的设计文档补充了以下关键内容：

1. **前端Vue组件实现** - 详细的订单和问卷创建组件
2. **状态管理Store** - 完整的Pinia状态管理逻辑
3. **API接口实现** - 前端API调用封装
4. **后端业务逻辑** - 订单服务和问卷服务的完整实现
5. **数据库优化** - 索引策略和查询优化
6. **WebSocket实时通信** - 完整的实时通信架构
7. **部署配置** - Docker和Nginx配置
8. **测试策略** - 单元测试和集成测试示例

通过这些实现细节，开发团队可以按照文档进行具体的代码开发，确保系统的完整性和高质量。
