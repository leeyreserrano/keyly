import { API_BASE } from './client';

export function getUserImage(path?: string | null) {
  if (!path) return undefined;
  return `${API_BASE}/usuari/get/image/${path}`;
}