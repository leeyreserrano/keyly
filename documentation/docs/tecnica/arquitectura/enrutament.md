# Enrutament i navegació

L'aplicació fa servir React Router v7 amb una estructura de dos nivells definida a `App.tsx`.

## Ruta pública

| Ruta | Component | Descripció |
|---|---|---|
| `/` | `Login` | Pantalla d'accés |

## Rutes protegides

Totes les rutes internes estan embolcallades per `ProtectedRoute`, que comprova el token JWT abans de renderitzar. Si el token ha expirat o no existeix, redirigeix a `/`.

| Ruta | Component | Descripció |
|---|---|---|
| `/home` | `Home` | Tauler principal |
| `/items` | `Items` | Llistat de credencials |
| `/item` | `Item` | Detall d'una credencial |
| `/additem` | `AddItem` | Formulari de creació |
| `/edititem` | `EditItem` | Formulari d'edició |
| `/duplicats` | `Duplicats` | Duplicar una credencial |
| `/choosetype` | `ChooseType` | Selector ítem o carpeta |
| `/carpetes` | `Carpetes` | Llistat de carpetes |
| `/carpeta` | `Carpeta` | Detall d'una carpeta |
| `/addcarpeta` | `AddCarpeta` | Creació de carpeta |
| `/editcarpeta` | `EditCarpeta` | Edició de carpeta |
| `/compartits` | `Compartits` | Credencials compartides |
| `/stadistics` | `Stadistics` | Estadístiques de seguretat |
| `/settings` | `UserConfig` | Configuració i perfil |

Qualsevol ruta no reconeguda redirigeix a `/home`.

## Pas d'estat entre pàgines

La navegació entre pàgines passa dades via `location.state` de React Router (per exemple, l'`uuid` d'un ítem quan s'obre el seu detall). No s'usen paràmetres a la URL per evitar exposar identificadors al historial del navegador.
