<script setup lang="ts">
import { toastState, Toast } from '@/utils/Toast'
import { CheckCircle2, AlertCircle, XCircle, Info, X } from 'lucide-vue-next'

const getIcon = (type: string) => {
  switch (type) {
    case 'success':
      return CheckCircle2
    case 'error':
      return XCircle
    case 'warning':
      return AlertCircle
    default:
      return Info
  }
}

const getColors = (type: string) => {
  switch (type) {
    case 'success':
      return 'bg-emerald-50 text-emerald-600 border-emerald-100 shadow-emerald-500/10'
    case 'error':
      return 'bg-red-50 text-red-600 border-red-100 shadow-red-500/10'
    case 'warning':
      return 'bg-amber-50 text-amber-600 border-amber-100 shadow-amber-500/10'
    default:
      return 'bg-slate-50 text-slate-600 border-slate-100 shadow-slate-500/10'
  }
}
</script>

<template>
  <div class="fixed top-6 right-6 z-[200] flex flex-col gap-3 max-w-sm w-full">
    <TransitionGroup
      enter-active-class="transition duration-300 ease-out"
      enter-from-class="translate-x-full opacity-0"
      enter-to-class="translate-x-0 opacity-100"
      leave-active-class="transition duration-200 ease-in absolute"
      leave-from-class="translate-x-0 opacity-100"
      leave-to-class="translate-x-full opacity-0"
    >
      <div
        v-for="toast in toastState.toasts"
        :key="toast.id"
        :class="[
          getColors(toast.type),
          'p-4 rounded-2xl border shadow-xl flex items-center justify-between gap-4 backdrop-blur-sm bg-opacity-95',
        ]"
      >
        <div class="flex items-center gap-3">
          <component :is="getIcon(toast.type)" :size="20" />
          <p class="text-xs font-black leading-tight">{{ toast.message }}</p>
        </div>
        <button
          @click="Toast.remove(toast.id)"
          class="text-slate-400 hover:text-slate-600 transition-colors"
        >
          <X :size="16" />
        </button>
      </div>
    </TransitionGroup>
  </div>
</template>
