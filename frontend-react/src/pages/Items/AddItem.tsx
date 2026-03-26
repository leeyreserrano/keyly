import { useState } from 'react';
import { useNavigate } from 'react-router';
import {
  Stack,
  Typography,
  Paper,
  Button,
  TextField,
  CssBaseline,
  Avatar,
  IconButton,
  Box,
} from '@mui/material';
import KeyRoundedIcon from '@mui/icons-material/VpnKeyRounded';
import LogoutOutlinedIcon from '@mui/icons-material/LogoutOutlined';
import ArrowBackOutlinedIcon from '@mui/icons-material/ArrowBackOutlined';
import Sidebar from '../../components/Sidebar';
import AppTheme from '../../theme/AppTheme';
import { itemsApi } from '../../api/itemsapi';

export default function AddItem() {
  const navigate = useNavigate();

  const [titol, setTitol] = useState('');
  const [nomUsuari, setNomUsuari] = useState('');
  const [url, setUrl] = useState('');
  const [contrasenya, setContrasenya] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSave = async () => {
    setLoading(true);
    try {
      await itemsApi.addItem({ titol, nomUsuari, url, contrasenya });
      navigate(-1);
    } catch (error) {
      console.error('Error creando item', error);
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => navigate(-1);

  return (
    <AppTheme>
      <CssBaseline enableColorScheme />
      <Stack direction="row" sx={{ minHeight: '100vh', width: '100%' }}>
        <Sidebar />

        <Stack sx={{ flex: 1, bgcolor: 'background.default', overflow: 'auto', minWidth: 0 }}>
          {/* Header */}
          <Stack
            direction="row"
            sx={{
              px: 4,
              py: 2.5,
              justifyContent: 'space-between',
              alignItems: 'center',
              borderBottom: '1px solid',
              borderColor: 'divider',
            }}
          >
            <Stack direction="row" sx={{ gap: 1.5, alignItems: 'center' }}>
              <KeyRoundedIcon sx={{ fontSize: 30, color: 'text.primary' }} />
              <Typography variant="h3" sx={{ fontWeight: 800 }}>
                Añadir Item
              </Typography>
            </Stack>

            <Stack direction="row" sx={{ gap: 2, alignItems: 'center' }}>
              <Avatar sx={{ bgcolor: 'grey.500', width: 36, height: 36, fontSize: 15, fontWeight: 700 }}>
                U
              </Avatar>
              <IconButton
                onClick={() => navigate('/')}
                size="small"
                sx={{ border: 'none', bgcolor: 'transparent', '&:hover': { bgcolor: 'action.hover' } }}
              >
                <LogoutOutlinedIcon sx={{ fontSize: 22, color: 'text.secondary' }} />
              </IconButton>
              <Button
                startIcon={<ArrowBackOutlinedIcon />}
                onClick={() => navigate(-1)}
                sx={{ textTransform: 'none', fontWeight: 600, bgcolor: 'primary.main', color: 'white', '&:hover': { bgcolor: 'primary.dark' } }}
              >
                Tornar
              </Button>
            </Stack>
          </Stack>

          {/* Formulario */}
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
              {/* Título */}
              <Stack spacing={0.5}>
                <Typography sx={{ fontSize: '0.8rem', fontWeight: 600, color: 'text.secondary' }}>
                  Título
                </Typography>
                <TextField fullWidth value={titol} onChange={(e) => setTitol(e.target.value)} />
              </Stack>

              {/* Usuario */}
              <Stack spacing={0.5}>
                <Typography sx={{ fontSize: '0.8rem', fontWeight: 600, color: 'text.secondary' }}>
                  Usuario / Email
                </Typography>
                <TextField fullWidth value={nomUsuari} onChange={(e) => setNomUsuari(e.target.value)} />
              </Stack>

              {/* URL */}
              <Stack spacing={0.5}>
                <Typography sx={{ fontSize: '0.8rem', fontWeight: 600, color: 'text.secondary' }}>
                  URL
                </Typography>
                <TextField fullWidth value={url} onChange={(e) => setUrl(e.target.value)} />
              </Stack>

              {/* Contraseña */}
              <Stack spacing={0.5}>
                <Typography sx={{ fontSize: '0.8rem', fontWeight: 600, color: 'text.secondary' }}>
                  Contraseña
                </Typography>
                <TextField
                  type="password"
                  fullWidth
                  value={contrasenya}
                  onChange={(e) => setContrasenya(e.target.value)}
                />
              </Stack>

              {/* Botones */}
              <Stack direction="row" spacing={2} sx={{ mt: 2, justifyContent: 'flex-end' }}>
                <Button
                  onClick={handleCancel}
                  sx={{
                    textTransform: 'none',
                    fontWeight: 600,
                    bgcolor: 'grey.300',
                    color: 'text.primary',
                    '&:hover': { bgcolor: 'grey.400' },
                  }}
                >
                  Cancelar
                </Button>
                <Button
                  onClick={handleSave}
                  disabled={loading}
                  sx={{
                    textTransform: 'none',
                    fontWeight: 600,
                    bgcolor: 'primary.main',
                    color: 'white',
                    '&:hover': { bgcolor: 'primary.dark' },
                  }}
                >
                  {loading ? 'Guardando...' : 'Guardar'}
                </Button>
              </Stack>
            </Paper>
          </Box>
        </Stack>
      </Stack>
    </AppTheme>
  );
}