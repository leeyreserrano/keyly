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
} from '../api/compartitsapi';

const PERMIS_COLOR: Record<Permisos, 'default' | 'primary' | 'error'> = {
  LECTURA: 'default',
  ESCRIPTURA: 'primary',
  ADMINISTRADOR: 'error',
};

const permisosOptions: Permisos[] = ['LECTURA', 'ESCRIPTURA', 'ADMINISTRADOR'];

export default function ItemsTab() {
  const { usuari } = useAuth();
  const isAdmin = usuari?.rolIntern === 'ADMIN';

  const [loading, setLoading] = useState(false);
  const [search, setSearch] = useState('');
  const [items, setItems] = useState<Compartit[]>([]);
  const [deletingId, setDeletingId] = useState<string | null>(null);

  const [filterPermisos, setFilterPermisos] = useState<Permisos | 'ALL'>('ALL');
  const [filterDepartament, setFilterDepartament] = useState<string | 'ALL'>('ALL');
  const [filterMode, setFilterMode] = useState<'RECEPTOR' | 'CREADOR'>('RECEPTOR');

  const loadItems = useCallback(async () => {
    setLoading(true);

    try {
      const data = isAdmin
        ? await compartitsApi.fetchAllAdmin()
        : await compartitsApi.fetchCompartitsCreats();

      let filtered = (data ?? []).filter(
        c => c.tipusEntitat === 'ITEM'
      );

      if (!isAdmin && usuari?.departament?.uuid) {
        filtered = filtered.filter(
          c =>
            c.usuariReceptor.departament?.uuid ===
            usuari.departament?.uuid
        );
      }

      setItems(filtered);
    } catch {
      toast.error('Error carregant items compartits');
    } finally {
      setLoading(false);
    }
  }, [isAdmin, usuari]);

  useEffect(() => {
    loadItems();
  }, [loadItems]);

  const visibleItems = items.filter(c => {
    const q = search.toLowerCase();

    const matchesSearch =
      c.item?.titol?.toLowerCase().includes(q) ||
      c.usuariCreador.nom.toLowerCase().includes(q) ||
      c.usuariReceptor.nom.toLowerCase().includes(q);

    const matchesPermisos =
      filterPermisos === 'ALL' || c.permisos === filterPermisos;

    const dep =
      filterMode === 'RECEPTOR'
        ? c.usuariReceptor.departament?.uuid
        : c.usuariCreador.departament?.uuid;

    const matchesDepartament =
      filterDepartament === 'ALL' || dep === filterDepartament;

    return matchesSearch && matchesPermisos && matchesDepartament;
  });

  const handleDelete = async (uuid: string) => {
    setDeletingId(uuid);

    try {
      await compartitsApi.deleteCompartit(uuid);

      setItems(prev => prev.filter(i => i.uuid !== uuid));

      toast.success('Compartit eliminat');
    } catch {
      toast.error('Error eliminant compartit');
    } finally {
      setDeletingId(null);
    }
  };

  const departamentsDisponibles = Array.from(
    new Map(
      items
        .flatMap(i => [i.usuariReceptor, i.usuariCreador])
        .filter(u => u?.departament?.uuid)
        .map(u => [u.departament!.uuid, u.departament!.departament])
    )
  ).map(([uuid, nom]) => ({ uuid, nom }));

  return (
    <Box>
      <Stack
        direction={{ xs: 'column', sm: 'row' }}
        spacing={1}
        sx={{ mb: 2, alignItems: 'center' }}
      >
        <TextField
          placeholder="Cerca item compartit..."
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
          value={filterMode}
          onChange={e => setFilterMode(e.target.value as any)}
          sx={{ width: { xs: '100%', sm: 200 } }}
        >
          <MenuItem value="RECEPTOR">Departament receptor</MenuItem>
          <MenuItem value="CREADOR">Departament creador</MenuItem>
        </TextField>

        <TextField
          select
          size="small"
          value={filterDepartament}
          onChange={e => setFilterDepartament(e.target.value as any)}
          sx={{ width: { xs: '100%', sm: 220 } }}
        >
          <MenuItem value="ALL">Tots els departaments</MenuItem>
          {departamentsDisponibles.map(d => (
            <MenuItem key={d.uuid} value={d.uuid}>
              {d.nom}
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
                <TableCell sx={{ fontWeight: 700 }}>Item</TableCell>
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
              {visibleItems.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={6} align="center" sx={{ py: 4, color: 'text.secondary' }}>
                    No hi ha items compartits
                  </TableCell>
                </TableRow>
              ) : (
                visibleItems.map(c => (
                  <TableRow key={c.uuid} hover>
                    <TableCell>{c.item?.titol ?? '—'}</TableCell>
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