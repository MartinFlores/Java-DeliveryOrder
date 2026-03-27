import { fetchInitialConfig } from '@/utils/axios'

export interface ApiConfig {
  api: {
    baseUrl: string;
    version: string;
  };
  apps: {
    superAdmin: string;
    cocina: string;
    cajero: string;
    mesero: string;
    menu: string;
  };
}

export async function fetchConfig(): Promise<ApiConfig> {
  return await fetchInitialConfig()
}
