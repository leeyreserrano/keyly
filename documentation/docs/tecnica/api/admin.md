# APIs d'administració

## configapi.ts

Fitxer: `src/api/configapi.ts`

Gestiona la configuració per sucursal (dominis permesos i dies d'expiració). Només accessible per `ADMIN`.

| Mètode | Endpoint | Descripció |
|---|---|---|
| `fetchAll()` | `GET /config/all/admin` | Totes les configuracions |
| `getByUuid(uuid)` | `GET /config/get/admin/{uuid}` | Configuració per UUID |
| `getBySucursal(sucursalUuid)` | `GET /config/get/admin/sucursal/{uuid}` | Configuració d'una sucursal |
| `updateByUuid(uuid, data)` | `PUT /config/update/admin/{uuid}` | Actualitza per UUID |
| `updateBySucursal(sucursalUuid, data)` | `PUT /config/update/admin/sucursal/{uuid}` | Actualitza per sucursal |

Tipus `Config`: `{ uuid, permetreTotsDominis: boolean, diesExpiracio: number }`.

## sucursalsapi.ts

Fitxer: `src/api/sucursalsapi.ts`

| Mètode | Endpoint | Descripció |
|---|---|---|
| `fetchAll()` | `GET /sucursal/all/admin` | Totes les sucursals |
| `add(data)` | `POST /sucursal/add/admin` | Crea una sucursal |
| `update(uuid, data)` | `PUT /sucursal/update/admin/{uuid}` | Actualitza una sucursal |
| `delete(uuid)` | `DELETE /sucursal/delete/admin/{uuid}` | Elimina una sucursal |

Tipus `Sucursal`: `{ uuid, nom, direccio, ciutat, pais, telefon, correu }`.

## rolsapi.ts

Fitxer: `src/api/rolsapi.ts`

| Mètode | Endpoint | Descripció |
|---|---|---|
| `fetchAll()` | `GET /rol/all/admin` | Tots els rols |
| `fetchOne(uuid)` | `GET /rol/get/admin/{uuid}` | Rol per UUID |
| `create(data)` | `POST /rol/add/admin` | Crea un rol |
| `update(uuid, data)` | `PUT /rol/update/admin/{uuid}` | Actualitza un rol |
| `delete(uuid)` | `DELETE /rol/delete/admin/{uuid}` | Elimina un rol |

Tipus `Rol`: `{ uuid, nom, sucursal?: { uuid, nom } }`.

## dominiapi.ts

Fitxer: `src/api/dominiapi.ts`

| Mètode | Endpoint | Descripció |
|---|---|---|
| `fetchAll()` | `GET /domini/all/admin` | Tots els dominis |
| `fetchBySucursal(sucursalUuid)` | `GET /domini/get/sucursal/{uuid}` | Dominis d'una sucursal |
| `add(data)` | `POST /domini/add/admin` | Afegeix un domini |
| `delete(uuid)` | `DELETE /domini/delete/admin/{uuid}` | Elimina un domini |

## utilsapi.ts

Fitxer: `src/api/utilsapi.ts`

`utilsApi.generatePassword(config)` — crida a `POST /utils/custom/password` per generar una contrasenya al servidor segons la configuració passada.

Tipus `PasswordConfig`: `{ longitud, may, quantitatMay, numeros, quantitatNumeros, caractersEspecials, quantitatCaractersEspecials }`.
