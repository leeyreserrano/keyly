import { apiRequest } from './client';

export type Domini = {
  uuid: string;
  domini: string;
  sucursal?: {
    uuid: string;
    nom: string;
  } | null;
};

export type CreateDomini = {
  domini: string;
  sucursalUuid: string;
};

export type UpdateDomini = {
  domini: string;
  sucursalUuid: string;
};

export const dominiApi = {
  fetchAll: () =>
    apiRequest<Domini[]>('/domini/all/admin', { method: 'GET' }),

  getByUuid: (uuid: string) =>
    apiRequest<Domini>(`/domini/get/admin/${uuid}`, { method: 'GET' }),

  add: (body: CreateDomini) =>
    apiRequest<Domini>('/domini/add/admin', {
      method: 'POST',
      body: JSON.stringify(body),
    }),

  update: (uuid: string, body: UpdateDomini) =>
    apiRequest<Domini>(`/domini/update/admin/${uuid}`, {
      method: 'PUT',
      body: JSON.stringify(body),
    }),

  delete: (uuid: string) =>
    apiRequest<void>(`/domini/delete/admin/${uuid}`, { method: 'DELETE' }),
};