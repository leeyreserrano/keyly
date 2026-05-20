import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router';
import { useTranslation } from 'react-i18next';
import Stack from '@mui/material/Stack';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import Typography from '@mui/material/Typography';
import Alert from '@mui/material/Alert';
import Skeleton from '@mui/material/Skeleton';
import KeyRoundedIcon from '@mui/icons-material/VpnKeyRounded';
import VpnKeyOffOutlinedIcon from '@mui/icons-material/VpnKeyOffOutlined';
import Header from '../../components/Header';
import ItemsToolbar from '../../components/ItemsToolbar';
import CustomPagination from '../../components/CustomPagination';
import DeleteConfirmationModal from '../../components/DeleteConfirmationModal';
import CredentialCard from '../../components/CredentialCard';
import { itemsApi, type Item } from '../../api/itemsapi';
import { carpetasApi, type Carpeta } from '../../api/carpetasapi';
import toast from 'react-hot-toast';

export type FilterValue = 'latest' | 'most_used' | 'favorites';
const ITEMS_PER_PAGE = 12;

export default function Items() {
  const navigate = useNavigate();
  const { t } = useTranslation('item');

  const [items, setItems] = useState<Item[]>([]);
  const [carpetas, setCarpetas] = useState<Carpeta[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [search, setSearch] = useState('');
  const [filter, setFilter] = useState<FilterValue>('latest');
  const [page, setPage] = useState(1);
  const [openDeleteModal, setOpenDeleteModal] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState<Item | null>(null);

  useEffect(() => {
    const loadData = async () => {
      setError(null);
      try {
        const [itemsData, carpetasData] = await Promise.all([
          itemsApi.fetchItems(),
          carpetasApi.fetchItems(),
        ]);
        setItems(itemsData ?? []);
        setCarpetas(carpetasData ?? []);
      } catch (err: unknown) {
        const message = err instanceof Error ? err.message : t('item.error.load');
        setError(message);
      } finally {
        setLoading(false);
      }
    };
    loadData();
  }, [t]);

  useEffect(() => { setPage(1); }, [search, filter]);

  const handleAccess = async (uuid: string) => {
    try {
      const updated = await itemsApi.registrarAcces(uuid);
      if (updated) setItems((prev) => prev.map((i) => (i.uuid === uuid ? { ...i, ...updated } : i)));
    } catch { }
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
        return new Date(b.ultimAcces).getTime() - new Date(a.ultimAcces).getTime();
      if (filter === 'most_used') {
        const diff = (b.comptadorAccess ?? 0) - (a.comptadorAccess ?? 0);
        if (diff !== 0) return diff;
        return new Date(b.ultimAcces).getTime() - new Date(a.ultimAcces).getTime();
      }
      return 0;
    });

  const totalPages = Math.ceil(filteredItems.length / ITEMS_PER_PAGE);
  const paginatedItems = filteredItems.slice((page - 1) * ITEMS_PER_PAGE, page * ITEMS_PER_PAGE);

  const handleDelete = (item: Item) => { setDeleteTarget(item); setOpenDeleteModal(true); };

  const confirmDelete = async () => {
    if (!deleteTarget) return;
    try {
      await itemsApi.deleteItem(deleteTarget.uuid);
      setItems((prev) => prev.filter((i) => i.uuid !== deleteTarget.uuid));
      toast.success(t('item.toast.delete'));
      setOpenDeleteModal(false);
      setDeleteTarget(null);
    } catch {
      toast.error(t('item.error.delete'));
    }
  };

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
            {search
              ? t('list.empty.search')
              : filter === 'favorites'
              ? t('item.toast.fav_remove')
              : t('list.empty.no_items')}
          </Typography>
          {!search && filter !== 'favorites' && (
            <Typography variant="body2" color="text.secondary">
              {t('list.empty.hint')}
            </Typography>
          )}
        </Stack>
      );
    }
    return (
      <Grid container spacing={2}>
        {paginatedItems.map((item) => {
          const estaEnCarpeta = carpetas.some((c) => c.items.some((i) => i.uuid === item.uuid));
          return (
            <Grid size={4} key={item.uuid}>
              <CredentialCard
                uuid={item.uuid}
                titol={item.titol}
                nomUsuari={item.nomUsuari}
                dataEditat={item.dataEditat}
                dataCreacio={item.dataCreacio}
                ultimAcces={item.ultimAcces}
                url={item.url}
                dinsCarpeta={estaEnCarpeta}
                favorit={item.favorit}
                onAccess={(uuid) => handleAccess(uuid)}
                onClick={() => navigate('/Item', { state: { uuid: item.uuid } })}
                onEdit={() => navigate('/EditItem', { state: { uuid: item.uuid } })}
                onDelete={() => handleDelete(item)}
              />
            </Grid>
          );
        })}
      </Grid>
    );
  };

  return (
    <Stack sx={{ height: '100%', overflow: 'hidden' }}>
      <Header
        title={t('item.title')}
        icon={<KeyRoundedIcon sx={{ fontSize: 30, color: 'text.primary' }} />}
      />

      <Stack sx={{ flex: 1, overflow: 'auto', minWidth: 0 }}>
        <ItemsToolbar
          search={search}
          setSearch={setSearch}
          filter={filter}
          setFilter={setFilter}
          onAdd={() => navigate('/AddItem')}
        />
        <Box sx={{ px: 4, pb: 3, flex: 1 }}>
          {renderContent()}
        </Box>
        {!loading && !error && filteredItems.length > 0 && (
          <CustomPagination count={totalPages} page={page} onChange={setPage} />
        )}
      </Stack>

      <DeleteConfirmationModal
        open={openDeleteModal}
        onClose={() => setOpenDeleteModal(false)}
        onConfirm={confirmDelete}
      />
    </Stack>
  );
}