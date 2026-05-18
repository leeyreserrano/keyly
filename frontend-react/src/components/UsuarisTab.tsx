import { useState, useEffect, useCallback, useRef } from 'react';
import { useTranslation } from 'react-i18next';
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
  Avatar,
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
import { configApi } from '../api/configapi';
import { dominiApi } from '../api/dominiapi';
import {
  deriveKey,
  generateKeyPair,
  encryptPrivateKey,
  bytesToBase64,
} from '../crypto/cryptoService';
import { API_BASE } from '../api/client';
import toast from 'react-hot-toast';

type RolType = 'error' | 'warning' | 'default';

const ROL_COLOR: Record<string, RolType> = {
  ADMIN: 'error',
  CAP: 'warning',
  USUARI: 'default',
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
  imatge?: string | null;
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
  rolIntern?: 'ADMIN' | 'CAP' | 'USUARI';
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

function UserAvatarCell({ usuari }: { usuari: UsuariAdmin }) {
  const [src, setSrc] = useState<string | null>(null);
  const objectUrlRef = useRef<string | null>(null);

  useEffect(() => {
    if (!usuari.imatge) return;
    let cancelled = false;

    const load = async () => {
      try {
        const token =
          localStorage.getItem('jwtToken') ||
          sessionStorage.getItem('jwtToken');

        const res = await fetch(
          `${API_BASE}/usuari/get/image/${usuari.uuid}`,
          { headers: token ? { Authorization: `Bearer ${token}` } : {} }
        );

        if (!res.ok) throw new Error(`HTTP ${res.status}`);

        const blob = await res.blob();
        if (cancelled) return;

        if (objectUrlRef.current) URL.revokeObjectURL(objectUrlRef.current);
        const url = URL.createObjectURL(blob);
        objectUrlRef.current = url;
        setSrc(url);
      } catch {
        if (!cancelled) setSrc(null);
      }
    };

    load();
    return () => { cancelled = true; };
  }, [usuari.uuid, usuari.imatge]);

  useEffect(() => {
    return () => {
      if (objectUrlRef.current) {
        URL.revokeObjectURL(objectUrlRef.current);
        objectUrlRef.current = null;
      }
    };
  }, []);

  return (
    <Avatar src={src ?? undefined} sx={{ width: 28, height: 28, fontSize: '0.75rem' }}>
      {!src ? usuari.nom.charAt(0).toUpperCase() : undefined}
    </Avatar>
  );
}

export default function UsuarisTab() {
  const { usuari } = useAuth();
  const { t } = useTranslation('config');
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
  const [domainError, setDomainError] = useState<string | null>(null);
  const [loadingConfig, setLoadingConfig] = useState(false);
  const [sucursalConfig, setSucursalConfig] = useState<{
    permetreTotsDominis: boolean;
    dominis: string[];
  } | null>(null);

  const [openEdit, setOpenEdit] = useState(false);
  const [editTarget, setEditTarget] = useState<UsuariAdmin | null>(null);
  const [editData, setEditData] = useState<UpdateUsuariData>({});
  const [savingEdit, setSavingEdit] = useState(false);

  const [openDelete, setOpenDelete] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState<UsuariAdmin | null>(null);
  const [deletingId, setDeletingId] = useState<string | null>(null);

  const deptsByCreate = departaments.filter(
    d => !createData.sucursalUuid || d.sucursal?.uuid === createData.sucursalUuid
  );

  const deptsByEdit = departaments.filter(
    d => !editData.sucursalUuid || d.sucursal?.uuid === editData.sucursalUuid
  );

  const loadUsuaris = useCallback(async () => {
    setLoading(true);
    try {
      const data = (await usuarisApi.fetchAll()) as unknown as UsuariAdmin[];
      setUsuaris(data);
    } catch {
      toast.error(t('users.load_error'));
    } finally {
      setLoading(false);
    }
  }, [t]);

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
      toast.error(t('users.form_load_error'));
    }
  }, [isAdmin, t]);

  useEffect(() => {
    loadUsuaris();
    loadSelectData();
  }, [loadUsuaris, loadSelectData]);

  const loadSucursalConfig = useCallback(async (sucursalUuid: string) => {
    if (!sucursalUuid) {
      setSucursalConfig(null);
      return;
    }
    setLoadingConfig(true);
    try {
      const [config, dominis] = await Promise.all([
        configApi.getBySucursal(sucursalUuid),
        dominiApi.fetchAll(),
      ]);
      setSucursalConfig({
        permetreTotsDominis: config?.permetreTotsDominis ?? true,
        dominis: (dominis ?? []).map(d => d.domini.toLowerCase()),
      });
    } catch {
      setSucursalConfig(null);
    } finally {
      setLoadingConfig(false);
    }
  }, []);

  const validateDomain = (email: string): boolean => {
    if (!sucursalConfig || sucursalConfig.permetreTotsDominis) return true;
    const parts = email.split('@');
    if (parts.length !== 2) return false;
    const domain = parts[1].toLowerCase();
    return sucursalConfig.dominis.some(d => d === domain || domain.endsWith(`.${d}`));
  };

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
      list = list.filter(
        u =>
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
    setSucursalConfig(null);
    setDomainError(null);
    setOpenCreate(true);
  };

  const handleCreate = async () => {
    if (!createData.nom || !createData.correu || !createData.contrasenya) {
      toast.error(t('users.create.error'));
      return;
    }
    if (!validateDomain(createData.correu)) {
      setDomainError(t('users.create.domain_error'));
      return;
    }
    setSavingCreate(true);
    try {
      const salt = generateSalt();
      const derivedKey = await deriveKey(createData.contrasenya, salt);
      const { publicKeyB64, privateKeyB64 } = await generateKeyPair();
      const encryptedPK = await encryptPrivateKey(privateKeyB64, derivedKey);

      await usuarisApi.createUsuari(
        {
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
        },
        isAdmin
      );

      toast.success(t('users.create.success'));
      setOpenCreate(false);
      loadUsuaris();
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : '';
      const isConflict = msg.includes('409') || msg.toLowerCase().includes('conflict');
      toast.error(isConflict ? t('users.create.error_conflict') : t('users.create.error'));
    } finally {
      setSavingCreate(false);
    }
  };

  const handleOpenEdit = (u: UsuariAdmin) => {
    setEditTarget(u);
    setEditData({
      nom: u.nom,
      correu: u.correu,
      rolIntern: u.rolIntern,
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
      if (editData.rolIntern !== editTarget.rolIntern) payload.rolIntern = editData.rolIntern;
      if (editData.rolUuid !== (editTarget.rol?.uuid ?? '')) payload.rolUuid = editData.rolUuid;
      if (editData.sucursalUuid !== (editTarget.sucursal?.uuid ?? '')) payload.sucursalUuid = editData.sucursalUuid;
      if (editData.departamentUuid !== (editTarget.departament?.uuid ?? '')) payload.departamentUuid = editData.departamentUuid;

      await usuarisApi.updateUsuariAdmin(editTarget.uuid, payload);
      toast.success(t('users.edit.success'));
      setOpenEdit(false);
      loadUsuaris();
    } catch {
      toast.error(t('users.edit.error'));
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
      toast.success(t('users.delete.success'));
    } catch {
      toast.error(t('users.delete.error'));
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
            placeholder={t('users.search')}
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
            {t('users.add')}
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
                  <TableCell sx={{ fontWeight: 700, width: 40 }} />
                  <TableCell sx={{ fontWeight: 700 }}>{t('users.col.name')}</TableCell>
                  <TableCell sx={{ fontWeight: 700 }}>{t('users.col.email')}</TableCell>
                  <TableCell sx={{ fontWeight: 700 }}>{t('users.col.department')}</TableCell>
                  {isAdmin && <TableCell sx={{ fontWeight: 700 }}>{t('users.col.branch')}</TableCell>}
                  <TableCell sx={{ fontWeight: 700 }}>{t('users.col.role')}</TableCell>
                  <TableCell align="right" sx={{ fontWeight: 700 }}>{t('users.col.actions')}</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {visibleUsuaris.length === 0 ? (
                  <TableRow>
                    <TableCell
                      colSpan={isAdmin ? 7 : 6}
                      align="center"
                      sx={{ py: 4, color: 'text.secondary' }}
                    >
                      {t('users.empty')}
                    </TableCell>
                  </TableRow>
                ) : (
                  visibleUsuaris.map(u => (
                    <TableRow key={u.uuid} hover>
                      <TableCell>
                        <UserAvatarCell usuari={u} />
                      </TableCell>
                      <TableCell>{u.nom}</TableCell>
                      <TableCell>{u.correu}</TableCell>
                      <TableCell>{u.departament?.departament ?? '—'}</TableCell>
                      {isAdmin && <TableCell>{u.sucursal?.nom ?? '—'}</TableCell>}
                      <TableCell>
                        <Chip
                          label={t(`users.role.${u.rolIntern.toLowerCase()}`)}
                          color={ROL_COLOR[u.rolIntern] ?? 'default'}
                          size="small"
                          sx={{ fontWeight: 600 }}
                        />
                      </TableCell>
                      <TableCell align="right">
                        <Stack direction="row" spacing={0.5} justifyContent="flex-end">
                          <Tooltip title={t('common.actions.edit')}>
                            <IconButton size="small" onClick={() => handleOpenEdit(u)}>
                              <EditOutlinedIcon fontSize="small" />
                            </IconButton>
                          </Tooltip>
                          <Tooltip title={t('common.actions.delete')}>
                            <IconButton
                              size="small"
                              sx={{
                                bgcolor: 'error.main',
                                color: 'white',
                                '&:hover': { bgcolor: 'error.dark' },
                              }}
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
                  ))
                )}
              </TableBody>
            </Table>
          </TableContainer>
        )}
      </Box>

      <Dialog open={openCreate} onClose={() => setOpenCreate(false)} maxWidth="sm" fullWidth>
        <DialogTitle sx={{ fontWeight: 700 }}>{t('users.create.title')}</DialogTitle>
        <Divider />
        <DialogContent>
          <Stack spacing={2} sx={{ pt: 1 }}>
            <Stack {...formFieldSx}>
              <FieldLabel>{t('users.field.name')}</FieldLabel>
              <TextField
                fullWidth
                value={createData.nom}
                onChange={e => setCreateData(p => ({ ...p, nom: e.target.value }))}
                placeholder={t('users.placeholder.name')}
              />
            </Stack>

            <Stack {...formFieldSx}>
              <FieldLabel>{t('users.field.email')}</FieldLabel>
              <TextField
                fullWidth
                type="email"
                value={createData.correu}
                onChange={e => {
                  setCreateData(p => ({ ...p, correu: e.target.value }));
                  if (domainError) setDomainError(null);
                }}
                onBlur={() => {
                  if (createData.correu && !validateDomain(createData.correu)) {
                    setDomainError(t('users.create.domain_error'));
                  } else {
                    setDomainError(null);
                  }
                }}
                placeholder={t('users.placeholder.email')}
                error={!!domainError}
                helperText={
                  domainError ??
                  (sucursalConfig && !sucursalConfig.permetreTotsDominis
                    ? t('users.create.domain_hint', {
                      domains: sucursalConfig.dominis.join(', '),
                    })
                    : undefined)
                }
              />
            </Stack>

            <Stack {...formFieldSx}>
              <FieldLabel>{t('users.field.password')}</FieldLabel>
              <TextField
                fullWidth
                type="password"
                value={createData.contrasenya}
                onChange={e => setCreateData(p => ({ ...p, contrasenya: e.target.value }))}
                placeholder={t('users.placeholder.password')}
              />
            </Stack>

            <Stack {...formFieldSx}>
              <FieldLabel>{t('users.field.role_intern')}</FieldLabel>
              <FormControl fullWidth>
                <Select
                  value={createData.rolIntern}
                  onChange={e =>
                    setCreateData(p => ({
                      ...p,
                      rolIntern: e.target.value as 'ADMIN' | 'CAP' | 'USUARI',
                    }))
                  }
                >
                  <MenuItem value="ADMIN">{t('users.role.admin')}</MenuItem>
                  <MenuItem value="CAP">{t('users.role.cap')}</MenuItem>
                  <MenuItem value="USUARI">{t('users.role.usuari')}</MenuItem>
                </Select>
              </FormControl>
            </Stack>

            {isAdmin && (
              <>
                <Stack {...formFieldSx}>
                  <FieldLabel>{t('users.field.branch')}</FieldLabel>
                  <FormControl fullWidth>
                    <Select
                      value={createData.sucursalUuid}
                      onChange={e => {
                        const uuid = e.target.value;
                        setCreateData(p => ({ ...p, sucursalUuid: uuid, departamentUuid: '' }));
                        setDomainError(null);
                        loadSucursalConfig(uuid);
                      }}
                      displayEmpty
                    >
                      <MenuItem value=""><em>{t('common.none')}</em></MenuItem>
                      {sucursals.map(s => (
                        <MenuItem key={s.uuid} value={s.uuid}>{s.nom}</MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                </Stack>

                <Stack {...formFieldSx}>
                  <FieldLabel>{t('users.field.department')}</FieldLabel>
                  <FormControl fullWidth>
                    <Select
                      value={createData.departamentUuid}
                      onChange={e => setCreateData(p => ({ ...p, departamentUuid: e.target.value }))}
                      displayEmpty
                      disabled={!createData.sucursalUuid}
                    >
                      <MenuItem value=""><em>{t('common.none')}</em></MenuItem>
                      {deptsByCreate.map(d => (
                        <MenuItem key={d.uuid} value={d.uuid}>{d.nom}</MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                </Stack>
              </>
            )}

            <Stack {...formFieldSx}>
              <FieldLabel>{t('users.field.role')}</FieldLabel>
              <FormControl fullWidth>
                <Select
                  value={createData.rolUuid}
                  onChange={e => setCreateData(p => ({ ...p, rolUuid: e.target.value }))}
                  displayEmpty
                >
                  <MenuItem value=""><em>{t('common.none')}</em></MenuItem>
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
            {t('common.cancel')}
          </Button>
          <Button
            variant="contained"
            onClick={handleCreate}
            disabled={savingCreate || !!domainError || loadingConfig}
            sx={{ textTransform: 'none', fontWeight: 600 }}
          >
            {savingCreate
              ? <CircularProgress size={18} color="inherit" />
              : t('common.create')
            }
          </Button>
        </DialogActions>
      </Dialog>

      <Dialog open={openEdit} onClose={() => setOpenEdit(false)} maxWidth="sm" fullWidth>
        <DialogTitle sx={{ fontWeight: 700 }}>{t('users.edit.title')}</DialogTitle>
        <Divider />
        <DialogContent>
          <Stack spacing={2} sx={{ pt: 1 }}>
            <Stack {...formFieldSx}>
              <FieldLabel>{t('users.field.name_edit')}</FieldLabel>
              <TextField
                fullWidth
                value={editData.nom ?? ''}
                onChange={e => setEditData(p => ({ ...p, nom: e.target.value }))}
                placeholder={t('users.placeholder.name')}
              />
            </Stack>

            <Stack {...formFieldSx}>
              <FieldLabel>{t('users.field.email_edit')}</FieldLabel>
              <TextField
                fullWidth
                type="email"
                value={editData.correu ?? ''}
                onChange={e => setEditData(p => ({ ...p, correu: e.target.value }))}
                placeholder={t('users.placeholder.email')}
              />
            </Stack>

            <Stack {...formFieldSx}>
              <FieldLabel>{t('users.field.role_intern')}</FieldLabel>
              <FormControl fullWidth>
                <Select
                  value={editData.rolIntern ?? 'USUARI'}
                  onChange={e =>
                    setEditData(p => ({
                      ...p,
                      rolIntern: e.target.value as 'ADMIN' | 'CAP' | 'USUARI',
                    }))
                  }
                >
                  <MenuItem value="ADMIN">{t('users.role.admin')}</MenuItem>
                  <MenuItem value="CAP">{t('users.role.cap')}</MenuItem>
                  <MenuItem value="USUARI">{t('users.role.usuari')}</MenuItem>
                </Select>
              </FormControl>
            </Stack>

            {isAdmin && (
              <>
                <Stack {...formFieldSx}>
                  <FieldLabel>{t('users.field.branch')}</FieldLabel>
                  <FormControl fullWidth>
                    <Select
                      value={editData.sucursalUuid ?? ''}
                      onChange={e =>
                        setEditData(p => ({
                          ...p,
                          sucursalUuid: e.target.value,
                          departamentUuid: '',
                        }))
                      }
                      displayEmpty
                    >
                      <MenuItem value=""><em>{t('common.none')}</em></MenuItem>
                      {sucursals.map(s => (
                        <MenuItem key={s.uuid} value={s.uuid}>{s.nom}</MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                </Stack>

                <Stack {...formFieldSx}>
                  <FieldLabel>{t('users.field.department')}</FieldLabel>
                  <FormControl fullWidth>
                    <Select
                      value={editData.departamentUuid ?? ''}
                      onChange={e => setEditData(p => ({ ...p, departamentUuid: e.target.value }))}
                      displayEmpty
                      disabled={!editData.sucursalUuid}
                    >
                      <MenuItem value=""><em>{t('common.none')}</em></MenuItem>
                      {deptsByEdit.map(d => (
                        <MenuItem key={d.uuid} value={d.uuid}>{d.nom}</MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                </Stack>
              </>
            )}

            <Stack {...formFieldSx}>
              <FieldLabel>{t('users.field.role')}</FieldLabel>
              <FormControl fullWidth>
                <Select
                  value={editData.rolUuid ?? ''}
                  onChange={e => setEditData(p => ({ ...p, rolUuid: e.target.value }))}
                  displayEmpty
                >
                  <MenuItem value=""><em>{t('common.none')}</em></MenuItem>
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
            {t('common.cancel')}
          </Button>
          <Button
            variant="contained"
            onClick={handleEdit}
            disabled={savingEdit}
            sx={{ textTransform: 'none', fontWeight: 600 }}
          >
            {savingEdit
              ? <CircularProgress size={18} color="inherit" />
              : t('common.save')
            }
          </Button>
        </DialogActions>
      </Dialog>

      <Dialog open={openDelete} onClose={() => setOpenDelete(false)} maxWidth="xs" fullWidth>
        <DialogTitle sx={{ fontWeight: 700 }}>{t('users.delete.title')}</DialogTitle>
        <Divider />
        <DialogContent>
          <Typography
            sx={{ pt: 1 }}
            dangerouslySetInnerHTML={{
              __html: t('users.delete.confirm', { nom: deleteTarget?.nom ?? '' }),
            }}
          />
        </DialogContent>
        <Divider />
        <DialogActions sx={{ px: 3, py: 2 }}>
          <Button
            onClick={() => setOpenDelete(false)}
            variant="outlined"
            sx={{ textTransform: 'none', fontWeight: 600 }}
          >
            {t('common.cancel')}
          </Button>
          <Button
            variant="contained"
            color="error"
            onClick={handleDelete}
            sx={{ textTransform: 'none', fontWeight: 600 }}
          >
            {t('common.delete')}
          </Button>
        </DialogActions>
      </Dialog>
    </>
  );
}