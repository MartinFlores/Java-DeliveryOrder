import { reactive } from "vue";

export type TerminalType =
  | "admin"
  | "caja"
  | "cocina"
  | "cocina_fria"
  | "cocina_caliente"
  | "mesero"
  | "bebidas";

export const setupState = reactive({
  step: 0,
  lang: "",
  businessName: "",
  pin: "",
  terminalType: "caja" as TerminalType,
  status: "",
  loading: false,
});
