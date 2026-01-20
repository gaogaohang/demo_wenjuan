import { onMounted } from 'vue'

export function useBluetoothLowEnergy() {
  const initBluetooth = () => {
    console.log('Bluetooth initialization triggered')
  }

  return {
    initBluetooth
  }
}
