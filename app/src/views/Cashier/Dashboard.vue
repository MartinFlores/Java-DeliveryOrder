<script setup lang="ts">
defineProps<{
  user: any
}>()

defineEmits(['logout'])
</script>

<template>
  <div class="min-h-screen bg-slate-50 flex">
    <div class="p-8">
      <h1 class="text-3xl font-black text-slate-900 mb-2">Bienvenido, {{ user?.name }}</h1>
      <p class="text-slate-500 mb-6">Sesión iniciada como {{ user?.role }}</p>
      <button
        @click="$emit('logout')"
        class="bg-white border border-slate-200 px-6 py-2 rounded-xl font-bold text-slate-600 hover:bg-slate-50 transition-colors"
      >
        Cerrar Sesión
      </button>
    </div>
  </div>
</template>
