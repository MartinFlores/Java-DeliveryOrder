<script setup lang="ts">
import { ref } from 'vue'
import { Banknote, KeyRound, Phone, Loader2, ArrowRight, Eye, EyeOff } from 'lucide-vue-next'
import { api } from '@/utils/axios'
import { Loader } from '@/utils/Loader'
import { useUserStore } from '@/stores/userStore'

const emit = defineEmits(['login-success'])
const userStore = useUserStore()

const phone = ref('')
const password = ref('')
const showPassword = ref(false)
const isSubmitting = ref(false)
const errorMessage = ref('')

const handleLogin = async () => {
  if (!phone.value || !password.value) {
    errorMessage.value = 'Por favor ingresa todos los campos'
    return
  }

  isSubmitting.value = true
  errorMessage.value = ''
  Loader.show('Iniciando sesión...')

  try {
    const { data } = await api.post('users/login', {
      whatsapp: phone.value,
      password: password.value,
    })

    if (data.status === 'ok') {
      userStore.setUser(data.data)
      emit('login-success', data.data)
    } else {
      errorMessage.value = data.message || 'Contraseña incorrecta'
    }
  } catch (error: any) {
    console.error('Login error:', error)
    errorMessage.value = 'Error al conectar con el servidor'
  } finally {
    isSubmitting.value = false
    Loader.hide()
  }
}
</script>

<template>
  <div class="min-h-screen bg-slate-50 flex flex-col items-center justify-center p-4">
    <div class="w-full max-w-[440px]">
      <!-- Header -->
      <div class="flex flex-col items-center mb-6 text-center">
        <div
          class="w-20 h-20 bg-emerald-500 rounded-3xl flex items-center justify-center shadow-lg shadow-emerald-200 mb-4"
        >
          <Banknote class="w-10 h-10 text-white" />
        </div>
        <h1 class="text-4xl font-black text-slate-900 mb-1 tracking-tight">Punto de Venta</h1>
        <p class="text-slate-400 font-medium">Terminal de Cajero</p>
      </div>

      <!-- Login Card -->
      <div
        class="bg-white p-8 md:p-10 rounded-[2.5rem] shadow-xl shadow-slate-200/60 border border-slate-100 relative overflow-hidden"
      >
        <!-- Decoration -->
        <div
          class="absolute top-0 right-0 w-32 h-32 bg-orange-50 rounded-full -mr-16 -mt-16 opacity-50"
        ></div>

        <form @submit.prevent="handleLogin" class="space-y-6 relative z-10">
          <div class="space-y-2">
            <label class="text-xs font-bold text-slate-500 uppercase tracking-wider ml-1"
              >Número de Teléfono</label
            >
            <div class="relative group">
              <div
                class="absolute inset-y-0 left-0 pl-5 flex items-center pointer-events-none text-slate-400 group-focus-within:text-orange-500 transition-colors"
              >
                <Phone class="w-5 h-5" />
              </div>
              <input
                v-model="phone"
                type="tel"
                required
                placeholder="Ej. 1234567890"
                class="w-full pl-12 pr-5 py-4 bg-slate-50 border-2 border-slate-200 rounded-2xl focus:outline-none focus:ring-4 focus:ring-orange-500/10 focus:border-orange-500 transition-all text-sm font-semibold text-slate-900"
              />
            </div>
          </div>

          <div class="space-y-2">
            <label class="text-xs font-bold text-slate-500 uppercase tracking-wider ml-1">
              Contraseña
            </label>
            <div class="relative group">
              <div
                class="absolute inset-y-0 left-0 pl-5 flex items-center pointer-events-none text-slate-400 group-focus-within:text-orange-500 transition-colors"
              >
                <KeyRound class="w-5 h-5" />
              </div>
              <input
                v-model="password"
                :type="showPassword ? 'text' : 'password'"
                required
                placeholder="••••"
                class="w-full pl-12 pr-12 py-4 bg-slate-50 border-2 border-slate-200 rounded-2xl focus:outline-none focus:ring-4 focus:ring-orange-500/10 focus:border-orange-500 transition-all text-sm font-semibold tracking-widest text-slate-900"
              />
              <button
                type="button"
                @click="showPassword = !showPassword"
                class="absolute inset-y-0 right-0 pr-5 flex items-center text-slate-400 hover:text-orange-500 transition-colors"
              >
                <Eye v-if="!showPassword" class="w-5 h-5" />
                <EyeOff v-else class="w-5 h-5" />
              </button>
            </div>
          </div>

          <!-- Error Message -->
          <Transition
            enter-active-class="transition duration-200 ease-out"
            enter-from-class="transform -translate-y-2 opacity-0"
            enter-to-class="transform translate-y-0 opacity-100"
            leave-active-class="transition duration-150 ease-in"
            leave-from-class="transform translate-y-0 opacity-100"
            leave-to-class="transform -translate-y-2 opacity-0"
          >
            <div
              v-if="errorMessage"
              class="bg-red-50 text-red-600 p-4 rounded-2xl text-xs font-bold flex items-center gap-3 border border-red-100"
            >
              <div class="w-1.5 h-1.5 rounded-full bg-red-500"></div>
              {{ errorMessage }}
            </div>
          </Transition>

          <button
            type="submit"
            :disabled="isSubmitting"
            class="w-full py-5 bg-slate-900 text-white rounded-2xl font-black text-lg shadow-xl shadow-slate-900/20 hover:bg-slate-800 active:scale-[0.98] transition-all flex items-center justify-center gap-3 disabled:opacity-70 group"
          >
            <span>Entrar</span>
            <ArrowRight
              v-if="!isSubmitting"
              class="w-5 h-5 group-hover:translate-x-1 transition-transform"
            />
            <Loader2 v-else class="w-5 h-5 animate-spin" />
          </button>
        </form>
      </div>
    </div>
  </div>
</template>
