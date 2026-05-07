import type { Item } from "./Item"

export type Compartit = {
  uuid: string
  usuariCreador: {
    uuid: string
    nom: string
    correu: string
  },
    usuariReceptor: {
        uuid: string
        nom: string
        correu: string
  }
  tipusEntitat: TipusEntitat
  permisos: Permisos
  carpeta?: CompartitCarpeta
  item?: Item
  dataCreacio: string
}

type CompartitCarpeta = {
  uuid: string
  nom: string
  favorit: boolean
  items: Item[]
}
export type CompartitRequest = {
  entitatUuid: string
  tipusEntitat: TipusEntitat
  usuaris: {
    usuariUuid: string
    permis: Permisos
    encryptedDataKey?: string
  }[]
}

export type CompartitItemRequest = {
  itemRequest: Item
  compartitRequest: CompartitRequest
}

export enum TipusEntitat {
  CARPETA = "CARPETA",
  ITEM = "ITEM"
}

export enum Permisos {
  LECTURA = "LECTURA",
  ESCRIPTURA = "ESCRIPTURA",
  ADMINISTRADOR = "ADMINISTRADOR"
}
