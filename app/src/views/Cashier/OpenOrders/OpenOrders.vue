<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useOrderStore, type Order } from '@/stores/orderStore'
import { useShiftStore } from '@/stores/shiftStore'
import {
  Clock,
  User as UserIcon,
  ChevronRight,
  ArrowLeft,
  Banknote,
  CreditCard,
  Smartphone,
  Plus,
  Loader2,
  CheckCircle2,
  AlertCircle,
} from 'lucide-vue-next'

import OrderAsideDetails from '../components/OrderAsideDetails.vue'

const orderStore = useOrderStore()
const shiftStore = useShiftStore()
const searchQuery = ref('')
const showDetails = ref(false)
const selectedOrderId = ref<number | null>(null)

onMounted(() => {
  refreshOrders()
})

const refreshOrders = () => {
  if (shiftStore.currentShift) {
    orderStore.fetchOpenOrders(shiftStore.currentShift.id)
  }
}

const filteredOrders = computed(() => {
  if (!searchQuery.value) return orderStore.openOrders
  return orderStore.openOrders.filter(
    (o) =>
      o.customer_name?.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      o.id.toString().includes(searchQuery.value),
  )
})

const formatCurrency = (value: number) => {
  return new Intl.NumberFormat('es-MX', { style: 'currency', currency: 'MXN' }).format(value)
}

const handleOrderClick = (order: Order) => {
  selectedOrderId.value = order.id
  showDetails.value = true
}
</script>

<template>
  <div class="h-full flex flex-col bg-slate-50 overflow-hidden">
    <!-- Header -->
    <div class="p-8 pb-4">
      <div class="flex flex-col md:flex-row md:items-center justify-between gap-6 mb-8">
        <div>
          <h2 class="text-3xl font-black text-slate-900 tracking-tight italic uppercase">
            Órdenes Abiertas
          </h2>
          <p class="text-slate-400 font-medium italic">Ordenes con pagos pendientes</p>
        </div>
        <div class="relative group">
          <input
            v-model="searchQuery"
            type="text"
            placeholder="Buscar orden..."
            class="pl-12 pr-4 py-3 bg-white border border-slate-200 rounded-2xl focus:outline-none focus:ring-4 focus:ring-orange-500/10 focus:border-orange-500 transition-all font-medium text-slate-700 shadow-sm min-w-[300px]"
          />
        </div>
      </div>
    </div>

    <!-- List -->
    <div class="flex-1 overflow-y-auto px-8 pb-8">
      <div
        v-if="filteredOrders.length === 0"
        class="h-full flex flex-col items-center justify-center text-slate-300 gap-6"
      >
        <div class="w-24 h-24 bg-slate-100 rounded-full flex items-center justify-center">
          <Clock class="w-12 h-12" />
        </div>
        <p class="text-xl font-bold text-slate-500">No hay órdenes abiertas</p>
      </div>

      <div v-else class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4">
        <div
          v-for="order in filteredOrders"
          :key="order.id"
          @click="handleOrderClick(order)"
          class="bg-white p-6 rounded-[2rem] border-2 border-slate-100 hover:border-orange-200 shadow-sm hover:shadow-xl transition-all cursor-pointer group"
        >
          <div class="flex justify-between items-start mb-4">
            <div class="flex items-center gap-3">
              <div
                class="w-10 h-10 bg-orange-100 text-orange-600 rounded-xl flex items-center justify-center font-black"
              >
                #{{ order.id }}
              </div>
              <h4 class="font-black text-slate-900 group-hover:text-orange-500 transition-colors">
                {{ order.customer_name || 'Sin Nombre' }}
              </h4>
            </div>
            <div
              class="bg-orange-50 text-orange-600 px-3 py-1 rounded-full text-[10px] font-black uppercase tracking-widest"
            >
              Pendiente
            </div>
          </div>

          <div class="space-y-2">
            <div class="flex justify-between text-xs font-bold text-slate-400 uppercase">
              <span>Total</span>
              <span>Pagado</span>
            </div>
            <div class="flex justify-between items-end">
              <span class="text-lg font-black text-slate-900">{{
                formatCurrency(order.total)
              }}</span>
              <span class="text-emerald-500 font-black">{{
                formatCurrency(order.paid_amount || 0)
              }}</span>
            </div>
            <!-- Progress Bar -->
            <div class="w-full h-2 bg-slate-100 rounded-full overflow-hidden">
              <div
                class="h-full bg-emerald-500 transition-all duration-500"
                :style="{ width: ((order.paid_amount || 0) / order.total) * 100 + '%' }"
              ></div>
            </div>
            <div class="text-right">
              <span class="text-[10px] font-black text-orange-500 uppercase tracking-widest italic">
                Pendiente {{ formatCurrency(order.total - (order.paid_amount || 0)) }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Details Overlay -->
    <OrderAsideDetails
      :open="showDetails"
      :order-id="selectedOrderId"
      @close="showDetails = false"
      @updated="refreshOrders"
    />
  </div>
</template>
