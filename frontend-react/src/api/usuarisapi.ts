import { apiRequest, apiMultipartRequest } from './client';
import type { Usuari } from '../context/AuthContext';

export type UsuariPublic = {
  uuid: string;
  nom: string;
  correu: string;
  imatge: string;
  publicKey: string;
};

export type CreateUsuariData = {
  nom: string;
  correu: string;
  contrasenya: string;
  kdfSalt: string;
  publicKey: string;
  encryptedPrivateKey: string;
  rolIntern: 'ADMIN' | 'CAP' | 'USUARI';
  rolUuid: string;
  sucursalUuid: string;
  departamentUuid: string;
  potAdministrar: boolean;
};

export type UsuariAmbDepartament = {
  uuid: string;
  nom: string;
  correu: string;
  imatge: string;
  publicKey: string;
  departament?: {
    uuid: string;
    departament: string;
  } | null;
};
export type UpdateUsuariAdminData = {
  nom?: string;
  correu?: string;
  rolUuid?: string;
  sucursalUuid?: string;
  departamentUuid?: string;
  potAdministrar?: boolean;
};

export type UpdatePasswordData = {
  contrasenya: string;
  kdfSalt: string;
  publicKey: string;
  encryptedPrivateKey: string;
};

export class usuarisApi {
  static uploadImage(file: File, token: string): Promise<Usuari | null> {
    const formData = new FormData();
    formData.append('file', file);
    return apiMultipartRequest<Usuari>('/usuari/upload/image', formData, token);
  }

  static updateSelf(data: UpdatePasswordData): Promise<void> {
    return apiRequest<void>('/usuari/update', {
      method: 'PUT',
      body: JSON.stringify(data),
    }).then(() => { });
  }

  static fetchAll(): Promise<unknown[]> {
    return apiRequest<unknown[]>('/usuari/all/admin').then(r => r ?? []);
  }

  static fetchAllPublic(): Promise<UsuariPublic[]> {
    return apiRequest<UsuariPublic[]>('/usuari/all').then(r => r ?? []);
  }

  static createUsuari(data: CreateUsuariData, isAdmin: boolean): Promise<unknown> {
    const endpoint = isAdmin ? '/usuari/add/admin' : '/usuari/add/admin/cap';
    return apiRequest<unknown>(endpoint, {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  static updateUsuariAdmin(uuid: string, data: UpdateUsuariAdminData): Promise<unknown> {
    return apiRequest<unknown>(`/usuari/update/admin/cap/${uuid}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    });
  }

  static deleteUsuari(uuid: string): Promise<void> {
    return apiRequest<void>(`/usuari/delete/admin/cap/${uuid}`, {
      method: 'DELETE',
    }).then(() => { });
  }

  static fetchAllAmbDepartament(): Promise<UsuariAmbDepartament[]> {
    return apiRequest<UsuariAmbDepartament[]>('/usuari/all/admin').then(r => r ?? []);
  }
}