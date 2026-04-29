import type { Carpeta } from "~models/Carpeta"
import type { Item, ItemResponse } from "~models/Item"

const API_BASE = "https://10.147.17.250:8081/api"

export class carpetasApi {
  static async fetchCarpetas(): Promise<Carpeta[]> {
    const token = localStorage.getItem("jwtToken")
    const response = await fetch(API_BASE + "/carpeta/get/all", {
      headers: { Authorization: `Bearer ${token}` }
    })
    if (!response.ok) throw new Error("Error en la petición")
    return response.json()
  }

  static async createNewItemInCarpeta(
    carpetaUuid: string,
    item: Item
  ): Promise<ItemResponse> {
    console.log("Aquí también entra")
    const token = localStorage.getItem("jwtToken")
    const response = await fetch(
      API_BASE + `/carpeta/add/${carpetaUuid}/item`,
      {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json"
        },
        body: JSON.stringify(item)
      }
    )

    console.log(carpetaUuid)
    console.log(JSON.stringify(item))

    if (!response.ok) throw new Error("Error en la petició")

    const text = await response.text()
    console.log(text ? JSON.parse(text) : null);
    return text ? JSON.parse(text) : null
  }

  static async addItemInCarpeta(
    carpetaUuid: string,
    itemUuid: string
  ): Promise<Item> {
    const token = localStorage.getItem("jwtToken")
    const response = await fetch(
      API_BASE + `/carpeta/add/${carpetaUuid}/item/existing/${itemUuid}`,
      {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json"
        }
      }
    )

    if (!response.ok) throw new Error("Error en la petició")
    return response.json()
  }

  static async updateCarpeta(carpeta: Carpeta): Promise<void> {
    const token = localStorage.getItem("jwtToken")

    const response = await fetch(API_BASE + `/carpeta/update/${carpeta.uuid}`, {
      method: "PUT",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json"
      },
      body: JSON.stringify(carpeta)
    })

    if (!response.ok) throw new Error("Error en la petició")
    return response.json()
  }

  static async deleteCarpeta(uuid: string): Promise<void> {
    const token = localStorage.getItem("jwtToken")
    const response = await fetch(API_BASE + `/carpeta/delete/${uuid}`, {
      method: "DELETE",
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
    if (!response.ok) throw new Error("Error eliminant la carpeta")
  }
}
