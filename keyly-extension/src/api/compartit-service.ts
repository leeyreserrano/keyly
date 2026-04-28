import { TipusEntitat, type Compartit } from "~models/Compartit"

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
