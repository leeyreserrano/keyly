import type { Item } from "./itemsapi";

function sleep(ms: number) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

export class carpetasApi {
  static async fetchItems(): Promise<Carpeta[]> {
    const token = localStorage.getItem("jwtToken");
    const response = await fetch("https://10.147.17.250:8081/api/carpetes", {
      headers: { Authorization: `Bearer ${token}` },
    });
    if (!response.ok) throw new Error("Error en la petición");
    return response.json();
  }

  static async deleteCarpeta(uuid: string): Promise<void> {
    const token = localStorage.getItem("jwtToken");
    const response = await fetch(`https://10.147.17.250:8081/api/carpeta/${uuid}`, {
      method: "DELETE",
      headers: { Authorization: `Bearer ${token}` },
    });
    if (!response.ok) throw new Error("Error eliminando la carpeta");
  }

  static async fetchItemsFromCarpeta(uuid: string): Promise<Item[]> {
    const token = localStorage.getItem("jwtToken");
    const response = await fetch(`https://10.147.17.250:8081/api/carpeta/${uuid}/item`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    if (!response.ok) throw new Error("Error fetching items de la carpeta");
    return response.json();
  }

  static async addCarpeta(data: Partial<Carpeta>): Promise<Carpeta> {
    const token = localStorage.getItem('jwtToken');
    const res = await fetch('https://10.147.17.250:8081/api/carpeta', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify(data),
    });
    if (!res.ok) throw new Error('Error creating folder');
    return res.json();
  }

  static async updateCarpeta(uuid: string, data: Partial<Carpeta>): Promise<Carpeta> {
    const token = localStorage.getItem('jwtToken');
    const res = await fetch(`https://10.147.17.250:8081/api/carpeta/${uuid}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify(data),
    });
    if (!res.ok) throw new Error('Error updating folder');
    return res.json();
  }
}

export type Carpeta = {
  uuid: string;
  nom: string;
  dataCreacio: string;
  favorit: boolean;
  items: {
    uuid: string;
    titol: string;
    dinsDeCarpeta: boolean;
  }[];
};