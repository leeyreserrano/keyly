# GeneratePasswordModal

Fitxer: `src/components/GeneratePasswordModal.tsx`

Modal que genera contrasenyes aleatòries segons els criteris que tria l'usuari: longitud, majúscules, números i símbols. La generació es fa al navegador amb `crypto.getRandomValues`. En acceptar, passa la contrasenya generada al component pare via callback.
