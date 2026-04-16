import {
  createContext,
  useContext,
  useState,
  useEffect,
  type ReactNode
} from 'react';

export type Usuari = {
  uuid: string;
  nom: string;
  correu: string;
  imatge: string;
  rolIntern: 'ADMIN' | 'CAP' | 'USUARI';
};

type AuthContextType = {
  usuari: Usuari | null;
  token: string | null;
  loadingAuth: boolean;
  login: (usuari: Usuari, token: string, rememberMe: boolean) => void;
  logout: () => void;
};

const AuthContext = createContext<AuthContextType | null>(null);

function readStoredSession() {
  const token =
    localStorage.getItem('jwtToken') ||
    sessionStorage.getItem('jwtToken');

  const raw =
    localStorage.getItem('usuari') ||
    sessionStorage.getItem('usuari');

  if (!token || !raw) return { usuari: null, token: null };

  try {
    return {
      usuari: JSON.parse(raw),
      token
    };
  } catch {
    return { usuari: null, token: null };
  }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [usuari, setUsuari] = useState<Usuari | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [loadingAuth, setLoadingAuth] = useState(true);

  useEffect(() => {
    const stored = readStoredSession();
    setUsuari(stored.usuari);
    setToken(stored.token);
    setLoadingAuth(false);
  }, []);

  const login = (usuariData: Usuari, tokenData: string, rememberMe: boolean) => {
    setUsuari(usuariData);
    setToken(tokenData);

    const storage = rememberMe ? localStorage : sessionStorage;

    storage.setItem('jwtToken', tokenData);
    storage.setItem('usuari', JSON.stringify(usuariData));

    if (rememberMe) {
      sessionStorage.clear();
    } else {
      localStorage.clear();
    }
  };

  const logout = () => {
    setUsuari(null);
    setToken(null);

    localStorage.clear();
    sessionStorage.clear();
  };

  return (
    <AuthContext.Provider value={{ usuari, token, login, logout, loadingAuth }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used inside AuthProvider');
  return ctx;
}