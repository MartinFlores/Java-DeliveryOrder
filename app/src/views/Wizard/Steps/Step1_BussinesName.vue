<!-- eslint-disable @typescript-eslint/no-explicit-any -->
<script setup lang="ts">
import { storeToRefs } from 'pinia'
import { useWizardStore } from '../store'
import { ref, onMounted } from 'vue'
import { api } from '@/utils/axios'

const wizardStore = useWizardStore()
const { businessName } = storeToRefs(wizardStore)
const configData = ref<any>(null)

onMounted(async () => {
  try {
    const { data } = await api.get('/config')
    configData.value = data
  } catch (error) {
    configData.value = { error: (error as Error).message }
    alert('Error cargando config: ' + (error as Error).message)
  }
})
</script>

<template>
  <div class="flex-col items-center justify-center text-left animate-fade flex py-6 px-4">
    <div class="w-full max-w-[448px]">
      <div
        class="w-16 h-16 bg-gradient-to-br from-orange-500 to-orange-600 rounded-2xl flex items-center justify-center mb-6 shadow-lg shadow-orange-300/50"
      >
        <span class="material-symbols-outlined text-white" style="font-size: 32px">store</span>
      </div>
      <h2 class="md:text-3xl text-xl font-black text-slate-800 md:mb-2">Nombre del Negocio</h2>
      <p class="text-slate-500 md:text-[16px] text-[12px] mb-2">
        ¿Cómo te reconocerán tus clientes?
      </p>

      <div class="flex flex-col gap-4">
        <!-- Input con icono -->
        <div class="bg-slate-50 rounded-2xl p-4 border-2 border-slate-200">
          <div class="relative">
            <div class="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400">
              <span class="material-symbols-outlined" style="font-size: 24px">storefront</span>
            </div>
            <input
              v-model="businessName"
              type="text"
              placeholder="Ej: Mi Tienda"
              class="w-full pl-14 pr-4 py-4 bg-white rounded-xl border-2 border-slate-200 focus:border-orange-500 focus:outline-none transition-all duration-300 font-medium text-slate-800 placeholder:text-slate-400"
              required
            />
          </div>
        </div>

        <!-- Preview -->
        <div v-if="businessName" class="bg-slate-100 rounded-2xl p-6 border-2 border-slate-200">
          <p class="text-xs font-bold text-slate-500 mb-4 uppercase tracking-wider">Vista previa</p>
          <div class="bg-white rounded-xl shadow-lg p-6">
            <div class="flex items-center gap-3">
              <div
                class="w-12 h-12 rounded-xl flex items-center justify-center shadow-md bg-gradient-to-br from-orange-500 to-orange-600"
              >
                <span class="material-symbols-outlined text-white">store</span>
              </div>
              <div>
                <h3 class="font-bold text-slate-800">{{ businessName }}</h3>
                <p class="text-xs text-slate-500">Punto de Venta</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
