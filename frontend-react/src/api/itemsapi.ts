function sleep(ms: number) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

export class itemsApi {
  static async fetchItems(limit: number): Promise<Item[]> {
    await sleep(1500);
    const response = await fetch(
      "https://jsonplaceholder.typicode.com/items?_limit=2" + limit,
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
};
