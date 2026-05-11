import { useEffect, useState, useMemo } from 'react';
import { useNavigate } from 'react-router';
import { useTranslation } from 'react-i18next';
import Stack from '@mui/material/Stack';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import Alert from '@mui/material/Alert';
import Skeleton from '@mui/material/Skeleton';
import HomeRoundedIcon from '@mui/icons-material/HomeRounded';
import CheckIcon from '@mui/icons-material/Check';
import VpnKeyOffOutlinedIcon from '@mui/icons-material/VpnKeyOffOutlined';
import Header from '../components/Header';
import CredentialCard from '../components/CredentialCard';
import DeleteConfirmationModal from '../components/DeleteConfirmationModal';
import CustomPagination from '../components/CustomPagination';
import { brand } from '../theme/themePrimitives';
import { itemsApi, type Item } from '../api/itemsapi';
import { carpetasApi, type Carpeta } from '../api/carpetasapi';
import toast from 'react-hot-toast';

const LAVENDER = '#EEE5FF';
const ITEMS_PER_PAGE = 12;

type TabValue = 'latest' | 'most_used' | 'favorites';

type HomeElement =
  | (Item & { esCarpeta: false })
  | (Carpeta & {
      esCarpeta: true;
      titol: string;
      nomUsuari: string;
      dataEditat: string;
      ultimAcces: string;
    });

export default function Home() {
  const navigate = useNavigate();
  const { t } = useTranslation('home');

  const tabs: { value: TabValue; label: string }[] = [
    { value: 'latest', label: t('tabs.latest') },
    { value: 'most_used', label: t('tabs.most_used') },
    { value: 'favorites', label: t('tabs.favorites') },
  ];

  const [items, setItems] = useState<Item[]>([]);
  const [carpetas, setCarpetas] = useState<Carpeta[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [activeTab, setActiveTab] = useState<TabValue>('latest');
  const [page, setPage] = useState(1);
  const [openDeleteModal, setOpenDeleteModal] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState<{ uuid: string; esCarpeta: boolean } | null>(null);

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
        const message = err instanceof Error ? err.message : t('error.loading');
        setError(message);
        toast.error(t('error.loading'));
      } finally {
        setLoading(false);
      }
    };
    loadData();
  }, []);

  const handleAccess = async (uuid: string, esCarpeta: boolean) => {
    try {
      if (esCarpeta) {
        const updated = await carpetasApi.registrarAcces(uuid);
        setCarpetas((prev) => prev.map((c) => (c.uuid === uuid ? { ...c, ...updated } : c)));
      } else {
        const updated = await itemsApi.registrarAcces(uuid);
        if (updated) {
          setItems((prev) => prev.map((i) => (i.uuid === uuid ? { ...i, ...updated } : i)));
        }
      }
    } catch {
      // silencioso
    }
  };

  const itemsEnCarpetaSet = useMemo(() => {
    const set = new Set<string>();
    carpetas.forEach((c) => c.items.forEach((i) => set.add(i.uuid)));
    return set;
  }, [carpetas]);

  const filteredData = useMemo<HomeElement[]>(() => {
    const allData: HomeElement[] = [
      ...items.map((i) => ({ ...i, esCarpeta: false as const })),
      ...carpetas.map((c) => ({
        ...c,
        esCarpeta: true as const,
        titol: c.nom,
        nomUsuari: '',
        dataEditat: c.dataEditat,
        ultimAcces: c.ultimAccess,
      })),
    ];

    const base =
      activeTab === 'favorites' ? allData.filter((i) => i.favorit) : allData;

    return [...base].sort((a, b) => {
      if (activeTab === 'latest') {
        return new Date(b.ultimAcces).getTime() - new Date(a.ultimAcces).getTime();
      }
      if (activeTab === 'most_used') {
        const countDiff = (b.comptadorAccess ?? 0) - (a.comptadorAccess ?? 0);
        if (countDiff !== 0) return countDiff;
        return new Date(b.ultimAcces).getTime() - new Date(a.ultimAcces).getTime();
      }
      return 0;
    });
  }, [items, carpetas, activeTab]);

  const paginatedData = useMemo(() => {
    return filteredData.slice((page - 1) * ITEMS_PER_PAGE, page * ITEMS_PER_PAGE);
  }, [filteredData, page]);

  const displayedCarpetas = paginatedData.filter((i) => i.esCarpeta);
  const displayedItems = paginatedData.filter((i) => !i.esCarpeta);
  const totalCount = filteredData.length;

  const handleDeleteClick = (uuid: string, esCarpeta: boolean) => {
    setDeleteTarget({ uuid, esCarpeta });
    setOpenDeleteModal(true);
  };

  const confirmDelete = async () => {
    if (!deleteTarget) return;
    const { uuid, esCarpeta } = deleteTarget;

    if (esCarpeta) {
      setCarpetas((prev) => prev.filter((c) => c.uuid !== uuid));
    } else {
      setItems((prev) => prev.filter((i) => i.uuid !== uuid));
    }

    setOpenDeleteModal(false);
    setDeleteTarget(null);

    try {
      if (esCarpeta) {
        await carpetasApi.deleteCarpeta(uuid);
      } else {
        await itemsApi.deleteItem(uuid);
      }
      toast.success(t('toast.delete_success'));
    } catch {
      toast.error(t('toast.delete_error'));
      try {
        const [itemsData, carpetasData] = await Promise.all([
          itemsApi.fetchItems(),
          carpetasApi.fetchItems(),
        ]);
        setItems(itemsData ?? []);
        setCarpetas(carpetasData ?? []);
      } catch {}
    }
  };

  const renderGrid = () => {
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

    if (displayedCarpetas.length === 0 && displayedItems.length === 0) {
      return (
        <Stack sx={{ alignItems: 'center', py: 10, gap: 2, color: 'text.disabled' }}>
          <VpnKeyOffOutlinedIcon sx={{ fontSize: 64 }} />
          <Typography variant="body1" sx={{ fontWeight: 600 }}>
            {activeTab === 'favorites' ? t('empty.favorites') : t('empty.no_items')}
          </Typography>
          {activeTab !== 'favorites' && (
            <Button variant="contained" onClick={() => navigate('/ChooseType')}>
              {t('empty.add_first')}
            </Button>
          )}
        </Stack>
      );
    }

    return (
      <Grid container spacing={2}>
        {displayedCarpetas.map((carpeta) => (
          <Grid size={4} key={carpeta.uuid}>
            <CredentialCard
              uuid={carpeta.uuid}
              titol={carpeta.nom}
              nomUsuari=""
              dataEditat={carpeta.dataEditat}
              dataCreacio={carpeta.dataCreacio}
              ultimAcces={(carpeta as HomeElement & { esCarpeta: true }).ultimAcces}
              favorit={carpeta.favorit}
              esCarpeta
              onAccess={handleAccess}
              onClick={() =>
                navigate('/Carpeta', { state: { uuid: carpeta.uuid, nombreCarpeta: carpeta.nom } })
              }
              onEdit={() => navigate('/editCarpeta', { state: { uuid: carpeta.uuid } })}
              onDelete={() => handleDeleteClick(carpeta.uuid, true)}
            />
          </Grid>
        ))}
        {displayedItems.map((item) => (
          <Grid size={4} key={item.uuid}>
            <CredentialCard
              uuid={item.uuid}
              titol={item.titol}
              nomUsuari=""
              dataEditat={item.dataEditat}
              dataCreacio={item.dataCreacio}
              url={item.url}
              ultimAcces={(item as Item).ultimAcces}
              dinsCarpeta={itemsEnCarpetaSet.has(item.uuid)}
              favorit={item.favorit}
              esCarpeta={false}
              onAccess={handleAccess}
              onClick={() => navigate('/Item', { state: { uuid: item.uuid } })}
              onEdit={() => navigate('/EditItem', { state: { uuid: item.uuid } })}
              onDelete={() => handleDeleteClick(item.uuid, false)}
            />
          </Grid>
        ))}
      </Grid>
    );
  };

  return (
    <Stack sx={{ height: '100%', overflow: 'hidden' }}>
      <Header title={t('title')} icon={<HomeRoundedIcon sx={{ fontSize: 30 }} />} />

      <Stack sx={{ flex: 1, overflow: 'auto', minWidth: 0 }}>
        <Stack
          direction="row"
          sx={{ px: 4, pt: 3, pb: 2, justifyContent: 'space-between', alignItems: 'center' }}
        >
          <Stack direction="row" sx={{ gap: 1 }}>
            {tabs.map((tab) => {
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
            {t('add_new')}
          </Button>
        </Stack>

        <Box sx={{ px: 4, pb: 3, flex: 1 }}>
          {renderGrid()}
        </Box>

        {!loading && !error && totalCount > ITEMS_PER_PAGE && (
          <CustomPagination
            count={Math.ceil(totalCount / ITEMS_PER_PAGE)}
            page={page}
            onChange={(val) => { setPage(val); window.scrollTo(0, 0); }}
          />
        )}
      </Stack>

      <DeleteConfirmationModal
        open={openDeleteModal}
        onClose={() => setOpenDeleteModal(false)}
        onConfirm={confirmDelete}
      />
    </Stack>
  );
}