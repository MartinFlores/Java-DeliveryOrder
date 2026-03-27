<script setup lang="ts">
import { ref } from 'vue'
import { X, Plus, GripVertical, Pencil, Trash2, Check, Type, Tags } from 'lucide-vue-next'
import draggable from 'vuedraggable'
import { useSaleStore, type Category } from '@/stores/saleStore'
import { Loader } from '@/utils/Loader'

const props = defineProps<{
  show: boolean
}>()

const emit = defineEmits(['close'])

const saleStore = useSaleStore()
const isAdding = ref(false)
const editingId = ref<number | null>(null)
const formData = ref({
  name: '',
  icon: 'Package',
})

const closeModal = () => {
  isAdding.value = false
  editingId.value = null
  emit('close')
}

const startAdd = () => {
  formData.value = { name: '', icon: 'Package' }
  isAdding.value = true
  editingId.value = null
}

const startEdit = (category: Category) => {
  formData.value = { name: category.name, icon: category.icon }
  editingId.value = category.id
  isAdding.value = false
}

const saveCategory = async () => {
  if (!formData.value.name.trim()) return

  Loader.show(editingId.value ? 'Guardando...' : 'Creando...')
  let res
  if (editingId.value !== null) {
    res = await saleStore.updateCategory({
      id: editingId.value,
      name: formData.value.name,
      icon: formData.value.icon,
    })
  } else {
    res = await saleStore.createCategory({
      name: formData.value.name,
      icon: formData.value.icon,
    })
  }
  Loader.hide()

  if (res.success) {
    isAdding.value = false
    editingId.value = null
  } else {
    alert(res.message || 'Error al guardar')
  }
}

const deleteCategory = async (id: number) => {
  if (
    !confirm(
      '¿Estás seguro de eliminar esta categoría? Los productos podrían quedar sin categoría.',
    )
  )
    return

  Loader.show('Eliminando...')
  const res = await saleStore.deleteCategory(id)
  Loader.hide()

  if (!res.success) {
    alert(res.message || 'Error al eliminar')
  }
}

const onDragEnd = async () => {
  const ids = saleStore.categories.map((c) => c.id)
  // No loader for reorder to keep it smooth, but we sync with backend
  await saleStore.reorderCategories(ids)
}

const cancel = () => {
  isAdding.value = false
  editingId.value = null
}
</script>

<template>
  <div
    v-if="show"
    class="fixed inset-0 z-[100] flex items-center justify-center p-0"
    style="margin: 0 !important"
  >
    <!-- Backdrop -->
    <div class="absolute inset-0 bg-slate-900/60 backdrop-blur-sm" @click="closeModal"></div>

    <!-- Modal Content -->
    <div
      class="relative w-full h-full md:h-auto md:max-h-[85vh] md:max-w-xl bg-white md:rounded-3xl shadow-2xl flex flex-col overflow-hidden"
    >
      <!-- Header -->
      <div
        class="px-6 py-4 border-b border-slate-100 flex items-center justify-between bg-white sticky top-0 z-10"
      >
        <div>
          <h3 class="text-xl font-black text-slate-900">Categorías</h3>
          <p class="text-xs font-bold text-slate-500 uppercase tracking-widest">
            Gestionar y Ordenar
          </p>
        </div>
        <button
          @click="closeModal"
          class="p-2 hover:bg-slate-100 rounded-xl text-slate-400 transition-colors"
        >
          <X :size="20" />
        </button>
      </div>

      <!-- Add Section (Inline Form) -->
      <div v-if="isAdding || editingId !== null" class="p-6 bg-slate-50 border-b border-slate-100">
        <div class="space-y-4">
          <h4 class="text-sm font-bold text-slate-800">
            {{ editingId ? 'Editar Categoría' : 'Nueva Categoría' }}
          </h4>
          <div class="grid grid-cols-1 gap-4">
            <div class="space-y-1.5">
              <label class="text-[10px] font-black text-slate-400 uppercase tracking-wider ml-1"
                >Nombre</label
              >
              <div class="relative">
                <Type class="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" :size="16" />
                <input
                  v-model="formData.name"
                  type="text"
                  placeholder="Ej. Bebidas, Postres..."
                  class="w-full pl-10 pr-4 py-2.5 bg-white border border-slate-200 rounded-xl focus:ring-2 focus:ring-orange-500/20 focus:border-orange-500 outline-none text-sm transition-all"
                />
              </div>
            </div>
          </div>
          <div class="flex items-center gap-3 pt-2">
            <button
              @click="cancel"
              class="flex-1 py-2.5 px-4 rounded-xl text-sm font-bold text-slate-600 bg-white border border-slate-200 hover:bg-slate-50 transition-all"
            >
              Cancelar
            </button>
            <button
              @click="saveCategory"
              class="flex-1 py-2.5 px-4 rounded-xl text-sm font-bold text-white bg-orange-500 hover:bg-orange-600 shadow-lg shadow-orange-500/20 transition-all flex items-center justify-center gap-2"
            >
              <Check :size="18" />
              {{ editingId ? 'Guardar Cambios' : 'Crear Categoría' }}
            </button>
          </div>
        </div>
      </div>

      <!-- List Section -->
      <div class="flex-1 overflow-y-auto p-4 bg-slate-50/30">
        <div v-if="!isAdding && editingId === null" class="mb-4">
          <button
            @click="startAdd"
            class="w-full py-3 px-4 rounded-2xl border-2 border-dashed border-slate-200 text-slate-500 hover:border-orange-500 hover:text-orange-600 hover:bg-orange-50 transition-all flex items-center justify-center gap-2 font-bold text-sm"
          >
            <Plus :size="18" />
            Agregar Categoría
          </button>
        </div>

        <draggable
          v-model="saleStore.categories"
          item-key="id"
          handle=".drag-handle"
          class="space-y-2"
          ghost-class="opacity-50"
          @end="onDragEnd"
        >
          <template #item="{ element: cat }">
            <div
              class="group bg-white p-3 rounded-2xl border border-slate-200 shadow-sm flex items-center gap-3 hover:border-orange-200 hover:shadow-md transition-all flex-shrink-0"
            >
              <GripVertical class="text-slate-300 cursor-move drag-handle" :size="20" />

              <div class="flex-1 min-w-0">
                <h4 class="text-sm font-bold text-slate-800 truncate">{{ cat.name }}</h4>
                <p class="text-[10px] font-medium text-slate-400 uppercase tracking-tighter">
                  ID: {{ cat.id }}
                </p>
              </div>

              <!-- Actions -->
              <div class="flex items-center gap-1">
                <button
                  @click="startEdit(cat)"
                  class="p-2 text-slate-400 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-all"
                >
                  <Pencil :size="16" />
                </button>
                <button
                  @click="deleteCategory(cat.id)"
                  class="p-2 text-slate-400 hover:text-red-600 hover:bg-red-50 rounded-lg transition-all"
                >
                  <Trash2 :size="16" />
                </button>
              </div>
            </div>
          </template>
        </draggable>

        <div v-if="saleStore.categories.length === 0" class="py-12 text-center">
          <div
            class="w-16 h-16 bg-slate-100 rounded-full flex items-center justify-center mx-auto mb-4"
          >
            <Tags class="text-slate-300" :size="32" />
          </div>
          <p class="text-slate-500 font-bold">No hay categorías aun</p>
        </div>
      </div>

      <!-- Footer -->
      <div class="p-6 border-t border-slate-100 bg-white md:block hidden">
        <button
          @click="closeModal"
          class="w-full py-3 bg-slate-900 text-white font-bold rounded-2xl hover:bg-slate-800 transition-all shadow-lg shadow-slate-900/10"
        >
          Finalizar Gestión
        </button>
      </div>
      <div class="p-4 border-t border-slate-100 bg-white md:hidden sticky bottom-0">
        <button
          @click="closeModal"
          class="w-full py-4 bg-slate-900 text-white font-black rounded-2xl hover:bg-slate-800 transition-all shadow-lg"
        >
          Listo
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
