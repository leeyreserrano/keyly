import { useEffect, useState } from 'react';
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
  Tooltip,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Divider,
} from '@mui/material';
import AddOutlinedIcon from '@mui/icons-material/AddOutlined';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import toast from 'react-hot-toast';

import { sucursalsApi, type Sucursal } from '../api/sucursalsapi';

type CreateSucursal = {
  nom: string;
  direccio: string;
  ciutat: string;
  pais: string;
  telefon: string;
  correu: string;
};

const EMPTY_CREATE: CreateSucursal = {
  nom: '',
  direccio: '',
  ciutat: '',
  pais: '',
  telefon: '',
  correu: '',
};

function FieldLabel({ children }: { children: string }) {
  return (
    <Typography sx={{ fontSize: '0.8rem', fontWeight: 600, color: 'text.secondary' }}>
      {children}
    </Typography>
  );
}

export default function SucursalsTab() {
  const [data, setData] = useState<Sucursal[]>([]);
  const [loading, setLoading] = useState(false);

  const [openCreate, setOpenCreate] = useState(false);
  const [createData, setCreateData] = useState<CreateSucursal>(EMPTY_CREATE);
  const [savingCreate, setSavingCreate] = useState(false);

  const [deletingId, setDeletingId] = useState<string | null>(null);

  const load = async () => {
    setLoading(true);
    try {
      setData(await sucursalsApi.fetchAll());
    } catch {
      toast.error('Error carregant sucursals');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  const handleCreate = async () => {
    if (!createData.nom) {
      toast.error('El nom és obligatori');
      return;
    }

    setSavingCreate(true);
    try {
      await sucursalsApi.add(createData);

      setCreateData(EMPTY_CREATE);
      setOpenCreate(false);
      load();

      toast.success('Sucursal creada');
    } catch {
      toast.error('Error creant sucursal');
    } finally {
      setSavingCreate(false);
    }
  };

  const handleDelete = async (id: string) => {
    setDeletingId(id);

    try {
      await sucursalsApi.delete(id);
      setData(prev => prev.filter(s => s.uuid !== id));
      toast.success('Sucursal eliminada');
    } catch {
      toast.error('Error eliminant sucursal');
    } finally {
      setDeletingId(null);
    }
  };

  return (
    <>
      <Box>
        <Stack
          direction="row"
          justifyContent="space-between"
          alignItems="center"
          sx={{ mb: 2 }}
        >
          <Typography sx={{ fontWeight: 700 }}>Sucursals</Typography>

          <Button
            variant="contained"
            startIcon={<AddOutlinedIcon />}
            onClick={() => setOpenCreate(true)}
            sx={{ textTransform: 'none', fontWeight: 700 }}
          >
            Afegir sucursal
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
                  <TableCell sx={{ fontWeight: 700 }}>Ciutat</TableCell>
                  <TableCell sx={{ fontWeight: 700 }}>País</TableCell>
                  <TableCell sx={{ fontWeight: 700 }}>Telèfon</TableCell>
                  <TableCell sx={{ fontWeight: 700 }}>Correu</TableCell>
                  <TableCell align="right" sx={{ fontWeight: 700 }}>
                    Accions
                  </TableCell>
                </TableRow>
              </TableHead>

              <TableBody>
                {data.length === 0 ? (
                  <TableRow>
                    <TableCell colSpan={6} align="center" sx={{ py: 4, color: 'text.secondary' }}>
                      No hi ha sucursals
                    </TableCell>
                  </TableRow>
                ) : (
                  data.map(s => (
                    <TableRow key={s.uuid} hover>
                      <TableCell>{s.nom}</TableCell>
                      <TableCell>{(s as any).ciutat ?? '—'}</TableCell>
                      <TableCell>{(s as any).pais ?? '—'}</TableCell>
                      <TableCell>{(s as any).telefon ?? '—'}</TableCell>
                      <TableCell>{(s as any).correu ?? '—'}</TableCell>

                      <TableCell align="right">
                        <Tooltip title="Eliminar">
                          <IconButton
                            color="error"
                            size="small"
                            onClick={() => handleDelete(s.uuid)}
                            disabled={deletingId === s.uuid}
                          >
                            {deletingId === s.uuid ? (
                              <CircularProgress size={16} color="inherit" />
                            ) : (
                              <DeleteOutlineIcon fontSize="small" />
                            )}
                          </IconButton>
                        </Tooltip>
                      </TableCell>
                    </TableRow>
                  ))
                )}
              </TableBody>
            </Table>
          </TableContainer>
        )}
      </Box>

      <Dialog open={openCreate} onClose={() => setOpenCreate(false)} maxWidth="sm" fullWidth>
        <DialogTitle sx={{ fontWeight: 700 }}>Nova sucursal</DialogTitle>
        <Divider />

        <DialogContent>
          <Stack spacing={2} sx={{ pt: 1 }}>
            <Stack spacing={0.5}>
              <FieldLabel>Nom *</FieldLabel>
              <TextField
                fullWidth
                value={createData.nom}
                onChange={e => setCreateData(p => ({ ...p, nom: e.target.value }))}
              />
            </Stack>

            <Stack spacing={0.5}>
              <FieldLabel>Direcció</FieldLabel>
              <TextField
                fullWidth
                value={createData.direccio}
                onChange={e => setCreateData(p => ({ ...p, direccio: e.target.value }))}
              />
            </Stack>

            <Stack spacing={0.5}>
              <FieldLabel>Ciutat</FieldLabel>
              <TextField
                fullWidth
                value={createData.ciutat}
                onChange={e => setCreateData(p => ({ ...p, ciutat: e.target.value }))}
              />
            </Stack>

            <Stack spacing={0.5}>
              <FieldLabel>País</FieldLabel>
              <TextField
                fullWidth
                value={createData.pais}
                onChange={e => setCreateData(p => ({ ...p, pais: e.target.value }))}
              />
            </Stack>

            <Stack spacing={0.5}>
              <FieldLabel>Telèfon</FieldLabel>
              <TextField
                fullWidth
                value={createData.telefon}
                onChange={e => setCreateData(p => ({ ...p, telefon: e.target.value }))}
              />
            </Stack>

            <Stack spacing={0.5}>
              <FieldLabel>Correu</FieldLabel>
              <TextField
                fullWidth
                value={createData.correu}
                onChange={e => setCreateData(p => ({ ...p, correu: e.target.value }))}
              />
            </Stack>
          </Stack>
        </DialogContent>

        <Divider />

        <DialogActions sx={{ px: 3, py: 2 }}>
          <Button
            onClick={() => setOpenCreate(false)}
            variant="outlined"
            sx={{ textTransform: 'none', fontWeight: 600 }}
          >
            Cancel·lar
          </Button>

          <Button
            variant="contained"
            onClick={handleCreate}
            disabled={savingCreate}
            sx={{ textTransform: 'none', fontWeight: 600 }}
          >
            {savingCreate ? <CircularProgress size={18} color="inherit" /> : 'Crear'}
          </Button>
        </DialogActions>
      </Dialog>
    </>
  );
}