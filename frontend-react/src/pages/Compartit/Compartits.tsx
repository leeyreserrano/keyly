import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router';
import { useTranslation } from 'react-i18next';
import Stack from '@mui/material/Stack';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import Typography from '@mui/material/Typography';
import Alert from '@mui/material/Alert';
import Skeleton from '@mui/material/Skeleton';
import Tabs from '@mui/material/Tabs';
import Tab from '@mui/material/Tab';
import ShareOutlinedIcon from '@mui/icons-material/ShareOutlined';
import VpnKeyOffOutlinedIcon from '@mui/icons-material/VpnKeyOffOutlined';
import Header from '../../components/Header';
import CustomPagination from '../../components/CustomPagination';
import DeleteConfirmationModal from '../../components/DeleteConfirmationModal';
import CredentialCard from '../../components/CredentialCard';
import { compartitsApi, type Compartit } from '../../api/compartitsapi';
import toast from 'react-hot-toast';

type TabValue = 'rebuts' | 'creats';
const ITEMS_PER_PAGE = 12;

export default function Compartits() {
  const navigate = useNavigate();
  const { t } = useTranslation('shared');

  const [compartits, setCompartits] = useState<Compartit[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [tab, setTab] = useState<TabValue>('rebuts');
  const [page, setPage] = useState(1);
  const [openDeleteModal, setOpenDeleteModal] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState<Compartit | null>(null);

  useEffect(() => {
    const loadData = async () => {
      setLoading(true);
      setError(null);

      try {
        const data =
          tab === 'rebuts'
            ? await compartitsApi.fetchCompartitsRebuts()
            : await compartitsApi.fetchCompartitsCreats();

        setCompartits(data ?? []);
      } catch (err: unknown) {
        const message =
          err instanceof Error
            ? err.message
            : t('error.load');

        setError(message);
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, [tab, t]);

  useEffect(() => {
    setPage(1);
  }, [tab]);

  const handleTabChange = (_: React.SyntheticEvent, newValue: TabValue) => {
    setTab(newValue);
  };

  const handleDelete = (compartit: Compartit) => {
    setDeleteTarget(compartit);
    setOpenDeleteModal(true);
  };

  const confirmDelete = async () => {
    if (!deleteTarget) return;

    try {
      await compartitsApi.deleteCompartit(deleteTarget.uuid);

      setCompartits((prev) =>
        prev.filter((c) => c.uuid !== deleteTarget.uuid)
      );

      toast.success(t('success.delete'));
      setOpenDeleteModal(false);
      setDeleteTarget(null);
    } catch {
      toast.error(t('error.delete'));
    }
  };

  const totalPages = Math.ceil(compartits.length / ITEMS_PER_PAGE);

  const paginatedCompartits = compartits.slice(
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

    if (compartits.length === 0) {
      return (
        <Stack sx={{ alignItems: 'center', py: 10, gap: 2, color: 'text.disabled' }}>
          <VpnKeyOffOutlinedIcon sx={{ fontSize: 64 }} />
          <Typography variant="body1" sx={{ fontWeight: 600 }}>
            {tab === 'rebuts'
              ? t('empty.received')
              : t('empty.created')}
          </Typography>
        </Stack>
      );
    }

    return (
      <Grid container spacing={2}>
        {paginatedCompartits.map((compartit) => {
          const esCarpeta = compartit.tipusEntitat === 'CARPETA';

          const titol = esCarpeta
            ? compartit.carpeta?.nom ?? ''
            : compartit.item?.titol ?? '';

          const nomUsuari = esCarpeta ? '' : (compartit.item?.nomUsuari ?? '');
          const url = esCarpeta ? undefined : compartit.item?.url;

          const dataEditat = esCarpeta
            ? compartit.carpeta?.dataEditat ?? compartit.dataCreacio
            : compartit.item?.dataEditat ?? compartit.dataCreacio;

          return (
            <Grid size={4} key={compartit.uuid}>
              <CredentialCard
                uuid={compartit.uuid}
                titol={titol}
                nomUsuari={nomUsuari}
                dataEditat={dataEditat}
                dataCreacio={compartit.dataCreacio}
                url={url}
                esCarpeta={esCarpeta}
                dinsCarpeta={false}
                favorit={false}
                showFavorit={false}
                onClick={() =>
                  navigate(esCarpeta ? '/Carpeta' : '/Item', {
                    state: esCarpeta
                      ? { uuid: compartit.carpeta?.uuid ?? '', compartitUuid: compartit.uuid }
                      : { uuid: compartit.item?.uuid ?? '', compartitUuid: compartit.uuid },
                  })
                }
                onDelete={() => handleDelete(compartit)}
              />
            </Grid>
          );
        })}
      </Grid>
    );
  };

  return (
    <Stack sx={{ height: '100%', overflow: 'hidden' }}>
      <Header
        title={t('title')}
        icon={<ShareOutlinedIcon sx={{ fontSize: 30, color: 'text.primary' }} />}
      />

      <Stack sx={{ flex: 1, overflow: 'auto', minWidth: 0 }}>
        <Box sx={{ px: 4, pt: 2, pb: 1 }}>
          <Tabs value={tab} onChange={handleTabChange}>
            <Tab label={t('tab.received')} value="rebuts" />
            <Tab label={t('tab.created')} value="creats" />
          </Tabs>
        </Box>

        <Box sx={{ px: 4, pb: 3, flex: 1, pt: 2 }}>
          {renderContent()}
        </Box>

        {!loading && !error && compartits.length > 0 && (
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
      />
    </Stack>
  );
}