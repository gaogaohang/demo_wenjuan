<script setup lang="ts">
import { useUserStore } from '@/stores/user'
import { useBluetoothLowEnergy } from '@/hooks/useBluetoothLowEnergy'

const userStore = useUserStore()
const { initBluetooth } = useBluetoothLowEnergy()

const themeVars = {
  primaryColor: '#667eea',
  successColor: '#07c160',
  warningColor: '#ff976a',
  dangerColor: '#ee0a24'
}

const initializeApp = async () => {
  if (userStore.isLoggedIn) {
    await userStore.fetchUserProfile()
    await userStore.fetchPairInfo()
  }
  initBluetooth()
}

initializeApp()
</script>

<template>
  <van-config-provider :theme-vars="themeVars">
    <router-view />
  </van-config-provider>
</template>

<script lang="ts">
export default {
  name: 'App'
}
</script>

<style>
#app {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #323233;
  font-size: 16px;
  background-color: #f7f8fa;
}
</style>
