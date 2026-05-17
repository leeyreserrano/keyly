# useTimeRefresh i timeUtils

## useTimeRefresh

Fitxer: `src/components/UseTimeRefresh.tsx`

Hook que retorna el timestamp actual (`Date.now()`) i el refresca automàticament cada 60 segons. Els components que mostren temps relatius ("Fa 3 dies") el fan servir per recalcular el valor sense recarregar dades del servidor.

```tsx
const now = useTimeRefresh();
const label = getTimeAgo(item.ultimAcces, now);
```

## timeUtils

Fitxer: `src/utils/timeUtils.ts`

### `getTimeAgo(date, now)`

Converteix una data ISO en una cadena relativa en català: "Fa 3 s", "Fa 5 min", "Fa 2 h", "Fa 4 dies", "Fa 2 setmanes", "Fa 3 mesos", "Fa 1 any".

Accepta dates amb espai en lloc de `T` com a separador (format de Spring Boot), que normalitza internament abans de parsear.

### `formatDate(date)`

Retorna la data en format llarg localitzat en català: "17 de maig de 2026". Usa `Intl.DateTimeFormat` amb locale `ca-ES`.
