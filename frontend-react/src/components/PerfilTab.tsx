import { useRef, useState } from 'react';
import {
  Stack,
  Paper,
  Box,
  Typography,
  Divider,
  Chip,
  IconButton,
  CircularProgress,
  Tooltip,
  TextField,
  Button,
  InputAdornment,
} from '@mui/material';
import EditOutlinedIcon from '@mui/icons-material/EditOutlined';
import VisibilityIcon from '@mui/icons-material/Visibility';
import VisibilityOffIcon from '@mui/icons-material/VisibilityOff';
import UserAvatar from './UserAvatar';
import { useAuth } from '../context/AuthContext';
import { usuarisApi } from '../api/usuarisapi';
import { deriveKey, generateKeyPair, encryptPrivateKey, bytesToBase64 } from '../crypto/cryptoService';
import toast from 'react-hot-toast';

type RolType = 'error' | 'warning' | 'default';

const ROL_LABEL: Record<string, { label: string; color: RolType }> = {
  ADMIN:  { label: 'Administrador', color: 'error' },
  CAP:    { label: 'Cap',           color: 'warning' },
  USUARI: { label: 'Usuari',        color: 'default' },
};

function generateSalt(): string {
  const bytes = crypto.getRandomValues(new Uint8Array(16));
  return bytesToBase64(bytes);
}

function FieldLabel({ children }: { children: string }) {
  return (
    <Typography sx={{ fontSize: '0.8rem', fontWeight: 600, color: 'text.secondary' }}>
      {children}
    </Typography>
  );
}

export default function PerfilTab() {
  const { usuari, login, token, refreshAvatar } = useAuth();
  const fileInputRef = useRef<HTMLInputElement | null>(null);
  const [uploadingImage, setUploadingImage] = useState(false);

  const [contrasenyaActual, setContrasenyaActual] = useState('');
  const [novaContra, setNovaContra] = useState('');
  const [confirmaContra, setConfirmaContra] = useState('');
  const [showCurrent, setShowCurrent] = useState(false);
  const [showNew, setShowNew] = useState(false);
  const [showConfirm, setShowConfirm] = useState(false);
  const [savingPassword, setSavingPassword] = useState(false);

  if (!usuari) return null;

  const rol = ROL_LABEL[usuari.rolIntern] ?? null;

  const handleImageChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file || !token) return;
    setUploadingImage(true);
    try {
      const updated = await usuarisApi.uploadImage(file, token);
      const merged = { ...usuari, ...(updated ?? {}) };
      login(merged, token, !!localStorage.getItem('jwtToken'));
      refreshAvatar();
      toast.success('Imatge actualitzada correctament');
    } catch {
      toast.error('Error actualitzant la imatge');
    } finally {
      setUploadingImage(false);
    }
  };

  const handleChangePassword = async () => {
    if (!contrasenyaActual || !novaContra || !confirmaContra) {
      toast.error('Omple tots els camps');
      return;
    }
    if (novaContra !== confirmaContra) {
      toast.error('Les contrasenyes no coincideixen');
      return;
    }
    if (!token) return;

    setSavingPassword(true);
    try {
      const newSalt = generateSalt();
      const newDerivedKeyB64 = await deriveKey(novaContra, newSalt);
      const { publicKeyB64, privateKeyB64 } = await generateKeyPair();
      const encryptedPK = await encryptPrivateKey(privateKeyB64, newDerivedKeyB64);

      await usuarisApi.updateSelf({
        contrasenya: novaContra,
        kdfSalt: newSalt,
        publicKey: publicKeyB64,
        encryptedPrivateKey: encryptedPK,
      });

      toast.success('Contrasenya actualitzada');
      setContrasenyaActual('');
      setNovaContra('');
      setConfirmaContra('');
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : 'Error actualitzant la contrasenya';
      toast.error(msg);
    } finally {
      setSavingPassword(false);
    }
  };

  const eyeButton = (visible: boolean, toggle: () => void) => (
    <InputAdornment position="end">
      <IconButton
        onClick={toggle}
        sx={{
          borderRadius: 2,
          border: '1px solid',
          borderColor: 'divider',
          color: 'text.secondary',
        }}
      >
        {visible ? <VisibilityOffIcon fontSize="small" /> : <VisibilityIcon fontSize="small" />}
      </IconButton>
    </InputAdornment>
  );

  return (
    <Stack direction="row" spacing={3} alignItems="stretch">
      <Paper variant="outlined" sx={{ p: 3, borderRadius: 2, flex: 1 }}>
        <Stack spacing={2.5} sx={{ height: '100%' }}>
          <Stack direction="row" spacing={2} alignItems="center">
            <Box sx={{ position: 'relative', display: 'inline-flex' }}>
              <UserAvatar size={72} />
              <Tooltip title="Canviar imatge">
                <IconButton
                  size="small"
                  onClick={() => fileInputRef.current?.click()}
                  disabled={uploadingImage}
                  sx={{
                    position: 'absolute',
                    bottom: -4,
                    right: -4,
                    bgcolor: 'background.paper',
                    border: '1px solid',
                    borderColor: 'divider',
                    width: 24,
                    height: 24,
                  }}
                >
                  {uploadingImage
                    ? <CircularProgress size={12} />
                    : <EditOutlinedIcon sx={{ fontSize: 14 }} />
                  }
                </IconButton>
              </Tooltip>
              <input
                ref={fileInputRef}
                type="file"
                accept="image/*"
                hidden
                onChange={handleImageChange}
              />
            </Box>

            <Stack>
              <Typography variant="h5" sx={{ fontWeight: 700 }}>
                {usuari.nom}
              </Typography>
              {rol && (
                <Chip
                  label={rol.label}
                  color={rol.color}
                  size="small"
                  sx={{ mt: 0.5, width: 'fit-content', fontWeight: 600 }}
                />
              )}
            </Stack>
          </Stack>

          <Divider />

          <Stack spacing={2} sx={{ flex: 1 }}>
            <Stack spacing={0.25}>
              <Typography sx={{ fontSize: '0.75rem', fontWeight: 600, color: 'text.secondary' }}>
                Nom
              </Typography>
              <Typography sx={{ fontWeight: 500 }}>{usuari.nom}</Typography>
            </Stack>
            <Stack spacing={0.25}>
              <Typography sx={{ fontSize: '0.75rem', fontWeight: 600, color: 'text.secondary' }}>
                Email
              </Typography>
              <Typography sx={{ fontWeight: 500 }}>{usuari.correu}</Typography>
            </Stack>
            <Stack spacing={0.25}>
              <Typography sx={{ fontSize: '0.75rem', fontWeight: 600, color: 'text.secondary' }}>
                Rol
              </Typography>
              <Typography sx={{ fontWeight: 500 }}>{rol?.label ?? usuari.rolIntern}</Typography>
            </Stack>
          </Stack>
        </Stack>
      </Paper>

      <Paper variant="outlined" sx={{ p: 3, borderRadius: 2, flex: 1 }}>
        <Typography variant="h6" sx={{ fontWeight: 700, mb: 1 }}>
          Canviar contrasenya
        </Typography>
        <Divider sx={{ mb: 2.5 }} />

        <Stack spacing={2}>
          <Stack spacing={0.5}>
            <FieldLabel>Contrasenya actual *</FieldLabel>
            <TextField
              fullWidth
              type={showCurrent ? 'text' : 'password'}
              value={contrasenyaActual}
              onChange={e => setContrasenyaActual(e.target.value)}
              placeholder="Contrasenya actual"
              InputProps={{ endAdornment: eyeButton(showCurrent, () => setShowCurrent(p => !p)) }}
            />
          </Stack>

          <Stack spacing={0.5}>
            <FieldLabel>Nova contrasenya *</FieldLabel>
            <TextField
              fullWidth
              type={showNew ? 'text' : 'password'}
              value={novaContra}
              onChange={e => setNovaContra(e.target.value)}
              placeholder="Nova contrasenya"
              InputProps={{ endAdornment: eyeButton(showNew, () => setShowNew(p => !p)) }}
            />
          </Stack>

          <Stack spacing={0.5}>
            <FieldLabel>Confirmar nova contrasenya *</FieldLabel>
            <TextField
              fullWidth
              type={showConfirm ? 'text' : 'password'}
              value={confirmaContra}
              onChange={e => setConfirmaContra(e.target.value)}
              placeholder="Repeteix la nova contrasenya"
              InputProps={{ endAdornment: eyeButton(showConfirm, () => setShowConfirm(p => !p)) }}
            />
          </Stack>

          <Button
            variant="contained"
            onClick={handleChangePassword}
            disabled={savingPassword}
            sx={{ alignSelf: 'flex-end', textTransform: 'none', fontWeight: 700 }}
          >
            {savingPassword
              ? <CircularProgress size={18} color="inherit" />
              : 'Guardar'
            }
          </Button>
        </Stack>
      </Paper>
    </Stack>
  );
}