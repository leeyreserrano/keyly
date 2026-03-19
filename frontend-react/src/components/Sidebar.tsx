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
  { label: 'Tus carpetas', icon: FolderOutlinedIcon, path: '/folders' },
  { label: 'Compartido', icon: PeopleAltOutlinedIcon, path: '/shared' },
  { label: 'Configuración', icon: EditNoteOutlinedIcon, path: '/settings' },
];

export default function Sidebar() {
  const location = useLocation();
  const navigate = useNavigate();

  return (
    <Stack
      sx={{
        width: 260,
        minHeight: '100vh',
        bgcolor: SIDEBAR_BG,
        flexShrink: 0,
      }}
    >
      {/* Logo */}
      <Stack sx={{ alignItems: 'center', py: 3, px: 2 }}>
        <KeylyLogo sx={{ width: 130, height: 130 }} />
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
                  gap: 2,
                  alignItems: 'center',
                  cursor: 'pointer',
                  bgcolor: isActive ? ACTIVE_BG : 'transparent',
                  borderLeft: isActive
                    ? `4px solid ${brand[400]}`
                    : '4px solid transparent',
                  transition: 'background-color 150ms ease',
                  '&:hover': {
                    bgcolor: 'rgba(255,255,255,0.35)',
                  },
                }}
              >
                <Icon sx={{ color: brand[900], fontSize: 22 }} />
                <Typography
                  variant="h6"
                  sx={{ fontWeight: 700, color: brand[900], lineHeight: 1 }}
                >
                  {item.label}
                </Typography>
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
