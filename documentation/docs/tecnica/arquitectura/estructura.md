# Estructura de directoris

```
src/
├── api/            Funcions de comunicació amb el backend (una per recurs)
├── assets/         Recursos estàtics: logo SVG
├── components/     Components React reutilitzables
├── context/        Contexts globals: autenticació i criptografia
├── crypto/         Lògica de Web Crypto API
├── hooks/          Hooks personalitzats
├── locales/        Fitxers de traducció JSON (ca / es / en)
├── pages/          Pàgines de l'aplicació, organitzades per domini
│   ├── Carpetes/
│   ├── Compartit/
│   └── Items/
├── routes/         Guard de ruta protegida
├── theme/          Configuració del tema Material UI
└── utils/          Funcions utilitàries generals
```

## Convencions

Els fitxers de pàgina segueixen PascalCase (`AddItem.tsx`, `Carpetes.tsx`). Els fitxers d'API segueixen el patró `{recurs}api.ts` en minúscules. Els components reutilitzables estan directament a `components/` sense subcarpetes.

## Punt d'entrada

`main.tsx` munta l'arbre de React. `App.tsx` defineix tots els proveïdors globals (`AuthProvider`, `CryptoProvider`) i el router amb totes les rutes de l'aplicació.
