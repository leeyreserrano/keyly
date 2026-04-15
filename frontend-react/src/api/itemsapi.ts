import { apiRequest } from './client';

export class itemsApi {
  static fetchItems(): Promise<Item[] | null> {
    return apiRequest<Item[]>('/item/get/all');
  }

  static addItem(data: Partial<Item>): Promise<Item | null> {
    return apiRequest<Item>('/item/add', {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  static updateItem(uuid: string, data: Partial<Item>): Promise<Item | null> {
    return apiRequest<Item>(`/item/update/${uuid}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    });
  }

  static deleteItem(uuid: string): Promise<void | null> {
    return apiRequest<void>(`/item/delete/${uuid}`, { method: 'DELETE' });
  }

  static getItem(uuid: string): Promise<Item | null> {
    return apiRequest<Item>(`/item/get/${uuid}`);
  }
}

export type Item = {
  uuid: string;
  titol: string;
  nomUsuari: string;
  contrasenya: string;
  url: string;
  notes?: string;
  dataCreacio: string;
  dataEditat: string;
  ultimAcces: string;
  dinsCarpeta: boolean;
  favorit: boolean;
};
