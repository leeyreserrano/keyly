# usuarisapi.ts

Fitxer: `src/api/usuarisapi.ts`

## Mètodes de `usuarisApi`

| Mètode | Endpoint | Descripció |
|---|---|---|
| `uploadImage(file, token)` | `POST /usuari/upload/image` | Puja la foto de perfil (multipart) |
| `updateSelf(data)` | `PUT /usuari/update/self` | Actualitza nom i correu propis |
| `updatePassword(data)` | `PUT /usuari/update/password` | Canvia contrasenya mestra i re-xifra la clau privada |
| `fetchAllPublic()` | `GET /usuari/get/all/public` | Obté usuaris amb nom, correu i clau pública |
| `fetchAllAmbDepartament()` | `GET /usuari/get/all/departament` | Usuaris amb el seu departament (per compartir per departament) |
| `createUsuari(data)` | `POST /usuari/add` | Crea un usuari nou (admin) |
| `updateUsuariAdmin(uuid, data)` | `PUT /usuari/update/{uuid}` | Edita dades d'un usuari (admin) |
| `deleteUsuari(uuid)` | `DELETE /usuari/delete/{uuid}` | Elimina un usuari (admin) |

## Tipus rellevants

`UsuariPublic` — dades no sensibles d'un usuari: `uuid`, `nom`, `correu`, `imatge`, `publicKey`.

`CreateUsuariData` — payload per crear un usuari: inclou `kdfSalt`, `publicKey` i `encryptedPrivateKey` generats al navegador.

`UpdatePasswordData` — payload per canviar la contrasenya: inclou el nou `kdfSalt`, `publicKey` i `encryptedPrivateKey` re-xifrada.
