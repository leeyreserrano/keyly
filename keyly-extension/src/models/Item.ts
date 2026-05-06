export type Item = {
  uuid: string
  titol: string
  nomUsuari: string
  contrasenya: string
  iv?: string
  encryptedDataKey: string
  url: string
  notes: string
  favorit: boolean
  dataCreacio: string
  dataEditat: string
  dinsDeCarpeta: boolean
  ultimAccess: string
}

export type ItemResponse = {
  uuid: string
}
