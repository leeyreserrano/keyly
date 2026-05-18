# Extensió de navegador — Documentació tècnica

## Stack i framework

L'extensió es construeix amb **Plasmo** (v0.90.5), un framework que genera extensions de Chrome (Manifest V3) a partir de React i TypeScript. El bundler és Plasmo, l'estil és Tailwind CSS i el router és React Router DOM v7 amb `MemoryRouter`.

## Estructura de fitxers

```
keyly-extension/
├── contents/
│   └── content.ts          Content script injectat a totes les pàgines web
├── src/
│   ├── popup.tsx            Punt d'entrada del popup (router + rutes)
│   ├── api/
│   │   ├── auth-service.ts  Login, derivació de clau, helpers HTTP
│   │   ├── item-service.ts  CRUD d'ítems
│   │   ├── carpeta-service.ts CRUD de carpetes
│   │   ├── compartit-service.ts Compartits + xifrat de DataKey
│   │   └── user-service.ts  Llista d'usuaris per compartir
│   ├── components/
│   │   ├── Layout.tsx       Embolcall comú del popup
│   │   ├── Navbar.tsx       Barra de navegació inferior (Ítems/Carpetes/Compartits)
│   │   ├── Searcher.tsx     Barra de cerca + botó +
│   │   ├── ItemCard.tsx     Llista d'ítems amb desxifrat
│   │   ├── CarpetCard.tsx   Llista de carpetes
│   │   ├── CompartitCard.tsx Llista de compartits
│   │   └── ModalConfimDelete.tsx Modal de confirmació d'eliminació
│   ├── models/
│   │   ├── Carpeta.ts
│   │   ├── Compartit.ts
│   │   ├── CustomPassword.ts
│   │   └── User.ts
│   ├── pages/
│   │   ├── home/            Home, ItemPage, CarpetaPage, CompartitPage
│   │   ├── item/            Item (detall) i EditItem
│   │   ├── create_item/     NewItem
│   │   ├── create_folder/   NewFolder
│   │   ├── folder/          CarpetaDetall
│   │   └── login/           Login
│   └── utils/
│       ├── AuthGate.tsx     Redirecció automàtica login/home
│       ├── autofill.ts      Lògica d'autofill via chrome.tabs
│       └── crypto-utils.ts  Xifrat AES-GCM i comprovació d'expiració JWT
```

## Rutes del popup

Definides a `popup.tsx` amb `MemoryRouter`:

| Ruta | Component | Descripció |
|---|---|---|
| `/` | `AuthGate` | Redirigeix a `/home` o `/login` segons token |
| `/login` | `Login` | Formulari d'accés |
| `/home` | `Home` | Llista d'ítems, carpetes i compartits |
| `/item/:id` | `Item` | Detall d'un ítem |
| `/item/edit/:id` | `EditItem` | Edició d'un ítem |
| `/carpeta/:id` | `CarpetaDetall` | Interior d'una carpeta |
| `/compartit` | `CompartitPage` | Llista de compartits |
| `/create/item` | `NewItem` | Formulari de creació d'ítem |
| `/create/folder` | `NewFolder` | Formulari de creació de carpeta |

Les rutes estan protegides per `ProtectedRoute`, que comprova l'existència i expiració del token JWT a `localStorage`.

## Autenticació i claus criptogràfiques

El flux de login a `auth-service.ts` és idèntic al de la web:

1. `POST /auth/login` → rep `token`, `kdfSalt`, `encryptedPrivateKey`, `usuari`.
2. `deriveKey(password, kdfSalt)` — PBKDF2 + SHA-256, 310.000 iteracions → clau derivada en base64.
3. `decryptPrivateKey(encryptedPrivateKey, derivedKey)` — AES-GCM → clau privada en base64.
4. Es desa `jwtToken`, `publicKey` i `privateKey` a `localStorage`.

A diferència de la versió web, l'extensió desa la clau privada a `localStorage` (no a `sessionStorage`) perquè el popup es destrueix i es torna a crear a cada obertura.

## Content script i autofill

`contents/content.ts` s'injecta a totes les pàgines web (`matches: ["<all_urls>"]`) i escolta missatges de tipus `AUTOFILL`. En rebre'n un, cerca el primer camp de tipus `text`/`email` i el primer camp de tipus `password` al DOM i els omple via `.value` + `dispatchEvent`.

`src/utils/autofill.ts` és la funció cridada des del popup:

1. Obté la pestanya activa amb `chrome.tabs.query`.
2. Comprova que la URL no sigui una pàgina del sistema (`chrome://`, `about:`, etc.).
3. Envia un missatge `AUTOFILL` al content script via `chrome.tabs.sendMessage`.
4. Si el content script no és disponible (pàgina sense inject), usa `chrome.scripting.executeScript` com a fallback.

## Criptografia

`src/utils/crypto-utils.ts` exposa:

`encryptItemPassword(contrasenya, rawDataKey)` — xifra una contrasenya amb AES-GCM usant la DataKey en cru. Retorna `{ contrasenyaEncriptada, ivB64 }`.

`isTokenExpired(token)` — descodifica el payload JWT i comprova el camp `exp` amb un marge de 60 segons.

El desxifrat dels ítems es fa a `ItemCard.tsx` amb les funcions `decryptItem` i `decryptItemWithRawKey`, que recuperen la clau privada de `localStorage`, la importen com a `CryptoKey` RSA-OAEP i la usen per desxifrar la DataKey de cada ítem.

## Permisos del manifest

```json
"permissions": ["scripting"],
"host_permissions": ["https://*/*", "http://*/*"]
```

El permís `scripting` és necessari per al fallback d'autofill via `chrome.scripting.executeScript`. `host_permissions` sobre totes les URLs permet al content script injectar-se a qualsevol pàgina.

## Construcció i empaquetament

```bash
pnpm dev       # Servidor de desenvolupament amb hot-reload
pnpm build     # Build de producció a build/chrome-mv3-prod
pnpm package   # Genera el .zip per pujar a la Chrome Web Store
```

Per carregar la build de desenvolupament a Chrome: `chrome://extensions` → Mode desenvolupador → Carregar descomprimida → `build/chrome-mv3-dev`.
