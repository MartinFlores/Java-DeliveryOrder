<script setup lang="ts">
import { onMounted, ref, computed } from 'vue'
import {
  Package,
  Plus,
  Search,
  Filter,
  Edit2,
  Trash2,
  ExternalLink,
  ChevronRight,
  Tags,
  AlertTriangle,
  Layers,
  LayoutList,
  LayoutGrid,
  FlaskConical,
  Activity,
} from 'lucide-vue-next'
import { useSaleStore, type Product } from '@/stores/saleStore'
import { useAdminNavigation } from '@/composables/useAdminNavigation'
import CategoryManagerModal from './CategoryManagerModal.vue'
import { Loader } from '@/utils/Loader'
import { Toast } from '@/utils/Toast'

const saleStore = useSaleStore()
const { navigate } = useAdminNavigation()

const startCreate = () => {
  saleStore.editProduct = null
  navigate('product-edit')
}

const startEdit = (product: Product) => {
  saleStore.editProduct = product
  navigate('product-edit')
}

const handleDelete = async (id: number) => {
  if (!confirm('¿Estás seguro de eliminar este producto?')) return

  Loader.show('Eliminando producto...')
  const result = await saleStore.deleteProduct(id)
  Loader.hide()

  if (result.success) {
    Toast.success('Producto eliminado con éxito')
  } else {
    Toast.error('Error al eliminar: ' + result.message)
  }
}

const showCategoryModal = ref(false)
const searchQuery = ref('')
const selectedCategory = ref('null')
const viewMode = ref<'list' | 'grid'>(
  (localStorage.getItem('admin_products_view_mode') as 'list' | 'grid') || 'list',
)

const toggleViewMode = (mode: 'list' | 'grid') => {
  viewMode.value = mode
  localStorage.setItem('admin_products_view_mode', mode)
}

onMounted(async () => {
  await saleStore.fetchInitialData()
})

const lowStockCount = computed(() => {
  return saleStore.products.filter(
    (p) => (p.stock || 0) <= 5 && p.type === 'simple' && p.inventory_item_id,
  ).length
})

const stats = computed(() => [
  {
    label: 'Total Productos',
    value: saleStore.products.length,
    icon: Package,
    color: 'text-blue-700',
    bgColor: 'bg-blue-100/80',
  },
  {
    label: 'Categorías',
    value: saleStore.categories.length,
    icon: Tags,
    color: 'text-purple-700',
    bgColor: 'bg-purple-100/80',
  },
  {
    label: 'Bajo Stock',
    value: lowStockCount.value,
    icon: AlertTriangle,
    color: 'text-amber-700',
    bgColor: 'bg-amber-100/80',
  },
  {
    label: 'Activos',
    value: saleStore.products.filter((p) => p.status === 'active').length,
    icon: Layers,
    color: 'text-emerald-700',
    bgColor: 'bg-emerald-100/80',
  },
])

const filteredProducts = computed(() => {
  return saleStore.products.filter((p) => {
    const matchesSearch = p.name.toLowerCase().includes(searchQuery.value.toLowerCase())
    const matchesCategory =
      selectedCategory.value === 'null' ||
      Object.values(p.categories || {}).includes(selectedCategory.value)
    return matchesSearch && matchesCategory
  })
})

const formatPrice = (price: number) => {
  return new Intl.NumberFormat('es-MX', {
    style: 'currency',
    currency: 'MXN',
  }).format(price)
}
</script>

<template>
  <div class="flex-1 p-6 lg:p-8 space-y-8 pb-20">
    <!-- Header Section -->
    <header class="flex flex-col md:flex-row md:items-center justify-between gap-4">
      <div>
        <div class="flex items-center gap-2 mb-1">
          <span class="h-1 w-8 bg-orange-500 rounded-full"></span>
          <span class="text-xs font-bold text-orange-600 uppercase tracking-wider"
            >Gestión ERP</span
          >
        </div>
        <h1 class="text-3xl font-extrabold text-slate-900 tracking-tight">
          Productos e Inventarios
        </h1>
        <p class="text-slate-500 mt-1">
          Administra productos simples, compuestos y stock dinámico.
        </p>
      </div>

      <div class="flex items-center gap-3">
        <button
          @click="showCategoryModal = true"
          class="flex items-center gap-2 px-4 py-2.5 bg-white border border-slate-200 text-slate-700 font-semibold rounded-xl hover:bg-slate-50 transition-all active:scale-95 shadow-sm"
        >
          <Tags :size="18" />
          Categorías
        </button>
        <button
          @click="startCreate"
          class="flex items-center gap-2 px-6 py-2.5 bg-slate-900 text-white font-bold rounded-xl hover:bg-slate-800 transition-all active:scale-95 shadow-lg shadow-slate-900/20"
        >
          <Plus :size="20" />
          Nuevo Producto
        </button>
      </div>
    </header>

    <!-- KPIs Grid -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
      <div
        v-for="stat in stats"
        :key="stat.label"
        class="bg-white p-4 rounded-2xl border border-slate-200 shadow-sm hover:shadow-md transition-all group flex items-center gap-4"
      >
        <div
          :class="[
            stat.bgColor,
            stat.color,
            'w-12 h-12 rounded-xl flex items-center justify-center transition-all group-hover:scale-110 duration-300 flex-shrink-0',
          ]"
        >
          <component :is="stat.icon" :size="22" />
        </div>
        <div class="flex-1 min-w-0">
          <p
            class="text-[10px] font-black text-slate-400 uppercase tracking-widest mb-0.5 truncate"
          >
            {{ stat.label }}
          </p>
          <h3 class="text-2xl font-black text-slate-900 leading-none tracking-tight">
            {{ stat.value }}
          </h3>
        </div>
      </div>
    </div>

    <!-- Table Section -->
    <div class="bg-white rounded-2xl border border-slate-200/60 shadow-sm overflow-hidden">
      <!-- Search & Filters -->
      <div class="p-4 border-b border-slate-100 flex flex-col md:flex-row gap-4 bg-slate-50/30">
        <div class="relative flex-1">
          <Search class="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" :size="18" />
          <input
            v-model="searchQuery"
            type="text"
            placeholder="Buscar productos por nombre..."
            class="w-full pl-10 pr-4 py-2 bg-white border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-orange-500/20 focus:border-orange-500 transition-all text-sm"
          />
        </div>
        <div class="flex items-center gap-2">
          <div
            class="flex items-center gap-2 bg-white border border-slate-200 rounded-xl px-3 py-2"
          >
            <Filter :size="16" class="text-slate-400" />
            <select
              v-model="selectedCategory"
              class="bg-transparent text-sm font-medium text-slate-700 outline-none"
            >
              <option value="null">Todas las categorías</option>
              <option v-for="cat in saleStore.categories" :key="cat.id" :value="cat.name">
                {{ cat.name }}
              </option>
            </select>
          </div>

          <!-- View Toggle -->
          <div class="flex items-center bg-white border border-slate-200 rounded-xl p-1 shadow-sm">
            <button
              @click="toggleViewMode('list')"
              :class="[
                'p-1.5 rounded-lg transition-all',
                viewMode === 'list'
                  ? 'bg-slate-900 text-white shadow-md'
                  : 'text-slate-400 hover:text-slate-600 hover:bg-slate-100',
              ]"
            >
              <LayoutList :size="18" />
            </button>
            <button
              @click="toggleViewMode('grid')"
              :class="[
                'p-1.5 rounded-lg transition-all',
                viewMode === 'grid'
                  ? 'bg-slate-900 text-white shadow-md'
                  : 'text-slate-400 hover:text-slate-600 hover:bg-slate-100',
              ]"
            >
              <LayoutGrid :size="18" />
            </button>
          </div>
        </div>
      </div>

      <!-- List View Content -->
      <div v-if="viewMode === 'list'" class="overflow-x-auto">
        <table class="w-full text-left border-collapse">
          <thead>
            <tr
              class="text-[11px] uppercase tracking-wider text-slate-500 font-bold bg-slate-50/50 border-b border-slate-100"
            >
              <th class="px-6 py-4">Producto</th>
              <th class="px-6 py-4">Tipo</th>
              <th class="px-6 py-4">Categoría</th>
              <th class="px-6 py-4 text-center">Stock</th>
              <th class="px-6 py-4">Precio</th>
              <th class="px-6 py-4 text-right">Acciones</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-100">
            <tr
              v-for="product in filteredProducts"
              :key="product.id"
              class="hover:bg-slate-50/80 transition-colors group"
            >
              <td class="px-6 py-4">
                <div class="flex items-center gap-4">
                  <div
                    class="w-12 h-12 rounded-xl bg-slate-100 overflow-hidden flex-shrink-0 border border-slate-200"
                  >
                    <img
                      v-if="product.images && product.images.length > 0"
                      :src="product.images[0]"
                      class="w-full h-full object-cover group-hover:scale-110 transition-transform duration-500"
                    />
                    <div
                      v-else
                      class="w-full h-full flex items-center justify-center text-slate-400"
                    >
                      <Package :size="20" />
                    </div>
                  </div>
                  <div>
                    <p class="text-sm font-bold text-slate-800">{{ product.name }}</p>
                    <p class="text-[10px] text-slate-500 truncate max-w-[200px]">
                      {{ product.description }}
                    </p>
                  </div>
                </div>
              </td>
              <td class="px-6 py-4">
                <div class="flex items-center gap-1.5">
                  <component
                    :is="product.type === 'composed' ? FlaskConical : Package"
                    :size="14"
                    :class="product.type === 'composed' ? 'text-purple-500' : 'text-slate-400'"
                  />
                  <span
                    class="text-[10px] font-black uppercase tracking-widest"
                    :class="product.type === 'composed' ? 'text-purple-600' : 'text-slate-600'"
                  >
                    {{ product.type === 'composed' ? 'Compuesto' : 'Simple' }}
                  </span>
                </div>
              </td>
              <td class="px-6 py-4">
                <div class="flex flex-wrap gap-1">
                  <span
                    v-for="(name, id) in product.categories"
                    :key="id"
                    class="px-2 py-0.5 rounded-lg bg-slate-100 text-slate-600 text-[10px] font-bold border border-slate-200"
                  >
                    {{ name }}
                  </span>
                </div>
              </td>
              <td class="px-6 py-4 text-center">
                <div class="flex flex-col items-center">
                  <span
                    class="text-sm font-black"
                    :class="(product.stock || 0) <= 5 ? 'text-red-500' : 'text-slate-900'"
                  >
                    {{ product.stock !== undefined ? product.stock : '-' }}
                  </span>
                  <span
                    v-if="product.type === 'composed'"
                    class="text-[8px] font-black uppercase text-purple-400 tracking-tighter"
                    >BOM Calc</span
                  >
                  <span
                    v-else-if="!product.inventory_item_id"
                    class="text-[8px] font-black uppercase text-slate-300 tracking-tighter"
                    >Infinito</span
                  >
                </div>
              </td>
              <td class="px-6 py-4">
                <span class="text-sm font-black text-slate-900">{{
                  formatPrice(product.price)
                }}</span>
              </td>
              <td class="px-6 py-4 text-right">
                <div
                  class="flex items-center justify-end gap-1 opacity-10 md:opacity-0 group-hover:opacity-100 transition-opacity"
                >
                  <button
                    @click="startEdit(product)"
                    class="p-2 text-slate-500 hover:text-orange-500 hover:bg-orange-50 rounded-lg transition-all"
                  >
                    <Edit2 :size="18" />
                  </button>
                  <button
                    @click="handleDelete(product.id)"
                    class="p-2 text-slate-400 hover:text-red-600 hover:bg-red-50 rounded-lg transition-all"
                  >
                    <Trash2 :size="18" />
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Grid View Content -->
      <div v-else class="p-6">
        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          <div
            v-for="product in filteredProducts"
            :key="product.id"
            class="group bg-white rounded-2xl border border-slate-200 shadow-sm hover:shadow-xl hover:border-orange-500/50 transition-all duration-300 overflow-hidden flex flex-col"
          >
            <div class="relative aspect-square overflow-hidden bg-slate-100">
              <img
                v-if="product.images && product.images.length > 0"
                :src="product.images[0]"
                class="w-full h-full object-cover group-hover:scale-110 transition-transform duration-700"
              />
              <div v-else class="w-full h-full flex items-center justify-center text-slate-400">
                <Package :size="48" stroke-width="1.5" />
              </div>

              <!-- Badges -->
              <div class="absolute top-3 left-3 flex flex-col gap-2">
                <div class="flex flex-wrap gap-1">
                  <span
                    v-for="(name, id) in product.categories"
                    :key="id"
                    class="px-2 py-0.5 rounded-lg bg-white/90 backdrop-blur-md text-slate-900 text-[9px] font-black uppercase tracking-wider shadow-sm border border-white/20"
                  >
                    {{ name }}
                  </span>
                </div>
                <div class="flex gap-1">
                  <span
                    class="px-2 py-0.5 rounded-lg bg-slate-900 text-white text-[8px] font-black uppercase tracking-widest flex items-center gap-1 shadow-lg"
                  >
                    <component
                      :is="product.type === 'composed' ? FlaskConical : Package"
                      :size="10"
                    />
                    {{ product.type === 'composed' ? 'BOM' : 'Simple' }}
                  </span>
                  <span
                    v-if="
                      (product.stock || 0) <= 5 &&
                      (product.type === 'composed' || product.inventory_item_id)
                    "
                    class="px-2 py-0.5 rounded-lg bg-red-500 text-white text-[8px] font-black uppercase tracking-widest shadow-lg"
                  >
                    Bajo Stock
                  </span>
                </div>
              </div>

              <div class="absolute bottom-3 right-3">
                <div
                  class="bg-white/90 backdrop-blur-md px-3 py-1 rounded-xl shadow-lg border border-white/20 flex flex-col items-center"
                >
                  <span class="text-[8px] font-black text-slate-400 uppercase tracking-tighter"
                    >Stock</span
                  >
                  <span class="text-sm font-black text-slate-900">{{
                    product.stock !== undefined ? product.stock : '∞'
                  }}</span>
                </div>
              </div>
            </div>

            <div class="p-4 flex-1 flex flex-col">
              <div class="flex justify-between items-start mb-2">
                <h3
                  class="text-sm font-bold text-slate-800 line-clamp-1 group-hover:text-orange-600 transition-colors"
                >
                  {{ product.name }}
                </h3>
                <span class="text-sm font-black text-slate-900">{{
                  formatPrice(product.price)
                }}</span>
              </div>

              <div class="mt-auto pt-4 flex items-center justify-between border-t border-slate-50">
                <div class="flex items-center gap-1.5">
                  <span
                    class="w-1.5 h-1.5 rounded-full"
                    :class="product.status === 'active' ? 'bg-emerald-500' : 'bg-red-500'"
                  ></span>
                  <span
                    class="text-[9px] font-black uppercase tracking-widest"
                    :class="product.status === 'active' ? 'text-emerald-600' : 'text-red-500'"
                  >
                    {{ product.status === 'active' ? 'Activo' : 'Oculto' }}
                  </span>
                </div>
                <div class="flex gap-1">
                  <button
                    @click="startEdit(product)"
                    class="p-2 bg-slate-50 text-slate-400 hover:text-orange-500 hover:bg-orange-50 rounded-xl transition-all"
                  >
                    <Edit2 :size="16" />
                  </button>
                  <button
                    @click="handleDelete(product.id)"
                    class="p-2 bg-slate-50 text-slate-400 hover:text-red-500 hover:bg-red-50 rounded-xl transition-all"
                  >
                    <Trash2 :size="16" />
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.overflow-x-auto::-webkit-scrollbar {
  height: 6px;
}
.overflow-x-auto::-webkit-scrollbar-thumb {
  background: #e2e8f0;
  border-radius: 10px;
}
</style>
