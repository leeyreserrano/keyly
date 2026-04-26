import { apiRequest } from './client';
import type { Usuari } from '../context/AuthContext';

export class usuarisApi {
  static uploadImage(file: string): Promise<Usuari | null> {
    return apiRequest<Usuari>('/usuari/upload/image', {
      method: 'POST',
      body: JSON.stringify({ file }),
    });
  }
}