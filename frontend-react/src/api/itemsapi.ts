import { apiRequest } from './client';

export class itemsApi {
  static fetchItems(): Promise<Item[] | null> {
    return apiRequest<Item[]>('/item/get/all');
  }

  static addItem(data: Partial<ItemPayload>): Promise<Item | null> {
    return apiRequest<Item>('/item/add', {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  static updateItem(uuid: string, data: Partial<ItemPayload>): Promise<Item | null> {
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

  static registrarAcces(uuid: string): Promise<Item | null> {
    return apiRequest<Item>(`/item/access/${uuid}`, { method: 'POST' });
  }
}

export type Item = {
  uuid: string;
  titol: string;
  nomUsuari: string;
  contrasenya: string;
  iv: string;
  url: string;
  notes?: string;
  dataCreacio: string;
  dataEditat: string;
  ultimAcces: string;
  comptadorAccess: number;
  dinsCarpeta: boolean;
  favorit: boolean;
};

export type ItemPayload = Omit<Item, 'uuid' | 'dataCreacio' | 'dataEditat' | 'ultimAcces' | 'comptadorAccess' | 'dinsCarpeta'>;