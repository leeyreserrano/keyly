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
  Avatar,
  TextField
} from '@mui/material';
import KeyRoundedIcon from '@mui/icons-material/VpnKeyRounded';
import ContentCopyOutlinedIcon from '@mui/icons-material/ContentCopyOutlined';
import VisibilityIcon from '@mui/icons-material/Visibility';
import VisibilityOffIcon from '@mui/icons-material/VisibilityOff';
import ArrowBackOutlinedIcon from '@mui/icons-material/ArrowBackOutlined';
import LogoutOutlinedIcon from '@mui/icons-material/LogoutOutlined';
import toast from 'react-hot-toast';
import Sidebar from '../../components/Sidebar';
import AppTheme from '../../theme/AppTheme';
import { itemsApi, type Item } from '../../api/itemsapi';
import { carpetasApi, type Carpeta } from '../../api/carpetasapi';

export default function EditItem() {
  const navigate = useNavigate();
  const location = useLocation();
  const { uuid } = location.state || {};

  const [item, setItem] = useState<Item | null>(null);
  const [loading, setLoading] = useState(true);
  const [carpetas, setCarpetas] = useState<Carpeta[]>([]);
  const [showPassword, setShowPassword] = useState(false);

  const [titol, setTitol] = useState('');
  const [nomUsuari, setNomUsuari] = useState('');
  const [url, setUrl] = useState('');
  const [contrasenya, setContrasenya] = useState('');

  useEffect(() => {
    if (!uuid) return;

    const loadData = async () => {
      try {
        const [allItems, allCarpetas] = await Promise.all([
          itemsApi.fetchItems(),
          carpetasApi.fetchItems(),
        ]);

        const found = allItems.find((i) => i.uuid === uuid);
        if (found) {
          setItem(found);
          setTitol(found.titol);
          setNomUsuari(found.nomUsuari);
          setUrl(found.url);
          setContrasenya(found.contrasenya);
        }

        setCarpetas(allCarpetas);
      } catch (error) {
        console.error('Error cargando item', error);
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, [uuid]);

  const toggleShowPassword = () => setShowPassword(!showPassword);

  const estaEnCarpeta = item
    ? carpetas.some((carpeta) =>
      carpeta.items.some((i) => i.uuid === item.uuid)
    )
    : false;

  const handleSave = async () => {
    if (!item) return;

    try {
      await itemsApi.updateItem(item.uuid, {
        titol,
        nomUsuari,
        url,
        contrasenya,
      });

      navigate(-1);
    } catch (error) {
      console.error('Error guardando item', error);
    }
  };
  const handleCancel = () => navigate(-1);
  const handleCopy = async () => {
    if (item?.contrasenya) {
      try {
        await navigator.clipboard.writeText(item.contrasenya);
        toast.success('Contrasenya copiada al portapapeles');
      } catch (error) {
        toast.error('Error al copiar');
      }
    }
  };
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
                Editar Item
              </Typography>
            </Stack>

            <Stack direction="row" sx={{ gap: 2, alignItems: 'center' }}>
              <Avatar sx={{ bgcolor: 'grey.500', width: 36, height: 36, fontSize: 15, fontWeight: 700 }}>U</Avatar>
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
                sx={{
                  textTransform: 'none',
                  fontWeight: 600,
                  bgcolor: 'primary.main',
                  color: 'white',
                  '&:hover': { bgcolor: 'primary.dark' },
                }}
              >
                Tornar
              </Button>
            </Stack>
          </Stack>

          {/* Contenido */}
          <Box sx={{ px: 4, py: 3 }}>
            {loading || !item ? (
              <Stack sx={{ alignItems: 'center', mt: 10 }}>
                <CircularProgress />
              </Stack>
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
                }}
              >
                <Stack spacing={0.5}>
                  <Typography sx={{ fontSize: '0.8rem', fontWeight: 600, color: 'text.secondary' }}>
                    Título
                  </Typography>
                  <TextField
                    fullWidth
                    value={titol}
                    onChange={(e) => setTitol(e.target.value)}
                  />
                </Stack>

                <Stack spacing={0.5}>
                  <Typography sx={{ fontSize: '0.8rem', fontWeight: 600, color: 'text.secondary' }}>
                    Usuario / Email
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
                    Contraseña
                  </Typography>

                  <Stack direction="row" sx={{ alignItems: 'center', gap: 1 }}>
                    <TextField
                      fullWidth
                      type={showPassword ? 'text' : 'password'}
                      value={contrasenya}
                      onChange={(e) => setContrasenya(e.target.value)}
                    />

                    <IconButton
                      onClick={toggleShowPassword}
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
                  </Stack>
                </Stack>

                <Stack direction="row" sx={{ mt: 2, gap: 1 }}>
                  <Button
                    onClick={handleCancel}
                    sx={{
                      textTransform: 'none',
                      fontWeight: 600,
                      bgcolor: 'grey.500',
                      color: 'white',
                      '&:hover': { bgcolor: 'grey.700' },
                    }}
                  >
                    Cancelar
                  </Button>
                  <Button
                    onClick={handleSave}
                    sx={{
                      textTransform: 'none',
                      fontWeight: 600,
                      bgcolor: 'primary.main',
                      color: 'white',
                      '&:hover': { bgcolor: 'primary.dark' },
                    }}
                  >
                    Guardar
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