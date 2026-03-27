/* eslint-disable @typescript-eslint/no-explicit-any */
import { defineStore } from 'pinia'
import type { ApiConfig } from '@/services/configService'
import { useRouterStore } from './routerStore'
import { api } from '@/utils/axios'
import { decrypt } from '@/utils/crypto'

export const useConfigStore = defineStore('config', {
  state: () => ({
    apiConfig: null as ApiConfig | null,
    isAppConfigured: false,
    businessDetails: {
      name: '',
      currency: 'USD',
      timezone: 'UTC',
    },
    superAdmin: {
      name: '',
      email: '',
    },
    isAdminLoggedIn: localStorage.getItem('admin_session') === 'true',
  }),
  getters: {
    apiBaseUrl: (state) =>
      state.apiConfig?.api.baseUrl || `${window.location.protocol}//${window.location.host}`,
  },
  actions: {
    setApiConfig(config: ApiConfig) {
      this.apiConfig = config
    },
    loadCompanyInfo() {
      const encrypted = localStorage.getItem('company_info')
      if (encrypted) {
        const data = decrypt(encrypted)
        if (data) {
          this.businessDetails.name = data.company_name || data.businessName
          if (data.username) {
            this.superAdmin.name = data.username
          }
        }
      }
    },
    completeSetup(data: any) {
      this.businessDetails = { ...data.business }
      this.superAdmin = { ...data.admin }
      this.isAppConfigured = true
      localStorage.setItem('pos_config_status', 'true')
      useRouterStore().navigate('/admin/dashboard')
    },
    async loadBusinessDetails() {
      try {
        const { data } = await api.get('/config/business-details')
        if (data.status === 'ok') {
          this.businessDetails.name = data.businessName
          this.superAdmin.name = data.username
        }
      } catch (error) {
        console.error('Error cargando detalles del negocio:', error)
      }
    },
    async checkConfigStatus() {
      const isConfigured = localStorage.getItem('pos_config_status') === 'true'
      const routerStore = useRouterStore()

      if (!isConfigured) {
        try {
          const { data } = await api.get('/config/status')

          if (data.configured) {
            localStorage.setItem('pos_config_status', 'true')
            this.isAppConfigured = true
            routerStore.navigate('/admin/dashboard')
          } else {
            routerStore.navigate('/setup')
          }
        } catch (error) {
          console.error('Error verificando configuración:', error)
          routerStore.navigate('/setup')
        }
      } else {
        this.isAppConfigured = true
        this.loadBusinessDetails()
      }
    },
    async adminLogin(pin: string) {
      try {
        const { data } = await api.post('/config/admin-login', { pin })
        if (data.status === 'ok') {
          this.isAdminLoggedIn = true
          localStorage.setItem('admin_session', 'true')
          return { success: true }
        } else {
          return { success: false, message: data.message }
        }
      } catch (error) {
        return { success: false, message: 'Error de conexión' }
      }
    },
    logoutAdmin() {
      this.isAdminLoggedIn = false
      localStorage.removeItem('admin_session')
      window.location.reload()
    },
  },
})
