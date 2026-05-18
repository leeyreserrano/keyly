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
