import { useEffect, useState, useCallback } from 'react';
import { useNavigate } from 'react-router';
import { useTranslation } from 'react-i18next';
import Stack from '@mui/material/Stack';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import Typography from '@mui/material/Typography';
import Alert from '@mui/material/Alert';
import Skeleton from '@mui/material/Skeleton';

import RepeatRoundedIcon from '@mui/icons-material/RepeatRounded';
import Header from '../../components/Header';
import ItemsToolbar from '../../components/ItemsToolbar';
import CustomPagination from '../../components/CustomPagination';
import DeleteConfirmationModal from '../../components/DeleteConfirmationModal';
import CredentialCard from '../../components/CredentialCard';

import { itemsApi, type Item } from '../../api/itemsapi';
import { carpetasApi, type Carpeta } from '../../api/carpetasapi';
import { useCrypto } from '../../context/CryptoContext';
import { rsaDecrypt, decryptPasswordWithDataKey } from '../../crypto/cryptoService';
import toast from 'react-hot-toast';

const ITEMS_PER_PAGE = 12;

function getDuplicatedItems(items: Item[], decryptedPasswords: Map<string, string>): Item[] {
  const frequency: Record<string, number> = {};

  items.forEach((item) => {
    const plain = decryptedPasswords.get(item.uuid);
    if (plain) {
      frequency[plain] = (frequency[plain] || 0) + 1;
    }
  });

  return items.filter((item) => {
    const plain = decryptedPasswords.get(item.uuid);
    return plain && frequency[plain] > 1;
  });
}

export default function Duplicats() {
  const navigate = useNavigate();
  const { t } = useTranslation('item');
  const { privateKey } = useCrypto();

  const [items, setItems] = useState<Item[]>([]);
  const [carpetas, setCarpetas] = useState<Carpeta[]>([]);
  const [loading, setLoading] = useState(true);
  const [decrypting, setDecrypting] = useState(false);
  const [decryptedPasswords, setDecryptedPasswords] = useState<Map<string, string>>(new Map());
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
        const message = err instanceof Error ? err.message : t('duplicates.error.load');
        setError(message);
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, [t]);

  const decryptAll = useCallback(async (allItems: Item[]) => {
    if (!privateKey || allItems.length === 0) return;
    setDecrypting(true);

    const plainMap = new Map<string, string>();

    await Promise.allSettled(
      allItems.map(async (item) => {
        try {
          const hasEncryption =
            item.encryptedDataKey?.encryptedDataKey && item.iv && item.contrasenya;

          if (hasEncryption) {
            const dataKeyBytes = await rsaDecrypt(
              privateKey,
              item.encryptedDataKey!.encryptedDataKey
            );
            const plain = await decryptPasswordWithDataKey(
              dataKeyBytes,
              item.contrasenya!,
              item.iv!
            );
            plainMap.set(item.uuid, plain);
          } else if (item.contrasenya) {
            plainMap.set(item.uuid, item.contrasenya);
          }
        } catch {
          // item individual ignorado
        }
      })
    );

    setDecryptedPasswords(new Map(plainMap));
    setDecrypting(false);
  }, [privateKey]);

  useEffect(() => {
    if (!loading && items.length > 0 && privateKey) {
      decryptAll(items);
    }
  }, [loading, items, privateKey, decryptAll]);

  useEffect(() => {
    setPage(1);
  }, [search]);

  const duplicats = getDuplicatedItems(items, decryptedPasswords);

  const filteredItems = duplicats.filter(
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
      toast.success(t('duplicates.success.delete'));
      setOpenDeleteModal(false);
      setDeleteTarget(null);
    } catch {
      toast.error(t('duplicates.error.delete'));
    }
  };

  const renderContent = () => {
    if (loading || decrypting) {
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
          <RepeatRoundedIcon sx={{ fontSize: 64 }} />
          <Typography variant="body1" sx={{ fontWeight: 600 }}>
            {search ? t('duplicates.empty.search') : t('duplicates.empty.no')}
          </Typography>
          {!search && (
            <Typography variant="body2" color="text.secondary">
              {t('duplicates.empty.subtitle')}
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
        title={t('duplicates.title')}
        icon={<RepeatRoundedIcon sx={{ fontSize: 30 }} />}
        showBackButton
      />

      <Stack sx={{ flex: 1, overflow: 'auto' }}>
        <ItemsToolbar search={search} setSearch={setSearch} />

        <Box sx={{ px: 4, pb: 3, pt: 3 }}>
          {renderContent()}
        </Box>

        {!loading && !decrypting && !error && filteredItems.length > 0 && (
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