import { apiRequest } from './client';

export type Rol = {
  uuid: string;
  nom: string;
};

export class rolsApi {
  static fetchAll(): Promise<Rol[]> {
    return apiRequest<Rol[]>('/rol/all/admin').then(r => r ?? []);
  }

  static fetchOne(uuid: string): Promise<Rol> {
    return apiRequest<Rol>(`/rol/get/admin/${uuid}`).then((r) => {
      if (r === null) {
        throw new Error('Rol not found');
      }
      return r;
    });
  }
}