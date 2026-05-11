import { apiRequest } from './client';

export type Departament = {
  uuid: string;
  nom: string;
  sucursal?: {
    uuid: string;
    nom: string;
  } | null;
};

export type DepartamentCreate = {
  nom: string;
  sucursalUuid: string;
};

export type DepartamentUpdate = {
  nom?: string;
  sucursalUuid?: string;
};

export class departamentsApi {
  static fetchAll(): Promise<Departament[]> {
    return apiRequest<Departament[]>('/departament/all/admin').then(r => r ?? []);
  }

  static fetchOne(uuid: string): Promise<Departament | null> {
    return apiRequest<Departament>(`/departament/get/admin/${uuid}`);
  }

  static add(data: DepartamentCreate): Promise<Departament | null> {
    return apiRequest<Departament>('/departament/add/admin', {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  static update(uuid: string, data: DepartamentUpdate): Promise<Departament | null> {
    return apiRequest<Departament>(`/departament/update/admin/${uuid}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    });
  }

  static delete(uuid: string): Promise<void> {
    return apiRequest<void>(`/departament/delete/admin/${uuid}`, {
      method: 'DELETE',
    }).then(() => {});
  }
}