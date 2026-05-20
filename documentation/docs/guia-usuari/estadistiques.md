# Estadístiques de seguretat

La secció **Estadístiques** analitza les teves credencials i genera un informe de seguretat. Les contrasenyes es desxifren al navegador per a l'anàlisi i es comproven contra la base de dades HIBP (*Have I Been Pwned*) sense enviar mai la contrasenya en text pla.

## Puntuació de seguretat global

Valor entre 0 i 100 que representa la qualitat mitjana de totes les teves contrasenyes. El color indica l'estat:

- Verd (≥ 70) — bon nivell de seguretat.
- Taronja (40–69) — cal millorar.
- Vermell (< 40) — risc alt.

## Alertes de seguretat

Si l'anàlisi detecta problemes, apareixeran alertes a la part superior:

- **Alerta vermella — Contrasenyes compromeses**: indica que una o més de les teves contrasenyes han aparegut en filtracions de dades conegudes. Prem **Revisar** per veure la llista d'ítems afectats.
- **Alerta groga — Contrasenyes reutilitzades**: indica que la mateixa contrasenya s'usa en més d'un ítem.

## Indicadors

| Indicador | Descripció |
|---|---|
| Compromeses | Contrasenyes detectades a la base de dades HIBP (consulta real contra el servidor) |
| Reutilitzades | Contrasenyes idèntiques usades en més d'un ítem |
| Febles | Contrasenyes que no superen el mínim de complexitat |
| Segures | Contrasenyes que compleixen tots els criteris |

## Gràfic de distribució

El gràfic de sectors mostra la proporció entre credencials segures, febles i compromeses.

## Ítems recents

Llistat dels darrers ítems accedits, amb l'opció d'anar directament a cada credencial.

## Contrasenyes compromeses (pàgina Pwned)

En prémer **Revisar** a l'alerta de contrasenyes compromeses, s'obre la pàgina de contrasenyes compromeses. Mostra únicament els ítems les contrasenyes dels quals han aparegut en filtracions reals. Pots cercar per títol o nom d'usuari, i eliminar directament qualsevol ítem afectat des d'aquí.

La comprovació usa el mètode k-anonymity: s'envia al servidor únicament un prefix de 5 caràcters del hash SHA-1 de la contrasenya. Mai s'envia la contrasenya ni el hash complet.
