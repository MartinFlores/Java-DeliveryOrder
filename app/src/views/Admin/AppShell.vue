<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useAdminNavigation, type AdminView } from '@/composables/useAdminNavigation'
import { useConfigStore } from '@/stores/configStore'

import Aside from '@/views/Admin/components/Aside.vue'
import Header from '@/views/Admin/components/Header.vue'
import Dashboard from '@/views/Admin/Dashboard/Dashboard.vue'
import Apps from '@/views/Admin/Apps/Apps.vue'
import Personal from '@/views/Admin/Personal/Personal.vue'
import Cashier from '@/views/Admin/Cashier/Cashier.vue'
import Printers from '@/views/Admin/Printers/Printers.vue'
import TPremiaDashboard from '@/views/Admin/Dashboard/TPremiaDashboard.vue'
import Login from '@/views/Admin/Login/Login.vue'
import Products from '@/views/Admin/Products/Products.vue'
import ProductEdit from '@/views/Admin/Products/ProductEdit.vue'

const configStore = useConfigStore()
const { currentView } = useAdminNavigation()
const mainContent = ref<HTMLElement | null>(null)

// Cada vez que cambie la vista, reseteamos el scroll al principio
watch(currentView, () => {
  if (mainContent.value) {
    mainContent.value.scrollTop = 0
  }
})

const viewMap: Partial<Record<AdminView, any>> = {
  dashboard: Dashboard,
  apps: Apps,
  staff: Personal,
  cashier: Cashier,
  printers: Printers,
  products: Products,
  'product-edit': ProductEdit,
  'tpremia-dashboard': TPremiaDashboard,
}

const currentComponent = computed(() => viewMap[currentView.value] || Dashboard)

const asideOpen = ref(true)
</script>

<template>
  <Transition
    mode="out-in"
    enter-active-class="transition duration-300 ease-out"
    enter-from-class="opacity-0 transform scale-95"
    enter-to-class="opacity-100 transform scale-100"
    leave-active-class="transition duration-200 ease-in"
    leave-from-class="opacity-100 transform scale-100"
    leave-to-class="opacity-0 transform scale-95"
  >
    <Login v-if="!configStore.isAdminLoggedIn" />

    <div v-else class="flex h-screen w-full overflow-hidden text-slate-800">
      <!-- ASIDE -->
      <Aside :open="asideOpen" @close="asideOpen = false" />

      <!-- MAIN -->
      <div class="flex-1 flex flex-col h-full overflow-hidden">
        <!-- HEADER -->
        <Header :current-view="currentView" @toggle-aside="asideOpen = !asideOpen" />

        <!-- CONTENIDO DINÁMICO -->
        <main ref="mainContent" class="flex-1 overflow-y-auto bg-slate-50 scroll-smooth">
          <component :is="currentComponent" />
        </main>
      </div>
    </div>
  </Transition>
</template>
