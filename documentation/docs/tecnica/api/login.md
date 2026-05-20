# loginapi.ts

Fitxer: `src/api/loginapi.ts`

A diferència dels altres fitxers d'API, no usa `apiRequest` perquè en el moment del login encara no hi ha token disponible.

## `loginUser(correu, contrasenya, rememberMe)`

Envia les credencials al backend (`POST /auth/login`) i, si l'autenticació és correcta, rep:

- `token` — JWT per a les peticions posteriors.
- `kdfSalt` — salt per derivar la clau criptogràfica.
- `encryptedPrivateKey` — clau privada RSA xifrada amb la clau derivada.
- `usuari` — dades del perfil de l'usuari.

Desa el token a `localStorage` (si `rememberMe` és `true`) o a `sessionStorage`.

## `logout()`

Elimina el token i les dades d'usuari de `localStorage` i `sessionStorage`. A la pràctica, el logout a la UI es fa via `AuthContext.logout()`, que també actualitza l'estat de React.
