import { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router';
import {Stack, Typography, Paper, IconButton, Button, Divider, Box, CircularProgress, CssBaseline, Avatar} from '@mui/material';
import KeyRoundedIcon from '@mui/icons-material/VpnKeyRounded';
import FolderOutlinedIcon from '@mui/icons-material/FolderOutlined';
import ContentCopyOutlinedIcon from '@mui/icons-material/ContentCopyOutlined';
import VisibilityIcon from '@mui/icons-material/Visibility';
import VisibilityOffIcon from '@mui/icons-material/VisibilityOff';
import ArrowBackOutlinedIcon from '@mui/icons-material/ArrowBackOutlined';
import LogoutOutlinedIcon from '@mui/icons-material/LogoutOutlined';
import EditOutlinedIcon from '@mui/icons-material/EditOutlined';
import DeleteOutlinedIcon from '@mui/icons-material/DeleteOutlined';
import DeleteConfirmationModal from '../components/DeleteConfirmationModal';
import Sidebar from '../components/Sidebar';
import AppTheme from '../theme/AppTheme';
import { itemsApi, type Item } from '../api/itemsapi';
import { carpetasApi, type Carpeta } from '../api/carpetasapi';

export default function Item() {
  const navigate = useNavigate();
  const location = useLocation();
  const { uuid } = location.state || {};

  const [item, setItem] = useState<Item | null>(null);
  const [carpetas, setCarpetas] = useState<Carpeta[]>([]);
  const [loading, setLoading] = useState(true);
  const [showPassword, setShowPassword] = useState(false);
  const [openDeleteModal, setOpenDeleteModal] = useState(false);

  useEffect(() => {
    if (!uuid) return;
    const loadData = async () => {
      try {
        const [allItems, allCarpetas] = await Promise.all([
          itemsApi.fetchItems(),
          carpetasApi.fetchItems(),
        ]);

        const found = allItems.find((i) => i.uuid === uuid);
        setItem(found || null);
        setCarpetas(allCarpetas);
      } catch (error) {
        console.error('Error al cargar el item', error);
      } finally {
        setLoading(false);
      }
    };
    loadData();
  }, [uuid]);

  const handleCopy = () => { if (item?.contrasenya) navigator.clipboard.writeText(item.contrasenya); };
  const toggleShowPassword = () => setShowPassword(!showPassword);
  const estaEnCarpeta = item
    ? carpetas.some((carpeta) =>
      carpeta.items.some((i) => i.uuid === item.uuid)
    )
    : false;

  // Solo abre el modal
  const handleDelete = () => {
    if (!item) return;
    setOpenDeleteModal(true);
  };

  // Función que elimina el item
  const confirmDelete = async () => {
    if (!item) return;
    try {
      await itemsApi.deleteItem(item.uuid);
      setOpenDeleteModal(false);
      navigate('/home');
    } catch (error) {
      console.error("Error eliminando el item:", error);
    }
  };

  return (
    <AppTheme>
      <CssBaseline enableColorScheme />
      <Stack direction="row" sx={{ minHeight: '100vh', width: '100%' }}>
        <Sidebar />

        <Stack sx={{ flex: 1, bgcolor: 'background.default', overflow: 'auto', minWidth: 0 }}>
          {/* HEADER */}
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
                {item?.titol || 'Item'}
              </Typography>
            </Stack>

            <Stack direction="row" sx={{ gap: 2, alignItems: 'center' }}>
              <Avatar sx={{ bgcolor: 'grey.500', width: 36, height: 36, fontSize: 15, fontWeight: 700 }}>U</Avatar>
              <IconButton onClick={() => navigate('/')} size="small" sx={{ border: 'none', bgcolor: 'transparent', '&:hover': { bgcolor: 'action.hover' } }}>
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

          {/* CONTENIDO */}
          <Box sx={{ px: 4, py: 3 }}>
            {loading ? (
              <Stack sx={{ alignItems: 'center', mt: 10 }}>
                <CircularProgress />
              </Stack>
            ) : !item ? (
              <Typography color="error" sx={{ mt: 4 }}>
                No s'ha trobat l'item.
              </Typography>
            ) : (
              <Paper
                variant="outlined"
                sx={{ p: 3, borderRadius: '12px', border: '1px solid', borderColor: 'divider', display: 'flex', flexDirection: 'column', gap: 2 }}
              >
                <Stack direction="row" sx={{ justifyContent: 'space-between', alignItems: 'center' }}>
                  <Stack direction="row" sx={{ alignItems: 'center', gap: 1 }}>
                    {estaEnCarpeta && <FolderOutlinedIcon />}
                    <Typography variant="h5" sx={{ fontWeight: 700 }}>{item.titol}</Typography>
                  </Stack>

                  <Stack direction="row" sx={{ gap: 0.5 }}>
                    <IconButton
                      onClick={() => navigate('/EditItem', { state: { uuid: item.uuid } })}
                      sx={{ border: '1px solid', borderColor: 'divider' }}
                    >
                      <EditOutlinedIcon fontSize="small" />
                    </IconButton>
                    <IconButton
                      onClick={handleDelete}
                      sx={{ border: '1px solid', borderColor: 'divider', bgcolor: 'error.main', color: 'white', '&:hover': { bgcolor: 'error.dark' } }}
                    >
                      <DeleteOutlinedIcon fontSize="small" />
                    </IconButton>
                  </Stack>
                </Stack>

                <Divider />

                <Typography><strong>Usuari / Email:</strong> {item.nomUsuari}</Typography>
                <Typography><strong>URL:</strong> {item.url}</Typography>

                <Stack direction="row" sx={{ alignItems: 'center', gap: 1 }}>
                  <Typography>
                    <strong>Contrasenya:</strong> {showPassword ? item.contrasenya : item.contrasenya ? '********' : 'No disponible'}
                  </Typography>

                  <Stack direction="row" spacing={1}>
                    <IconButton onClick={handleCopy} sx={{ borderRadius: 2, border: '1px solid', borderColor: 'divider', color: 'primary.main' }}>
                      <ContentCopyOutlinedIcon fontSize="small" />
                    </IconButton>
                    <IconButton onClick={toggleShowPassword} sx={{ borderRadius: 2, border: '1px solid', borderColor: 'divider', color: 'text.secondary' }}>
                      {showPassword ? <VisibilityOffIcon fontSize="small" /> : <VisibilityIcon fontSize="small" />}
                    </IconButton>
                  </Stack>
                </Stack>

                <Typography><strong>Data creació:</strong> {item.dataCreacio}</Typography>
                <Typography><strong>Última modificació:</strong> {item.dataEditat}</Typography>
                <Typography><strong>Últim accés:</strong> {item.ultimAcces}</Typography>
              </Paper>
            )}
          </Box>
          <DeleteConfirmationModal
            open={openDeleteModal}
            onClose={() => setOpenDeleteModal(false)}
            onConfirm={confirmDelete}
          />
        </Stack>
      </Stack>
    </AppTheme>
  );
}