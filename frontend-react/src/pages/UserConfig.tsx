import { useEffect, useState } from 'react';
import {
  Stack,
  Typography,
  Paper,
  Box,
  CircularProgress,
  CssBaseline,
  Avatar,
  Divider
} from '@mui/material';

import Sidebar from '../components/Sidebar';
import AppTheme from '../theme/AppTheme';
import Header from '../components/Header';
import { getCurrentUser } from '../api/loginapi';

export default function UserConfig() {
  const [user, setUser] = useState<any>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    try {
      const current = getCurrentUser();
      setUser(current?.user || null);
    } catch (error) {
      console.error('Error obteniendo usuario', error);
    } finally {
      setLoading(false);
    }
  }, []);

  return (
    <AppTheme>
      <CssBaseline enableColorScheme />

      <Stack direction="row" sx={{ minHeight: '100vh', width: '100%' }}>
        {/* SIDEBAR */}
        <Sidebar />

        {/* CONTENIDO PRINCIPAL */}
        <Stack sx={{ flex: 1, bgcolor: 'background.default', overflow: 'auto', minWidth: 0 }}>

          {/* HEADER*/}
          <Header
            title="Perfil"
            icon={
              <Avatar sx={{ width: 32, height: 32 }}>
                {user?.name?.charAt(0) || 'U'}
              </Avatar>
            }
            userInitial={user?.name?.charAt(0) || 'U'}
            showBackButton={false}
          />

          {/* CONTENIDO */}
          <Box sx={{ px: 4, py: 3 }}>
            {loading ? (
              <Stack sx={{ alignItems: 'center', mt: 10 }}>
                <CircularProgress />
              </Stack>
            ) : !user ? (
              <Typography color="error" sx={{ mt: 4 }}>
                No s'ha pogut obtenir l'usuari.
              </Typography>
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
                  gap: 2
                }}
              >

                {/* TITULO */}
                <Stack direction="row" sx={{ alignItems: 'center', gap: 1 }}>
                  <Avatar sx={{ width: 40, height: 40 }}>
                    {user?.name?.charAt(0) || 'U'}
                  </Avatar>

                  <Typography variant="h5" sx={{ fontWeight: 700 }}>
                    {user?.name || 'Usuari'}
                  </Typography>
                </Stack>

                <Divider />

                {/* INFO */}
                <Typography>
                  <strong>Nom:</strong> {user?.name}
                </Typography>

                <Typography>
                  <strong>Email:</strong> {user?.email}
                </Typography>

                {user?.id && (
                  <Typography>
                    <strong>ID:</strong> {user.id}
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