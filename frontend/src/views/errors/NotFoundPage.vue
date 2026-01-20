<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/')
}
</script>

<template>
  <div class="not-found-page">
    <div class="content">
      <div class="icon">
        <van-icon name="warning" size="80" color="#969799" />
      </div>
      <div class="title">页面不存在</div>
      <div class="desc">您访问的页面不存在或已被删除</div>
      <van-button type="primary" @click="goHome">返回首页</van-button>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.not-found-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f7f8fa;
}

.content {
  text-align: center;
  padding: 32px;
}

.icon {
  margin-bottom: 24px;
}

.title {
  font-size: 20px;
  font-weight: 600;
  color: #323233;
  margin-bottom: 12px;
}

.desc {
  font-size: 14px;
  color: #969799;
  margin-bottom: 32px;
}

.van-button {
  border-radius: 24px;
  padding: 0 32px;
}
</style>
