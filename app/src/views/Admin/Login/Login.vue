<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ShieldCheck, Loader2, ArrowRight } from 'lucide-vue-next'
import { useConfigStore } from '@/stores/configStore'

const configStore = useConfigStore()
const pinLength = 5
const pinValues = reactive(new Array(pinLength).fill(''))
const isSubmitting = ref(false)
const errorMessage = ref('')
const inputRefs = ref<HTMLInputElement[]>([])

const handleInput = (index: number, event: Event) => {
  const val = (event.target as HTMLInputElement).value

  if (!/^\d*$/.test(val)) {
    pinValues[index] = ''
    return
  }

  if (val.length > 1) {
    pinValues[index] = val.slice(-1)
  }

  if (pinValues[index] && index < pinLength - 1) {
    nextTick(() => {
      inputRefs.value[index + 1]?.focus()
    })
  }
}

const handleKeyDown = (index: number, event: KeyboardEvent) => {
  if (event.key === 'Backspace' && !pinValues[index] && index > 0) {
    inputRefs.value[index - 1]?.focus()
  }
}

const handleLogin = async () => {
  const pin = pinValues.join('')
  if (pin.length < pinLength) {
    errorMessage.value = 'Por favor ingresa el PIN completo'
    return
  }

  isSubmitting.value = true
  errorMessage.value = ''

  const result = await configStore.adminLogin(pin)

  if (!result.success) {
    errorMessage.value = result.message || 'PIN incorrecto'
    pinValues.fill('')
    inputRefs.value[0]?.focus()
  }

  isSubmitting.value = false
}

onMounted(() => {
  setTimeout(() => {
    inputRefs.value[0]?.focus()
  }, 500)
})
</script>

<template>
  <div class="min-h-screen bg-slate-900 flex flex-col items-center justify-center p-4">
    <div class="w-full max-w-[440px] animate-in fade-in zoom-in duration-500">
      <!-- Header -->
      <div class="flex flex-col items-center mb-8 text-center">
        <div
          class="w-20 h-20 bg-orange-500 rounded-3xl flex items-center justify-center shadow-lg shadow-orange-500/20 mb-6 group transition-transform hover:scale-110 active:scale-95 cursor-pointer"
        >
          <ShieldCheck class="w-10 h-10 text-white" />
        </div>
        <h1 class="text-4xl font-black text-white mb-2 tracking-tight">Modo Administrador</h1>
        <p class="text-slate-400 font-medium">Ingresa tu PIN de seguridad</p>
      </div>

      <!-- Login Card -->
      <div
        class="bg-[#1E293B] p-8 md:p-10 rounded-[2.5rem] shadow-2xl border border-slate-800 relative overflow-hidden"
      >
        <form @submit.prevent="handleLogin" class="space-y-8">
          <div class="flex justify-between gap-3">
            <input
              v-for="(_, index) in pinLength"
              :key="index"
              v-model="pinValues[index]"
              type="password"
              :ref="(el) => (inputRefs[index] = el as HTMLInputElement)"
              @input="handleInput(index, $event)"
              @keydown="handleKeyDown(index, $event)"
              maxlength="1"
              inputmode="numeric"
              class="w-full aspect-square text-center text-3xl font-bold bg-slate-800/50 border-2 border-slate-700 rounded-2xl focus:outline-none focus:ring-4 focus:ring-orange-500/20 focus:border-orange-500 transition-all text-white placeholder:text-slate-600"
              placeholder="•"
              required
            />
          </div>

          <!-- Error Message -->
          <Transition
            enter-active-class="transition duration-200 ease-out"
            enter-from-class="transform -translate-y-2 opacity-0"
            enter-to-class="transform translate-y-0 opacity-100"
          >
            <div
              v-if="errorMessage"
              class="bg-red-500/10 text-red-400 p-4 rounded-2xl text-xs font-bold flex items-center gap-3 border border-red-500/20"
            >
              <div class="w-1.5 h-1.5 rounded-full bg-red-500"></div>
              {{ errorMessage }}
            </div>
          </Transition>

          <button
            type="submit"
            :disabled="isSubmitting"
            class="w-full py-5 bg-orange-600 text-white rounded-2xl font-black text-lg shadow-xl shadow-orange-600/20 hover:bg-orange-500 active:scale-[0.98] transition-all flex items-center justify-center gap-3 disabled:opacity-70 group"
          >
            <span>Desbloquear</span>
            <ArrowRight
              v-if="!isSubmitting"
              class="w-5 h-5 group-hover:translate-x-1 transition-transform"
            />
            <Loader2 v-else class="w-5 h-5 animate-spin" />
          </button>
        </form>
      </div>

      <p class="mt-8 text-center text-slate-500 text-sm font-medium">
        ¿Olvidaste tu PIN? Contacta a soporte técnico
      </p>
    </div>
  </div>
</template>
