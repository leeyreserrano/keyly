import type { Carpeta } from "~models/Carpeta"

const API_BASE = "https://10.147.17.250:8081/api"

export class carpetasApi {
  static async fetchItems(): Promise<Carpeta[]> {
    const token = localStorage.getItem("jwtToken")
    const response = await fetch(API_BASE + "/carpeta/get/all", {
      headers: { Authorization: `Bearer ${token}` }
    })
    if (!response.ok) throw new Error("Error en la petición")
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
