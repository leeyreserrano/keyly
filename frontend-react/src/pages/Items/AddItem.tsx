import { useState } from 'react';
import { useNavigate } from 'react-router';
import {
  Stack,
  Typography,
  Paper,
  Button,
  TextField,
  CssBaseline,
  IconButton,
  Box,
  InputAdornment,
} from '@mui/material';
import KeyRoundedIcon from '@mui/icons-material/VpnKeyRounded';
import VisibilityIcon from '@mui/icons-material/Visibility';
import VisibilityOffIcon from '@mui/icons-material/VisibilityOff';
import Sidebar from '../../components/Sidebar';
import AppTheme from '../../theme/AppTheme';
import Header from '../../components/Header';
import { itemsApi } from '../../api/itemsapi';
import toast from 'react-hot-toast';

export default function AddItem() {
  const navigate = useNavigate();

  const [titol, setTitol] = useState('');
  const [nomUsuari, setNomUsuari] = useState('');
  const [url, setUrl] = useState('');
  const [contrasenya, setContrasenya] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);

  // Validació
  const [errors, setErrors] = useState<{ titol?: string; contrasenya?: string }>({});

  const validate = (): boolean => {
    const newErrors: { titol?: string; contrasenya?: string } = {};
    if (!titol.trim()) newErrors.titol = 'El títol és obligatori';
    if (!contrasenya.trim()) newErrors.contrasenya = 'La contrasenya és obligatòria';
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSave = async () => {
    if (!validate()) return;

    setLoading(true);
    try {
      await itemsApi.addItem({ titol, nomUsuari, url, contrasenya });
      toast.success('Item creat correctament');
      navigate('/Items');
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : 'Error creant l\'item';
      toast.error(message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <AppTheme>
      <CssBaseline enableColorScheme />
      <Stack direction="row" sx={{ minHeight: '100vh', width: '100%' }}>
        <Sidebar />

        <Stack sx={{ flex: 1, bgcolor: 'background.default', overflow: 'auto', minWidth: 0 }}>
          <Header
            title="Afegir Item"
            icon={<KeyRoundedIcon sx={{ fontSize: 30, color: 'text.primary' }} />}
            showBackButton
          />

          <Box sx={{ px: 4, py: 4, display: 'flex', justifyContent: 'center' }}>
            <Paper
              variant="outlined"
              sx={{
                p: 4,
                borderRadius: 3,
                width: '100%',
                maxWidth: 500,
                display: 'flex',
                flexDirection: 'column',
                gap: 3,
              }}
            >
              <Stack spacing={0.5}>
                <Typography sx={{ fontSize: '0.8rem', fontWeight: 600, color: 'text.secondary' }}>
                  Títol *
                </Typography>
                <TextField
                  fullWidth
                  value={titol}
                  onChange={(e) => setTitol(e.target.value)}
                  error={!!errors.titol}
                  helperText={errors.titol}
                  placeholder="Ex: Gmail personal"
                />
              </Stack>

              <Stack spacing={0.5}>
                <Typography sx={{ fontSize: '0.8rem', fontWeight: 600, color: 'text.secondary' }}>
                  Usuari / Email
                </Typography>
                <TextField
                  fullWidth
                  value={nomUsuari}
                  onChange={(e) => setNomUsuari(e.target.value)}
                  placeholder="Ex: usuari@exemple.com"
                />
              </Stack>

              <Stack spacing={0.5}>
                <Typography sx={{ fontSize: '0.8rem', fontWeight: 600, color: 'text.secondary' }}>
                  URL
                </Typography>
                <TextField
                  fullWidth
                  value={url}
                  onChange={(e) => setUrl(e.target.value)}
                  placeholder="Ex: https://gmail.com"
                />
              </Stack>

              <Stack spacing={0.5}>
                <Typography sx={{ fontSize: '0.8rem', fontWeight: 600, color: 'text.secondary' }}>
                  Contrasenya *
                </Typography>
                <TextField
                  fullWidth
                  type={showPassword ? 'text' : 'password'}
                  value={contrasenya}
                  onChange={(e) => setContrasenya(e.target.value)}
                  error={!!errors.contrasenya}
                  helperText={errors.contrasenya}
                  InputProps={{
                    endAdornment: (
                      <InputAdornment position="end">
                        <IconButton
                          onClick={() => setShowPassword((p) => !p)}
                          sx={{ borderRadius: 2 }}
                        >
                          {showPassword ? <VisibilityOffIcon fontSize="small" /> : <VisibilityIcon fontSize="small" />}
                        </IconButton>
                      </InputAdornment>
                    ),
                  }}
                />
              </Stack>

              <Stack direction="row" spacing={2} sx={{ mt: 1, justifyContent: 'flex-end' }}>
                <Button
                  onClick={() => navigate(-1)}
                  variant="outlined"
                  sx={{ textTransform: 'none', fontWeight: 600 }}
                >
                  Cancel·lar
                </Button>
                <Button
                  onClick={handleSave}
                  variant="contained"
                  disabled={loading}
                  sx={{ textTransform: 'none', fontWeight: 600 }}
                >
                  {loading ? 'Guardant...' : 'Guardar'}
                </Button>
              </Stack>
            </Paper>
          </Box>
        </Stack>
      </Stack>
    </AppTheme>
  );
}
