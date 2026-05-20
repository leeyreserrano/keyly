import { useLocation, useNavigate } from 'react-router';
import { useTranslation } from 'react-i18next';
import Stack from '@mui/material/Stack';
import Divider from '@mui/material/Divider';
import Typography from '@mui/material/Typography';
import MenuItem from '@mui/material/MenuItem';
import Select from '@mui/material/Select';
import HomeRoundedIcon from '@mui/icons-material/HomeRounded';
import VpnKeyRoundedIcon from '@mui/icons-material/VpnKeyRounded';
import FolderOutlinedIcon from '@mui/icons-material/FolderOutlined';
import PeopleAltOutlinedIcon from '@mui/icons-material/PeopleAltOutlined';
import EditNoteOutlinedIcon from '@mui/icons-material/EditNoteOutlined';
import BarChartRoundedIcon from '@mui/icons-material/BarChartRounded';
import type { SvgIconComponent } from '@mui/icons-material';
import { KeylyLogo } from './CustomIcons';
import { brand } from '../theme/themePrimitives';
import { useState } from 'react';
import i18n from '../i18n';

const SIDEBAR_BG = '#EEE5FF';
const ACTIVE_BG = 'rgba(255,255,255,0.55)';
const DIVIDER_COLOR = 'rgba(171, 61, 240, 0.15)';

interface NavItem {
  labelKey: string;
  icon: SvgIconComponent;
  path: string;
}

const navItems: NavItem[] = [
  { labelKey: 'nav.home', icon: HomeRoundedIcon, path: '/home' },
  { labelKey: 'nav.stats', icon: BarChartRoundedIcon, path: '/stadistics' },
  { labelKey: 'nav.items', icon: VpnKeyRoundedIcon, path: '/items' },
  { labelKey: 'nav.folders', icon: FolderOutlinedIcon, path: '/carpetes' },
  { labelKey: 'nav.shared', icon: PeopleAltOutlinedIcon, path: '/compartits' },
  { labelKey: 'nav.settings', icon: EditNoteOutlinedIcon, path: '/settings' },
];

export default function Sidebar() {
  const location = useLocation();
  const navigate = useNavigate();
  const { t } = useTranslation('sidebar');
  const [open, setOpen] = useState(false);

  const handleLanguageChange = (lang: string) => {
    i18n.changeLanguage(lang);
  };

  return (
    <Stack
      sx={{
        width: open ? 260 : 80,
        height: '100vh',
        flexShrink: 0,
        bgcolor: SIDEBAR_BG,
        transition: 'width 0.3s ease',
        display: 'flex',
        flexDirection: 'column',
        position: 'sticky',
        top: 0,
        alignSelf: 'flex-start',
      }}
    >
      <Stack
        sx={{ alignItems: 'center', py: 3, px: 2, cursor: 'pointer' }}
        onClick={() => setOpen(!open)}
      >
        <KeylyLogo sx={{ width: open ? 130 : 50, height: open ? 130 : 50 }} />
      </Stack>

      <Divider sx={{ borderColor: DIVIDER_COLOR }} />

      <Stack sx={{ flex: 1, pt: 1 }}>
        {navItems.map((item, index) => {
          const isActive = location.pathname === item.path;
          const Icon = item.icon;

          return (
            <div key={item.labelKey}>
              <Stack
                direction="row"
                onClick={() => navigate(item.path)}
                sx={{
                  px: 3,
                  py: 2,
                  gap: open ? 2 : 0,
                  justifyContent: open ? 'flex-start' : 'center',
                  alignItems: 'center',
                  cursor: 'pointer',
                  bgcolor: isActive ? ACTIVE_BG : 'transparent',
                  borderLeft: isActive
                    ? `4px solid ${brand[400]}`
                    : '4px solid transparent',
                  transition: 'all 150ms ease',
                  '&:hover': { bgcolor: 'rgba(255,255,255,0.35)' },
                }}
              >
                <Icon sx={{ color: brand[900], fontSize: 22 }} />
                {open && (
                  <Typography variant="h6" sx={{ fontWeight: 700, color: brand[900], lineHeight: 1 }}>
                    {t(item.labelKey)}
                  </Typography>
                )}
              </Stack>
              {index < navItems.length - 1 && (
                <Divider sx={{ borderColor: DIVIDER_COLOR }} />
              )}
            </div>
          );
        })}
      </Stack>

      <Divider sx={{ borderColor: DIVIDER_COLOR }} />

      <Stack sx={{ px: 2, py: 2, alignItems: open ? 'flex-start' : 'center' }}>
        <Select
          value={i18n.language === 'en' ? 'en' : i18n.language === 'es' ? 'es' : 'ca'}
          onChange={(e) => handleLanguageChange(e.target.value)}
          size="small"
          sx={{
            width: open ? '100%' : 50,
            fontSize: '0.75rem',
            fontWeight: 700,
            bgcolor: 'rgba(255,255,255,0.4)',
            '& .MuiSelect-select': { py: 0.75 },
          }}
        >
          <MenuItem value="ca">🇨🇦 CAT</MenuItem>
          <MenuItem value="en">🇬🇧 EN</MenuItem>
          <MenuItem value="es">🇪🇸 ES</MenuItem>
        </Select>
      </Stack>
    </Stack>
  );
}