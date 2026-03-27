<script setup lang="ts">
import { ref } from 'vue'
import { useShiftStore } from '@/stores/shiftStore'
import {
  Wallet,
  ArrowRight,
  Loader2,
  PlusCircle,
  ListOrdered,
  LogOut,
  AlertCircle,
  Clock,
} from 'lucide-vue-next'
import { useCashierNavigation } from '@/composables/useCashierNavigation'
import { useUserStore } from '@/stores/userStore'

const props = defineProps<{
  user: any
}>()

const shiftStore = useShiftStore()
const { navigate } = useCashierNavigation()
const openingAmount = ref<number>(0)
const errorMessage = ref('')
const showCloseConfirmation = ref(false)
const showLogoutPrompt = ref(false)
const closingAmount = ref<number>(0)
const userStore = useUserStore()

const handleOpenShift = async () => {
  if (openingAmount.value < 0) {
    errorMessage.value = 'El monto no puede ser negativo'
    return
  }

  errorMessage.value = ''

  const result = await shiftStore.openShift(openingAmount.value, props.user.id)

  if (result.success) {
    navigate('new_sale')
  } else {
    errorMessage.value = result.message || 'Error al abrir turno'
  }
}

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
</script>

<template>
  <div class="p-6 h-full flex items-center justify-center bg-slate-50/50">
    <!-- CASO 1: NO HAY TURNO ACTIVO -->
    <div
      v-if="!shiftStore.isShiftOpen"
      class="w-full max-w-md animate-in fade-in zoom-in duration-300"
    >
      <div
        class="bg-white rounded-[2.5rem] shadow-2xl shadow-slate-200/60 border border-slate-100 overflow-hidden"
      >
        <div
          class="bg-gradient-to-br from-slate-900 via-slate-800 to-slate-900 p-10 text-center relative overflow-hidden"
        >
          <!-- Decoración -->
          <div
            class="absolute -right-4 -top-4 w-32 h-32 bg-orange-500/10 rounded-full blur-3xl"
          ></div>
          <div
            class="absolute -left-4 -bottom-4 w-32 h-32 bg-blue-500/10 rounded-full blur-3xl"
          ></div>

          <div class="relative z-10">
            <div
              class="w-20 h-20 bg-white/10 backdrop-blur-md rounded-3xl flex items-center justify-center mx-auto mb-6 border border-white/20 shadow-2xl"
            >
              <Wallet class="w-10 h-10 text-orange-400" />
            </div>
            <h2 class="text-3xl font-black text-white mb-2 tracking-tight">No hay turno activo</h2>
            <p class="text-slate-400 font-medium">
              Inicia tu jornada laboral para comenzar a vender.
            </p>
          </div>
        </div>

        <div class="p-10 space-y-8">
          <div class="space-y-3">
            <label class="text-xs font-black text-slate-500 uppercase tracking-widest ml-1"
              >Monto inicial en caja</label
            >
            <div class="relative group">
              <span
                class="absolute left-5 top-1/2 -translate-y-1/2 text-slate-400 font-bold group-focus-within:text-orange-500 transition-colors text-xl"
                >$</span
              >
              <input
                v-model.number="openingAmount"
                type="number"
                class="w-full pl-12 pr-6 py-5 bg-slate-50 border-2 border-slate-100 rounded-2xl focus:bg-white focus:border-orange-500 focus:outline-none transition-all duration-300 text-2xl font-black text-slate-800"
                placeholder="0.00"
                @keyup.enter="handleOpenShift"
              />
            </div>
          </div>

          <!-- Error Message -->
          <Transition
            enter-active-class="transition duration-200 ease-out"
            enter-from-class="transform -translate-y-2 opacity-0"
            enter-to-class="transform translate-y-0 opacity-100"
          >
            <div
              v-if="errorMessage"
              class="bg-red-50 text-red-600 p-4 rounded-2xl text-xs font-bold border border-red-100 flex items-center gap-3"
            >
              <AlertCircle class="w-5 h-5 flex-shrink-0" />
              {{ errorMessage }}
            </div>
          </Transition>

          <div class="flex flex-col gap-3">
            <button
              @click="handleOpenShift"
              :disabled="shiftStore.isLoading"
              class="w-full py-5 bg-orange-500 hover:bg-orange-600 disabled:opacity-50 disabled:cursor-not-allowed text-white rounded-2xl font-black text-lg shadow-xl shadow-orange-500/30 transition-all active:scale-[0.98] flex items-center justify-center gap-3"
            >
              <template v-if="shiftStore.isLoading">
                <Loader2 class="w-6 h-6 animate-spin" />
                Abriendo...
              </template>
              <template v-else>
                Abrir Turno
                <ArrowRight class="w-6 h-6" />
              </template>
            </button>

            <button
              @click="handleFinalLogout"
              class="w-full py-4 text-slate-400 hover:text-red-500 font-bold transition-colors flex items-center justify-center gap-2 uppercase tracking-widest text-xs"
            >
              <LogOut class="w-4 h-4" /> Cerrar Sesión
            </button>
          </div>

          <p class="text-center text-[10px] text-slate-400 font-bold uppercase tracking-tighter">
            * Este monto será el fondo inicial para tu arqueo
          </p>
        </div>
      </div>
    </div>

    <!-- CASO 2: HAY TURNO ACTIVO -->
    <div v-else class="w-full max-w-2xl animate-in fade-in slide-in-from-bottom-4 duration-500">
      <div class="text-center mb-10">
        <div
          class="inline-flex items-center gap-2 px-4 py-2 bg-emerald-100 text-emerald-700 rounded-full text-xs font-black uppercase tracking-widest mb-4 border border-emerald-200 shadow-sm animate-pulse"
        >
          <div
            class="w-2 h-2 rounded-full bg-emerald-500 shadow-[0_0_8px_rgba(16,185,129,0.8)]"
          ></div>
          Turno Activo
        </div>
        <h2 class="text-5xl font-black text-slate-900 mb-4 tracking-tighter">Panel de Cajero</h2>
        <p class="text-slate-500 font-medium text-lg">
          Hola, {{ user?.name }}. Tienes un turno abierto desde las
          {{ formatDate(shiftStore.currentShift?.opened_at || 0) }}.
        </p>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-10">
        <!-- Card Info Turno -->
        <div
          class="bg-white p-8 rounded-[2.5rem] shadow-xl shadow-slate-200/50 border border-slate-100"
        >
          <div class="flex items-center gap-4 mb-6">
            <div
              class="w-12 h-12 bg-orange-100 rounded-2xl flex items-center justify-center text-orange-600"
            >
              <Wallet class="w-6 h-6" />
            </div>
            <div>
              <p class="text-xs font-black text-slate-400 uppercase tracking-widest">
                Monto Inicial
              </p>
              <p class="text-2xl font-black text-slate-900">
                {{ formatCurrency(shiftStore.currentShift?.opening_amount || 0) }}
              </p>
            </div>
          </div>
          <div class="flex items-center gap-4">
            <div
              class="w-12 h-12 bg-blue-100 rounded-2xl flex items-center justify-center text-blue-600"
            >
              <Clock class="w-6 h-6" />
            </div>
            <div>
              <p class="text-xs font-black text-slate-400 uppercase tracking-widest">Apertura</p>
              <p class="text-lg font-bold text-slate-700">
                {{ formatDate(shiftStore.currentShift?.opened_at || 0) }}
              </p>
            </div>
          </div>
        </div>

        <!-- Acciones Rápidas -->
        <div class="flex flex-col gap-4">
          <button
            @click="navigate('new_sale')"
            class="flex-1 bg-orange-500 hover:bg-orange-600 text-white rounded-[2rem] p-6 flex flex-col items-center justify-center gap-3 shadow-xl shadow-orange-500/30 transition-all hover:scale-[1.02] active:scale-95 group"
          >
            <PlusCircle class="w-12 h-12 group-hover:rotate-90 transition-transform duration-500" />
            <span class="text-2xl font-black tracking-tight">Nueva Venta</span>
          </button>

          <button
            @click="navigate('orders')"
            class="flex-1 bg-white hover:bg-slate-50 text-slate-800 border-2 border-slate-100 rounded-[2rem] p-6 flex items-center justify-center gap-4 shadow-lg shadow-slate-100 transition-all hover:border-slate-300 active:scale-95"
          >
            <ListOrdered class="w-8 h-8 text-blue-500" />
            <span class="text-xl font-black tracking-tight">Ver Órdenes</span>
          </button>
        </div>
      </div>

      <!-- Botón Discreto para Cerrar Turno -->
      <div class="flex justify-center">
        <button
          @click="showCloseConfirmation = true"
          class="flex items-center gap-2 px-6 py-3 text-slate-400 hover:text-red-500 transition-colors font-bold text-sm uppercase tracking-widest"
        >
          <LogOut class="w-5 h-5" />
          Cerrar Turno del Día
        </button>
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
            <p class="text-slate-500 font-medium mb-8">
              Debes ingresar el monto final en caja para el arqueo antes de finalizar.
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
                  <template v-else> Confirmar Cierre de Turno </template>
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
            <User class="w-8 h-8" />
          </div>
          <h3 class="text-2xl font-black text-slate-900 mb-2 tracking-tight">Turno Cerrado</h3>
          <p class="text-slate-500 font-medium mb-8">
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
              class="py-4 bg-slate-100 text-slate-600 rounded-2xl font-bold hover:bg-slate-200 transition-all active:scale-95"
            >
              No, volver
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>
