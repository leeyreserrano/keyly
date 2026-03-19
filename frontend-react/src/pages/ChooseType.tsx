import { useNavigate } from 'react-router';
import {
  Stack,
  Typography,
  Paper,
  Button,
  CssBaseline,
} from '@mui/material';
import FolderOutlinedIcon from '@mui/icons-material/FolderOutlined';
import KeyRoundedIcon from '@mui/icons-material/VpnKeyRounded';
import AppTheme from '../theme/AppTheme';
import Sidebar from '../components/Sidebar';

export default function ChooseType() {
  const navigate = useNavigate();

  return (
    <AppTheme>
      <CssBaseline enableColorScheme />
      <Stack direction="row" sx={{ minHeight: '100vh', width: '100%' }}>
        <Sidebar />

        <Stack sx={{ flex: 1, bgcolor: 'background.default', overflow: 'auto', minWidth: 0, px: 4, py: 6, alignItems: 'center' }}>
          <Typography variant="h3" sx={{ fontWeight: 800, mb: 4 }}>
            ¿Qué quieres crear?
          </Typography>

          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={4}>
            {/* Card para Item */}
            <Paper
              variant="outlined"
              sx={{
                p: 6,
                borderRadius: 3,
                border: '1px solid',
                borderColor: 'divider',
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                gap: 3,
                minWidth: 200,
                cursor: 'pointer',
                '&:hover': { boxShadow: 4 },
              }}
              onClick={() => navigate('/AddItem')}
            >
              <KeyRoundedIcon sx={{ fontSize: 50, color: 'primary.main' }} />
              <Typography variant="h5" sx={{ fontWeight: 700 }}>
                Nuevo Item
              </Typography>
              <Button
                variant="contained"
                onClick={() => navigate('/AddItem')}
              >
                Crear
              </Button>
            </Paper>

            {/* Card para Carpeta */}
            <Paper
              variant="outlined"
              sx={{
                p: 6,
                borderRadius: 3,
                border: '1px solid',
                borderColor: 'divider',
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                gap: 3,
                minWidth: 200,
                cursor: 'pointer',
                '&:hover': { boxShadow: 4 },
              }}
              onClick={() => navigate('/AddFolder')}
            >
              <FolderOutlinedIcon sx={{ fontSize: 50, color: 'primary.main' }} />
              <Typography variant="h5" sx={{ fontWeight: 700 }}>
                Nueva Carpeta
              </Typography>
              <Button
                variant="contained"
                onClick={() => navigate('/AddFolder')}
              >
                Crear
              </Button>
            </Paper>
          </Stack>
        </Stack>
      </Stack>
    </AppTheme>
  );
}