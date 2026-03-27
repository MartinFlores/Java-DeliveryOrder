<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Printer, RefreshCw, Save, CheckCircle2, Bluetooth, Settings2, FileText, Smartphone } from 'lucide-vue-next'
import { printerService, type Printer as IPrinter } from '@/services/printerService'

const pairedPrinters = ref<IPrinter[]>([])
const savedPrinter = ref<IPrinter | null>(null)
const loading = ref(false)
const refreshing = ref(false)
const saving = ref(false)
const successTimeout = ref<number | null>(null)
const showSuccess = ref(false)

const paperWidthOptions = [
  { label: '58mm (48mm área de impresión)', value: 48 },
  { label: '80mm (72mm área de impresión)', value: 72 }
]

const charsetOptions = [
  { label: 'PC437 [Default]', value: 'PC437' },
  { label: 'CP850 [Multilingual]', value: 'CP850' },
  { label: 'CP852 [Latin 2]', value: 'CP852' },
  { label: 'CP858 [Multilingual + Euro]', value: 'CP858' }
]

const selectedPrinter = ref<IPrinter>({
  name: '',
  mac: '',
  paperWidth: 48,
  charset: 'CP850'
})

const fetchPrinters = async () => {
  refreshing.value = true
  try {
    pairedPrinters.value = await printerService.listPairedPrinters()
  } catch (e) {
    console.error('Error fetching paired printers', e)
  } finally {
    refreshing.value = false
  }
}

const loadSavedPrinter = async () => {
  loading.value = true
  try {
    const saved = await printerService.getSavedPrinter()
    if (saved) {
      savedPrinter.value = saved
      selectedPrinter.value = { ...saved }
    }
  } catch (e) {
    console.error('Error fetching saved printer', e)
  } finally {
    loading.value = false
  }
}

const selectPrinter = (printer: IPrinter) => {
  selectedPrinter.value.name = printer.name
  selectedPrinter.value.mac = printer.mac
}

const handleSave = async () => {
  if (!selectedPrinter.value.mac) return
  
  saving.value = true
  try {
    await printerService.savePrinter(selectedPrinter.value)
    savedPrinter.value = { ...selectedPrinter.value }
    
    showSuccess.value = true
    if (successTimeout.value) clearTimeout(successTimeout.value)
    successTimeout.value = window.setTimeout(() => {
      showSuccess.value = false
    }, 3000)
  } catch (e) {
    console.error('Error saving printer', e)
  } finally {
    saving.value = false
  }
}

const handlePrintTest = async () => {
  try {
    await printerService.printTest()
  } catch (e) {
    console.error('Error printing test', e)
    alert('Error al imprimir ticket de prueba. Verifique que la impresora esté encendida y conectada.')
  }
}

onMounted(() => {
  loadSavedPrinter()
  fetchPrinters()
})
</script>

<template>
  <div class="p-6 max-w-5xl mx-auto space-y-8 animate-in fade-in duration-500">
    <!-- Header -->
    <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
      <div>
        <h1 class="text-3xl font-bold text-slate-900 tracking-tight">Configuración de Impresora</h1>
        <p class="text-slate-500 mt-1">Gestione su impresora térmica Bluetooth para los tickets de venta.</p>
      </div>
      
      <div v-if="showSuccess" class="flex items-center gap-2 px-4 py-2 bg-emerald-100 text-emerald-700 rounded-lg border border-emerald-200 animate-in slide-in-from-top-4 duration-300">
        <CheckCircle2 class="w-5 h-5" />
        <span class="font-medium">Configuración guardada</span>
      </div>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
      <!-- Paired Devices List -->
      <div class="lg:col-span-1 space-y-4">
        <div class="flex items-center justify-between">
          <h2 class="text-lg font-semibold text-slate-800 flex items-center gap-2">
            <Bluetooth class="w-5 h-5 text-blue-500" />
            Dispositivos Vinculados
          </h2>
          <button 
            @click="fetchPrinters" 
            class="p-2 hover:bg-slate-100 rounded-full transition-colors group"
            :disabled="refreshing"
          >
            <RefreshCw class="w-4 h-4 text-slate-400 group-hover:text-slate-600" :class="{ 'animate-spin': refreshing }" />
          </button>
        </div>

        <div class="bg-white rounded-2xl border border-slate-200 overflow-hidden shadow-sm">
          <div v-if="pairedPrinters.length === 0 && !refreshing" class="p-8 text-center">
            <div class="w-12 h-12 bg-slate-50 rounded-full flex items-center justify-center mx-auto mb-3">
              <Smartphone class="w-6 h-6 text-slate-300" />
            </div>
            <p class="text-sm text-slate-500">No se encontraron dispositivos Bluetooth vinculados.</p>
            <p class="text-xs text-slate-400 mt-1">Vincule su impresora en los ajustes de Android primero.</p>
          </div>

          <div v-else class="divide-y divide-slate-100 max-h-[400px] overflow-y-auto">
            <div 
              v-for="printer in pairedPrinters" 
              :key="printer.mac"
              @click="selectPrinter(printer)"
              class="group flex items-center justify-between p-4 hover:bg-slate-50 cursor-pointer transition-colors"
              :class="{ 'bg-blue-50/50 border-l-4 border-blue-500': selectedPrinter.mac === printer.mac }"
            >
              <div class="flex items-center gap-3">
                <div class="w-10 h-10 rounded-xl bg-slate-100 flex items-center justify-center group-hover:bg-white transition-colors">
                  <Printer class="w-5 h-5 text-slate-500" />
                </div>
                <div>
                  <h4 class="font-medium text-slate-900 text-sm">{{ printer.name }}</h4>
                  <p class="text-xs text-slate-500 font-mono">{{ printer.mac }}</p>
                </div>
              </div>
              <CheckCircle2 v-if="selectedPrinter.mac === printer.mac" class="w-5 h-5 text-blue-500" />
            </div>
          </div>
        </div>
        
        <div class="p-4 bg-blue-50/50 border border-blue-100 rounded-xl flex gap-3">
          <Settings2 class="w-5 h-5 text-blue-400 shrink-0 mt-0.5" />
          <p class="text-xs text-blue-700 leading-relaxed">
            Solo se muestran las impresoras previamente vinculadas por Bluetooth desde el sistema operativo Android.
          </p>
        </div>
      </div>

      <!-- Settings & Configuration -->
      <div class="lg:col-span-2 space-y-6">
        <div class="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
          <div class="p-6 border-b border-slate-100 flex items-center justify-between">
            <h2 class="text-lg font-semibold text-slate-800 flex items-center gap-2">
              <Settings2 class="w-5 h-5 text-orange-500" />
              Parámetros de Impresión
            </h2>
            <div v-if="savedPrinter" class="flex items-center gap-2 px-3 py-1 bg-slate-100 rounded-full">
              <div class="w-2 h-2 rounded-full bg-emerald-500"></div>
              <span class="text-[10px] font-bold uppercase text-slate-600">Configurado</span>
            </div>
          </div>

          <div class="p-6 space-y-6">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <!-- Name & MAC Readonly display -->
              <div class="space-y-2">
                <label class="text-xs font-bold uppercase tracking-wider text-slate-500">Impresora Seleccionada</label>
                <div class="p-3 bg-slate-50 border border-slate-200 rounded-xl">
                  <div v-if="selectedPrinter.name" class="flex items-center gap-3">
                    <Printer class="w-5 h-5 text-slate-400" />
                    <div>
                      <p class="text-sm font-semibold text-slate-800">{{ selectedPrinter.name }}</p>
                      <p class="text-xs text-slate-500 font-mono">{{ selectedPrinter.mac }}</p>
                    </div>
                  </div>
                  <div v-else class="text-sm text-slate-400 italic py-1">
                    Seleccione una impresora de la lista.
                  </div>
                </div>
              </div>

              <!-- Charset Selection -->
              <div class="space-y-2">
                <label class="text-xs font-bold uppercase tracking-wider text-slate-500">Codificación (Charset)</label>
                <select 
                  v-model="selectedPrinter.charset"
                  class="w-full p-3 bg-white border border-slate-200 rounded-xl text-sm focus:ring-2 focus:ring-orange-500/20 focus:border-orange-500 outline-none transition-all"
                >
                  <option v-for="option in charsetOptions" :key="option.value" :value="option.value">
                    {{ option.label }}
                  </option>
                </select>
              </div>

              <!-- Paper Width -->
              <div class="space-y-2">
                <label class="text-xs font-bold uppercase tracking-wider text-slate-500">Ancho del Papel</label>
                <div class="grid grid-cols-2 gap-3">
                  <button 
                    v-for="option in paperWidthOptions" 
                    :key="option.value"
                    @click="selectedPrinter.paperWidth = option.value"
                    class="p-3 border rounded-xl text-sm font-medium transition-all"
                    :class="selectedPrinter.paperWidth === option.value 
                      ? 'border-orange-500 bg-orange-50 text-orange-700 shadow-sm' 
                      : 'border-slate-200 bg-white text-slate-600 hover:border-slate-300'"
                  >
                    {{ option.label }}
                  </button>
                </div>
              </div>
            </div>

            <!-- Action Buttons -->
            <div class="pt-6 border-t border-slate-100 flex flex-col sm:flex-row gap-4">
              <button 
                @click="handleSave"
                class="flex-1 flex items-center justify-center gap-2 bg-orange-500 hover:bg-orange-600 disabled:bg-slate-200 disabled:cursor-not-allowed text-white font-semibold py-3 px-6 rounded-xl transition-all shadow-lg shadow-orange-500/20"
                :disabled="!selectedPrinter.mac || saving"
              >
                <Save v-if="!saving" class="w-5 h-5" />
                <RefreshCw v-else class="w-5 h-5 animate-spin" />
                {{ saving ? 'Guardando...' : 'Guardar Configuración' }}
              </button>
              
              <button 
                @click="handlePrintTest"
                class="flex items-center justify-center gap-2 bg-slate-900 hover:bg-slate-800 disabled:bg-slate-200 disabled:cursor-not-allowed text-white font-semibold py-3 px-6 rounded-xl transition-all"
                :disabled="!selectedPrinter.mac"
              >
                <FileText class="w-5 h-5" />
                Ticket de Prueba
              </button>
            </div>
          </div>
        </div>

        <!-- Preview / Info Card -->
        <div class="bg-gradient-to-br from-[#0B1120] to-[#1E293B] rounded-2xl p-6 text-white shadow-xl relative overflow-hidden group">
          <div class="absolute -right-10 -bottom-10 w-40 h-40 bg-orange-500/10 rounded-full blur-3xl group-hover:bg-orange-500/20 transition-all duration-700"></div>
          
          <div class="relative z-10 flex items-start gap-4">
            <div class="w-12 h-12 rounded-full bg-orange-500/20 border border-orange-500/30 flex items-center justify-center">
              <FileText class="w-6 h-6 text-orange-400" />
            </div>
            <div class="flex-1">
              <h3 class="text-xl font-bold mb-2">Previsualización de Impresión</h3>
              <p class="text-slate-400 text-sm leading-relaxed mb-4">
                La impresora se configurará con {{ selectedPrinter.paperWidth }}mm de área efectiva. Use el comando de prueba para verificar que los caracteres especiales y acentos se impriman correctamente con el charset {{ selectedPrinter.charset }}.
              </p>
              
              <div v-if="selectedPrinter.mac" class="space-y-1 font-mono text-[10px] text-orange-200/60 bg-white/5 p-3 rounded-lg border border-white/10">
                <p>> Bluetooth::Connect({{ selectedPrinter.mac }})</p>
                <p>> CodePage::Set({{ selectedPrinter.charset }})</p>
                <p>> Text::Print("¡HOLA MUNDO!")</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.animate-in {
  animation-duration: 0.5s;
}
</style>
