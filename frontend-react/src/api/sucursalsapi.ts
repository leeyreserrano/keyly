import { apiRequest } from './client';

export type Sucursal = {
  uuid: string;
  nom: string;
};

export class sucursalsApi {
  static fetchAll(): Promise<Sucursal[]> {
    return apiRequest<Sucursal[]>('/sucursal/all/admin').then(r => r ?? []);
  }

  static fetchOne(uuid: string): Promise<Sucursal | null> {
    return apiRequest<Sucursal>(`/sucursal/get/admin/${uuid}`);
  }

  static add(data: Partial<Sucursal>): Promise<Sucursal | null> {
    return apiRequest<Sucursal>('/sucursal/add/admin', {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  static update(uuid: string, data: Partial<Sucursal>): Promise<Sucursal | null> {
    return apiRequest<Sucursal>(`/sucursal/update/admin/${uuid}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    });
  }

  static delete(uuid: string): Promise<void> {
    return apiRequest<void>(`/sucursal/delete/admin/${uuid}`, {
      method: 'DELETE',
    }).then(() => {});
  }
}