# Extensió de navegador Keyly

L'extensió de Keyly per a Chrome permet accedir a les teves credencials directament des del navegador i omplir formularis de login de forma automàtica sense haver d'obrir la web.

## Instal·lació

L'extensió es distribueix com a fitxer empaquetat (`.zip` o `.crx`). Per instal·lar-la en mode desenvolupador:

1. Obre Chrome i accedeix a `chrome://extensions`.
2. Activa el **Mode de desenvolupador** (cantonada superior dreta).
3. Fes clic a **Carregar descomprimida** i selecciona la carpeta `build/chrome-mv3-dev` del projecte, o bé **Carregar extensió empaquetada** si tens el fitxer `.crx`.
4. L'extensió apareixerà a la barra d'eines del navegador amb la icona de Keyly.

## Accés i sessió

Fes clic a la icona de Keyly a la barra d'eines per obrir el popup. Si no has iniciat sessió, es mostrarà el formulari de login. Introdueix el correu electrònic corporatiu i la contrasenya mestra i prem **Login**.

La sessió es manté mentre el navegador estigui obert. Tancar i tornar a obrir Chrome requerirà tornar a iniciar sessió.

## Pantalla principal (Home)

Un cop autenticat, veuràs la pantalla principal amb tres seccions:

- **Ítems** — les teves credencials individuals.
- **Carpetes** — les teves carpetes amb ítems.
- **Compartits** — credencials que t'han compartit.

La barra de cerca superior filtra en temps real per títol o nom d'usuari a totes les seccions alhora.

El botó **+** de la barra de cerca desplega un menú per crear un ítem nou o una carpeta nova.

## Veure i copiar una credencial

Fes clic sobre qualsevol ítem de la llista per obrir el seu detall. Des d'aquí pots:

- Veure el títol, la URL, el nom d'usuari i la contrasenya (emmascarada per defecte).
- Fer clic a la icona d'ull per mostrar la contrasenya en text pla.
- Copiar el nom d'usuari o la contrasenya al porta-retalls amb la icona de còpia.
- Anar a la URL del servei fent clic al favicon o al títol.

## Autofill — omplir formularis automàticament

Des de la vista de detall d'un ítem, el botó **Autofill** omple automàticament el formulari de login de la pàgina activa al navegador: insereix el nom d'usuari al camp corresponent i la contrasenya al camp de contrasenya.

L'autofill detecta camps de tipus `text`, `email` i `password`. Si la pàgina no té cap camp reconeixible, o si estàs a una pàgina del sistema de Chrome (`chrome://`, `about:`, etc.), l'autofill mostrarà un avís i no s'executarà.

!!! note "Nota"
    Per que l'autofill funcioni, la pàgina activa ha de ser una pàgina web normal. No funciona a pàgines internes del navegador.

## Crear un ítem

Des del menú **+** del home, selecciona **Item**. El formulari conté els mateixos camps que la versió web: títol, URL, nom d'usuari, contrasenya (amb generador), notes i opció de seleccionar una carpeta.

Durant la creació pots seleccionar usuaris per compartir l'ítem directament. La contrasenya es xifra al navegador amb AES-GCM igual que a la resta de clients.

## Crear una carpeta

Des del menú **+**, selecciona **Carpeta**. Indica el nom. Un cop creada, pots afegir-hi ítems existents o compartir-la amb altres usuaris.

## Carpetes

Fes clic sobre una carpeta per veure el seu contingut. Des de la vista interior pots:

- Veure i accedir als ítems que conté.
- Editar el nom de la carpeta.
- Compartir la carpeta amb altres usuaris (seleccionant-los i assignant permís).
- Eliminar la carpeta.

## Compartits

La pestanya **Compartits** mostra les credencials que t'han compartit. Fes clic sobre qualsevol element per accedir-hi i usar l'autofill igual que amb els teus propis ítems.
