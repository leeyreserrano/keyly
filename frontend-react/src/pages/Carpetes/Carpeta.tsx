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
import toast from 'react-hot-toast';
import { itemsApi, type Item } from '../../api/itemsapi';
import { carpetasApi, type Carpeta } from '../../api/carpetasapi';
import Header from '../../components/Header';
import ItemsToolbar from '../../components/ItemsToolbar';
import CustomPagination from '../../components/CustomPagination';




export default function Carpeta() {
    const [search, setSearch] = useState('');
    type FilterValue = 'latest' | 'most_used' | 'favorites';
    const [filter, setFilter] = useState<FilterValue>('latest');

    const itemsPerPage = 12;
    const navigate = useNavigate();
    const location = useLocation();
    const { uuid, nombreCarpeta } = location.state as { uuid: string; nombreCarpeta: string };

    const [items, setItems] = useState<Item[]>([]);
    const [loading, setLoading] = useState(true);
    const [page, setPage] = useState(1);
    const [openDeleteModal, setOpenDeleteModal] = useState(false);
    const [deleteTarget, setDeleteTarget] = useState<Item | null>(null);

    useEffect(() => {
        const loadItems = async () => {
            try {
                const itemsData = await carpetasApi.fetchItemsFromCarpeta(uuid);
                setItems(itemsData);
            } catch (error) {
                console.error(error);
                toast.error("Error cargando los items de la carpeta");
            } finally {
                setLoading(false);
            }
        };
        loadItems();
    }, [uuid]);

    const handleDelete = (item: Item) => {
        setDeleteTarget(item);
        setOpenDeleteModal(true);
    };

    const confirmDelete = async () => {
        if (!deleteTarget) return;

        try {
            await itemsApi.deleteItem(deleteTarget.uuid);
            setItems((prev) => prev.filter((i) => i.uuid !== deleteTarget.uuid));
            toast.success("Se ha eliminado el item");
            setOpenDeleteModal(false);
            setDeleteTarget(null);
        } catch (error) {
            toast.error("Error eliminando el item");
        }
    };
    const filteredItems = items
        .filter(item =>
            item.titol.toLowerCase().includes(search.toLowerCase()) ||
            item.nomUsuari.toLowerCase().includes(search.toLowerCase())
        )
        .sort((a, b) => {
            if (filter === 'latest') return new Date(b.dataEditat).getTime() - new Date(a.dataEditat).getTime();
            if (filter === 'most_used') return (b.ultimAcces?.length || 0) - (a.ultimAcces?.length || 0);
            if (filter === 'favorites') return (b.favorit ? 1 : 0) - (a.favorit ? 1 : 0);
            return 0;
        });
    const displayedItems = items.slice((page - 1) * itemsPerPage, page * itemsPerPage);

    return (
        <AppTheme>
            <CssBaseline enableColorScheme />
            <Stack direction="row" sx={{ minHeight: '100vh', width: '100%' }}>
                <Sidebar />

                <Stack sx={{ flex: 1, bgcolor: 'background.default', overflow: 'auto', minWidth: 0 }}>
                    {/* HEADER */}
                    <Header
                        title={nombreCarpeta || 'Carpeta'}
                        icon={<FolderOutlinedIcon sx={{ fontSize: 30, color: 'text.primary' }} />}
                        userInitial="U"
                        showBackButton={true}
                    />
                    {/* Buscador + Filtro + Add New */}
                    <ItemsToolbar
                        search={search}
                        setSearch={setSearch}
                        filter={filter}
                        setFilter={setFilter}
                        onAdd={() => navigate('/AddItem')}
                    />
                    {/* GRID DE ITEMS */}
                    {loading ? (
                        <Typography sx={{ p: 4 }}>Cargando...</Typography>
                    ) : (
                        <Box sx={{ px: 4, pb: 3, flex: 1 }}>
                            <Grid container spacing={2}>
                                {displayedItems.map((item) => (
                                    <Grid size={4} key={item.uuid}>
                                        <CredentialCard
                                            uuid={item.uuid}
                                            titol={item.titol}
                                            nomUsuari={item.nomUsuari}
                                            dataEditat={item.dataEditat}
                                            favorit={item.favorit}
                                            onClick={() => navigate('/Item', { state: { uuid: item.uuid } })}
                                            onEdit={() => navigate('/edititem', { state: { uuid: item.uuid } })}
                                            onDelete={() => handleDelete(item)}
                                        />
                                    </Grid>
                                ))}
                            </Grid>
                        </Box>
                    )}

                    {/* PAGINACIÓN */}
                    <CustomPagination
                        count={Math.ceil(filteredItems.length / itemsPerPage)}
                        page={page}
                        onChange={setPage}
                    />
                </Stack>

                {/* MODAL DE ELIMINACIÓN */}
                <DeleteConfirmationModal
                    open={openDeleteModal}
                    onClose={() => setOpenDeleteModal(false)}
                    onConfirm={confirmDelete}
                />
            </Stack>
        </AppTheme>
    );
}