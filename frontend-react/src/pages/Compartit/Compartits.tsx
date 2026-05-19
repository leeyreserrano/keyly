import { useEffect, useState, useCallback } from 'react';
import { useNavigate } from 'react-router';
import { useTranslation } from 'react-i18next';
import Stack from '@mui/material/Stack';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import Typography from '@mui/material/Typography';
import Alert from '@mui/material/Alert';
import Skeleton from '@mui/material/Skeleton';
import Tabs from '@mui/material/Tabs';
import Tab from '@mui/material/Tab';
import Paper from '@mui/material/Paper';
import Chip from '@mui/material/Chip';
import Avatar from '@mui/material/Avatar';
import IconButton from '@mui/material/IconButton';
import Tooltip from '@mui/material/Tooltip';
import Select from '@mui/material/Select';
import MenuItem from '@mui/material/MenuItem';
import Dialog from '@mui/material/Dialog';
import DialogTitle from '@mui/material/DialogTitle';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogActions from '@mui/material/DialogActions';
import Button from '@mui/material/Button';
import ShareOutlinedIcon from '@mui/icons-material/ShareOutlined';
import VpnKeyOffOutlinedIcon from '@mui/icons-material/VpnKeyOffOutlined';
import FolderOutlinedIcon from '@mui/icons-material/FolderOutlined';
import KeyRoundedIcon from '@mui/icons-material/VpnKeyRounded';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import PersonAddOutlinedIcon from '@mui/icons-material/PersonAddOutlined';
import GroupsOutlinedIcon from '@mui/icons-material/GroupsOutlined';
import Header from '../../components/Header';
import CustomPagination from '../../components/CustomPagination';
import DeleteConfirmationModal from '../../components/DeleteConfirmationModal';
import ShareSelectorInline from '../../components/ShareSelectorInline';
import { compartitsApi, type Compartit, type Permisos } from '../../api/compartitsapi';
import { useShareSelector } from '../../hooks/useShareSelector';
import { formatDate } from '../../utils/timeUtils';
import { useAuth } from '../../context/AuthContext';
import toast from 'react-hot-toast';

type TabValue = 'rebuts' | 'creats';
const ITEMS_PER_PAGE = 9;

type EntitatAgrupada = {
  entitatUuid: string;
  tipusEntitat: 'ITEM' | 'CARPETA';
  nom: string;
  dataCreacio: string;
  compartits: Compartit[];
};

type FilaReceptor =
  | { tipus: 'usuari'; compartit: Compartit }
  | { tipus: 'departament'; departamentUuid: string; departamentNom: string; compartits: Compartit[] };

function agruparReceptorsPerDepartament(compartits: Compartit[]): FilaReceptor[] {
  const perDepartament = new Map<string, Compartit[]>();
  const senseDepartament: Compartit[] = [];

  for (const c of compartits) {
    const deptUuid = c.usuariReceptor?.departament?.uuid;
    if (deptUuid) {
      if (!perDepartament.has(deptUuid)) perDepartament.set(deptUuid, []);
      perDepartament.get(deptUuid)!.push(c);
    } else {
      senseDepartament.push(c);
    }
  }

  const resultat: FilaReceptor[] = [];

  for (const [deptUuid, membres] of perDepartament.entries()) {
    if (membres.length >= 2) {
      const nomDept = membres[0].usuariReceptor?.departament?.nom ?? deptUuid;
      resultat.push({ tipus: 'departament', departamentUuid: deptUuid, departamentNom: nomDept, compartits: membres });
    } else {
      senseDepartament.push(...membres);
    }
  }

  for (const c of senseDepartament) {
    resultat.push({ tipus: 'usuari', compartit: c });
  }

  return resultat;
}

function groupByEntitat(compartits: Compartit[]): EntitatAgrupada[] {
  const map = new Map<string, EntitatAgrupada>();
  for (const c of compartits) {
    const entitatUuid =
      c.tipusEntitat === 'CARPETA' ? (c.carpeta?.uuid ?? c.uuid) : (c.item?.uuid ?? c.uuid);
    const nom =
      c.tipusEntitat === 'CARPETA' ? (c.carpeta?.nom ?? '') : (c.item?.titol ?? '');
    if (!map.has(entitatUuid)) {
      map.set(entitatUuid, { entitatUuid, tipusEntitat: c.tipusEntitat, nom, dataCreacio: c.dataCreacio, compartits: [] });
    }
    map.get(entitatUuid)!.compartits.push(c);
  }
  return Array.from(map.values()).sort(
    (a, b) => new Date(b.dataCreacio).getTime() - new Date(a.dataCreacio).getTime()
  );
}

export default function Compartits() {
  const navigate = useNavigate();
  const { t } = useTranslation('shared');
  const { usuari } = useAuth();

  const [rebuts, setRebuts] = useState<Compartit[]>([]);
  const [creats, setCreats] = useState<Compartit[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [tab, setTab] = useState<TabValue>('rebuts');
  const [page, setPage] = useState(1);

  const [deleteTargets, setDeleteTargets] = useState<Compartit[]>([]);
  const [openDeleteModal, setOpenDeleteModal] = useState(false);

  const [editTarget, setEditTarget] = useState<Compartit | null>(null);
  const [editPermisos, setEditPermisos] = useState<Permisos>('LECTURA');
  const [savingPermisos, setSavingPermisos] = useState(false);

  const [addToGrup, setAddToGrup] = useState<EntitatAgrupada | null>(null);
  const [addingShare, setAddingShare] = useState(false);
  const shareSelector = useShareSelector();

  const loadData = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const [r, c] = await Promise.all([
        compartitsApi.fetchCompartitsRebuts(),
        compartitsApi.fetchCompartitsCreats(),
      ]);
      setRebuts(r ?? []);
      setCreats((c ?? []).filter((c) => c.usuariCreador?.uuid === usuari?.uuid));
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : t('error.load'));
    } finally {
      setLoading(false);
    }
  }, [t, usuari?.uuid]);

  useEffect(() => { loadData(); }, [loadData]);
  useEffect(() => { setPage(1); }, [tab]);

  const rebutsAgrupats = groupByEntitat(rebuts);
  const creatsAgrupats = groupByEntitat(creats);
  const llista = tab === 'rebuts' ? rebutsAgrupats : creatsAgrupats;
  const totalPages = Math.ceil(llista.length / ITEMS_PER_PAGE);
  const paginat = llista.slice((page - 1) * ITEMS_PER_PAGE, page * ITEMS_PER_PAGE);

  const handleDelete = (compartit: Compartit) => {
    setDeleteTargets([compartit]);
    setOpenDeleteModal(true);
  };

  const handleDeleteDepartament = (compartitsGrup: Compartit[]) => {
    setDeleteTargets(compartitsGrup);
    setOpenDeleteModal(true);
  };

  const confirmDelete = async () => {
    if (deleteTargets.length === 0) return;
    try {
      for (const c of deleteTargets) {
        await compartitsApi.deleteCompartit(c.uuid);
      }
      const uuidsEliminats = new Set(deleteTargets.map((c) => c.uuid));
      if (tab === 'rebuts') {
        setRebuts((prev) => prev.filter((c) => !uuidsEliminats.has(c.uuid)));
      } else {
        setCreats((prev) => prev.filter((c) => !uuidsEliminats.has(c.uuid)));
      }
      toast.success(t('success.delete'));
    } catch {
      toast.error(t('error.delete'));
    } finally {
      setOpenDeleteModal(false);
      setDeleteTargets([]);
    }
  };

  const handleOpenEdit = (compartit: Compartit) => {
    setEditTarget(compartit);
    setEditPermisos(compartit.permisos);
  };

  const handleSavePermisos = async () => {
    if (!editTarget) return;
    setSavingPermisos(true);
    try {
      await compartitsApi.updatePermisos(editTarget.uuid, editPermisos);
      setCreats((prev) =>
        prev.map((c) => (c.uuid === editTarget.uuid ? { ...c, permisos: editPermisos } : c))
      );
      toast.success(t('success.permisos'));
      setEditTarget(null);
    } catch {
      toast.error(t('error.permisos'));
    } finally {
      setSavingPermisos(false);
    }
  };

  const handleOpenAdd = (grup: EntitatAgrupada) => {
    shareSelector.resetShare();
    setAddToGrup(grup);
  };

  const handleConfirmAdd = async () => {
    if (!addToGrup || shareSelector.seleccionats.length === 0) return;
    setAddingShare(true);
    try {
      if (addToGrup.tipusEntitat === 'ITEM') {
        await shareSelector.compartirItem(addToGrup.entitatUuid);
      } else {
        await shareSelector.compartirCarpeta(addToGrup.entitatUuid);
      }
      toast.success(t('success.added'));
      setAddToGrup(null);
      await loadData();
    } catch {
      toast.error(t('error.add'));
    } finally {
      setAddingShare(false);
    }
  };

  const navigateToEntitat = (grup: EntitatAgrupada, compartitUuid?: string) => {
    if (grup.tipusEntitat === 'CARPETA') {
      navigate('/Carpeta', { state: { uuid: grup.entitatUuid, nombreCarpeta: grup.nom, compartitUuid } });
    } else {
      navigate('/Item', { state: { uuid: grup.entitatUuid, compartitUuid } });
    }
  };

  const renderRebutCard = (grup: EntitatAgrupada) => {
    const primer = grup.compartits[0];
    return (
      <Grid size={4} key={grup.entitatUuid}>
        <Paper
          variant="outlined"
          onClick={() => navigateToEntitat(grup, primer.uuid)}
          sx={{
            p: 2, borderRadius: '10px', bgcolor: 'background.default',
            border: '1px solid', borderColor: 'divider',
            cursor: 'pointer', '&:hover': { boxShadow: 1 },
            transition: 'box-shadow 150ms ease',
          }}
        >
          <Stack direction="row" sx={{ justifyContent: 'space-between', alignItems: 'flex-start', mb: 1 }}>
            <Stack direction="row" sx={{ gap: 0.75, alignItems: 'center', minWidth: 0 }}>
              {grup.tipusEntitat === 'CARPETA'
                ? <FolderOutlinedIcon sx={{ fontSize: 17, color: 'text.primary', flexShrink: 0 }} />
                : <KeyRoundedIcon sx={{ fontSize: 17, color: 'text.primary', flexShrink: 0 }} />
              }
              <Typography sx={{ fontWeight: 600, fontSize: '0.9rem', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                {grup.nom}
              </Typography>
            </Stack>
            <Chip label={primer.permisos} size="small" sx={{ fontSize: '0.7rem', fontWeight: 600, flexShrink: 0 }} />
          </Stack>
          <Typography sx={{ fontSize: '0.82rem', color: 'text.secondary', mb: 1 }}>
            {t('shared_by')}: {primer.usuariCreador?.nom ?? '—'}
          </Typography>
          <Typography sx={{ fontSize: '0.75rem', color: 'text.secondary' }}>
            {formatDate(primer.dataCreacio)}
          </Typography>
        </Paper>
      </Grid>
    );
  };

  const renderCreatCard = (grup: EntitatAgrupada) => {
    const receptorsAgrupats = agruparReceptorsPerDepartament(grup.compartits);
    const esCarpeta = grup.tipusEntitat === 'CARPETA';

    return (
      <Grid size={12} key={grup.entitatUuid}>
        <Paper
          variant="outlined"
          sx={{ p: 2.5, borderRadius: '10px', bgcolor: 'background.default', border: '1px solid', borderColor: 'divider' }}
        >
          <Stack direction="row" sx={{ alignItems: 'center', gap: 1, mb: 2 }}>
            {esCarpeta
              ? <FolderOutlinedIcon sx={{ fontSize: 18, color: 'text.primary' }} />
              : <KeyRoundedIcon sx={{ fontSize: 18, color: 'text.primary' }} />
            }
            <Typography
              sx={{ fontWeight: 700, fontSize: '0.95rem', cursor: 'pointer', '&:hover': { textDecoration: 'underline' } }}
              onClick={() => navigateToEntitat(grup)}
            >
              {grup.nom}
            </Typography>
            <Typography sx={{ fontSize: '0.78rem', color: 'text.secondary', ml: 'auto' }}>
              {formatDate(grup.dataCreacio)}
            </Typography>
            <Tooltip title={t('action.add_user')}>
              <IconButton size="small" onClick={() => handleOpenAdd(grup)}>
                <PersonAddOutlinedIcon sx={{ fontSize: 16 }} />
              </IconButton>
            </Tooltip>
          </Stack>

          <Stack sx={{ gap: 1 }}>
            {receptorsAgrupats.map((fila) => {
              if (fila.tipus === 'departament') {
                return (
                  <Stack
                    key={`dept-${fila.departamentUuid}`}
                    direction="row"
                    sx={{ alignItems: 'center', gap: 1.5, py: 0.75, px: 1, borderRadius: '8px', bgcolor: 'action.hover' }}
                  >
                    <Avatar sx={{ width: 28, height: 28, fontSize: '0.7rem', bgcolor: 'primary.main' }}>
                      <GroupsOutlinedIcon sx={{ fontSize: 16 }} />
                    </Avatar>
                    <Stack sx={{ minWidth: 0, flex: 1 }}>
                      <Typography sx={{ fontSize: '0.85rem', fontWeight: 600, lineHeight: 1.2 }}>
                        {fila.departamentNom}
                      </Typography>
                      <Typography sx={{ fontSize: '0.75rem', color: 'text.secondary' }}>
                        {fila.compartits.length} {t('share.users_count')}
                      </Typography>
                    </Stack>
                    <Chip
                      label={fila.compartits[0].permisos}
                      size="small"
                      sx={{ fontSize: '0.7rem', fontWeight: 600, ...(esCarpeta ? {} : { cursor: 'pointer' }) }}
                      onClick={esCarpeta ? undefined : () => handleOpenEdit(fila.compartits[0])}
                    />
                    <Tooltip title={t('action.revoke')}>
                      <IconButton size="small" sx={{
                          bgcolor: 'error.main',
                          color: 'white',
                          '&:hover': { bgcolor: 'error.dark' }
                        }} onClick={() => handleDeleteDepartament(fila.compartits)}>
                        <DeleteOutlineIcon sx={{ fontSize: 16 }} />
                      </IconButton>
                    </Tooltip>
                  </Stack>
                );
              }

              const c = fila.compartit;
              return (
                <Stack
                  key={c.uuid}
                  direction="row"
                  sx={{ alignItems: 'center', gap: 1.5, py: 0.75, px: 1, borderRadius: '8px', bgcolor: 'action.hover' }}
                >
                  <Avatar src={c.usuariReceptor?.imatge} sx={{ width: 28, height: 28, fontSize: '0.75rem' }}>
                    {c.usuariReceptor?.nom?.charAt(0) ?? '?'}
                  </Avatar>
                  <Stack sx={{ minWidth: 0, flex: 1 }}>
                    <Typography sx={{ fontSize: '0.85rem', fontWeight: 600, lineHeight: 1.2 }}>
                      {c.usuariReceptor?.nom ?? '—'}
                    </Typography>
                    <Typography sx={{ fontSize: '0.75rem', color: 'text.secondary', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                      {c.usuariReceptor?.correu ?? ''}
                    </Typography>
                  </Stack>
                  <Chip
                    label={c.permisos}
                    size="small"
                    sx={{ fontSize: '0.7rem', fontWeight: 600, ...(esCarpeta ? {} : { cursor: 'pointer' }) }}
                    onClick={esCarpeta ? undefined : () => handleOpenEdit(c)}
                  />
                  <Tooltip title={t('action.revoke')}>
                    <IconButton size="small" sx={{
                          bgcolor: 'error.main',
                          color: 'white',
                          '&:hover': { bgcolor: 'error.dark' }
                        }} onClick={() => handleDelete(c)}>
                      <DeleteOutlineIcon sx={{ fontSize: 16 }} />
                    </IconButton>
                  </Tooltip>
                </Stack>
              );
            })}
          </Stack>
        </Paper>
      </Grid>
    );
  };

  const renderContent = () => {
    if (loading) {
      return (
        <Grid container spacing={2}>
          {Array.from({ length: 4 }).map((_, i) => (
            <Grid size={tab === 'rebuts' ? 4 : 12} key={i}>
              <Skeleton variant="rounded" height={tab === 'rebuts' ? 110 : 130} sx={{ borderRadius: '10px' }} />
            </Grid>
          ))}
        </Grid>
      );
    }
    if (error) return <Alert severity="error" sx={{ mt: 2 }}>{error}</Alert>;
    if (llista.length === 0) {
      return (
        <Stack sx={{ alignItems: 'center', py: 10, gap: 2, color: 'text.disabled' }}>
          <VpnKeyOffOutlinedIcon sx={{ fontSize: 64 }} />
          <Typography variant="body1" sx={{ fontWeight: 600 }}>
            {tab === 'rebuts' ? t('empty.received') : t('empty.created')}
          </Typography>
        </Stack>
      );
    }
    return (
      <Grid container spacing={2}>
        {paginat.map((grup) => tab === 'rebuts' ? renderRebutCard(grup) : renderCreatCard(grup))}
      </Grid>
    );
  };

  return (
    <Stack sx={{ height: '100%', overflow: 'hidden' }}>
      <Header title={t('title')} icon={<ShareOutlinedIcon sx={{ fontSize: 30, color: 'text.primary' }} />} />

      <Stack sx={{ flex: 1, overflow: 'auto', minWidth: 0 }}>
        <Box sx={{ px: 4, pt: 2, pb: 1 }}>
          <Tabs value={tab} onChange={(_, v) => setTab(v as TabValue)}>
            <Tab label={t('tab.received')} value="rebuts" />
            <Tab label={t('tab.created')} value="creats" />
          </Tabs>
        </Box>
        <Box sx={{ px: 4, pb: 3, flex: 1, pt: 2 }}>{renderContent()}</Box>
        {!loading && !error && llista.length > 0 && (
          <CustomPagination count={totalPages} page={page} onChange={setPage} />
        )}
      </Stack>

      <DeleteConfirmationModal
        open={openDeleteModal}
        onClose={() => { setOpenDeleteModal(false); setDeleteTargets([]); }}
        onConfirm={confirmDelete}
      />

      <Dialog open={!!editTarget} onClose={() => setEditTarget(null)} maxWidth="xs" fullWidth>
        <DialogTitle sx={{ fontWeight: 700 }}>{t('edit.title')}</DialogTitle>
        <DialogContent>
          <DialogContentText sx={{ mb: 2, fontSize: '0.9rem' }}>
            {t('edit.subtitle', { nom: editTarget?.usuariReceptor?.nom ?? '' })}
          </DialogContentText>
          <Select size="small" fullWidth value={editPermisos} onChange={(e) => setEditPermisos(e.target.value as Permisos)}>
            <MenuItem value="LECTURA">{t('edit.read')}</MenuItem>
            <MenuItem value="ESCRIPTURA">{t('edit.write')}</MenuItem>
          </Select>
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2 }}>
          <Button onClick={() => setEditTarget(null)} sx={{ textTransform: 'none', fontWeight: 600, color: 'white' }}>
            {t('edit.cancel')}
          </Button>
          <Button
            onClick={handleSavePermisos}
            variant="contained"
            disabled={savingPermisos}
            sx={{ textTransform: 'none', fontWeight: 600, bgcolor: 'white', color: 'primary.main', '&:hover': { bgcolor: 'grey.100' } }}
          >
            {savingPermisos ? t('edit.saving') : t('edit.save')}
          </Button>
        </DialogActions>
      </Dialog>

      <Dialog open={!!addToGrup} onClose={() => setAddToGrup(null)} maxWidth="sm" fullWidth>
        <DialogTitle sx={{ fontWeight: 700 }}>
          {t('add.title', { nom: addToGrup?.nom ?? '' })}
        </DialogTitle>
        <DialogContent>
          <Box sx={{ pt: 1 }}>
            <ShareSelectorInline
              t={t}
              esAdmin={shareSelector.esAdmin}
              tab={shareSelector.tab}
              onTabChange={(v) => { shareSelector.setTab(v); shareSelector.setSeleccionats([]); shareSelector.handleSelectDepartament(''); }}
              filtrats={shareSelector.filtrats.filter(
                (u) => !addToGrup?.compartits.some((c) => c.usuariReceptor?.uuid === u.uuid)
              )}
              departamentsFiltrats={shareSelector.departamentsFiltrats}
              usuarisDepartament={shareSelector.usuarisDepartament}
              allUsuarisAmbDept={shareSelector.usuarisAmbDept}
              seleccionats={shareSelector.seleccionats}
              departamentSeleccionat={shareSelector.departamentSeleccionat}
              searchUsuaris={shareSelector.searchUsuaris}
              onSearchUsuaris={shareSelector.setSearchUsuaris}
              searchDept={shareSelector.searchDept}
              onSearchDept={shareSelector.setSearchDept}
              permisCompartir={shareSelector.permisCompartir}
              onPermisChange={shareSelector.setPermisCompartir}
              onToggleSeleccio={shareSelector.toggleSeleccio}
              onSelectDepartament={shareSelector.handleSelectDepartament}
            />
          </Box>
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2 }}>
          <Button onClick={() => setAddToGrup(null)} sx={{ textTransform: 'none', fontWeight: 600, color: 'white' }}>
            {t('add.cancel')}
          </Button>
          <Button
            onClick={handleConfirmAdd}
            variant="contained"
            disabled={addingShare || shareSelector.seleccionats.length === 0}
            sx={{ textTransform: 'none', fontWeight: 600, bgcolor: 'white', color: 'primary.main', '&:hover': { bgcolor: 'grey.100' }, '&.Mui-disabled': { bgcolor: 'grey.300', color: 'grey.500' } }}
          >
            {addingShare
              ? t('add.sharing')
              : shareSelector.seleccionats.length > 0
                ? t('add.confirm_count', { count: shareSelector.seleccionats.length })
                : t('add.confirm')}
          </Button>
        </DialogActions>
      </Dialog>
    </Stack>
  );
}