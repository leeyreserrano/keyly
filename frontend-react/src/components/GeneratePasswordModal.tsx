import { useState, useEffect } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Stack,
  Typography,
  Slider,
  Divider,
  TextField,
  IconButton,
  Tooltip,
  Checkbox,
  FormControlLabel,
  CircularProgress,
} from '@mui/material';
import ContentCopyOutlinedIcon from '@mui/icons-material/ContentCopyOutlined';
import toast from 'react-hot-toast';
import { utilsApi } from '../api/utilsapi';

interface GeneratePasswordModalProps {
  open: boolean;
  onClose: () => void;
  onConfirm: (password: string) => void;
}

export default function GeneratePasswordModal({ open, onClose, onConfirm }: GeneratePasswordModalProps) {
  const [length, setLength] = useState(16);
  const [useLower, setUseLower] = useState(true);
  const [useUpper, setUseUpper] = useState(true);
  const [useNumbers, setUseNumbers] = useState(true);
  const [useSpecial, setUseSpecial] = useState(false);
  const [preview, setPreview] = useState('');
  const [loadingPreview, setLoadingPreview] = useState(false);

  const activeCount = [useLower, useUpper, useNumbers, useSpecial].filter(Boolean).length;
  const isValid = activeCount > 0;

  const fetchPassword = async () => {
    if (!isValid) { setPreview(''); return; }
    setLoadingPreview(true);
    try {
      const result = await utilsApi.generatePassword({
        longitud: length,
        may: useUpper,
        quantitatMay: useUpper ? Math.floor(length * 0.25) : 0,
        numeros: useNumbers,
        quantitatNumeros: useNumbers ? Math.floor(length * 0.25) : 0,
        caractersEspecials: useSpecial,
        quantitatCaractersEspecials: useSpecial ? Math.floor(length * 0.15) : 0,
      });
      setPreview(result ?? '');
    } catch {
      toast.error('Error generant la contrasenya');
    } finally {
      setLoadingPreview(false);
    }
  };

  const [initialized, setInitialized] = useState(false);

  useEffect(() => {
    if (open && !initialized) {
      setInitialized(true);
      fetchPassword();
    }
    if (!open) {
      setInitialized(false);
    }
  }, [open]);

  useEffect(() => {
    if (!initialized) return;
    fetchPassword();
  }, [length, useLower, useUpper, useNumbers, useSpecial]);

  const handleCopy = async () => {
    try {
      await navigator.clipboard.writeText(preview);
      toast.success('Contrasenya copiada');
    } catch {
      toast.error('Error al copiar');
    }
  };

  const handleConfirm = () => {
    onConfirm(preview);
    onClose();
  };

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="xs">
      <DialogTitle sx={{ fontWeight: 700, color: 'text.primary' }}>
        Personalitzar contrasenya
      </DialogTitle>

      <DialogContent>
        <Stack spacing={2.5}>
          <Stack spacing={0.5}>
            <Stack direction="row" sx={{ justifyContent: 'space-between', alignItems: 'center' }}>
              <Typography sx={{ fontSize: '0.8rem', fontWeight: 600, color: 'text.secondary' }}>
                Longitud
              </Typography>
              <Typography sx={{ fontSize: '0.8rem', fontWeight: 700, color: 'text.primary' }}>
                {length}
              </Typography>
            </Stack>
            <Slider
              value={length}
              min={4}
              max={64}
              step={1}
              onChange={(_, v) => setLength(v as number)}
              size="small"
            />
          </Stack>

          <Divider />

          <Stack spacing={0.5}>
            <Typography sx={{ fontSize: '0.8rem', fontWeight: 600, color: 'text.secondary' }}>
              Tipus de caràcters
            </Typography>
            <FormControlLabel
              control={<Checkbox checked={useLower} onChange={(e) => setUseLower(e.target.checked)} size="small" />}
              label={<Typography sx={{ fontSize: '0.85rem' }}>Minúscules (a-z)</Typography>}
            />
            <FormControlLabel
              control={<Checkbox checked={useUpper} onChange={(e) => setUseUpper(e.target.checked)} size="small" />}
              label={<Typography sx={{ fontSize: '0.85rem' }}>Majúscules (A-Z)</Typography>}
            />
            <FormControlLabel
              control={<Checkbox checked={useNumbers} onChange={(e) => setUseNumbers(e.target.checked)} size="small" />}
              label={<Typography sx={{ fontSize: '0.85rem' }}>Números (0-9)</Typography>}
            />
            <FormControlLabel
              control={<Checkbox checked={useSpecial} onChange={(e) => setUseSpecial(e.target.checked)} size="small" />}
              label={<Typography sx={{ fontSize: '0.85rem' }}>Caràcters especials (!@#...)</Typography>}
            />
          </Stack>

          {!isValid && (
            <Typography sx={{ fontSize: '0.78rem', color: 'error.main', fontWeight: 600 }}>
              Selecciona almenys un tipus de caràcter.
            </Typography>
          )}

          <Divider />

          <Stack spacing={0.5}>
            <Typography sx={{ fontSize: '0.8rem', fontWeight: 600, color: 'text.secondary' }}>
              Previsualització
            </Typography>
            <Stack direction="row" sx={{ alignItems: 'center', gap: 1 }}>
              <TextField
                fullWidth
                value={loadingPreview ? '' : preview}
                InputProps={{
                  readOnly: true,
                  sx: { fontFamily: 'monospace', fontSize: '0.85rem' },
                  endAdornment: loadingPreview ? <CircularProgress size={14} /> : null,
                }}
                size="small"
              />
              <Tooltip title="Copiar">
                <span>
                  <IconButton
                    onClick={handleCopy}
                    disabled={!isValid || loadingPreview}
                    sx={{ borderRadius: 2, border: '1px solid', borderColor: 'divider', color: 'primary.main' }}
                  >
                    <ContentCopyOutlinedIcon fontSize="small" />
                  </IconButton>
                </span>
              </Tooltip>
            </Stack>
          </Stack>
        </Stack>
      </DialogContent>

      <DialogActions>
        <Button
          onClick={fetchPassword}
          disabled={!isValid || loadingPreview}
          sx={{ textTransform: 'none', fontWeight: 600, mr: 'auto' }}
        >
          Regenerar
        </Button>
        <Button onClick={onClose} sx={{ textTransform: 'none', fontWeight: 600 }}>
          Cancel·lar
        </Button>
        <Button
          onClick={handleConfirm}
          disabled={!isValid || loadingPreview || !preview}
          variant="contained"
          sx={{ textTransform: 'none', fontWeight: 600 }}
        >
          Utilitzar
        </Button>
      </DialogActions>
    </Dialog>
  );
}