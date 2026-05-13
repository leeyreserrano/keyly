import { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import {
  Dialog, DialogTitle, DialogContent, DialogActions,
  Button, Stack, Typography, TextField, Checkbox, Avatar,
  CircularProgress, Chip, FormControl, InputLabel, Select,
  MenuItem, Divider, Box,
} from '@mui/material';
import { compartitsApi, type Permisos } from '../api/compartitsapi';
import { usuarisApi, type UsuariPublic } from '../api/usuarisapi';
import { itemsApi } from '../api/itemsapi';
import { carpetasApi } from '../api/carpetasapi';
import { useCrypto } from '../context/CryptoContext';
import { useAuth } from '../context/AuthContext';
import { importPublicKey, rsaDecrypt, rsaEncrypt } from '../crypto/cryptoService';
import toast from 'react-hot-toast';

interface ShareModalProps {
  open: boolean;
  onClose: () => void;
  tipusEntitat: 'CARPETA' | 'ITEM';
  entitatUuid: string;
  entitatNom: string;
}

export default function ShareModal({
  open, onClose, tipusEntitat, entitatUuid, entitatNom,
}: ShareModalProps) {
  const { t } = useTranslation('share');
  const { privateKey } = useCrypto();
  const { usuari } = useAuth();

  const [usuaris, setUsuaris] = useState<UsuariPublic[]>([]);
  const [loadingUsuaris, setLoadingUsuaris] = useState(false);
  const [search, setSearch] = useState('');
  const [seleccionats, setSeleccionats] = useState<UsuariPublic[]>([]);
  const [permisos, setPermisos] = useState<Permisos>('LECTURA');
  const [sharing, setSharing] = useState(false);

  useEffect(() => {
    if (!open) return;
    setSearch('');
    setSeleccionats([]);
    setPermisos('LECTURA');

    const load = async () => {
      setLoadingUsuaris(true);
      try {
        const tots = await usuarisApi.fetchAllPublic();
        setUsuaris(tots.filter((u) => u.uuid !== usuari?.uuid));
      } catch {
        toast.error(t('error.load_users'));
      } finally {
        setLoadingUsuaris(false);
      }
    };
    load();
  }, [open, usuari?.uuid, t]);

  const filtrats = usuaris.filter(
    (u) =>
      u.nom.toLowerCase().includes(search.toLowerCase()) ||
      u.correu.toLowerCase().includes(search.toLowerCase())
  );

  const toggleSeleccio = (u: UsuariPublic) => {
    setSeleccionats((prev) =>
      prev.some((s) => s.uuid === u.uuid)
        ? prev.filter((s) => s.uuid !== u.uuid)
        : [...prev, u]
    );
  };

  const handleShare = async () => {
    if (seleccionats.length === 0) {
      toast.error(t('error.no_users'));
      return;
    }

    setSharing(true);
    try {
      if (tipusEntitat === 'ITEM') {
        if (!privateKey) {
          toast.error(t('error.crypto'));
          return;
        }

        const item = await itemsApi.getItem(entitatUuid);
        if (!item) throw new Error(t('error.item_not_found'));

        let dataKeyBytes: Uint8Array | null = null;
        if (item.encryptedDataKey?.encryptedDataKey && item.iv && item.contrasenya) {
          dataKeyBytes = await rsaDecrypt(privateKey, item.encryptedDataKey.encryptedDataKey);
        }

        const usuarisPayload = await Promise.all(
          seleccionats.map(async (receptor) => {
            if (dataKeyBytes && receptor.publicKey) {
              const pubKey = await importPublicKey(receptor.publicKey);
              const encryptedForReceptor = await rsaEncrypt(pubKey, dataKeyBytes);
              return {
                usuariUuid: receptor.uuid,
                permis: permisos,
                encryptedDataKeys: [{ itemUuid: entitatUuid, encryptedDataKey: encryptedForReceptor }],
              };
            }
            return { usuariUuid: receptor.uuid, permis: permisos, encryptedDataKeys: [] };
          })
        );

        await compartitsApi.addCompartit({ entitatUuid, tipusEntitat: 'ITEM', usuaris: usuarisPayload });
      } else {
        const carpeta = await carpetasApi.fetchItemsFromCarpeta(entitatUuid);

        const usuarisPayload = await Promise.all(
          seleccionats.map(async (receptor) => {
            if (!privateKey || !receptor.publicKey || carpeta.length === 0) {
              return { usuariUuid: receptor.uuid, permis: permisos, encryptedDataKeys: [] };
            }

            const pubKey = await importPublicKey(receptor.publicKey);
            const encryptedDataKeys = await Promise.all(
              carpeta
                .filter((item) => item.encryptedDataKey?.encryptedDataKey)
                .map(async (item) => {
                  const dataKeyBytes = await rsaDecrypt(privateKey, item.encryptedDataKey!.encryptedDataKey);
                  const encryptedForReceptor = await rsaEncrypt(pubKey, dataKeyBytes);
                  return { itemUuid: item.uuid, encryptedDataKey: encryptedForReceptor };
                })
            );

            return { usuariUuid: receptor.uuid, permis: permisos, encryptedDataKeys };
          })
        );

        await compartitsApi.addCompartit({ entitatUuid, tipusEntitat: 'CARPETA', usuaris: usuarisPayload });
      }

      toast.success(t('success', { nom: entitatNom }));
      onClose();
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : t('error.share');
      toast.error(message);
    } finally {
      setSharing(false);
    }
  };

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle sx={{ fontWeight: 700, color: 'text.primary' }}>
        {tipusEntitat === 'CARPETA' ? t('title.folder') : t('title.item')}
      </DialogTitle>

      <DialogContent>
        <Stack spacing={2} sx={{ pt: 0.5 }}>
          <Typography sx={{ color: 'text.secondary', fontSize: '0.9rem' }}>
            {t('subtitle', { nom: entitatNom })}
          </Typography>

          <FormControl size="small" fullWidth>
            <InputLabel>{t('permissions.label')}</InputLabel>
            <Select
              value={permisos}
              label={t('permissions.label')}
              onChange={(e) => setPermisos(e.target.value as Permisos)}
            >
              <MenuItem value="LECTURA">{t('permissions.read')}</MenuItem>
              <MenuItem value="ESCRIPTURA">{t('permissions.write')}</MenuItem>
            </Select>
          </FormControl>

          {seleccionats.length > 0 && (
            <Stack direction="row" flexWrap="wrap" gap={0.75}>
              {seleccionats.map((u) => (
                <Chip key={u.uuid} label={u.nom} onDelete={() => toggleSeleccio(u)} size="small" />
              ))}
            </Stack>
          )}

          <TextField
            placeholder={t('search_placeholder')}
            size="small"
            fullWidth
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />

          <Box
            sx={{
              maxHeight: 280, overflowY: 'auto',
              border: '1px solid', borderColor: 'divider', borderRadius: '8px',
            }}
          >
            {loadingUsuaris ? (
              <Stack sx={{ alignItems: 'center', py: 3 }}>
                <CircularProgress size={24} />
              </Stack>
            ) : filtrats.length === 0 ? (
              <Typography sx={{ py: 3, textAlign: 'center', color: 'text.disabled', fontSize: '0.875rem' }}>
                {t('no_users')}
              </Typography>
            ) : (
              filtrats.map((u, i) => {
                const seleccionat = seleccionats.some((s) => s.uuid === u.uuid);
                return (
                  <Box key={u.uuid}>
                    <Stack
                      direction="row"
                      sx={{
                        alignItems: 'center', px: 1.5, py: 1, gap: 1.5, cursor: 'pointer',
                        bgcolor: seleccionat ? 'action.selected' : 'transparent',
                        '&:hover': { bgcolor: 'action.hover' },
                        transition: 'background-color 150ms ease',
                      }}
                      onClick={() => toggleSeleccio(u)}
                    >
                      <Checkbox
                        checked={seleccionat}
                        size="small"
                        sx={{ p: 0 }}
                        onClick={(e) => e.stopPropagation()}
                        onChange={() => toggleSeleccio(u)}
                      />
                      <Avatar src={u.imatge} sx={{ width: 32, height: 32, fontSize: '0.8rem' }}>
                        {u.nom.charAt(0).toUpperCase()}
                      </Avatar>
                      <Stack sx={{ minWidth: 0 }}>
                        <Typography sx={{ fontWeight: 600, fontSize: '0.875rem' }}>{u.nom}</Typography>
                        <Typography sx={{ fontSize: '0.75rem', color: 'text.secondary' }}>{u.correu}</Typography>
                      </Stack>
                    </Stack>
                    {i < filtrats.length - 1 && <Divider />}
                  </Box>
                );
              })
            )}
          </Box>
        </Stack>
      </DialogContent>

      <DialogActions sx={{ px: 3, pb: 2 }}>
        <Button onClick={onClose} sx={{ textTransform: 'none', fontWeight: 600, color: 'white' }}>
          {t('cancel')}
        </Button>
        <Button
          onClick={handleShare}
          variant="contained"
          disabled={sharing || seleccionats.length === 0}
          sx={{
            textTransform: 'none', fontWeight: 600,
            bgcolor: 'white', color: 'primary.main',
            '&:hover': { bgcolor: 'grey.100' },
            '&.Mui-disabled': { bgcolor: 'grey.300', color: 'grey.500' },
          }}
        >
          {sharing
            ? t('sharing')
            : seleccionats.length > 0
            ? t('share_with_count', { count: seleccionats.length })
            : t('share')}
        </Button>
      </DialogActions>
    </Dialog>
  );
}