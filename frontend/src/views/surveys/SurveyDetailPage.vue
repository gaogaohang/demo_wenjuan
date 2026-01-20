<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { mockSurveyService, type Survey } from '@/services/mock'
import { showFailToast } from 'vant'

const route = useRoute()
const router = useRouter()

const survey = ref<Survey | null>(null)
const loading = ref(true)

const loadSurvey = async () => {
  const id = parseInt(route.params.id as string)
  loading.value = true
  try {
    const response = await mockSurveyService.getSurveyDetail(id)
    survey.value = response.data
  } catch (error) {
    showFailToast('加载问卷失败')
    router.back()
  } finally {
    loading.value = false
  }
}

const navigateToRespond = () => {
  router.push(`/surveys/${survey.value?.id}/respond`)
}

const getStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    draft: '草稿',
    published: '进行中',
    closed: '已结束'
  }
  return statusMap[status] || status
}

onMounted(() => {
  loadSurvey()
})
</script>

<template>
  <div class="survey-detail-page" v-if="survey">
    <div class="header-section">
      <div class="survey-title">{{ survey.title }}</div>
      <div class="survey-meta">
        <span class="status-tag" :class="survey.status">{{ getStatusText(survey.status) }}</span>
        <span class="meta-item">
          <van-icon name="user-o" />
          {{ survey.creatorName }}
        </span>
        <span class="meta-item">
          <van-icon name="clock-o" />
          {{ survey.responseCount }}人已回答
        </span>
      </div>
    </div>

    <div class="content-section">
      <div class="description" v-if="survey.description">
        <div class="section-label">问卷描述</div>
        <div class="description-text">{{ survey.description }}</div>
      </div>

      <div class="questions-section">
        <div class="section-label">问卷题目</div>
        <div class="question-list">
          <div
            class="question-item"
            v-for="(question, index) in survey.questions"
            :key="index"
          >
            <div class="question-header">
              <span class="question-number">Q{{ index + 1 }}</span>
              <span class="question-type">
                {{ question.type === 'single' ? '单选' : question.type === 'multiple' ? '多选' : '问答' }}
              </span>
            </div>
            <div class="question-content">{{ question.title }}</div>
            <div class="question-options" v-if="question.options && question.options.length > 0">
              <div
                class="option-item"
                v-for="(option, optIndex) in question.options"
                :key="optIndex"
              >
                <span class="option-letter">{{ String.fromCharCode(65 + optIndex) }}</span>
                <span class="option-text">{{ option }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="time-info">
        <div class="time-row">
          <van-icon name="clock-o" />
          <span>开始时间: {{ new Date(survey.startTime).toLocaleString() }}</span>
        </div>
        <div class="time-row">
          <van-icon name="clock-o" />
          <span>结束时间: {{ new Date(survey.endTime).toLocaleString() }}</span>
        </div>
      </div>
    </div>

    <div class="actions-section" v-if="survey.status === 'published'">
      <van-button type="primary" block round @click="navigateToRespond">
        填写问卷
      </van-button>
    </div>
  </div>

  <van-loading v-else size="24px" vertical>加载中...</van-loading>
</template>

<style lang="scss" scoped>
.survey-detail-page {
  padding: 16px;
  padding-bottom: 100px;
}

.header-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  margin: -16px -16px 16px;
  padding: 24px 16px;
  color: #fff;
}

.survey-title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 12px;
}

.survey-meta {
  display: flex;
  align-items: center;
  gap: 16px;
}

.status-tag {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  background: rgba(255, 255, 255, 0.2);
  
  &.published {
    background: #07c160;
  }
  
  &.draft {
    background: #969799;
  }
  
  &.closed {
    background: #ff976a;
  }
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  opacity: 0.9;
}

.content-section {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
}

.section-label {
  font-size: 14px;
  font-weight: 600;
  color: #323233;
  margin-bottom: 12px;
}

.description-text {
  font-size: 14px;
  color: #969799;
  line-height: 1.6;
  margin-bottom: 16px;
}

.question-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.question-item {
  padding: 16px;
  background: #f7f8fa;
  border-radius: 8px;
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.question-number {
  font-size: 14px;
  font-weight: 600;
  color: #1989fa;
}

.question-type {
  font-size: 12px;
  color: #969799;
}

.question-content {
  font-size: 16px;
  color: #323233;
  margin-bottom: 12px;
}

.question-options {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.option-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #323233;
}

.option-letter {
  width: 20px;
  height: 20px;
  background: #e6f4ff;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: #1989fa;
}

.time-info {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f5f5f5;
}

.time-row {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #969799;
  margin-bottom: 8px;
}

.actions-section {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 16px;
  background: #fff;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.1);
}
</style>
