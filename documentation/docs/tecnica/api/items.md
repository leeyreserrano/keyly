# itemsapi.ts

Fitxer: `src/api/itemsapi.ts`

## Mètodes de `itemsApi`

| Mètode | Endpoint | Mètode HTTP | Descripció |
|---|---|---|---|
| `fetchItems()` | `/item/get/all` | GET | Obté tots els ítems de l'usuari |
| `getItem(uuid)` | `/item/get/{uuid}` | GET | Obté el detall d'un ítem |
| `addItem(data)` | `/item/add` | POST | Crea un ítem nou |
| `updateItem(uuid, data)` | `/item/update/{uuid}` | PUT | Actualitza un ítem |
| `deleteItem(uuid)` | `/item/delete/{uuid}` | DELETE | Elimina un ítem |
| `registrarAcces(uuid)` | `/item/access/{uuid}` | POST | Registra un accés i actualitza el comptador |

## Tipus principals

### `Item`

```typescript
type Item = {
  uuid: string;
  titol: string;
  nomUsuari: string;
  contrasenya: string;       // xifrada en base64
  iv: string;                // IV de l'AES-GCM en base64
  encryptedDataKey: DataKey | null;
  url: string;
  notes?: string;
  dataCreacio: string;
  dataEditat: string;
  ultimAcces: string;
  comptadorAccess: number;
  dinsCarpeta: boolean;
  favorit: boolean;
}
```

### `DataKey`

```typescript
type DataKey = {
  uuid: string;
  encryptedDataKey: string;  // DataKey xifrada amb RSA-OAEP en base64
}
```

### `ItemPayload`

Subconjunt d'`Item` usat per a creació i actualització. Inclou els camps xifrats (`contrasenya`, `iv`) i la DataKey xifrada per a cada destinatari.
