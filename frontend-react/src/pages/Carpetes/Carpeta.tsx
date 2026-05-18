import { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router';
import { useTranslation } from 'react-i18next';
import Stack from '@mui/material/Stack';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import Typography from '@mui/material/Typography';
import Alert from '@mui/material/Alert';
import Skeleton from '@mui/material/Skeleton';
import Chip from '@mui/material/Chip';
import Dialog from '@mui/material/Dialog';
import DialogTitle from '@mui/material/DialogTitle';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogActions from '@mui/material/DialogActions';
import Button from '@mui/material/Button';
import FolderOutlinedIcon from '@mui/icons-material/FolderOutlined';
import ShareOutlinedIcon from '@mui/icons-material/ShareOutlined';
import VpnKeyOffOutlinedIcon from '@mui/icons-material/VpnKeyOffOutlined';
import CredentialCard from '../../components/CredentialCard';
import ShareModal from '../../components/ShareModal';
import Header from '../../components/Header';
import ItemsToolbar from '../../components/ItemsToolbar';
import CustomPagination from '../../components/CustomPagination';
import { itemsApi, type Item } from '../../api/itemsapi';
import { carpetasApi } from '../../api/carpetasapi';
import { compartitsApi, type Compartit, type CompartitItem } from '../../api/compartitsapi';
import { useAuth } from '../../context/AuthContext';
import toast from 'react-hot-toast';

type FilterValue = 'latest' | 'most_used' | 'favorites';
const ITEMS_PER_PAGE = 12;
type AnyItem = Item | CompartitItem;

export default function Carpeta() {
  const navigate = useNavigate();
  const location = useLocation();
  const { t } = useTranslation('folder');

  const { uuid, nombreCarpeta, compartitUuid } = location.state as {
    uuid: string;
    nombreCarpeta?: string;
    compartitUuid?: string;
  };

  const { usuari } = useAuth();
  const esCompartit = !!compartitUuid;

  const [items, setItems] = useState<AnyItem[]>([]);
  const [compartit, setCompartit] = useState<Compartit | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [search, setSearch] = useState('');
  const [filter, setFilter] = useState<FilterValue>('latest');
  const [page, setPage] = useState(1);
  const [openShareModal, setOpenShareModal] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState<Item | null>(null);
  const [openDeleteModal, setOpenDeleteModal] = useState(false);

  useEffect(() => {
    const load = async () => {
      setError(null);
      try {
        if (esCompartit) {
          const data = await compartitsApi.getCompartit(compartitUuid);
          if (!data || data.tipusEntitat !== 'CARPETA' || !data.carpeta) {
            setError(t('error.not_found_shared'));
            return;
          }
          setCompartit(data);

          const carpetaUuid = data.carpeta.uuid;
          const [rebuts, creats] = await Promise.all([
            compartitsApi.fetchCompartitsRebuts(),
            compartitsApi.fetchCompartitsCreats(),
          ]);

          const tots = [...(rebuts ?? []), ...(creats ?? [])];
          const itemsCompartits = tots
            .filter(
              (c) =>
                c.tipusEntitat === 'ITEM' &&
                c.item !== null &&
                Array.isArray(c.item?.carpeta) &&
                c.item.carpeta.some((cp: { uuid: string }) => cp.uuid === carpetaUuid)
            )
            .map((c) => c.item!);

          const itemsBase = data.carpeta.items ?? [];
          const uuidsBase = new Set(itemsBase.map((i) => i.uuid));
          const itemsExtra = itemsCompartits.filter((i) => !uuidsBase.has(i.uuid));
          setItems([...itemsBase, ...itemsExtra]);
        } else {
          const itemsData = await carpetasApi.fetchItemsFromCarpeta(uuid);
          setItems(itemsData);
        }
      } catch (err: unknown) {
        const message = err instanceof Error ? err.message : t('error.loading_items');
        setError(message);
        toast.error(message);
      } finally {
        setLoading(false);
      }
    };
    load();
  }, [uuid, compartitUuid, esCompartit, t]);

  useEffect(() => { setPage(1); }, [search, filter]);

  const handleAccess = async (itemUuid: string) => {
    if (esCompartit) return;
    try {
      const updated = await itemsApi.registrarAcces(itemUuid);
      if (updated) {
        setItems((prev) => prev.map((i) => i.uuid === itemUuid ? { ...i, ...updated } : i));
      }
    } catch {}
  };

  const handleDeleteClick = (item: Item) => {
    setDeleteTarget(item);
    setOpenDeleteModal(true);
  };

  const handleCloseDeleteModal = () => {
    setOpenDeleteModal(false);
    setDeleteTarget(null);
  };

  const handleRemoveFromCarpeta = async () => {
    if (!deleteTarget) return;
    try {
      await carpetasApi.removeItem(uuid, deleteTarget.uuid);
      setItems((prev) => prev.filter((i) => i.uuid !== deleteTarget.uuid));
      toast.success(t('item.removed_from_folder_success'));
    } catch {
      toast.error(t('item.removed_from_folder_error'));
    } finally {
      handleCloseDeleteModal();
    }
  };

  const handleDeleteDefinitiu = async () => {
    if (!deleteTarget) return;
    try {
      await itemsApi.deleteItem(deleteTarget.uuid);
      setItems((prev) => prev.filter((i) => i.uuid !== deleteTarget.uuid));
      toast.success(t('item.deleted_success'));
    } catch {
      toast.error(t('item.deleted_error'));
    } finally {
      handleCloseDeleteModal();
    }
  };

  const getUltimAcces = (item: AnyItem): string => {
    if ('ultimAcces' in item) return item.ultimAcces ?? '';
    if ('ultimAccess' in item) return item.ultimAccess ?? '';
    return '';
  };

  const filteredItems = items
    .filter((item) =>
      filter === 'favorites'
        ? item.favorit
        : item.titol.toLowerCase().includes(search.toLowerCase()) ||
          item.nomUsuari.toLowerCase().includes(search.toLowerCase())
    )
    .sort((a, b) => {
      if (filter === 'latest')
        return new Date(getUltimAcces(b)).getTime() - new Date(getUltimAcces(a)).getTime();
      if (filter === 'most_used') {
        const diff = (b.comptadorAccess ?? 0) - (a.comptadorAccess ?? 0);
        if (diff !== 0) return diff;
        return new Date(getUltimAcces(b)).getTime() - new Date(getUltimAcces(a)).getTime();
      }
      return 0;
    });

  const totalPages = Math.ceil(filteredItems.length / ITEMS_PER_PAGE);
  const paginatedItems = filteredItems.slice((page - 1) * ITEMS_PER_PAGE, page * ITEMS_PER_PAGE);

  const esPropietari = compartit?.usuariCreador.uuid === usuari?.uuid;
  const nomCarpeta = esCompartit
    ? compartit?.carpeta?.nom ?? t('title.shared_folder_fallback')
    : nombreCarpeta ?? t('title.shared_folder_fallback');

  const renderContent = () => {
    if (loading) {
      return (
        <Grid container spacing={2}>
          {Array.from({ length: 6 }).map((_, i) => (
            <Grid size={4} key={i}>
              <Skeleton variant="rounded" height={110} sx={{ borderRadius: '10px' }} />
            </Grid>
          ))}
        </Grid>
      );
    }
    if (error) return <Alert severity="error" sx={{ mt: 2 }}>{error}</Alert>;
    if (filteredItems.length === 0) {
      return (
        <Stack sx={{ alignItems: 'center', py: 10, gap: 2, color: 'text.disabled' }}>
          <VpnKeyOffOutlinedIcon sx={{ fontSize: 64 }} />
          <Typography variant="body1" sx={{ fontWeight: 600 }}>
            {search ? t('empty.search') : t('empty.no_items')}
          </Typography>
        </Stack>
      );
    }
    return (
      <Grid container spacing={2}>
        {paginatedItems.map((item) => (
          <Grid size={4} key={item.uuid}>
            <CredentialCard
              uuid={item.uuid}
              titol={item.titol}
              nomUsuari={item.nomUsuari}
              dataEditat={item.dataEditat}
              dataCreacio={item.dataCreacio}
              ultimAcces={getUltimAcces(item)}
              url={item.url}
              favorit={item.favorit}
              dinsCarpeta={true}
              showFavorit={!esCompartit}
              onAccess={!esCompartit ? handleAccess : undefined}
              onClick={() =>
                navigate('/Item', {
                  state: esCompartit
                    ? { compartitUuid, itemUuid: item.uuid }
                    : { uuid: item.uuid },
                })
              }
              onEdit={!esCompartit ? () => navigate('/EditItem', { state: { uuid: item.uuid } }) : undefined}
              onDelete={!esCompartit ? () => handleDeleteClick(item as Item) : undefined}
            />
          </Grid>
        ))}
      </Grid>
    );
  };

  return (
    <Stack sx={{ height: '100%', overflow: 'hidden' }}>
      <Header
        title={nomCarpeta}
        icon={<FolderOutlinedIcon sx={{ fontSize: 30, color: 'text.primary' }} />}
        showBackButton
        onShare={!esCompartit ? () => setOpenShareModal(true) : undefined}
      />

      <Stack sx={{ flex: 1, overflow: 'auto', minWidth: 0 }}>
        {esCompartit && compartit && (
          <Stack direction="row" sx={{ px: 4, pt: 2, gap: 1 }}>
            <Chip
              icon={<ShareOutlinedIcon />}
              label={
                esPropietari
                  ? t('shared.with', { name: compartit.usuariReceptor.nom })
                  : t('shared.from', { name: compartit.usuariCreador.nom })
              }
            />
          </Stack>
        )}

        <ItemsToolbar
          search={search}
          setSearch={setSearch}
          filter={filter}
          setFilter={setFilter}
          onAdd={!esCompartit ? () => navigate('/AddItem', { state: { carpetaUuid: uuid } }) : undefined}
        />

        <Box sx={{ px: 4, pb: 3, flex: 1 }}>{renderContent()}</Box>

        {!loading && !error && filteredItems.length > 0 && (
          <CustomPagination count={totalPages} page={page} onChange={setPage} />
        )}
      </Stack>

      <Dialog open={openDeleteModal} onClose={handleCloseDeleteModal}>
        <DialogTitle sx={{ fontWeight: 700, color: 'text.primary' }}>
          {t('delete_modal.title')}
        </DialogTitle>
        <DialogContent>
          <DialogContentText sx={{ color: 'text.secondary' }}>
            {t('delete_modal.description', { titol: deleteTarget?.titol ?? '' })}
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseDeleteModal} sx={{ textTransform: 'none', fontWeight: 600 }}>
            {t('delete_modal.cancel')}
          </Button>
          <Button onClick={handleRemoveFromCarpeta} variant="outlined" sx={{ textTransform: 'none', fontWeight: 600 }}>
            {t('delete_modal.remove_from_folder')}
          </Button>
          <Button
            onClick={handleDeleteDefinitiu}
            sx={{ textTransform: 'none', fontWeight: 600, bgcolor: 'error.main', color: 'white', '&:hover': { bgcolor: 'error.dark' } }}
          >
            {t('delete_modal.delete_permanently')}
          </Button>
        </DialogActions>
      </Dialog>

      {!esCompartit && (
        <ShareModal
          open={openShareModal}
          onClose={() => setOpenShareModal(false)}
          tipusEntitat="CARPETA"
          entitatUuid={uuid}
          entitatNom={nombreCarpeta ?? ''}
        />
      )}
    </Stack>
  );
}