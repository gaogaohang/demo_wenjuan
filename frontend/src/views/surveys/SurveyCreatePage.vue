<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showFailToast, showSuccessToast } from 'vant'

const router = useRouter()

const title = ref('')
const description = ref('')
const isAnonymous = ref(false)
const allowMultiple = ref(false)
const endTime = ref('')
const questions = ref([
  { id: 0, type: 'single', title: '', options: ['', ''] }
])
const loading = ref(false)

const addQuestion = () => {
  questions.value.push({
    id: questions.value.length,
    type: 'single',
    title: '',
    options: ['', '']
  })
}

const removeQuestion = (index: number) => {
  if (questions.value.length > 1) {
    questions.value.splice(index, 1)
  }
}

const addOption = (questionIndex: number) => {
  questions.value[questionIndex].options.push('')
}

const removeOption = (questionIndex: number, optionIndex: number) => {
  if (questions.value[questionIndex].options.length > 2) {
    questions.value[questionIndex].options.splice(optionIndex, 1)
  }
}

const handleCreate = async () => {
  if (!title.value) {
    showFailToast('请输入问卷标题')
    return
  }
  
  const validQuestions = questions.value.filter(q => q.title.trim())
  if (validQuestions.length === 0) {
    showFailToast('请至少添加一个问题')
    return
  }

  loading.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 500))
    showSuccessToast('问卷创建成功')
    router.back()
  } catch (error) {
    showFailToast('创建失败')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="survey-create-page">
    <van-form @submit="handleCreate">
      <div class="form-section">
        <div class="section-title">基本信息</div>
        
        <van-field
          v-model="title"
          label="标题"
          placeholder="请输入问卷标题"
          :rules="[{ required: true, message: '请输入问卷标题' }]"
        />
        
        <van-field
          v-model="description"
          label="描述"
          type="textarea"
          rows="2"
          placeholder="请输入问卷描述（选填）"
        />
        
        <van-field name="settings" label="设置">
          <template #input>
            <div class="settings-row">
              <span>匿名填写</span>
              <van-switch v-model="isAnonymous" />
            </div>
            <div class="settings-row">
              <span>允许多选</span>
              <van-switch v-model="allowMultiple" />
            </div>
          </template>
        </van-field>
        
        <van-field
          v-model="endTime"
          label="截止时间"
          type="date"
          placeholder="请选择截止时间"
        />
      </div>

      <div class="form-section">
        <div class="section-header">
          <div class="section-title">问卷题目</div>
          <van-button type="primary" size="small" plain @click="addQuestion">添加题目</van-button>
        </div>
        
        <div class="question-list">
          <div
            class="question-card"
            v-for="(question, qIndex) in questions"
            :key="qIndex"
          >
            <div class="question-header">
              <span class="question-num">Q{{ qIndex + 1 }}</span>
              <van-icon name="close" @click="removeQuestion(qIndex)" />
            </div>
            
            <van-field
              v-model="question.title"
              placeholder="请输入问题内容"
              size="small"
            />
            
            <van-field name="type" label="类型">
              <template #input>
                <van-radio-group v-model="question.type" direction="horizontal">
                  <van-radio name="single">单选</van-radio>
                  <van-radio name="multiple">多选</van-radio>
                  <van-radio name="text">问答</van-radio>
                </van-radio-group>
              </template>
            </van-field>
            
            <div class="options-section" v-if="question.type !== 'text'">
              <div class="options-label">选项</div>
              <div class="option-list">
                <div
                  class="option-row"
                  v-for="(option, oIndex) in question.options"
                  :key="oIndex"
                >
                  <span class="option-letter">{{ String.fromCharCode(65 + oIndex) }}</span>
                  <van-field
                    v-model="question.options[oIndex]"
                    placeholder="选项内容"
                    size="small"
                  />
                  <van-icon
                    name="close"
                    v-if="question.options.length > 2"
                    @click="removeOption(qIndex, oIndex)"
                  />
                </div>
                <div class="add-option" @click="addOption(qIndex)">
                  <van-icon name="plus" />
                  <span>添加选项</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="submit-section">
        <van-button type="primary" native-type="submit" block :loading="loading">
          创建问卷
        </van-button>
      </div>
    </van-form>
  </div>
</template>

<style lang="scss" scoped>
.survey-create-page {
  padding: 16px;
  padding-bottom: 100px;
}

.form-section {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #323233;
  margin-bottom: 12px;
}

.settings-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
}

.question-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.question-card {
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

.question-num {
  font-size: 14px;
  font-weight: 600;
  color: #1989fa;
}

.options-section {
  margin-top: 12px;
}

.options-label {
  font-size: 14px;
  color: #969799;
  margin-bottom: 8px;
}

.option-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.option-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.option-letter {
  width: 24px;
  height: 24px;
  background: #e6f4ff;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: #1989fa;
  flex-shrink: 0;
}

.add-option {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px;
  color: #1989fa;
  font-size: 14px;
  cursor: pointer;
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
