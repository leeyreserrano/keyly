import { apiRequest } from './client';

export type Sucursal = {
  uuid: string;
  nom: string;
  direccio?: string;
  ciutat?: string;
  pais?: string;
  telefon?: string;
  correu?: string;
};

export type CreateSucursal = {
  nom: string;
  direccio: string;
  ciutat: string;
  pais: string;
  telefon: string;
  correu: string;
};

export type UpdateSucursal = {
  nom: string;
  direccio: string;
  ciutat: string;
  pais: string;
  telefon: string;
  correu: string;
};

export const sucursalsApi = {
  fetchAll: () =>
    apiRequest<Sucursal[]>('/sucursal/all/admin', { method: 'GET' }).then(r => r ?? []),

  add: (data: CreateSucursal) =>
    apiRequest<Sucursal>('/sucursal/add/admin', {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  update: (uuid: string, data: UpdateSucursal) =>
    apiRequest<Sucursal>(`/sucursal/update/admin/${uuid}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    }),

  delete: (uuid: string) =>
    apiRequest<void>(`/sucursal/delete/admin/${uuid}`, { method: 'DELETE' }),
};