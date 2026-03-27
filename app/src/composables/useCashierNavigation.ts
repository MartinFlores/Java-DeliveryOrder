import { ref } from 'vue'

export type CashierView = 'home' | 'new_sale' | 'orders' | 'open_orders' | 'shift' | 'session'

const currentView = ref<CashierView>('home')

export function useCashierNavigation() {
  function navigate(view: CashierView) {
    currentView.value = view
  }

  return {
    currentView,
    navigate,
  }
}
