<script setup lang="ts">
import { useUserStore } from '@/stores/userStore'
import { useShiftStore } from '@/stores/shiftStore'
import {
  User as UserIcon,
  Mail,
  Phone,
  Shield,
  LogOut,
  Clock,
  Wallet,
  ArrowUpRight,
  ExternalLink,
} from 'lucide-vue-next'

const userStore = useUserStore()
const shiftStore = useShiftStore()

const handleLogout = () => {
  if (confirm('¿Estás seguro de que deseas cerrar tu sesión?')) {
    userStore.logout()
    shiftStore.closeShift()
  }
}

const formatCurrency = (value: number) => {
  return new Intl.NumberFormat('es-MX', { style: 'currency', currency: 'MXN' }).format(value)
}

const formatDate = (timestamp: number) => {
  return new Date(timestamp).toLocaleDateString('es-MX', {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  })
}
</script>

<template>
  <div class="h-full bg-slate-50 overflow-y-auto pb-10">
    <!-- Profile Header Background -->
    <div class="h-48 bg-slate-900 relative overflow-hidden">
      <div class="absolute inset-0 opacity-20">
        <div class="absolute -right-20 -top-20 w-80 h-80 bg-orange-500 rounded-full blur-3xl"></div>
        <div class="absolute -left-20 -bottom-20 w-80 h-80 bg-blue-500 rounded-full blur-3xl"></div>
      </div>
    </div>

    <div class="max-w-4xl mx-auto px-6 -mt-24 relative z-10">
      <!-- Profile Card -->
      <div
        class="bg-white rounded-[3rem] shadow-2xl shadow-slate-200 border border-slate-100 p-8 md:p-12 mb-8"
      >
        <div class="flex flex-col md:flex-row items-center md:items-start gap-8">
          <!-- Avatar -->
          <div class="relative">
            <div
              class="w-40 h-40 bg-slate-100 rounded-[2.5rem] border-8 border-white shadow-xl flex items-center justify-center overflow-hidden"
            >
              <img
                v-if="userStore.user?.avatar"
                :src="userStore.user.avatar"
                class="w-full h-full object-cover"
              />
              <UserIcon v-else class="w-20 h-20 text-slate-300" />
            </div>
            <div
              class="absolute -bottom-2 -right-2 w-10 h-10 bg-orange-500 border-4 border-white rounded-full flex items-center justify-center shadow-lg"
            >
              <Shield class="w-5 h-5 text-white" />
            </div>
          </div>

          <!-- User Info -->
          <div class="flex-1 text-center md:text-left pt-4">
            <div
              class="inline-flex items-center gap-2 px-3 py-1 bg-orange-50 text-orange-600 rounded-full text-[10px] font-black uppercase tracking-widest mb-4 border border-orange-100"
            >
              {{ userStore.user?.role }}
            </div>
            <h1 class="text-4xl font-black text-slate-900 tracking-tighter mb-2 italic uppercase">
              {{ userStore.user?.name }}
            </h1>
            <p class="text-slate-400 font-medium text-lg mb-6">
              Miembro del equipo desde hace tiempo
            </p>

            <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div
                class="flex items-center gap-3 text-slate-600 bg-slate-50 p-4 rounded-2xl border border-slate-100"
              >
                <Phone class="w-5 h-5 text-slate-400" />
                <span class="font-bold tracking-tight">{{ userStore.user?.whatsapp }}</span>
              </div>
              <div
                class="flex items-center gap-3 text-slate-600 bg-slate-50 p-4 rounded-2xl border border-slate-100"
              >
                <Shield class="w-5 h-5 text-slate-400" />
                <span class="font-bold tracking-tight">Acceso Nivel 1</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Stats / Current Shift -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        <div
          class="bg-white p-8 rounded-[2.5rem] shadow-xl shadow-slate-200/50 border border-slate-100 flex flex-col gap-4"
        >
          <div
            class="w-12 h-12 bg-blue-50 text-blue-500 rounded-2xl flex items-center justify-center"
          >
            <Clock class="w-6 h-6" />
          </div>
          <div>
            <p class="text-xs font-black text-slate-400 uppercase tracking-widest">
              Estado de Turno
            </p>
            <h4
              :class="[
                'text-xl font-black italic',
                shiftStore.isShiftOpen ? 'text-emerald-500' : 'text-slate-400',
              ]"
            >
              {{ shiftStore.isShiftOpen ? 'ACTIVO' : 'INACTIVO' }}
            </h4>
          </div>
        </div>

        <div
          v-if="shiftStore.isShiftOpen"
          class="bg-white p-8 rounded-[2.5rem] shadow-xl shadow-slate-200/50 border border-slate-100 flex flex-col gap-4"
        >
          <div
            class="w-12 h-12 bg-emerald-50 text-emerald-500 rounded-2xl flex items-center justify-center"
          >
            <Wallet class="w-6 h-6" />
          </div>
          <div>
            <p class="text-xs font-black text-slate-400 uppercase tracking-widest">Fondo Inicial</p>
            <h4 class="text-xl font-black italic text-slate-900">
              {{ formatCurrency(shiftStore.currentShift?.opening_amount || 0) }}
            </h4>
          </div>
        </div>

        <div
          class="bg-white p-8 rounded-[2.5rem] shadow-xl shadow-slate-200/50 border border-slate-100 flex flex-col gap-4"
        >
          <div
            class="w-12 h-12 bg-orange-50 text-orange-500 rounded-2xl flex items-center justify-center"
          >
            <ArrowUpRight class="w-6 h-6" />
          </div>
          <div>
            <p class="text-xs font-black text-slate-400 uppercase tracking-widest">Fecha Hoy</p>
            <h4 class="text-sm font-black italic text-slate-900 uppercase">
              {{ formatDate(Date.now()) }}
            </h4>
          </div>
        </div>
      </div>

      <!-- Danger Zone -->
      <div
        class="bg-white rounded-[2.5rem] shadow-xl shadow-slate-200/50 border border-red-50 p-8 flex items-center justify-between"
      >
        <div>
          <h4 class="text-xl font-black text-slate-900 italic uppercase italic leading-none mb-1">
            Cerrar Sesión
          </h4>
          <p class="text-slate-400 font-medium text-sm">
            Se eliminarán todos los datos locales del dispositivo.
          </p>
        </div>
        <button
          @click="handleLogout"
          class="w-14 h-14 bg-red-50 text-red-500 hover:bg-red-500 hover:text-white rounded-2xl flex items-center justify-center transition-all duration-300 shadow-lg shadow-red-100 active:scale-90"
        >
          <LogOut class="w-6 h-6" />
        </button>
      </div>

      <!-- Version Info -->
      <p class="text-center mt-12 text-[10px] font-black text-slate-300 uppercase tracking-widest">
        Sistema DeliveryOrder v1.0.0 • Desarrollado por Martin Flores
      </p>
    </div>
  </div>
</template>
