const API_BASE = import.meta.env.VITE_API_BASE as string;

function getToken(): string | null {
  return localStorage.getItem('jwtToken') || sessionStorage.getItem('jwtToken');
}

export async function apiRequest<T>(
  endpoint: string,
  options: RequestInit = {}
): Promise<T | null> {
  const token = getToken();

  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
    ...(options.headers as Record<string, string>),
  };

  const res = await fetch(`${API_BASE}${endpoint}`, {
    ...options,
    headers,
  });

  if (res.status === 401) {
    localStorage.clear();
    sessionStorage.clear();
    window.location.href = '/';
    throw new Error('Sesión expirada');
  }

  if (!res.ok) {
    let msg = `Error ${res.status}`;
    try {
      const data = await res.json();
      msg = data.message || msg;
    } catch {}
    throw new Error(msg);
  }
 
  const text = await res.text();
  return text ? JSON.parse(text) : null;
}

export async function apiImageRequest(path: string): Promise<string> {
  const token = getToken();

  const res = await fetch(`${API_BASE}/usuari/get/image/${path}`, {
    headers: {
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
  });

  if (!res.ok) {
    throw new Error('Error loading image');
  }

  const blob = await res.blob();
  return URL.createObjectURL(blob);
}
export { API_BASE };
