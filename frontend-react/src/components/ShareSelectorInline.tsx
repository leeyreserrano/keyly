import {
  Stack, Typography, TextField, Checkbox, Avatar,
  Chip, FormControl, Select, MenuItem, Divider, Box, Tabs, Tab,
} from '@mui/material';
import type { UsuariPublic, UsuariAmbDepartament } from '../api/usuarisapi';
import type { Departament } from '../api/departamentsapi';
import type { Compartit, Permisos } from '../api/compartitsapi';

interface ShareSelectorInlineProps {
  t: (key: string, opts?: Record<string, unknown>) => string;
  esAdmin: boolean;
  tab: 'usuaris' | 'departament';
  onTabChange: (v: 'usuaris' | 'departament') => void;
  filtrats: UsuariPublic[];
  departamentsFiltrats: Departament[];
  usuarisDepartament: UsuariAmbDepartament[];
  allUsuarisAmbDept: UsuariAmbDepartament[];
  seleccionats: UsuariPublic[];
  departamentSeleccionat: string;
  searchUsuaris: string;
  onSearchUsuaris: (v: string) => void;
  searchDept: string;
  onSearchDept: (v: string) => void;
  permisCompartir: Permisos;
  onPermisChange: (v: Permisos) => void;
  onToggleSeleccio: (u: UsuariPublic) => void;
  onSelectDepartament: (uuid: string) => void;
  showPermisos?: boolean;
  revocats?: string[];
  compartitsExistents?: Compartit[];
}

export default function ShareSelectorInline({
  t, esAdmin, tab, onTabChange,
  filtrats, departamentsFiltrats, usuarisDepartament, allUsuarisAmbDept,
  seleccionats, departamentSeleccionat,
  searchUsuaris, onSearchUsuaris,
  searchDept, onSearchDept,
  permisCompartir, onPermisChange,
  onToggleSeleccio, onSelectDepartament,
  showPermisos = true,
  revocats = [],
  compartitsExistents = [],
}: ShareSelectorInlineProps) {
  const esExistent = (uuid: string) => compartitsExistents.some((c) => c.usuariReceptor?.uuid === uuid);
  const esRevocat = (uuid: string) => {
    const compartit = compartitsExistents.find((c) => c.usuariReceptor?.uuid === uuid);
    return compartit ? revocats.includes(compartit.uuid) : false;
  };

  return (
    <Stack spacing={1}>
      <Stack direction="row" sx={{ justifyContent: 'space-between', alignItems: 'center' }}>
        <Typography sx={{ fontSize: '0.8rem', fontWeight: 600, color: 'text.secondary' }}>
          {t('share.title')}
        </Typography>
        {showPermisos && (
          <FormControl size="small" sx={{ width: 140 }}>
            <Select value={permisCompartir} onChange={(e) => onPermisChange(e.target.value as Permisos)}>
              <MenuItem value="LECTURA">{t('share.read')}</MenuItem>
              <MenuItem value="ESCRIPTURA">{t('share.write')}</MenuItem>
            </Select>
          </FormControl>
        )}
      </Stack>

      {esAdmin && (
        <Tabs
          value={tab}
          onChange={(_, v) => onTabChange(v)}
          textColor="primary"
          indicatorColor="primary"
          sx={{ minHeight: 36 }}
        >
          <Tab label={t('share.tab.users')} value="usuaris" sx={{ minHeight: 36, py: 0 }} />
          <Tab label={t('share.tab.department')} value="departament" sx={{ minHeight: 36, py: 0 }} />
        </Tabs>
      )}

      {seleccionats.length > 0 && (
        <Stack direction="row" flexWrap="wrap" gap={0.75}>
          {seleccionats.map((u) => {
            const revocat = esRevocat(u.uuid);
            const existent = esExistent(u.uuid);
            return (
              <Chip
                key={u.uuid}
                label={u.nom}
                onDelete={tab === 'usuaris' ? () => onToggleSeleccio(u) : undefined}
                size="small"
                color={revocat ? 'error' : existent ? 'primary' : 'default'}
                variant={revocat ? 'outlined' : 'filled'}
              />
            );
          })}
        </Stack>
      )}

      {tab === 'usuaris' ? (
        <>
          <TextField
            placeholder={t('share.search_placeholder')}
            size="small"
            fullWidth
            value={searchUsuaris}
            onChange={(e) => onSearchUsuaris(e.target.value)}
          />
          <Box sx={{ maxHeight: 200, overflowY: 'auto', border: '1px solid', borderColor: 'divider', borderRadius: '8px' }}>
            {filtrats.length === 0 ? (
              <Typography sx={{ py: 2, textAlign: 'center', color: 'text.disabled', fontSize: '0.875rem' }}>
                {t('share.no_users')}
              </Typography>
            ) : (
              filtrats.map((u, i) => {
                const seleccionat = seleccionats.some((s) => s.uuid === u.uuid);
                const revocat = esRevocat(u.uuid);
                const existent = esExistent(u.uuid);
                return (
                  <Box key={u.uuid}>
                    <Stack
                      direction="row"
                      sx={{
                        alignItems: 'center', px: 1.5, py: 1, gap: 1.5, cursor: 'pointer',
                        bgcolor: revocat ? 'error.light' : seleccionat ? 'action.selected' : 'transparent',
                        opacity: revocat ? 0.6 : 1,
                        '&:hover': { bgcolor: revocat ? 'error.light' : 'action.hover' },
                        transition: 'background-color 150ms ease',
                      }}
                      onClick={() => onToggleSeleccio(u)}
                    >
                      <Checkbox
                        checked={seleccionat && !revocat}
                        size="small"
                        sx={{ p: 0 }}
                        onClick={(e) => e.stopPropagation()}
                        onChange={() => onToggleSeleccio(u)}
                      />
                      <Avatar src={u.imatge} sx={{ width: 28, height: 28, fontSize: '0.75rem' }}>
                        {u.nom.charAt(0).toUpperCase()}
                      </Avatar>
                      <Stack sx={{ minWidth: 0, flex: 1 }}>
                        <Typography sx={{ fontWeight: 600, fontSize: '0.8rem' }}>{u.nom}</Typography>
                        <Typography sx={{ fontSize: '0.7rem', color: 'text.secondary' }}>{u.correu}</Typography>
                      </Stack>
                      {existent && !revocat && (
                        <Chip label={t('share.already_shared')} size="small" color="primary" variant="outlined" sx={{ fontSize: '0.65rem' }} />
                      )}
                      {revocat && (
                        <Chip label={t('share.will_revoke')} size="small" color="error" variant="outlined" sx={{ fontSize: '0.65rem' }} />
                      )}
                    </Stack>
                    {i < filtrats.length - 1 && <Divider />}
                  </Box>
                );
              })
            )}
          </Box>
        </>
      ) : (
        <>
          <TextField
            placeholder={t('share.search_department')}
            size="small"
            fullWidth
            value={searchDept}
            onChange={(e) => onSearchDept(e.target.value)}
          />
          <Box sx={{ maxHeight: 200, overflowY: 'auto', border: '1px solid', borderColor: 'divider', borderRadius: '8px' }}>
            {departamentsFiltrats.length === 0 ? (
              <Typography sx={{ py: 2, textAlign: 'center', color: 'text.disabled', fontSize: '0.875rem' }}>
                {t('share.no_departments')}
              </Typography>
            ) : (
              departamentsFiltrats.map((d, i) => {
                const seleccionat = departamentSeleccionat === d.uuid;
                const count = allUsuarisAmbDept.filter((u) => u.departament?.uuid === d.uuid).length;
                return (
                  <Box key={d.uuid}>
                    <Stack
                      direction="row"
                      sx={{
                        alignItems: 'center', px: 1.5, py: 1, gap: 1.5, cursor: 'pointer',
                        bgcolor: seleccionat ? 'action.selected' : 'transparent',
                        '&:hover': { bgcolor: 'action.hover' },
                        transition: 'background-color 150ms ease',
                      }}
                      onClick={() => onSelectDepartament(d.uuid)}
                    >
                      <Checkbox checked={seleccionat} size="small" sx={{ p: 0 }} onClick={(e) => e.stopPropagation()} onChange={() => onSelectDepartament(d.uuid)} />
                      <Stack sx={{ minWidth: 0, flex: 1 }}>
                        <Typography sx={{ fontWeight: 600, fontSize: '0.8rem' }}>{d.nom}</Typography>
                        <Typography sx={{ fontSize: '0.7rem', color: 'text.secondary' }}>
                          {count > 0 ? `${count} ${t('share.users_count')}` : ''}
                        </Typography>
                      </Stack>
                    </Stack>
                    {i < departamentsFiltrats.length - 1 && <Divider />}
                  </Box>
                );
              })
            )}
          </Box>

          {usuarisDepartament.length > 0 && (
            <Stack spacing={0.5}>
              <Typography sx={{ fontSize: '0.75rem', fontWeight: 600, color: 'text.secondary' }}>
                {t('share.department_users')}
              </Typography>
              <Stack direction="row" flexWrap="wrap" gap={0.5}>
                {usuarisDepartament.map((u) => (
                  <Chip
                    key={u.uuid}
                    avatar={<Avatar src={u.imatge}>{u.nom.charAt(0)}</Avatar>}
                    label={u.nom}
                    size="small"
                    variant="outlined"
                  />
                ))}
              </Stack>
            </Stack>
          )}
        </>
      )}
    </Stack>
  );
}