import type { User } from "~models/User"

const API_BASE = "https://10.147.17.250:8081/api"

export class userApi {
  static async fetchUsers(): Promise<User[]> {
    const token = localStorage.getItem("jwtToken")
    const response = await fetch(API_BASE + "/usuari/all", {
      headers: { Authorization: `Bearer ${token}` }
    })
    console.log("Hola")
    if (!response.ok) throw new Error("Error en la petició")
    const users = await response.json()
    console.log("Usuarios " + JSON.stringify(users))
    return users
  }
}
