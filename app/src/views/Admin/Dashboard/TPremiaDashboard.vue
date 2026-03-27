<script setup lang="ts">
import { ref } from 'vue'
import VueApexCharts from 'vue3-apexcharts'
import type { ApexOptions } from 'apexcharts'
import {
  Gem,
  Receipt,
  Users,
  Store,
  RefreshCcw,
  ArrowUpRight,
  ArrowDownRight,
  MoreVertical,
  Award,
  Sparkles,
  TrendingUp,
} from 'lucide-vue-next'

// Mock Data for KPI Cards
const kpiData = [
  {
    title: 'Gemas generadas este mes',
    value: '945',
    variation: '+12.5%',
    isPositive: true,
    icon: Gem,
    color: 'text-pink-600',
    bgColor: 'bg-pink-50',
    glowColor: 'group-hover:shadow-pink-200',
  },
  {
    title: 'Total de canjes',
    value: '184',
    variation: '+5.4%',
    isPositive: true,
    icon: Receipt,
    color: 'text-fuchsia-600',
    bgColor: 'bg-fuchsia-50',
    glowColor: 'group-hover:shadow-fuchsia-200',
  },
  {
    title: 'Usuarios activos',
    value: '842',
    variation: '+15.3%',
    isPositive: true,
    icon: Users,
    color: 'text-violet-600',
    bgColor: 'bg-violet-50',
    glowColor: 'group-hover:shadow-violet-200',
  },
]

// Charts Configuration
const lineChartOptions: ApexOptions = {
  chart: {
    id: 'gems-daily',
    toolbar: { show: false },
    fontFamily: 'Inter, sans-serif',
    animations: {
      enabled: true,
      speed: 800,
    },
  },
  stroke: {
    curve: 'smooth' as const,
    width: 4,
  },
  colors: ['#db2777'], // Pink 600
  fill: {
    type: 'gradient',
    gradient: {
      shadeIntensity: 1,
      opacityFrom: 0.45,
      opacityTo: 0.05,
      stops: [50, 100],
      colorStops: [
        { offset: 0, color: '#db2777', opacity: 0.4 },
        { offset: 100, color: '#fdf2f8', opacity: 0.1 },
      ],
    },
  },
  xaxis: {
    categories: ['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb', 'Dom'],
    axisBorder: { show: false },
    axisTicks: { show: false },
  },
  yaxis: {
    labels: {
      formatter: (val: number) => val.toLocaleString(),
    },
  },
  grid: {
    borderColor: '#f1f5f9',
    strokeDashArray: 4,
  },
  markers: {
    size: 5,
    colors: ['#db2777'],
    strokeColors: '#fff',
    strokeWidth: 2,
    hover: { size: 7 },
  },
  dataLabels: { enabled: false },
}

const lineChartSeries = [
  {
    name: 'Gemas',
    data: [120, 180, 150, 220, 195, 250, 270],
  },
]

const barChartOptions: ApexOptions = {
  chart: {
    id: 'top-products',
    toolbar: { show: false },
    fontFamily: 'Inter, sans-serif',
  },
  plotOptions: {
    bar: {
      borderRadius: 8,
      horizontal: true,
      barHeight: '55%',
      distributed: true,
    },
  },
  colors: [
    '#db2777', // Pink 600
    '#c026d3', // Fuchsia 600
    '#9333ea', // Purple 600
    '#7c3aed', // Violet 600
    '#4f46e5', // Indigo 600
    '#db2777',
    '#c026d3',
    '#9333ea',
    '#7c3aed',
    '#4f46e5',
  ],
  xaxis: {
    categories: [
      'Orden de Tacos al Pastor',
      'Gringa de Asada',
      'Taco Campechano',
      'Quesadilla con Carne',
      'Refresco 600ml',
      'Orden de Cebollitas',
      'Agua de Horchata Grande',
      'Taco de Lengua',
      'Combo Familiar',
      'Postre de la Casa',
    ],
  },
  grid: {
    borderColor: '#f1f5f9',
    strokeDashArray: 4,
    xaxis: { lines: { show: true } },
    yaxis: { lines: { show: false } },
  },
  dataLabels: {
    enabled: true,
    textAnchor: 'start',
    style: { colors: ['#fff'] },
    formatter: function (val, opt) {
      return opt.w.globals.labels[opt.dataPointIndex] + ': ' + val
    },
    offsetX: 0,
  },
  legend: { show: false },
}

const barChartSeries = [
  {
    name: 'Canjes',
    data: [450, 380, 320, 290, 275, 240, 210, 195, 180, 150],
  },
]

const areaChartOptions: ApexOptions = {
  chart: {
    id: 'redemptions-daily',
    toolbar: { show: false },
    fontFamily: 'Inter, sans-serif',
  },
  stroke: {
    curve: 'smooth' as const,
    width: 3,
  },
  colors: ['#c026d3'],
  fill: {
    type: 'gradient',
    gradient: {
      shadeIntensity: 1,
      opacityFrom: 0.4,
      opacityTo: 0.1,
      colorStops: [
        { offset: 0, color: '#c026d3', opacity: 0.4 },
        { offset: 100, color: '#fae8ff', opacity: 0.1 },
      ],
    },
  },
  xaxis: {
    categories: ['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb', 'Dom'],
    axisBorder: { show: false },
    axisTicks: { show: false },
  },
  grid: {
    borderColor: '#f1f5f9',
    strokeDashArray: 4,
  },
  dataLabels: { enabled: false },
}

const areaChartSeries = [
  {
    name: 'Canjes',
    data: [45, 62, 58, 89, 75, 110, 125],
  },
]

// Ranking Data
const topUsers = [
  {
    rank: 1,
    name: 'Alejandro Rivera',
    gems: 85,
    lastMove: 'Hace 5 min',
    avatar: 'AR',
    isVIP: true,
  },
  {
    rank: 2,
    name: 'Sofía Martínez',
    gems: 72,
    lastMove: 'Hace 12 min',
    avatar: 'SM',
    isVIP: true,
  },
  {
    rank: 3,
    name: 'Carlos Mendoza',
    gems: 68,
    lastMove: 'Hace 45 min',
    avatar: 'CM',
    isVIP: true,
  },
  {
    rank: 4,
    name: 'Lucía Fernández',
    gems: 54,
    lastMove: 'Hace 1h',
    avatar: 'LF',
    isVIP: false,
  },
  { rank: 5, name: 'Roberto Gómez', gems: 49, lastMove: 'Ayer', avatar: 'RG', isVIP: false },
]
</script>

<template>
  <div class="flex-1 overflow-y-auto p-4 md:p-8 bg-[#fdf2f8]/30 min-h-screen relative">
    <!-- Decorative background blobs -->
    <div
      class="absolute top-0 right-0 -translate-y-1/2 translate-x-1/2 w-[500px] h-[500px] bg-pink-100/40 rounded-full blur-3xl -z-10"
    ></div>
    <div
      class="absolute bottom-0 left-0 translate-y-1/2 -translate-x-1/2 w-[400px] h-[400px] bg-violet-100/40 rounded-full blur-3xl -z-10"
    ></div>

    <!-- Header -->
    <header class="mb-10 animate-fade-in">
      <div class="flex items-center gap-3 mb-4">
        <div class="h-1.5 w-12 bg-gradient-to-r from-pink-500 to-violet-500 rounded-full"></div>
        <span
          class="text-pink-600 font-bold tracking-widest uppercase text-xs flex items-center gap-2"
        >
          <Sparkles :size="14" />
          Dashboard
        </span>
      </div>

      <div class="flex items-end justify-between gap-4">
        <div>
          <h1
            class="text-5xl font-black text-slate-900 mb-2 tracking-tighter bg-clip-text text-transparent bg-gradient-to-r from-slate-900 via-pink-700 to-slate-900"
          >
            TPremia <span class="text-pink-500 text-3xl"></span>
          </h1>
          <p class="text-slate-500 max-w-2xl text-lg font-medium">
            Métricas estratégicas y análisis de rendimiento en tiempo real.
          </p>
        </div>
        <div
          class="hidden md:flex bg-white/60 backdrop-blur-md border border-white p-1 rounded-2xl shadow-sm"
        >
          <button
            class="px-6 py-2.5 rounded-xl bg-pink-500 text-white font-bold text-sm shadow-lg shadow-pink-200 transition-all hover:scale-105 active:scale-95"
          >
            Generar Reporte
          </button>
        </div>
      </div>
    </header>

    <!-- Section 1: KPI Cards -->
    <section class="mb-12">
      <div class="flex items-center justify-between mb-8">
        <h2 class="text-xl font-bold text-slate-800 flex items-center gap-3">
          <div class="p-2 bg-pink-100 rounded-lg text-pink-600"><TrendingUp :size="20" /></div>
          Rendimiento General
        </h2>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-3 gap-6 md:gap-8">
        <div
          v-for="(kpi, index) in kpiData"
          :key="index"
          class="bg-white/80 backdrop-blur-sm p-7 rounded-[2rem] border border-white shadow-[0_8px_30px_rgb(0,0,0,0.04)] hover:shadow-[0_20px_50px_rgba(236,72,153,0.15)] transition-all duration-500 group relative overflow-hidden animate-slide-up"
          :style="{ animationDelay: `${index * 100}ms` }"
        >
          <!-- Hover Glow Effect -->
          <div
            class="absolute inset-0 bg-gradient-to-br from-white via-white to-pink-50/30 opacity-0 group-hover:opacity-100 transition-opacity duration-500"
          ></div>

          <div class="relative z-10">
            <div class="flex justify-between items-start mb-6">
              <div
                :class="[kpi.bgColor, kpi.color]"
                class="p-4 rounded-2xl transition-all duration-500 group-hover:scale-110 shadow-sm"
              >
                <component :is="kpi.icon" :size="28" />
              </div>
              <div
                :class="
                  kpi.isPositive
                    ? 'bg-emerald-100/80 text-emerald-700'
                    : 'bg-red-100/80 text-red-700'
                "
                class="flex items-center gap-1 text-xs font-black px-3 py-1 rounded-full backdrop-blur-sm"
              >
                <ArrowUpRight v-if="kpi.isPositive" :size="14" />
                <ArrowDownRight v-else :size="14" />
                {{ kpi.variation }}
              </div>
            </div>
            <div>
              <p
                class="text-4xl font-black text-slate-900 mb-2 tracking-tighter tabular-nums group-hover:text-pink-600 transition-colors duration-300"
              >
                {{ kpi.value }}
              </p>
              <h4
                class="text-slate-500 text-xs font-bold uppercase tracking-widest opacity-80 group-hover:opacity-100 transition-opacity"
              >
                {{ kpi.title }}
              </h4>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Section 2: Charts -->
    <section class="mb-12">
      <div class="grid grid-cols-1 xl:grid-cols-2 gap-8">
        <!-- Gemas generadas por día -->
        <div
          class="bg-white/90 backdrop-blur-sm p-8 rounded-[2.5rem] border border-white shadow-[0_8px_30px_rgb(0,0,0,0.04)] animate-slide-up"
          style="animation-delay: 300ms"
        >
          <div class="flex justify-between items-center mb-10">
            <div>
              <h3 class="text-xl font-black text-slate-900 tracking-tight">Gemas generadas</h3>
              <p class="text-sm text-slate-400 font-medium mt-1">Actividad semanal consolidada</p>
            </div>
            <div class="flex gap-2">
              <button
                class="w-10 h-10 flex items-center justify-center bg-slate-50 hover:bg-pink-50 hover:text-pink-500 transition-all rounded-xl text-slate-400"
              >
                <RefreshCcw :size="18" />
              </button>
            </div>
          </div>
          <div class="h-80">
            <VueApexCharts
              type="line"
              height="100%"
              :options="lineChartOptions"
              :series="lineChartSeries"
            />
          </div>
        </div>

        <!-- Top Productos -->
        <div
          class="bg-white/90 backdrop-blur-sm p-8 rounded-[2.5rem] border border-white shadow-[0_8px_30px_rgb(0,0,0,0.04)] animate-slide-up"
          style="animation-delay: 400ms"
        >
          <div class="flex justify-between items-center mb-10">
            <div>
              <h3 class="text-xl font-black text-slate-900 tracking-tight">
                Productos más canjeados
              </h3>
              <p class="text-sm text-slate-400 font-medium mt-1">Top 10 preferencias de usuarios</p>
            </div>
            <div
              class="w-12 h-12 bg-pink-100 flex items-center justify-center rounded-2xl shadow-inner"
            >
              <Award class="text-pink-600" :size="24" />
            </div>
          </div>
          <div class="h-80">
            <VueApexCharts
              type="bar"
              height="100%"
              :options="barChartOptions"
              :series="barChartSeries"
            />
          </div>
        </div>

        <!-- Canjes realizados Full Width -->
        <div
          class="bg-white/90 backdrop-blur-sm p-10 rounded-[2.5rem] border border-white shadow-[0_8px_30px_rgb(0,0,0,0.04)] xl:col-span-2 animate-slide-up"
          style="animation-delay: 500ms"
        >
          <div class="flex justify-between items-center mb-10">
            <div>
              <h3 class="text-2xl font-black text-slate-900 tracking-tight">Histórico de Canjes</h3>
              <p class="text-slate-400 font-medium mt-1">Volumen de gemas redimidas</p>
            </div>
            <div
              class="px-5 py-2 bg-fuchsia-50 text-fuchsia-700 rounded-full text-xs font-bold border border-fuchsia-100 flex items-center gap-2"
            >
              <div class="w-2 h-2 rounded-full bg-fuchsia-500 animate-pulse"></div>
              En vivo
            </div>
          </div>
          <div class="h-96">
            <VueApexCharts
              type="area"
              height="100%"
              :options="areaChartOptions"
              :series="areaChartSeries"
            />
          </div>
        </div>
      </div>
    </section>

    <!-- Section 3: Ranking Table -->
    <section class="animate-slide-up" style="animation-delay: 600ms">
      <div
        class="bg-white/90 backdrop-blur-sm rounded-[2.5rem] border border-white shadow-[0_8px_30px_rgb(0,0,0,0.04)] overflow-hidden"
      >
        <div class="p-10 flex items-center justify-between border-b border-pink-50">
          <div>
            <h2 class="text-2xl font-black text-slate-900 tracking-tight">Ranking de Usuarios</h2>
            <p class="text-slate-400 font-medium mt-1">Nuestros clientes más leales</p>
          </div>
          <button
            class="group text-sm font-black text-pink-600 px-6 py-3 bg-pink-50 rounded-2xl hover:bg-pink-100 transition-all flex items-center gap-2"
          >
            Ver informe completo
            <ArrowUpRight
              :size="16"
              class="transition-transform group-hover:translate-x-0.5 group-hover:-translate-y-0.5"
            />
          </button>
        </div>

        <div class="overflow-x-auto px-4 pb-4">
          <table class="w-full text-left">
            <thead>
              <tr class="text-[10px] font-black text-slate-400 uppercase tracking-[0.2em]">
                <th class="px-8 py-6">Rank</th>
                <th class="px-8 py-6">Usuario</th>
                <th class="px-8 py-6 text-center">Gemas</th>
                <th class="px-8 py-6">Actividad</th>
                <th class="px-8 py-6 text-right">Perfil</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-slate-50">
              <tr
                v-for="user in topUsers"
                :key="user.rank"
                class="hover:bg-pink-50/30 transition-all duration-300 group"
              >
                <td class="px-8 py-6">
                  <div
                    class="flex items-center justify-center w-10 h-10 rounded-2xl font-black text-sm transition-all group-hover:scale-110 shadow-sm"
                    :class="
                      user.rank === 1
                        ? 'bg-pink-500 text-white shadow-pink-200 shadow-lg'
                        : user.rank === 2
                          ? 'bg-fuchsia-400 text-white'
                          : user.rank === 3
                            ? 'bg-violet-400 text-white'
                            : 'bg-slate-100 text-slate-500'
                    "
                  >
                    #{{ user.rank }}
                  </div>
                </td>
                <td class="px-8 py-6">
                  <div class="flex items-center gap-4">
                    <div class="relative">
                      <div
                        class="w-12 h-12 rounded-2xl bg-gradient-to-br from-slate-100 to-slate-200 flex items-center justify-center font-black text-slate-700 text-xs shadow-inner overflow-hidden border-2 border-white"
                      >
                        {{ user.avatar }}
                      </div>
                      <div
                        v-if="user.isVIP"
                        class="absolute -top-1.5 -right-1.5 bg-pink-500 text-white p-1 rounded-lg shadow-lg border-2 border-white"
                      >
                        <Award :size="12" />
                      </div>
                    </div>
                    <div>
                      <div class="flex items-center gap-2">
                        <p
                          class="font-bold text-slate-900 text-lg tracking-tight group-hover:text-pink-600 transition-colors"
                        >
                          {{ user.name }}
                        </p>
                        <span
                          v-if="user.isVIP"
                          class="px-2 py-0.5 bg-pink-100 text-pink-700 text-[10px] font-black rounded-lg uppercase"
                          >VIP</span
                        >
                      </div>
                      <p
                        class="text-xs text-slate-400 font-bold uppercase tracking-widest opacity-60"
                      >
                        ID #{{ 1000 + user.rank }}
                      </p>
                    </div>
                  </div>
                </td>
                <td class="px-8 py-6">
                  <div
                    class="flex items-center justify-center gap-2 px-4 py-2 bg-slate-50 rounded-2xl group-hover:bg-white transition-colors"
                  >
                    <Gem :size="16" class="text-pink-500 group-hover:animate-bounce" />
                    <span class="font-black text-slate-800 text-lg tabular-nums">{{
                      user.gems.toLocaleString()
                    }}</span>
                  </div>
                </td>
                <td class="px-8 py-6">
                  <div class="flex items-center gap-2">
                    <div
                      class="w-1.5 h-1.5 rounded-full bg-emerald-500 group-hover:scale-150 transition-transform"
                    ></div>
                    <span class="text-sm font-bold text-slate-500">{{ user.lastMove }}</span>
                  </div>
                </td>
                <td class="px-8 py-6 text-right">
                  <button
                    class="px-4 py-2 text-slate-400 hover:text-pink-600 hover:bg-pink-50 rounded-xl transition-all font-bold text-xs uppercase tracking-widest border border-transparent hover:border-pink-100"
                  >
                    Detalle
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800;900&display=swap');

:deep(.apexcharts-canvas) {
  margin: 0 auto;
}

:deep(.apexcharts-tooltip) {
  border-radius: 20px !important;
  border: none !important;
  box-shadow:
    0 20px 25px -5px rgb(0 0 0 / 0.1),
    0 8px 10px -6px rgb(0 0 0 / 0.1) !important;
  background: rgba(255, 255, 255, 0.9) !important;
  backdrop-filter: blur(8px);
}

:deep(.apexcharts-tooltip-title) {
  background: rgba(236, 72, 153, 0.05) !important;
  border-bottom: 1px solid rgba(236, 72, 153, 0.1) !important;
  font-weight: 800 !important;
  color: #db2777 !important;
  padding: 8px 12px !important;
  border-top-left-radius: 20px !important;
  border-top-right-radius: 20px !important;
}

/* Animations */
@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.animate-fade-in {
  animation: fadeIn 0.8s ease-out forwards;
}

.animate-slide-up {
  animation: slideUp 0.8s cubic-bezier(0.16, 1, 0.3, 1) forwards;
  opacity: 0;
}

/* Custom Scrollbar */
::-webkit-scrollbar {
  width: 6px;
}

::-webkit-scrollbar-track {
  background: transparent;
}

::-webkit-scrollbar-thumb {
  background: #fbcfe8;
  border-radius: 10px;
}

::-webkit-scrollbar-thumb:hover {
  background: #f9a8d4;
}
</style>
