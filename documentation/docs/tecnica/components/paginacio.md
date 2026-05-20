# CustomPagination

Fitxer: `src/components/CustomPagination.tsx`

Component de paginació reutilitzable basat en el component `Pagination` de MUI. Rep `count` (nombre total de pàgines), `page` (pàgina actual) i `onChange` (callback amb el nou número de pàgina).

Totes les llistes de l'aplicació mostren 12 elements per pàgina (`ITEMS_PER_PAGE = 12`). La paginació apareix només si el total d'elements supera aquest llindar.
