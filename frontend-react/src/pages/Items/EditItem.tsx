import { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router';
import { useTranslation } from 'react-i18next';
import {
  Stack, Typography, Paper, IconButton, Button,
  Box, CircularProgress, TextField,
  Alert, Divider, InputAdornment, MenuItem, Select, FormControl, Menu
} from '@mui/material';
import KeyRoundedIcon from '@mui/icons-material/VpnKeyRounded';
import ContentCopyOutlinedIcon from '@mui/icons-material/ContentCopyOutlined';
import VisibilityIcon from '@mui/icons-material/Visibility';
import VisibilityOffIcon from '@mui/icons-material/VisibilityOff';
import AutoFixHighOutlinedIcon from '@mui/icons-material/AutoFixHighOutlined';
import toast from 'react-hot-toast';
import Header from '../../components/Header';
import ShareSelectorInline from '../../components/ShareSelectorInline';
import { itemsApi, type Item } from '../../api/itemsapi';
import { carpetasApi, type Carpeta } from '../../api/carpetasapi';
import { compartitsApi, type Compartit, type CompartitItem } from '../../api/compartitsapi';
import { useShareSelector } from '../../hooks/useShareSelector';
import { useCrypto } from '../../context/CryptoContext';
import { utilsApi } from '../../api/utilsapi';
import {
  rsaDecrypt, rsaEncrypt,
  decryptPasswordWithDataKey, encryptPasswordWithDataKey,
  generateDataKey,
} from '../../crypto/cryptoService';
import GeneratePasswordModal from '../../components/GeneratePasswordModal';

const NOVA_CARPETA_VALUE = '__nova__';
const SENSE_CARPETA_VALUE = '__cap__';

export default function EditItem() {
  const navigate = useNavigate();
  const location = useLocation();
  const { t } = useTranslation('item');
  const { uuid, compartitUuid, itemUuid } = location.state || {};
  const { privateKey, publicKey } = useCrypto();

  const esCompartit = !!compartitUuid;

  const [item, setItem] = useState<Item | CompartitItem | null>(null);
  const [, setCompartit] = useState<Compartit | null>(null);
  const [openGenerateModal, setOpenGenerateModal] = useState(false);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [showPassword, setShowPassword] = useState(false);
  const [titol, setTitol] = useState('');
  const [nomUsuari, setNomUsuari] = useState('');
  const [url, setUrl] = useState('');
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const [notes, setNotes] = useState('');
  const [contrasenya, setContrasenya] = useState('');
  const [decryptedPassword, setDecryptedPassword] = useState<string>('');
  const [errors, setErrors] = useState<{ titol?: string; contrasenya?: string }>({});
  const [carpetes, setCarpetes] = useState<Carpeta[]>([]);
  const [carpetaOriginal, setCarpetaOriginal] = useState<string>(SENSE_CARPETA_VALUE);
  const [carpetaSeleccionada, setCarpetaSeleccionada] = useState<string>(SENSE_CARPETA_VALUE);
  const [novaCarpetaNom, setNovaCarpetaNom] = useState('');
  const [novaCarpetaError, setNovaCarpetaError] = useState<string | undefined>();
  const [compartitsExistents, setCompartitsExistents] = useState<Compartit[]>([]);
  const [revocats, setRevocats] = useState<string[]>([]);

  const shareSelector = useShareSelector();

  useEffect(() => {
    const targetUuid = esCompartit ? compartitUuid : uuid;
    if (!targetUuid) { setLoadError(t('edit.error.load')); setLoading(false); return; }

    const loadData = async () => {
      try {
        if (esCompartit) {
          const data = await compartitsApi.getCompartit(compartitUuid);
          if (!data) { setLoadError(t('edit.error.load')); return; }
          setCompartit(data);
          let targetItem: CompartitItem | null = data.item ?? null;
          if (itemUuid && data.carpeta) targetItem = data.carpeta.items.find((i) => i.uuid === itemUuid) ?? null;
          if (!targetItem) { setLoadError(t('item.error.not_found')); return; }
          setItem(targetItem);
          setTitol(targetItem.titol);
          setNomUsuari(targetItem.nomUsuari);
          setUrl(targetItem.url ?? '');
          setNotes(targetItem.notes ?? '');
          if (privateKey && targetItem.encryptedDataKey?.encryptedDataKey && targetItem.iv && targetItem.contrasenya) {
            try {
              const dataKeyBytes = await rsaDecrypt(privateKey, targetItem.encryptedDataKey.encryptedDataKey);
              const plain = await decryptPasswordWithDataKey(dataKeyBytes, targetItem.contrasenya, targetItem.iv);
              setDecryptedPassword(plain);
              setContrasenya(plain);
            } catch {
              setDecryptedPassword('');
              setContrasenya(targetItem.contrasenya);
            }
          } else {
            setContrasenya(targetItem.contrasenya ?? '');
          }
        } else {
          const [allItems, allCarpetes] = await Promise.all([
            itemsApi.fetchItems(),
            carpetasApi.fetchItems(),
          ]);
          if (!allItems) { setLoadError(t('edit.error.load')); return; }
          const found = allItems.find((i) => i.uuid === uuid);
          if (!found) { setLoadError(t('item.error.not_found')); return; }
          setItem(found);
          setTitol(found.titol);
          setNomUsuari(found.nomUsuari);
          setUrl(found.url);
          setNotes(found.notes ?? '');
          setCarpetes(allCarpetes);
          const carpetaActual = allCarpetes.find((c) => c.items.some((i) => i.uuid === uuid));
          const carpetaUuidVal = carpetaActual?.uuid ?? SENSE_CARPETA_VALUE;
          setCarpetaOriginal(carpetaUuidVal);
          setCarpetaSeleccionada(carpetaUuidVal);
          if (privateKey && found.encryptedDataKey?.encryptedDataKey && found.iv && found.contrasenya) {
            try {
              const dataKeyBytes = await rsaDecrypt(privateKey, found.encryptedDataKey.encryptedDataKey);
              const plain = await decryptPasswordWithDataKey(dataKeyBytes, found.contrasenya, found.iv);
              setDecryptedPassword(plain);
              setContrasenya(plain);
            } catch {
              setDecryptedPassword('');
              setContrasenya(found.contrasenya);
            }
          } else {
            setContrasenya((found as Item).contrasenya ?? '');
          }

          const creats = await compartitsApi.fetchCompartitsCreats();
          const itemCompartits = (creats ?? []).filter(
            (c) => c.tipusEntitat === 'ITEM' && c.item?.uuid === uuid
          );
          setCompartitsExistents(itemCompartits);

          const usuarisPreseleccionats = itemCompartits
            .map((c) => c.usuariReceptor)
            .filter((u): u is NonNullable<typeof u> => !!u)
            .map((u) => ({
              uuid: u.uuid,
              nom: u.nom,
              correu: u.correu,
              imatge: u.imatge,
              publicKey: u.publicKey,
            }));
          shareSelector.setSeleccionats(usuarisPreseleccionats);
        }
      } catch (err: unknown) {
        const message = err instanceof Error ? err.message : t('edit.error.load');
        setLoadError(message);
      } finally {
        setLoading(false);
      }
    };
    loadData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [uuid, compartitUuid, itemUuid, privateKey, esCompartit, t]);

  const validate = (): boolean => {
    const newErrors: { titol?: string; contrasenya?: string } = {};
    if (!titol.trim()) newErrors.titol = t('edit.required.title');
    if (!contrasenya.trim()) newErrors.contrasenya = t('edit.required.password');
    setErrors(newErrors);
    if (!esCompartit && carpetaSeleccionada === NOVA_CARPETA_VALUE && !novaCarpetaNom.trim()) {
      setNovaCarpetaError(t('edit.required.folder'));
      return false;
    }
    setNovaCarpetaError(undefined);
    return Object.keys(newErrors).length === 0;
  };

  const handleSave = async () => {
    if (!item || !validate()) return;
    if (!publicKey || !privateKey) { toast.error(t('edit.error.crypto')); return; }
    setSaving(true);
    try {
      const passwordHasChanged = contrasenya !== decryptedPassword;
      let encryptedContrasenya = (item as Item).contrasenya;
      let iv = item.iv;
      let encryptedDataKey = item.encryptedDataKey?.encryptedDataKey;
      if (passwordHasChanged) {
        const dataKeyBytes = generateDataKey();
        const { encrypted, iv: newIv } = await encryptPasswordWithDataKey(dataKeyBytes, contrasenya);
        const newEncryptedDataKey = await rsaEncrypt(publicKey, dataKeyBytes);
        encryptedContrasenya = encrypted;
        iv = newIv;
        encryptedDataKey = newEncryptedDataKey;
      }
      await itemsApi.updateItem(item.uuid, { titol, nomUsuari, url, notes, contrasenya: encryptedContrasenya, iv, encryptedDataKey });

      if (!esCompartit) {
        const carpetaCanviada = carpetaSeleccionada !== carpetaOriginal;
        if (carpetaCanviada) {
          if (carpetaOriginal !== SENSE_CARPETA_VALUE) await carpetasApi.removeItem(carpetaOriginal, item.uuid);
          if (carpetaSeleccionada === NOVA_CARPETA_VALUE) {
            const novaCarpeta = await carpetasApi.addCarpeta({ nom: novaCarpetaNom });
            await carpetasApi.addExistingItem(novaCarpeta.uuid, item.uuid);
          } else if (carpetaSeleccionada !== SENSE_CARPETA_VALUE) {
            await carpetasApi.addExistingItem(carpetaSeleccionada, item.uuid);
          }
        }

        for (const compartitUuidRevocar of revocats) {
          await compartitsApi.deleteCompartit(compartitUuidRevocar);
        }

        const uuidsExistents = new Set(
          compartitsExistents
            .filter((c) => !revocats.includes(c.uuid))
            .map((c) => c.usuariReceptor?.uuid)
        );
        const nouUsuaris = shareSelector.seleccionats.filter((u) => !uuidsExistents.has(u.uuid));
        if (nouUsuaris.length > 0) {
          const originals = shareSelector.seleccionats;
          shareSelector.setSeleccionats(nouUsuaris);
          await shareSelector.compartirItem(item.uuid);
          shareSelector.setSeleccionats(originals);
        }
      }

      toast.success(t('edit.toast.success'));
      navigate(-1);
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : t('edit.error.save');
      toast.error(message);
    } finally {
      setSaving(false);
    }
  };

  const handleCopy = () => {
    if (contrasenya) {
      navigator.clipboard.writeText(contrasenya).catch(() => { });
      toast.success(t('item.toast.copy'));
    }
  };
  const handleComplexitat = async (nivell: string) => {
    setAnchorEl(null);
    const configs = {
      baixa: { longitud: 8, may: true, quantitatMay: 0, numeros: false, quantitatNumeros: 0, caractersEspecials: false, quantitatCaractersEspecials: 0 },
      mitjana: { longitud: 12, may: true, quantitatMay: 3, numeros: true, quantitatNumeros: 3, caractersEspecials: false, quantitatCaractersEspecials: 0 },
      alta: { longitud: 20, may: true, quantitatMay: 5, numeros: true, quantitatNumeros: 4, caractersEspecials: true, quantitatCaractersEspecials: 3 },
    };
    try {
      const result = await utilsApi.generatePassword(configs[nivell as keyof typeof configs]);
      if (result) setContrasenya(result);
    } catch {
      toast.error(t('toast.error.generate_password'));
    }
  };
  const handleToggleRevocat = (compartitUuidTarget: string) => {
    setRevocats((prev) =>
      prev.includes(compartitUuidTarget)
        ? prev.filter((u) => u !== compartitUuidTarget)
        : [...prev, compartitUuidTarget]
    );
  };

  if (loading) {
    return (
      <Stack sx={{ height: '100%', overflow: 'hidden' }}>
        <Header title={t('edit.title')} icon={<KeyRoundedIcon sx={{ fontSize: 30, color: 'text.primary' }} />} showBackButton />
        <Stack sx={{ flex: 1, alignItems: 'center', justifyContent: 'center' }}>
          <CircularProgress />
        </Stack>
      </Stack>
    );
  }

  if (loadError) {
    return (
      <Stack sx={{ height: '100%', overflow: 'hidden' }}>
        <Header title={t('edit.title')} icon={<KeyRoundedIcon sx={{ fontSize: 30, color: 'text.primary' }} />} showBackButton />
        <Box sx={{ p: 4 }}><Alert severity="error">{loadError}</Alert></Box>
      </Stack>
    );
  }

  return (
    <Stack sx={{ height: '100%', overflow: 'hidden' }}>
      <Header title={t('edit.title')} icon={<KeyRoundedIcon sx={{ fontSize: 30, color: 'text.primary' }} />} showBackButton />
      <Stack sx={{ flex: 1, overflow: 'auto', minWidth: 0 }}>
        <Box sx={{ px: 4, py: 3, display: 'flex', justifyContent: 'center' }}>
          <Paper variant="outlined" sx={{ p: 4, borderRadius: 3, width: '70%', maxWidth: 600, display: 'flex', flexDirection: 'column', gap: 3 }}>
            <Typography variant="h5" sx={{ fontWeight: 700 }}>{t('edit.title')}</Typography>
            <Divider />

            <Stack spacing={0.5}>
              <Typography sx={{ fontSize: '0.8rem', fontWeight: 600, color: 'text.secondary' }}>{t('edit.field.title')} *</Typography>
              <TextField fullWidth value={titol} onChange={(e) => setTitol(e.target.value)} error={!!errors.titol} helperText={errors.titol} />
            </Stack>

            <Stack spacing={0.5}>
              <Stack direction="row" sx={{ justifyContent: 'space-between', alignItems: 'center' }}>
                <Typography sx={{ fontSize: '0.8rem', fontWeight: 600, color: 'text.secondary' }}>{t('field.password')} *</Typography>
                <Button size="small" startIcon={<AutoFixHighOutlinedIcon sx={{ fontSize: 15 }} />} onClick={(e) => setAnchorEl(e.currentTarget)} sx={{ textTransform: 'none', fontWeight: 600, fontSize: '0.75rem' }}>
                  {t('generate.button')}
                </Button>
                <Menu anchorEl={anchorEl} open={Boolean(anchorEl)} onClose={() => setAnchorEl(null)}>
                  <MenuItem onClick={() => handleComplexitat('baixa')}>{t('generate.low')}</MenuItem>
                  <MenuItem onClick={() => handleComplexitat('mitjana')}>{t('generate.medium')}</MenuItem>
                  <MenuItem onClick={() => handleComplexitat('alta')}>{t('generate.high')}</MenuItem>
                  <Divider />
                  <MenuItem onClick={() => { setAnchorEl(null); setOpenGenerateModal(true); }}>{t('generate.custom')}</MenuItem>
                </Menu>
              </Stack>
              <TextField
                fullWidth
                type={showPassword ? 'text' : 'password'}
                value={contrasenya}
                onChange={(e) => setContrasenya(e.target.value)}
                error={!!errors.contrasenya}
                helperText={errors.contrasenya}
                InputProps={{
                  endAdornment: (
                    <InputAdornment position="end">
                      <IconButton onClick={() => setShowPassword((p) => !p)} sx={{ borderRadius: 2, border: '1px solid', borderColor: 'divider', color: 'text.secondary' }}>
                        {showPassword ? <VisibilityOffIcon fontSize="small" /> : <VisibilityIcon fontSize="small" />}
                      </IconButton>
                      <IconButton onClick={handleCopy} sx={{ borderRadius: 2, border: '1px solid', borderColor: 'divider', color: 'primary.main' }}>
                        <ContentCopyOutlinedIcon fontSize="small" />
                      </IconButton>
                    </InputAdornment>
                  ),
                }}
              />
            </Stack>

            <GeneratePasswordModal open={openGenerateModal} onClose={() => setOpenGenerateModal(false)} onConfirm={(password) => setContrasenya(password)} />
              
            <Stack spacing={0.5}>
              <Typography sx={{ fontSize: '0.8rem', fontWeight: 600, color: 'text.secondary' }}>{t('edit.field.url')}</Typography>
              <TextField fullWidth value={url} onChange={(e) => setUrl(e.target.value)} />
            </Stack>

            <Stack spacing={0.5}>
              <Typography sx={{ fontSize: '0.8rem', fontWeight: 600, color: 'text.secondary' }}>{t('edit.field.notes')}</Typography>
              <TextField fullWidth multiline rows={3} value={notes} onChange={(e) => setNotes(e.target.value)} />
            </Stack>

            {!esCompartit && (
              <>
                <Stack spacing={0.5}>
                  <Typography sx={{ fontSize: '0.8rem', fontWeight: 600, color: 'text.secondary' }}>{t('edit.field.folder')}</Typography>
                  <FormControl fullWidth>
                    <Select value={carpetaSeleccionada} onChange={(e) => setCarpetaSeleccionada(e.target.value)}>
                      <MenuItem value={SENSE_CARPETA_VALUE}>{t('placeholder.folder')}</MenuItem>
                      {carpetes.map((c) => <MenuItem key={c.uuid} value={c.uuid}>{c.nom}</MenuItem>)}
                      <MenuItem value={NOVA_CARPETA_VALUE}>+ {t('edit.field.new_folder')}</MenuItem>
                    </Select>
                  </FormControl>
                </Stack>
                {carpetaSeleccionada === NOVA_CARPETA_VALUE && (
                  <Stack spacing={0.5}>
                    <Typography sx={{ fontSize: '0.8rem', fontWeight: 600, color: 'text.secondary' }}>{t('edit.field.new_folder')} *</Typography>
                    <TextField fullWidth value={novaCarpetaNom} onChange={(e) => setNovaCarpetaNom(e.target.value)} error={!!novaCarpetaError} helperText={novaCarpetaError} placeholder={t('edit.placeholder.new_folder')} />
                  </Stack>
                )}

                <Divider />

                <ShareSelectorInline
                  t={t}
                  esAdmin={shareSelector.esAdmin}
                  tab={shareSelector.tab}
                  onTabChange={(v) => { shareSelector.setTab(v); shareSelector.setSeleccionats([]); shareSelector.handleSelectDepartament(''); }}
                  filtrats={shareSelector.filtrats}
                  departamentsFiltrats={shareSelector.departamentsFiltrats}
                  usuarisDepartament={shareSelector.usuarisDepartament}
                  allUsuarisAmbDept={shareSelector.usuarisAmbDept}
                  seleccionats={shareSelector.seleccionats}
                  departamentSeleccionat={shareSelector.departamentSeleccionat}
                  searchUsuaris={shareSelector.searchUsuaris}
                  onSearchUsuaris={shareSelector.setSearchUsuaris}
                  searchDept={shareSelector.searchDept}
                  onSearchDept={shareSelector.setSearchDept}
                  permisCompartir={shareSelector.permisCompartir}
                  onPermisChange={shareSelector.setPermisCompartir}
                  onToggleSeleccio={(u) => {
                    const compartitExistent = compartitsExistents.find((c) => c.usuariReceptor?.uuid === u.uuid);
                    if (compartitExistent) {
                      handleToggleRevocat(compartitExistent.uuid);
                    }
                    shareSelector.toggleSeleccio(u);
                  }}
                  onSelectDepartament={shareSelector.handleSelectDepartament}
                  revocats={revocats}
                  compartitsExistents={compartitsExistents}
                />
              </>
            )}

            <Divider />
            <Stack direction="row" sx={{ gap: 1, justifyContent: 'flex-end' }}>
              <Button onClick={() => navigate(-1)} variant="outlined" sx={{ textTransform: 'none', fontWeight: 600 }}>{t('edit.button.cancel')}</Button>
              <Button onClick={handleSave} variant="contained" disabled={saving} sx={{ textTransform: 'none', fontWeight: 600 }}>
                {saving ? t('edit.button.saving') : t('edit.button.save')}
              </Button>
            </Stack>
          </Paper>
        </Box>
      </Stack>
    </Stack>
  );
}