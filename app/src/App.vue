<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import SetupWizard from '@/views/Wizard/SetupWizard.vue'
import AdminApp from '@/views/Admin/AppShell.vue'
import KitchenApp from '@/views/Kitchen/KitchenApp.vue'
import CashierApp from '@/views/Cashier/AppShell.vue'
import WaiterApp from '@/views/Waiter/WaiterApp.vue'
import GlobalLoader from '@/components/GlobalLoader.vue'
import GlobalToast from '@/components/GlobalToast.vue'

type AppMode = 'loading' | 'wizard' | 'admin' | 'kitchen' | 'cashier' | 'waiter'

const appMode = ref<AppMode>('loading')

function detectModeFromURL(): AppMode {
  const params = window.location.search

  if (params.includes('kitchen')) return 'kitchen'
  if (params.includes('cashier')) return 'cashier'
  if (params.includes('waiter')) return 'waiter'
  if (params.includes('admin')) return 'admin'
  if (params.includes('wizard')) return 'wizard'

  return 'admin'
}

onMounted(async () => {
  const localConfigured = localStorage.getItem('pos_config_status') === 'true'

  if (localConfigured) {
    appMode.value = detectModeFromURL()
    return
  }

  // localStorage vacío: verificar con el backend (ej. recarga antes de completar wizard)
  try {
    const res = await fetch('/api/v1/config/status')
    const data = await res.json()
    if (data.configured) {
      localStorage.setItem('pos_config_status', 'true')
      appMode.value = detectModeFromURL()
    } else {
      appMode.value = 'wizard'
    }
  } catch {
    appMode.value = 'wizard'
  }
})

const currentComponent = computed(() => {
  switch (appMode.value) {
    case 'wizard':
      return SetupWizard
    case 'admin':
      return AdminApp
    case 'kitchen':
      return KitchenApp
    case 'cashier':
      return CashierApp
    case 'waiter':
      return WaiterApp
    default:
      return null
  }
})
</script>

<template>
  <component :is="currentComponent" />
  <GlobalLoader />
  <GlobalToast />
</template>
