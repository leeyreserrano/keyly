# Configuració i perfil

La secció **Configuració** és accessible des del Sidebar. Les pestanyes disponibles varien segons el rol de l'usuari.

## Perfil

Disponible per a tots els rols. Mostra l'avatar, el nom, el correu i el rol intern de l'usuari. Des d'aquí pots:

- Canviar la foto de perfil fent clic a la icona de llapis sobre l'avatar.
- Canviar la contrasenya mestra omplint els camps de contrasenya actual, nova contrasenya i confirmació.

En canviar la contrasenya mestra, l'aplicació genera un nou parell de claus RSA i re-xifra la clau privada amb la nova contrasenya al navegador. El servidor mai rep la contrasenya en text pla.

!!! warning "Atenció"
    Si perds la nova contrasenya mestra, les teves credencials xifrades no es podran recuperar.

## Ítems (CAP i ADMIN)

Mostra tots els ítems que has compartit, agrupats per ítem i receptor. Permet veure qui té accés a cada credencial, afegir nous compartits i revocar l'accés per ítem.

## Carpetes (CAP i ADMIN)

Igual que la pestanya d'ítems però per a carpetes compartides. Permet gestionar qui té accés a cada carpeta i amb quin nivell de permís.

## Usuaris (ADMIN)

Permet crear, editar i eliminar usuaris de l'organització. En crear un usuari, l'aplicació genera automàticament el seu parell de claus RSA. Els camps disponibles són nom, correu, rol intern, sucursal, departament, rol organitzatiu i contrasenya inicial.

## Departaments (ADMIN)

Gestió dels departaments de l'organització. Cada departament pertany a una sucursal. Permet crear, editar i eliminar departaments.

## Sucursals (ADMIN)

Gestió de les sucursals de l'organització. Cada sucursal té nom, adreça, telèfon i correu. Des d'aquí també es pot configurar la política de dominis de la sucursal: permetre tots els dominis o restringir-los a la llista de dominis aprovats, i definir els dies d'expiració de contrasenyes.

## Dominis (ADMIN)

Llista de dominis de correu autoritzats per a cada sucursal (per exemple, `@keyly.com`). Permet afegir i eliminar dominis per controlar quins correus poden registrar-se.

## Rols (ADMIN)

Gestió dels rols organitzatius personalitzats per sucursal. Distints del `rol_intern` del sistema (`ADMIN`, `CAP`, `USUARI`), aquests rols representen càrrecs o funcions dins de l'empresa.
