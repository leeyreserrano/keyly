import { Stack, Typography, Avatar, IconButton, Button, Tooltip } from '@mui/material';
import LogoutOutlinedIcon from '@mui/icons-material/LogoutOutlined';
import ArrowBackOutlinedIcon from '@mui/icons-material/ArrowBackOutlined';
import { useNavigate } from 'react-router';
import type { ReactNode } from 'react';
import { useAuth } from '../context/AuthContext';
import { getUserImage } from '../api/userimageapi';

type HeaderProps = {
  title: string;
  icon: ReactNode;
  showBackButton?: boolean;
  onBack?: () => void;
};

export default function Header({
  title,
  icon,
  showBackButton = false,
  onBack,
}: HeaderProps) {
  const navigate = useNavigate();
  const { usuari, logout } = useAuth();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  const initial = usuari?.nom?.charAt(0).toUpperCase() ?? '?';
  const tooltipText = usuari ? `${usuari.nom} · ${usuari.rolIntern}` : '';

  const imageUrl = getUserImage(usuari?.imatge);

  return (
    <Stack
      direction="row"
      sx={{
        px: 4,
        py: 2.5,
        justifyContent: 'space-between',
        alignItems: 'center',
        borderBottom: '1px solid',
        borderColor: 'divider',
      }}
    >
      {/* Izquierda */}
      <Stack direction="row" sx={{ gap: 1.5, alignItems: 'center' }}>
        {icon}
        <Typography variant="h3" sx={{ fontWeight: 800 }}>
          {title}
        </Typography>
      </Stack>

      {/* Derecha */}
      <Stack direction="row" sx={{ gap: 1, alignItems: 'center' }}>
        <Tooltip title={tooltipText} arrow>
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
        </Tooltip>

        <Tooltip title="Tancar sessió" arrow>
          <IconButton
            onClick={handleLogout}
            size="small"
            sx={{
              bgcolor: 'transparent',
              '&:hover': { bgcolor: 'action.hover' },
            }}
          >
            <LogoutOutlinedIcon sx={{ fontSize: 22, color: 'text.secondary' }} />
          </IconButton>
        </Tooltip>

        {showBackButton && (
          <Button
            startIcon={<ArrowBackOutlinedIcon />}
            onClick={onBack || (() => navigate(-1))}
            sx={{
              textTransform: 'none',
              fontWeight: 600,
              bgcolor: 'primary.main',
              color: 'white',
              '&:hover': { bgcolor: 'primary.dark' },
            }}
          >
            Tornar
          </Button>
        )}
      </Stack>
    </Stack>
  );
}