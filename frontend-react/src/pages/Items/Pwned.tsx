import { useEffect, useState, useCallback } from 'react';
import { useNavigate } from 'react-router';
import { useTranslation } from 'react-i18next';
import Stack from '@mui/material/Stack';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import Typography from '@mui/material/Typography';
import Alert from '@mui/material/Alert';
import Skeleton from '@mui/material/Skeleton';

import BugReportOutlinedIcon from '@mui/icons-material/BugReportOutlined';
import Header from '../../components/Header';
import ItemsToolbar from '../../components/ItemsToolbar';
import CustomPagination from '../../components/CustomPagination';
import DeleteConfirmationModal from '../../components/DeleteConfirmationModal';
import CredentialCard from '../../components/CredentialCard';

import { itemsApi, type Item } from '../../api/itemsapi';
import { carpetasApi, type Carpeta } from '../../api/carpetasapi';
import { useCrypto } from '../../context/CryptoContext';
import { rsaDecrypt, decryptPasswordWithDataKey } from '../../crypto/cryptoService';
import { isPasswordPwned } from '../../utils/pwnedUtils';
import toast from 'react-hot-toast';

const ITEMS_PER_PAGE = 12;

export default function Pwned() {
  const navigate = useNavigate();
  const { t } = useTranslation('item');
  const { privateKey } = useCrypto();

  const [items, setItems] = useState<Item[]>([]);
  const [carpetas, setCarpetas] = useState<Carpeta[]>([]);
  const [loading, setLoading] = useState(true);
  const [checking, setChecking] = useState(false);
  const [pwnedUuids, setPwnedUuids] = useState<Set<string>>(new Set());
  const [error, setError] = useState<string | null>(null);
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(1);
  const [openDeleteModal, setOpenDeleteModal] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState<Item | null>(null);

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
        const message = err instanceof Error ? err.message : t('pwned.error.load');
        setError(message);
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, [t]);

  const checkPwned = useCallback(async (allItems: Item[]) => {
    if (!privateKey || allItems.length === 0) return;
    setChecking(true);

    const found = new Set<string>();

    await Promise.allSettled(
      allItems.map(async (item) => {
        try {
          let plain: string | null = null;

          const hasEncryption =
            item.encryptedDataKey?.encryptedDataKey && item.iv && item.contrasenya;

          if (hasEncryption) {
            const dataKeyBytes = await rsaDecrypt(
              privateKey,
              item.encryptedDataKey!.encryptedDataKey
            );
            plain = await decryptPasswordWithDataKey(
              dataKeyBytes,
              item.contrasenya!,
              item.iv!
            );
          } else if (item.contrasenya) {
            plain = item.contrasenya;
          }

          if (plain) {
            const pwned = await isPasswordPwned(plain);
            if (pwned) found.add(item.uuid);
          }
        } catch {
          // item individual ignorado
        }
      })
    );

    setPwnedUuids(new Set(found));
    setChecking(false);
  }, [privateKey]);

  useEffect(() => {
    if (!loading && items.length > 0 && privateKey) {
      checkPwned(items);
    }
  }, [loading, items, privateKey, checkPwned]);

  useEffect(() => {
    setPage(1);
  }, [search]);

  const pwnedItems = items.filter((item) => pwnedUuids.has(item.uuid));

  const filteredItems = pwnedItems.filter(
    (item) =>
      item.titol.toLowerCase().includes(search.toLowerCase()) ||
      item.nomUsuari.toLowerCase().includes(search.toLowerCase())
  );

  const totalPages = Math.ceil(filteredItems.length / ITEMS_PER_PAGE);

  const paginatedItems = filteredItems.slice(
    (page - 1) * ITEMS_PER_PAGE,
    page * ITEMS_PER_PAGE
  );

  const handleDelete = (item: Item) => {
    setDeleteTarget(item);
    setOpenDeleteModal(true);
  };

  const confirmDelete = async () => {
    if (!deleteTarget) return;
    try {
      await itemsApi.deleteItem(deleteTarget.uuid);
      setItems((prev) => prev.filter((i) => i.uuid !== deleteTarget.uuid));
      toast.success(t('pwned.success.delete'));
      setOpenDeleteModal(false);
      setDeleteTarget(null);
    } catch {
      toast.error(t('pwned.error.delete'));
    }
  };

  const renderContent = () => {
    if (loading || checking) {
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

    if (filteredItems.length === 0) {
      return (
        <Stack sx={{ alignItems: 'center', py: 10, gap: 2, color: 'text.disabled' }}>
          <BugReportOutlinedIcon sx={{ fontSize: 64 }} />
          <Typography variant="body1" sx={{ fontWeight: 600 }}>
            {search ? t('pwned.empty.search') : t('pwned.empty.no')}
          </Typography>
          {!search && (
            <Typography variant="body2" color="text.secondary">
              {t('pwned.empty.subtitle')}
            </Typography>
          )}
        </Stack>
      );
    }

    return (
      <Grid container spacing={2}>
        {paginatedItems.map((item) => {
          const estaEnCarpeta = carpetas.some((c) =>
            c.items.some((i) => i.uuid === item.uuid)
          );

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
                onEdit={() => navigate('/EditItem', { state: { uuid: item.uuid } })}
                onDelete={() => handleDelete(item)}
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
        title={t('pwned.title')}
        icon={<BugReportOutlinedIcon sx={{ fontSize: 30 }} />}
        showBackButton
      />

      <Stack sx={{ flex: 1, overflow: 'auto' }}>
        <ItemsToolbar search={search} setSearch={setSearch} />

        <Box sx={{ px: 4, pb: 3, pt: 3 }}>
          {renderContent()}
        </Box>

        {!loading && !checking && !error && filteredItems.length > 0 && (
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