<script setup lang="ts">
import { ref } from 'vue'
import { useUserStore } from '@/stores/user'
import { showFailToast, showSuccessToast, showConfirmDialog } from 'vant'

const userStore = useUserStore()

const nickname = ref(userStore.userInfo?.nickname || '')
const bio = ref(userStore.userInfo?.bio || '')
const birthday = ref(userStore.userInfo?.birthday || '')
const gender = ref(userStore.userInfo?.gender || 'male')
const loading = ref(false)

const handleSave = async () => {
  if (!nickname.value.trim()) {
    showFailToast('请输入昵称')
    return
  }

  loading.value = true
  try {
    await userStore.updateUserProfile({
      nickname: nickname.value,
      bio: bio.value,
      birthday: birthday.value,
      gender: gender.value
    })
    showSuccessToast('保存成功')
  } catch (error) {
    showFailToast('保存失败')
  } finally {
    loading.value = false
  }
}

const handleAvatarChange = () => {
  showSuccessToast('头像修改功能开发中')
}
</script>

<template>
  <div class="profile-edit-page">
    <div class="avatar-section">
      <div class="avatar-wrapper" @click="handleAvatarChange">
        <van-image round :src="userStore.userInfo?.avatarUrl" class="avatar" />
        <div class="avatar-edit">
          <van-icon name="photograph" />
        </div>
      </div>
      <div class="avatar-tips">点击更换头像</div>
    </div>

    <van-form @submit="handleSave">
      <div class="form-section">
        <van-field
          v-model="nickname"
          label="昵称"
          placeholder="请输入昵称"
          :rules="[{ required: true, message: '请输入昵称' }]"
        />
        
        <van-field
          v-model="bio"
          label="个人简介"
          type="textarea"
          rows="2"
          placeholder="请输入个人简介"
          maxlength="100"
          show-word-limit
        />
        
        <van-field
          v-model="birthday"
          label="生日"
          type="date"
          placeholder="请选择生日"
        />
        
        <van-field name="gender" label="性别">
          <template #input>
            <van-radio-group v-model="gender" direction="horizontal">
              <van-radio name="male">男</van-radio>
              <van-radio name="female">女</van-radio>
            </van-radio-group>
          </template>
        </van-field>
      </div>

      <div class="submit-section">
        <van-button type="primary" native-type="submit" block :loading="loading">
          保存
        </van-button>
      </div>
    </van-form>
  </div>
</template>

<style lang="scss" scoped>
.profile-edit-page {
  min-height: 100vh;
  background: #f7f8fa;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 32px 16px;
  background: #fff;
  margin-bottom: 16px;
}

.avatar-wrapper {
  position: relative;
  cursor: pointer;
}

.avatar {
  width: 80px;
  height: 80px;
  border: 3px solid #f5f5f5;
}

.avatar-edit {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 24px;
  height: 24px;
  background: #1989fa;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 14px;
}

.avatar-tips {
  margin-top: 12px;
  font-size: 12px;
  color: #969799;
}

.form-section {
  background: #fff;
  padding: 0 16px;
}

.submit-section {
  padding: 16px;
  margin-top: 16px;
  
  .van-button {
    border-radius: 24px;
  }
}
</style>
