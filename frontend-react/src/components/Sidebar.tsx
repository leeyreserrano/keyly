import { useLocation, useNavigate } from 'react-router';
import Stack from '@mui/material/Stack';
import Divider from '@mui/material/Divider';
import Typography from '@mui/material/Typography';
import HomeRoundedIcon from '@mui/icons-material/HomeRounded';
import VpnKeyRoundedIcon from '@mui/icons-material/VpnKeyRounded';
import FolderOutlinedIcon from '@mui/icons-material/FolderOutlined';
import PeopleAltOutlinedIcon from '@mui/icons-material/PeopleAltOutlined';
import EditNoteOutlinedIcon from '@mui/icons-material/EditNoteOutlined';
import type { SvgIconComponent } from '@mui/icons-material';
import { KeylyLogo } from './CustomIcons';
import { brand } from '../theme/themePrimitives';
import { useState } from 'react';

const SIDEBAR_BG = '#EEE5FF';
const ACTIVE_BG = 'rgba(255,255,255,0.55)';
const DIVIDER_COLOR = 'rgba(171, 61, 240, 0.15)';

interface NavItem {
  label: string;
  icon: SvgIconComponent;
  path: string;
}

const navItems: NavItem[] = [
  { label: 'Home', icon: HomeRoundedIcon, path: '/home' },
  { label: 'Items', icon: VpnKeyRoundedIcon, path: '/items' },
  { label: 'Tus carpetas', icon: FolderOutlinedIcon, path: '/carpetes' },
  { label: 'Compartido', icon: PeopleAltOutlinedIcon, path: '/shared' },
  { label: 'Configuración', icon: EditNoteOutlinedIcon, path: '/settings' },
];

export default function Sidebar() {
  const location = useLocation();
  const navigate = useNavigate();
  const [open, setOpen] = useState(false);

  return (
    <Stack
      sx={{
        width: open ? 260 : 80,
        minHeight: '100vh',
        bgcolor: SIDEBAR_BG,
        flexShrink: 0,
        transition: 'width 0.3s ease',
      }}
    >
      {/* Logo */}
      <Stack
        sx={{ alignItems: 'center', py: 3, px: 2, cursor: 'pointer' }}
        onClick={() => setOpen(!open)}
      >
        <KeylyLogo sx={{ width: open ? 130 : 50, height: open ? 130 : 50 }} />
      </Stack>

      <Divider sx={{ borderColor: DIVIDER_COLOR }} />

      {/* Nav Items */}
      <Stack sx={{ flex: 1, pt: 1 }}>
        {navItems.map((item, index) => {
          const isActive = location.pathname === item.path;
          const Icon = item.icon;

          return (
            <div key={item.label}>
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
                  '&:hover': {
                    bgcolor: 'rgba(255,255,255,0.35)',
                  },
                }}
              >
                <Icon sx={{ color: brand[900], fontSize: 22 }} />

                {open && (
                  <Typography
                    variant="h6"
                    sx={{
                      fontWeight: 700,
                      color: brand[900],
                      lineHeight: 1,
                    }}
                  >
                    {item.label}
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
    </Stack>
  );
}
