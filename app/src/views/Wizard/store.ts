import { defineStore } from 'pinia'
import type { CompanyDto } from '@/interfaces/CompanyDto'
import { api } from '@/utils/axios'
import { encrypt } from '@/utils/crypto'

interface WizardState {
  step: number
  businessName: string
  brandColor: string
  username: string
  pin: string
}

export const useWizardStore = defineStore('wizard', {
  state: (): WizardState => ({
    step: 1,
    businessName: '',
    brandColor: '#FF6B35',
    username: 'superadmin',
    pin: '',
  }),

  actions: {
    next() {
      if (this.step < 4) this.step++
    },

    prev() {
      if (this.step > 1) this.step--
    },

    reset() {
      this.$reset()
    },

    async createConfigCompany() {
      try {
        const companyDto: CompanyDto = {
          businessName: this.businessName,
          username: this.username,
          pin: this.pin,
          brandColor: this.brandColor,
        }

        const { data } = await api.post('/config/setup', companyDto)

        if (data.status === 'ok') {
          localStorage.setItem('pos_config_status', 'true')
          if (data.company) {
            localStorage.setItem('company_info', encrypt(data.company))
          }
          return true
        } else {
          alert('Error servidor: ' + data.error)
          console.error('Error:', data.error)
          return false
        }
      } catch (error: any) {
        alert('Error catch: ' + (error.message || error))
        console.error('Error al guardar configuración:', error)
        return false
      }
    },
  },
})
