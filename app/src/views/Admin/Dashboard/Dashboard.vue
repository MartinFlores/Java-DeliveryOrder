<script setup lang="ts">
import { onMounted } from 'vue'
import { useConfigStore } from '@/stores/configStore'
import { useAdminNavigation } from '@/composables/useAdminNavigation'
const { navigate } = useAdminNavigation()
const navigateToApps = () => {
  navigate('apps')
}

const configStore = useConfigStore()
onMounted(() => {
  configStore.loadCompanyInfo()
})
</script>

<template>
  <div class="flex-1 overflow-y-auto wrap-view">
    <!-- Header -->
    <header class="mb-6">
      <div class="flex items-center gap-3 mb-4">
        <span class="h-1 w-12 bg-orange-600 rounded-full"></span>
        <span class="text-orange-600 font-bold tracking-wider uppercase text-sm">Resumen</span>
      </div>
      <h1 class="text-4xl font-extrabold text-slate-900 mb-4 tracking-tight">Vista General</h1>
      <div class="flex items-center justify-between md:flex-row flex-col">
        <p class="text-slate-500 max-w-2xl text-lg leading-relaxed">
          Bienvenido al panel central. Aquí podrás visualizar el rendimiento, gestionar recursos y
          obtener información clave sobre el estado de tu negocio.
        </p>

        <div class="flex gap-2 ml-auto mt-4 md:mt-0">
          <button
            @click="navigateToApps"
            class="px-4 py-2 text-sm font-semibold text-slate-600 bg-white border border-slate-200 rounded-lg hover:border-orange-500 hover:text-orange-600 active:scale-95 transition-all shadow-sm"
          >
            Hoy
          </button>

          <button
            class="px-4 py-2 text-sm font-semibold text-white bg-slate-800 rounded-lg hover:bg-slate-900 active:scale-95 transition-all shadow-md shadow-slate-800/20"
          >
            Exportar
          </button>
        </div>
      </div>
    </header>

    <div class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-4 gap-6 mb-8">
      <div
        class="bg-white rounded-2xl p-6 border border-slate-200/60 shadow-[0_8px_30px_rgb(0,0,0,0.04)] hover:shadow-[0_8px_30px_rgb(0,0,0,0.08)] transition-all duration-300 group"
      >
        <div class="flex justify-between items-start mb-4">
          <div
            class="p-3 rounded-xl bg-orange-50 text-orange-600 transition-transform duration-300 group-hover:scale-110"
          >
            <Wallet :size="22" />
          </div>
          <div
            class="flex items-center gap-1 text-xs font-bold px-2 py-1 rounded-full bg-emerald-50 text-emerald-600"
          >
            <ArrowUpRight :size="14" />+12.5%
          </div>
        </div>
        <div>
          <h4 class="text-slate-500 text-sm font-medium mb-1">Ventas del día</h4>
          <p class="text-2xl font-black text-slate-800">$24,350.00</p>
        </div>
      </div>
      <div
        class="bg-white rounded-2xl p-6 border border-slate-200/60 shadow-[0_8px_30px_rgb(0,0,0,0.04)] hover:shadow-[0_8px_30px_rgb(0,0,0,0.08)] transition-all duration-300 group"
      >
        <div class="flex justify-between items-start mb-4">
          <div
            class="p-3 rounded-xl bg-blue-50 text-blue-600 transition-transform duration-300 group-hover:scale-110"
          >
            <ListOrdered :size="22" />
          </div>
          <div
            class="flex items-center gap-1 text-xs font-bold px-2 py-1 rounded-full bg-emerald-50 text-emerald-600"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="14"
              height="14"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
              class="lucide lucide-arrow-up-right"
              aria-hidden="true"
            >
              <path d="M7 7h10v10"></path>
              <path d="M7 17 17 7"></path></svg
            >+4 vs ayer
          </div>
        </div>
        <div>
          <h4 class="text-slate-500 text-sm font-medium mb-1">Órdenes activas</h4>
          <p class="text-2xl font-black text-slate-800">18</p>
        </div>
      </div>
      <div
        class="bg-white rounded-2xl p-6 border border-slate-200/60 shadow-[0_8px_30px_rgb(0,0,0,0.04)] hover:shadow-[0_8px_30px_rgb(0,0,0,0.08)] transition-all duration-300 group"
      >
        <div class="flex justify-between items-start mb-4">
          <div
            class="p-3 rounded-xl bg-emerald-50 text-emerald-600 transition-transform duration-300 group-hover:scale-110"
          >
            <TrendingUp :size="22" />
          </div>
          <div
            class="flex items-center gap-1 text-xs font-bold px-2 py-1 rounded-full bg-red-50 text-red-600"
          >
            <ArrowDownRight :size="14" />-2.1%
          </div>
        </div>
        <div>
          <h4 class="text-slate-500 text-sm font-medium mb-1">Ticket promedio</h4>
          <p class="text-2xl font-black text-slate-800">$345.50</p>
        </div>
      </div>
      <div
        class="bg-white rounded-2xl p-6 border border-slate-200/60 shadow-[0_8px_30px_rgb(0,0,0,0.04)] hover:shadow-[0_8px_30px_rgb(0,0,0,0.08)] transition-all duration-300 group"
      >
        <div class="flex justify-between items-start mb-4">
          <div
            class="p-3 rounded-xl bg-purple-50 text-purple-600 transition-transform duration-300 group-hover:scale-110"
          >
            <Activity :size="22" />
          </div>
          <div
            class="flex items-center gap-1 text-xs font-bold px-2 py-1 rounded-full bg-emerald-50 text-emerald-600"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="14"
              height="14"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
              class="lucide lucide-arrow-up-right"
              aria-hidden="true"
            >
              <path d="M7 7h10v10"></path>
              <path d="M7 17 17 7"></path></svg
            >+8.2%
          </div>
        </div>
        <div>
          <h4 class="text-slate-500 text-sm font-medium mb-1">Utilidad estimada</h4>
          <p class="text-2xl font-black text-slate-800">$8,520.00</p>
        </div>
      </div>
    </div>
    <div class="grid grid-cols-1 xl:grid-cols-3 gap-8">
      <div
        class="xl:col-span-2 bg-white rounded-2xl border border-slate-200/60 p-6 shadow-[0_8px_30px_rgb(0,0,0,0.04)] relative overflow-hidden group"
      >
        <div class="flex justify-between items-center mb-6">
          <div>
            <h3 class="text-lg font-bold text-slate-800">Flujo de Ingresos vs Órdenes</h3>
            <p class="text-sm text-slate-500">Comportamiento de las últimas 12 horas</p>
          </div>
          <button class="p-2 hover:bg-slate-100 rounded-lg text-slate-400 transition-colors">
            <EllipsisVertical :size="20" />
          </button>
        </div>
        <div class="h-64 flex items-end gap-2 sm:gap-4 mt-8 relative">
          <div class="absolute inset-0 flex flex-col justify-between pointer-events-none">
            <div class="w-full h-px bg-slate-100"></div>
            <div class="w-full h-px bg-slate-100"></div>
            <div class="w-full h-px bg-slate-100"></div>
            <div class="w-full h-px bg-slate-100"></div>
            <div class="w-full h-px bg-slate-100"></div>
          </div>
          <div class="relative flex-1 flex items-end group/bar cursor-pointer">
            <div
              class="w-full bg-slate-100 rounded-t-md relative overflow-hidden transition-all duration-300 group-hover/bar:bg-orange-100"
              style="height: 100%"
            >
              <div
                class="absolute bottom-0 w-full bg-orange-500 rounded-t-md transition-all duration-500 ease-out shadow-[0_0_15px_rgba(249,115,22,0.3)] group-hover/bar:bg-orange-400"
                style="height: 40%"
              ></div>
            </div>
            <div
              class="absolute -top-10 left-1/2 -translate-x-1/2 bg-slate-800 text-white text-xs font-medium py-1 px-2 rounded opacity-0 group-hover/bar:opacity-100 transition-opacity pointer-events-none z-10 whitespace-nowrap"
            >
              $4000
            </div>
          </div>
          <div class="relative flex-1 flex items-end group/bar cursor-pointer">
            <div
              class="w-full bg-slate-100 rounded-t-md relative overflow-hidden transition-all duration-300 group-hover/bar:bg-orange-100"
              style="height: 100%"
            >
              <div
                class="absolute bottom-0 w-full bg-orange-500 rounded-t-md transition-all duration-500 ease-out shadow-[0_0_15px_rgba(249,115,22,0.3)] group-hover/bar:bg-orange-400"
                style="height: 65%"
              ></div>
            </div>
            <div
              class="absolute -top-10 left-1/2 -translate-x-1/2 bg-slate-800 text-white text-xs font-medium py-1 px-2 rounded opacity-0 group-hover/bar:opacity-100 transition-opacity pointer-events-none z-10 whitespace-nowrap"
            >
              $6500
            </div>
          </div>
          <div class="relative flex-1 flex items-end group/bar cursor-pointer">
            <div
              class="w-full bg-slate-100 rounded-t-md relative overflow-hidden transition-all duration-300 group-hover/bar:bg-orange-100"
              style="height: 100%"
            >
              <div
                class="absolute bottom-0 w-full bg-orange-500 rounded-t-md transition-all duration-500 ease-out shadow-[0_0_15px_rgba(249,115,22,0.3)] group-hover/bar:bg-orange-400"
                style="height: 45%"
              ></div>
            </div>
            <div
              class="absolute -top-10 left-1/2 -translate-x-1/2 bg-slate-800 text-white text-xs font-medium py-1 px-2 rounded opacity-0 group-hover/bar:opacity-100 transition-opacity pointer-events-none z-10 whitespace-nowrap"
            >
              $4500
            </div>
          </div>
          <div class="relative flex-1 flex items-end group/bar cursor-pointer">
            <div
              class="w-full bg-slate-100 rounded-t-md relative overflow-hidden transition-all duration-300 group-hover/bar:bg-orange-100"
              style="height: 100%"
            >
              <div
                class="absolute bottom-0 w-full bg-orange-500 rounded-t-md transition-all duration-500 ease-out shadow-[0_0_15px_rgba(249,115,22,0.3)] group-hover/bar:bg-orange-400"
                style="height: 80%"
              ></div>
            </div>
            <div
              class="absolute -top-10 left-1/2 -translate-x-1/2 bg-slate-800 text-white text-xs font-medium py-1 px-2 rounded opacity-0 group-hover/bar:opacity-100 transition-opacity pointer-events-none z-10 whitespace-nowrap"
            >
              $8000
            </div>
          </div>
          <div class="relative flex-1 flex items-end group/bar cursor-pointer">
            <div
              class="w-full bg-slate-100 rounded-t-md relative overflow-hidden transition-all duration-300 group-hover/bar:bg-orange-100"
              style="height: 100%"
            >
              <div
                class="absolute bottom-0 w-full bg-orange-500 rounded-t-md transition-all duration-500 ease-out shadow-[0_0_15px_rgba(249,115,22,0.3)] group-hover/bar:bg-orange-400"
                style="height: 55%"
              ></div>
            </div>
            <div
              class="absolute -top-10 left-1/2 -translate-x-1/2 bg-slate-800 text-white text-xs font-medium py-1 px-2 rounded opacity-0 group-hover/bar:opacity-100 transition-opacity pointer-events-none z-10 whitespace-nowrap"
            >
              $5500
            </div>
          </div>
          <div class="relative flex-1 flex items-end group/bar cursor-pointer">
            <div
              class="w-full bg-slate-100 rounded-t-md relative overflow-hidden transition-all duration-300 group-hover/bar:bg-orange-100"
              style="height: 100%"
            >
              <div
                class="absolute bottom-0 w-full bg-orange-500 rounded-t-md transition-all duration-500 ease-out shadow-[0_0_15px_rgba(249,115,22,0.3)] group-hover/bar:bg-orange-400"
                style="height: 90%"
              ></div>
            </div>
            <div
              class="absolute -top-10 left-1/2 -translate-x-1/2 bg-slate-800 text-white text-xs font-medium py-1 px-2 rounded opacity-0 group-hover/bar:opacity-100 transition-opacity pointer-events-none z-10 whitespace-nowrap"
            >
              $9000
            </div>
          </div>
          <div class="relative flex-1 flex items-end group/bar cursor-pointer">
            <div
              class="w-full bg-slate-100 rounded-t-md relative overflow-hidden transition-all duration-300 group-hover/bar:bg-orange-100"
              style="height: 100%"
            >
              <div
                class="absolute bottom-0 w-full bg-orange-500 rounded-t-md transition-all duration-500 ease-out shadow-[0_0_15px_rgba(249,115,22,0.3)] group-hover/bar:bg-orange-400"
                style="height: 75%"
              ></div>
            </div>
            <div
              class="absolute -top-10 left-1/2 -translate-x-1/2 bg-slate-800 text-white text-xs font-medium py-1 px-2 rounded opacity-0 group-hover/bar:opacity-100 transition-opacity pointer-events-none z-10 whitespace-nowrap"
            >
              $7500
            </div>
          </div>
          <div class="relative flex-1 flex items-end group/bar cursor-pointer">
            <div
              class="w-full bg-slate-100 rounded-t-md relative overflow-hidden transition-all duration-300 group-hover/bar:bg-orange-100"
              style="height: 100%"
            >
              <div
                class="absolute bottom-0 w-full bg-orange-500 rounded-t-md transition-all duration-500 ease-out shadow-[0_0_15px_rgba(249,115,22,0.3)] group-hover/bar:bg-orange-400"
                style="height: 100%"
              ></div>
            </div>
            <div
              class="absolute -top-10 left-1/2 -translate-x-1/2 bg-slate-800 text-white text-xs font-medium py-1 px-2 rounded opacity-0 group-hover/bar:opacity-100 transition-opacity pointer-events-none z-10 whitespace-nowrap"
            >
              $10000
            </div>
          </div>
          <div class="relative flex-1 flex items-end group/bar cursor-pointer">
            <div
              class="w-full bg-slate-100 rounded-t-md relative overflow-hidden transition-all duration-300 group-hover/bar:bg-orange-100"
              style="height: 100%"
            >
              <div
                class="absolute bottom-0 w-full bg-orange-500 rounded-t-md transition-all duration-500 ease-out shadow-[0_0_15px_rgba(249,115,22,0.3)] group-hover/bar:bg-orange-400"
                style="height: 85%"
              ></div>
            </div>
            <div
              class="absolute -top-10 left-1/2 -translate-x-1/2 bg-slate-800 text-white text-xs font-medium py-1 px-2 rounded opacity-0 group-hover/bar:opacity-100 transition-opacity pointer-events-none z-10 whitespace-nowrap"
            >
              $8500
            </div>
          </div>
          <div class="relative flex-1 flex items-end group/bar cursor-pointer">
            <div
              class="w-full bg-slate-100 rounded-t-md relative overflow-hidden transition-all duration-300 group-hover/bar:bg-orange-100"
              style="height: 100%"
            >
              <div
                class="absolute bottom-0 w-full bg-orange-500 rounded-t-md transition-all duration-500 ease-out shadow-[0_0_15px_rgba(249,115,22,0.3)] group-hover/bar:bg-orange-400"
                style="height: 60%"
              ></div>
            </div>
            <div
              class="absolute -top-10 left-1/2 -translate-x-1/2 bg-slate-800 text-white text-xs font-medium py-1 px-2 rounded opacity-0 group-hover/bar:opacity-100 transition-opacity pointer-events-none z-10 whitespace-nowrap"
            >
              $6000
            </div>
          </div>
          <div class="relative flex-1 flex items-end group/bar cursor-pointer">
            <div
              class="w-full bg-slate-100 rounded-t-md relative overflow-hidden transition-all duration-300 group-hover/bar:bg-orange-100"
              style="height: 100%"
            >
              <div
                class="absolute bottom-0 w-full bg-orange-500 rounded-t-md transition-all duration-500 ease-out shadow-[0_0_15px_rgba(249,115,22,0.3)] group-hover/bar:bg-orange-400"
                style="height: 45%"
              ></div>
            </div>
            <div
              class="absolute -top-10 left-1/2 -translate-x-1/2 bg-slate-800 text-white text-xs font-medium py-1 px-2 rounded opacity-0 group-hover/bar:opacity-100 transition-opacity pointer-events-none z-10 whitespace-nowrap"
            >
              $4500
            </div>
          </div>
          <div class="relative flex-1 flex items-end group/bar cursor-pointer">
            <div
              class="w-full bg-slate-100 rounded-t-md relative overflow-hidden transition-all duration-300 group-hover/bar:bg-orange-100"
              style="height: 100%"
            >
              <div
                class="absolute bottom-0 w-full bg-orange-500 rounded-t-md transition-all duration-500 ease-out shadow-[0_0_15px_rgba(249,115,22,0.3)] group-hover/bar:bg-orange-400"
                style="height: 30%"
              ></div>
            </div>
            <div
              class="absolute -top-10 left-1/2 -translate-x-1/2 bg-slate-800 text-white text-xs font-medium py-1 px-2 rounded opacity-0 group-hover/bar:opacity-100 transition-opacity pointer-events-none z-10 whitespace-nowrap"
            >
              $3000
            </div>
          </div>
        </div>
        <div class="flex justify-between mt-4 text-xs font-semibold text-slate-400">
          <span>10:00</span><span>14:00</span><span>18:00</span><span>22:00</span>
        </div>
      </div>
      <div class="flex flex-col gap-6">
        <div
          class="bg-gradient-to-br from-slate-900 to-slate-800 rounded-2xl p-6 shadow-xl text-white relative overflow-hidden"
        >
          <div
            class="absolute top-0 right-0 w-32 h-32 bg-orange-500/20 rounded-full blur-3xl -mr-10 -mt-10"
          ></div>
          <h3 class="text-lg font-bold mb-1 relative z-10">Estado de Cocina</h3>
          <p class="text-slate-400 text-sm mb-6 relative z-10">Tiempo promedio actual</p>
          <div class="flex items-end gap-3 mb-2 relative z-10">
            <span class="text-4xl font-extrabold text-white">14</span
            ><span class="text-lg font-medium text-slate-300 mb-1">min</span>
          </div>
          <div class="w-full bg-slate-700/50 rounded-full h-2 mb-2 relative z-10">
            <div
              class="bg-orange-500 h-2 rounded-full w-2/3 shadow-[0_0_10px_rgba(249,115,22,0.5)]"
            ></div>
          </div>
          <p
            class="text-xs text-slate-400 font-medium relative z-10 flex items-center justify-between"
          >
            <span>Operación óptima</span><span class="text-emerald-400">En tiempo</span>
          </p>
        </div>
        <div
          class="bg-white rounded-2xl border border-slate-200/60 p-6 shadow-[0_8px_30px_rgb(0,0,0,0.04)] flex-1"
        >
          <div class="flex justify-between items-center mb-6">
            <h3 class="text-md font-bold text-slate-800">Productos Top Hoy</h3>
          </div>
          <div class="space-y-4">
            <div
              class="flex items-center justify-between group cursor-pointer hover:bg-slate-50 p-2 -mx-2 rounded-lg transition-colors"
            >
              <div class="flex items-center gap-3">
                <div
                  class="w-8 h-8 rounded-lg bg-orange-50 text-orange-600 flex items-center justify-center font-bold text-xs group-hover:bg-orange-100 transition-colors"
                >
                  #1
                </div>
                <div>
                  <p class="text-sm font-semibold text-slate-800">Hamburguesa Doble</p>
                  <p class="text-xs text-slate-500">42 vendidos</p>
                </div>
              </div>
              <span class="text-sm font-bold text-slate-700">$12.50</span>
            </div>
            <div
              class="flex items-center justify-between group cursor-pointer hover:bg-slate-50 p-2 -mx-2 rounded-lg transition-colors"
            >
              <div class="flex items-center gap-3">
                <div
                  class="w-8 h-8 rounded-lg bg-orange-50 text-orange-600 flex items-center justify-center font-bold text-xs group-hover:bg-orange-100 transition-colors"
                >
                  #2
                </div>
                <div>
                  <p class="text-sm font-semibold text-slate-800">Pizza Pepperoni</p>
                  <p class="text-xs text-slate-500">38 vendidos</p>
                </div>
              </div>
              <span class="text-sm font-bold text-slate-700">$18.00</span>
            </div>
            <div
              class="flex items-center justify-between group cursor-pointer hover:bg-slate-50 p-2 -mx-2 rounded-lg transition-colors"
            >
              <div class="flex items-center gap-3">
                <div
                  class="w-8 h-8 rounded-lg bg-orange-50 text-orange-600 flex items-center justify-center font-bold text-xs group-hover:bg-orange-100 transition-colors"
                >
                  #3
                </div>
                <div>
                  <p class="text-sm font-semibold text-slate-800">Alitas BBQ (12)</p>
                  <p class="text-xs text-slate-500">25 vendidos</p>
                </div>
              </div>
              <span class="text-sm font-bold text-slate-700">$14.00</span>
            </div>
            <div
              class="flex items-center justify-between group cursor-pointer hover:bg-slate-50 p-2 -mx-2 rounded-lg transition-colors"
            >
              <div class="flex items-center gap-3">
                <div
                  class="w-8 h-8 rounded-lg bg-orange-50 text-orange-600 flex items-center justify-center font-bold text-xs group-hover:bg-orange-100 transition-colors"
                >
                  #4
                </div>
                <div>
                  <p class="text-sm font-semibold text-slate-800">Cerveza Artesanal</p>
                  <p class="text-xs text-slate-500">56 vendidos</p>
                </div>
              </div>
              <span class="text-sm font-bold text-slate-700">$6.50</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div
      class="mt-8 bg-white rounded-2xl border border-slate-200/60 shadow-[0_8px_30px_rgb(0,0,0,0.04)] overflow-hidden"
    >
      <div
        class="p-6 border-b border-slate-100 flex flex-col sm:flex-row sm:items-center justify-between gap-4"
      >
        <div>
          <h3 class="text-lg font-bold text-slate-800">Órdenes Recientes</h3>
          <p class="text-sm text-slate-500">Últimas transacciones procesadas</p>
        </div>
        <button
          class="text-sm font-semibold text-orange-600 hover:text-orange-700 bg-orange-50 hover:bg-orange-100 px-4 py-2 rounded-lg transition-colors active:scale-95 flex items-center gap-2"
        >
          Ver todas
          <ArrowUpRight :size="16" />
        </button>
      </div>
      <div class="overflow-x-auto">
        <table class="w-full text-left border-collapse">
          <thead>
            <tr
              class="bg-slate-50/50 text-xs uppercase tracking-wider text-slate-500 font-semibold border-b border-slate-100"
            >
              <th class="p-4 pl-6">ID Orden</th>
              <th class="p-4">Cliente</th>
              <th class="p-4">Tipo</th>
              <th class="p-4">Monto</th>
              <th class="p-4">Estado</th>
              <th class="p-4 pr-6 text-right">Acciones</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-100 text-sm">
            <tr class="hover:bg-slate-50/80 transition-colors group">
              <td class="p-4 pl-6 font-semibold text-slate-800">#ORD-8092</td>
              <td class="p-4 font-medium text-slate-600">Mesa 04</td>
              <td class="p-4">
                <span
                  class="px-2.5 py-1 rounded-md bg-slate-100 text-slate-600 text-xs font-semibold"
                  >Local</span
                >
              </td>
              <td class="p-4 font-bold text-slate-800">$450.00</td>
              <td class="p-4">
                <span
                  class="inline-flex items-center px-2.5 py-1 rounded-full text-xs font-bold border bg-orange-50 text-orange-600 border-orange-200"
                  ><svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="14"
                    height="14"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    class="lucide lucide-clock mr-1.5"
                    aria-hidden="true"
                  >
                    <circle cx="12" cy="12" r="10"></circle>
                    <polyline points="12 6 12 12 16 14"></polyline></svg
                  >En preparación</span
                >
              </td>
              <td class="p-4 pr-6 text-right">
                <div
                  class="flex items-center justify-end gap-2 opacity-0 group-hover:opacity-100 transition-opacity"
                >
                  <button
                    class="p-1.5 text-slate-400 hover:text-orange-500 hover:bg-orange-50 rounded-lg transition-colors"
                  >
                    <Search :size="18" /></button
                  ><button
                    class="p-1.5 text-slate-400 hover:text-slate-800 hover:bg-slate-200 rounded-lg transition-colors"
                  >
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      width="18"
                      height="18"
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="currentColor"
                      stroke-width="2"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      class="lucide lucide-ellipsis-vertical"
                      aria-hidden="true"
                    >
                      <circle cx="12" cy="12" r="1"></circle>
                      <circle cx="12" cy="5" r="1"></circle>
                      <circle cx="12" cy="19" r="1"></circle>
                    </svg>
                  </button>
                </div>
              </td>
            </tr>
            <tr class="hover:bg-slate-50/80 transition-colors group">
              <td class="p-4 pl-6 font-semibold text-slate-800">#ORD-8091</td>
              <td class="p-4 font-medium text-slate-600">Juan Pérez</td>
              <td class="p-4">
                <span
                  class="px-2.5 py-1 rounded-md bg-slate-100 text-slate-600 text-xs font-semibold"
                  >Delivery</span
                >
              </td>
              <td class="p-4 font-bold text-slate-800">$285.50</td>
              <td class="p-4">
                <span
                  class="inline-flex items-center px-2.5 py-1 rounded-full text-xs font-bold border bg-blue-50 text-blue-600 border-blue-200"
                  ><svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="14"
                    height="14"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    class="lucide lucide-bell mr-1.5"
                    aria-hidden="true"
                  >
                    <path d="M10.268 21a2 2 0 0 0 3.464 0"></path>
                    <path
                      d="M3.262 15.326A1 1 0 0 0 4 17h16a1 1 0 0 0 .74-1.673C19.41 13.956 18 12.499 18 8A6 6 0 0 0 6 8c0 4.499-1.411 5.956-2.738 7.326"
                    ></path></svg
                  >Listo</span
                >
              </td>
              <td class="p-4 pr-6 text-right">
                <div
                  class="flex items-center justify-end gap-2 opacity-0 group-hover:opacity-100 transition-opacity"
                >
                  <button
                    class="p-1.5 text-slate-400 hover:text-orange-500 hover:bg-orange-50 rounded-lg transition-colors"
                  >
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      width="18"
                      height="18"
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="currentColor"
                      stroke-width="2"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      class="lucide lucide-search"
                      aria-hidden="true"
                    >
                      <path d="m21 21-4.34-4.34"></path>
                      <circle cx="11" cy="11" r="8"></circle>
                    </svg></button
                  ><button
                    class="p-1.5 text-slate-400 hover:text-slate-800 hover:bg-slate-200 rounded-lg transition-colors"
                  >
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      width="18"
                      height="18"
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="currentColor"
                      stroke-width="2"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      class="lucide lucide-ellipsis-vertical"
                      aria-hidden="true"
                    >
                      <circle cx="12" cy="12" r="1"></circle>
                      <circle cx="12" cy="5" r="1"></circle>
                      <circle cx="12" cy="19" r="1"></circle>
                    </svg>
                  </button>
                </div>
              </td>
            </tr>
            <tr class="hover:bg-slate-50/80 transition-colors group">
              <td class="p-4 pl-6 font-semibold text-slate-800">#ORD-8090</td>
              <td class="p-4 font-medium text-slate-600">María G.</td>
              <td class="p-4">
                <span
                  class="px-2.5 py-1 rounded-md bg-slate-100 text-slate-600 text-xs font-semibold"
                  >Para llevar</span
                >
              </td>
              <td class="p-4 font-bold text-slate-800">$120.00</td>
              <td class="p-4">
                <span
                  class="inline-flex items-center px-2.5 py-1 rounded-full text-xs font-bold border bg-emerald-50 text-emerald-600 border-emerald-200"
                  ><svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="14"
                    height="14"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    class="lucide lucide-circle-check mr-1.5"
                    aria-hidden="true"
                  >
                    <circle cx="12" cy="12" r="10"></circle>
                    <path d="m9 12 2 2 4-4"></path></svg
                  >Entregado</span
                >
              </td>
              <td class="p-4 pr-6 text-right">
                <div
                  class="flex items-center justify-end gap-2 opacity-0 group-hover:opacity-100 transition-opacity"
                >
                  <button
                    class="p-1.5 text-slate-400 hover:text-orange-500 hover:bg-orange-50 rounded-lg transition-colors"
                  >
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      width="18"
                      height="18"
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="currentColor"
                      stroke-width="2"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      class="lucide lucide-search"
                      aria-hidden="true"
                    >
                      <path d="m21 21-4.34-4.34"></path>
                      <circle cx="11" cy="11" r="8"></circle>
                    </svg></button
                  ><button
                    class="p-1.5 text-slate-400 hover:text-slate-800 hover:bg-slate-200 rounded-lg transition-colors"
                  >
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      width="18"
                      height="18"
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="currentColor"
                      stroke-width="2"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      class="lucide lucide-ellipsis-vertical"
                      aria-hidden="true"
                    >
                      <circle cx="12" cy="12" r="1"></circle>
                      <circle cx="12" cy="5" r="1"></circle>
                      <circle cx="12" cy="19" r="1"></circle>
                    </svg>
                  </button>
                </div>
              </td>
            </tr>
            <tr class="hover:bg-slate-50/80 transition-colors group">
              <td class="p-4 pl-6 font-semibold text-slate-800">#ORD-8089</td>
              <td class="p-4 font-medium text-slate-600">Mesa 12</td>
              <td class="p-4">
                <span
                  class="px-2.5 py-1 rounded-md bg-slate-100 text-slate-600 text-xs font-semibold"
                  >Local</span
                >
              </td>
              <td class="p-4 font-bold text-slate-800">$890.00</td>
              <td class="p-4">
                <span
                  class="inline-flex items-center px-2.5 py-1 rounded-full text-xs font-bold border bg-emerald-50 text-emerald-600 border-emerald-200"
                  ><svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="14"
                    height="14"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    class="lucide lucide-circle-check mr-1.5"
                    aria-hidden="true"
                  >
                    <circle cx="12" cy="12" r="10"></circle>
                    <path d="m9 12 2 2 4-4"></path></svg
                  >Entregado</span
                >
              </td>
              <td class="p-4 pr-6 text-right">
                <div
                  class="flex items-center justify-end gap-2 opacity-0 group-hover:opacity-100 transition-opacity"
                >
                  <button
                    class="p-1.5 text-slate-400 hover:text-orange-500 hover:bg-orange-50 rounded-lg transition-colors"
                  >
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      width="18"
                      height="18"
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="currentColor"
                      stroke-width="2"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      class="lucide lucide-search"
                      aria-hidden="true"
                    >
                      <path d="m21 21-4.34-4.34"></path>
                      <circle cx="11" cy="11" r="8"></circle>
                    </svg></button
                  ><button
                    class="p-1.5 text-slate-400 hover:text-slate-800 hover:bg-slate-200 rounded-lg transition-colors"
                  >
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      width="18"
                      height="18"
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="currentColor"
                      stroke-width="2"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      class="lucide lucide-ellipsis-vertical"
                      aria-hidden="true"
                    >
                      <circle cx="12" cy="12" r="1"></circle>
                      <circle cx="12" cy="5" r="1"></circle>
                      <circle cx="12" cy="19" r="1"></circle>
                    </svg>
                  </button>
                </div>
              </td>
            </tr>
            <tr class="hover:bg-slate-50/80 transition-colors group">
              <td class="p-4 pl-6 font-semibold text-slate-800">#ORD-8088</td>
              <td class="p-4 font-medium text-slate-600">Carlos R.</td>
              <td class="p-4">
                <span
                  class="px-2.5 py-1 rounded-md bg-slate-100 text-slate-600 text-xs font-semibold"
                  >Delivery</span
                >
              </td>
              <td class="p-4 font-bold text-slate-800">$340.00</td>
              <td class="p-4">
                <span
                  class="inline-flex items-center px-2.5 py-1 rounded-full text-xs font-bold border bg-red-50 text-red-600 border-red-200"
                  ><svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="14"
                    height="14"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    class="lucide lucide-circle-x mr-1.5"
                    aria-hidden="true"
                  >
                    <circle cx="12" cy="12" r="10"></circle>
                    <path d="m15 9-6 6"></path>
                    <path d="m9 9 6 6"></path></svg
                  >Cancelado</span
                >
              </td>
              <td class="p-4 pr-6 text-right">
                <div
                  class="flex items-center justify-end gap-2 opacity-0 group-hover:opacity-100 transition-opacity"
                >
                  <button
                    class="p-1.5 text-slate-400 hover:text-orange-500 hover:bg-orange-50 rounded-lg transition-colors"
                  >
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      width="18"
                      height="18"
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="currentColor"
                      stroke-width="2"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      class="lucide lucide-search"
                      aria-hidden="true"
                    >
                      <path d="m21 21-4.34-4.34"></path>
                      <circle cx="11" cy="11" r="8"></circle>
                    </svg></button
                  ><button
                    class="p-1.5 text-slate-400 hover:text-slate-800 hover:bg-slate-200 rounded-lg transition-colors"
                  >
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      width="18"
                      height="18"
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="currentColor"
                      stroke-width="2"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      class="lucide lucide-ellipsis-vertical"
                      aria-hidden="true"
                    >
                      <circle cx="12" cy="12" r="1"></circle>
                      <circle cx="12" cy="5" r="1"></circle>
                      <circle cx="12" cy="19" r="1"></circle>
                    </svg>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>
