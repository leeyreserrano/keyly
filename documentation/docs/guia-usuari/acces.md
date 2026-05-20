# Accés a l'aplicació

## Iniciar sessió

La pantalla de login té dues zones: un panell de marca a l'esquerra i el formulari d'accés a la dreta. Introdueix el teu correu electrònic corporatiu i la contrasenya mestra i prem **Iniciar sessió**.

L'opció **Recorda'm** manté la sessió activa fins que tanquis sessió manualment. Si no la marques, la sessió s'esborrarà quan tanquis el navegador.

## Procés intern al login

Quan t'autentiques, l'aplicació fa els passos següents de forma automàtica:

1. Envia el correu i la contrasenya al servidor.
2. Rep el token JWT, el `kdfSalt` i la clau privada xifrada.
3. Deriva una clau criptogràfica a partir de la teva contrasenya mestra i el `kdfSalt`.
4. Desxifra la clau privada RSA i la guarda a la memòria de sessió.

Aquest procés garanteix que la clau privada mai viatja en text pla.

## Tancar sessió

Fes clic a la icona de tancament de sessió a la capçalera, a la part superior dreta de qualsevol pàgina. Totes les dades de sessió i les claus criptogràfiques s'eliminen del navegador immediatament.

## Contrasenya oblidada

Utilitza l'enllaç **Has oblidat la contrasenya?** del formulari d'accés per iniciar el procés de recuperació.

!!! warning "Atenció"
    Si perds la contrasenya mestra, les credencials xifrades no es poden recuperar. La clau privada que les desxifra està protegida per la contrasenya mestra i no existeix cap còpia de seguretat al servidor.
