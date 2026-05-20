import { useNavigate } from 'react-router';
import { useTranslation } from 'react-i18next';
import { Stack, Typography, Paper, Button } from '@mui/material';
import FolderOutlinedIcon from '@mui/icons-material/FolderOutlined';
import KeyRoundedIcon from '@mui/icons-material/VpnKeyRounded';
import Header from '../components/Header';

export default function ChooseType() {
  const navigate = useNavigate();
  const { t } = useTranslation('choose');

  return (
    <Stack sx={{ height: '100%', overflow: 'hidden' }}>
      <Header
        title={t('title')}
        icon={undefined}
        showBackButton={true}
      />

      <Stack sx={{ flex: 1, overflow: 'auto', minWidth: 0 }}>
        <Stack sx={{ px: 4, py: 6, alignItems: 'center' }}>
          <Stack
            direction={{ xs: 'column', sm: 'row' }}
            spacing={{ xs: 4, sm: 8 }}
            sx={{ justifyContent: 'center', alignItems: 'center', width: '100%' }}
          >
            <Paper
              variant="outlined"
              sx={{
                p: 6, borderRadius: 3, border: '1px solid', borderColor: 'divider',
                display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 2,
                minWidth: 280, cursor: 'pointer', position: 'relative',
                '&:hover': { boxShadow: 8, transform: 'translateY(-5px)', transition: 'all 0.3s ease' },
              }}
              onClick={() => navigate('/AddItem')}
            >
              <KeyRoundedIcon sx={{ fontSize: 60, color: 'primary.main', filter: 'drop-shadow(2px 4px 6px rgba(0,0,0,0.2))' }} />
              <Typography variant="h5" sx={{ fontWeight: 700 }}>
                {t('item.title')}
              </Typography>
              <Typography variant="body2" sx={{ textAlign: 'center', color: 'text.secondary', maxWidth: 220 }}>
                {t('item.description')}
              </Typography>
              <Button
                variant="contained"
                sx={{ mt: 2, textTransform: 'none', fontWeight: 600 }}
                onClick={(e) => { e.stopPropagation(); navigate('/AddItem'); }}
              >
                {t('create')}
              </Button>
            </Paper>

            <Paper
              variant="outlined"
              sx={{
                p: 6, borderRadius: 3, border: '1px solid', borderColor: 'divider',
                display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 2,
                minWidth: 280, cursor: 'pointer', position: 'relative',
                '&:hover': { boxShadow: 8, transform: 'translateY(-5px)', transition: 'all 0.3s ease' },
              }}
              onClick={() => navigate('/AddCarpeta')}
            >
              <FolderOutlinedIcon sx={{ fontSize: 60, color: 'primary.main', filter: 'drop-shadow(2px 4px 6px rgba(0,0,0,0.2))' }} />
              <Typography variant="h5" sx={{ fontWeight: 700 }}>
                {t('folder.title')}
              </Typography>
              <Typography variant="body2" sx={{ textAlign: 'center', color: 'text.secondary', maxWidth: 220 }}>
                {t('folder.description')}
              </Typography>
              <Button
                variant="contained"
                sx={{ mt: 2, textTransform: 'none', fontWeight: 600 }}
                onClick={(e) => { e.stopPropagation(); navigate('/AddCarpeta'); }}
              >
                {t('create')}
              </Button>
            </Paper>
          </Stack>
        </Stack>
      </Stack>
    </Stack>
  );
}