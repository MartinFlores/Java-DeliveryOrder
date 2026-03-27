import { reactive } from 'vue'

interface LoaderState {
  visible: boolean
  message: string
}

export const loaderState = reactive<LoaderState>({
  visible: false,
  message: '',
})

export const Loader = {
  show(message: string = 'Cargando...') {
    loaderState.message = message
    loaderState.visible = true
  },
  hide() {
    loaderState.visible = false
  },
}

export const wait = (ms: number) => new Promise((resolve) => setTimeout(resolve, ms))
