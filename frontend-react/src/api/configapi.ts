import { apiRequest } from './client';

export type Config = {
  uuid: string;
  permetreTotsDominis: boolean;
  diesExpiracio: number;
};

export type UpdateConfig = {
  permetreTotsDominis: boolean;
  diesExpiracio: number;
};

export const configApi = {
  fetchAll: () =>
    apiRequest<Config[]>('/config/all/admin', { method: 'GET' }),

  getByUuid: (uuid: string) =>
    apiRequest<Config>(`/config/get/admin/${uuid}`, { method: 'GET' }),

  getBySucursal: (sucursalUuid: string) =>
    apiRequest<Config>(`/config/get/admin/sucursal/${sucursalUuid}`, { method: 'GET' }),

  updateByUuid: (uuid: string, data: UpdateConfig) =>
    apiRequest<Config>(`/config/update/admin/${uuid}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    }),

  updateBySucursal: (sucursalUuid: string, data: UpdateConfig) =>
    apiRequest<Config>(`/config/update/admin/sucursal/${sucursalUuid}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    }),
};