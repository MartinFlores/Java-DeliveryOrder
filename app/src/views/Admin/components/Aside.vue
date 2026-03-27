<script setup lang="ts">
import {
  ShelvingUnit,
  X,
  AppWindow,
  UtensilsCrossed,
  LayoutDashboard,
  Activity,
  Bell,
  ListOrdered,
  Wallet,
  ChefHat,
  Package,
  TrendingUp,
  Users,
  MonitorSmartphone,
  Network,
  Printer,
  ShieldCheck,
  LogOut,
  Gem,
} from 'lucide-vue-next'

import { useAdminNavigation, type AdminView } from '@/composables/useAdminNavigation'
import { useConfigStore } from '@/stores/configStore'
import { Loader } from '@/utils/Loader.ts'

const configStore = useConfigStore()

const props = defineProps<{
  open: boolean
}>()

const emit = defineEmits(['close'])
const { currentView, navigate } = useAdminNavigation()

const close = () => emit('close')

const nav = (view: AdminView) => {
  navigate(view)
  close()
}

const logout = () => {
  Loader.show('Cerrando sesión')
  setTimeout(() => {
    configStore.logoutAdmin()
  }, 500)
}
</script>
<template>
  <!-- Aside.bue -->
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
    <!-- Header -->
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
    <div class="flex-1 overflow-y-auto py-6 px-4 space-y-8">
      <div class="mb-6">
        <h3 class="px-4 text-[11px] font-bold uppercase tracking-wider text-slate-500 mb-3">
          Panel General
        </h3>
        <ul class="space-y-1">
          <li>
            <button
              @click="nav('dashboard')"
              :class="[
                'w-full flex items-center justify-between px-4 py-2.5 rounded-xl transition-all duration-200 group relative overflow-hidden',
                currentView === 'dashboard'
                  ? 'bg-orange-500/10 text-orange-500'
                  : 'text-slate-400 hover:bg-slate-800/50 hover:text-slate-200',
              ]"
            >
              <div
                v-if="currentView === 'dashboard'"
                class="absolute left-0 top-1/2 -translate-y-1/2 w-1 h-6 bg-orange-500 rounded-r-full shadow-[0_0_8px_rgba(249,115,22,0.8)]"
              ></div>
              <div class="flex items-center gap-3">
                <LayoutDashboard
                  :size="20"
                  :class="
                    currentView === 'dashboard'
                      ? 'text-orange-500'
                      : 'text-slate-500 group-hover:text-slate-300 transition-colors'
                  "
                /><span class="font-semibold text-sm">Resumen</span>
              </div>
            </button>
          </li>
          <li>
            <button
              @click="nav('activity')"
              :class="[
                'w-full flex items-center justify-between px-4 py-2.5 rounded-xl transition-all duration-200 group relative overflow-hidden',
                currentView === 'activity'
                  ? 'bg-orange-500/10 text-orange-500'
                  : 'text-slate-400 hover:bg-slate-800/50 hover:text-slate-200',
              ]"
            >
              <div
                v-if="currentView === 'activity'"
                class="absolute left-0 top-1/2 -translate-y-1/2 w-1 h-6 bg-orange-500 rounded-r-full shadow-[0_0_8px_rgba(249,115,22,0.8)]"
              ></div>
              <div class="flex items-center gap-3">
                <Activity
                  :size="20"
                  :class="
                    currentView === 'activity'
                      ? 'text-orange-500'
                      : 'text-slate-500 group-hover:text-slate-300 transition-colors'
                  "
                /><span class="font-semibold text-sm">Actividad</span>
              </div>
            </button>
          </li>
          <li>
            <button
              @click="nav('alerts')"
              :class="[
                'w-full flex items-center justify-between px-4 py-2.5 rounded-xl transition-all duration-200 group relative overflow-hidden',
                currentView === 'alerts'
                  ? 'bg-orange-500/10 text-orange-500'
                  : 'text-slate-400 hover:bg-slate-800/50 hover:text-slate-200',
              ]"
            >
              <div
                v-if="currentView === 'alerts'"
                class="absolute left-0 top-1/2 -translate-y-1/2 w-1 h-6 bg-orange-500 rounded-r-full shadow-[0_0_8px_rgba(249,115,22,0.8)]"
              ></div>
              <div class="flex items-center gap-3">
                <Bell
                  :size="20"
                  :class="
                    currentView === 'alerts'
                      ? 'text-orange-500'
                      : 'text-slate-500 group-hover:text-slate-300 transition-colors'
                  "
                /><span class="font-semibold text-sm">Alertas</span>
              </div>
              <span
                class="text-[10px] font-bold px-2 py-0.5 rounded-full bg-slate-700 text-slate-300"
                >3</span
              >
            </button>
          </li>
        </ul>
      </div>
      <div class="mb-6">
        <h3 class="px-4 text-[11px] font-bold uppercase tracking-wider text-slate-500 mb-3">
          Programas de Lealtad
        </h3>
        <ul class="space-y-1">
          <li>
            <button
              @click="nav('tpremia-dashboard')"
              :class="[
                'w-full flex items-center justify-between px-4 py-2.5 rounded-xl transition-all duration-200 group relative overflow-hidden',
                currentView === 'tpremia-dashboard'
                  ? 'bg-blue-500/10 text-blue-400'
                  : 'text-slate-400 hover:bg-slate-800/50 hover:text-slate-200',
              ]"
            >
              <div
                v-if="currentView === 'tpremia-dashboard'"
                class="absolute left-0 top-1/2 -translate-y-1/2 w-1 h-6 bg-blue-500 rounded-r-full shadow-[0_0_8px_rgba(59,130,246,0.8)]"
              ></div>
              <div class="flex items-center gap-3">
                <Gem
                  :size="20"
                  :class="
                    currentView === 'tpremia-dashboard'
                      ? 'text-blue-400'
                      : 'text-slate-500 group-hover:text-slate-300 transition-colors'
                  "
                /><span class="font-semibold text-sm">TPremia</span>
              </div>
            </button>
          </li>
        </ul>
      </div>
      <div class="mb-6">
        <h3 class="px-4 text-[11px] font-bold uppercase tracking-wider text-slate-500 mb-3">
          Gestión Operativa
        </h3>
        <ul class="space-y-1">
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
                /><span class="font-semibold text-sm">Órdenes</span>
              </div>
              <span class="text-[10px] text-white font-bold px-2 py-0.5 rounded-full bg-orange-600"
                >12</span
              >
            </button>
          </li>
          <li>
            <button
              @click="nav('cashier')"
              :class="[
                'w-full flex items-center justify-between px-4 py-2.5 rounded-xl transition-all duration-200 group relative overflow-hidden',
                currentView === 'cashier'
                  ? 'bg-orange-500/10 text-orange-500'
                  : 'text-slate-400 hover:bg-slate-800/50 hover:text-slate-200',
              ]"
            >
              <div
                v-if="currentView === 'cashier'"
                class="absolute left-0 top-1/2 -translate-y-1/2 w-1 h-6 bg-orange-500 rounded-r-full shadow-[0_0_8px_rgba(249,115,22,0.8)]"
              ></div>
              <div class="flex items-center gap-3">
                <Wallet
                  :size="20"
                  :class="
                    currentView === 'cashier'
                      ? 'text-orange-500'
                      : 'text-slate-500 group-hover:text-slate-300 transition-colors'
                  "
                /><span class="font-semibold text-sm">Caja</span>
              </div>
            </button>
          </li>
          <li>
            <button
              @click="nav('kitchen')"
              :class="[
                'w-full flex items-center justify-between px-4 py-2.5 rounded-xl transition-all duration-200 group relative overflow-hidden',
                currentView === 'kitchen'
                  ? 'bg-orange-500/10 text-orange-500'
                  : 'text-slate-400 hover:bg-slate-800/50 hover:text-slate-200',
              ]"
            >
              <div
                v-if="currentView === 'kitchen'"
                class="absolute left-0 top-1/2 -translate-y-1/2 w-1 h-6 bg-orange-500 rounded-r-full shadow-[0_0_8px_rgba(249,115,22,0.8)]"
              ></div>
              <div class="flex items-center gap-3">
                <ChefHat
                  :size="20"
                  :class="
                    currentView === 'kitchen'
                      ? 'text-orange-500'
                      : 'text-slate-500 group-hover:text-slate-300 transition-colors'
                  "
                /><span class="font-semibold text-sm">Cocina</span>
              </div>
            </button>
          </li>
        </ul>
      </div>
      <div class="mb-6">
        <h3 class="px-4 text-[11px] font-bold uppercase tracking-wider text-slate-500 mb-3">
          Gestión Comercial
        </h3>
        <ul class="space-y-1">
          <li>
            <button
              @click="nav('inventory')"
              :class="[
                'w-full flex items-center justify-between px-4 py-2.5 rounded-xl transition-all duration-200 group relative overflow-hidden',
                currentView === 'inventory'
                  ? 'bg-orange-500/10 text-orange-500'
                  : 'text-slate-400 hover:bg-slate-800/50 hover:text-slate-200',
              ]"
            >
              <div
                v-if="currentView === 'inventory'"
                class="absolute left-0 top-1/2 -translate-y-1/2 w-1 h-6 bg-orange-500 rounded-r-full shadow-[0_0_8px_rgba(249,115,22,0.8)]"
              ></div>
              <div class="flex items-center gap-3">
                <ShelvingUnit
                  :class="
                    currentView === 'inventory'
                      ? 'text-orange-500'
                      : 'text-slate-500 group-hover:text-slate-300 transition-colors'
                  "
                  class="w-[20px]"
                />
                <span class="font-semibold text-sm">Inventario</span>
              </div>
            </button>
          </li>

          <li>
            <button
              @click="nav('products')"
              :class="[
                'w-full flex items-center justify-between px-4 py-2.5 rounded-xl transition-all duration-200 group relative overflow-hidden',
                currentView === 'products'
                  ? 'bg-orange-500/10 text-orange-500'
                  : 'text-slate-400 hover:bg-slate-800/50 hover:text-slate-200',
              ]"
            >
              <div
                v-if="currentView === 'products'"
                class="absolute left-0 top-1/2 -translate-y-1/2 w-1 h-6 bg-orange-500 rounded-r-full shadow-[0_0_8px_rgba(249,115,22,0.8)]"
              ></div>
              <div class="flex items-center gap-3">
                <Package
                  :size="20"
                  :class="
                    currentView === 'products'
                      ? 'text-orange-500'
                      : 'text-slate-500 group-hover:text-slate-300 transition-colors'
                  "
                /><span class="font-semibold text-sm">Productos</span>
              </div>
            </button>
          </li>
          <li>
            <button
              @click="nav('sales')"
              :class="[
                'w-full flex items-center justify-between px-4 py-2.5 rounded-xl transition-all duration-200 group relative overflow-hidden',
                currentView === 'sales'
                  ? 'bg-orange-500/10 text-orange-500'
                  : 'text-slate-400 hover:bg-slate-800/50 hover:text-slate-200',
              ]"
            >
              <div
                v-if="currentView === 'sales'"
                class="absolute left-0 top-1/2 -translate-y-1/2 w-1 h-6 bg-orange-500 rounded-r-full shadow-[0_0_8px_rgba(249,115,22,0.8)]"
              ></div>
              <div class="flex items-center gap-3">
                <TrendingUp
                  :size="20"
                  :class="
                    currentView === 'sales'
                      ? 'text-orange-500'
                      : 'text-slate-500 group-hover:text-slate-300 transition-colors'
                  "
                /><span class="font-semibold text-sm">Ventas</span>
              </div>
            </button>
          </li>
          <li>
            <button
              @click="nav('staff')"
              :class="[
                'w-full flex items-center justify-between px-4 py-2.5 rounded-xl transition-all duration-200 group relative overflow-hidden',
                currentView === 'staff'
                  ? 'bg-orange-500/10 text-orange-500'
                  : 'text-slate-400 hover:bg-slate-800/50 hover:text-slate-200',
              ]"
            >
              <div
                v-if="currentView === 'staff'"
                class="absolute left-0 top-1/2 -translate-y-1/2 w-1 h-6 bg-orange-500 rounded-r-full shadow-[0_0_8px_rgba(249,115,22,0.8)]"
              ></div>
              <div class="flex items-center gap-3">
                <Users
                  :size="20"
                  :class="
                    currentView === 'staff'
                      ? 'text-orange-500'
                      : 'text-slate-500 group-hover:text-slate-300 transition-colors'
                  "
                /><span class="font-semibold text-sm">Personal</span>
              </div>
            </button>
          </li>
        </ul>
      </div>
      <div class="mb-6">
        <h3 class="px-4 text-[11px] font-bold uppercase tracking-wider text-slate-500 mb-3">
          Infraestructura
        </h3>
        <ul class="space-y-1">
          <li>
            <button
              @click="nav('apps')"
              :class="[
                'w-full flex items-center justify-between px-4 py-2.5 rounded-xl transition-all duration-200 group relative overflow-hidden',
                currentView === 'apps'
                  ? 'bg-orange-500/10 text-orange-500'
                  : 'text-slate-400 hover:bg-slate-800/50 hover:text-slate-200',
              ]"
            >
              <div
                v-if="currentView === 'apps'"
                class="absolute left-0 top-1/2 -translate-y-1/2 w-1 h-6 bg-orange-500 rounded-r-full shadow-[0_0_8px_rgba(249,115,22,0.8)]"
              ></div>
              <div class="flex items-center gap-3">
                <AppWindow
                  :class="
                    currentView === 'apps'
                      ? 'text-orange-500'
                      : 'text-slate-500 group-hover:text-slate-300 transition-colors'
                  "
                  class="w-[20px]"
                />

                <span class="font-semibold text-sm">Apps</span>
              </div>
            </button>
          </li>
          <li>
            <button
              @click="nav('device_status')"
              :class="[
                'w-full flex items-center justify-between px-4 py-2.5 rounded-xl transition-all duration-200 group relative overflow-hidden',
                currentView === 'device_status'
                  ? 'bg-orange-500/10 text-orange-500'
                  : 'text-slate-400 hover:bg-slate-800/50 hover:text-slate-200',
              ]"
            >
              <div
                v-if="currentView === 'device_status'"
                class="absolute left-0 top-1/2 -translate-y-1/2 w-1 h-6 bg-orange-500 rounded-r-full shadow-[0_0_8px_rgba(249,115,22,0.8)]"
              ></div>
              <div class="flex items-center gap-3">
                <MonitorSmartphone
                  :size="20"
                  :class="
                    currentView === 'device_status'
                      ? 'text-orange-500'
                      : 'text-slate-500 group-hover:text-slate-300 transition-colors'
                  "
                /><span class="font-semibold text-sm">Estado del dispositivo</span>
              </div>
            </button>
          </li>
          <li>
            <button
              @click="nav('network')"
              :class="[
                'w-full flex items-center justify-between px-4 py-2.5 rounded-xl transition-all duration-200 group relative overflow-hidden',
                currentView === 'network'
                  ? 'bg-orange-500/10 text-orange-500'
                  : 'text-slate-400 hover:bg-slate-800/50 hover:text-slate-200',
              ]"
            >
              <div
                v-if="currentView === 'network'"
                class="absolute left-0 top-1/2 -translate-y-1/2 w-1 h-6 bg-orange-500 rounded-r-full shadow-[0_0_8px_rgba(249,115,22,0.8)]"
              ></div>
              <div class="flex items-center gap-3">
                <Network
                  :size="20"
                  :class="
                    currentView === 'network'
                      ? 'text-orange-500'
                      : 'text-slate-500 group-hover:text-slate-300 transition-colors'
                  "
                /><span class="font-semibold text-sm">Red local</span>
              </div>
            </button>
          </li>
          <li>
            <button
              @click="nav('printers')"
              :class="[
                'w-full flex items-center justify-between px-4 py-2.5 rounded-xl transition-all duration-200 group relative overflow-hidden',
                currentView === 'printers'
                  ? 'bg-orange-500/10 text-orange-500'
                  : 'text-slate-400 hover:bg-slate-800/50 hover:text-slate-200',
              ]"
            >
              <div
                v-if="currentView === 'printers'"
                class="absolute left-0 top-1/2 -translate-y-1/2 w-1 h-6 bg-orange-500 rounded-r-full shadow-[0_0_8px_rgba(249,115,22,0.8)]"
              ></div>
              <div class="flex items-center gap-3">
                <Printer
                  :size="20"
                  :class="
                    currentView === 'printers'
                      ? 'text-orange-500'
                      : 'text-slate-500 group-hover:text-slate-300 transition-colors'
                  "
                /><span class="font-semibold text-sm">Impresoras</span>
              </div>
            </button>
          </li>
          <li>
            <button
              @click="nav('security')"
              :class="[
                'w-full flex items-center justify-between px-4 py-2.5 rounded-xl transition-all duration-200 group relative overflow-hidden',
                currentView === 'security'
                  ? 'bg-orange-500/10 text-orange-500'
                  : 'text-slate-400 hover:bg-slate-800/50 hover:text-slate-200',
              ]"
            >
              <div
                v-if="currentView === 'security'"
                class="absolute left-0 top-1/2 -translate-y-1/2 w-1 h-6 bg-orange-500 rounded-r-full shadow-[0_0_8px_rgba(249,115,22,0.8)]"
              ></div>
              <div class="flex items-center gap-3">
                <ShieldCheck
                  :size="20"
                  :class="
                    currentView === 'security'
                      ? 'text-orange-500'
                      : 'text-slate-500 group-hover:text-slate-300 transition-colors'
                  "
                /><span class="font-semibold text-sm">Seguridad</span>
              </div>
            </button>
          </li>
        </ul>
      </div>
      <div class="mb-6">
        <h3 class="px-4 text-[11px] font-bold uppercase tracking-wider text-slate-500 mb-3">
          Cuenta
        </h3>
        <ul class="space-y-1">
          <li>
            <button
              @click="logout()"
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
    <div class="p-4 border-t border-slate-800/80">
      <div
        class="flex items-center gap-3 p-3 rounded-xl bg-slate-800/40 border border-slate-700/50 hover:bg-slate-800/80 transition-colors cursor-pointer"
      >
        <div
          class="w-2 h-2 rounded-full bg-emerald-500 shadow-[0_0_8px_rgba(16,185,129,0.8)] animate-pulse"
        ></div>
        <div class="flex flex-col">
          <span class="text-sm font-medium text-white">Sistema en línea</span
          ><span class="text-xs text-slate-400">v1.0.1 - Producción</span>
        </div>
      </div>
    </div>
  </aside>
</template>
