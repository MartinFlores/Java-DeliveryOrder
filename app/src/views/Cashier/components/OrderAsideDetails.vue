<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useOrderStore } from '@/stores/orderStore'
import { useShiftStore } from '@/stores/shiftStore'
import {
  X,
  Clock,
  User as UserIcon,
  Calendar,
  ChevronRight,
  ArrowLeft,
  Banknote,
  CreditCard,
  Smartphone,
  Plus,
  Loader2,
  CheckCircle2,
  Printer,
  ChevronDown,
} from 'lucide-vue-next'

const props = defineProps<{
  open: boolean
  orderId: number | null
}>()

const emit = defineEmits(['close', 'updated'])

const orderStore = useOrderStore()
const shiftStore = useShiftStore()

// Payment State
const showPaymentModal = ref(false)
const selectedMethod = ref('Efectivo')
const payments = ref<{ payment_method: string; amount: number }[]>([])
const amountToAdd = ref(0)
const isSubmitting = ref(false)

const paymentMethods = [
  { id: 'Efectivo', icon: Banknote },
  { id: 'Tarjeta', icon: CreditCard },
  { id: 'Transferencia', icon: Smartphone },
]

// Fetch details when orderId changes
watch(
  () => props.orderId,
  async (newId) => {
    if (newId) {
      await orderStore.fetchOrderDetails(newId)
    }
  },
)

const formatCurrency = (value: number) => {
  return new Intl.NumberFormat('es-MX', { style: 'currency', currency: 'MXN' }).format(value)
}

const formatDate = (timestamp: number) => {
  if (!timestamp) return ''
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

const calculatedPaidAmount = computed(() => {
  if (!orderStore.selectedOrder) return 0
  const paymentsSum =
    orderStore.selectedOrder.payments?.reduce((sum, p) => sum + (p.amount || 0), 0) || 0
  return Math.max(paymentsSum, orderStore.selectedOrder.paid_amount || 0)
})

// Abonos Calculation
const totalAbonar = computed(() => payments.value.reduce((sum, p) => sum + p.amount, 0))
const currentRemaining = computed(() => {
  if (!orderStore.selectedOrder) return 0
  const balance = (orderStore.selectedOrder.total || 0) - calculatedPaidAmount.value
  return Math.max(0, balance - totalAbonar.value)
})

const openAddPayment = () => {
  if (!orderStore.selectedOrder) return
  payments.value = []
  const remaining = orderStore.selectedOrder.total - calculatedPaidAmount.value
  amountToAdd.value = Number(remaining.toFixed(2))
  selectedMethod.value = 'Efectivo'
  showPaymentModal.value = true
}

const addPaymentToList = () => {
  if (amountToAdd.value <= 0) return
  const amount = Math.min(amountToAdd.value, currentRemaining.value + totalAbonar.value)
  if (amount <= 0) return

  payments.value.push({
    payment_method: selectedMethod.value,
    amount: amount,
  })
  amountToAdd.value = Number(currentRemaining.value.toFixed(2))
}

const removePaymentFromList = (index: number) => {
  payments.value.splice(index, 1)
  amountToAdd.value = Number(currentRemaining.value.toFixed(2))
}

const handleAddPayment = async () => {
  if (!orderStore.selectedOrder || payments.value.length === 0) return

  isSubmitting.value = true
  const result = await orderStore.addPayment(
    orderStore.selectedOrder.id,
    null,
    null,
    payments.value,
  )
  isSubmitting.value = false

  if (result.status === 'ok') {
    showPaymentModal.value = false
    payments.value = []

    // Refresh the lists in the store first
    if (shiftStore.currentShift) {
      await Promise.all([
        orderStore.fetchOpenOrders(shiftStore.currentShift.id),
        orderStore.fetchOrdersByShift(shiftStore.currentShift.id),
      ])
    }

    // Then refresh details
    await orderStore.fetchOrderDetails(orderStore.selectedOrder.id)

    // Notify parent that data has changed
    emit('updated')
  } else {
    alert('Error al registrar pago: ' + result.message)
  }
}

const handlePrint = () => {
  alert('Impresión de ticket próximamente...')
}
</script>

<template>
  <Transition
    enter-active-class="transition duration-300 ease-out"
    enter-from-class="opacity-0 translate-x-full"
    enter-to-class="opacity-100 translate-x-0"
    leave-active-class="transition duration-200 ease-in"
    leave-from-class="opacity-100 translate-x-0"
    leave-to-class="opacity-0 translate-x-full"
  >
    <div v-if="open && orderStore.selectedOrder" class="fixed inset-0 z-[120] flex justify-end">
      <div class="absolute inset-0 bg-slate-900/40 backdrop-blur-sm" @click="emit('close')"></div>
      <div class="bg-white w-full max-w-lg relative z-10 shadow-2xl flex flex-col h-full">
        <!-- Header -->
        <div class="p-8 border-b border-slate-50 flex items-center justify-between">
          <button
            @click="emit('close')"
            class="flex items-center gap-2 text-slate-400 hover:text-slate-900 font-bold transition-colors"
          >
            <ArrowLeft class="w-5 h-5" />
            Volver
          </button>
          <div class="text-right">
            <span class="text-xs font-black text-slate-400 uppercase tracking-widest"
              >Orden #{{ orderStore.selectedOrder.id }}</span
            >
            <h3 class="text-xl font-black text-slate-900 tracking-tight leading-none italic">
              Detalles de Venta
            </h3>
          </div>
        </div>

        <!-- Summary Info (Combined Design) -->
        <div class="p-8 bg-slate-50 flex flex-col gap-6">
          <div class="flex items-center gap-4">
            <div
              class="w-16 h-16 bg-white rounded-[1.5rem] shadow-sm flex items-center justify-center"
            >
              <UserIcon class="w-8 h-8 text-slate-900" />
            </div>
            <div class="flex-1">
              <p class="text-xs font-black text-slate-400 uppercase tracking-widest mb-1">
                Cliente
              </p>
              <h4 class="text-2xl font-black text-slate-900 tracking-tight leading-none">
                {{ orderStore.selectedOrder.customer_name || 'Venta de Mostrador' }}
              </h4>
            </div>
          </div>

          <!-- Financial Status Card -->
          <div class="bg-slate-900 p-6 rounded-[2rem] text-white shadow-xl">
            <div class="flex justify-between items-center mb-4">
              <div>
                <p class="text-[10px] font-black text-slate-400 uppercase tracking-widest mb-1">
                  Total Orden
                </p>
                <span class="text-3xl font-black italic text-white">{{
                  formatCurrency(orderStore.selectedOrder.total)
                }}</span>
              </div>
              <div class="text-right">
                <p class="text-[10px] font-black text-emerald-400 uppercase mb-1">Pagado</p>
                <p class="text-xl font-black text-emerald-500">
                  {{ formatCurrency(calculatedPaidAmount) }}
                </p>
              </div>
            </div>

            <div
              v-if="orderStore.selectedOrder.status === 'open'"
              class="pt-4 border-t border-slate-800 flex justify-between items-center"
            >
              <div>
                <p class="text-[10px] font-black text-orange-400 uppercase tracking-widest mb-1">
                  Saldo Pendiente
                </p>
                <span class="text-2xl font-black text-orange-500 italic">
                  {{ formatCurrency(orderStore.selectedOrder.total - calculatedPaidAmount) }}
                </span>
              </div>
              <div class="bg-orange-500/10 px-3 py-1 rounded-full border border-orange-500/30">
                <span
                  class="text-[10px] font-black text-orange-500 uppercase tracking-widest animate-pulse"
                  >Abierta</span
                >
              </div>
            </div>
            <div v-else class="pt-4 border-t border-slate-800 flex justify-between items-center">
              <span class="text-xs font-bold text-slate-400 uppercase">Estado</span>
              <div class="flex items-center gap-2 text-emerald-500">
                <CheckCircle2 class="w-4 h-4" />
                <span class="text-xs font-black uppercase tracking-widest">Liquidada</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Items & Payments Tab-like view or sections -->
        <div class="flex-1 overflow-y-auto p-8 space-y-8">
          <!-- Products -->
          <div>
            <h4 class="text-xs font-black text-slate-400 uppercase tracking-widest mb-4">
              Productos
            </h4>
            <div class="space-y-3">
              <div
                v-for="item in orderStore.selectedOrder.items"
                :key="item.id"
                class="flex items-center gap-4 p-4 rounded-2xl border border-slate-50 hover:bg-slate-50 transition-colors"
              >
                <div
                  class="w-10 h-10 bg-slate-100 rounded-xl flex items-center justify-center text-lg"
                >
                  {{ item.product_image || '🍽️' }}
                </div>
                <div class="flex-1 min-w-0">
                  <p class="font-bold text-slate-900 truncate leading-none mb-1">
                    {{ item.product_name }}
                  </p>
                  <p class="text-[10px] font-bold text-slate-400 uppercase tracking-widest">
                    {{ item.quantity }} x {{ formatCurrency(item.price) }}
                  </p>
                </div>
                <div class="text-right">
                  <span class="font-black text-slate-900">{{ formatCurrency(item.subtotal) }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- Payments History -->
          <div v-if="orderStore.selectedOrder.payments?.length">
            <h4 class="text-xs font-black text-slate-400 uppercase tracking-widest mb-4">
              Historial de Pagos
            </h4>
            <div class="space-y-3">
              <div
                v-for="pay in orderStore.selectedOrder.payments"
                :key="pay.id"
                class="flex items-center justify-between p-4 border border-slate-50 rounded-2xl bg-white shadow-sm"
              >
                <div class="flex items-center gap-3">
                  <div
                    class="w-8 h-8 rounded-lg bg-emerald-50 flex items-center justify-center text-emerald-500"
                  >
                    <component :is="getPaymentIcon(pay.payment_method)" class="w-4 h-4" />
                  </div>
                  <div>
                    <p class="text-[10px] font-bold text-slate-400 uppercase leading-none mb-1">
                      Método
                    </p>
                    <p class="font-bold text-slate-700 text-sm">{{ pay.payment_method }}</p>
                  </div>
                </div>
                <span class="font-black text-slate-900">{{ formatCurrency(pay.amount) }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Action Footer -->
        <div class="p-8 border-t border-slate-100 bg-white space-y-3">
          <div class="flex gap-3">
            <button
              v-if="orderStore.selectedOrder.status === 'open'"
              @click="openAddPayment"
              class="flex-1 py-5 bg-orange-500 text-white rounded-[2rem] font-black text-lg flex items-center justify-center gap-3 shadow-xl shadow-orange-500/20 active:scale-95 transition-all"
            >
              <Plus class="w-6 h-6" />
              Abonar / Liquidar
            </button>
            <button
              @click="handlePrint"
              :class="[
                'py-5 px-6 bg-slate-900 text-white rounded-[2rem] font-black flex items-center justify-center transition-all active:scale-95 shadow-xl shadow-slate-900/20',
                orderStore.selectedOrder.status === 'open' ? 'w-20' : 'w-full text-lg gap-3',
              ]"
            >
              <Printer class="w-6 h-6" />
              <span v-if="orderStore.selectedOrder.status !== 'open'">Imprimir Ticket</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  </Transition>

  <!-- Payment Modal (Same as OpenOrders) -->
  <Transition
    enter-active-class="transition duration-300 ease-out"
    enter-from-class="opacity-0 translate-y-12"
    enter-to-class="opacity-100 translate-y-0"
    leave-active-class="transition duration-200 ease-in"
    leave-from-class="opacity-100 translate-y-0"
    leave-to-class="opacity-0 translate-y-12"
  >
    <div v-if="showPaymentModal" class="fixed inset-0 z-[130] flex items-center justify-center p-6">
      <div
        class="absolute inset-0 bg-slate-900/60 backdrop-blur-md"
        @click="showPaymentModal = false"
      ></div>
      <div
        class="bg-white w-full max-w-xl rounded-[2.5rem] shadow-2xl relative z-10 overflow-hidden border border-slate-100 flex flex-col max-h-[90vh]"
      >
        <!-- Header -->
        <div class="p-8 border-b border-slate-50 flex items-center justify-between">
          <div>
            <h3 class="text-2xl font-black text-slate-900 tracking-tight italic uppercase">
              Registrar Abonos
            </h3>
            <p class="text-slate-400 font-medium italic">Soporte para múltiples formas de pago</p>
          </div>
          <button
            @click="showPaymentModal = false"
            class="w-12 h-12 bg-slate-50 rounded-2xl flex items-center justify-center text-slate-400 hover:text-slate-600 transition-colors"
          >
            <X class="w-6 h-6" />
          </button>
        </div>

        <!-- Content -->
        <div class="flex-1 overflow-y-auto p-8 space-y-8">
          <!-- Balance Summary -->
          <div
            class="bg-slate-900 rounded-[2rem] p-8 text-white relative overflow-hidden shadow-2xl"
          >
            <div
              class="absolute -right-6 -bottom-6 w-32 h-32 bg-orange-500/20 rounded-full blur-3xl"
            ></div>
            <div class="relative z-10 grid grid-cols-2 gap-8">
              <div>
                <p class="text-slate-400 font-black uppercase tracking-widest text-[9px] mb-1">
                  Saldo Actual
                </p>
                <h4 class="text-2xl font-black tracking-tight italic">
                  {{
                    formatCurrency((orderStore.selectedOrder?.total || 0) - calculatedPaidAmount)
                  }}
                </h4>
              </div>
              <div class="text-right border-l border-white/10 pl-8">
                <p
                  class="text-orange-400 font-black uppercase tracking-widest text-[9px] mb-1 italic"
                >
                  Abonando Ahora
                </p>
                <h4 class="text-3xl font-black tracking-tighter text-orange-500">
                  {{ formatCurrency(totalAbonar) }}
                </h4>
              </div>
            </div>
          </div>

          <!-- Final Balance Preview -->
          <div class="flex items-center justify-between px-4 pb-2 border-b-2 border-slate-100">
            <span class="text-[10px] font-black text-slate-400 uppercase tracking-widest"
              >Saldo Final tras abonos:</span
            >
            <span
              class="text-lg font-black text-slate-900 italic"
              :class="{ 'text-emerald-600': currentRemaining <= 0.01 }"
            >
              {{ formatCurrency(currentRemaining) }}
            </span>
          </div>

          <!-- Add Abono Form -->
          <div
            v-if="currentRemaining > 0.01"
            class="bg-slate-50 p-6 rounded-[2rem] border border-slate-100 space-y-5"
          >
            <div class="flex justify-between items-center px-1">
              <span class="text-[10px] font-black text-slate-400 uppercase tracking-widest"
                >Nuevo Abono</span
              >
              <span class="text-xs font-black text-orange-600 italic"
                >+{{ formatCurrency(amountToAdd) }}</span
              >
            </div>

            <div class="grid grid-cols-3 gap-2">
              <button
                v-for="m in paymentMethods"
                :key="m.id"
                @click="selectedMethod = m.id"
                :class="[
                  'flex flex-col items-center gap-1.5 p-3 rounded-2xl border transition-all',
                  selectedMethod === m.id
                    ? 'bg-orange-500 text-white border-orange-500 shadow-lg shadow-orange-500/20'
                    : 'bg-white border-slate-200 text-slate-500 hover:border-orange-200',
                ]"
              >
                <component :is="m.icon" class="w-5 h-5" />
                <span class="text-[9px] font-black uppercase tracking-tighter">{{ m.id }}</span>
              </button>
            </div>

            <div class="flex gap-2">
              <div class="relative flex-1">
                <span
                  class="absolute left-4 top-1/2 -translate-y-1/2 font-black text-slate-400 text-sm"
                  >$</span
                >
                <input
                  v-model.number="amountToAdd"
                  type="number"
                  class="w-full pl-8 pr-4 py-4 bg-white border border-slate-200 rounded-2xl font-black text-slate-700 focus:ring-4 focus:ring-orange-500/10 focus:border-orange-500"
                />
              </div>
              <button
                @click="addPaymentToList"
                class="px-8 bg-slate-900 text-white font-black rounded-2xl hover:bg-orange-500 transition-all flex items-center justify-center shadow-lg active:scale-90"
              >
                <Plus class="w-6 h-6" />
              </button>
            </div>
          </div>

          <!-- Applied Abonos List -->
          <div v-if="payments.length > 0" class="space-y-4">
            <label
              class="block text-[10px] font-black text-slate-400 uppercase tracking-widest ml-1 italic"
              >Lista de Abonos a Procesar</label
            >
            <div class="space-y-2">
              <div
                v-for="(p, idx) in payments"
                :key="idx"
                class="flex items-center justify-between p-5 bg-white border border-slate-100 rounded-2xl shadow-sm hover:border-orange-100 transition-colors animate-in slide-in-from-left-4"
              >
                <div class="flex items-center gap-4">
                  <div
                    class="w-10 h-10 rounded-xl bg-slate-50 flex items-center justify-center text-slate-400"
                  >
                    <Banknote
                      v-if="p.payment_method === 'Efectivo'"
                      class="w-5 h-5 text-emerald-500"
                    />
                    <CreditCard
                      v-else-if="p.payment_method === 'Tarjeta'"
                      class="w-5 h-5 text-blue-500"
                    />
                    <Smartphone v-else class="w-5 h-5 text-purple-500" />
                  </div>
                  <div>
                    <span
                      class="text-xs font-black text-slate-400 uppercase tracking-tighter block leading-none mb-1"
                      >Vía</span
                    >
                    <span class="font-black text-slate-800">{{ p.payment_method }}</span>
                  </div>
                </div>
                <div class="flex items-center gap-6">
                  <span class="text-lg font-black text-slate-900 italic tracking-tighter">{{
                    formatCurrency(p.amount)
                  }}</span>
                  <button
                    @click="removePaymentFromList(idx)"
                    class="w-8 h-8 rounded-lg hover:bg-red-50 text-red-300 hover:text-red-500 transition-colors flex items-center justify-center"
                  >
                    <X class="w-5 h-5" />
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Footer -->
        <div class="p-8 bg-slate-50 border-t border-slate-100">
          <button
            @click="handleAddPayment"
            :disabled="isSubmitting || payments.length === 0"
            class="w-full py-5 bg-orange-500 hover:bg-orange-600 disabled:opacity-50 text-white rounded-[20rem] font-black text-lg shadow-xl shadow-orange-500/20 active:scale-95 transition-all flex items-center justify-center gap-3"
          >
            <Loader2 v-if="isSubmitting" class="w-6 h-6 animate-spin" />
            <CheckCircle2 v-else class="w-6 h-6" />
            <span>Confirmar y Aplicar {{ payments.length }} Abono(s)</span>
          </button>
        </div>
      </div>
    </div>
  </Transition>
</template>
