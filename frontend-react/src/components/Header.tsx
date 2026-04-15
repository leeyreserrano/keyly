import { Stack, Typography, IconButton, Button, Tooltip } from '@mui/material';
import LogoutOutlinedIcon from '@mui/icons-material/LogoutOutlined';
import ArrowBackOutlinedIcon from '@mui/icons-material/ArrowBackOutlined';
import { useNavigate } from 'react-router';
import type { ReactNode } from 'react';
import { useAuth } from '../context/AuthContext';
import UserAvatar from '../components/UserAvatar';

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

  const tooltipText = usuari ? `${usuari.nom} · ${usuari.rolIntern}` : '';

  return (
    <Stack direction="row" sx={{ px: 4, py: 2.5, justifyContent: 'space-between', alignItems: 'center' }}>

      <Stack direction="row" sx={{ gap: 1.5, alignItems: 'center' }}>
        {icon}
        <Typography variant="h3" sx={{ fontWeight: 800 }}>
          {title}
        </Typography>
      </Stack>

      <Stack direction="row" sx={{ gap: 1, alignItems: 'center' }}>
        <Tooltip title={tooltipText} arrow>
          <div>
            <UserAvatar />
          </div>
        </Tooltip>

        <Tooltip title="Tancar sessió" arrow>
          <IconButton onClick={handleLogout}>
            <LogoutOutlinedIcon />
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