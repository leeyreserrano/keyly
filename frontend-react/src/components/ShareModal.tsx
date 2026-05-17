import { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import {
  Dialog, DialogTitle, DialogContent, DialogActions,
  Button, Stack, Typography, TextField, Checkbox, Avatar,
  CircularProgress, Chip, FormControl, InputLabel, Select,
  MenuItem, Divider, Box, Tabs, Tab,
} from '@mui/material';
import { compartitsApi, type Permisos } from '../api/compartitsapi';
import { usuarisApi, type UsuariPublic, type UsuariAmbDepartament } from '../api/usuarisapi';
import { departamentsApi, type Departament } from '../api/departamentsapi';
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

type TabValue = 'usuaris' | 'departament';

export default function ShareModal({
  open, onClose, tipusEntitat, entitatUuid, entitatNom,
}: ShareModalProps) {
  const { t } = useTranslation('share');
  const { privateKey } = useCrypto();
  const { usuari } = useAuth();

  const esAdmin = usuari?.rolIntern === 'ADMIN';

  const [tab, setTab] = useState<TabValue>('usuaris');
  const [usuaris, setUsuaris] = useState<UsuariPublic[]>([]);
  const [usuarisAmbDept, setUsuarisAmbDept] = useState<UsuariAmbDepartament[]>([]);
  const [departaments, setDepartaments] = useState<Departament[]>([]);
  const [loadingUsuaris, setLoadingUsuaris] = useState(false);
  const [search, setSearch] = useState('');
  const [searchDept, setSearchDept] = useState('');
  const [seleccionats, setSeleccionats] = useState<UsuariPublic[]>([]);
  const [departamentSeleccionat, setDepartamentSeleccionat] = useState<string>('');
  const [permisos, setPermisos] = useState<Permisos>('LECTURA');
  const [sharing, setSharing] = useState(false);

  useEffect(() => {
    if (!open) return;
    setSearch('');
    setSearchDept('');
    setSeleccionats([]);
    setDepartamentSeleccionat('');
    setPermisos('LECTURA');
    setTab('usuaris');

    const load = async () => {
      setLoadingUsuaris(true);
      try {
        if (esAdmin) {
          const [tots, totsAmbDept, depts] = await Promise.all([
            usuarisApi.fetchAllPublic(),
            usuarisApi.fetchAllAmbDepartament(),
            departamentsApi.fetchAll(),
          ]);
          setUsuaris(tots.filter((u) => u.uuid !== usuari?.uuid));
          setUsuarisAmbDept(totsAmbDept.filter((u) => u.uuid !== usuari?.uuid));
          setDepartaments(depts);
        } else {
          const tots = await usuarisApi.fetchAllPublic();
          setUsuaris(tots.filter((u) => u.uuid !== usuari?.uuid));
        }
      } catch {
        toast.error(t('error.load_users'));
      } finally {
        setLoadingUsuaris(false);
      }
    };
    load();
  }, [open, usuari?.uuid, esAdmin, t]);

  const filtrats = usuaris.filter(
    (u) =>
      u.nom.toLowerCase().includes(search.toLowerCase()) ||
      u.correu.toLowerCase().includes(search.toLowerCase())
  );

  const departamentsFiltrats = departaments.filter((d) =>
    d.nom.toLowerCase().includes(searchDept.toLowerCase())
  );

  const usuarisDepartament = departamentSeleccionat
    ? usuarisAmbDept.filter((u) => u.departament?.uuid === departamentSeleccionat)
    : [];

  const toggleSeleccio = (u: UsuariPublic) => {
    setSeleccionats((prev) =>
      prev.some((s) => s.uuid === u.uuid)
        ? prev.filter((s) => s.uuid !== u.uuid)
        : [...prev, u]
    );
  };

  const handleSelectDepartament = (deptUuid: string) => {
    if (departamentSeleccionat === deptUuid) {
      setDepartamentSeleccionat('');
      setSeleccionats([]);
      return;
    }
    setDepartamentSeleccionat(deptUuid);
    const usuarisDelDept = usuarisAmbDept
      .filter((u) => u.departament?.uuid === deptUuid)
      .map((u) => ({
        uuid: u.uuid,
        nom: u.nom,
        correu: u.correu,
        imatge: u.imatge,
        publicKey: u.publicKey,
      }));
    setSeleccionats(usuarisDelDept);
  };

  const handleTabChange = (_: React.SyntheticEvent, v: TabValue) => {
    setTab(v);
    setSeleccionats([]);
    setDepartamentSeleccionat('');
  };

  const buildUsuarisPayloadItem = async (
    receptors: UsuariPublic[],
    dataKeyBytes: Uint8Array | null,
    itemUuid: string
  ) => {
    return Promise.all(
      receptors.map(async (receptor) => {
        if (dataKeyBytes && receptor.publicKey) {
          const pubKey = await importPublicKey(receptor.publicKey);
          const encryptedForReceptor = await rsaEncrypt(pubKey, dataKeyBytes);
          return {
            usuariUuid: receptor.uuid,
            permis: permisos,
            encryptedDataKeys: [{ itemUuid, encryptedDataKey: encryptedForReceptor }],
          };
        }
        return { usuariUuid: receptor.uuid, permis: permisos, encryptedDataKeys: [] };
      })
    );
  };

  const buildUsuarisPayloadCarpeta = async (
    receptors: UsuariPublic[],
    itemsAmbDataKey: { uuid: string; dataKeyBytes: Uint8Array }[]
  ) => {
    return Promise.all(
      receptors.map(async (receptor) => {
        if (!receptor.publicKey || itemsAmbDataKey.length === 0) {
          return { usuariUuid: receptor.uuid, permis: 'LECTURA' as Permisos, encryptedDataKeys: [] };
        }
        const pubKey = await importPublicKey(receptor.publicKey);
        const encryptedDataKeys = await Promise.all(
          itemsAmbDataKey.map(async ({ uuid, dataKeyBytes }) => {
            const encryptedForReceptor = await rsaEncrypt(pubKey, dataKeyBytes);
            return { itemUuid: uuid, encryptedDataKey: encryptedForReceptor };
          })
        );
        return { usuariUuid: receptor.uuid, permis: 'LECTURA' as Permisos, encryptedDataKeys };
      })
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

        const usuarisPayload = await buildUsuarisPayloadItem(seleccionats, dataKeyBytes, entitatUuid);
        await compartitsApi.addCompartit({ entitatUuid, tipusEntitat: 'ITEM', usuaris: usuarisPayload });

      } else {
        if (!privateKey) {
          toast.error(t('error.crypto'));
          return;
        }

        const carpeta = await carpetasApi.fetchItemsFromCarpeta(entitatUuid);

        const itemsAmbDataKey = await Promise.all(
          carpeta
            .filter((item) => item.encryptedDataKey?.encryptedDataKey)
            .map(async (item) => {
              const dataKeyBytes = await rsaDecrypt(privateKey, item.encryptedDataKey!.encryptedDataKey);
              return { uuid: item.uuid, dataKeyBytes };
            })
        );

        const usuarisPayload = await buildUsuarisPayloadCarpeta(seleccionats, itemsAmbDataKey);
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

          {tipusEntitat === 'ITEM' && (
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
          )}

          {esAdmin && (
            <Tabs
              value={tab}
              onChange={handleTabChange}
              textColor="primary"
              indicatorColor="primary"
            >
              <Tab label={t('tab.users')} value="usuaris" />
              <Tab label={t('tab.department')} value="departament" />
            </Tabs>
          )}

          {seleccionats.length > 0 && (
            <Stack direction="row" flexWrap="wrap" gap={0.75}>
              {seleccionats.map((u) => (
                <Chip
                  key={u.uuid}
                  label={u.nom}
                  onDelete={tab === 'usuaris' ? () => toggleSeleccio(u) : undefined}
                  size="small"
                />
              ))}
            </Stack>
          )}

          {tab === 'usuaris' ? (
            <>
              <TextField
                placeholder={t('search_placeholder')}
                size="small"
                fullWidth
                value={search}
                onChange={(e) => setSearch(e.target.value)}
              />
              <Box sx={{ maxHeight: 280, overflowY: 'auto', border: '1px solid', borderColor: 'divider', borderRadius: '8px' }}>
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
                          <Checkbox checked={seleccionat} size="small" sx={{ p: 0 }} onClick={(e) => e.stopPropagation()} onChange={() => toggleSeleccio(u)} />
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
            </>
          ) : (
            <>
              <TextField
                placeholder={t('search_department')}
                size="small"
                fullWidth
                value={searchDept}
                onChange={(e) => setSearchDept(e.target.value)}
              />
              <Box sx={{ maxHeight: 280, overflowY: 'auto', border: '1px solid', borderColor: 'divider', borderRadius: '8px' }}>
                {loadingUsuaris ? (
                  <Stack sx={{ alignItems: 'center', py: 3 }}>
                    <CircularProgress size={24} />
                  </Stack>
                ) : departamentsFiltrats.length === 0 ? (
                  <Typography sx={{ py: 3, textAlign: 'center', color: 'text.disabled', fontSize: '0.875rem' }}>
                    {t('no_departments')}
                  </Typography>
                ) : (
                  departamentsFiltrats.map((d, i) => {
                    const seleccionat = departamentSeleccionat === d.uuid;
                    const count = usuarisAmbDept.filter((u) => u.departament?.uuid === d.uuid).length;
                    return (
                      <Box key={d.uuid}>
                        <Stack
                          direction="row"
                          sx={{
                            alignItems: 'center', px: 1.5, py: 1, gap: 1.5, cursor: 'pointer',
                            bgcolor: seleccionat ? 'action.selected' : 'transparent',
                            '&:hover': { bgcolor: 'action.hover' },
                            transition: 'background-color 150ms ease',
                          }}
                          onClick={() => handleSelectDepartament(d.uuid)}
                        >
                          <Checkbox checked={seleccionat} size="small" sx={{ p: 0 }} onClick={(e) => e.stopPropagation()} onChange={() => handleSelectDepartament(d.uuid)} />
                          <Stack sx={{ minWidth: 0, flex: 1 }}>
                            <Typography sx={{ fontWeight: 600, fontSize: '0.875rem' }}>{d.nom}</Typography>
                            <Typography sx={{ fontSize: '0.75rem', color: 'text.secondary' }}>
                              {d.sucursal?.nom}{count > 0 ? ` · ${count} ${t('users_count')}` : ''}
                            </Typography>
                          </Stack>
                        </Stack>
                        {i < departamentsFiltrats.length - 1 && <Divider />}
                      </Box>
                    );
                  })
                )}
              </Box>

              {usuarisDepartament.length > 0 && (
                <Stack spacing={0.5}>
                  <Typography sx={{ fontSize: '0.75rem', fontWeight: 600, color: 'text.secondary' }}>
                    {t('department_users')}
                  </Typography>
                  <Stack direction="row" flexWrap="wrap" gap={0.5}>
                    {usuarisDepartament.map((u) => (
                      <Chip
                        key={u.uuid}
                        avatar={<Avatar src={u.imatge}>{u.nom.charAt(0)}</Avatar>}
                        label={u.nom}
                        size="small"
                        variant="outlined"
                      />
                    ))}
                  </Stack>
                </Stack>
              )}
            </>
          )}
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