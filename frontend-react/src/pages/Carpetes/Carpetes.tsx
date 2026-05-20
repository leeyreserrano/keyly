import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router';
import { useTranslation } from 'react-i18next';
import Stack from '@mui/material/Stack';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import Typography from '@mui/material/Typography';
import FolderOutlinedIcon from '@mui/icons-material/FolderOutlined';
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
  const { t } = useTranslation('folder');

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
    } catch {
      toast.error(t('error.load_list'));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadCarpetas();
  }, []);

  const handleAccess = async (uuid: string) => {
    try {
      const updated = await carpetasApi.registrarAcces(uuid);
      setCarpetas((prev) =>
        prev.map((c) => (c.uuid === uuid ? { ...c, ...updated } : c))
      );
    } catch {}
  };

  const filteredCarpetas = carpetas
    .filter((c) =>
      filter === 'favorites'
        ? c.favorit
        : c.nom.toLowerCase().includes(search.toLowerCase())
    )
    .sort((a, b) => {
      if (filter === 'latest')
        return new Date(b.ultimAccess).getTime() - new Date(a.ultimAccess).getTime();

      if (filter === 'most_used') {
        const diff = (b.comptadorAccess ?? 0) - (a.comptadorAccess ?? 0);
        if (diff !== 0) return diff;
        return new Date(b.ultimAccess).getTime() - new Date(a.ultimAccess).getTime();
      }

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
      toast.success(t('success.delete_list'));
    } catch {
      toast.error(t('error.delete_list'));
    }
  };

  return (
    <Stack sx={{ height: '100%', overflow: 'hidden' }}>
      <Header
        title={t('title.list')}
        icon={<FolderOutlinedIcon sx={{ fontSize: 30 }} />}
      />

      <Stack sx={{ flex: 1, overflow: 'auto', minWidth: 0 }}>
        <ItemsToolbar
          search={search}
          setSearch={setSearch}
          filter={filter}
          setFilter={setFilter}
          onAdd={() => navigate('/AddCarpeta')}
        />

        {loading ? (
          <Typography sx={{ p: 4 }}>{t('loading')}</Typography>
        ) : displayedCarpetas.length === 0 ? (
          <Typography sx={{ p: 4 }}>
            {search ? t('empty.search') : t('empty.list')}
          </Typography>
        ) : (
          <Box sx={{ px: 4, pb: 3 }}>
            <Grid container spacing={2}>
              {displayedCarpetas.map((carpeta) => (
                <Grid size={4} key={carpeta.uuid}>
                  <CredentialCard
                    uuid={carpeta.uuid}
                    titol={carpeta.nom}
                    nomUsuari=""
                    dataEditat={carpeta.dataEditat}
                    dataCreacio={carpeta.dataCreacio}
                    ultimAcces={carpeta.ultimAccess}
                    esCarpeta
                    favorit={carpeta.favorit}
                    onAccess={handleAccess}
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
  );
}