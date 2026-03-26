import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router';
import CssBaseline from '@mui/material/CssBaseline';
import toast from 'react-hot-toast';
import Stack from '@mui/material/Stack';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import Pagination from '@mui/material/Pagination';
import HomeRoundedIcon from '@mui/icons-material/HomeRounded';
import CheckIcon from '@mui/icons-material/Check';
import AppTheme from '../theme/AppTheme';
import Sidebar from '../components/Sidebar';
import CredentialCard from '../components/CredentialCard';
import DeleteConfirmationModal from '../components/DeleteConfirmationModal';
import { brand } from '../theme/themePrimitives';
import { itemsApi, type Item } from '../api/itemsapi';
import { carpetasApi, type Carpeta } from '../api/carpetasapi';
import Header from '../components/Header';

const LAVENDER = '#EEE5FF';
const itemsPerPage = 12;

type TabValue = 'latest' | 'most_used' | 'favorites';
const tabs: { value: TabValue; label: string }[] = [
  { value: 'latest', label: 'Últimos usados' },
  { value: 'most_used', label: 'Más usados' },
  { value: 'favorites', label: 'Favoritos' },
];

export default function Home() {
  const navigate = useNavigate();

  const [items, setItems] = useState<Item[]>([]);
  const [carpetas, setCarpetas] = useState<Carpeta[]>([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState<TabValue>('latest');
  const [page, setPage] = useState(1);
  const user = JSON.parse(localStorage.getItem('user') || '{}');
  const [openDeleteModal, setOpenDeleteModal] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState<{ uuid: string; esCarpeta: boolean } | null>(null);

  // Carga inicial
  useEffect(() => {
    const loadData = async () => {
      try {
        const [itemsData, carpetasData] = await Promise.all([
          itemsApi.fetchItems(),
          carpetasApi.fetchItems(),
        ]);
        setItems(itemsData);
        setCarpetas(carpetasData);
      } catch (error) {
        console.error(error);
        toast.error('Error cargando datos');
      } finally {
        setLoading(false);
      }
    };
    loadData();
  }, []);

  // Filtrado y orden según tab
  const filteredItems = items
    .filter(item => activeTab !== 'favorites' || item.favorit)
    .sort((a, b) => {
      if (activeTab === 'latest') return new Date(b.dataEditat).getTime() - new Date(a.dataEditat).getTime();
      if (activeTab === 'most_used') return (b.ultimAcces?.length || 0) - (a.ultimAcces?.length || 0);
      return 0;
    });

  // Paginación
  const displayedItems = filteredItems.slice((page - 1) * itemsPerPage, page * itemsPerPage);
  const displayedCarpetas = carpetas.slice((page - 1) * itemsPerPage, page * itemsPerPage);

  // Eliminación
  const handleDeleteClick = (uuid: string, esCarpeta: boolean) => {
    setDeleteTarget({ uuid, esCarpeta });
    setOpenDeleteModal(true);
  };

  const confirmDelete = async () => {
    if (!deleteTarget) return;
    const { uuid, esCarpeta } = deleteTarget;
    try {
      if (esCarpeta) {
        await carpetasApi.deleteCarpeta(uuid);
        setCarpetas(prev => prev.filter(c => c.uuid !== uuid));
        toast.success('Carpeta eliminada correctamente');
      } else {
        await itemsApi.deleteItem(uuid);
        setItems(prev => prev.filter(i => i.uuid !== uuid));
        toast.success('Item eliminado correctamente');
      }
      setOpenDeleteModal(false);
      setDeleteTarget(null);
    } catch (error) {
      console.error(error);
      toast.error('Error al eliminar');
    }
  };

  return (
    <AppTheme>
      <CssBaseline enableColorScheme />
      <Stack direction="row" sx={{ minHeight: '100vh', width: '100%' }}>
        <Sidebar />

        <Stack sx={{ flex: 1, bgcolor: 'background.default', overflow: 'auto', minWidth: 0 }}>
          
          {/* HEADER */}
          <Header
            title="Home"
            icon={<HomeRoundedIcon sx={{ fontSize: 30 }} />}
            userInitial={user.name?.[0]}
          />

          {/* TABS + Add */}
          <Stack
            direction="row"
            sx={{ px: 4, pt: 3, pb: 2, justifyContent: 'space-between', alignItems: 'center' }}
          >
            <Stack direction="row" sx={{ gap: 1 }}>
              {tabs.map(tab => {
                const isActive = activeTab === tab.value;
                return (
                  <Button
                    key={tab.value}
                    startIcon={isActive ? <CheckIcon sx={{ fontSize: '16px !important' }} /> : undefined}
                    onClick={() => { setActiveTab(tab.value); setPage(1); }}
                    sx={{
                      borderRadius: '100px',
                      textTransform: 'none',
                      fontWeight: 600,
                      px: 2.5,
                      py: 1,
                      fontSize: '0.875rem',
                      ...(isActive
                        ? { bgcolor: brand[400], color: 'white', '&:hover': { bgcolor: brand[500] } }
                        : { bgcolor: LAVENDER, color: 'text.primary', '&:hover': { bgcolor: '#E0D0FF' } }),
                    }}
                  >
                    {tab.label}
                  </Button>
                );
              })}
            </Stack>

            <Button
              variant="contained"
              onClick={() => navigate('/ChooseType')}
              sx={{ borderRadius: '8px', textTransform: 'none', fontWeight: 600, px: 2.5 }}
            >
              + Add New
            </Button>
          </Stack>

          {/* GRID */}
          {loading ? (
            <Typography sx={{ p: 4 }}>Cargando...</Typography>
          ) : (
            <Box sx={{ px: 4, pb: 3, flex: 1 }}>
              <Grid container spacing={2}>
                {displayedCarpetas.map((carpeta) => (
                  <Grid size={4} key={carpeta.uuid}>
                    <CredentialCard
                      uuid={carpeta.uuid}
                      titol={carpeta.nom}
                      favorit={carpeta.favorit}
                      nomUsuari=""
                      dataEditat={carpeta.dataCreacio}
                      esCarpeta
                      onClick={() => navigate('/carpeta', { state: { uuid: carpeta.uuid, nombreCarpeta: carpeta.nom } })}
                      onEdit={() => navigate('/editcarpeta', { state: { uuid: carpeta.uuid } })}
                      onDelete={() => handleDeleteClick(carpeta.uuid, true)}
                    />
                  </Grid>
                ))}

                {displayedItems.map(item => {
                  const estaEnCarpeta = carpetas.some(c => c.items.some(i => i.uuid === item.uuid));
                  return (
                    <Grid size={4} key={item.uuid}>
                      <CredentialCard
                        uuid={item.uuid}
                        titol={item.titol}
                        nomUsuari={item.nomUsuari}
                        dataEditat={item.dataEditat}
                        dinsCarpeta={estaEnCarpeta}
                        favorit={item.favorit}
                        onClick={() => navigate('/Item', { state: { uuid: item.uuid } })}
                        onEdit={() => navigate('/edititem', { state: { uuid: item.uuid } })}
                        onDelete={() => handleDeleteClick(item.uuid, false)}
                      />
                    </Grid>
                  );
                })}
              </Grid>
            </Box>
          )}

          {/* PAGINACIÓN */}
          <Stack sx={{ px: 4, pb: 4, alignItems: 'flex-end' }}>
            <Pagination
              count={Math.ceil((filteredItems.length + carpetas.length) / itemsPerPage)}
              page={page}
              onChange={(_, val) => setPage(val)}
              color="primary"
              siblingCount={1}
              boundaryCount={2}
              sx={{ '& .MuiPaginationItem-root': { borderRadius: '8px' } }}
            />
          </Stack>
        </Stack>
      </Stack>

      {/* MODAL DE ELIMINACIÓN */}
      <DeleteConfirmationModal
        open={openDeleteModal}
        onClose={() => setOpenDeleteModal(false)}
        onConfirm={confirmDelete}
      />
    </AppTheme>
  );
}