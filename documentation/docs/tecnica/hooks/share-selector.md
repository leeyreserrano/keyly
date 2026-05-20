# useShareSelector

Fitxer: `src/hooks/useShareSelector.ts`

Encapsula tota la lògica de la interfície de compartició: càrrega de dades, cerca, selecció de destinataris i enviament.

## Estat exposat

| Propietat | Descripció |
|---|---|
| `usuaris` | Llista de tots els usuaris disponibles (exclou l'usuari actual) |
| `departaments` | Llista de departaments (només admins) |
| `seleccionats` | Usuaris triats per compartir |
| `departamentSeleccionat` | UUID del departament seleccionat |
| `permisCompartir` | Nivell de permís triat: `LECTURA`, `ESCRIPTURA` o `ADMINISTRADOR` |
| `tab` | Pestanya activa: `usuaris` o `departament` |
| `searchUsuaris` / `searchDept` | Termes de cerca actuals |
| `loadingUsuaris` | Indicador de càrrega inicial |

## Funcions exposades

`toggleSeleccio(usuari)` — afegeix o treu un usuari de la llista de seleccionats.

`handleSelectDepartament(deptUuid)` — selecciona tots els membres del departament indicat i els afegeix a `seleccionats`.

`handleSubmitCompartir(itemUuid, tipusEntitat)` — executa el flux complet: desxifra la DataKey, la torna a xifrar per a cada destinatari i crida `compartitsApi.addCompartit()`.
