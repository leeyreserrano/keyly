import { useCallback, useEffect, useState } from 'react';
import {
  Box, Stack, Typography, Paper, Chip, Avatar, IconButton,
  Tooltip, TextField, InputAdornment, CircularProgress,
  Dialog, DialogTitle, DialogContent,
  DialogActions, Button,
} from '@mui/material';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import PersonAddOutlinedIcon from '@mui/icons-material/PersonAddOutlined';
import GroupsOutlinedIcon from '@mui/icons-material/GroupsOutlined';
import FolderOutlinedIcon from '@mui/icons-material/FolderOutlined';
import SearchIcon from '@mui/icons-material/Search';
import toast from 'react-hot-toast';

import { useAuth } from '../context/AuthContext';
import { compartitsApi, type Compartit } from '../api/compartitsapi';
import { useShareSelector } from '../hooks/useShareSelector';
import ShareSelectorInline from './ShareSelectorInline';
import DeleteConfirmationModal from './DeleteConfirmationModal';
import { formatDate } from '../utils/timeUtils';

type EntitatAgrupada = {
  entitatUuid: string;
  nom: string;
  dataCreacio: string;
  compartits: Compartit[];
};

type FilaReceptor =
  | { tipus: 'usuari'; compartit: Compartit }
  | { tipus: 'departament'; departamentUuid: string; departamentNom: string; compartits: Compartit[] };

function agruparReceptors(compartits: Compartit[]): FilaReceptor[] {
  const perDept = new Map<string, Compartit[]>();
  const sense: Compartit[] = [];
  for (const c of compartits) {
    const deptUuid = c.usuariReceptor?.departament?.uuid;
    if (deptUuid) {
      if (!perDept.has(deptUuid)) perDept.set(deptUuid, []);
      perDept.get(deptUuid)!.push(c);
    } else {
      sense.push(c);
    }
  }
  const resultat: FilaReceptor[] = [];
  for (const [deptUuid, membres] of perDept.entries()) {
    if (membres.length >= 2) {
      resultat.push({
        tipus: 'departament',
        departamentUuid: deptUuid,
        departamentNom: membres[0].usuariReceptor?.departament?.nom ?? deptUuid,
        compartits: membres,
      });
    } else {
      sense.push(...membres);
    }
  }
  for (const c of sense) resultat.push({ tipus: 'usuari', compartit: c });
  return resultat;
}

function groupByEntitat(compartits: Compartit[]): EntitatAgrupada[] {
  const map = new Map<string, EntitatAgrupada>();
  for (const c of compartits) {
    const uuid = c.carpeta?.uuid ?? c.uuid;
    const nom = c.carpeta?.nom ?? '—';
    if (!map.has(uuid)) map.set(uuid, { entitatUuid: uuid, nom, dataCreacio: c.dataCreacio, compartits: [] });
    map.get(uuid)!.compartits.push(c);
  }
  return Array.from(map.values()).sort(
    (a, b) => new Date(b.dataCreacio).getTime() - new Date(a.dataCreacio).getTime()
  );
}

export default function CarpetesTab() {
  const { usuari } = useAuth();
  const isAdmin = usuari?.rolIntern === 'ADMIN';

  const [loading, setLoading] = useState(false);
  const [search, setSearch] = useState('');
  const [carpetes, setCarpetes] = useState<Compartit[]>([]);

  const [deleteTargets, setDeleteTargets] = useState<Compartit[]>([]);
  const [openDeleteModal, setOpenDeleteModal] = useState(false);

  const [addToGrup, setAddToGrup] = useState<EntitatAgrupada | null>(null);
  const [addingShare, setAddingShare] = useState(false);
  const shareSelector = useShareSelector();

  const loadCarpetes = useCallback(async () => {
    setLoading(true);
    try {
      const data = isAdmin
        ? await compartitsApi.fetchAllAdmin()
        : await compartitsApi.fetchCompartitsCreats();

      let filtered = (data ?? []).filter((c) => c.tipusEntitat === 'CARPETA');

      if (!isAdmin && usuari?.departament?.uuid) {
        filtered = filtered.filter(
          (c) => c.usuariReceptor?.departament?.uuid === usuari.departament?.uuid
        );
      }

      setCarpetes(filtered);
    } catch {
      toast.error('Error carregant carpetes compartides');
    } finally {
      setLoading(false);
    }
  }, [isAdmin, usuari]);

  useEffect(() => { loadCarpetes(); }, [loadCarpetes]);

  const agrupats = groupByEntitat(carpetes).filter((g) =>
    g.nom.toLowerCase().includes(search.toLowerCase()) ||
    g.compartits.some(
      (c) =>
        c.usuariReceptor?.nom?.toLowerCase().includes(search.toLowerCase()) ||
        c.usuariReceptor?.departament?.nom?.toLowerCase().includes(search.toLowerCase())
    )
  );

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
      for (const c of deleteTargets) await compartitsApi.deleteCompartit(c.uuid);
      const uuids = new Set(deleteTargets.map((c) => c.uuid));
      setCarpetes((prev) => prev.filter((c) => !uuids.has(c.uuid)));
      toast.success('Compartit eliminat');
    } catch {
      toast.error('Error eliminant compartit');
    } finally {
      setOpenDeleteModal(false);
      setDeleteTargets([]);
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
      await shareSelector.compartirCarpeta(addToGrup.entitatUuid);
      toast.success('Compartit correctament');
      setAddToGrup(null);
      await loadCarpetes();
    } catch {
      toast.error('Error compartint');
    } finally {
      setAddingShare(false);
    }
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', py: 6 }}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box>
      <TextField
        placeholder="Cerca per carpeta, usuari o departament..."
        size="small"
        value={search}
        onChange={(e) => setSearch(e.target.value)}
        sx={{ mb: 3, width: 360 }}
        InputProps={{
          startAdornment: (
            <InputAdornment position="start">
              <SearchIcon fontSize="small" />
            </InputAdornment>
          ),
        }}
      />

      <Stack spacing={2}>
        {agrupats.length === 0 ? (
          <Typography sx={{ color: 'text.secondary', py: 4, textAlign: 'center' }}>
            No hi ha carpetes compartides
          </Typography>
        ) : (
          agrupats.map((grup) => {
            const receptors = agruparReceptors(grup.compartits);
            return (
              <Paper key={grup.entitatUuid} variant="outlined" sx={{ p: 2.5, borderRadius: '10px' }}>
                <Stack direction="row" sx={{ alignItems: 'center', gap: 1, mb: 2 }}>
                  <FolderOutlinedIcon sx={{ fontSize: 18, color: 'text.primary' }} />
                  <Typography sx={{ fontWeight: 700, fontSize: '0.95rem' }}>
                    {grup.nom}
                  </Typography>
                  <Typography sx={{ fontSize: '0.78rem', color: 'text.secondary', ml: 'auto' }}>
                    {formatDate(grup.dataCreacio)}
                  </Typography>
                  <Tooltip title="Afegir usuari">
                    <IconButton size="small" sx={{
                      bgcolor: 'primary.main',
                      color: 'white',
                      '&:hover': { bgcolor: 'primary.dark' }
                    }} onClick={() => handleOpenAdd(grup)}>
                      <PersonAddOutlinedIcon sx={{ fontSize: 16 }} />
                    </IconButton>
                  </Tooltip>
                </Stack>

                <Stack spacing={1}>
                  {receptors.map((fila) => {
                    if (fila.tipus === 'departament') {
                      return (
                        <Stack
                          key={`dept-${fila.departamentUuid}`}
                          direction="row"
                          sx={{ alignItems: 'center', gap: 1.5, py: 0.75, px: 1, borderRadius: '8px', bgcolor: 'action.hover' }}
                        >
                          <Avatar sx={{ width: 28, height: 28, bgcolor: 'primary.main' }}>
                            <GroupsOutlinedIcon sx={{ fontSize: 16 }} />
                          </Avatar>
                          <Stack sx={{ minWidth: 0, flex: 1 }}>
                            <Typography sx={{ fontSize: '0.85rem', fontWeight: 600 }}>
                              {fila.departamentNom}
                            </Typography>
                            <Typography sx={{ fontSize: '0.75rem', color: 'text.secondary' }}>
                              {fila.compartits.length} usuaris
                            </Typography>
                          </Stack>
                          <Chip
                            label={fila.compartits[0].permisos}
                            size="small"
                            sx={{ fontSize: '0.7rem', fontWeight: 600 }}
                          />
                          <Tooltip title="Revocar accés">
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
                          <Typography sx={{ fontSize: '0.85rem', fontWeight: 600 }}>
                            {c.usuariReceptor?.nom ?? '—'}
                          </Typography>
                          <Typography sx={{ fontSize: '0.75rem', color: 'text.secondary', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                            {c.usuariReceptor?.correu ?? ''}
                          </Typography>
                        </Stack>
                        <Chip
                          label={c.permisos}
                          size="small"
                          sx={{ fontSize: '0.7rem', fontWeight: 600 }}
                        />
                        <Tooltip title="Revocar accés">
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
            );
          })
        )}
      </Stack>

      <DeleteConfirmationModal
        open={openDeleteModal}
        onClose={() => { setOpenDeleteModal(false); setDeleteTargets([]); }}
        onConfirm={confirmDelete}
      />

      <Dialog open={!!addToGrup} onClose={() => setAddToGrup(null)} maxWidth="sm" fullWidth>
        <DialogTitle sx={{ fontWeight: 700 }}>
          Afegir usuaris a "{addToGrup?.nom ?? ''}"
        </DialogTitle>
        <DialogContent>
          <Box sx={{ pt: 1 }}>
            <ShareSelectorInline
              t={(key) => key}
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
            Cancel·lar
          </Button>
          <Button
            onClick={handleConfirmAdd}
            variant="contained"
            disabled={addingShare || shareSelector.seleccionats.length === 0}
            sx={{ textTransform: 'none', fontWeight: 600, bgcolor: 'white', color: 'primary.main', '&:hover': { bgcolor: 'grey.100' }, '&.Mui-disabled': { bgcolor: 'grey.300', color: 'grey.500' } }}
          >
            {addingShare
              ? 'Compartint...'
              : shareSelector.seleccionats.length > 0
                ? `Compartir (${shareSelector.seleccionats.length})`
                : 'Compartir'}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}