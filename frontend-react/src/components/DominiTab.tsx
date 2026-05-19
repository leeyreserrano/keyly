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
  InputAdornment,
  FormControl,
  Select,
  MenuItem,
} from '@mui/material';
import AddOutlinedIcon from '@mui/icons-material/AddOutlined';
import EditOutlinedIcon from '@mui/icons-material/EditOutlined';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import SearchIcon from '@mui/icons-material/Search';
import toast from 'react-hot-toast';
import { dominiApi, type Domini } from '../api/dominiapi';
import { sucursalsApi, type Sucursal } from '../api/sucursalsapi';

type DominiGroup = {
  domini: string;
  registres: Domini[];
  selectedUuid: string;
};

function FieldLabel({ children }: { children: ReactNode }) {
  return (
    <Typography sx={{ fontSize: '0.8rem', fontWeight: 600, color: 'text.secondary' }}>
      {children}
    </Typography>
  );
}

function buildGroups(data: Domini[]): DominiGroup[] {
  return data.map((d) => ({
    domini: d.domini,
    registres: [d],
    selectedUuid: d.uuid,
  }));
}

export default function DominiTab() {
  const { t } = useTranslation('config');

  const [data, setData] = useState<Domini[]>([]);
  const [sucursals, setSucursals] = useState<Sucursal[]>([]);
  const [groups, setGroups] = useState<DominiGroup[]>([]);
  const [loading, setLoading] = useState(false);
  const [search, setSearch] = useState('');

  const [openCreate, setOpenCreate] = useState(false);
  const [createValue, setCreateValue] = useState('');
  const [createSucursalUuid, setCreateSucursalUuid] = useState('');
  const [savingCreate, setSavingCreate] = useState(false);

  const [openEdit, setOpenEdit] = useState(false);
  const [editTarget, setEditTarget] = useState<Domini | null>(null);
  const [editValue, setEditValue] = useState('');
  const [editSucursalUuid, setEditSucursalUuid] = useState('');
  const [savingEdit, setSavingEdit] = useState(false);

  const [openDelete, setOpenDelete] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState<Domini | null>(null);
  const [deletingId, setDeletingId] = useState<string | null>(null);

  const load = async () => {
    setLoading(true);
    try {
      const [dominis, sucursalsList] = await Promise.all([
        dominiApi.fetchAll(),
        sucursalsApi.fetchAll(),
      ]);
      const d = dominis ?? [];
      setData(d);
      setSucursals(sucursalsList ?? []);
      setGroups(buildGroups(d));
    } catch {
      toast.error(t('domains.load_error'));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  const visible = groups.filter(g =>
    g.domini.toLowerCase().includes(search.toLowerCase()) ||
    g.registres.some(r => (r.sucursal?.nom ?? '').toLowerCase().includes(search.toLowerCase()))
  );

  const getSelected = (g: DominiGroup): Domini => g.registres[0];

  const handleOpenCreate = () => {
    setCreateValue('');
    setCreateSucursalUuid(sucursals[0]?.uuid ?? '');
    setOpenCreate(true);
  };

  const handleCreate = async () => {
    if (!createValue.trim()) {
      toast.error(t('domains.field.domain'));
      return;
    }
    if (!createSucursalUuid) {
      toast.error(t('domains.field.branch_required'));
      return;
    }
    setSavingCreate(true);
    try {
      await dominiApi.add({ domini: createValue.trim(), sucursalUuid: createSucursalUuid });
      toast.success(t('domains.create.success'));
      setOpenCreate(false);
      load();
    } catch (err) {
      const msg = err instanceof Error ? err.message : '';
      if (msg.includes('409') || msg.toLowerCase().includes('conflict')) {
        toast.error(t('domains.create.error_conflict'));
      } else {
        toast.error(t('domains.create.error'));
      }
    } finally {
      setSavingCreate(false);
    }
  };

  const handleOpenEdit = (g: DominiGroup) => {
    const selected = getSelected(g);
    setEditTarget(selected);
    setEditValue(selected.domini);
    setEditSucursalUuid(selected.sucursal?.uuid ?? '');
    setOpenEdit(true);
  };

  const handleEdit = async () => {
    if (!editTarget || !editValue.trim()) {
      toast.error(t('domains.field.domain'));
      return;
    }
    if (!editSucursalUuid) {
      toast.error(t('domains.field.branch_required'));
      return;
    }
    setSavingEdit(true);
    try {
      await dominiApi.update(editTarget.uuid, {
        domini: editValue.trim(),
        sucursalUuid: editSucursalUuid,
      });
      toast.success(t('domains.edit.success'));
      setOpenEdit(false);
      load();
    } catch (err) {
      const msg = err instanceof Error ? err.message : '';
      if (msg.includes('409') || msg.toLowerCase().includes('conflict')) {
        toast.error(t('domains.edit.error_conflict'));
      } else {
        toast.error(t('domains.edit.error'));
      }
    } finally {
      setSavingEdit(false);
    }
  };

  const handleOpenDelete = (g: DominiGroup) => {
    setDeleteTarget(getSelected(g));
    setOpenDelete(true);
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    setDeletingId(deleteTarget.uuid);
    setOpenDelete(false);
    try {
      await dominiApi.delete(deleteTarget.uuid);
      const newData = data.filter(d => d.uuid !== deleteTarget.uuid);
      setData(newData);
      setGroups(buildGroups(newData));
      toast.success(t('domains.delete.success'));
    } catch {
      toast.error(t('domains.delete.error'));
    } finally {
      setDeletingId(null);
      setDeleteTarget(null);
    }
  };

  return (
    <>
      <Box>
        <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{ mb: 2 }}>
          <TextField
            placeholder={t('domains.search')}
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
            {t('domains.add')}
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
                  <TableCell sx={{ fontWeight: 700 }}>{t('domains.col.domain')}</TableCell>
                  <TableCell sx={{ fontWeight: 700 }}>{t('domains.col.branch')}</TableCell>
                  <TableCell align="right" sx={{ fontWeight: 700 }}>{t('domains.col.actions')}</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {visible.length === 0 ? (
                  <TableRow>
                    <TableCell colSpan={3} align="center" sx={{ py: 4, color: 'text.secondary' }}>
                      {t('domains.empty')}
                    </TableCell>
                  </TableRow>
                ) : (
                  visible.map(g => (
                    <TableRow key={g.selectedUuid} hover>
                      <TableCell>{g.domini}</TableCell>
                      <TableCell>
                        <Typography sx={{ fontSize: '0.85rem' }}>
                          {g.registres[0].sucursal?.nom ?? '—'}
                        </Typography>
                      </TableCell>
                      <TableCell align="right">
                        <Stack direction="row" spacing={0.5} justifyContent="flex-end">
                          <Tooltip title={t('common.actions.edit')}>
                            <IconButton size="small" onClick={() => handleOpenEdit(g)}>
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
                              onClick={() => handleOpenDelete(g)}
                              disabled={deletingId === getSelected(g).uuid}
                            >
                              {deletingId === getSelected(g).uuid
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

      <Dialog open={openCreate} onClose={() => setOpenCreate(false)} maxWidth="xs" fullWidth>
        <DialogTitle sx={{ fontWeight: 700 }}>{t('domains.create.title')}</DialogTitle>
        <Divider />
        <DialogContent>
          <Stack spacing={2} sx={{ pt: 1 }}>
            <Stack spacing={0.5}>
              <FieldLabel>{t('domains.field.domain')} *</FieldLabel>
              <TextField
                fullWidth
                placeholder={t('domains.placeholder.domain')}
                value={createValue}
                onChange={e => setCreateValue(e.target.value)}
                onKeyDown={e => { if (e.key === 'Enter') handleCreate(); }}
              />
            </Stack>
            <Stack spacing={0.5}>
              <FieldLabel>{t('domains.field.branch')} *</FieldLabel>
              <FormControl fullWidth size="small">
                <Select
                  value={createSucursalUuid}
                  onChange={e => setCreateSucursalUuid(e.target.value)}
                  displayEmpty
                >
                  {sucursals.map(s => (
                    <MenuItem key={s.uuid} value={s.uuid}>{s.nom}</MenuItem>
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
            disabled={savingCreate}
            sx={{ textTransform: 'none', fontWeight: 600 }}
          >
            {savingCreate ? <CircularProgress size={18} color="inherit" /> : t('common.create')}
          </Button>
        </DialogActions>
      </Dialog>

      <Dialog open={openEdit} onClose={() => setOpenEdit(false)} maxWidth="xs" fullWidth>
        <DialogTitle sx={{ fontWeight: 700 }}>{t('domains.edit.title')}</DialogTitle>
        <Divider />
        <DialogContent>
          <Stack spacing={2} sx={{ pt: 1 }}>
            <Stack spacing={0.5}>
              <FieldLabel>{t('domains.field.domain')}</FieldLabel>
              <TextField
                fullWidth
                value={editValue}
                onChange={e => setEditValue(e.target.value)}
                onKeyDown={e => { if (e.key === 'Enter') handleEdit(); }}
              />
            </Stack>
            <Stack spacing={0.5}>
              <FieldLabel>{t('domains.field.branch')} *</FieldLabel>
              <FormControl fullWidth size="small">
                <Select
                  value={editSucursalUuid}
                  onChange={e => setEditSucursalUuid(e.target.value)}
                >
                  {sucursals.map(s => (
                    <MenuItem key={s.uuid} value={s.uuid}>{s.nom}</MenuItem>
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
            {savingEdit ? <CircularProgress size={18} color="inherit" /> : t('common.save')}
          </Button>
        </DialogActions>
      </Dialog>

      <Dialog open={openDelete} onClose={() => setOpenDelete(false)} maxWidth="xs" fullWidth>
        <DialogTitle sx={{ fontWeight: 700 }}>{t('domains.delete.title')}</DialogTitle>
        <Divider />
        <DialogContent>
          <Typography
            sx={{ pt: 1 }}
            dangerouslySetInnerHTML={{
              __html: t('domains.delete.confirm', { domini: deleteTarget?.domini ?? '' }),
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