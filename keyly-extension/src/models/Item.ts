export type Item = {
  uuid: string
  titol: string
  nomUsuari: string
  contrasenya: string
  iv?: string
  url: string
  notes: string
  favorit: boolean
  dataCreacio: string
  dataEditat: string
  ultimAcces: string
  dinsDeCarpeta: boolean
}

export type ItemResponse = {
  uuid: string
}
