import { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router';
import { Stack, Typography, Paper, IconButton, Divider, Box, CircularProgress, CssBaseline } from '@mui/material';
import KeyRoundedIcon from '@mui/icons-material/VpnKeyRounded';
import FolderOutlinedIcon from '@mui/icons-material/FolderOutlined';
import ContentCopyOutlinedIcon from '@mui/icons-material/ContentCopyOutlined';
import VisibilityIcon from '@mui/icons-material/Visibility';
import VisibilityOffIcon from '@mui/icons-material/VisibilityOff';
import DeleteConfirmationModal from '../../components/DeleteConfirmationModal';
import Sidebar from '../../components/Sidebar';
import AppTheme from '../../theme/AppTheme';
import { itemsApi, type Item } from '../../api/itemsapi';
import { carpetasApi, type Carpeta } from '../../api/carpetasapi';
import toast from 'react-hot-toast';
import Header from '../../components/Header';
import ActionButtons from '../../components/ActionButtons';
import { useTimeRefresh } from '../../components/UseTimeRefresh';
import { getTimeAgo, formatDate } from '../../utils/timeUtils';
import ShareModal from '../../components/ShareModal';
import { useCrypto } from '../../context/CryptoContext';
import { decryptPassword } from '../../crypto/cryptoService';

function esXifrat(iv: string): boolean {
  try {
    return atob(iv).length === 12;
  } catch {
    return false;
  }
}

export default function ItemPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const { uuid } = location.state || {};
  const { derivedKey } = useCrypto();

  const [item, setItem] = useState<Item | null>(null);
  const [carpetas, setCarpetas] = useState<Carpeta[]>([]);
  const [loading, setLoading] = useState(true);
  const [showPassword, setShowPassword] = useState(false);
  const [decryptedPassword, setDecryptedPassword] = useState<string | null>(null);
  const [openDeleteModal, setOpenDeleteModal] = useState(false);
  const [openShareModal, setOpenShareModal] = useState(false);
  const [isFavorit, setIsFavorit] = useState(false);

  const now = useTimeRefresh(60000);

  useEffect(() => {
    if (!uuid) return;

    const loadData = async () => {
      try {
        const [allItems, allCarpetas] = await Promise.all([
          itemsApi.fetchItems(),
          carpetasApi.fetchItems(),
        ]);

        const found = allItems?.find((i) => i.uuid === uuid) ?? null;
        setItem(found);
        setIsFavorit(found?.favorit ?? false);
        setCarpetas(allCarpetas);

        if (!found) return;

        if (!derivedKey) {
          setDecryptedPassword(null);
          return;
        }

        if (!found.iv || !found.contrasenya || !esXifrat(found.iv)) {
          setDecryptedPassword(found.contrasenya ?? null);
          return;
        }

        try {
          const plain = await decryptPassword(derivedKey, found.contrasenya, found.iv);
          setDecryptedPassword(plain);
        } catch {
          setDecryptedPassword(found.contrasenya);
        }
      } catch (error) {
        console.error('Error al cargar el item', error);
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, [uuid, derivedKey]);

  const toggleFavorit = async () => {
    if (!item) return;
    const newValue = !isFavorit;
    try {
      await itemsApi.updateItem(item.uuid, { favorit: newValue });
      setIsFavorit(newValue);
      toast.success(newValue ? 'Marcat com a favorit' : 'Eliminat de favorits');
    } catch {
      toast.error('Error al actualitzar favorit');
    }
  };

  const handleCopy = async () => {
    if (!decryptedPassword) return;
    try {
      await navigator.clipboard.writeText(decryptedPassword);
      toast.success('Contrasenya copiada al portapapers');
    } catch {
      toast.error('Error al copiar');
    }
  };

  const toggleShowPassword = () => setShowPassword((prev) => !prev);

  const estaEnCarpeta = item
    ? carpetas.some((carpeta) => carpeta.items.some((i) => i.uuid === item.uuid))
    : false;

  const handleDelete = () => {
    if (!item) return;
    setOpenDeleteModal(true);
  };

  const confirmDelete = async () => {
    if (!item) return;
    try {
      await itemsApi.deleteItem(item.uuid);
      toast.success('Item eliminat');
      navigate(-1);
    } catch {
      toast.error('Error eliminant item');
    }
  };

  const passwordDisplay = () => {
    if (!derivedKey) return 'Sessió expirada, torna a iniciar sessió';
    if (!decryptedPassword) return 'No disponible';
    return showPassword ? decryptedPassword : '********';
  };

  return (
    <AppTheme>
      <CssBaseline enableColorScheme />
      <Stack direction="row" sx={{ minHeight: '100vh', width: '100%' }}>
        <Sidebar />

        <Stack sx={{ flex: 1, bgcolor: 'background.default', overflow: 'auto', minWidth: 0 }}>
          <Header
            title={item?.titol || 'Item'}
            icon={<KeyRoundedIcon sx={{ fontSize: 30, color: 'text.primary' }} />}
            showBackButton={true}
          />

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
                    <Typography variant="h5" sx={{ fontWeight: 700 }}>
                      {item.titol}
                    </Typography>
                  </Stack>

                  <ActionButtons
                    isFavorit={isFavorit}
                    onToggleFavorit={toggleFavorit}
                    onEdit={() => navigate('/EditItem', { state: { uuid: item.uuid } })}
                    onDelete={handleDelete}
                    onShare={(e) => { e.stopPropagation(); setOpenShareModal(true); }}
                    size="card"
                    gap={0.5}
                    showFolderIcon={false}
                  />
                </Stack>

                <Divider />

                <Typography><strong>Usuari / Email:</strong> {item.nomUsuari}</Typography>
                <Typography><strong>Notes:</strong> {item.notes}</Typography>
                <Typography><strong>URL:</strong> {item.url}</Typography>

                <Stack direction="row" sx={{ alignItems: 'center', gap: 1 }}>
                  <Typography>
                    <strong>Contrasenya:</strong>{' '}
                    {passwordDisplay()}
                  </Typography>

                  <Stack direction="row" spacing={1}>
                    <IconButton
                      onClick={handleCopy}
                      disabled={!decryptedPassword}
                      sx={{ borderRadius: 2, border: '1px solid', borderColor: 'divider', color: 'primary.main' }}
                    >
                      <ContentCopyOutlinedIcon fontSize="small" />
                    </IconButton>
                    <IconButton
                      onClick={toggleShowPassword}
                      disabled={!decryptedPassword}
                      sx={{ borderRadius: 2, border: '1px solid', borderColor: 'divider', color: 'text.secondary' }}
                    >
                      {showPassword ? <VisibilityOffIcon fontSize="small" /> : <VisibilityIcon fontSize="small" />}
                    </IconButton>
                  </Stack>
                </Stack>

                <Typography>
                  <strong>Data creació:</strong> {formatDate(item.dataCreacio)}
                </Typography>
                <Typography>
                  <strong>Última modificació:</strong> {getTimeAgo(item.dataEditat, now)}
                </Typography>
                <Typography>
                  <strong>Últim accés:</strong> {getTimeAgo(item.ultimAcces, now)}
                </Typography>
              </Paper>
            )}
          </Box>

          <DeleteConfirmationModal
            open={openDeleteModal}
            onClose={() => setOpenDeleteModal(false)}
            onConfirm={confirmDelete}
          />

          {item && (
            <ShareModal
              open={openShareModal}
              onClose={() => setOpenShareModal(false)}
              tipusEntitat="ITEM"
              entitatUuid={item.uuid}
              entitatNom={item.titol}
            />
          )}
        </Stack>
      </Stack>
    </AppTheme>
  );
}