const API_BASE = 'https://10.147.17.250:8081/api'; 

// Login de usuario
export async function loginUser(correu: string, contrasenya: string, rememberMe: boolean = true) {
  try {
    const res = await fetch(`${API_BASE}/login`, {
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

    return { user, token };
  } catch (err: any) {
    throw new Error(err.message || 'Error de conexión');
  }
}

// Obtener usuario logueado
export function getCurrentUser() {
  const token =
    localStorage.getItem('jwtToken') ||
    sessionStorage.getItem('jwtToken');

  if (!token) return null;

  try {
    const payload = JSON.parse(atob(token.split('.')[1]));

    return {
      token,
      user: payload
    };
  } catch (error) {
    console.error('Error decoding token', error);
    return null;
  }
}

// Logout
export function logout() {
  localStorage.removeItem('jwtToken');
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