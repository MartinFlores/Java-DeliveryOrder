<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue'
import {
  ArrowLeft,
  Save,
  Package,
  Tag,
  DollarSign,
  BarChart3,
  Image as ImageIcon,
  Plus,
  X,
  CheckCircle2,
  AlertCircle,
  FlaskConical,
  Trash2,
  Info,
} from 'lucide-vue-next'
import { useSaleStore, type Product, type RecipeItem } from '@/stores/saleStore'
import { useAdminNavigation } from '@/composables/useAdminNavigation'
import { Loader } from '@/utils/Loader'
import { Toast } from '@/utils/Toast'

const saleStore = useSaleStore()
const { navigate } = useAdminNavigation()

const errors = ref<Record<string, string>>({})

const isEdit = !!saleStore.editProduct
const initialData = saleStore.editProduct
  ? { ...saleStore.editProduct }
  : {
      id: undefined as number | undefined,
      name: '',
      description: '',
      price: 0,
      purchase_price: 0,
      type: 'simple' as 'simple' | 'composed',
      inventory_item_id: null as number | null,
      track_inventory: false,
      recipe: [] as RecipeItem[],
      category_ids: [] as number[],
      images: [] as string[],
      status: 'active' as const,
      stock: 0,
    }

const formData = ref(initialData)
const selectedCategories = ref<number[]>(formData.value.category_ids || [])
const trackInventory = ref(!!formData.value.inventory_item_id)

// Data for new inventory item (if creating on-the-fly for simple)
const invData = ref({
  name: '',
  unit: 'pz',
  stock: 0,
  min_stock_alert: 0,
})

// Gallery mockup
const galleryMock = ref<string[]>(formData.value.images || [])

const goBack = () => {
  saleStore.editProduct = null
  navigate('products')
}

const toggleCategory = (id: number) => {
  const index = selectedCategories.value.indexOf(id)
  if (index > -1) {
    selectedCategories.value.splice(index, 1)
  } else {
    selectedCategories.value.push(id)
  }
}

// Recipe Management
const addRecipeItem = () => {
  if (saleStore.inventoryItems.length === 0) {
    Toast.warning('No hay insumos creados. Crea uno primero.')
    return
  }
  formData.value.recipe?.push({
    inventory_item_id: saleStore.inventoryItems[0]?.id || 0,
    quantity_required: 1,
  })
}

const removeRecipeItem = (index: number) => {
  formData.value.recipe?.splice(index, 1)
}

const validateForm = () => {
  errors.value = {}
  let isValid = true

  if (!formData.value.name.trim()) {
    errors.value.name = 'El nombre es obligatorio'
    isValid = false
  }

  if (formData.value.price <= 0) {
    errors.value.price = 'El precio debe ser mayor a 0'
    isValid = false
  }

  if (
    formData.value.type === 'composed' &&
    (!formData.value.recipe || formData.value.recipe.length === 0)
  ) {
    Toast.warning('Un producto compuesto debe tener al menos un insumo en su receta')
    isValid = false
  }

  if (!isValid) {
    Toast.warning('Completa los campos obligatorios')
  }

  return isValid
}

const saveProduct = async () => {
  if (!validateForm()) return

  Loader.show(isEdit ? 'Guardando cambios...' : 'Creando producto...')

  try {
    const payload: any = {
      ...formData.value,
      category_ids: selectedCategories.value,
      track_inventory: trackInventory.value,
    }

    if (formData.value.type === 'simple' && trackInventory.value && !isEdit) {
      payload.inventory_item_data = {
        ...invData.value,
        name: formData.value.name,
      }
    }

    let result
    if (isEdit) {
      result = await saleStore.updateProduct(payload as Product)
    } else {
      result = await saleStore.createProduct(payload as Partial<Product>)
    }

    if (result.success) {
      Toast.success(isEdit ? 'Producto actualizado' : 'Producto creado con éxito')
      goBack()
    } else {
      Toast.error(result.message || 'Error al guardar')
    }
  } catch (error: any) {
    Toast.error('Error de red o servidor')
  } finally {
    Loader.hide()
  }
}

onMounted(async () => {
  if (saleStore.categories.length === 0 || saleStore.inventoryItems.length === 0) {
    await saleStore.fetchInitialData()
  }

  if (!isEdit) {
    formData.value.type = 'simple'
  }
})

// Helpers
const getInventoryItemName = (id: number) => {
  return saleStore.inventoryItems.find((i) => i.id === id)?.name || 'Desconocido'
}

const getInventoryItemUnit = (id: number) => {
  return saleStore.inventoryItems.find((i) => i.id === id)?.unit || ''
}
</script>

<template>
  <div class="p-6 max-w-5xl mx-auto space-y-8 animate-in fade-in duration-500 pb-20">
    <!-- Header Navigation -->
    <div
      class="flex items-center justify-between sticky top-0 bg-slate-50/80 backdrop-blur-md z-20 py-4 -mx-6 px-6"
    >
      <div class="flex items-center gap-4">
        <button
          @click="goBack"
          class="p-2.5 rounded-2xl bg-white border border-slate-200 text-slate-500 hover:text-slate-900 hover:border-slate-300 hover:shadow-sm transition-all"
        >
          <ArrowLeft :size="20" />
        </button>
        <div>
          <h2 class="text-2xl font-black text-slate-900 tracking-tight">
            {{ isEdit ? 'Editar Producto' : 'Nuevo Producto' }}
          </h2>
          <p class="text-xs font-bold text-slate-400 uppercase tracking-widest">
            {{ isEdit ? 'ID: ' + formData.id : 'Configura tu producto ERP' }}
          </p>
        </div>
      </div>

      <button
        @click="saveProduct"
        class="bg-orange-500 hover:bg-orange-600 text-white px-6 py-3 rounded-2xl font-black text-sm shadow-xl shadow-orange-500/20 active:scale-95 transition-all flex items-center gap-2"
      >
        <Save :size="18" />
        {{ isEdit ? 'Actualizar' : 'Guardar Producto' }}
      </button>
    </div>

    <!-- Type Selection -->
    <section class="grid grid-cols-1 md:grid-cols-2 gap-4">
      <button
        @click="formData.type = 'simple'"
        class="p-6 rounded-[2rem] border-2 transition-all flex items-center gap-4 text-left group"
        :class="
          formData.type === 'simple'
            ? 'bg-slate-900 border-slate-900 text-white shadow-xl translate-y-[-4px]'
            : 'bg-white border-slate-100 hover:border-slate-300'
        "
      >
        <div
          class="w-14 h-14 rounded-2xl flex items-center justify-center transition-transform group-hover:scale-110"
          :class="formData.type === 'simple' ? 'bg-white/10' : 'bg-slate-50 text-slate-400'"
        >
          <Package :size="32" />
        </div>
        <div>
          <h4 class="font-black text-lg">Producto Simple</h4>
          <p
            class="text-xs font-medium"
            :class="formData.type === 'simple' ? 'text-slate-400' : 'text-slate-500'"
          >
            Venta directa con o sin inventario.
          </p>
        </div>
      </button>

      <button
        @click="formData.type = 'composed'"
        class="p-6 rounded-[2rem] border-2 transition-all flex items-center gap-4 text-left group"
        :class="
          formData.type === 'composed'
            ? 'bg-slate-900 border-slate-900 text-white shadow-xl translate-y-[-4px]'
            : 'bg-white border-slate-100 hover:border-slate-300'
        "
      >
        <div
          class="w-14 h-14 rounded-2xl flex items-center justify-center transition-transform group-hover:scale-110"
          :class="formData.type === 'composed' ? 'bg-white/10' : 'bg-slate-50 text-slate-400'"
        >
          <FlaskConical :size="32" />
        </div>
        <div>
          <h4 class="font-black text-lg">Producto Compuesto</h4>
          <p
            class="text-xs font-medium"
            :class="formData.type === 'composed' ? 'text-slate-400' : 'text-slate-500'"
          >
            Usa una receta (BOM) de múltiples insumos.
          </p>
        </div>
      </button>
    </section>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
      <!-- Left Column: Main Data -->
      <div class="lg:col-span-2 space-y-8">
        <!-- General Info Card -->
        <section class="bg-white p-8 rounded-[2rem] border border-slate-100 shadow-sm space-y-6">
          <div class="flex items-center gap-3 mb-2">
            <div
              class="w-10 h-10 rounded-xl bg-orange-50 text-orange-500 flex items-center justify-center"
            >
              <Info :size="20" />
            </div>
            <h3 class="text-lg font-black text-slate-800">Detalles del Producto</h3>
          </div>

          <div class="space-y-4">
            <div class="space-y-1.5">
              <label class="text-[10px] font-black text-slate-400 uppercase tracking-wider ml-1">
                Nombre <span class="text-red-500">*</span>
              </label>
              <input
                v-model="formData.name"
                type="text"
                placeholder="Ej. Hamburguesa Doble, Coca Cola 500ml..."
                class="w-full px-5 py-4 bg-slate-50 border rounded-2xl focus:ring-4 focus:ring-orange-500/10 focus:border-orange-500/50 outline-none text-base font-medium transition-all"
                :class="errors.name ? 'border-red-500 bg-red-50/50' : 'border-slate-100'"
              />
            </div>

            <div class="space-y-1.5">
              <label class="text-[10px] font-black text-slate-400 uppercase tracking-wider ml-1"
                >Descripción</label
              >
              <textarea
                v-model="formData.description"
                rows="2"
                placeholder="Ingredientes, tamaño, notas..."
                class="w-full px-5 py-4 bg-slate-50 border border-slate-100 rounded-2xl focus:ring-4 focus:ring-orange-500/10 focus:border-orange-500/50 outline-none text-base font-medium transition-all resize-none"
              ></textarea>
            </div>
          </div>
        </section>

        <!-- Dynamic Configuration (Simple vs Composed) -->

        <!-- SIMPLE: Inventory Logic -->
        <section
          v-if="formData.type === 'simple'"
          class="bg-white p-8 rounded-[2rem] border border-slate-100 shadow-sm space-y-6"
        >
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-3">
              <div
                class="w-10 h-10 rounded-xl bg-blue-50 text-blue-500 flex items-center justify-center"
              >
                <BarChart3 :size="20" />
              </div>
              <h3 class="text-lg font-black text-slate-800">Control de Inventario</h3>
            </div>
            <label class="relative inline-flex items-center cursor-pointer">
              <input type="checkbox" v-model="trackInventory" class="sr-only peer" />
              <div
                class="w-11 h-6 bg-slate-200 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-blue-600"
              ></div>
            </label>
          </div>

          <div
            v-if="trackInventory"
            class="grid grid-cols-1 md:grid-cols-3 gap-4 animate-in slide-in-from-top-2 duration-300"
          >
            <div v-if="!isEdit" class="space-y-1.5">
              <label class="text-[10px] font-black text-slate-400 uppercase tracking-wider ml-1"
                >Stock Inicial</label
              >
              <input
                v-model="invData.stock"
                type="number"
                class="w-full px-4 py-3 bg-slate-50 border border-slate-100 rounded-xl focus:ring-2 focus:ring-blue-500/20 outline-none font-bold"
              />
            </div>
            <div v-else class="space-y-1.5">
              <label class="text-[10px] font-black text-slate-400 uppercase tracking-wider ml-1"
                >Stock actual</label
              >
              <div
                class="w-full px-4 py-3 bg-slate-100 border border-slate-200 rounded-xl font-black text-slate-700"
              >
                {{ formData.stock || 0 }}
              </div>
            </div>

            <div class="space-y-1.5">
              <label class="text-[10px] font-black text-slate-400 uppercase tracking-wider ml-1"
                >Unidad</label
              >
              <input
                v-model="invData.unit"
                type="text"
                placeholder="pz, kg, lt..."
                class="w-full px-4 py-3 bg-slate-50 border border-slate-100 rounded-xl focus:ring-2 focus:ring-blue-500/20 outline-none font-bold"
                :disabled="isEdit"
              />
            </div>

            <div class="space-y-1.5">
              <label class="text-[10px] font-black text-slate-400 uppercase tracking-wider ml-1"
                >Alertar en</label
              >
              <input
                v-model="invData.min_stock_alert"
                type="number"
                class="w-full px-4 py-3 bg-slate-50 border border-slate-100 rounded-xl focus:ring-2 focus:ring-blue-500/20 outline-none font-bold"
              />
            </div>
          </div>
          <p v-else class="text-sm text-slate-400 italic">
            Este producto no rastrea existencias y siempre estará disponible.
          </p>
        </section>

        <!-- COMPOSED: Recipe Builder -->
        <section
          v-if="formData.type === 'composed'"
          class="bg-white p-8 rounded-[2rem] border border-slate-100 shadow-sm space-y-6"
        >
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-3">
              <div
                class="w-10 h-10 rounded-xl bg-purple-50 text-purple-500 flex items-center justify-center"
              >
                <FlaskConical :size="20" />
              </div>
              <h3 class="text-lg font-black text-slate-800">Receta / Insumos (BOM)</h3>
            </div>
            <button
              @click="addRecipeItem"
              class="text-xs font-black bg-purple-600 text-white px-4 py-2 rounded-xl hover:bg-purple-700 transition-all flex items-center gap-2"
            >
              <Plus :size="14" /> Agregar Insumo
            </button>
          </div>

          <div v-if="formData.recipe && formData.recipe.length > 0" class="space-y-3">
            <div
              v-for="(item, idx) in formData.recipe"
              :key="idx"
              class="flex gap-4 items-end p-4 bg-slate-50 rounded-2xl border border-slate-100 animate-in slide-in-from-right-4"
            >
              <div class="flex-1 space-y-1.5">
                <label class="text-[9px] font-black text-slate-400 uppercase tracking-wider ml-1"
                  >Seleccionar Insumo</label
                >
                <select
                  v-model="item.inventory_item_id"
                  class="w-full px-4 py-3 bg-white border border-slate-200 rounded-xl outline-none text-sm font-bold"
                >
                  <option v-for="inv in saleStore.inventoryItems" :key="inv.id" :value="inv.id">
                    {{ inv.name }} ({{ inv.stock }} {{ inv.unit }})
                  </option>
                </select>
              </div>

              <div class="w-32 space-y-1.5">
                <label class="text-[9px] font-black text-slate-400 uppercase tracking-wider ml-1"
                  >Cant. Requerida</label
                >
                <div class="relative">
                  <input
                    v-model="item.quantity_required"
                    type="number"
                    step="0.01"
                    class="w-full pl-4 pr-10 py-3 bg-white border border-slate-200 rounded-xl outline-none text-sm font-black"
                  />
                  <span
                    class="absolute right-3 top-1/2 -translate-y-1/2 text-[10px] font-bold text-slate-400 uppercase tracking-tighter"
                  >
                    {{ getInventoryItemUnit(item.inventory_item_id) }}
                  </span>
                </div>
              </div>

              <button
                @click="removeRecipeItem(idx)"
                class="p-3 text-red-400 hover:text-red-600 hover:bg-red-50 rounded-xl transition-all mb-0.5"
              >
                <Trash2 :size="18" />
              </button>
            </div>
          </div>

          <div
            v-else
            class="p-10 border-2 border-dashed border-slate-100 rounded-3xl flex flex-col items-center text-center"
          >
            <div
              class="w-16 h-16 bg-slate-50 rounded-full flex items-center justify-center mb-4 text-slate-300"
            >
              <FlaskConical :size="32" />
            </div>
            <h4 class="font-bold text-slate-500">No hay insumos agregados</h4>
            <p class="text-xs text-slate-400 max-w-xs mt-1">
              Define los ingredientes necesarios para este producto para calcular su disponibilidad
              dinámica.
            </p>
          </div>
        </section>

        <!-- Categories Selection -->
        <section class="bg-white p-8 rounded-[2rem] border border-slate-100 shadow-sm space-y-6">
          <div class="flex items-center gap-3 mb-2">
            <div
              class="w-10 h-10 rounded-xl bg-orange-50 text-orange-500 flex items-center justify-center"
            >
              <Tag :size="20" />
            </div>
            <h3 class="text-lg font-black text-slate-800">Categorías y Etiquetas</h3>
          </div>

          <div class="grid grid-cols-2 md:grid-cols-4 gap-3">
            <button
              v-for="cat in saleStore.categories"
              :key="cat.id"
              @click="toggleCategory(cat.id)"
              class="p-4 rounded-2xl border-2 transition-all flex flex-col items-center gap-2 group relative"
              :class="
                selectedCategories.includes(cat.id)
                  ? 'bg-orange-50 border-orange-200 translate-y-[-2px]'
                  : 'bg-slate-50 border-transparent hover:border-slate-200'
              "
            >
              <span class="text-2xl">{{ cat.icon }}</span>
              <span
                class="text-[10px] font-black uppercase tracking-tight"
                :class="selectedCategories.includes(cat.id) ? 'text-orange-600' : 'text-slate-500'"
              >
                {{ cat.name }}
              </span>
              <div
                v-if="selectedCategories.includes(cat.id)"
                class="absolute top-2 right-2 text-orange-500"
              >
                <CheckCircle2 :size="12" />
              </div>
            </button>
          </div>
        </section>
      </div>

      <!-- Right Column: Business Data -->
      <div class="space-y-8">
        <!-- Pricing Card -->
        <section
          class="bg-white p-8 rounded-[2rem] border border-slate-100 shadow-sm space-y-6 overflow-hidden relative"
        >
          <div class="absolute top-0 right-0 p-8 text-slate-50/50 -mr-4 -mt-4">
            <DollarSign :size="80" stroke-width="1" />
          </div>

          <div class="flex items-center gap-3 mb-2 relative">
            <div
              class="w-10 h-10 rounded-xl bg-emerald-50 text-emerald-500 flex items-center justify-center"
            >
              <DollarSign :size="20" />
            </div>
            <h3 class="text-lg font-black text-slate-800">Precios</h3>
          </div>

          <div class="space-y-5 relative">
            <div class="space-y-1.5">
              <label class="text-[10px] font-black text-slate-400 uppercase tracking-wider ml-1">
                Precio de Venta <span class="text-red-500">*</span>
              </label>
              <div class="relative">
                <span class="absolute left-5 top-1/2 -translate-y-1/2 font-black text-slate-400"
                  >$</span
                >
                <input
                  v-model="formData.price"
                  type="number"
                  step="0.01"
                  class="w-full pl-10 pr-5 py-4 bg-slate-50 border rounded-2xl focus:ring-4 focus:ring-emerald-500/10 focus:border-emerald-500/50 outline-none text-2xl font-black text-slate-800 transition-all"
                  :class="errors.price ? 'border-red-500 bg-red-50/50' : 'border-slate-100'"
                />
              </div>
            </div>

            <div class="space-y-1.5">
              <label class="text-[10px] font-black text-slate-400 uppercase tracking-wider ml-1"
                >Precio de Compra</label
              >
              <div class="relative">
                <span class="absolute left-5 top-1/2 -translate-y-1/2 font-black text-slate-400"
                  >$</span
                >
                <input
                  v-model="formData.purchase_price"
                  type="number"
                  step="0.01"
                  class="w-full pl-10 pr-5 py-4 bg-slate-50 border border-slate-100 rounded-2xl focus:ring-4 focus:ring-slate-500/10 focus:border-slate-500/50 outline-none text-base font-bold text-slate-600 transition-all"
                />
              </div>
            </div>

            <div class="pt-2">
              <div
                class="bg-emerald-50 p-4 rounded-2xl flex items-center justify-between border border-emerald-100/50"
              >
                <div class="flex flex-col">
                  <span class="text-[9px] font-black text-emerald-600 uppercase tracking-widest"
                    >Margen Bruto</span
                  >
                  <span class="text-xs font-medium text-emerald-600/70 italic"
                    >Calculado x unidad</span
                  >
                </div>
                <span class="text-xl font-black text-emerald-700">
                  ${{ (formData.price - (formData.purchase_price || 0)).toFixed(2) }}
                </span>
              </div>
            </div>
          </div>
        </section>

        <!-- Status Selection -->
        <section class="bg-white p-8 rounded-[2rem] border border-slate-100 shadow-sm">
          <div class="flex items-center gap-3 mb-6">
            <div
              class="w-10 h-10 rounded-xl bg-slate-900 text-white flex items-center justify-center"
            >
              <CheckCircle2 :size="20" />
            </div>
            <h3 class="text-lg font-black text-slate-800">Estado</h3>
          </div>

          <div class="flex items-center gap-4">
            <button
              @click="formData.status = 'active'"
              class="flex-1 py-4 rounded-2xl border-2 text-[10px] font-black uppercase tracking-widest transition-all shadow-sm"
              :class="
                formData.status === 'active'
                  ? 'bg-slate-900 border-slate-900 text-white shadow-xl translate-y-[-2px]'
                  : 'bg-white border-slate-100 text-slate-400'
              "
            >
              Activo
            </button>
            <button
              @click="formData.status = 'hidden'"
              class="flex-1 py-4 rounded-2xl border-2 text-[10px] font-black uppercase tracking-widest transition-all shadow-sm"
              :class="
                formData.status === 'hidden'
                  ? 'bg-red-500 border-red-500 text-white shadow-xl translate-y-[-2px]'
                  : 'bg-white border-slate-100 text-slate-400'
              "
            >
              Oculto
            </button>
          </div>
        </section>

        <!-- Help Card -->
        <div
          class="p-6 bg-slate-900 rounded-[2rem] text-white space-y-4 shadow-2xl overflow-hidden relative"
        >
          <div class="absolute -bottom-8 -right-8 opacity-10">
            <FlaskConical :size="140" />
          </div>
          <div class="flex items-center gap-2 relative">
            <div class="w-2 h-2 rounded-full bg-orange-500"></div>
            <h4 class="text-xs font-black uppercase tracking-widest">ERP Insight</h4>
          </div>
          <p class="text-[10px] font-bold text-slate-400 leading-relaxed relative">
            Los productos <span class="text-white">compuestos</span> bloquean la venta
            automáticamente si alguno de sus insumos no tiene stock suficiente.
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
input[type='number']::-webkit-inner-spin-button,
input[type='number']::-webkit-outer-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

.animate-in {
  animation: fadeIn 0.4s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
