import { apiRequest } from './client';

export type CompartitUsuari = {
  uuid: string;
  nom: string;
};

export type CompartitCarpeta = {
  uuid: string;
  nom: string;
};

export type CompartitItem = {
  uuid: string;
  titol: string;
  dinsDeCarpeta: boolean;
};

export type Compartit = {
  uuid: string;
  usuari: CompartitUsuari;
  tipusEntitat: 'CARPETA' | 'ITEM';
  carpeta?: CompartitCarpeta;
  item?: CompartitItem;
  permisos: 'LECTURA';
  dataCreacio: string;
};

export type AddCompartitData = {
  tipusEntitat: 'CARPETA' | 'ITEM';
  entitatUuid: string;
  permisos: 'LECTURA';
};

export class compartitsApi {
  static fetchAll(): Promise<Compartit[]> {
    return apiRequest<Compartit[]>('/compartit/get/all').then(result => result ?? []);
  }

  static fetchOne(uuid: string): Promise<Compartit | null> {
    return apiRequest<Compartit>(`/compartit/get/${uuid}`);
  }

  static add(data: AddCompartitData): Promise<Compartit | null> {
    return apiRequest<Compartit>('/compartit/add', {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  static update(uuid: string, data: Partial<AddCompartitData>): Promise<Compartit | null> {
    return apiRequest<Compartit>(`/compartit/update/${uuid}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    });
  }

  static delete(uuid: string): Promise<void> {
    return apiRequest<void>(`/compartit/delete/${uuid}`, {
      method: 'DELETE',
    }).then(() => {});
  }
}