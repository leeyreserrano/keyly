import { Paper, Stack, Typography, Chip } from '@mui/material';
import FolderOutlinedIcon from '@mui/icons-material/FolderOutlined';
import KeyRoundedIcon from '@mui/icons-material/VpnKeyRounded';
import ActionButtons from './ActionButtons';
import { formatDate } from '../utils/timeUtils';
import type { Compartit } from '../api/compartitsapi';

interface SharedCardProps {
  compartit: Compartit;
  onClick: () => void;
  onDelete: (e: React.MouseEvent<HTMLButtonElement>) => void;
}

export default function SharedCard({ compartit, onClick, onDelete }: SharedCardProps) {
  const nom =
    compartit.tipusEntitat === 'CARPETA'
      ? compartit.carpeta?.nom ?? ''
      : compartit.item?.titol ?? '';

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
          {compartit.tipusEntitat === 'CARPETA'
            ? <FolderOutlinedIcon sx={{ fontSize: 17, color: 'text.primary', flexShrink: 0 }} />
            : <KeyRoundedIcon sx={{ fontSize: 17, color: 'text.primary', flexShrink: 0 }} />
          }
          <Typography
            sx={{
              fontWeight: 600,
              fontSize: '0.9rem',
              overflow: 'hidden',
              textOverflow: 'ellipsis',
              whiteSpace: 'nowrap',
            }}
          >
            {nom}
          </Typography>
        </Stack>

        <ActionButtons
          onDelete={onDelete}
          size="small"
        />
      </Stack>

      <Typography
        sx={{
          fontSize: '0.82rem',
          color: 'text.secondary',
          overflow: 'hidden',
          textOverflow: 'ellipsis',
          whiteSpace: 'nowrap',
        }}
      >
        Compartit per: {compartit.usuari.nom}
      </Typography>

      <Stack direction="row" sx={{ justifyContent: 'space-between', alignItems: 'center' }}>
        <Chip
          label={compartit.permisos}
          size="small"
          sx={{ fontSize: '0.7rem', fontWeight: 600 }}
        />
        <Typography sx={{ fontSize: '0.75rem', color: 'text.secondary' }}>
          {formatDate(compartit.dataCreacio)}
        </Typography>
      </Stack>
    </Paper>
  );
}