import { API_BASE } from './client';

// Login de usuario
export async function loginUser(correu: string, contrasenya: string, rememberMe: boolean = true) {
  try {
    const res = await fetch(`${API_BASE}/auth/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ correu, contrasenya }),
    });

    if (!res.ok) {
      const errorData = await res.json();
      throw new Error(errorData.message || 'Error en login');
    }

    const data = await res.json();
    const { token, user } = data;

    if (rememberMe) {
      localStorage.setItem('jwtToken', token);
    } else {
      sessionStorage.setItem('jwtToken', token);
    }

  if (!res.ok) {
    throw new Error('Credenciales incorrectas');
  }

  const data = await res.json();

  return {
    token: data.token,
    kdfSalt: data.kdfSalt as string,
    usuari: {
      ...data.usuari,
      imatge: data.usuari?.imatge ?? null,
    },
  };
}

export function logout() {
  localStorage.removeItem('jwtToken');
  localStorage.removeItem('usuari');
  sessionStorage.removeItem('jwtToken');
}

// Peticiones GET con token
export async function apiGet(endpoint: string) {
  const token = localStorage.getItem('jwtToken') || sessionStorage.getItem('jwtToken');
  if (!token) throw new Error('No autenticado');

  const res = await fetch(`${API_BASE}${endpoint}`, {
    headers: { Authorization: `Bearer ${token}` }
  });

  if (!res.ok) throw new Error('Error en la petición GET');
  return await res.json();
}
