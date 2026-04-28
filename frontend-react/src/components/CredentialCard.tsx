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
import { getTimeAgo, formatDate } from '../utils/timeUtils';
import { useAuth } from '../context/AuthContext';

interface CredentialCardProps {
  uuid: string;
  titol: string;
  nomUsuari: string;
  dataEditat: string;
  dataCreacio?: string;
  ultimAcces?: string;
  esCarpeta?: boolean;
  dinsCarpeta?: boolean;
  favorit?: boolean;
  onClick?: () => void;
  onEdit?: () => void;
  onDelete?: () => void;
  onAccess?: (uuid: string, esCarpeta: boolean) => void;
}

export default function CredentialCard({
  uuid,
  titol,
  nomUsuari: _nomUsuari,
  dataEditat,
  dataCreacio,
  esCarpeta = false,
  dinsCarpeta = false,
  favorit = false,
  onClick,
  onEdit,
  onDelete,
  onAccess,
}: CredentialCardProps) {
  const navigate = useNavigate();
  const { usuari } = useAuth();
  const [isFavorit, setIsFavorit] = useState(favorit);
  const now = useTimeRefresh(10000);

  const handleClick = () => {
    onAccess?.(uuid, esCarpeta);
    onClick?.();
  };

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
      navigate('/EditCarpeta', { state: { uuid } });
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
      onClick={handleClick}
      variant="outlined"
      sx={{
        p: 2,
        borderRadius: '10px',
        bgcolor: 'background.default',
        border: '1px solid',
        borderColor: 'divider',
        height: 110,
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'space-between',
        '&:hover': { boxShadow: 1, cursor: 'pointer' },
        transition: 'box-shadow 150ms ease',
      }}
    >
      <Stack direction="row" sx={{ justifyContent: 'space-between', alignItems: 'flex-start' }}>
        <Stack direction="row" sx={{ gap: 0.75, alignItems: 'center', minWidth: 0 }}>
          {esCarpeta && (
            <FolderOutlinedIcon sx={{ fontSize: 17, color: 'text.primary', flexShrink: 0 }} />
          )}
          <Typography
            sx={{
              fontWeight: 600,
              fontSize: '0.9rem',
              overflow: 'hidden',
              textOverflow: 'ellipsis',
              whiteSpace: 'nowrap',
            }}
          >
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

      <Typography sx={{ fontSize: '0.75rem', color: 'text.secondary' }}>
        Propietari: {usuari?.nom ?? ''}
      </Typography>

      <Stack direction="row" sx={{ justifyContent: 'space-between', alignItems: 'center' }}>
        <Typography sx={{ fontSize: '0.75rem', color: 'text.secondary' }}>
          Modificat: {getTimeAgo(dataEditat, now)}
        </Typography>
        <Typography sx={{ fontSize: '0.75rem', color: 'text.secondary' }}>
          {dataCreacio
            ? `Creat: ${formatDate(dataCreacio)}`
            : ''}
        </Typography>
      </Stack>
    </Paper>
  );
}