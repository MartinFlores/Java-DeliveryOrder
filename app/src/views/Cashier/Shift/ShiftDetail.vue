<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useShiftStore } from '@/stores/shiftStore'
import {
  Wallet,
  TrendingUp,
  TrendingDown,
  Clock,
  Calendar,
  CreditCard,
  Banknote,
  Smartphone,
  ChevronRight,
  Loader2,
  PieChart,
  ArrowRight,
  User as UserIcon,
  LogOut,
} from 'lucide-vue-next'
import { useCashierNavigation } from '@/composables/useCashierNavigation'

import { useUserStore } from '@/stores/userStore'

const shiftStore = useShiftStore()
const userStore = useUserStore()
const { navigate } = useCashierNavigation()

const stats = ref<any>(null)
const isLoading = ref(true)
const showCloseConfirmation = ref(false)
const showLogoutPrompt = ref(false)
const closingAmount = ref<number>(0)

onMounted(async () => {
  if (shiftStore.currentShift) {
    stats.value = await shiftStore.fetchStats()
  }
  isLoading.value = false
})

const handleCloseShift = async () => {
  const result = await shiftStore.closeShiftApi(closingAmount.value)
  if (result.success) {
    showCloseConfirmation.value = false
    showLogoutPrompt.value = true
  } else {
    alert(result.message)
  }
}

const handleFinalLogout = () => {
  userStore.logout()
  shiftStore.closeShift()
}

const formatCurrency = (value: number) => {
  return new Intl.NumberFormat('es-MX', { style: 'currency', currency: 'MXN' }).format(value)
}

const formatDate = (timestamp: number) => {
  return new Date(timestamp).toLocaleTimeString('es-MX', { hour: '2-digit', minute: '2-digit' })
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
</script>

<template>
  <div class="h-full bg-slate-50 overflow-y-auto p-8">
    <div class="max-w-5xl mx-auto space-y-8">
      <!-- Header -->
      <div class="flex flex-col md:flex-row md:items-center justify-between gap-6">
        <div>
          <h2 class="text-3xl font-black text-slate-900 tracking-tight italic uppercase">
            Detalle de Turno
          </h2>
          <p class="text-slate-400 font-medium">Resumen financiero de tu jornada actual</p>
        </div>
        <div class="flex items-center gap-3">
          <button
            @click="showCloseConfirmation = true"
            class="px-6 py-2.5 bg-red-50 text-red-600 rounded-full font-black text-xs uppercase tracking-widest hover:bg-red-500 hover:text-white transition-all duration-300 shadow-sm shadow-red-100 active:scale-95 border border-red-100 flex items-center gap-2"
          >
            <LogOut class="w-4 h-4" />
            Cerrar Turno del Día
          </button>
          <div
            class="px-4 py-2 bg-emerald-50 text-emerald-600 rounded-full text-[10px] font-black uppercase tracking-widest border border-emerald-100 flex items-center gap-2"
          >
            <div class="w-2 h-2 rounded-full bg-emerald-500 animate-pulse"></div>
            Turno Abierto
          </div>
        </div>
      </div>

      <div
        v-if="isLoading"
        class="h-64 flex flex-col items-center justify-center text-slate-400 gap-4"
      >
        <Loader2 class="w-12 h-12 animate-spin text-orange-500" />
        <span class="font-black uppercase tracking-widest text-xs italic"
          >Calculando arqueo...</span
        >
      </div>

      <div
        v-else-if="!shiftStore.currentShift"
        class="bg-white p-12 rounded-[3rem] border border-slate-100 shadow-xl text-center"
      >
        <div
          class="w-20 h-20 bg-slate-50 rounded-3xl flex items-center justify-center mx-auto mb-6"
        >
          <Clock class="w-10 h-10 text-slate-300" />
        </div>
        <h3 class="text-2xl font-black text-slate-900 mb-2 mt">No hay turno activo</h3>
        <p class="text-slate-500 mb-8 max-w-sm mx-auto font-medium">
          Inicia un turno en el inicio del cajero para ver las estadísticas aquí.
        </p>
        <div class="flex flex-col gap-3">
          <button
            @click="navigate('home')"
            class="px-8 py-4 bg-slate-900 text-white rounded-2xl font-black shadow-xl shadow-slate-900/20 active:scale-95 transition-all flex items-center justify-center gap-2 mx-auto w-full max-w-xs"
          >
            Ir al Inicio <ArrowRight class="w-5 h-5" />
          </button>
          <button
            @click="handleFinalLogout"
            class="px-8 py-4 bg-white text-red-500 border-2 border-red-50 rounded-2xl font-black hover:bg-red-50 transition-all flex items-center justify-center gap-2 mx-auto w-full max-w-xs"
          >
            <LogOut class="w-5 h-5" /> Cerrar Sesión
          </button>
        </div>
      </div>

      <div v-else class="space-y-8 animate-in fade-in slide-in-from-bottom-6 duration-700">
        <!-- Main Metrics -->
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
          <!-- Opening Amount -->
          <div
            class="bg-white p-8 rounded-[2.5rem] shadow-xl shadow-slate-200/50 border border-slate-100 flex flex-col gap-4"
          >
            <div
              class="w-12 h-12 bg-blue-50 text-blue-500 rounded-2xl flex items-center justify-center"
            >
              <Wallet class="w-6 h-6" />
            </div>
            <div>
              <p
                class="text-[10px] font-black text-slate-400 uppercase tracking-widest leading-none mb-1"
              >
                Fondo Inicial
              </p>
              <h4 class="text-3xl font-black text-slate-900 italic tracking-tighter">
                {{ formatCurrency(shiftStore.currentShift.opening_amount) }}
              </h4>
            </div>
          </div>

          <!-- Total Sales -->
          <div
            class="bg-slate-900 p-8 rounded-[2.5rem] shadow-2xl shadow-slate-900/20 text-white flex flex-col gap-4 relative overflow-hidden"
          >
            <div class="absolute -right-6 -top-6 w-24 h-24 bg-white/5 rounded-full blur-2xl"></div>
            <div
              class="w-12 h-12 bg-white/10 text-white rounded-2xl flex items-center justify-center"
            >
              <TrendingUp class="w-6 h-6" />
            </div>
            <div>
              <p
                class="text-[10px] font-black text-white/40 uppercase tracking-widest leading-none mb-1"
              >
                Ventas Totales
              </p>
              <h4 class="text-3xl font-black text-white italic tracking-tighter">
                {{ formatCurrency(stats?.general?.total_sales || 0) }}
              </h4>
            </div>
          </div>

          <!-- Total Balance (Expected) -->
          <div
            class="bg-orange-500 p-8 rounded-[2.5rem] shadow-2xl shadow-orange-500/30 text-white flex flex-col gap-4 relative overflow-hidden"
          >
            <div
              class="absolute -right-6 -bottom-6 w-24 h-24 bg-white/10 rounded-full blur-2xl"
            ></div>
            <div
              class="w-12 h-12 bg-white/10 text-white rounded-2xl flex items-center justify-center"
            >
              <TrendingDown class="w-6 h-6" />
            </div>
            <div>
              <p
                class="text-[10px] font-black text-orange-100 uppercase tracking-widest leading-none mb-1"
              >
                Efectivo Esperado
              </p>
              <h4 class="text-3xl font-black text-white italic tracking-tighter">
                {{
                  formatCurrency(
                    shiftStore.currentShift.opening_amount + (stats?.general?.cash_sales || 0),
                  )
                }}
              </h4>
            </div>
          </div>
        </div>

        <!-- Details Grid -->
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
          <!-- Breakdown per payment method -->
          <div
            class="bg-white rounded-[3rem] shadow-xl shadow-slate-200/50 border border-slate-100 overflow-hidden"
          >
            <div class="p-8 border-b border-slate-50 flex items-center justify-between">
              <h3 class="text-xl font-black text-slate-900 italic uppercase">Desglose de Pagos</h3>
              <PieChart class="w-6 h-6 text-slate-300" />
            </div>
            <div class="p-8 space-y-4">
              <div
                v-if="!stats?.payments || stats.payments.length === 0"
                class="text-center py-6 text-slate-300 font-bold uppercase text-xs tracking-widest italic"
              >
                Sin ventas registradas
              </div>
              <div
                v-else
                v-for="pay in stats.payments"
                :key="pay.payment_method"
                class="flex items-center gap-4 p-5 rounded-3xl bg-slate-50 border border-slate-100 group hover:bg-slate-900 hover:text-white transition-all duration-300"
              >
                <div
                  class="w-12 h-12 bg-white rounded-2xl shadow-sm flex items-center justify-center group-hover:bg-white/10"
                >
                  <component
                    :is="getPaymentIcon(pay.payment_method)"
                    class="w-6 h-6 text-slate-900 group-hover:text-white transition-colors"
                  />
                </div>
                <div class="flex-1">
                  <h5 class="font-black italic uppercase tracking-tight">
                    {{ pay.payment_method }}
                  </h5>
                  <p
                    class="text-[10px] font-black text-slate-400 uppercase tracking-widest group-hover:text-white/40"
                  >
                    Acumulado en ventas
                  </p>
                </div>
                <div class="text-right">
                  <span class="text-xl font-black italic tracking-tighter">{{
                    formatCurrency(pay.amount)
                  }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- Shift Status & Timeline -->
          <div
            class="bg-white rounded-[3rem] shadow-xl shadow-slate-200/50 border border-slate-100 overflow-hidden flex flex-col"
          >
            <div class="p-8 border-b border-slate-50 flex items-center justify-between">
              <h3 class="text-xl font-black text-slate-900 italic uppercase">
                Cronología del Turno
              </h3>
              <Clock class="w-6 h-6 text-slate-300" />
            </div>
            <div class="p-8 flex-1 flex flex-col gap-6">
              <!-- Apertura -->
              <div class="flex gap-4">
                <div class="flex flex-col items-center">
                  <div class="w-4 h-4 rounded-full bg-blue-500 border-4 border-blue-100"></div>
                  <div class="w-0.5 flex-1 bg-slate-100 my-2"></div>
                </div>
                <div>
                  <h5 class="font-black italic text-slate-900 uppercase leading-none mb-1">
                    Apertura de Turno
                  </h5>
                  <p class="text-xs font-bold text-slate-400">
                    {{ formatDate(shiftStore.currentShift.opened_at) }}
                  </p>
                  <p class="mt-2 text-sm font-medium text-slate-600">
                    Iniciado con
                    <span class="font-black text-slate-900">{{
                      formatCurrency(shiftStore.currentShift.opening_amount)
                    }}</span>
                    para fondo de caja.
                  </p>
                </div>
              </div>

              <!-- Ventas -->
              <div class="flex gap-4">
                <div class="flex flex-col items-center">
                  <div class="w-4 h-4 rounded-full bg-orange-500 border-4 border-orange-100"></div>
                  <div class="w-0.5 flex-1 bg-slate-100 my-2"></div>
                </div>
                <div>
                  <h5 class="font-black italic text-slate-900 uppercase leading-none mb-1">
                    Actividad de Ventas
                  </h5>
                  <p class="text-xs font-bold text-slate-400">Total acumulado hoy</p>
                  <p class="mt-2 text-sm font-medium text-slate-600">
                    Se han procesado
                    <span class="font-black text-slate-900"
                      >{{ stats?.general?.total_orders || 0 }} órdenes</span
                    >
                    con éxito.
                  </p>
                </div>
              </div>

              <!-- Bottom -->
              <div
                class="mt-auto pt-8 flex items-center justify-between text-[10px] font-black uppercase tracking-[0.2em] text-slate-300"
              >
                <span>Sesión ID: #{{ shiftStore.currentShift.id }}</span>
                <span>Role: Cashier</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- MODAL DE CONFIRMACIÓN CIERRE -->
    <Transition
      enter-active-class="transition duration-300 ease-out"
      enter-from-class="opacity-0 scale-95"
      enter-to-class="opacity-100 scale-100"
      leave-active-class="transition duration-200 ease-in"
      leave-from-class="opacity-100 scale-100"
      leave-to-class="opacity-0 scale-95"
    >
      <div
        v-if="showCloseConfirmation"
        class="fixed inset-0 z-[100] flex items-center justify-center p-6"
      >
        <div
          class="absolute inset-0 bg-slate-900/60 backdrop-blur-md"
          @click="showCloseConfirmation = false"
        ></div>
        <div
          class="bg-white w-full max-w-md rounded-[3rem] shadow-2xl relative z-10 overflow-hidden border border-slate-100"
        >
          <div class="p-10 text-center">
            <div
              class="w-20 h-20 bg-red-100 text-red-600 rounded-3xl flex items-center justify-center mx-auto mb-6"
            >
              <LogOut class="w-10 h-10" />
            </div>
            <h3 class="text-3xl font-black text-slate-900 mb-4 tracking-tighter">¿Cerrar turno?</h3>
            <p class="text-slate-500 font-medium mb-8 text-sm">
              Ingresa el monto final en caja para el arqueo.
            </p>

            <div class="space-y-6">
              <div class="space-y-2 text-left">
                <label class="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-1"
                  >Monto final en caja</label
                >
                <div class="relative group">
                  <span
                    class="absolute left-5 top-1/2 -translate-y-1/2 text-slate-400 font-bold group-focus-within:text-red-500 transition-colors text-xl"
                    >$</span
                  >
                  <input
                    v-model.number="closingAmount"
                    type="number"
                    class="w-full pl-12 pr-6 py-5 bg-slate-50 border-2 border-slate-100 rounded-2xl focus:bg-white focus:border-red-500 focus:outline-none transition-all duration-300 text-2xl font-black text-slate-800"
                    placeholder="0.00"
                  />
                </div>
              </div>

              <div class="flex flex-col gap-3">
                <button
                  @click="handleCloseShift"
                  :disabled="shiftStore.isLoading"
                  class="w-full py-5 bg-red-500 hover:bg-red-600 text-white rounded-2xl font-black text-lg shadow-xl shadow-red-500/30 transition-all active:scale-95 flex items-center justify-center gap-3"
                >
                  <template v-if="shiftStore.isLoading">
                    <Loader2 class="w-6 h-6 animate-spin" />
                  </template>
                  <template v-else> Confirmar Cierre </template>
                </button>
                <button
                  @click="showCloseConfirmation = false"
                  class="w-full py-4 text-slate-400 hover:text-slate-700 font-bold transition-colors"
                >
                  Cancelar
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </Transition>

    <!-- MODAL DE PREGUNTA CERRAR SESIÓN -->
    <Transition
      enter-active-class="transition duration-300 ease-out"
      enter-from-class="opacity-0 scale-95"
      enter-to-class="opacity-100 scale-100"
      leave-active-class="transition duration-200 ease-in"
      leave-from-class="opacity-100 scale-100"
      leave-to-class="opacity-0 scale-95"
    >
      <div
        v-if="showLogoutPrompt"
        class="fixed inset-0 z-[110] flex items-center justify-center p-6"
      >
        <div class="absolute inset-0 bg-slate-900/40 backdrop-blur-md"></div>
        <div
          class="bg-white w-full max-w-sm rounded-[2.5rem] shadow-2xl relative z-10 overflow-hidden border border-slate-100 p-8 text-center"
        >
          <div
            class="w-16 h-16 bg-blue-50 text-blue-500 rounded-2xl flex items-center justify-center mx-auto mb-5"
          >
            <UserIcon class="w-8 h-8" />
          </div>
          <h3 class="text-2xl font-black text-slate-900 mb-2 tracking-tight">Turno Cerrado</h3>
          <p class="text-slate-500 font-medium mb-8 text-sm">
            El arqueo se registró con éxito. ¿Deseas cerrar tu sesión también?
          </p>

          <div class="grid grid-cols-2 gap-3">
            <button
              @click="handleFinalLogout"
              class="py-4 bg-slate-900 text-white rounded-2xl font-bold hover:bg-slate-800 transition-all active:scale-95"
            >
              Sí, salir
            </button>
            <button
              @click="showLogoutPrompt = false"
              class="py-4 bg-slate-100 text-slate-600 rounded-2xl font-bold hover:bg-slate-200 transition-all active:scale-95 text-sm"
            >
              No, volver
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>
