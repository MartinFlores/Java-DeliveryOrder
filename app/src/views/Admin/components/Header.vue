<script setup lang="ts">
import { useConfigStore } from '@/stores/configStore'
import { Menu, Search, Bell, ChevronDown } from 'lucide-vue-next'
import { onMounted } from 'vue'

const props = defineProps<{
  currentView: string
}>()

defineEmits(['toggleAside'])

const configStore = useConfigStore()

onMounted(() => {
  console.log('currentView', props.currentView)
})
</script>

<template>
  <header class="h-20 px-6 lg:px-10 flex items-center justify-between z-30 sticky top-0 header">
    <div class="flex items-center gap-4">
      <button
        class="lg:hidden p-2 rounded-xl hover:bg-slate-100 transition"
        @click="$emit('toggleAside')"
      >
        <Menu class="w-6 h-6 text-slate-700" />
      </button>

      <div>
        <h1 class="text-lg lg:text-2xl font-bold text-slate-800 tracking-tight">
          {{ configStore.businessDetails.name || 'Cargando...' }}
        </h1>
        <!-- <p class="text-sm text-slate-500 font-medium hidden sm:block">
          Sucursal Centro 
          <span class="text-green-500 font-bold">• Abierto</span>
        </p> -->
      </div>
    </div>
    <div class="flex items-center gap-3 lg:gap-6">
      <div class="hidden md:flex items-center relative">
        <Search class="absolute left-3 text-slate-400 w-5 h-5" />
        <input
          type="text"
          placeholder="Buscar orden, producto..."
          class="pl-10 pr-4 py-2 w-64 bg-white rounded-full border-2 border-slate-200 focus:border-orange-500 focus:outline-none transition-all duration-300 font-medium text-slate-800 placeholder:text-slate-400 text-sm"
        />
      </div>
      <button
        class="relative p-2 rounded-full text-slate-500 hover:bg-slate-100 hover:text-orange-500 transition-colors"
      >
        <Bell />
        <span
          class="absolute top-1.5 right-1.5 w-2 h-2 rounded-full bg-orange-500 border-2 border-white"
        ></span>
      </button>
      <div class="w-px h-8 bg-slate-200 hidden sm:block"></div>
      <div class="relative">
        <button
          class="flex items-center gap-3 p-1.5 pr-3 rounded-full hover:bg-slate-100 transition-all active:scale-95 border border-transparent hover:border-slate-200"
        >
          <div
            class="w-9 h-9 rounded-full bg-gradient-to-tr from-orange-500 to-orange-300 flex items-center justify-center text-white font-bold shadow-md shadow-orange-500/20"
          >
            {{ configStore.superAdmin.name?.substring(0, 2).toUpperCase() || 'AD' }}
          </div>
          <div class="hidden md:flex flex-col text-left">
            <span class="text-sm font-bold text-slate-800 leading-none capitalize">{{
              configStore.superAdmin.name || 'Admin'
            }}</span
            ><span class="text-xs text-slate-500 font-medium">Administrador</span>
          </div>
          <ChevronDown :size="16" class="text-slate-400 transition-transform duration-300" />
        </button>
      </div>
    </div>
  </header>
</template>
