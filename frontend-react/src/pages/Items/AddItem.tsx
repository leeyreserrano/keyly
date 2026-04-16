import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router';
import {
  Stack,
  Typography,
  Paper,
  Button,
  TextField,
  CssBaseline,
  IconButton,
  Box,
  Divider,
  InputAdornment,
  MenuItem,
  Select,
  FormControl,
  Menu,
} from '@mui/material';
import KeyRoundedIcon from '@mui/icons-material/VpnKeyRounded';
import VisibilityIcon from '@mui/icons-material/Visibility';
import VisibilityOffIcon from '@mui/icons-material/VisibilityOff';
import AutoFixHighOutlinedIcon from '@mui/icons-material/AutoFixHighOutlined';
import Sidebar from '../../components/Sidebar';
import AppTheme from '../../theme/AppTheme';
import Header from '../../components/Header';
import { itemsApi } from '../../api/itemsapi';
import { useLocation } from 'react-router';
import { carpetasApi, type Carpeta } from '../../api/carpetasapi';
import { utilsApi } from '../../api/utilsapi';
import GeneratePasswordModal from '../../components/GeneratePasswordModal';
import toast from 'react-hot-toast';

const NOVA_CARPETA_VALUE = '__nova__';
const SENSE_CARPETA_VALUE = '__cap__';

export default function AddItem() {
  const navigate = useNavigate();
  const location = useLocation();

  const [carpetaUuid] = useState<string | undefined>((location.state as { carpetaUuid?: string })?.carpetaUuid);
  const [titol, setTitol] = useState('');
  const [nomUsuari, setNomUsuari] = useState('');
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
      if (carpetaUuid) {
        setCarpetaSeleccionada(carpetaUuid);
      }
    }).catch(() => { });
  }, [carpetaUuid]);

  const validate = (): boolean => {
    const newErrors: { titol?: string; contrasenya?: string } = {};
    if (!titol.trim()) newErrors.titol = 'El títol és obligatori';
    if (!contrasenya.trim()) newErrors.contrasenya = 'La contrasenya és obligatòria';
    setErrors(newErrors);

    if (carpetaSeleccionada === NOVA_CARPETA_VALUE && !novaCarpetaNom.trim()) {
      setNovaCarpetaError('El nom de la carpeta és obligatori');
      return false;
    }
    setNovaCarpetaError(undefined);

    return Object.keys(newErrors).length === 0;
  };

  const handleSave = async () => {
    if (!validate()) return;
    setLoading(true);
    try {
      const newItem = await itemsApi.addItem({ titol, nomUsuari, url, contrasenya });
      if (!newItem) throw new Error("Error creant l'item");

      if (carpetaSeleccionada === NOVA_CARPETA_VALUE) {
        const novaCarpeta = await carpetasApi.addCarpeta({ nom: novaCarpetaNom });
        await carpetasApi.addExistingItem(novaCarpeta.uuid, newItem.uuid);
      } else if (carpetaSeleccionada !== SENSE_CARPETA_VALUE) {
        await carpetasApi.addExistingItem(carpetaSeleccionada, newItem.uuid);
      }

      toast.success('Item creat correctament');
      navigate('/Items');
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : "Error creant l'item";
      toast.error(message);
    } finally {
      setLoading(false);
    }
  };

  const handleComplexitat = async (nivell: string) => {
  setAnchorEl(null);

  const configs = {
    baixa:   { longitud: 8,  may: false, quantitatMay: 0, numeros: false, quantitatNumeros: 0, caractersEspecials: false, quantitatCaractersEspecials: 0 },
    mitjana: { longitud: 12, may: true,  quantitatMay: 3, numeros: true,  quantitatNumeros: 3, caractersEspecials: false, quantitatCaractersEspecials: 0 },
    alta:    { longitud: 20, may: true,  quantitatMay: 5, numeros: true,  quantitatNumeros: 4, caractersEspecials: true,  quantitatCaractersEspecials: 3 },
  };

  try {
    const result = await utilsApi.generatePassword(configs[nivell as keyof typeof configs]);
    if (result) setContrasenya(result);
  } catch {
    toast.error('Error generant la contrasenya');
  }
};
  return (
    <AppTheme>
      <CssBaseline enableColorScheme />
      <Stack direction="row" sx={{ minHeight: '100vh', width: '100%' }}>
        <Sidebar />

        <Stack sx={{ flex: 1, bgcolor: 'background.default', overflow: 'auto', minWidth: 0 }}>
          <Header
            title="Afegir Item"
            icon={<KeyRoundedIcon sx={{ fontSize: 30, color: 'text.primary' }} />}
            showBackButton
          />

          <Box sx={{ px: 4, py: 3, display: 'flex', justifyContent: 'center' }}>
            <Paper
              variant="outlined"
              sx={{
                p: 4,
                borderRadius: 3,
                width: '70%',
                maxWidth: 500,
                display: 'flex',
                flexDirection: 'column',
                gap: 3,
              }}
            >
              <Typography variant="h5" sx={{ fontWeight: 700 }}>
                Nou Item
              </Typography>

              <Divider />

              <Stack spacing={0.5}>
                <Typography sx={{ fontSize: '0.8rem', fontWeight: 600, color: 'text.secondary' }}>
                  Títol *
                </Typography>
                <TextField
                  fullWidth
                  value={titol}
                  onChange={(e) => setTitol(e.target.value)}
                  error={!!errors.titol}
                  helperText={errors.titol}
                  placeholder="Ex: Gmail personal"
                />
              </Stack>

              <Stack spacing={0.5}>
                <Typography sx={{ fontSize: '0.8rem', fontWeight: 600, color: 'text.secondary' }}>
                  Usuari / Email
                </Typography>
                <TextField
                  fullWidth
                  value={nomUsuari}
                  onChange={(e) => setNomUsuari(e.target.value)}
                  placeholder="Ex: usuari@exemple.com"
                />
              </Stack>

              <Stack spacing={0.5}>
                <Typography sx={{ fontSize: '0.8rem', fontWeight: 600, color: 'text.secondary' }}>
                  URL
                </Typography>
                <TextField
                  fullWidth
                  value={url}
                  onChange={(e) => setUrl(e.target.value)}
                  placeholder="Ex: https://gmail.com"
                />
              </Stack>

              <Stack spacing={0.5}>
                <Stack direction="row" sx={{ justifyContent: 'space-between', alignItems: 'center' }}>
                  <Typography sx={{ fontSize: '0.8rem', fontWeight: 600, color: 'text.secondary' }}>
                    Contrasenya *
                  </Typography>
                  <Button
                    size="small"
                    startIcon={<AutoFixHighOutlinedIcon sx={{ fontSize: 15 }} />}
                    onClick={(e) => setAnchorEl(e.currentTarget)}
                    sx={{ textTransform: 'none', fontWeight: 600, fontSize: '0.75rem' }}
                  >
                    Generar
                  </Button>
                  <Menu
                    anchorEl={anchorEl}
                    open={Boolean(anchorEl)}
                    onClose={() => setAnchorEl(null)}
                  >
                    <MenuItem onClick={() => handleComplexitat('baixa')}>Complexitat baixa</MenuItem>
                    <MenuItem onClick={() => handleComplexitat('mitjana')}>Complexitat mitjana</MenuItem>
                    <MenuItem onClick={() => handleComplexitat('alta')}>Complexitat alta</MenuItem>
                    <Divider />
                    <MenuItem onClick={() => { setAnchorEl(null); setOpenGenerateModal(true); }}>
                      Personalitzada
                    </MenuItem>
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
                        <IconButton
                          onClick={() => setShowPassword((p) => !p)}
                          sx={{ borderRadius: 2, border: '1px solid', borderColor: 'divider', color: 'text.secondary' }}
                        >
                          {showPassword ? <VisibilityOffIcon fontSize="small" /> : <VisibilityIcon fontSize="small" />}
                        </IconButton>
                      </InputAdornment>
                    ),
                  }}
                />
              </Stack>
              <GeneratePasswordModal
                open={openGenerateModal}
                onClose={() => setOpenGenerateModal(false)}
                onConfirm={(password) => setContrasenya(password)}
              />

              <Stack spacing={0.5}>
                <Typography sx={{ fontSize: '0.8rem', fontWeight: 600, color: 'text.secondary' }}>
                  Carpeta
                </Typography>
                <FormControl fullWidth>
                  <Select
                    value={carpetaSeleccionada}
                    onChange={(e) => setCarpetaSeleccionada(e.target.value)}
                    displayEmpty
                  >
                    <MenuItem value={SENSE_CARPETA_VALUE}>Sense carpeta</MenuItem>
                    {carpetes.map((c) => (
                      <MenuItem key={c.uuid} value={c.uuid}>{c.nom}</MenuItem>
                    ))}
                    <MenuItem value={NOVA_CARPETA_VALUE}>+ Nova carpeta</MenuItem>
                  </Select>
                </FormControl>
              </Stack>

              {carpetaSeleccionada === NOVA_CARPETA_VALUE && (
                <Stack spacing={0.5}>
                  <Typography sx={{ fontSize: '0.8rem', fontWeight: 600, color: 'text.secondary' }}>
                    Nom de la nova carpeta *
                  </Typography>
                  <TextField
                    fullWidth
                    value={novaCarpetaNom}
                    onChange={(e) => setNovaCarpetaNom(e.target.value)}
                    error={!!novaCarpetaError}
                    helperText={novaCarpetaError}
                    placeholder="Ex: Xarxes socials"
                  />
                </Stack>
              )}

              <Divider />

              <Stack direction="row" sx={{ gap: 1, justifyContent: 'flex-end' }}>
                <Button
                  onClick={() => navigate(-1)}
                  variant="outlined"
                  sx={{ textTransform: 'none', fontWeight: 600 }}
                >
                  Cancel·lar
                </Button>
                <Button
                  onClick={handleSave}
                  variant="contained"
                  disabled={loading}
                  sx={{ textTransform: 'none', fontWeight: 600 }}
                >
                  {loading ? 'Guardant...' : 'Guardar'}
                </Button>
              </Stack>
            </Paper>
          </Box>
        </Stack>
      </Stack>
    </AppTheme>
  );
}