# Desplegament amb Docker

El projecte es desplega amb Docker Compose. Tots els serveis es defineixen a `docker-compose.yml` i comparteixen una xarxa interna `network` de tipus bridge.

## Serveis

### `backend` (keyly-springboot)

Construeix la imatge des de `./backend/api/Dockerfile`. Exposa el port configurat a `SPRINGBOOT_PORT_LOCAL` i munta el volum `uploads` per als avatars d'usuari a `/app/uploads`.

### `db` (keyly-mysql)

Imatge oficial `mysql:8.0`. Els scripts SQL de `db/mysql-init/` s'executen en ordre en el primer arrencament:

1. `01-init-user.sql` — crea l'usuari de base de dades.
2. `02-init-tables.sql` — crea totes les taules.
3. `03-init-trigger.sql` — crea els triggers automàtics.
4. `04-init-inserts.sql` — insereix dades inicials de prova.

Les dades persisten al volum `mysql-data`.

### `cassandra` (keyly-cassandra)

Imatge `cassandra:5.0.8`. Emmagatzema la base de dades HIBP. Les dades persisten al volum `cassandra-data`. La configuració de memòria és `MAX_HEAP_SIZE=1G` i `HEAP_NEWSIZE=256M`.

### `nginx` (keyly-nginx)

Construeix la imatge des de `./frontend-react/Dockerfile`, que genera el build de React i el serveix amb Nginx. Munta la configuració SSL de `./nginx/conf.d` i actua de reverse proxy cap al backend:

- `/*` → serveix l'SPA React des de `/usr/share/nginx/html`.
- `/api/*` → fa proxy cap a `http://backend:8080`.

Nginx escolta en HTTPS (port 443) amb TLS 1.2/1.3.

## Fitxer `.env`

| Variable | Descripció |
|---|---|
| `MYSQL_ROOT_PASSWORD` | Contrasenya root de MySQL |
| `MYSQL_DATABASE` | Nom de la base de dades (`keyly`) |
| `MYSQL_USER` / `MYSQL_PASSWORD` | Credencials de l'usuari de l'aplicació |
| `MYSQL_PORT_LOCAL` / `MYSQL_PORT_DOCKER` | Port de MySQL (3306) |
| `CASSANDRA_PORT_LOCAL` / `CASSANDRA_PORT_DOCKER` | Port de Cassandra (9042) |
| `CASSANDRA_USER` / `CASSANDRA_PASSWORD` | Credencials de Cassandra |
| `SPRINGBOOT_PORT_LOCAL` | Port extern del backend (8082) |
| `SPRINGBOOT_PORT_DOCKER` | Port intern del backend (8080) |
| `NGINX_PORT_LOCAL` | Port extern del frontend (8081) |
| `NGINX_PORT_DOCKER` | Port intern de Nginx (443) |

!!! warning "Seguretat"
    El fitxer `.env` conté credencials sensibles. No s'ha d'incloure mai en un repositori públic.

## Arrencada

```bash
docker compose up -d
```

En el primer arrencament, MySQL executa els scripts d'inicialització automàticament. Cassandra pot trigar uns minuts a estar disponible; el backend reintenta la connexió fins que Cassandra estigui a punt.

## Ports exposats per defecte

| Servei | Port local | Descripció |
|---|---|---|
| Frontend (Nginx/HTTPS) | 8081 | Aplicació web |
| Backend (Spring Boot) | 8082 | API REST + Swagger UI |
| MySQL | 3306 | Base de dades principal |
| Cassandra | 9042 | Base de dades HIBP |

## SSL

El certificat i la clau privada de Nginx es troben a `nginx/conf.d/certificate.crt` i `nginx/conf.d/server.key`. Per a producció cal substituir-los per un certificat vàlid (Let's Encrypt o un certificat corporatiu).
