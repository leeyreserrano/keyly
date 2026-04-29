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
  dinsCarpeta: boolean
}

export type ItemResponse = {
  items: {
    uuid: string
  }
}