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

export class utilsApi {
  static async generatePassword(config: PasswordConfig): Promise<string> {
    const token = localStorage.getItem('jwtToken') || sessionStorage.getItem('jwtToken');

    const res = await fetch(`${API_BASE}/utils/custom/password`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
      },
      body: JSON.stringify(config),
    });

    if (res.status === 401) {
      localStorage.clear();
      sessionStorage.clear();
      window.location.href = '/';
      throw new Error('Sessió expirada');
    }

    if (!res.ok) {
      let msg = `Error ${res.status}`;
      try {
        const data = await res.json();
        msg = data.message || msg;
      } catch {}
      throw new Error(msg);
    }

    const data = await res.json();
    return data.contrasenya;
  }
}