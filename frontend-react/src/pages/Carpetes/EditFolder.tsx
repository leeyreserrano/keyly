import { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router';
import {
  Stack,
  Typography,
  Paper,
  Button,
  TextField,
  CssBaseline,
  Box,
  Divider,
  CircularProgress,
  Alert,
} from '@mui/material';
import FolderOutlinedIcon from '@mui/icons-material/FolderOutlined';
import Sidebar from '../../components/Sidebar';
import AppTheme from '../../theme/AppTheme';
import Header from '../../components/Header';
import { carpetasApi, type Carpeta } from '../../api/carpetasapi';
import toast from 'react-hot-toast';

export default function EditCarpeta() {
  const navigate = useNavigate();
  const location = useLocation();
  const { uuid } = location.state || {};

  const [carpeta, setCarpeta] = useState<Carpeta | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [loadError, setLoadError] = useState<string | null>(null);

  const [nom, setNom] = useState('');
  const [error, setError] = useState<string | undefined>();

  useEffect(() => {
    if (!uuid) {
      setLoadError("No s'ha especificat cap carpeta.");
      setLoading(false);
      return;
    }
    const loadData = async () => {
      try {
        const allCarpetas = await carpetasApi.fetchItems();
        const found = allCarpetas.find((c) => c.uuid === uuid);
        if (found) {
          setCarpeta(found);
          setNom(found.nom);
        } else {
          setLoadError('Carpeta no trobada.');
        }
      } catch (err: unknown) {
        const message = err instanceof Error ? err.message : 'Error carregant la carpeta';
        setLoadError(message);
      } finally {
        setLoading(false);
      }
    };
    loadData();
  }, [uuid]);

  const validate = (): boolean => {
    if (!nom.trim()) {
      setError('El nom de la carpeta és obligatori');
      return false;
    }
    setError(undefined);
    return true;
  };

  const handleSave = async () => {
    if (!carpeta || !validate()) return;
    setSaving(true);
    try {
      await carpetasApi.updateCarpeta(carpeta.uuid, { nom });
      toast.success('Carpeta actualitzada correctament');
      navigate(-1);
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : 'Error guardant la carpeta';
      toast.error(message);
    } finally {
      setSaving(false);
    }
  };

  return (
    <AppTheme>
      <CssBaseline enableColorScheme />
      <Stack direction="row" sx={{ minHeight: '100vh', width: '100%' }}>
        <Sidebar />

        <Stack sx={{ flex: 1, bgcolor: 'background.default', overflow: 'auto', minWidth: 0 }}>
          <Header
            title={carpeta?.nom || 'Editar Carpeta'}
            icon={<FolderOutlinedIcon sx={{ fontSize: 30, color: 'text.primary' }} />}
            showBackButton
          />

          <Box sx={{ px: 4, py: 3, display: 'flex', justifyContent: 'center' }}>
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
                  p: 3,
                  borderRadius: '12px',
                  border: '1px solid',
                  borderColor: 'divider',
                  display: 'flex',
                  flexDirection: 'column',
                  gap: 2,
                  maxWidth: 500,
                  width: '100%',
                }}
              >
                <Typography variant="h5" sx={{ fontWeight: 700 }}>
                  {carpeta?.nom}
                </Typography>

                <Divider />

                <Stack spacing={0.5}>
                  <Typography sx={{ fontSize: '0.8rem', fontWeight: 600, color: 'text.secondary' }}>
                    Nom *
                  </Typography>
                  <TextField
                    fullWidth
                    value={nom}
                    onChange={(e) => setNom(e.target.value)}
                    error={!!error}
                    helperText={error}
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