# ShareModal i ShareSelectorInline

## ShareModal

Fitxer: `src/components/ShareModal.tsx`

Modal que permet compartir un ítem o carpeta amb altres usuaris o departaments. Utilitza internament el hook `useShareSelector` per a tota la lògica de selecció i xifrat.

El flux de compartició dins del modal és:

1. L'usuari selecciona destinataris i tria el nivell de permís.
2. En confirmar, per a cada destinatari:
   - Es recupera la seva clau pública (`UsuariPublic.publicKey`).
   - Es desxifra la DataKey de l'ítem amb la clau privada de l'usuari actual (RSA-OAEP).
   - Es torna a xifrar la DataKey amb la clau pública del destinatari.
3. Es crida `compartitsApi.addCompartit()` amb la llista de DataKeys xifrades.

## ShareSelectorInline

Fitxer: `src/components/ShareSelectorInline.tsx`

Versió del selector de destinataris integrada directament en formularis (per exemple, durant la creació d'un ítem). Exposa el mateix estat que `useShareSelector` però sense el modal com a embolcall.

## useShareSelector

Fitxer: `src/hooks/useShareSelector.ts`

Hook que encapsula tota la lògica de selecció de destinataris per compartir:

- Carrega la llista d'usuaris i departaments disponibles en muntar-se.
- Gestiona la cerca per nom o correu.
- Manté la llista d'usuaris seleccionats i el permís triat.
- Ofereix `handleSubmitCompartir()` que executa el procés de xifrat i la crida a l'API.

Els administradors veuen la pestanya addicional de selecció per departament.
