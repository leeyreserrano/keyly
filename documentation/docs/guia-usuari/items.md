# Ítems

La secció **Ítems** és accessible des del Sidebar. Mostra totes les teves credencials individuals en forma de targetes.

## Llistar i cercar

Cada targeta mostra el títol, el favicon del servei (si té URL) i el temps transcorregut des de l'últim accés. Pots filtrar el contingut amb els tres botons de la barra superior:

- **Últims** — ordenats per data d'últim accés.
- **Més usats** — ordenats per nombre d'accessos.
- **Preferits** — només els marcats com a favorit.

La barra de cerca filtra per títol en temps real. Les targetes que formen part d'alguna carpeta mostren un indicador visual.

Si tens més de 12 ítems apareix la paginació a la part inferior.

## Crear un ítem

Fes clic a **Afegir nou** i selecciona **Credencial**. El formulari conté:

| Camp | Obligatori | Descripció |
|---|---|---|
| Títol | Sí | Nom que identifica la credencial |
| Nom d'usuari | No | Usuari o correu associat al servei |
| Contrasenya | No | Es xifra automàticament en desar |
| URL | No | Adreça web del servei |
| Notes | No | Text lliure |

La icona de daus al costat del camp de contrasenya obre el **generador de contrasenyes**, on pots triar la longitud i els tipus de caràcters.

Durant la creació pots seleccionar una carpeta on s'inclourà l'ítem, i compartir-lo directament amb altres usuaris.

Quan deses, la contrasenya es xifra al navegador amb AES-GCM. Mai s'envia en text pla al servidor.

## Veure i editar un ítem

Fes clic sobre qualsevol targeta per obrir el detall. Des d'aquí pots veure tots els camps, copiar la contrasenya i navegar a la URL.

Per editar, fes clic al botó de llapis. La contrasenya es mostra emmascarada per defecte; la icona d'ull la revela. En desar, la contrasenya es torna a xifrar amb un nou IV.

!!! note
    Si l'ítem té compartits actius, els canvis seran visibles per a tots els qui tinguin permís d'escriptura o superior.

## Duplicar un ítem

Des de la vista de detall pots duplicar un ítem per crear una còpia independent. La còpia no hereta els compartits de l'original.

## Eliminar un ítem

Fes clic a la icona de paperera de la targeta. Apareix un diàleg de confirmació. L'eliminació és permanent i no es pot desfer. Si l'ítem forma part d'una carpeta, s'elimina de la carpeta però la carpeta es manté.
