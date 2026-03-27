<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useOrderStore, type Order } from '@/stores/orderStore'
import { useShiftStore } from '@/stores/shiftStore'
import {
  Search,
  ChevronRight,
  Calendar,
  Clock,
  User as UserIcon,
  CreditCard,
  Banknote,
  Smartphone,
  ArrowLeft,
  Loader2,
  FileText,
  Printer,
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
    orderStore.fetchOrdersByShift(shiftStore.currentShift.id)
  }
}

const formatCurrency = (value: number) => {
  return new Intl.NumberFormat('es-MX', { style: 'currency', currency: 'MXN' }).format(value)
}

const formatDate = (timestamp: number) => {
  return new Intl.DateTimeFormat('es-MX', {
    hour: '2-digit',
    minute: '2-digit',
    hour12: true,
  }).format(new Date(timestamp))
}

const getPaymentIcon = (method: string) => {
  switch (method) {
    case 'Efectivo':
      return Banknote
    case 'Tarjeta':
      return CreditCard
    case 'Transferencia':
      return Smartphone
    default:
      return Banknote
  }
}

const filteredOrders = computed(() => {
  let list = orderStore.orders
  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase()
    list = list.filter(
      (o) => o.customer_name?.toLowerCase().includes(q) || o.id.toString().includes(q),
    )
  }
  return list
})

const handleViewDetails = (order: Order) => {
  selectedOrderId.value = order.id
  showDetails.value = true
}
</script>

<template>
  <div class="h-full flex flex-col bg-slate-50 overflow-hidden">
    <!-- Header Section -->
    <div class="p-8 pb-4">
      <div class="flex flex-col md:flex-row md:items-center justify-between gap-6 mb-8">
        <div>
          <h2 class="text-3xl font-black text-slate-900 tracking-tight italic uppercase">
            Órdenes del Día
          </h2>
          <p class="text-slate-400 font-medium italic">Gestiona las ventas de tu turno activo</p>
        </div>

        <div class="flex gap-3">
          <div class="relative group">
            <Search
              class="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 group-focus-within:text-orange-500 transition-colors"
            />
            <input
              v-model="searchQuery"
              type="text"
              placeholder="Buscar por cliente o folio..."
              class="pl-12 pr-4 py-3 bg-white border border-slate-200 rounded-2xl focus:outline-none focus:ring-4 focus:ring-orange-500/10 focus:border-orange-500 transition-all font-medium text-slate-700 shadow-sm min-w-[300px]"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- Orders List -->
    <div class="flex-1 overflow-y-auto px-8 pb-8">
      <div
        v-if="orderStore.isLoading"
        class="h-full flex flex-col items-center justify-center text-slate-400 gap-4"
      >
        <Loader2 class="w-12 h-12 animate-spin text-orange-500" />
        <span class="font-bold uppercase tracking-widest text-xs">Cargando historial...</span>
      </div>

      <div
        v-else-if="filteredOrders.length === 0"
        class="h-full flex flex-col items-center justify-center text-slate-300 gap-6"
      >
        <div class="w-24 h-24 bg-slate-100 rounded-full flex items-center justify-center">
          <FileText class="w-12 h-12" />
        </div>
        <div class="text-center">
          <h4 class="text-xl font-bold text-slate-500 mb-1">Sin órdenes registradas</h4>
          <p class="text-slate-400">Las ventas que realices hoy aparecerán aquí.</p>
        </div>
      </div>

      <div v-else class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4">
        <div
          v-for="order in filteredOrders"
          :key="order.id"
          @click="handleViewDetails(order)"
          class="bg-white p-6 rounded-[2rem] border border-slate-100 shadow-sm hover:shadow-xl hover:shadow-slate-200/50 hover:-translate-y-1 transition-all cursor-pointer group flex flex-col gap-4"
        >
          <div class="flex justify-between items-start">
            <div class="flex items-center gap-3">
              <div
                class="w-12 h-12 bg-slate-900 text-white rounded-2xl flex items-center justify-center font-black"
              >
                #{{ order.id }}
              </div>
              <div>
                <h4
                  class="font-black text-slate-900 group-hover:text-orange-500 transition-colors uppercase italic"
                >
                  {{ order.customer_name || 'Venta de Mostrador' }}
                </h4>
                <div
                  class="flex items-center gap-2 text-[10px] font-black uppercase tracking-wider text-slate-400"
                >
                  <Clock class="w-3 h-3" />
                  {{ formatDate(order.created_at) }}
                </div>
              </div>
            </div>
            <div class="flex flex-col items-end gap-2">
              <div
                v-if="order.status === 'open'"
                class="bg-orange-500 text-white px-3 py-1 rounded-full text-[10px] font-black uppercase tracking-widest animate-pulse"
              >
                Pendiente
              </div>
              <div
                v-else
                class="bg-emerald-500 text-white px-3 py-1 rounded-full text-[10px] font-black uppercase tracking-widest"
              >
                Liquidada
              </div>
              <div class="bg-slate-50 p-2 rounded-xl text-slate-400">
                <ChevronRight class="w-5 h-5 group-hover:translate-x-1 transition-transform" />
              </div>
            </div>
          </div>

          <div class="flex items-center justify-between pt-4 border-t border-slate-50">
            <div v-if="order.status !== 'open'" class="flex items-center gap-2">
              <component
                :is="
                  order.payment_method === 'Pendiente'
                    ? CreditCard
                    : getPaymentIcon(order.payment_method)
                "
                class="w-4 h-4 text-emerald-500"
              />
              <span class="text-xs font-bold text-slate-500 uppercase tracking-widest">{{
                order.payment_method === 'Pendiente' ? 'Mixto' : order.payment_method || 'Pagado'
              }}</span>
            </div>
            <div v-else class="flex items-center gap-2">
              <Clock class="w-4 h-4 text-orange-500" />
              <span class="text-xs font-black text-orange-500 uppercase italic"
                >Cuenta Abierta</span
              >
            </div>
            <span class="text-xl font-black text-slate-900 italic">{{
              formatCurrency(order.total)
            }}</span>
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
