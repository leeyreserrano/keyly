import { useTranslation } from 'react-i18next';
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
  title,
  description,
  confirmText,
  cancelText,
}: DeleteConfirmationModalProps) {
  const { t } = useTranslation('common');

  return (
    <Dialog open={open} onClose={onClose}>
      <DialogTitle sx={{ fontWeight: 700, color: 'text.primary' }}>
        {title ?? t('delete_modal.title')}
      </DialogTitle>
      <DialogContent>
        <DialogContentText sx={{ color: 'text.secondary', fontSize: '1rem' }}>
          {description ?? t('delete_modal.description')}
        </DialogContentText>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} color="primary" sx={{ textTransform: 'none', fontWeight: 600 }}>
          {cancelText ?? t('cancel')}
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
          {confirmText ?? t('delete')}
        </Button>
      </DialogActions>
    </Dialog>
  );
}