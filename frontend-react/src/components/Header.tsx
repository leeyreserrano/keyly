import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router';
import { Stack, Typography, IconButton, Button, Tooltip } from '@mui/material';
import LogoutOutlinedIcon from '@mui/icons-material/LogoutOutlined';
import ArrowBackOutlinedIcon from '@mui/icons-material/ArrowBackOutlined';
import ShareOutlinedIcon from '@mui/icons-material/ShareOutlined';
import EditOutlinedIcon from '@mui/icons-material/EditOutlined';
import DeleteOutlinedIcon from '@mui/icons-material/DeleteOutlined';
import StarIcon from '@mui/icons-material/Star';
import StarBorderIcon from '@mui/icons-material/StarBorder';
import type { ReactNode } from 'react';
import { useAuth } from '../context/AuthContext';
import UserAvatar from '../components/UserAvatar';
import HelpIcon from '@mui/icons-material/Help';

type HeaderProps = {
  title: string;
  icon: ReactNode;
  showBackButton?: boolean;
  onBack?: () => void;
  onShare?: () => void;
  onEdit?: () => void;
  onDelete?: () => void;
  isFavorit?: boolean;
  onToggleFavorit?: () => void;
};

export default function Header({
  title,
  icon,
  showBackButton = false,
  onBack,
  onShare,
  onEdit,
  onDelete,
  isFavorit = false,
  onToggleFavorit,
}: HeaderProps) {
  const navigate = useNavigate();
  const { t } = useTranslation('sidebar');
  const { usuari, logout } = useAuth();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  const tooltipText = usuari ? `${usuari.nom} · ${usuari.rolIntern}` : '';

  const btnSx = {
    border: '1px solid',
    borderColor: 'divider',
  };

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
        position: 'sticky',
        top: 0,
        zIndex: 10,
        bgcolor: 'background.default',
        flexShrink: 0,
      }}
    >
      <Stack direction="row" sx={{ gap: 1.5, alignItems: 'center' }}>
        {icon}
        <Typography variant="h3" sx={{ fontWeight: 800 }}>
          {title}
        </Typography>
      </Stack>

      <Stack direction="row" sx={{ gap: 1, alignItems: 'center' }}>
        <Tooltip title={tooltipText} arrow>
          <div onClick={() => navigate('/settings')} style={{ cursor: 'pointer' }}>
            <UserAvatar />
          </div>
        </Tooltip>
        <Tooltip title={t('Help')} arrow>
          <IconButton
            onClick={() => window.open('https://10.147.17.250:8081/docs/', '_blank')}
          >
            <HelpIcon />
          </IconButton>
        </Tooltip>

        <Tooltip title={t('logout')} arrow>
          <IconButton onClick={handleLogout}>
            <LogoutOutlinedIcon />
          </IconButton>
        </Tooltip>

        {onToggleFavorit && (
          <Tooltip title={isFavorit ? t('unfavorite') : t('favorite')} arrow>
            <IconButton
              onClick={onToggleFavorit}
              sx={{
                ...btnSx,
                bgcolor: isFavorit ? 'yellow.100' : 'transparent',
                color: isFavorit ? 'gold' : 'text.primary',
                '&:hover': { bgcolor: isFavorit ? 'yellow.200' : 'action.hover' },
              }}
            >
              {isFavorit ? <StarIcon /> : <StarBorderIcon />}
            </IconButton>
          </Tooltip>
        )}

        {onEdit && (
          <Tooltip title={t('edit')} arrow>
            <IconButton onClick={onEdit} sx={{ ...btnSx }}>
              <EditOutlinedIcon />
            </IconButton>
          </Tooltip>
        )}

        {onShare && (
          <Tooltip title={t('share')} arrow>
            <IconButton
              onClick={onShare}
              sx={{ ...btnSx, color: 'primary.main' }}
            >
              <ShareOutlinedIcon />
            </IconButton>
          </Tooltip>
        )}

        {onDelete && (
          <Tooltip title={t('delete')} arrow>
            <IconButton
              onClick={onDelete}
              sx={{ ...btnSx, bgcolor: 'error.main', color: 'white', '&:hover': { bgcolor: 'error.dark' } }}
            >
              <DeleteOutlinedIcon />
            </IconButton>
          </Tooltip>
        )}

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
            {t('back')}
          </Button>
        )}
      </Stack>
    </Stack>
  );
}