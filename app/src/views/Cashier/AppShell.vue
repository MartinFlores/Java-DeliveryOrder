<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { useCashierNavigation, type CashierView } from '@/composables/useCashierNavigation'
import { useShiftStore } from '@/stores/shiftStore'
import { useUserStore } from '@/stores/userStore'

import Login from './Login/Login.vue'
import Aside from './components/Aside.vue'
import Header from './components/Header.vue'
import Home from './Home/Home.vue'
import NewSale from './NewSale/NewSale.vue'
import Orders from './Orders/Orders.vue'
import Session from './Session/Session.vue'
import ShiftDetail from './Shift/ShiftDetail.vue'
import OpenOrders from './OpenOrders/OpenOrders.vue'
import Placeholder from './components/Placeholder.vue'

const userStore = useUserStore()
const shiftStore = useShiftStore()
const { currentView } = useCashierNavigation()

const asideOpen = ref(true)
const mainContent = ref<HTMLElement | null>(null)

// Cargar sesión y turno al montar
onMounted(async () => {
  userStore.loadSession()
  if (userStore.isLoggedIn && userStore.user) {
    await shiftStore.checkActiveShift(userStore.user.id)
  }
})

const handleLoginSuccess = async (userData: any) => {
  userStore.setUser(userData)
  await shiftStore.checkActiveShift(userData.id)
}

const handleLogout = () => {
  userStore.logout()
  shiftStore.closeShift() // Opcional: ¿cerrar turno al desloguear? Generalmente no, pero el estado local sí
}

// Map de componentes para la navegación virtual
const viewMap: Record<CashierView, any> = {
  home: Home,
  new_sale: NewSale,
  orders: Orders,
  open_orders: OpenOrders,
  shift: ShiftDetail,
  session: Session,
}

const currentComponent = computed(() => viewMap[currentView.value] || Home)

// Reset scroll al cambiar de vista
watch(currentView, () => {
  if (mainContent.value) {
    mainContent.value.scrollTop = 0
  }
})
</script>

<template>
  <div class="h-screen w-full overflow-hidden text-slate-900 bg-slate-50">
    <Transition
      mode="out-in"
      enter-active-class="transition duration-300 ease-out"
      enter-from-class="opacity-0 transform scale-95"
      enter-to-class="opacity-100 transform scale-100"
      leave-active-class="transition duration-200 ease-in"
      leave-from-class="opacity-100 transform scale-100"
      leave-to-class="opacity-0 transform scale-95"
    >
      <!-- VISTA DE LOGIN -->
      <Login v-if="!userStore.isLoggedIn" @login-success="handleLoginSuccess" />

      <!-- VISTA PRINCIPAL (AppShell) -->
      <div v-else class="flex h-screen w-full overflow-hidden">
        <!-- ASIDE -->
        <Aside
          v-if="shiftStore.isShiftOpen"
          :open="asideOpen"
          @close="asideOpen = false"
          @logout="handleLogout"
        />

        <!-- MAIN CONTENT AREA -->
        <div class="flex-1 flex flex-col h-full overflow-hidden">
          <!-- HEADER -->
          <Header
            :current-view="currentView"
            :user="userStore.user"
            @toggle-aside="asideOpen = !asideOpen"
          />

          <!-- DINAMIC VIEW -->
          <main ref="mainContent" class="flex-1 overflow-y-auto scroll-smooth">
            <Transition
              mode="out-in"
              enter-active-class="transition duration-200 ease-out"
              enter-from-class="opacity-0 translate-y-4"
              enter-to-class="opacity-100 translate-y-0"
              leave-active-class="transition duration-150 ease-in"
              leave-from-class="opacity-100 translate-y-0"
              leave-to-class="opacity-0 translate-y-4"
            >
              <component :is="currentComponent" :user="userStore.user" :title="currentView" />
            </Transition>
          </main>
        </div>
      </div>
    </Transition>
  </div>
</template>

<style scoped>
.scroll-smooth {
  scroll-behavior: smooth;
}
</style>
