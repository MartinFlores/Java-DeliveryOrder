<script setup lang="ts">
import { ref } from 'vue'
import {
  ChefHat,
  Banknote,
  Utensils,
  ArrowRight,
  X,
  QrCode,
  Compass,
  Smartphone,
  Copy,
  Check,
} from 'lucide-vue-next'
import QRCode from 'qrcode'
import { useConfigStore } from '@/stores/configStore'

const apps = [
  {
    title: 'Punto de Venta',
    id: 'cashier',
    subtitle: 'Control de ingresos y egresos',
    icon: Banknote,
    color: 'text-emerald-600',
    bgColor: 'bg-emerald-50',
    buttonColor: 'bg-emerald-600 hover:bg-emerald-700 shadow-emerald-100',
    modalBorderColor: 'border-emerald-400',
    modalShadowColor: 'shadow-emerald-400/20',
  },
  {
    title: 'Cocina',
    id: 'kitchen',
    subtitle: 'Gestión de órdenes en tiempo real',
    icon: ChefHat,
    color: 'text-orange-600',
    bgColor: 'bg-orange-50',
    buttonColor: 'bg-orange-600 hover:bg-orange-700 shadow-orange-100',
    modalBorderColor: 'border-orange-400',
    modalShadowColor: 'shadow-orange-400/20',
  },
  {
    title: 'Mesero',
    id: 'waiter',
    subtitle: 'Toma de pedidos móvil',
    icon: Utensils,
    color: 'text-blue-600',
    bgColor: 'bg-blue-50',
    buttonColor: 'bg-blue-600 hover:bg-blue-700 shadow-blue-100',
    modalBorderColor: 'border-blue-400',
    modalShadowColor: 'shadow-blue-400/20',
  },
  {
    title: 'Menú Digital',
    id: 'menu',
    subtitle: 'Escanea, mira el menú y ordena',
    icon: Smartphone,
    color: 'text-purple-600',
    bgColor: 'bg-purple-50',
    buttonColor: 'bg-purple-600 hover:bg-purple-700 shadow-purple-100',
    modalBorderColor: 'border-purple-400',
    modalShadowColor: 'shadow-purple-400/20',
  },
]

const configStore = useConfigStore()

const showModal = ref(false)
const selectedApp = ref<any>(null)
const qrCodeUrl = ref('')
const copied = ref(false)

const openAppModal = async (app: any) => {
  selectedApp.value = app
  const url = `${configStore.apiBaseUrl}?${app.id}`

  try {
    qrCodeUrl.value = await QRCode.toDataURL(url, {
      width: 400,
      margin: 2,
      color: {
        dark: '#0f172a', // slate-900
        light: '#ffffff',
      },
    })
    showModal.value = true
  } catch (err) {
    console.error('Error generating QR code', err)
  }
}

const copyToClipboard = () => {
  const url = `${configStore.apiBaseUrl}?${selectedApp.value.id}`
  navigator.clipboard.writeText(url)
  copied.value = true
  setTimeout(() => (copied.value = false), 2000)
}

const openLink = (id: string) => {
  window.open(`${configStore.apiBaseUrl}?${id}`, '_blank')
}
</script>

<template>
  <div class="wrap-view text-slate-800">
    <!-- Header -->
    <header class="mb-12">
      <div class="flex items-center gap-3 mb-4">
        <span class="h-1 w-12 bg-orange-600 rounded-full"></span>
        <span class="text-orange-600 font-bold tracking-wider uppercase text-sm">Apps</span>
      </div>
      <h1 class="text-4xl font-extrabold text-slate-900 mb-4 tracking-tight">
        Aplicaciones del Sistema
      </h1>
      <p class="text-slate-500 max-w-2xl text-lg leading-relaxed">
        Aquí encontrarás todas las herramientas necesarias para digitalizar cada área de tu
        establecimiento, optimizando la comunicación y maximizando la eficiencia de tu servicio.
      </p>
    </header>

    <!-- Apps Grid -->
    <div class="grid grid-cols-1 lg:grid-cols-2 lg:grid-cols-3 gap-8">
      <div
        v-for="app in apps"
        :key="app.title"
        class="group relative bg-white rounded-[2rem] p-8 border border-slate-200/60 shadow-[0_8px_30px_rgb(0,0,0,0.04)] hover:shadow-[0_8px_30px_rgb(0,0,0,0.08)] transition-all duration-300 flex flex-col h-full overflow-hidden"
      >
        <!-- Background Accent Decoration -->
        <div
          :class="[
            'absolute -right-8 -top-8 w-32 h-32 rounded-full opacity-[0.03] transition-transform duration-700 group-hover:scale-150',
            app.bgColor,
          ]"
        ></div>

        <div class="flex items-start justify-between mb-6 relative z-10">
          <div
            :class="[
              app.bgColor,
              'p-5 rounded-2xl transition-all duration-500 group-hover:scale-110 group-hover:shadow-lg group-hover:shadow-current/10',
            ]"
          >
            <component :is="app.icon" :class="['w-9 h-9', app.color]" />
          </div>
          <div
            class="flex items-center gap-1.5 bg-slate-50 px-4 py-1.5 rounded-full border border-slate-100"
          >
            <span class="w-1.5 h-1.5 rounded-full bg-emerald-500 animate-pulse"></span>
            <span class="text-[10px] font-bold text-slate-500 uppercase tracking-widest"
              >En Línea</span
            >
          </div>
        </div>

        <div class="mb-auto relative z-10">
          <h3 class="text-2xl font-bold text-slate-900 mb-2">{{ app.title }}</h3>
          <p :class="['text-sm font-bold mb-5 tracking-wide', app.color]">
            {{ app.subtitle }}
          </p>
          <div
            class="w-10 h-1 bg-slate-100 mb-6 group-hover:w-full transition-all duration-500"
          ></div>
        </div>

        <div class="pt-2 mt-auto relative z-10">
          <button
            @click="openAppModal(app)"
            :class="[
              'w-full py-4 px-6 rounded-2xl text-white font-bold flex items-center justify-center gap-3 transition-all duration-300 active:scale-[0.98] shadow-lg',
              app.buttonColor,
            ]"
          >
            Abrir App
            <ArrowRight
              class="w-5 h-5 group-hover:translate-x-1 transition-transform duration-300"
            />
          </button>
        </div>
      </div>
    </div>

    <!-- MODAL -->
    <Teleport to="body">
      <Transition
        enter-from-class="opacity-0"
        enter-to-class="opacity-100"
        leave-from-class="opacity-100"
        leave-to-class="opacity-0"
      >
        <div
          v-if="showModal"
          class="fixed inset-0 z-[100] flex items-center justify-center p-4 bg-slate-950/80"
          @click.self="showModal = false"
        >
          <div
            v-if="showModal"
            class="bg-white w-full max-w-[380px] md:max-h-[94vh] rounded-[2.5rem] shadow-2xl relative flex flex-col"
          >
            <!-- Close Button -->
            <button
              @click="showModal = false"
              class="absolute top-6 right-6 p-2 rounded-full bg-slate-100 text-slate-400 hover:text-slate-600 hover:bg-slate-200 transition-all z-20"
            >
              <X class="w-5 h-5" />
            </button>

            <!-- Modal Content Scrollable Area -->
            <div
              class="flex-1 overflow-y-auto p-6 pt-8 text-center custom-scrollbar max-w-xs m-auto w-full"
            >
              <!-- App Icon & Title -->
              <div class="flex flex-col items-center mb-4">
                <div
                  :class="[
                    selectedApp.bgColor,
                    selectedApp.modalBorderColor,
                    selectedApp.modalShadowColor,
                    'p-4 rounded-2xl mb-4 absolute top-[-35px] border-2 shadow-lg',
                  ]"
                >
                  <component :is="selectedApp.icon" :class="['w-8 h-8', selectedApp.color]" />
                </div>
                <h2 class="text-2xl font-black text-slate-900 leading-none mb-1 mt-4">
                  {{ selectedApp.title }}
                </h2>
                <p class="text-slate-400 font-medium text-sm">Acceso rápido a la app</p>
              </div>

              <!-- QR Code "Card" -->
              <div class="relative group mb-4">
                <div class="absolute inset-0 bg-slate-100 rounded-3xl"></div>
                <div
                  class="relative bg-white p-3 rounded-3xl border border-slate-100 shadow-sm pb-3"
                >
                  <div
                    class="bg-slate-50 p-2 rounded-2xl aspect-square flex items-center justify-center overflow-hidden border border-slate-100"
                  >
                    <img :src="qrCodeUrl" alt="QR Code" class="w-full h-full mix-blend-multiply" />
                  </div>
                  <div class="mt-3 flex items-center justify-center gap-2 text-slate-400">
                    <QrCode class="w-4 h-4" />
                    <span class="text-[11px] font-bold uppercase tracking-widest leading-none"
                      >Escanea para abrir</span
                    >
                  </div>
                </div>
              </div>

              <!-- Instructions & Link -->
              <div class="bg-slate-50 rounded-2xl p-4 mb-4">
                <p class="text-slate-500 text-xs mb-2">
                  Escanea este código con la cámara de tu dispositivo para acceder directamente.
                </p>
                <div
                  class="flex items-center gap-2 bg-white p-2 rounded-xl border border-slate-100"
                >
                  <div class="flex-1 overflow-hidden">
                    <p class="text-[13px] text-slate-400 truncate text-left px-2">
                      {{ configStore.apiBaseUrl }}?{{ selectedApp.id }}
                    </p>
                  </div>
                  <button
                    @click="copyToClipboard"
                    class="p-2 rounded-lg hover:bg-slate-50 transition-colors relative"
                  >
                    <Check v-if="copied" class="w-4 h-4 text-emerald-500" />
                    <Copy v-else class="w-4 h-4 text-slate-400" />
                    <span
                      v-if="copied"
                      class="absolute -top-8 left-1/2 -translate-x-1/2 bg-slate-800 text-white text-[10px] py-1 px-2 rounded whitespace-nowrap"
                      >¡Copiado!</span
                    >
                  </button>
                </div>
              </div>

              <!-- Footer Action -->
              <button
                @click="openLink(selectedApp.id)"
                class="w-full py-4 rounded-2xl bg-slate-900 text-white font-bold flex items-center justify-center gap-2 hover:bg-slate-800 transition-all active:scale-[0.98]"
              >
                <Compass class="w-4 h-4" />
                Abrir en mi navegador
              </button>
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>

    <!-- Footer Help -->
    <footer
      class="mt-20 p-8 rounded-3xl bg-slate-100/50 border border-slate-200/50 flex flex-col md:flex-row items-center justify-between gap-6"
    >
      <div class="flex items-center gap-4">
        <div class="w-12 h-12 rounded-full bg-white flex items-center justify-center shadow-sm">
          <span class="text-xl">✨</span>
        </div>
        <div>
          <h4 class="font-bold text-slate-900">¿Necesitas una configuración especial?</h4>
          <p class="text-slate-500 text-sm">
            Contáctanos para habilitar módulos personalizados para tu negocio.
          </p>
        </div>
      </div>
      <button
        class="px-6 py-2.5 bg-white border border-slate-200 rounded-xl text-slate-700 font-semibold hover:bg-slate-50 transition-colors shadow-sm"
      >
        Ver Documentación
      </button>
    </footer>
  </div>
</template>

<style scoped>
.shadow-current {
  --shadow-color: currentColor;
}

.custom-scrollbar::-webkit-scrollbar {
  width: 4px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: #e2e8f0;
  border-radius: 10px;
}
.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: #cbd5e1;
}
</style>
