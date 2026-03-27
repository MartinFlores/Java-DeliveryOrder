import { defineStore } from 'pinia'
import { markRaw, type Component } from 'vue'
import SetupWizard from '@/views/Wizard/SetupWizard.vue'
import Dashboard from '@/views/Admin/Dashboard/Dashboard.vue'

export const useRouterStore = defineStore('router', {
  state: () => ({
    currentRoute: '/setup' as string,
    routes: markRaw({
      '/setup': SetupWizard,
      '/admin/dashboard': Dashboard,
    } as Record<string, Component>),
  }),
  getters: {
    currentComponent: (state) => state.routes[state.currentRoute] || state.routes['/setup'],
  },
  actions: {
    navigate(path: string) {
      if (this.routes[path]) {
        this.currentRoute = path
      }
    },
  },
})
