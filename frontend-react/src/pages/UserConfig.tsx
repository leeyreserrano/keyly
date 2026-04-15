import {
  Stack,
  Typography,
  Paper,
  Box,
  CircularProgress,
  CssBaseline,
  Divider
} from '@mui/material';

import Sidebar from '../components/Sidebar';
import AppTheme from '../theme/AppTheme';
import Header from '../components/Header';
import { useAuth } from '../context/AuthContext';
import UserAvatar from '../components/UserAvatar';

export default function UserConfig() {
  const { usuari } = useAuth();

  const loading = false;
  const initial = usuari?.nom?.charAt(0).toUpperCase() ?? 'U';

  return (
    <AppTheme>
      <CssBaseline enableColorScheme />

      <Stack direction="row" sx={{ minHeight: '100vh', width: '100%' }}>
        <Sidebar />

        <Stack sx={{ flex: 1, bgcolor: 'background.default', overflow: 'auto' }}>

          {/* HEADER */}
          <Header
            title="Perfil"
            icon={<UserAvatar />}
            showBackButton={false}
          />

          <Box sx={{ px: 4, py: 3 }}>
            {loading ? (
              <Stack sx={{ alignItems: 'center', mt: 10 }}>
                <CircularProgress />
              </Stack>
            ) : !usuari ? (
              <Typography color="error" sx={{ mt: 4 }}>
                No s'ha pogut obtenir l'usuari.
              </Typography>
            ) : (
              <Paper
                variant="outlined"
                sx={{
                  p: 3,
                  borderRadius: '12px',
                  borderColor: 'divider',
                  display: 'flex',
                  flexDirection: 'column',
                  gap: 2
                }}
              >

                {/* USER INFO HEADER */}
                <Stack direction="row" sx={{ alignItems: 'center', gap: 1 }}>
                  <UserAvatar />

                  <Typography variant="h5" sx={{ fontWeight: 700 }}>
                    {usuari.nom}
                  </Typography>
                </Stack>

                <Divider />

                <Typography>
                  <strong>Nom:</strong> {usuari.nom}
                </Typography>

                <Typography>
                  <strong>Email:</strong> {usuari.correu}
                </Typography>

                {usuari.uuid && (
                  <Typography>
                    <strong>ID:</strong> {usuari.uuid}
                  </Typography>
                )}
              </Paper>
            )}
          </Box>
        </Stack>
      </Stack>
    </AppTheme>
  );
}