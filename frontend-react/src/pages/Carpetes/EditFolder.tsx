import { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router';
import { useTranslation } from 'react-i18next';
import {
  Stack,
  Typography,
  Paper,
  Button,
  TextField,
  Box,
  Divider,
  CircularProgress,
  Alert,
} from '@mui/material';
import FolderOutlinedIcon from '@mui/icons-material/FolderOutlined';
import Header from '../../components/Header';
import { carpetasApi, type Carpeta } from '../../api/carpetasapi';
import toast from 'react-hot-toast';

export default function EditCarpeta() {
  const navigate = useNavigate();
  const location = useLocation();
  const { t } = useTranslation('folder');

  const { uuid } = location.state || {};

  const [carpeta, setCarpeta] = useState<Carpeta | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [nom, setNom] = useState('');
  const [error, setError] = useState<string | undefined>();

  useEffect(() => {
    if (!uuid) {
      setLoadError(t('edit.not_specified'));
      setLoading(false);
      return;
    }

    const loadData = async () => {
      try {
        const allCarpetas = await carpetasApi.fetchItems();
        const found = allCarpetas.find((c) => c.uuid === uuid);

        if (found) {
          setCarpeta(found);
          setNom(found.nom);
        } else {
          setLoadError(t('edit.not_found'));
        }
      } catch (err: unknown) {
        const message =
          err instanceof Error
            ? err.message
            : t('edit.error_load');

        setLoadError(message);
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, [uuid, t]);

  const validate = (): boolean => {
    if (!nom.trim()) {
      setError(t('edit.name_required'));
      return false;
    }
    setError(undefined);
    return true;
  };

  const handleSave = async () => {
    if (!carpeta || !validate()) return;

    setSaving(true);

    try {
      await carpetasApi.updateCarpeta(carpeta.uuid, { nom });

      toast.success(t('edit.success'));
      navigate(-1);
    } catch (err: unknown) {
      const message =
        err instanceof Error
          ? err.message
          : t('edit.error_save');

      toast.error(message);
    } finally {
      setSaving(false);
    }
  };

  return (
    <Stack sx={{ height: '100%', overflow: 'hidden' }}>
      <Header
        title={t('edit.title')}
        icon={<FolderOutlinedIcon sx={{ fontSize: 30, color: 'text.primary' }} />}
        showBackButton
      />

      <Stack sx={{ flex: 1, overflow: 'auto', minWidth: 0 }}>
        <Box sx={{ px: 4, py: 3, display: 'flex', justifyContent: 'center' }}>
          {loading ? (
            <Stack sx={{ alignItems: 'center', mt: 10 }}>
              <CircularProgress />
            </Stack>
          ) : loadError ? (
            <Alert severity="error" sx={{ mt: 2 }}>
              {loadError}
            </Alert>
          ) : (
            <Paper
              variant="outlined"
              sx={{
                p: 3,
                borderRadius: '12px',
                border: '1px solid',
                borderColor: 'divider',
                display: 'flex',
                flexDirection: 'column',
                gap: 2,
                maxWidth: 500,
                width: '100%',
              }}
            >
              <Typography variant="h5" sx={{ fontWeight: 700 }}>
                {carpeta?.nom}
              </Typography>

              <Divider />

              <Stack spacing={0.5}>
                <Typography sx={{ fontSize: '0.8rem', fontWeight: 600, color: 'text.secondary' }}>
                  {t('edit.name')} *
                </Typography>

                <TextField
                  fullWidth
                  value={nom}
                  onChange={(e) => setNom(e.target.value)}
                  error={!!error}
                  helperText={error}
                />
              </Stack>

              <Divider />

              <Stack direction="row" sx={{ gap: 1, justifyContent: 'flex-end' }}>
                <Button
                  onClick={() => navigate(-1)}
                  variant="outlined"
                  sx={{ textTransform: 'none', fontWeight: 600 }}
                >
                  {t('edit.cancel')}
                </Button>

                <Button
                  onClick={handleSave}
                  variant="contained"
                  disabled={saving}
                  sx={{ textTransform: 'none', fontWeight: 600 }}
                >
                  {saving ? t('edit.saving') : t('edit.save')}
                </Button>
              </Stack>
            </Paper>
          )}
        </Box>
      </Stack>
    </Stack>
  );
}