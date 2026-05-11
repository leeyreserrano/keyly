import { apiRequest } from './client';

export class compartitsApi {
  static fetchCompartitsRebuts(): Promise<Compartit[] | null> {
    return apiRequest<Compartit[]>('/compartit/get/all');
  }

  static fetchCompartitsCreats(): Promise<Compartit[] | null> {
    return apiRequest<Compartit[]>('/compartit/get/all/creats');
  }

  static getCompartit(uuid: string): Promise<Compartit | null> {
    return apiRequest<Compartit>(`/compartit/get/${uuid}`);
  }

  static deleteCompartit(uuid: string): Promise<void | null> {
    return apiRequest<void>(`/compartit/delete/${uuid}`, { method: 'DELETE' });
  }

  static addCompartit(data: CompartitPayload): Promise<Compartit[] | null> {
    return apiRequest<Compartit[]>('/compartit/add', {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  static updatePermisos(compartitUuid: string, permisos: Permisos): Promise<Compartit | null> {
    return apiRequest<Compartit>(`/compartit/update/${compartitUuid}/${permisos}`, {
      method: 'PUT',
    });
  }

  static addItemCompartit(data: AddItemCompartitPayload): Promise<Compartit | null> {
    return apiRequest<Compartit>('/compartit/add/item', {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }
  static fetchCompartitsByCarpeta(carpetaUuid: string): Promise<Compartit[]> {
    return Promise.all([
      compartitsApi.fetchCompartitsRebuts(),
      compartitsApi.fetchCompartitsCreats(),
    ]).then(([rebuts, creats]) => {
      const tots = [...(rebuts ?? []), ...(creats ?? [])];
      return tots.filter(
        (c) =>
          c.tipusEntitat === 'ITEM' &&
          c.item?.dinsDeCarpeta === true &&
          c.carpeta?.uuid === carpetaUuid
      );
    });
  }
  static fetchAllAdmin(): Promise<Compartit[]> {
    return apiRequest<Compartit[]>('/compartit/all/admin')
      .then(result => result ?? []);
  }
}

export type Permisos = 'LECTURA' | 'ESCRIPTURA' | 'ADMINISTRADOR';
export type TipusEntitat = 'ITEM' | 'CARPETA';

export type CompartitUsuari = {
  uuid: string;
  nom: string;
  correu: string;
  imatge: string;
  publicKey: string;

  rolIntern?: 'ADMIN' | 'CAP' | 'USUARI';

  sucursal?: {
    uuid: string;
    nom: string;
  } | null;

  departament?: {
    uuid: string;
    departament: string;
  } | null;
};

export type CompartitDataKey = {
  uuid: string;
  encryptedDataKey: string;
};

export type CompartitItem = {
  uuid: string;
  titol: string;
  nomUsuari: string;
  contrasenya: string;
  iv: string;
  encryptedDataKey: CompartitDataKey | null;
  url: string;
  notes?: string;
  favorit: boolean;
  dataCreacio: string;
  dataEditat: string;
  ultimAccess: string;
  comptadorAccess: number;
  dinsDeCarpeta: boolean;
};

export type CompartitCarpeta = {
  uuid: string;
  nom: string;
  favorit: boolean;
  dataCreacio: string;
  dataEditat: string;
  ultimAccess: string;
  comptadorAccess: number;
  items: CompartitItem[];
};

export type Compartit = {
  usuari: any;
  uuid: string;
  usuariCreador: CompartitUsuari;
  usuariReceptor: CompartitUsuari;
  tipusEntitat: TipusEntitat;
  permisos: Permisos;
  dataCreacio: string;
  item?: CompartitItem | null;
  carpeta?: CompartitCarpeta | null;
};

export type EncryptedDataKeyEntry = {
  itemUuid: string;
  encryptedDataKey: string;
};

export type CompartitUsuariPayload = {
  usuariUuid: string;
  permis: Permisos;
  encryptedDataKeys?: EncryptedDataKeyEntry[];
};

export type CompartitPayload = {
  entitatUuid: string;
  tipusEntitat: TipusEntitat;
  usuaris: CompartitUsuariPayload[];
};

export type AddItemCompartitPayload = {
  itemRequest: {
    titol: string;
    nomUsuari: string;
    contrasenya: string;
    iv: string;
    encryptedDataKey: string;
    url?: string;
    notes?: string;
    favorit: boolean;
  };
  compartitRequest: {
    entitatUuid: string;
    tipusEntitat: TipusEntitat;
    usuaris: CompartitUsuariPayload[];
  };
};