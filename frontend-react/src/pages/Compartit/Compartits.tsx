import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router';
import {
  Stack,
  Typography,
  Box,
  CssBaseline,
  Grid,
  Alert,
  Skeleton,
} from '@mui/material';
import ShareOutlinedIcon from '@mui/icons-material/ShareOutlined';
import AppTheme from '../../theme/AppTheme';
import Sidebar from '../../components/Sidebar';
import Header from '../../components/Header';
import ItemsToolbar from '../../components/ItemsToolbar';
import CustomPagination from '../../components/CustomPagination';
import DeleteConfirmationModal from '../../components/DeleteConfirmationModal';
import { compartitsApi, type Compartit } from '../../api/compartitsapi';
import toast from 'react-hot-toast';
import type { FilterValue } from '../Items/Items';
import SharedCard from '../../components/SharedCard';


const ITEMS_PER_PAGE = 12;

export default function Compartits() {
  const navigate = useNavigate();

  const [compartits, setCompartits] = useState<Compartit[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [deleteTarget, setDeleteTarget] = useState<Compartit | null>(null);
  const [openDeleteModal, setOpenDeleteModal] = useState(false);
  const [search, setSearch] = useState('');
  const [filter, setFilter] = useState<FilterValue>('latest');
  const [page, setPage] = useState(1);

  useEffect(() => {
    const loadData = async () => {
      try {
        const data = await compartitsApi.fetchAll();
        setCompartits(data);
      } catch (err: unknown) {
        const message = err instanceof Error ? err.message : 'Error carregant compartits';
        setError(message);
      } finally {
        setLoading(false);
      }
    };
    loadData();
  }, []);

  useEffect(() => {
    setPage(1);
  }, [search, filter]);

  const handleDeleteClick = (compartit: Compartit) => {
    setDeleteTarget(compartit);
    setOpenDeleteModal(true);
  };

  const confirmDelete = async () => {
    if (!deleteTarget) return;
    try {
      await compartitsApi.delete(deleteTarget.uuid);
      setCompartits((prev) => prev.filter((c) => c.uuid !== deleteTarget.uuid));
      toast.success('Compartit eliminat correctament');
      setOpenDeleteModal(false);
      setDeleteTarget(null);
    } catch {
      toast.error('Error eliminant el compartit');
    }
  };

  const getNom = (compartit: Compartit) =>
    compartit.tipusEntitat === 'CARPETA'
      ? compartit.carpeta?.nom ?? ''
      : compartit.item?.titol ?? '';

  const filteredCompartits = compartits
    .filter((c) =>
      filter === 'favorites'
        ? false
        : getNom(c).toLowerCase().includes(search.toLowerCase()) ||
        c.usuari.nom.toLowerCase().includes(search.toLowerCase())
    )
    .sort((a, b) => {
      if (filter === 'latest')
        return new Date(b.dataCreacio).getTime() - new Date(a.dataCreacio).getTime();
      return 0;
    });

  const totalPages = Math.ceil(filteredCompartits.length / ITEMS_PER_PAGE);
  const paginatedCompartits = filteredCompartits.slice(
    (page - 1) * ITEMS_PER_PAGE,
    page * ITEMS_PER_PAGE
  );

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

    if (error) {
      return <Alert severity="error" sx={{ mt: 2 }}>{error}</Alert>;
    }

    if (filteredCompartits.length === 0) {
      return (
        <Stack sx={{ alignItems: 'center', py: 10, gap: 2, color: 'text.disabled' }}>
          <ShareOutlinedIcon sx={{ fontSize: 64 }} />
          <Typography variant="body1" sx={{ fontWeight: 600 }}>
            {search ? 'Cap resultat per a la cerca' : 'No tens cap element compartit'}
          </Typography>
          {!search && (
            <Typography variant="body2" color="text.secondary">
              Comparteix items o carpetes des de la seva vista de detall
            </Typography>
          )}
        </Stack>
      );
    }

    return (
      <Grid container spacing={2}>
        {paginatedCompartits.map((compartit) => {
          return (
            <Grid size={4} key={compartit.uuid}>
              <SharedCard
                compartit={compartit}
                onClick={() => {
                  if (compartit.tipusEntitat === 'CARPETA' && compartit.carpeta) {
                    navigate('/Carpeta', { state: { uuid: compartit.carpeta.uuid, nombreCarpeta: compartit.carpeta.nom } });
                  } else if (compartit.tipusEntitat === 'ITEM' && compartit.item) {
                    navigate('/Item', { state: { uuid: compartit.item.uuid } });
                  }
                }}
                onDelete={(e) => { e.stopPropagation(); handleDeleteClick(compartit); }}
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

        <Stack sx={{ flex: 1, bgcolor: 'background.default', overflow: 'auto', minWidth: 0 }}>
          <Header
            title="Compartits"
            icon={<ShareOutlinedIcon sx={{ fontSize: 30, color: 'text.primary' }} />}
          />

          <ItemsToolbar
            search={search}
            setSearch={setSearch}
            filter={filter}
            setFilter={setFilter}
            onAdd={() => { }}
          />

          <Box sx={{ px: 4, pb: 3, flex: 1 }}>
            {renderContent()}
          </Box>

          {!loading && !error && filteredCompartits.length > 0 && (
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
          title="Eliminar compartit"
          description="Deixaràs de compartir aquest element. Aquesta acció no es pot desfer."
          confirmText="Eliminar"
        />
      </Stack>
    </AppTheme>
  );
}