<script setup lang="ts">
import { nextTick, onMounted, reactive, ref } from 'vue'
import { useWizardStore } from '../store'

const wizardStore = useWizardStore()
const pinLength = 5
const pinValues = reactive(new Array(pinLength).fill(''))
const isPinVisible = ref(false)
const inputRefs = ref<HTMLInputElement[]>([])
const visibleIndices = reactive(new Set<number>())

const handleInput = (index: number, event: Event) => {
  const val = (event.target as HTMLInputElement).value

  if (!/^\d*$/.test(val)) {
    pinValues[index] = ''
    return
  }

  if (val.length > 1) {
    pinValues[index] = val.slice(-1)
  }

  if (pinValues[index]) {
    visibleIndices.add(index)
    setTimeout(() => {
      visibleIndices.delete(index)
    }, 300)
  }

  if (pinValues[index] && index < pinLength - 1) {
    nextTick(() => {
      inputRefs.value[index + 1]?.focus()
    })
  } else if (pinValues[index] && index === pinLength - 1) {
    nextTick(() => {
      inputRefs.value[index]?.blur()
    })
    wizardStore.pin = pinValues.join('')
  }
}

const handleFocus = (index: number) => {
  const firstEmptyIndex = pinValues.findIndex((val) => !val)
  if (firstEmptyIndex !== -1 && index > firstEmptyIndex) {
    nextTick(() => {
      inputRefs.value[firstEmptyIndex]?.focus()
    })
  }
}

const handleKeyDown = (index: number, event: KeyboardEvent) => {
  if (event.key === 'Enter' || event.key === 'Tab') {
    event.preventDefault()
    return
  }
  if (event.key === 'Backspace' && !pinValues[index] && index > 0) {
    inputRefs.value[index - 1]?.focus()
  }
}

const handlePaste = (event: ClipboardEvent) => {
  const pasteData =
    event.clipboardData?.getData('text').replace(/\D/g, '').slice(0, pinLength).split('') || []
  pasteData.forEach((char, i) => {
    pinValues[i] = char
  })

  const nextFocusIndex = Math.min(pasteData.length, pinLength - 1)
  inputRefs.value[nextFocusIndex]?.focus()
  wizardStore.pin = pinValues.join('')
}

onMounted(() => {
  setTimeout(() => {
    inputRefs.value[0]?.focus()
  }, 500)
})
</script>
<template>
  <div class="flex-col items-center justify-center px-6 py-12 text-left animate-fade flex">
    <div class="w-full max-w-md">
      <div
        class="w-16 h-16 orange-gradient rounded-2xl flex items-center justify-center mb-6 shadow-lg shadow-orange-300/50"
      >
        <span class="material-symbols-outlined text-white" style="font-size: 32px"
          >admin_panel_settings</span
        >
      </div>
      <h2 class="md:text-3xl text-xl font-black text-slate-800 md:mb-2">
        Crear PIN <span class="font-black text-orange-500">SuperAdministrador</span>
      </h2>
      <p class="text-slate-500 md:text-[16px] text-[12px] md:mb-8 mb-4">
        Crea tu PIN para poder gestionar todas las funciones del sistema.
      </p>

      <div class="space-y-6">
        <div class="relative bg-slate-50 rounded-2xl p-4 border-2 border-slate-200">
          <div class="flex justify-between items-center gap-3">
            <input
              v-for="(digit, index) in 5"
              v-bind:key="index"
              v-model="pinValues[index]"
              :type="isPinVisible || visibleIndices.has(index) ? 'text' : 'password'"
              :ref="(el) => (inputRefs[index] = el as HTMLInputElement)"
              @input="handleInput(index, $event)"
              @keydown="handleKeyDown(index, $event)"
              @paste="handlePaste"
              @focus="handleFocus(index)"
              maxlength="1"
              inputmode="numeric"
              enterkeyhint="done"
              class="pin-input w-full aspect-square text-center text-4xl font-bold bg-white rounded-xl border-2 border-slate-200 focus:border-orange-500 focus:outline-none transition-all duration-300 font-medium text-slate-800 placeholder:text-slate-400"
              placeholder="•"
              required
            />
          </div>
        </div>
        <button
          type="button"
          @mousedown="isPinVisible = true"
          @mouseup="isPinVisible = false"
          @mouseleave="isPinVisible = false"
          @touchstart="isPinVisible = true"
          @touchend="isPinVisible = false"
          class="mt-6 flex items-center gap-2 mx-auto text-xs font-semibold hover:text-slate-600/90 font-bold transition-colors uppercase tracking-widest px-4 py-2 rounded-full bg-slate-100 border border-slate-200 text-slate-400/90"
        >
          <span v-if="!isPinVisible" class="material-symbols-outlined">visibility</span>
          <span v-else class="material-symbols-outlined">visibility_off</span>

          {{ isPinVisible ? 'Ocultar' : 'Mostrar' }} PIN
        </button>
      </div>
      <div class="mt-4 flex items-center justify-center gap-1 text-xs text-slate-400">
        <span class="material-symbols-outlined text-sm text-green-500">verified_user</span
        ><span>Tu PIN de acceso estará encriptado y seguro.</span>
      </div>
    </div>
  </div>
</template>
