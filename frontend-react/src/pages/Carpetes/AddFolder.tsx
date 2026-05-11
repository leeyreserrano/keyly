import { useState } from 'react';
import { useNavigate } from 'react-router';
import { useTranslation } from 'react-i18next';
import {
  Stack,
  Typography,
  Paper,
  Button,
  TextField,
  Box,
  Divider,
} from '@mui/material';
import FolderOutlinedIcon from '@mui/icons-material/FolderOutlined';
import Header from '../../components/Header';
import { carpetasApi } from '../../api/carpetasapi';
import toast from 'react-hot-toast';

export default function AddCarpeta() {
  const navigate = useNavigate();
  const { t } = useTranslation('folder');

  const [nom, setNom] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | undefined>();

  const validate = (): boolean => {
    if (!nom.trim()) {
      setError(t('name_required'));
      return false;
    }
    setError(undefined);
    return true;
  };

  const handleSave = async () => {
    if (!validate()) return;

    setLoading(true);
    try {
      await carpetasApi.addCarpeta({ nom });

      toast.success(t('created_success'));
      navigate(-1);
    } catch (err: unknown) {
      const message =
        err instanceof Error
          ? err.message
          : t('error_create');

      toast.error(message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Stack sx={{ height: '100%', overflow: 'hidden' }}>
      <Header
        title={t('add_title')}
        icon={<FolderOutlinedIcon sx={{ fontSize: 30, color: 'text.primary' }} />}
        showBackButton
      />

      <Stack sx={{ flex: 1, overflow: 'auto', minWidth: 0 }}>
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
              {t('new')}
            </Typography>

            <Divider />

            <Stack spacing={0.5}>
              <Typography sx={{ fontSize: '0.8rem', fontWeight: 600, color: 'text.secondary' }}>
                {t('name')} *
              </Typography>

              <TextField
                fullWidth
                value={nom}
                onChange={(e) => setNom(e.target.value)}
                error={!!error}
                helperText={error}
                placeholder={t('placeholder')}
              />
            </Stack>

            <Divider />

            <Stack direction="row" sx={{ gap: 1, justifyContent: 'flex-end' }}>
              <Button
                onClick={() => navigate(-1)}
                variant="outlined"
                sx={{ textTransform: 'none', fontWeight: 600 }}
              >
                {t('cancel')}
              </Button>

              <Button
                onClick={handleSave}
                variant="contained"
                disabled={loading}
                sx={{ textTransform: 'none', fontWeight: 600 }}
              >
                {loading ? t('saving') : t('save')}
              </Button>
            </Stack>
          </Paper>
        </Box>
      </Stack>
    </Stack>
  );
}