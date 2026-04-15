import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import Paper from '@mui/material/Paper';
import FolderOutlinedIcon from '@mui/icons-material/FolderOutlined';
import { useState } from 'react';
import { useNavigate } from 'react-router';
import { itemsApi } from '../api/itemsapi';
import toast from 'react-hot-toast';
import { carpetasApi } from '../api/carpetasapi';
import ActionButtons from './ActionButtons';

interface CredentialCardProps {
  uuid: string;
  titol: string;
  nomUsuari: string;
  dataEditat: string;
  esCarpeta?: boolean;
  dinsCarpeta?: boolean;
  favorit?: boolean;
  onClick?: () => void;
  onEdit?: () => void;
  onDelete?: () => void;
}

export default function CredentialCard({
  uuid,
  titol,
  nomUsuari,
  dataEditat,
  esCarpeta = false,
  dinsCarpeta = false,
  favorit = false,
  onClick,
  onEdit,
  onDelete,
}: CredentialCardProps) {
  const navigate = useNavigate();
  const [isFavorit, setIsFavorit] = useState(favorit);

  const toggleFavorit = async (e: React.MouseEvent) => {
    e.stopPropagation();

    try {
      if (esCarpeta) {
        const updated = await carpetasApi.updateCarpeta(uuid, {
          favorit: !isFavorit,
        });

        if (updated) {
          setIsFavorit(!!updated.favorit);
        }
      } else {
        const updated = await itemsApi.updateItem(uuid, {
          favorit: !isFavorit,
        });

        if (updated) {
          setIsFavorit(!!updated.favorit);
        }
      }
    } catch (error) {
      console.error(error);
      toast.error('Error al cambiar favorito');
    }
  };

  const handleEdit = (e: React.MouseEvent) => {
    e.stopPropagation();

    if (esCarpeta) {
      navigate('/editCarpeta', { state: { uuid } });
    } else {
      onEdit?.();
    }
  };

  const handleDelete = (e: React.MouseEvent<HTMLButtonElement>) => {
    e.stopPropagation();
    onDelete?.();
  };

  return (
    <Paper
      onClick={onClick}
      variant="outlined"
      sx={{
        p: 2,
        borderRadius: '10px',
        bgcolor: 'background.default',
        border: '1px solid',
        borderColor: 'divider',
        minHeight: 110,
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'space-between',
        '&:hover': { boxShadow: 1, cursor: 'pointer' },
        transition: 'box-shadow 150ms ease',
      }}
    >
      <Stack direction="row" sx={{ justifyContent: 'space-between' }}>
        <Stack direction="row" sx={{ gap: 0.75, alignItems: 'center' }}>
          {esCarpeta && (
            <FolderOutlinedIcon sx={{ fontSize: 17, color: 'text.primary' }} />
          )}

          <Typography sx={{ fontWeight: 600, fontSize: '0.9rem' }}>
            {titol}
          </Typography>
        </Stack>

        <ActionButtons
          isFavorit={isFavorit}
          onToggleFavorit={toggleFavorit}
          onEdit={handleEdit}
          onDelete={handleDelete}
          showFolderIcon={dinsCarpeta}
          size="small"
        />
      </Stack>

      <Typography sx={{ fontSize: '0.82rem', color: 'text.secondary' }}>
        {nomUsuari}
      </Typography>

      <Typography sx={{ fontSize: '0.75rem', color: 'text.secondary' }}>
        Last modified: {dataEditat}
      </Typography>
    </Paper>
  );
}