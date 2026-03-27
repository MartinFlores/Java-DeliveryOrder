<script setup lang="ts">
import { storeToRefs } from 'pinia'
import { useWizardStore } from './store'
import { onMounted, ref, watch } from 'vue'
import { Loader, wait } from '@/utils/Loader'
import { useRouterStore } from '@/stores/routerStore'

import Header from './Header.vue'
import Step1_BussinesName from './Steps/Step1_BussinesName.vue'
import Step2_Personalize from './Steps/Step2_Personalize.vue'
import Step3_Credentials from './Steps/Step3_Credentials.vue'
import Step4_Finished from './Steps/Step4_Finished.vue'

const wizardStore = useWizardStore()
const routerStore = useRouterStore()
const { step } = storeToRefs(wizardStore)

const container = ref<HTMLElement | null>(null)

// Cada vez que cambie el paso, reseteamos el scroll al principio
watch(step, () => {
  if (container.value) {
    container.value.scrollTop = 0
  }
})

const handleNext = async (e: SubmitEvent) => {
  e.preventDefault()

  if (step.value === 3) {
    Loader.show('Configurando empresa...')
    const success = await wizardStore.createConfigCompany()
    Loader.hide()

    if (!success) {
      alert('Error al guardar la configuración')
      return
    }
  }

  if (step.value === 4) {
    routerStore.navigate('/admin/dashboard')
    return
  }

  wizardStore.next()
}

onMounted(async () => {
  // Loader.show('Configurando empresa...')
  // try {
  //   const { data } = await axios.get('http://localhost:7979/api/v1/apps')
  //   qrUrl.value = await QRCode.toDataURL(data.menu)
  // } catch (error) {
  //   console.error('Error obteniendo IP:', error)
  // }
})
</script>

<template>
  <form class="wizard-modal relative z-[999999] m-auto" @submit="handleNext">
    <Header />

    <!-- Pantallas -->
    <div ref="container" class="overflow-y-auto h-full scroll-smooth">
      <!-- <img v-if="qrUrl" :src="qrUrl" alt="QR Code" class="mx-auto my-4" /> -->

      <Step1_BussinesName v-if="step === 1" />
      <Step2_Personalize v-if="step === 2" />
      <Step3_Credentials v-if="step === 3" />
      <Step4_Finished v-if="step === 4" />
    </div>

    <!-- Footer -->
    <div
      v-if="step !== 4"
      class="sticky bottom-0 p-4 bg-slate-50 flex justify-between items-center border border-slate-200"
    >
      <!-- Botón Atrás -->
      <button
        v-if="step > 1"
        @click="wizardStore.prev()"
        type="button"
        class="w-auto p-3 px-10 pl-14 rounded-lg text-white font-bold shadow-md text-center bg-slate-500"
      >
        <span class="material-symbols-outlined absolute font-bold mr-1 -ml-8"> arrow_back </span>
        Atrás
      </button>

      <!-- Espacio vacío si no hay atrás -->
      <div v-if="step < 2"></div>

      <button
        class="w-auto p-3 px-10 pr-14 rounded-lg text-white font-bold shadow-md text-center bg-[#FF6B35]"
      >
        Siguiente
        <span class="material-symbols-outlined absolute font-bold ml-1"> arrow_forward </span>
      </button>
    </div>
  </form>
</template>
