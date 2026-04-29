import type { Item } from "./Item"

export type Compartit = {
  uuid: string
  usuariCreador: {
    uuid: string
    nom: string
  }
  tipusEntitat: TipusEntitat
  permisos: Permisos
  carpeta?: CompartitCarpeta
  item?: CompartitItem
}

export type CompartitRequest = {
  entitatUuid: string
  tipusEntitat: TipusEntitat
  usuaris: Record<string, Permisos>
}

export type CompartitItemRequest = {
  itemRequest: Item
  compartitRequest: CompartitRequest
}

type CompartitCarpeta = {
  uuid: string
  nom: string
  favorit: boolean
}

type CompartitItem = {
  uuid: string
  titol: string
  nomUsuari: string
  url: string
  favorit: boolean
  dinsDeCarpeta: boolean
}

export enum TipusEntitat {
  CARPETA = "CARPETA",
  ITEM = "ITEM"
}

export enum Permisos {
  LECTURA,
  ESCRIPTURA,
  ADMINISTRADOR
}
