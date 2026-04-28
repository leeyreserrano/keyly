import { apiRequest } from './client';
import type { Item } from './itemsapi';

export class carpetasApi {
  static fetchItems(): Promise<Carpeta[]> {
    return apiRequest<Carpeta[]>('/carpeta/get/all').then(result => result ?? []);
  }

  static addCarpeta(data: Partial<Carpeta>): Promise<Carpeta> {
    return apiRequest<Carpeta>('/carpeta/add', {
      method: 'POST',
      body: JSON.stringify(data),
    }).then(result => {
      if (!result) throw new Error('Failed to add carpeta');
      return result;
    });
  }

  static updateCarpeta(uuid: string, data: Partial<Carpeta>): Promise<Carpeta> {
    return apiRequest<Carpeta>(`/carpeta/update/${uuid}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    }).then(result => {
      if (!result) throw new Error('Failed to update carpeta');
      return result;
    });
  }

  static deleteCarpeta(uuid: string): Promise<void> {
    return apiRequest<void>(`/carpeta/delete/${uuid}`, {
      method: 'DELETE',
    }).then(() => {});
  }

  static fetchItemsFromCarpeta(uuid: string): Promise<Item[]> {
    return apiRequest<Item[]>(`/carpeta/get/${uuid}/item`).then(result => result ?? []);
  }

  static addExistingItem(carpetaUuid: string, itemUuid: string): Promise<void> {
    return apiRequest<void>(`/carpeta/add/${carpetaUuid}/item/existing/${itemUuid}`, {
      method: 'POST',
    }).then(() => {});
  }

  static removeItem(carpetaUuid: string, itemUuid: string): Promise<void> {
    return apiRequest<void>(`/carpeta/delete/${carpetaUuid}/item/${itemUuid}`, {
      method: 'DELETE',
    }).then(() => {});
  }

  static registrarAcces(uuid: string): Promise<Carpeta | null> {
    return apiRequest<Carpeta>(`/carpeta/access/${uuid}`, { method: 'POST' });
  }
}

export type Carpeta = {
  uuid: string;
  nom: string;
  dataCreacio: string;
  dataEditat: string;
  favorit: boolean;
  ultimAccess: string;
  comptadorAccess: number;
  items: {
    uuid: string;
    titol: string;
    dinsDeCarpeta: boolean;
  }[];
};