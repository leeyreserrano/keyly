# Layout i Sidebar

## Layout

Fitxer: `src/components/Layout.tsx`

Component embolcall de totes les pàgines protegides. Disposa el `Sidebar` a l'esquerra i el contingut de la pàgina a la dreta en un `Stack` horitzontal que ocupa el 100% del viewport.

```tsx
<Layout>
  <PàginaQualsevol />
</Layout>
```

## Sidebar

Fitxer: `src/components/Sidebar.tsx`

Navegació lateral persistent. Es pot col·lapsar (80px) o expandir (260px) fent clic a la icona del logo. L'estat obert/tancat és local al component.

### Elements de navegació

| Ruta | Icona | Clau i18n |
|---|---|---|
| `/home` | HomeRoundedIcon | `nav.home` |
| `/stadistics` | BarChartRoundedIcon | `nav.stats` |
| `/items` | VpnKeyRoundedIcon | `nav.items` |
| `/carpetes` | FolderOutlinedIcon | `nav.folders` |
| `/compartits` | PeopleAltOutlinedIcon | `nav.shared` |
| `/settings` | EditNoteOutlinedIcon | `nav.settings` |

L'element actiu es detecta comparant `location.pathname` amb el `path` de cada element. Rep un fons lleugerament diferent per indicar l'estat actiu.

### Selector d'idioma

Al peu del Sidebar hi ha un `Select` de MUI que invoca `i18n.changeLanguage(lang)` en canviar el valor. Mostra les opcions `ca`, `es` i `en`.

## Nous components de configuració

La pàgina `UserConfig` s'ha reestructurat amb pestanyes. Cada pestanya és un component independent:

| Component | Fitxer | Rol mínim | Descripció |
|---|---|---|---|
| `PerfilTab` | `components/PerfilTab.tsx` | Tots | Dades personals i canvi de contrasenya mestra |
| `ItemsTab` | `components/ItemsTab.tsx` | CAP | Compartits d'ítems creats, agrupats per receptor/departament |
| `CarpetesTab` | `components/CarpetesTab.tsx` | CAP | Compartits de carpetes creats |
| `UsuarisTab` | `components/UsuarisTab.tsx` | ADMIN | CRUD d'usuaris |
| `DepartamentsTab` | `components/DepartamentsTab.tsx` | ADMIN | CRUD de departaments |
| `SucursalsTab` | `components/SucursalsTab.tsx` | ADMIN | CRUD de sucursals + configuració de dominis i expiració |
| `DominiTab` | `components/DominiTab.tsx` | ADMIN | CRUD de dominis per sucursal |
| `RolsTab` | `components/RolsTab.tsx` | ADMIN | CRUD de rols organitzatius |

`SucursalsTab` inclou, per a cada sucursal, un panell expandible amb la configuració (`Config`): toggle per permetre tots els dominis i selector de dies d'expiració.

## SecurityAlert

`components/SecurityAlert.tsx` — component d'alerta reutilitzable que mostra una `Alert` de MUI amb títol, descripció i un botó d'acció. Rep les props `type` (`'compromised'` o `'reused'`), `count` i `onReview`. S'usa a la pàgina d'Estadístiques per navegar a `/Pwned` o filtrar reutilitzades.
