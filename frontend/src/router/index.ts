import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/auth/LoginPage.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('@/views/auth/RegisterPage.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'home',
        component: () => import('@/views/home/HomePage.vue'),
        meta: { title: '首页', showTabBar: true }
      },
      {
        path: 'orders',
        name: 'orders',
        component: () => import('@/views/orders/OrderListPage.vue'),
        meta: { title: '订单', showTabBar: true }
      },
      {
        path: 'orders/create',
        name: 'order-create',
        component: () => import('@/views/orders/OrderCreatePage.vue'),
        meta: { title: '创建订单' }
      },
      {
        path: 'orders/:id',
        name: 'order-detail',
        component: () => import('@/views/orders/OrderDetailPage.vue'),
        meta: { title: '订单详情' }
      },
      {
        path: 'surveys',
        name: 'surveys',
        component: () => import('@/views/surveys/SurveyListPage.vue'),
        meta: { title: '问卷', showTabBar: true }
      },
      {
        path: 'surveys/create',
        name: 'survey-create',
        component: () => import('@/views/surveys/SurveyCreatePage.vue'),
        meta: { title: '创建问卷' }
      },
      {
        path: 'surveys/:id',
        name: 'survey-detail',
        component: () => import('@/views/surveys/SurveyDetailPage.vue'),
        meta: { title: '问卷详情' }
      },
      {
        path: 'surveys/:id/respond',
        name: 'survey-respond',
        component: () => import('@/views/surveys/SurveyRespondPage.vue'),
        meta: { title: '填写问卷' }
      },
      {
        path: 'messages',
        name: 'messages',
        component: () => import('@/views/messages/MessageListPage.vue'),
        meta: { title: '消息', showTabBar: true }
      },
      {
        path: 'profile',
        name: 'profile',
        component: () => import('@/views/profile/ProfilePage.vue'),
        meta: { title: '我的', showTabBar: true }
      },
      {
        path: 'profile/edit',
        name: 'profile-edit',
        component: () => import('@/views/profile/ProfileEditPage.vue'),
        meta: { title: '编辑资料' }
      },
      {
        path: 'pair',
        name: 'pair',
        component: () => import('@/views/pair/PairPage.vue'),
        meta: { title: '情侣配对' }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'not-found',
    component: () => import('@/views/errors/NotFoundPage.vue'),
    meta: { title: '页面不存在' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = (to.meta.title as string) || '情侣互动'
  
  const token = localStorage.getItem('token')
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  
  if (requiresAuth && !token && to.name !== 'login') {
    next({ name: 'login', query: { redirect: to.fullPath } })
  } else if (to.name === 'login' && token) {
    next({ name: 'home' })
  } else {
    next()
  }
})

export default router
