import type { CustomPassword } from "~models/CustomPassword"

const API_BASE = "https://10.147.17.250:8081/api"

export class utilsService {
  static async customPassword(password: CustomPassword): Promise<string> {
    const token = localStorage.getItem("jwtToken")

    const response = await fetch(API_BASE + `/utils/custom/password`, {
      method: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json"
      },
      body: JSON.stringify(password)
    })

    if (!response.ok) throw new Error("Error en la petició")

    const data = await response.json()
    return data.contrasenya
  }
}
