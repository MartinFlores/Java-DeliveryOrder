<script setup lang="ts">
import { X, PlusCircle, ListOrdered, Wallet, User, LogOut, ClipboardClock } from 'lucide-vue-next'

import { useCashierNavigation, type CashierView } from '@/composables/useCashierNavigation'
import { useShiftStore } from '@/stores/shiftStore'
import { useOrderStore } from '@/stores/orderStore'

const props = defineProps<{
  open: boolean
}>()

const emit = defineEmits(['close', 'logout'])
const { currentView, navigate } = useCashierNavigation()
const shiftStore = useShiftStore()
const orderStore = useOrderStore()

const close = () => emit('close')

const nav = (view: CashierView) => {
  if (view === 'new_sale' && !shiftStore.isShiftOpen) return
  navigate(view)
  close()
}

const handleLogout = () => {
  emit('logout')
  close()
}
</script>

<template>
  <!-- Overly para móvil -->
  <div
    v-if="open"
    class="fixed inset-0 bg-black/40 backdrop-blur-sm z-40 lg:hidden"
    @click="close"
  />

  <aside
    :class="[
      'fixed lg:relative z-50 flex flex-col w-72 h-full bg-[#0B1120] text-slate-300 transition-transform duration-300 ease-in-out shadow-2xl border-r border-slate-800/50',
      open ? 'translate-x-0' : '-translate-x-full',
      'lg:translate-x-0',
    ]"
  >
    <!-- Header Logo -->
    <div
      class="flex items-center justify-between h-20 px-6 border-b border-slate-800/80 bg-[#0B1120]/50 backdrop-blur-md"
    >
      <div class="flex items-center gap-3 cursor-pointer">
        <div
          class="flex items-center justify-center w-10 h-10 rounded-xl bg-gradient-to-br from-orange-400 to-orange-600 shadow-lg shadow-orange-500/20 text-white"
        >
          <img src="/assets/logo-white.png" class="w-[24px]" />
        </div>
        <span class="text-xl font-bold tracking-wide text-white"
          >Delivery<span class="text-orange-500">Order</span></span
        >

        <!-- Botón cerrar solo en móvil -->
        <button class="lg:hidden p-2 rounded-lg hover:bg-slate-800 transition" @click="close">
          <X class="w-5 h-5 text-slate-400" />
        </button>
      </div>
    </div>

    <!-- Navegación -->
    <div class="flex-1 overflow-y-auto py-6 px-4 space-y-8">
      <div>
        <h3 class="px-4 text-[11px] font-bold uppercase tracking-wider text-slate-500 mb-3">
          Operaciones
        </h3>
        <ul class="space-y-1">
          <li>
            <button
              @click="nav('new_sale')"
              :disabled="!shiftStore.isShiftOpen"
              :class="[
                'w-full flex items-center justify-between px-4 py-2.5 rounded-xl transition-all duration-200 group relative overflow-hidden',
                currentView === 'new_sale'
                  ? 'bg-orange-500/10 text-orange-500'
                  : 'text-slate-400 hover:bg-slate-800/50 hover:text-slate-200',
                !shiftStore.isShiftOpen ? 'opacity-40 cursor-not-allowed grayscale' : '',
              ]"
            >
              <div
                v-if="currentView === 'new_sale'"
                class="absolute left-0 top-1/2 -translate-y-1/2 w-1 h-6 bg-orange-500 rounded-r-full shadow-[0_0_8px_rgba(249,115,22,0.8)]"
              ></div>
              <div class="flex items-center gap-3">
                <PlusCircle
                  :size="20"
                  :class="
                    currentView === 'new_sale'
                      ? 'text-orange-500'
                      : 'text-slate-500 group-hover:text-slate-300 transition-colors'
                  "
                /><span class="font-semibold text-sm">Nueva Orden</span>
              </div>
              <div v-if="!shiftStore.isShiftOpen" class="bg-slate-800/50 p-1 rounded-md">
                <X :size="12" class="text-slate-500" />
              </div>
            </button>
          </li>
          <li>
            <button
              @click="nav('open_orders')"
              :class="[
                'w-full flex items-center justify-between px-4 py-2.5 rounded-xl transition-all duration-200 group relative overflow-hidden',
                currentView === 'open_orders'
                  ? 'bg-orange-500/10 text-orange-500'
                  : 'text-slate-400 hover:bg-slate-800/50 hover:text-slate-200',
              ]"
            >
              <div
                v-if="currentView === 'open_orders'"
                class="absolute left-0 top-1/2 -translate-y-1/2 w-1 h-6 bg-orange-500 rounded-r-full shadow-[0_0_8px_rgba(249,115,22,0.8)]"
              ></div>
              <div class="flex items-center gap-3">
                <ClipboardClock
                  :size="20"
                  :class="
                    currentView === 'open_orders'
                      ? 'text-orange-500'
                      : 'text-slate-500 group-hover:text-slate-300 transition-colors'
                  "
                /><span class="font-semibold text-sm">Órdenes Abiertas</span>
              </div>

              <span
                v-if="orderStore.openOrdersCount > 0"
                class="text-[10px] text-white font-bold px-2 py-0.5 rounded-full bg-orange-600"
              >
                {{ orderStore.openOrdersCount }}
              </span>
            </button>
          </li>
          <li>
            <button
              @click="nav('orders')"
              :class="[
                'w-full flex items-center justify-between px-4 py-2.5 rounded-xl transition-all duration-200 group relative overflow-hidden',
                currentView === 'orders'
                  ? 'bg-orange-500/10 text-orange-500'
                  : 'text-slate-400 hover:bg-slate-800/50 hover:text-slate-200',
              ]"
            >
              <div
                v-if="currentView === 'orders'"
                class="absolute left-0 top-1/2 -translate-y-1/2 w-1 h-6 bg-orange-500 rounded-r-full shadow-[0_0_8px_rgba(249,115,22,0.8)]"
              ></div>
              <div class="flex items-center gap-3">
                <ListOrdered
                  :size="20"
                  :class="
                    currentView === 'orders'
                      ? 'text-orange-500'
                      : 'text-slate-500 group-hover:text-slate-300 transition-colors'
                  "
                /><span class="font-semibold text-sm">Órdenes del Día</span>
              </div>
            </button>
          </li>
        </ul>
      </div>

      <div>
        <h3 class="px-4 text-[11px] font-bold uppercase tracking-wider text-slate-500 mb-3">
          Administración
        </h3>
        <ul class="space-y-1">
          <li>
            <button
              @click="nav('shift')"
              :class="[
                'w-full flex items-center justify-between px-4 py-2.5 rounded-xl transition-all duration-200 group relative overflow-hidden',
                currentView === 'shift'
                  ? 'bg-orange-500/10 text-orange-500'
                  : 'text-slate-400 hover:bg-slate-800/50 hover:text-slate-200',
              ]"
            >
              <div
                v-if="currentView === 'shift'"
                class="absolute left-0 top-1/2 -translate-y-1/2 w-1 h-6 bg-orange-500 rounded-r-full shadow-[0_0_8px_rgba(249,115,22,0.8)]"
              ></div>
              <div class="flex items-center gap-3">
                <Wallet
                  :size="20"
                  :class="
                    currentView === 'shift'
                      ? 'text-orange-500'
                      : 'text-slate-500 group-hover:text-slate-300 transition-colors'
                  "
                /><span class="font-semibold text-sm">Turno</span>
              </div>
            </button>
          </li>
          <li>
            <button
              @click="nav('session')"
              :class="[
                'w-full flex items-center justify-between px-4 py-2.5 rounded-xl transition-all duration-200 group relative overflow-hidden',
                currentView === 'session'
                  ? 'bg-orange-500/10 text-orange-500'
                  : 'text-slate-400 hover:bg-slate-800/50 hover:text-slate-200',
              ]"
            >
              <div
                v-if="currentView === 'session'"
                class="absolute left-0 top-1/2 -translate-y-1/2 w-1 h-6 bg-orange-500 rounded-r-full shadow-[0_0_8px_rgba(249,115,22,0.8)]"
              ></div>
              <div class="flex items-center gap-3">
                <User
                  :size="20"
                  :class="
                    currentView === 'session'
                      ? 'text-orange-500'
                      : 'text-slate-500 group-hover:text-slate-300 transition-colors'
                  "
                /><span class="font-semibold text-sm">Mi Sesión</span>
              </div>
            </button>
          </li>
        </ul>
      </div>

      <div class="pt-4 mt-auto">
        <ul class="space-y-1">
          <li>
            <button
              @click="handleLogout"
              class="w-full flex items-center justify-between px-4 py-2.5 rounded-xl transition-all duration-200 group relative overflow-hidden text-red-400/90 hover:bg-red-500/10 hover:text-red-400"
            >
              <div class="flex items-center gap-3">
                <LogOut
                  :size="20"
                  class="text-red-500/70 group-hover:text-red-500 transition-colors"
                /><span class="font-semibold text-sm">Cerrar sesión</span>
              </div>
            </button>
          </li>
        </ul>
      </div>
    </div>

    <!-- Status Footer -->
    <div class="p-4 border-t border-slate-800/80">
      <div
        class="flex items-center gap-3 p-3 rounded-xl bg-slate-800/40 border border-slate-700/50 hover:bg-slate-800/80 transition-colors cursor-pointer"
      >
        <div
          class="w-2 h-2 rounded-full bg-emerald-500 shadow-[0_0_8px_rgba(16,185,129,0.8)] animate-pulse"
        ></div>
        <div class="flex flex-col">
          <span class="text-sm font-medium text-white">Cajero en línea</span>
          <span class="text-xs text-slate-400">v1.0.1 - POS</span>
        </div>
      </div>
    </div>
  </aside>
</template>
