import type { Item } from "~models/Item"

const API_BASE = "https://10.147.17.250:8081/api"

export class itemsApi {
  static async fetchItems(): Promise<Item[]> {
    const token = localStorage.getItem("jwtToken")
    const response = await fetch(API_BASE + "/item/get/all", {
      headers: { Authorization: `Bearer ${token}` }
    })
    if (!response.ok) throw new Error("Error en la petició")
    return response.json()
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
    const response = await fetch(API_BASE + `item/delete/${uuid}`, {
      method: "DELETE"
    })
    if (!response.ok) throw new Error("Error eliminando el item")
  }
}
