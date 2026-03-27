import { useConfigStore } from '@/stores/configStore'

export const getApiUrl = () => {
  const configStore = useConfigStore()
  return configStore.apiBaseUrl
}
