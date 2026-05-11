const API_BASE = import.meta.env.VITE_API_BASE as string;

function getToken(): string | null {
  return localStorage.getItem('jwtToken') || sessionStorage.getItem('jwtToken');
}

function handleUnauthorized() {
  localStorage.clear();
  sessionStorage.clear();
  window.location.href = '/';
}

function isTokenExpired(token: string): boolean {
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.exp * 1000 < Date.now();
  } catch {
    return true;
  }
}

export async function apiRequest<T>(
  endpoint: string,
  options: RequestInit & { _token?: string; _skipLogoutOn401?: boolean } = {}
): Promise<T | null> {
  const { _token, _skipLogoutOn401, ...fetchOptions } = options;
  const token = _token ?? getToken();

  if (token && isTokenExpired(token)) {
    handleUnauthorized();
    throw new Error('Sessió expirada');
  }

  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
    ...(fetchOptions.headers as Record<string, string>),
  };

  const res = await fetch(`${API_BASE}${endpoint}`, {
    ...fetchOptions,
    headers,
  });

  if (res.status === 401) {
    if (!_skipLogoutOn401) {
      handleUnauthorized();
    }
    throw new Error('No autoritzat');
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

export async function apiMultipartRequest<T>(
  endpoint: string,
  formData: FormData,
  token?: string
): Promise<T | null> {
  const resolvedToken = token ?? getToken();

  const res = await fetch(`${API_BASE}${endpoint}`, {
    method: 'POST',
    headers: {
      ...(resolvedToken ? { Authorization: `Bearer ${resolvedToken}` } : {}),
    },
    body: formData,
  });

  if (res.status === 401) {
    handleUnauthorized();
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