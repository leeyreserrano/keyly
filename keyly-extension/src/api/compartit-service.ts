import { Permisos, TipusEntitat, type Compartit } from "~models/Compartit"
import type { Item } from "~models/Item"
import type { User } from "~models/User"

import {
  type CompartitItemRequest,
  type CompartitRequest
} from "./../models/Compartit"

const API_BASE = "https://10.147.17.250:8081/api"

export class compartitApi {
  static async fetchCompartits(): Promise<Compartit[]> {
    const token = localStorage.getItem("jwtToken")
    const response = await fetch(API_BASE + "/compartit/get/all", {
      headers: { Authorization: `Bearer ${token}` }
    })
    if (!response.ok) throw new Error("Error en la petició")
    return response.json()
  }

  static async newItemCompartir(
    compartit: string[],
    item: Item
  ): Promise<void> {
    const compartitItemRequest: CompartitItemRequest = {
      itemRequest: {
        uuid: "",
        titol: item.titol,
        nomUsuari: item.nomUsuari,
        contrasenya: item.contrasenya,
        iv: item.iv,
        url: item.url,
        notes: item.notes,
        favorit: item.favorit,
        dataCreacio: "",
        dataEditat: "",
        ultimAcces: "",
        dinsCarpeta: false
      },
      compartitRequest: {
        entitatUuid: "",
        tipusEntitat: TipusEntitat.ITEM,
        usuaris: Object.fromEntries(
          compartit.map((c) => [c, Permisos.ESCRIPTURA])
        )
      }
    }

    const token = localStorage.getItem("jwtToken")
    const response = await fetch(API_BASE + "/compartit/add/item", {
      method: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json"
      },

      body: JSON.stringify(compartitItemRequest)
    })
    if (!response.ok) throw new Error("Error en la petició")
    return response.json()
  }

  static async addCompartit(compartit: CompartitRequest): Promise<void> {
    console.log("Compartido: " + compartit)

    const token = localStorage.getItem("jwtToken")
    const response = await fetch(API_BASE + "/compartit/add", {
      method: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json"
      },

      body: JSON.stringify(compartit)
    })
    if (!response.ok) throw new Error("Error en la petició")
    return response.json()
  }

  static async updateCompartit(compartit: Compartit): Promise<void> {
    const token = localStorage.getItem("jwtToken")

    if (compartit.tipusEntitat === TipusEntitat.CARPETA) {
      const response = await fetch(
        API_BASE + `/carpeta/update/${compartit.carpeta.uuid}`,
        {
          method: "PUT",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json"
          },
          body: JSON.stringify(compartit.carpeta)
        }
      )
      if (!response.ok) throw new Error("Error en la petició")
      return response.json()
    } else {
      const response = await fetch(
        API_BASE + `/item/update/${compartit.item.uuid}`,
        {
          method: "PUT",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json"
          },
          body: JSON.stringify(compartit.item)
        }
      )
      if (!response.ok) throw new Error("Error en la petició")
      return response.json()
    }
  }

  static async deleteCompartit(uuid: string): Promise<void> {
    const token = localStorage.getItem("jwtToken")
    const response = await fetch(API_BASE + `/compartit/delete/${uuid}`, {
      method: "DELETE",
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
    if (!response.ok) throw new Error("Error eliminant el compartit")
  }
}
