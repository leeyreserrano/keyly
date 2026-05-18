# Backend — Visió general

El backend de Keyly és una API REST construïda amb **Spring Boot 4.0.6** i **Java 21**. Gestiona tota la lògica de negoci, la persistència i la seguretat. Les contrasenyes dels ítems arriben ja xifrades des del client i el servidor les emmagatzema sense desxifrar-les mai.

## Stack

| Capa | Tecnologia |
|---|---|
| Framework | Spring Boot 4.0.6 |
| Llenguatge | Java 21 |
| Persistència principal | Spring Data JPA + MySQL 8.0 |
| Persistència secundària | Spring Data Cassandra 5.0.8 (base de dades HIBP) |
| Seguretat | Spring Security + JWT (jjwt 0.13.0) |
| Mappers | MapStruct 1.5.5 |
| Generació de codi | Lombok |
| Documentació API | SpringDoc OpenAPI 3 (Swagger UI) |
| Build | Maven |

## Estructura de paquets

```
com.keyly/
├── config/         Configuració de Spring (OpenAPI, Security)
├── controller/     Controladors REST (un per recurs)
├── exception/      Excepcions personalitzades i gestor global
├── mapper/         Interfaces MapStruct (entitat ↔ request/response)
├── model/
│   ├── enums/      Enumerats (Permisos, TipusEntitat, RolIntern)
│   ├── request/    DTOs d'entrada
│   └── response/   DTOs de sortida
├── repo/           Repositoris JPA i Cassandra
├── security/       Filtre JWT, JwtUtils, SecurityConfig
└── service/        Lògica de negoci (un per recurs)
```

## Context path i Swagger

Totes les rutes de l'API tenen el prefix `/api` (configurat via `server.servlet.context-path`).

La documentació interactiva Swagger UI és accessible a `/api/swagger-ui/index.html` quan el servidor està actiu.

## Rols i autorització

| Rol | Descripció |
|---|---|
| `ADMIN` | Accés total. Pot veure tots els recursos de tots els usuaris |
| `CAP` | Pot crear i gestionar usuaris i recursos de la seva sucursal |
| `USUARI` | Accés als seus propis recursos i als compartits que ha rebut |

L'autorització s'aplica a cada endpoint amb `@PreAuthorize("hasRole(...)")` o `hasAnyRole(...)`.

## Bases de dades

**MySQL** — base de dades principal amb totes les entitats del domini.

**Cassandra** — emmagatzema les contrasenyes comprometides del projecte HIBP (*Have I Been Pwned*) per consultes de seguretat des del client. El keyspace és `hibp` i la connexió es configura a `application.yml`.
