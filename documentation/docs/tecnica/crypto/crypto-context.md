# CryptoContext

Fitxer: `src/context/CryptoContext.tsx`

Gestiona la disponibilitat de la parella de claus RSA a l'arbre de components durant la sessió activa.

## Estat

| Propietat | Tipus | Descripció |
|---|---|---|
| `privateKey` | `CryptoKey \| null` | Clau privada RSA importada, disponible per desxifrar |
| `publicKey` | `CryptoKey \| null` | Clau pública RSA importada, disponible per xifrar |

## Funcions

`setPrivateKey(key)` / `setPublicKey(key)` — actualitzen les claus en memòria.

`clearCryptoState()` — elimina les claus de memòria i de `sessionStorage`. S'invoca en fer logout.

## Persistència de sessió

Al muntar el proveïdor, intenta restaurar les claus des de `sessionStorage` (on es guarden en base64 just després del login). Si la restauració falla, s'eliminen les entrades corrupcions i les claus queden a `null`.

Les claus mai es guarden a `localStorage`. Quan el navegador es tanca, desapareixen.

## Ús

```tsx
const { privateKey } = useCrypto();
const password = await decryptPasswordWithDataKey(
  await rsaDecrypt(privateKey, item.encryptedDataKey.encryptedDataKey),
  item.contrasenya,
  item.iv
);
```
