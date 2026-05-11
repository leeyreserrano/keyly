import { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router';
import { useTranslation } from 'react-i18next';
import {
  Stack, Typography, Paper, IconButton, Divider,
  Box, CircularProgress, Chip,
} from '@mui/material';
import KeyRoundedIcon from '@mui/icons-material/VpnKeyRounded';
import FolderOutlinedIcon from '@mui/icons-material/FolderOutlined';
import ContentCopyOutlinedIcon from '@mui/icons-material/ContentCopyOutlined';
import VisibilityIcon from '@mui/icons-material/Visibility';
import VisibilityOffIcon from '@mui/icons-material/VisibilityOff';
import ShareOutlinedIcon from '@mui/icons-material/ShareOutlined';
import LockOutlinedIcon from '@mui/icons-material/LockOutlined';
import EditOutlinedIcon from '@mui/icons-material/EditOutlined';
import DeleteConfirmationModal from '../../components/DeleteConfirmationModal';
import Header from '../../components/Header';
import ActionButtons from '../../components/ActionButtons';
import ShareModal from '../../components/ShareModal';
import { useTimeRefresh } from '../../components/UseTimeRefresh';
import { getTimeAgo, formatDate } from '../../utils/timeUtils';
import { itemsApi, type Item } from '../../api/itemsapi';
import { carpetasApi, type Carpeta } from '../../api/carpetasapi';
import { compartitsApi, type Compartit, type CompartitItem } from '../../api/compartitsapi';
import { useCrypto } from '../../context/CryptoContext';
import { useAuth } from '../../context/AuthContext';
import { rsaDecrypt, decryptPasswordWithDataKey } from '../../crypto/cryptoService';
import toast from 'react-hot-toast';

const getFavicon = (url?: string): string | null => {
  if (!url) return null;
  try {
    const domain = new URL(url).hostname;
    return `https://icons.duckduckgo.com/ip3/${domain}.ico`;
  } catch {
    return null;
  }
};

export default function ItemPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const { t } = useTranslation('item');
  const { uuid, compartitUuid, itemUuid } = location.state || {};
  const { privateKey } = useCrypto();
  const { usuari } = useAuth();

  const esCompartit = !!compartitUuid;

  const [item, setItem] = useState<Item | CompartitItem | null>(null);
  const [compartit, setCompartit] = useState<Compartit | null>(null);
  const [carpetas, setCarpetas] = useState<Carpeta[]>([]);
  const [loading, setLoading] = useState(true);
  const [showPassword, setShowPassword] = useState(false);
  const [decryptedPassword, setDecryptedPassword] = useState<string | null>(null);
  const [openDeleteModal, setOpenDeleteModal] = useState(false);
  const [openShareModal, setOpenShareModal] = useState(false);
  const [isFavorit, setIsFavorit] = useState(false);
  const [faviconError, setFaviconError] = useState(false);

  const now = useTimeRefresh(60000);

  useEffect(() => {
    const targetUuid = esCompartit ? compartitUuid : uuid;
    if (!targetUuid) return;

    const load = async () => {
      try {
        if (esCompartit) {
          const data = await compartitsApi.getCompartit(compartitUuid);
          setCompartit(data);
          setFaviconError(false);
          if (!data) return;
          let targetItem: CompartitItem | null = data.item ?? null;
          if (itemUuid && data.carpeta) targetItem = data.carpeta.items.find((i) => i.uuid === itemUuid) ?? null;
          setItem(targetItem);
          setIsFavorit(targetItem?.favorit ?? false);
          if (!targetItem || !privateKey) { setDecryptedPassword(null); return; }
          if (!targetItem.encryptedDataKey?.encryptedDataKey || !targetItem.iv || !targetItem.contrasenya) {
            setDecryptedPassword(targetItem.contrasenya ?? null); return;
          }
          try {
            const dataKeyBytes = await rsaDecrypt(privateKey, targetItem.encryptedDataKey.encryptedDataKey);
            const plain = await decryptPasswordWithDataKey(dataKeyBytes, targetItem.contrasenya, targetItem.iv);
            setDecryptedPassword(plain);
          } catch {
            setDecryptedPassword(null);
          }
        } else {
          const [allItems, allCarpetas] = await Promise.all([itemsApi.fetchItems(), carpetasApi.fetchItems()]);
          const found = allItems?.find((i) => i.uuid === uuid) ?? null;
          setItem(found);
          setIsFavorit(found?.favorit ?? false);
          setCarpetas(allCarpetas);
          setFaviconError(false);
          if (!found || !privateKey) { setDecryptedPassword(null); return; }
          if (!found.encryptedDataKey?.encryptedDataKey || !found.iv || !found.contrasenya) {
            setDecryptedPassword(found.contrasenya ?? null); return;
          }
          try {
            const dataKeyBytes = await rsaDecrypt(privateKey, found.encryptedDataKey.encryptedDataKey);
            const plain = await decryptPasswordWithDataKey(dataKeyBytes, found.contrasenya, found.iv);
            setDecryptedPassword(plain);
          } catch {
            setDecryptedPassword(null);
          }
        }
      } catch {
        toast.error(t('item.error.load'));
      } finally {
        setLoading(false);
      }
    };
    load();
  }, [uuid, compartitUuid, itemUuid, privateKey, esCompartit, t]);

  const toggleFavorit = async () => {
    if (!item || esCompartit) return;
    const newValue = !isFavorit;
    try {
      const current = await itemsApi.getItem((item as Item).uuid);
      if (!current) throw new Error('No trobat');
      await itemsApi.updateItem((item as Item).uuid, {
        titol: current.titol,
        nomUsuari: current.nomUsuari,
        contrasenya: current.contrasenya,
        iv: current.iv,
        encryptedDataKey: current.encryptedDataKey?.encryptedDataKey,
        url: current.url,
        notes: current.notes,
        favorit: newValue,
      });
      setIsFavorit(newValue);
      toast.success(newValue ? t('item.toast.fav_add') : t('item.toast.fav_remove'));
    } catch {
      toast.error(t('item.error.fav'));
    }
  };

  const handleCopy = async () => {
    if (!decryptedPassword) return;
    try {
      await navigator.clipboard.writeText(decryptedPassword);
      toast.success(t('item.toast.copy'));
    } catch {
      toast.error(t('item.error.copy'));
    }
  };

  const estaEnCarpeta = !esCompartit && item
    ? carpetas.some((c) => c.items.some((i) => i.uuid === item.uuid))
    : (item as CompartitItem)?.dinsDeCarpeta ?? false;

  const confirmDelete = async () => {
    try {
      if (esCompartit && compartit) {
        await compartitsApi.deleteCompartit(compartit.uuid);
        toast.success(t('item.toast.delete_share'));
      } else if (item) {
        await itemsApi.deleteItem((item as Item).uuid);
        toast.success(t('item.toast.delete'));
      }
      navigate(-1);
    } catch {
      toast.error(t('item.error.delete'));
    }
  };

  const passwordDisplay = () => {
    if (!privateKey) return t('item.error.session');
    if (!decryptedPassword) return t('item.password.na');
    return showPassword ? decryptedPassword : '••••••••';
  };

  const esPropietari = compartit?.usuariCreador.uuid === usuari?.uuid;
  const esEscriptura = compartit?.permisos === 'ESCRIPTURA';
  const faviconUrl = getFavicon(item?.url);

  const headerIcon = faviconUrl && !faviconError ? (
    <img src={faviconUrl} alt="" style={{ width: 30, height: 30, objectFit: 'contain' }} onError={() => setFaviconError(true)} />
  ) : (
    <KeyRoundedIcon sx={{ fontSize: 30, color: 'text.primary' }} />
  );

  return (
    <Stack sx={{ height: '100%', overflow: 'hidden' }}>
      <Header title={item?.titol || t('item.title')} icon={headerIcon} showBackButton />

      <Stack sx={{ flex: 1, overflow: 'auto', minWidth: 0 }}>
        <Box sx={{ px: 4, py: 3 }}>
          {loading ? (
            <Stack sx={{ alignItems: 'center', mt: 10 }}><CircularProgress /></Stack>
          ) : !item ? (
            <Typography color="error" sx={{ mt: 4 }}>{t('item.error.not_found')}</Typography>
          ) : (
            <Paper variant="outlined" sx={{ p: 3, borderRadius: '12px', border: '1px solid', borderColor: 'divider', display: 'flex', flexDirection: 'column', gap: 2 }}>
              <Stack direction="row" sx={{ justifyContent: 'space-between', alignItems: 'center' }}>
                <Stack direction="row" sx={{ alignItems: 'center', gap: 1 }}>
                  {estaEnCarpeta && <FolderOutlinedIcon />}
                  <Typography variant="h5" sx={{ fontWeight: 700 }}>{item.titol}</Typography>
                </Stack>
                <Stack direction="row" sx={{ alignItems: 'center', gap: 1 }}>
                  {esCompartit && compartit && (
                    <>
                      <Chip
                        icon={esEscriptura ? <EditOutlinedIcon sx={{ fontSize: 14 }} /> : <LockOutlinedIcon sx={{ fontSize: 14 }} />}
                        label={esEscriptura ? t('item.permission.write') : t('item.permission.read')}
                        size="small" variant="outlined" sx={{ fontSize: '0.7rem' }}
                      />
                      <Chip
                        icon={<ShareOutlinedIcon sx={{ fontSize: 14 }} />}
                        label={esPropietari
                          ? t('item.shared.with', { name: compartit.usuariReceptor.nom })
                          : t('item.shared.from', { name: compartit.usuariCreador.nom })}
                        size="small" variant="outlined" sx={{ fontSize: '0.7rem' }}
                      />
                    </>
                  )}
                  {!esCompartit ? (
                    <ActionButtons
                      isFavorit={isFavorit}
                      onToggleFavorit={toggleFavorit}
                      onEdit={() => navigate('/EditItem', { state: { uuid: (item as Item).uuid } })}
                      onDelete={() => setOpenDeleteModal(true)}
                      onShare={(e) => { e.stopPropagation(); setOpenShareModal(true); }}
                      size="card" gap={0.5} showFolderIcon={false}
                    />
                  ) : esEscriptura ? (
                    <ActionButtons
                      onEdit={() => navigate('/EditItem', { state: { uuid: item.uuid, compartitUuid, itemUuid } })}
                      size="card" gap={0.5}
                    />
                  ) : null}
                </Stack>
              </Stack>

              <Divider />
              <Typography><strong>{t('item.field.user')}:</strong> {item.nomUsuari}</Typography>
              <Typography><strong>{t('item.field.notes')}:</strong> {item.notes ?? '—'}</Typography>
              <Typography><strong>{t('item.field.url')}:</strong> {item.url ?? '—'}</Typography>

              <Stack direction="row" sx={{ alignItems: 'center', gap: 1 }}>
                <Typography><strong>{t('item.field.password')}:</strong> {passwordDisplay()}</Typography>
                <Stack direction="row" spacing={1}>
                  <IconButton onClick={handleCopy} disabled={!decryptedPassword} sx={{ borderRadius: 2, border: '1px solid', borderColor: 'divider', color: 'primary.main' }}>
                    <ContentCopyOutlinedIcon fontSize="small" />
                  </IconButton>
                  <IconButton onClick={() => setShowPassword((p) => !p)} disabled={!decryptedPassword} sx={{ borderRadius: 2, border: '1px solid', borderColor: 'divider', color: 'text.secondary' }}>
                    {showPassword ? <VisibilityOffIcon fontSize="small" /> : <VisibilityIcon fontSize="small" />}
                  </IconButton>
                </Stack>
              </Stack>

              <Divider />

              {esCompartit && compartit ? (
                <>
                  <Typography><strong>{t('item.field.owner')}:</strong> {compartit.usuariCreador.nom}</Typography>
                  <Typography><strong>{t('item.field.receiver')}:</strong> {compartit.usuariReceptor.nom}</Typography>
                  <Typography><strong>{t('item.field.share_date')}:</strong> {formatDate(compartit.dataCreacio)}</Typography>
                </>
              ) : (
                <Typography><strong>{t('item.field.created')}:</strong> {formatDate((item as Item).dataCreacio)}</Typography>
              )}
              <Typography><strong>{t('item.field.last_modified')}:</strong> {getTimeAgo(item.dataEditat, now)}</Typography>
              {!esCompartit && <Typography><strong>{t('item.field.last_access')}:</strong> {getTimeAgo((item as Item).ultimAcces, now)}</Typography>}
            </Paper>
          )}
        </Box>
      </Stack>

      <DeleteConfirmationModal open={openDeleteModal} onClose={() => setOpenDeleteModal(false)} onConfirm={confirmDelete} />
      {!esCompartit && item && (
        <ShareModal open={openShareModal} onClose={() => setOpenShareModal(false)} tipusEntitat="ITEM" entitatUuid={(item as Item).uuid} entitatNom={item.titol} />
      )}
    </Stack>
  );
}