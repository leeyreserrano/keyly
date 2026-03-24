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
import Pagination from '@mui/material/Pagination';
import HomeRoundedIcon from '@mui/icons-material/HomeRounded';
import LogoutOutlinedIcon from '@mui/icons-material/LogoutOutlined';
import CheckIcon from '@mui/icons-material/Check';
import AppTheme from '../theme/AppTheme';
import Sidebar from '../components/Sidebar';
import CredentialCard from '../components/CredentialCard';
import { brand } from '../theme/themePrimitives';
import { itemsApi } from '../api/itemsapi';
import { carpetasApi, type Carpeta } from '../api/carpetasapi';
import type { Item } from '../api/itemsapi';

const LAVENDER = '#EEE5FF';

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
  const [activeTab] = useState<TabValue>('latest');
  const [page, setPage] = useState(1);

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

  return (
    <AppTheme>
      <CssBaseline enableColorScheme />
      <Stack direction="row" sx={{ minHeight: '100vh', width: '100%' }}>
        <Sidebar />

        <Stack sx={{ flex: 1, bgcolor: 'background.default', overflow: 'auto', minWidth: 0 }}>
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
              <HomeRoundedIcon sx={{ fontSize: 30, color: 'text.primary' }} />
              <Typography variant="h3" sx={{ fontWeight: 800 }}>
                Home
              </Typography>
            </Stack>
            <Stack direction="row" sx={{ gap: 1, alignItems: 'center' }}>
              <Avatar
                sx={{
                  bgcolor: 'grey.500',
                  width: 36,
                  height: 36,
                  fontSize: 15,
                  fontWeight: 700,
                }}
              >
                U
              </Avatar>
              <IconButton
                onClick={() => navigate('/')}
                size="small"
                sx={{
                  border: 'none',
                  bgcolor: 'transparent',
                  '&:hover': { bgcolor: 'action.hover' },
                }}
              >
                <LogoutOutlinedIcon sx={{ fontSize: 22, color: 'text.secondary' }} />
              </IconButton>
            </Stack>
          </Stack>

          <Stack
            direction="row"
            sx={{
              px: 4,
              pt: 3,
              pb: 2,
              justifyContent: 'space-between',
              alignItems: 'center',
            }}
          >
            <Stack direction="row" sx={{ gap: 1 }}>
              {tabs.map((tab) => {
                const isActive = activeTab === tab.value;
                return (
                  <Button
                    key={tab.value}
                    startIcon={isActive ? <CheckIcon sx={{ fontSize: '16px !important' }} /> : undefined}
                    sx={{
                      borderRadius: '100px',
                      textTransform: 'none',
                      fontWeight: 600,
                      px: 2.5,
                      py: 1,
                      fontSize: '0.875rem',
                      ...(isActive
                        ? {
                          bgcolor: brand[400],
                          color: 'white',
                          '&:hover': { bgcolor: brand[500] },
                        }
                        : {
                          bgcolor: LAVENDER,
                          color: 'text.primary',
                          '&:hover': { bgcolor: '#E0D0FF' },
                        }),
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
              sx={{
                borderRadius: '8px',
                textTransform: 'none',
                fontWeight: 600,
                px: 2.5,
              }}
            >
              + Add New
            </Button>
          </Stack>

          {loading ? (
            <Typography sx={{ p: 4 }}>Cargando...</Typography>
          ) : (
            <Box sx={{ px: 4, pb: 3, flex: 1 }}>

              <Grid container spacing={2}>
                {carpetas.map((carpeta) => (
                  <Grid size={4} key={carpeta.uuid}>
                    <CredentialCard
                      titol={carpeta.nom}
                      nomUsuari=""
                      dataEditat={carpeta.dataCreacio}
                      esCarpeta
                      onEdit={() => navigate('/editcarpeta', { state: { uuid: carpeta.uuid } })}
                      onDelete={() => console.log('Eliminar carpeta', carpeta.uuid)}
                    />
                  </Grid>
                ))}

                {items.map((item) => {
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

          <Stack sx={{ px: 4, pb: 4, alignItems: 'flex-end' }}>
            <Pagination
              count={68}
              page={page}
              onChange={(_, val) => setPage(val)}
              color="primary"
              siblingCount={1}
              boundaryCount={2}
              sx={{
                '& .MuiPaginationItem-root': {
                  borderRadius: '8px',
                },
              }}
            />
          </Stack>
        </Stack>
      </Stack>
    </AppTheme>
  );
}