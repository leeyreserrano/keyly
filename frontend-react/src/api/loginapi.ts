const API_BASE = 'https://10.147.17.250/api';

/**
 * Login de usuario
 * @param email 
 * @param password 
 * @param remember Me - si true guarda token en localStorage, si false en sessionStorage
 */
export async function loginUser(
  email: string,
  password: string,
  remember: boolean = true
) {
  try {
    const res = await fetch(`${API_BASE}/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ email, password }),
    });

    const data = await res.json();

    if (!res.ok) {
      throw new Error(data.message || 'Error en login');
    }

    const { token, user } = data;
    const storage = remember ? localStorage : sessionStorage;
    storage.setItem('jwtToken', token);
    storage.setItem('user', JSON.stringify(user));

    return { user, token };
  } catch (error: any) {
    throw new Error(error.message || 'Error en login');
  }
}

/**
 * Obtener usuario logueado
 */
export function getCurrentUser() {
  const token = localStorage.getItem('jwtToken') || sessionStorage.getItem('jwtToken');
  const userString = localStorage.getItem('user') || sessionStorage.getItem('user');

  if (!token || !userString) return null;

  const user = JSON.parse(userString);
  return { token, user };
}

/**
 * Logout de usuario
 */
export function logout() {
  localStorage.removeItem('jwtToken');
  localStorage.removeItem('user');
  sessionStorage.removeItem('jwtToken');
  sessionStorage.removeItem('user');
}

/**
 * Petición GET autenticada
 * @param endpoint - endpoint relativo, ej: '/items'
 */
export async function apiGet(endpoint: string) {
  const token = localStorage.getItem('jwtToken') || sessionStorage.getItem('jwtToken');
  if (!token) throw new Error('No autenticado');

  const res = await fetch(`${API_BASE}${endpoint}`, {
    method: 'GET',
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
  });

  const data = await res.json();

  if (!res.ok) throw new Error(data.message || 'Error en la petición');

  return data;
}

/**
 * Petición POST autenticada
 */
export async function apiPost(endpoint: string, body: any) {
  const token = localStorage.getItem('jwtToken') || sessionStorage.getItem('jwtToken');
  if (!token) throw new Error('No autenticado');

  const res = await fetch(`${API_BASE}${endpoint}`, {
    method: 'POST',
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(body),
  });

  const data = await res.json();

  if (!res.ok) throw new Error(data.message || 'Error en la petición');

  return data;
}