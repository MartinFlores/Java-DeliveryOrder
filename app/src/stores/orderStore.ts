import { defineStore } from 'pinia'
import { api } from '@/utils/axios'

export interface Order {
  id: number
  user_id: number
  shift_id: number
  customer_name: string
  total: number
  payment_method: string
  status: string
  created_at: number
  paid_amount?: number
}

export interface OrderDetail extends Order {
  items: any[]
  payments: any[]
}

export const useOrderStore = defineStore('order', {
  state: () => ({
    orders: [] as Order[],
    openOrders: [] as Order[],
    isLoading: false,
    selectedOrder: null as OrderDetail | null,
  }),

  getters: {
    openOrdersCount: (state) => state.openOrders.length,
  },

  actions: {
    async fetchOrdersByShift(shiftId: number) {
      this.isLoading = true
      try {
        const response = await api.get(`orders/shift?shift_id=${shiftId}`)
        if (response.data.status === 'ok') {
          this.orders = response.data.data
        }
      } catch (error) {
        console.error('Error fetching orders:', error)
      } finally {
        this.isLoading = false
      }
    },

    async fetchOpenOrders(shiftId: number) {
      try {
        const response = await api.get(`orders/open?shift_id=${shiftId}`)
        if (response.data.status === 'ok') {
          this.openOrders = response.data.data
        }
      } catch (error) {
        console.error('Error fetching open orders:', error)
      }
    },

    async fetchOrderDetails(orderId: number) {
      try {
        const response = await api.get(`orders/details?order_id=${orderId}`)
        if (response.data.status === 'ok') {
          // Si el API devuelve la orden completa, úsala.
          // Si no, búscala en las listas pero asegúrate de tener los campos actualizados.
          let order =
            response.data.order ||
            this.orders.find((o) => o.id === orderId) ||
            this.openOrders.find((o) => o.id === orderId)

          if (order) {
            this.selectedOrder = {
              ...order,
              items: response.data.items,
              payments: response.data.payments,
            }
          }
        }
      } catch (error) {
        console.error('Error fetching order details:', error)
      }
    },

    async addPayment(
      orderId: number,
      method: string | null,
      amount: number | null,
      payments: { payment_method: string; amount: number }[] = [],
    ) {
      try {
        const payload: any = { order_id: orderId }
        if (payments.length > 0) {
          payload.payments = payments
        } else {
          payload.payment_method = method
          payload.amount = amount
        }

        const response = await api.post('orders/add-payment', payload)
        return response.data
      } catch (error: any) {
        return { status: 'error', message: error.message }
      }
    },
  },
})
