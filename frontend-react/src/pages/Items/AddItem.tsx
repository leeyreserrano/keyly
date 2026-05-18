import { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router';
import { useTranslation } from 'react-i18next';
import {
  Stack, Typography, Paper, Button, TextField,
  IconButton, Box, Divider, InputAdornment, MenuItem, Select,
  FormControl, Menu
} from '@mui/material';
import KeyRoundedIcon from '@mui/icons-material/VpnKeyRounded';
import VisibilityIcon from '@mui/icons-material/Visibility';
import VisibilityOffIcon from '@mui/icons-material/VisibilityOff';
import AutoFixHighOutlinedIcon from '@mui/icons-material/AutoFixHighOutlined';
import Header from '../../components/Header';
import GeneratePasswordModal from '../../components/GeneratePasswordModal';
import { itemsApi } from '../../api/itemsapi';
import { carpetasApi, type Carpeta } from '../../api/carpetasapi';
import { utilsApi } from '../../api/utilsapi';
import toast from 'react-hot-toast';
import { useCrypto } from '../../context/CryptoContext';
import {
  generateDataKey, encryptPasswordWithDataKey,
  rsaEncrypt
} from '../../crypto/cryptoService';
import { useShareSelector } from '../../hooks/useShareSelector';
import ShareSelectorInline from '../../components/ShareSelectorInline';

const NOVA_CARPETA_VALUE = '__nova__';
const SENSE_CARPETA_VALUE = '__cap__';

export default function AddItem() {
  const navigate = useNavigate();
  const location = useLocation();
  const { t } = useTranslation('item');
  const { publicKey } = useCrypto();

  const { carpetaUuid } = (location.state as { carpetaUuid?: string }) ?? {};

  const [titol, setTitol] = useState('');
  const [nomUsuari, setNomUsuari] = useState('');
  const [notes, setNotes] = useState('');
  const [url, setUrl] = useState('');
  const [contrasenya, setContrasenya] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState<{ titol?: string; contrasenya?: string }>({});
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const [openGenerateModal, setOpenGenerateModal] = useState(false);
  const [carpetes, setCarpetes] = useState<Carpeta[]>([]);
  const [carpetaSeleccionada, setCarpetaSeleccionada] = useState<string>(SENSE_CARPETA_VALUE);
  const [novaCarpetaNom, setNovaCarpetaNom] = useState('');
  const [novaCarpetaError, setNovaCarpetaError] = useState<string | undefined>();


  useEffect(() => {
    carpetasApi.fetchItems().then((data) => {
      setCarpetes(data);
      if (carpetaUuid) setCarpetaSeleccionada(carpetaUuid);
    }).catch(() => { });
  }, [carpetaUuid]);


  const shareSelector = useShareSelector();
  const validate = (): boolean => {
    const newErrors: { titol?: string; contrasenya?: string } = {};
    if (!titol.trim()) newErrors.titol = t('required.title');
    if (!contrasenya.trim()) newErrors.contrasenya = t('required.password');
    setErrors(newErrors);
    if (carpetaSeleccionada === NOVA_CARPETA_VALUE && !novaCarpetaNom.trim()) {
      setNovaCarpetaError(t('required.folder_name'));
      return false;
    }
    setNovaCarpetaError(undefined);
    return Object.keys(newErrors).length === 0;
  };

  const handleSave = async () => {
    if (!validate()) return;
    if (!publicKey) {
      toast.error(t('error.crypto_session'));
      return;
    }
    setLoading(true);
    try {
      const dataKeyBytes = generateDataKey();
      const { encrypted: encryptedPassword, iv } = await encryptPasswordWithDataKey(dataKeyBytes, contrasenya);
      const encryptedDataKeyPropi = await rsaEncrypt(publicKey, dataKeyBytes);

      const newItem = await itemsApi.addItem({
        titol, nomUsuari, notes, url,
        contrasenya: encryptedPassword, iv,
        encryptedDataKey: encryptedDataKeyPropi,
      });
      if (!newItem) throw new Error(t('toast.error.create'));

      if (carpetaSeleccionada === NOVA_CARPETA_VALUE) {
        const novaCarpeta = await carpetasApi.addCarpeta({ nom: novaCarpetaNom });
        await carpetasApi.addExistingItem(novaCarpeta.uuid, newItem.uuid);
      } else if (carpetaSeleccionada !== SENSE_CARPETA_VALUE) {
        await carpetasApi.addExistingItem(carpetaSeleccionada, newItem.uuid);
      }

      toast.success(t('toast.success.create'));
      navigate(-1);
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : t('toast.error.create');
      toast.error(message);
    } finally {
      setLoading(false);
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

  return (
    <Stack sx={{ height: '100%', overflow: 'hidden' }}>
      <Header
        title={t('add.title')}
        icon={<KeyRoundedIcon sx={{ fontSize: 30, color: 'text.primary' }} />}
        showBackButton
      />
      <Stack sx={{ flex: 1, overflow: 'auto', minWidth: 0 }}>
        <Box sx={{ px: 4, py: 3, display: 'flex', justifyContent: 'center' }}>
          <Paper variant="outlined" sx={{ p: 4, borderRadius: 3, width: '70%', maxWidth: 500, display: 'flex', flexDirection: 'column', gap: 3 }}>
            <Typography variant="h5" sx={{ fontWeight: 700 }}>{t('add.new')}</Typography>
            <Divider />

            <Stack spacing={0.5}>
              <Typography sx={{ fontSize: '0.8rem', fontWeight: 600, color: 'text.secondary' }}>{t('field.title')} *</Typography>
              <TextField fullWidth value={titol} onChange={(e) => setTitol(e.target.value)} error={!!errors.titol} helperText={errors.titol} placeholder={t('placeholder.title')} />
            </Stack>

            <Stack spacing={0.5}>
              <Typography sx={{ fontSize: '0.8rem', fontWeight: 600, color: 'text.secondary' }}>{t('field.user')}</Typography>
              <TextField fullWidth value={nomUsuari} onChange={(e) => setNomUsuari(e.target.value)} placeholder={t('placeholder.user')} />
            </Stack>

            <Stack spacing={0.5}>
              <Typography sx={{ fontSize: '0.8rem', fontWeight: 600, color: 'text.secondary' }}>{t('field.notes')}</Typography>
              <TextField fullWidth value={notes} onChange={(e) => setNotes(e.target.value)} placeholder={t('placeholder.notes')} />
            </Stack>

            <Stack spacing={0.5}>
              <Typography sx={{ fontSize: '0.8rem', fontWeight: 600, color: 'text.secondary' }}>{t('field.url')}</Typography>
              <TextField fullWidth value={url} onChange={(e) => setUrl(e.target.value)} placeholder={t('placeholder.url')} />
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
                    </InputAdornment>
                  ),
                }}
              />
            </Stack>

            <GeneratePasswordModal open={openGenerateModal} onClose={() => setOpenGenerateModal(false)} onConfirm={(password) => setContrasenya(password)} />

            <Stack spacing={0.5}>
              <Typography sx={{ fontSize: '0.8rem', fontWeight: 600, color: 'text.secondary' }}>{t('field.folder')}</Typography>
              <FormControl fullWidth>
                <Select value={carpetaSeleccionada} onChange={(e) => setCarpetaSeleccionada(e.target.value)} displayEmpty>
                  <MenuItem value={SENSE_CARPETA_VALUE}>{t('placeholder.folder')}</MenuItem>
                  {carpetes.map((c) => <MenuItem key={c.uuid} value={c.uuid}>{c.nom}</MenuItem>)}
                  <MenuItem value={NOVA_CARPETA_VALUE}>+ {t('field.new_folder_name')}</MenuItem>
                </Select>
              </FormControl>
            </Stack>

            {carpetaSeleccionada === NOVA_CARPETA_VALUE && (
              <Stack spacing={0.5}>
                <Typography sx={{ fontSize: '0.8rem', fontWeight: 600, color: 'text.secondary' }}>{t('field.new_folder_name')} *</Typography>
                <TextField fullWidth value={novaCarpetaNom} onChange={(e) => setNovaCarpetaNom(e.target.value)} error={!!novaCarpetaError} helperText={novaCarpetaError} placeholder={t('placeholder.new_folder')} />
              </Stack>
            )}

            <ShareSelectorInline
              t={t}
              esAdmin={shareSelector.esAdmin}
              tab={shareSelector.tab}
              onTabChange={(v) => { shareSelector.setTab(v); shareSelector.setSeleccionats([]); shareSelector.handleSelectDepartament?.(''); } }
              filtrats={shareSelector.filtrats}
              departamentsFiltrats={shareSelector.departamentsFiltrats}
              usuarisDepartament={shareSelector.usuarisDepartament}
              seleccionats={shareSelector.seleccionats}
              departamentSeleccionat={shareSelector.departamentSeleccionat}
              searchUsuaris={shareSelector.searchUsuaris}
              onSearchUsuaris={shareSelector.setSearchUsuaris}
              searchDept={shareSelector.searchDept}
              onSearchDept={shareSelector.setSearchDept}
              permisCompartir={shareSelector.permisCompartir}
              onPermisChange={shareSelector.setPermisCompartir}
              onToggleSeleccio={shareSelector.toggleSeleccio}
              onSelectDepartament={shareSelector.handleSelectDepartament} 
              allUsuarisAmbDept={[]}           
               />

            <Divider />
            <Stack direction="row" sx={{ gap: 1, justifyContent: 'flex-end' }}>
              <Button onClick={() => navigate(-1)} variant="outlined" sx={{ textTransform: 'none', fontWeight: 600 }}>{t('button.cancel')}</Button>
              <Button onClick={handleSave} variant="contained" disabled={loading} sx={{ textTransform: 'none', fontWeight: 600 }}>
                {loading ? t('button.saving') : t('button.save')}
              </Button>
            </Stack>
          </Paper>
        </Box>
      </Stack>
    </Stack>
  );
}