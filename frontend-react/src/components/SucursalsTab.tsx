import { useEffect, useState, type ReactNode } from 'react';
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
  IconButton,
  CircularProgress,
  Tooltip,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Divider,
  Switch,
  FormControlLabel,
  Checkbox,
  FormGroup,
  Alert,
} from '@mui/material';
import AddOutlinedIcon from '@mui/icons-material/AddOutlined';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import EditOutlinedIcon from '@mui/icons-material/EditOutlined';
import toast from 'react-hot-toast';
import { sucursalsApi, type Sucursal, type UpdateSucursal } from '../api/sucursalsapi';
import { configApi, type Config } from '../api/configapi';
import { dominiApi, type Domini } from '../api/dominiapi';

type SucursalWithConfig = Sucursal & {
  config: Config | null;
  loadingConfig: boolean;
};

type CreateSucursal = {
  nom: string;
  direccio: string;
  ciutat: string;
  pais: string;
  telefon: string;
  correu: string;
};

type EditSucursalData = UpdateSucursal & {
  permetreTotsDominis: boolean;
  diesExpiracio: number;
};

const EMPTY_CREATE: CreateSucursal = {
  nom: '',
  direccio: '',
  ciutat: '',
  pais: '',
  telefon: '',
  correu: '',
};

function FieldLabel({ children }: { children: ReactNode }) {
  return (
    <Typography sx={{ fontSize: '0.8rem', fontWeight: 600, color: 'text.secondary' }}>
      {children}
    </Typography>
  );
}

export default function SucursalsTab() {
  const { t } = useTranslation('config');

  const [data, setData] = useState<SucursalWithConfig[]>([]);
  const [loading, setLoading] = useState(false);

  const [openCreate, setOpenCreate] = useState(false);
  const [createData, setCreateData] = useState<CreateSucursal>(EMPTY_CREATE);
  const [savingCreate, setSavingCreate] = useState(false);

  const [openEdit, setOpenEdit] = useState(false);
  const [editTarget, setEditTarget] = useState<SucursalWithConfig | null>(null);
  const [editData, setEditData] = useState<EditSucursalData>({
    nom: '', direccio: '', ciutat: '', pais: '', telefon: '', correu: '',
    permetreTotsDominis: true, diesExpiracio: 0,
  });
  const [savingEdit, setSavingEdit] = useState(false);

  const [allDominis, setAllDominis] = useState<Domini[]>([]);
  const [sucursalDominis, setSucursalDominis] = useState<string[]>([]);
  const [loadingDominis, setLoadingDominis] = useState(false);

  const [deletingId, setDeletingId] = useState<string | null>(null);
  const [togglingId, setTogglingId] = useState<string | null>(null);

  const load = async () => {
    setLoading(true);
    try {
      const sucursals = await sucursalsApi.fetchAll();
      const withConfig: SucursalWithConfig[] = sucursals.map(s => ({
        ...s,
        config: null,
        loadingConfig: true,
      }));
      setData(withConfig);

      const configs = await Promise.allSettled(
        sucursals.map(s => configApi.getBySucursal(s.uuid))
      );

      setData(sucursals.map((s, i) => ({
        ...s,
        config: configs[i].status === 'fulfilled' ? configs[i].value : null,
        loadingConfig: false,
      })));
    } catch {
      toast.error(t('branches.load_error'));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  const handleToggleDominis = async (s: SucursalWithConfig) => {
    if (!s.config) return;
    setTogglingId(s.uuid);
    const newValue = !s.config.permetreTotsDominis;
    try {
      await configApi.updateBySucursal(s.uuid, {
        permetreTotsDominis: newValue,
        diesExpiracio: s.config.diesExpiracio,
      });
      setData(prev =>
        prev.map(item =>
          item.uuid === s.uuid
            ? { ...item, config: { ...item.config!, permetreTotsDominis: newValue } }
            : item
        )
      );
      toast.success(t('branches.domains_updated'));
    } catch {
      toast.error(t('branches.domains_error'));
    } finally {
      setTogglingId(null);
    }
  };

  const handleOpenEdit = async (s: SucursalWithConfig) => {
    setEditTarget(s);
    setEditData({
      nom: s.nom,
      direccio: s.direccio ?? '',
      ciutat: s.ciutat ?? '',
      pais: s.pais ?? '',
      telefon: s.telefon ?? '',
      correu: s.correu ?? '',
      permetreTotsDominis: s.config?.permetreTotsDominis ?? true,
      diesExpiracio: s.config?.diesExpiracio ?? 0,
    });
    setOpenEdit(true);

    setLoadingDominis(true);
    try {
      const tots = await dominiApi.fetchAll() ?? [];
      const deSucursal = tots.filter(d => d.sucursal?.uuid === s.uuid);
      setAllDominis(tots);
      setSucursalDominis(deSucursal.map(d => d.uuid));
    } catch {
      setAllDominis([]);
      setSucursalDominis([]);
    } finally {
      setLoadingDominis(false);
    }
  };

  const handleEdit = async () => {
    if (!editTarget) return;
    setSavingEdit(true);
    try {
      await sucursalsApi.update(editTarget.uuid, {
        nom: editData.nom,
        direccio: editData.direccio,
        ciutat: editData.ciutat,
        pais: editData.pais,
        telefon: editData.telefon,
        correu: editData.correu,
      });

      if (editTarget.config) {
        await configApi.updateBySucursal(editTarget.uuid, {
          permetreTotsDominis: editData.permetreTotsDominis,
          diesExpiracio: editData.diesExpiracio,
        });
      }

      toast.success(t('branches.edit.success'));
      setOpenEdit(false);
      load();
    } catch {
      toast.error(t('branches.edit.error'));
    } finally {
      setSavingEdit(false);
    }
  };

  const handleCreate = async () => {
    if (!createData.nom) {
      toast.error(t('branches.create.name_required'));
      return;
    }
    setSavingCreate(true);
    try {
      await sucursalsApi.add(createData);
      setCreateData(EMPTY_CREATE);
      setOpenCreate(false);
      load();
      toast.success(t('branches.create.success'));
    } catch {
      toast.error(t('branches.create.error'));
    } finally {
      setSavingCreate(false);
    }
  };

  const handleDelete = async (id: string) => {
    setDeletingId(id);
    try {
      await sucursalsApi.delete(id);
      setData(prev => prev.filter(s => s.uuid !== id));
      toast.success(t('branches.delete.success'));
    } catch {
      toast.error(t('branches.delete.error'));
    } finally {
      setDeletingId(null);
    }
  };

  const dominisPerSucursal = allDominis.filter(
    d => (d as any).sucursalUuid === editTarget?.uuid || (d as any).sucursal?.uuid === editTarget?.uuid
  );

  const handleToggleDominiSeleccio = (uuid: string, checked: boolean) => {
    setSucursalDominis(prev =>
      checked ? [...prev, uuid] : prev.filter(id => id !== uuid)
    );
  };

  return (
    <>
      <Box>
        <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{ mb: 2 }}>
          <Typography sx={{ fontWeight: 700 }}>{t('tabs.branches')}</Typography>
          <Button
            variant="contained"
            startIcon={<AddOutlinedIcon />}
            onClick={() => setOpenCreate(true)}
            sx={{ textTransform: 'none', fontWeight: 700 }}
          >
            {t('branches.add')}
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
                  <TableCell sx={{ fontWeight: 700 }}>{t('branches.col.name')}</TableCell>
                  <TableCell sx={{ fontWeight: 700 }}>{t('branches.col.city')}</TableCell>
                  <TableCell sx={{ fontWeight: 700 }}>{t('branches.col.country')}</TableCell>
                  <TableCell sx={{ fontWeight: 700 }}>{t('branches.col.phone')}</TableCell>
                  <TableCell sx={{ fontWeight: 700 }}>{t('branches.col.email')}</TableCell>
                  <TableCell sx={{ fontWeight: 700 }}>{t('branches.col.all_domains')}</TableCell>
                  <TableCell sx={{ fontWeight: 700 }}>{t('branches.col.expiry_days')}</TableCell>
                  <TableCell align="right" sx={{ fontWeight: 700 }}>{t('branches.col.actions')}</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {data.length === 0 ? (
                  <TableRow>
                    <TableCell colSpan={8} align="center" sx={{ py: 4, color: 'text.secondary' }}>
                      {t('branches.empty')}
                    </TableCell>
                  </TableRow>
                ) : (
                  data.map(s => (
                    <TableRow key={s.uuid} hover>
                      <TableCell>{s.nom}</TableCell>
                      <TableCell>{s.ciutat ?? '—'}</TableCell>
                      <TableCell>{s.pais ?? '—'}</TableCell>
                      <TableCell>{s.telefon ?? '—'}</TableCell>
                      <TableCell>{s.correu ?? '—'}</TableCell>
                      <TableCell>
                        {s.loadingConfig ? (
                          <CircularProgress size={16} />
                        ) : (
                          <Tooltip title={s.config?.permetreTotsDominis
                            ? t('branches.domains.disable')
                            : t('branches.domains.enable')
                          }>
                            <Switch
                              size="small"
                              checked={s.config?.permetreTotsDominis ?? false}
                              disabled={togglingId === s.uuid || !s.config}
                              onChange={() => handleToggleDominis(s)}
                            />
                          </Tooltip>
                        )}
                      </TableCell>
                      <TableCell>
                        {s.loadingConfig ? (
                          <CircularProgress size={16} />
                        ) : (
                          s.config?.diesExpiracio != null
                            ? `${s.config.diesExpiracio}d`
                            : '—'
                        )}
                      </TableCell>
                      <TableCell align="right">
                        <Stack direction="row" spacing={0.5} justifyContent="flex-end">
                          <Tooltip title={t('common.actions.edit')}>
                            <IconButton size="small" onClick={() => handleOpenEdit(s)}>
                              <EditOutlinedIcon fontSize="small" />
                            </IconButton>
                          </Tooltip>
                          <Tooltip title={t('common.actions.delete')}>
                            <IconButton
                              sx={{
                                bgcolor: 'error.main',
                                color: 'white',
                                '&:hover': { bgcolor: 'error.dark' },
                              }}
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
        <DialogTitle sx={{ fontWeight: 700 }}>{t('branches.create.title')}</DialogTitle>
        <Divider />
        <DialogContent>
          <Stack spacing={2} sx={{ pt: 1 }}>
            {(['nom', 'direccio', 'ciutat', 'pais', 'telefon', 'correu'] as const).map(field => (
              <Stack key={field} spacing={0.5}>
                <FieldLabel>
                  {t(`branches.field.${field}`)}
                  {field === 'nom' ? ' *' : ''}
                </FieldLabel>
                <TextField
                  fullWidth
                  value={createData[field]}
                  onChange={e => setCreateData(p => ({ ...p, [field]: e.target.value }))}
                />
              </Stack>
            ))}
          </Stack>
        </DialogContent>
        <Divider />
        <DialogActions sx={{ px: 3, py: 2 }}>
          <Button onClick={() => setOpenCreate(false)} variant="outlined" sx={{ textTransform: 'none', fontWeight: 600 }}>
            {t('common.cancel')}
          </Button>
          <Button variant="contained" onClick={handleCreate} disabled={savingCreate} sx={{ textTransform: 'none', fontWeight: 600 }}>
            {savingCreate ? <CircularProgress size={18} color="inherit" /> : t('common.create')}
          </Button>
        </DialogActions>
      </Dialog>

      <Dialog open={openEdit} onClose={() => setOpenEdit(false)} maxWidth="sm" fullWidth>
        <DialogTitle sx={{ fontWeight: 700 }}>{t('branches.edit.title')}</DialogTitle>
        <Divider />
        <DialogContent>
          <Stack spacing={2} sx={{ pt: 1 }}>
            {(['nom', 'direccio', 'ciutat', 'pais', 'telefon', 'correu'] as const).map(field => (
              <Stack key={field} spacing={0.5}>
                <FieldLabel>{t(`branches.field.${field}`)}</FieldLabel>
                <TextField
                  fullWidth
                  value={editData[field]}
                  onChange={e => setEditData(p => ({ ...p, [field]: e.target.value }))}
                />
              </Stack>
            ))}

            <Divider />

            <FormControlLabel
              control={
                <Switch
                  checked={editData.permetreTotsDominis}
                  onChange={e => setEditData(p => ({ ...p, permetreTotsDominis: e.target.checked }))}
                />
              }
              label={
                <Stack spacing={0}>
                  <Typography sx={{ fontWeight: 600, fontSize: '0.9rem' }}>
                    {t('branches.field.all_domains')}
                  </Typography>
                  <Typography sx={{ fontSize: '0.75rem', color: 'text.secondary' }}>
                    {t('branches.field.all_domains_hint')}
                  </Typography>
                </Stack>
              }
            />

            {!editData.permetreTotsDominis && (
              <Stack spacing={1}>
                <FieldLabel>{t('branches.field.allowed_domains')}</FieldLabel>
                {loadingDominis ? (
                  <Box sx={{ display: 'flex', justifyContent: 'center', py: 2 }}>
                    <CircularProgress size={20} />
                  </Box>
                ) : dominisPerSucursal.length === 0 ? (
                  <Alert severity="info" sx={{ fontSize: '0.8rem' }}>
                    {t('branches.field.no_domains_available')}
                  </Alert>
                ) : (
                  <Paper variant="outlined" sx={{ px: 2, py: 1, borderRadius: 1.5, maxHeight: 180, overflowY: 'auto' }}>
                    <FormGroup>
                      {dominisPerSucursal.map(d => (
                        <FormControlLabel
                          key={d.uuid}
                          control={
                            <Checkbox
                              size="small"
                              checked={sucursalDominis.includes(d.uuid)}
                              onChange={e => handleToggleDominiSeleccio(d.uuid, e.target.checked)}
                            />
                          }
                          label={<Typography sx={{ fontSize: '0.85rem' }}>{d.domini}</Typography>}
                        />
                      ))}
                    </FormGroup>
                  </Paper>
                )}
              </Stack>
            )}

            <Stack spacing={0.5}>
              <FieldLabel>{t('branches.field.expiry_days')}</FieldLabel>
              <TextField
                fullWidth
                type="number"
                inputProps={{ min: 0 }}
                value={editData.diesExpiracio}
                onChange={e => setEditData(p => ({ ...p, diesExpiracio: Math.max(0, Number(e.target.value)) }))}
                helperText={t('branches.field.expiry_days_hint')}
              />
            </Stack>
          </Stack>
        </DialogContent>
        <Divider />
        <DialogActions sx={{ px: 3, py: 2 }}>
          <Button onClick={() => setOpenEdit(false)} variant="outlined" sx={{ textTransform: 'none', fontWeight: 600 }}>
            {t('common.cancel')}
          </Button>
          <Button variant="contained" onClick={handleEdit} disabled={savingEdit} sx={{ textTransform: 'none', fontWeight: 600 }}>
            {savingEdit ? <CircularProgress size={18} color="inherit" /> : t('common.save')}
          </Button>
        </DialogActions>
      </Dialog>
    </>
  );
}