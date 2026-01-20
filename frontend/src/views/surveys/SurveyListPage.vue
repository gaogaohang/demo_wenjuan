<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { mockSurveyService, type Survey } from '@/services/mock'
import { showFailToast } from 'vant'

const router = useRouter()
const surveys = ref<Survey[]>([])
const loading = ref(true)
const activeTab = ref('all')

const tabs = [
  { name: '全部', value: 'all' },
  { name: '我创建的', value: 'created' },
  { name: '待填写', value: 'target' }
]

const filteredSurveys = computed(() => {
  if (activeTab.value === 'all') {
    return surveys.value
  } else if (activeTab.value === 'created') {
    return surveys.value.filter(s => s.creatorId === 1)
  } else {
    return surveys.value.filter(s => s.targetId === 1 && s.status === 'published')
  }
})

const loadSurveys = async () => {
  loading.value = true
  try {
    const response = await mockSurveyService.getSurveys()
    surveys.value = response.data
  } catch (error) {
    showFailToast('加载问卷失败')
  } finally {
    loading.value = false
  }
}

const handleTabChange = (tab: string) => {
  activeTab.value = tab
}

const navigateToCreate = () => {
  router.push('/surveys/create')
}

const navigateToDetail = (id: number) => {
  router.push(`/surveys/${id}`)
}

const navigateToRespond = (id: number) => {
  router.push(`/surveys/${id}/respond`)
}

const getStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    draft: '草稿',
    published: '进行中',
    closed: '已结束'
  }
  return statusMap[status] || status
}

const getStatusColor = (status: string) => {
  const colorMap: Record<string, string> = {
    draft: '#969799',
    published: '#07c160',
    closed: '#ff976a'
  }
  return colorMap[status] || '#969799'
}

const formatTime = (time: string) => {
  return new Date(time).toLocaleDateString('zh-CN')
}

onMounted(() => {
  loadSurveys()
})
</script>

<template>
  <div class="survey-list-page">
    <div class="tabs-header">
      <van-tabs v-model:active="activeTab" @change="handleTabChange">
        <van-tab
          v-for="tab in tabs"
          :key="tab.value"
          :name="tab.value"
          :title="tab.name"
        />
      </van-tabs>
    </div>

    <div class="survey-list" v-if="filteredSurveys.length > 0">
      <div
        class="survey-card"
        v-for="survey in filteredSurveys"
        :key="survey.id"
      >
        <div class="card-header">
          <span class="survey-status" :style="{ color: getStatusColor(survey.status) }">
            {{ getStatusText(survey.status) }}
          </span>
          <span class="survey-time">{{ formatTime(survey.createdTime) }}</span>
        </div>
        
        <div class="card-content" @click="navigateToDetail(survey.id)">
          <div class="survey-title">{{ survey.title }}</div>
          <div class="survey-desc" v-if="survey.description">{{ survey.description }}</div>
          <div class="survey-meta">
            <span class="meta-item">
              <van-icon name="user-o" />
              {{ survey.creatorName }}
            </span>
            <span class="meta-item">
              <van-icon name="description" />
              {{ survey.questions.length }}题
            </span>
            <span class="meta-item">
              <van-icon name="clock-o" />
              {{ survey.responseCount }}人已回答
            </span>
          </div>
        </div>
        
        <div class="card-footer">
          <van-button
            v-if="survey.status === 'published' && survey.targetId === 1"
            type="primary"
            size="small"
            round
            @click="navigateToRespond(survey.id)"
          >
            填写问卷
          </van-button>
          <van-button
            v-else-if="survey.status === 'published'"
            type="primary"
            size="small"
            round
            plain
            @click="navigateToDetail(survey.id)"
          >
            查看详情
          </van-button>
        </div>
      </div>
    </div>

    <van-empty v-else-if="!loading" description="暂无问卷" />

    <van-loading v-if="loading" size="24px" vertical>加载中...</van-loading>

    <van-button
      type="primary"
      round
      fixed
      right="16px"
      bottom="80px"
      icon="plus"
      @click="navigateToCreate"
    >
      创建问卷
    </van-button>
  </div>
</template>

<style lang="scss" scoped>
.survey-list-page {
  padding: 16px;
}

.tabs-header {
  margin-bottom: 16px;
}

.survey-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.survey-card {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.survey-status {
  font-size: 14px;
  font-weight: 500;
}

.survey-time {
  font-size: 12px;
  color: #969799;
}

.card-content {
  cursor: pointer;
}

.survey-title {
  font-size: 16px;
  font-weight: 600;
  color: #323233;
  margin-bottom: 8px;
}

.survey-desc {
  font-size: 14px;
  color: #969799;
  margin-bottom: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.survey-meta {
  display: flex;
  gap: 16px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #969799;
}

.card-footer {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #f5f5f5;
  display: flex;
  justify-content: flex-end;
}
</style>
