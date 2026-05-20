# client.ts — HTTP base

Fitxer: `src/api/client.ts`

Centralitza tota la comunicació HTTP amb el backend. Tots els fitxers d'API importan les seves funcions en lloc de fer `fetch` directament.

## Variables d'entorn

La URL base del backend es llegeix de `import.meta.env.VITE_API_BASE`. Cal definir-la al fitxer `.env` o a les variables d'entorn del servidor de desplegament.

```
VITE_API_BASE=https://api.keyly.exemple.com
```

## `apiRequest<T>(endpoint, options)`

Funció genèrica per a totes les peticions JSON. Gestiona automàticament:

- Lectura del token JWT des de `localStorage` o `sessionStorage`.
- Comprovació de l'expiració del token abans d'enviar la petició.
- Afegit de la capçalera `Authorization: Bearer {token}`.
- Gestió del codi HTTP 401: neteja la sessió i redirigeix a `/`.
- Parsejat de la resposta JSON, retornant `null` si el cos és buit.

L'opció `_skipLogoutOn401` permet suprimir el redireccionament en casos concrets (per exemple, al verificar la contrasenya actual abans de canviar-la).

## `apiMultipartRequest<T>(endpoint, formData, token?)`

Variant per a peticions `multipart/form-data`. No afegeix la capçalera `Content-Type` perquè el navegador la genera automàticament amb el boundary correcte. S'usa per pujar la imatge de perfil.

## `apiImageRequest(path)`

Descarrega una imatge protegida per token i retorna una URL d'objecte local (`blob:`) per mostrar-la al navegador sense exposar la URL real de l'API.

## Expiració del token

`isTokenExpired(token)` descodifica el payload JWT (base64) i compara el camp `exp` amb el temps actual. Si ha expirat, `handleUnauthorized()` buida `localStorage` i `sessionStorage` i redirigeix a la pàgina de login.
