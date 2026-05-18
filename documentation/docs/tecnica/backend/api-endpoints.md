# API REST — Endpoints

Tots els endpoints requereixen el header `Authorization: Bearer {token}` excepte `POST /auth/login`. La URL base és `/api`.

## Auth

| Mètode | Ruta | Rol | Descripció |
|---|---|---|---|
| POST | `/auth/login` | Públic | Autenticació. Retorna JWT, `kdfSalt` i `encryptedPrivateKey` |

## Ítems

| Mètode | Ruta | Rol | Descripció |
|---|---|---|---|
| GET | `/item/all/admin` | ADMIN | Tots els ítems del sistema |
| GET | `/item/get/all` | Tots | Ítems de l'usuari autenticat |
| GET | `/item/get/admin/{uuid}` | ADMIN | Ítem per UUID (admin) |
| GET | `/item/get/{uuid}` | Tots | Ítem per UUID (propietari o compartit) |
| POST | `/item/add` | Tots | Crea un ítem nou |
| PUT | `/item/update/{uuid}` | Tots | Actualitza un ítem |
| DELETE | `/item/delete/{uuid}` | Tots | Elimina un ítem |
| POST | `/item/access/{uuid}` | Tots | Registra un accés i incrementa el comptador |

## Carpetes

| Mètode | Ruta | Rol | Descripció |
|---|---|---|---|
| GET | `/carpeta/all/admin` | ADMIN | Totes les carpetes del sistema |
| GET | `/carpeta/get/all` | Tots | Carpetes de l'usuari autenticat |
| GET | `/carpeta/get/admin/{uuid}` | ADMIN | Carpeta per UUID (admin) |
| GET | `/carpeta/get/{uuid}` | Tots | Carpeta per UUID amb els seus ítems |
| POST | `/carpeta/add` | Tots | Crea una carpeta nova |
| PUT | `/carpeta/update/{uuid}` | Tots | Actualitza el nom d'una carpeta |
| DELETE | `/carpeta/delete/{uuid}` | Tots | Elimina una carpeta |
| GET | `/carpeta/get/{uuid}/item` | Tots | Ítems d'una carpeta |
| POST | `/carpeta/add/{uuid}/item/existing/{itemUuid}` | Tots | Afegeix un ítem existent a una carpeta |
| DELETE | `/carpeta/delete/{uuid}/item/{itemUuid}` | Tots | Treu un ítem d'una carpeta |
| POST | `/carpeta/access/{uuid}` | Tots | Registra un accés a la carpeta |

## Compartits

| Mètode | Ruta | Rol | Descripció |
|---|---|---|---|
| GET | `/compartit/all/admin` | ADMIN | Tots els compartits del sistema |
| GET | `/compartit/get/all` | Tots | Compartits rebuts per l'usuari |
| GET | `/compartit/get/all/creats` | Tots | Compartits creats per l'usuari |
| GET | `/compartit/get/{uuid}` | Tots | Compartit per UUID |
| POST | `/compartit/add` | Tots | Crea un compartit |
| POST | `/compartit/add/item` | Tots | Crea ítem i compartit en una sola operació |
| POST | `/compartit/add/carpeta` | Tots | Crea carpeta i compartit en una sola operació |
| PUT | `/compartit/update/{uuid}/{permisos}` | Tots | Actualitza el permís d'un compartit |
| DELETE | `/compartit/delete/{uuid}` | Tots | Elimina un compartit |

## Usuaris

| Mètode | Ruta | Rol | Descripció |
|---|---|---|---|
| GET | `/usuari/all/admin` | ADMIN | Tots els usuaris del sistema |
| GET | `/usuari/all` | Tots | Usuaris de la mateixa sucursal |
| GET | `/usuari/get/all/public` | Tots | Usuaris amb clau pública (per compartir) |
| GET | `/usuari/get/all/departament` | Tots | Usuaris agrupats per departament |
| POST | `/usuari/add/admin` | ADMIN / CAP | Crea un usuari |
| PUT | `/usuari/update/{uuid}` | ADMIN / CAP | Actualitza un usuari |
| PUT | `/usuari/update/self` | Tots | Actualitza les dades pròpies |
| PUT | `/usuari/update/password` | Tots | Canvia la contrasenya mestra |
| DELETE | `/usuari/delete/{uuid}` | ADMIN | Elimina un usuari |
| POST | `/usuari/upload/image` | Tots | Puja la foto de perfil |
| GET | `/usuari/image/{filename}` | Tots | Obté la foto de perfil |

## Sucursals, Departaments, Rols, Dominis, Config

Tots segueixen el mateix patró CRUD amb els prefixos `/sucursal`, `/departament`, `/rol`, `/domini` i `/config`, protegits per `ADMIN` o `CAP` per a les operacions d'escriptura.

## Utils

| Mètode | Ruta | Rol | Descripció |
|---|---|---|---|
| GET | `/utils/password/{contrasenya}` | Tots | Comprova si una contrasenya apareix a la base de dades HIBP |
| POST | `/utils/password/generate` | Tots | Genera una contrasenya aleatòria amb criteris |
