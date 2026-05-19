import { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router';
import { useTranslation } from 'react-i18next';
import {
  Stack, Typography, Paper, Button, TextField,
  Box, Divider, CircularProgress, Alert,
} from '@mui/material';
import FolderOutlinedIcon from '@mui/icons-material/FolderOutlined';
import Header from '../../components/Header';
import ShareSelectorInline from '../../components/ShareSelectorInline';
import { carpetasApi, type Carpeta } from '../../api/carpetasapi';
import { compartitsApi, type Compartit } from '../../api/compartitsapi';
import { useShareSelector } from '../../hooks/useShareSelector';
import toast from 'react-hot-toast';

export default function EditCarpeta() {
  const navigate = useNavigate();
  const location = useLocation();
  const { t } = useTranslation('folder');
  const { uuid } = location.state || {};

  const [carpeta, setCarpeta] = useState<Carpeta | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [nom, setNom] = useState('');
  const [error, setError] = useState<string | undefined>();
  const [compartitsExistents, setCompartitsExistents] = useState<Compartit[]>([]);
  const [revocats, setRevocats] = useState<string[]>([]);

  const shareSelector = useShareSelector();

  useEffect(() => {
    if (!uuid) { setLoadError(t('edit.not_specified')); setLoading(false); return; }

    const loadData = async () => {
      try {
        const [allCarpetas, creats] = await Promise.all([
          carpetasApi.fetchItems(),
          compartitsApi.fetchCompartitsCreats(),
        ]);
        const found = allCarpetas.find((c) => c.uuid === uuid);
        if (!found) { setLoadError(t('edit.not_found')); return; }
        setCarpeta(found);
        setNom(found.nom);

        const carpetaCompartits = (creats ?? []).filter(
          (c) => c.tipusEntitat === 'CARPETA' && c.carpeta?.uuid === uuid
        );
        setCompartitsExistents(carpetaCompartits);

        const usuarisPreseleccionats = carpetaCompartits
          .map((c) => c.usuariReceptor)
          .filter((u): u is NonNullable<typeof u> => !!u)
          .map((u) => ({
            uuid: u.uuid,
            nom: u.nom,
            correu: u.correu,
            imatge: u.imatge,
            publicKey: u.publicKey,
          }));
        shareSelector.setSeleccionats(usuarisPreseleccionats);
      } catch (err: unknown) {
        const message = err instanceof Error ? err.message : t('edit.error_load');
        setLoadError(message);
      } finally {
        setLoading(false);
      }
    };
    loadData();
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [uuid, t]);

  const validate = (): boolean => {
    if (!nom.trim()) { setError(t('edit.name_required')); return false; }
    setError(undefined);
    return true;
  };

  const handleToggleRevocat = (compartitUuidTarget: string) => {
    setRevocats((prev) =>
      prev.includes(compartitUuidTarget)
        ? prev.filter((u) => u !== compartitUuidTarget)
        : [...prev, compartitUuidTarget]
    );
  };

  const handleSave = async () => {
    if (!carpeta || !validate()) return;
    setSaving(true);
    try {
      await carpetasApi.updateCarpeta(carpeta.uuid, { nom });

      for (const compartitUuidRevocar of revocats) {
        await compartitsApi.deleteCompartit(compartitUuidRevocar);
      }

      const uuidsExistents = new Set(
        compartitsExistents
          .filter((c) => !revocats.includes(c.uuid))
          .map((c) => c.usuariReceptor?.uuid)
      );
      const nouUsuaris = shareSelector.seleccionats.filter((u) => !uuidsExistents.has(u.uuid));
      if (nouUsuaris.length > 0) {
        const originals = shareSelector.seleccionats;
        shareSelector.setSeleccionats(nouUsuaris);
        await shareSelector.compartirCarpeta(carpeta.uuid);
        shareSelector.setSeleccionats(originals);
      }

      toast.success(t('edit.success'));
      navigate(-1);
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : t('edit.error_save');
      toast.error(message);
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <Stack sx={{ height: '100%', overflow: 'hidden' }}>
        <Header title={t('edit.title')} icon={<FolderOutlinedIcon sx={{ fontSize: 30, color: 'text.primary' }} />} showBackButton />
        <Stack sx={{ flex: 1, alignItems: 'center', justifyContent: 'center' }}>
          <CircularProgress />
        </Stack>
      </Stack>
    );
  }

  if (loadError) {
    return (
      <Stack sx={{ height: '100%', overflow: 'hidden' }}>
        <Header title={t('edit.title')} icon={<FolderOutlinedIcon sx={{ fontSize: 30, color: 'text.primary' }} />} showBackButton />
        <Box sx={{ p: 4 }}><Alert severity="error">{loadError}</Alert></Box>
      </Stack>
    );
  }

  return (
    <Stack sx={{ height: '100%', overflow: 'hidden' }}>
      <Header title={t('edit.title')} icon={<FolderOutlinedIcon sx={{ fontSize: 30, color: 'text.primary' }} />} showBackButton />
      <Stack sx={{ flex: 1, overflow: 'auto', minWidth: 0 }}>
        <Box sx={{ px: 4, py: 3, display: 'flex', justifyContent: 'center' }}>
          <Paper variant="outlined" sx={{ p: 4, borderRadius: 3, width: '70%', maxWidth: 500, display: 'flex', flexDirection: 'column', gap: 3 }}>
            <Typography variant="h5" sx={{ fontWeight: 700 }}>{t('edit.title')}</Typography>
            <Divider />

            <Stack spacing={0.5}>
              <Typography sx={{ fontSize: '0.8rem', fontWeight: 600, color: 'text.secondary' }}>{t('edit.name')} *</Typography>
              <TextField fullWidth value={nom} onChange={(e) => setNom(e.target.value)} error={!!error} helperText={error} />
            </Stack>

            <Divider />

            <ShareSelectorInline
              t={t}
              esAdmin={shareSelector.esAdmin}
              tab={shareSelector.tab}
              onTabChange={(v) => { shareSelector.setTab(v); shareSelector.setSeleccionats([]); shareSelector.handleSelectDepartament(''); }}
              filtrats={shareSelector.filtrats}
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
              onToggleSeleccio={(u) => {
                const compartitExistent = compartitsExistents.find((c) => c.usuariReceptor?.uuid === u.uuid);
                if (compartitExistent) {
                  handleToggleRevocat(compartitExistent.uuid);
                }
                shareSelector.toggleSeleccio(u);
              }}
              onSelectDepartament={shareSelector.handleSelectDepartament}
              showPermisos={false}
              revocats={revocats}
              compartitsExistents={compartitsExistents}
            />

            <Divider />
            <Stack direction="row" sx={{ gap: 1, justifyContent: 'flex-end' }}>
              <Button onClick={() => navigate(-1)} variant="outlined" sx={{ textTransform: 'none', fontWeight: 600 }}>{t('edit.cancel')}</Button>
              <Button onClick={handleSave} variant="contained" disabled={saving} sx={{ textTransform: 'none', fontWeight: 600 }}>
                {saving ? t('edit.saving') : t('edit.save')}
              </Button>
            </Stack>
          </Paper>
        </Box>
      </Stack>
    </Stack>
  );
}