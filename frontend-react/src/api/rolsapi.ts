import { apiRequest } from './client';

export type Rol = {
  uuid: string;
  nom: string;
  sucursal?: {
    uuid: string;
    nom: string;
  } | null;
};

export type RolCreate = {
  sucursalUuid: string;
  nom: string;
};

export type RolUpdate = {
  sucursalUuid: string;
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

  static delete(uuid: string): Promise<Rol> {
    return apiRequest<Rol>(`/rol/delete/admin/${uuid}`, {
      method: 'DELETE',
    }).then((r) => {
      if (r === null) {
        throw new Error('Rol not found');
      }
      return r;
    });
  }

  static create(data: RolCreate): Promise<Rol> {
    return apiRequest<Rol>('/rol/add/admin', {
      method: 'POST',
      body: JSON.stringify(data),
    }).then((r) => {
      if (r === null) {
        throw new Error('Rol not created');
      }
      return r;
    });
  }

  static update(uuid: string, data: RolUpdate): Promise<Rol> {
    return apiRequest<Rol>(`/rol/update/admin/${uuid}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    }).then((r) => {
      if (r === null) {
        throw new Error('Rol not updated');
      }
      return r;
    });
  }
}