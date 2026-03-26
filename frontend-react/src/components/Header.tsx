import { Stack, Typography, Avatar, IconButton, Button } from '@mui/material';
import LogoutOutlinedIcon from '@mui/icons-material/LogoutOutlined';
import ArrowBackOutlinedIcon from '@mui/icons-material/ArrowBackOutlined';
import { useNavigate } from 'react-router';
import type { ReactNode } from 'react';

type HeaderProps = {
  title: string;
  icon: ReactNode;
  userInitial?: string;
  showBackButton?: boolean;
  onBack?: () => void;
};

export default function Header({
  title,
  icon,
  userInitial,
  showBackButton = false,
  onBack,
}: HeaderProps) {
  const navigate = useNavigate();

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
        <Avatar
          sx={{
            bgcolor: 'grey.500',
            width: 36,
            height: 36,
            fontSize: 15,
            fontWeight: 700,
          }}
        >
          {userInitial}
        </Avatar>

        <IconButton
          onClick={() => navigate('/')}
          size="small"
          sx={{
            border: 'none',
            bgcolor: 'transparent',
            '&:hover': { bgcolor: 'action.hover' },
          }}
        >
          <LogoutOutlinedIcon sx={{ fontSize: 22, color: 'text.secondary' }} />
        </IconButton>

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