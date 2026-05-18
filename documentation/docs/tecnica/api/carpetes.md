# carpetasapi.ts

Fitxer: `src/api/carpetasapi.ts`

## Mètodes de `carpetasApi`

| Mètode | Endpoint | Mètode HTTP | Descripció |
|---|---|---|---|
| `fetchItems()` | `/carpeta/get/all` | GET | Obté totes les carpetes de l'usuari |
| `getCarpeta(uuid)` | `/carpeta/get/{uuid}` | GET | Obté el detall d'una carpeta |
| `addCarpeta(data)` | `/carpeta/add` | POST | Crea una carpeta nova |
| `updateCarpeta(uuid, data)` | `/carpeta/update/{uuid}` | PUT | Actualitza una carpeta |
| `deleteCarpeta(uuid)` | `/carpeta/delete/{uuid}` | DELETE | Elimina una carpeta |
| `fetchItemsFromCarpeta(uuid)` | `/carpeta/get/{uuid}/item` | GET | Obté els ítems d'una carpeta |
| `addExistingItem(carpetaUuid, itemUuid)` | `/carpeta/add/{uuid}/item/existing/{uuid}` | POST | Afegeix un ítem existent a la carpeta |
| `removeItem(carpetaUuid, itemUuid)` | `/carpeta/delete/{uuid}/item/{uuid}` | DELETE | Treu un ítem de la carpeta |
| `registrarAcces(uuid)` | `/carpeta/access/{uuid}` | POST | Registra un accés a la carpeta |

## Tipus `Carpeta`

```typescript
type Carpeta = {
  uuid: string;
  nom: string;
  dataCreacio: string;
  dataEditat: string;
  ultimAccess: string;
  comptadorAccess: number;
  favorit: boolean;
  items: Item[];
}
```
