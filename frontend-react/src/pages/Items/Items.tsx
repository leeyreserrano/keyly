import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router';
import CssBaseline from '@mui/material/CssBaseline';
import Stack from '@mui/material/Stack';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import Grid from '@mui/material/Grid';
import Alert from '@mui/material/Alert';
import Skeleton from '@mui/material/Skeleton';
import KeyRoundedIcon from '@mui/icons-material/VpnKeyRounded';
import VpnKeyOffOutlinedIcon from '@mui/icons-material/VpnKeyOffOutlined';

import AppTheme from '../../theme/AppTheme';
import Sidebar from '../../components/Sidebar';
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
        const message =
          err instanceof Error ? err.message : 'Error carregant les dades';
        setError(message);
      } finally {
        setLoading(false);
      }
    };
    loadData();
  }, []);

  // Reset página cuando cambia filtro o búsqueda
  useEffect(() => {
    setPage(1);
  }, [search, filter]);

  // Filtrado y ordenación
  const filteredItems = items
    .filter((item) =>
      filter === 'favorites'
        ? item.favorit
        : item.titol.toLowerCase().includes(search.toLowerCase()) ||
          item.nomUsuari.toLowerCase().includes(search.toLowerCase())
    )
    .sort((a, b) => {
      if (filter === 'latest')
        return (
          new Date(b.dataEditat).getTime() -
          new Date(a.dataEditat).getTime()
        );
      if (filter === 'most_used')
        return (
          new Date(b.ultimAcces).getTime() -
          new Date(a.ultimAcces).getTime()
        );
      return 0;
    });

  const totalPages = Math.ceil(filteredItems.length / ITEMS_PER_PAGE);

  const paginatedItems = filteredItems.slice(
    (page - 1) * ITEMS_PER_PAGE,
    page * ITEMS_PER_PAGE
  );

  const handleDelete = (item: Item) => {
    setDeleteTarget(item);
    setOpenDeleteModal(true);
  };

  const confirmDelete = async () => {
    if (!deleteTarget) return;
    try {
      await itemsApi.deleteItem(deleteTarget.uuid);
      setItems((prev) => prev.filter((i) => i.uuid !== deleteTarget.uuid));
      toast.success('Item eliminat correctament');
      setOpenDeleteModal(false);
      setDeleteTarget(null);
    } catch {
      toast.error("Error eliminant l'item");
    }
  };

  const renderContent = () => {
    if (loading) {
      return (
        <Grid container spacing={2}>
          {Array.from({ length: 6 }).map((_, i) => (
            <Grid size={4} key={i}>
              <Skeleton
                variant="rounded"
                height={110}
                sx={{ borderRadius: '10px' }}
              />
            </Grid>
          ))}
        </Grid>
      );
    }

    if (error) {
      return (
        <Alert severity="error" sx={{ mt: 2 }}>
          {error}
        </Alert>
      );
    }

    if (filteredItems.length === 0) {
      return (
        <Stack
          sx={{
            alignItems: 'center',
            py: 10,
            gap: 2,
            color: 'text.disabled',
          }}
        >
          <VpnKeyOffOutlinedIcon sx={{ fontSize: 64 }} />
          <Typography variant="body1" sx={{ fontWeight: 600 }}>
            {search
              ? 'Cap resultat per a la cerca'
              : filter === 'favorites'
              ? 'No tens cap favorit'
              : 'No tens cap contrasenya guardada'}
          </Typography>

          {!search && filter !== 'favorites' && (
            <Typography variant="body2" color="text.secondary">
              Afegeix la primera des del botó "+ Add New"
            </Typography>
          )}
        </Stack>
      );
    }

    return (
      <Grid container spacing={2}>
        {paginatedItems.map((item) => {
          const estaEnCarpeta = carpetas.some((c) =>
            c.items.some((i) => i.uuid === item.uuid)
          );

          return (
            <Grid size={4} key={item.uuid}>
              <CredentialCard
                uuid={item.uuid}
                titol={item.titol}
                nomUsuari={item.nomUsuari}
                dataEditat={item.dataEditat}
                dinsCarpeta={estaEnCarpeta}
                favorit={item.favorit}
                onClick={() =>
                  navigate('/Item', { state: { uuid: item.uuid } })
                }
                onEdit={() =>
                  navigate('/EditItem', { state: { uuid: item.uuid } })
                }
                onDelete={() => handleDelete(item)}
              />
            </Grid>
          );
        })}
      </Grid>
    );
  };

  return (
    <AppTheme>
      <CssBaseline enableColorScheme />

      <Stack direction="row" sx={{ minHeight: '100vh', width: '100%' }}>
        <Sidebar />

        <Stack
          sx={{
            flex: 1,
            bgcolor: 'background.default',
            overflow: 'auto',
            minWidth: 0,
          }}
        >
          <Header
            title="Items"
            icon={<KeyRoundedIcon sx={{ fontSize: 30, color: 'text.primary' }} />}
          />

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
            <CustomPagination
              count={totalPages}
              page={page}
              onChange={setPage}
            />
          )}
        </Stack>

        <DeleteConfirmationModal
          open={openDeleteModal}
          onClose={() => setOpenDeleteModal(false)}
          onConfirm={confirmDelete}
        />
      </Stack>
    </AppTheme>
  );
}