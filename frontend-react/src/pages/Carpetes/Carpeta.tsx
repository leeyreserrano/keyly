import { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router';
import CssBaseline from '@mui/material/CssBaseline';
import Stack from '@mui/material/Stack';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import Typography from '@mui/material/Typography';
import FolderOutlinedIcon from '@mui/icons-material/FolderOutlined';
import AppTheme from '../../theme/AppTheme';
import Sidebar from '../../components/Sidebar';
import CredentialCard from '../../components/CredentialCard';
import DeleteConfirmationModal from '../../components/DeleteConfirmationModal';
import ShareModal from '../../components/ShareModal';
import toast from 'react-hot-toast';
import { itemsApi, type Item } from '../../api/itemsapi';
import { carpetasApi } from '../../api/carpetasapi';
import Header from '../../components/Header';
import ItemsToolbar from '../../components/ItemsToolbar';
import CustomPagination from '../../components/CustomPagination';

type FilterValue = 'latest' | 'most_used' | 'favorites';

const ITEMS_PER_PAGE = 12;

export default function Carpeta() {
  const navigate = useNavigate();
  const location = useLocation();
  const { uuid, nombreCarpeta } = location.state as { uuid: string; nombreCarpeta: string };

  const [items, setItems] = useState<Item[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [filter, setFilter] = useState<FilterValue>('latest');
  const [page, setPage] = useState(1);
  const [openDeleteModal, setOpenDeleteModal] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState<Item | null>(null);
  const [openShareModal, setOpenShareModal] = useState(false);

  useEffect(() => {
    const loadItems = async () => {
      try {
        const itemsData = await carpetasApi.fetchItemsFromCarpeta(uuid);
        setItems(itemsData);
      } catch {
        toast.error('Error carregant els items de la carpeta');
      } finally {
        setLoading(false);
      }
    };
    loadItems();
  }, [uuid]);

  useEffect(() => {
    setPage(1);
  }, [search, filter]);

  const handleAccess = async (uuid: string) => {
    try {
      const updated = await itemsApi.registrarAcces(uuid);
      if (updated) {
        setItems((prev) => prev.map((i) => (i.uuid === uuid ? { ...i, ...updated } : i)));
      }
    } catch {
    }
  };

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
  const paginatedItems = filteredItems.slice(
    (page - 1) * ITEMS_PER_PAGE,
    page * ITEMS_PER_PAGE
  );

  return (
    <AppTheme>
      <CssBaseline enableColorScheme />
      <Stack direction="row" sx={{ minHeight: '100vh', width: '100%' }}>
        <Sidebar />

        <Stack sx={{ flex: 1, bgcolor: 'background.default', overflow: 'auto', minWidth: 0 }}>
          <Header
            title={nombreCarpeta || 'Carpeta'}
            icon={<FolderOutlinedIcon sx={{ fontSize: 30, color: 'text.primary' }} />}
            showBackButton={true}
            onShare={() => setOpenShareModal(true)}
          />

          <ItemsToolbar
            search={search}
            setSearch={setSearch}
            filter={filter}
            setFilter={setFilter}
            onAdd={() => navigate('/AddItem', { state: { carpetaUuid: uuid } })}
          />

          {loading ? (
            <Typography sx={{ p: 4 }}>Carregant...</Typography>
          ) : (
            <Box sx={{ px: 4, pb: 3, flex: 1 }}>
              <Grid container spacing={2}>
                {paginatedItems.map((item) => (
                  <Grid size={4} key={item.uuid}>
                    <CredentialCard
                      uuid={item.uuid}
                      titol={item.titol}
                      nomUsuari={item.nomUsuari}
                      dataEditat={item.dataEditat}
                      dataCreacio={item.dataCreacio}
                      ultimAcces={item.ultimAcces}
                      favorit={item.favorit}
                      onAccess={(itemUuid) => handleAccess(itemUuid)}
                      onClick={() => navigate('/Item', { state: { uuid: item.uuid } })}
                      onEdit={() => navigate('/EditItem', { state: { uuid: item.uuid } })}
                      onDelete={() => handleDelete(item)}
                    />
                  </Grid>
                ))}
              </Grid>
            </Box>
          )}

          <CustomPagination count={totalPages} page={page} onChange={setPage} />
        </Stack>

        <DeleteConfirmationModal
          open={openDeleteModal}
          onClose={() => setOpenDeleteModal(false)}
          onConfirm={confirmDelete}
        />

        <ShareModal
          open={openShareModal}
          onClose={() => setOpenShareModal(false)}
          tipusEntitat="CARPETA"
          entitatUuid={uuid}
          entitatNom={nombreCarpeta}
        />
      </Stack>
    </AppTheme>
  );
}