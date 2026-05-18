# Header

Fitxer: `src/components/Header.tsx`

Capçalera persistent de cada pàgina. Mostra el títol, l'avatar de l'usuari, el botó de logout i, opcionalment, el botó de retrocés i el botó de compartir.

## Props

| Prop | Tipus | Descripció |
|---|---|---|
| `title` | `string` | Títol de la pàgina |
| `icon` | `ReactNode` | Icona que acompanya el títol |
| `showBackButton` | `boolean` | Mostra el botó de retrocés (per defecte `false`) |
| `onBack` | `() => void` | Callback personalitzat per al botó de retrocés. Si no s'indica, fa `navigate(-1)` |
| `onShare` | `() => void` | Si s'indica, apareix el botó de compartir |

## Avatar i logout

L'avatar mostra la foto de perfil de l'usuari. El tooltip de l'avatar mostra `{nom} · {rolIntern}`. El botó de logout invoca `AuthContext.logout()` i redirigeix a `/`.
