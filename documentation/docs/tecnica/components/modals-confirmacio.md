# Modals de confirmació

## DeleteConfirmationModal

Fitxer: `src/components/DeleteConfirmationModal.tsx`

Diàleg de confirmació genèric per a qualsevol operació d'eliminació. Rep les props `open`, `onClose` i `onConfirm`. No conté lògica de negoci pròpia: l'acció destructiva l'executa el component pare al callback `onConfirm`.

El patró d'ús a les pàgines és optimista: primer s'actualitza l'estat local eliminant l'element de la llista, després es fa la crida a l'API. Si la crida falla, es recarreguen les dades per restaurar l'estat correcte.
