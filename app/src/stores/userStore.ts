import { defineStore } from 'pinia'

export interface User {
  id: number
  name: string
  whatsapp: string
  role: string
  avatar?: string
}

export const useUserStore = defineStore('user', {
  state: () => ({
    user: null as User | null,
    isLoggedIn: false,
  }),
  actions: {
    setUser(userData: User) {
      this.user = userData
      this.isLoggedIn = true
      localStorage.setItem('cashier_user', JSON.stringify(userData))
    },
    logout() {
      this.user = null
      this.isLoggedIn = false
      localStorage.removeItem('cashier_user')
    },
    loadSession() {
      const savedUser = localStorage.getItem('cashier_user')
      if (savedUser) {
        try {
          this.user = JSON.parse(savedUser)
          this.isLoggedIn = true
        } catch (e) {
          console.error('Error parsing saved user', e)
          this.logout()
        }
      }
    },
  },
})
