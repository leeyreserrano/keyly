# CredentialCard

Fitxer: `src/components/CredentialCard.tsx`

Targeta reutilitzable que representa un ítem o una carpeta. S'usa al tauler, a la llista d'ítems, a la vista de carpeta i a la llista de compartits.

## Props principals

| Prop | Tipus | Descripció |
|---|---|---|
| `uuid` | `string` | Identificador de l'entitat |
| `titol` | `string` | Títol de la credencial o carpeta |
| `dataEditat` | `string` | Data de l'última modificació |
| `ultimAcces` | `string` | Data de l'últim accés |
| `url` | `string?` | URL del servei (per obtenir el favicon) |
| `esCarpeta` | `boolean` | Canvia la icona i el comportament |
| `dinsCarpeta` | `boolean` | Mostra un indicador si l'ítem pertany a una carpeta |
| `favorit` | `boolean` | Estat actual de favorit |
| `onClick` | `() => void` | Acció en fer clic a la targeta |
| `onEdit` | `() => void` | Acció del botó d'edició |
| `onDelete` | `() => void` | Acció del botó d'eliminació |
| `onAccess` | `(uuid, esCarpeta) => void` | Callback que es crida quan s'accedeix a la targeta |

## Favicon

Obté el favicon del servei via `https://icons.duckduckgo.com/ip3/{domini}.ico`. Si la URL no és vàlida o la imatge no carrega, es mostra la icona genèrica de credencial.

## Temps relatiu

El temps des de l'últim accés es mostra en format relatiu ("Fa 3 dies") usant `getTimeAgo` de `timeUtils`. El valor s'actualitza automàticament gràcies al hook `useTimeRefresh`.

## Favorit

El botó de favorit fa la crida a l'API directament des de la targeta i actualitza l'estat local sense recarregar la llista.
