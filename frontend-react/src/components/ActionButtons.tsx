import { Stack, IconButton } from '@mui/material';
import StarIcon from '@mui/icons-material/Star';
import StarBorderIcon from '@mui/icons-material/StarBorder';
import EditOutlinedIcon from '@mui/icons-material/EditOutlined';
import DeleteOutlinedIcon from '@mui/icons-material/DeleteOutlined';
import FolderOutlinedIcon from '@mui/icons-material/FolderOutlined';
import type { MouseEventHandler } from 'react';

type ActionButtonsProps = {
    isFavorit: boolean;
    onToggleFavorit: MouseEventHandler<HTMLButtonElement>;
    onEdit: MouseEventHandler<HTMLButtonElement>;
    onDelete?: MouseEventHandler<HTMLButtonElement>;
    size?: 'small' | 'medium' | 'card'; // añadimos tamaño tipo card
    highlightDelete?: boolean;
    showFolderIcon?: boolean;
    folderIconSize?: number;
    gap?: number;
};

export default function ActionButtons({
    isFavorit,
    onToggleFavorit,
    onEdit,
    onDelete,
    size = 'small',
    showFolderIcon = false,
    folderIconSize = 16,
    gap = 0.5,
}: ActionButtonsProps) {
    // Definimos tamaños según size
    const dimensions = {
        small: 28,
        medium: 36,
        card: 40, // tamaño tipo card
    };
    const iconFont: Record<'small' | 'medium' | 'card', 'inherit' | 'small' | 'medium' | 'large'> = {
        small: 'small',
        medium: 'medium',
        card: 'medium',
    };

    return (
        <Stack direction="row" sx={{ gap, alignItems: 'center' }}>
            {showFolderIcon && (
                <FolderOutlinedIcon sx={{ fontSize: folderIconSize, color: 'text.primary' }} />
            )}

            {/* Favorito */}
            <IconButton
                size={size === 'card' ? 'medium' : size}
                onClick={onToggleFavorit}
                sx={{
                    width: dimensions[size],
                    height: dimensions[size],
                    border: '1px solid',
                    borderColor: 'divider',
                    bgcolor: isFavorit ? 'yellow.100' : 'background.paper',
                    color: isFavorit ? 'gold' : 'text.primary',
                    '&:hover': { bgcolor: isFavorit ? 'yellow.200' : 'action.hover' },
                    p: 0,
                }}
            >
                {isFavorit ? <StarIcon fontSize={iconFont[size]} /> : <StarBorderIcon fontSize={iconFont[size]} />}
            </IconButton>

            {/* Edit */}
            <IconButton
                size={size === 'card' ? 'medium' : size}
                sx={{
                    width: dimensions[size],
                    height: dimensions[size],
                    border: '1px solid',
                    borderColor: 'divider',
                    bgcolor: 'transparent',
                    p: 0,
                }}
                onClick={onEdit}
            >
                <EditOutlinedIcon fontSize={iconFont[size]} />
            </IconButton>

            {/* Delete */}
            {onDelete && (
                <IconButton
                    size={size === 'card' ? 'medium' : size}
                    onClick={onDelete}
                    sx={{
                        width: dimensions[size],
                        height: dimensions[size],
                        border: '1px solid',
                        borderColor: 'divider',
                        bgcolor: 'error.main',
                        color: 'white',
                        '&:hover': { bgcolor: 'error.dark' },
                        p: 0,
                    }}
                >
                    <DeleteOutlinedIcon fontSize={iconFont[size]} />
                </IconButton>
            )}
        </Stack>
    );
}