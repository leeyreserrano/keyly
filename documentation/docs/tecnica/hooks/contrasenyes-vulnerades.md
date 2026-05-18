# Contrasenyes vulnerades

Fitxer: `src/utils/pwnedUtils.ts`

La comprovació de contrasenyes vulnerades es fa localment generant un hash SHA-1 de la contrasenya i consultant el backend amb una part del hash.

## Funció `isPasswordPwned(plainPassword)`

Rep una contrasenya en text pla i retorna un `boolean` indicant si la contrasenya apareix en una base de dades de contrasenyes compromeses.

## Procés

1. La contrasenya es converteix a bytes amb `TextEncoder`.
2. Es genera un hash SHA-1 utilitzant `crypto.subtle.digest`.
3. El hash es transforma a hexadecimal en majúscules.
4. Es divideix en:
   - `prefix`: primers 5 caràcters.
   - `suffix`: caràcters del 5 al 7.
5. Es fa una petició al backend amb el prefix i el suffix.
6. Es comprova si algun hash retornat coincideix amb el hash complet.

## Integració amb `pwnedApi`

La funció utilitza:

```ts
pwnedApi.checkPassword(prefix, suffix)
```

que fa una petició:

POST /utils/pwned/password/{prefix}/{suffix}