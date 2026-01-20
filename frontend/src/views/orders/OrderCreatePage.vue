<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { mockOrderService, type OrderItem } from '@/services/mock'
import { showFailToast, showSuccessToast } from 'vant'

const router = useRouter()

const title = ref('')
const description = ref('')
const note = ref('')
const orderType = ref('other')
const location = ref('')
const items = ref<OrderItem[]>([{ id: 0, name: '', description: '', quantity: 1, unitPrice: 0, totalPrice: 0 }])
const loading = ref(false)

const addItem = () => {
  items.value.push({
    id: items.value.length,
    name: '',
    description: '',
    quantity: 1,
    unitPrice: 0,
    totalPrice: 0
  })
}

const removeItem = (index: number) => {
  if (items.value.length > 1) {
    items.value.splice(index, 1)
  }
}

const calculateTotal = () => {
  return items.value.reduce((sum, item) => sum + item.totalPrice, 0)
}

const handleCreate = async () => {
  if (!title.value) {
    showFailToast('请输入订单标题')
    return
  }
  
  const validItems = items.value.filter(item => item.name && item.quantity > 0)
  if (validItems.length === 0) {
    showFailToast('请至少添加一个订单项')
    return
  }

  loading.value = true
  try {
    await mockOrderService.createOrder({
      title: title.value,
      description: description.value,
      type: orderType.value,
      note: note.value,
      location: location.value,
      items: validItems,
      estimatedTime: new Date(Date.now() + 86400000).toISOString()
    })
    showSuccessToast('订单创建成功')
    router.back()
  } catch (error) {
    showFailToast('创建失败')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="order-create-page">
    <van-form @submit="handleCreate">
      <div class="form-section">
        <div class="section-title">订单信息</div>
        
        <van-field
          v-model="title"
          label="标题"
          placeholder="请输入订单标题"
          :rules="[{ required: true, message: '请输入订单标题' }]"
        />
        
        <van-field
          v-model="description"
          label="描述"
          type="textarea"
          rows="2"
          placeholder="请输入订单描述（选填）"
        />
        
        <van-field name="type" label="类型">
          <template #input>
            <van-radio-group v-model="orderType" direction="horizontal">
              <van-radio name="food">餐饮</van-radio>
              <van-radio name="shopping">购物</van-radio>
              <van-radio name="other">其他</van-radio>
            </van-radio-group>
          </template>
        </van-field>
        
        <van-field
          v-model="location"
          label="地址"
          placeholder="请输入地址（选填）"
          left-icon="location-o"
        />
        
        <van-field
          v-model="note"
          label="备注"
          type="textarea"
          rows="2"
          placeholder="请输入备注（选填）"
        />
      </div>

      <div class="form-section">
        <div class="section-header">
          <div class="section-title">订单项目</div>
          <van-button type="primary" size="small" plain @click="addItem">添加项目</van-button>
        </div>
        
        <div class="item-list">
          <div class="item-card" v-for="(item, index) in items" :key="index">
            <van-row :gutter="12">
              <van-col :span="14">
                <van-field
                  v-model="item.name"
                  placeholder="项目名称"
                  size="small"
                />
              </van-col>
              <van-col :span="4">
                <van-field
                  v-model.number="item.quantity"
                  type="number"
                  placeholder="数量"
                  size="small"
                />
              </van-col>
              <van-col :span="4">
                <van-field
                  v-model.number="item.unitPrice"
                  type="number"
                  placeholder="单价"
                  size="small"
                />
              </van-col>
              <van-col :span="2">
                <van-icon name="close" @click="removeItem(index)" />
              </van-col>
            </van-row>
          </div>
        </div>
        
        <div class="total-row">
          <span>合计</span>
          <span class="total-price">¥{{ calculateTotal().toFixed(2) }}</span>
        </div>
      </div>

      <div class="submit-section">
        <van-button type="primary" native-type="submit" block :loading="loading">
          创建订单
        </van-button>
      </div>
    </van-form>
  </div>
</template>

<style lang="scss" scoped>
.order-create-page {
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

.item-list {
  margin-bottom: 12px;
}

.item-card {
  padding: 12px;
  background: #f7f8fa;
  border-radius: 8px;
  margin-bottom: 8px;
}

.total-row {
  display: flex;
  justify-content: flex-end;
  padding-top: 12px;
  border-top: 1px solid #f5f5f5;
  gap: 16px;
  font-size: 16px;
}

.total-price {
  font-size: 20px;
  font-weight: 600;
  color: #07c160;
}

.submit-section {
  padding: 16px;
  background: #fff;
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
}
</style>
