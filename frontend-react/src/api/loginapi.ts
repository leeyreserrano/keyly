import { API_BASE } from './client';

// Login de usuario
export async function loginUser(
  correu: string,
  contrasenya: string,
  rememberMe: boolean = true
) {
  try {
    const res = await fetch(`${API_BASE}/auth/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ correu, contrasenya }),
    });

    const data = await res.json();

    if (!res.ok) {
      throw new Error(data.message || 'Error en login');
    }

    const { token, usuari, kdfSalt } = data;

    // Guardar token
    if (rememberMe) {
      localStorage.setItem('jwtToken', token);
    } else {
      sessionStorage.setItem('jwtToken', token);
    }

    return {
      token,
      kdfSalt,
      usuari: {
        ...usuari,
        imatge: usuari?.imatge ?? null,
      },
    };
  } catch (error: any) {
    throw new Error(error.message || 'Error inesperado');
  }
}

// Logout
export function logout() {
  localStorage.removeItem('jwtToken');
  localStorage.removeItem('usuari');
  sessionStorage.removeItem('jwtToken');
}

// GET con token
export async function apiGet(endpoint: string) {
  const token =
    localStorage.getItem('jwtToken') ||
    sessionStorage.getItem('jwtToken');

  if (!token) throw new Error('No autenticado');

  const res = await fetch(`${API_BASE}${endpoint}`, {
    headers: { Authorization: `Bearer ${token}` },
  });

  if (!res.ok) throw new Error('Error en la petición GET');

  return await res.json();
}