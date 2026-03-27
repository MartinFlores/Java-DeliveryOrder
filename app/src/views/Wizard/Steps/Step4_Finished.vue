<script setup lang="ts">
import { Loader, wait } from '@/utils/Loader'

const handleReload = async () => {
  Loader.show('Iniciando App')
  await wait(1500)
  window.location.reload()
}
</script>

<template>
  <div class="flex-col items-center justify-center px-6 py-12 text-center animate-fade flex">
    <div class="w-full max-w-md items-center justify-center flex flex-col">
      <div
        class="w-20 h-20 bg-gradient-to-br from-orange-500 to-orange-600 rounded-2xl flex items-center justify-center mb-8 shadow-lg shadow-orange-300/50"
      >
        <span class="material-symbols-outlined text-white" style="font-size: 56px"> verified </span>
      </div>

      <h2 class="text-3xl text-xl font-black text-slate-800 mb-4">¡App lista!</h2>

      <p class="text-slate-500 md:text-base text-sm mb-2">
        Tu punto de venta fue configurado correctamente.
      </p>

      <p class="text-slate-400 md:text-sm text-xs mb-8">
        Ya puedes comenzar a registrar ventas, gestionar productos y administrar tu negocio desde el
        panel principal.
      </p>

      <div
        @click="handleReload"
        class="relative w-auto p-3 px-8 pr-12 rounded-lg text-white font-bold shadow-md text-center bg-[#FF6B35] hover:scale-105 transition-transform cursor-pointer"
      >
        Ir al panel de ventas
        <span class="material-symbols-outlined absolute font-bold ml-1"> arrow_forward </span>
      </div>
    </div>
  </div>
</template>
