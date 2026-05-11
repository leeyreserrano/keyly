import { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router';
import { useTranslation } from 'react-i18next';
import {
  Stack, Typography, Paper, IconButton, Button,
  Box, CircularProgress, TextField,
  Alert, Divider, InputAdornment, MenuItem, Select, FormControl,
} from '@mui/material';
import KeyRoundedIcon from '@mui/icons-material/VpnKeyRounded';
import ContentCopyOutlinedIcon from '@mui/icons-material/ContentCopyOutlined';
import VisibilityIcon from '@mui/icons-material/Visibility';
import VisibilityOffIcon from '@mui/icons-material/VisibilityOff';
import toast from 'react-hot-toast';
import Header from '../../components/Header';
import { itemsApi, type Item } from '../../api/itemsapi';
import { carpetasApi, type Carpeta } from '../../api/carpetasapi';
import { compartitsApi, type Compartit, type CompartitItem } from '../../api/compartitsapi';
import { useCrypto } from '../../context/CryptoContext';
import {
  rsaDecrypt, rsaEncrypt,
  decryptPasswordWithDataKey, encryptPasswordWithDataKey,
  generateDataKey,
} from '../../crypto/cryptoService';

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
  const [compartit, setCompartit] = useState<Compartit | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [showPassword, setShowPassword] = useState(false);
  const [titol, setTitol] = useState('');
  const [nomUsuari, setNomUsuari] = useState('');
  const [url, setUrl] = useState('');
  const [notes, setNotes] = useState('');
  const [contrasenya, setContrasenya] = useState('');
  const [decryptedPassword, setDecryptedPassword] = useState<string>('');
  const [errors, setErrors] = useState<{ titol?: string; contrasenya?: string }>({});
  const [carpetes, setCarpetes] = useState<Carpeta[]>([]);
  const [carpetaOriginal, setCarpetaOriginal] = useState<string>(SENSE_CARPETA_VALUE);
  const [carpetaSeleccionada, setCarpetaSeleccionada] = useState<string>(SENSE_CARPETA_VALUE);
  const [novaCarpetaNom, setNovaCarpetaNom] = useState('');
  const [novaCarpetaError, setNovaCarpetaError] = useState<string | undefined>();

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
          const [allItems, allCarpetes] = await Promise.all([itemsApi.fetchItems(), carpetasApi.fetchItems()]);
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
        }
      } catch (err: unknown) {
        const message = err instanceof Error ? err.message : t('edit.error.load');
        setLoadError(message);
      } finally {
        setLoading(false);
      }
    };
    loadData();
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
    if (!publicKey || !privateKey) {
      toast.error(t('edit.error.crypto'));
      return;
    }
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

  const handleCopy = async () => {
    try {
      await navigator.clipboard.writeText(contrasenya);
      toast.success(t('edit.toast.copy'));
    } catch {
      toast.error(t('edit.toast.error_copy'));
    }
  };

  return (
    <Stack sx={{ height: '100%', overflow: 'hidden' }}>
      <Header
        title={item?.titol || t('edit.title_fallback')}
        icon={<KeyRoundedIcon sx={{ fontSize: 30, color: 'text.primary' }} />}
        showBackButton
      />

      <Stack sx={{ flex: 1, overflow: 'auto', minWidth: 0 }}>
        <Box sx={{ px: 4, py: 4, display: 'flex', justifyContent: 'center' }}>
          {loading ? (
            <Stack sx={{ alignItems: 'center', mt: 10 }}><CircularProgress /></Stack>
          ) : loadError ? (
            <Alert severity="error" sx={{ mt: 2 }}>{loadError}</Alert>
          ) : (
            <Paper variant="outlined" sx={{ p: 4, borderRadius: 3, width: '70%', maxWidth: 500, display: 'flex', flexDirection: 'column', gap: 3 }}>
              <Typography variant="h5" sx={{ fontWeight: 700 }}>{item?.titol}</Typography>
              <Divider />

              <Stack spacing={0.5}>
                <Typography sx={{ fontSize: '0.8rem', fontWeight: 600, color: 'text.secondary' }}>{t('edit.field.title')} *</Typography>
                <TextField fullWidth value={titol} onChange={(e) => setTitol(e.target.value)} error={!!errors.titol} helperText={errors.titol} />
              </Stack>
              <Stack spacing={0.5}>
                <Typography sx={{ fontSize: '0.8rem', fontWeight: 600, color: 'text.secondary' }}>{t('edit.field.user')}</Typography>
                <TextField fullWidth value={nomUsuari} onChange={(e) => setNomUsuari(e.target.value)} />
              </Stack>
              <Stack spacing={0.5}>
                <Typography sx={{ fontSize: '0.8rem', fontWeight: 600, color: 'text.secondary' }}>{t('edit.field.notes')}</Typography>
                <TextField fullWidth value={notes} onChange={(e) => setNotes(e.target.value)} />
              </Stack>
              <Stack spacing={0.5}>
                <Typography sx={{ fontSize: '0.8rem', fontWeight: 600, color: 'text.secondary' }}>{t('edit.field.url')}</Typography>
                <TextField fullWidth value={url} onChange={(e) => setUrl(e.target.value)} />
              </Stack>

              <Stack spacing={0.5}>
                <Typography sx={{ fontSize: '0.8rem', fontWeight: 600, color: 'text.secondary' }}>{t('edit.field.password')} *</Typography>
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
          )}
        </Box>
      </Stack>
    </Stack>
  );
}