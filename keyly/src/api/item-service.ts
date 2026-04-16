import type { Item } from "~models/Item"

const API_BASE = "https://10.147.17.250:8081/api"

function sleep(ms: number) {
  return new Promise((resolve) => setTimeout(resolve, ms))
}

export class itemsApi {
  static async fetchItems(): Promise<Item[]> {
    await sleep(1500)
    const token = localStorage.getItem("jwtToken")
    const response = await fetch(API_BASE + "/item/get/all", {
      headers: { Authorization: `Bearer ${token}` }
    })
    if (!response.ok) throw new Error("Error en la petición")
    return response.json()
  }

  static async deleteItem(uuid: string): Promise<void> {
    const response = await fetch(API_BASE + `item/delete/${uuid}`, {
      method: "DELETE"
    })
    if (!response.ok) throw new Error("Error eliminando el item")
  }
}
