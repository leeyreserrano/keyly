import { useEffect, useState, useCallback } from 'react';
import {
  Box,
  Stack,
  Paper,
  Typography,
  TextField,
  Button,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  IconButton,
  CircularProgress,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  FormControl,
  Select,
  MenuItem,
  InputAdornment,
  Divider,
} from '@mui/material';

import AddOutlinedIcon from '@mui/icons-material/AddOutlined';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import SearchIcon from '@mui/icons-material/Search';

import toast from 'react-hot-toast';

import { rolsApi, type Rol } from '../api/rolsapi';
import { sucursalsApi, type Sucursal } from '../api/sucursalsapi';

type CreateRol = {
  nom: string;
  sucursalUuid: string;
};

const EMPTY_CREATE: CreateRol = {
  nom: '',
  sucursalUuid: '',
};

function FieldLabel({ children }: { children: string }) {
  return (
    <Typography sx={{ fontSize: '0.8rem', fontWeight: 600, color: 'text.secondary' }}>
      {children}
    </Typography>
  );
}

export default function RolsTab() {
  const [data, setData] = useState<Rol[]>([]);
  const [sucursals, setSucursals] = useState<Sucursal[]>([]);
  const [loading, setLoading] = useState(false);

  const [search, setSearch] = useState('');

  const [openCreate, setOpenCreate] = useState(false);
  const [createData, setCreateData] = useState<CreateRol>(EMPTY_CREATE);
  const [savingCreate, setSavingCreate] = useState(false);

  const [deletingId, setDeletingId] = useState<string | null>(null);

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const [r, s] = await Promise.all([
        rolsApi.fetchAll(),
        sucursalsApi.fetchAll(),
      ]);
      setData(r);
      setSucursals(s);
    } catch {
      toast.error('Error carregant rols');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const visible = data.filter(r => {
    const q = search.toLowerCase();
    return (
      r.nom.toLowerCase().includes(q) ||
      r.sucursal?.nom?.toLowerCase().includes(q)
    );
  });

  const handleCreate = async () => {
    if (!createData.nom || !createData.sucursalUuid) {
      toast.error('Omple tots els camps');
      return;
    }

    setSavingCreate(true);
    try {
      await rolsApi.create({
        nom: createData.nom,
        sucursalUuid: createData.sucursalUuid,
      });

      toast.success('Rol creat');
      setOpenCreate(false);
      setCreateData(EMPTY_CREATE);
      load();
    } catch {
      toast.error('Error creant rol');
    } finally {
      setSavingCreate(false);
    }
  };

  const handleDelete = async (uuid: string) => {
    setDeletingId(uuid);
    try {
      await rolsApi.delete(uuid);
      setData(prev => prev.filter(r => r.uuid !== uuid));
      toast.success('Rol eliminat');
    } catch {
      toast.error('Error eliminant rol');
    } finally {
      setDeletingId(null);
    }
  };

  return (
    <Box>
      <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{ mb: 2 }}>
        <TextField
          placeholder="Cerca rol o sucursal..."
          size="small"
          value={search}
          onChange={e => setSearch(e.target.value)}
          sx={{ width: 320 }}
          InputProps={{
            startAdornment: (
              <InputAdornment position="start">
                <SearchIcon fontSize="small" />
              </InputAdornment>
            ),
          }}
        />

        <Button
          variant="contained"
          startIcon={<AddOutlinedIcon />}
          onClick={() => setOpenCreate(true)}
          sx={{ textTransform: 'none', fontWeight: 700 }}
        >
          Nou rol
        </Button>
      </Stack>

      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', py: 6 }}>
          <CircularProgress />
        </Box>
      ) : (
        <TableContainer component={Paper} variant="outlined" sx={{ borderRadius: 2 }}>
          <Table size="small">
            <TableHead>
              <TableRow>
                <TableCell sx={{ fontWeight: 700 }}>Nom</TableCell>
                <TableCell sx={{ fontWeight: 700 }}>Sucursal</TableCell>
                <TableCell align="right" sx={{ fontWeight: 700 }}>Accions</TableCell>
              </TableRow>
            </TableHead>

            <TableBody>
              {visible.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={3} align="center" sx={{ py: 4, color: 'text.secondary' }}>
                    No hi ha rols
                  </TableCell>
                </TableRow>
              ) : (
                visible.map(r => (
                  <TableRow key={r.uuid} hover>
                    <TableCell>{r.nom}</TableCell>
                    <TableCell>{r.sucursal?.nom ?? '—'}</TableCell>
                    <TableCell align="right">
                      <IconButton
                        sx={{
                          bgcolor: 'error.main',
                          color: 'white',
                          '&:hover': { bgcolor: 'error.dark' },
                        }}
                        onClick={() => handleDelete(r.uuid)}
                        disabled={deletingId === r.uuid}
                      >
                        {deletingId === r.uuid ? (
                          <CircularProgress size={16} />
                        ) : (
                          <DeleteOutlineIcon />
                        )}
                      </IconButton>
                    </TableCell>
                  </TableRow>
                ))
              )}
            </TableBody>
          </Table>
        </TableContainer>
      )}

      <Dialog open={openCreate} onClose={() => setOpenCreate(false)} fullWidth maxWidth="sm">
        <DialogTitle sx={{ fontWeight: 700 }}>Nou rol</DialogTitle>
        <Divider />

        <DialogContent>
          <Stack spacing={2} sx={{ pt: 1 }}>
            <Stack spacing={0.5}>
              <FieldLabel>Nom</FieldLabel>
              <TextField
                fullWidth
                value={createData.nom}
                onChange={e => setCreateData(p => ({ ...p, nom: e.target.value }))}
              />
            </Stack>

            <Stack spacing={0.5}>
              <FieldLabel>Sucursal</FieldLabel>
              <FormControl fullWidth>
                <Select
                  value={createData.sucursalUuid}
                  onChange={e =>
                    setCreateData(p => ({ ...p, sucursalUuid: e.target.value }))
                  }
                  displayEmpty
                >
                  <MenuItem value=""><em>Selecciona sucursal</em></MenuItem>
                  {sucursals.map(s => (
                    <MenuItem key={s.uuid} value={s.uuid}>
                      {s.nom}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Stack>
          </Stack>
        </DialogContent>

        <Divider />

        <DialogActions sx={{ px: 3, py: 2 }}>
          <Button onClick={() => setOpenCreate(false)}>Cancelar</Button>

          <Button
            variant="contained"
            onClick={handleCreate}
            disabled={savingCreate}
          >
            {savingCreate ? <CircularProgress size={18} /> : 'Crear'}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}