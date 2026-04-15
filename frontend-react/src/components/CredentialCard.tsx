import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import Paper from '@mui/material/Paper';
import FolderOutlinedIcon from '@mui/icons-material/FolderOutlined';
import { useNavigate } from 'react-router';
import ActionButtons from './ActionButtons';
import toast from 'react-hot-toast';
import { carpetasApi } from '../api/carpetasapi';
import { itemsApi } from '../api/itemsapi';
import { useState } from 'react';
import { useTimeRefresh } from './UseTimeRefresh';
import { getTimeAgo } from '../utils/timeUtils';

interface CredentialCardProps {
  uuid: string;
  titol: string;
  nomUsuari: string;
  dataEditat: string;
  dataCreacio?: string;
  esCarpeta?: boolean;
  dinsCarpeta?: boolean;
  favorit?: boolean;
  onClick?: () => void;
  onEdit?: () => void;
  onDelete?: () => void;
  onToggleFavorit?: () => void;
}

export default function CredentialCard({
  uuid,
  titol,
  nomUsuari,
  dataEditat,
  dataCreacio,
  esCarpeta = false,
  dinsCarpeta = false,
  favorit = false,
  onClick,
  onEdit,
  onDelete,
}: CredentialCardProps) {
  const navigate = useNavigate();
  const [isFavorit, setIsFavorit] = useState(favorit);
  const now = useTimeRefresh(10000);

  const handleToggleFavorit = async (e: React.MouseEvent<HTMLButtonElement>) => {
    e.stopPropagation();
    const newValue = !isFavorit;
    setIsFavorit(newValue);
    try {
      if (esCarpeta) {
        await carpetasApi.updateCarpeta(uuid, { favorit: newValue });
      } else {
        await itemsApi.updateItem(uuid, { favorit: newValue });
      }
    } catch {
      setIsFavorit(!newValue);
      toast.error('Error al canviar favorit');
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
          onToggleFavorit={handleToggleFavorit}
          onEdit={handleEdit}
          onDelete={handleDelete}
          showFolderIcon={dinsCarpeta}
          size="small"
        />
      </Stack>

      {!esCarpeta && (
        <Typography sx={{ fontSize: '0.82rem', color: 'text.secondary' }}>
          {nomUsuari}
        </Typography>
      )}

      <Stack direction="row" sx={{ justifyContent: 'space-between', mt: 0.5 }}>
        <Typography sx={{ fontSize: '0.75rem', color: 'text.secondary' }}>
          Modificat: {getTimeAgo(dataEditat, now)}
        </Typography>
        {dataCreacio && (
          <Typography sx={{ fontSize: '0.75rem', color: 'text.secondary' }}>
            Creat: {getTimeAgo(dataCreacio, now)}
          </Typography>
        )}
      </Stack>
    </Paper>
  );
}