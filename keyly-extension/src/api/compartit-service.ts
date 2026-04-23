import type { Compartit } from "~models/Compartit"

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
}
