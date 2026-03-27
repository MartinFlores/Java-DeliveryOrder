<template>
  <Transition name="loader-fade">
    <div
      v-if="loaderState.visible"
      class="fixed inset-0 z-[9999999] flex items-center justify-center bg-black/50 backdrop-blur-sm"
    >
      <div
        class="bg-white rounded-2xl shadow-2xl p-8 flex flex-col items-center gap-4 animate-scale-in"
      >
        <div class="relative w-16 h-16">
          <div class="absolute inset-0 border-4 border-orange-200 rounded-full"></div>
          <div
            class="absolute inset-0 border-4 border-transparent border-t-orange-500 rounded-full animate-spin"
            style="animation-duration: 0.6s;"
          ></div>
        </div>
        <p class="text-slate-700 font-bold text-lg">{{ loaderState.message }}</p>
      </div>
    </div>
  </Transition>
</template>

<script setup lang="ts">
import { loaderState } from '@/utils/Loader'
</script>

<style scoped>
.loader-fade-enter-active,
.loader-fade-leave-active {
  transition: opacity 0.2s ease;
}

.loader-fade-enter-from,
.loader-fade-leave-to {
  opacity: 0;
}

@keyframes scale-in {
  from {
    transform: scale(0.9);
    opacity: 0;
  }
  to {
    transform: scale(1);
    opacity: 1;
  }
}

.animate-scale-in {
  animation: scale-in 0.2s ease-out;
}
</style>
