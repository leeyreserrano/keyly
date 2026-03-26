import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router';
import CssBaseline from '@mui/material/CssBaseline';
import Stack from '@mui/material/Stack';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import Grid from '@mui/material/Grid';
import KeyRoundedIcon from '@mui/icons-material/VpnKeyRounded';
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

export default function Items() {
  const itemsPerPage = 12;
  const navigate = useNavigate();

  const [items, setItems] = useState<Item[]>([]);
  const [carpetas, setCarpetas] = useState<Carpeta[]>([]);
  const [loading, setLoading] = useState(true);

  const [search, setSearch] = useState('');
  const [filter, setFilter] = useState<FilterValue>('latest');
  const [page, setPage] = useState(1);

  const [openDeleteModal, setOpenDeleteModal] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState<Item | null>(null);

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
      } finally {
        setLoading(false);
      }
    };
    loadData();
  }, []);

  // Filtrado y orden
  const filteredItems = items
    .filter((item) =>
      item.titol.toLowerCase().includes(search.toLowerCase()) ||
      item.nomUsuari.toLowerCase().includes(search.toLowerCase())
    )
    .sort((a, b) => {
      if (filter === 'latest') return new Date(b.dataEditat).getTime() - new Date(a.dataEditat).getTime();
      if (filter === 'most_used') return (b.ultimAcces?.length || 0) - (a.ultimAcces?.length || 0);
      if (filter === 'favorites') return (b.favorit ? 1 : 0) - (a.favorit ? 1 : 0);
      return 0;
    });

  const handleDelete = (item: Item) => {
    setDeleteTarget(item);
    setOpenDeleteModal(true);
  };

  const confirmDelete = async () => {
    if (!deleteTarget) return;
    try {
      await itemsApi.deleteItem(deleteTarget.uuid);
      setItems((prev) => prev.filter((i) => i.uuid !== deleteTarget.uuid));
      toast.success('Se ha eliminado el item');
      setOpenDeleteModal(false);
      setDeleteTarget(null);
      navigate('/items');
    } catch (error) {
      toast.error('Error eliminando el item');
    }
  };

  return (
    <AppTheme>
      <CssBaseline enableColorScheme />
      <Stack direction="row" sx={{ minHeight: '100vh', width: '100%' }}>
        <Sidebar />

        <Stack sx={{ flex: 1, bgcolor: 'background.default', overflow: 'auto', minWidth: 0 }}>
          {/* Header */}
          <Header
            title="Items"
            icon={<KeyRoundedIcon sx={{ fontSize: 30, color: 'text.primary' }} />}
          />

          {/* Buscador + Filtro + Add New */}
          <ItemsToolbar
            search={search}
            setSearch={setSearch}
            filter={filter}
            setFilter={setFilter}
            onAdd={() => navigate('/AddItem')}
          />

          {/* Grid de Items */}
          {loading ?
            (<Typography sx={{ p: 4 }}>Cargando...</Typography>
            ) : (
              <Box sx={{ px: 4, pb: 3, flex: 1 }}>
                <Grid container spacing={2}>
                  {filteredItems.map((item) => {
                    const estaEnCarpeta = carpetas.some((carpeta) => carpeta.items.some((i) => i.uuid === item.uuid));
                    return (<Grid size={4} key={item.uuid}>
                      <CredentialCard
                        uuid={item.uuid}
                        titol={item.titol}
                        nomUsuari={item.nomUsuari}
                        dataEditat={item.dataEditat}
                        dinsCarpeta={estaEnCarpeta}
                        favorit={item.favorit}
                        onClick={() => navigate('/Item', { state: { uuid: item.uuid } })}
                        onEdit={() => navigate('/edititem', { state: { uuid: item.uuid } })}
                        onDelete={() => handleDelete(item)} />
                    </Grid>);
                  })} </Grid> </Box>)}
          {/* Pagination */}
          <CustomPagination
            count={Math.ceil(filteredItems.length / itemsPerPage)}
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