import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import './assets/main.css'
import './assets/fonts.css'
import './assets/icons.css'
import { fetchConfig } from './services/configService'
import { useConfigStore } from './stores/configStore'
import { updateApiBaseUrl } from './utils/axios'

const pinia = createPinia()
const app = createApp(App)

app.use(pinia)

fetchConfig()
  .then((config) => {
    const configStore = useConfigStore()
    configStore.setApiConfig(config)
    updateApiBaseUrl(config.api.baseUrl)
    configStore.checkConfigStatus()
    app.mount('#app')
  })
  .catch((error) => {
    app.mount('#app')
  })
