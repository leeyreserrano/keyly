# cryptoService

Fitxer: `src/crypto/cryptoService.ts`

Totes les operacions criptogràfiques fan servir la Web Crypto API nativa del navegador (`crypto.subtle`). Cap biblioteca externa de criptografia.

## Funcions exportades

### `deriveKey(password, saltB64)`

Deriva una clau AES-GCM de 256 bits a partir d'una contrasenya i un salt en base64 usant PBKDF2 amb 310.000 iteracions i SHA-256. Retorna la clau derivada en base64.

### `decryptPrivateKey(encryptedPrivateKey, derivedKeyB64)`

Desxifra la clau privada RSA emmagatzemada al servidor. El format d'entrada és `{ivB64}:{ciphertextB64}`. Retorna la clau privada en base64.

### `encryptPrivateKey(privateKeyB64, derivedKeyB64)`

Xifra la clau privada RSA amb AES-GCM. Genera un IV aleatori de 96 bits. Retorna la cadena `{ivB64}:{ciphertextB64}`. S'usa al registre i al canvi de contrasenya mestra.

### `generateKeyPair()`

Genera un parell de claus RSA-OAEP de 2048 bits. Retorna `{ publicKeyB64, privateKeyB64 }` en format SPKI i PKCS8 respectivament, codificats en base64.

### `importPublicKey(publicKeyB64)`

Importa una clau pública SPKI en base64 com a objecte `CryptoKey` per a operacions de xifrat.

### `importPrivateKey(privateKeyB64)`

Importa una clau privada PKCS8 en base64 com a objecte `CryptoKey` per a operacions de desxifrat.

### `rsaEncrypt(publicKey, data)`

Xifra un `Uint8Array` amb RSA-OAEP. Retorna el resultat en base64. S'usa per xifrar la DataKey de cada ítem quan es comparteix.

### `rsaDecrypt(privateKey, encryptedB64)`

Desxifra un valor RSA-OAEP en base64. Retorna el `Uint8Array` original. S'usa per recuperar la DataKey en accedir a un ítem.

### `generateDataKey()`

Genera una DataKey aleatòria de 256 bits com a `Uint8Array`. S'usa en crear un ítem nou.

### `encryptPasswordWithDataKey(dataKeyBytes, plainPassword)`

Xifra una contrasenya en text pla amb AES-GCM usant la DataKey. Genera un IV aleatori de 96 bits. Retorna `{ encrypted: string, iv: string }` en base64.

### `decryptPasswordWithDataKey(dataKeyBytes, encryptedB64, ivB64)`

Desxifra una contrasenya xifrada amb AES-GCM. Retorna la contrasenya en text pla.

## Helpers interns

`bytesToBase64` / `base64ToBytes` — conversió entre `Uint8Array` i strings base64.

`buf()` — garanteix que el `Uint8Array` apunta a un `ArrayBuffer` net (no `SharedArrayBuffer`), requisit de la Web Crypto API.
