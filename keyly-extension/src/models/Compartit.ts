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

type CompartitItem = {
  uuid: string
  titol: string
  nomUsuari: string
  contrasenya: string
  iv: string
  encryptedDataKey: {
    uuid: string
    encryptedDatakey: string
  }
  url: string
  notes: string
  favorit: boolean
  dinsDeCarpeta: boolean
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
