<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import {
  Wallet,
  Calendar,
  Search,
  Clock,
  User as UserIcon,
  TrendingUp,
  ListOrdered,
  ChevronRight,
  Loader2,
  AlertCircle,
} from 'lucide-vue-next'
import { api } from '@/utils/axios'

const shifts = ref<any[]>([])
const selectedDate = ref(new Date().toISOString().split('T')[0])
const isLoading = ref(false)
const errorMessage = ref('')

const fetchShifts = async () => {
  isLoading.value = true
  errorMessage.value = ''
  try {
    const { data } = await api.get(`shifts/list?date=${selectedDate.value}`)
    if (data.status === 'ok') {
      shifts.value = data.data
    } else {
      errorMessage.value = data.message || 'Error al cargar los turnos'
    }
  } catch (error) {
    console.error('Error fetching shifts:', error)
    errorMessage.value = 'Error de conexión con el servidor'
  } finally {
    isLoading.value = false
  }
}

onMounted(fetchShifts)
watch(selectedDate, fetchShifts)

const formatCurrency = (value: number) => {
  return new Intl.NumberFormat('es-MX', { style: 'currency', currency: 'MXN' }).format(value)
}

const formatTime = (timestamp: number) => {
  return new Date(timestamp).toLocaleTimeString('es-MX', { hour: '2-digit', minute: '2-digit' })
}

const getStatusColor = (status: string) => {
  return status === 'Abierto'
    ? 'bg-emerald-100 text-emerald-700 border-emerald-200'
    : 'bg-slate-100 text-slate-600 border-slate-200'
}
</script>

<template>
  <div class="p-6 lg:p-10 space-y-8 animate-in fade-in duration-500">
    <!-- Header -->
    <div class="flex flex-col md:flex-row md:items-center justify-between gap-6">
      <div>
        <h2 class="text-3xl font-black text-slate-900 tracking-tight italic uppercase">
          Cortes de Caja
        </h2>
        <p class="text-slate-500 font-medium">
          Monitorea los turnos y la actividad financiera por fecha
        </p>
      </div>

      <div
        class="flex items-center gap-4 bg-white p-2 rounded-2xl shadow-sm border border-slate-100"
      >
        <div class="flex items-center gap-2 px-3 text-slate-400">
          <Calendar class="w-5 h-5" />
          <span class="text-xs font-black uppercase tracking-widest">Fecha:</span>
        </div>
        <input
          v-model="selectedDate"
          type="date"
          class="bg-slate-50 border-none rounded-xl px-4 py-2 text-sm font-bold text-slate-800 focus:ring-2 focus:ring-orange-500/20"
        />
      </div>
    </div>

    <!-- Main Content -->
    <div
      v-if="isLoading"
      class="h-64 flex flex-col items-center justify-center text-slate-400 gap-4"
    >
      <Loader2 class="w-12 h-12 animate-spin text-orange-500" />
      <span class="font-black uppercase tracking-widest text-xs italic">Cargando turnos...</span>
    </div>

    <div
      v-else-if="errorMessage"
      class="bg-red-50 border border-red-100 p-8 rounded-[2rem] text-center"
    >
      <AlertCircle class="w-12 h-12 text-red-500 mx-auto mb-4" />
      <h3 class="text-xl font-black text-red-900 mb-2">Ups, algo salió mal</h3>
      <p class="text-red-600 font-medium">{{ errorMessage }}</p>
      <button
        @click="fetchShifts"
        class="mt-6 px-6 py-2 bg-red-600 text-white rounded-xl font-bold text-sm"
      >
        Reintentar
      </button>
    </div>

    <div
      v-else-if="shifts.length === 0"
      class="bg-white p-20 rounded-[3rem] border border-slate-100 shadow-xl text-center"
    >
      <div class="w-20 h-20 bg-slate-50 rounded-3xl flex items-center justify-center mx-auto mb-6">
        <Clock class="w-10 h-10 text-slate-300" />
      </div>
      <h3 class="text-2xl font-black text-slate-900 mb-2">Sin actividad</h3>
      <p class="text-slate-500 max-w-sm mx-auto font-medium">
        No se encontraron turnos registrados para la fecha seleccionada.
      </p>
    </div>

    <div v-else class="grid grid-cols-1 gap-6">
      <div
        v-for="shift in shifts"
        :key="shift.id"
        class="bg-white rounded-[2.5rem] border border-slate-100 shadow-xl shadow-slate-200/40 overflow-hidden group hover:border-orange-200 transition-all duration-300"
      >
        <div class="p-8 flex flex-col lg:flex-row lg:items-center gap-8">
          <!-- User & Status -->
          <div class="flex items-center gap-4 min-w-[200px]">
            <div
              class="w-14 h-14 bg-slate-100 rounded-2xl flex items-center justify-center text-slate-400 group-hover:bg-orange-50 group-hover:text-orange-500 transition-colors"
            >
              <UserIcon class="w-7 h-7" />
            </div>
            <div>
              <h4 class="font-black text-slate-900 italic uppercase tracking-tight">
                {{ shift.user_name }}
              </h4>
              <div
                :class="[
                  'inline-flex items-center px-2 py-0.5 mt-1 rounded-md text-[9px] font-black uppercase border',
                  getStatusColor(shift.status),
                ]"
              >
                {{ shift.status }}
              </div>
            </div>
          </div>

          <!-- Times -->
          <div class="grid grid-cols-2 gap-8 flex-1">
            <div>
              <p class="text-[10px] font-black text-slate-400 uppercase tracking-widest mb-1">
                Apertura
              </p>
              <p class="font-bold text-slate-700 flex items-center gap-2">
                <Clock class="w-4 h-4 text-blue-400" />
                {{ formatTime(shift.opened_at) }}
              </p>
            </div>
            <div v-if="shift.closed_at">
              <p class="text-[10px] font-black text-slate-400 uppercase tracking-widest mb-1">
                Cierre
              </p>
              <p class="font-bold text-slate-700 flex items-center gap-2">
                <Clock class="w-4 h-4 text-red-300" />
                {{ formatTime(shift.closed_at) }}
              </p>
            </div>
            <div v-else>
              <p class="text-[10px] font-black text-slate-400 uppercase tracking-widest mb-1">
                Cierre
              </p>
              <p class="text-slate-300 italic text-sm">En curso...</p>
            </div>
          </div>

          <!-- Financials -->
          <div class="grid grid-cols-2 gap-8 flex-1 border-l border-slate-100 pl-8">
            <div>
              <p class="text-[10px] font-black text-slate-400 uppercase tracking-widest mb-1">
                Fondo Inicial
              </p>
              <p class="text-xl font-black text-slate-900 italic tracking-tighter">
                {{ formatCurrency(shift.opening_amount) }}
              </p>
            </div>
            <div>
              <p class="text-[10px] font-black text-emerald-500 uppercase tracking-widest mb-1">
                Ventas
              </p>
              <p class="text-xl font-black text-emerald-600 italic tracking-tighter">
                +{{ formatCurrency(shift.total_sales || 0) }}
              </p>
            </div>
          </div>

          <!-- Stats -->
          <div class="flex items-center gap-6 border-l border-slate-100 pl-8">
            <div class="text-center">
              <p class="text-[10px] font-black text-slate-400 uppercase tracking-widest mb-1">
                Órdenes
              </p>
              <span class="px-3 py-1 bg-blue-50 text-blue-600 rounded-lg font-black text-sm">{{
                shift.total_orders || 0
              }}</span>
            </div>
            <button
              class="w-12 h-12 bg-slate-50 hover:bg-slate-900 hover:text-white rounded-2xl flex items-center justify-center transition-all"
            >
              <ChevronRight class="w-6 h-6" />
            </button>
          </div>
        </div>

        <!-- Bottom Info -->
        <div
          class="px-8 py-4 bg-slate-50 border-t border-slate-100 flex justify-between items-center group-hover:bg-orange-50/50 transition-colors"
        >
          <div
            class="flex items-center gap-2 text-[10px] font-black text-slate-400 uppercase tracking-tighter"
          >
            <TrendingUp class="w-3 h-3 text-emerald-500" />
            Efectivo Esperado:
            <span class="text-slate-800 tracking-normal">{{
              formatCurrency(shift.opening_amount + (shift.cash_sales || 0))
            }}</span>
          </div>
          <span
            v-if="shift.closing_amount"
            class="text-[10px] font-black text-slate-400 uppercase tracking-tighter"
          >
            Efectivo Reportado:
            <span
              :class="[
                'tracking-normal',
                Math.abs(shift.closing_amount - (shift.opening_amount + (shift.cash_sales || 0))) >
                1
                  ? 'text-red-500'
                  : 'text-emerald-500',
              ]"
            >
              {{ formatCurrency(shift.closing_amount) }}
            </span>
          </span>
        </div>
      </div>
    </div>
  </div>
</template>
