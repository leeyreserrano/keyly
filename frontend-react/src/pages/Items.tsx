import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router';
import CssBaseline from '@mui/material/CssBaseline';
import Stack from '@mui/material/Stack';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import Avatar from '@mui/material/Avatar';
import IconButton from '@mui/material/IconButton';
import TextField from '@mui/material/TextField';
import MenuItem from '@mui/material/MenuItem';
import type { Item } from '../api/itemsapi';
import { itemsApi } from '../api/itemsapi';
import Pagination from '@mui/material/Pagination';
import KeyRoundedIcon from '@mui/icons-material/VpnKeyRounded';
import LogoutOutlinedIcon from '@mui/icons-material/LogoutOutlined';
import AppTheme from '../theme/AppTheme';
import Sidebar from '../components/Sidebar';
import { carpetasApi, type Carpeta } from '../api/carpetasapi';
import CredentialCard from '../components/CredentialCard';

const LAVENDER = '#EEE5FF';

type FilterValue = 'latest' | 'most_used' | 'favorites';

const filters: { value: FilterValue; label: string }[] = [
    { value: 'latest', label: 'Últimos usados' },
    { value: 'most_used', label: 'Más usados' },
    { value: 'favorites', label: 'Favoritos' },
];

export default function Items() {
    const navigate = useNavigate();
    const [items, setItems] = useState<Item[]>([]);
    const [loading, setLoading] = useState(true);
    const [search, setSearch] = useState('');
    const [filter, setFilter] = useState<FilterValue>('latest');
    const [page, setPage] = useState(1);
    const [carpetas, setCarpetas] = useState<Carpeta[]>([]);

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

    // Filtrar y buscar
    const filteredItems = items
        .filter((item) =>
            item.titol.toLowerCase().includes(search.toLowerCase()) ||
            item.nomUsuari.toLowerCase().includes(search.toLowerCase())
        )
        .sort((a, b) => {
            if (filter === 'latest') return new Date(b.dataEditat).getTime() - new Date(a.dataEditat).getTime();
            if (filter === 'most_used') return (b.ultimAcces?.length || 0) - (a.ultimAcces?.length || 0);
            return 0;
        });

    return (
        <AppTheme>
            <CssBaseline enableColorScheme />
            <Stack direction="row" sx={{ minHeight: '100vh', width: '100%' }}>
                <Sidebar />

                <Stack sx={{ flex: 1, bgcolor: 'background.default', overflow: 'auto', minWidth: 0 }}>
                    {/* Header */}
                    <Stack
                        direction="row"
                        sx={{
                            px: 4,
                            py: 2.5,
                            justifyContent: 'space-between',
                            alignItems: 'center',
                            borderBottom: '1px solid',
                            borderColor: 'divider',
                        }}
                    >
                        <Stack direction="row" sx={{ gap: 1.5, alignItems: 'center' }}>
                            <KeyRoundedIcon sx={{ fontSize: 30, color: 'text.primary' }} />
                            <Typography variant="h3" sx={{ fontWeight: 800 }}>
                                Items
                            </Typography>
                        </Stack>
                        <Stack direction="row" sx={{ gap: 1, alignItems: 'center' }}>
                            <Avatar sx={{ bgcolor: 'grey.500', width: 36, height: 36, fontSize: 15, fontWeight: 700 }}>
                                U
                            </Avatar>
                            <IconButton
                                onClick={() => navigate('/')}
                                size="small"
                                sx={{ border: 'none', bgcolor: 'transparent', '&:hover': { bgcolor: 'action.hover' } }}
                            >
                                <LogoutOutlinedIcon sx={{ fontSize: 22, color: 'text.secondary' }} />
                            </IconButton>
                        </Stack>
                    </Stack>

                    {/* Buscador + Filtro + Add New */}
                    <Stack
                        direction={{ xs: 'column', sm: 'row' }}
                        sx={{
                            px: 4,
                            py: 3,
                            justifyContent: 'flex-start',
                            alignItems: 'center',
                            gap: 1,
                        }}
                    >
                        {/* Buscador ancho */}
                        <TextField
                            placeholder="Buscar por título o usuario"
                            value={search}
                            onChange={(e) => setSearch(e.target.value)}
                            size="small"
                            sx={{ width: { xs: '100%', sm: 1090 } }}
                        />

                        {/* Selector de filtro */}
                        <TextField
                            select
                            value={filter}
                            onChange={(e) => setFilter(e.target.value as FilterValue)}
                            size="small"
                            sx={{ width: { xs: '100%', sm: 180 } }}
                        >
                            {filters.map((f) => (
                                <MenuItem key={f.value} value={f.value}>
                                    {f.label}
                                </MenuItem>
                            ))}
                        </TextField>

                        {/* Add New */}
                        <Button
                            variant="contained"
                            onClick={() => navigate('/AddItem')}
                            sx={{ borderRadius: '8px', textTransform: 'none', fontWeight: 600 }}
                        >
                            + Add New
                        </Button>
                    </Stack>

                    {/* Grid de Items */}
                    {loading ? (
                        <Typography sx={{ p: 4 }}>Cargando...</Typography>
                    ) : (
                        <Box sx={{ px: 4, pb: 3, flex: 1 }}>
                            <Grid container spacing={2}>
                                {filteredItems.map((item) => {
                                    const estaEnCarpeta = carpetas.some((carpeta) =>
                                        carpeta.items.some((i) => i.uuid === item.uuid)
                                    );

                                    return (
                                        <Grid size={4} key={item.uuid}>
                                            <CredentialCard
                                                uuid={item.uuid}
                                                titol={item.titol}
                                                nomUsuari={item.nomUsuari}
                                                dataEditat={item.dataEditat}
                                                dinsCarpeta={estaEnCarpeta}
                                                onClick={() => navigate('/Item', { state: { uuid: item.uuid } })}
                                                onEdit={() => navigate('/edititem', { state: { uuid: item.uuid } })}
                                                onDelete={() => console.log('Eliminar item', item.uuid)}
                                            />
                                        </Grid>
                                    );
                                })}
                            </Grid>
                        </Box>
                    )}

                    {/* Pagination */}
                    <Stack sx={{ px: 4, pb: 4, alignItems: 'flex-end' }}>
                        <Pagination
                            count={68}
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
        </AppTheme>
    );
}