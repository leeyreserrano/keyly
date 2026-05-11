import { createContext, useContext, useState, useEffect, type ReactNode } from 'react';
import type { FC } from 'react';
import { importPrivateKey, importPublicKey } from '../crypto/cryptoService';

interface CryptoContextType {
  privateKey: CryptoKey | null;
  setPrivateKey: (key: CryptoKey | null) => void;
  publicKey: CryptoKey | null;
  setPublicKey: (key: CryptoKey | null) => void;
  clearCryptoState: () => void;
}

const CryptoContext = createContext<CryptoContextType | null>(null);

export const CryptoProvider: FC<{ children: ReactNode }> = ({ children }) => {
  const [privateKey, setPrivateKeyState] = useState<CryptoKey | null>(null);
  const [publicKey, setPublicKeyState] = useState<CryptoKey | null>(null);

  useEffect(() => {
    const restore = async () => {
      try {
        const privateKeyB64 = sessionStorage.getItem('privateKey');
        const publicKeyB64 = sessionStorage.getItem('publicKey');

        if (privateKeyB64) {
          const pk = await importPrivateKey(privateKeyB64);
          setPrivateKeyState(pk);
        }

        if (publicKeyB64) {
          const pub = await importPublicKey(publicKeyB64);
          setPublicKeyState(pub);
        }
      } catch {
        sessionStorage.removeItem('privateKey');
        sessionStorage.removeItem('publicKey');
      }
    };

    restore();
  }, []);

  const setPrivateKey = (key: CryptoKey | null) => setPrivateKeyState(key);
  const setPublicKey = (key: CryptoKey | null) => setPublicKeyState(key);

  const clearCryptoState = () => {
    setPrivateKeyState(null);
    setPublicKeyState(null);
    sessionStorage.removeItem('privateKey');
    sessionStorage.removeItem('publicKey');
  };

  return (
    <CryptoContext.Provider value={{ privateKey, setPrivateKey, publicKey, setPublicKey, clearCryptoState }}>
      {children}
    </CryptoContext.Provider>
  );
};

export function useCrypto(): CryptoContextType {
  const ctx = useContext(CryptoContext);
  if (!ctx) throw new Error('useCrypto must be used inside CryptoProvider');
  return ctx;
}