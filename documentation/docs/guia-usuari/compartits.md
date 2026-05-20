# Compartits

La secció **Compartits** mostra les credencials que has compartit amb altres usuaris i les que t'han compartit a tu.

## Pestanyes

**Rebuts** — ítems i carpetes que altres usuaris t'han cedit. Pots accedir-hi però no necessàriament editar-los, depenent del permís rebut.

**Creats** — ítems i carpetes que tu has compartit. Des d'aquí pots modificar permisos o revocar l'accés.

## Compartir un ítem o carpeta

Des de la vista de detall d'un ítem o carpeta, fes clic al botó de compartir de la capçalera. S'obre el modal de compartició, on pots:

- Cercar usuaris per nom o correu electrònic.
- Seleccionar un departament sencer (només administradors).
- Triar el nivell de permís.

Quan confirmes, l'aplicació xifra la clau de dades de l'ítem amb la clau pública de cada destinatari. Cada usuari podrà desxifrar les credencials amb la seva pròpia clau privada.

## Nivells de permís

| Permís | Pot veure | Pot editar | Pot compartir |
|---|---|---|---|
| `LECTURA` | Sí | No | No |
| `ESCRIPTURA` | Sí | Sí | No |
| `ADMINISTRADOR` | Sí | Sí | Sí |

Per canviar el permís d'un compartit creat, localitza'l a la pestanya **Creats** i selecciona el nou nivell. Per revocar l'accés, elimina el compartit; l'usuari deixarà de veure la credencial immediatament.
