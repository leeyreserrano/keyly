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
  avatarVersion: number;
  login: (usuari: Usuari, token: string, rememberMe: boolean) => void;
  logout: () => void;
  refreshAvatar: () => void;
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
    return { usuari: JSON.parse(raw), token };
  } catch {
    return { usuari: null, token: null };
  }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [usuari, setUsuari] = useState<Usuari | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [loadingAuth, setLoadingAuth] = useState(true);
  const [avatarVersion, setAvatarVersion] = useState(0);

  useEffect(() => {
    const stored = readStoredSession();
    setUsuari(stored.usuari);
    setToken(stored.token);
    setLoadingAuth(false);
  }, []);

  const login = (usuariData: Usuari, tokenData: string, rememberMe: boolean) => {
    setUsuari(usuariData);
    setToken(tokenData);

    localStorage.removeItem('jwtToken');
    localStorage.removeItem('usuari');
    sessionStorage.removeItem('jwtToken');
    sessionStorage.removeItem('usuari');

    const storage = rememberMe ? localStorage : sessionStorage;
    storage.setItem('jwtToken', tokenData);
    storage.setItem('usuari', JSON.stringify(usuariData));
  };

  const logout = () => {
    setUsuari(null);
    setToken(null);
    localStorage.clear();
    sessionStorage.clear();
  };

  const refreshAvatar = () => setAvatarVersion(v => v + 1);

  return (
    <AuthContext.Provider value={{ usuari, token, login, logout, loadingAuth, avatarVersion, refreshAvatar }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used inside AuthProvider');
  return ctx;
}