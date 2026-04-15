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
import Header from '../components/Header';

export default function ChooseType() {
  const navigate = useNavigate();

  return (
    <AppTheme>
      <CssBaseline enableColorScheme />
      <Stack direction="row" sx={{ minHeight: '100vh', width: '100%' }}>
        <Sidebar />

        <Stack sx={{ flex: 1, bgcolor: 'background.default', overflow: 'auto', minWidth: 0 }}>
          {/* HEADER */}
          <Header
            title="Crear nuevo elemento" 
            icon={undefined}
            showBackButton={true}          
            />

          {/* CONTINGUT PRINCIPAL */}
          <Stack sx={{ px: 4, py: 6, alignItems: 'center' }}>
          {/* CARDS */}
          <Stack
            direction={{ xs: 'column', sm: 'row' }}
            spacing={{ xs: 4, sm: 8 }}
            sx={{ justifyContent: 'center', alignItems: 'center', width: '100%' }}
          >
            {/* Card Item */}
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
                gap: 2,
                minWidth: 280,
                cursor: 'pointer',
                position: 'relative',
                '&:hover': {
                  boxShadow: 8,
                  transform: 'translateY(-5px)',
                  transition: 'all 0.3s ease',
                },
              }}
              onClick={() => navigate('/AddItem')}
            >
              <KeyRoundedIcon
                sx={{
                  fontSize: 60,
                  color: 'primary.main',
                  filter: 'drop-shadow(2px 4px 6px rgba(0,0,0,0.2))',
                }}
              />
              <Typography variant="h5" sx={{ fontWeight: 700 }}>
                Nuevo Item
              </Typography>
              <Typography
                variant="body2"
                sx={{ textAlign: 'center', color: 'text.secondary', maxWidth: 220 }}
              >
                Guarda tus credenciales, contraseñas o información importante de forma segura.
              </Typography>
              <Button
                variant="contained"
                sx={{ mt: 2, textTransform: 'none', fontWeight: 600 }}
                onClick={() => navigate('/AddItem')}
              >
                Crear
              </Button>
            </Paper>

            {/* Card Carpeta */}
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
                gap: 2,
                minWidth: 280,
                cursor: 'pointer',
                position: 'relative',
                '&:hover': {
                  boxShadow: 8,
                  transform: 'translateY(-5px)',
                  transition: 'all 0.3s ease',
                },
              }}
              onClick={() => navigate('/AddCarpeta')}
            >
              <FolderOutlinedIcon
                sx={{
                  fontSize: 60,
                  color: 'primary.main',
                  filter: 'drop-shadow(2px 4px 6px rgba(0,0,0,0.2))',
                }}
              />
              <Typography variant="h5" sx={{ fontWeight: 700 }}>
                Nueva Carpeta
              </Typography>
              <Typography
                variant="body2"
                sx={{ textAlign: 'center', color: 'text.secondary', maxWidth: 220 }}
              >
                Organiza tus items en carpetas y mantén todo bien estructurado y accesible.
              </Typography>
              <Button
                variant="contained"
                sx={{ mt: 2, textTransform: 'none', fontWeight: 600 }}
                onClick={() => navigate('/AddCarpeta')}
              >
                Crear
              </Button>
            </Paper>
          </Stack>
        </Stack>
      </Stack>
    </Stack>
    </AppTheme>
  );
}