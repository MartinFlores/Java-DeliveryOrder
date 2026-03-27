<script setup lang="ts">
import { storeToRefs } from 'pinia'
import { useWizardStore } from '../store'

const wizardStore = useWizardStore()
const { brandColor } = storeToRefs(wizardStore)

const colors = [
  { name: 'Naranja', value: '#FF6B35', gradient: 'from-orange-500 to-orange-600' },
  { name: 'Azul', value: '#3B82F6', gradient: 'from-blue-500 to-blue-600' },
  { name: 'Verde', value: '#10B981', gradient: 'from-green-500 to-green-600' },
  { name: 'Morado', value: '#8B5CF6', gradient: 'from-purple-500 to-purple-600' },
  { name: 'Rosa', value: '#EC4899', gradient: 'from-pink-500 to-pink-600' },
  { name: 'Rojo', value: '#EF4444', gradient: 'from-red-500 to-red-600' },
  { name: 'Negro', value: '#1F2937', gradient: 'from-gray-800 to-gray-900' },
  { name: 'Gris', value: '#6B7280', gradient: 'from-gray-500 to-gray-600' },
  { name: 'Amarillo', value: '#F59E0B', gradient: 'from-amber-500 to-amber-600' },
  { name: 'Tinto', value: '#991B1B', gradient: 'from-red-800 to-red-900' },
  { name: 'Dorado', value: '#CA8A04', gradient: 'from-yellow-600 to-yellow-700' },
  { name: 'Plateado', value: '#71717A', gradient: 'from-zinc-500 to-zinc-600' },
]
</script>

<template>
  <div class="flex-col items-center justify-center text-left animate-fade flex py-6 px-4">
    <div class="w-full max-w-[448px]">
      <div
        class="w-16 h-16 orange-gradient rounded-2xl flex items-center justify-center mb-5 shadow-lg shadow-orange-300/50"
      >
        <span class="material-symbols-outlined text-white" style="font-size: 32px">palette</span>
      </div>
      <h2 class="md:text-3xl text-xl font-black text-slate-800 md:mb-2">Personaliza tu marca</h2>
      <p class="text-slate-500 md:text-[16px] text-[12px] mb-2">
        Elige el color que mejor represente a tu marca.
      </p>

      <div class="flex flex-col gap-4">
        <!-- Selector de colores -->

        <div
          class="flex space-x-4 overflow-x-auto bg-slate-50 rounded-2xl p-4 border-2 border-slate-200 scrollbar-hidden"
        >
          <button
            v-for="color in colors"
            :key="color.value"
            type="button"
            @click="brandColor = color.value"
            class="flex-shrink-0 w-20 h-24 flex flex-col items-center justify-between p-2 rounded-xl border-2 transition-all duration-300 relative bg-white"
            :style="
              brandColor === color.value ? { borderColor: color.value } : { borderColor: '#E5E7EB' }
            "
          >
            <!-- Color cuadrado arriba -->
            <div
              class="w-12 h-12 rounded-md shadow-md mb-2 relative bg-gradient-to-br"
              :class="color.gradient"
            >
              <!-- Check centrado solo si está seleccionado -->
              <span
                v-if="brandColor === color.value"
                class="material-symbols-outlined text-white absolute inset-0 m-auto w-6 h-6 flex items-center justify-center"
                style="display: flex; align-items: center; justify-content: center"
                >check_circle</span
              >
            </div>

            <!-- Nombre debajo, cambia al color seleccionado -->
            <span
              class="font-bold text-sm text-center"
              :style="brandColor === color.value ? { color: color.value } : { color: '#374151' }"
            >
              {{ color.name }}
            </span>
          </button>
        </div>

        <!-- Preview -->
        <div class="bg-slate-100 rounded-2xl p-6 border-2 border-slate-200">
          <p class="text-xs font-bold text-slate-500 mb-4 uppercase tracking-wider">Vista previa</p>
          <div class="bg-white rounded-xl shadow-lg p-6 space-y-4">
            <div class="flex items-center w-full gap-3">
              <div
                :style="{ backgroundColor: brandColor }"
                class="w-12 h-12 rounded-xl flex items-center justify-center shadow-md"
              >
                <span class="material-symbols-outlined text-white">store</span>
              </div>
              <div>
                <h3 class="font-bold text-slate-800">
                  {{ wizardStore.businessName || 'Tu Negocio' }}
                </h3>
                <p class="text-xs text-slate-500">Punto de Venta</p>
              </div>
            </div>
            <div class="space-y-2">
              <div
                readonly
                :style="{ backgroundColor: brandColor }"
                class="w-full py-3 rounded-lg text-white font-bold shadow-md text-center"
              >
                Botón Principal
              </div>
              <div class="flex gap-2">
                <div :style="{ backgroundColor: brandColor }" class="h-2 w-1/3 rounded-full"></div>
                <div class="h-2 w-2/3 rounded-full bg-slate-200"></div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
