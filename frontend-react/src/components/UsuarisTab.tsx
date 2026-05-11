import { useState, useEffect, useCallback } from 'react';
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
  Chip,
  IconButton,
  CircularProgress,
  Tooltip,
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
import EditOutlinedIcon from '@mui/icons-material/EditOutlined';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import SearchIcon from '@mui/icons-material/Search';
import { useAuth } from '../context/AuthContext';
import type { SucursalObj, DepartamentObj } from '../context/AuthContext';
import { usuarisApi } from '../api/usuarisapi';
import { sucursalsApi, type Sucursal } from '../api/sucursalsapi';
import { departamentsApi, type Departament } from '../api/departamentsapi';
import { rolsApi, type Rol } from '../api/rolsapi';
import { deriveKey, generateKeyPair, encryptPrivateKey, bytesToBase64 } from '../crypto/cryptoService';
import toast from 'react-hot-toast';

type RolType = 'error' | 'warning' | 'default';

const ROL_LABEL: Record<string, { label: string; color: RolType }> = {
  ADMIN: { label: 'Administrador', color: 'error' },
  CAP: { label: 'Cap', color: 'warning' },
  USUARI: { label: 'Usuari', color: 'default' },
};

type RolObj = { uuid: string; nom: string };

type UsuariAdmin = {
  uuid: string;
  nom: string;
  correu: string;
  rolIntern: 'ADMIN' | 'CAP' | 'USUARI';
  sucursal: SucursalObj | null;
  departament: DepartamentObj | null;
  rol: RolObj | null;
  potAdministrar: boolean;
};

type CreateFormData = {
  nom: string;
  correu: string;
  contrasenya: string;
  rolIntern: 'ADMIN' | 'CAP' | 'USUARI';
  rolUuid: string;
  sucursalUuid: string;
  departamentUuid: string;
  potAdministrar: boolean;
};

type UpdateUsuariData = {
  nom?: string;
  correu?: string;
  rolUuid?: string;
  sucursalUuid?: string;
  departamentUuid?: string;
};

const EMPTY_CREATE: CreateFormData = {
  nom: '',
  correu: '',
  contrasenya: '',
  rolIntern: 'USUARI',
  rolUuid: '',
  sucursalUuid: '',
  departamentUuid: '',
  potAdministrar: false,
};

function generateSalt(): string {
  const bytes = crypto.getRandomValues(new Uint8Array(32));
  return bytesToBase64(bytes);
}

function FieldLabel({ children }: { children: string }) {
  return (
    <Typography sx={{ fontSize: '0.8rem', fontWeight: 600, color: 'text.secondary' }}>
      {children}
    </Typography>
  );
}

export default function UsuarisTab() {
  const { usuari } = useAuth();
  const isAdmin = usuari?.rolIntern === 'ADMIN';

  const [usuaris, setUsuaris] = useState<UsuariAdmin[]>([]);
  const [loading, setLoading] = useState(false);
  const [search, setSearch] = useState('');

  const [sucursals, setSucursals] = useState<Sucursal[]>([]);
  const [departaments, setDepartaments] = useState<Departament[]>([]);
  const [rols, setRols] = useState<Rol[]>([]);

  const [openCreate, setOpenCreate] = useState(false);
  const [createData, setCreateData] = useState<CreateFormData>(EMPTY_CREATE);
  const [savingCreate, setSavingCreate] = useState(false);

  const [openEdit, setOpenEdit] = useState(false);
  const [editTarget, setEditTarget] = useState<UsuariAdmin | null>(null);
  const [editData, setEditData] = useState<UpdateUsuariData>({});
  const [savingEdit, setSavingEdit] = useState(false);

  const [openDelete, setOpenDelete] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState<UsuariAdmin | null>(null);
  const [deletingId, setDeletingId] = useState<string | null>(null);

  const loadUsuaris = useCallback(async () => {
    setLoading(true);
    try {
      const data = await usuarisApi.fetchAll() as unknown as UsuariAdmin[];
      setUsuaris(data);
    } catch {
      toast.error('Error carregant els usuaris');
    } finally {
      setLoading(false);
    }
  }, []);

  const loadSelectData = useCallback(async () => {
    if (!isAdmin) return;
    try {
      const [sucursalsData, departamentsData, rolsData] = await Promise.all([
        sucursalsApi.fetchAll(),
        departamentsApi.fetchAll(),
        rolsApi.fetchAll(),
      ]);
      setSucursals(sucursalsData);
      setDepartaments(departamentsData);
      setRols(rolsData);
    } catch {
      toast.error('Error carregant dades del formulari');
    }
  }, [isAdmin]);

  useEffect(() => {
    loadUsuaris();
    loadSelectData();
  }, [loadUsuaris, loadSelectData]);

  const visibleUsuaris = (() => {
    let list = usuaris;
    if (!isAdmin && usuari) {
      const capDeptUuid = usuari.departament?.uuid;
      if (capDeptUuid) {
        list = list.filter(u => u.departament?.uuid === capDeptUuid);
      }
    }
    if (search.trim()) {
      const q = search.toLowerCase();
      list = list.filter(u =>
        u.nom.toLowerCase().includes(q) ||
        u.correu.toLowerCase().includes(q) ||
        u.departament?.departament?.toLowerCase().includes(q) ||
        u.sucursal?.nom?.toLowerCase().includes(q)
      );
    }
    return list;
  })();

  const handleOpenCreate = () => {
    const base: CreateFormData = { ...EMPTY_CREATE };
    if (!isAdmin && usuari) {
      base.sucursalUuid = usuari.sucursal?.uuid ?? '';
      base.departamentUuid = usuari.departament?.uuid ?? '';
    }
    setCreateData(base);
    setOpenCreate(true);
  };

  const handleCreate = async () => {
    if (!createData.nom || !createData.correu || !createData.contrasenya) {
      toast.error('Omple els camps obligatoris');
      return;
    }
    setSavingCreate(true);
    try {
      const salt = generateSalt();
      const derivedKey = await deriveKey(createData.contrasenya, salt);
      const { publicKeyB64, privateKeyB64 } = await generateKeyPair();
      const encryptedPK = await encryptPrivateKey(privateKeyB64, derivedKey);

      await usuarisApi.createUsuari({
        nom: createData.nom,
        correu: createData.correu,
        contrasenya: createData.contrasenya,
        kdfSalt: salt,
        publicKey: publicKeyB64,
        encryptedPrivateKey: encryptedPK,
        rolIntern: createData.rolIntern,
        rolUuid: createData.rolUuid,
        sucursalUuid: createData.sucursalUuid,
        departamentUuid: createData.departamentUuid,
        potAdministrar: createData.potAdministrar,
      }, isAdmin);

      toast.success('Usuari creat correctament');
      setOpenCreate(false);
      loadUsuaris();
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : "Error creant l'usuari";
      const friendly = msg.includes('409') || msg.toLowerCase().includes('conflict')
        ? 'Aquest correu ja està en ús'
        : msg;
      toast.error(friendly);
    } finally {
      setSavingCreate(false);
    }
  };

  const handleOpenEdit = (u: UsuariAdmin) => {
    setEditTarget(u);
    setEditData({
      nom: u.nom,
      correu: u.correu,
      rolUuid: u.rol?.uuid ?? '',
      sucursalUuid: u.sucursal?.uuid ?? '',
      departamentUuid: u.departament?.uuid ?? '',
    });
    setOpenEdit(true);
  };

  const handleEdit = async () => {
    if (!editTarget) return;
    setSavingEdit(true);
    try {
      const payload: UpdateUsuariData = {};
      if (editData.nom !== editTarget.nom) payload.nom = editData.nom;
      if (editData.correu !== editTarget.correu) payload.correu = editData.correu;
      if (editData.rolUuid !== (editTarget.rol?.uuid ?? '')) payload.rolUuid = editData.rolUuid;
      if (editData.sucursalUuid !== (editTarget.sucursal?.uuid ?? '')) payload.sucursalUuid = editData.sucursalUuid;
      if (editData.departamentUuid !== (editTarget.departament?.uuid ?? '')) payload.departamentUuid = editData.departamentUuid;

      await usuarisApi.updateUsuariAdmin(editTarget.uuid, payload);
      toast.success('Usuari actualitzat');
      setOpenEdit(false);
      loadUsuaris();
    } catch (err: unknown) {
      toast.error(err instanceof Error ? err.message : "Error actualitzant l'usuari");
    } finally {
      setSavingEdit(false);
    }
  };

  const handleOpenDelete = (u: UsuariAdmin) => {
    setDeleteTarget(u);
    setOpenDelete(true);
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    setDeletingId(deleteTarget.uuid);
    setOpenDelete(false);
    try {
      await usuarisApi.deleteUsuari(deleteTarget.uuid);
      setUsuaris(prev => prev.filter(u => u.uuid !== deleteTarget.uuid));
      toast.success('Usuari eliminat');
    } catch (err: unknown) {
      toast.error(err instanceof Error ? err.message : "Error eliminant l'usuari");
    } finally {
      setDeletingId(null);
      setDeleteTarget(null);
    }
  };

  const formFieldSx = { spacing: 0.5 };

  return (
    <>
      <Box>
        <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{ mb: 2 }}>
          <TextField
            placeholder="Cerca per nom, email o departament..."
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
            onClick={handleOpenCreate}
            sx={{ textTransform: 'none', fontWeight: 700 }}
          >
            Afegir usuari
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
                <TableRow sx={{ bgcolor: 'background.default' }}>
                  <TableCell sx={{ fontWeight: 700 }}>Nom</TableCell>
                  <TableCell sx={{ fontWeight: 700 }}>Email</TableCell>
                  <TableCell sx={{ fontWeight: 700 }}>Departament</TableCell>
                  {isAdmin && <TableCell sx={{ fontWeight: 700 }}>Sucursal</TableCell>}
                  <TableCell sx={{ fontWeight: 700 }}>Rol</TableCell>
                  <TableCell align="right" sx={{ fontWeight: 700 }}>Accions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {visibleUsuaris.length === 0 ? (
                  <TableRow>
                    <TableCell colSpan={isAdmin ? 6 : 5} align="center" sx={{ py: 4, color: 'text.secondary' }}>
                      No s'han trobat usuaris
                    </TableCell>
                  </TableRow>
                ) : (
                  visibleUsuaris.map(u => {
                    const rolInfo = ROL_LABEL[u.rolIntern];
                    return (
                      <TableRow key={u.uuid} hover>
                        <TableCell>{u.nom}</TableCell>
                        <TableCell>{u.correu}</TableCell>
                        <TableCell>{u.departament?.departament ?? '—'}</TableCell>
                        {isAdmin && <TableCell>{u.sucursal?.nom ?? '—'}</TableCell>}
                        <TableCell>
                          <Chip
                            label={rolInfo?.label ?? u.rolIntern}
                            color={rolInfo?.color ?? 'default'}
                            size="small"
                            sx={{ fontWeight: 600 }}
                          />
                        </TableCell>
                        <TableCell align="right">
                          <Stack direction="row" spacing={0.5} justifyContent="flex-end">
                            <Tooltip title="Editar">
                              <IconButton size="small" onClick={() => handleOpenEdit(u)}>
                                <EditOutlinedIcon fontSize="small" />
                              </IconButton>
                            </Tooltip>
                            <Tooltip title="Eliminar">
                              <IconButton
                                size="small"
                                color="error"
                                onClick={() => handleOpenDelete(u)}
                                disabled={deletingId === u.uuid}
                              >
                                {deletingId === u.uuid
                                  ? <CircularProgress size={16} color="inherit" />
                                  : <DeleteOutlineIcon fontSize="small" />
                                }
                              </IconButton>
                            </Tooltip>
                          </Stack>
                        </TableCell>
                      </TableRow>
                    );
                  })
                )}
              </TableBody>
            </Table>
          </TableContainer>
        )}
      </Box>

      <Dialog open={openCreate} onClose={() => setOpenCreate(false)} maxWidth="sm" fullWidth>
        <DialogTitle sx={{ fontWeight: 700 }}>Nou usuari</DialogTitle>
        <Divider />
        <DialogContent>
          <Stack spacing={2} sx={{ pt: 1 }}>
            <Stack {...formFieldSx}>
              <FieldLabel>Nom *</FieldLabel>
              <TextField
                fullWidth
                value={createData.nom}
                onChange={e => setCreateData(p => ({ ...p, nom: e.target.value }))}
                placeholder="Ex: Joan Garcia"
              />
            </Stack>

            <Stack {...formFieldSx}>
              <FieldLabel>Email *</FieldLabel>
              <TextField
                fullWidth
                type="email"
                value={createData.correu}
                onChange={e => setCreateData(p => ({ ...p, correu: e.target.value }))}
                placeholder="Ex: joan@empresa.com"
              />
            </Stack>

            <Stack {...formFieldSx}>
              <FieldLabel>Contrasenya *</FieldLabel>
              <TextField
                fullWidth
                type="password"
                value={createData.contrasenya}
                onChange={e => setCreateData(p => ({ ...p, contrasenya: e.target.value }))}
                placeholder="Contrasenya"
              />
            </Stack>

            {isAdmin && (
              <>
                <Stack {...formFieldSx}>
                  <FieldLabel>Sucursal</FieldLabel>
                  <FormControl fullWidth>
                    <Select
                      value={createData.sucursalUuid}
                      onChange={e => setCreateData(p => ({ ...p, sucursalUuid: e.target.value }))}
                      displayEmpty
                    >
                      <MenuItem value=""><em>Cap</em></MenuItem>
                      {sucursals.map(s => (
                        <MenuItem key={s.uuid} value={s.uuid}>{s.nom}</MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                </Stack>

                <Stack {...formFieldSx}>
                  <FieldLabel>Departament</FieldLabel>
                  <FormControl fullWidth>
                    <Select
                      value={createData.departamentUuid}
                      onChange={e => setCreateData(p => ({ ...p, departamentUuid: e.target.value }))}
                      displayEmpty
                    >
                      <MenuItem value=""><em>Cap</em></MenuItem>
                      {departaments.map(d => (
                        <MenuItem key={d.uuid} value={d.uuid}>{d.nom}</MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                </Stack>
              </>
            )}

            <Stack {...formFieldSx}>
              <FieldLabel>Rol</FieldLabel>
              <FormControl fullWidth>
                <Select
                  value={createData.rolUuid}
                  onChange={e => setCreateData(p => ({ ...p, rolUuid: e.target.value }))}
                  displayEmpty
                >
                  <MenuItem value=""><em>Cap</em></MenuItem>
                  {rols.map(r => (
                    <MenuItem key={r.uuid} value={r.uuid}>{r.nom}</MenuItem>
                  ))}
                </Select>
              </FormControl>
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

      <Dialog open={openEdit} onClose={() => setOpenEdit(false)} maxWidth="sm" fullWidth>
        <DialogTitle sx={{ fontWeight: 700 }}>Editar usuari</DialogTitle>
        <Divider />
        <DialogContent>
          <Stack spacing={2} sx={{ pt: 1 }}>
            <Stack {...formFieldSx}>
              <FieldLabel>Nom</FieldLabel>
              <TextField
                fullWidth
                value={editData.nom ?? ''}
                onChange={e => setEditData(p => ({ ...p, nom: e.target.value }))}
                placeholder="Ex: Joan Garcia"
              />
            </Stack>

            <Stack {...formFieldSx}>
              <FieldLabel>Email</FieldLabel>
              <TextField
                fullWidth
                type="email"
                value={editData.correu ?? ''}
                onChange={e => setEditData(p => ({ ...p, correu: e.target.value }))}
                placeholder="Ex: joan@empresa.com"
              />
            </Stack>

            {isAdmin && (
              <>
                <Stack {...formFieldSx}>
                  <FieldLabel>Sucursal</FieldLabel>
                  <FormControl fullWidth>
                    <Select
                      value={editData.sucursalUuid ?? ''}
                      onChange={e => setEditData(p => ({ ...p, sucursalUuid: e.target.value }))}
                      displayEmpty
                    >
                      <MenuItem value=""><em>Cap</em></MenuItem>
                      {sucursals.map(s => (
                        <MenuItem key={s.uuid} value={s.uuid}>{s.nom}</MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                </Stack>

                <Stack {...formFieldSx}>
                  <FieldLabel>Departament</FieldLabel>
                  <FormControl fullWidth>
                    <Select
                      value={editData.departamentUuid ?? ''}
                      onChange={e => setEditData(p => ({ ...p, departamentUuid: e.target.value }))}
                      displayEmpty
                    >
                      <MenuItem value=""><em>Cap</em></MenuItem>
                      {departaments.map(d => (
                        <MenuItem key={d.uuid} value={d.uuid}>{d.nom}</MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                </Stack>
              </>
            )}

            <Stack {...formFieldSx}>
              <FieldLabel>Rol</FieldLabel>
              <FormControl fullWidth>
                <Select
                  value={editData.rolUuid ?? ''}
                  onChange={e => setEditData(p => ({ ...p, rolUuid: e.target.value }))}
                  displayEmpty
                >
                  <MenuItem value=""><em>Cap</em></MenuItem>
                  {rols.map(r => (
                    <MenuItem key={r.uuid} value={r.uuid}>{r.nom}</MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Stack>
          </Stack>
        </DialogContent>
        <Divider />
        <DialogActions sx={{ px: 3, py: 2 }}>
          <Button
            onClick={() => setOpenEdit(false)}
            variant="outlined"
            sx={{ textTransform: 'none', fontWeight: 600 }}
          >
            Cancel·lar
          </Button>
          <Button
            variant="contained"
            onClick={handleEdit}
            disabled={savingEdit}
            sx={{ textTransform: 'none', fontWeight: 600 }}
          >
            {savingEdit ? <CircularProgress size={18} color="inherit" /> : 'Guardar'}
          </Button>
        </DialogActions>
      </Dialog>

      <Dialog open={openDelete} onClose={() => setOpenDelete(false)} maxWidth="xs" fullWidth>
        <DialogTitle sx={{ fontWeight: 700 }}>Eliminar usuari</DialogTitle>
        <Divider />
        <DialogContent>
          <Typography sx={{ pt: 1 }}>
            Segur que vols eliminar <strong>{deleteTarget?.nom}</strong>? Aquesta acció no es pot desfer.
          </Typography>
        </DialogContent>
        <Divider />
        <DialogActions sx={{ px: 3, py: 2 }}>
          <Button
            onClick={() => setOpenDelete(false)}
            variant="outlined"
            sx={{ textTransform: 'none', fontWeight: 600 }}
          >
            Cancel·lar
          </Button>
          <Button
            variant="contained"
            color="error"
            onClick={handleDelete}
            sx={{ textTransform: 'none', fontWeight: 600 }}
          >
            Eliminar
          </Button>
        </DialogActions>
      </Dialog>
    </>
  );
}