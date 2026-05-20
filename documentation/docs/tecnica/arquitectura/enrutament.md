# Enrutament i navegació

L'aplicació fa servir React Router v7 amb una estructura de dos nivells definida a `App.tsx`.

## Ruta pública

| Ruta | Component | Descripció |
|---|---|---|
| `/` | `Login` | Pantalla d'accés (layout split-screen) |

## Rutes protegides

Totes les rutes internes estan embolcallades per `ProtectedRoute`, que comprova el token JWT abans de renderitzar. Si el token ha expirat o no existeix, redirigeix a `/`.

| Ruta | Component | Descripció |
|---|---|---|
| `/home` | `Home` | Tauler principal |
| `/Items` | `Items` | Llistat de credencials |
| `/Item` | `Item` | Detall d'una credencial |
| `/AddItem` | `AddItem` | Formulari de creació |
| `/EditItem` | `EditItem` | Formulari d'edició |
| `/Duplicats` | `Duplicats` | Duplicar una credencial |
| `/Pwned` | `Pwned` | Credencials compromeses (HIBP) |
| `/ChooseType` | `ChooseType` | Selector ítem o carpeta |
| `/Carpetes` | `Carpetes` | Llistat de carpetes |
| `/Carpeta` | `Carpeta` | Detall d'una carpeta |
| `/AddCarpeta` | `AddCarpeta` | Creació de carpeta |
| `/EditCarpeta` | `EditCarpeta` | Edició de carpeta |
| `/Compartits` | `Compartits` | Credencials compartides |
| `/Stadistics` | `Stadistics` | Estadístiques de seguretat |
| `/Settings` | `UserConfig` | Configuració i perfil |

Qualsevol ruta no reconeguda redirigeix a `/home`.

## Pas d'estat entre pàgines

La navegació entre pàgines passa dades via `location.state` de React Router (per exemple, l'`uuid` d'un ítem quan s'obre el seu detall). No s'usen paràmetres a la URL per evitar exposar identificadors al historial del navegador.
