import type { Item, ItemResponse } from "~models/Item"

const API_BASE = "https://10.147.17.250:8081/api"

export class itemsApi {
  static async fetchItems(): Promise<ItemResponse[]> {
    const token = localStorage.getItem("jwtToken")
    const response = await fetch(API_BASE + "/item/get/all", {
      headers: { Authorization: `Bearer ${token}` }
    })
    if (!response.ok) throw new Error("Error en la petició")
    return response.json()
  }

  static async fetchItem(uuid: string): Promise<any> {
    const token = localStorage.getItem("jwtToken")
    const response = await fetch(API_BASE + `/item/get/${uuid}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    if (!response.ok) throw new Error("Error en la petició")
    return response.json()
  }

  static async saveItem(item: Item): Promise<void> {
    const token = localStorage.getItem("jwtToken")

    const response = await fetch(API_BASE + "/item/add", {
      method: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json"
      },
      body: JSON.stringify(item)
    })

    if (!response.ok) throw new Error("Error en la petició")
    return
  }

  static async updateItem(item: Item): Promise<void> {
    const token = localStorage.getItem("jwtToken")

    const response = await fetch(API_BASE + `/item/update/${item.uuid}`, {
      method: "PUT",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json"
      },
      body: JSON.stringify(item)
    })

    if (!response.ok) throw new Error("Error en la petició")
    return response.json()
  }

  static async deleteItem(uuid: string): Promise<void> {
    const token = localStorage.getItem("jwtToken")
    const response = await fetch(API_BASE + `/item/delete/${uuid}`, {
      method: "DELETE",
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
    if (!response.ok) throw new Error("Error eliminando el item")
  }

  static async accesItem(uuid: string): Promise<void> {
    const token = localStorage.getItem("jwtToken")
    const response = await fetch(API_BASE + `/item/access/${uuid}`, {
      method: "POST",
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
    if (!response.ok) throw new Error("Error accediento al item")
    return response.json()
  }
}
