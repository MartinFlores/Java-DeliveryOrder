<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import {
  X,
  UserPlus,
  UserRoundPen,
  Check,
  Loader2,
  ChevronDown,
  Eye,
  EyeOff,
} from 'lucide-vue-next'
import { Listbox, ListboxButton, ListboxOptions, ListboxOption } from '@headlessui/vue'

interface Props {
  show: boolean
  isSubmitting: boolean
  user?: any // Usuario para editar
  errorMessage?: string
}

const props = defineProps<Props>()
const emit = defineEmits(['close', 'submit'])

const roleOptions = ['Cajero', 'Cocina', 'Barra', 'Mesero'] as const
const statusOptions = ['Activo', 'Inactivo'] as const

const showPassword = ref(false)

const form = ref({
  name: '',
  whatsapp: '',
  password: '',
  role: 'Mesero' as 'Cajero' | 'Cocina' | 'Barra' | 'Mesero',
  status: 'Activo' as 'Activo' | 'Inactivo',
})

const isEditing = computed(() => !!props.user)

// Reset form when modal opens or user changes
watch(
  () => [props.show, props.user],
  ([newShow, newUser]) => {
    if (newShow) {
      if (newUser) {
        // Modo edición
        form.value = {
          name: newUser.name || '',
          whatsapp: newUser.whatsapp || '',
          password: newUser.password || '',
          role: newUser.role || 'Mesero',
          status: newUser.status || 'Activo',
        }
      } else {
        // Modo creación
        form.value = {
          name: '',
          whatsapp: '',
          password: '',
          role: 'Mesero',
          status: 'Activo',
        }
      }
    }
  },
)

const handleSubmit = () => {
  const data = { ...form.value }
  if (isEditing.value) {
    ;(data as any).id = props.user.id
  }
  emit('submit', data)
}
</script>

<template>
  <Teleport to="body">
    <Transition
      enter-from-class="opacity-0"
      enter-to-class="opacity-100"
      leave-from-class="opacity-100"
      leave-to-class="opacity-0"
    >
      <div
        v-if="show"
        class="fixed inset-0 z-[100] flex items-center justify-center md:p-4 bg-slate-950/80 backdrop-blur-sm"
        @click.self="emit('close')"
      >
        <Transition
          appear
          enter-from-class="opacity-0 scale-95 translate-y-4"
          enter-to-class="opacity-100 scale-100 translate-y-0"
          leave-from-class="opacity-100 scale-100 translate-y-0"
          leave-to-class="opacity-0 scale-95 translate-y-4"
        >
          <div
            v-if="show"
            class="bg-white w-full max-w-[500px] md:h-auto h-full md:rounded-[2.5rem] shadow-2xl relative overflow-hidden overflow-y-auto"
          >
            <!-- Close Button -->
            <button
              @click="emit('close')"
              class="absolute top-6 right-6 p-2 rounded-full bg-slate-100 text-slate-400 hover:text-slate-600 hover:bg-slate-200 transition-all z-20"
            >
              <X class="w-5 h-5" />
            </button>

            <div class="p-8">
              <!-- Icon & Title -->
              <div class="flex flex-col items-center mb-8 text-center">
                <div
                  :class="[
                    isEditing
                      ? 'bg-blue-50 border-blue-400 shadow-blue-400/20 text-blue-600'
                      : 'bg-orange-50 border-orange-400 shadow-orange-400/20 text-orange-600',
                  ]"
                  class="w-16 h-16 border-2 shadow-lg rounded-2xl flex items-center justify-center mb-4"
                >
                  <UserRoundPen v-if="isEditing" class="w-8 h-8" />
                  <UserPlus v-else class="w-8 h-8" />
                </div>
                <h2 class="text-2xl font-black text-slate-900 mb-2">
                  {{ isEditing ? 'Editar Usuario' : 'Nuevo Usuario' }}
                </h2>
                <p class="text-slate-400 font-medium text-sm">
                  {{
                    isEditing
                      ? 'Actualiza la información de este integrante.'
                      : 'Registra a un nuevo integrante en tu equipo.'
                  }}
                </p>
              </div>

              <!-- Form -->
              <form @submit.prevent="handleSubmit" class="space-y-6">
                <div class="space-y-2">
                  <label class="text-xs font-bold text-slate-500 uppercase tracking-wider ml-1"
                    >Nombre Completo</label
                  >
                  <input
                    v-model="form.name"
                    type="text"
                    required
                    placeholder="Ej. Juan Pérez"
                    class="w-full px-5 py-4 bg-slate-50 border border-slate-200 rounded-2xl focus:outline-none focus:ring-2 focus:ring-orange-500/20 focus:border-orange-500 transition-all text-sm font-medium"
                  />
                </div>

                <div class="space-y-2">
                  <label class="text-xs font-bold text-slate-500 uppercase tracking-wider ml-1"
                    >Número de WhatsApp (10 dígitos)</label
                  >
                  <input
                    v-model="form.whatsapp"
                    type="tel"
                    required
                    maxlength="10"
                    pattern="[0-9]{10}"
                    placeholder="Ej. 3312345678"
                    class="w-full px-5 py-4 bg-slate-50 border border-slate-200 rounded-2xl focus:outline-none focus:ring-2 focus:ring-orange-500/20 focus:border-orange-500 transition-all text-sm font-medium"
                    @input="form.whatsapp = form.whatsapp.replace(/\D/g, '')"
                  />
                </div>

                <div class="space-y-2">
                  <label class="text-xs font-bold text-slate-500 uppercase tracking-wider ml-1"
                    >Contraseña</label
                  >
                  <div class="relative">
                    <input
                      v-model="form.password"
                      :type="showPassword ? 'text' : 'password'"
                      required
                      placeholder="••••••••"
                      class="w-full px-5 py-4 bg-slate-50 border border-slate-200 rounded-2xl focus:outline-none focus:ring-2 focus:ring-orange-500/20 focus:border-orange-500 transition-all text-sm font-medium pr-12"
                    />
                    <button
                      type="button"
                      @click="showPassword = !showPassword"
                      class="absolute right-4 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600 transition-colors"
                    >
                      <Eye v-if="!showPassword" class="w-5 h-5" />
                      <EyeOff v-else class="w-5 h-5" />
                    </button>
                  </div>
                </div>

                <div class="grid grid-cols-2 gap-4">
                  <div class="space-y-2">
                    <label class="text-xs font-bold text-slate-500 uppercase tracking-wider ml-1"
                      >Rol</label
                    >
                    <Listbox v-model="form.role" v-slot="{ open }">
                      <div class="relative">
                        <ListboxButton
                          class="relative w-full px-5 py-4 bg-slate-50 border border-slate-200 rounded-2xl text-left focus:outline-none focus:ring-2 focus:ring-orange-500/20 focus:border-orange-500 transition-all text-sm font-medium"
                        >
                          <span class="block truncate">{{ form.role }}</span>
                          <span
                            class="pointer-events-none absolute inset-y-0 right-0 flex items-center pr-4"
                          >
                            <ChevronDown
                              :class="[
                                'h-5 w-5 text-slate-400 transition-transform duration-300',
                                open ? 'rotate-180' : '',
                              ]"
                            />
                          </span>
                        </ListboxButton>

                        <Transition
                          leave-active-class="transition duration-100 ease-in"
                          leave-from-class="opacity-100"
                          leave-to-class="opacity-0"
                        >
                          <ListboxOptions
                            class="absolute mt-2 max-h-60 w-full overflow-auto rounded-2xl bg-white p-1 text-base shadow-2xl ring-1 ring-black/5 focus:outline-none sm:text-sm z-50"
                          >
                            <ListboxOption
                              v-slot="{ active, selected }"
                              v-for="role in roleOptions"
                              :key="role"
                              :value="role"
                              as="template"
                            >
                              <li
                                :class="[
                                  active ? 'bg-orange-50 text-orange-900' : 'text-slate-900',
                                  'relative cursor-default select-none py-3 pl-10 pr-4 rounded-xl transition-colors',
                                ]"
                              >
                                <span
                                  :class="[
                                    selected ? 'font-bold' : 'font-medium',
                                    'block truncate',
                                  ]"
                                  >{{ role }}</span
                                >
                                <span
                                  v-if="selected"
                                  class="absolute inset-y-0 left-0 flex items-center pl-3 text-orange-600"
                                >
                                  <Check class="h-5 w-5" aria-hidden="true" />
                                </span>
                              </li>
                            </ListboxOption>
                          </ListboxOptions>
                        </Transition>
                      </div>
                    </Listbox>
                  </div>
                  <div class="space-y-2">
                    <label class="text-xs font-bold text-slate-500 uppercase tracking-wider ml-1">
                      Estado
                    </label>
                    <Listbox v-model="form.status" v-slot="{ open }">
                      <div class="relative">
                        <ListboxButton
                          class="relative w-full px-5 py-4 bg-slate-50 border border-slate-200 rounded-2xl text-left focus:outline-none focus:ring-2 focus:ring-orange-500/20 focus:border-orange-500 transition-all text-sm font-medium"
                        >
                          <span class="block truncate">{{ form.status }}</span>
                          <span
                            class="pointer-events-none absolute inset-y-0 right-0 flex items-center pr-4"
                          >
                            <ChevronDown
                              :class="[
                                'h-5 w-5 text-slate-400 transition-transform duration-300',
                                open ? 'rotate-180' : '',
                              ]"
                            />
                          </span>
                        </ListboxButton>

                        <Transition
                          leave-active-class="transition duration-100 ease-in"
                          leave-from-class="opacity-100"
                          leave-to-class="opacity-0"
                        >
                          <ListboxOptions
                            class="absolute mt-2 max-h-60 w-full overflow-auto rounded-2xl bg-white p-1 text-base shadow-2xl ring-1 ring-black/5 focus:outline-none sm:text-sm z-50"
                          >
                            <ListboxOption
                              v-slot="{ active, selected }"
                              v-for="status in statusOptions"
                              :key="status"
                              :value="status"
                              as="template"
                            >
                              <li
                                :class="[
                                  active ? 'bg-orange-50 text-orange-900' : 'text-slate-900',
                                  'relative cursor-default select-none py-3 pl-10 pr-4 rounded-xl transition-colors',
                                ]"
                              >
                                <span
                                  :class="[
                                    selected ? 'font-bold' : 'font-medium',
                                    'block truncate',
                                  ]"
                                >
                                  {{ status }}
                                </span>
                                <span
                                  v-if="selected"
                                  class="absolute inset-y-0 left-0 flex items-center pl-3 text-orange-600"
                                >
                                  <Check class="h-5 w-5" aria-hidden="true" />
                                </span>
                              </li>
                            </ListboxOption>
                          </ListboxOptions>
                        </Transition>
                      </div>
                    </Listbox>
                  </div>
                </div>

                <!-- Error Message -->
                <div
                  v-if="errorMessage"
                  class="bg-red-50 border border-red-100 p-4 rounded-2xl flex items-start gap-3 animate-in fade-in slide-in-from-top-2"
                >
                  <div class="w-2 h-2 rounded-full bg-red-500 mt-1.5 shrink-0"></div>
                  <p class="text-xs font-bold text-red-600 leading-relaxed">{{ errorMessage }}</p>
                </div>

                <div class="flex flex-col gap-3 pt-4 pb-4">
                  <button
                    type="submit"
                    :disabled="isSubmitting"
                    class="w-full py-4 rounded-2xl bg-slate-900 text-white font-bold flex items-center justify-center gap-2 hover:bg-slate-800 transition-all active:scale-[0.98] disabled:opacity-70"
                  >
                    <Check v-if="!isSubmitting" class="w-5 h-5" />
                    <Loader2 v-else class="w-5 h-5 animate-spin" />
                    {{
                      isSubmitting
                        ? 'Guardando...'
                        : isEditing
                          ? 'Actualizar Usuario'
                          : 'Registrar Usuario'
                    }}
                  </button>
                  <button
                    type="button"
                    @click="emit('close')"
                    class="w-full py-3 text-slate-500 font-bold text-sm hover:text-slate-800 transition-colors"
                  >
                    Cancelar
                  </button>
                </div>
              </form>
            </div>
          </div>
        </Transition>
      </div>
    </Transition>
  </Teleport>
</template>
