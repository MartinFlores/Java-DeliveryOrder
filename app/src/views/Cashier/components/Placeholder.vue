<template>
  <div class="p-8">
    <h2 class="text-2xl font-bold text-slate-800">{{ title }}</h2>
    <p class="text-slate-500">Módulo en construcción...</p>
  </div>
</template>

<script setup lang="ts">
defineProps<{ title: string }>()
</script>
