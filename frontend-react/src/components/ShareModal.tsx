import { useState } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Stack,
  Typography,
} from '@mui/material';
import { compartitsApi } from '../api/compartitsapi';
import toast from 'react-hot-toast';

interface ShareModalProps {
  open: boolean;
  onClose: () => void;
  tipusEntitat: 'CARPETA' | 'ITEM';
  entitatUuid: string;
  entitatNom: string;
}

export default function ShareModal({
  open,
  onClose,
  tipusEntitat,
  entitatUuid,
  entitatNom,
}: ShareModalProps) {
  const [loading, setLoading] = useState(false);

  const handleShare = async () => {
    setLoading(true);
    try {
      await compartitsApi.add({
        tipusEntitat,
        entitatUuid,
        permisos: 'LECTURA',
      });
      toast.success(`"${entitatNom}" compartit correctament`);
      onClose();
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : 'Error compartint';
      toast.error(message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="xs">
      <DialogTitle sx={{ fontWeight: 700, color: 'text.primary' }}>
        Compartir {tipusEntitat === 'CARPETA' ? 'carpeta' : 'item'}
      </DialogTitle>

      <DialogContent>
        <Stack spacing={1.5}>
          <Typography sx={{ color: 'text.secondary', fontSize: '0.9rem' }}>
            Compartiràs <strong>"{entitatNom}"</strong> amb permisos de lectura.
          </Typography>
        </Stack>
      </DialogContent>

      <DialogActions>
        <Button
          onClick={onClose}
          sx={{ textTransform: 'none', fontWeight: 600 }}
        >
          Cancel·lar
        </Button>
        <Button
          onClick={handleShare}
          variant="contained"
          disabled={loading}
          sx={{ textTransform: 'none', fontWeight: 600 }}
        >
          {loading ? 'Compartint...' : 'Compartir'}
        </Button>
      </DialogActions>
    </Dialog>
  );
}