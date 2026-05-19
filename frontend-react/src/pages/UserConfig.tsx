import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Stack, Typography, Box, Tabs, Tab } from '@mui/material';
import EditNoteOutlinedIcon from '@mui/icons-material/EditNoteOutlined';
import Header from '../components/Header';
import { useAuth } from '../context/AuthContext';
import PerfilTab from '../components/PerfilTab';
import UsuarisTab from '../components/UsuarisTab';
import ItemsTab from '../components/ItemsTab';
import CarpetesTab from '../components/CarpetesTab';
import DepartamentsTab from '../components/DepartamentsTab';
import SucursalsTab from '../components/SucursalsTab';
import DominiTab from '../components/DominiTab';
import RolsTab from '../components/RolsTab';

type TabValue =
  | 'perfil'
  | 'items'
  | 'carpetes'
  | 'usuaris'
  | 'departaments'
  | 'sucursals'
  | 'dominis'
  | 'rols';

export default function UserConfig() {
  const { usuari } = useAuth();
  const { t } = useTranslation('config');
  const [tab, setTab] = useState<TabValue>('perfil');

  const isCapOrAdmin = usuari?.rolIntern === 'CAP' || usuari?.rolIntern === 'ADMIN';
  const isAdmin = usuari?.rolIntern === 'ADMIN';

  const tabsConfig: Array<{ value: TabValue; label: string }> = [
    { value: 'perfil', label: t('tabs.profile') },
    ...(isCapOrAdmin
      ? [
          { value: 'items' as TabValue, label: t('tabs.items') },
          { value: 'carpetes' as TabValue, label: t('tabs.folders') },
        ]
      : []),
    ...(isAdmin
      ? [
          { value: 'usuaris' as TabValue, label: t('tabs.users') },
          { value: 'departaments' as TabValue, label: t('tabs.departments') },
          { value: 'sucursals' as TabValue, label: t('tabs.branches') },
          { value: 'dominis' as TabValue, label: t('tabs.domains') },
          { value: 'rols' as TabValue, label: t('tabs.roles') },
        ]
      : []),
  ];

  return (
    <Stack sx={{ height: '100%', overflow: 'hidden' }}>
      <Header
        title={t('title')}
        icon={<EditNoteOutlinedIcon sx={{ fontSize: 30, color: 'text.primary' }} />}
        showBackButton={false}
      />

      <Stack sx={{ flex: 1, overflow: 'auto', minWidth: 0 }}>
        <Box sx={{ px: 4, py: 3 }}>
          {!usuari ? (
            <Typography color="error">{t('error.no_user')}</Typography>
          ) : (
            <>
              <Tabs
                value={tab}
                onChange={(_, v) => setTab(v)}
                sx={{ mb: 3, borderBottom: 1, borderColor: 'divider' }}
              >
                {tabsConfig.map(tc => (
                  <Tab key={tc.value} value={tc.value} label={tc.label} />
                ))}
              </Tabs>

              {tab === 'perfil' && <PerfilTab />}
              {tab === 'items' && isCapOrAdmin && <ItemsTab />}
              {tab === 'carpetes' && isCapOrAdmin && <CarpetesTab />}
              {tab === 'usuaris' && isCapOrAdmin && <UsuarisTab />}
              {tab === 'departaments' && isCapOrAdmin && <DepartamentsTab />}
              {tab === 'sucursals' && isAdmin && <SucursalsTab />}
              {tab === 'dominis' && isAdmin && <DominiTab />}
              {tab === 'rols' && isAdmin && <RolsTab />}
            </>
          )}
        </Box>
      </Stack>
    </Stack>
  );
}