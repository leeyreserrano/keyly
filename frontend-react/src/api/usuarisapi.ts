import { apiMultipartRequest } from './client';
import type { Usuari } from '../context/AuthContext';

export class usuarisApi {
  static uploadImage(file: File, token: string): Promise<Usuari | null> {
    const formData = new FormData();
    formData.append('file', file);
    return apiMultipartRequest<Usuari>('/usuari/upload/image', formData, token);
  }
}