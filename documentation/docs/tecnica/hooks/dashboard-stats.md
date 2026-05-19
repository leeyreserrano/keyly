# useDashboardStats

Fitxer: `src/hooks/useDashboardstats.ts`

Hook que calcula les estadístiques de seguretat a partir de la llista d'ítems desxifrats. Tota la lògica és al client, dins d'un `useMemo`.

## Interfície `DashboardStats`

| Camp | Tipus | Descripció |
|---|---|---|
| `totalItems` | `number` | Total d'ítems analitzats |
| `compromisedCount` | `number` | Contrasenyes que coincideixen amb la llista de contrasenyes comunes |
| `reusedCount` | `number` | Contrasenyes idèntiques usades en més d'un ítem |
| `weakCount` | `number` | Contrasenyes amb puntuació inferior a 40 |
| `secureCount` | `number` | Contrasenyes amb puntuació igual o superior a 70 |
| `avgSecurityScore` | `number` | Puntuació mitjana (0–100) |
| `recentItems` | `Item[]` | Darrers 5 ítems accedits |

## Criteris de puntuació (`evaluatePasswordStrength`)

| Criteri | Punts |
|---|---|
| Longitud ≥ 12 caràcters | +30 |
| Longitud ≥ 8 caràcters | +15 |
| Conté majúscules | +20 |
| Conté números | +20 |
| Conté símbols | +30 |

La puntuació màxima és 100. Les contrasenyes amb longitud inferior a 8 no sumen punts per longitud.

## Detecció de contrasenyes comunes

Es compara la contrasenya (en minúscules) contra una llista de 10 contrasenyes molt usades: `123456`, `password`, `qwerty`, `admin`, etc.

## Integració amb HIBP

A partir d'aquesta versió, el comptador `compromisedCount` ja no es basa en una llista local de contrasenyes comunes, sinó en consultes reals a la base de dades HIBP via `isPasswordPwned` de `pwnedUtils.ts`.

`isPasswordPwned(plainPassword)` calcula el hash SHA-1 de la contrasenya al navegador, n'extreu el prefix de 5 caràcters i consulta `pwnedApi.checkPassword(prefix, suffix)`. Compara el hash complet amb els resultats retornats pel servidor. Si hi ha coincidència, la contrasenya es considera compromesa.

La pàgina `Pwned` (`/Pwned`) mostra únicament els ítems compromesos i permet eliminar-los directament.
