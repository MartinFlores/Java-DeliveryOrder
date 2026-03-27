<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useSaleStore, type Product } from '@/stores/saleStore'
import {
  Search,
  ShoppingCart,
  Trash2,
  Plus,
  Minus,
  ChevronRight,
  Check,
  PackageX,
  Loader2,
  UtensilsCrossed,
  CreditCard,
  Banknote,
  Smartphone,
  User as UserIcon,
  X,
  Clock,
} from 'lucide-vue-next'

const saleStore = useSaleStore()
const showCheckoutModal = ref(false)
const customerName = ref('Público General')
const isProcessing = ref(false)
const isOpenOrder = ref(false)

const payments = ref<{ method: string; amount: number }[]>([])
const selectedMethod = ref('Efectivo')
const amountToAdd = ref(0)

const paymentMethods = [
  { id: 'Efectivo', icon: Banknote, color: 'text-green-500', bg: 'bg-green-50' },
  { id: 'Tarjeta', icon: CreditCard, color: 'text-blue-500', bg: 'bg-blue-50' },
  { id: 'Transferencia', icon: Smartphone, color: 'text-purple-500', bg: 'bg-purple-50' },
]

const totalPaid = computed(() => payments.value.reduce((sum, p) => sum + p.amount, 0))
const remainingBalance = computed(() => Math.max(0, saleStore.cartTotal - totalPaid.value))

const openCheckout = () => {
  if (saleStore.cart.length === 0) return
  payments.value = []
  customerName.value = 'Público General'
  isOpenOrder.value = false

  // Por defecto empezamos con la intención de cobrar todo (Pagar Orden)
  payments.value.push({
    method: 'Efectivo',
    amount: saleStore.cartTotal,
  })

  amountToAdd.value = 0
  showCheckoutModal.value = true
}

const toggleOrderType = (isNowOpen: boolean) => {
  isOpenOrder.value = isNowOpen
  if (isOpenOrder.value) {
    // Si es orden abierta, quitamos los pagos pre-cargados
    payments.value = []
    amountToAdd.value = saleStore.cartTotal
  } else {
    // Si es para pagar, re-cargamos el pago total en efectivo
    payments.value = [{ method: 'Efectivo', amount: saleStore.cartTotal }]
    amountToAdd.value = 0
  }
}

const addPayment = () => {
  if (amountToAdd.value <= 0) return
  const amount = Math.min(amountToAdd.value, remainingBalance.value)
  if (amount <= 0) return

  payments.value.push({
    method: selectedMethod.value,
    amount: amount,
  })

  amountToAdd.value = remainingBalance.value
}

const removePayment = (index: number) => {
  payments.value.splice(index, 1)
  amountToAdd.value = remainingBalance.value
}

const handleCheckout = async () => {
  if (saleStore.cart.length === 0) return

  // Si no hay pagos y no hay nombre, pedir uno para poder identificar la orden abierta
  if (remainingBalance.value > 0.01 && !customerName.value.trim()) {
    alert('Por favor ingresa el nombre del cliente o mesa para dejar la orden abierta')
    return
  }

  isProcessing.value = true

  const paymentsData = payments.value.map((p) => ({
    payment_method: p.method,
    amount: p.amount,
  }))

  const finalCustomerName = customerName.value.trim() || 'Público General'
  const result = await saleStore.placeOrder(finalCustomerName, paymentsData)

  if (result.success) {
    alert(result.message)
    showCheckoutModal.value = false
    customerName.value = 'Público General'
    payments.value = []
  } else {
    alert(result.message)
  }

  isProcessing.value = false
}

const formatCurrency = (value: number) => {
  return new Intl.NumberFormat('es-MX', { style: 'currency', currency: 'MXN' }).format(value)
}

const toggleOT = (type: boolean) => {
  toggleOrderType(type)
}

onMounted(() => {
  saleStore.fetchInitialData()
})
</script>

<template>
  <div class="h-full flex flex-col md:flex-row overflow-hidden bg-slate-50">
    <!-- LEFT: PRODUCT SELECTION -->
    <div class="flex-1 flex flex-col overflow-hidden p-6 gap-6">
      <!-- Search and Filters -->
      <div class="flex flex-col md:flex-row gap-4">
        <div class="relative flex-1 group">
          <Search
            class="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 group-focus-within:text-orange-500 transition-colors"
          />
          <input
            v-model="saleStore.searchQuery"
            type="text"
            placeholder="Buscar producto..."
            class="w-full pl-12 pr-4 py-4 bg-white border border-slate-200 rounded-2xl focus:outline-none focus:ring-4 focus:ring-orange-500/10 focus:border-orange-500 transition-all font-medium text-slate-700 shadow-sm"
          />
        </div>
      </div>

      <!-- Categories -->
      <div class="flex gap-3 overflow-x-auto pb-2 scrollbar-hide">
        <button
          @click="saleStore.selectedCategoryId = null"
          :class="[
            'px-6 py-3 rounded-2xl font-bold text-sm transition-all whitespace-nowrap shadow-sm border',
            saleStore.selectedCategoryId === null
              ? 'bg-slate-900 text-white border-slate-900'
              : 'bg-white text-slate-500 border-slate-200 hover:border-slate-300',
          ]"
        >
          Todos
        </button>
        <button
          v-for="cat in saleStore.categories"
          :key="cat.id"
          @click="saleStore.selectedCategoryId = cat.id"
          :class="[
            'px-6 py-3 rounded-2xl font-bold text-sm transition-all whitespace-nowrap flex items-center gap-2 shadow-sm border',
            saleStore.selectedCategoryId === cat.id
              ? 'bg-orange-500 text-white border-orange-500 shadow-lg shadow-orange-500/20'
              : 'bg-white text-slate-500 border-slate-200 hover:border-slate-300',
          ]"
        >
          <span>{{ cat.icon }}</span>
          {{ cat.name }}
        </button>
      </div>

      <!-- Products Grid -->
      <div class="flex-1 overflow-y-auto scroll-smooth pr-2">
        <div
          v-if="saleStore.isLoading"
          class="h-full flex flex-col items-center justify-center text-slate-400 gap-4"
        >
          <Loader2 class="w-12 h-12 animate-spin text-orange-500" />
          <span class="font-bold uppercase tracking-widest text-xs">Cargando menú...</span>
        </div>

        <div
          v-else-if="saleStore.filteredProducts.length === 0"
          class="h-full flex flex-col items-center justify-center text-slate-400 gap-4"
        >
          <div class="w-20 h-20 bg-slate-100 rounded-3xl flex items-center justify-center">
            <PackageX class="w-10 h-10" />
          </div>
          <p class="font-bold">No se encontraron productos</p>
        </div>

        <div v-else class="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-3 gap-4 pb-6">
          <div
            v-for="product in saleStore.filteredProducts"
            :key="product.id"
            @click="saleStore.addToCart(product)"
            class="bg-white p-4 rounded-3xl border border-slate-100 shadow-sm hover:shadow-xl hover:shadow-slate-200/50 hover:-translate-y-1 transition-all cursor-pointer group relative overflow-hidden"
          >
            <!-- Content -->
            <div class="flex flex-col h-full gap-3">
              <div class="flex justify-between items-start">
                <div
                  class="w-12 h-12 bg-slate-50 rounded-2xl flex items-center justify-center text-2xl overflow-hidden"
                >
                  <img
                    v-if="product.images && product.images.length > 0"
                    :src="product.images[0]"
                    class="w-full h-full object-cover"
                  />
                  <span v-else>🍽️</span>
                </div>
                <div
                  v-if="product.categories && Object.keys(product.categories).length > 0"
                  class="bg-orange-50 text-orange-600 px-3 py-1 rounded-full text-[10px] font-black uppercase truncate max-w-[100px]"
                >
                  {{ Object.values(product.categories)[0] }}
                </div>
              </div>

              <div>
                <h4 class="font-black text-slate-900 leading-tight mb-1">{{ product.name }}</h4>
                <p class="text-xs text-slate-400 font-medium line-clamp-2">
                  {{ product.description }}
                </p>
              </div>

              <div class="mt-auto pt-3 border-t border-slate-50 flex justify-between items-center">
                <span class="text-xl font-black text-slate-900">{{
                  formatCurrency(product.price)
                }}</span>
                <div
                  class="w-10 h-10 bg-slate-900 text-white rounded-xl flex items-center justify-center group-hover:bg-orange-500 transition-colors shadow-lg shadow-slate-900/10"
                >
                  <Plus class="w-5 h-5" />
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- RIGHT: CART SUMMARY -->
    <div
      class="w-full md:w-[400px] bg-white border-l border-slate-200 flex flex-col shadow-2xl overflow-hidden"
    >
      <!-- Cart Header -->
      <div class="p-6 border-b border-slate-100 flex items-center justify-between">
        <div class="flex items-center gap-3">
          <div
            class="w-10 h-10 bg-orange-100 text-orange-600 rounded-xl flex items-center justify-center"
          >
            <ShoppingCart class="w-6 h-6" />
          </div>
          <h3 class="text-xl font-black text-slate-900 tracking-tight">Tu Orden</h3>
        </div>
        <button
          @click="saleStore.clearCart"
          class="text-slate-400 hover:text-red-500 transition-colors"
          title="Vaciar carrito"
        >
          <Trash2 class="w-5 h-5" />
        </button>
      </div>

      <!-- Cart Items -->
      <div class="flex-1 overflow-y-auto p-6 scroll-smooth">
        <div
          v-if="saleStore.cart.length === 0"
          class="h-full flex flex-col items-center justify-center text-slate-300 text-center gap-4"
        >
          <UtensilsCrossed class="w-16 h-16 opacity-20" />
          <p
            class="font-bold text-slate-400 uppercase tracking-widest text-xs px-10 leading-relaxed"
          >
            Agrega productos a la orden para comenzar
          </p>
        </div>

        <div v-else class="space-y-4">
          <div
            v-for="item in saleStore.cart"
            :key="item.id"
            class="flex items-center gap-4 p-4 rounded-3xl bg-slate-50 border border-slate-100 animate-in slide-in-from-right-4 duration-300"
          >
            <div
              class="w-12 h-12 bg-white rounded-2xl flex items-center justify-center text-xl shadow-sm overflow-hidden"
            >
              <img
                v-if="item.images && item.images.length > 0"
                :src="item.images[0]"
                class="w-full h-full object-cover"
              />
              <span v-else>🍽️</span>
            </div>

            <div class="flex-1 min-w-0">
              <h5 class="font-bold text-slate-900 truncate">{{ item.name }}</h5>
              <p class="text-xs font-black text-orange-500">{{ formatCurrency(item.price) }}</p>
            </div>

            <div
              class="flex items-center gap-3 bg-white p-1.5 rounded-2xl shadow-sm border border-slate-100"
            >
              <button
                @click="saleStore.removeFromCart(item.id)"
                class="w-7 h-7 flex items-center justify-center rounded-xl hover:bg-slate-100 text-slate-400 transition-colors"
              >
                <Minus class="w-4 h-4" />
              </button>
              <span class="w-6 text-center font-black text-slate-900">{{ item.quantity }}</span>
              <button
                @click="saleStore.addToCart(item)"
                class="w-7 h-7 flex items-center justify-center rounded-xl hover:bg-slate-100 text-slate-400 transition-colors"
              >
                <Plus class="w-4 h-4" />
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Cart Footer -->
      <div class="p-8 bg-slate-50 border-t border-slate-100 space-y-6">
        <div class="space-y-3">
          <div class="flex justify-between items-center text-slate-500 font-bold text-sm">
            <span>Subtotal</span>
            <span>{{ formatCurrency(saleStore.cartTotal) }}</span>
          </div>
          <div class="flex justify-between items-center text-slate-500 font-bold text-sm">
            <span>Descuento</span>
            <span>- $0.00</span>
          </div>
          <div class="flex justify-between items-center pt-3 border-t border-slate-200">
            <span class="text-xl font-black text-slate-900 italic">TOTAL</span>
            <span class="text-3xl font-black text-orange-600 tracking-tighter">{{
              formatCurrency(saleStore.cartTotal)
            }}</span>
          </div>
        </div>

        <button
          @click="openCheckout"
          :disabled="saleStore.cart.length === 0"
          class="w-full py-5 bg-orange-500 hover:bg-orange-600 disabled:bg-slate-300 disabled:cursor-not-allowed text-white rounded-[2rem] font-black text-lg shadow-xl shadow-orange-500/30 transition-all hover:scale-[1.02] active:scale-95 flex items-center justify-center gap-3 group"
        >
          <span>Continuar</span>
          <ChevronRight class="w-6 h-6 group-hover:translate-x-1 transition-transform" />
        </button>
      </div>
    </div>

    <!-- MODAL DE PAGO (MULTI-PAGO) -->
    <Transition
      enter-active-class="transition duration-300 ease-out"
      enter-from-class="opacity-0 scale-95"
      enter-to-class="opacity-100 scale-100"
      leave-active-class="transition duration-200 ease-in"
      leave-from-class="opacity-100 scale-100"
      leave-to-class="opacity-0 scale-95"
    >
      <div
        v-if="showCheckoutModal"
        class="fixed inset-0 z-[110] flex items-center justify-center p-6"
      >
        <div
          class="absolute inset-0 bg-slate-900/40 backdrop-blur-md"
          @click="showCheckoutModal = false"
        ></div>
        <div
          class="bg-white w-full max-w-xl rounded-[2.5rem] shadow-2xl relative z-10 overflow-hidden border border-slate-100 flex flex-col max-h-[90vh]"
        >
          <!-- Header -->
          <div class="p-8 border-b border-slate-50 flex items-center justify-between">
            <div>
              <h3 class="text-2xl font-black text-slate-900 tracking-tight italic uppercase">
                Finalizar Venta
              </h3>
              <p class="text-slate-400 font-medium">Soporte multi-pago disponible</p>
            </div>
            <button
              @click="showCheckoutModal = false"
              class="w-12 h-12 bg-slate-50 rounded-2xl flex items-center justify-center text-slate-400 hover:text-slate-600 transition-colors"
            >
              <X class="w-6 h-6" />
            </button>
          </div>

          <!-- Content -->
          <div class="flex-1 overflow-y-auto p-8 space-y-8">
            <!-- Order Type Toggle -->
            <div class="bg-slate-100 p-2 rounded-[2rem] flex items-center gap-2">
              <button
                @click="toggleOT(false)"
                :class="[
                  'flex-1 flex items-center justify-center gap-2 py-3 rounded-[1.5rem] font-black text-xs uppercase tracking-widest transition-all',
                  !isOpenOrder ? 'bg-white text-orange-600 shadow-sm' : 'text-slate-500',
                ]"
              >
                <Check class="w-4 h-4" /> Finalizar y Cobrar
              </button>
              <button
                @click="toggleOT(true)"
                :class="[
                  'flex-1 flex items-center justify-center gap-2 py-3 rounded-[1.5rem] font-black text-xs uppercase tracking-widest transition-all',
                  isOpenOrder ? 'bg-slate-900 text-white shadow-lg' : 'text-slate-500',
                ]"
              >
                <Clock class="w-4 h-4" /> Dejar Orden Abierta
              </button>
            </div>

            <!-- Customer Name -->
            <div class="space-y-3">
              <label
                class="block text-[10px] font-black text-slate-400 uppercase tracking-widest ml-1"
                >Cliente</label
              >
              <div class="relative group">
                <UserIcon
                  class="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 group-focus-within:text-orange-500 transition-colors"
                />
                <input
                  v-model="customerName"
                  type="text"
                  placeholder="Ej: Mesa 4 / Juan Pérez"
                  class="w-full pl-12 pr-4 py-4 bg-slate-50 border border-slate-100 rounded-2xl focus:outline-none focus:ring-4 focus:ring-orange-500/10 focus:border-orange-500 transition-all font-bold text-slate-700"
                />
              </div>
            </div>

            <!-- Summary Balance Card -->
            <div
              class="bg-slate-900 rounded-[2rem] p-8 text-white relative overflow-hidden shadow-2xl"
            >
              <div
                class="absolute -right-6 -bottom-6 w-32 h-32 bg-orange-500/20 rounded-full blur-3xl"
              ></div>
              <div class="relative z-10 grid grid-cols-2 gap-8">
                <div>
                  <p class="text-slate-400 font-black uppercase tracking-widest text-[9px] mb-1">
                    Total Orden
                  </p>
                  <h4 class="text-2xl font-black tracking-tight italic">
                    {{ formatCurrency(saleStore.cartTotal) }}
                  </h4>
                </div>
                <div class="text-right border-l border-white/10 pl-8">
                  <p
                    class="text-orange-400 font-black uppercase tracking-widest text-[9px] mb-1 italic"
                  >
                    Pendiente
                  </p>
                  <h4 class="text-3xl font-black tracking-tighter text-orange-500">
                    {{ formatCurrency(remainingBalance) }}
                  </h4>
                </div>
              </div>
            </div>

            <!-- Add Payment Form (Solo si NO es orden abierta) -->
            <div
              v-if="!isOpenOrder && remainingBalance > 0.01"
              class="bg-slate-50 p-6 rounded-[2rem] border border-slate-100 space-y-5 animate-in fade-in duration-300"
            >
              <div class="flex justify-between items-center px-1">
                <span class="text-[10px] font-black text-slate-400 uppercase tracking-widest"
                  >Registrar Pago</span
                >
                <span class="text-xs font-black text-orange-600"
                  >Monto: +{{ formatCurrency(amountToAdd) }}</span
                >
              </div>

              <div class="grid grid-cols-3 gap-2">
                <button
                  v-for="m in paymentMethods"
                  :key="m.id"
                  @click="selectedMethod = m.id"
                  :class="[
                    'flex flex-col items-center gap-1.5 p-3 rounded-2xl border transition-all',
                    selectedMethod === m.id
                      ? 'bg-orange-500 text-white border-orange-500 shadow-lg shadow-orange-500/20'
                      : 'bg-white border-slate-200 text-slate-500 hover:border-orange-200',
                  ]"
                >
                  <component :is="m.icon" class="w-5 h-5" />
                  <span class="text-[9px] font-black uppercase tracking-tighter">{{ m.id }}</span>
                </button>
              </div>

              <div class="flex gap-2">
                <div class="relative flex-1">
                  <span
                    class="absolute left-4 top-1/2 -translate-y-1/2 font-black text-slate-400 text-sm"
                    >$</span
                  >
                  <input
                    v-model.number="amountToAdd"
                    type="number"
                    class="w-full pl-8 pr-4 py-4 bg-white border border-slate-200 rounded-2xl font-black text-slate-700 focus:ring-4 focus:ring-orange-500/10 focus:border-orange-500"
                  />
                </div>
                <button
                  @click="addPayment"
                  class="px-8 bg-slate-900 text-white font-black rounded-2xl hover:bg-orange-500 transition-all flex items-center justify-center shadow-lg active:scale-90"
                >
                  <Plus class="w-6 h-6" />
                </button>
              </div>
            </div>

            <!-- Payments List (Solo si NO es orden abierta) -->
            <div v-if="!isOpenOrder && payments.length > 0" class="space-y-4">
              <label
                class="block text-[10px] font-black text-slate-400 uppercase tracking-widest ml-1 italic"
                >Pagos Aplicados</label
              >
              <div class="space-y-2">
                <div
                  v-for="(p, idx) in payments"
                  :key="idx"
                  class="flex items-center justify-between p-5 bg-white border border-slate-100 rounded-2xl shadow-sm hover:border-orange-100 transition-colors animate-in slide-in-from-left-4 duration-300"
                >
                  <div class="flex items-center gap-4">
                    <div
                      class="w-10 h-10 rounded-xl bg-slate-50 flex items-center justify-center text-slate-400"
                    >
                      <Banknote v-if="p.method === 'Efectivo'" class="w-5 h-5 text-emerald-500" />
                      <CreditCard
                        v-else-if="p.method === 'Tarjeta'"
                        class="w-5 h-5 text-blue-500"
                      />
                      <Smartphone v-else class="w-5 h-5 text-purple-500" />
                    </div>
                    <div>
                      <span
                        class="text-xs font-black text-slate-400 uppercase tracking-tighter block leading-none mb-1"
                        >Cubre por</span
                      >
                      <span class="font-black text-slate-800">{{ p.method }}</span>
                    </div>
                  </div>
                  <div class="flex items-center gap-6">
                    <span class="text-lg font-black text-slate-900 italic tracking-tighter">{{
                      formatCurrency(p.amount)
                    }}</span>
                    <button
                      @click="removePayment(idx)"
                      class="w-8 h-8 rounded-lg hover:bg-red-50 text-red-300 hover:text-red-500 transition-colors flex items-center justify-center"
                    >
                      <X class="w-5 h-5" />
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Action Footer -->
          <div class="p-8 bg-slate-50 border-t border-slate-100 space-y-3">
            <button
              @click="handleCheckout"
              :disabled="isProcessing"
              :class="[
                'w-full py-5 rounded-[2rem] font-black text-lg shadow-xl transition-all hover:scale-[1.02] active:scale-95 flex items-center justify-center gap-3',
                remainingBalance > 0.01
                  ? 'bg-slate-900 hover:bg-slate-800 text-white shadow-slate-900/20'
                  : 'bg-orange-500 hover:bg-orange-600 text-white shadow-orange-500/20',
              ]"
            >
              <Loader2 v-if="isProcessing" class="w-6 h-6 animate-spin" />
              <div v-else class="flex items-center gap-2">
                <Check v-if="remainingBalance <= 0.01" class="w-6 h-6" />
                <Clock v-else class="w-6 h-6" />
                <span>{{
                  remainingBalance > 0.01
                    ? totalPaid > 0
                      ? 'Guardar Orden Abierta'
                      : 'Guardar Solo Orden'
                    : 'Confirmar y Finalizar Venta'
                }}</span>
              </div>
            </button>
            <p
              v-if="remainingBalance > 0.01"
              class="text-center text-[10px] font-bold text-slate-500 uppercase tracking-widest italic"
            >
              {{
                totalPaid > 0
                  ? `Se cobrarán ${formatCurrency(totalPaid)} y restarán ${formatCurrency(remainingBalance)}`
                  : `Se guardará la orden por ${formatCurrency(remainingBalance)} sin pago previo`
              }}
            </p>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<style scoped>
.scrollbar-hide::-webkit-scrollbar {
  display: none;
}
.scrollbar-hide {
  -ms-overflow-style: none;
  scrollbar-width: none;
}
</style>
