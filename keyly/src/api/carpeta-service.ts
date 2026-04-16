import type { Carpeta } from "~models/Carpeta";

function sleep(ms: number) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

export class carpetasApi {
  static async fetchItems(): Promise<Carpeta[]> {
  await sleep(1500);
  const token = localStorage.getItem("jwtToken");
  const response = await fetch("https://10.147.17.250:8081/api/carpeta/get/all", {
    headers: { "Authorization": `Bearer ${token}` }
  });
  if (!response.ok) throw new Error("Error en la petición");
  return response.json();
}

}