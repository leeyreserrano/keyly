import { useTranslation } from 'react-i18next';
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
  url?: string;
  esCarpeta?: boolean;
  dinsCarpeta?: boolean;
  favorit?: boolean;
  showFavorit?: boolean;
  onClick?: () => void;
  onEdit?: () => void;
  onDelete?: () => void;
  onAccess?: (uuid: string, esCarpeta: boolean) => void;
}

const getFavicon = (url?: string): string | null => {
  if (!url) return null;
  try {
    const domain = new URL(url).hostname;
    return `https://icons.duckduckgo.com/ip3/${domain}.ico`;
  } catch {
    return null;
  }
};

export default function CredentialCard({
  uuid,
  titol,
  nomUsuari: _nomUsuari,
  dataEditat,
  dataCreacio,
  url,
  esCarpeta = false,
  dinsCarpeta = false,
  favorit = false,
  showFavorit = true,
  onClick,
  onEdit,
  onDelete,
  onAccess,
}: CredentialCardProps) {
  const navigate = useNavigate();
  const { t } = useTranslation('card');
  const { usuari } = useAuth();
  const [isFavorit, setIsFavorit] = useState(favorit);
  const [imgError, setImgError] = useState(false);
  const now = useTimeRefresh(10000);

  const faviconUrl = getFavicon(url);

  const handleClick = () => {
    onAccess?.(uuid, esCarpeta);
    onClick?.();
  };

  const handleToggleFavorit = async (e: React.MouseEvent<HTMLButtonElement>) => {
    e.stopPropagation();
    e.preventDefault();
    const newValue = !isFavorit;
    setIsFavorit(newValue);
    try {
      if (esCarpeta) {
        const current = await carpetasApi.getCarpeta(uuid);
        if (!current) throw new Error('No trobada');
        await carpetasApi.updateCarpeta(uuid, {
          bagulUuid: current.bagulUuid ?? '',
          nom: current.nom,
          favorit: newValue,
        });
      } else {
        const current = await itemsApi.getItem(uuid);
        if (!current) throw new Error('No trobat');
        await itemsApi.updateItem(uuid, {
          titol: current.titol,
          nomUsuari: current.nomUsuari,
          contrasenya: current.contrasenya,
          iv: current.iv,
          encryptedDataKey: current.encryptedDataKey?.encryptedDataKey,
          url: current.url,
          notes: current.notes,
          favorit: newValue,
        });
      }
    } catch {
      setIsFavorit(!newValue);
      toast.error(t('error.fav'));
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

  const renderIcon = () => {
    if (esCarpeta) {
      return <FolderOutlinedIcon sx={{ fontSize: 17, color: 'text.primary', flexShrink: 0 }} />;
    }
    if (faviconUrl && !imgError) {
      return (
        <img
          src={faviconUrl}
          alt=""
          style={{ width: 16, height: 16, flexShrink: 0, objectFit: 'contain' }}
          onError={() => setImgError(true)}
        />
      );
    }
    return (
      <svg
        xmlns="http://www.w3.org/2000/svg"
        fill="none"
        viewBox="0 0 24 24"
        strokeWidth={1.5}
        stroke="currentColor"
        style={{ width: 16, height: 16, flexShrink: 0, color: 'inherit' }}
      >
        <path
          strokeLinecap="round"
          strokeLinejoin="round"
          d="M15.75 5.25a3 3 0 0 1 3 3m3 0a6 6 0 0 1-7.029 5.912c-.563-.097-1.159.026-1.563.43L10.5 17.25H8.25v2.25H6v2.25H2.25v-2.818c0-.597.237-1.17.659-1.591l6.499-6.499c.404-.404.527-1 .43-1.563A6 6 0 1 1 21.75 8.25Z"
        />
      </svg>
    );
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
          {renderIcon()}
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
          onToggleFavorit={showFavorit ? handleToggleFavorit : undefined}
          onEdit={onEdit ? handleEdit : undefined}
          onDelete={onDelete ? handleDelete : undefined}
          showFolderIcon={dinsCarpeta}
          size="small"
        />
      </Stack>

      <Typography sx={{ fontSize: '0.75rem', color: 'text.secondary' }}>
        {t('owner')}: {usuari?.nom ?? ''}
      </Typography>

      <Stack direction="row" sx={{ justifyContent: 'space-between', alignItems: 'center' }}>
        <Typography sx={{ fontSize: '0.75rem', color: 'text.secondary' }}>
          {t('modified')}: {getTimeAgo(dataEditat, now)}
        </Typography>
        <Typography sx={{ fontSize: '0.75rem', color: 'text.secondary' }}>
          {dataCreacio ? `${t('created')}: ${formatDate(dataCreacio)}` : ''}
        </Typography>
      </Stack>
    </Paper>
  );
}