import { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router';
import {
  Stack,
  Typography,
  Paper,
  IconButton,
  Button,
  Box,
  CircularProgress,
  CssBaseline,
  TextField,
  Alert,
  Divider,
  InputAdornment,
} from '@mui/material';
import KeyRoundedIcon from '@mui/icons-material/VpnKeyRounded';
import ContentCopyOutlinedIcon from '@mui/icons-material/ContentCopyOutlined';
import VisibilityIcon from '@mui/icons-material/Visibility';
import VisibilityOffIcon from '@mui/icons-material/VisibilityOff';
import toast from 'react-hot-toast';
import Sidebar from '../../components/Sidebar';
import AppTheme from '../../theme/AppTheme';
import Header from '../../components/Header';
import { itemsApi, type Item } from '../../api/itemsapi';

export default function EditItem() {
  const navigate = useNavigate();
  const location = useLocation();
  const { uuid } = location.state || {};

  const [item, setItem] = useState<Item | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [showPassword, setShowPassword] = useState(false);

  const [titol, setTitol] = useState('');
  const [nomUsuari, setNomUsuari] = useState('');
  const [url, setUrl] = useState('');
  const [contrasenya, setContrasenya] = useState('');
  const [errors, setErrors] = useState<{ titol?: string; contrasenya?: string }>({});

  useEffect(() => {
    if (!uuid) {
      setLoadError("No s'ha especificat cap item.");
      setLoading(false);
      return;
    }
    const loadData = async () => {
      try {
        const allItems = await itemsApi.fetchItems();
        if (!allItems) {
          setLoadError('Error carregant items.');
          return;
        }
        const found = allItems.find((i) => i.uuid === uuid);
        if (found) {
          setItem(found);
          setTitol(found.titol);
          setNomUsuari(found.nomUsuari);
          setUrl(found.url);
          setContrasenya(found.contrasenya);
        } else {
          setLoadError('Item no trobat.');
        }
      } catch (err: unknown) {
        const message = err instanceof Error ? err.message : "Error carregant l'item";
        setLoadError(message);
      } finally {
        setLoading(false);
      }
    };
    loadData();
  }, [uuid]);

  const validate = (): boolean => {
    const newErrors: { titol?: string; contrasenya?: string } = {};
    if (!titol.trim()) newErrors.titol = 'El títol és obligatori';
    if (!contrasenya.trim()) newErrors.contrasenya = 'La contrasenya és obligatòria';
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSave = async () => {
    if (!item || !validate()) return;
    setSaving(true);
    try {
      await itemsApi.updateItem(item.uuid, { titol, nomUsuari, url, contrasenya });
      toast.success('Item actualitzat correctament');
      navigate(-1);
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : "Error guardant l'item";
      toast.error(message);
    } finally {
      setSaving(false);
    }
  };

  const handleCopy = async () => {
    try {
      await navigator.clipboard.writeText(contrasenya);
      toast.success('Contrasenya copiada');
    } catch {
      toast.error('Error al copiar');
    }
  };

  return (
    <AppTheme>
      <CssBaseline enableColorScheme />
      <Stack direction="row" sx={{ minHeight: '100vh', width: '100%' }}>
        <Sidebar />

        <Stack sx={{ flex: 1, bgcolor: 'background.default', overflow: 'auto', minWidth: 0 }}>
          <Header
            title={item?.titol || 'Editar Item'}
            icon={<KeyRoundedIcon sx={{ fontSize: 30, color: 'text.primary' }} />}
            showBackButton
          />

          <Box sx={{ px: 4, py: 4, display: 'flex', justifyContent: 'center' }}>
            {loading ? (
              <Stack sx={{ alignItems: 'center', mt: 10 }}>
                <CircularProgress />
              </Stack>
            ) : loadError ? (
              <Alert severity="error" sx={{ mt: 2 }}>{loadError}</Alert>
            ) : (
              <Paper
                variant="outlined"
                sx={{
                  p: 4,
                  borderRadius: 3,
                  width: '70%',
                  maxWidth: 500,
                  display: 'flex',
                  flexDirection: 'column',
                  gap: 3,
                }}
              >
                <Typography variant="h5" sx={{ fontWeight: 700 }}>
                  {item?.titol}
                </Typography>

                <Divider />

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
                            sx={{ borderRadius: 2, border: '1px solid', borderColor: 'divider', color: 'text.secondary' }}
                          >
                            {showPassword ? <VisibilityOffIcon fontSize="small" /> : <VisibilityIcon fontSize="small" />}
                          </IconButton>
                          <IconButton
                            onClick={handleCopy}
                            sx={{ borderRadius: 2, border: '1px solid', borderColor: 'divider', color: 'primary.main' }}
                          >
                            <ContentCopyOutlinedIcon fontSize="small" />
                          </IconButton>
                        </InputAdornment>
                      ),
                    }}
                  />
                </Stack>

                <Divider />

                <Stack direction="row" sx={{ gap: 1, justifyContent: 'flex-end' }}>
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
                    disabled={saving}
                    sx={{ textTransform: 'none', fontWeight: 600 }}
                  >
                    {saving ? 'Guardant...' : 'Guardar'}
                  </Button>
                </Stack>
              </Paper>
            )}
          </Box>
        </Stack>
      </Stack>
    </AppTheme>
  );
}