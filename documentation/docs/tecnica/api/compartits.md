# compartitsapi.ts

Fitxer: `src/api/compartitsapi.ts`

## Mètodes de `compartitsApi`

| Mètode | Endpoint | Mètode HTTP | Descripció |
|---|---|---|---|
| `fetchCompartitsRebuts()` | `/compartit/get/all` | GET | Compartits rebuts per l'usuari |
| `fetchCompartitsCreats()` | `/compartit/get/all/creats` | GET | Compartits creats per l'usuari |
| `getCompartit(uuid)` | `/compartit/get/{uuid}` | GET | Detall d'un compartit |
| `addCompartit(data)` | `/compartit/add` | POST | Crea un compartit nou |
| `addItemCompartit(data)` | `/compartit/add/item` | POST | Afegeix un ítem a un compartit existent |
| `updatePermisos(uuid, permisos)` | `/compartit/update/{uuid}/{permisos}` | PUT | Modifica el permís d'un compartit |
| `deleteCompartit(uuid)` | `/compartit/delete/{uuid}` | DELETE | Elimina un compartit |
| `fetchAllAdmin()` | `/compartit/all/admin` | GET | Tots els compartits (només `ADMIN`) |

## Tipus

```typescript
type Permisos = 'LECTURA' | 'ESCRIPTURA' | 'ADMINISTRADOR';
type TipusEntitat = 'ITEM' | 'CARPETA';
```

`CompartitPayload` inclou l'uuid de l'entitat, el tipus, la llista de destinataris i la DataKey xifrada amb la clau pública de cada destinatari.
