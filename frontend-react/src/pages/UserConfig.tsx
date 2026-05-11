import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Stack, Typography, Paper, Box, Tabs, Tab } from '@mui/material';
import SettingsOutlinedIcon from '@mui/icons-material/SettingsOutlined';
import Header from '../components/Header';
import { useAuth } from '../context/AuthContext';
import PerfilTab from '../components/PerfilTab';
import UsuarisTab from '../components/UsuarisTab';
import ItemsTab from '../components/ItemsTab';
import CarpetesTab from '../components/CarpetesTab';
import DepartamentsTab from '../components/DepartamentsTab';
import SucursalsTab from '../components/SucursalsTab';

type TabValue = 'perfil' | 'items' | 'carpetes' | 'usuaris' | 'departaments' | 'sucursals';

export default function UserConfig() {
  const { usuari } = useAuth();
  const { t } = useTranslation('config');
  const [tab, setTab] = useState<TabValue>('perfil');

  const isCapOrAdmin = usuari?.rolIntern === 'CAP' || usuari?.rolIntern === 'ADMIN';

  const tabsConfig: Array<{ value: TabValue; label: string }> = [
    { value: 'perfil', label: t('tabs.profile') },
    { value: 'items', label: t('tabs.items') },
    { value: 'carpetes', label: t('tabs.folders') },
    ...(isCapOrAdmin ? [{ value: 'usuaris' as TabValue, label: t('tabs.users') }] : []),
    ...(isCapOrAdmin ? [{ value: 'departaments' as TabValue, label: t('tabs.departments') }] : []),
    ...(isCapOrAdmin ? [{ value: 'sucursals' as TabValue, label: t('tabs.branches') }] : []),
  ];

  return (
    <Stack sx={{ height: '100%', overflow: 'hidden' }}>
      <Header
        title={t('title')}
        icon={<SettingsOutlinedIcon sx={{ fontSize: 30, color: 'text.primary' }} />}
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
                {tabsConfig.map((t) => (
                  <Tab key={t.value} value={t.value} label={t.label} />
                ))}
              </Tabs>

              {tab === 'perfil' && <PerfilTab />}
              {tab === 'items' && <ItemsTab />}
              {tab === 'carpetes' && <CarpetesTab />}
              {tab === 'usuaris' && isCapOrAdmin && <UsuarisTab />}
              {tab === 'departaments' && isCapOrAdmin && <DepartamentsTab />}
              {tab === 'sucursals' && isCapOrAdmin && <SucursalsTab />}
            </>
          )}
        </Box>
      </Stack>
    </Stack>
  );
}