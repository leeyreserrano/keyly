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
import { useAuth } from '../context/AuthContext';
import { getUserImage } from '../api/userimageapi';

export default function UserConfig() {
  const { usuari } = useAuth();
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(false);
  }, []);

  const imageUrl = getUserImage(usuari?.imatge);
  const initial = usuari?.nom?.charAt(0).toUpperCase() ?? 'U';

  return (
    <AppTheme>
      <CssBaseline enableColorScheme />

      <Stack direction="row" sx={{ minHeight: '100vh', width: '100%' }}>
        <Sidebar />

        <Stack sx={{ flex: 1, bgcolor: 'background.default', overflow: 'auto', minWidth: 0 }}>

          <Header
            title="Perfil"
            icon={
              <Avatar
                src={imageUrl}
                sx={{
                  bgcolor: 'primary.main',
                  width: 36,
                  height: 36,
                  fontWeight: 700,
                  cursor: 'default',
                }}
              >
                {!imageUrl && initial}
              </Avatar>
            }
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
                  border: '1px solid',
                  borderColor: 'divider',
                  display: 'flex',
                  flexDirection: 'column',
                  gap: 2
                }}
              >

                <Stack direction="row" sx={{ alignItems: 'center', gap: 1 }}>
                  <Avatar
                    src={imageUrl}
                    sx={{
                      bgcolor: 'primary.main',
                      width: 36,
                      height: 36,
                      fontWeight: 700,
                      cursor: 'default',
                    }}
                  >
                    {!imageUrl && initial}
                  </Avatar>

                  <Typography variant="h5" sx={{ fontWeight: 700 }}>
                    {usuari?.nom || 'Usuari'}
                  </Typography>
                </Stack>

                <Divider />

                <Typography>
                  <strong>Nom:</strong> {usuari?.nom}
                </Typography>

                <Typography>
                  <strong>Email:</strong> {usuari?.correu}
                </Typography>

                {usuari?.uuid && (
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