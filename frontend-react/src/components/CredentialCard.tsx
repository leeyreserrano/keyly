import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import IconButton from '@mui/material/IconButton';
import Paper from '@mui/material/Paper';
import EditOutlinedIcon from '@mui/icons-material/EditOutlined';
import DeleteOutlinedIcon from '@mui/icons-material/DeleteOutlined';
import FolderOutlinedIcon from '@mui/icons-material/FolderOutlined';

interface CredentialCardProps {
  name: string;
  email: string;
  modified: string;
  hasFolder?: boolean;
}

export default function CredentialCard({
  name,
  email,
  modified,
  hasFolder = false,
}: CredentialCardProps) {
  return (
    <Paper
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
        '&:hover': { boxShadow: 1 },
        transition: 'box-shadow 150ms ease',
      }}
    >
      {/* Title row */}
      <Stack
        direction="row"
        sx={{ justifyContent: 'space-between', alignItems: 'flex-start' }}
      >
        <Stack direction="row" sx={{ gap: 0.75, alignItems: 'center' }}>
          {hasFolder && (
            <FolderOutlinedIcon
              sx={{ fontSize: 17, color: 'text.secondary' }}
            />
          )}
          <Typography
            variant="body1"
            sx={{ fontWeight: 600, color: 'text.primary', fontSize: '0.9rem' }}
          >
            {name}
          </Typography>
        </Stack>
        <Stack direction="row" sx={{ gap: 0.25, ml: 1 }}>
          <IconButton
            size="small"
            sx={{
              width: 28,
              height: 28,
              border: '1px solid',
              borderColor: 'divider',
              bgcolor: 'transparent',
              p: 0,
            }}
          >
            <EditOutlinedIcon sx={{ fontSize: 14, color: 'text.secondary' }} />
          </IconButton>
          <IconButton
            size="small"
            sx={{
              width: 28,
              height: 28,
              border: '1px solid',
              borderColor: 'divider',
              bgcolor: 'transparent',
              p: 0,
            }}
          >
            <DeleteOutlinedIcon
              sx={{ fontSize: 14, color: 'text.secondary' }}
            />
          </IconButton>
        </Stack>
      </Stack>

      {/* Email */}
      <Typography
        variant="body2"
        sx={{ color: 'text.secondary', mt: 0.5, fontSize: '0.82rem' }}
      >
        {email}
      </Typography>

      {/* Modified */}
      <Typography
        variant="caption"
        sx={{ color: 'text.secondary', mt: 0.75, display: 'block' }}
      >
        Last modified: {modified}
      </Typography>
    </Paper>
  );
}
