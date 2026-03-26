function sleep(ms: number) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

export class itemsApi {
  static async fetchItems(): Promise<Item[]> {
    await sleep(1000);
    const token = localStorage.getItem('jwtToken');
    const res = await fetch('https://10.147.17.250:8081/api/items', {
      headers: { Authorization: `Bearer ${token}` },
    });
    if (!res.ok) throw new Error('Error fetching items');
    return res.json();
  }

  static async updateItem(uuid: string, data: Partial<Item>): Promise<Item> {
    const token = localStorage.getItem('jwtToken');
    const res = await fetch(`https://10.147.17.250:8081/api/item/${uuid}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify(data),
    });
    if (!res.ok) throw new Error('Error updating item');
    return res.json();
  }

  static async deleteItem(uuid: string): Promise<void> {
    const token = localStorage.getItem('jwtToken');
    const res = await fetch(`https://10.147.17.250:8081/api/item/${uuid}`, {
      method: 'DELETE',
      headers: { Authorization: `Bearer ${token}` },
    });
    if (!res.ok) throw new Error('Error deleting item');
  }
  
  static async addItem(data: Partial<Item>): Promise<Item> {
    const token = localStorage.getItem('jwtToken');
    const res = await fetch('https://10.147.17.250:8081/api/item', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify(data),
    });
    if (!res.ok) throw new Error('Error creating item');
    return res.json();
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
  favorit: boolean;
};
