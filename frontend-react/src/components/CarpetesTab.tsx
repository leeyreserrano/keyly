import { useCallback, useEffect, useState } from 'react';
import {
  Box,
  Chip,
  CircularProgress,
  IconButton,
  InputAdornment,
  MenuItem,
  Paper,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TextField,
  Tooltip,
} from '@mui/material';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import SearchIcon from '@mui/icons-material/Search';
import toast from 'react-hot-toast';

import { useAuth } from '../context/AuthContext';
import {
  compartitsApi,
  type Compartit,
  type Permisos,
  type TipusEntitat,
} from '../api/compartitsapi';

const PERMIS_COLOR: Record<Permisos, 'default' | 'primary' | 'error'> = {
  LECTURA: 'default',
  ESCRIPTURA: 'primary',
  ADMINISTRADOR: 'error',
};

const permisosOptions: Permisos[] = ['LECTURA', 'ESCRIPTURA', 'ADMINISTRADOR'];
const tipusOptions: TipusEntitat[] = ['ITEM', 'CARPETA'];

export default function CarpetesTab() {
  const { usuari } = useAuth();
  const isAdmin = usuari?.rolIntern === 'ADMIN';

  const [loading, setLoading] = useState(false);
  const [search, setSearch] = useState('');
  const [carpetes, setCarpetes] = useState<Compartit[]>([]);
  const [deletingId, setDeletingId] = useState<string | null>(null);

  const [filterPermisos, setFilterPermisos] = useState<Permisos | 'ALL'>('ALL');
  const [filterTipus, setFilterTipus] = useState<TipusEntitat | 'ALL'>('CARPETA');

  const loadCarpetes = useCallback(async () => {
    setLoading(true);

    try {
      const data = isAdmin
        ? await compartitsApi.fetchAllAdmin()
        : await compartitsApi.fetchCompartitsCreats();

      let filtered = (data ?? []).filter(c => c.tipusEntitat === 'CARPETA');

      if (!isAdmin && usuari?.departament?.uuid) {
        filtered = filtered.filter(
          c =>
            c.usuariReceptor.departament?.uuid ===
            usuari.departament?.uuid
        );
      }

      setCarpetes(filtered);
    } catch {
      toast.error('Error carregant carpetes compartides');
    } finally {
      setLoading(false);
    }
  }, [isAdmin, usuari]);

  useEffect(() => {
    loadCarpetes();
  }, [loadCarpetes]);

  const visibleCarpetes = carpetes.filter(c => {
    const q = search.toLowerCase();

    const matchesSearch =
      c.carpeta?.nom?.toLowerCase().includes(q) ||
      c.usuariCreador.nom.toLowerCase().includes(q) ||
      c.usuariReceptor.nom.toLowerCase().includes(q);

    const matchesPermisos =
      filterPermisos === 'ALL' || c.permisos === filterPermisos;

    const matchesTipus =
      filterTipus === 'ALL' || c.tipusEntitat === filterTipus;

    return matchesSearch && matchesPermisos && matchesTipus;
  });

  const handleDelete = async (uuid: string) => {
    setDeletingId(uuid);

    try {
      await compartitsApi.deleteCompartit(uuid);

      setCarpetes(prev => prev.filter(i => i.uuid !== uuid));

      toast.success('Compartit eliminat');
    } catch {
      toast.error('Error eliminant compartit');
    } finally {
      setDeletingId(null);
    }
  };

  return (
    <Box>
      <Stack
        direction={{ xs: 'column', sm: 'row' }}
        spacing={1}
        sx={{ mb: 2, alignItems: 'center' }}
      >
        <TextField
          placeholder="Cerca carpeta compartida..."
          size="small"
          value={search}
          onChange={e => setSearch(e.target.value)}
          sx={{ width: { xs: '100%', sm: 320 } }}
          InputProps={{
            startAdornment: (
              <InputAdornment position="start">
                <SearchIcon fontSize="small" />
              </InputAdornment>
            ),
          }}
        />

        <TextField
          select
          size="small"
          value={filterPermisos}
          onChange={e => setFilterPermisos(e.target.value as any)}
          sx={{ width: { xs: '100%', sm: 200 } }}
        >
          <MenuItem value="ALL">Tots els permisos</MenuItem>
          {permisosOptions.map(p => (
            <MenuItem key={p} value={p}>
              {p}
            </MenuItem>
          ))}
        </TextField>

        <TextField
          select
          size="small"
          value={filterTipus}
          onChange={e => setFilterTipus(e.target.value as any)}
          sx={{ width: { xs: '100%', sm: 200 } }}
        >
          <MenuItem value="ALL">Tots els tipus</MenuItem>
          {tipusOptions.map(t => (
            <MenuItem key={t} value={t}>
              {t}
            </MenuItem>
          ))}
        </TextField>
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
                <TableCell sx={{ fontWeight: 700 }}>Carpeta</TableCell>
                <TableCell sx={{ fontWeight: 700 }}>Creador</TableCell>
                <TableCell sx={{ fontWeight: 700 }}>Receptor</TableCell>
                <TableCell sx={{ fontWeight: 700 }}>Departament</TableCell>
                <TableCell sx={{ fontWeight: 700 }}>Permisos</TableCell>
                <TableCell align="right" sx={{ fontWeight: 700 }}>
                  Accions
                </TableCell>
              </TableRow>
            </TableHead>

            <TableBody>
              {visibleCarpetes.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={6} align="center" sx={{ py: 4, color: 'text.secondary' }}>
                    No hi ha carpetes compartides
                  </TableCell>
                </TableRow>
              ) : (
                visibleCarpetes.map(c => (
                  <TableRow key={c.uuid} hover>
                    <TableCell>{c.carpeta?.nom ?? '—'}</TableCell>
                    <TableCell>{c.usuariCreador.nom}</TableCell>
                    <TableCell>{c.usuariReceptor.nom}</TableCell>
                    <TableCell>{c.usuariReceptor.departament?.departament ?? '—'}</TableCell>
                    <TableCell>
                      <Chip
                        label={c.permisos}
                        size="small"
                        color={PERMIS_COLOR[c.permisos]}
                        sx={{ fontWeight: 600 }}
                      />
                    </TableCell>
                    <TableCell align="right">
                      <Tooltip title="Eliminar compartit">
                        <IconButton
                          color="error"
                          size="small"
                          disabled={deletingId === c.uuid}
                          onClick={() => handleDelete(c.uuid)}
                        >
                          {deletingId === c.uuid ? (
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
  );
}