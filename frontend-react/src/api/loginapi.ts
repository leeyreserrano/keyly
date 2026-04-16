import { API_BASE } from './client';

export async function loginUser(correu: string, contrasenya: string) {
  const res = await fetch(`${API_BASE}/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ correu, contrasenya }),
  });

  if (!res.ok) {
    throw new Error('Credenciales incorrectas');
  }

  const data = await res.json();

  return {
    token: data.token,
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
  sessionStorage.removeItem('usuari');
}