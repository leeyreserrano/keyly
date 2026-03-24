import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import IconButton from '@mui/material/IconButton';
import Paper from '@mui/material/Paper';
import EditOutlinedIcon from '@mui/icons-material/EditOutlined';
import DeleteOutlinedIcon from '@mui/icons-material/DeleteOutlined';
import FolderOutlinedIcon from '@mui/icons-material/FolderOutlined';

interface CredentialCardProps {
  uuid: string;
  titol: string;
  nomUsuari: string;
  dataEditat: string;
  esCarpeta?: boolean;
  dinsCarpeta?: boolean;
  onClick?: () => void;
  onEdit?: () => void;
  onDelete?: () => void;
}

export default function CredentialCard({
  titol,
  nomUsuari,
  dataEditat,
  esCarpeta = false,
  dinsCarpeta = false,
  onClick,
  onEdit,
  onDelete,
}: CredentialCardProps) {
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
          {esCarpeta && <FolderOutlinedIcon sx={{ fontSize: 17, color: 'text.secondary' }} />}
          <Typography variant="body1" sx={{ fontWeight: 600, color: 'text.primary', fontSize: '0.9rem' }}>
            {titol}
          </Typography>
        </Stack>

        <Stack direction="row" sx={{ gap: 0.25, ml: 1, alignItems: 'center' }}>
          {dinsCarpeta && <FolderOutlinedIcon sx={{ fontSize: 16, color: 'text.secondary', mr: 0.5 }} />}
          {onEdit && (
            <IconButton
              size="small"
              sx={{ width: 28, height: 28, border: '1px solid', borderColor: 'divider', bgcolor: 'transparent', p: 0 }}
              onClick={(e) => { e.stopPropagation(); onEdit(); }}
            >
              <EditOutlinedIcon sx={{ fontSize: 14, color: 'text.secondary' }} />
            </IconButton>
          )}
          {onDelete && (
            <IconButton
              size="small"
              sx={{ width: 28, height: 28, border: '1px solid', borderColor: 'divider', bgcolor: 'error.main', color: 'white', p: 0, '&:hover': { bgcolor: 'error.dark' } }}
              onClick={(e) => { e.stopPropagation(); onDelete(); }}
            >
              <DeleteOutlinedIcon sx={{ fontSize: 14 }} />
            </IconButton>
          )}
        </Stack>
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