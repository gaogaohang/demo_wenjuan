<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { mockSurveyService, type Survey, type SurveyQuestion } from '@/services/mock'
import { showFailToast, showSuccessToast } from 'vant'

const route = useRoute()
const router = useRouter()

const survey = ref<Survey | null>(null)
const answers = ref<Record<number, string | string[]>>({})
const loading = ref(true)
const submitting = ref(false)

const loadSurvey = async () => {
  const id = parseInt(route.params.id as string)
  loading.value = true
  try {
    const response = await mockSurveyService.getSurveyDetail(id)
    survey.value = response.data
    survey.value.questions.forEach((q, index) => {
      if (q.type === 'single') {
        answers.value[index] = ''
      } else if (q.type === 'multiple') {
        answers.value[index] = []
      } else {
        answers.value[index] = ''
      }
    })
  } catch (error) {
    showFailToast('加载问卷失败')
    router.back()
  } finally {
    loading.value = false
  }
}

const handleSingleChange = (questionIndex: number, value: string) => {
  answers.value[questionIndex] = value
}

const handleMultipleChange = (questionIndex: number, value: string, checked: boolean) => {
  const current = (answers.value[questionIndex] as string[]) || []
  if (checked) {
    answers.value[questionIndex] = [...current, value]
  } else {
    answers.value[questionIndex] = current.filter(v => v !== value)
  }
}

const handleSubmit = async () => {
  if (!survey.value) return
  
  const hasEmptyAnswer = survey.value.questions.some((q, index) => {
    const answer = answers.value[index]
    if (q.type === 'text') {
      return !answer || (answer as string).trim() === ''
    }
    return !answer || (Array.isArray(answer) && answer.length === 0)
  })
  
  if (hasEmptyAnswer) {
    showFailToast('请回答所有问题')
    return
  }

  submitting.value = true
  try {
    const formattedAnswers = Object.entries(answers.value).map(([questionId, answer]) => ({
      questionId: parseInt(questionId),
      answer: Array.isArray(answer) ? answer.join(',') : answer
    }))
    
    await mockSurveyService.submitResponse(survey.value.id, formattedAnswers)
    showSuccessToast('问卷已提交')
    router.back()
  } catch (error) {
    showFailToast('提交失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadSurvey()
})
</script>

<template>
  <div class="survey-respond-page" v-if="survey">
    <div class="header-section">
      <div class="survey-title">{{ survey.title }}</div>
      <div class="survey-tips">请认真回答以下问题</div>
    </div>

    <div class="questions-section">
      <div
        class="question-card"
        v-for="(question, index) in survey.questions"
        :key="index"
      >
        <div class="question-header">
          <span class="question-num">Q{{ index + 1 }}</span>
          <span class="question-type">
            {{ question.type === 'single' ? '单选' : question.type === 'multiple' ? '多选' : '问答' }}
          </span>
        </div>
        
        <div class="question-title">{{ question.title }}</div>
        
        <div class="question-answer">
          <template v-if="question.type === 'single'">
            <van-radio-group
              :modelValue="answers[index] as string"
              @update:modelValue="handleSingleChange(index, $event)"
            >
              <van-radio
                v-for="(option, optIndex) in question.options"
                :key="optIndex"
                :name="option"
              >
                {{ option }}
              </van-radio>
            </van-radio-group>
          </template>
          
          <template v-else-if="question.type === 'multiple'">
            <van-checkbox-group
              :modelValue="answers[index] as string[]"
              @update:modelValue="handleMultipleChange(index, $event, true)"
            >
              <van-checkbox
                v-for="(option, optIndex) in question.options"
                :key="optIndex"
                :name="option"
              >
                {{ option }}
              </van-checkbox>
            </van-checkbox-group>
          </template>
          
          <template v-else>
            <van-field
              :modelValue="answers[index] as string"
              @update:modelValue="answers[index] = $event"
              type="textarea"
              rows="3"
              placeholder="请输入您的回答"
            />
          </template>
        </div>
      </div>
    </div>

    <div class="submit-section">
      <van-button type="primary" block :loading="submitting" @click="handleSubmit">
        提交问卷
      </van-button>
    </div>
  </div>

  <van-loading v-else size="24px" vertical>加载中...</van-loading>
</template>

<style lang="scss" scoped>
.survey-respond-page {
  min-height: 100vh;
  background: #f7f8fa;
  padding-bottom: 100px;
}

.header-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  margin: -16px -16px 0;
  padding: 24px 16px;
  color: #fff;
}

.survey-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 8px;
}

.survey-tips {
  font-size: 14px;
  opacity: 0.9;
}

.questions-section {
  padding: 16px;
}

.question-card {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.question-num {
  font-size: 14px;
  font-weight: 600;
  color: #1989fa;
}

.question-type {
  font-size: 12px;
  color: #969799;
}

.question-title {
  font-size: 16px;
  color: #323233;
  margin-bottom: 16px;
}

.question-answer {
  .van-radio,
  .van-checkbox {
    margin-bottom: 12px;
    
    &:last-child {
      margin-bottom: 0;
    }
  }
}

.submit-section {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 16px;
  background: #fff;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.1);
}
</style>
