import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router';
import CssBaseline from '@mui/material/CssBaseline';
import Stack from '@mui/material/Stack';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import Grid from '@mui/material/Grid';
import Alert from '@mui/material/Alert';
import Skeleton from '@mui/material/Skeleton';
import RepeatRoundedIcon from '@mui/icons-material/RepeatRounded';

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

const ITEMS_PER_PAGE = 12;

function getDuplicatedItems(items: Item[]): Item[] {
  const frequency: Record<string, number> = {};
  items.forEach((i) => {
    if (i.contrasenya) {
      frequency[i.contrasenya] = (frequency[i.contrasenya] || 0) + 1;
    }
  });
  return items.filter((i) => i.contrasenya && frequency[i.contrasenya] > 1);
}

export default function Duplicats() {
  const navigate = useNavigate();

  const [items, setItems] = useState<Item[]>([]);
  const [carpetas, setCarpetas] = useState<Carpeta[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [search, setSearch] = useState('');
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

  useEffect(() => {
    setPage(1);
  }, [search]);

  const duplicats = getDuplicatedItems(items);

  const filteredItems = duplicats.filter(
    (item) =>
      item.titol.toLowerCase().includes(search.toLowerCase()) ||
      item.nomUsuari.toLowerCase().includes(search.toLowerCase())
  );

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
          <RepeatRoundedIcon sx={{ fontSize: 64 }} />
          <Typography variant="body1" sx={{ fontWeight: 600 }}>
            {search
              ? 'Cap resultat per a la cerca'
              : 'No hi ha contrasenyes duplicades'}
          </Typography>
          {!search && (
            <Typography variant="body2" color="text.secondary">
              Totes les teves contrasenyes són úniques.
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
            title="Contrasenyes duplicades"
            icon={<RepeatRoundedIcon sx={{ fontSize: 30, color: 'text.primary' }} />}
            showBackButton={true}
          />

          <ItemsToolbar
            search={search}
            setSearch={setSearch}
            sx={{ width: '100%', maxWidth: 1280, mx: 'auto' }}
          />

          <Box sx={{ px: 4, pb: 3, flex: 1, pt: 3 }}>
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