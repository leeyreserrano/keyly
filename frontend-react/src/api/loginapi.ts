import { API_BASE } from './client';

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

    const { token, usuari, kdfSalt, encryptedPrivateKey } = data;

    if (rememberMe) {
      localStorage.setItem('jwtToken', token);
    } else {
      sessionStorage.setItem('jwtToken', token);
    }

    return {
      token,
      kdfSalt,
      encryptedPrivateKey,
      publicKeyB64: usuari?.publicKey ?? null,
      usuari: {
        ...usuari,
        imatge: usuari?.imatge ?? null,
      },
    };
  } catch (error: any) {
    throw new Error(error.message || 'Error inesperado');
  }
}

export function logout() {
  localStorage.removeItem('jwtToken');
  localStorage.removeItem('usuari');
  sessionStorage.removeItem('jwtToken');
}