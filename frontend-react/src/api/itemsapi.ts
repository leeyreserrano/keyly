function sleep(ms: number) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

export class itemsApi {
  static async fetchItems(): Promise<Item[]> {
    await sleep(1500);
    const response = await fetch(
      "https://10.147.17.250:8081/api/items",
    );

    if (!response.ok) {
      throw new Error("Error en la petición");
    }

    return response.json();
  }
}

export type Item = {
  uuid: string;
  titol: string;
  nomUsuari: string;
  contrasenya: string;
  url: string;
  dataCreacio: string;
  dataEditat: string;
  ultimAcces: string;
  dinsCarpeta: boolean;
};
