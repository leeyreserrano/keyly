import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router';
import CssBaseline from '@mui/material/CssBaseline';
import Stack from '@mui/material/Stack';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import Typography from '@mui/material/Typography';
import FolderOutlinedIcon from '@mui/icons-material/FolderOutlined';

import AppTheme from '../../theme/AppTheme';
import Sidebar from '../../components/Sidebar';
import Header from '../../components/Header';
import ItemsToolbar from '../../components/ItemsToolbar';
import CustomPagination from '../../components/CustomPagination';
import DeleteConfirmationModal from '../../components/DeleteConfirmationModal';
import CredentialCard from '../../components/CredentialCard';

import { carpetasApi, type Carpeta } from '../../api/carpetasapi';
import toast from 'react-hot-toast';

export type FilterValue = 'latest' | 'most_used' | 'favorites';

const itemsPerPage = 12;

export default function Carpetas() {
  const navigate = useNavigate();

  const [carpetas, setCarpetas] = useState<Carpeta[]>([]);
  const [loading, setLoading] = useState(true);

  const [search, setSearch] = useState('');
  const [filter, setFilter] = useState<FilterValue>('latest');
  const [page, setPage] = useState(1);

  const [openDeleteModal, setOpenDeleteModal] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState<Carpeta | null>(null);

  const loadCarpetas = async () => {
    try {
      setLoading(true);
      const data = await carpetasApi.fetchItems();
      setCarpetas(data);
    } catch (error) {
      console.error(error);
      toast.error('Error cargando carpetas');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadCarpetas();
  }, []);

  const filteredCarpetas = carpetas
  .filter((c) =>
    filter === 'favorites'
      ? c.favorit
      : c.nom.toLowerCase().includes(search.toLowerCase())
  )
  .sort((a, b) => {
    if (filter === 'latest')
      return (
        new Date(b.dataEditat).getTime() -
        new Date(a.dataEditat).getTime()
      );
    if (filter === 'most_used')
      return (b.items?.length || 0) - (a.items?.length || 0);
    return 0;
  });

  const displayedCarpetas = filteredCarpetas.slice(
    (page - 1) * itemsPerPage,
    page * itemsPerPage
  );

  const handleDelete = (carpeta: Carpeta) => {
    setDeleteTarget(carpeta);
    setOpenDeleteModal(true);
  };

  const confirmDelete = async () => {
  if (!deleteTarget) return;

  const uuid = deleteTarget.uuid;

  setCarpetas((prev) => prev.filter((c) => c.uuid !== uuid));
  setOpenDeleteModal(false);
  setDeleteTarget(null);

  try {
    await carpetasApi.deleteCarpeta(uuid);
    toast.success('Carpeta eliminada correctamente');
  } catch {
    toast.error('Error eliminando carpeta');

    try {
      const data = await carpetasApi.fetchItems();
      setCarpetas(data);
    } catch {}
  }
};

  return (
    <AppTheme>
      <CssBaseline enableColorScheme />

      <Stack direction="row" sx={{ minHeight: '100vh', width: '100%' }}>
        <Sidebar />

        <Stack sx={{ flex: 1, bgcolor: 'background.default' }}>
          <Header
            title="Carpetas"
            icon={<FolderOutlinedIcon sx={{ fontSize: 30 }} />}
          />

          <ItemsToolbar
            search={search}
            setSearch={setSearch}
            filter={filter}
            setFilter={setFilter}
            onAdd={() => navigate('/AddCarpeta')}
          />

          {loading ? (
            <Typography sx={{ p: 4 }}>Cargando...</Typography>
          ) : (
            <Box sx={{ px: 4, pb: 3 }}>
              <Grid container spacing={2}>
                {displayedCarpetas.map((carpeta) => (
                  <Grid size={4} key={carpeta.uuid}>
                    <CredentialCard
                      uuid={carpeta.uuid}
                      titol={carpeta.nom}
                      nomUsuari=""
                      dataEditat={carpeta.dataCreacio}
                      dataCreacio={carpeta.dataCreacio}
                      esCarpeta
                      favorit={carpeta.favorit}
                      onClick={() =>
                        navigate('/Carpeta', {
                          state: {
                            uuid: carpeta.uuid,
                            nombreCarpeta: carpeta.nom,
                          },
                        })
                      }
                      onEdit={() =>
                        navigate('/editcarpeta', {
                          state: { uuid: carpeta.uuid },
                        })
                      }
                      onDelete={() => handleDelete(carpeta)}
                    />
                  </Grid>
                ))}
              </Grid>
            </Box>
          )}

          <CustomPagination
            count={Math.ceil(filteredCarpetas.length / itemsPerPage)}
            page={page}
            onChange={setPage}
          />
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