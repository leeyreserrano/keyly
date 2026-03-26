import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import Paper from '@mui/material/Paper';
import FolderOutlinedIcon from '@mui/icons-material/FolderOutlined';
import { useState } from 'react';
import { useNavigate } from 'react-router';
import { itemsApi, type Item } from '../api/itemsapi';
import { toast } from 'react-hot-toast/headless';
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
        const updatedCarpeta = await carpetasApi.updateCarpeta(uuid, { favorit: !isFavorit });
        setIsFavorit(updatedCarpeta.favorit ?? false);
        toast.success(
          updatedCarpeta.favorit ? "Carpeta añadida a favoritos" : "Carpeta quitada de favoritos"
        );
      } else {
        const updatedItem: Item = await itemsApi.updateItem(uuid, { favorit: !isFavorit });
        setIsFavorit(updatedItem.favorit ?? false);
        toast.success(
          updatedItem.favorit ? "Item añadido a favoritos" : "Item quitado de favoritos"
        );
      }
    } catch (error) {
      console.error("Error actualizando favorito", error);
      toast.error(`Error cambiando favorito ${esCarpeta ? "de carpeta" : "de item"}`);
    }
  };

  const handleEdit = (e: React.MouseEvent) => {
    e.stopPropagation();
    if (esCarpeta) {
      navigate('/editCarpeta', { state: { uuid } });
    } else {
      onEdit && onEdit();
    }
  };

  function handleDelete(event: React.MouseEvent<HTMLButtonElement>): void {
    event.stopPropagation();
    onDelete && onDelete();
  }

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
      <Stack direction="row" sx={{ justifyContent: 'space-between', alignItems: 'flex-start' }}>
        <Stack direction="row" sx={{ gap: 0.75, alignItems: 'center' }}>
          {esCarpeta && <FolderOutlinedIcon sx={{ fontSize: 17, color: 'text.primary' }} />}
          <Typography variant="body1" sx={{ fontWeight: 600, color: 'text.primary', fontSize: '0.9rem' }}>
            {titol}
          </Typography>
        </Stack>

        <ActionButtons
          isFavorit={isFavorit}
          onToggleFavorit={toggleFavorit}
          onEdit={handleEdit}
          onDelete={handleDelete}
          highlightDelete={false}
          showFolderIcon={dinsCarpeta}
          size="small"
        />
      </Stack>

        <Typography variant="body2" sx={{ color: 'text.secondary', mt: 0.5, fontSize: '0.82rem' }}>
          {nomUsuari}
        </Typography>

        <Typography variant="caption" sx={{ color: 'text.secondary', mt: 0.75, display: 'block' }}>
          Last modified: {dataEditat}
        </Typography>
    </Paper>
  );
}