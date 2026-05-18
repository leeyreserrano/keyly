# Estadístiques de seguretat

Fitxer: `src/pages/Stadistics.tsx`

La secció **Estadístiques** mostra informació sobre la seguretat de les credencials desxifrades de l'usuari.

## Càrrega de dades

En carregar la pàgina:

1. Es recuperen els ítems amb `itemsApi.fetchItems()`.
2. Es desxifren les contrasenyes utilitzant:
   - `rsaDecrypt`
   - `decryptPasswordWithDataKey`
3. Es comprova si les contrasenyes estan compromeses amb `isPasswordPwned`.

Les contrasenyes desxifrades es guarden en un `Map<string, string>` i els UUID compromesos en un `Set<string>`.

## Targetes d'estadístiques

Es mostren quatre targetes principals:

| Targeta | Descripció |
|---|---|
| Total | Nombre total d'ítems |
| Compromeses | Contrasenyes detectades com vulnerades |
| Reutilitzades | Credencials amb contrasenyes repetides |
| Puntuació mitjana | Valor de seguretat global |

## Puntuació de seguretat

La puntuació mitjana es calcula amb `useDashboardStats`.

El color varia segons el valor:

- Verd: puntuació igual o superior a 70.
- Lila: puntuació entre 40 i 69.
- Vermell: puntuació inferior a 40.

També es mostra:

- Barra de progrés.
- Nombre de contrasenyes segures.
- Nombre de contrasenyes febles.
- Nombre de contrasenyes compromeses.

## Gràfic de distribució

Es mostra un gràfic circular amb:

- Contrasenyes segures.
- Contrasenyes febles.
- Contrasenyes compromeses.

El gràfic utilitza components de `recharts`:

- `PieChart`
- `Pie`
- `Cell`
- `Legend`
- `ResponsiveContainer`

## Alertes de seguretat

Quan existeixen contrasenyes compromeses o reutilitzades es mostren components `SecurityAlert`.

Les alertes permeten navegar a:

- `/Items`
- `/Duplicats`

## Ítems recents

Es mostren els 5 ítems més recents segons `dataEditat`.

Cada element mostra:

- Inicial del títol.
- Títol.
- Nom d'usuari.
- Temps relatiu amb `getTimeAgo`.
- Indicador de contrasenya compromesa si correspon.

En prémer un ítem es navega a:

```txt
/Item
```