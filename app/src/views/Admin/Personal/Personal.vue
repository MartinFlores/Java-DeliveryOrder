<script setup lang="ts">
import { ref, computed } from 'vue'
import {
  Users,
  UserPlus,
  Search,
  MessageSquare,
  Shield,
  MoreVertical,
  Edit,
  Trash2,
  ChevronRight,
  Filter,
  Loader2,
} from 'lucide-vue-next'
import { api } from '@/utils/axios'
import { onMounted } from 'vue'
import UserModal from './components/UserModal.vue'
import { Loader } from '@/utils/Loader'

interface User {
  id: number
  name: string
  whatsapp: string
  role: 'Cajero' | 'Cocina' | 'Barra' | 'Mesero'
  status: 'Activo' | 'Inactivo' | 'Pendiente'
  password: string
  avatar?: string
  lastActive: string
}

const users = ref<User[]>([])
const isLoading = ref(true)
const searchQuery = ref('')
const showModal = ref(false)
const isSubmitting = ref(false)
const editingUser = ref<User | null>(null)
const modalError = ref('')

const fetchPersonnel = async () => {
  isLoading.value = true
  try {
    const { data } = await api.get('/users/')
    if (data.status === 'ok') {
      users.value = data.data.map((item: any) => ({
        id: item.id,
        name: item.name,
        whatsapp: item.whatsapp,
        role: item.role,
        status: item.status,
        password: item.password,
        avatar: item.avatar,
        lastActive: 'Desconocido',
      }))
    }
  } catch (error) {
    console.error('Error fetching users:', error)
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  fetchPersonnel()
})

const filteredUsers = computed(() => {
  if (!searchQuery.value) return users.value
  const query = searchQuery.value.toLowerCase()
  return users.value.filter(
    (user) =>
      user.name.toLowerCase().includes(query) ||
      user.whatsapp.toLowerCase().includes(query) ||
      user.role.toLowerCase().includes(query),
  )
})

const openModal = () => {
  editingUser.value = null
  modalError.value = ''
  showModal.value = true
}

const editUser = (user: User) => {
  editingUser.value = user
  modalError.value = ''
  showModal.value = true
}

const deleteUser = async (user: User) => {
  if (!confirm(`¿Estás seguro de que deseas eliminar a ${user.name}?`)) {
    return
  }

  Loader.show('Eliminando usuario...')
  try {
    const { data } = await api.post('/users/delete', { id: user.id })
    if (data.status === 'ok') {
      await fetchPersonnel()
    } else {
      alert('Error: ' + data.message)
    }
  } catch (error) {
    console.error('Error deleting user:', error)
    alert('Error al eliminar usuario')
  } finally {
    Loader.hide()
  }
}

const handleSubmit = async (userData: any) => {
  isSubmitting.value = true
  Loader.show(editingUser.value ? 'Actualizando...' : 'Registrando...')
  try {
    const endpoint = editingUser.value ? '/users/update' : '/users/create'
    const { data } = await api.post(endpoint, userData)

    if (data.status === 'ok') {
      await fetchPersonnel() // Recargamos la lista
      showModal.value = false
    } else {
      modalError.value = data.message
    }
  } catch (error: any) {
    console.error('Error saving user:', error)
    alert('Error al guardar usuario')
  } finally {
    isSubmitting.value = false
    Loader.hide()
  }
}

const getRoleClass = (role: string) => {
  switch (role) {
    case 'Cajero':
      return 'bg-emerald-50 text-emerald-600 border-emerald-100'
    case 'Cocina':
      return 'bg-orange-50 text-orange-600 border-orange-100'
    case 'Mesero':
      return 'bg-blue-50 text-blue-600 border-blue-100'
    default:
      return 'bg-slate-50 text-slate-600 border-slate-100'
  }
}

const getStatusClass = (status: string) => {
  switch (status) {
    case 'Activo':
      return 'bg-emerald-50 text-emerald-600'
    case 'Inactivo':
      return 'bg-red-50 text-red-600'
    case 'Pendiente':
      return 'bg-amber-50 text-amber-600'
    default:
      return 'bg-slate-50 text-slate-600'
  }
}
</script>

<template>
  <div class="wrap-view text-slate-800">
    <!-- Header -->
    <header class="mb-10">
      <div class="flex items-center gap-3 mb-4">
        <span class="h-1 w-12 bg-orange-600 rounded-full"></span>
        <span class="text-orange-600 font-bold tracking-wider uppercase text-sm">Personal</span>
      </div>
      <div class="flex flex-col md:flex-row md:items-end justify-between gap-6">
        <div>
          <h1 class="text-4xl font-extrabold text-slate-900 mb-4 tracking-tight">
            Gestión de Usuarios
          </h1>
          <p class="text-slate-500 max-w-2xl text-lg leading-relaxed">
            Administra los accesos de tu equipo, asigna roles específicos y monitorea su actividad
            en el sistema.
          </p>
        </div>
        <button
          @click="openModal"
          class="flex items-center gap-2 bg-slate-900 text-white px-6 py-3.5 rounded-2xl font-bold shadow-lg shadow-slate-900/20 hover:bg-slate-800 transition-all active:scale-[0.98] whitespace-nowrap"
        >
          <UserPlus class="w-5 h-5" />
          Dar de alta nuevo
        </button>
      </div>
    </header>

    <!-- Content Card -->
    <div
      class="bg-white rounded-[2.5rem] border border-slate-200/60 shadow-[0_8px_30px_rgb(0,0,0,0.04)] overflow-hidden"
    >
      <!-- Toolbar -->
      <div
        class="p-6 border-b border-slate-100 flex flex-col md:flex-row gap-4 items-center justify-between"
      >
        <div class="relative w-full md:w-96">
          <Search class="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-400" />
          <input
            v-model="searchQuery"
            type="text"
            placeholder="Buscar por nombre, whatsapp o rol..."
            class="w-full pl-12 pr-4 py-3 bg-slate-50 border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-orange-500/20 focus:border-orange-500 transition-all text-sm"
          />
        </div>
        <div class="flex items-center gap-3 w-full md:w-auto">
          <button
            class="flex-1 md:flex-none flex items-center justify-center gap-2 px-4 py-3 bg-white border border-slate-200 rounded-xl text-slate-600 text-sm font-semibold hover:bg-slate-50 transition-colors"
          >
            <Filter class="w-4 h-4" />
            Filtros
          </button>
          <div class="h-8 w-px bg-slate-200 hidden md:block"></div>
          <p class="text-sm text-slate-500 font-medium">
            <span class="font-bold text-slate-900">{{ filteredUsers.length }}</span> usuarios
            encontrados
          </p>
        </div>
      </div>

      <!-- Table -->
      <div class="overflow-x-auto relative min-h-[400px]">
        <!-- Loader Overaly -->
        <div
          v-if="isLoading && users.length === 0"
          class="absolute inset-0 bg-white/50 backdrop-blur-[2px] z-10 flex flex-col items-center justify-center gap-4"
        >
          <div class="relative">
            <div
              class="w-12 h-12 border-4 border-slate-100 border-t-orange-600 rounded-full animate-spin"
            ></div>
            <div class="absolute inset-0 flex items-center justify-center">
              <Users class="w-5 h-5 text-orange-600/30" />
            </div>
          </div>
          <p class="text-slate-500 font-bold animate-pulse text-sm">Cargando equipo...</p>
        </div>

        <table class="w-full text-left border-collapse">
          <thead>
            <tr
              class="bg-slate-50/50 text-xs uppercase tracking-wider text-slate-500 font-bold border-b border-slate-100"
            >
              <th class="p-5 pl-8">Usuario</th>
              <th class="p-5">Rol</th>
              <th class="p-5">Estado</th>
              <th class="p-5">Última actividad</th>
              <th class="p-5 pr-8 text-right">Acciones</th>
            </tr>
          </thead>
          <tbody
            class="divide-y divide-slate-100"
            :class="{ 'opacity-50 pointer-events-none': isLoading && users.length > 0 }"
          >
            <tr
              v-for="user in filteredUsers"
              :key="user.id"
              class="hover:bg-slate-50/50 transition-colors group"
            >
              <td class="p-5 pl-8">
                <div class="flex items-center gap-4">
                  <div
                    class="w-11 h-11 rounded-full bg-gradient-to-br from-slate-100 to-slate-200 border-2 border-white shadow-sm flex items-center justify-center overflow-hidden"
                  >
                    <span v-if="!user.avatar" class="text-slate-500 font-bold text-sm">{{
                      user.name.charAt(0)
                    }}</span>
                    <img v-else :src="user.avatar" alt="Avatar" />
                  </div>
                  <div class="flex flex-col">
                    <span class="font-bold text-slate-900">{{ user.name }}</span>
                    <span class="text-xs text-slate-500 flex items-center gap-1">
                      <MessageSquare class="w-3 h-3 text-emerald-500" />
                      {{ user.whatsapp }}
                    </span>
                  </div>
                </div>
              </td>
              <td class="p-5">
                <span
                  :class="[
                    'px-3 py-1 rounded-lg text-xs font-bold border',
                    getRoleClass(user.role),
                  ]"
                >
                  {{ user.role }}
                </span>
              </td>
              <td class="p-5">
                <span
                  :class="[
                    'inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-bold',
                    getStatusClass(user.status),
                  ]"
                >
                  <span
                    :class="[
                      'w-1.5 h-1.5 rounded-full',
                      user.status === 'Activo'
                        ? 'bg-emerald-500'
                        : user.status === 'Inactivo'
                          ? 'bg-red-500'
                          : 'bg-amber-500',
                    ]"
                  ></span>
                  {{ user.status }}
                </span>
              </td>
              <td class="p-5 text-sm text-slate-500 font-medium">
                {{ user.lastActive }}
              </td>
              <td class="p-5 pr-8 text-right">
                <div
                  class="flex items-center justify-end gap-2 opacity-0 group-hover:opacity-100 transition-opacity"
                >
                  <button
                    @click="editUser(user)"
                    class="p-2 text-slate-400 hover:text-slate-900 hover:bg-slate-100 rounded-lg transition-all"
                  >
                    <Edit class="w-4.5 h-4.5" />
                  </button>
                  <button
                    @click="deleteUser(user)"
                    class="p-2 text-slate-400 hover:text-red-600 hover:bg-red-50 rounded-lg transition-all"
                  >
                    <Trash2 class="w-4.5 h-4.5" />
                  </button>
                  <button
                    class="p-2 text-slate-400 hover:text-slate-900 hover:bg-slate-100 rounded-lg transition-all"
                  >
                    <MoreVertical class="w-4.5 h-4.5" />
                  </button>
                </div>
              </td>
            </tr>
            <!-- Empty State -->
            <tr v-if="filteredUsers.length === 0 && !isLoading">
              <td colspan="5" class="p-20 text-center">
                <div class="flex flex-col items-center">
                  <div class="p-4 bg-slate-50 rounded-full mb-4">
                    <Users class="w-10 h-10 text-slate-300" />
                  </div>
                  <h3 class="text-lg font-bold text-slate-900 mb-1">No se encontraron usuarios</h3>
                  <p class="text-slate-500 text-sm">Intenta con otros términos de búsqueda.</p>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- MODAL REGISTRO -->
    <UserModal
      :show="showModal"
      :is-submitting="isSubmitting"
      :user="editingUser"
      :error-message="modalError"
      @close="showModal = false"
      @submit="handleSubmit"
    />
  </div>
</template>

<style scoped>
.wrap-view {
  padding: 2rem;
  max-width: 1400px;
  margin: 0 auto;
}

@media (max-width: 640px) {
  .wrap-view {
    padding: 1rem;
  }
}
</style>
