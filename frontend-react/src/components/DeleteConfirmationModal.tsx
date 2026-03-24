import * as React from 'react';
import { Dialog, DialogTitle, DialogContent, DialogContentText, DialogActions, Button } from '@mui/material';

interface DeleteConfirmationModalProps {
  open: boolean;
  onClose: () => void;
  onConfirm: () => void;
  title?: string;
  description?: string;
  confirmText?: string;
  cancelText?: string;
}

export default function DeleteConfirmationModal({
  open,
  onClose,
  onConfirm,
  title = "Confirmar eliminación",
  description = "¿Estás segura de que quieres eliminar este item? Esta acción no se puede deshacer.",
  confirmText = "Eliminar",
  cancelText = "Cancelar"
}: DeleteConfirmationModalProps) {
  return (
    <Dialog open={open} onClose={onClose}>
      <DialogTitle sx={{ fontWeight: 700, color: 'text.primary' }}>{title}</DialogTitle>
      <DialogContent>
        <DialogContentText sx={{ color: 'text.secondary', fontSize: '1rem' }}>
          {description}
        </DialogContentText>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} color="primary" sx={{ textTransform: 'none', fontWeight: 600 }}>
          {cancelText}
        </Button>
        <Button
          onClick={onConfirm}
          sx={{
            textTransform: 'none',
            fontWeight: 600,
            bgcolor: 'error.main',
            color: 'white',
            '&:hover': { bgcolor: 'error.dark' },
          }}
        >
          {confirmText}
        </Button>
      </DialogActions>
    </Dialog>
  );
}