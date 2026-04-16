import { API_BASE } from './client';

export type PasswordConfig = {
  longitud: number;
  may: boolean;
  quantitatMay: number;
  numeros: boolean;
  quantitatNumeros: number;
  caractersEspecials: boolean;
  quantitatCaractersEspecials: number;
};

function getToken(): string | null {
  return localStorage.getItem('jwtToken') || sessionStorage.getItem('jwtToken');
}

export class utilsApi {
  static async generatePassword(config: PasswordConfig): Promise<string> {
    const token = getToken();

    const res = await fetch(`${API_BASE}/utils/costum/password`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
      },
      body: JSON.stringify(config),
    });

    if (!res.ok) throw new Error(`Error ${res.status}`);

    return res.text();
  }
}