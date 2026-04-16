import { Stack, IconButton } from '@mui/material';
import StarIcon from '@mui/icons-material/Star';
import StarBorderIcon from '@mui/icons-material/StarBorder';
import EditOutlinedIcon from '@mui/icons-material/EditOutlined';
import DeleteOutlinedIcon from '@mui/icons-material/DeleteOutlined';
import FolderOutlinedIcon from '@mui/icons-material/FolderOutlined';
import ShareOutlinedIcon from '@mui/icons-material/ShareOutlined';
import type { MouseEventHandler } from 'react';

type ActionButtonsProps = {
  isFavorit?: boolean;
  onToggleFavorit?: MouseEventHandler<HTMLButtonElement>;
  onEdit?: MouseEventHandler<HTMLButtonElement>;
  onDelete?: MouseEventHandler<HTMLButtonElement>;
  onShare?: MouseEventHandler<HTMLButtonElement>;
  size?: 'small' | 'medium' | 'card';
  highlightDelete?: boolean;
  showFolderIcon?: boolean;
  folderIconSize?: number;
  gap?: number;
};

export default function ActionButtons({
  isFavorit = false,
  onToggleFavorit,
  onEdit,
  onDelete,
  onShare,
  size = 'small',
  showFolderIcon = false,
  folderIconSize = 16,
  gap = 0.5,
}: ActionButtonsProps) {
  const dimensions = {
    small: 28,
    medium: 36,
    card: 40,
  };

  const iconFont: Record<'small' | 'medium' | 'card', 'inherit' | 'small' | 'medium' | 'large'> = {
    small: 'small',
    medium: 'medium',
    card: 'medium',
  };

  const btnSx = {
    width: dimensions[size],
    height: dimensions[size],
    border: '1px solid',
    borderColor: 'divider',
    p: 0,
  };

  return (
    <Stack direction="row" sx={{ gap, alignItems: 'center' }}>
      {showFolderIcon && (
        <FolderOutlinedIcon sx={{ fontSize: folderIconSize, color: 'text.primary' }} />
      )}

      {onToggleFavorit && (
        <IconButton
          size={size === 'card' ? 'medium' : size}
          onClick={onToggleFavorit}
          sx={{
            ...btnSx,
            bgcolor: isFavorit ? 'yellow.100' : 'background.paper',
            color: isFavorit ? 'gold' : 'text.primary',
            '&:hover': { bgcolor: isFavorit ? 'yellow.200' : 'action.hover' },
          }}
        >
          {isFavorit ? <StarIcon fontSize={iconFont[size]} /> : <StarBorderIcon fontSize={iconFont[size]} />}
        </IconButton>
      )}

      {onEdit && (
        <IconButton
          size={size === 'card' ? 'medium' : size}
          onClick={onEdit}
          sx={{ ...btnSx, bgcolor: 'transparent' }}
        >
          <EditOutlinedIcon fontSize={iconFont[size]} />
        </IconButton>
      )}

      {onShare && (
        <IconButton
          size={size === 'card' ? 'medium' : size}
          onClick={onShare}
          sx={{ ...btnSx, bgcolor: 'transparent', color: 'primary.main' }}
        >
          <ShareOutlinedIcon fontSize={iconFont[size]} />
        </IconButton>
      )}

      {onDelete && (
        <IconButton
          size={size === 'card' ? 'medium' : size}
          onClick={onDelete}
          sx={{
            ...btnSx,
            bgcolor: 'error.main',
            color: 'white',
            '&:hover': { bgcolor: 'error.dark' },
          }}
        >
          <DeleteOutlinedIcon fontSize={iconFont[size]} />
        </IconButton>
      )}
    </Stack>
  );
}