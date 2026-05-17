# Configuració i perfil

La secció **Configuració** és accessible des del Sidebar. Conté pestanyes que varien segons el rol de l'usuari.

## Perfil

Pots actualitzar la foto de perfil, el nom d'usuari i el correu electrònic. Els canvis es desen de forma individual.

## Canviar la contrasenya mestra

La contrasenya mestra protegeix la teva clau privada RSA. Per canviar-la:

1. Introdueix la contrasenya actual per verificar la identitat.
2. Introdueix la nova contrasenya i confirma-la.
3. L'aplicació deriva una nova clau criptogràfica i re-xifra la clau privada al navegador.
4. El servidor només rep la clau privada re-xifrada, mai la contrasenya en text pla.

!!! warning "Atenció"
    Si perds la nova contrasenya mestra, les teves credencials xifrades no es podran recuperar.

## Administració d'usuaris

Visible per a usuaris amb rol `CAP` o `ADMIN`. Permet crear usuaris, editar les seves dades i assignar rol, sucursal i departament.

| Camp | Descripció |
|---|---|
| Nom | Nom visible |
| Correu | Identificador d'accés |
| Rol | `USUARI`, `CAP` o `ADMIN` |
| Sucursal | Oficina assignada |
| Departament | Departament de l'organigrama |
| Pot administrar | Permís per gestionar compartits de tota l'organització |

En crear un usuari, l'aplicació genera automàticament el seu parell de claus RSA.

## Departaments i sucursals

Visible per a usuaris amb rol `CAP` o `ADMIN`.

Els **departaments** agrupen usuaris per àrees funcionals. Quan es comparteix una credencial amb un departament, tots els membres reben accés automàticament.

Les **sucursals** representen ubicacions físiques o delegacions de l'empresa i s'assignen a usuaris per facilitar la segmentació organitzativa.
