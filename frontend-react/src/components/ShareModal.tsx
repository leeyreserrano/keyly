import { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import {
  Dialog, DialogTitle, DialogContent, DialogActions,
  Button, Stack, Typography, TextField, Checkbox, Avatar,
  CircularProgress, Chip, FormControl, Select,
  MenuItem, Divider, Box, Tabs, Tab,
} from '@mui/material';
import { compartitsApi, type Compartit, type Permisos } from '../api/compartitsapi';
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

  const [compartitsExistents, setCompartitsExistents] = useState<Compartit[]>([]);
  const [revocats, setRevocats] = useState<string[]>([]);

  useEffect(() => {
    if (!open) return;

    setSearch('');
    setSearchDept('');
    setSeleccionats([]);
    setDepartamentSeleccionat('');
    setPermisos('LECTURA');
    setTab('usuaris');
    setRevocats([]);
    setCompartitsExistents([]);

    const load = async () => {
      setLoadingUsuaris(true);
      try {
        const promises: Promise<unknown>[] = [
          usuarisApi.fetchAllPublic(),
          compartitsApi.fetchCompartitsCreats(),
        ];
        if (esAdmin) {
          promises.push(usuarisApi.fetchAllAmbDepartament());
          promises.push(departamentsApi.fetchAll());
        }

        const results = await Promise.all(promises);
        const totsUsuaris = results[0] as UsuariPublic[];
        const totsCreats = results[1] as Compartit[];

        if (esAdmin) {
          setUsuarisAmbDept((results[2] as UsuariAmbDepartament[]).filter((u) => u.uuid !== usuari?.uuid));
          setDepartaments(results[3] as Departament[]);
        }

        const filteredUsuaris = totsUsuaris.filter((u) => u.uuid !== usuari?.uuid);
        setUsuaris(filteredUsuaris);

        const compartitsEntitat = totsCreats.filter(
          (c) =>
            c.tipusEntitat === tipusEntitat &&
            (tipusEntitat === 'ITEM' ? c.item?.uuid === entitatUuid : c.carpeta?.uuid === entitatUuid)
        );
        setCompartitsExistents(compartitsEntitat);

        const uuidsCompartits = new Set(compartitsEntitat.map((c) => c.usuariReceptor?.uuid).filter(Boolean));
        const preseleccionats = filteredUsuaris.filter((u) => uuidsCompartits.has(u.uuid));
        setSeleccionats(preseleccionats);
      } catch {
        toast.error(t('error.load_users'));
      } finally {
        setLoadingUsuaris(false);
      }
    };
    load();
  }, [open, usuari?.uuid, esAdmin, tipusEntitat, entitatUuid, t]);

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

  const esExistent = (uuid: string) =>
    compartitsExistents.some((c) => c.usuariReceptor?.uuid === uuid);

  const esRevocat = (uuid: string) => {
    const compartit = compartitsExistents.find((c) => c.usuariReceptor?.uuid === uuid);
    return compartit ? revocats.includes(compartit.uuid) : false;
  };

  const toggleSeleccio = (u: UsuariPublic) => {
    const compartitExistent = compartitsExistents.find((c) => c.usuariReceptor?.uuid === u.uuid);

    if (compartitExistent) {
      setRevocats((prev) =>
        prev.includes(compartitExistent.uuid)
          ? prev.filter((id) => id !== compartitExistent.uuid)
          : [...prev, compartitExistent.uuid]
      );
      setSeleccionats((prev) =>
        prev.some((s) => s.uuid === u.uuid)
          ? prev.filter((s) => s.uuid !== u.uuid)
          : [...prev, u]
      );
    } else {
      setSeleccionats((prev) =>
        prev.some((s) => s.uuid === u.uuid)
          ? prev.filter((s) => s.uuid !== u.uuid)
          : [...prev, u]
      );
    }
  };

  const handleSelectDepartament = (deptUuid: string) => {
    if (departamentSeleccionat === deptUuid) {
      setDepartamentSeleccionat('');
      const uuidsExistents = new Set(compartitsExistents.map((c) => c.usuariReceptor?.uuid));
      setSeleccionats((prev) => prev.filter((u) => uuidsExistents.has(u.uuid)));
      return;
    }
    setDepartamentSeleccionat(deptUuid);
    const membresDept = usuarisAmbDept
      .filter((u) => u.departament?.uuid === deptUuid)
      .map((u) => ({ uuid: u.uuid, nom: u.nom, correu: u.correu, imatge: u.imatge, publicKey: u.publicKey }));

    setSeleccionats((prev) => {
      const uuidsActuals = new Set(prev.map((u) => u.uuid));
      const nous = membresDept.filter((u) => !uuidsActuals.has(u.uuid));
      return [...prev, ...nous];
    });
  };

  const handleTabChange = (_: React.SyntheticEvent, v: TabValue) => {
    setTab(v);
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
    const uuidsExistents = new Set(
      compartitsExistents
        .filter((c) => !revocats.includes(c.uuid))
        .map((c) => c.usuariReceptor?.uuid)
    );
    const nouUsuaris = seleccionats.filter((u) => !uuidsExistents.has(u.uuid) && !esRevocat(u.uuid));

    if (revocats.length === 0 && nouUsuaris.length === 0) {
      toast.error(t('error.no_changes'));
      return;
    }

    setSharing(true);
    try {
      for (const compartitUuid of revocats) {
        await compartitsApi.deleteCompartit(compartitUuid);
      }

      if (nouUsuaris.length > 0) {
        if (!privateKey) {
          toast.error(t('error.crypto'));
          return;
        }

        if (tipusEntitat === 'ITEM') {
          const item = await itemsApi.getItem(entitatUuid);
          if (!item) throw new Error(t('error.item_not_found'));

          let dataKeyBytes: Uint8Array | null = null;
          if (item.encryptedDataKey?.encryptedDataKey && item.iv && item.contrasenya) {
            dataKeyBytes = await rsaDecrypt(privateKey, item.encryptedDataKey.encryptedDataKey);
          }

          const usuarisPayload = await buildUsuarisPayloadItem(nouUsuaris, dataKeyBytes, entitatUuid);
          await compartitsApi.addCompartit({ entitatUuid, tipusEntitat: 'ITEM', usuaris: usuarisPayload });
        } else {
          const carpeta = await carpetasApi.fetchItemsFromCarpeta(entitatUuid);
          const itemsAmbDataKey = await Promise.all(
            carpeta
              .filter((item) => item.encryptedDataKey?.encryptedDataKey)
              .map(async (item) => {
                const dataKeyBytes = await rsaDecrypt(privateKey, item.encryptedDataKey!.encryptedDataKey);
                return { uuid: item.uuid, dataKeyBytes };
              })
          );
          const usuarisPayload = await buildUsuarisPayloadCarpeta(nouUsuaris, itemsAmbDataKey);
          await compartitsApi.addCompartit({ entitatUuid, tipusEntitat: 'CARPETA', usuaris: usuarisPayload });
        }
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

  const seleccionatsActius = seleccionats.filter((u) => !esRevocat(u.uuid));
  const totalCambis = revocats.length + seleccionatsActius.filter((u) => !esExistent(u.uuid)).length;

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
              <Select
                value={permisos}
                onChange={(e) => setPermisos(e.target.value as Permisos)}
              >
                <MenuItem value="LECTURA">{t('permissions.read')}</MenuItem>
                <MenuItem value="ESCRIPTURA">{t('permissions.write')}</MenuItem>
              </Select>
            </FormControl>
          )}

          {esAdmin && (
            <Tabs value={tab} onChange={handleTabChange} textColor="primary" indicatorColor="primary">
              <Tab label={t('tab.users')} value="usuaris" />
              <Tab label={t('tab.department')} value="departament" />
            </Tabs>
          )}

          {seleccionats.length > 0 && (
            <Stack direction="row" flexWrap="wrap" gap={0.75}>
              {seleccionats.map((u) => {
                const revocat = esRevocat(u.uuid);
                const existent = esExistent(u.uuid);
                return (
                  <Chip
                    key={u.uuid}
                    label={u.nom}
                    onDelete={() => toggleSeleccio(u)}
                    size="small"
                    color={revocat ? 'error' : existent ? 'primary' : 'default'}
                    variant={revocat ? 'outlined' : 'filled'}
                  />
                );
              })}
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
                  <Typography sx={{ py: 2, textAlign: 'center', color: 'text.disabled', fontSize: '0.875rem' }}>
                    {t('no_users')}
                  </Typography>
                ) : (
                  filtrats.map((u, i) => {
                    const seleccionat = seleccionats.some((s) => s.uuid === u.uuid);
                    const revocat = esRevocat(u.uuid);
                    const existent = esExistent(u.uuid);
                    return (
                      <Box key={u.uuid}>
                        <Stack
                          direction="row"
                          sx={{
                            alignItems: 'center', px: 1.5, py: 1, gap: 1.5, cursor: 'pointer',
                            bgcolor: revocat ? 'error.light' : seleccionat ? 'action.selected' : 'transparent',
                            opacity: revocat ? 0.6 : 1,
                            '&:hover': { bgcolor: revocat ? 'error.light' : 'action.hover' },
                            transition: 'background-color 150ms ease',
                          }}
                          onClick={() => toggleSeleccio(u)}
                        >
                          <Checkbox
                            checked={seleccionat && !revocat}
                            size="small"
                            sx={{ p: 0 }}
                            onClick={(e) => e.stopPropagation()}
                            onChange={() => toggleSeleccio(u)}
                          />
                          <Avatar src={u.imatge} sx={{ width: 28, height: 28, fontSize: '0.75rem' }}>
                            {u.nom.charAt(0).toUpperCase()}
                          </Avatar>
                          <Stack sx={{ minWidth: 0, flex: 1 }}>
                            <Typography sx={{ fontWeight: 600, fontSize: '0.8rem' }}>{u.nom}</Typography>
                            <Typography sx={{ fontSize: '0.7rem', color: 'text.secondary' }}>{u.correu}</Typography>
                          </Stack>
                          {existent && !revocat && (
                            <Chip label={t('already_shared')} size="small" color="primary" variant="outlined" sx={{ fontSize: '0.65rem' }} />
                          )}
                          {revocat && (
                            <Chip label={t('will_revoke')} size="small" color="error" variant="outlined" sx={{ fontSize: '0.65rem' }} />
                          )}
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
                  <Typography sx={{ py: 2, textAlign: 'center', color: 'text.disabled', fontSize: '0.875rem' }}>
                    {t('no_departments')}
                  </Typography>
                ) : (
                  departamentsFiltrats.map((d, i) => {
                    const seleccionat = departamentSeleccionat === d.uuid;
                    const membres = usuarisAmbDept.filter((u) => u.departament?.uuid === d.uuid);
                    const count = membres.length;
                    const jaCompartits = membres.filter((u) => esExistent(u.uuid)).length;
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
                          <Checkbox
                            checked={seleccionat}
                            size="small"
                            sx={{ p: 0 }}
                            onClick={(e) => e.stopPropagation()}
                            onChange={() => handleSelectDepartament(d.uuid)}
                          />
                          <Stack sx={{ minWidth: 0, flex: 1 }}>
                            <Typography sx={{ fontWeight: 600, fontSize: '0.8rem' }}>{d.nom}</Typography>
                            <Typography sx={{ fontSize: '0.7rem', color: 'text.secondary' }}>
                              {count > 0
                                ? jaCompartits > 0
                                  ? `${count} ${t('users_count')} · ${jaCompartits} ${t('already_shared_count')}`
                                  : `${count} ${t('users_count')}`
                                : ''}
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
                        color={esExistent(u.uuid) ? 'primary' : 'default'}
                        variant={esExistent(u.uuid) ? 'filled' : 'outlined'}
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
          disabled={sharing || totalCambis === 0}
          sx={{
            textTransform: 'none', fontWeight: 600,
            bgcolor: 'white', color: 'primary.main',
            '&:hover': { bgcolor: 'grey.100' },
            '&.Mui-disabled': { bgcolor: 'grey.300', color: 'grey.500' },
          }}
        >
          {sharing ? t('sharing') : totalCambis > 0 ? t('save_changes_count', { count: totalCambis }) : t('share')}
        </Button>
      </DialogActions>
    </Dialog>
  );
}