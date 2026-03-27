import { defineStore } from 'pinia'
import { api } from '@/utils/axios'
import { useUserStore } from './userStore'
import { useShiftStore } from './shiftStore'

export interface InventoryItem {
  id: number
  name: string
  unit: string
  min_stock_alert: number
  stock: number
  created_at: number
}

export interface RecipeItem {
  id?: number
  product_id?: number
  inventory_item_id: number
  inventory_item_name?: string
  quantity_required: number
  unit?: string
  available_stock?: number
}

export interface Product {
  id: number
  name: string
  description: string
  price: number
  purchase_price?: number
  stock?: number // Calculated
  images: string[]
  categories?: Record<string, string>
  category_ids: number[]
  status: 'active' | 'hidden'
  type: 'simple' | 'composed'
  inventory_item_id?: number | null
  recipe?: RecipeItem[]
  created_at?: number
}

export interface Category {
  id: number
  name: string
  icon: string
}

export interface CartItem extends Product {
  quantity: number
}

export const useSaleStore = defineStore('sale', {
  state: () => ({
    products: [] as Product[],
    categories: [] as Category[],
    inventoryItems: [] as InventoryItem[],
    cart: [] as CartItem[],
    isLoading: false,
    selectedCategoryId: null as number | null,
    searchQuery: '',
    editProduct: null as Product | null,
  }),

  getters: {
    filteredProducts: (state) => {
      return state.products.filter((p) => {
        const matchesCategory = state.selectedCategoryId
          ? p.category_ids?.includes(state.selectedCategoryId)
          : true
        const matchesSearch = p.name.toLowerCase().includes(state.searchQuery.toLowerCase())
        return matchesCategory && matchesSearch
      })
    },
    cartTotal: (state) => {
      return state.cart.reduce((total, item) => total + item.price * item.quantity, 0)
    },
    cartCount: (state) => {
      return state.cart.reduce((count, item) => count + item.quantity, 0)
    },
  },

  actions: {
    async fetchInitialData() {
      this.isLoading = true
      try {
        const [prodRes, catRes, invRes] = await Promise.all([
          api.get('products/all'),
          api.get('products/categories'),
          api.get('products/inventory/items'),
        ])

        if (prodRes.data.status === 'ok') this.products = prodRes.data.data
        if (catRes.data.status === 'ok') this.categories = catRes.data.data
        if (invRes.data.status === 'ok') this.inventoryItems = invRes.data.data
      } catch (error) {
        console.error('Error fetching sale data:', error)
      } finally {
        this.isLoading = false
      }
    },

    async fetchInventoryItems() {
      try {
        const response = await api.get('products/inventory/items')
        if (response.data.status === 'ok') {
          this.inventoryItems = response.data.data
        }
      } catch (error) {
        console.error('Error fetching inventory items:', error)
      }
    },

    addToCart(product: Product) {
      const existing = this.cart.find((item) => item.id === product.id)
      if (existing) {
        existing.quantity++
      } else {
        this.cart.push({ ...product, quantity: 1 })
      }
    },

    removeFromCart(productId: number) {
      const item = this.cart.find((item) => item.id === productId)
      if (item) {
        if (item.quantity > 1) {
          item.quantity--
        } else {
          const index = this.cart.indexOf(item)
          if (index > -1) this.cart.splice(index, 1)
        }
      }
    },

    async placeOrder(customerName: string, payments: { payment_method: string; amount: number }[]) {
      const userStore = useUserStore()
      const shiftStore = useShiftStore()

      if (!userStore.user || !shiftStore.currentShift) {
        return { success: false, message: 'Sesión o turno no válido' }
      }

      const orderData = {
        user_id: userStore.user.id,
        shift_id: shiftStore.currentShift.id,
        customer_name: customerName,
        total: this.cartTotal,
        payment_method:
          payments.length > 0
            ? payments.length > 1
              ? 'Dividido'
              : payments[0]?.payment_method || 'Efectivo'
            : 'Pendiente',
        payments: payments,
        items: this.cart.map((item) => ({
          product_id: item.id,
          quantity: item.quantity,
          price: item.price,
          subtotal: item.price * item.quantity,
        })),
      }

      try {
        const response = await api.post('orders/create', orderData)
        if (response.data.status === 'ok') {
          this.clearCart()
          await this.fetchInitialData() // Update stock
          return { success: true, message: 'Venta realizada con éxito' }
        } else {
          return { success: false, message: response.data.message }
        }
      } catch (error: any) {
        return {
          success: false,
          message: error.response?.data?.message || error.message || 'Error al procesar la venta',
        }
      }
    },

    async createProduct(product: Partial<Product>) {
      try {
        const response = await api.post('products/create', product)
        if (response.data.status === 'ok') {
          await this.fetchInitialData()
          return { success: true }
        }
        return { success: false, message: response.data.message }
      } catch (error: any) {
        return { success: false, message: error.message }
      }
    },

    async updateProduct(product: Product) {
      try {
        const response = await api.post('products/update', product)
        if (response.data.status === 'ok') {
          await this.fetchInitialData()
          return { success: true }
        }
        return { success: false, message: response.data.message }
      } catch (error: any) {
        return { success: false, message: error.message }
      }
    },

    async createInventoryItem(item: Partial<InventoryItem>) {
      try {
        const response = await api.post('products/inventory/create', item)
        if (response.data.status === 'ok') {
          await this.fetchInventoryItems()
          return { success: true, id: response.data.id }
        }
        return { success: false, message: response.data.message }
      } catch (error: any) {
        return { success: false, message: error.message }
      }
    },

    async deleteProduct(id: number) {
      try {
        const response = await api.post('products/delete', { id })
        if (response.data.status === 'ok') {
          await this.fetchInitialData()
          return { success: true }
        }
        return { success: false, message: response.data.message }
      } catch (error: any) {
        return { success: false, message: error.message }
      }
    },

    async createCategory(category: Partial<Category>) {
      try {
        const response = await api.post('products/categories/create', category)
        if (response.data.status === 'ok') {
          await this.fetchInitialData()
          return { success: true }
        }
        return { success: false, message: response.data.message }
      } catch (error: any) {
        return { success: false, message: error.message }
      }
    },

    async updateCategory(category: Category) {
      try {
        const response = await api.post('products/categories/update', category)
        if (response.data.status === 'ok') {
          await this.fetchInitialData()
          return { success: true }
        }
        return { success: false, message: response.data.message }
      } catch (error: any) {
        return { success: false, message: error.message }
      }
    },

    async deleteCategory(id: number) {
      try {
        const response = await api.post('products/categories/delete', { id })
        if (response.data.status === 'ok') {
          await this.fetchInitialData()
          return { success: true }
        }
        return { success: false, message: response.data.message }
      } catch (error: any) {
        return { success: false, message: error.message }
      }
    },

    async reorderCategories(ids: number[]) {
      try {
        const response = await api.post('products/categories/reorder', { reorder: ids })
        if (response.data.status === 'ok') {
          return { success: true }
        }
        return { success: false, message: response.data.message }
      } catch (error: any) {
        return { success: false, message: error.message }
      }
    },

    clearCart() {
      this.cart = []
    },
  },
})
