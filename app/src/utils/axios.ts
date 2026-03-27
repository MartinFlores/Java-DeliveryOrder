import axios from 'axios'
import { wait } from '@/utils/Loader'

const addRandomDelay = (instance: any) => {
  instance.interceptors.request.use(async (config: any) => {
    const delay = Math.floor(Math.random() * (900 - 500 + 1)) + 500
    await wait(delay)
    return config
  })
}

const initialApi = axios.create({
  baseURL: '/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
})
addRandomDelay(initialApi)

export const api = axios.create({
  headers: {
    'Content-Type': 'application/json',
  },
})
addRandomDelay(api)

export function updateApiBaseUrl(baseUrl: string) {
  api.defaults.baseURL = baseUrl + '/api/v1'
}

export function getApiBaseUrl() {
  return api.defaults.baseURL
}

export async function fetchInitialConfig() {
  try {
    const { data } = await initialApi.get('/config')
    return data
  } catch (error: any) {
    alert('ERROR: ' + (error.message || error))
    throw error
  }
}
