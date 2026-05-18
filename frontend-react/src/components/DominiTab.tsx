import { useEffect, useState } from 'react';
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
} from '@mui/material';
import AddOutlinedIcon from '@mui/icons-material/AddOutlined';
import EditOutlinedIcon from '@mui/icons-material/EditOutlined';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import SearchIcon from '@mui/icons-material/Search';
import toast from 'react-hot-toast';
import { dominiApi, type Domini } from '../api/dominiapi';

function FieldLabel({ children }: { children: string }) {
  return (
    <Typography sx={{ fontSize: '0.8rem', fontWeight: 600, color: 'text.secondary' }}>
      {children}
    </Typography>
  );
}

export default function DominiTab() {
  const { t } = useTranslation('config');

  const [data, setData] = useState<Domini[]>([]);
  const [loading, setLoading] = useState(false);
  const [search, setSearch] = useState('');

  const [openCreate, setOpenCreate] = useState(false);
  const [createValue, setCreateValue] = useState('');
  const [savingCreate, setSavingCreate] = useState(false);

  const [openEdit, setOpenEdit] = useState(false);
  const [editTarget, setEditTarget] = useState<Domini | null>(null);
  const [editValue, setEditValue] = useState('');
  const [savingEdit, setSavingEdit] = useState(false);

  const [openDelete, setOpenDelete] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState<Domini | null>(null);
  const [deletingId, setDeletingId] = useState<string | null>(null);

  const load = async () => {
    setLoading(true);
    try {
      setData((await dominiApi.fetchAll()) ?? []);
    } catch {
      toast.error(t('domains.load_error'));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  const visible = data.filter(d =>
    d.domini.toLowerCase().includes(search.toLowerCase())
  );

  const handleCreate = async () => {
    if (!createValue.trim()) {
      toast.error(t('domains.field.domain'));
      return;
    }
    setSavingCreate(true);
    try {
      await dominiApi.add({ domini: createValue.trim() });
      toast.success(t('domains.create.success'));
      setOpenCreate(false);
      setCreateValue('');
      load();
    } catch {
      toast.error(t('domains.create.error'));
    } finally {
      setSavingCreate(false);
    }
  };

  const handleOpenEdit = (d: Domini) => {
    setEditTarget(d);
    setEditValue(d.domini);
    setOpenEdit(true);
  };

  const handleEdit = async () => {
    if (!editTarget || !editValue.trim()) {
      toast.error(t('domains.field.domain'));
      return;
    }
    setSavingEdit(true);
    try {
      await dominiApi.update(editTarget.uuid, { domini: editValue.trim() });
      toast.success(t('domains.edit.success'));
      setOpenEdit(false);
      load();
    } catch {
      toast.error(t('domains.edit.error'));
    } finally {
      setSavingEdit(false);
    }
  };

  const handleOpenDelete = (d: Domini) => {
    setDeleteTarget(d);
    setOpenDelete(true);
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    setDeletingId(deleteTarget.uuid);
    setOpenDelete(false);
    try {
      await dominiApi.delete(deleteTarget.uuid);
      setData(prev => prev.filter(d => d.uuid !== deleteTarget.uuid));
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
            onClick={() => { setCreateValue(''); setOpenCreate(true); }}
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
                  <TableCell align="right" sx={{ fontWeight: 700 }}>{t('domains.col.actions')}</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {visible.length === 0 ? (
                  <TableRow>
                    <TableCell colSpan={2} align="center" sx={{ py: 4, color: 'text.secondary' }}>
                      {t('domains.empty')}
                    </TableCell>
                  </TableRow>
                ) : (
                  visible.map(d => (
                    <TableRow key={d.uuid} hover>
                      <TableCell>{d.domini}</TableCell>
                      <TableCell align="right">
                        <Stack direction="row" spacing={0.5} justifyContent="flex-end">
                          <Tooltip title={t('common.actions.edit')}>
                            <IconButton size="small" onClick={() => handleOpenEdit(d)}>
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
                              onClick={() => handleOpenDelete(d)}
                              disabled={deletingId === d.uuid}
                            >
                              {deletingId === d.uuid
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
          <Stack spacing={0.5} sx={{ pt: 1 }}>
            <FieldLabel>{t('domains.field.domain')}</FieldLabel>
            <TextField
              fullWidth
              placeholder={t('domains.placeholder.domain')}
              value={createValue}
              onChange={e => setCreateValue(e.target.value)}
              onKeyDown={e => { if (e.key === 'Enter') handleCreate(); }}
            />
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
          <Stack spacing={0.5} sx={{ pt: 1 }}>
            <FieldLabel>{t('domains.field.domain')}</FieldLabel>
            <TextField
              fullWidth
              value={editValue}
              onChange={e => setEditValue(e.target.value)}
              onKeyDown={e => { if (e.key === 'Enter') handleEdit(); }}
            />
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