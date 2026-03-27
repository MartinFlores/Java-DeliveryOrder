import { defineStore } from 'pinia'
import { api } from '@/utils/axios'

export interface Shift {
  id: number
  opened_at: number
  opening_amount: number
  user_id: number
  status: string
}

export const useShiftStore = defineStore('shift', {
  state: () => ({
    currentShift: null as Shift | null,
    isLoading: false,
  }),
  getters: {
    isShiftOpen: (state) => !!state.currentShift && state.currentShift.status === 'Abierto',
  },
  actions: {
    async openShift(amount: number, userId: number) {
      this.isLoading = true
      try {
        const { data } = await api.post('shifts/open', {
          user_id: userId,
          opening_amount: amount,
        })

        if (data.status === 'ok') {
          this.currentShift = data.data
          localStorage.setItem('current_shift', JSON.stringify(this.currentShift))
          return { success: true }
        } else {
          return { success: false, message: data.message }
        }
      } catch (error) {
        console.error('Error opening shift:', error)
        return { success: false, message: 'Error de conexión' }
      } finally {
        this.isLoading = false
      }
    },
    async checkActiveShift(userId: number) {
      try {
        const { data } = await api.get(`shifts/active?user_id=${userId}`)
        if (data.status === 'ok') {
          this.currentShift = data.data
          localStorage.setItem('current_shift', JSON.stringify(this.currentShift))
        } else {
          this.currentShift = null
          localStorage.removeItem('current_shift')
        }
      } catch (error) {
        console.error('Error checking active shift:', error)
      }
    },
    async closeShiftApi(closingAmount: number) {
      if (!this.currentShift) return { success: false, message: 'No hay turno activo' }
      this.isLoading = true
      try {
        const { data } = await api.post('shifts/close', {
          id: this.currentShift.id,
          closing_amount: closingAmount,
        })

        if (data.status === 'ok') {
          this.closeShift()
          return { success: true }
        } else {
          return { success: false, message: data.message }
        }
      } catch (error) {
        console.error('Error closing shift:', error)
        return { success: false, message: 'Error de conexión' }
      } finally {
        this.isLoading = false
      }
    },
    closeShift() {
      this.currentShift = null
      localStorage.removeItem('current_shift')
    },
    loadShift() {
      const saved = localStorage.getItem('current_shift')
      if (saved) {
        try {
          this.currentShift = JSON.parse(saved)
        } catch (e) {
          localStorage.removeItem('current_shift')
        }
      }
    },
    async fetchStats() {
      if (!this.currentShift) return null
      try {
        const { data } = await api.get(`shifts/stats?shift_id=${this.currentShift.id}`)
        if (data.status === 'ok') {
          return data.data
        }
      } catch (error) {
        console.error('Error fetching shift stats:', error)
      }
      return null
    },
  },
})
