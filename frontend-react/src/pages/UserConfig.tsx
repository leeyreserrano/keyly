import { useState, useRef } from 'react';
import {
  Stack,
  Typography,
  Paper,
  Box,
  CircularProgress,
  CssBaseline,
  Divider,
  IconButton,
  Chip,
  Tooltip,
} from '@mui/material';
import EditOutlinedIcon from '@mui/icons-material/EditOutlined';
import Sidebar from '../components/Sidebar';
import AppTheme from '../theme/AppTheme';
import Header from '../components/Header';
import { useAuth } from '../context/AuthContext';
import UserAvatar from '../components/UserAvatar';
import { usuarisApi } from '../api/usuarisapi';
import toast from 'react-hot-toast';

const ROL_LABEL: Record<string, { label: string; color: 'error' | 'warning' | 'default' }> = {
  ADMIN:  { label: 'Administrador', color: 'error' },
  CAP:    { label: 'Cap',           color: 'warning' },
  USUARI: { label: 'Usuari',        color: 'default' },
};

export default function UserConfig() {
  const { usuari, login, token } = useAuth();
  const fileInputRef = useRef<HTMLInputElement>(null);
  const [uploadingImage, setUploadingImage] = useState(false);

  const rol = usuari?.rolIntern ? ROL_LABEL[usuari.rolIntern] : null;

  const handleImageChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file || !usuari || !token) return;

    const reader = new FileReader();
    reader.onload = async () => {
      const base64 = reader.result as string;
      setUploadingImage(true);
      try {
        const updated = await usuarisApi.uploadImage(base64);
        if (updated) {
          login(updated, token, !!localStorage.getItem('jwtToken'));
          toast.success('Imatge actualitzada correctament');
        }
      } catch {
        toast.error('Error actualitzant la imatge');
      } finally {
        setUploadingImage(false);
      }
    };
    reader.readAsDataURL(file);
  };

  return (
    <AppTheme>
      <CssBaseline enableColorScheme />

      <Stack direction="row" sx={{ minHeight: '100vh', width: '100%' }}>
        <Sidebar />

        <Stack sx={{ flex: 1, bgcolor: 'background.default', overflow: 'auto' }}>
          <Header
            title="Perfil"
            icon={<UserAvatar />}
            showBackButton={false}
          />

          <Box sx={{ px: 4, py: 3 }}>
            {!usuari ? (
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
                  gap: 2,
                  maxWidth: 500,
                }}
              >
                <Stack direction="row" sx={{ alignItems: 'center', gap: 2 }}>
                  <Box sx={{ position: 'relative' }}>
                    <UserAvatar size={64} />

                    <Tooltip title="Canviar imatge">
                      <IconButton
                        onClick={() => fileInputRef.current?.click()}
                        disabled={uploadingImage}
                        sx={{
                          position: 'absolute',
                          bottom: -4,
                          right: -4,
                          width: 24,
                          height: 24,
                          bgcolor: 'background.paper',
                          border: '1px solid',
                          borderColor: 'divider',
                          '&:hover': { bgcolor: 'action.hover' },
                        }}
                      >
                        {uploadingImage
                          ? <CircularProgress size={12} />
                          : <EditOutlinedIcon sx={{ fontSize: 13 }} />
                        }
                      </IconButton>
                    </Tooltip>

                    <input
                      ref={fileInputRef}
                      type="file"
                      accept="image/*"
                      hidden
                      onChange={handleImageChange}
                    />
                  </Box>

                  <Stack>
                    <Typography variant="h5" sx={{ fontWeight: 700 }}>
                      {usuari.nom}
                    </Typography>
                    {rol && (
                      <Chip
                        label={rol.label}
                        color={rol.color}
                        size="small"
                        sx={{ mt: 0.5, width: 'fit-content', fontWeight: 600 }}
                      />
                    )}
                  </Stack>
                </Stack>

                <Divider />

                <Stack spacing={1.5}>
                  <Stack spacing={0.25}>
                    <Typography sx={{ fontSize: '0.75rem', fontWeight: 600, color: 'text.secondary' }}>
                      Nom
                    </Typography>
                    <Typography sx={{ fontWeight: 500 }}>
                      {usuari.nom}
                    </Typography>
                  </Stack>

                  <Stack spacing={0.25}>
                    <Typography sx={{ fontSize: '0.75rem', fontWeight: 600, color: 'text.secondary' }}>
                      Email
                    </Typography>
                    <Typography sx={{ fontWeight: 500 }}>
                      {usuari.correu}
                    </Typography>
                  </Stack>
                </Stack>
              </Paper>
            )}
          </Box>
        </Stack>
      </Stack>
    </AppTheme>
  );
}