import { reactive } from 'vue'

interface Toast {
  id: number
  message: string
  type: 'success' | 'error' | 'warning' | 'info'
}

export const toastState = reactive({
  toasts: [] as Toast[],
})

let toastId = 0

export const Toast = {
  show(message: string, type: Toast['type'] = 'info', duration = 3000) {
    const id = ++toastId
    toastState.toasts.push({ id, message, type })

    setTimeout(() => {
      this.remove(id)
    }, duration)
  },

  success(message: string) {
    this.show(message, 'success')
  },
  error(message: string) {
    this.show(message, 'error')
  },
  warning(message: string) {
    this.show(message, 'warning')
  },

  remove(id: number) {
    const index = toastState.toasts.findIndex((t) => t.id === id)
    if (index > -1) {
      toastState.toasts.splice(index, 1)
    }
  },
}
