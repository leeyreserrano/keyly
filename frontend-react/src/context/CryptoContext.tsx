import { createContext, useContext, useState, type ReactNode } from 'react';

type CryptoContextType = {
  derivedKey: CryptoKey | null;
  setDerivedKey: (key: CryptoKey | null) => void;
};

const CryptoContext = createContext<CryptoContextType | null>(null);

export function CryptoProvider({ children }: { children: ReactNode }) {
  const [derivedKey, setDerivedKey] = useState<CryptoKey | null>(null);

  return (
    <CryptoContext.Provider value={{ derivedKey, setDerivedKey }}>
      {children}
    </CryptoContext.Provider>
  );
}

export function useCrypto() {
  const ctx = useContext(CryptoContext);
  if (!ctx) throw new Error('useCrypto must be used inside CryptoProvider');
  return ctx;
}