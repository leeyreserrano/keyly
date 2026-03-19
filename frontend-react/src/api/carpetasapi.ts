function sleep(ms: number) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

export class carpetasApi {
  static async fetchItems(): Promise<Carpeta[]> {
    await sleep(1500);
    const response = await fetch(
      "https://10.147.17.250:8081/api/carpetes",
    );

    if (!response.ok) {
      throw new Error("Error en la petición");
    }

    return response.json();
  }
}

export type Carpeta = {
 uuid: string;
};
