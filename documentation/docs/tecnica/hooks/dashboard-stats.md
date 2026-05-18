# useDashboardStats

Fitxer: `src/hooks/useDashboardstats.ts`

Hook que calcula estadístiques de seguretat a partir dels ítems i de les contrasenyes desxifrades.

La lògica es calcula dins d'un `useMemo`.

## Interfície `DashboardStats`

| Camp | Tipus | Descripció |
|---|---|---|
| `totalItems` | `number` | Total d'ítems |
| `pwnedCount` | `number` | Nombre de contrasenyes compromeses |
| `reusedCount` | `number` | Nombre de contrasenyes reutilitzades |
| `weakCount` | `number` | Nombre de contrasenyes febles |
| `secureCount` | `number` | Nombre de contrasenyes segures |
| `avgSecurityScore` | `number` | Puntuació mitjana |
| `recentItems` | `Item[]` | 5 ítems més recents |

## Funció `getPasswordStrengthScore(password)`

Calcula una puntuació de seguretat entre 0 i 100.

### Criteris

| Criteri | Punts |
|---|---|
| Longitud ≥ 8 | +20 |
| Longitud ≥ 12 | +10 |
| Longitud ≥ 16 | +10 |
| Conté minúscules | +10 |
| Conté majúscules | +10 |
| Conté números | +15 |
| Conté símbols | +25 |

La puntuació màxima és 100.

## Contrasenyes reutilitzades

Es crea un objecte `passwordFrequency` amb el nombre de vegades que apareix cada contrasenya.

Una contrasenya es considera reutilitzada quan apareix més d'una vegada.

## Contrasenyes segures i febles

Durant el càlcul:

- `secureCount` augmenta quan la puntuació és igual o superior a 70.
- `weakCount` augmenta quan la puntuació és inferior a 70.

## Puntuació mitjana

La puntuació mitjana es calcula amb:

```ts
Math.round(totalScore / items.length)
```

## Ítems recents

Els ítems es ordenen per dataEditat de forma descendent i es retornen els 5 primers.