import { api } from '@/utils/axios'

export interface Printer {
  name: string
  mac: string
  paperWidth?: number
  charset?: string
}

export const printerService = {
  async listPairedPrinters(): Promise<Printer[]> {
    const { data } = await api.get('/printers/paired')
    return data
  },

  async savePrinter(printer: Printer): Promise<void> {
    await api.post('/printers/save', printer)
  },

  async getSavedPrinter(): Promise<Printer | null> {
    const { data } = await api.get('/printers/saved')
    return data
  },

  async printTest(): Promise<void> {
    await api.post('/printers/test')
  }
}
