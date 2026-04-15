import { API_BASE } from './client';

export function getUserImage(path?: string | null) {
  if (!path) return undefined;

  const token =
    localStorage.getItem('jwtToken') ||
    sessionStorage.getItem('jwtToken');

  return `${API_BASE}/usuari/get/image/${path}?token=${token}`;
}