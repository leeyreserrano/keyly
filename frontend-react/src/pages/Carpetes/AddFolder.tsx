import { useState } from 'react';
import { useNavigate } from 'react-router';
import {
  Stack,
  Typography,
  Paper,
  Button,
  TextField,
  CssBaseline,
  Box,
  Divider,
} from '@mui/material';
import FolderOutlinedIcon from '@mui/icons-material/FolderOutlined';
import Sidebar from '../../components/Sidebar';
import AppTheme from '../../theme/AppTheme';
import Header from '../../components/Header';
import { carpetasApi } from '../../api/carpetasapi';
import toast from 'react-hot-toast';

export default function AddCarpeta() {
  const navigate = useNavigate();
  const [nom, setNom] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | undefined>();

  const validate = (): boolean => {
    if (!nom.trim()) {
      setError('El nom de la carpeta és obligatori');
      return false;
    }
    setError(undefined);
    return true;
  };

  const handleSave = async () => {
    if (!validate()) return;
    setLoading(true);
    try {
      await carpetasApi.addCarpeta({ nom });
      toast.success('Carpeta creada correctament');
      navigate(-1);
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : 'Error creant la carpeta';
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

        <Stack sx={{ flex: 1, bgcolor: 'background.default', overflow: 'auto', minWidth: 0}}>
          <Header
            title="Afegir Carpeta"
            icon={<FolderOutlinedIcon sx={{ fontSize: 30, color: 'text.primary' }} />}
            showBackButton
          />

          <Box sx={{ px: 4, py: 3, display: 'flex', justifyContent: 'center' }}>
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
                Nova Carpeta
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
                  placeholder="Ex: Xarxes socials"
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