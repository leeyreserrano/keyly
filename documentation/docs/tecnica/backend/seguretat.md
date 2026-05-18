# Seguretat i JWT

## Configuració de Spring Security

`SecurityConfig` defineix la cadena de filtres:

- Totes les peticions a `/auth/**` són públiques.
- La resta requereix autenticació amb JWT.
- CSRF desactivat (API stateless).
- CORS configurat per permetre les origines del frontend.

El filtre `JwtAuthenticationFilter` intercepta cada petició, extreu el token del header `Authorization: Bearer`, el valida amb `JwtUtils` i carrega el principal a `SecurityContextHolder`. El principal és l'UUID de l'usuari, que els controladors usen per filtrar recursos.

## JwtUtils

Gestiona la generació i validació dels tokens:

- Firma amb HS256 usant la clau secreta definida a `jwt.secret` (propietat de configuració).
- Expiració configurable via `jwt.expiration-ms` (per defecte 30 minuts: 1.800.000 ms).
- El payload inclou `sub` (UUID de l'usuari) i `roles` (llista de rols Spring Security).

## Gestió d'excepcions

`GestorGlobalExcepcions` captura les excepcions personalitzades i retorna respostes HTTP estructurades:

| Excepció | HTTP |
|---|---|
| `EntitatNoTrobadaException` | 404 Not Found |
| `UsuariException` | 400 Bad Request |
| `CorreuException` | 400 Bad Request |
| `DominiInvalidException` | 403 Forbidden |
| `CompartitException` | 400 Bad Request |
| `ImageException` | 400 Bad Request |
| `GeneracioContrasenyaException` | 500 Internal Server Error |

El cos de la resposta segueix el format `ErrorResponse { message, status, timestamp }`.

## Emmagatzematge de contrasenyes mestres

Les contrasenyes mestres dels usuaris s'emmagatzemen al camp `contrasenya_master` de la taula `Usuaris` amb hash BCrypt (cost 12). El servidor no té accés a la contrasenya en text pla ni a les claus criptogràfiques dels usuaris.
