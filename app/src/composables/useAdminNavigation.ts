import { ref } from 'vue'

export type AdminView =
  | 'dashboard'
  | 'activity'
  | 'alerts'
  | 'orders'
  | 'cashier'
  | 'kitchen'
  | 'inventory'
  | 'products'
  | 'sales'
  | 'staff'
  | 'apps'
  | 'device_status'
  | 'network'
  | 'printers'
  | 'security'
  | 'tpremia-dashboard'
  | 'product-edit'

const currentView = ref<AdminView>('dashboard')

export function useAdminNavigation() {
  function navigate(view: AdminView) {
    currentView.value = view
  }

  return {
    currentView,
    navigate,
  }
}
